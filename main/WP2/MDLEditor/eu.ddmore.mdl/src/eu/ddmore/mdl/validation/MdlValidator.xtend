/*
 * generated by Xtext
 */
package eu.ddmore.mdl.validation

import eu.ddmore.mdl.mdl.BlockArgument
import eu.ddmore.mdl.mdl.BlockStatement
import eu.ddmore.mdl.mdl.ForwardDeclaration
import eu.ddmore.mdl.mdl.MclObject
import eu.ddmore.mdl.mdl.MdlPackage
import eu.ddmore.mdl.mdl.ValuePair
import org.eclipse.xtext.validation.Check
import eu.ddmore.mdl.mdl.BlockArguments

//import org.eclipse.xtext.validation.Check

/**
 * Custom validation rules. 
 *
 * see http://www.eclipse.org/Xtext/documentation.html#validation
 */
class MdlValidator extends AbstractMdlValidator {
	public val static MDLOBJ = 'mdlobj'
	public val static DATAOBJ = 'dataobj'
	public val static TASKOBJ = 'taskobj'
	public val static PARAMOBJ = 'parobj'
	public val static MOGOBJ = 'mogobj'

	extension BlockArgumentValidationHelper movh = new BlockArgumentValidationHelper
	extension BlockValidationHelper blokHelper = new BlockValidationHelper

	// Block arguments validation
	public static val UNKNOWN_BLOCK_ARG_DECL = "eu.ddmore.mdl.validation.UnknownBlockArgDecl"
	public static val UNKNOWN_BLOCK_ARG_PROP = "eu.ddmore.mdl.validation.UnknownBlockArgProp"
	public static val MANDATORY_BLOCK_ARG_MISSING = "eu.ddmore.mdl.validation.MandatoryBlockArgMissing"
	public static val MANDATORY_BLOCK_PROP_MISSING = "eu.ddmore.mdl.validation.MandatoryBlockPropMissing"

	// Block validation
	public static val UNKNOWN_BLOCK = "eu.ddmore.mdl.validation.UnknownBlock"
	public static val WRONG_SUBBLOCK = "eu.ddmore.mdl.validation.WrongSubBlock"
	public static val WRONG_PARENT_BLOCK = "eu.ddmore.mdl.validation.WrongParentBlock"
	public static val MANDATORY_BLOCK_MISSING = "eu.ddmore.mdl.validation.MandatoryBlockMissing"


	@Check
	def validateMdlObjArguments(MclObject it){
		blkArgs.unusedMandatoryObjVarDecl.forEach[blk, mand| error("mandatory argument '" + blk + "' is missing in " + mdlObjType
															+ " '" + name + "'",
					MdlPackage.eINSTANCE.mclObject_BlkArgs, MANDATORY_BLOCK_ARG_MISSING, blk) ]
		blkArgs.unusedMandatoryPropertyArguments.forEach[blk, mand| error("mandatory property '" + blk + "' is missing in " + mdlObjType
															+ " '" + name + "'",
					MdlPackage.eINSTANCE.mclObject_BlkArgs, MANDATORY_BLOCK_PROP_MISSING, blk) ]
	}
	
	@Check
	def validateMdlObjectHasCorrectBlocks(MclObject it){
		// check if mandatory blocks missing
		unusedMandatoryBlocks.forEach[blk, mand| error("mandatory block '" + blk + "' is missing in mdlobj '" + name + "'",
					MdlPackage.eINSTANCE.mclObject_Blocks, MANDATORY_BLOCK_MISSING, blk) ]
	}

	@Check
	def validateMdlObjBlockArgs(BlockStatement it){
		blkArgs.unusedMandatoryObjVarDecl.forEach[blk, mand| error("mandatory argument '" + blk + "' is missing in block '" + identifier + "'",
					MdlPackage.eINSTANCE.blockStatement_BlkArgs, MANDATORY_BLOCK_ARG_MISSING, blk) ]
		blkArgs.unusedMandatoryPropertyArguments.forEach[blk, mand| error("mandatory property '" + blk + "' is missing in block '" + identifier + "'",
					MdlPackage.eINSTANCE.blockStatement_BlkArgs, MANDATORY_BLOCK_PROP_MISSING, blk) ]
	}

	@Check
	def validateMdlObjBlocks(BlockStatement it){
		val parent = eContainer
		switch(parent){
			MclObject case !isModelBlock:
					error("block '" + identifier + "' cannot be used in a " + parent.mdlObjType,
						MdlPackage.eINSTANCE.blockStatement_Identifier, UNKNOWN_BLOCK, identifier)
			BlockStatement: {
				if (!isModelSubBlock) {
					error("block '" + identifier + "' cannot be used as a sub-block",
						MdlPackage.eINSTANCE.blockStatement_Identifier, WRONG_SUBBLOCK,
						identifier)
				} else if (!subBlockHasCorrectParent(parent)) {
					// recognised sub-block but in the wrong place
					error("sub-block '" + identifier + "' cannot be used in the '" + parent.identifier + "' block",
						MdlPackage.eINSTANCE.blockStatement_Identifier,
						WRONG_PARENT_BLOCK, identifier)
				}
			}
				
		}
//		if(eContainer instanceof MclObject){
//			val mdlObj = eContainer as MclObject
//			if(mdlObj.mdlObjType == MDLOBJ){
//				if(!identifier.isModelBlock){
//				}
//			}
//		}
//		if (eContainer instanceof BlockStatement) {
//			val blkStatement = eContainer as BlockStatement
//			if (!identifier.isModelSubBlock) {
//				error("block '" + identifier + "' cannot be used as a sub-block",
//					MdlPackage.eINSTANCE.blockStatement_Identifier, WRONG_SUBBLOCK,
//					identifier)
//			} else if (!identifier.subBlockHasCorrectParent(blkStatement.identifier)) {
//				// recognised sub-block but in the wrong place
//				error("sub-block '" + identifier + "' cannot be used in the '" + blkStatement.identifier + "' block",
//						MdlPackage.eINSTANCE.blockStatement_Identifier,
//						WRONG_PARENT_BLOCK, identifier)
//
//			}
//		}
	}


	@Check
	def validateBlockArgument(BlockArgument blkArg){
		if (blkArg.eContainer instanceof BlockArguments && (blkArg.eContainer.eContainer instanceof MclObject || blkArg.eContainer.eContainer instanceof BlockStatement)) {
			switch (blkArg) {
				ForwardDeclaration case !blkArg.isValidObjVarDecl: {
					error("unrecognised variable declaration type '" + blkArg.declType + "'",
						MdlPackage.eINSTANCE.forwardDeclaration_DeclType, UNKNOWN_BLOCK_ARG_DECL, blkArg.declType)
				}
				ValuePair case !blkArg.isValidBlkArgProperty: {
					error("unrecognised property '" + blkArg.argumentName + "'",
						MdlPackage.eINSTANCE.valuePair_ArgumentName, UNKNOWN_BLOCK_ARG_PROP, blkArg.argumentName)
				}
			}
		}
	}
}
