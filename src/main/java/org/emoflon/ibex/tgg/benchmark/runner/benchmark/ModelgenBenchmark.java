package org.emoflon.ibex.tgg.benchmark.runner.benchmark;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkRunParameters;
import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.MODELGEN_App;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGEN;

public class ModelgenBenchmark extends Benchmark<MODELGEN> {

    public ModelgenBenchmark(BenchmarkRunParameters runParameters) {
        super(runParameters);
    }

    @Override
    protected void createOperationalizationInstance() throws BenchmarkFaildException {
        try {
            op = new MODELGEN_App(runParameters);
        } catch (IOException e) {
            LOG.debug("TGG={}, OP={}, SIZE={}, RUN={}: {}", runParameters.getProjectName(),
                    runParameters.getOperationalization(), new Integer(runParameters.getModelSize()),
                    runParameters.getRepetition(), e.getMessage());
            runResult.setError(e.getMessage());
            throw new BenchmarkFaildException();
        }
    }

    @Override
    protected void measureTimes() throws BenchmarkFaildException {
        super.measureTimes();
        try {
            op.saveModels();
        } catch (IOException e) {
            LOG.debug("TGG={}, OP={}, SIZE={}: Failed to save model. Reason: {}", runParameters.getProjectName(),
                    runParameters.getOperationalization(), new Integer(runParameters.getModelSize()), e.getMessage());
            runResult.setError("Failed to save model. Reason: " + e.getMessage());
        }
    }
}
