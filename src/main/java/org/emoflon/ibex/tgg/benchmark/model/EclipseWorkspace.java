package org.emoflon.ibex.tgg.benchmark.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

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

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * EclipseWorkspace represents an Eclipse workspace. It allows easy access to
 * TGG projects.
 *
 * @author Andre Lehmann
 */
public class EclipseWorkspace implements IEclipseWorkspace {

    private static final Logger LOG = LogManager.getLogger(Core.PLUGIN_NAME);

    private final ObjectProperty<Path> location;
    private final IWorkspaceRoot workspaceRoot;
    private final ListProperty<EclipseTggProject> tggProjects;

    /**
     * Constructor for {@link EclipseWorkspace}.
     */
    public EclipseWorkspace() {
        this.workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        this.location = new SimpleObjectProperty<Path>(Paths.get(workspaceRoot.getLocation().toOSString()));
        this.tggProjects = new SimpleListProperty<EclipseTggProject>();
    }

    private EclipseJavaProject getJavaProject(IJavaProject javaProject) {
        IProject project = javaProject.getProject();
        Path projectPath = Paths.get(project.getFullPath().toString());
        Path outputPath = getJavaProjectOutputPath(javaProject);

        Set<EclipseJavaProject> referencedProjects = new HashSet<EclipseJavaProject>();
        try {
            for (IProject referencedProject : project.getReferencedProjects()) {
                if (referencedProject.hasNature(JavaCore.NATURE_ID)) {
                    referencedProjects.add(getJavaProject((IJavaProject) referencedProject));
                }
            }
        } catch (CoreException e) {
            // ignore, can't do anything about it
        }
        return new EclipseJavaProject(project.getName(), projectPath, outputPath, referencedProjects);
    }

    public void addTggProject(IJavaProject tggProject) {
        IProject project = tggProject.getProject();
        Path projectPath = Paths.get(project.getFullPath().toString());
        Path outputPath = getJavaProjectOutputPath(tggProject);

        Set<EclipseJavaProject> referencedProjects = new HashSet<EclipseJavaProject>();
        try {
            for (IProject referencedProject : project.getReferencedProjects()) {
                if (referencedProject.hasNature(JavaCore.NATURE_ID)) {
                    referencedProjects.add(getJavaProject((IJavaProject) referencedProject));
                }
            }
            this.tggProjectsProperty().get()
                    .add(new EclipseTggProject(project.getName(), projectPath, outputPath, referencedProjects));
        } catch (CoreException e) {
            // ignore, can't do anything about it
        }
    }

    private Path getJavaProjectOutputPath(IJavaProject javaProject) {
        try {
            return Paths.get(javaProject.getProject().getLocation().toOSString(),
                    javaProject.getOutputLocation().removeFirstSegments(1).toString());
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
    
    /** {@inheritDoc} */
    @Override
    public Path getPluginPreferencesFilePath() {
        return getPluginStateLocation().resolve("tgg-benchmark.json");
    }

    public final ListProperty<EclipseTggProject> tggProjectsProperty() {
        return this.tggProjects;
    }

    @Override
    public final ObservableList<EclipseTggProject> getTggProjects() {
        ObservableList<EclipseTggProject> tggProjects = this.tggProjectsProperty().get();
        if (tggProjects == null) {
            tggProjects = FXCollections.observableArrayList();
            for (IProject project : workspaceRoot.getProjects()) {
                try {
                    if (project.isOpen() && project.hasNature("org.emoflon.ibex.tgg.ide.nature")
                            && project.hasNature(JavaCore.NATURE_ID)) {
                        addTggProject((IJavaProject) project);
                    }
                } catch (CoreException e) {
                    // ignore, can't do anything about it
                }
            }
        }

        return tggProjects;
    }

    public final ObjectProperty<Path> locationProperty() {
        return this.location;
    }

    @Override
    public final Path getLocation() {
        return this.locationProperty().get();
    }
}
