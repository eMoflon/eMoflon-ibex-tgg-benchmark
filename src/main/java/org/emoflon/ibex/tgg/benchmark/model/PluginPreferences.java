package org.emoflon.ibex.tgg.benchmark.model;

import javax.json.Json;
import javax.json.JsonObject;

import org.emoflon.ibex.tgg.benchmark.runner.report.ReportFileType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PluginPreferences implements IPreferences {

    // general
    private final StringProperty benchmarkPreferencesFileName;

    // benchmark
    private final IntegerProperty maxMemorySize;
    private IntegerProperty repetitions;

    // report
    private final StringProperty reportFilePath;
    private final StringProperty reportFileType;
    private final BooleanProperty includeErrors;

    // default values

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

}
