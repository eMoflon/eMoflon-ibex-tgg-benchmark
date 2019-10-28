package org.emoflon.tggbenchmark.gui.model;

import org.controlsfx.glyphfont.FontAwesome.Glyph;
import org.emoflon.tggbenchmark.gui.controller.generic_preferences.GenericPreferencesController;

import javafx.scene.Parent;

/**
 * CategoryDataModel represents the data model for categories used in the
 * {@link GenericPreferencesController}.
 *
 * @author Andre Lehmann
 */
public class Category {

    private String displayName;
    private Glyph displayIcon;
    private Parent content;

    /**
     * Constructor for {@link Category}.
     * 
     * @param displayName The display name for the category
     * @param displayIcon The fontawesome icon for the category
     * @param content     The GUI content of the category
     */
    public Category(String displayName, Glyph displayIcon, Parent content) {
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
