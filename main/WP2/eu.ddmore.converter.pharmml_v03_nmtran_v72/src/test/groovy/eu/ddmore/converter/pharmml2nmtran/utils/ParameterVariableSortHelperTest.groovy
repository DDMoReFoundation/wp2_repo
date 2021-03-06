package eu.ddmore.converter.pharmml2nmtran.utils;

import static org.junit.Assert.*

import eu.ddmore.libpharmml.dom.PharmML
import org.junit.Before;
import org.junit.Test;

import eu.ddmore.libpharmml.PharmMlFactory
import eu.ddmore.libpharmml.dom.PharmML

import org.apache.commons.io.FileUtils;
import static eu.ddmore.converter.pharmml2nmtran.MainTest.TEST_DATA_DIR
import static eu.ddmore.converter.pharmml2nmtran.MainTest.V_0_3_SUBDIR

class ParameterVariableSortHelperTest {

	private final String prolactinModel = TEST_DATA_DIR + V_0_3_SUBDIR + "FribergCPT2009/Friberg2009Prolactin_v20140523v14_NONMEM.xml"

    @Test	
    public void testArrangeParameterElements() {
        final URL urlToFile = Parameters2008ThamJCCRTest.class.getResource(prolactinModel);

		def pmlAPI = PharmMlFactory.getInstance().createLibPharmML()
		PharmML pmlDOM = pmlAPI.createDomFromResource(urlToFile.openStream()).getDom()

		ParameterVariableSortHelper paramSortHelper = new ParameterVariableSortHelper();

		//[VCI:[], CLI:[], VPI:[], QI:[], KAI:[], FI:[], DURI:[], LAGI:[], D2:[], F2:[], F5:[], KDA:[], UPDA:[], DPHS:[], PHS1:["PAT", "Pat", "HV"], AMP2:[], pop_KOUT:[], omega_KOUT:[], KOUT:[], pop_PRL0:["Study", "PAT", "F", "Pat", "M", "STU101", "HV", "Sex"], omega_PRL0:[], gamma_PRL0:[], PRL0:[], F4:[], pop_AMP1:[], omega_AMP1:[], AMP1:[], pop_PHS2:["PAT", "Pat", "HV"], omega_PHS2:[], PHS2:["eta_PHS2", "pop_PHS2"], pop_KI:[], omega_KI:[], KI:[]] //, Ad:["Ad", "KAI"], Ac:["Ac", "Ad", "KAI", "Ap", "CLI", "VCI", "QI", "VPI"], Ap:["Ac", "Ap", "VCI", "QI", "VPI"], CP:["Ac", "VCI"], DIU:["AMP1", "AMP2"], KINB:["KOUT", "PRL0"], KINM:["KINB"], AAI:["Adp"], FEED:["Apr"], Apr:["KOUT", "KINM", "Apr", "DIU", "KINB", "AAI"], Adp:["Adp", "FEED", "KDA"], a:["PAT", "F", "Pat", "HV", "Sex"]]
		Map references = paramSortHelper.getParameterVariableRefs(pmlDOM.modelDefinition)
		Map arrangedMap = paramSortHelper.arrangeParameterElements(references)

		String arrangedParametersList = ["VCI", "CLI", "VPI", "QI", "KAI", "FI", "DURI", "LAGI", "KDA", "UPDA", "DPHS", "AMP2", "pop_KOUT", "omega_KOUT", "KOUT", "omega_PRL0", "gamma_PRL0", "PRL0", "pop_AMP1", "omega_AMP1", "AMP1", "omega_PHS2", "pop_KI", "omega_KI", "KI", "D2", "F2", "F5", "PHS1", "pop_PRL0", "F4", "pop_PHS2", "PHS2", "Ad", "Ac", "Ap", "CP", "DIU", "KINB", "KINM", "Adp", "AAI", "Apr", "FEED", "a"]
		assertEquals(arrangedParametersList, arrangedMap.keySet().toString())
	}
	
	@Test
	public void testGetParameterVariableRefs() {
        final URL urlToFile = Parameters2008ThamJCCRTest.class.getResource(prolactinModel);

		def pmlAPI = PharmMlFactory.getInstance().createLibPharmML()
		PharmML pmlDOM = pmlAPI.createDomFromResource(urlToFile.openStream()).getDom()

		ParameterVariableSortHelper paramSortHelper = new ParameterVariableSortHelper();
		Map references = paramSortHelper.getParameterVariableRefs(pmlDOM.modelDefinition)
		Map referenceMap = [VCI:[], CLI:[], VPI:[], QI:[], KAI:[], FI:[], DURI:[], LAGI:[], KDA:["INITIAL_VALUE"], UPDA:["INITIAL_VALUE"], DPHS:["INITIAL_VALUE"], AMP2:[], pop_KOUT:[], omega_KOUT:[], KOUT:["PAT", "Pat", "HV"], omega_PRL0:[], gamma_PRL0:[], PRL0:[], F4:[], pop_PRL0:["Study", "PAT", "F", "STUD101", "Pat", "M", "HV", "Sex"], omega_AMP1:[], AMP1:[], omega_PHS2:[], pop_KI:["PRL0", "INITIAL_VALUE"], omega_KI:[], KI:[], D2:[], F2:["PAT", "Pat", "HV"], F5:[], PHS1:["eta_PHS2", "pop_PHS2"], pop_AMP1:[], pop_PHS2:[], PHS2:[], DADT1:["INITIAL_VALUE"], Ad:["Ad", "KAI"], DADT2:["INITIAL_VALUE"],Ac:["Ac", "Ad", "KAI", "Ap", "CLI", "VCI", "QI", "VPI"], DADT3:["INITIAL_VALUE"], Ap:["Ac", "Ap", "VCI", "QI", "VPI"], CP:["Ac", "VCI"],	DIU:["t", "AMP1", "AMP2", "PHS2", "PHS1"], KINB:["KOUT", "PRL0"], KINM:["KINB"], AAI:["Adp"], FEED:["Apr"], DADT4:["INITIAL_VALUE"], Apr:["KOUT", "KINM", "Apr", "DIU", "KINB", "AAI"], DADT5:["INITIAL_VALUE"], Adp:["Adp", "FEED", "KDA"], a:["PAT", "F", "Pat", "HV", "Sex"]]
		assert references.values().asList().equals(referenceMap.values().asList())
	}

}
