package org.emoflon.tggbenchmark.gui.benchmark_case_preferences;

import java.io.IOException;

import javax.json.JsonException;

import org.emoflon.tggbenchmark.Core;
import org.emoflon.tggbenchmark.gui.BenchmarkCaseWindow;
import org.emoflon.tggbenchmark.gui.GUIUtils;
import org.emoflon.tggbenchmark.gui.model.BenchmarkCase;
import org.emoflon.tggbenchmark.gui.model.PluginPreferences;
import org.emoflon.tggbenchmark.model.EclipseWorkspaceDebug;

import javafx.application.Application;
import javafx.stage.Stage;

public class BenchmarkCasePreferencesWindowTestApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        GUIUtils.initConfiguration();
        PluginPreferences pluginPreferences = Core.getInstance().getPluginPreferences();
        EclipseWorkspaceDebug eclipseWorkspace = (EclipseWorkspaceDebug) Core.getInstance().getWorkspace();

        BenchmarkCase bc = eclipseWorkspace.getTggProjects().get(0).getBenchmarkCase().get(0);
        BenchmarkCaseWindow bcw = new BenchmarkCaseWindow(bc);

        try {
            bcw.show();
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
        launch();
    }
}
