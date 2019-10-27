package org.emoflon.ibex.tgg.benchmark.ui.plugin_preferences;

import java.io.IOException;

import javax.json.JsonException;

import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.model.EclipseWorkspaceDebug;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;
import org.emoflon.ibex.tgg.benchmark.runner.report.ReportFileType;
import org.emoflon.ibex.tgg.benchmark.ui.Utils;
import org.emoflon.ibex.tgg.benchmark.utils.JsonUtils;

import javafx.application.Application;
import javafx.stage.Stage;

public class PluginPreferencesWindowTest extends Application {

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

    public static void main(String[] args) throws JsonException, IOException {
        Utils.initConfiguration();

        pluginPreferences = Core.getInstance().getPluginPreferences();
        eclipseWorkspace = (EclipseWorkspaceDebug) Core.getInstance().getWorkspace();

        pluginPreferences.setReportFileType(ReportFileType.EXCEL);

        System.out.println("Initial Plugin Preferences");
        System.out.println(JsonUtils.jsonToString(pluginPreferences.toJson()));

        System.out.println(System.getProperty("java.class.path"));
        
        launch(args);

        System.out.println("\n\nPlugin Preferences after closing");
        System.out.println(JsonUtils.jsonToString(pluginPreferences.toJson()));
    }
}
