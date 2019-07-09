package org.emoflon.ibex.tgg.benchmark;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	// http://blog.vogella.com/2010/07/06/reading-resources-from-plugin/
//    @Inject @Named(E4Workbench.INSTANCE_LOCATION) private String instanceLocation;
	
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		System.out.println("TGG Activator");
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
