package org.emoflon.ibex.tgg.benchmark.runner.benchmark;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkRunParameters;
import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.SYNC_App;
import org.emoflon.ibex.tgg.benchmark.utils.ReflectionUtils;
import org.emoflon.ibex.tgg.operational.strategies.sync.SYNC;

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
                System.out.println("############ MODEL: " + model);

                Method incrementalEditMethod = ReflectionUtils.getStaticMethodByName(getClass().getClassLoader(),
                        runParameters.getIncrementalEditClassName(), runParameters.getIncrementalEditMethodName(),
                        EObject.class);
                System.out.println("############### here2" + incrementalEditMethod);
                for (EObject o : model.getContents()) {
                    System.out.println(o);
                }
                System.out.println("############### here3" + incrementalEditMethod);
                System.out.println(model.getContents().get(0));
                // incrementalEditMethod.getDeclaringClass().
                incrementalEditMethod.invoke(null, new Object[] { model.getContents().get(0) });
                System.out.println("########## here4");
            } catch (NullPointerException e) {
                // TODO: remove this
                e.printStackTrace();
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
