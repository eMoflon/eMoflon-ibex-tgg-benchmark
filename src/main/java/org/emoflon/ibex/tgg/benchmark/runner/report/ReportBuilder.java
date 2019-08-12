package org.emoflon.ibex.tgg.benchmark.runner.report;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
    }

    public void addEntry(BenchmarkResult benchmarkResult) throws IOException {
        benchmarkResults.add(benchmarkResult);

        LOG.debug("Add result to report file '{}'", reportFilePath);
    }

    protected abstract void createReportFile() throws IOException;

    public abstract void close() throws IOException;
}