package org.emoflon.tggbenchmark.runner.benchmark;

import java.io.IOException;

import org.emoflon.ibex.tgg.operational.strategies.opt.FWD_OPT;
import org.emoflon.tggbenchmark.runner.BenchmarkRunParameters;
import org.emoflon.tggbenchmark.runner.benchmark.strategies.FWD_OPT_App;

public class FwdOptBenchmark extends Benchmark<FWD_OPT> {

    public FwdOptBenchmark(BenchmarkRunParameters runParameters) {
        super(runParameters);
    }

    @Override
    protected void createOperationalizationInstance() throws BenchmarkFailedException {
        LOG.debug("CASE={}, OP={}, SIZE={}, RUN={}: Create an instance of FWD_OPT_App",
                runParameters.getBenchmarkCaseName(), runParameters.getOperationalization(),
                Integer.valueOf(runParameters.getModelSize()), runParameters.getRepetition());
        try {
            op = new FWD_OPT_App(runParameters);
        } catch (IOException e) {
            throw new BenchmarkFailedException("Failed to create operationalization. Reason: " + e.getMessage());
        }
    }
}
