package org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.controlsfx.glyphfont.FontAwesome.Glyph;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryPart;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryDataModel;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.GenericPreferencesPart;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class BenchmarkCasePreferencesWindow {

	private Stage stage;
	private BorderPane layout;
	private GenericPreferencesPart<BenchmarkCasePreferences> controller;
	private ObservableList<CategoryDataModel> categoriesViewData = FXCollections.observableArrayList();
	
	private Button cancelButton;
	private Button runButton;
	private Button saveAndCloseButton;
	
	private BenchmarkCasePreferences preferencesData;

	public BenchmarkCasePreferencesWindow(BenchmarkCasePreferences bcp) throws IOException {
		// prepare stage
		stage = new Stage();
		stage.titleProperty().bind(Bindings.concat("Benchmark Case Preferences: ", bcp.benchmarkCaseNameProperty()));
		stage.initModality(Modality.APPLICATION_MODAL);

		// init categories
		categoriesViewData.add(initCategory("General", Glyph.CUBES, "../../resources/fxml/benchmark_case_preferences/CategoryGeneral.fxml"));
		categoriesViewData.add(initCategory("Input", Glyph.SIGN_IN, "../../resources/fxml/benchmark_case_preferences/CategoryInput.fxml"));
		categoriesViewData.add(initCategory("Output", Glyph.SIGN_OUT, "../../resources/fxml/benchmark_case_preferences/CategoryOutput.fxml"));
		categoriesViewData.add(initCategory("Operationalizations", Glyph.GEARS, "../../resources/fxml/benchmark_case_preferences/CategoryOperationalizations.fxml"));
		
		// get layout and controller
		controller = new GenericPreferencesPart<BenchmarkCasePreferences>();
		controller.initData(preferencesData, categoriesViewData);

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
		
		controller.populateButtonPane(Arrays.asList(cancelButton), Arrays.asList(saveAndCloseButton, runButton));

		stage.setScene(new Scene(controller.getLayout()));
	}
	
	private CategoryDataModel initCategory(String displayName, Glyph displayIcon, String fxmlResourcePath) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlResourcePath));
		CategoryPart<BenchmarkCasePreferences> controller = loader.<CategoryPart<BenchmarkCasePreferences>>getController();
		return new CategoryDataModel(displayName, displayIcon, loader.load());
	}

	public void show() {
		stage.setMinWidth(600.0);
		stage.setMinHeight(200.0);
		stage.show();
	}
	
	private void runBenchmarkCase() {
		
	}

	private void savePreferences() {
		
	}
	
	private void closeWindow() {
		stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
//		Window window = root.getScene().getWindow();
//		window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
	}
}
