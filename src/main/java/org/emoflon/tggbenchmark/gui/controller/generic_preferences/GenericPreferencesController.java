package org.emoflon.tggbenchmark.gui.controller.generic_preferences;

import java.io.IOException;
import java.util.Collection;

import org.controlsfx.glyphfont.FontAwesome.Glyph;
import org.emoflon.tggbenchmark.gui.component.CategoryListCell;
import org.emoflon.tggbenchmark.gui.controller.FXMLController;
import org.emoflon.tggbenchmark.gui.model.Category;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * GenericPreferencesPart represents a GUI FXML part for a preferences window.
 *
 * @author Andre Lehmann
 */
public class GenericPreferencesController extends FXMLController {

    private ObservableList<Category> categoriesViewData;

    // elements from the FXML resource
    @FXML
    private HBox buttonPane;
    @FXML
    private ScrollPane mainContentPane;
    @FXML
    private VBox contentContainer;
    @FXML
    private ListView<Category> categories;

    /**
     * Constructor for {@link GenericPreferencesController}.
     * 
     * @throws IOException if the FXML resource couldn't be found
     */
    public GenericPreferencesController() throws IOException {
        super("../../../resources/view/generic_preferences/GenericPreferences.fxml");
    }

    /**
     * Sets the preference content of a category so it will be visible to the user.
     *
     * @param pane The selected Node in the 'categories' ListView
     */
    private void setCategoryContent(Node pane) {
        // The content area will be cleared and repopulated
        ObservableList<Node> children = contentContainer.getChildren();
        children.clear();
        // the pane is null so it is the 'List All' category -> add all categories
        if (pane == null) {
            categoriesViewData.forEach(e -> {
                if (e.getContent() != null) {
                    children.add(e.getContent());
                }
            });
        } else {
            children.add(pane);
        }
    }

    /**
     * Initializes the parts elements by binding them to a data model. This needs to
     * be done after an instance of this class has been created, because only then
     * will the @FXML elements be populated from the FXML resource.
     *
     * @param categoriesViewData The data that represents the categories
     */
    public void initCategoriesView(ObservableList<Category> categoriesViewData) {
        this.categoriesViewData = categoriesViewData;

        categoriesViewData.add(0, new Category("List All", Glyph.BARS, null));

        // main content
        categoriesViewData.forEach(e -> {
            if (e.getContent() != null) {
                contentContainer.getChildren().add(e.getContent());
            }
        });

        // sidemenu
        categories.setItems(categoriesViewData);
        categories.setCellFactory(s -> new CategoryListCell());
        categories.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setCategoryContent(newValue.getContent());
        });
        categories.getSelectionModel().selectFirst();
    }

    /**
     * Populate the lower button pane.
     * 
     * @param leftButtons  The buttons floating on the left side
     * @param rightButtons The buttons floating on the right side
     */
    public void populateButtonPane(Collection<Node> leftButtons, Collection<Node> rightButtons) {
        buttonPane.getChildren().addAll(leftButtons);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        buttonPane.getChildren().add(spacer);
        buttonPane.getChildren().addAll(rightButtons);
    }
}
