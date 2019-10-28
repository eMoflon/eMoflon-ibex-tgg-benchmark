package org.emoflon.tggbenchmark.gui.benchmark_case_preferences;

import java.io.IOException;

import javax.json.JsonException;

import org.emoflon.tggbenchmark.Core;
import org.emoflon.tggbenchmark.gui.BenchmarkCaseWindow;
import org.emoflon.tggbenchmark.gui.Utils;
import org.emoflon.tggbenchmark.gui.model.BenchmarkCase;
import org.emoflon.tggbenchmark.gui.model.PluginPreferences;
import org.emoflon.tggbenchmark.model.EclipseWorkspaceDebug;

import javafx.application.Application;
import javafx.stage.Stage;

public class BenchmarkCasePreferencesWindowTest extends Application {

    private static PluginPreferences pluginPreferences;
    private static EclipseWorkspaceDebug eclipseWorkspace;
    private static Core pluginCore;

    @Override
    public void start(Stage primaryStage) throws IOException {
        BenchmarkCase bc = eclipseWorkspace.getTggProjects().get(0).getBenchmarkCase().get(0);
        BenchmarkCaseWindow bcw = new BenchmarkCaseWindow(bc);

        try {
            bcw.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws JsonException, IOException {
        Utils.initConfiguration();

        pluginPreferences = Core.getInstance().getPluginPreferences();
        eclipseWorkspace = (EclipseWorkspaceDebug) Core.getInstance().getWorkspace();

        launch(args);

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
