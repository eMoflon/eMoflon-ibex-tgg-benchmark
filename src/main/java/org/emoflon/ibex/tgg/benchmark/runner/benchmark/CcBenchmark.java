package org.emoflon.ibex.tgg.benchmark.runner.benchmark;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkRunParameters;
import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.CC_App;
import org.emoflon.ibex.tgg.operational.strategies.opt.cc.CC;

public class CcBenchmark extends Benchmark<CC> {

    public CcBenchmark(BenchmarkRunParameters runParameters) {
        super(runParameters);
    }

    @Override
    protected void createOperationalizationInstance() throws BenchmarkFaildException {
        try {
            op = new CC_App(runParameters);
        } catch (IOException e) {
            LOG.debug("TGG={}, OP={}, SIZE={}, RUN={}: {}", runParameters.getProjectName(),
                    runParameters.getOperationalization(), new Integer(runParameters.getModelSize()),
                    runParameters.getRepetition(), e.getMessage());
            runResult.setError(e.getMessage());
            throw new BenchmarkFaildException();
        }
    }
}
