package org.emoflon.ibex.tgg.benchmark;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osgi.service.datalocation.Location;
import org.emoflon.ibex.tgg.Utils;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.model.EclipseProject;
import org.emoflon.ibex.tgg.benchmark.model.IEclipseWorkspace;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;
import org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences.BenchmarkCasePreferencesWindow;
import org.emoflon.ibex.tgg.benchmark.utils.AsyncActions;
import org.emoflon.ibex.tgg.benchmark.utils.JsonUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Core class for TGG Benchmark plugin.
 */
public class Core {

    public static final String VERSION = "0.1.0";
    public static final String PLUGIN_NAME = "TGG-Benchmark";

    private static final Logger LOG = LoggerFactory.getLogger(Core.PLUGIN_NAME);

    private static Core instance;
    private final ObservableList<EclipseProject> tggProjects;

    private PluginPreferences pluginPreferences;

    private final Path pluginPreferencesFilePath;

    private final IEclipseWorkspace workspace;

    private Core(IEclipseWorkspace workspace) {
        super();

        this.workspace = workspace;
        this.tggProjects = FXCollections.observableArrayList();
        this.pluginPreferencesFilePath = workspace.getPluginStateLocation().resolve("tgg-benchmark-plugin.json");
    }

    public void loadPluginPreferences() throws JsonException, IOException {
        if (Files.exists(pluginPreferencesFilePath)) {
            LOG.debug("Load plugin preferences from file '{}'", pluginPreferencesFilePath);

            JsonObject prefsJsonObject = null;
            try {
                prefsJsonObject = JsonUtils.loadJsonFile(pluginPreferencesFilePath);
            } catch (JsonException | IOException e) {
                LOG.error("Failed to load plugin preferences from file '{}'", pluginPreferencesFilePath);
                throw e;
            }

            // used in case of different versions of the file format
            String fileVersion = prefsJsonObject.getString("version", Core.VERSION);

            pluginPreferences = new PluginPreferences(prefsJsonObject);

        } else {
            LOG.info("Create default preferences for plugin");
            pluginPreferences = new PluginPreferences();
            savePluginPreferences();
        }

        tggProjects.addAll(workspace.getTGGProjects());
    }

    public void savePluginPreferences() {
        if (pluginPreferences == null)
            return;

        Runnable saveAction = () -> {
            LOG.debug("Save plugin preferences to file '{}'", pluginPreferencesFilePath);

            JsonObject prefsJsonObject = pluginPreferences.toJson();
            JsonUtils.addKey(prefsJsonObject, "version", Json.createValue(Core.VERSION));

            try {
                JsonUtils.saveJsonToFile(prefsJsonObject, pluginPreferencesFilePath);
            } catch (IOException e) {
                LOG.error("Couldn't save plugin preferences to file '{}'. Reason: {}", pluginPreferencesFilePath, e.getMessage());
            }
        };

        AsyncActions.runUniqueAction(saveAction, 30000, pluginPreferencesFilePath.toString());
    }

    public void addBenchmarkCase() {
        // try {
        // BenchmarkCasePreferencesWindow bcpw = new
        // BenchmarkCasePreferencesWindow(bcp);
        // bcpw.show();
        // } catch (IOException e) {
        // System.err.println("Error creating window: " + e.getMessage());
        // }
    }

    public void editBenchmarkCase(EclipseProject project) {
        try {
            BenchmarkCasePreferencesWindow bcpw = new BenchmarkCasePreferencesWindow(project);
            bcpw.show();
        } catch (IOException e) {
            LOG.error("Error creating window: " + e.getMessage());
        }
    }

    // public void deleteBenchmarkCase(BenchmarkCasePreferences bcp) {
    //     Alert confirmation = new Alert(AlertType.CONFIRMATION);
    //     confirmation.setTitle("Delete Benchmark Case");
    //     confirmation.setHeaderText("Do you really want to delete this Benchmark Case?");
    //     confirmation.setContentText(bcp.getBenchmarkCaseName());

    //     Optional<ButtonType> option = confirmation.showAndWait();
    //     if (option.get() == ButtonType.OK) {
    //         benchmarkCasePreferencesList.remove(bcp);
    //         savePluginPreferences();
    //         System.out.println("Deleting Benchmark Case: " + bcp.getBenchmarkCaseName());
    //     }
    // }

    public static Core createInstance(IEclipseWorkspace workspace) throws JsonException, IOException {
        if (Core.instance == null) {
            Core.instance = new Core(workspace);
        }
        return Core.instance;
    }
    
    /**
     * @return the tggProjects
     */
    public ObservableList<EclipseProject> getTggProjects() {
        return tggProjects;
    }

    /**
     * @return the instance
     */
    public static Core getInstance() {
        return Core.instance;
    }
    
    public IEclipseWorkspace getWorkspace() {
        return workspace;
    }

    /**
     * Schedules benchmark jobs to be exucuted.
     * 
     * @param projects the benchmark cases that shall be executed
     */
    public void scheduleJobs(List<EclipseProject> projects) {

    }

    public PluginPreferences getPluginPreferences() {
        return pluginPreferences;
    }
}