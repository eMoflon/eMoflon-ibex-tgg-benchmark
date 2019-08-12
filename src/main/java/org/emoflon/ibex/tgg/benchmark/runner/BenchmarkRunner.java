package org.emoflon.ibex.tgg.benchmark.runner;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.model.EclipseProject;
import org.emoflon.ibex.tgg.benchmark.model.EclipseWorkspace;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;
import org.emoflon.ibex.tgg.benchmark.runner.benchmark.Benchmark;
import org.emoflon.ibex.tgg.benchmark.runner.benchmark.ModelgenBenchmark;
import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.OperationalizationType;
import org.emoflon.ibex.tgg.benchmark.runner.report.CSVReportBuilder;
import org.emoflon.ibex.tgg.benchmark.runner.report.ReportBuilder;
import org.emoflon.ibex.tgg.benchmark.runner.report.ReportFileType;
import org.emoflon.ibex.tgg.operational.strategies.OperationalStrategy;
import org.emoflon.ibex.tgg.benchmark.utils.RefelctionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenchmarkRunner implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(Core.PLUGIN_NAME);

    private final PluginPreferences pluginPreferences;
    private final List<EclipseProject> eclipseProjects;
    private final EclipseWorkspace eclipseWorkspace;
    private ReportBuilder reportBuilder;

    public BenchmarkRunner(EclipseWorkspace eclipseWorkspace, List<EclipseProject> eclipseProjects, PluginPreferences pluginPreferences) throws IOException {
        this.eclipseWorkspace = eclipseWorkspace;
        this.eclipseProjects = eclipseProjects;
        this.pluginPreferences = pluginPreferences;

        if (pluginPreferences.getReportFileType() == ReportFileType.CSV) {
            reportBuilder = new CSVReportBuilder(Paths.get(pluginPreferences.getReportFilePath()),
                    pluginPreferences.isIncludeErrors());
        } else if (pluginPreferences.getReportFileType() == ReportFileType.EXCEL) {
            reportBuilder = new CSVReportBuilder(Paths.get(pluginPreferences.getReportFilePath()),
                    pluginPreferences.isIncludeErrors());
        }
    }

    private LinkedList<LinkedList<BenchmarkRunParameters>> createBenchmarkCaseRunParameters(EclipseProject eclipseProject, BenchmarkCasePreferences bcp) {
        LinkedList<LinkedList<BenchmarkRunParameters>> opRunParameters = new LinkedList<>();

        // for the different model sizes
        for (Integer modelSize : bcp.getModelgenModelSizes()) {

            // MODELGEN
            LinkedList<BenchmarkRunParameters> modelgen = new LinkedList<>();
            for (int repetition = 0; repetition < bcp.getRepetitions(); repetition++) {
                BenchmarkRunParameters runParameters = createGenericRunParameters(eclipseProject, bcp, modelSize.intValue(), repetition);
                runParameters.setOperationalization(OperationalizationType.MODELGEN);
                runParameters.setTimeout(bcp.getFinalModelgenTimeout());
                runParameters.setTggRule(bcp.getModelgenTggRule());
                runParameters.setIncludeInReport(bcp.isModelgenCreateReport());
                modelgen.add(runParameters);
            }
            opRunParameters.add(modelgen);

            // INITIAL_FWD
            if (bcp.isInitialFwdActive()) {
                LinkedList<BenchmarkRunParameters> initialFwd = new LinkedList<>();
                for (int repetition = 0; repetition < bcp.getRepetitions(); repetition++) {
                    BenchmarkRunParameters runParameters = createGenericRunParameters(eclipseProject, bcp, modelSize.intValue(), repetition);
                    runParameters.setOperationalization(OperationalizationType.INITIAL_FWD);
                    runParameters.setTimeout(bcp.getInitialFwdTimeout());
                    initialFwd.add(runParameters);
                }
                opRunParameters.add(initialFwd);
            }

            // INITIAL_BWD
            if (bcp.isInitialBwdActive()) {
                LinkedList<BenchmarkRunParameters> initialBwd = new LinkedList<>();
                for (int repetition = 0; repetition < bcp.getRepetitions(); repetition++) {
                    BenchmarkRunParameters runParameters = createGenericRunParameters(eclipseProject, bcp, modelSize.intValue(), repetition);
                    runParameters.setOperationalization(OperationalizationType.INITIAL_BWD);
                    runParameters.setTimeout(bcp.getInitialBwdTimeout());
                    initialBwd.add(runParameters);
                }
                opRunParameters.add(initialBwd);
            }

            // FWD
            if (bcp.isFwdActive()) {
                LinkedList<BenchmarkRunParameters> fwd = new LinkedList<>();
                for (int repetition = 0; repetition < bcp.getRepetitions(); repetition++) {
                    BenchmarkRunParameters runParameters = createGenericRunParameters(eclipseProject, bcp, modelSize.intValue(), repetition);
                    if (bcp.getFwdIncrementalEditMethod() == "") {
                        runParameters.setOperationalization(OperationalizationType.FWD);
                    } else {
                        runParameters.setOperationalization(OperationalizationType.INCREMENTAL_FWD);
                        runParameters.setIncrementalEditMethod(bcp.getFwdIncrementalEditMethod());
                    }
                    runParameters.setTimeout(bcp.getFwdTimeout());
                    fwd.add(runParameters);
                }
                opRunParameters.add(fwd);
            }
            
            // BWD
            if (bcp.isBwdActive()) {
                LinkedList<BenchmarkRunParameters> bwd = new LinkedList<>();
                for (int repetition = 0; repetition < bcp.getRepetitions(); repetition++) {
                    BenchmarkRunParameters runParameters = createGenericRunParameters(eclipseProject, bcp, modelSize.intValue(), repetition);
                    if (bcp.getBwdIncrementalEditMethod() == "") {
                        runParameters.setOperationalization(OperationalizationType.BWD);
                    } else {
                        runParameters.setOperationalization(OperationalizationType.INCREMENTAL_BWD);
                        runParameters.setIncrementalEditMethod(bcp.getBwdIncrementalEditMethod());
                    }
                    runParameters.setTimeout(bcp.getBwdTimeout());
                    bwd.add(runParameters);
                }
                opRunParameters.add(bwd);
            }

            // FWD_OPT
            if (bcp.isFwdOptActive()) {
                LinkedList<BenchmarkRunParameters> fwdOpt = new LinkedList<>();
                for (int repetition = 0; repetition < bcp.getRepetitions(); repetition++) {
                    BenchmarkRunParameters runParameters = createGenericRunParameters(eclipseProject, bcp, modelSize.intValue(), repetition);
                    runParameters.setOperationalization(OperationalizationType.FWD_OPT);
                    runParameters.setTimeout(bcp.getFwdOptTimeout());
                    fwdOpt.add(runParameters);
                }
                opRunParameters.add(fwdOpt);
            }

            // BWD_OPT
            if (bcp.isBwdOptActive()) {
                LinkedList<BenchmarkRunParameters> bwdOpt = new LinkedList<>();
                for (int repetition = 0; repetition < bcp.getRepetitions(); repetition++) {
                    BenchmarkRunParameters runParameters = createGenericRunParameters(eclipseProject, bcp, modelSize.intValue(), repetition);
                    runParameters.setOperationalization(OperationalizationType.BWD_OPT);
                    runParameters.setTimeout(bcp.getBwdOptTimeout());
                    bwdOpt.add(runParameters);
                }
                opRunParameters.add(bwdOpt);
            }

            // CC
            if (bcp.isCcActive()) {
                LinkedList<BenchmarkRunParameters> cc = new LinkedList<>();
                for (int repetition = 0; repetition < bcp.getRepetitions(); repetition++) {
                    BenchmarkRunParameters runParameters = createGenericRunParameters(eclipseProject, bcp, modelSize.intValue(), repetition);
                    runParameters.setOperationalization(OperationalizationType.CC);
                    runParameters.setTimeout(bcp.getCcTimeout());
                    cc.add(runParameters);
                }
                opRunParameters.add(cc);
            }

            // CO
            if (bcp.isCoActive()) {
                LinkedList<BenchmarkRunParameters> co = new LinkedList<>();
                for (int repetition = 0; repetition < bcp.getRepetitions(); repetition++) {
                    BenchmarkRunParameters runParameters = createGenericRunParameters(eclipseProject, bcp, modelSize.intValue(), repetition);
                    runParameters.setOperationalization(OperationalizationType.CO);
                    runParameters.setTimeout(bcp.getCoTimeout());
                    co.add(runParameters);
                }
                opRunParameters.add(co);
            }
        }

        return opRunParameters;
    }

    private BenchmarkRunParameters createGenericRunParameters(EclipseProject project, BenchmarkCasePreferences bcp, int modelSize, int repetition) {
        BenchmarkRunParameters runParameters = new BenchmarkRunParameters();

        runParameters.setProjectName(project.getName());
        runParameters.setModelInstancesPath(Paths.get(pluginPreferences.getModelInstancesPath()));
        runParameters.setWorkspacePath(eclipseWorkspace.getLocation());
        runParameters.setClassPaths(project.getClassPathURLs());
        runParameters.setPatternMatchingEngine(PatternMatchingEngine.valueOf(bcp.getPatternMatchingEngine()));
        runParameters.setMetamodelsRegistrationMethod(bcp.getMetamodelsRegistrationMethod());
        runParameters.setModelSize(modelSize);
        runParameters.setRepetition(repetition);

        return runParameters;
    }

    @Override
    public void run() {
        try {
            // benchmark all projects
            for (EclipseProject eclipseProject : eclipseProjects) {
                
                // benchmark a single project
                BenchmarkCasePreferences bcp = eclipseProject.getBenchmarkCasePreferences();
                LinkedList<LinkedList<BenchmarkRunParameters>> benchmarkCaseRunParameters = createBenchmarkCaseRunParameters(eclipseProject, bcp);
                
                for (LinkedList<BenchmarkRunParameters> operationalizationRunParameters : benchmarkCaseRunParameters) {
                    // benchmark a single operationalization
                    benchmarkSingleOperationalization(operationalizationRunParameters);
                }
            }
        } catch (IOException e) {
            LOG.error("Benchmark process failed. Reason: {}", e.getMessage());
        } finally {
            try {
                reportBuilder.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private void benchmarkSingleOperationalization(LinkedList<BenchmarkRunParameters> repetitions) throws IOException {
        if (!repetitions.isEmpty()) {
            BenchmarkResult benchmarkResult = new BenchmarkResult(repetitions.getFirst());
            OperationalizationType type = repetitions.getFirst().getOperationalization();
            for (BenchmarkRunParameters repetition : repetitions) {
                Benchmark<? extends OperationalStrategy> benchmarkRun = null;
                if (type == OperationalizationType.MODELGEN) {
                    benchmarkRun = new ModelgenBenchmark(repetition);
                } else if (type == OperationalizationType.CC) {

                } else if (type == OperationalizationType.FWD) {

                } else if (type == OperationalizationType.BWD) {

                } else if (type == OperationalizationType.CO) {

                } else if (type == OperationalizationType.INITIAL_FWD) {

                } else if (type == OperationalizationType.INITIAL_BWD) {

                } else if (type == OperationalizationType.FWD_OPT) {

                } else if (type == OperationalizationType.BWD_OPT) {

                }

                SingleRunResult singleRunResult = benchmarkRun.run();

                if (repetition.isIncludeInReport()) {
                    benchmarkResult.addSingleResult(singleRunResult);
                }

                if (singleRunResult.getError() != null) {
                    break;
                }
            }

            if (repetitions.getFirst().isIncludeInReport() && repetitions.getFirst().isIncludeInReport()) {
                reportBuilder.addEntry(benchmarkResult);
            }
        }
    }
}