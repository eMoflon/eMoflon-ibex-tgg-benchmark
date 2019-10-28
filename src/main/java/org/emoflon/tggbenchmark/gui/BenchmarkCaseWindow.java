package org.emoflon.tggbenchmark.gui;

import java.io.IOException;
import java.util.Arrays;

import org.controlsfx.glyphfont.FontAwesome.Glyph;
import org.emoflon.tggbenchmark.Core;
import org.emoflon.tggbenchmark.gui.controller.benchmark_case_preferences.CategoryBenchmarkController;
import org.emoflon.tggbenchmark.gui.controller.benchmark_case_preferences.CategoryOperationalizationsController;
import org.emoflon.tggbenchmark.gui.controller.generic_preferences.GenericPreferencesController;
import org.emoflon.tggbenchmark.gui.model.BenchmarkCase;
import org.emoflon.tggbenchmark.gui.model.Category;

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

public class BenchmarkCaseWindow {

    private final Stage stage;
    private GenericPreferencesController mainPart;

    private Button cancelButton;
    private Button runButton;
    private Button saveAndCloseButton;

    private CategoryBenchmarkController categoryBenchmarkController;
    private CategoryOperationalizationsController categoryOperationalizationsController;

    private ObservableList<Category> categoriesViewData;

    private BenchmarkCase benchmarkCase;
    private BenchmarkCase benchmarkCaseWorkingCopy;

    public BenchmarkCaseWindow(BenchmarkCase bc) throws IOException {
        // create content
        createContent();
        initData(bc);

        // prepare stage
        stage = new Stage();
        stage.setTitle("Edit Benchmark Case");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNIFIED);
        stage.setScene(new Scene(mainPart.getContent()));
    }

    private void createContent() throws IOException {
        // get sub parts
        categoryBenchmarkController = new CategoryBenchmarkController();
        categoryOperationalizationsController = new CategoryOperationalizationsController();

        // init categories
        categoriesViewData = FXCollections.observableArrayList(
                new Category("Benchmark", Glyph.TACHOMETER, categoryBenchmarkController.getContent()),
                new Category("Operationalizations", Glyph.GEARS, categoryOperationalizationsController.getContent()));

        // init and add buttons
        runButton = new Button("Run");
        runButton.setOnAction((event) -> {
            if (savePreferences()) {
                runBenchmarkCase();
                close();
            }
        });

        saveAndCloseButton = new Button("Save and Close");
        saveAndCloseButton.setOnAction((event) -> {
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
                saveAndCloseButton.setDisable(true);
                runButton.setDisable(true);
            } else {
                saveAndCloseButton.setDisable(false);
                runButton.setDisable(false);
            }
        };

        categoryBenchmarkController.getValidation().invalidProperty().addListener(validationChange);
        categoryOperationalizationsController.getValidation().invalidProperty().addListener(validationChange);

        // main part
        mainPart = new GenericPreferencesController();
        mainPart.populateButtonPane(Arrays.asList(cancelButton), Arrays.asList(saveAndCloseButton, runButton));
    }

    /**
     * Initializes the parts elements by binding them to a data model. This needs to
     * be done after an instance of this class has been created, because only then
     * will the @FXML elements be populated from the FXML resource.
     * 
     * @param benchmarkCase The data model
     */
    public void initData(BenchmarkCase bc) {
        this.benchmarkCase = bc;
        if (benchmarkCase != null) {
            this.benchmarkCaseWorkingCopy = new BenchmarkCase(benchmarkCase);
        } else {
            this.benchmarkCaseWorkingCopy = new BenchmarkCase();
        }

        mainPart.initCategoriesView(categoriesViewData);

        categoryBenchmarkController.initData(this.benchmarkCaseWorkingCopy);
        categoryOperationalizationsController.initData(this.benchmarkCaseWorkingCopy);
    }

    /**
     * Run the current open benchmark case(s).
     */
    private void runBenchmarkCase() {
        Core.getInstance().runBenchmark(Arrays.asList(benchmarkCaseWorkingCopy));
    }

    /**
     * Save the preferences.
     *
     * @return true if saving was successfull
     */
    private boolean savePreferences() {
        if (benchmarkCase != null) {
            benchmarkCase.getEclipseProject().getBenchmarkCase().remove(benchmarkCase);
            if (benchmarkCase.getEclipseProject() != benchmarkCaseWorkingCopy.getEclipseProject()) {
                benchmarkCase.getEclipseProject().delayedSavePreferences();
            }
        }
        benchmarkCaseWorkingCopy.getEclipseProject().getBenchmarkCase().add(benchmarkCaseWorkingCopy);

        try {
            benchmarkCaseWorkingCopy.getEclipseProject().savePreferences();
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);

            alert.setTitle("Save Benchmark Case Preferences");
            alert.setHeaderText("Failed to save the benchmark case preferences.");
            alert.setContentText(e.getMessage());

            alert.showAndWait();
            return false;
        }

        return true;
    }

    public void show() {
        stage.setMinWidth(700.0);
        stage.setMinHeight(300.0);
        stage.setWidth(1070.0);
        stage.setHeight(600.0);
        stage.show();
    }

    /**
     * Close the window containing this part.
     */
    private void close() {
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
