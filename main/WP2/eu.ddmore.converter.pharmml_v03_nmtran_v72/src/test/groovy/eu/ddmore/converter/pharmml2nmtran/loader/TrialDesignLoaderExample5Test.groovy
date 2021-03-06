package eu.ddmore.converter.pharmml2nmtran.loader;

import static org.junit.Assert.*

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import eu.ddmore.libpharmml.PharmMlFactory
import eu.ddmore.libpharmml.dom.PharmML
import eu.ddmore.pharmacometrics.model.trialdesign.Subject
import eu.ddmore.pharmacometrics.model.trialdesign.math.BinaryExpression
import eu.ddmore.pharmacometrics.model.trialdesign.math.Constant
import eu.ddmore.pharmacometrics.model.trialdesign.math.Variable
import eu.ddmore.pharmacometrics.model.trialdesign.math.BinaryExpression.Operator;
import eu.ddmore.pharmacometrics.model.trialdesign.math.Expression
import eu.ddmore.pharmacometrics.model.trialdesign.structure.Activity
import eu.ddmore.pharmacometrics.model.trialdesign.structure.Arm;
import eu.ddmore.pharmacometrics.model.trialdesign.structure.Bolus
import eu.ddmore.pharmacometrics.model.trialdesign.structure.Cell
import eu.ddmore.pharmacometrics.model.trialdesign.structure.DosingTimes
import eu.ddmore.pharmacometrics.model.trialdesign.structure.DosingTimesSequence
import eu.ddmore.pharmacometrics.model.trialdesign.structure.Epoch
import eu.ddmore.pharmacometrics.model.trialdesign.structure.Segment


import org.apache.commons.io.FileUtils;

import static eu.ddmore.converter.pharmml2nmtran.MainTest.TEST_DATA_DIR
import static eu.ddmore.converter.pharmml2nmtran.MainTest.V_0_3_SUBDIR


class TrialDesignLoaderExample5Test {
    TrialDesignLoader loader;

    @Before
    public void init() {
        
        final URL urlToFile = TrialDesignLoaderExample5Test.class.getResource(TEST_DATA_DIR + V_0_3_SUBDIR + "example5/example5.xml");
        
        def pmlAPI = PharmMlFactory.getInstance().createLibPharmML()
        def pmlDOM = pmlAPI.createDomFromResource(urlToFile.openStream()).getDom()
        loader = new TrialDesignLoader("trialDesign":pmlDOM.trialDesign)
        loader.load()
    }

    @Test
    public void testArms() {
        List<Arm> arms = new ArrayList<Arm>()
        Arm arm1 = new Arm("arm1", 1)
        for (int i=1; i<=21; i++) {
            Subject subject = new Subject("i"+i, i)
            arm1.add(subject)
        }
        arms.add(arm1)
        assertEquals(arms, loader.structure.getArms())
    }

    @Test
    public void testEpochs() {
        List<Epoch> expected = new ArrayList<Epoch>()
        Epoch epoch = new Epoch("start":0, "end":200)
        expected.add(epoch)
        assertEquals(expected, loader.structure.getEpochs())
    }

}
