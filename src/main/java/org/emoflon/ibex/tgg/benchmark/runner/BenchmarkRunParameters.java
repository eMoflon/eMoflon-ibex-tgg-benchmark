package org.emoflon.ibex.tgg.benchmark.runner;

import java.io.Serializable;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.OperationalizationType;
import org.emoflon.ibex.tgg.benchmark.utils.ReflectionUtils;

/**
 * Class BenchmarkRunParameters holds the parameters needed to run a benchmark.
 * 
 * @see org.emoflon.ibex.tgg.benchmark.runner.benchmark.Benchmark
 */
public class BenchmarkRunParameters implements Serializable {

    private static final long serialVersionUID = 8107641259806223277L;

    // general
    private String benchmarkCaseName;
    private String tggProject;
    private OperationalizationType operationalization;
    private PatternMatchingEngine patternMatchingEngine;

    // in order to be serializable the paths must be of type String
    private String modelInstancesBasePath;
    private String workspacePath;

    private long timeout;
    // count starts at 1
    private int repetition;
    private int modelSize;

    // specific to MODELGEN
    private String metamodelsRegistrationClassName;
    private String metamodelsRegistrationMethodName;
    private Map<String, Integer> ruleCount;

    // specific to SYNC
    private String incrementalEditClassName;
    private String incrementalEditMethodName;

    /**
     * @return the benchmarkCaseName
     */
    public String getBenchmarkCaseName() {
        return benchmarkCaseName;
    }

    /**
     * @param benchmarkCaseName the benchmarkCaseName to set
     */
    public void setBenchmarkCaseName(String benchmarkCaseName) {
        this.benchmarkCaseName = benchmarkCaseName;
    }

    /**
     * @return the tggProject
     */
    public String getTggProject() {
        return tggProject;
    }

    /**
     * @param tggProject the tggProject to set
     */
    public void setTggProject(String tggProject) {
        this.tggProject = tggProject;
    }

    /**
     * @return the operationalization
     */
    public OperationalizationType getOperationalization() {
        return operationalization;
    }

    /**
     * @param operationalization the operationalization to set
     */
    public void setOperationalization(OperationalizationType operationalization) {
        this.operationalization = operationalization;
    }

    /**
     * @return the patternMatchingEngine
     */
    public PatternMatchingEngine getPatternMatchingEngine() {
        return patternMatchingEngine;
    }

    /**
     * @param patternMatchingEngine the patternMatchingEngine to set
     */
    public void setPatternMatchingEngine(PatternMatchingEngine patternMatchingEngine) {
        this.patternMatchingEngine = patternMatchingEngine;
    }

    /**
     * @return the modelInstancesPath which needs to be workspace relative
     */
    public Path getModelInstancesPath() {
        return Paths.get(modelInstancesBasePath).normalize().resolve(String.valueOf(modelSize))
                .resolve(String.valueOf(repetition));
    }

    /**
     * @return the modelInstancesBasePath which needs to be workspace relative
     */
    public String getModelInstancesBasePath() {
        return modelInstancesBasePath;
    }

    /**
     * @param modelInstancesBasePath the modelInstancesBasePath to set
     */
    public void setModelInstancesbasePath(String modelInstancesBasePath) {
        this.modelInstancesBasePath = modelInstancesBasePath;
    }

    /**
     * @return the workspacePath
     */
    public Path getWorkspacePath() {
        return Paths.get(workspacePath).toAbsolutePath().normalize();
    }

    /**
     * @param workspacePath the workspacePath to set
     */
    public void setWorkspacePath(String workspacePath) {
        this.workspacePath = workspacePath;
    }

    /**
     * @return the timeout
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
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
     * @return the modelSize
     */
    public int getModelSize() {
        return modelSize;
    }

    /**
     * @param modelSize the modelSize to set
     */
    public void setModelSize(int modelSize) {
        this.modelSize = modelSize;
    }

    /**
     * @return the metamodelsRegistrationClassName
     */
    public String getMetamodelsRegistrationClassName() {
        return metamodelsRegistrationClassName;
    }

    /**
     * @return the metamodelsRegistrationMethodName
     */
    public String getMetamodelsRegistrationMethodName() {
        return metamodelsRegistrationMethodName;
    }

    /**
     * @param metamodelsRegistrationMethod the metamodelsRegistrationMethod to set
     */
    public void setMetamodelsRegistrationMethod(String metamodelsRegistrationMethod) {
        String[] names = ReflectionUtils.splitMethodIdentifier(metamodelsRegistrationMethod);
        this.metamodelsRegistrationClassName = names[0];
        this.metamodelsRegistrationMethodName = names[1];
    }

    /**
     * @return the ruleCount
     */
    public Map<String, Integer> getRuleCount() {
        return ruleCount;
    }

    /**
     * @param ruleCount the ruleCount to set
     */
    public void setRuleCount(Map<String, Integer> ruleCount) {
        this.ruleCount = ruleCount;
    }

    /**
     * @return the incrementalEditClassName
     */
    public String getIncrementalEditClassName() {
        return incrementalEditClassName;
    }

    /**
     * @return the incrementalEditMethodName
     */
    public String getIncrementalEditMethodName() {
        return incrementalEditMethodName;
    }

    /**
     * @param incrementalEditClassName the incrementalEditClassName to set
     */
    public void setIncrementalEditMethod(String incrementalEditMethod) {
        String[] names = ReflectionUtils.splitMethodIdentifier(incrementalEditMethod);
        this.incrementalEditClassName = names[0];
        this.incrementalEditMethodName = names[1];
    }
}