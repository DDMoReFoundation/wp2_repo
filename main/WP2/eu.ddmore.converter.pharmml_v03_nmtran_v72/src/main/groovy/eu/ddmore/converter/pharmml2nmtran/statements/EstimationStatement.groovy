/*******************************************************************************
 * Copyright (C) 2014 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
package eu.ddmore.converter.pharmml2nmtran.statements

import eu.ddmore.libpharmml.dom.PharmML;
import eu.ddmore.libpharmml.dom.modellingsteps.EstimationStepType
import eu.ddmore.libpharmml.dom.modellingsteps.SimulationStepType
import javax.xml.bind.JAXBElement


class EstimationStatement extends NMTranFormatter {

    def getStatement(PharmML pmlDOM) {
        def sb = new StringBuilder()
        boolean estFIMFound = false;
        for (JAXBElement elem in pmlDOM.modellingSteps.commonModellingStep) {
            if (elem.value instanceof EstimationStepType) {
                sb << "\$EST "
                EstimationStepType estStep = (EstimationStepType) elem.value
                estStep.operation.each {
                    if (it.opType.value.equals("estPop")) {
                        sb << computeMethod(estStep.operation[0].algorithm)
                    }
                    if (!estFIMFound) {
                        estFIMFound = it.opType.value.equals("estFIM")
                    }
                }
            }
        }
        sb << "\n${estFIMFound ? "\$COV\n": "" }"
    }
    
    def computeMethod(algorithm) {
        def sb = new StringBuilder()
        sb << "METHOD="
        if (algorithm) {
            if (algorithm.definition.equals("FOCEI")) {
                sb << "COND INTER MAXEVALS=9999 PRINT=10"
            }else if (algorithm.definition.equals("SAEM")) {
                sb << "SAEM INTER NBURN=2000 NITER=1000 ISAMPLE=2 IACCEPT=0.4 PRINT=10 CTYPE=3"
            } else {
                sb << algorithm.definition
            }
        } else {
            sb << "COND"
        }
        sb
    }
}