/*
* generated by Xtext
*/
package eu.ddmore.mdl;

import org.eclipse.emf.ecore.EPackage;

import com.google.inject.Injector;

import eu.ddmore.mdl.mdl.MdlPackage;
import eu.ddmore.mdl.mdllib.mdllib.MdlLibPackage;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class MdlStandaloneSetup extends MdlStandaloneSetupGenerated{

	public static void doSetup() {
		new MdlStandaloneSetup().createInjectorAndDoEMFRegistration();
	}


    @Override
    public void register(Injector injector) {
        if (!EPackage.Registry.INSTANCE.containsKey("http://eu.ddmore.mdl.mdllib/mdl/mdllib/MdlLib")) {
            EPackage.Registry.INSTANCE.put("http://eu.ddmore.mdl.mdllib/mdl/mdllib/MdlLib", MdlLibPackage.eINSTANCE);
        }
        super.register(injector);
    }

}

