package org.emoflon.ibex.tgg.benchmark.runner.benchmark;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkRunParameters;
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
	    LOG.debug("TGG={}, OP={}, SIZE={}, RUN={}: Create an instance of SYNC_App", runParameters.getProjectName(),
                runParameters.getOperationalization(), new Integer(runParameters.getModelSize()),
                runParameters.getRepetition());
		try {
			SYNC_App sync = new SYNC_App(runParameters);
			op = sync;

			// apply incremental edit method
			if (sync.isIncremental()) {
				Resource model = sync.isForward() ? sync.getSourceResource() : sync.getTargetResource();
				Method incrementalEditMethod = org.emoflon.ibex.tgg.benchmark.utils.ReflectionUtils.getMethodByName(
						classLoader, runParameters.getIncrementalEditClassName(),
						runParameters.getIncrementalEditMethodName(), EObject.class);
				incrementalEditMethod.invoke(null, new Object[] { model.getContents().get(0) });
			}
		} catch (NoSuchMethodException | IOException | IllegalAccessException | IllegalArgumentException
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
