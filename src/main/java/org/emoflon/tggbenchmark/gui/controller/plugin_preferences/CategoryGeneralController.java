package org.emoflon.tggbenchmark.gui.controller.plugin_preferences;

import java.io.IOException;

import org.apache.logging.log4j.spi.StandardLevel;
import org.controlsfx.validation.Validator;
import org.emoflon.tggbenchmark.gui.controller.generic_preferences.CategoryController;
import org.emoflon.tggbenchmark.gui.model.PluginPreferences;
import org.emoflon.tggbenchmark.utils.UIUtils;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

public class CategoryGeneralController extends CategoryController<PluginPreferences> {

        // elements from the FXML resource
        @FXML
        private TextField benchmarkPreferencesFileName;
        @FXML
        private ChoiceBox<StandardLevel> logLevel;

        private Tooltip benchmarkPreferencesFileNameTooltip;
        private Tooltip logLevelTooltip;

        public CategoryGeneralController() throws IOException {
                super("../../../resources/view/plugin_preferences/CategoryGeneral.fxml");
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
                validation.registerValidator(benchmarkPreferencesFileName, Validator
                                .createEmptyValidator("A name for the benchmark preferences file must be specified"));

                UIUtils.bindEnumChoiceBox(logLevel, FXCollections.observableArrayList(StandardLevel.values()),
                                preferencesData.logLevelProperty());
                logLevel.setTooltip(logLevelTooltip);
        }
}
