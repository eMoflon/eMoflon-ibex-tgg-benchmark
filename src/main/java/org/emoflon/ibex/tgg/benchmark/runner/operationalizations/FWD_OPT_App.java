package org.emoflon.ibex.tgg.benchmark.runner.operationalizations;

import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Path;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkRunParameters;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.strategies.opt.FWD_OPT;

public class FWD_OPT_App extends FWD_OPT {

	private final BenchmarkRunParameters runParameters;
	private final URLClassLoader classLoader;

	public FWD_OPT_App(BenchmarkRunParameters runParameters) throws IOException {
		super(new IbexOptions().projectName(runParameters.getProjectName()).projectPath(runParameters.getProjectName())
				.workspacePath(runParameters.getWorkspacePath().toString()));

		this.runParameters = runParameters;
		this.classLoader = new URLClassLoader(runParameters.getClassPaths());
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
		s = loadResource(instPath.resolve("src.xmi").toString());
		t = createResource(instPath.resolve("trg.xmi").toString());
		c = createResource(instPath.resolve("corr.xmi").toString());
		p = createResource(instPath.resolve("protocol.xmi").toString());

		EcoreUtil.resolveAll(rs);
	}

	@Override
	public void saveModels() {
		// only models created with MODELGEN need to be saved
	}

	@Override
	public void terminate() throws IOException {
		super.terminate();
		if (classLoader != null) {
			classLoader.close();
		}
	}
}
