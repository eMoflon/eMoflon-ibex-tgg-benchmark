package org.emoflon.ibex.tgg.benchmark.runner.operationalizations;


import java.io.IOException;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emoflon.ibex.tgg.operational.strategies.sync.SYNC;
import org.emoflon.ibex.tgg.benchmark.runner.util.PerformanceTestUtil;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;

public class SYNC_App extends SYNC {

	private final BenchmarkRunParameters runParameters;
	private final URLClassLoader classLoader;
	
	protected String instancePath;
	protected boolean isFwd;
	protected boolean isIncr;

	public SYNC_App(String projectName, String workspacePath, boolean debug, String instancePath, boolean isFwd, boolean isIncr) throws IOException {
		super(new IbexOptions().projectName(runParameters.getProjectName()).projectPath(runParameters.getProjectName())
                .workspacePath(runParameters.getWorkspacePath().toString()));

        this.runParameters = runParameters;
        this.classLoader = new URLClassLoader(runParameters.getClassPaths());

		this.instancePath = instancePath;
		this.isFwd = isFwd;
		this.isIncr = isIncr;
	}

	@Override
	protected void registerUserMetamodels() throws IOException {
		new PerformanceTestUtil().registerUserMetamodels(options.projectPath(), rs, this);
		
		// Register correspondence metamodel last
		loadAndRegisterCorrMetamodel(options.projectPath() + "/model/" + options.projectName() + ".ecore");
	}
	
	/** 
	 * Load the models for the operationalization. For incremental synchronization,
	 * all models must be loaded. For batch translation, the protocol, corr
	 * and either src or trg model have to be created, depending on the direction.
	 *  */
	@Override
	public void loadModels() throws IOException {
		Path instPath = runParameters.getModelInstancesPath();
        // s = createResource(instPath.resolve("src.xmi").toString());
        // t = createResource(instPath.resolve("trg.xmi").toString());
        // c = createResource(instPath.resolve("corr.xmi").toString());
        // p = createResource(instPath.resolve("protocol.xmi").toString());

		if (isIncr) {
			s = loadResource(instancePath + "/src.xmi");
			t = loadResource(instancePath + "/trg.xmi");
			c = loadResource(instancePath + "/corr.xmi");
			p = loadResource(instancePath + "/protocol.xmi");
		} else {
			if (isFwd) {
				s = loadResource(instancePath + "/src.xmi");
				t = createResource(instancePath + "/trg.xmi");
			} else {
				s = createResource(instancePath + "/src.xmi");
				t = loadResource(instancePath + "/trg.xmi");
			}
			c = createResource(instancePath + "/corr.xmi");
			p = createResource(instancePath + "/protocol.xmi");
		}
		
		EcoreUtil.resolveAll(rs);
	}
	
	@Override
	public void saveModels() {
		// Models needn't be saved for all Operationalizations except MODELGEN
	}
	
	protected static IbexOptions createIbexOptions() {
		IbexOptions options = new IbexOptions();
		return options;
	}
}
