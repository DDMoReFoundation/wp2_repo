package eu.ddmore.converter.mdl2json;

import static org.junit.Assert.*

import org.apache.log4j.Logger
import org.junit.Test

import eu.ddmore.converter.mdl2json.domain.Task

class TestJSONTaskObjectToMDL extends ConverterTestsParent  {
	
	private static Logger logger = Logger.getLogger(TestJSONTaskObjectToMDL.class)
	
	private static final String estimateBlockJson =
		/ {"ESTIMATE":"target=MLXTRAN_CODE\nversion=\"4.3.2\"\nalgo=[\"SAEM\"]"} /

	@Test
	public void testEstimate() {
		
		def json = getJson(estimateBlockJson)
		
		def taskObj = new Task(json)
				
		String expected = """taskobj {

    ESTIMATE {
        target=MLXTRAN_CODE
        version="4.3.2"
        algo=["SAEM"]
    }

}
"""
		assertEquals(expected, taskObj.toMDL())
	}
	
}
