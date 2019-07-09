package org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryPart;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.NumberStringConverter;

public class CategoryInputPart extends CategoryPart<BenchmarkCasePreferences> {

	// elements from the FXML resource
	@FXML
	private ChoiceBox inputSourceMetaModel;
	@FXML
	private ChoiceBox inputTargetMetaModel;
	@FXML
	private ChoiceBox inputSourcePackage;
	@FXML
	private ChoiceBox inputTargetPackage;

	public CategoryInputPart() throws IOException {
		super("../../resources/fxml/benchmark_case_preferences/CategoryInput.fxml");
	}

	@Override
	public void initData(BenchmarkCasePreferences prefsData) {
		super.initData(prefsData);
	}
}
