package org.emoflon.ibex.tgg.benchmark.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BenchmarkCasePreferences {
	
	// general
	private BooleanProperty markedForExecution;
	private StringProperty benchmarkCaseName;
	private IntegerProperty defaultTimeout;
	private IntegerProperty numberOfRuns;
	
	// input
	
	
	// output
	
	
	// operationalizations

	private BooleanProperty modelgenCreateReport;
	private IntegerProperty modelgenTimeout;
	private ListProperty<Integer> modelgenModelSizes;
	private BooleanProperty initialFwdActive;
	private IntegerProperty initialFwdTimeout;
	private IntegerProperty initialFwdMaxModelSize;
	private BooleanProperty initialBwdActive;
	private IntegerProperty initialBwdTimeout;
	private IntegerProperty initialBwdMaxModelSize;
	private BooleanProperty fwdOptActive;
	private IntegerProperty fwdOptTimeout;
	private IntegerProperty fwdOptMaxModelSize;
	private BooleanProperty bwdOptActive;
	private IntegerProperty bwdOptTimeout;
	private IntegerProperty bwdOptMaxModelSize;
	private BooleanProperty syncActive;
	private IntegerProperty syncTimeout;
	private IntegerProperty syncMaxModelSize;
	private StringProperty syncDirection;
	private IntegerProperty syncConsumer;
	private BooleanProperty ccActive;
	private IntegerProperty ccTimeout;
	private IntegerProperty ccMaxModelSize;
	private BooleanProperty coActive;
	private IntegerProperty coTimeout;
	private IntegerProperty coMaxModelSize;
	
	public enum syncDirectionValues {
        FWD("fwd"),
        BWD("bwd"),
        BOTH("both");

        private String direction;

        syncDirectionValues(String direction) {
            this.direction = direction;
        }

        public String toString() {
            return direction;
        }
    }
	
	public BenchmarkCasePreferences() {
		// general
		markedForExecution = new SimpleBooleanProperty(true);
		defaultTimeout = new SimpleIntegerProperty(600);
		benchmarkCaseName = new SimpleStringProperty("");
		numberOfRuns = new SimpleIntegerProperty(3);
		
		// input
		
		// output
		
		// operationalizations
		modelgenCreateReport = new SimpleBooleanProperty(true);
		modelgenTimeout = new SimpleIntegerProperty(-1);
		modelgenModelSizes = new SimpleListProperty<>(FXCollections.observableArrayList(1000, 2000, 4000, 8000));
		
		initialFwdActive = new SimpleBooleanProperty(true);
		initialFwdTimeout = new SimpleIntegerProperty(0);
		initialFwdMaxModelSize = new SimpleIntegerProperty(-1);
		
		initialBwdActive = new SimpleBooleanProperty(true);
		initialBwdTimeout = new SimpleIntegerProperty(-1);
		initialBwdMaxModelSize = new SimpleIntegerProperty(-1);
		
		fwdOptActive = new SimpleBooleanProperty(true);
		fwdOptTimeout = new SimpleIntegerProperty(-1);
		fwdOptMaxModelSize = new SimpleIntegerProperty(-1);
		
		bwdOptActive = new SimpleBooleanProperty(true);
		bwdOptTimeout = new SimpleIntegerProperty(-1);
		bwdOptMaxModelSize = new SimpleIntegerProperty(-1);
		
		syncActive = new SimpleBooleanProperty(true);
		syncTimeout = new SimpleIntegerProperty(-1);
		syncMaxModelSize = new SimpleIntegerProperty(-1);
		syncDirection = new SimpleStringProperty(syncDirectionValues.FWD.toString());
		syncConsumer = new SimpleIntegerProperty(1);

		ccActive = new SimpleBooleanProperty(true);
		ccTimeout = new SimpleIntegerProperty(-1);
		ccMaxModelSize = new SimpleIntegerProperty(-1);
		
		coActive = new SimpleBooleanProperty(true);
		coTimeout = new SimpleIntegerProperty(-1);
		coMaxModelSize = new SimpleIntegerProperty(-1);
	}
	
	public BenchmarkCasePreferences(BenchmarkCasePreferences source) {
		this();
		setChanges(source);
	}
	
	public JsonObject getAsJSON() {
		
		JsonArrayBuilder modelSizesBuilder = Json.createArrayBuilder();
		for (Integer integer : getModelgenModelSizes()) {
			modelSizesBuilder.add(integer);
		}
		
        JsonObject preferences = Json.createObjectBuilder()
			.add("general", Json.createObjectBuilder()
				.add("markedForExecution", isMarkedForExecution())
				.add("benchmarkCaseName", getBenchmarkCaseName())
				.add("defaultTimeout", getDefaultTimeout())
				.add("numberOfRuns", getNumberOfRuns()).build())
            .add("input", Json.createObjectBuilder().build())
            .add("output", Json.createObjectBuilder().build())
            .add("operationalizations", Json.createObjectBuilder()
				.add("modelgen", Json.createObjectBuilder()
					.add("createReport", isModelgenCreateReport())
					.add("timeout", getModelgenTimeout())
					.add("modelSizes", modelSizesBuilder.build()).build())
				.add("initialFwd", Json.createObjectBuilder()
					.add("active", isInitialFwdActive())
					.add("timeout", getInitialFwdTimeout())
					.add("maxModelSize", getInitialFwdMaxModelSize()).build())
				.add("initialBwd", Json.createObjectBuilder()
					.add("active", isInitialBwdActive())
					.add("timeout", getInitialBwdTimeout())
					.add("maxModelSize", getInitialBwdMaxModelSize()).build())
				.add("fwdOpt", Json.createObjectBuilder()
					.add("active", isFwdOptActive())
					.add("timeout", getFwdOptTimeout())
					.add("maxModelSize", getFwdOptMaxModelSize()).build())
				.add("bwdOpt", Json.createObjectBuilder()
					.add("active", isBwdOptActive())
					.add("timeout", getBwdOptTimeout())
					.add("maxModelSize", getBwdOptMaxModelSize()).build())
				.add("sync", Json.createObjectBuilder()
					.add("active", isSyncActive())
					.add("timeout", getSyncTimeout())
					.add("maxModelSize", getSyncMaxModelSize())
					.add("syncDirection", getSyncDirection())
					.add("consumer", getSyncConsumer()).build())
				.add("cc", Json.createObjectBuilder()
					.add("active", isCcActive())
					.add("timeout", getCcTimeout())
					.add("maxModelSize", getCcMaxModelSize()).build())
				.add("co", Json.createObjectBuilder()
					.add("active", isCoActive())
					.add("timeout", getCoTimeout())
					.add("maxModelSize", getCoMaxModelSize()).build()).build()).build();

		return preferences;
	}
	
	public void loadFromJSON(JsonObject data) {
		JsonObject general = data.getJsonObject("general");
		if (general != null) {
			setMarkedForExecution(data.getBoolean("markedForExecution", isMarkedForExecution()));
			setBenchmarkCaseName(data.getString("benchmarkCaseName", getBenchmarkCaseName()));
			setDefaultTimeout(data.getInt("defaultTimeout", getDefaultTimeout()));
			setNumberOfRuns(data.getInt("numberOfRuns", getNumberOfRuns()));
		}

		JsonObject input = data.getJsonObject("input");
		if (input != null) {
			
		}

		JsonObject output = data.getJsonObject("output");
		if (output != null) {
			
		}

		JsonObject operationalizations = data.getJsonObject("operationalizations");
		if (operationalizations != null) {
			JsonObject modelgen = operationalizations.getJsonObject("modelgen");
			if (modelgen != null) {
				setModelgenCreateReport(modelgen.getBoolean("createReport", isModelgenCreateReport()));
				setModelgenTimeout(modelgen.getInt("timeout", getModelgenTimeout()));
				JsonArray modelSizesArray = modelgen.getJsonArray("modelSizes");
				if (modelSizesArray != null) {
					ObservableList<Integer> modelSizes = FXCollections.observableArrayList();
					for (int i = 0; i < modelSizesArray.size(); i++) {
						try {
							modelSizes.add(modelSizesArray.getInt(i));
						} catch (ClassCastException e) {
							System.err.println(String.format("Invalid model size! '%s' is not an integer number", modelSizesArray.getString(i)));
						}
					}
					setModelgenModelSizes(modelSizes);
				}
			}

			JsonObject initialFwd = operationalizations.getJsonObject("initialFwd");
			if (initialFwd != null) {
				setInitialFwdActive(initialFwd.getBoolean("active", isInitialFwdActive()));
				setInitialFwdTimeout(initialFwd.getInt("timeout", getInitialFwdTimeout()));
				setInitialFwdMaxModelSize(initialFwd.getInt("maxModelSize", getInitialFwdMaxModelSize()));
			}

			JsonObject initialBwd = operationalizations.getJsonObject("initialBwd");
			if (initialBwd != null) {
				setInitialBwdActive(initialBwd.getBoolean("active", isInitialBwdActive()));
				setInitialBwdTimeout(initialBwd.getInt("timeout", getInitialBwdTimeout()));
				setInitialBwdMaxModelSize(initialBwd.getInt("maxModelSize", getInitialBwdMaxModelSize()));
			}

			JsonObject fwdOpt = operationalizations.getJsonObject("fwdOpt");
			if (fwdOpt != null) {
				setFwdOptActive(fwdOpt.getBoolean("active", isFwdOptActive()));
				setFwdOptTimeout(fwdOpt.getInt("timeout", getFwdOptTimeout()));
				setFwdOptMaxModelSize(fwdOpt.getInt("maxModelSize", getFwdOptMaxModelSize()));
			}

			JsonObject bwdOpt = operationalizations.getJsonObject("bwdOpt");
			if (bwdOpt != null) {
				setBwdOptActive(bwdOpt.getBoolean("active", isBwdOptActive()));
				setBwdOptTimeout(bwdOpt.getInt("timeout", getBwdOptTimeout()));
				setBwdOptMaxModelSize(bwdOpt.getInt("maxModelSize", getBwdOptMaxModelSize()));
			}

			JsonObject sync = operationalizations.getJsonObject("sync");
			if (sync != null) {
				setSyncActive(sync.getBoolean("active", isSyncActive()));
				setSyncTimeout(sync.getInt("timeout", getSyncTimeout()));
				setSyncMaxModelSize(sync.getInt("maxModelSize", getSyncMaxModelSize()));
				setSyncDirection(sync.getString("syncDirection", getSyncDirection()));
				setSyncConsumer(sync.getInt("consumer", getSyncConsumer()));
			}

			JsonObject cc = operationalizations.getJsonObject("cc");
			if (cc != null) {
				setCcActive(cc.getBoolean("active", isCcActive()));
				setCcTimeout(cc.getInt("timeout", getCcTimeout()));
				setCcMaxModelSize(cc.getInt("maxModelSize", getCcMaxModelSize()));
			}

			JsonObject co = operationalizations.getJsonObject("co");
			if (co != null) {
				setCoActive(co.getBoolean("active", isCoActive()));
				setCoTimeout(co.getInt("timeout", getCoTimeout()));
				setCoMaxModelSize(co.getInt("maxModelSize", getCoMaxModelSize()));
			}
		}
	}
	
	public final List<Observable> getAllProperties() {
		return new LinkedList<Observable>(Arrays.asList(
				markedForExecution,
				benchmarkCaseName,
				defaultTimeout,
				numberOfRuns,
				modelgenCreateReport,
				modelgenTimeout,
				modelgenModelSizes,
				initialFwdActive,
				initialFwdTimeout,
				initialFwdMaxModelSize,
				initialBwdActive,
				initialBwdTimeout,
				initialBwdMaxModelSize,
				fwdOptActive,
				fwdOptTimeout,
				fwdOptMaxModelSize,
				bwdOptActive,
				bwdOptTimeout,
				bwdOptMaxModelSize,
				syncActive,
				syncTimeout,
				syncMaxModelSize,
				syncDirection,
				syncConsumer,
				ccActive,
				ccTimeout,
				ccMaxModelSize,
				coActive,
				coTimeout,
				coMaxModelSize
				));
	}
	
	public void setChanges(BenchmarkCasePreferences source) {
		List<Observable> myProperties = getAllProperties();
		List<Observable> theirProperties = source.getAllProperties();
		
		for (int i = 0; i < myProperties.size(); i++) {
			if (myProperties.get(i) instanceof StringProperty) {
				StringProperty myProperty = (StringProperty) myProperties.get(i);
				StringProperty theirProperty = (StringProperty) theirProperties.get(i);
				if (!myProperty.getValue().equals(theirProperty.getValue())) {
					myProperty.setValue(theirProperty.getValue());
				}
			} else if (myProperties.get(i) instanceof BooleanProperty) {
				BooleanProperty myProperty = (BooleanProperty) myProperties.get(i);
				BooleanProperty theirProperty = (BooleanProperty) theirProperties.get(i);
				if (!myProperty.getValue().equals(theirProperty.getValue())) {
					myProperty.setValue(theirProperty.getValue());
				}
			} else if (myProperties.get(i) instanceof IntegerProperty) {
				IntegerProperty myProperty = (IntegerProperty) myProperties.get(i);
				IntegerProperty theirProperty = (IntegerProperty) theirProperties.get(i);
				if (!myProperty.getValue().equals(theirProperty.getValue())) {
					myProperty.setValue(theirProperty.getValue());
				}
			} else if (myProperties.get(i) instanceof ListProperty<?>) {
				ListProperty<?> myProperty = (ListProperty<?>) myProperties.get(i);
				ListProperty<?> theirProperty = (ListProperty<?>) theirProperties.get(i);
				if (!myProperty.getValue().equals(theirProperty.getValue())) {
					myProperty.setAll((List)theirProperty.stream().collect(Collectors.toList()));
				}
			}
		}
	}

	public final StringProperty benchmarkCaseNameProperty() {
		return this.benchmarkCaseName;
	}
	

	public final String getBenchmarkCaseName() {
		return this.benchmarkCaseNameProperty().get();
	}
	

	public final void setBenchmarkCaseName(final String benchmarkCaseName) {
		this.benchmarkCaseNameProperty().set(benchmarkCaseName);
	}
	

	public final IntegerProperty defaultTimeoutProperty() {
		return this.defaultTimeout;
	}
	

	public final int getDefaultTimeout() {
		return this.defaultTimeoutProperty().get();
	}
	

	public final void setDefaultTimeout(final int defaultTimeout) {
		this.defaultTimeoutProperty().set(defaultTimeout);
	}
	

	public final IntegerProperty numberOfRunsProperty() {
		return this.numberOfRuns;
	}
	

	public final int getNumberOfRuns() {
		return this.numberOfRunsProperty().get();
	}
	

	public final void setNumberOfRuns(final int numberOfRuns) {
		this.numberOfRunsProperty().set(numberOfRuns);
	}
	

	public final BooleanProperty modelgenCreateReportProperty() {
		return this.modelgenCreateReport;
	}
	

	public final boolean isModelgenCreateReport() {
		return this.modelgenCreateReportProperty().get();
	}
	

	public final void setModelgenCreateReport(final boolean modelgenCreateReport) {
		this.modelgenCreateReportProperty().set(modelgenCreateReport);
	}
	

	public final IntegerProperty modelgenTimeoutProperty() {
		return this.modelgenTimeout;
	}
	

	public final int getModelgenTimeout() {
		return this.modelgenTimeoutProperty().get();
	}
	

	public final void setModelgenTimeout(final int modelgenTimeout) {
		this.modelgenTimeoutProperty().set(modelgenTimeout);
	}

	public final BooleanProperty initialFwdActiveProperty() {
		return this.initialFwdActive;
	}
	

	public final boolean isInitialFwdActive() {
		return this.initialFwdActiveProperty().get();
	}
	

	public final void setInitialFwdActive(final boolean initialFwdActive) {
		this.initialFwdActiveProperty().set(initialFwdActive);
	}
	

	public final IntegerProperty initialFwdTimeoutProperty() {
		return this.initialFwdTimeout;
	}
	

	public final int getInitialFwdTimeout() {
		return this.initialFwdTimeoutProperty().get();
	}
	

	public final void setInitialFwdTimeout(final int initialFwdTimeout) {
		this.initialFwdTimeoutProperty().set(initialFwdTimeout);
	}
	

	public final IntegerProperty initialFwdMaxModelSizeProperty() {
		return this.initialFwdMaxModelSize;
	}
	

	public final int getInitialFwdMaxModelSize() {
		return this.initialFwdMaxModelSizeProperty().get();
	}
	

	public final void setInitialFwdMaxModelSize(final int initialFwdMaxModelSize) {
		this.initialFwdMaxModelSizeProperty().set(initialFwdMaxModelSize);
	}
	

	public final BooleanProperty initialBwdActiveProperty() {
		return this.initialBwdActive;
	}
	

	public final boolean isInitialBwdActive() {
		return this.initialBwdActiveProperty().get();
	}
	

	public final void setInitialBwdActive(final boolean initialBwdActive) {
		this.initialBwdActiveProperty().set(initialBwdActive);
	}
	

	public final IntegerProperty initialBwdTimeoutProperty() {
		return this.initialBwdTimeout;
	}
	

	public final int getInitialBwdTimeout() {
		return this.initialBwdTimeoutProperty().get();
	}
	

	public final void setInitialBwdTimeout(final int initialBwdTimeout) {
		this.initialBwdTimeoutProperty().set(initialBwdTimeout);
	}
	

	public final IntegerProperty initialBwdMaxModelSizeProperty() {
		return this.initialBwdMaxModelSize;
	}
	

	public final int getInitialBwdMaxModelSize() {
		return this.initialBwdMaxModelSizeProperty().get();
	}
	

	public final void setInitialBwdMaxModelSize(final int initialBwdMaxModelSize) {
		this.initialBwdMaxModelSizeProperty().set(initialBwdMaxModelSize);
	}
	

	public final BooleanProperty fwdOptActiveProperty() {
		return this.fwdOptActive;
	}
	

	public final boolean isFwdOptActive() {
		return this.fwdOptActiveProperty().get();
	}
	

	public final void setFwdOptActive(final boolean fwdOptActive) {
		this.fwdOptActiveProperty().set(fwdOptActive);
	}
	

	public final IntegerProperty fwdOptTimeoutProperty() {
		return this.fwdOptTimeout;
	}
	

	public final int getFwdOptTimeout() {
		return this.fwdOptTimeoutProperty().get();
	}
	

	public final void setFwdOptTimeout(final int fwdOptTimeout) {
		this.fwdOptTimeoutProperty().set(fwdOptTimeout);
	}
	

	public final IntegerProperty fwdOptMaxModelSizeProperty() {
		return this.fwdOptMaxModelSize;
	}
	

	public final int getFwdOptMaxModelSize() {
		return this.fwdOptMaxModelSizeProperty().get();
	}
	

	public final void setFwdOptMaxModelSize(final int fwdOptMaxModelSize) {
		this.fwdOptMaxModelSizeProperty().set(fwdOptMaxModelSize);
	}
	

	public final BooleanProperty bwdOptActiveProperty() {
		return this.bwdOptActive;
	}
	

	public final boolean isBwdOptActive() {
		return this.bwdOptActiveProperty().get();
	}
	

	public final void setBwdOptActive(final boolean bwdOptActive) {
		this.bwdOptActiveProperty().set(bwdOptActive);
	}
	

	public final IntegerProperty bwdOptTimeoutProperty() {
		return this.bwdOptTimeout;
	}
	

	public final int getBwdOptTimeout() {
		return this.bwdOptTimeoutProperty().get();
	}
	

	public final void setBwdOptTimeout(final int bwdOptTimeout) {
		this.bwdOptTimeoutProperty().set(bwdOptTimeout);
	}
	

	public final IntegerProperty bwdOptMaxModelSizeProperty() {
		return this.bwdOptMaxModelSize;
	}
	

	public final int getBwdOptMaxModelSize() {
		return this.bwdOptMaxModelSizeProperty().get();
	}
	

	public final void setBwdOptMaxModelSize(final int bwdOptMaxModelSize) {
		this.bwdOptMaxModelSizeProperty().set(bwdOptMaxModelSize);
	}
	

	public final BooleanProperty syncActiveProperty() {
		return this.syncActive;
	}
	

	public final boolean isSyncActive() {
		return this.syncActiveProperty().get();
	}
	

	public final void setSyncActive(final boolean syncActive) {
		this.syncActiveProperty().set(syncActive);
	}
	

	public final IntegerProperty syncTimeoutProperty() {
		return this.syncTimeout;
	}
	

	public final int getSyncTimeout() {
		return this.syncTimeoutProperty().get();
	}
	

	public final void setSyncTimeout(final int syncTimeout) {
		this.syncTimeoutProperty().set(syncTimeout);
	}
	

	public final IntegerProperty syncMaxModelSizeProperty() {
		return this.syncMaxModelSize;
	}
	

	public final int getSyncMaxModelSize() {
		return this.syncMaxModelSizeProperty().get();
	}
	

	public final void setSyncMaxModelSize(final int syncMaxModelSize) {
		this.syncMaxModelSizeProperty().set(syncMaxModelSize);
	}
	

	public final IntegerProperty syncConsumerProperty() {
		return this.syncConsumer;
	}
	

	public final int getSyncConsumer() {
		return this.syncConsumerProperty().get();
	}
	

	public final void setSyncConsumer(final int syncConsumer) {
		this.syncConsumerProperty().set(syncConsumer);
	}
	

	public final BooleanProperty ccActiveProperty() {
		return this.ccActive;
	}
	

	public final boolean isCcActive() {
		return this.ccActiveProperty().get();
	}
	

	public final void setCcActive(final boolean ccActive) {
		this.ccActiveProperty().set(ccActive);
	}
	

	public final IntegerProperty ccTimeoutProperty() {
		return this.ccTimeout;
	}
	

	public final int getCcTimeout() {
		return this.ccTimeoutProperty().get();
	}
	

	public final void setCcTimeout(final int ccTimeout) {
		this.ccTimeoutProperty().set(ccTimeout);
	}
	

	public final IntegerProperty ccMaxModelSizeProperty() {
		return this.ccMaxModelSize;
	}
	

	public final int getCcMaxModelSize() {
		return this.ccMaxModelSizeProperty().get();
	}
	

	public final void setCcMaxModelSize(final int ccMaxModelSize) {
		this.ccMaxModelSizeProperty().set(ccMaxModelSize);
	}
	

	public final BooleanProperty coActiveProperty() {
		return this.coActive;
	}
	

	public final boolean isCoActive() {
		return this.coActiveProperty().get();
	}
	

	public final void setCoActive(final boolean coActive) {
		this.coActiveProperty().set(coActive);
	}
	

	public final IntegerProperty coTimeoutProperty() {
		return this.coTimeout;
	}
	

	public final int getCoTimeout() {
		return this.coTimeoutProperty().get();
	}
	

	public final void setCoTimeout(final int coTimeout) {
		this.coTimeoutProperty().set(coTimeout);
	}
	

	public final IntegerProperty coMaxModelSizeProperty() {
		return this.coMaxModelSize;
	}
	

	public final int getCoMaxModelSize() {
		return this.coMaxModelSizeProperty().get();
	}
	

	public final void setCoMaxModelSize(final int coMaxModelSize) {
		this.coMaxModelSizeProperty().set(coMaxModelSize);
	}

	public final BooleanProperty markedForExecutionProperty() {
		return this.markedForExecution;
	}
	

	public final boolean isMarkedForExecution() {
		return this.markedForExecutionProperty().get();
	}
	

	public final void setMarkedForExecution(final boolean markedForExecution) {
		this.markedForExecutionProperty().set(markedForExecution);
	}

	public final StringProperty syncDirectionProperty() {
		return this.syncDirection;
	}
	

	public final String getSyncDirection() {
		return this.syncDirectionProperty().get();
	}
	

	public final void setSyncDirection(final String syncDirection) {
		try {
			this.syncDirection.set(syncDirectionValues.valueOf(syncDirection).toString());
		} catch (IllegalArgumentException e) {
			System.err.println(String.format("'%s' is not a valid sync direction", syncDirection));
		}
	}

	public final ListProperty<Integer> modelgenModelSizesProperty() {
		return this.modelgenModelSizes;
	}
	

	public final ObservableList<Integer> getModelgenModelSizes() {
		return this.modelgenModelSizesProperty().get();
	}
	

	public final void setModelgenModelSizes(final ObservableList<Integer> modelgenModelSizes) {
		this.modelgenModelSizesProperty().set(modelgenModelSizes);
	}
	
}
