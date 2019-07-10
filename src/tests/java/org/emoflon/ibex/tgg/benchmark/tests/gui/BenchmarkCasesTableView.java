package org.emoflon.ibex.tgg.benchmark.tests.gui;

import java.util.Random;

import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.parts.TGGBenchmarkView;
import org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.parts.TGGBenchmarkViewPart;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage; 

public class BenchmarkCasesTableView extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Single Benchmark Cases Preferences");
		
		try {
			BorderPane mainLayout = new BorderPane();
			
			// buttons
			Button runBenchmarkCaseButton = new Button("Run");
			Button editBenchmarkCaseButton = new Button("Edit");
			Button addBenchmarkCaseButton = new Button("Add");
			Button deleteBenchmakCaseButton = new Button("Delete");
			Button pluginPreferencesButton = new Button("Plugin Preferences");
			HBox buttonPane = new HBox(runBenchmarkCaseButton, editBenchmarkCaseButton, addBenchmarkCaseButton, deleteBenchmakCaseButton, pluginPreferencesButton);
			buttonPane.setSpacing(8.0);
			buttonPane.setPadding(new Insets(10.0));
			mainLayout.setTop(buttonPane);
			
			// initialize table view
			TGGBenchmarkViewPart tggBenchmarkViewPart = new TGGBenchmarkViewPart();
			tggBenchmarkViewPart.initData(generateData(10));
			Parent tableView = tggBenchmarkViewPart.getContent();
			mainLayout.setCenter(tableView);
			
			// display the scene
			Scene mainScene = new Scene(mainLayout);
			primaryStage.setScene(mainScene);
			primaryStage.setMinWidth(600.0);
			primaryStage.setMinHeight(200.0);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * TODO: remove function
	 * 
	 * @param count
	 * @return
	 */
	private ObservableList<BenchmarkCasePreferences> generateData(int count) {
		ObservableList<BenchmarkCasePreferences> data = FXCollections.observableArrayList();

		for (int i = 0; i < count; i++) {
			BenchmarkCasePreferences bcp = new BenchmarkCasePreferences();
			bcp.setBenchmarkCaseName("Benchmark Case " + i);
			bcp.setMarkedForExecution(new Random().nextBoolean());
			data.add(bcp);
		}

		return data;
	}
}
