/*
* generated by Xtext
*/
package eu.ddmore.pml;

import org.eclipse.xtext.junit4.IInjectorProvider;

import com.google.inject.Injector;

public class PharmMLUiInjectorProvider implements IInjectorProvider {
	
	public Injector getInjector() {
		return eu.ddmore.pml.ui.internal.PharmMLActivator.getInstance().getInjector("eu.ddmore.pml.PharmML");
	}
	
}