package org.emoflon.ibex.tgg.benchmark.ui;

import java.io.IOException;

import javax.json.JsonException;

import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.handlers.AddBenchmarkCaseHandler;
import org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.handlers.DeleteBenchmarkCaseHandler;
import org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.handlers.EditBenchmarkCaseHandler;
import org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.handlers.RunBenchmarkCaseHandler;
import org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.parts.TGGBenchmarkViewPart;
import org.emoflon.ibex.tgg.benchmark.ui.plugin_preferences.PluginPreferencesWindow;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class BenchmarkCasesTableViewTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();

        // initialize table view
        TGGBenchmarkViewPart tggBenchmarkViewPart = new TGGBenchmarkViewPart();
        mainLayout.setCenter(tggBenchmarkViewPart.getContent());

        // buttons
        Button runBenchmarkCaseButton = new Button("Run");
        runBenchmarkCaseButton.setOnAction(e -> {
            new RunBenchmarkCaseHandler().execute(Core.getInstance().getBenchmarkCases());
        });

        Button editBenchmarkCaseButton = new Button("Edit");
        editBenchmarkCaseButton.setOnAction(e -> {
            new EditBenchmarkCaseHandler().execute(tggBenchmarkViewPart.getTable());
        });

        Button addBenchmarkCaseButton = new Button("Add");
        addBenchmarkCaseButton.setOnAction(e -> {
            new AddBenchmarkCaseHandler().execute();
        });

        Button deleteBenchmakCaseButton = new Button("Delete");
        deleteBenchmakCaseButton.setOnAction(e -> {
            new DeleteBenchmarkCaseHandler().execute(tggBenchmarkViewPart.getTable());
        });

        Button pluginPreferencesButton = new Button("Plugin Preferences");
        pluginPreferencesButton.setOnAction(e -> {
            try {
                PluginPreferencesWindow ppw = new PluginPreferencesWindow();
                ppw.show();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        HBox buttonPane = new HBox(runBenchmarkCaseButton, editBenchmarkCaseButton, addBenchmarkCaseButton,
                deleteBenchmakCaseButton, pluginPreferencesButton);
        buttonPane.setSpacing(8.0);
        buttonPane.setPadding(new Insets(10.0));
        mainLayout.setTop(buttonPane);

        // display the scene
        Scene mainScene = new Scene(mainLayout);
        primaryStage.setScene(mainScene);
        primaryStage.setMinWidth(600.0);
        primaryStage.setMinHeight(200.0);

        try {
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws JsonException, IOException {
        Utils.initConfiguration();

        launch(args);
    }
}
