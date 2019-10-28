package org.emoflon.tggbenchmark.workspace;

import java.io.IOException;
import java.net.URL;
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
import org.emoflon.tggbenchmark.Core;
import org.emoflon.tggbenchmark.gui.model.BenchmarkCase;
import org.emoflon.tggbenchmark.utils.JsonUtils;

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

    private final ListProperty<BenchmarkCase> benchmarkCase;

    private Thread preferencesSaveThread;

    /**
     * Constructor for {@link EclipseTggProject}.
     */
    public EclipseTggProject(String name, Path projectPath, Path outputPath, Set<URL> classPaths,
            Set<EclipseJavaProject> referencedProjects) {
        super(name, projectPath, outputPath, classPaths, referencedProjects);
        this.benchmarkCase = new SimpleListProperty<BenchmarkCase>();
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
        for (BenchmarkCase bc : getBenchmarkCase()) {
            benchmarkCasesArray.add(bc.toJson());
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
        LinkedList<BenchmarkCase> benchmarkCase = new LinkedList<>();

        if (hasPreferencesFile()) {
            Path preferencesFilePath = getPreferencesPath();

            LOG.debug("Load preferences of project '{}' from '{}'", getName(), preferencesFilePath);

            try {
                JsonObject prefsJsonObject = JsonUtils.loadJsonFile(preferencesFilePath);

                JsonArray benchmarkCases = prefsJsonObject.getJsonArray("benchmarkCases");
                if (benchmarkCases != null) {
                    for (int i = 0; i < benchmarkCases.size(); i++) {
                        JsonValue benchmarkCaseValue = benchmarkCases.get(i);
                        if (benchmarkCaseValue instanceof JsonObject) {
                            BenchmarkCase bc = new BenchmarkCase((JsonObject) benchmarkCaseValue);
                            bc.setEclipseProject(this);
                            benchmarkCase.add(bc);
                        }
                    }
                }
            } catch (JsonException | IOException e) {
                LOG.error("Failed to load preferences of project '{}' from '{}'. Reason: {}", getName(),
                        preferencesFilePath, e.getMessage());
            }
        }
        setBenchmarkCasePreferences(FXCollections.observableArrayList(benchmarkCase));
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

    public void addBenchmarkCase(BenchmarkCase bc) {
        getBenchmarkCase().add(bc);
        delayedSavePreferences();
    }

    public void removeBenchmarkCase(BenchmarkCase bc) {
        getBenchmarkCase().remove(bc);
        delayedSavePreferences();
    }

    public final ListProperty<BenchmarkCase> benchmarkCaseProperty() {
        return this.benchmarkCase;
    }

    public final ObservableList<BenchmarkCase> getBenchmarkCase() {
        return this.benchmarkCaseProperty().get();
    }

    public final void setBenchmarkCasePreferences(final ObservableList<BenchmarkCase> benchmarkCase) {
        this.benchmarkCaseProperty().set(benchmarkCase);
    }
}
