package org.emoflon.ibex.tgg.benchmark.runner.report;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkResult;
import org.emoflon.ibex.tgg.benchmark.runner.SingleRunResult;

/**
 * ExcelReportBuilder creates a report from a list of benchmark results and
 * saves them to a Excel spreadsheet file.
 *
 * @author Andre Lehmann
 */
public class ExcelReportBuilder extends ReportBuilder {

    private OutputStream reportOutputStream;
    private XSSFWorkbook reportWorkbook;
    private XSSFSheet resultsSheet;
    private XSSFSheet rawResultsSheet;
    private LinkedList<Map<String, Object>> resultsSheetDefinition;
    private LinkedList<Map<String, Object>> rawResultsSheetDefinition;
    private CellStyle headerStyle;
    private CellStyle cellStyle;

    /**
     * Constructor for {@link ExcelReportBuilder}.
     * 
     * @param reportFilePath The path for the report file
     * @throws IOException if the report file could not be created or saved
     */
    public ExcelReportBuilder(Path reportFilePath, boolean includeErrors) throws IOException {
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

        reportOutputStream = Files.newOutputStream(reportFilePath, StandardOpenOption.CREATE);
        initializeSheetDefinitions();
        initalizeWorkbook();
    }

    /**
     * Initialize the workbook, which is the representation of the excel file.
     */
    private void initalizeWorkbook() {
        reportWorkbook = new XSSFWorkbook();

        // styles
        headerStyle = reportWorkbook.createCellStyle();
        XSSFFont headerFont = reportWorkbook.createFont();
        headerFont.setFontHeightInPoints((short) 9);
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        cellStyle = reportWorkbook.createCellStyle();
        XSSFFont cellFont = reportWorkbook.createFont();
        cellFont.setFontHeightInPoints((short) 9);
        cellStyle.setFont(cellFont);

        resultsSheet = reportWorkbook.createSheet("Benchmark Results");
        Row header = resultsSheet.createRow(0);
        for (int i = 0; i < resultsSheetDefinition.size(); i++) {
            Map<String, Object> m = resultsSheetDefinition.get(i);
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue((String) m.get("name"));
            headerCell.setCellStyle(headerStyle);
            resultsSheet.setColumnWidth(i, (int) m.get("columnWidth"));
        }

        rawResultsSheet = reportWorkbook.createSheet("Raw Benchmark Results");
        header = rawResultsSheet.createRow(0);
        for (int i = 0; i < rawResultsSheetDefinition.size(); i++) {
            Map<String, Object> m = rawResultsSheetDefinition.get(i);
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue((String) m.get("name"));
            headerCell.setCellStyle(headerStyle);
            rawResultsSheet.setColumnWidth(i, (int) m.get("columnWidth"));
        }
    }

    /**
     * Define the column structure of the sheets. The definition will be used when
     * creating the report and adding the results.
     */
    private void initializeSheetDefinitions() {
        this.resultsSheetDefinition = new LinkedList<>();
        this.rawResultsSheetDefinition = new LinkedList<>();

        Map<String, Object> projectName = new HashMap<>();
        projectName.put("name", "Project Name");
        projectName.put("columnWidth", 6000);
        projectName.put("valueType", CellType.STRING);
        projectName.put("valueSelector", (Function<BenchmarkResult, String>) BenchmarkResult::getProjectName);
        projectName.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(projectName);
        rawResultsSheetDefinition.add(projectName);

        Map<String, Object> patternMatchingEngine = new HashMap<>();
        patternMatchingEngine.put("name", "Pattern Matching Engine");
        patternMatchingEngine.put("columnWidth", 5700);
        patternMatchingEngine.put("valueType", CellType.STRING);
        patternMatchingEngine.put("valueSelector",
                (Function<BenchmarkResult, String>) br -> br.getPatternMatchingEngine().toString());
        patternMatchingEngine.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(patternMatchingEngine);
        rawResultsSheetDefinition.add(patternMatchingEngine);

        Map<String, Object> operationalization = new HashMap<>();
        operationalization.put("name", "Operationalization");
        operationalization.put("columnWidth", 4000);
        operationalization.put("valueType", CellType.STRING);
        operationalization.put("valueSelector",
                (Function<BenchmarkResult, String>) br -> br.getOperationalization().toString());
        operationalization.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(operationalization);
        rawResultsSheetDefinition.add(operationalization);

        Map<String, Object> modelSize = new HashMap<>();
        modelSize.put("name", "Model Size");
        modelSize.put("columnWidth", 2500);
        modelSize.put("valueType", CellType.NUMERIC);
        modelSize.put("valueSelector", (Function<BenchmarkResult, Double>) br -> (double) br.getModelSize());
        modelSize.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(modelSize);
        rawResultsSheetDefinition.add(modelSize);

        Map<String, Object> run = new HashMap<>();
        run.put("name", "Run");
        run.put("columnWidth", 1300);
        run.put("valueType", CellType.NUMERIC);
        run.put("valueSelector", (Function<SingleRunResult, Double>) sr -> (double) sr.getRepetition());
        run.put("valueSelectorClass", SingleRunResult.class);
        rawResultsSheetDefinition.add(run);

        Map<String, Object> initalizationTime = new HashMap<>();
        initalizationTime.put("name", "Initalization Time");
        initalizationTime.put("columnWidth", 4000);
        initalizationTime.put("valueType", CellType.NUMERIC);
        initalizationTime.put("valueSelector",
                (Function<SingleRunResult, Double>) sr -> toSeconds(sr.getInitializationTime()));
        initalizationTime.put("valueSelectorClass", SingleRunResult.class);
        rawResultsSheetDefinition.add(initalizationTime);

        Map<String, Object> executionTime = new HashMap<>();
        executionTime.put("name", "Execution Time");
        executionTime.put("columnWidth", 4000);
        executionTime.put("valueType", CellType.NUMERIC);
        executionTime.put("valueSelector", (Function<SingleRunResult, Double>) sr -> toSeconds(sr.getExecutionTime()));
        executionTime.put("valueSelectorClass", SingleRunResult.class);
        rawResultsSheetDefinition.add(executionTime);

        Map<String, Object> averageInitalizationTime = new HashMap<>();
        averageInitalizationTime.put("name", "Average Initalization Time");
        averageInitalizationTime.put("columnWidth", 5500);
        averageInitalizationTime.put("valueType", CellType.NUMERIC);
        averageInitalizationTime.put("valueSelector",
                (Function<BenchmarkResult, Double>) br -> toSeconds(br.getAverageInitalizationTime()));
        averageInitalizationTime.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(averageInitalizationTime);

        Map<String, Object> medianInitalizationTime = new HashMap<>();
        medianInitalizationTime.put("name", "Median Initalization Time");
        medianInitalizationTime.put("columnWidth", 5500);
        medianInitalizationTime.put("valueType", CellType.NUMERIC);
        medianInitalizationTime.put("valueSelector",
                (Function<BenchmarkResult, Double>) br -> toSeconds(br.getMedianInitializationTime()));
        medianInitalizationTime.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(medianInitalizationTime);

        Map<String, Object> averageExecutionTime = new HashMap<>();
        averageExecutionTime.put("name", "Average Execution Time");
        averageExecutionTime.put("columnWidth", 5500);
        averageExecutionTime.put("valueType", CellType.NUMERIC);
        averageExecutionTime.put("valueSelector",
                (Function<BenchmarkResult, Double>) br -> toSeconds(br.getAverageExecutionTime()));
        averageExecutionTime.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(averageExecutionTime);

        Map<String, Object> medianExecutionTime = new HashMap<>();
        medianExecutionTime.put("name", "Median Execution Time");
        medianExecutionTime.put("columnWidth", 5500);
        medianExecutionTime.put("valueType", CellType.NUMERIC);
        medianExecutionTime.put("valueSelector",
                (Function<BenchmarkResult, Double>) br -> toSeconds(br.getMedianExecutionTime()));
        medianExecutionTime.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(medianExecutionTime);

        Map<String, Object> createdElements = new HashMap<>();
        createdElements.put("name", "Created Elements");
        createdElements.put("columnWidth", 4000);
        createdElements.put("valueType", CellType.NUMERIC);
        createdElements.put("valueSelector",
                (Function<SingleRunResult, Double>) sr -> (double) sr.getCreatedElements());
        createdElements.put("valueSelectorClass", SingleRunResult.class);
        rawResultsSheetDefinition.add(createdElements);

        Map<String, Object> deletedElements = new HashMap<>();
        deletedElements.put("name", "Deleted Elements");
        deletedElements.put("columnWidth", 4000);
        deletedElements.put("valueType", CellType.NUMERIC);
        deletedElements.put("valueSelector",
                (Function<SingleRunResult, Double>) sr -> (double) sr.getDeletedElements());
        deletedElements.put("valueSelectorClass", SingleRunResult.class);
        rawResultsSheetDefinition.add(deletedElements);

        Map<String, Object> foundMatches = new HashMap<>();
        foundMatches.put("name", "Found Matches");
        foundMatches.put("columnWidth", 4000);
        foundMatches.put("valueType", CellType.NUMERIC);
        foundMatches.put("valueSelector",
                (Function<SingleRunResult, Double>) sr -> (double) sr.getFoundMatches());
        foundMatches.put("valueSelectorClass", SingleRunResult.class);
        rawResultsSheetDefinition.add(foundMatches);

        Map<String, Object> appliedMatches = new HashMap<>();
        appliedMatches.put("name", "Applied Matches");
        appliedMatches.put("columnWidth", 4000);
        appliedMatches.put("valueType", CellType.NUMERIC);
        appliedMatches.put("valueSelector",
                (Function<SingleRunResult, Double>) sr -> (double) sr.getAppliedMatches());
        appliedMatches.put("valueSelectorClass", SingleRunResult.class);
        rawResultsSheetDefinition.add(appliedMatches);

        Map<String, Object> averageCreatedElements = new HashMap<>();
        averageCreatedElements.put("name", "Average Created Elements");
        averageCreatedElements.put("columnWidth", 5300);
        averageCreatedElements.put("valueType", CellType.NUMERIC);
        averageCreatedElements.put("valueSelector",
                (Function<BenchmarkResult, Double>) br -> roundDouble(br.getAverageCreatedElements()));
        averageCreatedElements.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(averageCreatedElements);

        Map<String, Object> medianCreatedElements = new HashMap<>();
        medianCreatedElements.put("name", "Median Created Elements");
        medianCreatedElements.put("columnWidth", 5300);
        medianCreatedElements.put("valueType", CellType.NUMERIC);
        medianCreatedElements.put("valueSelector",
                (Function<BenchmarkResult, Double>) br -> roundDouble(br.getMedianCreatedElements()));
        medianCreatedElements.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(medianCreatedElements);

        Map<String, Object> averageDeletedElements = new HashMap<>();
        averageDeletedElements.put("name", "Average Deleted Elements");
        averageDeletedElements.put("columnWidth", 5300);
        averageDeletedElements.put("valueType", CellType.NUMERIC);
        averageDeletedElements.put("valueSelector",
                (Function<BenchmarkResult, Double>) br -> roundDouble(br.getAverageDeletedElements()));
        averageDeletedElements.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(averageDeletedElements);

        Map<String, Object> medianDeletedElements = new HashMap<>();
        medianDeletedElements.put("name", "Median Deleted Elements");
        medianDeletedElements.put("columnWidth", 5300);
        medianDeletedElements.put("valueType", CellType.NUMERIC);
        medianDeletedElements.put("valueSelector",
                (Function<BenchmarkResult, Double>) br -> roundDouble(br.getMedianDeletedElements()));
        medianDeletedElements.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(medianDeletedElements);

        Map<String, Object> averageFoundMatches = new HashMap<>();
        averageFoundMatches.put("name", "Average Found Matches");
        averageFoundMatches.put("columnWidth", 5300);
        averageFoundMatches.put("valueType", CellType.NUMERIC);
        averageFoundMatches.put("valueSelector",
                (Function<BenchmarkResult, Double>) br -> roundDouble(br.getAverageFoundMatches()));
        averageFoundMatches.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(averageFoundMatches);

        Map<String, Object> medianFoundMatches = new HashMap<>();
        medianFoundMatches.put("name", "Median Found Matches");
        medianFoundMatches.put("columnWidth", 5300);
        medianFoundMatches.put("valueType", CellType.NUMERIC);
        medianFoundMatches.put("valueSelector",
                (Function<BenchmarkResult, Double>) br -> roundDouble(br.getMedianFoundMatches()));
        medianFoundMatches.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(medianFoundMatches);

        Map<String, Object> averageAppliedMatches = new HashMap<>();
        averageAppliedMatches.put("name", "Average Applied Matches");
        averageAppliedMatches.put("columnWidth", 5300);
        averageAppliedMatches.put("valueType", CellType.NUMERIC);
        averageAppliedMatches.put("valueSelector",
                (Function<BenchmarkResult, Double>) br -> roundDouble(br.getAverageAppliedMatches()));
        averageAppliedMatches.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(averageAppliedMatches);

        Map<String, Object> medianAppliedMatches = new HashMap<>();
        medianAppliedMatches.put("name", "Median Applied Matches");
        medianAppliedMatches.put("columnWidth", 5300);
        medianAppliedMatches.put("valueType", CellType.NUMERIC);
        medianAppliedMatches.put("valueSelector",
                (Function<BenchmarkResult, Double>) br -> roundDouble(br.getMedianAppliedMatches()));
        medianAppliedMatches.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(medianAppliedMatches);

        Map<String, Object> error = new HashMap<>();
        error.put("name", "Error");
        error.put("columnWidth", 20000);
        error.put("valueType", CellType.STRING);
        error.put("valueSelector", (Function<BenchmarkResult, String>) BenchmarkResult::getError);
        error.put("valueSelectorClass", BenchmarkResult.class);
        resultsSheetDefinition.add(error);
        
        Map<String, Object> rawError = new HashMap<>();
        rawError.put("name", "Error");
        rawError.put("columnWidth", 20000);
        rawError.put("valueType", CellType.STRING);
        rawError.put("valueSelector", (Function<SingleRunResult, String>) SingleRunResult::getError);
        rawError.put("valueSelectorClass", SingleRunResult.class);
        rawResultsSheetDefinition.add(rawError);
    }

    @Override
    public void addEntry(BenchmarkResult benchmarkResult) throws IOException {
        super.addEntry(benchmarkResult);

        if (benchmarkResult.getError() != null && !includeErrors) {
            return;
        }

        // add benchmark results
        Row row = resultsSheet.createRow(resultsSheet.getLastRowNum() + 1);
        // add each column
        for (int i = 0; i < resultsSheetDefinition.size(); i++) {
            Map<String, Object> m = resultsSheetDefinition.get(i);
            Cell cell = row.createCell(i, (CellType) m.get("valueType"));
            cell.setCellStyle(cellStyle);
            if ((CellType) m.get("valueType") == CellType.STRING) {
                @SuppressWarnings("unchecked")
                Function<BenchmarkResult, String> valueSelector = (Function<BenchmarkResult, String>) m
                        .get("valueSelector");
                cell.setCellValue(valueSelector.apply(benchmarkResult));
            } else if ((CellType) m.get("valueType") == CellType.NUMERIC) {
                @SuppressWarnings("unchecked")
                Function<BenchmarkResult, Double> valueSelector = (Function<BenchmarkResult, Double>) m
                        .get("valueSelector");
                cell.setCellValue(valueSelector.apply(benchmarkResult));
            }
        }

        // add raw results (single run results)
        for (SingleRunResult singleRunResult : benchmarkResult.getRunResults()) {
            row = rawResultsSheet.createRow(rawResultsSheet.getLastRowNum() + 1);
            // add each column
            for (int i = 0; i < rawResultsSheetDefinition.size(); i++) {
                Map<String, Object> m = rawResultsSheetDefinition.get(i);
                Cell cell = row.createCell(i, (CellType) m.get("valueType"));
                cell.setCellStyle(cellStyle);
                Class<?> valueSelectorClass = (Class<?>) m.get("valueSelectorClass");
                if (valueSelectorClass.equals(BenchmarkResult.class)) {
                    if ((CellType) m.get("valueType") == CellType.STRING) {
                        @SuppressWarnings("unchecked")
                        Function<BenchmarkResult, String> valueSelector = (Function<BenchmarkResult, String>) m
                                .get("valueSelector");
                        cell.setCellValue(valueSelector.apply(benchmarkResult));
                    } else if ((CellType) m.get("valueType") == CellType.NUMERIC) {
                        @SuppressWarnings("unchecked")
                        Function<BenchmarkResult, Double> valueSelector = (Function<BenchmarkResult, Double>) m
                                .get("valueSelector");
                        cell.setCellValue(valueSelector.apply(benchmarkResult));
                    }
                } else if (valueSelectorClass.equals(SingleRunResult.class)) {
                    if ((CellType) m.get("valueType") == CellType.STRING) {
                        @SuppressWarnings("unchecked")
                        Function<SingleRunResult, String> valueSelector = (Function<SingleRunResult, String>) m
                                .get("valueSelector");
                        cell.setCellValue(valueSelector.apply(singleRunResult));
                    } else if ((CellType) m.get("valueType") == CellType.NUMERIC) {
                        @SuppressWarnings("unchecked")
                        Function<SingleRunResult, Double> valueSelector = (Function<SingleRunResult, Double>) m
                                .get("valueSelector");
                        cell.setCellValue(valueSelector.apply(singleRunResult));
                    }
                }
            }
        }

        // save report so the results doesn't get lost if the benchmark is
        // stopped before completion
        save();
    }

    @Override
    public void close() throws IOException {
        reportWorkbook.close();
        reportOutputStream.close();
    }

    @Override
    public void save() throws IOException {
        reportWorkbook.write(reportOutputStream);
    }
}