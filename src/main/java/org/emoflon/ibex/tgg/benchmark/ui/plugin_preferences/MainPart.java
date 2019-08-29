package org.emoflon.ibex.tgg.benchmark.ui.plugin_preferences;

import java.io.IOException;
import java.util.Arrays;

import org.controlsfx.glyphfont.FontAwesome.Glyph;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryDataModel;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.GenericPreferencesPart;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * MainPart is the main GUI part of the {@link PluginPreferencesWindow}.
 *
 * @author Andre Lehmann
 */
public class MainPart extends GenericPreferencesPart {

    private final Button cancelButton;
    private final Button saveButton;

    private final CategoryGeneralPart categoryGeneralController;
    private final CategoryBenchmarkPart categoryBenchmarkController;
    private final CategoryReportPart categoryReportController;
    private final CategoryDefaultsPart categoryDefaultsController;

    private ObservableList<CategoryDataModel> categoriesViewData;

    private PluginPreferences preferencesData;
    private PluginPreferences preferencesDataWorkingCopy;

    /**
     * Constructor for {@link MainPart}.
     * 
     * @throws IOException if the FXML resources could not be found
     */
    public MainPart() throws IOException {
        super();

        // get sub parts
        categoryGeneralController = new CategoryGeneralPart();
        categoryBenchmarkController = new CategoryBenchmarkPart();
        categoryReportController = new CategoryReportPart();
        categoryDefaultsController = new CategoryDefaultsPart();

        // init categories
        categoriesViewData = FXCollections.observableArrayList(
                new CategoryDataModel("General", Glyph.CIRCLE, categoryGeneralController.getContent()),
                new CategoryDataModel("Benchmark", Glyph.TACHOMETER, categoryBenchmarkController.getContent()),
                new CategoryDataModel("Report", Glyph.FILE, categoryReportController.getContent()),
                new CategoryDataModel("Defaults", Glyph.SITEMAP, categoryDefaultsController.getContent()));

        // init and add buttons
        saveButton = new Button("Save and Close");
        saveButton.setOnAction((event) -> {
            if (savePreferences()) {
                closeWindow();
            }
        });

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction((event) -> {
            closeWindow();
        });

        ChangeListener<Boolean> validationChange = (obs, wasInvalid, isNowInvalid) -> {
            if (isNowInvalid) {
                saveButton.setDisable(true);
            } else {
                saveButton.setDisable(false);
            }
        };

        categoryGeneralController.getValidation().invalidProperty().addListener(validationChange);
        categoryBenchmarkController.getValidation().invalidProperty().addListener(validationChange);
        categoryReportController.getValidation().invalidProperty().addListener(validationChange);
        categoryDefaultsController.getValidation().invalidProperty().addListener(validationChange);

        populateButtonPane(Arrays.asList(cancelButton), Arrays.asList(saveButton));
        initData();
    }

    /**
     * Initializes the parts elements by binding them to a data model. This needs to
     * be done after an instance of this class has been created, because only then
     * will the @FXML elements be populated from the FXML resource.
     * 
     * @param preferencesData The data model
     */
    public void initData() {
        this.preferencesData = Core.getInstance().getPluginPreferences();
        this.preferencesDataWorkingCopy = new PluginPreferences(preferencesData);

        initCategoriesView(categoriesViewData);

        categoryGeneralController.initData(this.preferencesDataWorkingCopy);
        categoryBenchmarkController.initData(this.preferencesDataWorkingCopy);
        categoryReportController.initData(this.preferencesDataWorkingCopy);
        categoryDefaultsController.initData(this.preferencesDataWorkingCopy);
    }

    /**
     * Save the preferences.
     *
     * @return true if saving was successfull
     */
    private boolean savePreferences() {
        this.preferencesData.copyValues(preferencesDataWorkingCopy);
        try {
            preferencesData.saveToFile(Core.getInstance().getWorkspace().getPluginPreferencesFilePath());
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);

            alert.setTitle("Save Plugin Preferences");
            alert.setHeaderText("Failed to save the plugin preferences.");
            alert.setContentText(e.getMessage());

            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Close the window containing this part.
     */
    private void closeWindow() {
        Window window = content.getScene().getWindow();
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
