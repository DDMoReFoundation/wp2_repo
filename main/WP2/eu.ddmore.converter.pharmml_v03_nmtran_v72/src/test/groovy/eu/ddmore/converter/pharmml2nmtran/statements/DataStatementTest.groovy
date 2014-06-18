package eu.ddmore.converter.pharmml2nmtran.statements;

import static eu.ddmore.converter.pharmml2nmtran.MainTest.TEST_DATA_DIR
import static eu.ddmore.converter.pharmml2nmtran.MainTest.V_0_3_SUBDIR
import static org.junit.Assert.*

import java.io.File;

import org.apache.commons.io.FileUtils
import org.junit.Test

import eu.ddmore.converter.pharmml2nmtran.utils.ConversionContext
import eu.ddmore.libpharmml.PharmMlFactory


class DataStatementTest {

	private static final String WORKING_DIR = "target/DataStatementTest_Working_Dir/"

	@Test
	public void shouldBeCorrectDataStatementObjectiveDataSet() {
		File src = getFile('example3/example3.xml', V_0_3_SUBDIR)
		DataStatement ds = new DataStatement(getDom(src), src.toString().replace(".xml", ""))
		assertEquals("Statement should be correct.", "\$DATA example3_data.csv IGNORE=@\n", ds.statement)
	}
	
	@Test
	public void shouldBeCorrectDataStatementNonmemDataSet() {
		File src = getFile('example3/example3_NONMEM.xml', V_0_3_SUBDIR)
		DataStatement ds = new DataStatement(getDom(src), src.toString().replace(".xml", ""))
		assertEquals("Statement should be correct.", "\$DATA warfarin_conc_pca.csv IGNORE=@\n", ds.statement)
	}

	@Test
	public void shouldBeCorrectHeadersEx3() {
		File src = getFile('example3/example3.xml', V_0_3_SUBDIR)
		DataStatement ds = new DataStatement(getDom(src), src.toString().replace(".xml", ""))

		List<String> headers = new ArrayList<String>()
		headers.add("ID")
		headers.add("TIME")
		headers.add("DV")
		headers.add("ARM")
		headers.add("WEIGHT")
		headers.add("AMT")
		headers.add("MDV")
		headers.add("EVID")
		assertEquals(headers, ds.headers)
	}

	@Test
	public void shouldBeCorrectHeadersEx5() {
		File src = getFile('example5/example5.xml', V_0_3_SUBDIR)
		DataStatement ds = new DataStatement(getDom(src), src.toString().replace(".xml", ""))
		
		List<String> headers = new ArrayList<String>()
		headers.add("ID")
		headers.add("TIME")
		headers.add("DV")
		headers.add("ARM")
		headers.add("AMT")
		headers.add("MDV")
		headers.add("EVID")
		assertEquals(headers, ds.headers)
	}

	@Test
    public void testExample3() {
        File src = getFile('example3/example3.xml', V_0_3_SUBDIR)
        ConversionContext conversionContext = new ConversionContext(getDom(src), src)
        assertEquals("\$EST METHOD=COND INTER MAXEVALS=9999 PRINT=10 NOABORT\n\$COV\n", conversionContext.getEstimationStatement().toString())
    }

	private File getFile(final String relativePathToFile, final String versionSubDirectory) {
		def urlToFile = this.class.getResource(TEST_DATA_DIR + versionSubDirectory + relativePathToFile)
		def destFile = new File(WORKING_DIR + versionSubDirectory + relativePathToFile)
		FileUtils.copyURLToFile(urlToFile, destFile)

		destFile
	}
		
	private getDom(file) {
		def pmlAPI = PharmMlFactory.getInstance().createLibPharmML()
		pmlAPI.createDomFromResource(FileUtils.openInputStream(file)).getDom()
	}
}