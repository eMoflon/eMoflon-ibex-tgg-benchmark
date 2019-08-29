package org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class BenchmarkCasePreferencesWindow {

    private final Stage stage;
    private final MainPart mainPart;

    public BenchmarkCasePreferencesWindow(BenchmarkCasePreferences bcp) throws IOException {
        // prepare stage
        stage = new Stage();
        stage.setTitle("Edit Benchmark Case");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNIFIED);

        // load main part
        mainPart = new MainPart();
        mainPart.initData(bcp);
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
