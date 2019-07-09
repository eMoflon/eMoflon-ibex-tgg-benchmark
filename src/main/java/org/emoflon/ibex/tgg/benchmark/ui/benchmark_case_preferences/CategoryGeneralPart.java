package org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryPart;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.NumberStringConverter;

public class CategoryGeneralPart extends CategoryPart<BenchmarkCasePreferences> {

	@FXML
	private TextField benchmarkCaseName;
	@FXML
	private TextField defaultTimeout;
	@FXML
	private TextField numberOfRuns;

	public CategoryGeneralPart() throws IOException {
		super("../../resources/fxml/benchmark_case_preferences/CategoryGeneral.fxml");
	}

	@Override
	public void initData(BenchmarkCasePreferences preferencesData) {
		super.initData(preferencesData);

		benchmarkCaseName.textProperty().bindBidirectional(preferencesData.benchmarkCaseNameProperty());
		defaultTimeout.textProperty().bindBidirectional(preferencesData.defaultTimeoutProperty(),
				new NumberStringConverter());
		defaultTimeout.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
		numberOfRuns.textProperty().bindBidirectional(preferencesData.defaultTimeoutProperty(),
				new NumberStringConverter());
		numberOfRuns.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));

		preferencesData.benchmarkCaseNameProperty().addListener(e -> {
			System.out.println(preferencesData.getBenchmarkCaseName());
		});
	}
}
