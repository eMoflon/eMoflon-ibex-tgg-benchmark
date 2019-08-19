package org.emoflon.ibex.tgg.benchmark.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.utils.AsyncActions;
import org.emoflon.ibex.tgg.benchmark.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * EclipseProject is a simple representation of an Eclipse project.
 *
 * @author Andre Lehmann
 */
public class EclipseProject {

    private static final Logger LOG = LoggerFactory.getLogger(Core.PLUGIN_NAME);

    private final StringProperty name;
    private final Path path;
    private final Path[] classPaths;
    private BenchmarkCasePreferences benchmarkCasePreferences;

    /**
     * Constructor for {@link EclipseProject}.
     */
    public EclipseProject(String name, Path path, Path[] classPaths) {
        this.name = new SimpleStringProperty(name);
        this.path = path;
        this.classPaths = classPaths;
    }

    /**
     * Save the benchmark case preferences for this project.
     */
    public void savePreferences() {
        savePreferences(0L);
    }

    public void savePreferences(long waitBeforeSaving) {
        if (benchmarkCasePreferences == null)
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
                    .add("benchmarkCase", benchmarkCasePreferences.toJson()).build();
    
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
     * @return the name property
     */
    public final StringProperty nameProperty() {
        return this.name;
    }
    
    /**
     * @return the project name
     */
    public final String getName() {
        return this.nameProperty().get();
    }
    
    /**
     * @param name the project name to set
     */
    public final void setName(final String name) {
        this.nameProperty().set(name);
    }

    /**
     * @return the class paths for this project
     */
    public Path[] getClassPaths() {
        return classPaths;
    }
    
    /**
     * @return the class paths for this project as URLs
     */
    public URL[] getClassPathURLs() {
        LinkedList<URL> urls = new LinkedList<URL>();
        for (Path path : classPaths) {
            try {
                urls.add(path.toUri().toURL());
            } catch (MalformedURLException e) {
                // ignore
            }
        }
        return urls.toArray(new URL[urls.size()]);
    }
    
    /**
     * @return the project path
     */
    public Path getPath() {
        return path;
    }

    /**
     * @return the file path for the preference file
     */
    public Path getPreferencesPath() {
        return path.resolve(Core.getInstance().getPluginPreferences().getBenchmarkPreferencesFileName());
    }

    /**
     * @return true if a preferences file exists in the project
     */
    public boolean hasPreferencesFile() {
        return Files.exists(getPreferencesPath());
    }

    /**
     * @return the preferences object
     */
    public BenchmarkCasePreferences getBenchmarkCasePreferences() {
        return benchmarkCasePreferences;
    }

    /**
     * @param benchmarkCasePreferences the benchmarkCasePreferences to set
     */
    public void setBenchmarkCasePreferences(BenchmarkCasePreferences benchmarkCasePreferences) {
        this.benchmarkCasePreferences = benchmarkCasePreferences;
    }
}
