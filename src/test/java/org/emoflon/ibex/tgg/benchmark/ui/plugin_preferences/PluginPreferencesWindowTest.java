package org.emoflon.ibex.tgg.benchmark.ui.plugin_preferences;

import java.io.IOException;
import javax.json.JsonException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.model.EclipseWorkspaceDebug;
import org.emoflon.ibex.tgg.benchmark.model.IEclipseWorkspace;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;
import org.emoflon.ibex.tgg.benchmark.utils.JsonUtils;

import javafx.application.Application;
import javafx.stage.Stage;

public class PluginPreferencesWindowTest extends Application {

    private static PluginPreferences pluginPreferences;
    private static IEclipseWorkspace eclipseWorkspace;
    private static Core pluginCore;

    @Override
    public void start(Stage primaryStage) throws IOException {
        PluginPreferencesWindow ppw = new PluginPreferencesWindow();

        try {
            ppw.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws JsonException, IOException {

        // configure logger
        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.ALL);

        eclipseWorkspace = new EclipseWorkspaceDebug();
        pluginPreferences = new PluginPreferences();
        pluginCore = Core.createInstance(eclipseWorkspace);
        pluginCore.setPluginPreferences(pluginPreferences);

        System.out.println("Initial Plugin Preferences");
        System.out.println(JsonUtils.jsonToString(pluginPreferences.toJson()));

        launch(args);

        System.out.println("\n\nPlugin Preferences after closing");
        System.out.println(JsonUtils.jsonToString(pluginPreferences.toJson()));
    }
}
