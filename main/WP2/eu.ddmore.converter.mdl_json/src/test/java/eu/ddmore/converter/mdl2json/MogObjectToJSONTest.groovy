package eu.ddmore.converter.mdl2json;

import static org.junit.Assert.*

import org.apache.log4j.Logger
import org.junit.Ignore
import org.junit.Test

import eu.ddmore.converter.mdl2json.domain.Mog
import eu.ddmore.converter.mdl2json.domain.Task

class MogObjectToJSONTest extends ConverterTestsParent {
	private static Logger logger = Logger.getLogger(MogObjectToJSONTest.class)
	
    @Test
    public void testObjectsBlock() {
        def json = getJsonFromMDLFile("Warfarin_MogObject.mdl")[0] // The [0] is because the JSON is enclosed within superfluous square brackets [...]
        
        def mogObject = json.warfarin_PK_ODE_mog
        
        def objects = mogObject[Mog.OBJECTS]
        
        logger.debug(objects)
        
        assertEquals("Checking number of imported objects", 4, objects.size())
        
        assertTrue("Checking that data object is imported", objects.containsKey("warfarin_PK_ODE_dat"))
        assertTrue("Checking that model object is imported", objects.containsKey("warfarin_PK_ODE_mdl"))
        assertTrue("Checking that parameter object is imported", objects.containsKey("warfarin_PK_ODE_par"))
        assertTrue("Checking that task object is imported", objects.containsKey("warfarin_PK_ODE_task"))
        
    }
    
	@Test
    @Ignore("Mappings block doesn't currently parse so WarfarinPkSim_MogObject.mdl is invalid and cannot be read")
    public void testObjectsBlockWhereUsingAliasing() {
        def json = getJsonFromMDLFile("WarfarinPkSim_MogObject.mdl")[0] // The [0] is because the JSON is enclosed within superfluous square brackets [...]
        
        def mogObject = json.warfarin_PK_SIM
        
        def objects = mogObject[Mog.OBJECTS]
        
        logger.debug(objects)
        
        assertEquals("Checking number of imported objects", 4, objects.size())
        
        assertTrue("Checking that data object is imported", objects.containsKey("warfarin_design"))
        assertEquals("Checking the alias of the data object", "do", objects["warfarin_design"])
        assertTrue("Checking that model object is imported", objects.containsKey("warfarin_PK_SIM_mdl"))
        assertEquals("Checking the alias of the model object", "mo", objects["warfarin_PK_SIM_mdl"])
        assertTrue("Checking that parameter object is imported", objects.containsKey("warfarin_PK_SIM_par"))
        assertEquals("Checking the alias of the parameter object", "po", objects["warfarin_PK_SIM_par"])
        assertTrue("Checking that task object is imported", objects.containsKey("warfarin_PK_SIM_task"))
        assertEquals("Checking the alias of the task object", "to", objects["warfarin_PK_SIM_task"])
        
    }
    
    @Test
    @Ignore("Mappings block doesn't currently parse so WarfarinPkSim_MogObject.mdl is invalid and cannot be read")
    public void testMappingBlock() {
        def json = getJsonFromMDLFile("WarfarinPkSim_MogObject.mdl")[0] // The [0] is because the JSON is enclosed within superfluous square brackets [...]
        
        def mogObject = json.warfarin_PK_SIM_mog
        
        def mappings = mogObject[Mog.MAPPING]
        
        logger.debug(mappings)
        
        assertEquals("Checking number of mappings", 1, mappings.size())
        assertTrue("Checking that data object's WT_MEAN variable is mapped", mappings.containsKey("do.WT_MEAN"))
        assertEquals("Checking that data object's WT_MEAN variable is mapped correctly", "mo.MEAN_WT", mappings["do.WT_MEAN"])

    }
	
}