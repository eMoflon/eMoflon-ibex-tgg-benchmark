package org.emoflon.tggbenchmark.runner.benchmark.strategies;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emoflon.ibex.tgg.operational.benchmark.FullBenchmarkLogger;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGEN;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGENStopCriterion;
import org.emoflon.ibex.tgg.operational.updatepolicy.RandomMatchUpdatePolicy;
import org.emoflon.ibex.tgg.operational.updatepolicy.TimedUpdatePolicy;
import org.emoflon.tggbenchmark.runner.BenchmarkRunParameters;
import org.emoflon.tggbenchmark.runner.OperationalizationUtils;

public class MODELGEN_App extends MODELGEN {

    private final BenchmarkRunParameters runParameters;

    public MODELGEN_App(BenchmarkRunParameters runParameters) throws IOException {
        super(new IbexOptions().projectName(runParameters.getTggProject()).projectPath(runParameters.getTggProject())
                .workspacePath(runParameters.getWorkspacePath().toString())
                .setBenchmarkLogger(new FullBenchmarkLogger()));

        this.runParameters = runParameters;
    }

    public MODELGENStopCriterion createStopCriterion(int modelSize, Map<String, Integer> ruleCount) {
        MODELGENStopCriterion stop = new MODELGENStopCriterion(this.getTGG());
        stop.setMaxElementCount(modelSize);
        for (Map.Entry<String, Integer> pair : ruleCount.entrySet()) {
            stop.setMaxRuleCount(pair.getKey(), pair.getValue());
        }
        return stop;
    }

    @Override
    protected void registerUserMetamodels() throws IOException {
        OperationalizationUtils.registerUserMetamodels(rs, this, getClass().getClassLoader(),
                runParameters.getMetamodelsRegistrationClassName(),
                runParameters.getMetamodelsRegistrationMethodName());

        loadAndRegisterCorrMetamodel(options.projectPath() + "/model/" + options.projectPath() + ".ecore");
    }

    @Override
    public void loadModels() throws IOException {
        Path instPath = runParameters.getModelInstancesPath();
        s = createResource(instPath.resolve("src.xmi").toString());
        t = createResource(instPath.resolve("trg.xmi").toString());
        c = createResource(instPath.resolve("corr.xmi").toString());
        p = createResource(instPath.resolve("protocol.xmi").toString());

        EcoreUtil.resolveAll(rs);

        setStopCriterion(createStopCriterion(runParameters.getModelSize(), runParameters.getRuleCount()));
        setUpdatePolicy(
                new TimedUpdatePolicy(new RandomMatchUpdatePolicy(10), runParameters.getTimeout(), TimeUnit.SECONDS));
    }
}