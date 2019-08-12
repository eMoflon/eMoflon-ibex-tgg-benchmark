package org.emoflon.ibex.tgg.benchmark.runner;

import java.util.LinkedList;
import java.util.function.Function;

import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.OperationalizationType;

/**
 * BenchmarkResult represents a result for a single benchmark run of an
 * operationalization. It contains a set of {@link SingleRunResult}s, which hold
 * the results of the individual repetitions.
 *
 * @author Andre Lehmann
 */
public class BenchmarkResult {

    private final String projectName;
    private final PatternMatchingEngine patternMatchingEngine;
    private final OperationalizationType operationalization;
    private final int modelSize;
    private final LinkedList<SingleRunResult> runResults;
    private String error;

    /**
     * Constructor for {@link BenchmarkResult}.
     * 
     * @param runParameters The parameters used to run the benchmark
     */
    public BenchmarkResult(BenchmarkRunParameters runParameters) {
        this.projectName = runParameters.getProjectName();
        this.patternMatchingEngine = runParameters.getPatternMatchingEngine();
        this.operationalization = runParameters.getOperationalization();
        this.modelSize = runParameters.getModelSize();

        this.runResults = new LinkedList<>();
        this.error = null;
    }

    /**
     * Add a result from a single benchmark run.
     * 
     * @param result The result to add
     */
    public void addSingleResult(SingleRunResult result) {
        runResults.add(result);
    }

    /**
     * @return the runResults
     */
    public LinkedList<SingleRunResult> getRunResults() {
        return runResults;
    }

    /**
     * Get the average of a list of values.
     * 
     * @param selector The selector for the runResults list
     * @return
     */
    private double getAverageOf(Function<SingleRunResult, Long> selector) {
        if (runResults.isEmpty()) {
            return 0.0;
        }

        long sum = runResults.stream().mapToLong(r -> selector.apply(r)).sum();
        return sum / (double) (runResults.size());
    }

    /**
     * Get the median of a list of values.
     * 
     * @param selector The selector for the runResults list
     * @return
     */
    private double getMedianOf(Function<SingleRunResult, Long> selector) {
        if (runResults.isEmpty()) {
            return 0.0;
        }

        long[] values = runResults.stream().mapToLong(r -> selector.apply(r)).sorted().toArray();
        if (values.length % 2 == 1)
            return values[(values.length - 1) / 2];
        else
            return (values[values.length / 2 - 1] + values[values.length / 2]) / 2.0;
    }

    /**
     * @return the averageExecutionTime
     */
    public double getAverageExecutionTime() {
        return getAverageOf(SingleRunResult::getExecutionTime);
    }

    /**
     * @return the averageInitalizationTime
     */
    public double getAverageInitalizationTime() {
        return getAverageOf(SingleRunResult::getInitializationTime);
    }

    /**
     * @return the averageCreatedElements
     */
    public double getAverageCreatedElements() {
        return getAverageOf(SingleRunResult::getCreatedElements);
    }

    /**
     * @return the averageDeletedElements
     */
    public double getAverageDeletedElements() {
        return getAverageOf(SingleRunResult::getDeletedElements);
    }

    /**
     * @return the medianExecutionTime
     */
    public double getMedianExecutionTime() {
        return getMedianOf(SingleRunResult::getExecutionTime);
    }

    /**
     * @return the medianInitializationTime
     */
    public double getMedianInitializationTime() {
        return getMedianOf(SingleRunResult::getInitializationTime);
    }

    /**
     * @return the medianCreatedElements
     */
    public double getMedianCreatedElements() {
        return getMedianOf(SingleRunResult::getCreatedElements);
    }

    /**
     * @return the medianDeletedElements
     */
    public double getMedianDeletedElements() {
        return getMedianOf(SingleRunResult::getDeletedElements);
    }

    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @return the patternMatchingEngine
     */
    public PatternMatchingEngine getPatternMatchingEngine() {
        return patternMatchingEngine;
    }

    /**
     * @return the operationalization
     */
    public OperationalizationType getOperationalization() {
        return operationalization;
    }

    /**
     * @return the modelSize
     */
    public int getModelSize() {
        return modelSize;
    }

    /**
     * @return the error
     */
    public String getError() {
        if (this.error != null) {
            return error;
        }

        for (SingleRunResult runResult : runResults) {
            if (runResult.getError() != null) {
                return runResult.getError();
            }
        }

        return null;
    }

    /**
     * @param error the error to set
     */
    public void setError(String error) {
        this.error = error;
    }
}
