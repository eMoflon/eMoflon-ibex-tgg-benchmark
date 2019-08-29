package org.emoflon.ibex.tgg.benchmark.ui.plugin_preferences;

import java.io.IOException;

import org.apache.logging.log4j.spi.StandardLevel;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;
import org.emoflon.ibex.tgg.benchmark.ui.UIUtils;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryPart;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

public class CategoryGeneralPart extends CategoryPart<PluginPreferences> {

    // elements from the FXML resource
    @FXML
    private TextField benchmarkPreferencesFileName;
    @FXML
    private ChoiceBox<StandardLevel> logLevel;

    private Tooltip benchmarkPreferencesFileNameTooltip;
    private Tooltip logLevelTooltip;

    public CategoryGeneralPart() throws IOException {
        super("../../resources/fxml/plugin_preferences/CategoryGeneral.fxml");
    }

    @Override
    public void initData(PluginPreferences preferencesData) {
        super.initData(preferencesData);

        // tooltips
        benchmarkPreferencesFileNameTooltip = new Tooltip(
                "TGG project relative file path for benchmark preferences file");
        logLevelTooltip = new Tooltip("Log level used in this plugin");

        // bindings
        benchmarkPreferencesFileName.textProperty()
                .bindBidirectional(preferencesData.benchmarkPreferencesFileNameProperty());
        benchmarkPreferencesFileName.setTooltip(benchmarkPreferencesFileNameTooltip);

        UIUtils.bindEnumChoiceBox(logLevel, FXCollections.observableArrayList(StandardLevel.values()),
                preferencesData.logLevelProperty());
        logLevel.setTooltip(logLevelTooltip);
    }
}
