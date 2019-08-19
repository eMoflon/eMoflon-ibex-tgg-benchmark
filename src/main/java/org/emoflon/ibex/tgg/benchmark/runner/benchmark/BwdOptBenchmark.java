package org.emoflon.ibex.tgg.benchmark.runner.benchmark;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkRunParameters;
import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.BWD_OPT_App;
import org.emoflon.ibex.tgg.operational.strategies.opt.BWD_OPT;

public class BwdOptBenchmark extends Benchmark<BWD_OPT> {

    public BwdOptBenchmark(BenchmarkRunParameters runParameters) {
        super(runParameters);
    }

    @Override
    protected void createOperationalizationInstance() throws BenchmarkFaildException {
        LOG.debug("TGG={}, OP={}, SIZE={}, RUN={}: Create an instance of BWD_OPT_App", runParameters.getProjectName(),
                runParameters.getOperationalization(), new Integer(runParameters.getModelSize()),
                runParameters.getRepetition());
        try {
            op = new BWD_OPT_App(runParameters);
        } catch (IOException e) {
            LOG.debug("TGG={}, OP={}, SIZE={}, RUN={}: {}", runParameters.getProjectName(),
                    runParameters.getOperationalization(), new Integer(runParameters.getModelSize()),
                    runParameters.getRepetition(), e.getMessage());
            runResult.setError(e.getMessage());
            throw new BenchmarkFaildException();
        }
    }
}
