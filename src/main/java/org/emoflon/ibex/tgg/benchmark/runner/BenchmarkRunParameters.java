package org.emoflon.ibex.tgg.benchmark.runner;

import java.io.Serializable;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.OperationalizationType;
import org.emoflon.ibex.tgg.benchmark.utils.RefelctionUtils;

/**
 * Class BenchmarkRunParameters holds the parameters needed to run a benchmark.
 * 
 * @see org.emoflon.ibex.tgg.benchmark.runner.benchmark.Benchmark
 */
public class BenchmarkRunParameters implements Serializable {

    private static final long serialVersionUID = 8107641259806223277L;
    
    // general
    private String projectName;
    private OperationalizationType operationalization;
    private PatternMatchingEngine patternMatchingEngine;

    // in order to be serializable the paths must be of type String
    private String modelInstancesBasePath;
    private String workspacePath;
    private URL[] classPaths;

    private long timeout;
    private int repetition;
    private int modelSize;

    // specific to MODELGEN
    private String metamodelsRegistrationClassName;
    private String metamodelsRegistrationMethodName;
    private String tggRule;

    // specific to SYNC
    private String incrementalEditClassName;
    private String incrementalEditMethodName;

    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
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
     * @return the classPaths
     */
    public URL[] getClassPaths() {
        return classPaths;
    }

    /**
     * @param classPaths the classPaths to set
     */
    public void setClassPaths(URL[] classPaths) {
        this.classPaths = classPaths;
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
        String[] names = RefelctionUtils.splitClassAndMethodName(metamodelsRegistrationMethod);
        this.metamodelsRegistrationClassName = names[0];
        this.metamodelsRegistrationMethodName = names[1];
    }

    /**
     * @return the tggRule
     */
    public String getTggRule() {
        return tggRule;
    }

    /**
     * @param tggRule the tggRule to set
     */
    public void setTggRule(String tggRule) {
        this.tggRule = tggRule;
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
        String[] names = RefelctionUtils.splitClassAndMethodName(incrementalEditMethod);
        this.incrementalEditClassName = names[0];
        this.incrementalEditMethodName = names[1];
    }
}