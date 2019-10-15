package org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCase;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class BenchmarkCaseWindow {

    private final Stage stage;
    private final MainPart mainPart;

    public BenchmarkCaseWindow(BenchmarkCase bc) throws IOException {
        // prepare stage
        stage = new Stage();
        stage.setTitle("Edit Benchmark Case");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNIFIED);

        // load main part
        mainPart = new MainPart();
        mainPart.initData(bc);
        stage.setScene(new Scene(mainPart.getContent()));
    }

    public void show() {
        stage.setMinWidth(700.0);
        stage.setMinHeight(300.0);
        stage.setWidth(1070.0);
        stage.setHeight(600.0);
        stage.show();
    }
}
