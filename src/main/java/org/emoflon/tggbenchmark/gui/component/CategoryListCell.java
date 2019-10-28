package org.emoflon.tggbenchmark.gui.component;

import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.emoflon.tggbenchmark.gui.model.Category;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

/**
 * CategoryListCell represents a custom {@link ListCell} for the category
 * ListView.
 *
 * @author Andre Lehmann
 */
public class CategoryListCell extends ListCell<Category> {

    private HBox layout;
    private Label nameLabel;
    private Label icon;
    private GlyphFont fontAwesome;

    /**
     * Constructor for {@link CategoryListCell}.
     */
    public CategoryListCell() {
        nameLabel = new Label();
        icon = new Label();

        layout = new HBox();
        layout.setPadding(new Insets(8.0));
        layout.setSpacing(14.0);
        layout.getChildren().addAll(icon, nameLabel);

        fontAwesome = GlyphFontRegistry.font("FontAwesome");
    }

    @Override
    protected void updateItem(Category prefCat, boolean empty) {
        super.updateItem(prefCat, empty);
        setText(null);

        if (empty || prefCat == null) {
            setGraphic(null);
        } else {
            nameLabel.setText(prefCat.getDisplayName());
            icon.setGraphic(fontAwesome.create(prefCat.getDisplayIcon()));
            setGraphic(layout);
        }
    }

}
