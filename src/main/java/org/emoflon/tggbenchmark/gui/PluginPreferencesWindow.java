package org.emoflon.tggbenchmark.gui;

import java.io.IOException;
import java.util.Arrays;

import org.apache.logging.log4j.Level;
import org.controlsfx.glyphfont.FontAwesome.Glyph;
import org.emoflon.tggbenchmark.Core;
import org.emoflon.tggbenchmark.gui.controller.generic_preferences.GenericPreferencesController;
import org.emoflon.tggbenchmark.gui.controller.plugin_preferences.CategoryBenchmarkController;
import org.emoflon.tggbenchmark.gui.controller.plugin_preferences.CategoryDefaultsController;
import org.emoflon.tggbenchmark.gui.controller.plugin_preferences.CategoryGeneralController;
import org.emoflon.tggbenchmark.gui.controller.plugin_preferences.CategoryReportController;
import org.emoflon.tggbenchmark.gui.model.Category;
import org.emoflon.tggbenchmark.gui.model.PluginPreferences;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class PluginPreferencesWindow {

    private final Stage stage;
    private GenericPreferencesController mainPart;

    private Button cancelButton;
    private Button saveButton;

    private CategoryGeneralController categoryGeneralController;
    private CategoryBenchmarkController categoryBenchmarkController;
    private CategoryReportController categoryReportController;
    private CategoryDefaultsController categoryDefaultsController;

    private ObservableList<Category> categoriesViewData;

    private PluginPreferences pluginPreferences;
    private PluginPreferences pluginPreferencesWorkingCopy;

    public PluginPreferencesWindow() throws IOException {
        // create window content
        createContent();
        initData();

        // prepare stage
        stage = new Stage();
        stage.setTitle("Edit TGG Benchmark Plugin Preferences");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNIFIED);
        stage.setScene(new Scene(mainPart.getContent()));
    }

    private void createContent() throws IOException {
        // get sub parts
        categoryGeneralController = new CategoryGeneralController();
        categoryBenchmarkController = new CategoryBenchmarkController();
        categoryReportController = new CategoryReportController();
        categoryDefaultsController = new CategoryDefaultsController();

        // init categories
        categoriesViewData = FXCollections.observableArrayList(
                new Category("General", Glyph.CIRCLE, categoryGeneralController.getContent()),
                new Category("Benchmark", Glyph.TACHOMETER, categoryBenchmarkController.getContent()),
                new Category("Report", Glyph.FILE, categoryReportController.getContent()),
                new Category("Defaults", Glyph.SITEMAP, categoryDefaultsController.getContent()));

        // init and add buttons
        saveButton = new Button("Save and Close");
        saveButton.setOnAction((event) -> {
            if (savePreferences()) {
                close();
            }
        });

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction((event) -> {
            close();
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

        // main part
        mainPart = new GenericPreferencesController();
        mainPart.populateButtonPane(Arrays.asList(cancelButton), Arrays.asList(saveButton));
    }

    /**
     * Initializes the parts elements by binding them to a data model. This needs to
     * be done after an instance of this class has been created, because only then
     * will the @FXML elements be populated from the FXML resource.
     * 
     * @param pluginPreferences The data model
     */
    public void initData() {
        this.pluginPreferences = Core.getInstance().getPluginPreferences();
        this.pluginPreferencesWorkingCopy = new PluginPreferences(pluginPreferences);

        mainPart.initCategoriesView(categoriesViewData);

        categoryGeneralController.initData(this.pluginPreferencesWorkingCopy);
        categoryBenchmarkController.initData(this.pluginPreferencesWorkingCopy);
        categoryReportController.initData(this.pluginPreferencesWorkingCopy);
        categoryDefaultsController.initData(this.pluginPreferencesWorkingCopy);
    }

    /**
     * Save the preferences.
     *
     * @return true if saving was successfull
     */
    private boolean savePreferences() {
        this.pluginPreferences.copyValues(pluginPreferencesWorkingCopy);
        Core.setLogLevel(Level.getLevel(pluginPreferences.getLogLevel().toString()));
        try {
            pluginPreferences.saveToFile(Core.getInstance().getWorkspace().getPluginPreferencesFilePath());
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
     * Show the window.
     */
    public void show() {
        stage.setMinWidth(700.0);
        stage.setMinHeight(300.0);
        stage.setWidth(1070.0);
        stage.setHeight(600.0);
        stage.show();
    }

    /**
     * Close the window.
     */
    private void close() {
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

}
