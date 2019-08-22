package org.emoflon.ibex.tgg.benchmark.runner.operationalizations;

import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkRunParameters;
import org.emoflon.ibex.tgg.operational.benchmark.FullBenchmarkLogger;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGEN;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGENStopCriterion;
import org.emoflon.ibex.tgg.operational.updatepolicy.RandomMatchUpdatePolicy;
import org.emoflon.ibex.tgg.operational.updatepolicy.TimedUpdatePolicy;

public class MODELGEN_App extends MODELGEN {

    private final BenchmarkRunParameters runParameters;
    private final URLClassLoader classLoader;

    public MODELGEN_App(BenchmarkRunParameters runParameters) throws IOException {
        super(new IbexOptions().projectName(runParameters.getProjectName()).projectPath(runParameters.getProjectName())
                .workspacePath(runParameters.getWorkspacePath().toString())
                .setBenchmarkLogger(new FullBenchmarkLogger()));

        this.runParameters = runParameters;
        this.classLoader = new URLClassLoader(runParameters.getClassPaths());
    }

    public MODELGENStopCriterion createStopCriterion(String tggName, int size) {
        MODELGENStopCriterion stop = new MODELGENStopCriterion(this.getTGG());
        stop.setMaxElementCount(size);
        stop.setMaxRuleCount(tggName, 1);
        return stop;
    }

    @Override
    protected void registerUserMetamodels() throws IOException {
        OperationalizationUtils.registerUserMetamodels(rs, this, classLoader,
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

        setStopCriterion(createStopCriterion(runParameters.getTggRule(), runParameters.getModelSize()));
        setUpdatePolicy(
                new TimedUpdatePolicy(new RandomMatchUpdatePolicy(10), runParameters.getTimeout(), TimeUnit.SECONDS));
    }

    @Override
    public void terminate() throws IOException {
        super.terminate();
        if (classLoader != null) {
            classLoader.close();
        }
    }
}