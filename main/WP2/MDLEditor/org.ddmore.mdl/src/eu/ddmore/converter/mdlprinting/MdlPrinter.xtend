/*
 * MDL converter toolbox
 * @DDMoRe
 * Author: Natallia Kokash, LIACS, 2012
 */
package eu.ddmore.converter.mdlprinting

import org.ddmore.mdl.mdl.Mcl
import org.ddmore.mdl.mdl.FunctionCall
import org.ddmore.mdl.mdl.SymbolDeclaration
import org.ddmore.mdl.mdl.RandomVariable
import org.ddmore.mdl.mdl.Block
import org.ddmore.mdl.mdl.BlockStatement
import org.ddmore.mdl.mdl.List
import org.ddmore.mdl.mdl.RandomList
import org.ddmore.mdl.mdl.OdeList
import org.ddmore.mdl.mdl.ModelObject
import org.ddmore.mdl.mdl.Primary
import org.ddmore.mdl.mdl.ConditionalStatement
import org.ddmore.mdl.mdl.Expression
import org.ddmore.mdl.mdl.AnyExpression
import org.ddmore.mdl.mdl.OrExpression
import org.ddmore.mdl.mdl.AndExpression
import org.ddmore.mdl.mdl.LogicalExpression
import org.ddmore.mdl.mdl.ConditionalExpression
import org.ddmore.mdl.mdl.AdditiveExpression
import org.ddmore.mdl.mdl.MultiplicativeExpression
import org.ddmore.mdl.mdl.PowerExpression
import org.ddmore.mdl.mdl.UnaryExpression
import org.ddmore.mdl.mdl.ParExpression
import org.ddmore.mdl.mdl.Arguments
import org.ddmore.mdl.mdl.DistributionArguments
import org.ddmore.mdl.mdl.DistributionArgument
import org.ddmore.mdl.mdl.TargetBlock
import org.ddmore.mdl.mdl.FullyQualifiedSymbolName
import org.ddmore.mdl.mdl.Vector
import org.ddmore.mdl.mdl.SymbolModification
import org.ddmore.mdl.mdl.ObservationBlock
import org.ddmore.mdl.mdl.EnumType
import org.ddmore.mdl.mdl.Distribution
import org.ddmore.mdl.mdl.UseType
import org.ddmore.mdl.mdl.FullyQualifiedArgumentName
import org.ddmore.mdl.mdl.Selector
import org.ddmore.mdl.mdl.ParameterObject
import org.ddmore.mdl.mdl.EstimateTask
import org.ddmore.mdl.mdl.SimulateTask
import org.ddmore.mdl.mdl.DataObject
import org.ddmore.mdl.mdl.FormalArguments
import org.ddmore.mdl.mdl.SimulationBlock
import org.ddmore.mdl.mdl.EstimationBlock
import org.ddmore.mdl.mdl.FileBlockStatement
import org.apache.commons.io.FilenameUtils


class MdlPrinter {
	
	//Get MDL file name
	def fileName(Mcl m){
		var fileName = m.eResource.getURI().path;
		var baseName = FilenameUtils::getBaseName(fileName);
		return baseName;
	}	
	
	def isTrue(AnyExpression e){
		if (e.expression != null){
			if (e.expression.conditionalExpression.expression != null){
				val orExpr = e.expression.conditionalExpression.expression;
				val andExpr = orExpr.expression.get(0);
				val logicalExpr = andExpr.expression.get(0);	
				if (logicalExpr.boolean != null){	
					if ((logicalExpr.negation == null) && logicalExpr.boolean.equals("true")) return true;
					if ((logicalExpr.negation != null) && logicalExpr.boolean.equals("false")) return true;
				}
			}
		}
		return (e.toStr.equalsIgnoreCase("yes") || e.toStr.equalsIgnoreCase("true") || e.toStr.equals("1"));
	}	
	
	def isAttributeTrue(Arguments a, String attrName){
		for (arg: a.arguments)
			if (arg.argumentName.name.equals(attrName)){
				return arg.expression.isTrue;
			}
		return false;
	}
	
	//Return value of an attribute with a given name
	def getAttribute(DistributionArguments a, String attrName){
		for (arg: a.arguments)
			if (arg.argumentName != null && arg.argumentName.name.equals(attrName)){
				return arg.valueToStr;
			}				
		return "";
	} 
	
	def findAttribute(DistributionArguments a, String attrName){
		for (arg: a.arguments)
			if (arg.argumentName != null && arg.argumentName.name.equals(attrName)){
				return arg;
			}				
		return null;
	}
	
	def String valueToStr(DistributionArgument arg){
		if (arg.distribution != null)
			return arg.distribution.identifier;
		if (arg.value != null)
			return arg.value.toStr;	
		if (arg.component != null)
			return arg.component.toStr;
		return "";	
	}	
	
	//Return value of an attribute with a given name
	def getAttribute(Arguments a, String attrName){
		for (arg: a.arguments)
			if (arg.argumentName != null && arg.argumentName.name.equals(attrName))
				return arg.expression.toStr
		return "";
	}	
	
	//Return value of an attribute with a given name
	def getAttributeExpression(Arguments a, String attrName){
		for (arg: a.arguments)
			if (arg.argumentName != null && arg.argumentName.name.equals(attrName))
				return arg.expression
		return null;
	}	
	
	//Find reference to a data file 
	public def getDataSource(Mcl m){
		for (obj: m.objects){
			if (obj.dataObject != null){
				for (b: obj.dataObject.blocks){
					if (b.fileBlock != null){
						for (s: b.fileBlock.statements){
							return s.getDataSource;
						}
					} 
				}
			}
		}
		return "";
	}
	
	def getDataSource(FileBlockStatement s){
		if (s.variable != null){
			if (s.variable.symbolName.name.equals("data")){
				if (s.variable.expression != null){
					if (s.variable.expression.list != null)
						return s.variable.expression.list.arguments.getAttribute("source");
				}
			}
		}
		return "";
	}
	
	
	///////////////////////////////////////////////////////////////////////////////
	//Check whether MDL blocks are defined and non empty
	///////////////////////////////////////////////////////////////////////////////
	//Check that HEADER block is not empty
	def isHeaderDefined(DataObject obj){
		for (b:obj.blocks){
			if (b.dataInputBlock != null){
				if (b.dataInputBlock.variables.size > 0){
				 	return true;
				}
			}
		}
		return false;
	}

    //Check that PRIOR block is not empty
	def isPriorDefined(ParameterObject obj){
		for (b:obj.blocks){
			if (b.priorBlock != null){
				if (b.priorBlock.statements.size > 0)
				 	return true;
			}
		}
		return false;
	}
	
	//Check that STRUCTURAL is not empty
	def isStructuralDefined(ParameterObject obj){
		for (b:obj.blocks){
			if (b.structuralBlock != null){
				if (b.structuralBlock.parameters.size > 0)
				 	return true;
			}
		}
		return false;
	}

	//Check that VARIABILITY block or its subblocks are not empty
	def isVariabilityDefined(ParameterObject obj){
		for (b:obj.blocks){
			if (b.variabilityBlock != null){
				if (b.variabilityBlock.statements.size > 0){
				 	return true;
				}
			}
		}
		return false;
	}

	//Check that VARIABILITY subblocks are not empty
	def isVariabilitySubBlocksDefined(ParameterObject obj){
		for (b:obj.blocks){
			if (b.variabilityBlock != null){
				for (bb: b.variabilityBlock.statements){
					if ((bb.diagBlock != null) || (bb.matrixBlock != null) || (bb.sameBlock != null))
				 		return true;
				}
			}
		}
		return false;
	}
	
	//Return true if there are definitions in MODEL PREDICTION or OBSERVATION blocks,
	def isErrorDefined(ModelObject o){
    	for (mob:o.blocks){
	    	if (mob.modelPredictionBlock != null){
				for (s: mob.modelPredictionBlock.statements){
					if (s.statement != null) return true;
				}
	    	}
	    	if (mob.observationBlock != null){
				if (mob.observationBlock.statements.size > 0){
					return true;
				}
	    	}
	    	if (mob.simulationBlock != null){
				if (mob.simulationBlock.statements.size > 0){
					return true;
				}
	    	}
	    	if (mob.estimationBlock != null){
				if (mob.estimationBlock.statements.size > 0){
					return true;
				}
	    	}	    	
		}
    	return false;
    } 
	
    //Check if there are definitions in ODE block
    def isODEDefined(ModelObject o){
		for (mob: o.blocks){
			if (mob.modelPredictionBlock != null){
				var b = mob.modelPredictionBlock;
		    	for (s: b.statements){
		    		if (s.odeBlock != null) return true;
		    	}
		    }
		}
    	return false;
    }

	//Check if GROUP or INDIVIDUAL variables are defined
	def isGroupOrIndividualDefined(ModelObject o){
		for (mob: o.blocks){
			if (mob.groupVariablesBlock != null){
				if (mob.groupVariablesBlock != null){
					for (st: mob.groupVariablesBlock.statements)
						if (st.statement != null)
							return true;
				}
			}
			if (mob.individualVariablesBlock != null){
				if (mob.individualVariablesBlock.statements.size > 0){
					return true;
				}
			}
		}
		return false;
	}
	
	//Check if MIXTURE block is defined 
	def isMixDefined(ModelObject o){
		for (mob: o.blocks){
			if (mob.groupVariablesBlock != null){
				if (mob.groupVariablesBlock != null){
					for (st: mob.groupVariablesBlock.statements){
						if (st.mixtureBlock != null){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

    //Check if LIBRARY block is defined
    def isLibraryDefined(ModelObject o){
    	for (mob: o.blocks){
			if (mob.modelPredictionBlock != null){
				for (s: mob.modelPredictionBlock.statements){
				    if (s.libraryBlock != null) return true;
				}
			}
    	}
    	return false;
    }
    
    //Check if LIBRARY block is defined
    def getNumberOfCompartments(ModelObject o){
    	for (mob: o.blocks){
			if (mob.modelPredictionBlock != null){
				for (s: mob.modelPredictionBlock.statements){
				    if (s.libraryBlock != null) {
				    	for (ss: s.libraryBlock.statements){
				    		var nmct = ss.expression.arguments.getAttribute("ncmt");
				    		if (!nmct.equals("")) {
				    			try{
						    		return Integer::parseInt(nmct);
				    			}
				    			catch(NumberFormatException e){
				    				return 0;		
				    			}
				    		}
				    	}
				    }
				}
			}
    	}
    	return 0;
    }
    
    
    //Check if OUTPUT_VARIABLES block is defined and has content
	def isOutputVariablesDefined(ModelObject o){
		for (b: o.blocks){
			if ( b.outputVariablesBlock != null){
				if ( b.outputVariablesBlock.variables.size > 0) return true;
			}
		}
		return false;
	}
	
	//Check if attribute cov is define in ESTIMATE block
	def isCovarianceDefined(EstimateTask b){
		for (s: b.statements)
			if (s.symbol != null){
				if (s.symbol.symbolName.name.equals("cov"))
					if (s.symbol.expression != null) return true;
			}
		return false;		
	}
	
 
    //Check whether there is a target block in a list of block statements			
    def isInlineTargetDefined(String targetName, EstimateTask task){
		for (s: task.statements){
			if (s.targetBlock != null){
				val target = s.targetBlock.arguments.getAttribute("target");
		 		if (target != null) 
					if (target.equals(targetName)) {
						return true;
					}
			}
		}
		return false;
	}
	
	 //Check whether there is a target block in a list of block statements			
    def isInlineTargetDefined(String targetName, SimulateTask task){
		for (s: task.statements){
			if (s.targetBlock != null){
				val target = s.targetBlock.arguments.getAttribute("target");
		 		if (target != null) 
					if (target.equals(targetName)) {
						return true;
					}
			}
		}
		return false;
	}
	
	/////////////////////////////////////////////////////////////

    //Map variableDeclaration identifiers, identity for MDL printing, to be rewritten in super classes 
	def convertID(String id){
		return id;
	}
	
	//Map operators, identity for MDL printing, to be rewritten in super classes 
	def convertOperator(String op){
		return op;
	}
	
	def toStr(FullyQualifiedSymbolName name){
		var res = ""; 
		if (name.object != null){
			res  = name.object.name + "$" ;
		}
		res = res + name.symbol.name.convertID;
		return res;
	}
	
	def toStr(SymbolDeclaration v){
		var res = "";
		if (v.symbolName.name != null) {
			res = res + v.symbolName.name.convertID;
		}
		var expr = ""; //First make sure that expression is not empty, than print "=" 
		if (v.expression != null){
			expr = v.expression.toStr;
			if (!expr.trim().equals(""))
				res = res + " = " + expr;
		}
		if (v.randomList != null){
			expr = v.randomList.toStr;
			if (!expr.trim().equals(""))
				res = res + expr;
		}
		return res;
	}

	def toStr(RandomVariable v){
		var res = "";
		if (v.functionName != null){
			res = res + v.functionName.name.convertID + '('      
		}
		if (v.symbolName != null) {     
			res = res + v.symbolName.name.convertID;
		}
		if (v.functionName != null){
			res = res + ')' 
		}
		res = res + " = " + v.randomList.toStr;
		return res;
	}	

	def toStr(SymbolModification v){
		var res = "";
		if (v.identifier != null){
			v.identifier.toStr;
		}
		if (v.list != null){	
			res = res + " = " + v.list.toStr;
		}
		return res;
	}	
	 
	def String toStr(AnyExpression e){
		var res = "";
		if (e.expression != null){
			res = res + e.expression.toStr;
		}
		if (e.list != null){
			res  = res + e.list.toStr;
		}
		if (e.odeList != null){
			res = res + e.odeList.toStr;
		}
		if (e.type != null){
			res = res + e.type.toStr;
		}
		if (e.vector != null){
			res = res + e.vector.toStr;
		}
		return res;
	}
	
	def toStr(EnumType type) {
		if (type.categorical != null){
			var res = "";
			if (type.categorical.arguments != null){
				res = type.categorical.arguments.toStr;
			}
			return type.categorical.identifier + '(' + res + ')'
		} 
		if (type.continuous != null){
			return type.continuous.identifier
		} 
		if (type.use != null){
			return type.use.toStr
		} 
		if (type.likelihood != null){
			return type.likelihood.identifier	
		} 
		if (type.target != null){
			return type.target.identifier 
		} 
	}
	
	
	def String toStr(Distribution d) { 
		return d.identifier
	}

	def String toStr(UseType l) { 
		return l.identifier
	}
	
	def toStr(RandomList l){
		if (l.arguments != null){
			return "~" + "(" + l.arguments.toStr + ")";
		}
		return "";
	}

	def toStr(List l){
		if (l.arguments != null){
			return "list" + "(" + l.arguments.toStr + ")";
		}
		return "";
	}
	
	def toStr(OdeList l){
		if (l.arguments != null){ 
			return "ode" + "(" + l.arguments.toStr + ")"
		}
	}
	
	def String toStr(Expression e){
		return e.conditionalExpression.toStr
	}
	
	def toStr(ConditionalExpression e){ 
		var res = e.expression.toStr;
		if (e.expression1 != null){
			res  = res + "?" + e.expression1.toStr + ":" + e.expression2.toStr
		}
		return res;
	}

	def toStr(OrExpression e){
		var res = "";
		var iterator = e.expression.iterator();
		var operatorIterator = e.operator.iterator();
		if (iterator.hasNext ) {
			res = iterator.next.toStr;
		}
		while (iterator.hasNext && operatorIterator.hasNext){
			res  = res + operatorIterator.next.convertOperator + iterator.next.toStr;
		}
		return res;
	}
	
	def toStr(AndExpression e){
		var res = "";
		var iterator = e.expression.iterator();
		var operatorIterator = e.operator.iterator();
		if (iterator.hasNext ) {
			res = iterator.next.toStr;
		}
		while (iterator.hasNext && operatorIterator.hasNext){
			res  = res + operatorIterator.next.convertOperator + iterator.next.toStr;
		}
		return res;	
	}
	
	//(==, !=, <, > etc.)
	def toStr(LogicalExpression e){
		var res = "";
		if (e.negation != null){
			res = res + e.negation;
		}
		if (e.boolean != null){
			res = res + e.boolean.toString;
		}
		if (e.expression1 != null){
			res  = res + e.expression1.toStr;
			if (e.expression2 != null){
				res = res + e.operator.convertOperator + e.expression2.toStr;
			}
		}
		return res;
	}
	
	def toStr(AdditiveExpression e){
		var res  = "";
		if (e.expression != null)
		{ 
			var iterator = e.expression.iterator();
			var operatorIterator  = e.operator.iterator();
			if (iterator.hasNext ) {
				res = iterator.next.toStr;
			}
			while (iterator.hasNext && operatorIterator.hasNext){
				res  = res + operatorIterator.next.convertOperator + iterator.next.toStr;
			}
		}
		if (e.string != null){		
			res  = e.string; 
		}
		return res;
	}
	
	def toStr(MultiplicativeExpression e){
		var res = "";
		var iterator = e.expression.iterator();
		var operatorIterator  = e.operator.iterator();
		if (iterator.hasNext ) {
			res = iterator.next.toStr;
		}
		while (iterator.hasNext){
			res  = res + operatorIterator.next.convertOperator + iterator.next.toStr;
		}
		return res;
	}
	
	def String toStr(PowerExpression e) { 
		var res = "";
		var iterator = e.expression.iterator();
		var operatorIterator  = e.operator.iterator();
		if (iterator.hasNext ) {
			res = iterator.next.toStr;
		}
		while (iterator.hasNext){
			res  = res + operatorIterator.next.convertOperator + iterator.next.toStr;
		}
		return res;
	}
	
	def String toStr(UnaryExpression e){
		var res = "";
		if (e.expression != null){
			res = res + e.operator.convertOperator + e.expression.toStr
		}
		if (e.parExpression != null){
			res = res + e.parExpression.toStr;
		}
		if (e.number != null){
			return e.number;
		}
		if (e.symbol != null){
			return e.symbol.toStr; 
		}
		if (e.functionCall != null) {
			return e.functionCall.toStr
		}
		if (e.attribute != null) {
			return e.attribute.toStr
		}
		return res;
	}	
	
	def toStr(FunctionCall call){
		return call.identifier.toStr + "(" + call.arguments.toStr + ")";
	}
	
	def String toStr(Primary p){
		if (p.number != null){
			return  p.number;
		}
		if (p.symbol != null){
			return p.symbol.toStr; 
		}
		if (p.vector != null) {
			return p.vector.toStr
		}
	}
	
	def toStr(FullyQualifiedArgumentName name) { 
		var res = name.parent.toStr;
		for (s: name.selectors){
			res = res + s.toStr
		}
		return res;
	}
	
	def toStr(Selector s) { 
		if (s.argumentName != null)
			return "." + s.argumentName.name;
		if (s.selector != null)
			return "[" + s.selector + "]";
	}
	
	def toStr(Vector v) { 
		var res  = v.identifier + '(';
		var iterator = v.values.iterator();
		if (iterator.hasNext) {
			res = res + iterator.next.toStr;
		}
		while (iterator.hasNext){
			res  = res + ', ';
			res = res + iterator.next.toStr;
		}
		return res + ')';
	}
	
	def toStr(ParExpression e){
		return "(" + e.expression.toStr + ")";
	}
	
	def toStr(Arguments arg){
		var res  = "";
		var iterator = arg.arguments.iterator();
		if (iterator.hasNext ) {
			var a = iterator.next; 
			if (a.argumentName != null){
				res  = res + a.argumentName.name + " = ";
			}
			if (a.expression != null){
				res = res + a.expression.toStr;
			}
		}
		while (iterator.hasNext){
			res  = res + ', ';
			var a = iterator.next; 
			if (a.argumentName != null){
				res  = res + a.argumentName.name + " = ";
			}
			if (a.expression != null){
				res = res + a.expression.toStr;
			}
		}
		return res;
	}
	
	def toStr(DistributionArguments arg){
		var res  = "";
		var iterator = arg.arguments.iterator();
		if (iterator.hasNext ) {
			var a = iterator.next; 
			if (a.argumentName != null){
				res  = res + a.argumentName.name + " = ";
			}
			res = res + a.valueToStr;
		}
		while (iterator.hasNext){
			res  = res + ', ';
			var a = iterator.next; 
			if (a.argumentName != null){
				res  = res + a.argumentName.name + " = ";
			}
			res  = res + a.valueToStr;
		}
		return res;
	}			
	
	def toStr(FormalArguments arg) { 
		var res  = "";
		var iterator = arg.arguments.iterator();
		if (iterator.hasNext ) {
			var a = iterator.next; 
			if (a.name != null){
				res  = res + a.name;
			}
		}
		while (iterator.hasNext){
			res  = res + ', ';
			var a = iterator.next; 
			if (a.name != null){
				res  = res + a.name;
			}
		}
		return res;
	}
	
	
	def toStr(TargetBlock b){
		return b.externalCode.substring(3, b.externalCode.length - 3)
	}

	//////////////////////////////////////////////////////////////////////////
	//Printing
    //////////////////////////////////////////////////////////////////////////	
	 
	def print(TargetBlock b)'''
	«IF b.externalCode != null»
		«var printedCode = b.externalCode.substring(3, b.externalCode.length - 3)»
		«printedCode»
	«ENDIF»
	'''	
	
	def String print(Block b)'''
		«FOR st: b.statements»
			«st.print»
		«ENDFOR»
	'''
	
	def print(ObservationBlock b)'''
		«FOR s: b.statements»
			«s.print»
		«ENDFOR»
	'''		
	
	def print(SimulationBlock b)'''
		«FOR s: b.statements»
			«s.print»
		«ENDFOR»
	'''	

	def print(EstimationBlock b)'''
		«FOR s: b.statements»
			«s.print»
		«ENDFOR»
	'''	
		
	def String print(BlockStatement st)'''
		«IF st.symbol != null»«st.symbol.print»«ENDIF»
		«IF st.functionCall != null»«st.functionCall.print»«ENDIF»
		«IF st.statement != null»«st.statement.print»«ENDIF»
		«IF st.targetBlock != null»«st.targetBlock.print»«ENDIF»
		'''

	def print(ConditionalStatement s)'''
		«IF s.expression != null»
			if «s.expression.print»
				«IF s.ifStatement != null»
					«s.ifStatement.print»
				«ENDIF»
				«IF s.ifBlock != null»
					«s.ifBlock.print»
				«ENDIF»
			«IF s.elseStatement != null || s.elseBlock != null»
			else 
				«IF s.elseStatement != null»
					«s.elseStatement.print»
				«ENDIF»
				«IF s.elseBlock != null»
					«s.elseBlock.print»
				«ENDIF»
			«ENDIF»
		«ENDIF»
	'''
	
	def print(FunctionCall call)'''«call.toStr»'''
		
    def print(SymbolDeclaration v)'''«v.toStr»'''

	def print(RandomVariable v)'''«v.toStr»'''

    def print(SymbolModification v)'''«v.toStr»'''

	def print(AnyExpression e)'''«e.toStr»'''
		
	def print(RandomList l)'''«l.toStr»'''

	def print(List l)'''«l.toStr»'''
	
	def print(OdeList l)'''«l.toStr»'''

	def print(Expression e)'''«e.toStr»'''
	
	def print(ConditionalExpression e)'''«e.toStr»'''

	def print(OrExpression e)'''«e.toStr»'''
	
	def print(AndExpression e)'''«e.toStr»'''
	
	def print(LogicalExpression e)'''«e.toStr»'''
	
	def print(AdditiveExpression e)'''«e.toStr»'''
	
	def print(MultiplicativeExpression e)'''«e.toStr»'''
	
	def print(PowerExpression e)'''«e.toStr»'''
	
	def print(UnaryExpression e)'''«e.toStr»'''

	def print(Primary p)'''«p.toStr»'''
	
	def print(ParExpression e)'''«e.toStr»'''
	
	def print(Arguments arg)'''«arg.toStr»'''
}