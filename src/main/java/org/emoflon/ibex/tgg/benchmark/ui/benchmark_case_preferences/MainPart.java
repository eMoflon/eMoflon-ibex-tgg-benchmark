package org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.controlsfx.glyphfont.FontAwesome.Glyph;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryDataModel;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.GenericPreferencesPart;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * MainPart is the main GUI part of the {@link BenchmarkCasePreferencesWindow}.
 *
 * @author Andre Lehmann
 * @version 1.0
 * @since 2019-07-09
 */
public class MainPart extends GenericPreferencesPart<BenchmarkCasePreferences> {

    private final Button cancelButton;
    private final Button runButton;
    private final Button saveAndCloseButton;

    private final CategoryGeneralPart categoryGeneralController;
    private final CategoryInputPart categoryInputController;
    private final CategoryOutputPart categoryOutputController;
    private final CategoryOperationalizationsPart categoryOperationalizationsController;

    private final Core pluginCore;
    
    private ObservableList<CategoryDataModel> categoriesViewData;

    private BenchmarkCasePreferences preferencesData;
    private BenchmarkCasePreferences preferencesDataWorkingCopy;

    /**
     * Constructor for {@link MainPart}.
     * 
     * @throws IOException if the FXML resources could not be found
     */
    public MainPart() throws IOException {
        super();

        // get plugin core object
        pluginCore = Core.getInstance();

        // get sub parts
        categoryGeneralController = new CategoryGeneralPart();
        categoryInputController = new CategoryInputPart();
        categoryOutputController = new CategoryOutputPart();
        categoryOperationalizationsController = new CategoryOperationalizationsPart();

        // init categories
        categoriesViewData = FXCollections.observableArrayList();
        categoriesViewData.add(new CategoryDataModel("General", Glyph.CUBES, categoryGeneralController.getContent()));
        categoriesViewData.add(new CategoryDataModel("Input", Glyph.SIGN_IN, categoryInputController.getContent()));
        categoriesViewData.add(new CategoryDataModel("Output", Glyph.SIGN_OUT, categoryOutputController.getContent()));
        categoriesViewData.add(new CategoryDataModel("Operationalizations", Glyph.GEARS,
                categoryOperationalizationsController.getContent()));

        // init and add buttons
        runButton = new Button("Run");
        runButton.setOnAction((event) -> {
            if (savePreferences()) {
                runBenchmarkCase();
                closeWindow();
            }
        });

        saveAndCloseButton = new Button("Save & Close");
        saveAndCloseButton.setOnAction((event) -> {
            if (savePreferences()) {
                closeWindow();
            }
        });

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction((event) -> {
            closeWindow();
        });

        populateButtonPane(Arrays.asList(cancelButton), Arrays.asList(saveAndCloseButton, runButton));
    }

    /**
     * Initializes the parts elements by binding them to a data model. This
     * needs to be done after an instance of this class has been created,
     * because only then will the @FXML elements be populated from the FXML
     * resource.
     * 
     * @param preferencesData The data model
     */
    public void initData(BenchmarkCasePreferences preferencesData) {
        this.preferencesData = preferencesData; 
        preferencesDataWorkingCopy = new BenchmarkCasePreferences(preferencesData);
        super.initData(preferencesDataWorkingCopy, categoriesViewData);

        categoryGeneralController.initData(preferencesDataWorkingCopy);
        categoryInputController.initData(preferencesDataWorkingCopy);
        categoryOutputController.initData(preferencesDataWorkingCopy);
        categoryOperationalizationsController.initData(preferencesDataWorkingCopy);
    }

    private void runBenchmarkCase() {
        pluginCore.scheduleJobs(Arrays.asList(preferencesData));
    }

    private boolean savePreferences() {
        if (preferencesDataWorkingCopy != null) {
            // write the changes back
            preferencesData.setChanges(preferencesDataWorkingCopy);
            
            return pluginCore.savePreferences();
        }
        return false;
    }

    /**
     * Close the window containing this part.
     */
    private void closeWindow() {
        Window window = content.getScene().getWindow();
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
