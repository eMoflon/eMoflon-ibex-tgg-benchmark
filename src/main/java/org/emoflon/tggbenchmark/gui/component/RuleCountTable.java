package org.emoflon.tggbenchmark.gui.component;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Pair;
import javafx.util.converter.NumberStringConverter;

public class RuleCountTable extends TabThroughTable<Pair<StringProperty, IntegerProperty>> {

    public RuleCountTable() {
        super();

        TableColumn<Pair<StringProperty, IntegerProperty>, String> ruleCol = new TableColumn<>("Rule");
        ruleCol.setMinWidth(120);
        ruleCol.setCellValueFactory(cellData -> cellData.getValue().getKey());
        ruleCol.setCellFactory(column -> {
            EditCell<Pair<StringProperty, IntegerProperty>, String> cell = EditCell.createStringEditCell();
            cell.onTableViewFocusLost(() -> pruneEmtpyRows());
            return cell;
        });
        getColumns().add(ruleCol);

        TableColumn<Pair<StringProperty, IntegerProperty>, Number> countCol = new TableColumn<>("Count");
        countCol.setMinWidth(10);
        countCol.setCellValueFactory(cellData -> cellData.getValue().getValue());
        countCol.setCellFactory(column -> {
            EditCell<Pair<StringProperty, IntegerProperty>, Number> cell = new EditCell<Pair<StringProperty, IntegerProperty>, Number>(
                    new IntegerTextField(), new NumberStringConverter());
            
            cell.onTableViewFocusLost(() -> pruneEmtpyRows());
            return cell;
        });
        getColumns().add(countCol);
        
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @Override
    public Pair<StringProperty, IntegerProperty> createDefaultItem() {
        return new Pair<StringProperty, IntegerProperty>(new SimpleStringProperty(""), new SimpleIntegerProperty(0));
    }

    @Override
    public boolean isRowEmtpty(Pair<StringProperty, IntegerProperty> rowData) {
        return rowData.getKey().getValue().isEmpty();
    }
}
