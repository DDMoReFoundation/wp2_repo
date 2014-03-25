/*
 * MDL IDE, @DDMoRe
 * Author: Natallia Kokash, LIACS, 2014
 * 
 * A class to validate MDL specifications: checks function calls
 */
package org.ddmore.mdl.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.ddmore.mdl.mdl.Arguments;
import org.ddmore.mdl.mdl.BlockStatement;
import org.ddmore.mdl.mdl.DataObject;
import org.ddmore.mdl.mdl.DataObjectBlock;
import org.ddmore.mdl.mdl.EstimateTask;
import org.ddmore.mdl.mdl.ExecuteTask;
import org.ddmore.mdl.mdl.FunctionCall;
import org.ddmore.mdl.mdl.ImportedFunction;
import org.ddmore.mdl.mdl.Mcl;
import org.ddmore.mdl.mdl.MclObject;
import org.ddmore.mdl.mdl.MdlPackage;
import org.ddmore.mdl.mdl.ModelObject;
import org.ddmore.mdl.mdl.ModelObjectBlock;
import org.ddmore.mdl.mdl.ParameterObject;
import org.ddmore.mdl.mdl.ParameterObjectBlock;
import org.ddmore.mdl.mdl.SimulateTask;
import org.ddmore.mdl.mdl.SymbolDeclaration;
import org.ddmore.mdl.mdl.TaskFunctionDeclaration;
import org.ddmore.mdl.mdl.TaskObject;
import org.ddmore.mdl.mdl.TaskObjectBlock;
import org.ddmore.mdl.mdl.impl.AnyExpressionImpl;
import org.ddmore.mdl.mdl.impl.BlockStatementImpl;
import org.ddmore.mdl.mdl.impl.DesignBlockStatementImpl;
import org.ddmore.mdl.mdl.impl.DiagBlockImpl;
import org.ddmore.mdl.mdl.impl.EstimateTaskImpl;
import org.ddmore.mdl.mdl.impl.ExecuteTaskImpl;
import org.ddmore.mdl.mdl.impl.FileBlockStatementImpl;
import org.ddmore.mdl.mdl.impl.HeaderBlockImpl;
import org.ddmore.mdl.mdl.impl.ImportedFunctionImpl;
import org.ddmore.mdl.mdl.impl.InputVariablesBlockImpl;
import org.ddmore.mdl.mdl.impl.LibraryBlockImpl;
import org.ddmore.mdl.mdl.impl.ListImpl;
import org.ddmore.mdl.mdl.impl.MatrixBlockImpl;
import org.ddmore.mdl.mdl.impl.OdeBlockImpl;
import org.ddmore.mdl.mdl.impl.OdeListImpl;
import org.ddmore.mdl.mdl.impl.ParameterDeclarationImpl;
import org.ddmore.mdl.mdl.impl.SameBlockImpl;
import org.ddmore.mdl.mdl.impl.SimulateTaskImpl;
import org.ddmore.mdl.mdl.impl.StructuralBlockImpl;
import org.ddmore.mdl.mdl.impl.SymbolDeclarationImpl;
import org.ddmore.mdl.mdl.impl.SymbolModificationImpl;
import org.ddmore.mdl.mdl.impl.TargetBlockImpl;
import org.ddmore.mdl.mdl.impl.VariabilityBlockStatementImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

import com.google.inject.Inject;

public class FunctionValidator extends AbstractDeclarativeValidator{

	@Override
    @Inject
    public void register(EValidatorRegistrar registrar) {}
		
	public final static String MSG_FUNCTION_DEFINED  = "A function with such name is already defined";
	public final static String MSG_FUNCTION_UNKNOWN  = "Unknown function";
	public final static String MSG_FUNCTION_INVALID = "Invalid function call";
	
	public final static String MSG_FUNCTION_PROPERTY_UNKNOWN = "Unknown property";
	public final static String MSG_FUNCTION_PROPERTY_MISSING = "Required property is not set";
	public final static String MSG_FUNCTION_PROPERTY_DEFINED = "Property defined more than once";
	
	//List of recognized mathematical functions (MDL = PharMML)
	final static List<String> standardFunctions = Arrays.asList(
		"abs", "exp", "factorial", "factl", "gammaln", "ln", "log", "logistic", "logit", "normcdf",
		"probit", "sqrt", "sin", "cos", "tan", "sec", "csc", "cot", "sinh", "cosh", "tanh", 
		"sech", "csch", "coth", "arcsin", "arccos", "arctan", "arcsec", "arccsc", 
		"arccot", "arcsinh", "arccosh", "arctanh", "arcsech", "arccsch", "arccoth", 
		"floor", "ceiling", "logx", "root", "min", "max");
	
	//Number of arguments for standard functions 
	//if a function is not in the list, 1 argument is implied
	//-1 stands for 1 and more, -2 for 2 and more, etc. 
	final static HashMap<String, Integer> functionParameters = 
		new HashMap<String , Integer>() {private static final long serialVersionUID = 1L;
		{//NOTE: PharmML standard functions (operators) support either 1 or 2 parameters 
		    put("logx", 2);
		    put("root", 2);
		    put("min", 2);
		    put("max", 2);
		    put("seq", 3);
		}
	};
	
	//List of recognized MDL functions
	final static List<String> specialFunctions = Arrays.asList("seq", "update", "runif", "errorexit", "PHI");
	//List of declared function names per object
	static HashMap<String, ArrayList<String>> externalFunctions = new HashMap<String, ArrayList<String>>();
	//List of declared function names per object
	static HashMap<String, ArrayList<String>> declaredFunctions = new HashMap<String, ArrayList<String>>();

	///////////////////////////////////////////////////////////////////////////////////////////////
	//Task object
	final static Attribute attr_task_command = new Attribute("command", DataType.TYPE_STRING, true);
	final static Attribute attr_task_target = new Attribute("target", DataType.TYPE_OBJ_REF, true);
	final static Attribute attr_task_model = new Attribute("model", DataType.TYPE_OBJ_REF, true);
	final static Attribute attr_task_parameter = new Attribute("parameter", DataType.TYPE_OBJ_REF, true);
	final static Attribute attr_task_data = new Attribute("data", DataType.TYPE_OBJ_REF, true);
	final static Attribute attr_task_algo = new Attribute("algo", DataType.TYPE_OBJ_REF, false);
	final static Attribute attr_task_max = new Attribute("max", DataType.TYPE_OBJ_REF, false);
	final static Attribute attr_task_sig = new Attribute("sig", DataType.TYPE_OBJ_REF, false);
	final static Attribute attr_task_cov = new Attribute("cov", DataType.TYPE_OBJ_REF, false);
		
	final static List<Attribute> attrs_task = Arrays.asList(
			attr_task_target, attr_task_model, attr_task_parameter, attr_task_data, 
			attr_task_algo, attr_task_max, attr_task_sig, attr_task_cov);
	final static List<Attribute> attrs_exec_task = Arrays.asList(
			attr_task_command);
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	//TODO
	//Validate named attributes of special functions, e.g., for "seq"
	//start, stepSize, end
 	//start, stepSize, repetition
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	Boolean isStandardFunction(String funcName){
		if (standardFunctions.contains(funcName) || specialFunctions.contains(funcName)) return true;
		return false;
	}
	
	//Update the list of recognised functions
	@Check
	public void updateDeclaredFunctionList(Mcl mcl){
		declaredFunctions.clear();
		for (MclObject obj: mcl.getObjects()){
			if (obj.getTaskObject() != null){
				TaskObject taskObj = obj.getTaskObject();
				ArrayList<String> funcList =  new ArrayList<String>();
				for (TaskObjectBlock block : taskObj.getBlocks()){
					if (block.getFunctionDeclaration() != null){
						funcList.add(block.getFunctionDeclaration().getFunctionName().getName());
					}
				}
				declaredFunctions.put(obj.getObjectName().getName(), funcList);
			}
		}
	}
	
	//Update the list of recognised external functions
	@Check
	public void updateExternalFunctionList(Mcl mcl){
		externalFunctions.clear();
		for (MclObject obj: mcl.getObjects()){
			ArrayList<String> funcList =  new ArrayList<String>();
			if (obj.getModelObject() != null){
				ModelObject modelObj = obj.getModelObject();
				for (ModelObjectBlock block : modelObj.getBlocks()){
					Utils.addSymbol(funcList, block.getImportBlock());
				}
				externalFunctions.put(obj.getObjectName().getName(), funcList);
			}
			if (obj.getParameterObject() != null){
				ParameterObject paramObj = obj.getParameterObject();
				for (ParameterObjectBlock block : paramObj.getBlocks()){
					Utils.addSymbol(funcList, block.getImportBlock());
				}
				externalFunctions.put(obj.getObjectName().getName(), funcList);
			}
			if (obj.getDataObject() != null){
				DataObject dataObj = obj.getDataObject();
				for (DataObjectBlock block : dataObj.getBlocks()){
					Utils.addSymbol(funcList, block.getImportBlock());
				}
				externalFunctions.put(obj.getObjectName().getName(), funcList);
			}
			if (obj.getTaskObject() != null){
				TaskObject taskObj = obj.getTaskObject();
				for (TaskObjectBlock block : taskObj.getBlocks()){
					Utils.addSymbol(funcList, block.getImportBlock());
				}
				externalFunctions.put(obj.getObjectName().getName(), funcList);
			}
		}
	}
	
	//Check whether the function with such a name is already defined
	@Check
	public void checkFunctionDeclaration(TaskFunctionDeclaration func){
		if (Utils.isSymbolDeclaredMoreThanOnce(declaredFunctions, func.getFunctionName().getName())){
			warning(MSG_FUNCTION_DEFINED, MdlPackage.Literals.TASK_FUNCTION_DECLARATION__FUNCTION_NAME);
		}
	}
	
	//Check whether the function with such a name is already defined
	@Check
	public void checkExportFunctionDeclaration(ImportedFunction func){
		if (Utils.isSymbolDeclaredMoreThanOnce(externalFunctions, func.getFunctionName().getName())){
			warning(MSG_FUNCTION_DEFINED, MdlPackage.Literals.IMPORTED_FUNCTION__FUNCTION_NAME);
		}
		if (Utils.isSymbolDeclared(declaredFunctions, func.getFunctionName().getName(), null)){
			warning(MSG_FUNCTION_DEFINED, MdlPackage.Literals.IMPORTED_FUNCTION__FUNCTION_NAME);
		}
		//Check that it is not standard and was not defined in Task objects
	}	
	
	//Check that the function call is to an existing function
	@Check
	public void checkFunctionCall(FunctionCall call) {
		if (!isStandardFunction(call.getIdentifier().getSymbol().getName())){
			if(!(Utils.isSymbolDeclared(declaredFunctions, call.getIdentifier().getSymbol().getName(), call.getIdentifier().getObject()))
				&& !(Utils.isSymbolDeclared(externalFunctions, call.getIdentifier().getSymbol().getName(), call.getIdentifier().getObject()))){
			warning(MSG_FUNCTION_UNKNOWN, 
					MdlPackage.Literals.FUNCTION_CALL__IDENTIFIER,
					MSG_FUNCTION_UNKNOWN, call.getIdentifier().getSymbol().getName());
			} else {
				//declared functions (Task object) or external functions (IMPORT)
				//TODO: match number of actual parameters with the number of references in param attribute 
				//or input and output attributes together
				//TODO check parameter types
			}
		}
		else {//standard function
			//TODO: check number of expected parameters
			Integer expected = functionParameters.get(call.getIdentifier().getSymbol().getName());
			if (expected == null) expected = 1; //1 by default
			Integer actual = 0;
			if (call.getArguments().getArguments() != null)
				actual = call.getArguments().getArguments().size();
			if ((expected < 0) && (actual < -expected)){
				warning(MSG_FUNCTION_INVALID + ": " +  
						call.getIdentifier().getSymbol().getName() + " expects " + (-expected) + " or more parameters.", 
						MdlPackage.Literals.FUNCTION_CALL__ARGUMENTS,
						MSG_FUNCTION_INVALID, 
						call.getIdentifier().getSymbol().getName());
			}
			if ((expected >= 0) && (actual < expected)){
				warning(MSG_FUNCTION_INVALID + ": " +
						call.getIdentifier().getSymbol().getName() + " expects " + expected + " parameter(s).", 
						MdlPackage.Literals.FUNCTION_CALL__ARGUMENTS,
						MSG_FUNCTION_INVALID, 
						call.getIdentifier().getSymbol().getName());
			}
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	@Check
	public void checkRequiredProperties(EstimateTask t){
		HashSet<String> properties = new HashSet<String>();
		for (BlockStatement b: t.getStatements()){
			if (b.getSymbol() != null){
				if (!properties.contains(b.getSymbol().getSymbolName().getName())){
					properties.add(b.getSymbol().getSymbolName().getName());
				}
				else {
					warning(MSG_FUNCTION_PROPERTY_DEFINED + ": " + b.getSymbol().getSymbolName().getName(), 
							MdlPackage.Literals.ESTIMATE_TASK__IDENTIFIER,
							MSG_FUNCTION_PROPERTY_DEFINED, b.getSymbol().getSymbolName().getName());	
				}
			}
		}	
		for (String attrName: getRequiredAttributeNames(t)){
			if (!properties.contains(attrName)) 
				warning(MSG_FUNCTION_PROPERTY_MISSING + ": " + attrName, 
				MdlPackage.Literals.ESTIMATE_TASK__IDENTIFIER, MSG_FUNCTION_PROPERTY_MISSING, attrName);
		}
	}

	@Check
	public void checkRequiredProperties(SimulateTask t){
		HashSet<String> properties = new HashSet<String>();
		for (BlockStatement b: t.getStatements()){
			if (b.getSymbol() != null){
				if (!properties.contains(b.getSymbol().getSymbolName().getName())){
					properties.add(b.getSymbol().getSymbolName().getName());
				}
				else {
					warning(MSG_FUNCTION_PROPERTY_DEFINED + ": " + b.getSymbol().getSymbolName().getName(), 
							MdlPackage.Literals.SIMULATE_TASK__IDENTIFIER,
							MSG_FUNCTION_PROPERTY_DEFINED, b.getSymbol().getSymbolName().getName());	
				}
			}
		}
		for (String attrName: getRequiredAttributeNames(t)){
			if (!properties.contains(attrName)) 
				warning(MSG_FUNCTION_PROPERTY_MISSING + ": " + attrName, 
				MdlPackage.Literals.SIMULATE_TASK__IDENTIFIER, MSG_FUNCTION_PROPERTY_MISSING, attrName);
		}
	}

	@Check
	public void checkRequiredProperties(ExecuteTask t){
		HashSet<String> properties = new HashSet<String>();
		for (BlockStatement b: t.getStatements()){
			if (b.getSymbol() != null){
				if (!properties.contains(b.getSymbol().getSymbolName().getName())){
					properties.add(b.getSymbol().getSymbolName().getName());
				}
				else {
					warning(MSG_FUNCTION_PROPERTY_DEFINED + ": " + b.getSymbol().getSymbolName().getName(), 
							MdlPackage.Literals.EXECUTE_TASK__IDENTIFIER,
							MSG_FUNCTION_PROPERTY_DEFINED, b.getSymbol().getSymbolName().getName());	
				}
			}
		}	
		for (String attrName: getRequiredAttributeNames(t)){
			if (!properties.contains(attrName)) 
				warning(MSG_FUNCTION_PROPERTY_MISSING + ": " + attrName, 
				MdlPackage.Literals.EXECUTE_TASK__IDENTIFIER, MSG_FUNCTION_PROPERTY_MISSING, attrName);
		}
	}
	
	@Check
	public void checkAllProperties(SymbolDeclaration s){
		EObject container = s.eContainer();
		while (container instanceof BlockStatementImpl)
			container = container.eContainer();
		if (container instanceof EstimateTaskImpl ||
			container instanceof SimulateTaskImpl ||
			container instanceof ExecuteTaskImpl){
			//check that an argument is recognized
			List<Attribute> knownAttributes = getAllAttributes(container);
			if (knownAttributes != null){
				List<String> attributeNames = Utils.getAllNames(knownAttributes);
				if (!attributeNames.contains(s.getSymbolName().getName())){
					warning(MSG_FUNCTION_PROPERTY_UNKNOWN + ": " + s.getSymbolName().getName(), 
					MdlPackage.Literals.SYMBOL_DECLARATION__SYMBOL_NAME,
					MSG_FUNCTION_PROPERTY_UNKNOWN, s.getSymbolName().getName());		
					return;
				}
			}			
		}
	}
	
	List<Attribute> getAllAttributes(EObject obj){
		if (obj instanceof EstimateTaskImpl || obj instanceof SimulateTaskImpl)
			return attrs_task;
		if (obj instanceof ExecuteTaskImpl)
			return attrs_exec_task;
		return null;
	}
	
	List<String> getRequiredAttributeNames(EObject obj){
		if (obj instanceof EstimateTaskImpl || obj instanceof SimulateTaskImpl)
			return Utils.getRequiredNames(attrs_task);
		if (obj instanceof ExecuteTaskImpl)
			return Utils.getRequiredNames(attrs_exec_task);
		return null;
	}	
	
}
