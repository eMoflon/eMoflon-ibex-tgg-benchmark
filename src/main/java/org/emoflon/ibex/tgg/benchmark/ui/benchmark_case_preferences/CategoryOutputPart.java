package org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryPart;

public class CategoryOutputPart extends CategoryPart<BenchmarkCasePreferences> {

	// elements from the FXML resource

	public CategoryOutputPart() throws IOException {
		super("../../resources/fxml/benchmark_case_preferences/CategoryOutput.fxml");
	}

	@Override
	public void initData(BenchmarkCasePreferences prefsData) {
		super.initData(prefsData);
	}
}
