package org.emoflon.ibex.tgg.benchmark.ui.plugin_preferences;

import java.io.IOException;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PluginPreferencesWindow {

    private final Stage stage;
    private final MainPart mainPart;

    public PluginPreferencesWindow() throws IOException {
        // prepare stage
        stage = new Stage();
        stage.setTitle("Edit TGG Benchmark Plugin Preferences");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNIFIED);

        // load main part
        mainPart = new MainPart();
        stage.setScene(new Scene(mainPart.getContent()));
    }

    public void show() {
        stage.setMinWidth(600.0);
        stage.setMinHeight(300.0);
        stage.setWidth(870.0);
        stage.setHeight(600.0);
        stage.show();
    }
}
