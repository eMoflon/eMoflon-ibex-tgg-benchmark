package org.emoflon.tggbenchmark.runner.benchmark.strategies;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emoflon.ibex.tgg.operational.benchmark.FullBenchmarkLogger;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.strategies.sync.BWD_Strategy;
import org.emoflon.ibex.tgg.operational.strategies.sync.FWD_Strategy;
import org.emoflon.ibex.tgg.operational.strategies.sync.SYNC;
import org.emoflon.tggbenchmark.runner.BenchmarkRunParameters;
import org.emoflon.tggbenchmark.runner.OperationalizationType;

public class SYNC_App extends SYNC {

	protected final BenchmarkRunParameters runParameters;
	protected final boolean isForward;
	protected final boolean isIncremental;

	public SYNC_App(BenchmarkRunParameters runParameters) throws IOException {
		super(new IbexOptions().projectName(runParameters.getTggProject()).projectPath(runParameters.getTggProject())
				.workspacePath(runParameters.getWorkspacePath().toString())
				.setBenchmarkLogger(new FullBenchmarkLogger()));

		this.runParameters = runParameters;
		this.isForward = runParameters.getOperationalization() == OperationalizationType.FWD
				|| runParameters.getOperationalization() == OperationalizationType.INITIAL_FWD
				|| runParameters.getOperationalization() == OperationalizationType.INCREMENTAL_FWD;
		this.isIncremental = runParameters.getOperationalization() == OperationalizationType.INCREMENTAL_FWD
				|| runParameters.getOperationalization() == OperationalizationType.INCREMENTAL_BWD;

		strategy = isForward ? new FWD_Strategy() : new BWD_Strategy();
	}

	@Override
	protected void registerUserMetamodels() throws IOException {
		StrategiesUtils.registerUserMetamodels(rs, this, getClass().getClassLoader(),
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
