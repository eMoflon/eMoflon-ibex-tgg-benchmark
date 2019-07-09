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
 * @version 1.0
 * @since 2019-07-09
 */
public class MainPart extends GenericPreferencesPart<BenchmarkCasePreferences> {

	private ObservableList<CategoryDataModel> categoriesViewData;
	private Button cancelButton;
	private Button runButton;
	private Button saveAndCloseButton;

	private CategoryGeneralPart categoryGeneralController;
	private CategoryInputPart categoryInputController;
	private CategoryOutputPart categoryOutputController;
	private CategoryOperationalizationsPart categoryOperationalizationsController;

	/**
	 * Constructor for {@link MainPart}.
	 * 
	 * @throws IOException if the FXML resources could not be found
	 */
	public MainPart() throws IOException {
		super();

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
			savePreferences();
			runBenchmarkCase();
		});

		saveAndCloseButton = new Button("Save & Close");
		saveAndCloseButton.setOnAction((event) -> {
			savePreferences();
			closeWindow();
		});

		cancelButton = new Button("Cancel");
		cancelButton.setOnAction((event) -> {
			closeWindow();
		});

		populateButtonPane(Arrays.asList(cancelButton), Arrays.asList(saveAndCloseButton, runButton));
	}

	/**
	 * Initializes the parts elements by binding them to a data model.
	 * 
	 * @param preferencesData The data model
	 */
	public void initData(BenchmarkCasePreferences preferencesData) {
		super.initData(preferencesData, categoriesViewData);

		categoryGeneralController.initData(preferencesData);
		categoryInputController.initData(preferencesData);
		categoryOutputController.initData(preferencesData);
		categoryOperationalizationsController.initData(preferencesData);
	}

	private void runBenchmarkCase() {

	}

	private void savePreferences() {

	}

	/**
	 * Close the window containing this part.
	 */
	private void closeWindow() {
		Window window = content.getScene().getWindow();
		window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
	}
}
