package org.emoflon.ibex.tgg.benchmark.ui.plugin_preferences;

import java.io.IOException;

import org.controlsfx.validation.ValidationResult;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;
import org.emoflon.ibex.tgg.benchmark.ui.components.IntegerTextField;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryPart;

import javafx.fxml.FXML;
import javafx.scene.control.Control;
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
        validation.registerValidator(maxMemorySize,
                (Control c, String newValue) -> ValidationResult.fromErrorIf(maxMemorySize,
                        "Max memory must be greater 0MiB", newValue.isEmpty() || newValue.equals("0")));

        repetitions.bindIntegerProperty(preferencesData.repetitionsProperty());
        repetitions.setTooltip(repetitionsTooltip);
        validation.registerValidator(repetitions,
                (Control c, String newValue) -> ValidationResult.fromErrorIf(repetitions,
                        "Number of repetitions must be greater than 0", newValue.isEmpty() || newValue.equals("0")));
    }
}
