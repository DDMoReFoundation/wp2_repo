/*
 * MDL converter toolbox
 * @DDMoRe
 * Author: Natallia Kokash, LIACS, 2012
 */
package eu.ddmore.converter.mdl2nonmem

import java.util.ArrayList
import org.ddmore.mdl.mdl.BlockStatement
import org.ddmore.mdl.mdl.DataBlockStatement
import org.ddmore.mdl.mdl.DataObject
import org.ddmore.mdl.mdl.DiagBlock
import org.ddmore.mdl.mdl.EstimateTask
import org.ddmore.mdl.mdl.ExecuteTask
import org.ddmore.mdl.mdl.FileBlock
import org.ddmore.mdl.mdl.FileBlockStatement
import org.ddmore.mdl.mdl.FullyQualifiedSymbolName
import org.ddmore.mdl.mdl.MatrixBlock
import org.ddmore.mdl.mdl.Mcl
import org.ddmore.mdl.mdl.MixtureBlock
import org.ddmore.mdl.mdl.ModelObject
import org.ddmore.mdl.mdl.ModelPredictionBlock
import org.ddmore.mdl.mdl.ParameterDeclaration
import org.ddmore.mdl.mdl.ParameterObject
import org.ddmore.mdl.mdl.SameBlock
import org.ddmore.mdl.mdl.SimulateTask
import org.ddmore.mdl.mdl.SymbolDeclaration
import org.ddmore.mdl.mdl.TaskObject
import org.ddmore.mdl.mdl.TaskObjectBlock
import java.util.HashMap
import org.ddmore.mdl.mdl.ModelObjectBlock
import org.ddmore.mdl.mdl.ParameterObjectBlock
import org.ddmore.mdl.mdl.ImportBlock
import org.ddmore.mdl.mdl.DataObjectBlock
import org.ddmore.mdl.mdl.TargetBlock
import java.util.HashSet
import org.ddmore.mdl.mdl.ImportedFunction
import org.ddmore.mdl.mdl.Argument
import eu.ddmore.converter.mdlprinting.MdlPrinter
import org.ddmore.mdl.mdl.FullyQualifiedArgumentName
import org.ddmore.mdl.mdl.FunctionCall
import org.ddmore.mdl.mdl.List
import org.ddmore.mdl.mdl.LogicalExpression
import org.ddmore.mdl.mdl.Selector
import org.ddmore.mdl.mdl.Arguments
import org.ddmore.mdl.mdl.ConditionalStatement
import org.ddmore.mdl.mdl.OrExpression
import org.ddmore.mdl.mdl.AndExpression
import org.ddmore.mdl.validation.AttributeValidator
import org.ddmore.mdl.validation.MdlJavaValidator
import org.ddmore.mdl.validation.FunctionValidator
import org.ddmore.mdl.types.VariabilityType

class Mdl2Nonmem extends MdlPrinter{
	
	protected var Mcl mcl = null;
	protected val TARGET = "NMTRAN_CODE";
	
	//Print file name and analyse MCL objects in the source file
  	def convertToNMTRAN(Mcl m){
  		mcl = m;
  		m.prepareCollections();

  		var version = "1.02";
  		var date = "24.08.2013"
		
  		var java.util.List<DataObject> dataObjects = new ArrayList<DataObject>
  		var java.util.List<TaskObject> taskObjects = new ArrayList<TaskObject>
  		
        for (o:m.objects) {
            if (o.dataObject != null)
                dataObjects.add(o.dataObject);
            if (o.taskObject != null)
                taskObjects.add(o.taskObject);
        }
	
		'''
		;mdl2nt «version» beta, last modification «date», Natallia Kokash (natallia.kokash@gmail.com)  
		«printSIZES»
		«printPROB»
		«FOR d:dataObjects»
			«convertToNMTRAN(d, taskObjects)»
	  	«ENDFOR»
		«FOR t:taskObjects»
			«t.printIGNORE»
	  	«ENDFOR»
		«printABBREVIATED»
		«FOR o:m.objects»
			«IF o.modelObject != null»«o.modelObject.convertToNMTRAN»«ENDIF»
	  	«ENDFOR»
		«printAES»
	  	«FOR o:m.objects»
			«IF o.parameterObject != null»«o.parameterObject.convertToNMTRAN»«ENDIF»
	  	«ENDFOR»
		«FOR o:m.objects»
			«IF o.taskObject != null»«o.taskObject.convertToNMTRAN»«ENDIF»
		«ENDFOR»
		«FOR o:m.objects»
			«IF o.modelObject != null»«o.modelObject.printTABLE»«ENDIF»
	  	«ENDFOR»
		'''
	}
	
////////////////////////////////////	
//convertToNonmem MCL
////////////////////////////////////
	//Print NM-TRAN record $SIZES
	def printSIZES()'''
	«IF "$SIZES".isTargetDefined»
	
	«getExternalCodeStart("$SIZES")»
	«getExternalCodeEnd("$SIZES")»
	«ENDIF»	
	'''

	//Print NM-TRAN record $PROB/$PROBLEM
	def printPROB()'''
	«IF "$PROBLEM".isTargetDefined || "$PROB".isTargetDefined»
	
	«getExternalCodeStart("$PROBLEM")»
	«getExternalCodeStart("$PROB")»
	«getExternalCodeEnd("$PROBLEM")»
	«getExternalCodeEnd("$PROB")»
	«ELSE»
	
	$PROB «mcl.fileName.toUpperCase»
	«ENDIF»
	'''

	//Print NM-TRAN record $ABB/$ABBREVIATED
	def printABBREVIATED()'''
	«IF "$ABB".isTargetDefined || "$ABBREVIATED".isTargetDefined»

	«getExternalCodeStart("$ABB")»
	«getExternalCodeStart("$ABBREVIATED")»
	«getExternalCodeEnd("$ABB")»
	«getExternalCodeEnd("$ABBREVIATED")»
	«ENDIF»
	'''
	
	//Print NM-TRAN record $AES
	def printAES()'''
	«IF "$AES".isTargetDefined»
	
	«getExternalCodeStart("$AES")»
	«getExternalCodeEnd("$AES")»
	«ENDIF»
	'''

////////////////////////////////////	
//convertToNonmem Model Object
////////////////////////////////////	
	def convertToNMTRAN(ModelObject o){
		val isLibraryDefined = o.isLibraryDefined;
		val isPKDefined = o.isGroupOrIndividualDefined;
		val isErrorDefined = o.isErrorDefined;
		val isODEDefined = o.isODEDefined;
		'''
		«IF isLibraryDefined»
			
			«o.printSUBR(isPKDefined)»
		«ENDIF»
		«o.printMODEL(isODEDefined)»
		«o.generateMODEL»
		«IF isLibraryDefined»

			«o.printPK(isPKDefined)»
			«o.printMIX»
			«o.printERROR(isErrorDefined)»
		«ELSE» 
			«o.printPRED(isPKDefined, isErrorDefined)»
		«ENDIF»
		«o.printDES(isODEDefined)»
		'''
	}
	
	//Print NM-TRAN record $PRED
	def printPRED(ModelObject o, Boolean isPKDefined, Boolean isErrorDefined)'''
	«IF isPKDefined || isErrorDefined»		
	
	$PRED
	«ENDIF»
	«getExternalCodeStart("$PRED")»
	«o.printPKContent»
	«o.printMIXContent»
	«o.printErrorContent»
	«getExternalCodeEnd("$PRED")»
	'''

	//Print NM-TRAN  record $PK
	def printPK(ModelObject o, Boolean isPKDefined)'''
	«IF isPKDefined»
	
	$PK
	«ENDIF»
	«getExternalCodeStart("$PK")»
	«o.printPKContent»
	«getExternalCodeEnd("$PK")»
	'''
	
	//Processing GROUP_VARIABLES, INDIVIDUAL_VARIABLES, MODEL_PREDICTION (init conditions) for $PK
	def printPKContent(ModelObject o)'''
	«FOR b:o.blocks»
		«IF	b.groupVariablesBlock != null»
			«FOR st: b.groupVariablesBlock.statements»
				«IF st.statement != null»
					«st.statement.print»
				«ENDIF»
			«ENDFOR»
		«ENDIF»
		«IF b.individualVariablesBlock != null»
			«FOR s: b.individualVariablesBlock.statements SEPARATOR ' '»
				«s.print»
			«ENDFOR»
		«ENDIF»
		«IF b.modelPredictionBlock != null»		
			«IF init_vars.entrySet.size > 0»

				;initial conditions
				«FOR e: init_vars.entrySet»
					A_0(«e.key») = «e.value»
				«ENDFOR»
			«ENDIF»
		«ENDIF»
	«ENDFOR»
	'''
	
	//Print NM-TRAN record $MIX 
	def printMIX(ModelObject o)'''
	«IF o.isMixDefined»
	
	$MIX
	«ENDIF»
	«getExternalCodeStart("$MIX")»
	«getExternalCodeStart("$MIXTURE")»
	«o.printMIXContent»
	«getExternalCodeEnd("$MIX")»
	«getExternalCodeEnd("$MIXTURE")»
	'''

	def printMIXContent(ModelObject o)'''
	«FOR b:o.blocks»
		«IF	b.groupVariablesBlock != null»
			«FOR st: b.groupVariablesBlock.statements»
				«IF st.mixtureBlock != null»
					«st.mixtureBlock.print»
				«ENDIF»
			«ENDFOR»
		«ENDIF»
	«ENDFOR»
	'''
	
	//Print block
	def print(MixtureBlock b){
		var nspop = 0;
		for (BlockStatement st: b.statements){
			if (st.symbol != null){
				if (st.symbol.expression.list != null){
					nspop = st.symbol.expression.list.arguments.arguments.size;
				}
			}
		}		
		var res = "";
		if (nspop > 0){
			res  = "NSPOP = " + nspop + "\n";
			var i = 1;
			for (BlockStatement st: b.statements){
				if (st.symbol != null){
					if (st.symbol.expression.expression != null){
						res = res + "P(" + i + ") = " + st.symbol.expression.expression.toStr + "\n";
						i = i + 1;
					}
				}
			}		
		}
		'''«res»'''
	}
	
	//Print NM-TRAN record $ERROR
	def printERROR(ModelObject o, Boolean isErrorDefined)'''
	«IF isErrorDefined»	
	
	$ERROR
	«ENDIF»
	«getExternalCodeStart("$ERROR")»
	«o.printErrorContent»
	«getExternalCodeEnd("$ERROR")»
	'''	
	
	//Processing MODEL_PREDICTION, OBSERVATION for $ERROR
	def printErrorContent(ModelObject o)'''
		«FOR mob:o.blocks»
			«IF mob.modelPredictionBlock != null»
				«FOR s: mob.modelPredictionBlock.statements»
					«IF s.statement != null»
						«s.statement.print»
					«ENDIF»
				«ENDFOR»
			«ENDIF»
			«IF mob.observationBlock != null»
				«mob.observationBlock.print»
			«ENDIF»
			«IF mob.simulationBlock != null»
				«mob.simulationBlock.print»
			«ENDIF»
			«IF mob.estimationBlock != null»
				«mob.estimationBlock.print»
			«ENDIF»			
		«ENDFOR»
	'''
	
	//If there is a definition with ncmt=N, define N compartment names
	def generateMODEL(ModelObject o){
		var res = "";
		var nmct =  o.numberOfCompartments;		
		//return '''Testing: «nmct»''';		
		if (nmct > 0) {
			res = res + "\n$MODEL\n";
			for (i : 1 ..nmct) {
 			   res = res + "COMP (comp" + i + ")\n"
			}
		}
		'''«res»'''
	}

	//Processing MODEL_PREDICTION for $MODEL
	def printMODEL(ModelObject o, Boolean isODEDefined)'''
	«IF isODEDefined»
	
	$MODEL
	«ENDIF»
	«getExternalCodeStart("$MODEL")»
	«FOR b:o.blocks»
		«IF b.modelPredictionBlock != null»
			«var bb = b.modelPredictionBlock»
			«FOR s: bb.statements»
				«IF s.odeBlock != null»
					«FOR ss: s.odeBlock.statements»
						«var x = ss.symbol»
						«IF x != null»
							«IF x.expression != null»
								«IF x.expression.odeList != null»
									COMP(«x.symbolName.name»)
								«ENDIF»
							«ENDIF»
						«ENDIF»
					«ENDFOR»
				«ENDIF»
			«ENDFOR»
		«ENDIF»
	«ENDFOR»
	«getExternalCodeEnd("$MODEL")»
	'''

	//Processing MODEL_PREDICTION for $DES
	def printDES(ModelObject o, Boolean isODEDefined)'''
	«IF isODEDefined»

	$DES
	«ENDIF»
	«getExternalCodeStart("$DES")»
	«FOR b:o.blocks»
		«IF b.modelPredictionBlock != null»
			«var bb = b.modelPredictionBlock»
			«FOR s: bb.statements»
				«IF s.odeBlock != null»
					«FOR ss: s.odeBlock.statements»
						«var x = ss.symbol»
						«IF x != null»
							«IF x.expression != null»
								«IF x.expression.expression != null»
									«x.print»
								«ENDIF»
								«IF x.expression.odeList != null»
									«var deriv = x.expression.odeList.arguments.getAttribute(AttributeValidator::attr_req_deriv.name)»
									«IF !deriv.equals("")»
										«var id = x.symbolName.name»
										«IF dadt_vars.get(id) != null»
											DADT(«dadt_vars.get(id)») = «deriv»
										«ENDIF»	
									«ENDIF»
								«ENDIF»
							«ENDIF»
						«ENDIF»
						«IF ss.statement != null»
							«ss.statement.print»
						«ENDIF»
					«ENDFOR»
				«ENDIF»
			«ENDFOR»
		«ENDIF»
	«ENDFOR»
	«getExternalCodeEnd("$DES")»
	'''    
    
	//Processing MODEL_PREDICTION for $SUBR
	def printSUBR(ModelObject o, Boolean isPKDefined)'''
	«IF isPKDefined»

	$SUBR
	«ENDIF»
	«getExternalCodeStart("$SUBR")»
	«getExternalCodeStart("$SUBROUTINE")»
	«FOR b:o.blocks»
		«IF b.modelPredictionBlock != null»
			«b.modelPredictionBlock.printSUBR»
		«ENDIF»
	«ENDFOR»
	«getExternalCodeEnd("$SUBR")»
	«getExternalCodeEnd("$SUBROUTINE")»
    ''' 
    
    //Processing MODEL_PREDICTION for $SUBR
    //Find an imported function name and attributes "model", "trans"
	def printSUBR(ModelPredictionBlock b){
		for (ss: b.statements){
			if (ss.libraryBlock != null){
				for (st: ss.libraryBlock.statements){
					var libraryRef = st.expression.identifier;
					var attributes = libraryRef.getExternalFunctionAttributes();
					var library = libraryRef.symbol.name;
					if (attributes != null){
						var name = attributes.get("name");
						if (name != null) library = name;
					}
					val model = st.expression.arguments.getAttribute(AttributeValidator::attr_req_model.name);
					val trans = st.expression.arguments.getAttribute(AttributeValidator::attr_trans.name);
					val tol = getTOL;
					return '''«IF !model.equals("")»«library.toUpperCase()»«model»«ENDIF» «IF !trans.equals("")»TRANS«trans»«ENDIF» «IF !tol.equals("")»TOL = «tol»«ENDIF»'''
				}
			}
		}
	}
		
	//Print NM-TRAN record $TABLE
	def printTABLE(ModelObject o)'''
	«IF o.isOutputVariablesDefined»

	$TABLE 
	«ENDIF»	
	«getExternalCodeStart("$TABLE")»
	«FOR b:o.blocks»
		«IF b.outputVariablesBlock != null»
			«var bb = b.outputVariablesBlock»
			«IF bb.variables.size > 0»
				«FOR st: bb.variables SEPARATOR ' '»«st.toStr»«ENDFOR»
				«val file = getTaskObjectName»
				ONEHEADER NOPRINT «IF !file.equals("")»FILE=«file».fit«ENDIF» 
			«ENDIF»
		«ENDIF»	
	«ENDFOR»
	«getExternalCodeEnd("$TABLE")»
	'''
	
 
////////////////////////////////////
//convertToNonmem PARAMETER OBJECT
/////////////////////////////////////		
	def convertToNMTRAN(ParameterObject obj){
	obj.collectDimensionsForSame;	
	'''
	«obj.printPRIOR»
	«obj.printTHETA»
	«obj.printOMEGA»
	«obj.printSIGMA»
	'''
	}

	//Copy statements from PRIOR block to $PRIOR
	def printPRIOR(ParameterObject obj)'''
	«IF obj.isPriorDefined»
	
	$PRIOR
	«ENDIF»	
	«getExternalCodeStart("$PRIOR")»
	«FOR b:obj.blocks»			
		«IF b.priorBlock != null»
			«FOR st: b.priorBlock.statements»
				«st.print»
			«ENDFOR»
		«ENDIF»
	«ENDFOR»
	«getExternalCodeEnd("$PRIOR")»
	'''
		
	//Processing STRUCTURAL for $THETA
	def printTHETA(ParameterObject obj)'''
	«IF obj.isStructuralDefined»

	$THETA
	«ENDIF»
	«getExternalCodeStart("$THETA")»
	«FOR b:obj.blocks»			
		«IF b.structuralBlock != null»
			«FOR st: b.structuralBlock.parameters»
				«st.printTheta»
			«ENDFOR»
		«ENDIF»
	«ENDFOR»
	«getExternalCodeEnd("$THETA")»
	«IF "$THETAI".isTargetDefined»
	
	«externalCodeStart.get("$THETAI")»
	«externalCodeEnd.get("$THETAI")»
	«ENDIF»
	«IF "$THETAR".isTargetDefined»
	
	«externalCodeStart.get("$THETAR")»
	«externalCodeEnd.get("$THETAR")»
	«ENDIF»
	'''
	
	//Processing VARIABILITY for $OMEGA
	def printOMEGA(ParameterObject obj)'''
	«externalCodeStart.get("$OMEGA")»
	«obj.printVariabilityBlock("$OMEGA")»
	«externalCodeEnd.get("$OMEGA")»
	'''
	
	//Processing VARIABILITY for $SIGMA
	def printSIGMA(ParameterObject obj)'''
	«getExternalCodeStart("$SIGMA")»
	«obj.printVariabilityBlock("$SIGMA")»
	«getExternalCodeEnd("$SIGMA")»
	'''
	
	//Return VARIABILITY block statements for $SIGMA or $OMEGA
	def printVariabilityBlock(ParameterObject obj, String section){
		var res = "";
		for (b:obj.blocks){
			if (b.variabilityBlock != null){
				var printSectionName = true;
				for (c: b.variabilityBlock.statements){
					if (c.parameter != null){
						var tmp = c.parameter.printVariabilityParameter(section);
						if (!tmp.equals("")){
							if (printSectionName){
								res = res + "\n" + section + "\n";
								printSectionName = false;
							}
							res = res + tmp;
						}
					}
					if (c.diagBlock != null){
						res  = res + c.diagBlock.printDiag(section);
						printSectionName = true;
					}
					if (c.matrixBlock != null){
						res  = res + c.matrixBlock.printMatrix(section);
						printSectionName = true;
					}
					if (c.sameBlock != null){
						res  = res + c.sameBlock.printSame(section);
						printSectionName = true;
					}
				}
			}
		}
		return res;
	}
	
	//$OMEGA BLOCK(dim) SAME ; varName
	//$SIGMA BLOCK(dim) SAME ; varName
	def printSame(SameBlock b, String section) { 
		var name = b.arguments.getAttribute(AttributeValidator::attr_name.name);
		if (name.equals("")) return '''''';
		val isOmega = section.equals("$OMEGA") && (namedOmegaBlocks.get(name) != null);
		val isSigma = section.equals("$SIGMA") && (namedSigmaBlocks.get(name) != null);
		if (isOmega || isSigma)	{			
			var k = 0;
			if (isOmega) k = namedOmegaBlocks.get(name);
			if (isSigma) k = namedSigmaBlocks.get(name);
			return 
			'''
			
			«section» «IF k > 0»BLOCK («k») SAME«ENDIF»
			«IF b.parameters != null»
				«FOR p: b.parameters.arguments»
					; «p.name»
				«ENDFOR»
			«ENDIF»
			'''
		}
		return "";
	}	
	
	//Create maps with dimensions for same blocks
	def collectDimensionsForSame(ParameterObject obj){
		for (b:obj.blocks){
			if (b.variabilityBlock != null){
				for (c: b.variabilityBlock.statements){
					if (c.diagBlock != null)
						c.diagBlock.collectDimensionsForSame;
					if (c.matrixBlock != null)
						c.matrixBlock.collectDimensionsForSame;
				}
			}
		}
	}
	
	//Create maps with dimensions for same blocks corresponding to diag(...){...}
	def collectDimensionsForSame(DiagBlock b)
	{
		var k = 0; 
		var name = b.arguments.getAttribute(AttributeValidator::attr_name.name);
		var isOmega = false;
		var isSigma = false;
		if (name != null){
			if (b.parameters != null)		
				for (p: b.parameters.arguments) {
					if (p.expression != null){
						k = k + 1;
						if (p.argumentName != null){
							if (eta_vars.get("eta_" + p.argumentName.name) != null) isOmega = true;
							if (eps_vars.get("eps_" + p.argumentName.name) != null) isSigma = true;
						} 
					}
				}		
			if (isOmega) namedOmegaBlocks.put(name, k);
			if (isSigma) namedSigmaBlocks.put(name, k);
		}
	}
	
	//Create maps with dimensions for same blocks corresponding to matrix(...){...}
	def collectDimensionsForSame(MatrixBlock b)
	{
		var k = 0;
		var name = b.arguments.getAttribute(AttributeValidator::attr_name.name);
		var isOmega = false;
		var isSigma = false;
		if (b.parameters != null)
			for (p: b.parameters.arguments) {
				if (p.expression != null){
					if (p.argumentName != null){
						if (eta_vars.get("eta_" + p.argumentName.name) != null) isOmega = true;
						if (eps_vars.get("eps_" + p.argumentName.name) != null) isSigma = true;
						k = k + 1;
					}
				}
			}
		if (isOmega) namedOmegaBlocks.put(name, k);
		if (isSigma) namedSigmaBlocks.put(name, k);
	}

	//Print diag(...){...} subblock of VARIABILITY
	def printDiag(DiagBlock b, String section)
	{
		var result = "";
		var printFix = false;
		var k = 0; 
		for (a: b.arguments.arguments){
			if (a.argumentName != null){ 
				if (a.argumentName.name.equals(AttributeValidator::attr_fix.name)){ 
					if (a.expression != null){
						printFix = a.expression.isTrue	
					}
				}				
			}
		}	
		if (b.parameters != null)		
			for (p: b.parameters.arguments) {
				if (p.expression != null){
					var  i = 0;
					var tmpRes = "";
					while (i < k){
						tmpRes = tmpRes + "0 ";
						i = i + 1;
					}
					k = k + 1;
					if (p.argumentName != null){
						val isOmega = section.equals("$OMEGA") && (eta_vars.get("eta_" + p.argumentName.name) != null);
						val isSigma = section.equals("$SIGMA") && (eps_vars.get("eps_" + p.argumentName.name) != null);
						if (isOmega || isSigma)	{
							result = result + tmpRes + p.expression.toStr + " ";
							result = result + "; " + p.argumentName.name + "\n";
						}
					} 
					else
						if (!result.equals("")) result = result + p.expression.toStr + " ";
				}
			}		
		if (printFix && !result.equals("")) result = result + "FIX\n";
		if (result.equals("")) return "";		
		return
		'''
		
		«section» «IF k > 0»BLOCK («k») «ENDIF»
		«result»
		'''
	}
	
	//Print matrix(...){...} subblock of VARIABILITY
	def printMatrix(MatrixBlock b, String section)
	{
		var result = "";
		var printFix = false;
		var k = 0; 
		for (a: b.arguments.arguments){
			if (a.argumentName != null){ 
				if (a.argumentName.name.equals(AttributeValidator::attr_fix.name)) 
					if (a.expression != null){
						printFix = a.expression.isTrue		
					}
			}
		}
		if (b.parameters != null)
			for (p: b.parameters.arguments) {
				if (p.expression != null){
					if (p.argumentName != null){
						val isOmega = section.equals("$OMEGA") && (eta_vars.get("eta_" + p.argumentName.name) != null);
						val isSigma = section.equals("$SIGMA") && (eps_vars.get("eps_" + p.argumentName.name) != null);
						if (isOmega || isSigma)	{
							result = result + p.expression.toStr + " ";
							result = result + "; " + p.argumentName.name + "\n";
							k = k + 1;
						}
					} 
					else
						if (!result.equals("")) result = result + p.expression.toStr + " ";
				}
			}
		if (printFix && !result.equals("")) result = result + "FIX\n";
		if (result.equals("")) return "";
		return 
		'''
		
		«section» «IF k > 0»BLOCK («k») «ENDIF»
		«result»
		'''; 
	}	

	//Print VARIABILITY parameter in $SIGMA or $OMEGA
	def printVariabilityParameter(ParameterDeclaration s, String section){
		var name = s.symbolName.name;
		//SIGMA <=> EPS, OMEGA <=> ETA	
		var isOmega = (section.equals("$OMEGA") && eta_vars.get("eta_" + name) != null);
		var isSigma = (section.equals("$SIGMA") && eps_vars.get("eps_" + name) != null);
		if (isOmega || isSigma)
		{
			val value = s.list.arguments.getAttribute(AttributeValidator::attr_req_value.name);
			val printFix = s.list.arguments.isAttributeTrue(AttributeValidator::attr_fix.name);
			return							
			'''
			«IF !value.equals("")»«value»«IF printFix» FIX«ENDIF»«ENDIF» ; «name»
			'''
		}
		return "";
	}
	
	
	//Find attributes in STRUCTURAL_VARIABLES and form an NMTRAN statement
	def printTheta(ParameterDeclaration s){
		if (s.list != null){		
			var name = s.symbolName.name;
			val value = s.list.arguments.getAttribute(AttributeValidator::attr_value.name);
			val lo = s.list.arguments.getAttribute(AttributeValidator::attr_lo.name);
			val hi = s.list.arguments.getAttribute(AttributeValidator::attr_hi.name);
			val printFix = s.list.arguments.isAttributeTrue(AttributeValidator::attr_fix.name);
			if (value.equals("")) return "";
			if (lo.equals("") && hi.equals("")) return '''«value»«IF printFix» FIX«ENDIF» ; «name»'''
			if (lo.equals("")) return '''(-INF, «value», «hi»)«IF printFix» FIX«ENDIF» ; «name»'''
			if (hi.equals("")) return '''(«lo», «value», INF)«IF printFix» FIX«ENDIF» ; «name»'''
			return '''(«lo», «value», «hi»)«IF printFix» FIX«ENDIF» ; «name»'''
		}
	}	
	
////////////////////////////////////	
//convertToNonmem DATA OBJECT
////////////////////////////////////
	def convertToNMTRAN(DataObject d, java.util.List<TaskObject> t)'''
	«printINPUT(d,t)»
	«d.printDATA»
	'''

	//Print NM-TRAN record $INPUT
	def printINPUT(DataObject d, java.util.List<TaskObject> t)'''
	«IF d.isHeaderDefined»
		
	$INPUT
	«ENDIF»
	«getExternalCodeStart("$INPUT")»
	«FOR b:d.blocks»
	    «IF b.dataInputBlock != null»
			«FOR st: b.dataInputBlock.variables SEPARATOR ' '»«IF isDrop(st.symbolName.name, t)»«st.symbolName.name»=DROP«ELSE»«st.symbolName.name»«ENDIF»«ENDFOR»
        «ENDIF»
	«ENDFOR»
	«getExternalCodeEnd("$INPUT")»
	'''
	
	//Note: We drop now all the variables in the specification regardless of the object they refer to
    def isDrop(String id, java.util.List<TaskObject> t) {
        for (TaskObject tObj :t) {
            for (b: tObj.blocks) {
                if (b.dataBlock !=  null) {
                    for (DataBlockStatement block: b.dataBlock.statements) {
                        if (block.dropList != null) {
                            for (FullyQualifiedSymbolName symbol : block.dropList.list.symbols) {
                                if (id.equals(symbol.symbol.name))
                                    return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
	
	//Print NM-TRAN record $DATA
	def printDATA(DataObject o)'''
	«FOR b:o.blocks»
		«IF b.fileBlock != null» 
			«var data = b.fileBlock.getData»
			«IF !data.equals("")»
			
			$DATA «data»
			«ENDIF»
			«getExternalCodeStart("$DATA")»
			«FOR s: b.fileBlock.statements»
				«s.printIGNORE»
			«ENDFOR»
			«getExternalCodeEnd("$DATA")»
		«ENDIF»
	«ENDFOR»
	'''
	
	//if (st.inlineBlock != null) st.inlineBlock.convertToNonmem
	//if (st.designBlock != null) st.designBlock.convertToNonmem
	//if (st.rscriptBlock != null) st.rscriptBlock.convertToNonmem
	
	//Processing FILE block statement for $DATA
	def printIGNORE(FileBlockStatement s){
		if (s.variable != null){
			if (s.variable.symbolName.name.equals(MdlJavaValidator::var_file_data)){
				if (s.variable.expression != null){
					if (s.variable.expression.list != null){
						var ignore = s.variable.expression.list.arguments.getAttribute(AttributeValidator::attr_ignore.name);
						return '''«IF !ignore.equals("")»IGNORE=«ignore»«ENDIF»'''
					}
				}
			}
		}
	}
	
	def getData(FileBlock b){
		for (s: b.statements){
			if (s.variable.symbolName.name.equals(MdlJavaValidator::var_file_data)){
				if (s.variable.expression != null){
					if (s.variable.expression.list != null){
						var data = s.variable.expression.list.arguments.getAttribute(AttributeValidator::attr_req_source.name);
						return data;
					}
				}
			}
		}
	}
	
	
/////////////////////////////////////////////////
//convertToNonmem TASK OBJECT
/////////////////////////////////////////////////

	//Processing task object for $EST and $SIM
	def convertToNMTRAN(TaskObject o)'''
	«FOR b:o.blocks»
	«IF b.functionDeclaration != null»
		«val body = b.functionDeclaration.functionBody»
		«IF body != null»
			«FOR bb: body.blocks»
			«IF bb.estimateBlock != null»
				«bb.estimateBlock.print»
			«ENDIF»
			«IF bb.simulateBlock != null»
				«bb.simulateBlock.print»
			«ENDIF»
			«IF bb.executeBlock != null»
				«bb.executeBlock.print»
			«ENDIF»
			«ENDFOR»
		«ENDIF»
	«ENDIF»
	«ENDFOR»
	'''
	
	//Print NM-TRAN IGNORE=... statements
	def printIGNORE(TaskObject o)'''
	«FOR b: o.blocks»
		«IF b.dataBlock !=  null»
			«FOR DataBlockStatement block: b.dataBlock.statements»
				«IF block.ignoreList != null»
					«block.ignoreList.identifier» («block.ignoreList.expression.toCommaSeparatedStr»)
				«ENDIF»
			«ENDFOR»
		«ENDIF»
	«ENDFOR»
	''' 
	
	//Processing SIMULATE block for $SIM 
	def print(SimulateTask b)'''
		«var isInlineTargetDefined = TARGET.isInlineTargetDefined(b)»
		«IF !isInlineTargetDefined»
		
		$SIM 
		«ENDIF»
		«getExternalCodeStart("$SIM")»
		«getExternalCodeStart("$SIMULATION")»
		«IF !isInlineTargetDefined»
			«FOR s: b.statements»
				«IF s.symbol != null»«s.symbol.printDefaultSimulate»«ENDIF»
			«ENDFOR»
			NOABORT
		«ELSE»
			«FOR s: b.statements»
				«IF s.targetBlock != null»«s.targetBlock.print»«ENDIF»
			«ENDFOR»
		«ENDIF» 
		«getExternalCodeEnd("$SIM")»
		«getExternalCodeEnd("$SIMULATION")»	
	'''
	
	//Processing ESTIMATE block for $EST
	def print(EstimateTask b)'''
		«var isInlineTargetDefined = TARGET.isInlineTargetDefined(b)»
		«IF !isInlineTargetDefined»
		
		$EST 
		«ENDIF»
		«getExternalCodeStart("$EST")»
		«getExternalCodeStart("$ESTIMATION")»
		«IF !isInlineTargetDefined»
			«FOR s: b.statements»
				«IF s.symbol != null»«s.symbol.printDefaultEstimate»«ENDIF»
			«ENDFOR»
			NOABORT 
			«b.printCovariance»
		«ELSE»
			«FOR s: b.statements»
				«IF s.targetBlock != null»«s.targetBlock.print»«ENDIF»
			«ENDFOR»
		«ENDIF»
		«getExternalCodeEnd("$EST")»
		«getExternalCodeEnd("$ESTIMATION")»
	'''
	
	//Print attributes for default $EST record
	def printDefaultEstimate(SymbolDeclaration s) { 
		if (s.symbolName.name.equals(FunctionValidator::attr_task_algo.name)){
			if (s.expression.expression != null)
				''' METHOD=«s.expression.expression.toStr»'''
			else {
				//print first attribute of the list!?
				if (s.expression.list != null){
					var args = s.expression.list.arguments;
					if (args != null){
						if (args.arguments.size > 0)
							''' METHOD=«args.arguments.get(0).expression.toStr»'''
					}
				}
			}	
		}
		else
			if (s.symbolName.name.equals(FunctionValidator::attr_task_max.name))
			''' MAX=«s.expression.print»'''
		else
			if (s.symbolName.name.equals(FunctionValidator::attr_task_sig.name))
			''' SIG=«s.expression.print»'''
	}
	
	//Print attributes for default $SIM record
	def printDefaultSimulate(SymbolDeclaration s)'''
	'''
	
	//Print $COV 
	def printCovariance(EstimateTask b)'''
	«IF b.isCovarianceDefined»
	
	$COV 
	«ENDIF»
	«getExternalCodeStart("$COV")»
	«getExternalCodeStart("$COVARIANCE")»
	«FOR s: b.statements»«IF s.symbol != null»«s.symbol.printCovariance»«ENDIF»«ENDFOR»
	«getExternalCodeEnd("$COV")»
	«getExternalCodeEnd("$COVARIANCE")»
	'''	
	
	//Print "cov" attribute for $COVARIATE record 
	def printCovariance(SymbolDeclaration s) { 
		if (s.symbolName.name.equals(FunctionValidator::attr_task_cov.name)){
			if (s.expression != null){
				if (s.expression.toStr.replaceAll("\\s","").equals(""))
				'''«s.expression.print»'''
			}
		}
	}

	
	def print(ExecuteTask b)'''
	
	«FOR s: b.statements»
		«IF s.targetBlock != null»«s.targetBlock.print»«ENDIF»
	«ENDFOR»
	'''
		
	//Get task object name 
	def getTaskObjectName(){
		for (obj: mcl.objects){
  			if (obj.taskObject != null)
  				return obj.objectName.name;
  		}
		return "";
	}
	
	//Get $TOL attribute
	def getTOL(){
		for (obj: mcl.objects){
  			if (obj.taskObject != null){
  				val tol = obj.taskObject.getTOL;
				if (tol.length > 0) return tol;
  			}
  		}
		return "";
	}
	
	//Get $TOL attribute
	def getTOL(TaskObject obj){
		for (TaskObjectBlock b: obj.blocks){
			if (b.modelBlock != null){
				for (ss: b.modelBlock.statements){
					var x = ss.statement.symbol;
					if (x != null){
						if (x.symbolName.name.equals(MdlJavaValidator::var_model_tolrel)){
							if (x.expression != null)
								return x.expression.toStr;
						}
					}
				}
			}
		}
		return "";
	}	
	
	 
	protected var externalFunctions = new HashMap<String, HashMap<String, String>>() //list of external functions 
	protected var externalCodeStart = new HashMap<String, ArrayList<String>>() //external code per target language section,
	protected var externalCodeEnd = new HashMap<String, ArrayList<String>>() //external code per target language section,
 	
	protected var theta_vars = newHashMap //THETAs - Structural parameters
	protected var dadt_vars = newHashMap  //DADT   - Model prediction -> ODE	
	protected var init_vars = newHashMap  //A      - Model prediction -> ODE -> init attribute	
	
	protected var eps_vars = newHashMap   //EPSs   - Random variables, level 1
	protected var eta_vars = newHashMap	  //ETAs   - Random variables, level 2
	
	protected var level_vars = newHashMap //       - Input variables, level attribute			
	
	//These maps are used to print same blocks (NM-TRAN SAME opetion in $OMEGA and $SIGMA)
	protected var namedOmegaBlocks = new HashMap<String, Integer>() //Collection of names of $OMEGA records with dimensions
	protected var namedSigmaBlocks = new HashMap<String, Integer>() //Collection of names of $SIGMA records with dimensions
	
 	def protected clearCollections(){
		init_vars.clear;
		dadt_vars.clear;
		theta_vars.clear;
		eta_vars.clear;
    	eps_vars.clear; 
    	namedOmegaBlocks.clear;
    	namedSigmaBlocks.clear;	
    	eta_vars.clear;
    	eps_vars.clear; 
	}
	
	//Collect variables from the MDL file
	def protected prepareCollections(Mcl m){
    	clearCollections();
    	for (o:m.objects){
	  		if (o.modelObject != null){
	  			setLevelVars(o.modelObject);
	  			setRandomVariables(o.modelObject);
	  			setStructuralParameters(o.modelObject);
	  			setModelPredictionVariables(o.modelObject);
	  			setInitialConditions(o.modelObject);
	  		} 
  		}
  		m.prepareExternals;
	}	
	
	def setLevelVars(ModelObject o){
		var tmp = o.getLevelVars("1");
		for (v: tmp){
			if (level_vars.get(v) == null)
				level_vars.put(v, "1");
		}
		tmp = o.getLevelVars("2");
		for (v: tmp){
			if (level_vars.get(v) == null)
				level_vars.put(v, "2");
		}
	}
		
	def protected getLevelVars(ModelObject o, String levelId) {
		var levelVars = new HashSet<String>();
		for (b: o.blocks){
			if(b.inputVariablesBlock != null){
				for (SymbolDeclaration s: b.inputVariablesBlock.variables){
					if (s.expression != null){
						if (s.expression.list != null){
							var level = s.expression.list.arguments.getAttribute(AttributeValidator::attr_level.name);
							if (level.equals(levelId)){
								if (!levelVars.contains(s.symbolName.name)){
									levelVars.add(s.symbolName.name);
								}
							}
						}
					}
				}
			}
		}
		return levelVars;
	}	
		
	//Assign indices to variability parameters ($ETA, $ESP)
	def protected setRandomVariables(ModelObject o){
    	var i = 1; var j = 1; 
		for (b: o.blocks){
	  		if (b.randomVariableDefinitionBlock != null){
				for (s: b.randomVariableDefinitionBlock.variables) {
					if (s.randomList != null){	
						var level = s.randomList.arguments.getAttribute(AttributeValidator::attr_level.name);
						val id = s.symbolName.name;
						if (level_vars.get(level) != null){
							if (level_vars.get(level).equals("2")){
								if (eta_vars.get(id) == null){
									eta_vars.put(id, i);
									i = i + 1;
								}
							}
							if (level_vars.get(level).equals("1"))
								if (eps_vars.get(id) == null){
									eps_vars.put(id, j);
									j = j + 1;
								}	
							}
						}
	  			}
	  		}
  		}
	}
	
	
	//Collect initial conditions from ODE list, init attribute
	def protected setInitialConditions(ModelObject o){
		var i = 1;
		for (b:o.blocks){
			if (b.modelPredictionBlock != null)
				for (s: b.modelPredictionBlock.statements){
					if (s.odeBlock != null)
						for (ss: s.odeBlock.statements){
							if (ss.symbol != null)
								if (ss.symbol.expression != null)
									if (ss.symbol.expression.odeList != null){
										val initCond = ss.symbol.expression.odeList.arguments.getAttribute(AttributeValidator::attr_init.name);
										if (!initCond.equals("")){
											init_vars.put(i, initCond);
										} else init_vars.put(i, "0");
										i = i + 1;
									}
						}
				}
		}
	}
	
	//Assign indices to MODEL variables and expressions
	def protected setModelPredictionVariables(ModelObject o) { 
		var i = 1;
		for (b:o.blocks){
			if (b.modelPredictionBlock != null){
				for (s: b.modelPredictionBlock.statements){
					if (s.odeBlock != null){
						for (ss: s.odeBlock.statements){
							var x = ss.symbol;
							if (x != null){
								if (x.expression != null){
									if (x.expression.odeList != null){
										var id = x.symbolName.name;
										if (dadt_vars.get(id) == null){
											dadt_vars.put(id, i);
											i = i + 1;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	//Assign indices to THETAs
	def protected setStructuralParameters(ModelObject o){
    	var i = 1; 
		for (b: o.blocks){
	  		if (b.structuralParametersBlock != null){
				for (id: b.structuralParametersBlock.parameters) {
					if (theta_vars.get(id.symbol.name) == null){
						theta_vars.put(id.symbol.name, i);
						i = i + 1;
					}
				}
	  		}	  				
  		}
	}
	
	//Find NM-TRAN functions
	def protected prepareExternals(Mcl mcl) {
		externalFunctions.clear;	
		externalCodeStart.clear;	
		externalCodeEnd.clear;	
		for (o:mcl.objects){
  			if (o.modelObject != null){
  				for (ModelObjectBlock block: o.modelObject.blocks){
  					if (block.importBlock != null)
  						block.importBlock.prepareExternalFunctions(o.objectName.name);
  					if (block.targetBlock != null)
  						block.targetBlock.prepareExternalCode;
  				}
  			}
  			if (o.parameterObject != null){
  				for (ParameterObjectBlock block: o.parameterObject.blocks){
  					if (block.importBlock != null)
  						block.importBlock.prepareExternalFunctions(o.objectName.name);
  					if (block.targetBlock != null)
  						block.targetBlock.prepareExternalCode;
  				}
  	  		}
   			if (o.dataObject != null){
  				for (DataObjectBlock block: o.dataObject.blocks){
  					if (block.importBlock != null)
  						block.importBlock.prepareExternalFunctions(o.objectName.name);
  					if (block.targetBlock != null)
  						block.targetBlock.prepareExternalCode;
  				}
  			}
  			if (o.taskObject != null){
  				for (TaskObjectBlock block: o.taskObject.blocks){
  					if (block.importBlock != null)
  						block.importBlock.prepareExternalFunctions(o.objectName.name);
  					if (block.targetBlock != null)
  						block.targetBlock.prepareExternalCode;
  				}
  			}
  			/*if (o.telObject != null){
  				for (BlockStatement block: o.telObject.statements){
  					if (block.targetBlock != null)
  						block.targetBlock.prepareExternalCode;
  				}
  			}*/
  		}
	}
	
	//Prepare a list of external function declarations to define their NMTRAN names 
	 def void prepareExternalFunctions(ImportBlock b, String objName){
		for (ImportedFunction f: b.functions){
			var args = new HashMap<String, String>();
			var target = f.list.arguments.getAttribute(AttributeValidator::attr_req_target.name);
		 	if (target != null){ 
				if (target.equals(TARGET)) {
					for (Argument arg: f.list.arguments.arguments){
						if (arg.argumentName != null)
							args.put(arg.argumentName.name, arg.expression.toStr)
					}
					externalFunctions.put(objName + "$" + f.functionName.name, args);
				}
			}
		}
	}	
	
	 //Prepare a map of section with corresponding target blocks
	 def void prepareExternalCode(TargetBlock b){
		val target = b.arguments.getAttribute(AttributeValidator::attr_req_target.name);
		if (target != null){ 
			if (target.equals(TARGET)) {
				val location = b.arguments.getAttribute(AttributeValidator::attr_location.name);
				if (b.arguments.isAttributeTrue(AttributeValidator::attr_first.name)){
					var codeSnippets = externalCodeStart.get(location);
					if (codeSnippets == null) codeSnippets = new ArrayList<String>();
					codeSnippets.add(b.toStr);
					externalCodeStart.put(location, codeSnippets);
				} else {
					var codeSnippets = externalCodeEnd.get(location);
					if (codeSnippets == null) codeSnippets = new ArrayList<String>();
					codeSnippets.add(b.toStr);
					externalCodeEnd.put(location, codeSnippets);
				} 
			}
		}
	}	

	
 	//Print a list of external code snippets: beginning of section
	def protected getExternalCodeStart(String sectionName){
		var res = "";		
		val snippets = externalCodeStart.get(sectionName) 
		if (snippets != null){
			for (x: snippets){
				res = res + "\n" + x;
			}
		}
		return res;
	}
	
	//Print a list of external code snippets: end of section
	def protected getExternalCodeEnd(String sectionName){
		var res = "";		
		val snippets = externalCodeEnd.get(sectionName) 
		if (snippets != null){
			for (x: snippets){
				res = res + "\n" + x;
			}
		}
		return res;
	}
 
 	//Return attributes of an external function	from the collection
	def protected getExternalFunctionAttributes(FullyQualifiedSymbolName ref) { 
		if (ref.object != null)				
			return externalFunctions.get(ref.object.name + "$" + ref.symbol.name)
		else {
			//match the short name
			for (pair: externalFunctions.entrySet){
				val str = pair.key as String;
				if (str != null){
					val functID = str.substring(str.indexOf("$") + 1);
					if (functID.equals(ref.symbol.name)) return pair.value; 
				}
			}
		}	
	}
	
	def isTargetDefined(String sectionName){
		return externalCodeStart.containsKey(sectionName) || externalCodeEnd.containsKey(sectionName);
	}		
	
		//(==, !=, <, > etc.) - here we skip boolean values!
	override toStr(LogicalExpression e){
		if (e.boolean != null) 	return "";
		return super.toStr(e);
	}				
	
	//Print variableDeclaration substituting ID with "Y" if it is a list with LIKELIHOOD or continuous type
	override toStr(SymbolDeclaration v){
		if (v.expression != null){
			if (v.expression.list != null){
				var type = v.expression.list.arguments.getAttribute(AttributeValidator::attr_cc_type.name);
				var res = "";
				if (type.equals(VariabilityType::CC_CONTINUOUS)){
					res = "F_FLAG = 0\n" 
				}	
				if (type.equals(VariabilityType::CC_LIKELIHOOD)){
					res = "F_FLAG = 1\n"	
				}		
				//substitute variable name with Y
				var listExpr  = v.expression.list.toStr;
				if (!listExpr.equals("") && !res.equals("")){
					return res + "Y = " + listExpr + "\n"; 	
				}
			}
		}
		return super.toStr(v);
	}
	
	//Instead of list(...) we print an expression from a certain attribute (depends on the type)
	override toStr(List l){		
		var type = l.arguments.getAttribute(AttributeValidator::attr_cc_type.name);
		var res = "";
		if (type.equals(VariabilityType::CC_LIKELIHOOD)){
			res = l.arguments.getAttribute(AttributeValidator::attr_likelihood.name);
		} else if (type.equals(VariabilityType::CC_CONTINUOUS)){
			var ruv = l.arguments.getAttribute(AttributeValidator::attr_ruv.name);
			var prediction = l.arguments.getAttribute(AttributeValidator::attr_prediction.name)
			res = prediction + ruv
		}			
		return res;
	}

	
	//References to attributes: skip variable name and replace selectors, e.g,  amount.A[2] -> A(2)
	override toStr(FullyQualifiedArgumentName name) { 
		var res = "";
		for (s: name.selectors){
			res = res + s.toStr
		}
		return res;
	}
	
	//Selectors [] -> (), e.g., A[2] -> A(2)
	override toStr(Selector s) { 
		if (s.argumentName != null)
			return s.argumentName.name;
		if (s.selector != null)
			return "(" + s.selector + ")";
	}	
	
	//toStr function call
	override toStr(FunctionCall call){
		if (call.identifier.toStr.trim.equals(FunctionValidator::funct_error_exit))
			return "EXIT" + call.arguments.toStrWithoutCommas;
		return super.toStr(call);	
	}	
		
	//toStr list arguments without commas
	def toStrWithoutCommas(Arguments arg){
		var res  = "";
		var iterator = arg.arguments.iterator();
		while (iterator.hasNext){
			var a = iterator.next; 
			res = res + " " + a.expression.toStr;
		}
		return res;
	}	
	
		//Convert variable names to NM-TRAN versions
	override convertID(String id){
		if (id.indexOf('_') > 0){			

			if (eta_vars.get(id) != null){
				return "ETA(" + eta_vars.get(id) + ")";
			}
			if (eps_vars.get(id) != null){
				return "EPS(" + eps_vars.get(id) + ")";
			}
			if (theta_vars.get(id) != null){
				return "THETA(" + theta_vars.get(id) + ")"; 
			}
		}
		if (dadt_vars.get(id) != null){
			return "A(" + dadt_vars.get(id) + ")"; 
		}
		if (id.equalsIgnoreCase("ln"))
			return "LOG";
		return id.toUpperCase();	
	}	
	
	//Override MDL operators with NM-TRAN operators
	override convertOperator(String op){
		if (op.equals("<")) return ".LT.";
		if (op.equals(">")) return ".GT.";
		if (op.equals("<=")) return ".LE.";
		if (op.equals(">=")) return ".GE.";
		if (op.equals("==")) return ".EQ.";
		if (op.equals("!=")) return ".NE.";		
		if (op.equals("^")) return "**";
		if (op.equals("||")) return ".OR.";
		if (op.equals("&&")) return ".AND.";
		return op;	
	}
	
	//This is needed because of a bug in NONMEM x||y -> x, y for IGNORE statement
	//toStr OR expression
	def toCommaSeparatedStr(OrExpression e){
		var res = "";
		var iterator = e.expression.iterator();
		if (iterator.hasNext ) res = iterator.next.toStr;
		while (iterator.hasNext) res  = res + ', ' + iterator.next.toStr;	
		return res;
	}
	
	//This is needed because of a bug in NONMEM x&&y -> x, y for ACCEPT statement
	//toStr AND expression
	def toCommaSeparatedStr(AndExpression e){
		var res = "";
		var iterator = e.expression.iterator();
		if (iterator.hasNext ) res = iterator.next.toStr;
		while (iterator.hasNext)
			res  = res + ', ' + iterator.next.toStr;
		return res;	
	}
	
	override print(TargetBlock b){
		var target = "";
		if (b.arguments != null) target = b.arguments.getAttribute(AttributeValidator::attr_req_target.name);
		if (target.equals(TARGET)) {
		'''
		«var printedCode = b.externalCode.substring(3, b.externalCode.length - 3)»
		«printedCode»
		'''
		}
	}
		
	//Override statement printing to substitute MDL conditional operators with NM-TRAN operators
	override print(ConditionalStatement s)'''
	«IF s.expression != null»
		IF «s.expression.print» THEN
			«IF s.ifStatement != null»
				«s.ifStatement.print»
			«ENDIF»
			«IF s.ifBlock != null»
				«s.ifBlock.print»
			«ENDIF»
		«IF s.elseStatement != null || s.elseBlock != null»
		ELSE 
			«IF s.elseStatement != null»
				«s.elseStatement.print»
			«ENDIF»
			«IF s.elseBlock != null»
				«s.elseBlock.print»
			«ENDIF»
		«ENDIF»
		ENDIF
	«ENDIF»
	'''	
}