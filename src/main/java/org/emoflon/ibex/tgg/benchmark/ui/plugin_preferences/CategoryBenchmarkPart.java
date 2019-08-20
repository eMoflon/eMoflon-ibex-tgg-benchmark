package org.emoflon.ibex.tgg.benchmark.ui.plugin_preferences;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;
import org.emoflon.ibex.tgg.benchmark.ui.components.IntegerTextField;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryPart;

import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;

public class CategoryBenchmarkPart extends CategoryPart<PluginPreferences> {

    // elements from the FXML resource
    @FXML
    private IntegerTextField maxMemorySize;
    @FXML
    private IntegerTextField repetitions;

    private Tooltip maxMemorySizeTooltip;
    private Tooltip repetitionsTooltip;

    public CategoryBenchmarkPart() throws IOException {
        super("../../resources/fxml/plugin_preferences/CategoryBenchmark.fxml");
    }

    @Override
    public void initData(PluginPreferences preferencesData) {
        super.initData(preferencesData);

        // tooltips
        maxMemorySizeTooltip = new Tooltip("Memory size of the JVM for the benchmark run in MiB");
        repetitionsTooltip = new Tooltip("Number of benchmark repetition for each operationalization");

        // bindings
        maxMemorySize.bindIntegerProperty(preferencesData.maxMemorySizeProperty());
        maxMemorySize.setTooltip(maxMemorySizeTooltip);

        repetitions.bindIntegerProperty(preferencesData.repetitionsProperty());
        repetitions.setTooltip(repetitionsTooltip);
    }
}
