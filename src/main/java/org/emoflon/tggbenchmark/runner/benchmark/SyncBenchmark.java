package org.emoflon.tggbenchmark.runner.benchmark;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.ibex.tgg.operational.strategies.sync.SYNC;
import org.emoflon.tggbenchmark.runner.BenchmarkRunParameters;
import org.emoflon.tggbenchmark.runner.benchmark.strategies.SYNC_App;
import org.emoflon.tggbenchmark.utils.ReflectionUtils;

public class SyncBenchmark extends Benchmark<SYNC> {

    public SyncBenchmark(BenchmarkRunParameters runParameters) {
        super(runParameters);
    }

    @Override
    protected void createOperationalizationInstance() throws BenchmarkFailedException {
        LOG.debug("CASE={}, OP={}, SIZE={}, RUN={}: Create an instance of SYNC_App",
                runParameters.getBenchmarkCaseName(), runParameters.getOperationalization(),
                Integer.valueOf(runParameters.getModelSize()), runParameters.getRepetition());
        try {
            op = new SYNC_App(runParameters);
        } catch (IOException e) {
            throw new BenchmarkFailedException("Failed to create SYNC_APP. Reason: " + e.getMessage());
        }
    }

    @Override
    protected void postInitialization() throws BenchmarkFailedException {
        SYNC_App sync = (SYNC_App) op;

        // apply incremental edit method
        if (sync.isIncremental()) {
            try {
                Resource model = sync.isForward() ? sync.getSourceResource() : sync.getTargetResource();
                Method incrementalEditMethod = ReflectionUtils.getMethodByName(getClass().getClassLoader(),
                        runParameters.getIncrementalEditClassName(), runParameters.getIncrementalEditMethodName(), true,
                        EObject.class);
                incrementalEditMethod.invoke(null, new Object[] { model.getContents().get(0) });
            } catch (NullPointerException e) {
                throw new BenchmarkFailedException();
            } catch (InvocationTargetException e) {
                throw new BenchmarkFailedException("Error in incremental edit method: " + e.getCause().getMessage());
            } catch (Exception e) {
                throw new BenchmarkFailedException(
                        "Failed to invoke incremental edit method. Reason: " + e.getMessage());
            }
        }
    }
}
