package org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.ui.components.IntegerTextField;
import org.emoflon.ibex.tgg.benchmark.ui.components.TimeTextField;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryPart;

import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;

public class CategoryGeneralPart extends CategoryPart<BenchmarkCasePreferences> {

    // elements from the FXML resource
    @FXML
    private TimeTextField defaultTimeout;
    @FXML
    private IntegerTextField repetitions;

    private Tooltip timeoutTooltip;
    private Tooltip repetitionsTooltip;

    public CategoryGeneralPart() throws IOException {
        super("../../resources/fxml/benchmark_case_preferences/CategoryGeneral.fxml");
    }

    @Override
    public void initData(BenchmarkCasePreferences preferencesData) {
        super.initData(preferencesData);

        // tooltips
        timeoutTooltip = new Tooltip("Default timeout for the operationalizations.\nThe following formats are allowed: 30, 30s, 5m, 1h");
        repetitionsTooltip = new Tooltip("Number of runs the benchmark will execute to measure the performance");

        defaultTimeout.bindIntegerProperty(preferencesData.defaultTimeoutProperty());
        defaultTimeout.setTooltip(timeoutTooltip);
        
        repetitions.bindIntegerProperty(preferencesData.defaultTimeoutProperty());
        repetitions.setTooltip(repetitionsTooltip);
    }
}
