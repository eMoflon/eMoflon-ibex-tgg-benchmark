package org.emoflon.ibex.tgg.benchmark.runner.report;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkResult;
import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.OperationalizationType;

/**
 * CSVReportBuilder creates a report from a list of benchmark results and saves
 * them to a CSV file.
 *
 * @author Andre Lehmann
 */
public class CSVReportBuilder extends ReportBuilder {

    private OutputStream reportOutputStream;
    private CSVPrinter reportSheet;

    /**
     * Constructor for {@link CSVReportBuilder}.
     * 
     * @param reportFilePath The path for the report file
     * @throws IOException if the report file could not be created or saved
     */
    public CSVReportBuilder(Path reportFilePath, boolean includeErrors) throws IOException {
        super(reportFilePath, includeErrors);
    }

    protected void createReportFile() throws IOException {
        LOG.debug("Creating report file '{}'", reportFilePath);
        try {
            Files.createDirectories(reportFilePath.getParent());
        } catch (FileAlreadyExistsException e) {
            // ignore
        }

        String[] HEADERS = { "Project Name", "Operationalization", "Model Size", "Average Initalization Time",
                "Median Initalization Time", "Average Execution Time", "Median Execution Time",
                "Average Created Elements", "Median Created Elements", "Average Deleted Elements",
                "Median Deleted Elements", "Error" };

        FileWriter reportFileWriter = new FileWriter(reportFilePath.toFile(), false);
        reportSheet = new CSVPrinter(reportFileWriter, CSVFormat.EXCEL.withHeader(HEADERS));
    }

    public void addEntry(BenchmarkResult benchmarkResult) throws IOException {
        super.addEntry(benchmarkResult);

        if (benchmarkResult.getError() != null && !includeErrors) {
            return;
        }

        reportSheet.printRecord(benchmarkResult.getProjectName(), benchmarkResult.getOperationalization(),
                benchmarkResult.getModelSize(), benchmarkResult.getAverageInitalizationTime(),
                benchmarkResult.getMedianInitializationTime(), benchmarkResult.getAverageExecutionTime(),
                benchmarkResult.getMedianExecutionTime(), benchmarkResult.getAverageCreatedElements(),
                benchmarkResult.getMedianCreatedElements(), benchmarkResult.getAverageCreatedElements(),
                benchmarkResult.getMedianCreatedElements(), benchmarkResult.getError());
    }

    public void close() throws IOException {
        reportSheet.close();
        reportOutputStream.close();
    }
}