package eu.ddmore.mdl.validation

import com.google.inject.Inject
import eu.ddmore.mdl.MdlInjectorProvider
import eu.ddmore.mdl.mdl.Mcl
import eu.ddmore.mdl.mdl.MdlPackage
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Ignore

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(MdlInjectorProvider))
class MclListAttributeValidationTest {
	@Inject extension ParseHelper<Mcl>
	@Inject extension ValidationTestHelper
	

	@Test
	def void testValidAttributes(){
		val mcl = '''bar = dataobj {
			DATA_INPUT_VARIABLES{
				ID : { use is id }
			}
			
			SOURCE{	}
		}'''.parse
		
		mcl.assertNoErrors
	}

	@Test
	def void testMissingMandatoryAttribute(){
		val mcl = '''bar = dataobj {
			DECLARED_VARIABLES{ Y }
			
			DATA_INPUT_VARIABLES{
				ID : { variable = Y }
			}
			
			SOURCE{	}
		}'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.attributeList,
			MdlValidator::MANDATORY_LIST_KEY_ATT_MISSING,
			"mandatory key attribute is missing in list"
		)
	}

	@Ignore  // language currently doesn't have any cases that use this.
	def void testMissingAttributeWithDep(){
		val mcl = '''bar = dataobj {
			DECLARED_VARIABLES{ Y }
			
			DATA_INPUT_VARIABLES{
				aCov : { use is covariate, categorical with {male, female} }
			}
			
			SOURCE{	}
		}'''.parse
		
		//@TODO: Implement the correct validation to check that categories are not defined.
		mcl.assertError(MdlPackage::eINSTANCE.attributeList,
			MdlValidator::MANDATORY_LIST_ATT_MISSING,
			"categories are not mapped to data"
		)
	}

	@Ignore // @TODO: Write a test for dep attributes.
	def void testMissingDependentAttribute(){
		val mcl = '''bar = dataobj {
			DECLARED_VARIABLES{ Y }
			
			DATA_INPUT_VARIABLES{
				aCov : { use is covariate, categories with {male when 1, female when 2} }
			}
			
			SOURCE{	}
		}'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.attributeList,
			MdlValidator::MANDATORY_LIST_ATT_MISSING,
			"mandatory attribute 'categories'"
		)
	}

	@Test
	def void testDataDerivedVariablesAttributes(){
		val mcl = '''bar = dataobj {
			DECLARED_VARIABLES{ D }
			
			DATA_INPUT_VARIABLES{
				AMT : { use is amt, variable = D }
				TIME : { use is idv }
			}
			DATA_DERIVED_VARIABLES{
				DT : { column = TIME, condition = D > 0 }
			}
			
			SOURCE{	}
		}'''.parse
		
		mcl.assertNoErrors
	}

	@Test
	def void testAnonymousCompartmentAttributesOK(){
		val mcl = '''bar = mdlobj {
			IDV{ T }
			
			VARIABILITY_LEVELS{
				ID : { type is parameter, level=1 }
			}
			
			STRUCTURAL_PARAMETERS{
				KA
				ALAG1
				F1
				V
				CL
			}
			
			MODEL_PREDICTION{
	  			COMPARTMENT {
					INPUT_KA : {type is depot, modelCmt=1, to=CENTRAL, ka=KA, tlag=ALAG1, finput=F1}
					CENTRAL  : {type is compartment, modelCmt=2}
                    	    ::  {type is elimination, modelCmt=2, from=CENTRAL, v=V, cl=CL}
   				}# end COMPARTMENT
				CONC=CENTRAL/V
			} # end MODEL_PREDICTION
		}'''.parse
		
		mcl.assertNoErrors
	}

	@Test
	def void testAnonymousCompartmentAttributesNoKey(){
		val mcl = '''bar = mdlobj {
			IDV{ T }
			
			VARIABILITY_LEVELS{
				ID : { type is idv, level=1 }
			}
			
			STRUCTURAL_PARAMETERS{
				KA
				ALAG1
				F1
				V
				CL
			}
			
			MODEL_PREDICTION{
	  			COMPARTMENT {
					INPUT_KA:   {type is depot, modelCmt=1, output=CENTRAL, ka=KA, tlag=ALAG1, finput=F1}
					CENTRAL:    {type is compartment, modelCmt=2}
                    	::		{modelCmt=2, input=CENTRAL, v=V, cl=CL}
   				}# end COMPARTMENT
				CONC=CENTRAL/V
			} # end MODEL_PREDICTION
		}'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.attributeList,
			MdlValidator::MANDATORY_LIST_KEY_ATT_MISSING,
			"mandatory key attribute is missing in list"
		)
	}


	@Test
	def void testUnrecognizedAttribute(){
		val mcl = '''bar = mdlobj(idv T) {
			VARIABILITY_LEVELS{
				ID : { type is idv, level=1 }
			}
			
			STRUCTURAL_PARAMETERS{
				KA
				ALAG1
				F1
				V
				CL
			}
			
			MODEL_PREDICTION{
	  			COMPARTMENT {
					INPUT_KA:   {type is depot, blahblah=1, to=CENTRAL, ka=KA, tlag=ALAG1, finput=F1}
					CENTRAL:    {type is compartment, modelCmt=2}
                    	:: {modelCmt=2, from=CENTRAL, v=V, cl=CL}
   				}# end COMPARTMENT
				CONC=CENTRAL/V
			} # end MODEL_PREDICTION
		}'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.valuePair,
			MdlValidator::UNRECOGNIZED_LIST_ATT,
			"attribute 'blahblah' is not recognised in this context"
		)
	}

	@Test
	def void testUnrecognizedAttribute2(){
		val mcl = '''
		foo = dataobj {
			DATA_INPUT_VARIABLES{
			}

			SOURCE{
				foo : { file="aFile", inputformat is nonmem }
			}
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.valuePair,
			MdlValidator::UNRECOGNIZED_LIST_ATT,
			"attribute 'inputformat' is not recognised in this context"
		)
	}

	@Test
	def void testInValidMissingWithCats(){
		val mcl = '''
		foo = mdlobj{
			VARIABILITY_LEVELS{
			}

			MODEL_PREDICTION{
			}# end MODEL_PREDICTION
			
			OBSERVATION{
				PAIN : { type is categorical }
			}
		}
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.enumPair,
			MdlValidator::INVALID_CATEGORY_DEFINITION,
			"Category definition is missing."
		)
	}

	@Test
	def void testInvalidAttributeWithUnexpectedCatDefn(){
		val mcl = '''
		foo = dataobj {
			DATA_INPUT_VARIABLES{
			}

			SOURCE{
				foo : { file="aFile", inputFormat is nonmemFormat withCategories { hello when 0, goodbye when 2 } }
			}
		} # end of model object
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.enumPair,
			MdlValidator::INVALID_CATEGORY_DEFINITION,
			"Unexpected category definition."
		)
	}

	@Test
	def void testValidSubListDefinition(){
		val mcl = '''
foo = mdlobj {
   COVARIATES{
   	  WT
   }# end COVARIATES

   VARIABILITY_LEVELS{
      ID: {type is parameter, level=2}
   }# end VARIABILITY_LEVELS

   STRUCTURAL_PARAMETERS{
      POP_CL
      POP_VC
      POP_Q
      POP_VP
      POP_KA
      POP_TLAG
      POP_BETA_CL_WT
      POP_BETA_V_WT
      RUV_PROP
      RUV_ADD
   }# end STRUCTURAL_PARAMETERS

   VARIABILITY_PARAMETERS{
      PPV_CL
      PPV_VC
      PPV_Q
      PPV_VP
      PPV_KA
      PPV_TLAG
      RUV_EPS1
   }# end VARIABILITY_PARAMETERS

   RANDOM_VARIABLE_DEFINITION (level=ID) {
      eta_PPV_CL ~ Normal(mean=0, sd=PPV_CL)
   }# end RANDOM_VARIABLE_DEFINITION (level=ID)

   INDIVIDUAL_VARIABLES{
      CL = linear(pop = POP_CL, fixEff = [{coeff=POP_BETA_CL_WT, cov=WT}] , ranEff = eta_PPV_CL)
   }# end INDIVIDUAL_VARIABLES
} 
		'''.parse
		
		mcl.assertNoErrors
	}


	@Test
	def void testInvalidSubListDefinition(){
		val mcl = '''
foo = mdlobj {
   COVARIATES{
   	  WT
   }# end COVARIATES

   VARIABILITY_LEVELS{
      ID: {type is parameter, level=2}
   }# end VARIABILITY_LEVELS

   STRUCTURAL_PARAMETERS{
      POP_CL
      POP_VC
      POP_Q
      POP_VP
      POP_KA
      POP_TLAG
      POP_BETA_CL_WT
      POP_BETA_V_WT
      RUV_PROP
      RUV_ADD
   }# end STRUCTURAL_PARAMETERS

   VARIABILITY_PARAMETERS{
      PPV_CL
      PPV_VC
      PPV_Q
      PPV_VP
      PPV_KA
      PPV_TLAG
      RUV_EPS1
   }# end VARIABILITY_PARAMETERS

   RANDOM_VARIABLE_DEFINITION (level=ID) {
      eta_PPV_CL ~ Normal(mean=0, sd=PPV_CL)
   }# end RANDOM_VARIABLE_DEFINITION (level=ID)

   INDIVIDUAL_VARIABLES{
      CL = linear(pop = POP_CL, fixEff = [{foobar=POP_BETA_CL_WT, covariate=WT}] , ranEff = eta_PPV_CL)
   }# end INDIVIDUAL_VARIABLES
} 
		'''.parse
		
		mcl.assertError(MdlPackage::eINSTANCE.assignPair,
			MdlValidator::INCOMPATIBLE_TYPES,
			"argument 'fixEff' expected value of type 'vector:Sublist:fixEffAtts' but was 'vector:Undefined'."
		)
//		mcl.assertError(MdlPackage::eINSTANCE.enumPair,
//			MdlValidator::MANDATORY_LIST_ATT_MISSING,
//			"mandatory attribute 'categories' is missing"
//		)
	}

}