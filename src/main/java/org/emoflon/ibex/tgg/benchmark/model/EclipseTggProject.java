package org.emoflon.ibex.tgg.benchmark.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.utils.JsonUtils;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 
 *
 * @author Andre Lehmann
 */
public class EclipseTggProject extends EclipseJavaProject {

    private static final Logger LOG = LogManager.getLogger(Core.PLUGIN_NAME);

    private final ListProperty<BenchmarkCasePreferences> benchmarkCasePreferences;

    private Thread preferencesSaveThread;

    /**
     * Constructor for {@link EclipseTggProject}.
     */
    public EclipseTggProject(String name, Path projectPath, Path outputPath,
            Set<EclipseJavaProject> referencedProjects) {
        super(name, projectPath, outputPath, referencedProjects);
        this.benchmarkCasePreferences = new SimpleListProperty<BenchmarkCasePreferences>();
    }

    /**
     * Save the benchmark cases for this project.
     * 
     * @throws IOException
     */
    public void savePreferences() throws IOException {
        Path preferencesFilePath = getPreferencesPath();

        LOG.debug("Save benchmark cases of project '{}' to file '{}'", getName(), preferencesFilePath);

        JsonArrayBuilder benchmarkCasesArray = Json.createArrayBuilder();
        for (BenchmarkCasePreferences bcp : getBenchmarkCasePreferences()) {
            benchmarkCasesArray.add(bcp.toJson());
        }

        JsonObject prefsJsonObject = Json.createObjectBuilder().add("version", Core.VERSION)
                .add("benchmarkCases", benchmarkCasesArray).build();

        try {
            JsonUtils.saveJsonToFile(prefsJsonObject, preferencesFilePath);
        } catch (IOException e) {
            LOG.error("Couldn't save benchmark cases of project '{}' to file '{}'. Reason: {}", getName(),
                    preferencesFilePath, e.getMessage());
            throw e;
        }
    }

    /**
     * Save the benchmark cases for this project with a delay of 5s.
     * 
     * The delay prevents multiple quick saving actions in a row.
     */
    public void delayedSavePreferences() {
        if (preferencesSaveThread == null || !preferencesSaveThread.isAlive()) {
            Runnable saveAction = () -> {
                try {
                    TimeUnit.SECONDS.sleep(5L);
                } catch (InterruptedException e) {
                }
                try {
                    savePreferences();
                } catch (IOException e) {
                }
            };

            preferencesSaveThread = new Thread(saveAction);
            preferencesSaveThread.start();
        }
    }

    /**
     * Load the benchmark case preferences of this project. If the project doesn't
     * have a configuration yet a new one with default values will be created.
     *
     * @throws JsonException if the file contains invalid json
     * @throws IOException   if an error ocurred while loading the file
     */
    public void loadPreferences() {
        LinkedList<BenchmarkCasePreferences> benchmarkCasePreferences = new LinkedList<>();
        
        if (hasPreferencesFile()) {
            Path preferencesFilePath = getPreferencesPath();

            LOG.debug("Load preferences of project '{}' from '{}'", getName(), preferencesFilePath);

            try {
                JsonObject prefsJsonObject = JsonUtils.loadJsonFile(preferencesFilePath);
             
                // used in case of different versions of the file format
                String fileVersion = prefsJsonObject.getString("version", Core.VERSION);

                JsonArray benchmarkCases = prefsJsonObject.getJsonArray("benchmarkCases");
                if (benchmarkCases != null) {
                    for (int i = 0; i < benchmarkCases.size(); i++) {
                        JsonValue benchmarkCase = benchmarkCases.get(i);
                        if (benchmarkCase instanceof JsonObject) {
                            BenchmarkCasePreferences bcp = new BenchmarkCasePreferences((JsonObject) benchmarkCase);
                            bcp.setEclipseProject(this);
                            benchmarkCasePreferences.add(bcp);
                        }
                    }
                }
            } catch (JsonException | IOException e) {
                LOG.error("Failed to load preferences of project '{}' from '{}'. Reason: {}", getName(), preferencesFilePath, e.getMessage());
            }
        } 
        setBenchmarkCasePreferences(FXCollections.observableArrayList(benchmarkCasePreferences));
    }

    /**
     * @return the file path for the preference file
     */
    public Path getPreferencesPath() {
        return getProjectPath().resolve(Core.getInstance().getPluginPreferences().getBenchmarkPreferencesFileName());
    }

    /**
     * @return true if a preferences file exists in the project
     */
    public boolean hasPreferencesFile() {
        return Files.exists(getPreferencesPath());
    }

    public void addBenchmarkCase(BenchmarkCasePreferences bcp) {
        getBenchmarkCasePreferences().add(bcp);
        delayedSavePreferences();
    }

    public void removeBenchmarkCase(BenchmarkCasePreferences bcp) {
        getBenchmarkCasePreferences().remove(bcp);
        delayedSavePreferences();
    }

    public final ListProperty<BenchmarkCasePreferences> benchmarkCasePreferencesProperty() {
        return this.benchmarkCasePreferences;
    }

    public final ObservableList<BenchmarkCasePreferences> getBenchmarkCasePreferences() {
        return this.benchmarkCasePreferencesProperty().get();
    }

    public final void setBenchmarkCasePreferences(
            final ObservableList<BenchmarkCasePreferences> benchmarkCasePreferences) {
        this.benchmarkCasePreferencesProperty().set(benchmarkCasePreferences);
    }
}
