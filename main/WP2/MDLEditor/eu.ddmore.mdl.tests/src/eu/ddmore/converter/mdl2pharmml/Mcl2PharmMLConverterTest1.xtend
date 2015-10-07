package eu.ddmore.converter.mdl2pharmml

import com.google.inject.Inject
import eu.ddmore.mdl.MdlInjectorProvider
import eu.ddmore.mdl.mdl.Mcl
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Ignore

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(MdlInjectorProvider))
class Mcl2PharmMLConverterTest1 {
	@Inject extension ParseHelper<Mcl>
	@Inject extension ValidationTestHelper

	extension ConverterTestHarness cth = new ConverterTestHarness
	
	@Before
	def void setUp(){
	}
	
	
	@After
	def void tearDown(){
	}
	
	private def validateConversion(String useCaseName){
		val mclFile = "src/eu/ddmore/converter/mdl2pharmml/" + useCaseName + ".mdl"
		val mcl = readFile(mclFile).parse
		mcl.assertNoErrors
		val pharmMLFile = "convertedFiles/" + useCaseName + ".xml" 
		mcl.convertTo(pharmMLFile)
		assertIsValid(pharmMLFile)
	} 
	
	@Test
	def void testUseCase1(){
		validateConversion("UseCase1")
	}

	@Test
	def void testUseCase2(){
		validateConversion("UseCase2")
	}

	@Ignore
	def void testUseCase2_5(){
		validateConversion("UseCase2_5")
	}

	@Test
	def void testUseCase3(){
		validateConversion("UseCase3")
	}

	@Test
	def void testUseCase4(){
		validateConversion("UseCase4")
	}

	@Test
	def void testUseCase4_1(){
		validateConversion("UseCase4_1")
	}

	@Test
	def void testUseCase5(){
		validateConversion("UseCase5")
	}

	@Test
	def void testUseCase6(){
		validateConversion("UseCase6")
	}

	@Test
	def void testUseCase7(){
		validateConversion("UseCase7")
	}

	@Test
	def void testUseCase8(){
		validateConversion("UseCase8")
	}

	@Test
	def void testUseCase8_4(){
		validateConversion("UseCase8_4")
	}

	@Test
	def void testUseCase9(){
		validateConversion("UseCase9")
	}

	@Test
	def void testUseCase10(){
		validateConversion("UseCase10")
	}

	@Test
	def void testUseCase10_1(){
		validateConversion("UseCase10_1")
	}

	@Ignore
	def void testUseCase11(){
		validateConversion("UseCase11")
	}

	@Ignore
	def void testUseCase12(){
		validateConversion("UseCase12")
	}

	@Ignore
	def void testUseCase13(){
		validateConversion("UseCase13")
	}

	@Ignore
	def void testUseCase14(){
		validateConversion("UseCase14")
	}

	@Ignore
	def void testUseCase15(){
		validateConversion("UseCase15")
	}

	@Ignore
	def void testUseCase16(){
		validateConversion("UseCase16")
	}

	@Ignore
	def void testUseCase17(){
		validateConversion("UseCase17")
	}

}