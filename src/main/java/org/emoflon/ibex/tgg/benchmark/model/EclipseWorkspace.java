package org.emoflon.ibex.tgg.benchmark.model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.json.JsonException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.emoflon.ibex.tgg.benchmark.Activator;
import org.emoflon.ibex.tgg.benchmark.Core;

/**
 * EclipseWorkspace represents an Eclipse workspace. It allows easy access to
 * TGG projects.
 *
 * @author Andre Lehmann
 */
public class EclipseWorkspace implements IEclipseWorkspace {

    private static final Logger LOG = LogManager.getLogger(Core.PLUGIN_NAME);
    private final Path location;
    private final IWorkspaceRoot workspaceRoot;

    /**
     * Constructor for {@link EclipseWorkspace}.
     */
    public EclipseWorkspace() {
        this.workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        this.location = Paths.get(workspaceRoot.getLocation().toOSString());
    }

    /** {@inheritDoc} */
    @Override
    public Path getLocation() {
        return location;
    }

    /** {@inheritDoc} */
    @Override
    public List<EclipseProject> getTGGProjects() {
        List<EclipseProject> tggProjects = new LinkedList<>();

        IProject[] projects = workspaceRoot.getProjects();
        for (IProject project : projects) {
            
            try {
                // TODO: get the correct project natures
                for (IProject referencedProjects : project.getReferencedProjects()) {
                    System.out.println(referencedProjects.getName());
                    for (String nature : referencedProjects.getDescription().getNatureIds()) {
                        System.out.println("  " + nature);
                    };
                }
                
                if (project.isOpen() && project.hasNature("org.emoflon.ibex.tgg.ide.nature")
                        && project.hasNature(JavaCore.NATURE_ID)) {
                    IJavaProject javaProject = JavaCore.create(project);
                    try {
                        LinkedList<Path> paths = new LinkedList<Path>();
                        paths.add(getOutputPath(javaProject));
                        for (IProject referencedProject : project.getReferencedProjects()) {
                            if (referencedProject.hasNature("org.emoflon.ibex.tgg.ide.nature")) {
                                paths.add(getOutputPath((IJavaProject) referencedProject));
                            }
                        }
                        
                        EclipseProject tggProject = new EclipseProject(project.getName(), Paths.get(project.getFullPath().toString()), paths.toArray(new Path[paths.size()]));
                        tggProject.loadPreferences();
                        tggProjects.add(tggProject);
                    } catch (JsonException | IOException e) {
                        // ignore, can't do anything about it
                    }
                }
            } catch (CoreException e) {
               // ignore, can't do anything about it
            }
        }

        return tggProjects;
    }
    
    private Path getOutputPath(IJavaProject javaProject) {
        try {
            return Paths.get(javaProject.getProject().getLocation().toOSString(), javaProject.getOutputLocation().removeFirstSegments(1).toString());
        } catch (JavaModelException e) {
            // fallback
            return Paths.get(javaProject.getProject().getLocation().toOSString(), "bin"); 
        }
    }

    /** {@inheritDoc} */
    @Override
    public Path getPluginStateLocation() {
        return Paths.get(Platform.getStateLocation(Activator.getInstance().getBundle()).toOSString());
    }
}
