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
    private final StringProperty modelInstancesPath;
    private final StringProperty reportFilePath;
    private final StringProperty reportFileType;

    private final IntegerProperty maxMemorySize;

    private final BooleanProperty includeErrors;

    // input

    // output

    public PluginPreferences() {
        benchmarkPreferencesFileName = new SimpleStringProperty(".tgg-benchmark.json");
        modelInstancesPath = new SimpleStringProperty("instances/tgg-benchmark/");
        reportFilePath = new SimpleStringProperty("{workspace_path}/{Y}-{M}-{D}--{h}-{m}_TGGBenchmarkReport");

        reportFileType = new SimpleStringProperty(ReportFileType.CSV.toString());

        maxMemorySize = new SimpleIntegerProperty(4096);

        includeErrors = new SimpleBooleanProperty(true);
    }

    public PluginPreferences(JsonObject data) {
        this();

        JsonObject general = data.getJsonObject("general");
        if (general != null) {
            setBenchmarkPreferencesFileName(
                    data.getString("benchmarkPreferencesFileName", getBenchmarkPreferencesFileName()));
            setModelInstancesPath(data.getString("modelInstancesPath", getModelInstancesPath()));
        }

        JsonObject input = data.getJsonObject("input");
        if (input != null) {

        }

        JsonObject output = data.getJsonObject("output");
        if (output != null) {

        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject preferences = Json.createObjectBuilder().add("general",
                Json.createObjectBuilder().add("benchmarkPreferencesFileName", getBenchmarkPreferencesFileName())
                        .add("input", Json.createObjectBuilder().build())
                        .add("output", Json.createObjectBuilder().build())
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

    public final StringProperty modelInstancesPathProperty() {
        return this.modelInstancesPath;
    }

    public final String getModelInstancesPath() {
        return this.modelInstancesPathProperty().get();
    }

    public final void setModelInstancesPath(final String modelInstancesPath) {
        this.modelInstancesPathProperty().set(modelInstancesPath);
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

}
