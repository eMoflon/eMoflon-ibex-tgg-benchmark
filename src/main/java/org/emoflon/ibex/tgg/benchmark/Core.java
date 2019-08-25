package org.emoflon.ibex.tgg.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.emoflon.ibex.tgg.benchmark.model.EclipseTggProject;
import org.emoflon.ibex.tgg.benchmark.model.IEclipseWorkspace;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;
import org.emoflon.ibex.tgg.benchmark.utils.AsyncActions;
import org.emoflon.ibex.tgg.benchmark.utils.JsonUtils;

/**
 * Core class for TGG Benchmark plugin.
 */
public class Core {

    public static final String VERSION = "0.1.0";
    public static final String PLUGIN_NAME = "TGG-Benchmark";
    
    private static final Logger LOG = LogManager.getLogger(Core.PLUGIN_NAME);

    private static Core instance;
    private PluginPreferences pluginPreferences;
    private IEclipseWorkspace workspace;
    // private Path pluginPreferencesFilePath;

    private Core() {
        super();

        // this.pluginPreferencesFilePath = workspace.getPluginStateLocation().resolve("tgg-benchmark.json");
    }

    /**
     * @return get a singleton of Core
     */
    public static synchronized Core getInstance() {
        if (Core.instance == null) {
            Core.instance = new Core();
        }
        return Core.instance;
    }

    public static void configureLogger() {
        Configurator.initialize(new DefaultConfiguration());
        // set loglevel until the user preferences has been loaded
        Configurator.setRootLevel(Level.INFO);
    }

    // public void loadPluginPreferences() throws JsonException, IOException {
    //     if (Files.exists(pluginPreferencesFilePath)) {
    //         LOG.debug("Load plugin preferences from file '{}'", pluginPreferencesFilePath);

    //         JsonObject prefsJsonObject = null;
    //         try {
    //             prefsJsonObject = JsonUtils.loadJsonFile(pluginPreferencesFilePath);
    //         } catch (JsonException | IOException e) {
    //             LOG.error("Failed to load plugin preferences from file '{}'", pluginPreferencesFilePath);
    //             throw e;
    //         }

    //         // used in case of different versions of the file format
    //         String fileVersion = prefsJsonObject.getString("version", Core.VERSION);

    //         pluginPreferences = new PluginPreferences(prefsJsonObject);

    //     } else {
    //         LOG.info("Create default preferences for plugin");
    //         pluginPreferences = new PluginPreferences();
    //         savePluginPreferences();
    //     }
    // }

    // public void savePluginPreferences() {
    //     if (pluginPreferences == null)
    //         return;

    //     Runnable saveAction = () -> {
    //         LOG.debug("Save plugin preferences to file '{}'", pluginPreferencesFilePath);

    //         JsonObject prefsJsonObject = pluginPreferences.toJson();
    //         JsonUtils.addKey(prefsJsonObject, "version", Json.createValue(Core.VERSION));

    //         try {
    //             JsonUtils.saveJsonToFile(prefsJsonObject, pluginPreferencesFilePath);
    //         } catch (IOException e) {
    //             LOG.error("Couldn't save plugin preferences to file '{}'. Reason: {}", pluginPreferencesFilePath, e.getMessage());
    //         }
    //     };

    //     AsyncActions.runUniqueAction(saveAction, 30000, pluginPreferencesFilePath.toString());
    // }

    public void addBenchmarkCase() {
        // try {
        // BenchmarkCasePreferencesWindow bcpw = new
        // BenchmarkCasePreferencesWindow(bcp);
        // bcpw.show();
        // } catch (IOException e) {
        // System.err.println("Error creating window: " + e.getMessage());
        // }
    }

    public void editBenchmarkCase(EclipseTggProject project) {
//        try {
//            BenchmarkCasePreferencesWindow bcpw = new BenchmarkCasePreferencesWindow(project);
//            bcpw.show();
//        } catch (IOException e) {
//            LOG.error("Error creating window: " + e.getMessage());
//        }
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
    
    /**
     * Schedules benchmark jobs to be exucuted.
     * 
     * @param projects the benchmark cases that shall be executed
     */
    public void scheduleJobs(List<EclipseTggProject> projects) {

    }

    /**
     * @return the workspace
     */
    public IEclipseWorkspace getWorkspace() {
        return workspace;
    }

    /**
     * @param workspace the workspace to set
     */
    public void setWorkspace(IEclipseWorkspace workspace) {
        this.workspace = workspace;
    }

    /**
     * @return the pluginPreferences
     */
    public PluginPreferences getPluginPreferences() {
        return pluginPreferences;
    }

    /**
     * @param pluginPreferences the pluginPreferences to set
     */
    public void setPluginPreferences(PluginPreferences pluginPreferences) {
        this.pluginPreferences = pluginPreferences;
    }
}