/*******************************************************************************
 * Copyright (C) 2014 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
package eu.ddmore.converter.pharmml2nmtran.statements

import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.ddmore.converter.pharmml2nmtran.model.Theta;
import eu.ddmore.converter.pharmml2nmtran.utils.ConversionContext;
import eu.ddmore.converter.pharmml2nmtran.utils.ParameterVariableSortHelper
import eu.ddmore.converter.pharmml2nmtran.utils.Parameters;
import eu.ddmore.converter.pharmml2nmtran.utils.TargetBlockConverter;
import eu.ddmore.libpharmml.dom.PharmML;
import eu.ddmore.libpharmml.dom.commontypes.CommonVariableDefinitionType
import eu.ddmore.libpharmml.dom.commontypes.DerivativeVariableType
import eu.ddmore.libpharmml.dom.commontypes.IntValueType
import eu.ddmore.libpharmml.dom.commontypes.RealValueType;
import eu.ddmore.libpharmml.dom.commontypes.Rhs;
import eu.ddmore.libpharmml.dom.commontypes.SymbolRefType
import eu.ddmore.libpharmml.dom.commontypes.VariableDefinitionType
import eu.ddmore.libpharmml.dom.maths.BinopType
import eu.ddmore.libpharmml.dom.maths.Equation
import eu.ddmore.libpharmml.dom.maths.LogicBinOpType;
import eu.ddmore.libpharmml.dom.maths.PieceType;
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
import eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomEffectType
import eu.ddmore.libpharmml.dom.modeldefn.SimpleParameterType;
import eu.ddmore.libpharmml.dom.modeldefn.StructuralModelType
import eu.ddmore.pharmacometrics.model.trialdesign.CategoricalAttribute
import groovy.lang.GString;

import javax.xml.bind.JAXBElement


class PredStatement extends NMTranFormatter {

    private PharmML pmlDOM
    private Parameters parameters
    private ConversionContext conversionContext

    private Map<String, String> continuousCovariates
    private Map<String, CategoricalAttribute> categoricalCovariates

    //Computed when the sigma statement is created. Needed here for the error model
    private final Map<String, String> epsilonToSigma
    private List<DerivativeVariableType> derivativeVariableTypes

    private final Map<String, String> derivativeNames = new HashMap<String, String>()
    private final Set<String> definedInDES = new HashSet<String>()
	TargetBlockConverter targetBlockConverter
	
    public PredStatement(PharmML pmlDOM, Parameters parameters, ConversionContext conversionContext) {
        this.pmlDOM = pmlDOM
        this.parameters = parameters
        this.conversionContext = conversionContext
        this.epsilonToSigma = conversionContext.epsilonToSigma
		targetBlockConverter = conversionContext.targetBlockConverter 
		findCovariates()
    }

    def findCovariates() {
        continuousCovariates = new HashMap<String, String>()
        categoricalCovariates = new HashMap<String, CategoricalAttribute>()
        for (CovariateModelType covariatelModel in pmlDOM.modelDefinition.covariateModel ) {
            covariatelModel.covariate.each {
                if (it.continuous) {
                    UniopType uniopType = it.continuous.transformation.equation.uniop
                    if (uniopType) {
                        String covariate = conversionContext.convert(uniopType)
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

    def getStatement() {
		def statementName = "\$PRED";
		def trimmedBlockName = targetBlockConverter.trimLocationName(statementName);

		if (getDerivativeVariableTypes()) {
			statementName = "\$SUB";
			def targetBlockClosure={
				getDerivativePredStatement()
			}
			def nonTargetBlockClosure={
				"\$SUBS ADVAN13 TOL=9\n"+getDerivativePredStatement()
			}
			targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure,targetBlockClosure)
		} else {
			def targetBlockClosure={
				getNonDerivativePredStatement()
			}
			def nonTargetBlockClosure={
				statementName+" \n"+ getNonDerivativePredStatement()
			}
			targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure,targetBlockClosure)
		}
    }

    private List<DerivativeVariableType> getDerivativeVariableTypes() {
        if(derivativeVariableTypes == null) {
            derivativeVariableTypes = new ArrayList<DerivativeVariableType>()
            pmlDOM.modelDefinition.structuralModel.each {
                it.commonVariable.each {
                    if (it.value instanceof DerivativeVariableType) {
                        derivativeVariableTypes.add( it.value )
                    }
                }
            }
        }
        return derivativeVariableTypes;
    }
    
    
    def getDerivativePredStatement() {
        StringBuilder sb = new StringBuilder()
		
		sb << getModelStatement()
		sb << getAbbreviatedStatement()
		sb << getPKStatement()
        sb << getDifferentialEquationsStatement()
		sb << getAESStatement()
        sb << getErrorStatement()
        sb
    }
	
    def getDifferentialInitialConditions() {
        StringBuilder sb = new StringBuilder();
        int i=1
        getDerivativeVariableTypes().each { var ->
            String initialCondition
            if (var.initialCondition) {
				if(var.initialCondition.initialValue) {
	                if (var.initialCondition.initialValue.assign.symbRef) {
	                    initialCondition = var.initialCondition.initialValue.assign.symbRef.symbIdRef
	                    sb << endline(indent( "A_0(${i}) = ${rename(initialCondition.toUpperCase())}" ))
	                } else if (var.initialCondition.initialValue.assign.scalar) {
	                    initialCondition = getInitialConditionFromScalar(var.initialCondition.initialValue.assign.scalar, i) 
	                    sb << endline(indent( initialCondition ))
	                }
				} else if(var.initialCondition.assign.scalar) {
                    initialCondition = getInitialConditionFromScalar(var.initialCondition.assign.scalar, i) 
                    sb << endline(indent( initialCondition ))
				}
            } 
            i++
        }
        sb
    }

	def getInitialConditionFromScalar(scalar, i) {
		def initialCondition = scalar.value.value
		"A_0(${i}) = ${initialCondition}"
	}
	
	/**
	 * This will wrap error statement code block with target blocks if available and return it.
	 *
	 * @return
	 */
	def getErrorStatement(){
		def statementName = "\$ERROR"
		def targetBlockClosure={
			"\n"+ getErrorFormula()
		}
		def nonTargetBlockClosure={
			error(getErrorFormula().toString())
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure,targetBlockClosure)
	}
	
	def getDifferentialEquationsStatement(){
		
		def statementName = "\$DES"
		def targetBlockClosure={
			"\n"+ getDESCodeBlock()
		}
		def nonTargetBlockClosure={
			des(getDESCodeBlock())
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure,targetBlockClosure)
	}
	
    def getDESCodeBlock() {
        StringBuilder sb = new StringBuilder();
        int i=1

        getDerivativeVariableTypes().each { var ->
            sb << endline(indent("${rename(var.symbId.toUpperCase())}=A(${i})"))
            derivativeNames[var.symbId] = "A(${i})"
            i++
        }
        sb << endline("")
        sb << endline(getStructuralParameters().toString())
        i=1
        getDerivativeVariableTypes().each { var ->
            sb << conversionContext.convert(var, i++)
        }
        sb.toString()
    }
	
	def getAESStatement(){
		
		def statementName = "\$AES"

		def nonTargetBlockClosure={
			"\n"//Currently we dont handle AES block so this space is to handle it when target block is not there.
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure)
	}

    def getNonDerivativePredStatement() {
        StringBuilder sb = new StringBuilder();
        sb << endline(indent("IF (AMT.GT.0) ${rename('DOSE')}=AMT"))
        sb << getPredCoreStatement()
        sb << getErrorStatement()
        sb
    }

    def getPredCoreStatement() {
        StringBuilder sb = new StringBuilder();
        sb << getCovariatesFromModel()
        sb << getIndividualsFromModel()
        if (!getDerivativeVariableTypes()) {
            sb << getStructuralParameters()
        }
        sb << getConditionalStatements().join("")
        sb
    }
	
	/**
	 * This will wrap Abbreviated statement code block with target blocks if available ad return it.
	 *
	 * @return
	 */
	def getAbbreviatedStatement(){
		def statementName = "\$ABBR";
		
		def nonTargetBlockClosure={
			"\n"//Currently we don't handle AES block so this space is to handle it when target block is not there.
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure)
	}

	/**
	 * This will wrap PK statement code block with target blocks if available and return it.
	 *
	 * @return
	 */
	def getPKStatement(){
		def statementName = "\$PK"
		
		def targetBlockClosure={
			"\n"+ getPredCoreStatement().toString() + getDifferentialInitialConditions().toString()
		}
		def nonTargetBlockClosure={
			pk(getPredCoreStatement().toString() + getDifferentialInitialConditions().toString())
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure,targetBlockClosure)
	}

	/**
	 * This will wrap model statement code block with target blocks if available ad return it.
	 * 
	 * @return
	 */
    def getModelStatement() {
        def statementName = "\$MODEL";
		
		def targetBlockClosure={
			"\n"+ getModelStatementCodeBlock()
		}
		def nonTargetBlockClosure={
			model(getModelStatementCodeBlock().toString())
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure,targetBlockClosure)
    }
	
	/**
	 * This method adds model statement code block
	 * 
	 * @return
	 */
	def getModelStatementCodeBlock() {
		StringBuilder sb = new StringBuilder();
		getDerivativeVariableTypes().each { var ->
			sb << endline(indent("COMP (${rename(var.symbId.toUpperCase())})"))
		}
		sb
	}

    def getStructuralParameters() {
        StringBuilder sb = new StringBuilder();

        parameters.structuralVars.each { name, type ->
            if (! type.getClass().equals( DerivativeVariableType.class) ) {
				def dvt = conversionContext.convert(type)
                sb << endline("${dvt}")
                if (getDerivativeVariableTypes()) {
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
        sb << endline("${conversionContext.convert(type, inputNameToVariable)}")
        sb
    }

    def getCovariatesFromModel() {
        StringBuilder cov = new StringBuilder();
        pmlDOM.modelDefinition.parameterModel.each {
			 cov << endline(getCovariatesFromModelType(it).toString()) 
			 }
        cov
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
                        cov << conversionContext.convert(individualParameterType.gaussianModel.generalCovariate.assign)
                    }
                } else if (individualParameterType.assign) {
                    Equation equation = individualParameterType.assign.equation
					ParameterVariableSortHelper paramSortHelper = new ParameterVariableSortHelper();
					String name = paramSortHelper.getSymbIdName(equation,true)
					
                    Theta theta = parameters.isTheta(name)
                    if (theta) {
                        int thetaIndex = theta.index
                        visitedThetas.add(name)
                        cov << buildCovariateString(name.toUpperCase(), thetaIndex, null)
                    } else {
                    	SimpleParameterType simpleParam = parameters.getGroupVariable(name);
						if(simpleParam!=null){
							cov << printParameter(simpleParam.assign,name)
						}
                    }
                }
            }
        }
        cov << reportNotVisitedThetas(visitedThetas)
        cov << reportCovariate()
        cov
    }
	
    def reportCovariate() {
        def sb = new StringBuilder();
        pmlDOM.modelDefinition.covariateModel.each {
            it.covariate.each { sb << endline(indent("${conversionContext.convert(it)}")) }
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

    def getIndividualsFromModel() {
		StringBuilder ind = new StringBuilder();
		
		ParameterVariableSortHelper paramSortHelper = new ParameterVariableSortHelper();
		Map references = paramSortHelper.getParameterVariableRefs(pmlDOM.modelDefinition)
		Map comparisonMap = paramSortHelper.arrangeParameterElements(references)
		comparisonMap.each {k,v ->
			ind << endline(getIndividualsFromModelType(v).toString())
		}
		ind
    }

    private StringBuilder getIndividualsFromModelType(JAXBElement elem) {
        StringBuilder ind = new StringBuilder();

            if (elem.value instanceof IndividualParameterType) {
                ind = printIndividualParameter(elem, ind)
			} else if (!parameters.isOmega(elem.value.symbId) && (elem.value instanceof SimpleParameterType) && elem.value.assign) {
                ind = printParameter(elem.value.assign, elem.value.symbId)
			}
		ind
	}


	private StringBuilder printIndividualParameter(JAXBElement elem, StringBuilder ind) {
		IndividualParameterType individualParameterType = (IndividualParameterType) elem.value;
		String symbolName = individualParameterType.symbId

		if (individualParameterType.gaussianModel) {
			String name =null
			if (individualParameterType.gaussianModel.linearCovariate) {
				name = individualParameterType.gaussianModel.linearCovariate.populationParameter.assign.symbRef.symbIdRef
				def omegaIndices = []
				individualParameterType.gaussianModel.randomEffects.each {
					it.symbRef.each {
						SymbolRefType pret = (SymbolRefType)it
						String etaName = pret.getSymbIdRef()

						int omegaIndex = conversionContext.getOmegaIndex(etaName)
						omegaIndices.add(omegaIndex)
					}
				}
				ind.append(buildIndividualString(symbolName.toUpperCase(), name.toUpperCase(), omegaIndices))
			} else if (individualParameterType.gaussianModel.generalCovariate) {
				ind << printParameter(individualParameterType.gaussianModel.generalCovariate.assign,symbolName)
			}
		} else if (individualParameterType.assign) {
			ind << printParameter(individualParameterType.assign,symbolName)
		}
		return ind
	}
	
	/**
	 * When we come across individual or simple parameter we are passing variable symbol and RHS,
	 * in order to parse RHS and to define the variable accordingly.
	 * 
	 * @param assign
	 * @param symbId
	 * @return
	 */
	private StringBuilder printParameter(Rhs assign, String symbId) {
		StringBuilder ind = new StringBuilder();
		String rightPart
		if(assign?.equation?.piecewise) {
			String pieceWiseAsNmtran = conversionContext.convert(assign.equation.piecewise,symbId)
					.replaceAll("\\(t-tD\\)", "TIME").toUpperCase()
			ind << endline(indent(pieceWiseAsNmtran))
		} else if (assign.equation) {
			rightPart = conversionContext.convert(assign.equation)
		} else if (assign.scalar) {
			rightPart = assign.scalar.value.value
		}
		if(rightPart) {
			ind << indent("${rename(symbId.toUpperCase())}=${rightPart}")
		}
		return ind
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
            List<SimpleParameterType> simpleParameterTypes = pmlDOM.modelDefinition.structuralModel.simpleParameter
            for (CommonVariableDefinitionType varType in structuralModel.commonVariable.value ) {
                String variableName = varType.symbId
                if (varType.assign && (varType.assign.equation.piecewise) ) {
                    PiecewiseType piecewise = varType.assign.equation.piecewise
                    String pieceWiseAsNmtran = conversionContext.convert(piecewise, variableName).replaceAll("\\(t-tD\\)", "TIME").toUpperCase()
                    conditionals.add(pieceWiseAsNmtran);
                }
            }
        }
        conditionals
    }

    def getErrorFormula() {
        def sb = new StringBuilder();
        for (ObservationModelType type in pmlDOM.modelDefinition.observationModel) {
            if (type.observationError.value instanceof GaussianObsError) {
                ObservationErrorType errorType = type.observationError.value
                String name = String.valueOf(errorType.output.symbRef.symbIdRef.value)
                if (definedInDES || getDerivativeVariableTypes()) {
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
                def secondPart = "${conversionContext.convert(errorType.errorModel.assign.equation.functionCall, derivativeNames, true)}"
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
                sb << indent("Y${conversionContext.convert(errorType.assign)}")
            }
        }
        endline(sb.toString())
    }

}
