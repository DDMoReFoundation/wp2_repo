package org.ddmore.mdl.validation;

import java.util.ArrayList;
import java.util.List;

import org.ddmore.mdl.mdl.DataObject;
import org.ddmore.mdl.mdl.DataObjectBlock;
import org.ddmore.mdl.mdl.ExpressionDeclaration;
import org.ddmore.mdl.mdl.ImportObjectBlock;
import org.ddmore.mdl.mdl.ImportObjectStatement;
import org.ddmore.mdl.mdl.ListDeclaration;
import org.ddmore.mdl.mdl.MOGObject;
import org.ddmore.mdl.mdl.MclObject;
import org.ddmore.mdl.mdl.MdlPackage;
import org.ddmore.mdl.mdl.ModelObject;
import org.ddmore.mdl.mdl.ModelObjectBlock;
import org.ddmore.mdl.mdl.ParameterObject;
import org.ddmore.mdl.mdl.ParameterObjectBlock;
import org.ddmore.mdl.mdl.ReferenceDeclaration;
import org.ddmore.mdl.mdl.SymbolDeclaration;
import org.ddmore.mdl.mdl.SymbolRef;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

import com.google.inject.Inject;

public class MOGValidator extends AbstractDeclarativeValidator{

	public final static String MSG_MODEL_OBJ_MISSING = "MOG should include a model object";
	public final static String MSG_DATA_OBJ_MISSING  = "MOG should include a data object";
	public final static String MSG_PARAM_OBJ_MISSING = "MOG should include a parameter object";
	public final static String MSG_TASK_OBJ_MISSING  = "MOG should include a task object";
	public final static String MSG_OBJ_DEFINED          = "Cannot create a MOG";
	public final static String MSG_MODEL_DATA_MISMATCH  = "Inconsistent sets of model/data variables";
	public final static String MSG_STRUCTURAL_MISMATCH  = "Inconsistent sets of structural parameters";
	public final static String MSG_VARIABILITY_MISMATCH = "Inconsistent sets of variability parameters";
	public final static String MSG_MOG_FILE_NOT_FOUND   = "Cannot find MDL file: path may be incorrect";
	public final static String MSG_MOG_OBJECT_NOT_FOUND = "Incorrect reference to an MDL object";

	@Override
    @Inject
    public void register(EValidatorRegistrar registrar) {}

	/*Validate that MDL files are in the workspace*/
//	@Check
//	public void validateMOGObjectFiles(ImportObjectStatement s){
//		if (s.getImportURI() != null){
//		    IFile file = Utils.getFile(s, s.getImportURI());
//		    if (!file.exists()){
//				warning(MSG_MOG_FILE_NOT_FOUND, 
//					MdlPackage.Literals.IMPORT_OBJECT_STATEMENT__IMPORT_URI,
//					MSG_MOG_FILE_NOT_FOUND, s.getImportURI());
//			} else {
//				String mclObjeResource = s.getObjectName().eResource().getURI().path();
//				mclObjeResource = FilenameUtils.getBaseName(mclObjeResource);
//				String importResource = FilenameUtils.getBaseName(s.getImportURI());
//				if (!mclObjeResource.equalsIgnoreCase(importResource)){
//					warning(MSG_MOG_OBJECT_NOT_FOUND + ": " +
//						"the object is not in the imported file or project contains several objects with the same name.\n" + 
//						"Object resource: " + mclObjeResource + "\n" +
//						"Imported resource: " + importResource,
//						MdlPackage.Literals.IMPORT_OBJECT_STATEMENT__OBJECT_NAME,
//						MSG_MOG_OBJECT_NOT_FOUND, s.getObjectName().getName());
//				}
//			}
//		}
//	}
	
	/*Validate MOG object set*/
	@Check
	public void validateMOGObjectSet(ImportObjectBlock objBlock){
		Integer [] params = {0, 0, 0, 0};
		List<SymbolRef> objList = new ArrayList<SymbolRef>();
		for (ImportObjectStatement s: objBlock.getObjects()){
			if (s.getObjectRef() != null)
				objList.add(s.getObjectRef());
		}
		for (SymbolRef objName: objList){
			if (objName.getSymbolRef() instanceof MclObject){
				MclObject obj = (MclObject)objName.getSymbolRef();
				if (obj != null){
					if (obj.getModelObject() != null) params[0] += 1;
					if (obj.getParameterObject() != null) params[1] += 1;
					if (obj.getDataObject() != null)  params[2] += 1;
					if (obj.getTaskObject() != null)  params[3] += 1;
				}
			}
		}
		if (params[0] == 0)
			error(MSG_MODEL_OBJ_MISSING, 
				MdlPackage.Literals.IMPORT_OBJECT_BLOCK__OBJECTS,
				MSG_MODEL_OBJ_MISSING, objBlock.getIdentifier());
		if (params[1] == 0)
			error(MSG_PARAM_OBJ_MISSING, 
					MdlPackage.Literals.IMPORT_OBJECT_BLOCK__OBJECTS,
					MSG_PARAM_OBJ_MISSING, objBlock.getIdentifier());
		if (params[2] == 0)
			error(MSG_DATA_OBJ_MISSING, 
					MdlPackage.Literals.IMPORT_OBJECT_BLOCK__OBJECTS,
					MSG_DATA_OBJ_MISSING, objBlock.getIdentifier());
		if (params[3] == 0)
			error(MSG_TASK_OBJ_MISSING, 
					MdlPackage.Literals.IMPORT_OBJECT_BLOCK__OBJECTS,
					MSG_TASK_OBJ_MISSING, objBlock.getIdentifier());
		String [] names = {"model", "parameter", "data", "task"};
		for (int i = 0; i < 4; i++){
			if (params[i] > 1)
				error(MSG_OBJ_DEFINED + ": two or more " + names[i] + " objects selected!", 
					MdlPackage.Literals.IMPORT_OBJECT_BLOCK__OBJECTS,
					MSG_OBJ_DEFINED,  objBlock.getIdentifier());
		}			
		for (int i = 0; i < 4; i++)
			if (params[i] != 1) return;
	}
	
	/*Validate objects grouped to a MOG*/
	@Check
	public void validateMOG(MOGObject mog){
		List<MclObject> objects = Utils.getMOGObjects(mog);
		ModelObject mObj        = Utils.getModelObject(objects);
		ParameterObject pObj    = Utils.getParameterObject(objects);
		DataObject dObj         = Utils.getDataObject(objects);
		//Model object vs data object
		if (mObj != null && dObj != null)
			validateMOG_Model_vs_Data(mObj, dObj, mog);
		//Model object vs. parameter object
		if (mObj != null && pObj != null){
			validateMOG_Structural_Model_vs_Parameter(mObj, pObj, mog);
			validateMOG_Variability_Model_vs_Parameter(mObj, pObj, mog);
		}
	}	
	
	//TODO
	//Check crossmatched variable attribute dependencies:
	  //required level vs. use=dv, use=id
	  //optional administration vs. use=amt
	  //required prediction vs. use=dv		
	  //required type vs. use = covariate

	
	//MODEL_INPUT_VARIABLES \in COVARIATE and VARIABILITY
	private void validateMOG_Model_vs_Data(ModelObject mObj, DataObject dObj, MOGObject mog){
		List<String> dVars = new ArrayList<String>();
		for (DataObjectBlock b: dObj.getBlocks()){
			if (b.getDataInputBlock() != null)
				for (ListDeclaration s: b.getDataInputBlock().getVariables())
					if (s.getName() != null) dVars.add(s.getName());
			if (b.getDataDerivedBlock() != null)
				for (ExpressionDeclaration s: b.getDataDerivedBlock().getVariables())
					if (s.getName() != null) dVars.add(s.getName());
			if (b.getDeclaredVariables() != null)
				for (ReferenceDeclaration s: b.getDeclaredVariables().getVariables())
					if (s.getName() != null) dVars.add(s.getName());

		}
		for (ModelObjectBlock b: mObj.getBlocks()){
			if (b.getCovariateBlock() != null){
				for (SymbolDeclaration s: b.getCovariateBlock().getVariables()){
					//Math only unassigned covariates
					if (s.getName() != null && s.getExpression() == null) {
//						String dVarName = Utils.getMatchingVariable(mog, s.getSymbolName());
//						if (dVarName == null) dVarName = s.getSymbolName().getName();
						String dVarName = s.getName();
						if (!dVars.contains(dVarName))
							error(MSG_MODEL_DATA_MISMATCH + 
								": no mapping for model variable " + s.getName() + " found in " + 
								Utils.getObjectName(dObj) + " object", 
								MdlPackage.Literals.MOG_OBJECT__IDENTIFIER,
								MSG_MODEL_DATA_MISMATCH,  mog.getIdentifier());
					}
				}
			}
			if (b.getVariabilityBlock() != null){
				for (SymbolDeclaration s: b.getVariabilityBlock().getVariables()){
					if (s.getName() != null) {
//						String dVarName = Utils.getMatchingVariable(mog, s.getSymbolName());
//						if (dVarName == null) dVarName = s.getSymbolName().getName();
						String dVarName = s.getName();
						if (!dVars.contains(dVarName))
							error(MSG_MODEL_DATA_MISMATCH + 
								": no mapping for model variable " + s.getName() + " found in " + 
								Utils.getObjectName(dObj) + " object", 
								MdlPackage.Literals.MOG_OBJECT__IDENTIFIER,
								MSG_MODEL_DATA_MISMATCH,  mog.getIdentifier());
					}
				}
			}
		}
	}
	
	//STRUCTURAL vs. STRUCTURAL_PARAMETERS
	private void validateMOG_Structural_Model_vs_Parameter(ModelObject mObj, ParameterObject pObj, MOGObject mog){
		List<String> structuralVars = new ArrayList<String>();
		//Get structural variables
		for (ParameterObjectBlock b: pObj.getBlocks())
			if (b.getStructuralBlock() != null)
				for (SymbolDeclaration s: b.getStructuralBlock().getParameters())
					if (s.getName() != null) structuralVars.add(s.getName());
		for (ModelObjectBlock b: mObj.getBlocks()){
			if (b.getStructuralParametersBlock() != null){
				for (SymbolDeclaration s: b.getStructuralParametersBlock().getParameters()){
					if (s.getName() != null){
						String varName = s.getName();
						if (varName.length() > 0){
							if (!structuralVars.contains(varName))
								error(MSG_STRUCTURAL_MISMATCH + 
									": no mapping for parameter " + varName + " found in " + 
									Utils.getObjectName(pObj) + " object", 
									MdlPackage.Literals.MOG_OBJECT__IDENTIFIER,
									MSG_STRUCTURAL_MISMATCH, mog.getIdentifier());
						}
					} 
				}
			}
		}
	}
	
	//VARIABILITY vs. VARIABILITY_PARAMETERS
	private void validateMOG_Variability_Model_vs_Parameter(ModelObject mObj, ParameterObject pObj, MOGObject mog){
		List<String> variabilityVars = new ArrayList<String>();
		//Get structural variables
		for (ParameterObjectBlock b: pObj.getBlocks())
			if (b.getVariabilityBlock() != null)
				for (SymbolDeclaration s: b.getVariabilityBlock().getParameters())
					if (s.getName() != null) variabilityVars.add(s.getName());
		for (ModelObjectBlock b: mObj.getBlocks()){
			if (b.getVariabilityParametersBlock() != null){
				for (SymbolDeclaration s: b.getVariabilityParametersBlock().getParameters()){
					if (s.getName() != null){
						String varName = s.getName();
						if (varName.length() > 0){
							if (!variabilityVars.contains(varName))
								error(MSG_VARIABILITY_MISMATCH + 
									": no mapping for parameter " + varName + " found in " + 
									Utils.getObjectName(pObj) + " object", 
									MdlPackage.Literals.MOG_OBJECT__IDENTIFIER,
									MSG_VARIABILITY_MISMATCH,  mog.getIdentifier());
						}
					} 
				}
			}
		}
	}
}