package org.emoflon.ibex.tgg.benchmark.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import org.emoflon.ibex.tgg.benchmark.runner.PatternMatchingEngine;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BenchmarkCasePreferences implements IPreferences {

    // general
    private BooleanProperty markedForExecution;
    private StringProperty patternMatchingEngine;
    private IntegerProperty defaultTimeout;
    private IntegerProperty repetitions;

    

    // input
    private StringProperty metamodelsRegistrationMethod;

    // output

    // operationalizations
    private BooleanProperty modelgenCreateReport;
    private IntegerProperty modelgenTimeout;
    private ListProperty<Integer> modelgenModelSizes;
    private StringProperty modelgenTggRule;

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

    public BenchmarkCasePreferences() {
        // general
        markedForExecution = new SimpleBooleanProperty(true);
        patternMatchingEngine = new SimpleStringProperty(PatternMatchingEngine.Democles.toString());
        defaultTimeout = new SimpleIntegerProperty(600);
        repetitions = new SimpleIntegerProperty(3);

        // input
        metamodelsRegistrationMethod = new SimpleStringProperty("");

        // output

        // operationalizations
        modelgenCreateReport = new SimpleBooleanProperty(true);
        modelgenTimeout = new SimpleIntegerProperty(0);
        modelgenModelSizes = new SimpleListProperty<>(FXCollections.observableArrayList(1000, 2000, 4000, 8000));
        modelgenTggRule = new SimpleStringProperty("");

        initialFwdActive = new SimpleBooleanProperty(true);
        initialFwdTimeout = new SimpleIntegerProperty(0);
        initialFwdMaxModelSize = new SimpleIntegerProperty(-1);

        initialBwdActive = new SimpleBooleanProperty(true);
        initialBwdTimeout = new SimpleIntegerProperty(0);
        initialBwdMaxModelSize = new SimpleIntegerProperty(-1);

        fwdActive = new SimpleBooleanProperty(true);
        fwdTimeout = new SimpleIntegerProperty(0);
        fwdMaxModelSize = new SimpleIntegerProperty(-1);
        fwdIncrementalEditMethod = new SimpleStringProperty("");

        bwdActive = new SimpleBooleanProperty(true);
        bwdTimeout = new SimpleIntegerProperty(0);
        bwdMaxModelSize = new SimpleIntegerProperty(-1);
        bwdIncrementalEditMethod = new SimpleStringProperty("");

        fwdOptActive = new SimpleBooleanProperty(true);
        fwdOptTimeout = new SimpleIntegerProperty(0);
        fwdOptMaxModelSize = new SimpleIntegerProperty(-1);

        bwdOptActive = new SimpleBooleanProperty(true);
        bwdOptTimeout = new SimpleIntegerProperty(0);
        bwdOptMaxModelSize = new SimpleIntegerProperty(-1);

        ccActive = new SimpleBooleanProperty(true);
        ccTimeout = new SimpleIntegerProperty(0);
        ccMaxModelSize = new SimpleIntegerProperty(-1);

        coActive = new SimpleBooleanProperty(true);
        coTimeout = new SimpleIntegerProperty(0);
        coMaxModelSize = new SimpleIntegerProperty(-1);
    }

    public BenchmarkCasePreferences(EclipseProject project) {
        // TODO: implement it
        this();
    }

    public BenchmarkCasePreferences(BenchmarkCasePreferences source) {
        this();
        setChanges(source);
    }

    public BenchmarkCasePreferences(JsonObject data) {
        this();

        JsonObject general = data.getJsonObject("general");
        if (general != null) {
            setMarkedForExecution(general.getBoolean("markedForExecution", isMarkedForExecution()));
            setDefaultTimeout(general.getInt("defaultTimeout", getDefaultTimeout()));
            setRepetitions(general.getInt("repetitions", getRepetitions()));
        }

        JsonObject input = data.getJsonObject("input");
        if (input != null) {
            setMetamodelsRegistrationMethod(
                    input.getString("metamodelsRegistrationMethod", getMetamodelsRegistrationMethod()));
        }

        JsonObject output = data.getJsonObject("output");
        if (output != null) {
            // TODO: implement it
        }

        JsonObject operationalizations = data.getJsonObject("operationalizations");
        if (operationalizations != null) {
            JsonObject modelgen = operationalizations.getJsonObject("modelgen");
            if (modelgen != null) {
                setModelgenCreateReport(modelgen.getBoolean("createReport", isModelgenCreateReport()));
                setModelgenTimeout(modelgen.getInt("timeout", getModelgenTimeout()));
                JsonArray modelSizesArray = modelgen.getJsonArray("modelSizes");
                if (modelSizesArray != null) {
                    ObservableList<Integer> modelSizes = FXCollections.observableArrayList();
                    for (int i = 0; i < modelSizesArray.size(); i++) {
                        try {
                            modelSizes.add(modelSizesArray.getInt(i));
                        } catch (ClassCastException e) {
                            System.err.println(String.format("Invalid model size! '%s' is not an integer number",
                                    modelSizesArray.getString(i)));
                        }
                    }
                    setModelgenModelSizes(modelSizes);
                }
                setModelgenTggRule(modelgen.getString("modelgenTggRule", getModelgenTggRule()));
            }

            JsonObject initialFwd = operationalizations.getJsonObject("initialFwd");
            if (initialFwd != null) {
                setInitialFwdActive(initialFwd.getBoolean("active", isInitialFwdActive()));
                setInitialFwdTimeout(initialFwd.getInt("timeout", getInitialFwdTimeout()));
                setInitialFwdMaxModelSize(initialFwd.getInt("maxModelSize", getInitialFwdMaxModelSize()));
            }

            JsonObject initialBwd = operationalizations.getJsonObject("initialBwd");
            if (initialBwd != null) {
                setInitialBwdActive(initialBwd.getBoolean("active", isInitialBwdActive()));
                setInitialBwdTimeout(initialBwd.getInt("timeout", getInitialBwdTimeout()));
                setInitialBwdMaxModelSize(initialBwd.getInt("maxModelSize", getInitialBwdMaxModelSize()));
            }

            JsonObject fwd = operationalizations.getJsonObject("fwd");
            if (fwd != null) {
                setFwdActive(fwd.getBoolean("active", isFwdActive()));
                setFwdTimeout(fwd.getInt("timeout", getFwdTimeout()));
                setFwdMaxModelSize(fwd.getInt("maxModelSize", getFwdMaxModelSize()));
                setFwdIncrementalEditMethod(fwd.getString("incrementalEditMethod", getFwdIncrementalEditMethod()));
            }

            JsonObject bwd = operationalizations.getJsonObject("bwd");
            if (bwd != null) {
                setBwdActive(bwd.getBoolean("active", isBwdActive()));
                setBwdTimeout(bwd.getInt("timeout", getBwdTimeout()));
                setBwdMaxModelSize(bwd.getInt("maxModelSize", getBwdMaxModelSize()));
                setBwdIncrementalEditMethod(bwd.getString("incrementalEditMethod", getBwdIncrementalEditMethod()));
            }

            JsonObject fwdOpt = operationalizations.getJsonObject("fwdOpt");
            if (fwdOpt != null) {
                setFwdOptActive(fwdOpt.getBoolean("active", isFwdOptActive()));
                setFwdOptTimeout(fwdOpt.getInt("timeout", getFwdOptTimeout()));
                setFwdOptMaxModelSize(fwdOpt.getInt("maxModelSize", getFwdOptMaxModelSize()));
            }

            JsonObject bwdOpt = operationalizations.getJsonObject("bwdOpt");
            if (bwdOpt != null) {
                setBwdOptActive(bwdOpt.getBoolean("active", isBwdOptActive()));
                setBwdOptTimeout(bwdOpt.getInt("timeout", getBwdOptTimeout()));
                setBwdOptMaxModelSize(bwdOpt.getInt("maxModelSize", getBwdOptMaxModelSize()));
            }

            JsonObject cc = operationalizations.getJsonObject("cc");
            if (cc != null) {
                setCcActive(cc.getBoolean("active", isCcActive()));
                setCcTimeout(cc.getInt("timeout", getCcTimeout()));
                setCcMaxModelSize(cc.getInt("maxModelSize", getCcMaxModelSize()));
            }

            JsonObject co = operationalizations.getJsonObject("co");
            if (co != null) {
                setCoActive(co.getBoolean("active", isCoActive()));
                setCoTimeout(co.getInt("timeout", getCoTimeout()));
                setCoMaxModelSize(co.getInt("maxModelSize", getCoMaxModelSize()));
            }
        }
    }

    public JsonObject toJson() {

        JsonArrayBuilder modelSizesBuilder = Json.createArrayBuilder();
        for (Integer integer : getModelgenModelSizes()) {
            modelSizesBuilder.add(integer);
        }

        JsonObject preferences = Json.createObjectBuilder()
                .add("general", Json.createObjectBuilder().add("markedForExecution", isMarkedForExecution())
                        .add("defaultTimeout", getDefaultTimeout()).add("repetitions", getRepetitions()).build())
                .add("input",
                        Json.createObjectBuilder()
                                .add("metamodelsRegistrationMethod", getMetamodelsRegistrationMethod()).build())
                .add("output", Json.createObjectBuilder().build())
                .add("operationalizations",
                        Json.createObjectBuilder()
                                .add("modelgen",
                                        Json.createObjectBuilder().add("createReport", isModelgenCreateReport())
                                                .add("timeout", getModelgenTimeout())
                                                .add("modelSizes", modelSizesBuilder.build())
                                                .add("tggRule", getModelgenTggRule()).build())
                                .add("initialFwd",
                                        Json.createObjectBuilder().add("active", isInitialFwdActive())
                                                .add("timeout", getInitialFwdTimeout())
                                                .add("maxModelSize", getInitialFwdMaxModelSize()).build())
                                .add("initialBwd",
                                        Json.createObjectBuilder().add("active", isInitialBwdActive())
                                                .add("timeout", getInitialBwdTimeout())
                                                .add("maxModelSize", getInitialBwdMaxModelSize()).build())
                                .add("fwd",
                                        Json.createObjectBuilder().add("active", isFwdActive())
                                                .add("timeout", getFwdTimeout())
                                                .add("maxModelSize", getFwdMaxModelSize())
                                                .add("incrementalEditMethod", getFwdIncrementalEditMethod())
                                                .build())
                                .add("bwd",
                                        Json.createObjectBuilder().add("active", isBwdActive())
                                                .add("timeout", getBwdTimeout())
                                                .add("maxModelSize", getBwdMaxModelSize())
                                                .add("incrementalEditMethod", getBwdIncrementalEditMethod())
                                                .build())
                                .add("fwdOpt",
                                        Json.createObjectBuilder().add("active", isFwdOptActive())
                                                .add("timeout", getFwdOptTimeout())
                                                .add("maxModelSize", getFwdOptMaxModelSize()).build())
                                .add("bwdOpt",
                                        Json.createObjectBuilder().add("active", isBwdOptActive())
                                                .add("timeout", getBwdOptTimeout())
                                                .add("maxModelSize", getBwdOptMaxModelSize()).build())
                                .add("cc",
                                        Json.createObjectBuilder().add("active", isCcActive())
                                                .add("timeout", getCcTimeout()).add("maxModelSize", getCcMaxModelSize())
                                                .build())
                                .add("co",
                                        Json.createObjectBuilder().add("active", isCoActive())
                                                .add("timeout", getCoTimeout()).add("maxModelSize", getCoMaxModelSize())
                                                .build())
                                .build())
                .build();

        return preferences;
    }

    public final List<Observable> getAllProperties() {
        return new LinkedList<Observable>(Arrays.asList(markedForExecution, defaultTimeout, repetitions,
                modelgenCreateReport, modelgenTimeout, modelgenModelSizes, initialFwdActive, initialFwdTimeout,
                initialFwdMaxModelSize, initialBwdActive, initialBwdTimeout, initialBwdMaxModelSize, fwdOptActive,
                fwdOptTimeout, fwdOptMaxModelSize, bwdOptActive, bwdOptTimeout, bwdOptMaxModelSize, syncActive,
                syncTimeout, syncMaxModelSize, syncDirection, incrementalEditMethod, ccActive, ccTimeout,
                ccMaxModelSize, coActive, coTimeout, coMaxModelSize));
    }

    public void setChanges(BenchmarkCasePreferences source) {
        List<Observable> myProperties = getAllProperties();
        List<Observable> theirProperties = source.getAllProperties();

        for (int i = 0; i < myProperties.size(); i++) {
            if (myProperties.get(i) instanceof StringProperty) {
                StringProperty myProperty = (StringProperty) myProperties.get(i);
                StringProperty theirProperty = (StringProperty) theirProperties.get(i);
                if (!myProperty.getValue().equals(theirProperty.getValue())) {
                    myProperty.setValue(theirProperty.getValue());
                }
            } else if (myProperties.get(i) instanceof BooleanProperty) {
                BooleanProperty myProperty = (BooleanProperty) myProperties.get(i);
                BooleanProperty theirProperty = (BooleanProperty) theirProperties.get(i);
                if (!myProperty.getValue().equals(theirProperty.getValue())) {
                    myProperty.setValue(theirProperty.getValue());
                }
            } else if (myProperties.get(i) instanceof IntegerProperty) {
                IntegerProperty myProperty = (IntegerProperty) myProperties.get(i);
                IntegerProperty theirProperty = (IntegerProperty) theirProperties.get(i);
                if (!myProperty.getValue().equals(theirProperty.getValue())) {
                    myProperty.setValue(theirProperty.getValue());
                }
            } else if (myProperties.get(i) instanceof ListProperty<?>) {
                ListProperty<?> myProperty = (ListProperty<?>) myProperties.get(i);
                ListProperty<?> theirProperty = (ListProperty<?>) theirProperties.get(i);
                if (!myProperty.getValue().equals(theirProperty.getValue())) {
                    myProperty.setAll((List) theirProperty.stream().collect(Collectors.toList()));
                }
            }
        }
    }

    private <T> T getFinalValue(T value, T whenValue, T defaultValue) {
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

    public final void setDefaultTimeout(final int defaultTimeout) {
        this.defaultTimeoutProperty().set(defaultTimeout);
    }

    public final IntegerProperty repetitionsProperty() {
        return this.repetitions;
    }

    public final int getRepetitions() {
        return this.repetitionsProperty().get();
    }

    public final void setRepetitions(final int repetitions) {
        this.repetitionsProperty().set(repetitions);
    }

    public final BooleanProperty modelgenCreateReportProperty() {
        return this.modelgenCreateReport;
    }

    public final boolean isModelgenCreateReport() {
        return this.modelgenCreateReportProperty().get();
    }

    public final void setModelgenCreateReport(final boolean modelgenCreateReport) {
        this.modelgenCreateReportProperty().set(modelgenCreateReport);
    }

    public final IntegerProperty modelgenTimeoutProperty() {
        return this.modelgenTimeout;
    }

    public final int getModelgenTimeout() {
        return this.modelgenTimeoutProperty().get();
    }

    public final int getFinalModelgenTimeout() {
        return getFinalValue(getModelgenTimeout(), 0, getDefaultTimeout());
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

    public final int getFinalInitialFwdTimeout() {
        return getFinalValue(getInitialFwdTimeout(), 0, getDefaultTimeout());
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

    public final int getFinalInitialFwdMaxModelSize() {
        return getFinalValue(getInitialFwdMaxModelSize(), -1, modelgenModelSizes.get(modelgenModelSizes.size() - 1));
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

    public final int getFinalInitialBwdTimeout() {
        return getFinalValue(getInitialBwdTimeout(), 0, getDefaultTimeout());
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

    public final int getFinalInitialBwdMaxModelSize() {
        return getFinalValue(getInitialBwdMaxModelSize(), -1, modelgenModelSizes.get(modelgenModelSizes.size() - 1));
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

    public final int getFinalFwdOptTimeout() {
        return getFinalValue(getFwdOptTimeout(), 0, getDefaultTimeout());
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

    public final int getFinalFwdOptMaxModelSize() {
        return getFinalValue(getFwdOptMaxModelSize(), -1, modelgenModelSizes.get(modelgenModelSizes.size() - 1));
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

    public final int getFinalBwdOptTimeout() {
        return getFinalValue(getBwdOptTimeout(), 0, getDefaultTimeout());
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

    public final int getFinalBwdOptMaxModelSize() {
        return getFinalValue(getBwdOptMaxModelSize(), -1, modelgenModelSizes.get(modelgenModelSizes.size() - 1));
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

    public final int getFinalCcTimeout() {
        return getFinalValue(getCcTimeout(), 0, getDefaultTimeout());
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

    public final int getFinalCcMaxModelSize() {
        return getFinalValue(getCcMaxModelSize(), -1, modelgenModelSizes.get(modelgenModelSizes.size() - 1));
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

    public final int getFinalCoTimeout() {
        return getFinalValue(getCoTimeout(), 0, getDefaultTimeout());
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

    public final int getFinalCoMaxModelSize() {
        return getFinalValue(getCoMaxModelSize(), -1, modelgenModelSizes.get(modelgenModelSizes.size() - 1));
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

    public final StringProperty modelgenTggRuleProperty() {
        return this.modelgenTggRule;
    }

    public final String getModelgenTggRule() {
        return this.modelgenTggRuleProperty().get();
    }

    public final void setModelgenTggRule(final String modelgenTggRule) {
        this.modelgenTggRuleProperty().set(modelgenTggRule);
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

    public final int getFinalFwdTimeout() {
        return getFinalValue(getFwdTimeout(), 0, getDefaultTimeout());
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

    public final int getFinalFwdMaxModelSize() {
        return getFinalValue(getFwdMaxModelSize(), -1, modelgenModelSizes.get(modelgenModelSizes.size() - 1));
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

    public final int getFinalBwdTimeout() {
        return getFinalValue(getFwdTimeout(), 0, getDefaultTimeout());
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

    public final int getFinalBwdMaxModelSize() {
        return getFinalValue(getBwdMaxModelSize(), -1, modelgenModelSizes.get(modelgenModelSizes.size() - 1));
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

    public final StringProperty patternMatchingEngineProperty() {
        return this.patternMatchingEngine;
    }
    

    public final String getPatternMatchingEngine() {
        return this.patternMatchingEngineProperty().get();
    }
    

    public final void setPatternMatchingEngine(final String patternMatchingEngine) {
        this.patternMatchingEngineProperty().set(patternMatchingEngine);
    }
    

}
