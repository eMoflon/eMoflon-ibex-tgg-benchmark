package org.emoflon.tggbenchmark.runner.benchmark;

import java.io.IOException;

import org.emoflon.ibex.tgg.operational.strategies.opt.BWD_OPT;
import org.emoflon.tggbenchmark.runner.BenchmarkRunParameters;
import org.emoflon.tggbenchmark.runner.benchmark.strategies.BWD_OPT_App;

public class BwdOptBenchmark extends Benchmark<BWD_OPT> {

    public BwdOptBenchmark(BenchmarkRunParameters runParameters) {
        super(runParameters);
    }

    @Override
    protected void createOperationalizationInstance() throws BenchmarkFailedException {
        LOG.debug("CASE={}, OP={}, SIZE={}, RUN={}: Create an instance of BWD_OPT_App",
                runParameters.getBenchmarkCaseName(), runParameters.getOperationalization(),
                Integer.valueOf(runParameters.getModelSize()), runParameters.getRepetition());
        try {
            op = new BWD_OPT_App(runParameters);
        } catch (IOException e) {
            throw new BenchmarkFailedException("Failed to create operationalization. Reason: " + e.getMessage());
        }
    }
}
