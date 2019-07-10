package org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryPart;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class CategoryInputPart extends CategoryPart<BenchmarkCasePreferences> {

	// elements from the FXML resource
	@FXML
	private ChoiceBox<String> inputSourceMetaModel;
	@FXML
	private ChoiceBox<String> inputTargetMetaModel;
	@FXML
	private ChoiceBox<String> inputSourcePackage;
	@FXML
	private ChoiceBox<String> inputTargetPackage;

	public CategoryInputPart() throws IOException {
		super("../../resources/fxml/benchmark_case_preferences/CategoryInput.fxml");
	}

	@Override
	public void initData(BenchmarkCasePreferences prefsData) {
		super.initData(prefsData);
	}
}
