package org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryPart;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class CategoryOperationalizationsPart extends CategoryPart<BenchmarkCasePreferences> {

	private ObservableList<Integer> modelSizes;
	private ObservableList<String> maxModelSizeChoiceList;

	// elements from the FXML resource
	@FXML
	private CheckBox modelgenCreateReport;
	@FXML
	private TextField modelgenTimeout;
	@FXML
	private ListView<Integer> modelgenModelSizes;
	@FXML
	private CheckBox initialFwdActive;
	@FXML
	private TextField initialFwdTimeout;
	@FXML
	private ChoiceBox<String> initialFwdMaxModelSize;
	@FXML
	private CheckBox initialBwdActive;
	@FXML
	private TextField initialBwdTimeout;
	@FXML
	private ChoiceBox<String> initialBwdMaxModelSize;
	@FXML
	private CheckBox fwdOptActive;
	@FXML
	private TextField fwdOptTimeout;
	@FXML
	private ChoiceBox<String> fwdOptMaxModelSize;
	@FXML
	private CheckBox bwdOptActive;
	@FXML
	private TextField bwdOptTimeout;
	@FXML
	private ChoiceBox<String> bwdOptMaxModelSize;
	@FXML
	private CheckBox syncActive;
	@FXML
	private TextField syncTimeout;
	@FXML
	private ChoiceBox<String> syncMaxModelSize;
	@FXML
	private ChoiceBox<?> syncDirection;
	@FXML
	private TextField syncConsumer;
	@FXML
	private CheckBox ccActive;
	@FXML
	private TextField ccTimeout;
	@FXML
	private ChoiceBox<String> ccMaxModelSize;
	@FXML
	private CheckBox coActive;
	@FXML
	private TextField coTimeout;
	@FXML
	private ChoiceBox<String> coMaxModelSize;

	public CategoryOperationalizationsPart() throws IOException {
		super("../../resources/fxml/benchmark_case_preferences/CategoryOperationalizations.fxml");
		
		modelSizes = FXCollections.observableArrayList(1000, 2000, 4000, 8000);
		maxModelSizeChoiceList = FXCollections.observableArrayList("no limit");
	}
	
	@Override
	public void initData(BenchmarkCasePreferences prefsData) {
		super.initData(prefsData);
		
		// MODELGEN
		modelSizes.forEach((e) -> maxModelSizeChoiceList.add(e.toString()));
		
		modelgenModelSizes.setItems(modelSizes);


		maxModelSizeChoiceList.addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				changeSelection(initialFwdMaxModelSize);
				changeSelection(initialBwdMaxModelSize);
				changeSelection(fwdOptMaxModelSize);
				changeSelection(bwdOptMaxModelSize);
				changeSelection(syncMaxModelSize);
				changeSelection(ccMaxModelSize);
				changeSelection(coMaxModelSize);
			}
			private void changeSelection(ChoiceBox<String> cbx) {
				int selectionIndex = cbx.getSelectionModel().getSelectedIndex();
				System.out.println(cbx.getChildrenUnmodifiable().get(selectionIndex));
			}
		});

		// INITIAL FWD
		initialFwdMaxModelSize.setItems(maxModelSizeChoiceList);
		initialFwdMaxModelSize.getSelectionModel().selectFirst();

		// INITIAL BWD
		initialBwdMaxModelSize.setItems(maxModelSizeChoiceList);
		initialBwdMaxModelSize.getSelectionModel().selectFirst();

		// FWD OPT
		fwdOptMaxModelSize.setItems(maxModelSizeChoiceList);
		fwdOptMaxModelSize.getSelectionModel().selectFirst();

		// BWD OPT
		bwdOptMaxModelSize.setItems(maxModelSizeChoiceList);
		bwdOptMaxModelSize.getSelectionModel().selectFirst();

		// SYNC
		syncMaxModelSize.setItems(maxModelSizeChoiceList);
		syncMaxModelSize.getSelectionModel().selectFirst();

		// CC
		ccMaxModelSize.setItems(maxModelSizeChoiceList);
		ccMaxModelSize.getSelectionModel().selectFirst();

		// CO
		coMaxModelSize.setItems(maxModelSizeChoiceList);
		coMaxModelSize.getSelectionModel().selectFirst();
	}

	@FXML
	private void handleButtonAction(ActionEvent event)
	{
		System.out.println("You clicked me!");
	}

	
	@FXML
    protected void doSomething() {
        System.out.println("The button was clicked!");
    }
	
}
