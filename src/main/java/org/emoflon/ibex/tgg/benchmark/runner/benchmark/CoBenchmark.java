package org.emoflon.ibex.tgg.benchmark.runner.benchmark;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkRunParameters;
import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.CO_App;
import org.emoflon.ibex.tgg.operational.strategies.opt.CO;

public class CoBenchmark extends Benchmark<CO> {

    public CoBenchmark(BenchmarkRunParameters runParameters) {
        super(runParameters);
    }

    @Override
    protected void createOperationalizationInstance() throws BenchmarkFailedException {
        LOG.debug("CASE={}, OP={}, SIZE={}, RUN={}: Create an instance of CO_App", runParameters.getBenchmarkCaseName(),
                runParameters.getOperationalization(), Integer.valueOf(runParameters.getModelSize()),
                runParameters.getRepetition());
        try {
            op = new CO_App(runParameters);
        } catch (IOException e) {
            throw new BenchmarkFailedException("Failed to create operationalization. Reason: " + e.getMessage());
        }
    }
}
