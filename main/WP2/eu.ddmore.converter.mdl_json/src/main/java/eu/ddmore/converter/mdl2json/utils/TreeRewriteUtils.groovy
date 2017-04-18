package eu.ddmore.converter.mdl2json.utils;

import eu.ddmore.mdl.mdl.Mcl
import org.eclipse.emf.ecore.util.EcoreUtil

class TreeRewriteUtils {
	

	def Mcl rewriteTree(Mcl orig){
		def mdlRoot = EcoreUtil.copy(orig)
		def vectArgR = new VectorAttributeRewrite()
		def cntr = 0
		def iter = mdlRoot.eAllContents
		while(iter.hasNext){
			val node = iter.next
			if(vectArgR.doSwitch(node) != null) cntr++	
		}
		mdlRoot
	}
	
	
}