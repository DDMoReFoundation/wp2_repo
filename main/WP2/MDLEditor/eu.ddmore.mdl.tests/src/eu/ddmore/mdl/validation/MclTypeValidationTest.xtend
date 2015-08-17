package eu.ddmore.mdl.validation

import com.google.inject.Inject
import eu.ddmore.mdl.MdlInjectorProvider
import eu.ddmore.mdl.mdl.Mcl
import eu.ddmore.mdl.mdl.MdlPackage
import eu.ddmore.mdl.validation.MdlValidator
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Ignore

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(MdlInjectorProvider))
class MclTypeValidationTest {
	@Inject extension ParseHelper<Mcl>
	@Inject extension ValidationTestHelper
	

	@Test
	def void testValidNumericalEquationExpression(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }
			
			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				B
				C = T
				A = B + C - 22
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertNoErrors
	}
	
	@Test
	def void testValidWhenEquationExpression(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				B
				C
				A = if(B > 0 && false) then B + C - 22 elseif(C == B || 22 < 0) then B^180 else 22
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertNoErrors
	}
	
	@Test
	def void testValidWithDerivExpression(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				DEQ{
					B : { deriv = C, init = 33 }
				}
				C
				A = if(B > 0 && false) then B + C - 22 elseif(C == B || 22 < 0) then B^180 else 22
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertNoErrors
	}
	
	@Test
	def void testInValidWithVarLvlExpression(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
				ID : { type is parameter, level=1 }
			}
		
			
			MODEL_PREDICTION{
				DEQ{
					B : { deriv = C, init = 33 }
				}
				C = ID
				A = if(B > 0 && false) then B + C - 22 elseif(C == B || 22 < 0) then B^180 else 22
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.equationDefinition,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Expected Real type, but was List:VarLevel."
		)
	}
	
	@Test
	def void testInValidRelation(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				B
				C
				A = C == "B"
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.equalityExpression,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Expected Real type, but was String."
		)
	}
	
	@Test
	def void testValidUniopExpressionOK(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				B = if(!true) then 0 
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertNoErrors
	}
	
	@Test
	def void testValidUniopExpressionInvalid(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				B = !0
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.unaryExpression,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Expected Boolean type, but was Int."
		)
	}
	
	@Test
	def void testValidUniopExpressionInvalidNumber(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				B = +true
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.unaryExpression,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Expected Real type, but was Boolean."
		)
	}
	
	@Test
	def void testValidUniopExpressionInvalidMalformed(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				B = +
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.unaryExpression,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Expected Real type, but was Undefined."
		)
	}
	
	@Test
	def void testValidUniopExpressionInvalidBool(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				B = !
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.unaryExpression,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Expected Boolean type, but was Undefined."
		)
	}
	
	@Test
	def void testInvalidWhenEquationExpression(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }
			
			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				B
				C
				A = if(B > 0 && "false") then B + C - 22 if(C == B || 22 < 0) then B^180 else 22
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.andExpression,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Expected Boolean type, but was String."
		)
	}
	
	@Ignore
	def void testInValidFunctionArgumentExpression(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }
			
			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				log(B) = 26
				C
				log(A) =  exp("B") + C - 22
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.unnamedFuncArguments,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Expected Real type, but was String."
		)
	}
	
	@Test
	def void testValidFunctionEquationExpression(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }
			
			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				log(B) = 26
				C
				log(A) =  exp(B) + C - 22
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertNoErrors
	}
	
	@Test
	def void testValidDistnFunctionExpression(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }
			
			VARIABILITY_LEVELS{
				ID : { type is parameter, level = 1 }
			}
		
			RANDOM_VARIABLE_DEFINITION(level=ID){
				foo ~ Normal(mean=0, sd=1)
			}
			
			
		} # end of model object
		'''.parse
		
		mcl.assertNoErrors
	}
	
	@Test
	def void testValidVectorEquationExpression(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }
			
			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				B = 26
				C
				A[] =  [ 20, C, B]
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertNoErrors
	}
	
	@Test
	def void testInValidVectorEquationExpression(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				B = 26
				C
				A[] =  [ 20, true, B]
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.vectorElement,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Element type 'Boolean' is incompatible with vector type 'Vector:Real'."
		)
	}
	
	@Test
	def void testInValidVectorEquationDefinition(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				B = 26
				C
				A =  [ 20, C, B]
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.equationDefinition,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Expected Real type, but was Vector:Real."
		)
	}
	
	@Test
	def void testInValidVectorLiteralIncompatibleEquationDefinition(){
		val mcl = '''
		d1g=desobj{
			ADMINISTRATION{
			}
			
			STUDY_DESIGN{
				Conc
			}
			
			SAMPLING{
			 	pkwin1 : { type is simple, outcome=Conc, sampleTime = 0.5 }
				pkwin2 : { type is simple, outcome=Conc, numberSamples=0 }
			}
			DESIGN_SPACES{
				DS3[] = [pkwin1,pkwin2]
			}
		}
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.equationDefinition,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Expected Vector:Real type, but was Vector:List:Sampling."
		)
	}
	
	@Test
	def void testInValidVectorNestedVectorEquationDefinition(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				A[] =  [ [ 0 ], 20, 26]
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.vectorElement,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Element type 'Vector:Int' is incompatible with vector type 'Vector:Int'."
		)
	}
	
	@Test
	def void testInValidVectorNestedVectorRefEquationDefinition(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				A[] =  [ C, 20, 26]
				C[] = [ 1, 2, 3]
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.vectorElement,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Element type 'Vector:Real' is incompatible with vector type 'Vector:Int'."
		)
	}
	
	@Test
	def void testInValidInconsistentVectorLiteral(){
		val mcl = 	'''
		d1g=desobj{
			STUDY_DESIGN{}
			
			
			ADMINISTRATION{
				Conc
			}
			SAMPLING{
			 	pkwin1 : { type is simple, outcome=Conc, sampleTime = [0.5,2] }
				pkwin2 : { type is simple, outcome=Conc, numberSamples=0 }
			}
			DESIGN_SPACES{
				DS3 : { name=[pkwin1,pkwin2, 1.0], element is numberTimes, discrete=[1,2] }
			}
		}
	'''.parse
		mcl.assertError(MdlPackage::eINSTANCE.vectorElement,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Element type 'Real' is incompatible with vector type 'Vector:List:Sampling'."
		)
	}
	
	
	@Test
	def void testValidListVectorLiteral(){
		val mcl = 	'''
		d1g=desobj{
			ADMINISTRATION{
			}
			
			STUDY_DESIGN{
				Conc
			}
			
			SAMPLING{
			 	pkwin1 : { type is simple, outcome=Conc, sampleTime = [0.5] }
				pkwin2 : { type is simple, outcome=Conc, numberSamples = [0] }
			}
			DESIGN_SPACES{
				DS3 : { name=[pkwin1,pkwin2], element is numberTimes, discrete = [1] }
			}
		}
	'''.parse
		
		mcl.assertNoErrors
	}
	
	
	@Test
	def void testValidEnumExpression(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			COVARIATES{
				SEX withCategories {male, female}
			}
		
			
			MODEL_PREDICTION{
				V = if(SEX == SEX.male) then 1 else 0
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertNoErrors
	}
	
	@Test
	def void testInValidEnumNumExpression(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			COVARIATES{
				SEX withCategories {male, female}
			}
		
			
			MODEL_PREDICTION{
				V = if(SEX == 2) then 1 else 0
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.equalityExpression,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Expected Enum:SEX type, but was Int."
		)
	}
	
	@Test
	def void testInValidNumEnumExpression(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			COVARIATES{
				SEX withCategories {male, female}
			}
		
			
			MODEL_PREDICTION{
				V = if(2 == SEX) then 1 else 0
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.equalityExpression,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Expected Real type, but was Enum:SEX."
		)
	}
	
	@Test
	def void testInValidEnumStringExpression(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			COVARIATES{
				SEX withCategories {male, female}
			}
		
			
			MODEL_PREDICTION{
				V = if(SEX == "2") then 1 else 0
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.equalityExpression,
			MdlValidator::INCOMPATIBLE_TYPES,
			"Expected Enum:SEX type, but was String."
		)
	}
	
	@Test
	def void testValidBuiltinEnum(){
		val mcl = '''
		foo = dataobj {
			DATA_INPUT_VARIABLES{
			}

			SOURCE{
				foo : { file="aFile", inputFormat is nonmemFormat }
			}
		} # end of model object
		'''.parse
		
		mcl.assertNoErrors()
	}
	
	@Test
	def void testValidIntVectorWithIntVector(){
		val mcl = '''
d1g=desobj{
	DECLARED_VARIABLES{
		Conc
		Effect
	}
	ADMINISTRATION{
		dose1 : {adm=1, amount=100, doseTime=[0], duration=[1]} 
	}
	SAMPLING{
	}
	DESIGN_SPACES{
		DS1 : { name=[dose1], element is amount, discrete=[10,100,200] }
	}
	STUDY_DESIGN{
	}
}
		'''.parse
		
		mcl.assertNoErrors()
	}
	
	@Test
	def void testValidMappingStructure(){
		val mcl = '''
d1g=desobj{
	DECLARED_VARIABLES{
		Conc
	}
	ADMINISTRATION{
		dose1 : {adm=1, amount=100, doseTime=[0], duration=[1]} 
	}
	SAMPLING{
	 	pkwin1 : { type is simple, outcome=Conc, sampleTime = [0.5,2] }
	}

	STUDY_DESIGN{
		totalSize=100
		arm1 : {
			armSize=100,
			interventionSequence={{
				interventionList=[dose1]
			}},
			samplingSequence={{
				samplingList=[pkwin1],
				start=[0]
				}
			}
		}
	}
}
		'''.parse
		
		mcl.assertNoErrors()
	}
	
	@Test
	def void testInvalidBuiltinEnum(){
		val mcl = '''
		foo = dataobj {
			DATA_INPUT_VARIABLES{
			}

			SOURCE{
				foo : { file="aFile", inputFormat is foobar }
			}
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.valuePair,
			MdlValidator::INCOMPATIBLE_TYPES,
			"attribute 'inputFormat' expected value of type 'Enum:input' but was 'Undefined'"
		)
	}
	
	
	@Test
	def void testInvalidBuiltinEnumWrongType(){
		val mcl = '''
		foo = dataobj {
			DATA_INPUT_VARIABLES{ alt }

			SOURCE{
				foo : { file="aFile", inputFormat = alt }
			}
		} 
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.valuePair,
			MdlValidator::INCOMPATIBLE_TYPES,
			"attribute 'inputFormat' expected value of type 'Enum:input' but was 'Real'"
		)
	}
	
	@Test
	def void testValidBoolExpressionAttributes(){
		val mcl = '''bar = dataobj {
			DECLARED_VARIABLES{ D }
			
			DATA_INPUT_VARIABLES{
				TIME : { use is idv }
			}
			DATA_DERIVED_VARIABLES{
				DT : { column = TIME, condition = (D - 4) > (0 - 2) }
			}
			
			SOURCE{	}
		}'''.parse
		
		mcl.assertNoErrors
	}

	@Test
	def void testValidMappingExpressionAttributes(){
		val mcl = '''bar = dataobj {
			DECLARED_VARIABLES{ D; E }
			
			DATA_INPUT_VARIABLES{
				CMT : { use is cmt }
				AMT : { use is amt, define = {1 in CMT as D, 2 in CMT as E } }
			}
			DATA_DERIVED_VARIABLES{
			}
			
			SOURCE{	}
		}'''.parse
		
		mcl.assertNoErrors
	}

	@Test
	def void testInValidMappingExpressionAttributes(){
		val mcl = '''bar = dataobj {
			DECLARED_VARIABLES{ D }
			
			DATA_INPUT_VARIABLES{
				AMT : { use is amt, define = D }
			}
			DATA_DERIVED_VARIABLES{
			}
			
			SOURCE{	}
		}'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.valuePair,
			MdlValidator::INCOMPATIBLE_TYPES,
			"attribute 'define' expected value of type 'Mapping' but was 'Real'"
		)
	}

	@Ignore
	def void testInvalidNoVarRefAttributes(){
		val mcl = '''bar = dataobj {
			DECLARED_VARIABLES{ D }
			
			DATA_INPUT_VARIABLES{
				AMT : { use is amt, variable = 0.0 }
			}
			DATA_DERIVED_VARIABLES{
			}
			
			SOURCE{	}
		}'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.valuePair,
			MdlValidator::INCOMPATIBLE_TYPES,
			"attribute 'define' expected REFERENCE of type 'Real' but was non-reference 'Real'"
		)
	}

	@Test
	def void testInvalidNonBoolExpressionAttributes(){
		val mcl = '''bar = dataobj {
			DECLARED_VARIABLES{ D }
			
			DATA_INPUT_VARIABLES{
				TIME : { use is idv }
			}
			DATA_DERIVED_VARIABLES{
				DT : { column = TIME, condition = D + 0 - 2 }
			}
			
			SOURCE{	}
		}'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.valuePair,
			MdlValidator::INCOMPATIBLE_TYPES,
			"attribute 'condition' expected value of type 'Boolean' but was 'Real'"
		)
	}

	@Test
	def void testInvalidNonRealExpressionAttributes(){
		val mcl = '''
		warfarin_PK_SEXAGE_mdl = mdlobj {
			IDV{ T }

			VARIABILITY_LEVELS{
			}
		
			
			MODEL_PREDICTION{
				DEQ{
					B : { deriv = C > 0 && false, init = 33 }
				}
				C
				A = if(B > 0 && false) then B + C - 22 elseif(C == B || 22 < 0) then B^180 else 22
			}
			
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.valuePair,
			MdlValidator::INCOMPATIBLE_TYPES,
			"attribute 'deriv' expected value of type 'Real' but was 'Boolean'"
		)
	}

}