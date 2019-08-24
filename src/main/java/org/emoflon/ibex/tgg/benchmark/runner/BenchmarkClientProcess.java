package org.emoflon.ibex.tgg.benchmark.runner;

import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.spi.StandardLevel;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.runner.benchmark.Benchmark;
import org.emoflon.ibex.tgg.benchmark.runner.benchmark.BwdOptBenchmark;
import org.emoflon.ibex.tgg.benchmark.runner.benchmark.CcBenchmark;
import org.emoflon.ibex.tgg.benchmark.runner.benchmark.CoBenchmark;
import org.emoflon.ibex.tgg.benchmark.runner.benchmark.FwdOptBenchmark;
import org.emoflon.ibex.tgg.benchmark.runner.benchmark.ModelgenBenchmark;
import org.emoflon.ibex.tgg.benchmark.runner.benchmark.SyncBenchmark;
import org.emoflon.ibex.tgg.operational.strategies.OperationalStrategy;
import org.terracotta.ipceventbus.event.EventBusClient;

/**
 * BenchmarkClientProcess
 */
public class BenchmarkClientProcess {

    private static final int EVENT_BUS_PORT = 24842;
    private static Logger LOG;

    private static volatile EventBusClient eventBus = null;
    private static volatile Thread benchmarkThread = null;

    static class BenchmarkRunner implements Runnable {

        private final BenchmarkRunParameters runParameters;
        private final EventBusClient eventBus;

        public BenchmarkRunner(BenchmarkRunParameters runParameters, EventBusClient eventBus) {
            this.runParameters = runParameters;
            this.eventBus = eventBus;
        }

        @Override
        public void run() {

            Benchmark<? extends OperationalStrategy> benchmarkRun = null;
            switch (runParameters.getOperationalization()) {
            case MODELGEN:
                benchmarkRun = new ModelgenBenchmark(runParameters);
                break;
            case CC:
                benchmarkRun = new CcBenchmark(runParameters);
                break;
            case CO:
                benchmarkRun = new CoBenchmark(runParameters);
                break;
            case FWD_OPT:
                benchmarkRun = new FwdOptBenchmark(runParameters);
                break;
            case BWD_OPT:
                benchmarkRun = new BwdOptBenchmark(runParameters);
                break;
            case FWD:
            case BWD:
            case INITIAL_FWD:
            case INITIAL_BWD:
            case INCREMENTAL_FWD:
            case INCREMENTAL_BWD:
                benchmarkRun = new SyncBenchmark(runParameters);
                break;
            }

            SingleRunResult singleRunResult = benchmarkRun.run();

            eventBus.trigger("result", singleRunResult);
        }
    }

    public static void main(String[] args) {
        // configure log4j2
        Level logLevel = Level.ALL;
        if (args.length > 0) {
            try {
                logLevel = Level.getLevel(StandardLevel.valueOf(args[0]).toString());
            } catch (IllegalArgumentException e) {
                // keep default
            }
        }
        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(logLevel);
        LOG = LogManager.getLogger(Core.PLUGIN_NAME);
        
        LOG.debug("Benchmark client process started");

        // handle process termination
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (benchmarkThread != null) {
                    benchmarkThread.interrupt();
                    try {
                        benchmarkThread.join(30000);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
                if (eventBus != null) {
                    try {
                        eventBus.close();
                        eventBus = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // try to connect several times
        LOG.debug("Benchmark client tries to connect to benchmark parent process");
        int i = 0;
        while (true) {
            try {
                eventBus = new EventBusClient.Builder().connect("localhost", EVENT_BUS_PORT).build();
            } catch (Exception e) {
                i++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    System.exit(1);
                }
                if (i >= 5) {
                    LOG.error("Benchmark client failed to connect to benchmark parent process");
                    System.exit(1);
                }
                continue;
            }
            break;
        }

        // on receive
        eventBus.on("sendRunParameters", e -> {
            if (benchmarkThread == null) {
                LOG.debug("Benchmark client received run parameters, performing benchmark now");
                BenchmarkRunner runner = new BenchmarkRunner(e.getData(BenchmarkRunParameters.class), eventBus);
                benchmarkThread = new Thread(runner);
                benchmarkThread.start();
            }
        });

        // wait for parameters to receive
        i = 0;
        while (true) {
            eventBus.trigger("requestRunParameters");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e2) {
                System.exit(1);
            }
            if (benchmarkThread != null) {
                break;
            }
            i++;
            if (i >= 5) {
                LOG.error("Benchmark client didn't receive any run parameters. Stopping now");
                System.exit(1);
            }
        }

        // wait for the benchmark thread to exit
        try {
            benchmarkThread.join();
        } catch (InterruptedException e1) {
            System.exit(1);
        }
        System.exit(0);
    }
}
