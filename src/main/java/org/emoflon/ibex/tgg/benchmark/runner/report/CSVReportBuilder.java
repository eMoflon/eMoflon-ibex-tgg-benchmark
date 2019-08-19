package org.emoflon.ibex.tgg.benchmark.runner.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkResult;
import org.emoflon.ibex.tgg.benchmark.runner.SingleRunResult;

/**
 * CSVReportBuilder creates a report from a list of benchmark results and saves
 * them to a CSV file.
 *
 * @author Andre Lehmann
 */
public class CSVReportBuilder extends ReportBuilder {

    private FileWriter reportFileWriter;
    private FileWriter rawReportFileWriter;
    private CSVPrinter resultsSheet;
    private CSVPrinter rawResultsSheet;

    /**
     * Constructor for {@link CSVReportBuilder}.
     * 
     * @param reportFilePath The path for the report file
     * @throws IOException if the report file could not be created or saved
     */
    public CSVReportBuilder(Path reportFilePath, boolean includeErrors) throws IOException {
        super(reportFilePath, includeErrors);
    }

    @Override
    protected void createReportFile() throws IOException {
        LOG.debug("Creating report file '{}'", reportFilePath);
        try {
            Files.createDirectories(reportFilePath.toAbsolutePath().getParent());
        } catch (FileAlreadyExistsException e) {
            // ignore
        }

        String[] resultsHeaders = { "Project Name", "Operationalization", "Model Size", "Average Initalization Time",
                "Median Initalization Time", "Average Execution Time", "Median Execution Time",
                "Average Created Elements", "Median Created Elements", "Average Deleted Elements",
                "Median Deleted Elements", "Error" };

        String[] rawResultsHeaders = { "Project Name", "Pattern Matching Engine", "Operationalization", "Model Size",
                "Run", "Initalization Time", "Execution Time", "Created Elements", "Deleted Elements", "Error" };

        reportFileWriter = new FileWriter(reportFilePath.toFile(), false);
        resultsSheet = new CSVPrinter(reportFileWriter, CSVFormat.EXCEL.withHeader(resultsHeaders));
        rawReportFileWriter = new FileWriter(new File(reportFilePath.toString().replaceAll("\\.csv$", ".raw.csv")),
                false);
        rawResultsSheet = new CSVPrinter(rawReportFileWriter, CSVFormat.EXCEL.withHeader(rawResultsHeaders));
    }

    @Override
    public void addEntry(BenchmarkResult benchmarkResult) throws IOException {
        super.addEntry(benchmarkResult);

        if (benchmarkResult.getError() != null && !includeErrors) {
            return;
        }

        // add benchmark results
        resultsSheet.printRecord(benchmarkResult.getProjectName(), benchmarkResult.getOperationalization(),
                benchmarkResult.getModelSize(), toSeconds(benchmarkResult.getAverageInitalizationTime()),
                toSeconds(benchmarkResult.getMedianInitializationTime()),
                toSeconds(benchmarkResult.getAverageExecutionTime()),
                toSeconds(benchmarkResult.getMedianExecutionTime()),
                roundDouble(benchmarkResult.getAverageCreatedElements()),
                roundDouble(benchmarkResult.getMedianCreatedElements()),
                roundDouble(benchmarkResult.getAverageCreatedElements()),
                roundDouble(benchmarkResult.getMedianCreatedElements()), benchmarkResult.getError());

        // add raw results (single run results)
        for (SingleRunResult singleRunResult : benchmarkResult.getRunResults()) {
            rawResultsSheet.printRecord(benchmarkResult.getProjectName(), benchmarkResult.getOperationalization(),
                    benchmarkResult.getModelSize(), singleRunResult.getRepetition(),
                    toSeconds(singleRunResult.getInitializationTime()), toSeconds(singleRunResult.getExecutionTime()),
                    singleRunResult.getCreatedElements(), singleRunResult.getDeletedElements(),
                    benchmarkResult.getError());
        }
    }

    @Override
    public void close() throws IOException {
        resultsSheet.close();
        rawResultsSheet.close();
        reportFileWriter.close();
        rawReportFileWriter.close();
    }

    @Override
    public void save() throws IOException {
        // everything gets written to file automatically
    }
}