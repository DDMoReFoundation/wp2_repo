package eu.ddmore.mdl

import com.google.inject.Inject
import eu.ddmore.mdl.mdl.Mcl
import org.eclipse.xtext.diagnostics.Diagnostic
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(MdlAndLibInjectorProvider))
class MclParserModelObj11Test {
	@Inject extension LibraryTestHelper<Mcl>
	@Inject extension ValidationTestHelper
	
	val static CODE_SNIPPET = '''
warfarin_PK_ODE_mdl = mdlObj {
	IDV{ T }
	
	FUNCTIONS{
	   	# define a function. The return type of the function is given by it's name.
	   	# In this case it is a real. If it were a vector or matric it would use [] or [[]] 
		userFunc = function(int arg1, real arg2, string arg3) {
			# the function can contain only a single expression
		    arg2 + arg1  # return types have to match type of func name
		}
	}
	
	MODEL_PREDICTION{
		# refer to a user defined function as a normal function
		Z = &userFunc(arg1=1, arg2=2.0, arg3="foo") + 22.2
	}

	VARIABILITY_LEVELS{
		ID : { level=2, type is parameter }
		DV : { level=1, type is observation }
	} 

} # end of model object
		'''
	
	@Ignore
	def void testParsing(){
		val mdl = CODE_SNIPPET.parse
		mdl.assertNoErrors(Diagnostic::SYNTAX_DIAGNOSTIC)
	}
	
	@Test
	// needed to stop initialisation failure
	def void testDummy(){
	}
}