package org.emoflon.ibex.tgg.benchmark.runner.operationalizations;

import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Path;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkRunParameters;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.strategies.sync.BWD_Strategy;
import org.emoflon.ibex.tgg.operational.strategies.sync.FWD_Strategy;
import org.emoflon.ibex.tgg.operational.strategies.sync.SYNC;

public class SYNC_App extends SYNC {

	protected final BenchmarkRunParameters runParameters;
	protected final URLClassLoader classLoader;
	protected final boolean isForward;
	protected final boolean isIncremental;

	public SYNC_App(BenchmarkRunParameters runParameters) throws IOException {
		super(new IbexOptions().projectName(runParameters.getProjectName()).projectPath(runParameters.getProjectName())
				.workspacePath(runParameters.getWorkspacePath().toString()));

		this.runParameters = runParameters;
		this.classLoader = new URLClassLoader(runParameters.getClassPaths());
		this.isForward = runParameters.getOperationalization() == OperationalizationType.FWD
				|| runParameters.getOperationalization() == OperationalizationType.INITIAL_FWD
				|| runParameters.getOperationalization() == OperationalizationType.INCREMENTAL_FWD;
		this.isIncremental = runParameters.getOperationalization() == OperationalizationType.INCREMENTAL_FWD
				|| runParameters.getOperationalization() == OperationalizationType.INCREMENTAL_BWD;

		strategy = isForward ? new FWD_Strategy() : new BWD_Strategy();
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

		if (isIncremental) {
			s = loadResource(instPath.resolve("src.xmi").toString());
			t = loadResource(instPath.resolve("trg.xmi").toString());
			c = loadResource(instPath.resolve("corr.xmi").toString());
			p = loadResource(instPath.resolve("protocol.xmi").toString());
		} else {
			if (isForward) {
				s = loadResource(instPath.resolve("src.xmi").toString());
				t = createResource(instPath.resolve("trg.xmi").toString());
			} else {
				s = createResource(instPath.resolve("src.xmi").toString());
				t = loadResource(instPath.resolve("trg.xmi").toString());
			}
			c = createResource(instPath.resolve("corr.xmi").toString());
			p = createResource(instPath.resolve("protocol.xmi").toString());
		}

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

	/**
	 * @return the isForward
	 */
	public boolean isForward() {
		return isForward;
	}

	/**
	 * @return the isIncremental
	 */
	public boolean isIncremental() {
		return isIncremental;
	}
}
