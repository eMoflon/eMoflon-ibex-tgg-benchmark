package org.emoflon.tggbenchmark.gui.plugin_preferences;

import java.io.IOException;

import javax.json.JsonException;

import org.emoflon.tggbenchmark.Core;
import org.emoflon.tggbenchmark.gui.PluginPreferencesWindow;
import org.emoflon.tggbenchmark.gui.GUIUtils;
import org.emoflon.tggbenchmark.gui.model.PluginPreferences;
import org.emoflon.tggbenchmark.model.EclipseWorkspaceDebug;
import org.emoflon.tggbenchmark.runner.report.ReportFileType;
import org.emoflon.tggbenchmark.utils.JsonUtils;

import javafx.application.Application;
import javafx.stage.Stage;

public class PluginPreferencesWindowTestApp extends Application {

    private static PluginPreferences pluginPreferences;
    private static EclipseWorkspaceDebug eclipseWorkspace;

    @Override
    public void start(Stage primaryStage) throws IOException {
        PluginPreferencesWindow ppw = new PluginPreferencesWindow();

        try {
            ppw.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the application. Scince JavaFX 11 it is required, that the main method
     * and the "launch" call are decoupled, otherwise the call will fail. See
     * https://github.com/javafxports/openjdk-jfx/issues/236#issuecomment-426583174
     * 
     * @throws JsonException
     * @throws IOException
     */
    public static void run() throws JsonException, IOException {
        GUIUtils.initConfiguration();

        pluginPreferences = Core.getInstance().getPluginPreferences();
        eclipseWorkspace = (EclipseWorkspaceDebug) Core.getInstance().getWorkspace();

        pluginPreferences.setReportFileType(ReportFileType.EXCEL);

        System.out.println("Initial Plugin Preferences");
        System.out.println(JsonUtils.jsonToString(pluginPreferences.toJson()));

        System.out.println(System.getProperty("java.class.path"));

        launch();

        System.out.println("\n\nPlugin Preferences after closing");
        System.out.println(JsonUtils.jsonToString(pluginPreferences.toJson()));
    }
}
