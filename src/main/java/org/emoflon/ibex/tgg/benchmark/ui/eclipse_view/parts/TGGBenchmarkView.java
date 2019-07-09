package org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.parts;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences.BenchmarkCasePreferencesWindow;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.embed.swt.FXCanvas;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class TGGBenchmarkView {
	private final ObservableList<BenchmarkCasePreferences> benchmarkCasePreferencesList = generateData(5);
	private BenchmarkCaseTableView table; 
    private AnchorPane wrapperPane;
    
    private FXCanvas fxCanvas;
    
    private String version = "0.0.1";
    
    private Path preferencesFilePath = Paths.get("TGGBenchmark.json");
    
    public TGGBenchmarkView() {
    	System.out.println("TGG Constructor");
    	
	}

    @PostConstruct
	public void createPartControl(Composite parent) {
    	System.out.println("TGG createPartControl");
    	
    	// create canvas and initialize FX toolkit
		fxCanvas = new FXCanvas(parent, SWT.NONE);

		Scene view_scene = new Scene((Parent) getPanel());
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
	
	public Node getPanel() {
		if (wrapperPane == null) {
			table = new BenchmarkCaseTableView();
			table.setItems(benchmarkCasePreferencesList);
			
	        wrapperPane = new AnchorPane(table);
	        AnchorPane.setTopAnchor(table, 0.0);
	        AnchorPane.setBottomAnchor(table, 0.0);
	        AnchorPane.setLeftAnchor(table, 0.0);
	        AnchorPane.setRightAnchor(table, 0.0);
		}
        return wrapperPane;
    }
	

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

	public class BenchmarkCaseTableView extends TableView<BenchmarkCasePreferences> {

		private final TableColumn<BenchmarkCasePreferences, Boolean> executionColumn = createCheckboxColumn("Exc", "Mark benchmark case for execution", BenchmarkCasePreferences::markedForExecutionProperty);
		private final TableColumn<BenchmarkCasePreferences, String> nameColumn = createTextColumn("Benchmark Case Name", BenchmarkCasePreferences::benchmarkCaseNameProperty);
		private final TableColumn<BenchmarkCasePreferences, Boolean> modelgenActiveColumn = createCheckboxColumn("MG", "Enable the operationalization MODELGEN", BenchmarkCasePreferences::modelgenCreateReportProperty);
		private final TableColumn<BenchmarkCasePreferences, Boolean> initialFwdActiveColumn = createCheckboxColumn("IF", "Enable the operationalization INITIAL_FWD", BenchmarkCasePreferences::initialFwdActiveProperty);
		private final TableColumn<BenchmarkCasePreferences, Boolean> initialBwdActiveColumn = createCheckboxColumn("IB", "Enable the operationalization INITIAL_BWD", BenchmarkCasePreferences::initialBwdActiveProperty);
		private final TableColumn<BenchmarkCasePreferences, Boolean> fwdOptActiveColumn = createCheckboxColumn("FO", "Enable the operationalization FWD_OPT", BenchmarkCasePreferences::fwdOptActiveProperty);
		private final TableColumn<BenchmarkCasePreferences, Boolean> bwdOptActiveColumn = createCheckboxColumn("BO", "Enable the operationalization BWD_OPT", BenchmarkCasePreferences::bwdOptActiveProperty);
		private final TableColumn<BenchmarkCasePreferences, Boolean> syncActiveColumn = createCheckboxColumn("SY", "Enable the operationalization SYNC", BenchmarkCasePreferences::syncActiveProperty);
		private final TableColumn<BenchmarkCasePreferences, Boolean> ccActiveColumn = createCheckboxColumn("CC", "Enable the operationalization CC", BenchmarkCasePreferences::ccActiveProperty);
		private final TableColumn<BenchmarkCasePreferences, Boolean> coActiveColumn = createCheckboxColumn("CO", "Enable the operationalization CO", BenchmarkCasePreferences::coActiveProperty);

		class SelectAllCheckBox<S> extends CheckBox {
		    private Runnable selectAllStateChangeProcessor;
		    private Function<S, BooleanProperty> property;
		    private ObservableList<S> observableList;

		    public SelectAllCheckBox(ObservableList<S> observableList, Function<S, BooleanProperty> property) {
		    	super();
		    	this.property = property;
		    	this.observableList = observableList;
		    	
		    	for (S item : observableList) {
		    		property.apply(item).addListener((observable, wasSelected, isSelected) -> {
		    			updateSelectedState();
		    		});
				}
		    	observableList.addListener((Change<? extends S> c) -> {
		    		while (c.next()) {
			            if (c.wasAdded()) {
			            	for (S item : c.getAddedSubList()) {
			            		property.apply(item).addListener((observable, wasSelected, isSelected) -> {
					    			updateSelectedState();
					    		});
							}
						}
		    		}
		        });
		    	
		    	this.selectedProperty().addListener((observable, wasSelected, isSelected) ->
	               scheduleSelectAllStateChangeProcessing()
		        );
		    	
		    	updateSelectedState();
		    }
		    
		    private List<BooleanProperty> getAllProperties() {
				return observableList.stream().map((item) -> property.apply(item)).collect(Collectors.toList());
			}
		    
		    private void updateSelectedState() {
		    	if (selectAllStateChangeProcessor == null) {
	            	List<BooleanProperty> allProperties = getAllProperties();
	            	boolean allSelected = allProperties.stream()
	                        .map(BooleanProperty::get)
	                        .reduce(true, (a, b) -> a && b);
	                boolean anySelected = allProperties.stream()
	                		.map(BooleanProperty::get)
	                        .reduce(false, (a, b) -> a || b);

	                if (allSelected) {
	                    this.setSelected(true);
	                    this.setIndeterminate(false);
	                }

	                if (!anySelected) {
	                	this.setSelected(false);
	                	this.setIndeterminate(false);
	                }

	                if (anySelected && !allSelected) {
	                	this.setSelected(false);
	                	this.setIndeterminate(true);
	                }
	            }
			}
		    
		    private void scheduleSelectAllStateChangeProcessing() {
		        if (selectAllStateChangeProcessor == null) {
		            selectAllStateChangeProcessor = this::processSelectAllStateChange;
		            Platform.runLater(selectAllStateChangeProcessor);
		        }
		    }

		    private void processSelectAllStateChange() {
		    	if (!this.isIndeterminate()) {
		    		getAllProperties().forEach(bp -> bp.set(this.isSelected()));
				}
		        selectAllStateChangeProcessor = null;
		    }
		}
		
		@SuppressWarnings("unchecked")
		public BenchmarkCaseTableView() {
			// the table view itself
			setEditable(true);
			setRowFactory(tv -> {
				final TableRow<BenchmarkCasePreferences> row = new TableRow<>();
				
				// row menu
				final ContextMenu rowMenu = new ContextMenu();
				MenuItem editSelectedBenchmarkCases = new MenuItem("Edit Selected");
	            editSelectedBenchmarkCases.setOnAction(e -> {
	            	System.out.println("Action");
	            });
	            MenuItem markSelectedForExecution = new MenuItem("Mark for execution");
	            markSelectedForExecution.setOnAction(e -> {
	            	getSelectionModel().getSelectedItems().forEach(bcp -> bcp.setMarkedForExecution(true));
	            });
	            MenuItem addBenchmarkCase = new MenuItem("Add Benchmark Case");
	            addBenchmarkCase.setOnAction(e -> {
	            	createBenchmarkCase();
	            });
	            MenuItem deleteBenchmarkCase = new MenuItem("Delete Benchmark Case");
	            deleteBenchmarkCase.setOnAction(e -> {
	            	table.getItems().remove(row.getItem());
	            });
	            rowMenu.getItems().addAll(
	            		editSelectedBenchmarkCases,
	            		markSelectedForExecution,
	            		new SeparatorMenuItem(),
	            		addBenchmarkCase,
	            		deleteBenchmarkCase);
	            row.contextMenuProperty().bind(
                    Bindings.when(Bindings.isNotNull(row.itemProperty()))
                    .then(rowMenu)
                    .otherwise((ContextMenu)null));
	            
	            // double click event
			    row.setOnMouseClicked(event -> {
			    	if (!row.isEmpty()) {
			    		if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {

				        	BenchmarkCasePreferences clickedRow = row.getItem();
				            System.out.println(clickedRow.getBenchmarkCaseName());
				        }
					}
			        
			    });
			    
			    return row ;
			});

			getColumns().setAll(
					executionColumn, 
					nameColumn, 
					modelgenActiveColumn, 
					initialFwdActiveColumn, 
					initialBwdActiveColumn, 
					fwdOptActiveColumn, 
					bwdOptActiveColumn, 
					syncActiveColumn, 
					ccActiveColumn, 
					coActiveColumn);
		}
		
		private <S,T> TableColumn<S,T> createTextColumn(String title, Function<S, ObservableValue<T>> property) {
	        TableColumn<S,T> col = new TableColumn<>(title);
	        col.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
	        return col ;
	    }
		
		private TableColumn<BenchmarkCasePreferences, Boolean> createCheckboxColumn(String title, String tooltip, Function<BenchmarkCasePreferences, BooleanProperty> property) {
			TableColumn<BenchmarkCasePreferences, Boolean> column = new TableColumn<>();
	        column.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
	        column.setCellFactory(CheckBoxTableCell.forTableColumn(column));
	        column.setMinWidth(60.0);
	        
	        Label columnLabel = new Label(title);
	        columnLabel.setMaxWidth(Double.MAX_VALUE);
	        columnLabel.getStyleClass().add("column-header-label");
	        SelectAllCheckBox<BenchmarkCasePreferences> columnChkBox = new SelectAllCheckBox<BenchmarkCasePreferences>(benchmarkCasePreferencesList, property);
	        HBox columnLayout = new HBox(columnChkBox, columnLabel);
	        columnLayout.setSpacing(2);
	        columnLayout.setAlignment(Pos.CENTER);
	        if (tooltip != "") {
	        	Tooltip columnTooltip = new Tooltip(tooltip); 
	        	columnLabel.setTooltip(columnTooltip);
	        	columnChkBox.setTooltip(columnTooltip);
			}
	        column.setGraphic(columnLayout);
	        
	        return column ;
	    }
		
		private void createBenchmarkCase() {
			BenchmarkCasePreferences newBenchmarkCase = new BenchmarkCasePreferences();
			
			try {
				BenchmarkCasePreferencesWindow bcpWindow = new BenchmarkCasePreferencesWindow(newBenchmarkCase);
				bcpWindow.show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
//			if (save) {
//				
//			}
		}
		
		private void editBenchmarkCase(BenchmarkCasePreferences benchmarkCase) {
			BenchmarkCasePreferences tmpBenchmarkCaseCopy = new BenchmarkCasePreferences(benchmarkCase);
			
//			if (save) {
//			
//		}
		}
	}
}
