/*
* generated by Xtext
*/
package org.ddmore.mdl.ui.outline;

import static org.ddmore.mdl.ui.outline.Images.*;

import org.ddmore.mdl.mdl.AndExpression;
import org.ddmore.mdl.mdl.AnyExpression;
import org.ddmore.mdl.mdl.Argument;
import org.ddmore.mdl.mdl.Arguments;
import org.ddmore.mdl.mdl.DataObject;
import org.ddmore.mdl.mdl.DataObjectBlock;
import org.ddmore.mdl.mdl.DesignObject;
import org.ddmore.mdl.mdl.DesignObjectBlock;
import org.ddmore.mdl.mdl.EnumType;
import org.ddmore.mdl.mdl.Expression;
import org.ddmore.mdl.mdl.ExpressionBranch;
import org.ddmore.mdl.mdl.FormalArguments;
import org.ddmore.mdl.mdl.FullyQualifiedArgumentName;
import org.ddmore.mdl.mdl.FunctionCall;
import org.ddmore.mdl.mdl.FunctionCallStatement;
import org.ddmore.mdl.mdl.GroupVariablesBlockStatement;
import org.ddmore.mdl.mdl.IndividualVarType;
import org.ddmore.mdl.mdl.InputFormatType;
import org.ddmore.mdl.mdl.List;
import org.ddmore.mdl.mdl.MOGObject;
import org.ddmore.mdl.mdl.MOGObjectBlock;
import org.ddmore.mdl.mdl.Mcl;
import org.ddmore.mdl.mdl.MclObject;
import org.ddmore.mdl.mdl.MdlPackage;
import org.ddmore.mdl.mdl.MixtureBlock;
import org.ddmore.mdl.mdl.ModelObject;
import org.ddmore.mdl.mdl.ModelObjectBlock;
import org.ddmore.mdl.mdl.ModelPredictionBlockStatement;
import org.ddmore.mdl.mdl.ObjectName;
import org.ddmore.mdl.mdl.OrExpression;
import org.ddmore.mdl.mdl.ParExpression;
import org.ddmore.mdl.mdl.ParameterObject;
import org.ddmore.mdl.mdl.ParameterObjectBlock;
import org.ddmore.mdl.mdl.PropertyDeclaration;
import org.ddmore.mdl.mdl.RandomList;
import org.ddmore.mdl.mdl.SymbolDeclaration;
import org.ddmore.mdl.mdl.SymbolName;
import org.ddmore.mdl.mdl.TargetBlock;
import org.ddmore.mdl.mdl.TargetType;
import org.ddmore.mdl.mdl.TaskObject;
import org.ddmore.mdl.mdl.TaskObjectBlock;
import org.ddmore.mdl.mdl.TrialType;
import org.ddmore.mdl.mdl.UseType;
import org.ddmore.mdl.mdl.VarType;
import org.ddmore.mdl.mdl.VariabilityType;
import org.ddmore.mdl.mdl.VariableList;
import org.ddmore.mdl.mdl.Vector;
import org.ddmore.mdl.mdl.impl.ArgumentNameImpl;
import org.ddmore.mdl.mdl.impl.CovariateDefinitionBlockImpl;
import org.ddmore.mdl.mdl.impl.DataBlockImpl;
import org.ddmore.mdl.mdl.impl.DataInputBlockImpl;
import org.ddmore.mdl.mdl.impl.EstimateTaskImpl;
import org.ddmore.mdl.mdl.impl.GroupVariablesBlockImpl;
import org.ddmore.mdl.mdl.impl.IndividualVariablesBlockImpl;
import org.ddmore.mdl.mdl.impl.ModelPredictionBlockImpl;
import org.ddmore.mdl.mdl.impl.ObservationBlockImpl;
import org.ddmore.mdl.mdl.impl.OdeBlockImpl;
//import org.ddmore.mdl.mdl.impl.OutputVariablesBlockImpl;
import org.ddmore.mdl.mdl.impl.RandomVariableDefinitionBlockImpl;
import org.ddmore.mdl.mdl.impl.SimulateTaskImpl;
import org.ddmore.mdl.mdl.impl.StructuralBlockImpl;
import org.ddmore.mdl.mdl.impl.StructuralParametersBlockImpl;
import org.ddmore.mdl.mdl.impl.VarTypeImpl;
import org.ddmore.mdl.mdl.impl.VariabilityBlockImpl;
import org.ddmore.mdl.mdl.impl.VariabilityDefinitionBlockImpl;
import org.ddmore.mdl.mdl.impl.VariabilityParametersBlockImpl;
import org.ddmore.mdl.validation.PropertyValidator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.ui.IImageHelper;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;

import com.google.inject.Inject;

import eu.ddmore.converter.mdlprinting.MdlPrinter;

/**
 * customization of the default outline structure
 */
public class MdlOutlineTreeProvider extends DefaultOutlineTreeProvider {
	
	@Inject IImageHelper imageHelper;
	@Inject MdlPrinter mdlPrinter;
	
 	protected Image _image(Mcl e) {
        return imageHelper.getImage(getPath(MDL));
    }
	protected Image _image(ModelObject e) {
        return imageHelper.getImage(getPath(MODEL_OBJ));
    }
    protected Image _image(ParameterObject d) {
        return imageHelper.getImage(getPath(PARAMETER_OBJ));
    }
    protected Image _image(DataObject p) {
        return imageHelper.getImage(getPath(DATA_OBJ));
    }
    protected Image _image(TaskObject f) {
        return imageHelper.getImage(getPath(TASK_OBJ));
    }
    protected Image _image(DesignObject f) {
        return imageHelper.getImage(getPath(DESIGN_OBJ));
    }

    protected Image _image(MOGObject f) {
        return imageHelper.getImage(getPath(MOG_OBJ));
    }
    
    protected Image _image(ObjectName objName) {
    	return imageHelper.getImage(getPath(REFERENCE));
    }
    
    protected Image _image(FullyQualifiedArgumentName f) {
        return imageHelper.getImage(getPath(ATTRIBUTE_REFERENCE));
    }
    
    protected Image _image(TargetBlock f) {
        return imageHelper.getImage(getPath(TARGET));
    }

    protected Image _image(SymbolDeclaration s) {
        return imageHelper.getImage(getPath(SYMBOL_DECLARATION));
    }
    
    protected Image _image(VariabilityType e) {
        return imageHelper.getImage(getPath(VARIABILITY_TYPE));
    }
    
    protected Image _image(Vector f) {
        return imageHelper.getImage(getPath(VECTOR));
    }

    protected Image _image(PropertyDeclaration s) {
        //Decorate special properties?
        if (s.getPropertyName() != null){
        	if (s.getPropertyName().getName().equals(PropertyValidator.attr_data_ignore.getName())){
                return imageHelper.getImage(getPath(IGNORE));
        	}
        	if (s.getPropertyName().getName().equals(PropertyValidator.attr_data_accept.getName())){
                return imageHelper.getImage(getPath(ACCEPT));
        	}
        	if (s.getPropertyName().getName().equals(PropertyValidator.attr_data_drop.getName())){
                return imageHelper.getImage(getPath(DROP));
        	}
        	if (s.getPropertyName().getName().equals(PropertyValidator.attr_model_remove.getName())){
                return imageHelper.getImage(getPath(REMOVE));
        	}
        	if (s.getPropertyName().getName().equals(PropertyValidator.attr_model_add.getName())){
                return imageHelper.getImage(getPath(ADD));
        	}
        }
        return imageHelper.getImage(getPath(ATTRIBUTE));
    }


    protected Image _image(FunctionCallStatement f) {
        return imageHelper.getImage(getPath(FUNCTION_CALL_STATEMENT));
    }

    protected Image _image(RandomList r) {
        return imageHelper.getImage(getPath(RANDOM));
    }
    	    
    protected Image _image(List r) {
    	EObject container = r.eContainer();
    	if (container instanceof OdeBlockImpl)
    		return imageHelper.getImage(getPath(ODE)); // decorate ODE lists 
    	return imageHelper.getImage(getPath(LIST));
    }
    
    protected Image _image(FunctionCall r) {
        return imageHelper.getImage(getPath(FUNCTION));
    }
    
    protected Image _image(Argument f) {
        return imageHelper.getImage(getPath(ATTRIBUTE));
    }
    
    protected Image _image(ParExpression e) {
        return imageHelper.getImage(getPath(EXPRESSION));
    }
    
    protected Image _image(OrExpression e) {
        return imageHelper.getImage(getPath(EXPRESSION_OR));
    }

    protected Image _image(AndExpression e) {
        return imageHelper.getImage(getPath(EXPRESSION_AND));
    }
    
    protected Image _image(MixtureBlock e) {
        return imageHelper.getImage(getPath(MIXTURE));
    }

    protected Image _image(StructuralBlockImpl e) {
        return imageHelper.getImage(getPath(STRUCTURAL_BLOCK));
    }

    protected Image _image(VariabilityBlockImpl e) {
        return imageHelper.getImage(getPath(VARIABILITY_BLOCK));
    }

    protected Image _image(CovariateDefinitionBlockImpl e) {
        return imageHelper.getImage(getPath(COVARIATE_DEFINITION_BLOCK));
    }

    protected Image _image(VariabilityDefinitionBlockImpl e) {
        return imageHelper.getImage(getPath(VARIABILITY_DEFINITION_BLOCK));
    }

    protected Image _image(StructuralParametersBlockImpl e) {
        return imageHelper.getImage(getPath(STRUCTURAL_PARAMETERS_BLOCK));
    }

    protected Image _image(VariabilityParametersBlockImpl e) {
        return imageHelper.getImage(getPath(VARIABILITY_PARAMETERS_BLOCK));
    }

    protected Image _image(ModelPredictionBlockImpl e) {
        return imageHelper.getImage(getPath(MODEL_PREDICTION_BLOCK));
    }

    protected Image _image(ObservationBlockImpl e) {
        return imageHelper.getImage(getPath(OBSERVATIONS_BLOCK));
    }

    /*
    protected Image _image(OutputVariablesBlockImpl e) {
        return imageHelper.getImage(getPath(OUTPUT_VARIABLES_BLOCK));
    }*/

    protected Image _image(TargetType e) {
        return imageHelper.getImage(getPath(TARGET_LANGUAGE));
    }
    
    protected Image _image(UseType e) {
        return imageHelper.getImage(getPath(USE_TYPE));
    }

    protected Image _image(VarTypeImpl e) {
        return imageHelper.getImage(getPath(CC_TYPE));
    }

    protected Image _image(ArgumentNameImpl e) {
        return imageHelper.getImage(getPath(ARGUMENT_NAME));
    }

    protected Image _image(EstimateTaskImpl e) {
        return imageHelper.getImage(getPath(ESTIMATE_TASK));
    }

    protected Image _image(SimulateTaskImpl e) {
        return imageHelper.getImage(getPath(SIMULATE_TASK));
    }

    protected Image _image(GroupVariablesBlockImpl e) {
        return imageHelper.getImage(getPath(GROUP_VARIABLES_BLOCK));
    }
    
    protected Image _image(RandomVariableDefinitionBlockImpl e) {
        return imageHelper.getImage(getPath(RANDOM_VARIABLES_BLOCK));
    }
    
    protected Image _image(IndividualVariablesBlockImpl e) {
        return imageHelper.getImage(getPath(INDIVIDUAL_VARIABLES_BLOCK));
    }
    
    protected Image _image(DataInputBlockImpl e) {
        return imageHelper.getImage(getPath(DATA_INPUT_BLOCK));
    }
    
    protected Image _image(DataBlockImpl e) {
        return imageHelper.getImage(getPath(DATA_BLOCK));
    }
    
    protected Image getLogImage(){
    	return imageHelper.getImage(getPath(LOG));
    }
    
    protected Image getTrueImage() {
        return imageHelper.getImage(getPath(TRUE));
    }
    
    protected Image getFalseImage() {
        return imageHelper.getImage(getPath(FALSE));    
    }
    
	protected void _createChildren(IOutlineNode parentNode, Mcl mcl) {
		for (EObject element : mcl.eContents()) {
			for (EObject obj: element.eContents()){
        		createNode(parentNode, obj);
			}
        }
    }
	
	protected void _createNode(IOutlineNode parentNode, Mcl obj) {
		createEStructuralFeatureNode(parentNode,
				obj,
				MdlPackage.Literals.MCL__OBJECTS,
				_image(obj),
				obj.eContainingFeature().getName(),
				false);
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//Print all object children
    /////////////////////////////////////////////////////////////////////////////
	
	protected void  _createNode(IOutlineNode parentNode, ModelObject obj) {
		createEStructuralFeatureNode(parentNode,
				obj,
				MdlPackage.Literals.MODEL_OBJECT__BLOCKS,
				_image(obj),
				((MclObject) obj.eContainer()).getObjectName().getName(),
			false);
	}
	
	protected void  _createNode(IOutlineNode parentNode, ParameterObject obj) {
		createEStructuralFeatureNode(parentNode,
				obj,
				MdlPackage.Literals.PARAMETER_OBJECT__BLOCKS,
				_image(obj),
				((MclObject) obj.eContainer()).getObjectName().getName(),
				false);
	}

	protected void  _createNode(IOutlineNode parentNode, TaskObject obj) {
		createEStructuralFeatureNode(parentNode,
			obj,
			MdlPackage.Literals.TASK_OBJECT__BLOCKS,
			_image(obj),
			((MclObject) obj.eContainer()).getObjectName().getName(),
			false);
	}
	
	protected void  _createNode(IOutlineNode parentNode, DesignObject obj) {
		createEStructuralFeatureNode(parentNode,
				obj,
				MdlPackage.Literals.DESIGN_OBJECT__BLOCKS,
				_image(obj),
				((MclObject) obj.eContainer()).getObjectName().getName(),
			false);
	}
	
	protected void  _createNode(IOutlineNode parentNode, DataObject obj) {
		createEStructuralFeatureNode(parentNode,
			obj,
			MdlPackage.Literals.DATA_OBJECT__BLOCKS,
			_image(obj),
			((MclObject) obj.eContainer()).getObjectName().getName(),
			false);
	}
		
	protected void  _createNode(IOutlineNode parentNode, MOGObject obj) {
		createEStructuralFeatureNode(parentNode,
			obj,
			MdlPackage.Literals.MOG_OBJECT__BLOCKS,
			_image(obj),
			((MclObject) obj.eContainer()).getObjectName().getName(),
			false);
	}
		
	protected void  _createNode(IOutlineNode parentNode, ObjectName name){
	}
				
	/////////////////////////////////////////////////////////////////////////////
	//Print object blocks
    /////////////////////////////////////////////////////////////////////////////

	protected void  _createNode(IOutlineNode parentNode, ModelObjectBlock block){
		for (EObject obj: block.eContents())
			createNode(parentNode, obj);
	}
		
	
	protected void  _createNode(IOutlineNode parentNode, TaskObjectBlock block) {
		for (EObject obj: block.eContents()){
			createNode(parentNode, obj);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, ParameterObjectBlock block) {
		for (EObject obj: block.eContents()){
			createNode(parentNode, obj);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, DataObjectBlock block) {
		for (EObject obj: block.eContents()){
			createNode(parentNode, obj);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, DesignObjectBlock block) {
		for (EObject obj: block.eContents()){
			createNode(parentNode, obj);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, MOGObjectBlock block){
		for (EObject obj: block.eContents())
			createNode(parentNode, obj);
	}
	
	protected void  _createNode(IOutlineNode parentNode, MixtureBlock b) {
		createEStructuralFeatureNode(parentNode,
			b,
			MdlPackage.Literals.MIXTURE_BLOCK__VARIABLES,
			_image(b),
			b.getIdentifier(),
		false);
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//Skip subblock statements

	protected void  _createNode(IOutlineNode parentNode, VariableList st){
		for (EObject obj: st.eContents()){
			createNode(parentNode, obj);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, GroupVariablesBlockStatement st){
		for (EObject obj: st.eContents()){
			createNode(parentNode, obj);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, ModelPredictionBlockStatement st){
		for (EObject obj: st.eContents()){
			createNode(parentNode, obj);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, Arguments st){
		for (EObject obj: st.eContents()){
			createNode(parentNode, obj);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, Argument a){
		if (a.getArgumentName() != null){
			createEStructuralFeatureNode(parentNode,
				a,
				MdlPackage.Literals.ARGUMENT__EXPRESSION,
				_image(a),
				a.getArgumentName().getName(),
				false);
		} else {
			if (a.getExpression() != null)
				createNode(parentNode, a.getExpression());
			else if (a.getRandomList() != null)
				createNode(parentNode, a.getRandomList());
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, FormalArguments st){
		for (EObject obj: st.eContents()){
			createNode(parentNode, obj);
		}
	}	
	
	protected void  _createNode(IOutlineNode parentNode, AnyExpression st){
		for (EObject obj: st.eContents()){
			createNode(parentNode, obj);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, SymbolName name){
		createEStructuralFeatureNode(parentNode,
				name,
				MdlPackage.Literals.SYMBOL_NAME__NAME,
				_image(name),
				name.getName(),
				true);
	}

	protected void  _createNode(IOutlineNode parentNode, FullyQualifiedArgumentName name){
		createEStructuralFeatureNode(parentNode,
				name,
				MdlPackage.Literals.FULLY_QUALIFIED_ARGUMENT_NAME__PARENT,
				_image(name),
				mdlPrinter.toStr(name),
				true);
	}
	

	protected void  _createNode(IOutlineNode parentNode, ParExpression e){
		createEStructuralFeatureNode(parentNode,
				e,
				MdlPackage.Literals.PAR_EXPRESSION__EXPRESSION,
				_image(e),
				mdlPrinter.toStr(e),
				true);
	}
	
	protected void  _createNode(IOutlineNode parentNode, FunctionCall st) {
			createEStructuralFeatureNode(parentNode,
				st,
				MdlPackage.Literals.FUNCTION_CALL__ARGUMENTS,
				_image(st),
				st.getIdentifier().getName(),
				false);
	}

	protected void  _createNode(IOutlineNode parentNode, OrExpression e){
		createEStructuralFeatureNode(parentNode,
			e,
			MdlPackage.Literals.OR_EXPRESSION__EXPRESSION,
			_image(e),
			mdlPrinter.toStr(e),
			true);
	}
	
	protected void  _createNode(IOutlineNode parentNode, AndExpression e){
		createEStructuralFeatureNode(parentNode,
			e,
			MdlPackage.Literals.AND_EXPRESSION__EXPRESSION,
			_image(e),
			mdlPrinter.toStr(e),
			true);
	}
		
	protected void  _createNode(IOutlineNode parentNode, SymbolDeclaration p){
		String name = "";
		if (p.getSymbolName() != null)
			name = 	p.getSymbolName().getName();
		else name = mdlPrinter.toStr(p.getArgumentName());
		if (p.getExpression() != null){
			createEStructuralFeatureNode(parentNode,
				p,
				MdlPackage.Literals.SYMBOL_DECLARATION__EXPRESSION,
				(p.getFunctionName() != null)? getLogImage(): _image(p),
				(p.getFunctionName() != null)? p.getFunctionName().getName() + 
						'(' + name + ')' : name,
				false);
		}
		if (p.getList() != null){
			createEStructuralFeatureNode(parentNode,
				p,
				MdlPackage.Literals.SYMBOL_DECLARATION__LIST,
				(p.getFunctionName() != null)? getLogImage(): _image(p),
				(p.getFunctionName() != null)? p.getFunctionName().getName() + 
						'(' + name + ')' : name,
				false);
		}
		if (p.getRandomList() != null){
			createEStructuralFeatureNode(parentNode,
				p,
				MdlPackage.Literals.SYMBOL_DECLARATION__RANDOM_LIST,
				(p.getFunctionName() != null)? getLogImage(): _image(p),
				(p.getFunctionName() != null)? p.getFunctionName().getName() + 
						'(' + name + ')' : name,
				false);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, List l){
		createEStructuralFeatureNode(parentNode,
			l,
			MdlPackage.Literals.LIST__ARGUMENTS,
			_image(l),
			"list",
			false);
	}
	
	protected void  _createNode(IOutlineNode parentNode, Vector v){
		createEStructuralFeatureNode(parentNode,
			v, MdlPackage.Literals.VECTOR__EXPRESSION,
			_image(v),
			mdlPrinter.toStr(v),
			true);
	}

	protected void  _createNode(IOutlineNode parentNode, PropertyDeclaration a){
		createEStructuralFeatureNode(parentNode,
			a,
			MdlPackage.Literals.PROPERTY_DECLARATION__EXPRESSION,
			_image(a),
			a.getPropertyName().getName(),
			false);
	}
	
	protected void  _createNode(IOutlineNode parentNode, FunctionCallStatement s){
		createEStructuralFeatureNode(parentNode,
			s,
			MdlPackage.Literals.FUNCTION_CALL_STATEMENT__EXPRESSION,
			_image(s),
			 s.getSymbolName().getName(),
		false);
	}
	
	protected void  _createNode(IOutlineNode parentNode, EnumType b){
		if (b.getType() != null){
			createNode(parentNode, b.getType());
		} else if (b.getUse() != UseType.NO_USE){
			createEStructuralFeatureNode(parentNode,
			b, MdlPackage.Literals.ENUM_TYPE__USE,
			_image(b), b.getUse().toString(), true);
		} else if (b.getIndividualVar() != IndividualVarType.NO_INDIVIDUAL_VAR){
			createEStructuralFeatureNode(parentNode,
			b, MdlPackage.Literals.ENUM_TYPE__INDIVIDUAL_VAR,
			_image(b), b.getIndividualVar().toString(), true); 
		} else if (b.getInput() != InputFormatType.NO_INPUT_FORMAT){
			createEStructuralFeatureNode(parentNode,
			b, MdlPackage.Literals.ENUM_TYPE__INPUT,
			_image(b), b.getInput().toString(), true); 
		} else if (b.getTarget() != TargetType.NO_TARGET){
			createEStructuralFeatureNode(parentNode,
			b, MdlPackage.Literals.ENUM_TYPE__TARGET,
			_image(b), b.getTarget().toString(), true); 
		} else if (b.getTrial() != TrialType.NO_TRIAL){
			createEStructuralFeatureNode(parentNode,
			b, MdlPackage.Literals.ENUM_TYPE__TRIAL,
			_image(b), b.getTrial().toString(), true); 
		} else if (b.getVariability() != VariabilityType.NO_VARIABILITY){
			createEStructuralFeatureNode(parentNode,
			b, MdlPackage.Literals.ENUM_TYPE__VARIABILITY,
			_image(b), b.getVariability().toString(), true); 
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, VarType t){
		if (t.getCategorical() != null)
			createEStructuralFeatureNode(parentNode,
				t,
				MdlPackage.Literals.VAR_TYPE__CATEGORICAL,
				_image(t),
				mdlPrinter.toStr(t),
				true);
		if (t.getContinuous() != null)
			createEStructuralFeatureNode(parentNode,
				t,
				MdlPackage.Literals.VAR_TYPE__CONTINUOUS,
				_image(t),
				mdlPrinter.toStr(t),
				true);
		if (t.getLikelihood() != null)
			createEStructuralFeatureNode(parentNode,
				t,
				MdlPackage.Literals.VAR_TYPE__LIKELIHOOD,
				_image(t),
				mdlPrinter.toStr(t),
				true);
		if (t.getM2LL() != null)
			createEStructuralFeatureNode(parentNode,
				t,
				MdlPackage.Literals.VAR_TYPE__M2LL,
				_image(t),
				mdlPrinter.toStr(t),
				true);
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	//Show expression as a leaf node
	/////////////////////////////////////////////////////////////////////////////////////
	protected void  _createNode(IOutlineNode parentNode, Expression e){
		createEStructuralFeatureNode(parentNode,
			e,
			MdlPackage.Literals.EXPRESSION__EXPRESSION,
			_image(e),
			mdlPrinter.toStr(e.getExpression()) + 
				((e.getCondition() != null)? " when " + mdlPrinter.toStr(e.getCondition()): ""),
		true);
		if (e.getWhenBranches() != null){
			for (ExpressionBranch b: e.getWhenBranches()){
				createEStructuralFeatureNode(parentNode,
					e,
					MdlPackage.Literals.EXPRESSION_BRANCH__EXPRESSION,
					_image(e),
					mdlPrinter.toStr(b.getExpression()) +
						" when " + mdlPrinter.toStr(b.getCondition()),
					true);
			}
		}		
		if (e.getElseExpression() != null){
			createEStructuralFeatureNode(parentNode,
					e,
					MdlPackage.Literals.EXPRESSION__ELSE_EXPRESSION,
					_image(e),
					mdlPrinter.toStr(e.getElseExpression()) + " otherwise",
				true);
		}
	}
}

