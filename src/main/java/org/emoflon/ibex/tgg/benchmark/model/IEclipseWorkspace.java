package org.emoflon.ibex.tgg.benchmark.model;

import java.nio.file.Path;
import java.util.List;

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
    public List<EclipseProject> getTGGProjects();
    
    /**
     * @return the plugin state location where preferences might be safed
     */
    public Path getPluginStateLocation();
}
