package org.emoflon.ibex.tgg.benchmark.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.utils.AsyncActions;
import org.emoflon.ibex.tgg.benchmark.utils.JsonUtils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * 
 *
 * @author Andre Lehmann
 */
public class EclipseTggProject extends EclipseJavaProject {
    
    private static final Logger LOG = LogManager.getLogger(Core.PLUGIN_NAME);

    private final ObjectProperty<BenchmarkCasePreferences> benchmarkCasePreferences;

    /**
     * Constructor for {@link EclipseTggProject}.
     */
    public EclipseTggProject(String name, Path projectPath, Path outputPath, Set<EclipseJavaProject> referencedProjects) {
        super(name, projectPath, outputPath, referencedProjects);
        this.benchmarkCasePreferences = new SimpleObjectProperty<BenchmarkCasePreferences>();
    }

    /**
     * Save the benchmark case preferences for this project.
     */
    public void savePreferences() {
        savePreferences(0L);
    }

    public void savePreferences(long waitBeforeSaving) {
        if (getBenchmarkCasePreferences() == null)
            return;

        Runnable saveAction = () -> {
            if (waitBeforeSaving > 0) {
                try {
                    Thread.sleep(waitBeforeSaving);
                } catch (InterruptedException e) {}
            }
    
            Path preferencesFilePath = getPreferencesPath();
    
            LOG.debug("Save preferences of project '{}' to file '{}'", getName(), preferencesFilePath);
    
            JsonObject prefsJsonObject = Json.createObjectBuilder().add("version", Core.VERSION)
                    .add("benchmarkCase", getBenchmarkCasePreferences().toJson()).build();
    
            try {
                JsonUtils.saveJsonToFile(prefsJsonObject, preferencesFilePath);
            } catch (IOException e) {
                LOG.error("Couldn't save preferences of project '{}' to file '{}'. Reason: {}", getName(), preferencesFilePath,
                        e.getMessage());
            }
        };

        AsyncActions.runUniqueAction(saveAction, 30, getName());
    }

    /**
     * Load the benchmark case preferences of this project. If the project
     * doesn't have a configuration yet a new one with default values will be
     * created.
     *
     * @throws JsonException if the file contains invalid json
     * @throws IOException if an error ocurred while loading the file
     */
    public void loadPreferences() throws JsonException, IOException {
        if (hasPreferencesFile()) {
            Path preferencesFilePath = getPreferencesPath();

            LOG.debug("Load preferences of project '{}' from '{}'", getName(), preferencesFilePath);

            JsonObject prefsJsonObject = JsonUtils.loadJsonFile(preferencesFilePath);
            // used in case of different versions of the file format
            String fileVersion = prefsJsonObject.getString("version", Core.VERSION);

            JsonObject benchmarkCase = prefsJsonObject.getJsonObject("benchmarkCase");
            if (benchmarkCase != null && benchmarkCase instanceof JsonObject) {
                setBenchmarkCasePreferences(new BenchmarkCasePreferences(benchmarkCase));
            } else {
                LOG.info("Project '{}' has an empty configuration, use default parameters", getName());
                setBenchmarkCasePreferences(new BenchmarkCasePreferences());
                savePreferences();
            }
        } else {
            LOG.info("Create default preferences for project '{}'", getName());
            setBenchmarkCasePreferences(new BenchmarkCasePreferences());
            savePreferences();
        }
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

    public final ObjectProperty<BenchmarkCasePreferences> benchmarkCasePreferencesProperty() {
        return this.benchmarkCasePreferences;
    }
    

    public final BenchmarkCasePreferences getBenchmarkCasePreferences() {
        return this.benchmarkCasePreferencesProperty().get();
    }
    

    public final void setBenchmarkCasePreferences(final BenchmarkCasePreferences benchmarkCasePreferences) {
        this.benchmarkCasePreferencesProperty().set(benchmarkCasePreferences);
    }
    
}
