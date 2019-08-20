package org.emoflon.ibex.tgg.benchmark.runner.report;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;

import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ReportBuilder creates a report from a list of benchmark results and saves
 * them to a spreadsheet file.
 *
 * @author Andre Lehmann
 */
public abstract class ReportBuilder {

    protected static final Logger LOG = LoggerFactory.getLogger(Core.PLUGIN_NAME);
    protected final Path reportFilePath;
    protected final LinkedList<BenchmarkResult> benchmarkResults;
    protected final boolean includeErrors;

    /**
     * Constructor for {@link ReportBuilder}.
     * 
     * @param reportFilePath The path for the report file
     * @throws IOException if the report file could not be created or saved
     */
    public ReportBuilder(Path reportFilePath, boolean includeErrors) throws IOException {
        this.reportFilePath = reportFilePath;
        this.includeErrors = includeErrors;
        this.benchmarkResults = new LinkedList<>();

        createReportFile();
        save();
    }

    public void addEntry(BenchmarkResult benchmarkResult) throws IOException {
        benchmarkResults.add(benchmarkResult);

        LOG.debug("Add result to report file '{}'", reportFilePath);
    }

    protected abstract void createReportFile() throws IOException;

    public abstract void save() throws IOException;
    
    public abstract void close() throws IOException;

    protected Double toSeconds(long milliseconds) {
        if (milliseconds > 0L) {
            return roundDouble(milliseconds/1000.0);
        }
        return -1.0;
    }

    protected Double toSeconds(double milliseconds) {
        if (milliseconds > 0.0) {
            return roundDouble(milliseconds/1000.0);
        }
        return -1.0;
    }

    protected Double roundDouble(Double d) {
        return Math.round(d.doubleValue()*1000.0)/1000.0;
    }
}