package org.emoflon.ibex.tgg.benchmark.runner;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.model.EclipseJavaProject;
import org.emoflon.ibex.tgg.benchmark.model.EclipseTggProject;
import org.emoflon.ibex.tgg.benchmark.model.IEclipseWorkspace;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;
import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.OperationalizationType;
import org.emoflon.ibex.tgg.benchmark.runner.report.CSVReportBuilder;
import org.emoflon.ibex.tgg.benchmark.runner.report.ExcelReportBuilder;
import org.emoflon.ibex.tgg.benchmark.runner.report.ReportBuilder;
import org.emoflon.ibex.tgg.benchmark.runner.report.ReportFileType;
import org.emoflon.ibex.tgg.benchmark.utils.ReflectionUtils;
import org.emoflon.ibex.tgg.benchmark.utils.StringUtils;
import org.emoflon.ibex.tgg.operational.strategies.OperationalStrategy;
import org.terracotta.ipceventbus.event.EventBusException;
import org.terracotta.ipceventbus.event.EventBusServer;
import org.terracotta.ipceventbus.proc.JavaProcess;

/**
 * The BenchmarkRunner class is used to perform the benchmarks.
 *
 * @author Andre Lehmann
 */
public class BenchmarkRunner implements Runnable {

    private static final Logger LOG = LogManager.getLogger(Core.PLUGIN_NAME);
    private static final int EVENT_BUS_PORT = 24842;
    private static final LinkedList<File> CLASS_PATHS;

    private final SubMonitor progressMonitor;
    private final PluginPreferences pluginPreferences;
    private final List<BenchmarkCasePreferences> benchmarkCases;
    private final IEclipseWorkspace eclipseWorkspace;
    private final LocalDateTime startTime;
    private final Path modelInstancesBasePath;

    private final Map<Path, ReportBuilder> reportBuilders;

    static {
        String[] classPaths = System.getProperty("java.class.path").split(":");

        CLASS_PATHS = new LinkedList<>();
        for (String path : classPaths) {
            CLASS_PATHS.add(new File(path));
        }
    }

    /**
     * Constructor for {@link BenchmarkRunner}.
     * 
     * @param benchmarkCasePreferences a single benchmark case
     * @throws IOException if the report file couldn't be created
     */
    public BenchmarkRunner(BenchmarkCasePreferences benchmarkCasePreferences, IProgressMonitor progressMonitor) {
        this(Arrays.asList(benchmarkCasePreferences), progressMonitor);
    }

    /**
     * Constructor for {@link BenchmarkRunner}.
     * 
     * @param benchmarkCases a list of benchmark cases
     * @throws IOException if the report file couldn't be created
     */
    public BenchmarkRunner(List<BenchmarkCasePreferences> benchmarkCases, IProgressMonitor progressMonitor) {
        this.benchmarkCases = benchmarkCases;
        this.progressMonitor = SubMonitor.convert(progressMonitor, benchmarkCases.size());
        this.eclipseWorkspace = Core.getInstance().getWorkspace();
        this.pluginPreferences = Core.getInstance().getPluginPreferences();
        this.startTime = LocalDateTime.now();
        this.modelInstancesBasePath = eclipseWorkspace.getPluginStateLocation().resolve("model_instances");
        this.reportBuilders = new HashMap<>();
    }

    public List<String> checkForErrors() {
        LinkedList<String> foundErrors = new LinkedList<>();

        // check Eclipse projects
        Set<EclipseJavaProject> checkedProjects = new HashSet<>();
        Stack<EclipseJavaProject> projectsToCheck = new Stack<>();
        while (!projectsToCheck.isEmpty()) {
            EclipseJavaProject project = projectsToCheck.pop();
            if (!checkedProjects.contains(project)) {
                try {
                    if (!Files.exists(project.getOutputPath()) || Files.list(project.getOutputPath()).count() == 0) {
                        foundErrors.add(String.format(
                                "Eclipse project '%s': Output folder '%s' doesn't exist or is empty. Recompile your project",
                                project.getName(), project.getOutputPath()));
                    }
                } catch (IOException e) {
                    foundErrors.add(String.format("Eclipse project '%s': %s", project.getName(), e.getMessage()));
                }
                checkedProjects.add(project);
                projectsToCheck.addAll(project.getReferencedProjects());
            }
        }

        // check run parameters (most of them are covered by input validation already)
        for (BenchmarkCasePreferences benchmarkCase : benchmarkCases) {

            if (benchmarkCase.getBenchmarkCaseName().isEmpty()) {
                foundErrors.add(String.format("Benchmark case '%s': The benchmark case needs to be named",
                        benchmarkCase.getEclipseProject().getName()));
            }
            if (benchmarkCase.getModelgenTggRule().isEmpty()) {
                foundErrors
                        .add(String.format("Benchmark case '%s': TGG rule for model generation needs to be specified",
                                benchmarkCase.getEclipseProject().getName()));
            }

            Path projectOutputPath = benchmarkCase.getEclipseProject().getOutputPath();
            try {
                if (Files.exists(projectOutputPath) && Files.list(projectOutputPath).count() > 0) {
                    try (URLClassLoader classLoader = ReflectionUtils.createClassLoader(projectOutputPath);) {
                        if (benchmarkCase.getMetamodelsRegistrationMethod().isEmpty()) {
                            foundErrors.add(
                                    String.format("Benchmark case '%s': Meta model registration method needs to be set",
                                            benchmarkCase.getBenchmarkCaseName()));
                        } else {
                            try {
                                ReflectionUtils.getMethodByName(classLoader,
                                        benchmarkCase.getMetamodelsRegistrationMethod(), ResourceSet.class,
                                        OperationalStrategy.class);
                            } catch (NoSuchMethodException e) {
                                foundErrors.add(String.format(
                                        "Benchmark case '%s': Meta model registration method doesn't exist",
                                        benchmarkCase.getBenchmarkCaseName()));
                            }
                        }
                        if (benchmarkCase.isFwdActive()) {
                            if (!benchmarkCase.getFwdIncrementalEditMethod().isEmpty()) {
                                try {
                                    ReflectionUtils.getMethodByName(classLoader,
                                            benchmarkCase.getFwdIncrementalEditMethod(), EObject.class);
                                } catch (NoSuchMethodException e) {
                                    foundErrors.add(String.format(
                                            "Benchmark case '%s': Incremental edit method for FWD operationalization doesn't exist",
                                            benchmarkCase.getBenchmarkCaseName()));
                                }

                            }
                        }
                        if (benchmarkCase.isBwdActive()) {
                            if (!benchmarkCase.getFwdIncrementalEditMethod().isEmpty()) {
                                try {
                                    ReflectionUtils.getMethodByName(classLoader,
                                            benchmarkCase.getBwdIncrementalEditMethod(), EObject.class);
                                } catch (NoSuchMethodException e) {
                                    foundErrors.add(String.format(
                                            "Benchmark case '%s': Incremental edit method for BWD operationalization doesn't exist",
                                            benchmarkCase.getBenchmarkCaseName()));
                                }

                            }
                        }
                    } catch (Exception e) {
                        foundErrors.add(String.format("Benchmark case '%s': %s", benchmarkCase.getBenchmarkCaseName(),
                                e.getMessage()));
                    }
                }
            } catch (IOException e) {
                foundErrors.add(
                        String.format("Benchmark case '%s': %s", benchmarkCase.getBenchmarkCaseName(), e.getMessage()));
            }
        }

        // check report files (try to open them and notify the user for errors if any)
        Map<OperationalizationType, Function<BenchmarkCasePreferences, Boolean>> includeOps = new HashMap<>();
        includeOps.put(OperationalizationType.MODELGEN, BenchmarkCasePreferences::isModelgenIncludeReport);
        includeOps.put(OperationalizationType.INITIAL_FWD, BenchmarkCasePreferences::isInitialFwdActive);
        includeOps.put(OperationalizationType.INITIAL_BWD, BenchmarkCasePreferences::isInitialBwdActive);
        includeOps.put(OperationalizationType.FWD,
                (bcp) -> bcp.isFwdActive() && bcp.getFwdIncrementalEditMethod() == "");
        includeOps.put(OperationalizationType.BWD,
                (bcp) -> bcp.isBwdActive() && bcp.getFwdIncrementalEditMethod() == "");
        includeOps.put(OperationalizationType.INCREMENTAL_FWD,
                (bcp) -> bcp.isFwdActive() && bcp.getFwdIncrementalEditMethod() != "");
        includeOps.put(OperationalizationType.INCREMENTAL_BWD,
                (bcp) -> bcp.isBwdActive() && bcp.getFwdIncrementalEditMethod() != "");
        includeOps.put(OperationalizationType.FWD_OPT, BenchmarkCasePreferences::isFwdOptActive);
        includeOps.put(OperationalizationType.BWD_OPT, BenchmarkCasePreferences::isBwdOptActive);
        includeOps.put(OperationalizationType.CO, BenchmarkCasePreferences::isCoActive);
        includeOps.put(OperationalizationType.CC, BenchmarkCasePreferences::isCoActive);
        boolean anyReportFileErrors = false;
        for (BenchmarkCasePreferences benchmarkCase : benchmarkCases) {
            for (Map.Entry<OperationalizationType, Function<BenchmarkCasePreferences, Boolean>> includeOp : includeOps
                    .entrySet()) {
                if (includeOp.getValue().apply(benchmarkCase)) {
                    Path reportFilePath = StringUtils.createPathFromString(pluginPreferences.getReportFilePath(),
                            eclipseWorkspace.getLocation(), benchmarkCase, includeOp.getKey(), startTime);
                    try {
                        getReportBuilderByPath(reportFilePath);
                    } catch (IOException e) {
                        foundErrors
                                .add(String.format("Report file '%s': %s", reportFilePath.toString(), e.getMessage()));
                        anyReportFileErrors = true;
                    }
                }
            }
        }
        if (anyReportFileErrors) {
            closeAllReportBuilders();
        }

        return foundErrors;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        try {
            LOG.info("Benchmark started");
            try {
                for (BenchmarkCasePreferences bcp : benchmarkCases) {
                    progressMonitor.setTaskName("Benchmarking '" + bcp.getBenchmarkCaseName() + "'");
                    ;
                    benchmarkCase(bcp);
                    progressMonitor.split(1);
                }
                LOG.info("Benchmark finished");
            } catch (IOException e1) {
                LOG.error("Benchmark process failed. Reason: {}", e1.getMessage());
            } finally {
                closeAllReportBuilders();
                try {
                    if (Files.exists(modelInstancesBasePath)) {
                        Files.walk(modelInstancesBasePath).sorted(Comparator.reverseOrder()).map(Path::toFile)
                                .forEach(File::delete);
                    }
                } catch (IOException e) {
                    LOG.error("Failed to delete model instances. Reason: {}", e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            // ignore
        }
    }

    /**
     * Benchmarks a single benchmark case. Each type of operationalization will be
     * benchmarked with different model sizes and multiple repetition to measure an
     * average value.
     *
     * @param eclipseProject the project the benchmark case belongs to
     * @param bcp            the benchmark case preferences
     * @throws IOException          if writing the report fails
     * @throws InterruptedException
     */
    private void benchmarkCase(BenchmarkCasePreferences bcp) throws IOException, InterruptedException {
        BiFunction<Integer, Integer, Integer> min = (i1, i2) -> i1 < i2 ? i1 : i2;

        BiFunction<Integer, Integer, BenchmarkRunParameters> runParametersGenerator = null;
        ReportBuilder reportBuilder = null;

        LOG.info("Benchmark case '{}'", bcp.getBenchmarkCaseName());

        // MODELGEN
        runParametersGenerator = (Integer modelSize, Integer repetition) -> {
            BenchmarkRunParameters runParameters = createGenericRunParameters(bcp, modelSize, repetition);
            runParameters.setOperationalization(OperationalizationType.MODELGEN);
            runParameters.setTimeout(bcp.getEffectiveModelgenTimeout());
            runParameters.setTggRule(bcp.getModelgenTggRule());
            return runParameters;
        };
        reportBuilder = getReportBuilderByPath(StringUtils.createPathFromString(pluginPreferences.getReportFilePath(),
                eclipseWorkspace.getLocation(), bcp, OperationalizationType.MODELGEN, startTime));
        Integer maxSuccessfullModelSize = benchmarkOperationalization(runParametersGenerator,
                bcp.getModelgenModelSizes(), bcp.getModelgenModelSizes().get(bcp.getModelgenModelSizes().size() - 1),
                pluginPreferences.getRepetitions(), bcp.isModelgenIncludeReport(), reportBuilder);

        // INITIAL_FWD
        if (bcp.isInitialFwdActive()) {
            runParametersGenerator = (Integer modelSize, Integer repetition) -> {
                BenchmarkRunParameters runParameters = createGenericRunParameters(bcp, modelSize, repetition);
                runParameters.setOperationalization(OperationalizationType.INITIAL_FWD);
                runParameters.setTimeout(bcp.getEffectiveInitialFwdTimeout());
                return runParameters;
            };
            reportBuilder = getReportBuilderByPath(
                    StringUtils.createPathFromString(pluginPreferences.getReportFilePath(),
                            eclipseWorkspace.getLocation(), bcp, OperationalizationType.INITIAL_FWD, startTime));
            benchmarkOperationalization(runParametersGenerator, bcp.getModelgenModelSizes(),
                    min.apply(maxSuccessfullModelSize, bcp.getEffectiveInitialFwdMaxModelSize()),
                    pluginPreferences.getRepetitions(), true, reportBuilder);
        }

        // INITIAL_BWD
        if (bcp.isInitialBwdActive()) {
            runParametersGenerator = (Integer modelSize, Integer repetition) -> {
                BenchmarkRunParameters runParameters = createGenericRunParameters(bcp, modelSize, repetition);
                runParameters.setOperationalization(OperationalizationType.INITIAL_BWD);
                runParameters.setTimeout(bcp.getEffectiveInitialBwdTimeout());
                return runParameters;
            };
            reportBuilder = getReportBuilderByPath(
                    StringUtils.createPathFromString(pluginPreferences.getReportFilePath(),
                            eclipseWorkspace.getLocation(), bcp, OperationalizationType.INITIAL_BWD, startTime));
            benchmarkOperationalization(runParametersGenerator, bcp.getModelgenModelSizes(),
                    min.apply(maxSuccessfullModelSize, bcp.getEffectiveInitialBwdMaxModelSize()),
                    pluginPreferences.getRepetitions(), true, reportBuilder);
        }

        // FWD, INCREMENTAL_FWD
        if (bcp.isFwdActive()) {
            runParametersGenerator = (Integer modelSize, Integer repetition) -> {
                BenchmarkRunParameters runParameters = createGenericRunParameters(bcp, modelSize, repetition);
                runParameters.setTimeout(bcp.getEffectiveFwdTimeout());
                if (bcp.getFwdIncrementalEditMethod() == "") {
                    runParameters.setOperationalization(OperationalizationType.FWD);
                } else {
                    runParameters.setOperationalization(OperationalizationType.INCREMENTAL_FWD);
                    runParameters.setIncrementalEditMethod(bcp.getFwdIncrementalEditMethod());
                }
                return runParameters;
            };
            reportBuilder = getReportBuilderByPath(StringUtils.createPathFromString(
                    pluginPreferences.getReportFilePath(), eclipseWorkspace.getLocation(), bcp,
                    bcp.getFwdIncrementalEditMethod() == "" ? OperationalizationType.FWD
                            : OperationalizationType.INCREMENTAL_FWD,
                    startTime));
            benchmarkOperationalization(runParametersGenerator, bcp.getModelgenModelSizes(),
                    min.apply(maxSuccessfullModelSize, bcp.getEffectiveFwdMaxModelSize()),
                    pluginPreferences.getRepetitions(), true, reportBuilder);
        }

        // BWD, INCREMENTAL_BWD
        if (bcp.isBwdActive()) {
            runParametersGenerator = (Integer modelSize, Integer repetition) -> {
                BenchmarkRunParameters runParameters = createGenericRunParameters(bcp, modelSize.intValue(),
                        repetition);
                runParameters.setTimeout(bcp.getEffectiveBwdTimeout());
                if (bcp.getBwdIncrementalEditMethod() == "") {
                    runParameters.setOperationalization(OperationalizationType.BWD);
                } else {
                    runParameters.setOperationalization(OperationalizationType.INCREMENTAL_BWD);
                    runParameters.setIncrementalEditMethod(bcp.getBwdIncrementalEditMethod());
                }
                return runParameters;
            };
            reportBuilder = getReportBuilderByPath(StringUtils.createPathFromString(
                    pluginPreferences.getReportFilePath(), eclipseWorkspace.getLocation(), bcp,
                    bcp.getBwdIncrementalEditMethod() == "" ? OperationalizationType.BWD
                            : OperationalizationType.INCREMENTAL_BWD,
                    startTime));
            benchmarkOperationalization(runParametersGenerator, bcp.getModelgenModelSizes(),
                    min.apply(maxSuccessfullModelSize, bcp.getEffectiveBwdMaxModelSize()),
                    pluginPreferences.getRepetitions(), true, reportBuilder);
        }

        // FWD_OPT
        if (bcp.isFwdOptActive()) {
            runParametersGenerator = (Integer modelSize, Integer repetition) -> {
                BenchmarkRunParameters runParameters = createGenericRunParameters(bcp, modelSize, repetition);
                runParameters.setOperationalization(OperationalizationType.FWD_OPT);
                runParameters.setTimeout(bcp.getEffectiveFwdOptTimeout());
                return runParameters;
            };
            reportBuilder = getReportBuilderByPath(
                    StringUtils.createPathFromString(pluginPreferences.getReportFilePath(),
                            eclipseWorkspace.getLocation(), bcp, OperationalizationType.FWD_OPT, startTime));
            benchmarkOperationalization(runParametersGenerator, bcp.getModelgenModelSizes(),
                    min.apply(maxSuccessfullModelSize, bcp.getEffectiveFwdOptMaxModelSize()),
                    pluginPreferences.getRepetitions(), true, reportBuilder);
        }

        // BWD_OPT
        if (bcp.isBwdOptActive()) {
            runParametersGenerator = (Integer modelSize, Integer repetition) -> {
                BenchmarkRunParameters runParameters = createGenericRunParameters(bcp, modelSize, repetition);
                runParameters.setOperationalization(OperationalizationType.BWD_OPT);
                runParameters.setTimeout(bcp.getEffectiveBwdOptTimeout());
                return runParameters;
            };
            reportBuilder = getReportBuilderByPath(
                    StringUtils.createPathFromString(pluginPreferences.getReportFilePath(),
                            eclipseWorkspace.getLocation(), bcp, OperationalizationType.BWD_OPT, startTime));
            benchmarkOperationalization(runParametersGenerator, bcp.getModelgenModelSizes(),
                    min.apply(maxSuccessfullModelSize, bcp.getEffectiveBwdOptMaxModelSize()),
                    pluginPreferences.getRepetitions(), true, reportBuilder);
        }

        // CC
        if (bcp.isCcActive()) {
            runParametersGenerator = (Integer modelSize, Integer repetition) -> {
                BenchmarkRunParameters runParameters = createGenericRunParameters(bcp, modelSize, repetition);
                runParameters.setOperationalization(OperationalizationType.CC);
                runParameters.setTimeout(bcp.getEffectiveCcTimeout());
                return runParameters;
            };
            reportBuilder = getReportBuilderByPath(
                    StringUtils.createPathFromString(pluginPreferences.getReportFilePath(),
                            eclipseWorkspace.getLocation(), bcp, OperationalizationType.CC, startTime));
            benchmarkOperationalization(runParametersGenerator, bcp.getModelgenModelSizes(),
                    min.apply(maxSuccessfullModelSize, bcp.getEffectiveCcMaxModelSize()),
                    pluginPreferences.getRepetitions(), true, reportBuilder);
        }

        // CO
        if (bcp.isCoActive()) {
            runParametersGenerator = (Integer modelSize, Integer repetition) -> {
                BenchmarkRunParameters runParameters = createGenericRunParameters(bcp, modelSize, repetition);
                runParameters.setOperationalization(OperationalizationType.CO);
                runParameters.setTimeout(bcp.getEffectiveCoTimeout());
                return runParameters;
            };
            reportBuilder = getReportBuilderByPath(
                    StringUtils.createPathFromString(pluginPreferences.getReportFilePath(),
                            eclipseWorkspace.getLocation(), bcp, OperationalizationType.CO, startTime));
            benchmarkOperationalization(runParametersGenerator, bcp.getModelgenModelSizes(),
                    min.apply(maxSuccessfullModelSize, bcp.getEffectiveCoMaxModelSize()),
                    pluginPreferences.getRepetitions(), true, reportBuilder);
        }
    }

    /**
     * Benchmarks a single operationalization. The operationalization will be
     * benchmarked with different model sizes and multiple repetition to measure an
     * average value.
     * 
     * @param runParametersGenerator generator for run parameters
     * @param modelSizes             model size that need to be benchmarked
     * @param maxEffectiveModelSize  max model size to test
     * @param numberOfRepetitions    number of repetition to run each model size
     * @param includeInReport        wheter to include the results into the report
     *                               or not
     * @return the last model size that benchmarked successfully
     * @throws IOException          if writing the report fails
     * @throws InterruptedException
     */
    private Integer benchmarkOperationalization(
            BiFunction<Integer, Integer, BenchmarkRunParameters> runParametersGenerator, List<Integer> modelSizes,
            Integer maxEffectiveModelSize, Integer numberOfRepetitions, boolean includeInReport,
            ReportBuilder reportBuilder) throws IOException, InterruptedException {

        Integer lastSuccessfullModelSize = -1;

        if (numberOfRepetitions <= 0) {
            return lastSuccessfullModelSize;
        }

        opBenchmark: for (Integer modelSize : modelSizes) {
            if (modelSize > maxEffectiveModelSize) {
                break;
            }

            String lastError = null;
            BenchmarkResult benchmarkResult = null;

            repetition: for (Integer repetition = 0; repetition < numberOfRepetitions; repetition++) {
                BenchmarkRunParameters runParameters = runParametersGenerator.apply(modelSize, repetition);
                SingleRunResult singleRunResult = runBenchmarkChildProcess(runParameters);

                if (benchmarkResult == null) {
                    benchmarkResult = new BenchmarkResult(runParameters);
                }
                benchmarkResult.addSingleResult(singleRunResult);
                lastError = singleRunResult.getError();

                if (lastError != null) {
                    break repetition;
                }
            }

            // add result to the report
            if (includeInReport && (lastError == null || pluginPreferences.isIncludeErrors())) {
                reportBuilder.addEntry(benchmarkResult);
            }

            if (lastError != null) {
                break opBenchmark;
            }

            lastSuccessfullModelSize = modelSize;
        }

        return lastSuccessfullModelSize;
    }

    private SingleRunResult runBenchmarkChildProcess(BenchmarkRunParameters runParameters) throws InterruptedException {
        SingleRunResult result = new SingleRunResult();
        result.setError("Unknown error");

        // helper to get the result back from an event
        final Stack<SingleRunResult> resultWrapper = new Stack<>();

        try (EventBusServer eventBus = new EventBusServer.Builder().listen(EVENT_BUS_PORT).build()) {
            eventBus.on("requestRunParameters", e -> {
                eventBus.trigger("sendRunParameters", runParameters);
            });
            eventBus.on("result", e -> {
                resultWrapper.add(e.getData(SingleRunResult.class));
            });

            LOG.debug("Starting benchmark in child process");
            JavaProcess javaProcess = JavaProcess.newBuilder().mainClass(BenchmarkClientProcess.class.getName())
                    .classpath(CLASS_PATHS).addJvmArg("-Xmx" + pluginPreferences.getMaxMemorySize() + "m")
                    .addJvmArg("-Xms" + pluginPreferences.getMaxMemorySize() + "m")
                    .arguments(LOG.getLevel().getStandardLevel().toString()).pipeStdout().pipeStderr().build();
            try {
                javaProcess.waitFor();
            } catch (InterruptedException e) {
                javaProcess.destroy();
                LOG.error("Benchmark process interrupted");
                throw e;
            }

            if (javaProcess.exitValue() == 1) {
                LOG.error("Benchmark child process execution failed");
            }

            // wait that the results are pushed on resultWrapper stack
            Thread.sleep(10);

            if (!resultWrapper.empty()) {
                result = resultWrapper.pop();
            }
        } catch (EventBusException | IOException e3) {
            result.setError(e3.getMessage());
        }

        return result;
    }

    /**
     * Creates an instance of {@link BenchmarkRunParameters}.
     * 
     * @param project    the eclipse project the benchmark case belongs to
     * @param bcp        the benchmark case
     * @param modelSize  the model size
     * @param repetition the repetition number
     * @return a generic instance of {@link BenchmarkRunParameters}
     */
    private BenchmarkRunParameters createGenericRunParameters(BenchmarkCasePreferences bcp, Integer modelSize,
            Integer repetition) {
        BenchmarkRunParameters runParameters = new BenchmarkRunParameters();
        EclipseTggProject eclipseProject = bcp.getEclipseProject();

        runParameters.setProjectName(eclipseProject.getName());
        runParameters.setModelInstancesbasePath(
                eclipseWorkspace.getLocation().relativize(modelInstancesBasePath).toString());
        runParameters.setWorkspacePath(eclipseWorkspace.getLocation().toString());
        runParameters.setClassPaths(eclipseProject.getClassPathURLs());
        runParameters.setPatternMatchingEngine(bcp.getPatternMatchingEngine());
        runParameters.setMetamodelsRegistrationMethod(bcp.getMetamodelsRegistrationMethod());
        runParameters.setModelSize(modelSize.intValue());
        runParameters.setRepetition(repetition.intValue());

        return runParameters;
    }

    private ReportBuilder getReportBuilderByPath(Path path) throws IOException {
        ReportBuilder reportBuilder = reportBuilders.get(path);
        if (reportBuilder == null) {
            if (pluginPreferences.getReportFileType() == ReportFileType.CSV) {
                reportBuilder = new CSVReportBuilder(Paths.get(path.toString() + ".csv"),
                        pluginPreferences.isIncludeErrors());
            } else if (pluginPreferences.getReportFileType() == ReportFileType.EXCEL) {
                reportBuilder = new ExcelReportBuilder(Paths.get(path.toString() + ".xlsx"),
                        pluginPreferences.isIncludeErrors());
            }
            reportBuilders.put(path, reportBuilder);
        }
        return reportBuilder;
    }

    private void closeAllReportBuilders() {
        for (ReportBuilder reportBuilder : reportBuilders.values()) {
            try {
                reportBuilder.close();
            } catch (IOException e) {
                // ignore
            }
        }
        reportBuilders.clear();
    }
}