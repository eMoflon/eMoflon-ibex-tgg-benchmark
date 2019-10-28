package org.emoflon.tggbenchmark.workspace;

import java.nio.file.Path;

import javafx.collections.ObservableList;

/**
 * IEclipseWorkspace allows access to an Eclipse workspace without necessarily
 * using the Eclipse API. This allows easier testing of the plugin.
 *
 * @author Andre Lehmann
 */
public interface IEclipseWorkspace {

    /**
     * @return the workspace location
     */
    public Path getLocation();

    /**
     * @return the TGG projects contained in this workspace
     */
    public ObservableList<EclipseTggProject> getTggProjects();

    /**
     * @return the plugin state location where preferences might be safed
     */
    public Path getPluginStateLocation();

    /**
     * @return the path of the plugin prefernces file
     */
    public Path getPluginPreferencesFilePath();
}
