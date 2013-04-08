/*
 * generated by Xtext
 */
package org.ddmore.mdl.ui;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;

import com.google.inject.Injector;

import org.ddmore.mdl.ui.internal.MdlActivator;

/**
 * This class was generated. Customizations should only happen in a newly
 * introduced subclass. 
 */
public class MdlExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return MdlActivator.getInstance().getBundle();
	}
	
	@Override
	protected Injector getInjector() {
		return MdlActivator.getInstance().getInjector(MdlActivator.ORG_DDMORE_MDL_MDL);
	}
	
}