package org.emoflon.ibex.tgg.benchmark.model;

import javax.json.Json;
import javax.json.JsonObject;

import org.emoflon.ibex.tgg.benchmark.runner.report.ReportFileType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ch.qos.logback.*;

public class PluginPreferences implements IPreferences {

    // general
    private final StringProperty benchmarkPreferencesFileName;
    // TODO: implement LogLevel

    // benchmark
    private final IntegerProperty maxMemorySize;
    private IntegerProperty repetitions;

    // report
    private final StringProperty reportFilePath;
    private final StringProperty reportFileType;
    private final BooleanProperty includeErrors;

    // default values
    private IntegerProperty defaultTimeout;
    private ListProperty<Integer> defaultModelSizes;
    private BooleanProperty defaultModelgenIncludeReport;
    private BooleanProperty defaultInitialFwdActive;
    private BooleanProperty defaultInitialBwdActive;
    private BooleanProperty defaultFwdActive;
    private BooleanProperty defaultBwdActive;
    private BooleanProperty defaultFwdOptActive;
    private BooleanProperty defaultBwdOptActive;
    private BooleanProperty defaultCcActive;
    private BooleanProperty defaultCoActive;

    public PluginPreferences() {
        // general
        benchmarkPreferencesFileName = new SimpleStringProperty(".tgg-benchmark.json"); // relative to project

        // benchmark
        repetitions = new SimpleIntegerProperty(3);
        maxMemorySize = new SimpleIntegerProperty(4096);

        // report
        reportFilePath = new SimpleStringProperty("{workspace_path}/{Y}-{M}-{D} {h}-{m} TGGBenchmarkReport");
        reportFileType = new SimpleStringProperty(ReportFileType.EXCEL.toString());
        includeErrors = new SimpleBooleanProperty(true);

        // defaults
        defaultTimeout = new SimpleIntegerProperty(300);
        defaultModelSizes = new SimpleListProperty<>(FXCollections.observableArrayList(500, 1000, 2000, 4000));
        defaultModelgenIncludeReport = new SimpleBooleanProperty(true);
        defaultInitialFwdActive = new SimpleBooleanProperty(true);
        defaultInitialBwdActive = new SimpleBooleanProperty(true);
        defaultFwdActive = new SimpleBooleanProperty(true);
        defaultBwdActive = new SimpleBooleanProperty(true);
        defaultFwdOptActive = new SimpleBooleanProperty(false);
        defaultBwdOptActive = new SimpleBooleanProperty(false);
        defaultCcActive = new SimpleBooleanProperty(false);
        defaultCoActive = new SimpleBooleanProperty(false);
    }

    public PluginPreferences(JsonObject data) {
        this();

        JsonObject general = data.getJsonObject("general");
        if (general != null) {
            setBenchmarkPreferencesFileName(
                    data.getString("benchmarkPreferencesFileName", getBenchmarkPreferencesFileName()));
            setRepetitions(general.getInt("repetitions", getRepetitions()));
        }

        JsonObject benchmark = data.getJsonObject("benchmark");
        if (benchmark != null) {

        }

        JsonObject report = data.getJsonObject("report");
        if (report != null) {

        }

        JsonObject defaults = data.getJsonObject("defaults");
        if (defaults != null) {

        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject preferences = Json.createObjectBuilder().add("general",
                Json.createObjectBuilder().add("benchmarkPreferencesFileName", getBenchmarkPreferencesFileName())
                        .add("input", Json.createObjectBuilder().build())
                        .add("report", Json.createObjectBuilder().build())
                        .add("repetitions", getRepetitions())
                        .add("operationalizations", Json.createObjectBuilder()).build())
                .build();

        return preferences;
    }

    public final StringProperty benchmarkPreferencesFileNameProperty() {
        return this.benchmarkPreferencesFileName;
    }

    public final String getBenchmarkPreferencesFileName() {
        return this.benchmarkPreferencesFileNameProperty().get();
    }

    public final void setBenchmarkPreferencesFileName(final String benchmarkPreferencesFileName) {
        this.benchmarkPreferencesFileNameProperty().set(benchmarkPreferencesFileName);
    }

    public final StringProperty reportFilePathProperty() {
        return this.reportFilePath;
    }

    public final String getReportFilePath() {
        return this.reportFilePathProperty().get();
    }

    public final void setReportFilePath(final String reportFilePath) {
        this.reportFilePathProperty().set(reportFilePath);
    }

    public final IntegerProperty maxMemorySizeProperty() {
        return this.maxMemorySize;
    }

    public final int getMaxMemorySize() {
        return this.maxMemorySizeProperty().get();
    }

    public final void setMaxMemorySize(final int maxMemorySize) {
        this.maxMemorySizeProperty().set(maxMemorySize);
    }

    public final BooleanProperty includeErrorsProperty() {
        return this.includeErrors;
    }

    public final boolean isIncludeErrors() {
        return this.includeErrorsProperty().get();
    }

    public final void setIncludeErrors(final boolean includeErrors) {
        this.includeErrorsProperty().set(includeErrors);
    }

    public final StringProperty reportFileTypeProperty() {
        return this.reportFileType;
    }

    public final ReportFileType getReportFileType() {
        return ReportFileType.valueOf(this.reportFileTypeProperty().get());
    }

    public final void setReportFileType(final String reportFileType) {
        this.reportFileTypeProperty().set(reportFileType);
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

    public final IntegerProperty defaultTimeoutProperty() {
        return this.defaultTimeout;
    }
    

    public final int getDefaultTimeout() {
        return this.defaultTimeoutProperty().get();
    }
    

    public final void setDefaultTimeout(final int defaultTimeout) {
        this.defaultTimeoutProperty().set(defaultTimeout);
    }
    

    public final ListProperty<Integer> defaultModelSizesProperty() {
        return this.defaultModelSizes;
    }
    

    public final ObservableList<Integer> getDefaultModelSizes() {
        return this.defaultModelSizesProperty().get();
    }
    

    public final void setDefaultModelSizes(final ObservableList<Integer> defaultModelSizes) {
        this.defaultModelSizesProperty().set(defaultModelSizes);
    }
    

    public final BooleanProperty defaultModelgenIncludeReportProperty() {
        return this.defaultModelgenIncludeReport;
    }
    

    public final boolean isDefaultModelgenIncludeReport() {
        return this.defaultModelgenIncludeReportProperty().get();
    }
    

    public final void setDefaultModelgenIncludeReport(final boolean defaultModelgenIncludeReport) {
        this.defaultModelgenIncludeReportProperty().set(defaultModelgenIncludeReport);
    }
    

    public final BooleanProperty defaultInitialFwdActiveProperty() {
        return this.defaultInitialFwdActive;
    }
    

    public final boolean isDefaultInitialFwdActive() {
        return this.defaultInitialFwdActiveProperty().get();
    }
    

    public final void setDefaultInitialFwdActive(final boolean defaultInitialFwdActive) {
        this.defaultInitialFwdActiveProperty().set(defaultInitialFwdActive);
    }
    

    public final BooleanProperty defaultInitialBwdActiveProperty() {
        return this.defaultInitialBwdActive;
    }
    

    public final boolean isDefaultInitialBwdActive() {
        return this.defaultInitialBwdActiveProperty().get();
    }
    

    public final void setDefaultInitialBwdActive(final boolean defaultInitialBwdActive) {
        this.defaultInitialBwdActiveProperty().set(defaultInitialBwdActive);
    }
    

    public final BooleanProperty defaultFwdActiveProperty() {
        return this.defaultFwdActive;
    }
    

    public final boolean isDefaultFwdActive() {
        return this.defaultFwdActiveProperty().get();
    }
    

    public final void setDefaultFwdActive(final boolean defaultFwdActive) {
        this.defaultFwdActiveProperty().set(defaultFwdActive);
    }
    

    public final BooleanProperty defaultBwdActiveProperty() {
        return this.defaultBwdActive;
    }
    

    public final boolean isDefaultBwdActive() {
        return this.defaultBwdActiveProperty().get();
    }
    

    public final void setDefaultBwdActive(final boolean defaultBwdActive) {
        this.defaultBwdActiveProperty().set(defaultBwdActive);
    }
    

    public final BooleanProperty defaultFwdOptActiveProperty() {
        return this.defaultFwdOptActive;
    }
    

    public final boolean isDefaultFwdOptActive() {
        return this.defaultFwdOptActiveProperty().get();
    }
    

    public final void setDefaultFwdOptActive(final boolean defaultFwdOptActive) {
        this.defaultFwdOptActiveProperty().set(defaultFwdOptActive);
    }
    

    public final BooleanProperty defaultBwdOptActiveProperty() {
        return this.defaultBwdOptActive;
    }
    

    public final boolean isDefaultBwdOptActive() {
        return this.defaultBwdOptActiveProperty().get();
    }
    

    public final void setDefaultBwdOptActive(final boolean defaultBwdOptActive) {
        this.defaultBwdOptActiveProperty().set(defaultBwdOptActive);
    }
    

    public final BooleanProperty defaultCcActiveProperty() {
        return this.defaultCcActive;
    }
    

    public final boolean isDefaultCcActive() {
        return this.defaultCcActiveProperty().get();
    }
    

    public final void setDefaultCcActive(final boolean defaultCcActive) {
        this.defaultCcActiveProperty().set(defaultCcActive);
    }
    

    public final BooleanProperty defaultCoActiveProperty() {
        return this.defaultCoActive;
    }
    

    public final boolean isDefaultCoActive() {
        return this.defaultCoActiveProperty().get();
    }
    

    public final void setDefaultCoActive(final boolean defaultCoActive) {
        this.defaultCoActiveProperty().set(defaultCoActive);
    }
    

}
