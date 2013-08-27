/*
 * generated by Xtext
 */
package org.ddmore.mdl.generator

import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.IGenerator
import org.eclipse.xtext.generator.IFileSystemAccess
import org.ddmore.mdl.mdl.Mcl

class MdlGenerator extends Mdl2PharmML implements IGenerator{

 	override void doGenerate(Resource resource, IFileSystemAccess fsa) {
 		for(m: resource.allContents.toIterable.filter(typeof(Mcl))) {
			fsa.generateFile(
				resource.fileName + ".ctl", m.convertToNMTRAN)
			//fsa.generateFile(
			//	resource.fileName + ".xml", m.convertToPharmML)	
		}
	}
	
	def void doGenerateNMTRAN(Resource resource, IFileSystemAccess fsa) {
 		for(m: resource.allContents.toIterable.filter(typeof(Mcl))) {
			fsa.generateFile(
				resource.fileName + ".ctl", m.convertToNMTRAN)
		}
	}
	
	def void doGeneratePharmML(Resource resource, IFileSystemAccess fsa) {
 		for(m: resource.allContents.toIterable.filter(typeof(Mcl))) {
			fsa.generateFile(
				resource.fileName + ".xml", m.convertToPharmML)	
		}
	}
}
