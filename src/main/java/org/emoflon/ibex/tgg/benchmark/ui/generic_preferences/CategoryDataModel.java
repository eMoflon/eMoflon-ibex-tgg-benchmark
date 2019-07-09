package org.emoflon.ibex.tgg.benchmark.ui.generic_preferences;

import org.controlsfx.glyphfont.FontAwesome.Glyph;

import javafx.scene.Parent;

/**
 * CategoryDataModel represents the data model for categories used in the
 * {@link GenericPreferencesPart}.
 *
 * @author Andre Lehmann
 * @version 1.0
 * @since 2019-07-09
 */
public class CategoryDataModel {

	private String displayName;
	private Glyph displayIcon;
	private Parent content;

	/**
	 * Constructor for {@link CategoryDataModel}.
	 * 
	 * @param displayName The display name for the category
	 * @param displayIcon The fontawesome icon for the category
	 * @param content     The GUI content of the category
	 */
	public CategoryDataModel(String displayName, Glyph displayIcon, Parent content) {
		this.displayName = displayName;
		this.displayIcon = displayIcon;
		this.content = content;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return the displayIcon
	 */
	public Glyph getDisplayIcon() {
		return displayIcon;
	}

	/**
	 * @return the content
	 */
	public Parent getContent() {
		return content;
	}
}
