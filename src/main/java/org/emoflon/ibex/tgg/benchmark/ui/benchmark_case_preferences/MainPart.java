package org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences;

import java.io.IOException;
import java.util.Arrays;

import org.controlsfx.glyphfont.FontAwesome.Glyph;
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
 */
public class MainPart extends GenericPreferencesPart {

    private final Button cancelButton;
    private final Button runButton;
    private final Button saveAndCloseButton;

    private final CategoryBenchmarkPart categoryBenchmarkController;
    private final CategoryOperationalizationsPart categoryOperationalizationsController;

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

        // get sub parts
        categoryBenchmarkController = new CategoryBenchmarkPart();
        categoryOperationalizationsController = new CategoryOperationalizationsPart();

        // init categories
        categoriesViewData = FXCollections.observableArrayList();
        categoriesViewData
                .add(new CategoryDataModel("Benchmark", Glyph.TACHOMETER, categoryBenchmarkController.getContent()));
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
     * Initializes the parts elements by binding them to a data model. This needs to
     * be done after an instance of this class has been created, because only then
     * will the @FXML elements be populated from the FXML resource.
     * 
     * @param preferencesData The data model
     */
    public void initData(BenchmarkCasePreferences bcp) {
        this.preferencesData = bcp;
        if (preferencesData != null) {
            this.preferencesDataWorkingCopy = new BenchmarkCasePreferences(preferencesData);
        } else {
            this.preferencesDataWorkingCopy = new BenchmarkCasePreferences();
        }

        initCategoriesView(categoriesViewData);

        categoryBenchmarkController.initData(this.preferencesDataWorkingCopy);
        categoryOperationalizationsController.initData(this.preferencesDataWorkingCopy);
    }

    /**
     * Run the current open benchmark case(s).
     */
    private void runBenchmarkCase() {
        // pluginCore.scheduleJobs(Arrays.asList(eclipseProject));
    }

    /**
     * Save the preferences.
     *
     * @return true if saving was successfull
     */
    private boolean savePreferences() {
        if (preferencesDataWorkingCopy != null) {
            // write the changes back
            // eclipseProject.setBenchmarkCasePreferences(preferencesDataWorkingCopy);

            // eclipseProject.savePreferences();

            // TODO: remove
            // try {
            // eclipseProject.savePreferences();
            // } catch (IOException e) {
            // Alert alert = new Alert(AlertType.ERROR);
            // alert.setTitle("Error saving benchmark case preferences");
            // alert.setHeaderText("Could not save benchmark case preferences");
            // alert.setContentText(e.getMessage());
            // alert.showAndWait();
            //
            // return false;
            // }
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
