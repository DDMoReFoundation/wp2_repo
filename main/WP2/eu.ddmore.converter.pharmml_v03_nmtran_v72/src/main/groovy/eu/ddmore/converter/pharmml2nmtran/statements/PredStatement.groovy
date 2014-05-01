/*******************************************************************************
 * Copyright (C) 2014 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
package eu.ddmore.converter.pharmml2nmtran.statements

import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.ddmore.converter.pharmml2nmtran.model.Theta;
import eu.ddmore.converter.pharmml2nmtran.utils.ConverterUtils;
import eu.ddmore.converter.pharmml2nmtran.utils.Parameters;
import eu.ddmore.libpharmml.dom.PharmML;
import eu.ddmore.libpharmml.dom.commontypes.CommonVariableDefinitionType
import eu.ddmore.libpharmml.dom.commontypes.DerivativeVariableType
import eu.ddmore.libpharmml.dom.commontypes.VariableDefinitionType
import eu.ddmore.libpharmml.dom.maths.Equation
import eu.ddmore.libpharmml.dom.maths.PiecewiseType
import eu.ddmore.libpharmml.dom.maths.UniopType
import eu.ddmore.libpharmml.dom.modeldefn.CovariateModelType
import eu.ddmore.libpharmml.dom.modeldefn.CovariateRelationType
import eu.ddmore.libpharmml.dom.modeldefn.GaussianObsError
import eu.ddmore.libpharmml.dom.modeldefn.GeneralObsError
import eu.ddmore.libpharmml.dom.modeldefn.IndividualParameterType
import eu.ddmore.libpharmml.dom.modeldefn.ObservationErrorType
import eu.ddmore.libpharmml.dom.modeldefn.ObservationModelType
import eu.ddmore.libpharmml.dom.modeldefn.ParameterModelType;
import eu.ddmore.libpharmml.dom.modeldefn.SimpleParameterType;
import eu.ddmore.libpharmml.dom.modeldefn.StructuralModelType
import eu.ddmore.pharmacometrics.model.trialdesign.CategoricalAttribute
import groovy.lang.GString;

import javax.xml.bind.JAXBElement


class PredStatement extends NMTranFormatter {

    private PharmML pmlDOM
    private Parameters parameters
    private ConverterUtils converterUtils

    private Map<String, String> continuousCovariates
    private Map<String, CategoricalAttribute> categoricalCovariates
    private Map<String,String> simpleParameterToNmtran

    //Computed when the sigma statement is created. Needed here for the error model
    private Map<String, String> epsilonToSigma = converterUtils.epsilonToSigma
    private List<DerivativeVariableType> derivativeVariableTypes

    private Map<String, String> derivativeNames
    private Set<String> definedInDES


    public PredStatement(PharmML pmlDOM, Parameters parameters, ConverterUtils converterUtils) {
        this.pmlDOM = pmlDOM
        this.parameters = parameters
        this.converterUtils = converterUtils
        derivativeNames = new HashMap<String, String>()
        definedInDES = new HashSet<String>()
    }

    def getStatement() {
        def sb = new StringBuilder()
        derivativeVariableTypes = new ArrayList<DerivativeVariableType>()
        pmlDOM.modelDefinition.structuralModel.each {
            it.commonVariable.each {
                if (it.value instanceof DerivativeVariableType) {
                    derivativeVariableTypes.add( it.value )
                }
            }
        }

        if (derivativeVariableTypes) {
            sb << getDerivativePredStatement()
        } else {
            sb << getNonDerivativePredStatement()
        }
    }

    def getDerivativePredStatement() {
        StringBuilder sb = new StringBuilder()
        sb << "\$SUBS ADVAN13 TOL=9\n"
        sb << getModelStatement()

        sb << pk(getPredCoreStatement().toString() + getDifferentialInitialConditions().toString())
        sb << getDifferentialEquationsStatement()

        sb << error(getErrorFormula().toString())
        sb
    }

    def getDifferentialInitialConditions() {
        StringBuilder sb = new StringBuilder();
        int i=1
        derivativeVariableTypes.each { var ->
            String initialCondition
            if (var.initialCondition) {
                if (var.initialCondition.initialValue.assign.symbRef) {
                    initialCondition = var.initialCondition.initialValue.assign.symbRef.symbIdRef
                    sb << endline(indent( "A_0(${i}) = ${rename(initialCondition.toUpperCase())}" ))
                } else if (var.initialCondition.initialValue.assign.scalar) {
                    initialCondition = var.initialCondition.initialValue.assign.scalar.value.value
                    sb << endline(indent( "A_0(${i}) = ${initialCondition}" ))
                }
            }
            i++
        }
        sb
    }

    def getDifferentialEquationsStatement() {
        StringBuilder sb = new StringBuilder();
        int i=1

        derivativeVariableTypes.each { var ->
            sb << endline(indent("${rename(var.symbId.toUpperCase())}=A(${i})"))
            derivativeNames[var.symbId] = "A(${i})"
            i++
        }
        sb << endline("")
        sb << endline(getStructuralParameters().toString())
        i=1
        derivativeVariableTypes.each { var ->
            sb << converterUtils.convert(var, i++)
        }
        des(sb.toString())
    }

    def getNonDerivativePredStatement() {
        StringBuilder sb = new StringBuilder();
        sb << endline(indent("IF (AMT.GT.0) ${rename('DOSE')}=AMT"))
        sb << getPredCoreStatement()
        sb << getErrorFormula()
        pred(sb.toString())
    }

    def getPredCoreStatement() {
        StringBuilder sb = new StringBuilder();
        sb << getCovariatesFromModel()
        sb << getIndividualsFromModel()
        if (!derivativeVariableTypes) {
            sb << getStructuralParameters()
        }
        sb << getConditionalStatements().join("")
        sb
    }

    def getModelStatement() {
        StringBuilder sb = new StringBuilder();
        derivativeVariableTypes.each { var ->
            sb << endline(indent("COMP (${rename(var.symbId.toUpperCase())})"))
        }
        model(sb.toString())
    }

    def getStructuralParameters() {
        StringBuilder sb = new StringBuilder();

        parameters.structuralVars.each { name, type ->
            if (! type.getClass().equals( DerivativeVariableType.class) ) {
                sb << endline("${converterUtils.convert(type)}")
                if (derivativeVariableTypes) {
                    definedInDES.add(name)
                }
            }
        }
        sb
    }

    def getErrorStructuralParameterRedefinition(String name) {
        StringBuilder sb = new StringBuilder();
        CommonVariableDefinitionType type = parameters.structuralVars[name]
        
        Map<String, String> inputNameToVariable = new HashMap<String, String>(derivativeNames)
        inputNameToVariable[name] = name + "2"
        sb << endline("${converterUtils.convert(type, inputNameToVariable)}")
        sb
    }

    def getCovariatesFromModel() {
        findCovariates()
        StringBuilder cov = new StringBuilder();
        pmlDOM.modelDefinition.parameterModel.each { cov << endline(getCovariatesFromModelType(it).toString()) }
        cov
    }

    def getIndividualsFromModel() {
        StringBuilder ind = new StringBuilder();
        pmlDOM.modelDefinition.parameterModel.each { ind << endline(getIndividualsFromModelType(it).toString()) }
        ind
    }

    private StringBuilder getCovariatesFromModelType(ParameterModelType parameterModelType) {
        StringBuilder cov = new StringBuilder();
        def visitedThetas = new HashSet<String>();
        for (JAXBElement elem in parameterModelType.commonParameterElement) {
            if (elem.value instanceof IndividualParameterType) {
                IndividualParameterType individualParameterType = (IndividualParameterType) elem.value;
                String symbolName = individualParameterType.symbId

                if (individualParameterType.gaussianModel) {
                    if (individualParameterType.gaussianModel.linearCovariate) {
                        String name = individualParameterType.gaussianModel.linearCovariate.populationParameter.assign.symbRef.symbIdRef
                        int thetaIndex = parameters.isTheta(name).index

                        String covString
                        if (individualParameterType.gaussianModel.linearCovariate.covariate) {
                            CovariateRelationType covariateRelationType = individualParameterType.gaussianModel.linearCovariate.covariate[0]
                            String covariateName = covariateRelationType.symbRef.symbIdRef
                            String covariateMappedName = parameters.varToName.get(covariateName);
                            covString = continuousCovariates.get(covariateMappedName)
                        }
                        visitedThetas.add(name)
                        cov << buildCovariateString(name.toUpperCase(), thetaIndex, covString)
                    } else if (individualParameterType.gaussianModel.generalCovariate) {
                        cov << converterUtils.convert(individualParameterType.gaussianModel.generalCovariate.assign)
                    }
                } else if (individualParameterType.assign) {
                    Equation equation = individualParameterType.assign.equation
                    String name = equation.binop.content[0].value.symbIdRef
                    Theta theta = parameters.isTheta(name)
                    if (theta) {
                        int thetaIndex = theta.index
                        visitedThetas.add(name)
                        cov << buildCovariateString(name.toUpperCase(), thetaIndex, null)
                    } else {
                        cov << endline(indent({converterUtils.convert(parameters.getGroupVariable(name))}))
                    }
                }
            }
        }
        cov << reportNotVisitedThetas(visitedThetas)
        cov << reportCovariate()
        cov
    }

    def reportSimpleParamsWithAssigns() {
        def sb = new StringBuilder();
        pmlDOM.modelDefinition.parameterModel.each {
            it.commonParameterElement.each {
                if ( !parameters.isOmega(it.value.symbId) && (it.value instanceof SimpleParameterType) && it.value.assign ){
                    String rightPart
                    if (it.value.assign.equation) {
                        rightPart = converterUtils.convert(it.value.assign.equation)
                    } else if (it.value.assign.scalar) {
                        rightPart = it.value.assign.scalar.value.value
                    }
                    sb << endline(indent("${rename(it.value.symbId.toUpperCase())}=${rightPart}"))
                }
            }
        }
        sb
    }

    def reportCovariate() {
        def sb = new StringBuilder();
        pmlDOM.modelDefinition.covariateModel.each {
            it.covariate.each { sb << endline(indent("${converterUtils.convert(it)}")) }
        }
        sb
    }

    /**
     * Iterates over all thetas ('parameter.theats') and outputs a line of the form 'name.uppercase=THETA(INDEX)'
     * for each theta that has not yet visited by the code.
     * @param visited the set of thetas that the code has visited.
     */
    private StringBuilder reportNotVisitedThetas(HashSet<String> visited) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Theta> e : parameters.thetas.entrySet()) {
            String thetaName = e.getKey()
            Theta theta = e.getValue()
            if (!visited.contains(thetaName)) {
                sb << endline(indent("${rename(thetaName.toUpperCase())}=THETA(${theta.index})"))
            }
        }
        sb
    }

    private String buildCovariateString(String nameInUpperCase, int thetaIndex, String covString) {
        def cov = indent("${rename(nameInUpperCase)}=THETA(${thetaIndex})")
        if (covString) {
            cov << "*${covString}"
        }
        endline(cov.toString())
    }

    private StringBuilder getIndividualsFromModelType(ParameterModelType parameterModelType) {
        StringBuilder ind = new StringBuilder();
        for (JAXBElement elem in parameterModelType.commonParameterElement) {
            if (elem.value instanceof IndividualParameterType) {
                IndividualParameterType individualParameterType = (IndividualParameterType) elem.value;
                String symbolName = individualParameterType.symbId

                if (individualParameterType.gaussianModel) {
                    String name =null
                    if (individualParameterType.gaussianModel.linearCovariate) {
                        name = individualParameterType.gaussianModel.linearCovariate.populationParameter.assign.symbRef.symbIdRef
                        def omegaIndices = []
                        individualParameterType.gaussianModel.randomEffects.symbRef.each {
                            String etaName = it.symbIdRef

                            //The next line is added becasue libPharmML 3.0 returns the 'etaName' as '[etaName]', so I have to remove '[' and ']'. If they fix it, remove the line.
                            etaName = etaName.replace('[', '').replace(']', '')

                            String omega = parameters.etaToOmega[etaName]

                            int omegaIndex = converterUtils.omegasInPrintOrder.indexOf(omega)+1
                            omegaIndices.add(omegaIndex)
                        }
                        ind.append(buildIndividualString(symbolName.toUpperCase(), name.toUpperCase(), omegaIndices))
                    } else if (individualParameterType.gaussianModel.generalCovariate) {
                        ind << endline(indent("${rename(symbolName.toUpperCase())}${converterUtils.convert(individualParameterType.gaussianModel.generalCovariate.assign)}"))
                    }
                } else if (individualParameterType.assign) {
                    ind << endline(indent("${rename(symbolName.toUpperCase())}${converterUtils.convert(individualParameterType.assign)}"))
                }
            }
        }
        ind << reportSimpleParamsWithAssigns()
        ind
    }

    private String buildIndividualString(String symbolNameInUpperCase, String nameInUpperCase, List<Integer> omegaIndices) {
        def sb = new StringBuilder()
        sb << indent("${rename(symbolNameInUpperCase)}=${rename(nameInUpperCase)}")
        symbolNameInUpperCase
        omegaIndices.each {  sb << "*EXP(ETA(${it}))" }
        endline(sb.toString())
    }

    public List<String> getConditionalStatements() {
        List<String> conditionals = new ArrayList<String>();
        for (StructuralModelType structuralModel in pmlDOM.modelDefinition.structuralModel ) {
            findSimpleParametersInStructuralModel(structuralModel);
            List<SimpleParameterType> simpleParameterTypes = pmlDOM.modelDefinition.structuralModel.simpleParameter
            for (CommonVariableDefinitionType varType in structuralModel.commonVariable.value ) {
                String variableName = varType.symbId
                if (varType.assign && (varType.assign.equation.piecewise) ) {
                    PiecewiseType piecewise = varType.assign.equation.piecewise
                    String pieceWiseAsNmtran = converterUtils.convert(piecewise, variableName, simpleParameterToNmtran).replaceAll("\\(t-tD\\)", "TIME").toUpperCase()
                    conditionals.add(pieceWiseAsNmtran);
                }
            }
        }
        conditionals
    }

    private void findSimpleParametersInStructuralModel(StructuralModelType structuralModel) {
        simpleParameterToNmtran = new HashMap<String,String>()
        structuralModel.simpleParameter.each {
            simpleParameterToNmtran[it.symbId] = converterUtils.convert(it)
        }
    }

    def getErrorFormula() {
        def sb = new StringBuilder();
        for (ObservationModelType type in pmlDOM.modelDefinition.observationModel) {
            if (type.observationError.value instanceof GaussianObsError) {
                ObservationErrorType errorType = type.observationError.value
                String name = String.valueOf(errorType.output.symbRef.symbIdRef.value)
                if (definedInDES || derivativeVariableTypes) {
                    if (derivativeNames[name]) {
                        name = derivativeNames[name].toUpperCase()
                    } else {
                        sb << getErrorStructuralParameterRedefinition(name)
                        name = rename(name.toUpperCase()) + "2"
                    }
                } else {
                    name = rename(name.toUpperCase())
                }
                def firstPart = "Y=${name}+"
                def secondPart = "${converterUtils.convert(errorType.errorModel.assign.equation.functionCall, derivativeNames)}"
                sb << indent(firstPart)

                if (errorType.residualError) {
                    String epsilonName = errorType.residualError.symbRef.symbIdRef
                    String sigmaName = epsilonToSigma.get(epsilonName)

                    int sigmaIndex = parameters.isSigma(sigmaName).index
                    if (secondPart.contains('+') || secondPart.contains('-')) {
                        sb << "(${secondPart})"
                    } else {
                        sb << "${secondPart}"
                    }
                    sb << "*EPS($sigmaIndex)"
                } else {
                    sb << secondPart
                }
            } else if (type.observationError.value instanceof GeneralObsError) {
                GeneralObsError errorType = type.observationError.value
                sb << indent("Y${converterUtils.convert(errorType.assign)}")
            }
        }
        endline(sb.toString())
    }

    def findCovariates() {
        continuousCovariates = new HashMap<String, String>()
        categoricalCovariates = new HashMap<String, CategoricalAttribute>()
        for (CovariateModelType covariatelModel in pmlDOM.modelDefinition.covariateModel ) {
            covariatelModel.covariate.each {
                if (it.continuous) {
                    UniopType uniopType = it.continuous.transformation.equation.uniop
                    if (uniopType) {
                        String covariate = converterUtils.convert(uniopType)
                        String covName = parameters.varToName.get(it.symbId) ?: it.symbId
                        continuousCovariates.put(covName, covariate)
                    }
                } else {
                    String name = it.symbId
                    Set<String> categories = new HashSet<String>()
                    it.categorical.category.each { cat ->
                        categories.add(cat.catId)
                    }
                    CategoricalAttribute categoricalAttribute = new CategoricalAttribute(name, categories)
                    categoricalCovariates[name] = categoricalAttribute
                }
            }
        }
    }

}