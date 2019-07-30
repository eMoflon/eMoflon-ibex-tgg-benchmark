package org.emoflon.ibex.tgg.benchmark;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences.BenchmarkCasePreferencesWindow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Core
 */
public class Core {

    private final String version = "0.1.0";
    
    private static Core instance;
    private final ObservableList<BenchmarkCasePreferences> benchmarkCasePreferencesList;
    private Path preferencesFilePath = Paths.get("preferences.json").toAbsolutePath();

    private Core() {
        super();
        
        System.out.println("Core constructor");
        
        benchmarkCasePreferencesList = FXCollections.observableArrayList();

        loadPreferences();
//        benchmarkCasePreferencesList = generateData(10);

        savePreferences();
    }

    public void loadPreferences() {
        if (Files.exists(preferencesFilePath)) {
            JsonObject prefsJsonObject;
            try (InputStream in = Files.newInputStream(preferencesFilePath)) {
                //prefFileContent = new String(Files.readAllBytes(preferencesFilePath));
                try (JsonReader reader = Json.createReader(in)) {
                    prefsJsonObject = reader.readObject();
                } catch (JsonException e) {
                    // TODO: handle exception
                    System.err.println(e);
                    return;
                } catch (Exception e) {
                    // TODO: handle exception
                    System.err.println(e);
                    return;
                }
            } catch (IOException e) {
                // TODO: handle exception
                System.err.println(e);
                return;
            }
            
            String fileVersion = prefsJsonObject.getString("version", version);
            JsonObject input = prefsJsonObject.getJsonObject("input");
            JsonObject output = prefsJsonObject.getJsonObject("output");

            JsonArray benchmarkCases = prefsJsonObject.getJsonArray("benchmarkCases");
            if (benchmarkCases != null) {
                for (JsonValue benchmarkCase : benchmarkCases) {
                    if (benchmarkCase instanceof JsonObject) {
                        BenchmarkCasePreferences bcp = new BenchmarkCasePreferences();
                        bcp.loadFromJSON((JsonObject)benchmarkCase);
                        benchmarkCasePreferencesList.add(bcp);
                    } else {
                        System.err.println("Invalid benchmark case configuration");
                    }
                }
            }
            
        } else {
            // save newly created config file
            savePreferences();
        }
        
    }
    
    public boolean savePreferences() {
        JsonArrayBuilder benchmarkCasesArrayBuilder = Json.createArrayBuilder();
        for (BenchmarkCasePreferences benchmarkCase : benchmarkCasePreferencesList) {
            benchmarkCasesArrayBuilder.add(benchmarkCase.getAsJSON());
        }
        
        JsonObject prefsJsonObject = Json.createObjectBuilder()
            .add("version", version)
            .add("benchmarkCases", benchmarkCasesArrayBuilder.build()).build();
                
        try {
            Files.createDirectories(preferencesFilePath.getParent());
        } catch (FileAlreadyExistsException e) {
            // ignore
        } catch (Exception e) {
            showSaveErrorDialog(String.format("The directory '%s' could not be created: %s", preferencesFilePath.getParent(), e.getMessage()));
            System.err.println(e);
            return false;
        }
        
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        Path tmpFile = Paths.get(preferencesFilePath + ".tmp");
        try (OutputStream out = Files.newOutputStream(tmpFile, StandardOpenOption.CREATE_NEW)) {
            // write tmp file
            JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
            JsonWriter jsonWriter = writerFactory.createWriter(out);
            jsonWriter.writeObject(prefsJsonObject);
            jsonWriter.close();
            
            // replace old preferences file
            Files.move(tmpFile, preferencesFilePath, StandardCopyOption.REPLACE_EXISTING);
            
        } catch (IOException e) {
            showSaveErrorDialog(e.getMessage());
            System.err.println(e);
        }
        
        // save successful
        return true;
    }
    
    public void addBenchmarkCase() {
        // try {
        //     BenchmarkCasePreferencesWindow bcpw = new BenchmarkCasePreferencesWindow(bcp);
        //     bcpw.show();
        // } catch (IOException e) {
        //     System.err.println("Error creating window: " + e.getMessage());
        // }
    }

    public void editBenchmarkCase(BenchmarkCasePreferences bcp) {
        try {
            BenchmarkCasePreferencesWindow bcpw = new BenchmarkCasePreferencesWindow(bcp);
            bcpw.show();
        } catch (IOException e) {
            System.err.println("Error creating window: " + e.getMessage());
        }
    }

    public void deleteBenchmarkCase(BenchmarkCasePreferences bcp) {
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Benchmark Case");
        confirmation.setHeaderText("Do you really want to delete this Benchmark Case?");
        confirmation.setContentText(bcp.getBenchmarkCaseName());

        Optional<ButtonType> option = confirmation.showAndWait();
        if (option.get() == ButtonType.OK) {
            benchmarkCasePreferencesList.remove(bcp);
            savePreferences();
            System.out.println("Deleting Benchmark Case: " + bcp.getBenchmarkCaseName());
        }
    }
    
    private void showSaveErrorDialog(String text) {
        showErrorDialog("Error saving preferences of TGG Benchmark module",
                "Could not save preferences of TGG Benchmark", 
                text);
    }
    
    private void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
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
    
    /**
     * @return the benchmarkCasePreferencesList
     */
    public ObservableList<BenchmarkCasePreferences> getBenchmarkCasePreferencesList() {
        return benchmarkCasePreferencesList;
    }

    /**
     * @return the instance
     */
    public static Core getInstance() {
        if (Core.instance == null) {
            Core.instance = new Core ();
        }
        return Core.instance;
    }

    /**
     * Schedules benchmark jobs to be exucuted.
     * 
     * @param bcpl the benchmark cases that shall be executed
     */
    public void scheduleJobs(List<BenchmarkCasePreferences> bcpl) {

    }
}