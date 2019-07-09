package org.emoflon.ibex.tgg.benchmark.ui.generic_preferences;

import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

/**
 * CategoryListCell represents a custom ListCell for the category ListView.
 *
 * @author Andre Lehmann
 * @version 1.0
 * @since 2019-07-09
 */
public class CategoryListCell extends ListCell<CategoryDataModel> {

	private HBox layout;
	private Label nameLabel;
	private Label icon;
	private GlyphFont fontAwesome;

	/**
	 * Constructor for CategoryListCell
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
	protected void updateItem(CategoryDataModel prefCat, boolean empty) {
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
