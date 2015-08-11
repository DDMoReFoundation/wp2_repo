package eu.ddmore.mdl.utils

import eu.ddmore.mdl.mdl.AttributeList
import eu.ddmore.mdl.mdl.BlockBody
import eu.ddmore.mdl.mdl.BlockStatement
import eu.ddmore.mdl.mdl.BlockStatementBody
import eu.ddmore.mdl.mdl.BlockTextBody
import eu.ddmore.mdl.mdl.ValuePair
import org.eclipse.xtext.EcoreUtil2

class DomainObjectModelUtils {
	
	static def getStatements(BlockStatement it){
		(body as BlockStatementBody).statements
	} 
	
	static def getBlockText(BlockStatement it){
		(body as BlockTextBody).text
	} 

	static def getParentOfBlockStatement(BlockStatement it){
		if(eContainer instanceof BlockBody)	eContainer.eContainer
		else eContainer
	}
	
	static def getParentStatement(AttributeList it){
		EcoreUtil2.getContainerOfType(it, BlockStatement)
//		eContainer.eContainer.eContainer as BlockStatement
	}

	static def getParentBlock(ValuePair it){
		EcoreUtil2.getContainerOfType(it, BlockStatement)
//		eContainer.eContainer.eContainer.eContainer as BlockStatement
	}
	
	static def getParentList(ValuePair it){
		EcoreUtil2.getContainerOfType(it, AttributeList)
//		eContainer as AttributeList
	}
	
}