/*
 
* generated by Xtext
 */
package eu.ddmore.mdl.ui.contentassist

import eu.ddmore.mdl.mdl.BlockStatement
import eu.ddmore.mdl.mdl.EnumPair
import eu.ddmore.mdl.mdl.ListDefinition
import eu.ddmore.mdl.type.MclTypeProvider.BuiltinEnumTypeInfo
import eu.ddmore.mdl.validation.ListDefinitionProvider
import java.util.ArrayList
import java.util.List
import org.eclipse.emf.ecore.EObject
import org.eclipse.jface.viewers.StyledString
import org.eclipse.swt.graphics.Image
import org.eclipse.xtext.Assignment
import org.eclipse.xtext.RuleCall
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor

import static extension org.eclipse.xtext.EcoreUtil2.*

/**
 * see http://www.eclipse.org/Xtext/documentation.html#contentAssist on how to customize content assistant
 */
class MdlProposalProvider extends AbstractMdlProposalProvider {

	extension ListDefinitionProvider listHelper = new ListDefinitionProvider

	override complete_IS(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		val props = #['is']
		addProposals(context, acceptor, props, null)
	}
	
	
	override complete_ASSIGN(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		val props = #['=']
		addProposals(context, acceptor, props, null)
	}


	override completeEnumPair_Expression(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		val parent = model.getContainerOfType(ListDefinition)
		if(parent != null){
			val parentBlock = model.getContainerOfType(BlockStatement)
			val enumType = getTypeOfEnumAttribute(parentBlock.identifier, (model as EnumPair).argumentName)
			val attributes = new ArrayList<String>
			if(enumType instanceof BuiltinEnumTypeInfo){
				attributes.addAll((enumType as BuiltinEnumTypeInfo).expectedValues)
			}
			addProposals(context, acceptor, attributes, null);
		}
	}
	
	def addProposals(ContentAssistContext context, ICompletionProposalAcceptor acceptor, List<String> attributes, Image img){
		for (String proposal: attributes){
			val displayedString = new StyledString();
			displayedString.append(proposal);
			val p = doCreateProposal(proposal, displayedString, img, 1000, context);
			acceptor.accept(p);
		}
	}
}