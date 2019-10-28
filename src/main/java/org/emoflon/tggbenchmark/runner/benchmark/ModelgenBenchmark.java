package org.emoflon.tggbenchmark.runner.benchmark;

import java.io.IOException;

import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGEN;
import org.emoflon.tggbenchmark.runner.BenchmarkRunParameters;
import org.emoflon.tggbenchmark.runner.benchmark.strategies.MODELGEN_App;

public class ModelgenBenchmark extends Benchmark<MODELGEN> {

    public ModelgenBenchmark(BenchmarkRunParameters runParameters) {
        super(runParameters);
    }

    @Override
    protected void createOperationalizationInstance() throws BenchmarkFailedException {
        LOG.debug("CASE={}, OP={}, SIZE={}, RUN={}: Create an instance of MODELGEN_App",
                runParameters.getBenchmarkCaseName(), runParameters.getOperationalization(),
                Integer.valueOf(runParameters.getModelSize()), runParameters.getRepetition());
        try {
            op = new MODELGEN_App(runParameters);
        } catch (IOException e) {
            throw new BenchmarkFailedException("Failed to create operationalization. Reason: " + e.getMessage());
        }
    }

    @Override
    protected void measureTimes() throws BenchmarkFailedException {
        super.measureTimes();
        try {
            op.saveModels();
        } catch (IOException e) {
            throw new BenchmarkFailedException("Failed to save models. Reason: " + e.getMessage());
        }
    }
}
