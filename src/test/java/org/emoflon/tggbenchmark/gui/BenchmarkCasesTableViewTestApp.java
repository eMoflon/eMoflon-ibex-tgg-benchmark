package org.emoflon.tggbenchmark.gui;

import java.io.IOException;

import javax.json.JsonException;

import org.emoflon.tggbenchmark.Core;
import org.emoflon.tggbenchmark.gui.PluginPreferencesWindow;
import org.emoflon.tggbenchmark.gui.controller.benchmark_cases_table.BenchmarkCasesTableController;
import org.emoflon.tggbenchmark.gui.handler.AddBenchmarkCaseHandler;
import org.emoflon.tggbenchmark.gui.handler.DeleteBenchmarkCaseHandler;
import org.emoflon.tggbenchmark.gui.handler.EditBenchmarkCaseHandler;
import org.emoflon.tggbenchmark.gui.handler.RunBenchmarkCaseHandler;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class BenchmarkCasesTableViewTestApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();

        // initialize table view
        BenchmarkCasesTableController tggBenchmarkViewPart = new BenchmarkCasesTableController();
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

    /**
     * Run the application. Scince JavaFX 11 it is required, that the main method
     * and the "launch" call are decoupled, otherwise the call will fail. See
     * https://github.com/javafxports/openjdk-jfx/issues/236#issuecomment-426583174
     * 
     * @throws JsonException
     * @throws IOException
     */
    public static void run() throws JsonException, IOException {
        GUIUtils.initConfiguration();

        launch();
    }
}
