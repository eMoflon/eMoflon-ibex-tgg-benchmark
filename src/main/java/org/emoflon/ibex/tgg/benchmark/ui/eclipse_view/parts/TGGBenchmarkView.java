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

	public void loadPreferences() {
		if (Files.exists(preferencesFilePath)) {
			JsonObject prefsJsonObject;
			try (InputStream in = Files.newInputStream(preferencesFilePath)) {
				//prefFileContent = new String(Files.readAllBytes(preferencesFilePath));
				try (JsonReader reader = Json.createReader(in)) {
					prefsJsonObject = reader.readObject();
				} catch (JsonException e) {
					// TODO: handle exception
					return;
				} catch (Exception e) {
					// TODO: handle exception
					return;
				}
			} catch (IOException e) {
				// TODO: handle exception
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
			// TODO: create default settings and save
		}
		
	}
	
	public void savePreferences() {
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
			// TODO: handle exception
			return;
		} catch (Exception e) {
			// TODO: handle exception
			return;
		}
        
        Path tmpFile = Paths.get(preferencesFilePath + ".tmp");
        try (OutputStream out = Files.newOutputStream(tmpFile, StandardOpenOption.CREATE_NEW)) {
        	// write tmp file
        	JsonWriter prefsJsonWriter = Json.createWriter(out);
            prefsJsonWriter.writeObject(prefsJsonObject);
            prefsJsonWriter.close();
        	
        	// replace old preferences file
            Files.move(tmpFile, preferencesFilePath, StandardCopyOption.REPLACE_EXISTING);
            
        } catch (IOException e) {
        	// TODO: handle exception
        }
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
