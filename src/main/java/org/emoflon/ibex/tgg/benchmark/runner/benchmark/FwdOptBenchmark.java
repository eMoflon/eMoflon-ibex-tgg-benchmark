package org.emoflon.ibex.tgg.benchmark.runner.benchmark;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkRunParameters;
import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.FWD_OPT_App;
import org.emoflon.ibex.tgg.operational.strategies.opt.FWD_OPT;

public class FwdOptBenchmark extends Benchmark<FWD_OPT> {

	public FwdOptBenchmark(BenchmarkRunParameters runParameters) {
		super(runParameters);
	}

	@Override
	protected void createOperationalizationInstance() throws BenchmarkFaildException {
		try {
			op = new FWD_OPT_App(runParameters);
		} catch (IOException e) {
			LOG.debug("TGG={}, OP={}, SIZE={}, RUN={}: {}", runParameters.getProjectName(),
					runParameters.getOperationalization(), new Integer(runParameters.getModelSize()),
					runParameters.getRepetition(), e.getMessage());
			runResult.setError(e.getMessage());
			throw new BenchmarkFaildException();
		}
	}
}
