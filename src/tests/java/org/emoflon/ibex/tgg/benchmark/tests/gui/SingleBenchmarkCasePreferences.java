package org.emoflon.ibex.tgg.benchmark.tests.gui;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences.BenchmarkCasePreferencesWindow;
import javafx.application.Application;
import javafx.stage.Stage; 


public class SingleBenchmarkCasePreferences extends Application {
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		primaryStage.setTitle("Benchmark Cases Table View");
		
		BenchmarkCasePreferences bcp = new BenchmarkCasePreferences();
		BenchmarkCasePreferencesWindow bcpw = new BenchmarkCasePreferencesWindow(bcp);
		bcpw.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
