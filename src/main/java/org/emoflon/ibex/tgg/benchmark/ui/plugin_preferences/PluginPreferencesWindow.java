package org.emoflon.ibex.tgg.benchmark.ui.plugin_preferences;

import java.io.IOException;
import java.util.List;

import org.emoflon.ibex.tgg.benchmark.model.EclipseProject;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PluginPreferencesWindow {

    private final Stage stage;
    private final MainPart mainPart;

    public PluginPreferencesWindow(PluginPreferences pluginPreferences) throws IOException {
        // prepare stage
        stage = new Stage();
        stage.setTitle("TGG Benchmark Plugin Preferences");
        stage.initModality(Modality.APPLICATION_MODAL);

        // load main part
        mainPart = new MainPart();
        mainPart.initData(pluginPreferences);
        stage.setScene(new Scene(mainPart.getContent()));
    }

    public PluginPreferencesWindow(List<EclipseProject> projects) throws IOException {
        // TODO: Implement it
     
        stage = new Stage();
        stage.setTitle("Benchmark Case Preferences");
        mainPart = new MainPart();
    }

    public void show() {
        stage.setMinWidth(600.0);
        stage.setMinHeight(300.0);
        stage.setWidth(870.0);
        stage.setHeight(600.0);
        stage.show();
    }
}
