package org.emoflon.ibex.tgg.benchmark.runner.benchmark;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkRunParameters;
import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.Initial_SYNC_App;
import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.OperationalizationType;
import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.SYNC_App;
import org.emoflon.ibex.tgg.operational.strategies.sync.SYNC;

public class SyncBenchmark extends Benchmark<SYNC> {

	protected final URLClassLoader classLoader;

	public SyncBenchmark(BenchmarkRunParameters runParameters) {
		super(runParameters);
		this.classLoader = new URLClassLoader(runParameters.getClassPaths());
	}

	@Override
	protected void createOperationalizationInstance() throws BenchmarkFaildException {
		try {
			SYNC_App sync = null;
			if (runParameters.getOperationalization() == OperationalizationType.INITIAL_FWD
					|| runParameters.getOperationalization() == OperationalizationType.INITIAL_BWD) {
				sync = new Initial_SYNC_App(runParameters);
			} else {
				sync = new SYNC_App(runParameters);
			}
			op = sync;

			// apply incremental edit method
			if (sync.isIncremental()) {
				Resource model = sync.isForward() ? sync.getSourceResource() : sync.getTargetResource();
				Method incrementalEditMethod = org.emoflon.ibex.tgg.benchmark.utils.RefelctionUtils.getMethodForName(
						classLoader, runParameters.getIncrementalEditClassName(),
						runParameters.getIncrementalEditMethodName(), EObject.class);
				incrementalEditMethod.invoke(null, new Object[] { model.getContents().get(0) });
			}
		} catch (ClassNotFoundException e) {
			String msg = String.format(
					"Class '%s' containing the incremental edit method not found. Check your benchmark preferences.",
					runParameters.getIncrementalEditClassName());
			LOG.debug("TGG={}, OP={}, SIZE={}, RUN={}: {}", runParameters.getProjectName(),
					runParameters.getOperationalization(), new Integer(runParameters.getModelSize()),
					runParameters.getRepetition(), msg);
			runResult.setError(msg);
			throw new BenchmarkFaildException();
		} catch (NoSuchMethodException e) {
			String msg = String.format(
					"Class '%s' doesn't contain a method '%s' or its signature is wrong. Check your benchmark preferences.",
					runParameters.getIncrementalEditClassName(), runParameters.getIncrementalEditMethodName());
			LOG.debug("TGG={}, OP={}, SIZE={}, RUN={}: {}", runParameters.getProjectName(),
					runParameters.getOperationalization(), new Integer(runParameters.getModelSize()),
					runParameters.getRepetition(), msg);
			runResult.setError(msg);
			throw new BenchmarkFaildException();
		} catch (IOException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			LOG.debug("TGG={}, OP={}, SIZE={}, RUN={}: {}", runParameters.getProjectName(),
					runParameters.getOperationalization(), new Integer(runParameters.getModelSize()),
					runParameters.getRepetition(), e.getMessage());
			runResult.setError(e.getMessage());
			throw new BenchmarkFaildException();
		}
	}

	@Override
	protected void terminateOperationalization() {
		super.terminateOperationalization();
		if (classLoader != null) {
			try {
				classLoader.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
}
