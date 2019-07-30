package org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.parts;

import org.eclipse.swt.widgets.Composite;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;

import org.eclipse.swt.SWT;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;

public class TGGBenchmarkView {
    private final ObservableList<BenchmarkCasePreferences> benchmarkCasePreferencesList = generateData(5);
    
    private FXCanvas fxCanvas;
    
    private String version = "0.0.1";
    
    private Path preferencesFilePath = Paths.get("TGGBenchmark.json");
    
    private TGGBenchmarkViewPart tggBenchmarkViewPart;
    
    public TGGBenchmarkView() {
        System.out.println("TGG Constructor");
        
    }

    @PostConstruct
    public void createPartControl(Composite parent) {
        System.out.println("TGG createPartControl");
        
        // create canvas and initialize FX toolkit
        fxCanvas = new FXCanvas(parent, SWT.NONE);

        // create GUI part
        tggBenchmarkViewPart = new TGGBenchmarkViewPart();
        tggBenchmarkViewPart.initData(benchmarkCasePreferencesList);
    
        // set scene
        Scene view_scene = new Scene(tggBenchmarkViewPart.getContent());
        fxCanvas.setScene(view_scene);
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
