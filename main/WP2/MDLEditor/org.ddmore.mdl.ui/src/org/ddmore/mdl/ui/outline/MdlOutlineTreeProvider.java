/*
* generated by Xtext
*/
package org.ddmore.mdl.ui.outline;

import static org.ddmore.mdl.ui.outline.Images.*;

import org.ddmore.mdl.generator.MdlPrinting;
import org.ddmore.mdl.mdl.AcceptList;
import org.ddmore.mdl.mdl.AddList;
import org.ddmore.mdl.mdl.AndExpression;
import org.ddmore.mdl.mdl.AnyExpression;
import org.ddmore.mdl.mdl.Argument;
import org.ddmore.mdl.mdl.Arguments;
import org.ddmore.mdl.mdl.Block;
import org.ddmore.mdl.mdl.BlockStatement;
import org.ddmore.mdl.mdl.ConditionalExpression;
import org.ddmore.mdl.mdl.ConditionalStatement;
import org.ddmore.mdl.mdl.DataBlockStatement;
import org.ddmore.mdl.mdl.DataObject;
import org.ddmore.mdl.mdl.DataObjectBlock;
import org.ddmore.mdl.mdl.DesignBlockStatement;
import org.ddmore.mdl.mdl.DropList;
import org.ddmore.mdl.mdl.EnumType;
import org.ddmore.mdl.mdl.Expression;
import org.ddmore.mdl.mdl.FileBlockStatement;
import org.ddmore.mdl.mdl.FormalArguments;
import org.ddmore.mdl.mdl.FullyQualifiedSymbolName;
import org.ddmore.mdl.mdl.FunctionCall;
import org.ddmore.mdl.mdl.GroupVariablesBlockStatement;
import org.ddmore.mdl.mdl.IgnoreList;
import org.ddmore.mdl.mdl.ImportedFunction;
import org.ddmore.mdl.mdl.List;
import org.ddmore.mdl.mdl.Mcl;
import org.ddmore.mdl.mdl.MclObject;
import org.ddmore.mdl.mdl.MdlPackage;
import org.ddmore.mdl.mdl.MixtureBlock;
import org.ddmore.mdl.mdl.ModelBlockStatement;
import org.ddmore.mdl.mdl.ModelObject;
import org.ddmore.mdl.mdl.ModelObjectBlock;
import org.ddmore.mdl.mdl.ModelPredictionBlockStatement;
import org.ddmore.mdl.mdl.ObjectName;
import org.ddmore.mdl.mdl.OdeList;
import org.ddmore.mdl.mdl.OrExpression;
import org.ddmore.mdl.mdl.ParExpression;
import org.ddmore.mdl.mdl.ParameterDeclaration;
import org.ddmore.mdl.mdl.ParameterObject;
import org.ddmore.mdl.mdl.ParameterObjectBlock;
import org.ddmore.mdl.mdl.RandomList;
import org.ddmore.mdl.mdl.RemoveList;
import org.ddmore.mdl.mdl.SymbolDeclaration;
import org.ddmore.mdl.mdl.SymbolList;
import org.ddmore.mdl.mdl.SymbolModification;
import org.ddmore.mdl.mdl.TELObject;
import org.ddmore.mdl.mdl.TargetBlock;
import org.ddmore.mdl.mdl.TaskFunctionBlock;
import org.ddmore.mdl.mdl.TaskFunctionBody;
import org.ddmore.mdl.mdl.TaskObject;
import org.ddmore.mdl.mdl.TaskObjectBlock;
import org.ddmore.mdl.mdl.VariabilityBlockStatement;
import org.ddmore.mdl.mdl.VariableList;
import org.ddmore.mdl.mdl.impl.CategoricalImpl;
import org.ddmore.mdl.mdl.impl.ContinuousImpl;
import org.ddmore.mdl.mdl.impl.CovariateImpl;
import org.ddmore.mdl.mdl.impl.DataBlockImpl;
import org.ddmore.mdl.mdl.impl.EstimateTaskImpl;
import org.ddmore.mdl.mdl.impl.FormalArgumentImpl;
import org.ddmore.mdl.mdl.impl.GroupVariablesBlockImpl;
import org.ddmore.mdl.mdl.impl.HeaderBlockImpl;
import org.ddmore.mdl.mdl.impl.IndividualVariablesBlockImpl;
import org.ddmore.mdl.mdl.impl.InputVariablesBlockImpl;
import org.ddmore.mdl.mdl.impl.ModelPredictionBlockImpl;
import org.ddmore.mdl.mdl.impl.ObservationBlockImpl;
import org.ddmore.mdl.mdl.impl.OutputVariablesBlockImpl;
import org.ddmore.mdl.mdl.impl.RandomVariableDefinitionBlockImpl;
import org.ddmore.mdl.mdl.impl.StructuralBlockImpl;
import org.ddmore.mdl.mdl.impl.StructuralParametersBlockImpl;
import org.ddmore.mdl.mdl.impl.TaskFunctionDeclarationImpl;
import org.ddmore.mdl.mdl.impl.UseTypeImpl;
import org.ddmore.mdl.mdl.impl.VariabilityBlockImpl;
import org.ddmore.mdl.mdl.impl.VariabilityParametersBlockImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.ui.IImageHelper;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;

import com.google.inject.Inject;

/**
 * customization of the default outline structure
 * 
 */
public class MdlOutlineTreeProvider extends DefaultOutlineTreeProvider {
	@Inject
	private IImageHelper imageHelper;
	
	 	protected Image _image(Mcl e) {
	        return imageHelper.getImage(getPath(MDL));
	    }
		protected Image _image(ModelObject e) {
	        return imageHelper.getImage(getPath(MODEL));
	    }
	    protected Image _image(ParameterObject d) {
	        return imageHelper.getImage(getPath(PARAMETER));
	    }
	    protected Image _image(DataObject p) {
	        return imageHelper.getImage(getPath(DATA));
	    }
	    protected Image _image(TaskObject f) {
	        return imageHelper.getImage(getPath(TASK));
	    }
	    protected Image _image(TELObject f) {
	        return imageHelper.getImage(getPath(TEL));
	    }
	    
	    protected Image _image(ConditionalStatement f) {
	        return imageHelper.getImage(getPath(CONDITION));
	    }
	    
	    protected Image _image(FullyQualifiedSymbolName f) {
	        return imageHelper.getImage(getPath(REFERENCE));
	    }
	    
	    protected Image _image(ParameterDeclaration f) {
	        return imageHelper.getImage(getPath(PARAMETER_DECLARATION));
	    }
	    
	    protected Image _image(TargetBlock f) {
	        return imageHelper.getImage(getPath(TARGET));
	    }

	    protected Image _image(ImportedFunction f) {
	        return imageHelper.getImage(getPath(IMPORT));
	    }

	    protected Image _image(SymbolDeclaration f) {
	        return imageHelper.getImage(getPath(SYMBOL_DECLARATION));
	    }
	    
	    protected Image _image(RandomList r) {
	        return imageHelper.getImage(getPath(RANDOM));
	    }
	    	    
	    protected Image _image(List r) {
	        return imageHelper.getImage(getPath(LIST));
	    }

	    protected Image _image(AddList r) {
	        return imageHelper.getImage(getPath(ADD));
	    }
	    
	    protected Image _image(IgnoreList r) {
	        return imageHelper.getImage(getPath(IGNORE));
	    }
	    
	    protected Image _image(AcceptList r) {
	        return imageHelper.getImage(getPath(ACCEPT));
	    }	    

	    protected Image _image(RemoveList r) {
	        return imageHelper.getImage(getPath(REMOVE));
	    }

	    protected Image _image(DropList r) {
	        return imageHelper.getImage(getPath(DROP));
	    }

	    protected Image _image(FunctionCall r) {
	        return imageHelper.getImage(getPath(FUNCTION));
	    }
	    
	    protected Image _image(OdeList r) {
	        return imageHelper.getImage(getPath(ODE));
	    }
	    	    
	    protected Image _image(Argument f) {
	        return imageHelper.getImage(getPath(ATTRIBUTE));
	    }	    
	    
	    protected Image _image(SymbolModification f) {
	        return imageHelper.getImage(getPath(SYMBOL_MODIFICATION));
	    }
	    
	    protected Image _image(ConditionalExpression e) {
	        return imageHelper.getImage(getPath(EXPRESSION_CONDITION));
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

	    protected Image _image(InputVariablesBlockImpl e) {
	        return imageHelper.getImage(getPath(INPUT_VARIABLES_BLOCK));
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

	    protected Image _image(OutputVariablesBlockImpl e) {
	        return imageHelper.getImage(getPath(OUTPUT_VARIABLES_BLOCK));
	    }

	    protected Image _image(ContinuousImpl e) {
	        return imageHelper.getImage(getPath(CONTINUOUS));
	    }

	    protected Image _image(UseTypeImpl e) {
	        return imageHelper.getImage(getPath(USE_TYPE));
	    }

	    protected Image _image(CategoricalImpl e) {
	        return imageHelper.getImage(getPath(CATEGORICAL));
	    }

	    protected Image _image(CovariateImpl e) {
	        return imageHelper.getImage(getPath(COVARIATE));
	    }

	    protected Image _image(TaskFunctionDeclarationImpl e) {
	        return imageHelper.getImage(getPath(TASK_FUNCTION_DECLARATION));
	    }

	    protected Image _image(FormalArgumentImpl e) {
	        return imageHelper.getImage(getPath(FORMAL_ARGUMENT));
	    }

	    protected Image _image(EstimateTaskImpl e) {
	        return imageHelper.getImage(getPath(ESTIMATE_TASK));
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
	    protected Image _image(HeaderBlockImpl e) {
	        return imageHelper.getImage(getPath(HEADER_BLOCK));
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
	    
	static MdlPrinting printer = new MdlPrinting();
	
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
	
	
	protected void _createNode(IOutlineNode parentNode, MclObject obj) {
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//Print all object children
    /////////////////////////////////////////////////////////////////////////////
	
	protected void  _createNode(IOutlineNode parentNode, ModelObject obj) {
		createEStructuralFeatureNode(parentNode,
				obj,
				MdlPackage.Literals.MODEL_OBJECT__BLOCKS,
				_image(obj),
				((MclObject) obj.eContainer()).getIdentifier().getName(),
			false);
	}
	
	protected void  _createNode(IOutlineNode parentNode, ParameterObject obj) {
		createEStructuralFeatureNode(parentNode,
				obj,
				MdlPackage.Literals.PARAMETER_OBJECT__BLOCKS,
				_image(obj),
				((MclObject) obj.eContainer()).getIdentifier().getName(),
				false);
	}

	protected void  _createNode(IOutlineNode parentNode, TaskObject obj) {
		createEStructuralFeatureNode(parentNode,
				obj,
				MdlPackage.Literals.TASK_OBJECT__BLOCKS,
				_image(obj),
				((MclObject) obj.eContainer()).getIdentifier().getName(),
				false);
	}
	
	protected void  _createNode(IOutlineNode parentNode, DataObject obj) {
		createEStructuralFeatureNode(parentNode,
				obj,
				MdlPackage.Literals.DATA_OBJECT__BLOCKS,
				_image(obj),
				((MclObject) obj.eContainer()).getIdentifier().getName(),
				false);
	}
	
	
	protected void  _createNode(IOutlineNode parentNode, TELObject obj) {
		createEStructuralFeatureNode(parentNode,
				obj,
				MdlPackage.Literals.TEL_OBJECT__STATEMENTS,
				_image(obj),
				((MclObject) obj.eContainer()).getIdentifier().getName(),
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
	
	protected void  _createNode(IOutlineNode parentNode, BlockStatement st) {
		for (EObject obj: st.eContents()){
			createNode(parentNode, obj);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, MixtureBlock b) {
		createEStructuralFeatureNode(parentNode,
			b,
			MdlPackage.Literals.MIXTURE_BLOCK__STATEMENTS,
			_image(b),
			b.getIdentifier(),
		false);
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//Skip subblock statements

	protected void  _createNode(IOutlineNode parentNode, SymbolList st){
		for (EObject obj: st.eContents()){
			createNode(parentNode, obj);
		}
	}
	
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
	
	protected void  _createNode(IOutlineNode parentNode, VariabilityBlockStatement st){
		for (EObject obj: st.eContents()){
			createNode(parentNode, obj);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, FileBlockStatement st){
		for (EObject obj: st.eContents()){
			createNode(parentNode, obj);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, DataBlockStatement st){
		for (EObject obj: st.eContents()){
			createNode(parentNode, obj);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, ModelBlockStatement st){
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
		String attrName = "unnamed attribute";
		if (a.getIdentifier() != null)
			attrName = a.getIdentifier();
		createEStructuralFeatureNode(parentNode,
				a,
				MdlPackage.Literals.ARGUMENT__EXPRESSION,
				_image(a),
				attrName,
				false);
	}
	
	protected void  _createNode(IOutlineNode parentNode, FormalArguments st){
		for (EObject obj: st.eContents()){
			createNode(parentNode, obj);
		}
	}	
	
	protected void  _createNode(IOutlineNode parentNode, TaskFunctionBody st){
		for (EObject obj: st.eContents()){
			createNode(parentNode, obj);
		}
	}	
	
	protected void  _createNode(IOutlineNode parentNode, TaskFunctionBlock st){
		for (EObject obj: st.eContents()){
			createNode(parentNode, obj);
		}
	}	
	
	protected void  _createNode(IOutlineNode parentNode, AnyExpression st){
		for (EObject obj: st.eContents()){
			createNode(parentNode, obj);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, FullyQualifiedSymbolName name){
		createEStructuralFeatureNode(parentNode,
				name,
				MdlPackage.Literals.FULLY_QUALIFIED_SYMBOL_NAME__IDENTIFIER,
				_image(name),
				printer.toStr(name),
				true);
	}
	
	protected void  _createNode(IOutlineNode parentNode, SymbolModification sm){
		createEStructuralFeatureNode(parentNode,
				sm,
				MdlPackage.Literals.SYMBOL_MODIFICATION__LIST,
				_image(sm),
				printer.toStr(sm.getIdentifier()),
				false);
	}
	

	
	protected void  _createNode(IOutlineNode parentNode, ConditionalStatement st){
		createEStructuralFeatureNode(parentNode,
				st,
				MdlPackage.Literals.CONDITIONAL_STATEMENT__PAR_EXPRESSION,
				_image(st),
				"if",
				false);
		if (st.getIfStatement() != null){
			createEStructuralFeatureNode(parentNode,
					st,
					MdlPackage.Literals.CONDITIONAL_STATEMENT__IF_STATEMENT,
					getTrueImage(),
					"[true]",
					false);
		}
		if (st.getIfBlock() != null){
			createEStructuralFeatureNode(parentNode,
					st,
					MdlPackage.Literals.CONDITIONAL_STATEMENT__IF_BLOCK,
					getTrueImage(),
					"[true]",
					false);
		}
		if (st.getElseStatement() != null){
			createEStructuralFeatureNode(parentNode,
					st,
					MdlPackage.Literals.CONDITIONAL_STATEMENT__ELSE_STATEMENT,
					getFalseImage(),
					"else",
					false);
		}
		if (st.getElseBlock() != null){
			createEStructuralFeatureNode(parentNode,
					st,
					MdlPackage.Literals.CONDITIONAL_STATEMENT__ELSE_BLOCK,
					getFalseImage(),
					"else",
					false);
		}				
	}
	
	protected void  _createNode(IOutlineNode parentNode, ParExpression e){
		createEStructuralFeatureNode(parentNode,
				e,
				MdlPackage.Literals.PAR_EXPRESSION__EXPRESSION,
				_image(e),
				 printer.toStr(e),
				true);
	}
	
	protected void  _createNode(IOutlineNode parentNode, ParameterDeclaration p){
		createEStructuralFeatureNode(parentNode,
				p,
				MdlPackage.Literals.PARAMETER_DECLARATION__LIST,
				_image(p),
				 p.getIdentifier(),
				false);
	}
	
	protected void  _createNode(IOutlineNode parentNode, ImportedFunction p){
		createEStructuralFeatureNode(parentNode,
				p,
				MdlPackage.Literals.IMPORTED_FUNCTION__LIST,
				_image(p),
				 p.getIdentifier(),
				false);
	}
	
	protected void  _createNode(IOutlineNode parentNode, FunctionCall st) {
		createEStructuralFeatureNode(parentNode,
				st,
				MdlPackage.Literals.FUNCTION_CALL__ARGUMENTS,
				_image(st),
				printer.toStr(st.getIdentifier()),
				false);
	}
	
	protected void  _createNode(IOutlineNode parentNode, IgnoreList st) {
		createEStructuralFeatureNode(parentNode,
				st,
				MdlPackage.Literals.IGNORE_LIST__EXPRESSION,
				_image(st),
				st.getIdentifier(),
				false);
	}

	protected void  _createNode(IOutlineNode parentNode, AcceptList st) {
		createEStructuralFeatureNode(parentNode,
				st,
				MdlPackage.Literals.ACCEPT_LIST__EXPRESSION,
				_image(st),
				st.getIdentifier(),
				false);
	}

	protected void  _createNode(IOutlineNode parentNode, OrExpression e){
		createEStructuralFeatureNode(parentNode,
			e,
			MdlPackage.Literals.OR_EXPRESSION__EXPRESSION,
			_image(e),
			 printer.toStr(e),
			true);
	}
	
	protected void  _createNode(IOutlineNode parentNode, AndExpression e){
		createEStructuralFeatureNode(parentNode,
			e,
			MdlPackage.Literals.AND_EXPRESSION__EXPRESSION,
			_image(e),
			 printer.toStr(e),
			true);
	}
		
	protected void  _createNode(IOutlineNode parentNode, DesignBlockStatement p){
			createEStructuralFeatureNode(parentNode,
				p,
				MdlPackage.Literals.DESIGN_BLOCK_STATEMENT__EXPRESSION,
				_image(p),
				printer.toStr(p.getIdentifier()),
				false);
	}
	
	protected void  _createNode(IOutlineNode parentNode, SymbolDeclaration p){
		if (p.getExpression() != null){
			createEStructuralFeatureNode(parentNode,
				p,
				MdlPackage.Literals.SYMBOL_DECLARATION__EXPRESSION,
				_image(p),
				 p.getIdentifier(),
				false);
		}
		if (p.getRandomList() != null){
			createEStructuralFeatureNode(parentNode,
				p,
				MdlPackage.Literals.SYMBOL_DECLARATION__RANDOM_LIST,
				(p.getFunction() != null)? getLogImage(): _image(p),
				(p.getFunction() != null)? p.getFunction() + '(' + p.getIdentifier() + ')' : p.getIdentifier(),
				false);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, Block b){
		for (EObject obj: b.eContents()){
			createNode(parentNode, obj);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, EnumType b){
		for (EObject obj: b.eContents()){
			createNode(parentNode, obj);
		}
	}
	
	protected void  _createNode(IOutlineNode parentNode, DropList obj) {
		createEStructuralFeatureNode(parentNode,
				obj,
				MdlPackage.Literals.DROP_LIST__LIST,
				_image(obj),
				obj.getIdentifier(),
				false);
	}
	
	protected void  _createNode(IOutlineNode parentNode, AddList obj) {
		createEStructuralFeatureNode(parentNode,
				obj,
				MdlPackage.Literals.ADD_LIST__LIST,
				_image(obj),
				obj.getIdentifier(),
				false);
	}
	
	protected void  _createNode(IOutlineNode parentNode, RemoveList obj) {
		createEStructuralFeatureNode(parentNode,
				obj,
				MdlPackage.Literals.REMOVE_LIST__LIST,
				_image(obj),
				obj.getIdentifier(),
				false);
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	//Show expression as a leaf node
	/////////////////////////////////////////////////////////////////////////////////////
	protected void  _createNode(IOutlineNode parentNode, Expression e){
		for (EObject obj: e.eContents()){
			createNode(parentNode, obj);
		}
	}

	protected void  _createNode(IOutlineNode parentNode, ConditionalExpression e){
		createEStructuralFeatureNode(parentNode,
			e,
			MdlPackage.Literals.EXPRESSION__CONDITIONAL_EXPRESSION,
			_image(e),
			 printer.toStr(e),
			true);
	}
}

