/*******************************************************************************
 * Copyright (C) 2016 Mango Business Solutions Ltd, http://www.mango-solutions.com
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/agpl-3.0.html>.
 *******************************************************************************/
package eu.ddmore.converter.mdl2json

import static org.junit.Assert.*
import static eu.ddmore.converter.mdl2json.MdlAndJsonFileUtils.*
import static eu.ddmore.converter.mdl2json.testutils.MdlFileContentTestUtils.*
import eu.ddmore.converter.mdl2json.domain.Mcl
import org.junit.rules.TemporaryFolder;

import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import org.junit.Ignore;
import org.junit.Rule
import org.junit.Test

class EndToEndIntegrationTest {
    private static final Logger LOGGER = Logger.getLogger(EndToEndIntegrationTest.class)
    
	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder()
	
    /**
     * Tests for the the presence of the top-level objects in the JSON representation of an MDL file:
     * <ul>
     * <li>dataObj
     * <li>parObj
     * <li>mdlObj
     * <li>taskObj
     * <li>mogObj
     * </ul>
     */
    @Test
    public void topLevelObjectsMustBePresentInJsonRepresention_skeleton() {
        def json = getJsonFromMDLFile("skeleton.mdl")
        
        assertTrue("Returned Json should be a List of length 5", json instanceof List && json.size() == 5)

        assertTrue("Each element in the top-level List should be a Map", ((List) json).every { it instanceof Map })
        
        def dataObj = json[0]
        def parObj = json[1]
        def mdlObj = json[2]
        def taskObj = json[3]
        def mogObj = json[4]
        
        assertEquals("Checking content of data object", ['name':'skeleton_data', 'blocks':[:], 'type':'dataObj'], dataObj)
        assertEquals("Checking content of parameter object", ['name':'skeleton_par', 'blocks':[:], 'type':'parObj'], parObj)
        assertEquals("Checking content of model object", ['name':'skeleton_mdl', 'blocks':[:], 'type':'mdlObj'], mdlObj)
        assertEquals("Checking content of task object", ['name':'skeleton_task', 'blocks':[:], 'type':'taskObj'], taskObj)
        assertEquals("Checking content of mog object", ['name':'skeleton_mog', 'blocks':[:], 'type':'mogObj'], mogObj)
    }
    @Test
    public void topLevelObjectsMustBePresentInJsonRepresention_DesignObj_Population() {
        def json = getJsonFromMDLFile("DesignObj_Population.mdl")
        def mclFromJson = new Mcl(json)
        def outputMdl = mclFromJson.toMDL()
        
        LOGGER.debug(outputMdl)
        assertTrue("Returned Json should be a List of length 5", json instanceof List && json.size() == 5)

        assertTrue("Each element in the top-level List should be a Map", ((List) json).every { it instanceof Map })
        
        def designObj = json[0]
        def parObj = json[1]
        def mdlObj = json[2]
        def taskObj = json[3]
        def mogObj = json[4]
        
        assertEquals("Checking content of design object", 5, designObj.blocks.size())
        assertEquals("Checking content of parameter object", 2, parObj.blocks.size())
        assertEquals("Checking content of model object", 9, mdlObj.blocks.size())
        assertEquals("Checking content of task object", 1, taskObj.blocks.size())
        assertEquals("Checking content of mog object", 1, mogObj.blocks.size())
    }
    /**
     * Converting a MDL file to JSON then back to MDL should give rise to syntactically and
     * semantically equivalent blocks to those of the original MDL.
     * <p>
     * The MDL file "FullyPopulated.mdl" is a syntactically valid, but semantically invalid model,
     * created to try and cover as much of the conversion code with one model as possible.
     */
    @Test
    public void mdlFileConvertedToJsonAndBackAgainShouldBeEquivalentToOriginalMdlFile() {
        def origMdlFile = getFile("UseCase1_Product5.mdl")
        
        def json = getJsonFromMDLFile(origMdlFile)
        
        def mclFromJson = new Mcl(json)
        def outputMdl = mclFromJson.toMDL()
        
        LOGGER.debug(outputMdl)
        
        ALL_BLOCK_NAMES.each { blockName ->
            LOGGER.info("About to process block " + blockName + "...")
            assertMDLBlockEqualityIgnoringWhitespaceAndComments(origMdlFile, blockName, outputMdl)
        }
    }
    
	@Test
	public void roundtripComparisonFromMdl_DesignObj_StudyDesign() {
		final File origMdlFile = getFile("DesignObj_StudyDesign.mdl")
		
		def json = getJsonFromMDLFile(origMdlFile)
		
		def mclFromJson = new Mcl(json)
		def outputMdl = mclFromJson.toMDL()
		
		LOGGER.debug(outputMdl)
		
		
		ALL_BLOCK_NAMES.each { blockName ->
			LOGGER.info("About to process block " + blockName + "...")
			assertMDLBlockEqualityIgnoringWhitespaceAndComments(origMdlFile, blockName, outputMdl)
		}
	}

	private assertRoundTripComparisonIsTheSame(URL origMdlFile){
		File mdlCopy = tmpDir.newFile("testFile.mdl")
		FileUtils.copyURLToFile(origMdlFile, mdlCopy)
		
		def json = getJsonFromMDLFile(mdlCopy)
		
		def mclFromJson = new Mcl(json)
		def outputMdl = mclFromJson.toMDL()
		
		LOGGER.debug(outputMdl)
		
		
		ALL_BLOCK_NAMES.each { blockName ->
			LOGGER.info("About to process block " + blockName + "...")
			assertMDLBlockEqualityIgnoringWhitespaceAndComments(mdlCopy, blockName, outputMdl)
		}
	}

	@Test
	public void useCase6RoundTripTest(){
		def mdlFile = getClass().getResource("/test-models/MDL/Product5.1/UseCase6.mdl")
		assertRoundTripComparisonIsTheSame(mdlFile)
	}
	
	@Test
	public void useCase1_PRIORRoundTripTest(){
		def mdlFile = getClass().getResource("UseCase1_PRIOR.mdl")
		assertRoundTripComparisonIsTheSame(mdlFile)
	}
	
	@Test
	public void useCase8RoundTripTest(){
		def mdlFile = getClass().getResource("/test-models/MDL/Product5.1/UseCase8.mdl")
		assertRoundTripComparisonIsTheSame(mdlFile)
	}
	
	@Test
	public void useCase9RoundTripTest(){
		def mdlFile = getClass().getResource("/test-models/MDL/Product5.1/UseCase9.mdl")
		assertRoundTripComparisonIsTheSame(mdlFile)
	}
	
	@Test
	public void useCase11RoundTripTest(){
		def mdlFile = getClass().getResource("/test-models/MDL/Product5.1/UseCase11.mdl")
		assertRoundTripComparisonIsTheSame(mdlFile)
	}
	
	@Test
	public void useCase12RoundTripTest(){
		def mdlFile = getClass().getResource("/test-models/MDL/Product5.1/UseCase12.mdl")
		assertRoundTripComparisonIsTheSame(mdlFile)
	}
	
	@Test
	public void useCase13RoundTripTest(){
		def mdlFile = getClass().getResource("/test-models/MDL/Product5.1/UseCase13.mdl")
		assertRoundTripComparisonIsTheSame(mdlFile)
	}
	
	@Test
	public void useCase14RoundTripTest(){
		def mdlFile = getClass().getResource("/test-models/MDL/Product5.1/UseCase14.mdl")
		assertRoundTripComparisonIsTheSame(mdlFile)
	}
	
	@Test
	public void useCaseM2000RoundTripTest(){
		def mdlFile = getClass().getResource("Magni_2000_diabetes_C-peptide.mdl")
		assertRoundTripComparisonIsTheSame(mdlFile)
	}
	
	@Test
	public void useCaseM2004RoundTripTest(){
		def mdlFile = getClass().getResource("Magni_2004_diabetes_IVGTT.mdl")
		assertRoundTripComparisonIsTheSame(mdlFile)
	}
	

    @Test
    public void readMdlFileFromJSON_DesignObj_StudyDesign() {
        final File origMdlFile = getFile("DesignObj_StudyDesign.mdl")
        def inputJSON = FileUtils.readFileToString(getFile("DesignObj_StudyDesign.output.json"));
        LOGGER.debug(inputJSON)
        def json = getJson(inputJSON)
        def mclFromJson = new Mcl(json)
        def outputMdl = mclFromJson.toMDL()
        
        LOGGER.debug(outputMdl)
        
        
        ALL_BLOCK_NAMES.each { blockName ->
            LOGGER.info("About to process block " + blockName + "...")
            assertMDLBlockEqualityIgnoringWhitespaceAndComments(origMdlFile, blockName, outputMdl)
        }
    }
}
