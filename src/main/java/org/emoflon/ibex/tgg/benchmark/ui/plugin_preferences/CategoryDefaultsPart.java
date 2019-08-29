package org.emoflon.ibex.tgg.benchmark.ui.plugin_preferences;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;
import org.emoflon.ibex.tgg.benchmark.ui.components.ModelSizesTextArea;
import org.emoflon.ibex.tgg.benchmark.ui.components.TimeTextField;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryPart;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;

public class CategoryDefaultsPart extends CategoryPart<PluginPreferences> {

    // elements from the FXML resource
    @FXML
    private TimeTextField defaultTimeout;
    @FXML
    private ModelSizesTextArea defaultModelSizes;
    @FXML
    private CheckBox defaultModelgenIncludeReport;
    @FXML
    private CheckBox defaultInitialFwdActive;
    @FXML
    private CheckBox defaultInitialBwdActive;
    @FXML
    private CheckBox defaultFwdActive;
    @FXML
    private CheckBox defaultBwdActive;
    @FXML
    private CheckBox defaultFwdOptActive;
    @FXML
    private CheckBox defaultBwdOptActive;
    @FXML
    private CheckBox defaultCcActive;
    @FXML
    private CheckBox defaultCoActive;

    private Tooltip defaultTimeoutTooltip;

    public CategoryDefaultsPart() throws IOException {
        super("../../resources/fxml/plugin_preferences/CategoryDefaults.fxml");
    }

    @Override
    public void initData(PluginPreferences preferencesData) {
        super.initData(preferencesData);

        // tooltips
        defaultTimeoutTooltip = new Tooltip("The timeout can be specified in the following manner: 30, 30s, 5m, 1h");

        // bindings
        defaultTimeout.bindIntegerProperty(preferencesData.defaultTimeoutProperty());
        defaultTimeout.setTooltip(defaultTimeoutTooltip);

        defaultModelSizes.bindListProperty(preferencesData.defaultModelSizesProperty());

        defaultModelgenIncludeReport.selectedProperty()
                .bindBidirectional(preferencesData.defaultModelgenIncludeReportProperty());
        defaultInitialFwdActive.selectedProperty().bindBidirectional(preferencesData.defaultInitialFwdActiveProperty());
        defaultInitialBwdActive.selectedProperty().bindBidirectional(preferencesData.defaultInitialBwdActiveProperty());
        defaultFwdActive.selectedProperty().bindBidirectional(preferencesData.defaultFwdActiveProperty());
        defaultBwdActive.selectedProperty().bindBidirectional(preferencesData.defaultBwdActiveProperty());
        defaultFwdOptActive.selectedProperty().bindBidirectional(preferencesData.defaultFwdOptActiveProperty());
        defaultBwdOptActive.selectedProperty().bindBidirectional(preferencesData.defaultBwdOptActiveProperty());
        defaultCcActive.selectedProperty().bindBidirectional(preferencesData.defaultCcActiveProperty());
        defaultCoActive.selectedProperty().bindBidirectional(preferencesData.defaultCcActiveProperty());
    }
}
