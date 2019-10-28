package org.emoflon.tggbenchmark.runner;

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
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.ibex.tgg.operational.strategies.OperationalStrategy;
import org.emoflon.tggbenchmark.Core;
import org.emoflon.tggbenchmark.gui.model.BenchmarkCase;
import org.emoflon.tggbenchmark.gui.model.PluginPreferences;
import org.emoflon.tggbenchmark.runner.benchmark.BenchmarkProcess;
import org.emoflon.tggbenchmark.runner.report.CSVReportBuilder;
import org.emoflon.tggbenchmark.runner.report.ExcelReportBuilder;
import org.emoflon.tggbenchmark.runner.report.ReportBuilder;
import org.emoflon.tggbenchmark.runner.report.ReportFileType;
import org.emoflon.tggbenchmark.runner.result.BenchmarkResult;
import org.emoflon.tggbenchmark.runner.result.SingleRunResult;
import org.emoflon.tggbenchmark.utils.ReflectionUtils;
import org.emoflon.tggbenchmark.utils.StringUtils;
import org.emoflon.tggbenchmark.workspace.EclipseJavaProject;
import org.emoflon.tggbenchmark.workspace.EclipseTggProject;
import org.emoflon.tggbenchmark.workspace.IEclipseWorkspace;
import org.terracotta.ipceventbus.event.EventBusException;
import org.terracotta.ipceventbus.event.EventBusServer;
import org.terracotta.ipceventbus.event.RethrowingErrorListener;
import org.terracotta.ipceventbus.proc.JavaProcess;

import io.github.classgraph.ClassGraph;
import javafx.util.Pair;

/**
 * The BenchmarkRunner class is used to perform the benchmarks.
 *
 * @author Andre Lehmann
 */
public class BenchmarkRunner implements Runnable {

    private static final Logger LOG = LogManager.getLogger(Core.PLUGIN_NAME);
    private static final int EVENT_BUS_PORT = 24842;
    private static final String pluginClasspathArgument;

    private final SubMonitor benchmarkCaseMonitor;
    private final PluginPreferences pluginPreferences;
    private final List<BenchmarkCase> benchmarkCases;
    private final IEclipseWorkspace eclipseWorkspace;
    private final LocalDateTime startTime;
    private final Path modelInstancesBasePath;

    private final Map<Path, ReportBuilder> reportBuilders;

    static {
        // In order to run a new java process for benchmarking we need the
        // required classpaths. This code block will gather all the classpaths
        // of this plugin and turn it into a argument string, that can be used
        // to call a new java process.
        Set<String> classpathSet = new HashSet<>();
        new ClassGraph().getClasspathFiles().forEach(file -> {
            Path absPath = file.toPath().toAbsolutePath();
            if (!Files.isDirectory(absPath)) {
                // when the path is a file turn it into a wildcard path
                // this will significantly reduce the number of paths
                classpathSet.add(absPath.getParent().resolve("*").toString());
            } else {
                classpathSet.add(absPath.toString() + "/");
            }
        });
        String sep = System.getProperty("os.name", "unknown").toLowerCase().contains("windows") ? ";" : ":";
        pluginClasspathArgument = String.join(sep, classpathSet.toArray(new String[classpathSet.size()]));
    }

    /**
     * Constructor for {@link BenchmarkRunner}.
     * 
     * @param benchmarkCase   a single benchmark case
     * @param progressMonitor the progress monitor for progress updates
     */
    public BenchmarkRunner(BenchmarkCase benchmarkCase, IProgressMonitor progressMonitor) {
        this(Arrays.asList(benchmarkCase), progressMonitor);
    }

    /**
     * Constructor for {@link BenchmarkRunner}.
     * 
     * @param benchmarkCases  a list of benchmark cases
     * @param progressMonitor the progress monitor for progress updates
     */
    public BenchmarkRunner(List<BenchmarkCase> benchmarkCases, IProgressMonitor progressMonitor) {
        this.benchmarkCases = benchmarkCases;
        this.benchmarkCaseMonitor = SubMonitor.convert(progressMonitor, benchmarkCases.size());
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

        if (pluginPreferences.getRepetitions() <= 0) {
            foundErrors.add("Number of repetitions must be greater zero.");
        }
        if (pluginPreferences.getDefaultTimeout() <= 0) {
            foundErrors.add("Default timeout in plugin preferences must be greater zero.");
        }
        if (pluginPreferences.getMaxMemorySize() <= 0) {
            foundErrors.add("Max JVM memory size in plugin preferences must be greater zero.");
        }

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
        for (BenchmarkCase benchmarkCase : benchmarkCases) {

            if (benchmarkCase.getBenchmarkCaseName().isEmpty()) {
                foundErrors.add(String.format("Benchmark case '%s': The benchmark case needs to be named",
                        benchmarkCase.getEclipseProject().getName()));
            }

            Path projectOutputPath = benchmarkCase.getEclipseProject().getOutputPath();
            try {
                if (Files.exists(projectOutputPath) && Files.list(projectOutputPath).count() > 0) {
                    try (URLClassLoader classLoader = ReflectionUtils
                            .createClassLoader(benchmarkCase.getEclipseProject())) {
                        if (benchmarkCase.getMetamodelsRegistrationMethod().isEmpty()) {
                            foundErrors.add(
                                    String.format("Benchmark case '%s': Meta model registration method needs to be set",
                                            benchmarkCase.getBenchmarkCaseName()));
                        } else {
                            try {
                                ReflectionUtils.getStaticMethodByName(classLoader,
                                        benchmarkCase.getMetamodelsRegistrationMethod(), ResourceSet.class,
                                        OperationalStrategy.class);
                            } catch (NoSuchMethodException e) {
                                foundErrors.add(String.format(
                                        "Benchmark case '%s': Meta model registration method '%s' doesn't exist",
                                        benchmarkCase.getBenchmarkCaseName(),
                                        benchmarkCase.getMetamodelsRegistrationMethod()));
                            }
                        }
                        if (benchmarkCase.isFwdActive()) {
                            if (!benchmarkCase.getFwdIncrementalEditMethod().isEmpty()) {
                                try {
                                    ReflectionUtils.getStaticMethodByName(classLoader,
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
                                    ReflectionUtils.getStaticMethodByName(classLoader,
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
        Map<OperationalizationType, Function<BenchmarkCase, Boolean>> includeOps = new HashMap<>();
        includeOps.put(OperationalizationType.MODELGEN, BenchmarkCase::isModelgenIncludeReport);
        includeOps.put(OperationalizationType.INITIAL_FWD, BenchmarkCase::isInitialFwdActive);
        includeOps.put(OperationalizationType.INITIAL_BWD, BenchmarkCase::isInitialBwdActive);
        includeOps.put(OperationalizationType.FWD, (bc) -> bc.isFwdActive() && bc.getFwdIncrementalEditMethod() == "");
        includeOps.put(OperationalizationType.BWD, (bc) -> bc.isBwdActive() && bc.getFwdIncrementalEditMethod() == "");
        includeOps.put(OperationalizationType.INCREMENTAL_FWD,
                (bc) -> bc.isFwdActive() && bc.getFwdIncrementalEditMethod() != "");
        includeOps.put(OperationalizationType.INCREMENTAL_BWD,
                (bc) -> bc.isBwdActive() && bc.getFwdIncrementalEditMethod() != "");
        includeOps.put(OperationalizationType.FWD_OPT, BenchmarkCase::isFwdOptActive);
        includeOps.put(OperationalizationType.BWD_OPT, BenchmarkCase::isBwdOptActive);
        includeOps.put(OperationalizationType.CO, BenchmarkCase::isCoActive);
        includeOps.put(OperationalizationType.CC, BenchmarkCase::isCoActive);
        boolean anyReportFileErrors = false;
        for (BenchmarkCase benchmarkCase : benchmarkCases) {
            for (Map.Entry<OperationalizationType, Function<BenchmarkCase, Boolean>> includeOp : includeOps
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
                for (BenchmarkCase bc : benchmarkCases) {
                    benchmarkCaseMonitor.setTaskName("Benchmarking " + bc.getBenchmarkCaseName());
                    benchmarkCase(bc, benchmarkCaseMonitor.split(1));
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
     * @param bc             the benchmark case preferences
     * @throws IOException          if writing the report fails
     * @throws InterruptedException
     */
    private void benchmarkCase(BenchmarkCase bc, IProgressMonitor monitor) throws IOException, InterruptedException {
        // split the monitor into number of active ops
        SubMonitor operationalizationMonitor = SubMonitor.convert(monitor,
                (int) Arrays
                        .asList(bc.isInitialFwdActive(), bc.isInitialBwdActive(), bc.isFwdActive(), bc.isBwdActive(),
                                bc.isFwdOptActive(), bc.isBwdOptActive(), bc.isCcActive(), bc.isCoActive())
                        .stream().filter(b -> b).count() + 1);

        BiFunction<Integer, Integer, BenchmarkRunParameters> runParametersGenerator = null;
        ReportBuilder reportBuilder = null;

        // we need the classpaths of the plugin and the TGG project
        String classpathArg = createJavaClasspathArgument(bc.getEclipseProject());

        // MODELGEN
        runParametersGenerator = (Integer modelSize, Integer repetition) -> {
            BenchmarkRunParameters runParameters = createGenericRunParameters(bc, modelSize, repetition);
            runParameters.setOperationalization(OperationalizationType.MODELGEN);
            runParameters.setTimeout(bc.getEffectiveModelgenTimeout());
            Map<String, Integer> ruleCount = new HashMap<>();
            for (Pair<String, Integer> rc : bc.getModelgenRuleCount()) {
                ruleCount.put(rc.getKey(), rc.getValue());
            }
            runParameters.setRuleCount(ruleCount);
            return runParameters;
        };
        reportBuilder = getReportBuilderByPath(StringUtils.createPathFromString(pluginPreferences.getReportFilePath(),
                eclipseWorkspace.getLocation(), bc, OperationalizationType.MODELGEN, startTime));
        Integer maxSuccessfullModelSize = benchmarkOperationalization(runParametersGenerator,
                bc.getModelgenModelSizes(), bc.getModelgenModelSizes().get(bc.getModelgenModelSizes().size() - 1),
                pluginPreferences.getRepetitions(), bc.isModelgenIncludeReport(), reportBuilder, classpathArg,
                operationalizationMonitor.split(1));

        // INITIAL_FWD
        if (bc.isInitialFwdActive()) {
            runParametersGenerator = (Integer modelSize, Integer repetition) -> {
                BenchmarkRunParameters runParameters = createGenericRunParameters(bc, modelSize, repetition);
                runParameters.setOperationalization(OperationalizationType.INITIAL_FWD);
                runParameters.setTimeout(bc.getEffectiveInitialFwdTimeout());
                return runParameters;
            };
            reportBuilder = getReportBuilderByPath(
                    StringUtils.createPathFromString(pluginPreferences.getReportFilePath(),
                            eclipseWorkspace.getLocation(), bc, OperationalizationType.INITIAL_FWD, startTime));
            benchmarkOperationalization(runParametersGenerator, bc.getModelgenModelSizes(),
                    min(maxSuccessfullModelSize, bc.getEffectiveInitialFwdMaxModelSize()),
                    pluginPreferences.getRepetitions(), true, reportBuilder, classpathArg,
                    operationalizationMonitor.split(1));
        }

        // INITIAL_BWD
        if (bc.isInitialBwdActive()) {
            runParametersGenerator = (Integer modelSize, Integer repetition) -> {
                BenchmarkRunParameters runParameters = createGenericRunParameters(bc, modelSize, repetition);
                runParameters.setOperationalization(OperationalizationType.INITIAL_BWD);
                runParameters.setTimeout(bc.getEffectiveInitialBwdTimeout());
                return runParameters;
            };
            reportBuilder = getReportBuilderByPath(
                    StringUtils.createPathFromString(pluginPreferences.getReportFilePath(),
                            eclipseWorkspace.getLocation(), bc, OperationalizationType.INITIAL_BWD, startTime));
            benchmarkOperationalization(runParametersGenerator, bc.getModelgenModelSizes(),
                    min(maxSuccessfullModelSize, bc.getEffectiveInitialBwdMaxModelSize()),
                    pluginPreferences.getRepetitions(), true, reportBuilder, classpathArg,
                    operationalizationMonitor.split(1));
        }

        // FWD, INCREMENTAL_FWD
        if (bc.isFwdActive()) {
            runParametersGenerator = (Integer modelSize, Integer repetition) -> {
                BenchmarkRunParameters runParameters = createGenericRunParameters(bc, modelSize, repetition);
                runParameters.setTimeout(bc.getEffectiveFwdTimeout());
                if (bc.getFwdIncrementalEditMethod() == "") {
                    runParameters.setOperationalization(OperationalizationType.FWD);
                } else {
                    runParameters.setOperationalization(OperationalizationType.INCREMENTAL_FWD);
                    runParameters.setIncrementalEditMethod(bc.getFwdIncrementalEditMethod());
                }
                return runParameters;
            };
            reportBuilder = getReportBuilderByPath(StringUtils.createPathFromString(
                    pluginPreferences.getReportFilePath(), eclipseWorkspace.getLocation(), bc,
                    bc.getFwdIncrementalEditMethod() == "" ? OperationalizationType.FWD
                            : OperationalizationType.INCREMENTAL_FWD,
                    startTime));
            benchmarkOperationalization(runParametersGenerator, bc.getModelgenModelSizes(),
                    min(maxSuccessfullModelSize, bc.getEffectiveFwdMaxModelSize()), pluginPreferences.getRepetitions(),
                    true, reportBuilder, classpathArg, operationalizationMonitor.split(1));
        }

        // BWD, INCREMENTAL_BWD
        if (bc.isBwdActive()) {
            runParametersGenerator = (Integer modelSize, Integer repetition) -> {
                BenchmarkRunParameters runParameters = createGenericRunParameters(bc, modelSize.intValue(), repetition);
                runParameters.setTimeout(bc.getEffectiveBwdTimeout());
                if (bc.getBwdIncrementalEditMethod() == "") {
                    runParameters.setOperationalization(OperationalizationType.BWD);
                } else {
                    runParameters.setOperationalization(OperationalizationType.INCREMENTAL_BWD);
                    runParameters.setIncrementalEditMethod(bc.getBwdIncrementalEditMethod());
                }
                return runParameters;
            };
            reportBuilder = getReportBuilderByPath(StringUtils.createPathFromString(
                    pluginPreferences.getReportFilePath(), eclipseWorkspace.getLocation(), bc,
                    bc.getBwdIncrementalEditMethod() == "" ? OperationalizationType.BWD
                            : OperationalizationType.INCREMENTAL_BWD,
                    startTime));
            benchmarkOperationalization(runParametersGenerator, bc.getModelgenModelSizes(),
                    min(maxSuccessfullModelSize, bc.getEffectiveBwdMaxModelSize()), pluginPreferences.getRepetitions(),
                    true, reportBuilder, classpathArg, operationalizationMonitor.split(1));
        }

        // FWD_OPT
        if (bc.isFwdOptActive()) {
            runParametersGenerator = (Integer modelSize, Integer repetition) -> {
                BenchmarkRunParameters runParameters = createGenericRunParameters(bc, modelSize, repetition);
                runParameters.setOperationalization(OperationalizationType.FWD_OPT);
                runParameters.setTimeout(bc.getEffectiveFwdOptTimeout());
                return runParameters;
            };
            reportBuilder = getReportBuilderByPath(
                    StringUtils.createPathFromString(pluginPreferences.getReportFilePath(),
                            eclipseWorkspace.getLocation(), bc, OperationalizationType.FWD_OPT, startTime));
            benchmarkOperationalization(runParametersGenerator, bc.getModelgenModelSizes(),
                    min(maxSuccessfullModelSize, bc.getEffectiveFwdOptMaxModelSize()),
                    pluginPreferences.getRepetitions(), true, reportBuilder, classpathArg,
                    operationalizationMonitor.split(1));
        }

        // BWD_OPT
        if (bc.isBwdOptActive()) {
            runParametersGenerator = (Integer modelSize, Integer repetition) -> {
                BenchmarkRunParameters runParameters = createGenericRunParameters(bc, modelSize, repetition);
                runParameters.setOperationalization(OperationalizationType.BWD_OPT);
                runParameters.setTimeout(bc.getEffectiveBwdOptTimeout());
                return runParameters;
            };
            reportBuilder = getReportBuilderByPath(
                    StringUtils.createPathFromString(pluginPreferences.getReportFilePath(),
                            eclipseWorkspace.getLocation(), bc, OperationalizationType.BWD_OPT, startTime));
            benchmarkOperationalization(runParametersGenerator, bc.getModelgenModelSizes(),
                    min(maxSuccessfullModelSize, bc.getEffectiveBwdOptMaxModelSize()),
                    pluginPreferences.getRepetitions(), true, reportBuilder, classpathArg,
                    operationalizationMonitor.split(1));
        }

        // CC
        if (bc.isCcActive()) {
            runParametersGenerator = (Integer modelSize, Integer repetition) -> {
                BenchmarkRunParameters runParameters = createGenericRunParameters(bc, modelSize, repetition);
                runParameters.setOperationalization(OperationalizationType.CC);
                runParameters.setTimeout(bc.getEffectiveCcTimeout());
                return runParameters;
            };
            reportBuilder = getReportBuilderByPath(
                    StringUtils.createPathFromString(pluginPreferences.getReportFilePath(),
                            eclipseWorkspace.getLocation(), bc, OperationalizationType.CC, startTime));
            benchmarkOperationalization(runParametersGenerator, bc.getModelgenModelSizes(),
                    min(maxSuccessfullModelSize, bc.getEffectiveCcMaxModelSize()), pluginPreferences.getRepetitions(),
                    true, reportBuilder, classpathArg, operationalizationMonitor.split(1));
        }

        // CO
        if (bc.isCoActive()) {
            runParametersGenerator = (Integer modelSize, Integer repetition) -> {
                BenchmarkRunParameters runParameters = createGenericRunParameters(bc, modelSize, repetition);
                runParameters.setOperationalization(OperationalizationType.CO);
                runParameters.setTimeout(bc.getEffectiveCoTimeout());
                return runParameters;
            };
            reportBuilder = getReportBuilderByPath(
                    StringUtils.createPathFromString(pluginPreferences.getReportFilePath(),
                            eclipseWorkspace.getLocation(), bc, OperationalizationType.CO, startTime));
            benchmarkOperationalization(runParametersGenerator, bc.getModelgenModelSizes(),
                    min(maxSuccessfullModelSize, bc.getEffectiveCoMaxModelSize()), pluginPreferences.getRepetitions(),
                    true, reportBuilder, classpathArg, operationalizationMonitor.split(1));
        }
    }

    private Integer min(Integer i1, Integer i2) {
        return i1 < i2 ? i1 : i2;
    }

    /**
     * Creates a command line argument of the classpath to start a java process.
     *
     * The problem with the default implementation of the JavaProcessBuilder class
     * is, that the paths are checked for existence. I like to use wildcard
     * classpaths 'path/*' and therefore I created a custom implementation.
     *
     * @param eclipseProject the Eclipse project
     * @return a concatenated string of classpaths
     */
    private String createJavaClasspathArgument(EclipseJavaProject eclipseProject) {
        Set<String> classpathSet = new HashSet<>();
        eclipseProject.getAllClasspaths().forEach(url -> {
            Path absPath = Paths.get(url.getPath()).toAbsolutePath();
            if (!Files.isDirectory(absPath)) {
                // when the path is a file turn it into a wildcard path
                // this will significantly reduce the number of paths
                classpathSet.add(absPath.getParent().resolve("*").toString());
            } else {
                classpathSet.add(absPath.toString() + "/");
            }
        });

        String sep = System.getProperty("os.name", "unknown").toLowerCase().contains("windows") ? ";" : ":";
        String[] classpathsArray = classpathSet.toArray(new String[classpathSet.size() + 1]);
        classpathsArray[classpathsArray.length - 1] = pluginClasspathArgument;
        String classpathArgument = String.join(sep, classpathsArray);

        return classpathArgument;
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
            ReportBuilder reportBuilder, String classpathArgument, IProgressMonitor monitor)
            throws IOException, InterruptedException {
        modelSizes = modelSizes.stream().filter(ms -> ms <= maxEffectiveModelSize).collect(Collectors.toList());

        SubMonitor singleRunMonitor = SubMonitor.convert(monitor, modelSizes.size() * numberOfRepetitions);

        Integer lastSuccessfullModelSize = -1;

        opBenchmark: for (Integer modelSize : modelSizes) {
            String lastError = null;
            BenchmarkResult benchmarkResult = null;

            repetition: for (Integer repetition = 0; repetition < numberOfRepetitions; repetition++) {
                BenchmarkRunParameters runParameters = runParametersGenerator.apply(modelSize, repetition);
                singleRunMonitor.subTask(String.format("OP=%s, SIZE=%s, RUN=%s", runParameters.getOperationalization(),
                        modelSize, repetition + 1));
                SingleRunResult singleRunResult = runBenchmarkChildProcess(runParameters, classpathArgument);
                // consume tick
                singleRunMonitor.split(1);

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

        // consume the rest of the ticks
        singleRunMonitor.done();

        return lastSuccessfullModelSize;
    }

    private SingleRunResult runBenchmarkChildProcess(BenchmarkRunParameters runParameters, String classpathArgument)
            throws InterruptedException {
        SingleRunResult result = new SingleRunResult();
        result.setError("Unknown error");

        // helper to get the result back from an event
        final Stack<SingleRunResult> resultWrapper = new Stack<>();

        try (EventBusServer eventBus = new EventBusServer.Builder().id("BenchmarkServerProcess")
                .onError(new RethrowingErrorListener()).listen(EVENT_BUS_PORT).build()) {
            eventBus.on("requestRunParameters", e -> {
                eventBus.trigger("sendRunParameters", runParameters);
            });
            eventBus.on("result", e -> {
                resultWrapper.add(e.getData(SingleRunResult.class));
            });

            LOG.debug("Starting benchmark in child process");

            JavaProcess javaProcess = JavaProcess.newBuilder().mainClass(BenchmarkProcess.class.getName())
                    .addJvmArg("-Xmx" + pluginPreferences.getMaxMemorySize() + "m")
                    .addJvmArg("-Xms" + pluginPreferences.getMaxMemorySize() + "m").addJvmArg("-classpath")
                    .addJvmArg(classpathArgument).arguments("--loglevel", LOG.getLevel().getStandardLevel().toString())
                    .pipeStdout().pipeStderr().build();

            try {
                javaProcess.waitFor();
            } catch (InterruptedException e) {
                LOG.warn("Benchmark process interrupted, destroying subprocess...");
                javaProcess.destroy();
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
            LOG.error("Communication with benchmark child process failed.");
            result.setError(e3.getMessage());
        }

        return result;
    }

    /**
     * Creates an instance of {@link BenchmarkRunParameters}.
     * 
     * @param project    the eclipse project the benchmark case belongs to
     * @param bc         the benchmark case
     * @param modelSize  the model size
     * @param repetition the repetition number
     * @return a generic instance of {@link BenchmarkRunParameters}
     */
    private BenchmarkRunParameters createGenericRunParameters(BenchmarkCase bc, Integer modelSize, Integer repetition) {
        BenchmarkRunParameters runParameters = new BenchmarkRunParameters();
        EclipseTggProject eclipseProject = bc.getEclipseProject();

        runParameters.setBenchmarkCaseName(bc.getBenchmarkCaseName());
        runParameters.setTggProject(eclipseProject.getName());
        runParameters.setModelInstancesbasePath(
                eclipseWorkspace.getLocation().relativize(modelInstancesBasePath).toString());
        runParameters.setWorkspacePath(eclipseProject.getProjectPath().toAbsolutePath().getParent().toString());
        runParameters.setPatternMatchingEngine(bc.getPatternMatchingEngine());
        runParameters.setMetamodelsRegistrationMethod(bc.getMetamodelsRegistrationMethod());
        runParameters.setModelSize(modelSize.intValue());
        runParameters.setRepetition(repetition.intValue() + 1);

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