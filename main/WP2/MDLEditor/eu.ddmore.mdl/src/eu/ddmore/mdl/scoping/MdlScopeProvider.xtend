/*
 * generated by Xtext
 */
package eu.ddmore.mdl.scoping

import eu.ddmore.mdl.mdl.Expression
import eu.ddmore.mdl.mdl.MclObject
import eu.ddmore.mdl.mdl.SymbolDefinition
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.scoping.IScope
import org.eclipse.xtext.scoping.Scopes
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider

/**
 * This class contains custom scoping description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation.html#scoping
 * on how and when to use it 
 *
 */
class MdlScopeProvider extends AbstractDeclarativeScopeProvider {

	def scope_SymbolReference_ref(Expression context, EReference reference){
		val retVal = context.eContainer.findDeclarationsInContext(context)
		retVal
	}

	def dispatch IScope findDeclarationsInContext(EObject container, EObject o) {
		container.eContainer.findDeclarationsInContext(o.eContainer)
	}

	def dispatch IScope findDeclarationsInContext(MclObject m, EObject o) {
		val retVal = Scopes::scopeFor(EcoreUtil2.getAllContentsOfType(m, SymbolDefinition))
		retVal
	}

}
