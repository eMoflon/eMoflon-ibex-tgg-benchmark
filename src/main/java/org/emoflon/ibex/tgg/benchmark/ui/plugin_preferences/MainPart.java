package org.emoflon.ibex.tgg.benchmark.ui.plugin_preferences;

import java.io.IOException;
import java.util.Arrays;

import org.controlsfx.glyphfont.FontAwesome.Glyph;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.model.EclipseProject;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryDataModel;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.GenericPreferencesPart;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
        categoriesViewData = FXCollections.observableArrayList();
        categoriesViewData.add(new CategoryDataModel("General", Glyph.CIRCLE, categoryGeneralController.getContent()));
        categoriesViewData
                .add(new CategoryDataModel("Benchmark", Glyph.TACHOMETER, categoryBenchmarkController.getContent()));
        categoriesViewData.add(new CategoryDataModel("Report", Glyph.FILE, categoryReportController.getContent()));
        categoriesViewData
                .add(new CategoryDataModel("Defaults", Glyph.SITEMAP, categoryDefaultsController.getContent()));

        // init and add buttons
        saveButton = new Button("Save");
        saveButton.setOnAction((event) -> {
            if (savePreferences()) {
                closeWindow();
            }
        });

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction((event) -> {
            closeWindow();
        });

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
        if (true) {
            // TODO: save the changes

            // try to save

            // close on success

            // error message on fail
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
