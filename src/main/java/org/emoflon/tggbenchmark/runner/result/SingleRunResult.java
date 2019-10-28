package org.emoflon.tggbenchmark.runner.result;

import java.io.Serializable;

/**
 * SingleRunResult represents a result for a single benchmark run and consists
 * of the measured data.
 *
 * @author Andre Lehmann
 */
public class SingleRunResult implements Serializable {

    private static final long serialVersionUID = -344586299291242439L;

    private int repetition;

    // time in mili seconds
    private long initializationTime;
    private long executionTime;

    private long createdElements;
    private long deletedElements;
    private long foundMatches;
    private long appliedMatches;

    private String error;

    /**
     * Constructor for {@link SingleRunResult}.
     */
    public SingleRunResult() {
        this.repetition = 0;
        this.createdElements = -1;
        this.deletedElements = -1;
        this.initializationTime = -1L;
        this.foundMatches = -1;
        this.appliedMatches = -1L;
        this.executionTime = -1L;
        this.error = null;
    }

    /**
     * @return the repetition
     */
    public int getRepetition() {
        return repetition;
    }

    /**
     * @param repetition the repetition to set
     */
    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    /**
     * @return the createdElements
     */
    public long getCreatedElements() {
        return createdElements;
    }

    /**
     * @param createdElements the createdElements to set
     */
    public void setCreatedElements(long createdElements) {
        this.createdElements = createdElements;
    }

    /**
     * @return the deletedElements
     */
    public long getDeletedElements() {
        return deletedElements;
    }

    /**
     * @param deletedElements the deletedElements to set
     */
    public void setDeletedElements(long deletedElements) {
        this.deletedElements = deletedElements;
    }

    /**
     * @return the initializationTime
     */
    public long getInitializationTime() {
        return initializationTime;
    }

    /**
     * @param initializationTime the initializationTime to set
     */
    public void setInitializationTime(long initializationTime) {
        this.initializationTime = initializationTime;
    }

    /**
     * @return the executionTime
     */
    public long getExecutionTime() {
        return executionTime;
    }

    /**
     * @param executionTime the executionTime to set
     */
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    /**
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * @return the foundMatches
     */
    public long getFoundMatches() {
        return foundMatches;
    }

    /**
     * @param foundMatches the foundMatches to set
     */
    public void setFoundMatches(long foundMatches) {
        this.foundMatches = foundMatches;
    }

    /**
     * @return the appliedMatches
     */
    public long getAppliedMatches() {
        return appliedMatches;
    }

    /**
     * @param appliedMatches the appliedMatches to set
     */
    public void setAppliedMatches(long appliedMatches) {
        this.appliedMatches = appliedMatches;
    }
}
