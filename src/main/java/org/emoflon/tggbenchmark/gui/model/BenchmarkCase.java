package org.emoflon.tggbenchmark.gui.model;

import java.util.Collections;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.emoflon.tggbenchmark.Core;
import org.emoflon.tggbenchmark.runner.PatternMatchingEngine;
import org.emoflon.tggbenchmark.workspace.EclipseTggProject;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

public class BenchmarkCase {

    private final PluginPreferences pluginPreferences;

    // benchmark
    private BooleanProperty markedForExecution;
    private ObjectProperty<EclipseTggProject> eclipseProject;
    private StringProperty benchmarkCaseName;
    private StringProperty metamodelsRegistrationMethod;
    private ObjectProperty<PatternMatchingEngine> patternMatchingEngine;
    private IntegerProperty defaultTimeout;

    // operationalizations
    private BooleanProperty modelgenIncludeReport;
    private IntegerProperty modelgenTimeout;
    private ListProperty<Integer> modelgenModelSizes;
    private ListProperty<Pair<StringProperty, IntegerProperty>> modelgenRuleCount;

    private BooleanProperty initialFwdActive;
    private IntegerProperty initialFwdTimeout;
    private IntegerProperty initialFwdMaxModelSize;

    private BooleanProperty initialBwdActive;
    private IntegerProperty initialBwdTimeout;
    private IntegerProperty initialBwdMaxModelSize;

    private BooleanProperty fwdActive;
    private IntegerProperty fwdTimeout;
    private IntegerProperty fwdMaxModelSize;
    private StringProperty fwdIncrementalEditMethod;

    private BooleanProperty bwdActive;
    private IntegerProperty bwdTimeout;
    private IntegerProperty bwdMaxModelSize;
    private StringProperty bwdIncrementalEditMethod;

    private BooleanProperty fwdOptActive;
    private IntegerProperty fwdOptTimeout;
    private IntegerProperty fwdOptMaxModelSize;

    private BooleanProperty bwdOptActive;
    private IntegerProperty bwdOptTimeout;
    private IntegerProperty bwdOptMaxModelSize;

    private BooleanProperty ccActive;
    private IntegerProperty ccTimeout;
    private IntegerProperty ccMaxModelSize;

    private BooleanProperty coActive;
    private IntegerProperty coTimeout;
    private IntegerProperty coMaxModelSize;

    public BenchmarkCase() {
        pluginPreferences = Core.getInstance().getPluginPreferences();

        // benchmark
        markedForExecution = new SimpleBooleanProperty(true);
        eclipseProject = new SimpleObjectProperty<>();
        benchmarkCaseName = new SimpleStringProperty("");
        metamodelsRegistrationMethod = new SimpleStringProperty("");
        patternMatchingEngine = new SimpleObjectProperty<>(PatternMatchingEngine.Democles);
        defaultTimeout = new SimpleIntegerProperty(0);

        // operationalizations
        modelgenIncludeReport = new SimpleBooleanProperty(pluginPreferences.isDefaultModelgenIncludeReport());
        modelgenTimeout = new SimpleIntegerProperty(0);
        modelgenModelSizes = new SimpleListProperty<>(
                FXCollections.observableArrayList(pluginPreferences.getDefaultModelSizes()));
        modelgenRuleCount = new SimpleListProperty<>(FXCollections.observableArrayList());

        initialFwdActive = new SimpleBooleanProperty(pluginPreferences.isDefaultInitialFwdActive());
        initialFwdTimeout = new SimpleIntegerProperty(0);
        initialFwdMaxModelSize = new SimpleIntegerProperty(-1);

        initialBwdActive = new SimpleBooleanProperty(pluginPreferences.isDefaultInitialBwdActive());
        initialBwdTimeout = new SimpleIntegerProperty(0);
        initialBwdMaxModelSize = new SimpleIntegerProperty(-1);

        fwdActive = new SimpleBooleanProperty(pluginPreferences.isDefaultFwdActive());
        fwdTimeout = new SimpleIntegerProperty(0);
        fwdMaxModelSize = new SimpleIntegerProperty(-1);
        fwdIncrementalEditMethod = new SimpleStringProperty("");

        bwdActive = new SimpleBooleanProperty(pluginPreferences.isDefaultBwdActive());
        bwdTimeout = new SimpleIntegerProperty(0);
        bwdMaxModelSize = new SimpleIntegerProperty(-1);
        bwdIncrementalEditMethod = new SimpleStringProperty("");

        fwdOptActive = new SimpleBooleanProperty(pluginPreferences.isDefaultFwdOptActive());
        fwdOptTimeout = new SimpleIntegerProperty(0);
        fwdOptMaxModelSize = new SimpleIntegerProperty(-1);

        bwdOptActive = new SimpleBooleanProperty(pluginPreferences.isDefaultBwdOptActive());
        bwdOptTimeout = new SimpleIntegerProperty(0);
        bwdOptMaxModelSize = new SimpleIntegerProperty(-1);

        ccActive = new SimpleBooleanProperty(pluginPreferences.isDefaultCcActive());
        ccTimeout = new SimpleIntegerProperty(0);
        ccMaxModelSize = new SimpleIntegerProperty(-1);

        coActive = new SimpleBooleanProperty(pluginPreferences.isDefaultCoActive());
        coTimeout = new SimpleIntegerProperty(0);
        coMaxModelSize = new SimpleIntegerProperty(-1);
    }

    public BenchmarkCase(BenchmarkCase source) {
        this();
        copyValues(source);
    }

    public BenchmarkCase(JsonObject data) {
        this();

        JsonObject benchmark = data.getJsonObject("benchmark");
        if (benchmark != null) {
            setMarkedForExecution(benchmark.getBoolean("markedForExecution", isMarkedForExecution()));
            setBenchmarkCaseName(benchmark.getString("benchmarkCaseName", getBenchmarkCaseName()));
            setMetamodelsRegistrationMethod(
                    benchmark.getString("metamodelsRegistrationMethod", getMetamodelsRegistrationMethod()));
            try {
                setPatternMatchingEngine(PatternMatchingEngine
                        .valueOf(benchmark.getString("patternMatchingEngine", getPatternMatchingEngine().toString())));
            } catch (IllegalArgumentException e) {
                // keep default
            }
            int defaultTimeout = benchmark.getInt("timeout", getDefaultTimeout());
            setDefaultTimeout(defaultTimeout > 0 ? defaultTimeout : getDefaultTimeout());
        }

        JsonObject operationalizations = data.getJsonObject("operationalizations");
        if (operationalizations != null) {
            JsonObject modelgen = operationalizations.getJsonObject("modelgen");
            if (modelgen != null) {
                setModelgenIncludeReport(modelgen.getBoolean("includeReport", isModelgenIncludeReport()));
                int timout = modelgen.getInt("timeout", getModelgenTimeout());
                setModelgenTimeout(timout > 0 ? timout : getModelgenTimeout());
                JsonArray modelSizesArray = modelgen.getJsonArray("modelSizes");
                if (modelSizesArray != null) {
                    ObservableList<Integer> modelSizes = FXCollections.observableArrayList();
                    for (int i = 0; i < modelSizesArray.size(); i++) {
                        try {
                            modelSizes.add(modelSizesArray.getInt(i));
                        } catch (ClassCastException e) {
                            // ignore value
                        }
                    }
                    setModelgenModelSizes(modelSizes);
                }
                JsonObject ruleCountObject = modelgen.getJsonObject("ruleCount");
                if (ruleCountObject != null) {
                    ObservableList<Pair<StringProperty, IntegerProperty>> ruleCount = FXCollections.observableArrayList();
                    for (String key : ruleCountObject.keySet()) {
                        ruleCount.add(new Pair<>(new SimpleStringProperty(key), new SimpleIntegerProperty(ruleCountObject.getInt(key, 0))));
                    }
                    setModelgenRuleCount(ruleCount);
                }
            }

            JsonObject initialFwd = operationalizations.getJsonObject("initialFwd");
            if (initialFwd != null) {
                setInitialFwdActive(initialFwd.getBoolean("active", isInitialFwdActive()));
                int timout = initialFwd.getInt("timeout", getInitialFwdTimeout());
                setInitialFwdTimeout(timout > 0 ? timout : getInitialFwdTimeout());
                setInitialFwdMaxModelSize(initialFwd.getInt("maxModelSize", getInitialFwdMaxModelSize()));
            }

            JsonObject initialBwd = operationalizations.getJsonObject("initialBwd");
            if (initialBwd != null) {
                setInitialBwdActive(initialBwd.getBoolean("active", isInitialBwdActive()));
                int timout = initialBwd.getInt("timeout", getInitialBwdTimeout());
                setInitialBwdTimeout(timout > 0 ? timout : getInitialBwdTimeout());
                setInitialBwdMaxModelSize(initialBwd.getInt("maxModelSize", getInitialBwdMaxModelSize()));
            }

            JsonObject fwd = operationalizations.getJsonObject("fwd");
            if (fwd != null) {
                setFwdActive(fwd.getBoolean("active", isFwdActive()));
                int timout = fwd.getInt("timeout", getFwdTimeout());
                setFwdTimeout(timout > 0 ? timout : getFwdTimeout());
                setFwdMaxModelSize(fwd.getInt("maxModelSize", getFwdMaxModelSize()));
                setFwdIncrementalEditMethod(fwd.getString("incrementalEditMethod", getFwdIncrementalEditMethod()));
            }

            JsonObject bwd = operationalizations.getJsonObject("bwd");
            if (bwd != null) {
                setBwdActive(bwd.getBoolean("active", isBwdActive()));
                int timout = bwd.getInt("timeout", getBwdTimeout());
                setBwdTimeout(timout > 0 ? timout : getBwdTimeout());
                setBwdMaxModelSize(bwd.getInt("maxModelSize", getBwdMaxModelSize()));
                setBwdIncrementalEditMethod(bwd.getString("incrementalEditMethod", getBwdIncrementalEditMethod()));
            }

            JsonObject fwdOpt = operationalizations.getJsonObject("fwdOpt");
            if (fwdOpt != null) {
                setFwdOptActive(fwdOpt.getBoolean("active", isFwdOptActive()));
                int timout = fwdOpt.getInt("timeout", getFwdOptTimeout());
                setFwdOptTimeout(timout > 0 ? timout : getFwdOptTimeout());
                setFwdOptMaxModelSize(fwdOpt.getInt("maxModelSize", getFwdOptMaxModelSize()));
            }

            JsonObject bwdOpt = operationalizations.getJsonObject("bwdOpt");
            if (bwdOpt != null) {
                setBwdOptActive(bwdOpt.getBoolean("active", isBwdOptActive()));
                int timout = bwdOpt.getInt("timeout", getBwdOptTimeout());
                setBwdOptTimeout(timout > 0 ? timout : getBwdOptTimeout());
                setBwdOptMaxModelSize(bwdOpt.getInt("maxModelSize", getBwdOptMaxModelSize()));
            }

            JsonObject cc = operationalizations.getJsonObject("cc");
            if (cc != null) {
                setCcActive(cc.getBoolean("active", isCcActive()));
                int timout = cc.getInt("timeout", getCcTimeout());
                setCcTimeout(timout > 0 ? timout : getCcTimeout());
                setCcMaxModelSize(cc.getInt("maxModelSize", getCcMaxModelSize()));
            }

            JsonObject co = operationalizations.getJsonObject("co");
            if (co != null) {
                setCoActive(co.getBoolean("active", isCoActive()));
                int timout = co.getInt("timeout", getCoTimeout());
                setCoTimeout(timout > 0 ? timout : getCoTimeout());
                setCoMaxModelSize(co.getInt("maxModelSize", getCoMaxModelSize()));
            }
        }
    }

    /**
     * Converts the preferences object into a {@link JsonObject}.
     * 
     * @return JSON representation
     */
    public JsonObject toJson() {

        JsonArrayBuilder modelSizesBuilder = Json.createArrayBuilder();
        for (Integer integer : getModelgenModelSizes()) {
            modelSizesBuilder.add(integer);
        }

        JsonObjectBuilder ruleCountBuilder = Json.createObjectBuilder();
        for (Pair<StringProperty, IntegerProperty> rc : getModelgenRuleCount()) {
            ruleCountBuilder.add(rc.getKey().get(), rc.getValue().get());
        }

        JsonObject preferences = Json.createObjectBuilder()
                .add("benchmark",
                        Json.createObjectBuilder().add("markedForExecution", isMarkedForExecution())
                                .add("benchmarkCaseName", getBenchmarkCaseName())
                                .add("metamodelsRegistrationMethod", getMetamodelsRegistrationMethod())
                                .add("patternMatchingEngine", getPatternMatchingEngine().toString())
                                .add("timeout", getDefaultTimeout()).build())
                .add("operationalizations", Json.createObjectBuilder()
                        .add("modelgen", Json.createObjectBuilder().add("includeReport", isModelgenIncludeReport())
                                .add("timeout", getModelgenTimeout()).add("modelSizes", modelSizesBuilder.build())
                                .add("ruleCount", ruleCountBuilder.build()).build())
                        .add("initialFwd",
                                Json.createObjectBuilder().add("active", isInitialFwdActive())
                                        .add("timeout", getInitialFwdTimeout())
                                        .add("maxModelSize", getInitialFwdMaxModelSize()).build())
                        .add("initialBwd",
                                Json.createObjectBuilder().add("active", isInitialBwdActive())
                                        .add("timeout", getInitialBwdTimeout())
                                        .add("maxModelSize", getInitialBwdMaxModelSize()).build())
                        .add("fwd",
                                Json.createObjectBuilder().add("active", isFwdActive()).add("timeout", getFwdTimeout())
                                        .add("maxModelSize", getFwdMaxModelSize())
                                        .add("incrementalEditMethod", getFwdIncrementalEditMethod()).build())
                        .add("bwd",
                                Json.createObjectBuilder().add("active", isBwdActive()).add("timeout", getBwdTimeout())
                                        .add("maxModelSize", getBwdMaxModelSize())
                                        .add("incrementalEditMethod", getBwdIncrementalEditMethod()).build())
                        .add("fwdOpt",
                                Json.createObjectBuilder().add("active", isFwdOptActive())
                                        .add("timeout", getFwdOptTimeout()).add("maxModelSize", getFwdOptMaxModelSize())
                                        .build())
                        .add("bwdOpt",
                                Json.createObjectBuilder().add("active", isBwdOptActive())
                                        .add("timeout", getBwdOptTimeout()).add("maxModelSize", getBwdOptMaxModelSize())
                                        .build())
                        .add("cc",
                                Json.createObjectBuilder().add("active", isCcActive()).add("timeout", getCcTimeout())
                                        .add("maxModelSize", getCcMaxModelSize()).build())
                        .add("co", Json.createObjectBuilder().add("active", isCoActive()).add("timeout", getCoTimeout())
                                .add("maxModelSize", getCoMaxModelSize()).build())
                        .build())
                .build();

        return preferences;
    }

    public void copyValues(BenchmarkCase source) {
        // benchmark
        setMarkedForExecution(source.isMarkedForExecution());
        setEclipseProject(source.getEclipseProject());
        setBenchmarkCaseName(source.getBenchmarkCaseName());
        setMetamodelsRegistrationMethod(source.getMetamodelsRegistrationMethod());
        setPatternMatchingEngine(source.getPatternMatchingEngine());
        setDefaultTimeout(source.getDefaultTimeout());

        // operationalizations
        setModelgenIncludeReport(source.isModelgenIncludeReport());
        setModelgenTimeout(source.getModelgenTimeout());
        setModelgenModelSizes(FXCollections.observableArrayList(source.getModelgenModelSizes()));
        setModelgenRuleCount(FXCollections.observableArrayList(source.getModelgenRuleCount()));

        setInitialFwdActive(source.isInitialFwdActive());
        setInitialFwdTimeout(source.getInitialFwdTimeout());
        setInitialFwdMaxModelSize(source.getInitialFwdMaxModelSize());

        setInitialBwdActive(source.isInitialBwdActive());
        setInitialBwdTimeout(source.getInitialBwdTimeout());
        setInitialBwdMaxModelSize(source.getInitialBwdMaxModelSize());

        setFwdActive(source.isFwdActive());
        setFwdTimeout(source.getFwdTimeout());
        setFwdMaxModelSize(source.getFwdMaxModelSize());
        setFwdIncrementalEditMethod(source.getFwdIncrementalEditMethod());

        setBwdActive(source.isBwdActive());
        setBwdTimeout(source.getBwdTimeout());
        setBwdMaxModelSize(source.getBwdMaxModelSize());
        setBwdIncrementalEditMethod(source.getBwdIncrementalEditMethod());

        setFwdOptActive(source.isFwdOptActive());
        setFwdOptTimeout(source.getFwdOptTimeout());
        setFwdOptMaxModelSize(source.getFwdOptMaxModelSize());

        setBwdOptActive(source.isBwdOptActive());
        setBwdOptTimeout(source.getBwdOptTimeout());
        setBwdOptMaxModelSize(source.getBwdOptMaxModelSize());

        setCcActive(source.isCcActive());
        setCcTimeout(source.getCcTimeout());
        setCcMaxModelSize(source.getCcMaxModelSize());

        setCoActive(source.isCoActive());
        setCoTimeout(source.getCoTimeout());
        setCoMaxModelSize(source.getCoMaxModelSize());
    }

    private <T> T getValueOrDefault(T value, T whenValue, T defaultValue) {
        if (value == whenValue) {
            return defaultValue;
        }
        return value;
    }

    public final IntegerProperty defaultTimeoutProperty() {
        return this.defaultTimeout;
    }

    public final int getDefaultTimeout() {
        return this.defaultTimeoutProperty().get();
    }

    public final int getEffectiveDefaultTimeout() {
        return getValueOrDefault(getDefaultTimeout(), 0, pluginPreferences.getDefaultTimeout());
    }

    public final void setDefaultTimeout(final int defaultTimeout) {
        this.defaultTimeoutProperty().set(defaultTimeout);
    }

    public final BooleanProperty modelgenIncludeReportProperty() {
        return this.modelgenIncludeReport;
    }

    public final boolean isModelgenIncludeReport() {
        return this.modelgenIncludeReportProperty().get();
    }

    public final void setModelgenIncludeReport(final boolean modelgenIncludeReport) {
        this.modelgenIncludeReportProperty().set(modelgenIncludeReport);
    }

    public final IntegerProperty modelgenTimeoutProperty() {
        return this.modelgenTimeout;
    }

    public final int getModelgenTimeout() {
        return this.modelgenTimeoutProperty().get();
    }

    public final int getEffectiveModelgenTimeout() {
        return getValueOrDefault(getModelgenTimeout(), 0, getEffectiveDefaultTimeout());
    }

    public final void setModelgenTimeout(final int modelgenTimeout) {
        this.modelgenTimeoutProperty().set(modelgenTimeout);
    }

    public final BooleanProperty initialFwdActiveProperty() {
        return this.initialFwdActive;
    }

    public final boolean isInitialFwdActive() {
        return this.initialFwdActiveProperty().get();
    }

    public final void setInitialFwdActive(final boolean initialFwdActive) {
        this.initialFwdActiveProperty().set(initialFwdActive);
    }

    public final IntegerProperty initialFwdTimeoutProperty() {
        return this.initialFwdTimeout;
    }

    public final int getInitialFwdTimeout() {
        return this.initialFwdTimeoutProperty().get();
    }

    public final int getEffectiveInitialFwdTimeout() {
        return getValueOrDefault(getInitialFwdTimeout(), 0, getEffectiveDefaultTimeout());
    }

    public final void setInitialFwdTimeout(final int initialFwdTimeout) {
        this.initialFwdTimeoutProperty().set(initialFwdTimeout);
    }

    public final IntegerProperty initialFwdMaxModelSizeProperty() {
        return this.initialFwdMaxModelSize;
    }

    public final int getInitialFwdMaxModelSize() {
        return this.initialFwdMaxModelSizeProperty().get();
    }

    public final int getEffectiveInitialFwdMaxModelSize() {
        return getValueOrDefault(getInitialFwdMaxModelSize(), -1, Collections.max(modelgenModelSizes));
    }

    public final void setInitialFwdMaxModelSize(final int initialFwdMaxModelSize) {
        this.initialFwdMaxModelSizeProperty().set(initialFwdMaxModelSize);
    }

    public final BooleanProperty initialBwdActiveProperty() {
        return this.initialBwdActive;
    }

    public final boolean isInitialBwdActive() {
        return this.initialBwdActiveProperty().get();
    }

    public final void setInitialBwdActive(final boolean initialBwdActive) {
        this.initialBwdActiveProperty().set(initialBwdActive);
    }

    public final IntegerProperty initialBwdTimeoutProperty() {
        return this.initialBwdTimeout;
    }

    public final int getInitialBwdTimeout() {
        return this.initialBwdTimeoutProperty().get();
    }

    public final int getEffectiveInitialBwdTimeout() {
        return getValueOrDefault(getInitialBwdTimeout(), 0, getEffectiveDefaultTimeout());
    }

    public final void setInitialBwdTimeout(final int initialBwdTimeout) {
        this.initialBwdTimeoutProperty().set(initialBwdTimeout);
    }

    public final IntegerProperty initialBwdMaxModelSizeProperty() {
        return this.initialBwdMaxModelSize;
    }

    public final int getInitialBwdMaxModelSize() {
        return this.initialBwdMaxModelSizeProperty().get();
    }

    public final int getEffectiveInitialBwdMaxModelSize() {
        return getValueOrDefault(getInitialBwdMaxModelSize(), -1, Collections.max(modelgenModelSizes));
    }

    public final void setInitialBwdMaxModelSize(final int initialBwdMaxModelSize) {
        this.initialBwdMaxModelSizeProperty().set(initialBwdMaxModelSize);
    }

    public final BooleanProperty fwdOptActiveProperty() {
        return this.fwdOptActive;
    }

    public final boolean isFwdOptActive() {
        return this.fwdOptActiveProperty().get();
    }

    public final void setFwdOptActive(final boolean fwdOptActive) {
        this.fwdOptActiveProperty().set(fwdOptActive);
    }

    public final IntegerProperty fwdOptTimeoutProperty() {
        return this.fwdOptTimeout;
    }

    public final int getFwdOptTimeout() {
        return this.fwdOptTimeoutProperty().get();
    }

    public final int getEffectiveFwdOptTimeout() {
        return getValueOrDefault(getFwdOptTimeout(), 0, getEffectiveDefaultTimeout());
    }

    public final void setFwdOptTimeout(final int fwdOptTimeout) {
        this.fwdOptTimeoutProperty().set(fwdOptTimeout);
    }

    public final IntegerProperty fwdOptMaxModelSizeProperty() {
        return this.fwdOptMaxModelSize;
    }

    public final int getFwdOptMaxModelSize() {
        return this.fwdOptMaxModelSizeProperty().get();
    }

    public final int getEffectiveFwdOptMaxModelSize() {
        return getValueOrDefault(getFwdOptMaxModelSize(), -1, Collections.max(modelgenModelSizes));
    }

    public final void setFwdOptMaxModelSize(final int fwdOptMaxModelSize) {
        this.fwdOptMaxModelSizeProperty().set(fwdOptMaxModelSize);
    }

    public final BooleanProperty bwdOptActiveProperty() {
        return this.bwdOptActive;
    }

    public final boolean isBwdOptActive() {
        return this.bwdOptActiveProperty().get();
    }

    public final void setBwdOptActive(final boolean bwdOptActive) {
        this.bwdOptActiveProperty().set(bwdOptActive);
    }

    public final IntegerProperty bwdOptTimeoutProperty() {
        return this.bwdOptTimeout;
    }

    public final int getBwdOptTimeout() {
        return this.bwdOptTimeoutProperty().get();
    }

    public final int getEffectiveBwdOptTimeout() {
        return getValueOrDefault(getBwdOptTimeout(), 0, getEffectiveDefaultTimeout());
    }

    public final void setBwdOptTimeout(final int bwdOptTimeout) {
        this.bwdOptTimeoutProperty().set(bwdOptTimeout);
    }

    public final IntegerProperty bwdOptMaxModelSizeProperty() {
        return this.bwdOptMaxModelSize;
    }

    public final int getBwdOptMaxModelSize() {
        return this.bwdOptMaxModelSizeProperty().get();
    }

    public final int getEffectiveBwdOptMaxModelSize() {
        return getValueOrDefault(getBwdOptMaxModelSize(), -1, Collections.max(modelgenModelSizes));
    }

    public final void setBwdOptMaxModelSize(final int bwdOptMaxModelSize) {
        this.bwdOptMaxModelSizeProperty().set(bwdOptMaxModelSize);
    }

    public final BooleanProperty ccActiveProperty() {
        return this.ccActive;
    }

    public final boolean isCcActive() {
        return this.ccActiveProperty().get();
    }

    public final void setCcActive(final boolean ccActive) {
        this.ccActiveProperty().set(ccActive);
    }

    public final IntegerProperty ccTimeoutProperty() {
        return this.ccTimeout;
    }

    public final int getCcTimeout() {
        return this.ccTimeoutProperty().get();
    }

    public final int getEffectiveCcTimeout() {
        return getValueOrDefault(getCcTimeout(), 0, getEffectiveDefaultTimeout());
    }

    public final void setCcTimeout(final int ccTimeout) {
        this.ccTimeoutProperty().set(ccTimeout);
    }

    public final IntegerProperty ccMaxModelSizeProperty() {
        return this.ccMaxModelSize;
    }

    public final int getCcMaxModelSize() {
        return this.ccMaxModelSizeProperty().get();
    }

    public final int getEffectiveCcMaxModelSize() {
        return getValueOrDefault(getCcMaxModelSize(), -1, Collections.max(modelgenModelSizes));
    }

    public final void setCcMaxModelSize(final int ccMaxModelSize) {
        this.ccMaxModelSizeProperty().set(ccMaxModelSize);
    }

    public final BooleanProperty coActiveProperty() {
        return this.coActive;
    }

    public final boolean isCoActive() {
        return this.coActiveProperty().get();
    }

    public final void setCoActive(final boolean coActive) {
        this.coActiveProperty().set(coActive);
    }

    public final IntegerProperty coTimeoutProperty() {
        return this.coTimeout;
    }

    public final int getCoTimeout() {
        return this.coTimeoutProperty().get();
    }

    public final int getEffectiveCoTimeout() {
        return getValueOrDefault(getCoTimeout(), 0, getEffectiveDefaultTimeout());
    }

    public final void setCoTimeout(final int coTimeout) {
        this.coTimeoutProperty().set(coTimeout);
    }

    public final IntegerProperty coMaxModelSizeProperty() {
        return this.coMaxModelSize;
    }

    public final int getCoMaxModelSize() {
        return this.coMaxModelSizeProperty().get();
    }

    public final int getEffectiveCoMaxModelSize() {
        return getValueOrDefault(getCoMaxModelSize(), -1, Collections.max(modelgenModelSizes));
    }

    public final void setCoMaxModelSize(final int coMaxModelSize) {
        this.coMaxModelSizeProperty().set(coMaxModelSize);
    }

    public final BooleanProperty markedForExecutionProperty() {
        return this.markedForExecution;
    }

    public final boolean isMarkedForExecution() {
        return this.markedForExecutionProperty().get();
    }

    public final void setMarkedForExecution(final boolean markedForExecution) {
        this.markedForExecutionProperty().set(markedForExecution);
    }

    public final ListProperty<Integer> modelgenModelSizesProperty() {
        return this.modelgenModelSizes;
    }

    public final ObservableList<Integer> getModelgenModelSizes() {
        return this.modelgenModelSizesProperty().get();
    }

    public final void setModelgenModelSizes(final ObservableList<Integer> modelgenModelSizes) {
        this.modelgenModelSizesProperty().set(modelgenModelSizes);
    }

    public final StringProperty metamodelsRegistrationMethodProperty() {
        return this.metamodelsRegistrationMethod;
    }

    public final String getMetamodelsRegistrationMethod() {
        return this.metamodelsRegistrationMethodProperty().get();
    }

    public final void setMetamodelsRegistrationMethod(final String metamodelsRegistrationMethod) {
        this.metamodelsRegistrationMethodProperty().set(metamodelsRegistrationMethod);
    }

    public final BooleanProperty fwdActiveProperty() {
        return this.fwdActive;
    }

    public final boolean isFwdActive() {
        return this.fwdActiveProperty().get();
    }

    public final void setFwdActive(final boolean fwdActive) {
        this.fwdActiveProperty().set(fwdActive);
    }

    public final IntegerProperty fwdTimeoutProperty() {
        return this.fwdTimeout;
    }

    public final int getFwdTimeout() {
        return this.fwdTimeoutProperty().get();
    }

    public final int getEffectiveFwdTimeout() {
        return getValueOrDefault(getFwdTimeout(), 0, getEffectiveDefaultTimeout());
    }

    public final void setFwdTimeout(final int fwdTimeout) {
        this.fwdTimeoutProperty().set(fwdTimeout);
    }

    public final IntegerProperty fwdMaxModelSizeProperty() {
        return this.fwdMaxModelSize;
    }

    public final int getFwdMaxModelSize() {
        return this.fwdMaxModelSizeProperty().get();
    }

    public final int getEffectiveFwdMaxModelSize() {
        return getValueOrDefault(getFwdMaxModelSize(), -1, Collections.max(modelgenModelSizes));
    }

    public final void setFwdMaxModelSize(final int fwdMaxModelSize) {
        this.fwdMaxModelSizeProperty().set(fwdMaxModelSize);
    }

    public final StringProperty fwdIncrementalEditMethodProperty() {
        return this.fwdIncrementalEditMethod;
    }

    public final String getFwdIncrementalEditMethod() {
        return this.fwdIncrementalEditMethodProperty().get();
    }

    public final void setFwdIncrementalEditMethod(final String fwdIncrementalEditMethod) {
        this.fwdIncrementalEditMethodProperty().set(fwdIncrementalEditMethod);
    }

    public final BooleanProperty bwdActiveProperty() {
        return this.bwdActive;
    }

    public final boolean isBwdActive() {
        return this.bwdActiveProperty().get();
    }

    public final void setBwdActive(final boolean bwdActive) {
        this.bwdActiveProperty().set(bwdActive);
    }

    public final IntegerProperty bwdTimeoutProperty() {
        return this.bwdTimeout;
    }

    public final int getBwdTimeout() {
        return this.bwdTimeoutProperty().get();
    }

    public final int getEffectiveBwdTimeout() {
        return getValueOrDefault(getFwdTimeout(), 0, getEffectiveDefaultTimeout());
    }

    public final void setBwdTimeout(final int bwdTimeout) {
        this.bwdTimeoutProperty().set(bwdTimeout);
    }

    public final IntegerProperty bwdMaxModelSizeProperty() {
        return this.bwdMaxModelSize;
    }

    public final int getBwdMaxModelSize() {
        return this.bwdMaxModelSizeProperty().get();
    }

    public final int getEffectiveBwdMaxModelSize() {
        return getValueOrDefault(getBwdMaxModelSize(), -1, Collections.max(modelgenModelSizes));
    }

    public final void setBwdMaxModelSize(final int bwdMaxModelSize) {
        this.bwdMaxModelSizeProperty().set(bwdMaxModelSize);
    }

    public final StringProperty bwdIncrementalEditMethodProperty() {
        return this.bwdIncrementalEditMethod;
    }

    public final String getBwdIncrementalEditMethod() {
        return this.bwdIncrementalEditMethodProperty().get();
    }

    public final void setBwdIncrementalEditMethod(final String bwdIncrementalEditMethod) {
        this.bwdIncrementalEditMethodProperty().set(bwdIncrementalEditMethod);
    }

    public final ObjectProperty<EclipseTggProject> eclipseProjectProperty() {
        return this.eclipseProject;
    }

    public final EclipseTggProject getEclipseProject() {
        return this.eclipseProjectProperty().get();
    }

    public final void setEclipseProject(final EclipseTggProject eclipseProject) {
        this.eclipseProjectProperty().set(eclipseProject);
    }

    public final StringProperty benchmarkCaseNameProperty() {
        return this.benchmarkCaseName;
    }

    public final String getBenchmarkCaseName() {
        return this.benchmarkCaseNameProperty().get();
    }

    public final void setBenchmarkCaseName(final String benchmarkCaseName) {
        this.benchmarkCaseNameProperty().set(benchmarkCaseName);
    }

    public final ObjectProperty<PatternMatchingEngine> patternMatchingEngineProperty() {
        return this.patternMatchingEngine;
    }

    public final PatternMatchingEngine getPatternMatchingEngine() {
        return this.patternMatchingEngineProperty().get();
    }

    public final void setPatternMatchingEngine(final PatternMatchingEngine patternMatchingEngine) {
        this.patternMatchingEngineProperty().set(patternMatchingEngine);
    }

    public final ListProperty<Pair<StringProperty, IntegerProperty>> modelgenRuleCountProperty() {
        return this.modelgenRuleCount;
    }
    

    public final ObservableList<Pair<StringProperty, IntegerProperty>> getModelgenRuleCount() {
        return this.modelgenRuleCountProperty().get();
    }
    

    public final void setModelgenRuleCount(final ObservableList<Pair<StringProperty, IntegerProperty>> modelgenRuleCount) {
        this.modelgenRuleCountProperty().set(modelgenRuleCount);
    }
    

}
