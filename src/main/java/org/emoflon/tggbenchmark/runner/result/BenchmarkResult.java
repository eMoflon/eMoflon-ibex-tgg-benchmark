package org.emoflon.tggbenchmark.runner.result;

import java.util.LinkedList;
import java.util.function.Function;

import org.emoflon.tggbenchmark.runner.BenchmarkRunParameters;
import org.emoflon.tggbenchmark.runner.OperationalizationType;
import org.emoflon.tggbenchmark.runner.PatternMatchingEngine;

/**
 * BenchmarkResult represents a result for a single benchmark run of an
 * operationalization. It contains a set of {@link SingleRunResult}s, which hold
 * the results of the individual repetitions.
 *
 * @author Andre Lehmann
 */
public class BenchmarkResult {

    private final String benchmarkCaseName;
    private final String tggProject;
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
        this.benchmarkCaseName = runParameters.getBenchmarkCaseName();
        this.tggProject = runParameters.getTggProject();
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
     * @return the benchmarkCaseName
     */
    public String getBenchmarkCaseName() {
        return benchmarkCaseName;
    }

    /**
     * @return the tggProject
     */
    public String getTggProject() {
        return tggProject;
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
     * @return the averageFoundMatches
     */
    public double getAverageFoundMatches() {
        return getAverageOf(SingleRunResult::getFoundMatches);
    }

    /**
     * @return the averageAppliedMatches
     */
    public double getAverageAppliedMatches() {
        return getAverageOf(SingleRunResult::getAppliedMatches);
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
     * @return the medianFoundMatches
     */
    public double getMedianFoundMatches() {
        return getMedianOf(SingleRunResult::getFoundMatches);
    }

    /**
     * @return the medianAppliedMatches
     */
    public double getMedianAppliedMatches() {
        return getMedianOf(SingleRunResult::getAppliedMatches);
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
}
