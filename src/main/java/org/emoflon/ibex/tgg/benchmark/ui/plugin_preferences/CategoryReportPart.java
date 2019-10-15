package org.emoflon.ibex.tgg.benchmark.ui.plugin_preferences;

import java.io.IOException;

import org.controlsfx.validation.Validator;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;
import org.emoflon.ibex.tgg.benchmark.runner.report.ReportFileType;
import org.emoflon.ibex.tgg.benchmark.ui.UIUtils;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryPart;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

public class CategoryReportPart extends CategoryPart<PluginPreferences> {

    // elements from the FXML resource
    @FXML
    private TextField reportFilePath;
    @FXML
    private ChoiceBox<ReportFileType> reportFileType;
    @FXML
    private CheckBox includeErrors;

    private Tooltip reportFilePathTooltip;
    private Tooltip reportFileTypeTooltip;
    private Tooltip includeErrorsTooltip;

    public CategoryReportPart() throws IOException {
        super("../../resources/fxml/plugin_preferences/CategoryReport.fxml");
    }

    @Override
    public void initData(PluginPreferences preferencesData) {
        super.initData(preferencesData);

        // tooltips
        reportFilePathTooltip = new Tooltip(String.join("\n", "File path of the report. The path can contain",
                "variables like '{var}'. Following variables are defined:",
                "  Y, year, M, month, D, day: The current date", "  date: Current date in Y-M-D format",
                "  workspace_path: The workspace path", "  project_path: Path of TGG project",
                "  project_name: Name of the TGG project", "  operationalization: The executing operationalization"));
        reportFileTypeTooltip = new Tooltip("The file type used for the report");
        includeErrorsTooltip = new Tooltip("Include benchmark runs that failed with an error");

        // bindings
        reportFilePath.textProperty().bindBidirectional(preferencesData.reportFilePathProperty());
        reportFilePath.setTooltip(reportFilePathTooltip);
        validation.registerValidator(reportFilePath,
                Validator.createEmptyValidator("A path for the report file must be specified"));

        UIUtils.bindEnumChoiceBox(reportFileType, FXCollections.observableArrayList(ReportFileType.values()),
                preferencesData.reportFileTypeProperty());
        reportFileType.setTooltip(reportFileTypeTooltip);

        includeErrors.selectedProperty().bindBidirectional(preferencesData.includeErrorsProperty());
        includeErrors.setTooltip(includeErrorsTooltip);
    }
}
