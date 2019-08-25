package org.emoflon.ibex.tgg.benchmark.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.StandardLevel;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.runner.report.ReportFileType;
import org.emoflon.ibex.tgg.benchmark.utils.AsyncActions;
import org.emoflon.ibex.tgg.benchmark.utils.JsonUtils;

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

/**
 * PluginPreferences is the data model for preferences of the plugin.
 *
 * @author Andre Lehmann
 */
public class PluginPreferences {

    private static final Logger LOG = LogManager.getLogger(Core.PLUGIN_NAME);

    // general
    private final StringProperty benchmarkPreferencesFileName;
    private final ObjectProperty<StandardLevel> logLevel;

    // benchmark
    private final IntegerProperty maxMemorySize;
    private final IntegerProperty repetitions;

    // report
    private final StringProperty reportFilePath;
    private final ObjectProperty<ReportFileType> reportFileType;
    private final BooleanProperty includeErrors;

    // default values
    private final IntegerProperty defaultTimeout;
    private final ListProperty<Integer> defaultModelSizes;
    private final BooleanProperty defaultModelgenIncludeReport;
    private final BooleanProperty defaultInitialFwdActive;
    private final BooleanProperty defaultInitialBwdActive;
    private final BooleanProperty defaultFwdActive;
    private final BooleanProperty defaultBwdActive;
    private final BooleanProperty defaultFwdOptActive;
    private final BooleanProperty defaultBwdOptActive;
    private final BooleanProperty defaultCcActive;
    private final BooleanProperty defaultCoActive;

    /**
     * Constructor for {@link PluginPreferences}.
     */
    public PluginPreferences() {
        // general
        benchmarkPreferencesFileName = new SimpleStringProperty(".tgg-benchmark.json"); // relative to project
        logLevel = new SimpleObjectProperty<>(StandardLevel.ALL);

        // benchmark
        maxMemorySize = new SimpleIntegerProperty(4096);
        repetitions = new SimpleIntegerProperty(3);

        // report
        reportFilePath = new SimpleStringProperty("{workspace_path}/{Y}-{M}-{D} {h}-{m} TGGBenchmarkReport");
        reportFileType = new SimpleObjectProperty<>(ReportFileType.EXCEL);
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

    /**
     * Constructor for {@link PluginPreferences} that copies the values from a
     * source instance.
     * 
     * @param source The source instance to copy from
     */
    public PluginPreferences(PluginPreferences source) {
        this();
        copyValues(source);
    }

    /**
     * Constructor for {@link PluginPreferences} that loads the preferences from a
     * given JSON file.
     * 
     * @param filePath the file to load from
     * @throws NoSuchFileException if file doesn't exists
     * @throws JsonException       if JSON is invalid
     * @throws IOException         if loading the file failed for some reason
     */
    public PluginPreferences(Path filePath) throws NoSuchFileException, JsonException, IOException {
        this();
        loadFromFile(filePath);
    }

    /**
     * Constructor for {@link PluginPreferences} initalizes the values from a
     * {@link JsonObject}.
     * 
     * @param data The JSONObject as a value source
     */
    public PluginPreferences(JsonObject data) {
        this();
        fromJson(data);
    }

    /**
     * Copies the values of the given {@link PluginPreferences} into this instance.
     *
     * @param source The source instance
     */
    public void copyValues(PluginPreferences source) {
        setBenchmarkPreferencesFileName(source.getBenchmarkPreferencesFileName());
        setLogLevel(source.getLogLevel());

        // benchmark
        setMaxMemorySize(source.getMaxMemorySize());
        setRepetitions(source.getRepetitions());

        // report
        setReportFilePath(source.getReportFilePath());
        setReportFileType(source.getReportFileType());
        setIncludeErrors(source.isIncludeErrors());

        // defaults
        setDefaultTimeout(source.getDefaultTimeout());
        setDefaultModelSizes(FXCollections.observableArrayList(source.getDefaultModelSizes()));
        setDefaultModelgenIncludeReport(source.isDefaultModelgenIncludeReport());
        setDefaultInitialFwdActive(source.isDefaultInitialFwdActive());
        setDefaultInitialBwdActive(source.isDefaultInitialBwdActive());
        setDefaultFwdActive(source.isDefaultFwdActive());
        setDefaultBwdActive(source.isDefaultBwdActive());
        setDefaultFwdOptActive(source.isDefaultFwdOptActive());
        setDefaultBwdOptActive(source.isDefaultBwdOptActive());
        setDefaultCcActive(source.isDefaultCcActive());
        setDefaultCoActive(source.isDefaultCoActive());
    }

    /**
     * Loads the plugin preferences from a file.
     * 
     * @param filePath the file to load from
     * @throws NoSuchFileException if file doesn't exists
     * @throws JsonException       if JSON is invalid
     * @throws IOException         if loading the file failed for some reason
     */
    private void loadFromFile(Path filePath) throws NoSuchFileException, JsonException, IOException {
        if (!Files.exists(filePath)) {
            throw new NoSuchFileException("File '" + filePath.toString() + "' doesn't exist");
        }

        LOG.debug("Load plugin preferences from file '{}'", filePath);

        JsonObject prefsJsonObject = null;
        try {
            prefsJsonObject = JsonUtils.loadJsonFile(filePath);
        } catch (JsonException | IOException e) {
            LOG.error("Failed to load plugin preferences from file '{}'", filePath.toString());
            throw e;
        }

        // used in case of different versions of the file format
        String fileVersion = prefsJsonObject.getString("version", Core.VERSION);

        fromJson(prefsJsonObject);
    }

    public void saveToFile(Path filePath) {
        LOG.debug("Save plugin preferences to file '{}'", filePath.toString());

        JsonObject prefsJsonObject = toJson();
        JsonUtils.addKey(prefsJsonObject, "version", Json.createValue(Core.VERSION));

        try {
            JsonUtils.saveJsonToFile(prefsJsonObject, filePath);
        } catch (IOException e) {
            LOG.error("Couldn't save plugin preferences to file '{}'. Reason: {}", filePath.toString(), e.getMessage());
        }
    }

    /**
     * Loads the plugin preferences from a {@link JsonObject}.
     * 
     * @param data The JSONObject as a value source
     */
    private void fromJson(JsonObject data) {
        JsonObject general = data.getJsonObject("general");
        if (general != null) {
            String benchmarkPreferencesFileName = general.getString("benchmarkPreferencesFileName",
                    getBenchmarkPreferencesFileName());
            setBenchmarkPreferencesFileName(benchmarkPreferencesFileName != null ? benchmarkPreferencesFileName
                    : getBenchmarkPreferencesFileName());
            try {
                setLogLevel(StandardLevel.valueOf(general.getString("logLevel", getLogLevel().toString())));
            } catch (IllegalArgumentException e) {
                // keep default
            }
        }

        JsonObject benchmark = data.getJsonObject("benchmark");
        if (benchmark != null) {
            int maxMemorxSize = benchmark.getInt("maxMemorySize", getMaxMemorySize());
            setMaxMemorySize(maxMemorxSize > 0 ? maxMemorxSize : getMaxMemorySize());
            int repetitions = benchmark.getInt("repetitions", getRepetitions());
            setRepetitions(repetitions > 0 ? repetitions : getRepetitions());
        }

        JsonObject report = data.getJsonObject("report");
        if (report != null) {
            String reportFilePath = general.getString("filePath", getReportFilePath());
            setReportFilePath(reportFilePath != null ? reportFilePath : getReportFilePath());
            try {
                setReportFileType(
                        ReportFileType.valueOf(general.getString("fileType", getReportFileType().toString())));
            } catch (IllegalArgumentException e) {
                // keep default
            }
            setIncludeErrors(general.getBoolean("includeErrors", isIncludeErrors()));
        }

        JsonObject defaults = data.getJsonObject("defaults");
        if (defaults != null) {
            int defaultTimeout = benchmark.getInt("timeout", getDefaultTimeout());
            setDefaultTimeout(defaultTimeout > 0 ? defaultTimeout : getDefaultTimeout());
            JsonArray modelSizesArray = benchmark.getJsonArray("modelSizes");
            if (modelSizesArray != null) {
                ObservableList<Integer> modelSizes = FXCollections.observableArrayList();
                for (int i = 0; i < modelSizesArray.size(); i++) {
                    try {
                        modelSizes.add(modelSizesArray.getInt(i));
                    } catch (ClassCastException e) {
                        // ignore
                    }
                }
                setDefaultModelSizes(modelSizes);
            }
            setDefaultModelgenIncludeReport(
                    general.getBoolean("modelgenIncludeReport", isDefaultModelgenIncludeReport()));
            setDefaultInitialFwdActive(general.getBoolean("initialFwdActive", isDefaultInitialFwdActive()));
            setDefaultInitialBwdActive(general.getBoolean("initialBwdActive", isDefaultInitialBwdActive()));
            setDefaultFwdActive(general.getBoolean("fwdActive", isDefaultFwdActive()));
            setDefaultBwdActive(general.getBoolean("bwdActive", isDefaultBwdActive()));
            setDefaultFwdOptActive(general.getBoolean("fwdOptActive", isDefaultFwdOptActive()));
            setDefaultBwdOptActive(general.getBoolean("bwdOptActive", isDefaultBwdOptActive()));
            setDefaultCcActive(general.getBoolean("ccActive", isDefaultCcActive()));
            setDefaultCoActive(general.getBoolean("coActive", isDefaultCoActive()));
        }
    }

    /**
     * Converts the preferences object into a {@link JsonObject}.
     * 
     * @return JSON representation
     */
    public JsonObject toJson() {

        JsonArrayBuilder modelSizesBuilder = Json.createArrayBuilder();
        for (Integer integer : getDefaultModelSizes()) {
            modelSizesBuilder.add(integer);
        }

        JsonObject preferences = Json.createObjectBuilder().add("general",
                Json.createObjectBuilder().add("benchmarkPreferencesFileName", getBenchmarkPreferencesFileName())
                        .add("logLevel", getLogLevel().toString()).build())

                .add("benchmark",
                        Json.createObjectBuilder().add("maxMemorySize", getMaxMemorySize())
                                .add("repetitions", getRepetitions()).build())

                .add("report",
                        Json.createObjectBuilder().add("filePath", getReportFilePath())
                                .add("fileType", getReportFileType().toString()).add("includeErrors", isIncludeErrors())
                                .build())
                .add("defaults", Json.createObjectBuilder().add("timeout", getBenchmarkPreferencesFileName())
                        .add("modelSizes", modelSizesBuilder.build())
                        .add("modelgenIncludeReport", isDefaultModelgenIncludeReport())
                        .add("initialFwdActive", isDefaultInitialFwdActive())
                        .add("initialBwdActive", isDefaultInitialBwdActive()).add("fwdActive", isDefaultFwdActive())
                        .add("bwdActive", isDefaultBwdActive()).add("fwdOptActive", isDefaultFwdOptActive())
                        .add("bwdOptActive", isDefaultBwdOptActive()).add("ccActive", isDefaultCcActive())
                        .add("coActive", isDefaultCoActive()).build())
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

    public final ObjectProperty<StandardLevel> logLevelProperty() {
        return this.logLevel;
    }

    public final StandardLevel getLogLevel() {
        return this.logLevelProperty().get();
    }

    public final void setLogLevel(final StandardLevel logLevel) {
        this.logLevelProperty().set(logLevel);
    }

    public final ObjectProperty<ReportFileType> reportFileTypeProperty() {
        return this.reportFileType;
    }

    public final ReportFileType getReportFileType() {
        return this.reportFileTypeProperty().get();
    }

    public final void setReportFileType(final ReportFileType reportFileType) {
        this.reportFileTypeProperty().set(reportFileType);
    }
}
