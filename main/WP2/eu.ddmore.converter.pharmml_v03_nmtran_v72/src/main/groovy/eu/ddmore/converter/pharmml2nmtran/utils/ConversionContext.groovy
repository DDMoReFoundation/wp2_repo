/*******************************************************************************
 * Copyright (C) 2014 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
package eu.ddmore.converter.pharmml2nmtran.utils

import java.util.Map;

import javax.xml.bind.JAXBElement;

import eu.ddmore.converter.pharmml2nmtran.equivalence.EquivalenceClass
import eu.ddmore.converter.pharmml2nmtran.equivalence.EquivalenceClassesComputer;
import eu.ddmore.converter.pharmml2nmtran.model.CorrelationKey
import eu.ddmore.converter.pharmml2nmtran.model.Omega
import eu.ddmore.converter.pharmml2nmtran.model.TargetBlock
import eu.ddmore.converter.pharmml2nmtran.model.Theta;
import eu.ddmore.converter.pharmml2nmtran.statements.DataStatement
import eu.ddmore.converter.pharmml2nmtran.statements.EstimationStatement
import eu.ddmore.converter.pharmml2nmtran.statements.NMTranFormatter
import eu.ddmore.converter.pharmml2nmtran.statements.OmegasStatement
import eu.ddmore.converter.pharmml2nmtran.statements.PredStatement
import eu.ddmore.converter.pharmml2nmtran.statements.SigmasStatement
import eu.ddmore.converter.pharmml2nmtran.statements.SimulationStatement
import eu.ddmore.converter.pharmml2nmtran.statements.TableStatement
import eu.ddmore.converter.pharmml2nmtran.statements.TargetBlockStatement;
import eu.ddmore.converter.pharmml2nmtran.statements.ThetasStatement;
import eu.ddmore.libpharmml.dom.PharmML;
import eu.ddmore.libpharmml.dom.commontypes.CommonVariableDefinitionType
import eu.ddmore.libpharmml.dom.commontypes.DerivativeVariableType
import eu.ddmore.libpharmml.dom.commontypes.FuncParameterDefinitionType
import eu.ddmore.libpharmml.dom.commontypes.FunctionDefinitionType
import eu.ddmore.libpharmml.dom.commontypes.IdValueType;
import eu.ddmore.libpharmml.dom.commontypes.IntValueType
import eu.ddmore.libpharmml.dom.commontypes.RealValueType;
import eu.ddmore.libpharmml.dom.commontypes.Rhs;
import eu.ddmore.libpharmml.dom.commontypes.ScalarRhs
import eu.ddmore.libpharmml.dom.commontypes.SymbolRefType;
import eu.ddmore.libpharmml.dom.commontypes.VariableAssignmentType
import eu.ddmore.libpharmml.dom.commontypes.VariableDefinitionType
import eu.ddmore.libpharmml.dom.maths.BinopType;
import eu.ddmore.libpharmml.dom.maths.Condition;
import eu.ddmore.libpharmml.dom.maths.ConstantType
import eu.ddmore.libpharmml.dom.maths.Equation;
import eu.ddmore.libpharmml.dom.maths.EquationType
import eu.ddmore.libpharmml.dom.maths.FunctionCallType
import eu.ddmore.libpharmml.dom.maths.LogicBinOpType;
import eu.ddmore.libpharmml.dom.maths.PieceType;
import eu.ddmore.libpharmml.dom.maths.PiecewiseType;
import eu.ddmore.libpharmml.dom.maths.UniopType;
import eu.ddmore.libpharmml.dom.maths.FunctionCallType.FunctionArgument
import eu.ddmore.libpharmml.dom.modeldefn.CommonParameterType
import eu.ddmore.libpharmml.dom.modeldefn.CorrelationType
import eu.ddmore.libpharmml.dom.modeldefn.CovariateDefinitionType
import eu.ddmore.libpharmml.dom.modeldefn.CovariateModelType
import eu.ddmore.libpharmml.dom.modeldefn.CovariateRelationType
import eu.ddmore.libpharmml.dom.modeldefn.GaussianObsError
import eu.ddmore.libpharmml.dom.modeldefn.GeneralObsError
import eu.ddmore.libpharmml.dom.modeldefn.IndividualParameterType
import eu.ddmore.libpharmml.dom.modeldefn.ObservationErrorType
import eu.ddmore.libpharmml.dom.modeldefn.ObservationModelType
import eu.ddmore.libpharmml.dom.modeldefn.ParameterModelType
import eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomVariableType
import eu.ddmore.libpharmml.dom.modeldefn.SimpleParameterType;
import eu.ddmore.libpharmml.dom.modeldefn.StructuralModelType
import eu.ddmore.libpharmml.dom.modeldefn.IndividualParameterType.GaussianModel
import eu.ddmore.libpharmml.dom.modellingsteps.EstimationStepType
import eu.ddmore.libpharmml.dom.modellingsteps.InitialEstimateType
import eu.ddmore.libpharmml.dom.modellingsteps.ParameterEstimateType
import eu.ddmore.libpharmml.dom.modellingsteps.SimulationStepType
import eu.ddmore.libpharmml.dom.modellingsteps.ToEstimateType
import eu.ddmore.libpharmml.dom.trialdesign.PopulationMappingType
import eu.ddmore.libpharmml.dom.uncertml.NormalDistribution
import eu.ddmore.pharmacometrics.model.trialdesign.CategoricalAttribute

/**
 * This is a main conversion class that provides overloaded 'convert' methods 
 * that convert libPharmML types to NMTRAN. 
 * The first argument of each convert method is a libPharmML type we want to convert.
 * In many cases maps are passed as arguments for string substitutions 
 * (inspired by the substitution evaluation strategy in lamda-calculus).
 */
public class ConversionContext extends NMTranFormatter {

    private PharmML pmlDOM
    private Parameters parameters
    private Set<String> symbolRefs
    private Map<String, String> epsilonToSigma
    private Map<String, FunctionDefinitionType> functions
	private PredStatement predStatement
	SigmasStatement sigmaStatement
    private String inputHeaders
    private String fileBase
    private List<String> omegasInPrintOrder
	private Map<String,String> simpleParameterToNmtran = new HashMap<String,String>()
	private boolean isGaussianObsError
	TargetBlockConverter targetBlockConverter = new TargetBlockConverter()
	

    public ConversionContext(PharmML pmlDOM, File src) {
        this.pmlDOM = pmlDOM
        fileBase = src.name.substring(0, src.name.indexOf('.'))
        symbolRefs = new HashSet<String>()
        epsilonToSigma = new HashMap<String, String>()
        functions = new HashMap<String, FunctionDefinitionType>()
        omegasInPrintOrder = new ArrayList<String>()
        simpleParameterToNmtran = new HashMap<String,String>()
        parameters = new Parameters(pmlDOM)
        parameters.init()
        findFunctions()
        sigmaStatement = new SigmasStatement("pmlDOM":pmlDOM, "conversionContext":this)
        epsilonToSigma = sigmaStatement.epsilonToSigma

		predStatement = new PredStatement(pmlDOM, parameters, this)
		setupSimpleParameters()
		//Target block xml location is always 'target/'
		File targetBlocksFile = new File(src.parentFile.path+File.separator+"targetblock"+File.separator+src.getName())
		if(targetBlocksFile.exists()){
			prepareTargetBlocks(targetBlocksFile)
		}
    }
	
	
	
	/**
	 * This method reads target block file and populates maps with code blocks for statements. 
	 * 
	 * @param targetSrc
	 */
	private void prepareTargetBlocks(File targetblockSrcFile){
		targetBlockConverter.initExternalCodeMaps()
		TargetBlockStatement targetBlockStatement = new TargetBlockStatement()
		targetBlockStatement.getTargetBlocks(targetblockSrcFile).each { targetblock ->			
			targetBlockConverter.prepareExternalCode(targetblock)
		}
	}

    private void findFunctions() {
        pmlDOM.functionDefinition.each {
            FunctionDefinitionType type = it;
            functions.put(type.symbId, type)
        }
    }

	private void setupSimpleParameters() {
		for (StructuralModelType structuralModel in pmlDOM.modelDefinition.structuralModel ) {
			structuralModel.simpleParameter.each {
				simpleParameterToNmtran[it.symbId] = convert(it)
			}
		}
	}
	
	def getSizeStatement(){
		def statementName = "\$SIZE";
		
		def nonTargetBlockClosure={
			"\n"//Currently we dont handle SIZE block so this space is to handle it when target block is not there.
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure)
	}
	
    def getProblemStatement() {
		def statementName = "\$PROBLEM";
		
		def nonTargetBlockClosure={
			statementName+" ${pmlDOM.name.value}\n"
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure)
    }
	
    def getInputStatement(List<String> headers) {
		
		def statementName = "\$INPUT";
		inputHeaders = headers.join(' ')
		
		def targetBlockClosure={
			"\n${inputHeaders}\n"
		}
		
		def nonTargetBlockClosure={
				statementName+" ${inputHeaders}\n"
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure,targetBlockClosure)
    }
	
	def getDataStatement(DataStatement ds){
		def statementName = "\$DATA";
		def targetBlockClosure={
			ds.getStatement()
		}
		def nonTargetBlockClosure={
				statementName+ " "+ ds.getStatement()
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure,targetBlockClosure)
	}

    def getEstimationStatement() {
		def statementName = "\$EST";
		def estimStatement = new EstimationStatement()
		def sb = new StringBuilder()
		
		def targetBlockClosure={
			"\n" +estimStatement.getStatement(pmlDOM)			
		}
		
		def nonTargetBlockClosure={
			statementName+" " +estimStatement.getStatement(pmlDOM)
		}
		sb << targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure,targetBlockClosure)+"\n"
		
		//after $EST, as cov statement depends upon estFIM flag output of estimation statement
		sb << getCovStatement(estimStatement.estFIMFound)
    }
	
	/**
	 * This method adds $COV block. 
	 * 
	 * @param estFIMFound
	 * @return
	 */
	def getCovStatement(boolean estFIMFound){
		def statementName = "\$COV";

		// We don't have any code blocks to append for $COV. Not sure about the details though.
		def targetBlockClosure={
			"\n" 
		}
		
		def nonTargetBlockClosure={
			"\n"+(estFIMFound)? statementName+"\n": ""
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure,targetBlockClosure)
	}
	
    def getSimulationStatement() {
		def statementName = "\$SIM";
		
		def targetBlockClosure={
			"\n"+new SimulationStatement().getStatement(pmlDOM)
		}
		
		def nonTargetBlockClosure={
			statementName+" "+new SimulationStatement().getStatement(pmlDOM)
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure,targetBlockClosure)
    }
	
	def getPriorStatement(){
		def statementName = "\$PRIOR";
		
		def nonTargetBlockClosure={
			"\n"//Currently we dont handle PRIOR block so this space is to handle it when target block is not there.
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure)
	}

    def getPred() {
		predStatement.getStatement()
    }

    def getTableStatement() {
		def statementName = "\$TABLE";
		
		def targetBlockClosure={
			"\n"+ new TableStatement("parameters":parameters, "inputHeaders":inputHeaders, conversionContext:this).getStatement(fileBase)
		}
		
		def nonTargetBlockClosure={
				"\n"+ new TableStatement("parameters":parameters, "inputHeaders":inputHeaders, conversionContext:this).getStatement(fileBase)
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure,targetBlockClosure)
        
    }

    def getThetasStatement() {
		def statementName = "\$THETA";
		
		def targetBlockClosure={
			"\n"+ new ThetasStatement("parameters":parameters, "pmlDOM":pmlDOM, conversionContext:this).getStatement()
		}
		
		def nonTargetBlockClosure={
				statementName+"\n"+ new ThetasStatement("parameters":parameters, "pmlDOM":pmlDOM, conversionContext:this).getStatement()
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure,targetBlockClosure)
    }

    def getOmegasStatement() {
		def statementName = "\$OMEGA";
		OmegasStatement omegaStatement = new OmegasStatement("parameters":parameters, "pmlDOM":pmlDOM, "conversionContext":this)
		
		def targetBlockClosure={
			def sb = new StringBuilder()
			sb << omegaStatement.getCorrelatedEquivalenceClass()
			sb << "\n"+ omegaStatement.getStatement()
			sb
		}
		
		def nonTargetBlockClosure={
			def sb = new StringBuilder()
			sb << omegaStatement.getCorrelatedEquivalenceClass()			
			sb << statementName+"\n"+ omegaStatement.getStatement()
			sb
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure,targetBlockClosure)
    }

    public StringBuilder getSigmasStatement() {
		
		def statementName = "\$SIGMA";
		def targetBlockClosure={
			"\n"+ sigmaStatement.getStatement()
		}
		def nonTargetBlockClosure={
			new StringBuilder(statementName+"\n"+ sigmaStatement.getStatement())
		}
		targetBlockConverter.addTargetBlockCode(statementName,nonTargetBlockClosure,targetBlockClosure)
    }

    public StringBuilder convert(VariableAssignmentType type) {
        return convert(type, true)
    }
	
	/**
	 * This method should convert IDValuetypes but at this moment as we don't have details,
	 * it just returns associated value.
	 * @param type
	 * @return
	 */
	public StringBuilder convert(IdValueType type) {
		def sb = new StringBuilder()
		sb << " \n\t${type.value}\n"
	}

    /**
     * 
     * @param type the VariableAssignmentType to convert
     * @param verbose if true, NMTRAN comments are appended in the end of the line
     * @return the NMTRAN representation of the type
     */
    public StringBuilder convert(VariableAssignmentType type, boolean verbose) {
        def sb = new StringBuilder();
        sb << convert(type.assign, false)
        if (verbose) {
            sb << " ; ${rename(type.symbRef.symbIdRef.toUpperCase())}\n"
        }
        sb
    }
	
	/**
	 * Verifies if variable is not categorical covariate before renaming it.
	 * 
	 * @param name
	 * @return
	 */
	public String verifyAndRename(String name){
		return (isCovariateVariableType(name))?name:rename(name)
	}
	
	/**
	 * Checks if variable name is of categorical covariates type or not.
	 * 
	 * @param variableTypeName
	 * @return
	 */
	private boolean isCovariateVariableType(String variableTypeName){
		boolean result
		if(predStatement == null){
				return result
		}
		if(predStatement.categoricalCovariates.containsKey(variableTypeName)){
			return true;
		}else{
			for(CategoricalAttribute catCovariate: predStatement.categoricalCovariates.values()){
				result = catCovariate.categoriesToIndex.containsKey(variableTypeName)
				if(result){
					return result
				}
			}
		}		
	}

    public StringBuilder convert(FunctionCallType type) {
        convert(type, simpleParameterToNmtran, false)
    }

    /**
     * 
     * @param type the FunctionCallType to convert
     * @param inputNameToValue a string substitution map (inspired by the substitution evaluation strategy in lamda-calculus).
     * @return the NMTRAN representation of the type
     */
    public StringBuilder convert(FunctionCallType type, Map<String, String> inputNameToValue, boolean isGaussianObsError) {
        StringBuilder sb = new StringBuilder();
        String functionName = type.symbRef.symbIdRef
        List<String> args = []
		this.isGaussianObsError = isGaussianObsError
        type.functionArgument.each {
            if (it.constant) {
                args.add(convert(it.constant))
            } else if (it.equation) {
                args.add(convert(it.equation, inputNameToValue) )
            } else if (it.scalar) {
                args.add(convert(it.scalar.value) )
            } else if (it.symbRef) {
				if(isGaussianObsError){
					args.add(convert(it.symbRef, simpleParameterToNmtran).toString())
				}else{
                	args.add(toTheta(convert(it.symbRef).toString() ))
				}
            }
        }
        sb << convert(functions.get(functionName), args)
    }

    /**
     * 
     * @param type the FunctionDefinitionType to convert
     * @param args the list of function arguments
     * @return the NMTRAN representation of the type
     */
    public StringBuilder convert(FunctionDefinitionType type, List<String> args) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> inputNameToValue = new HashMap<String, String>()
        for (int i=0; i<type.functionArgument.size(); i++) {
            String argName = type.functionArgument[i].symbId
            String argValue = args[i]
            inputNameToValue.put(argName, argValue)
        }
        if (type.definition.equation) {
            sb.append( convert(type.definition.equation, inputNameToValue) )
        } else {
            sb.append( convert(type.definition.symbRef, inputNameToValue) )
        }
        sb
    }

    /**
     * 
     * @param s
     * @return a Theta NMTRAN representation of s, if s is a Theta, otherwise s
     */
    private String toTheta(String s) {
        Theta theta = parameters.isTheta(s)
        theta ? "THETA(${theta.index})" : s
    }

    public StringBuilder convert(ParameterEstimateType type) {
        return convert(type, true)
    }

    /**
     * 
     * @param type the ParameterEstimateType type to convert
     * @param verbose if true NMTRAN comment will be appended in the end of the line and 
     *                if the parameter is fixed the 'FIX' label will be appended
     * @return the NMTRAN representation of the type
     */
    public StringBuilder convert(ParameterEstimateType type, boolean verbose) {
        String name = type.symbRef.symbIdRef

        def sb = new StringBuilder("(")
        if (type.lowerBound) {
            sb << "${convert(type.lowerBound)},"
        }
        sb << convert(type.initialEstimate)
        if (type.upperBound) {
            sb << ",${convert(type.upperBound)}"
        }

        if (!verbose || !type.initialEstimate.fixed) {
            sb << ")"
        }

        if (verbose) {
            if (type.initialEstimate.fixed) {
                sb << " FIX)"
            }
            sb << " ; ${name}\n"
        }
        sb
    }

    public StringBuilder convert(ScalarRhs type) {
        StringBuilder sb = new StringBuilder();
        type.scalar.value
        if (type.scalar) {
            sb << convert(type.scalar.value)
        }
        sb
    }

    public StringBuilder convert(InitialEstimateType type) {
        StringBuilder sb = new StringBuilder();
        if (type.equation) {
            sb.append( convert(type.equation) )
        }

        if (type.scalar) {
            sb.append( convert(type.scalar.value) )
        }
        sb
    }

    public StringBuilder convert(PiecewiseType piecewise, String name) {
        convert(piecewise, name, simpleParameterToNmtran)
    }
	
	public StringBuilder convert(PiecewiseType piecewise, Map<String,String> simpleParameterToNmtran) {
		convert(piecewise, null, simpleParameterToNmtran)
	}

    /**
     * 
     * @param piecewise the PiecewiseType type to convert
     * @param variableName the name of the variable that will be assigned to different values depending on the condition 
     * @param simpleParameterToNmtran a map of precomputed nmtran representations of parameters
     * @return the NMTRAN representation of the type
     */
    public StringBuilder convert(PiecewiseType piecewise, String variableName, Map<String,String> simpleParameterToNmtran) {
        StringBuilder sb = new StringBuilder();
        piecewise.piece.each {
            PieceType pieceIt = (PieceType)it
            if (sb) {
                sb << endline(indent("ELSE"));
            }
            StringBuilder pieceBuilder = convert(pieceIt, variableName);

            for (String symbolRef : symbolRefs) {
                String nmTranSymbolDefinition = simpleParameterToNmtran.get(symbolRef);
                if (nmTranSymbolDefinition && !nmTranSymbolDefinition.equalsIgnoreCase(rename(symbolRef))) {
                    sb << endline(indent("${nmTranSymbolDefinition}"))
                }
            }
            sb << pieceBuilder
        }
        sb << endline(indent("ENDIF"));
    }

    public StringBuilder convert(VariableDefinitionType type) {
        convert(type, simpleParameterToNmtran)
    }

    /**
     * 
     * @param type the VariableDefinitionType type to convert
     * @param inputNameToVariable a string substitution map
     * @return the NMTRAN representation of the type
     */
    public StringBuilder convert(VariableDefinitionType type, Map<String, String> inputNameToVariable) {
        def sb = new StringBuilder()
        String name = type.symbId
        if (inputNameToVariable.containsKey(name)) {
            name = inputNameToVariable[name]
        }
        sb << "\t${rename(name.toUpperCase())}${convert(type.assign, true, inputNameToVariable)}"
    }

    /**
     * 
     * @param piece the PieceType to convert
     * @param variableName the name of the variable to assign a value
     * @return the NMTRAN representation of the type
     */
    public StringBuilder convert(PieceType piece, String variableName) {
		String variableLHS = (variableName)?"\t${rename(variableName)}=":""
        StringBuilder sb = new StringBuilder();
        if (piece.condition) {
            sb << convert(piece.condition) << "\n";
        }
        if (piece.scalar) {
            sb << endline(variableLHS+piece.scalar.value.value);
        }
        if (piece.binop) {
            sb << endline(variableLHS+"${convert(piece.binop)}")
        } else if(piece.symbRef) {
			sb << endline(variableLHS+"${convert(piece.symbRef)}")
        }
        sb
    }

    /**
     * 
     * @param type the RealValueType type to convert
     * @param variableName the name of the variable to assign a value
     * @return the NMTRAN representation of the type
     */
    public StringBuilder convert(RealValueType type, String variableName) {
        def sb = new StringBuilder()
        sb << " \n\t${rename(variableName)}=${type.value}\n"
    }

    public String convert(RealValueType type) {
        return convert(type, simpleParameterToNmtran)
    }

    public String convert(RealValueType type, Map<String, String> inputNameToValue) {
        type.value
    }

    public StringBuilder convert(Condition condition) {
        StringBuilder sb = new StringBuilder();
        if (condition.logicBinop) {
			// IF only applied at the level of the conditional; logical operations are all within the same IF statement
            sb << indent("IF${convert(condition.logicBinop)} THEN ")
        }
        sb
    }

    public StringBuilder convert(LogicBinOpType logicBinaryOperator) {
		// Convert the logical to it's purest form (x.eq.y, x.ne.z, etc.)
		// Brackets are around the logical to preserve operator precedence 
        def sb = "(${convert(logicBinaryOperator.content.get(0).value)}.${getMathRepresentationOf(logicBinaryOperator.op)}"
        sb << ".${convert(logicBinaryOperator.content.get(1).value)})"
    }

    public String convert(IntValueType type) {
        return type.value
    }

    public StringBuilder convert(BinopType binopType) {
        return convert(binopType, simpleParameterToNmtran)
    }

    /**
     * 
     * @param binopType the BinopType to convert
     * @param inputNameToValue a string substitution map
     * @return the NMTRAN representation of the type
     */
    public StringBuilder convert(BinopType binopType, Map<String, String> inputNameToValue) {
        def sb = new StringBuilder()
		simpleParameterToNmtran = inputNameToValue
        String left = convert(binopType.content.get(0).value)//, inputNameToValue)
        String operator = getMathRepresentationOf(binopType.op)
        String right = convert(binopType.content.get(1).value)//, inputNameToValue)
        switch(binopType.op) {
            case 'plus':
                sb << sum(left, operator, right)
            break;
            case 'minus':
                sb << subtraction(left, operator, right)
            break;
            case 'divide':
                sb << division(left, operator, right)
            break;
            default:
                sb << genericMathOperation(left, operator, right)
            break
        }
        return sb
    }
    
    private String sum(String left, String operator, String right) {
        left + operator + right
    }
    
    private String subtraction(String left, String operator, String right) {
        '(' + addParenthesesIfNeeded(left) + operator + addParenthesesIfNeeded(right) + ')'
    }
    
    private String division(String left, String operator, String right) {
        addParenthesesIfNeeded(left) + operator + "(${right})"
    }
    
    private String genericMathOperation(String left, String operator, String right) {
        addParenthesesIfNeeded(left) + operator + addParenthesesIfNeeded(right)
    }
    private String addParenthesesIfNeeded(String operand) {
        if ( (operand.contains('+') || operand.contains('-')) && !operand.startsWith('(')) {
            return '('+ operand +')'
        } else {
            return operand
        }
    }

    public StringBuilder convert(UniopType type) {
        return convert(type, simpleParameterToNmtran)
    }

    /**
     * 
     * @param type the UniopType type to convert
     * @param inputNameToValue a string substitution map
     * @return the NMTRAN representation of the type
     */
    public StringBuilder convert(UniopType type, Map<String, String> inputNameToValue) {
        StringBuilder sb = new StringBuilder()

        sb.append(getMathRepresentationOf(type.op))

        if (type.symbRef) {
            sb.append( convert(type.symbRef, inputNameToValue));
        } else {
            sb.append('(')

            if (type.binop) {
                sb.append( convert(type.binop, inputNameToValue) );
            }
            if (type.constant) {
                sb.append(  convert(type.constant, inputNameToValue) );
            }
            if (type.functionCall) {
                sb.append( convert(type.functionCall, inputNameToValue));
            }
            if (type.scalar) {
                sb.append( convert(type.scalar.value));
            }
            if (type.uniop) {
                sb.append( convert(type.uniop, inputNameToValue));
            }
            sb.append(')')
        }
        sb
    }
	
	/**
	 * 
	 * @param type
	 * @return
	 */
    public StringBuilder convert(SymbolRefType type) {
        return convert(type, simpleParameterToNmtran)
    }

    /**
     * 
     * @param type the SymbolRefType type to convert
     * @param inputNameToValue a string substitution map
     * @return the NMTRAN representation of the type
     */
    public StringBuilder convert(SymbolRefType type, Map<String, String> inputNameToValue) {
        StringBuilder sb = new StringBuilder()
        String name = type.symbIdRef

        CommonVariableDefinitionType structVar = parameters.getStructuralVariable(name)
		Theta theta
		if(!isGaussianObsError){
        	theta = parameters.isTheta(name)
		}
		if(theta){
				sb.append(theta.toIndexString())
        } else if (parameters.etas.contains(name)) {
            sb << "ETA(${getOmegaIndex(name)})"
        } else if (epsilonToSigma.get(name)) {
            String sigmaName = epsilonToSigma.get(name)
            int sigmaIndex = parameters.isSigma(sigmaName).index
            sb << "EPS(${sigmaIndex})"
        } else if (parameters.getDosingTimeVarname().equals(name)) {
            sb.append(parameters.getDosingTime())
        } else if (name.equals("t")) {
            sb.append('TIME')
        } else {
            if (inputNameToValue.containsKey(name)) {
                name = inputNameToValue.get(name)
            } else if (parameters.varToName.containsKey(name)) {
                name = parameters.varToName.get(name)
            }
            if (!name.contains("A(")) {
                name = verifyAndRename(name)
            }
            sb.append(name.toUpperCase())
        }
        symbolRefs.add(type.symbIdRef);
        sb
    }

    /**
     * 
     * @param type the DerivativeVariableType type to convert
     * @param index the order of type among all the derivative variables of the model
     * @return the NMTRAN representation of the type
     */
    public StringBuilder convert(DerivativeVariableType type, int index) {
        StringBuilder sb = new StringBuilder()
        sb << "\tDADT(${index})${convert(type.assign)}\n"
        sb
    }

    public StringBuilder convert(SimpleParameterType type) {
        def sb = new StringBuilder()
        sb << "${rename(type.symbId.toUpperCase())}"
        if (type.assign) {
            sb << convert(type.assign)
        }
        sb
    }

    public StringBuilder convert(Rhs type) {
        convert(type, true, simpleParameterToNmtran)
    }
		
    /**
     * 
     * @param type the Rhs type to convert
     * @param verbose if true '=' symbol will be printed before the assignment
     * @param inputNameToValue a string substitution map
     * @return the NMTRAN representation of the type
     */
    public StringBuilder convert(Rhs type, boolean verbose, Map<String, String> inputNameToValue) {
        def sb = new StringBuilder()
        if (verbose) {
            sb << "="
        }
        if (type.equation) {
            sb << convert(type.equation, inputNameToValue)
        } else if (type.symbRef) {
            sb << convert(type.symbRef, inputNameToValue)
        } else if (type.scalar) {
            sb << type.scalar.value.value
        }
        sb
    }

    public StringBuilder convert(Equation type) {
        return convert(type, simpleParameterToNmtran)
    }

    /**
     * 
     * @param type the Equation type to convert
     * @param inputNameToValue a string substitution map
     * @return he NMTRAN representation of the type
     */
    public StringBuilder convert(Equation type, Map<String, String> inputNameToValue) {
        StringBuilder sb = new StringBuilder()
        if (type.binop) {
            sb << convert(type.binop, inputNameToValue)
        } else if (type.symbRef) {
            sb << convert(type.symbRef, inputNameToValue)
        } else if(type.piecewise) {
        	sb << convert(type.piecewise, inputNameToValue)
		} else if(type.scalar) {
        	sb << type.scalar.value.value
		}else if(type.uniop) {
        	sb << convert(type.uniop, inputNameToValue)
		}
        
        sb
    }
	
	public StringBuilder convert(ConstantType type) {
		return convert(type, simpleParameterToNmtran)
	}

    /**
     * 
     * @param type the ConstantType to convert
     * @param inputNameToValue a string substitution map
     * @return the NMTRAN representation of the type
     */
	public StringBuilder convert(ConstantType type, Map<String, String> inputNameToValue) {
		StringBuilder sb = new StringBuilder()
		sb << getValueOfConstant(type.op)
		sb
	}
	
	private String getValueOfConstant(String s){
		Properties constantProperties = new Properties();
		constantProperties.load(ConversionContext.class.getResource("/constants.properties").openStream());
		if(constantProperties.stringPropertyNames().contains(s)){
			return constantProperties.getProperty(s)
		}else{
			throw new IllegalArgumentException("Not supported constant: "+ s);
		}
	}

    public StringBuilder convert(CovariateDefinitionType type) {
        StringBuilder sb = new StringBuilder()
        String name = type.symbId
        if (type.continuous && type.continuous.abstractContinuousUnivariateDistribution) {
            String mean = type.continuous.abstractContinuousUnivariateDistribution.value.mean.var.varId
            String stddev = type.continuous.abstractContinuousUnivariateDistribution.value.stddev.var.varId
            Omega omega = parameters.isOmega(stddev)

            sb << "${rename(name.toUpperCase())} = ${mean.toUpperCase()}*EXP(ETA(${omega.index}))"
        }
        sb
    }

	/**
	 * Returns the omega index of the given eta.
	 * 
	 * Maps the eta to the omega in parameters, and returns the value from the print order
	 * 
	 * @param etaName
	 * @return int
	 */
	public int getOmegaIndex(String etaName) {
		String omega = parameters.etaToOmega[etaName]
		if(omega==null) {
			throw new RuntimeException("Cannot find eta ${etaName} in list ${parameters.etaToOmega.toMapString()}")
		}
		if(!omegasInPrintOrder.contains(omega)) {
			//throw new RuntimeException("Cannot find omega ${omega} in omegas ${omegasInPrintOrder.toString()}")
		}
		omegasInPrintOrder.indexOf(omega)+1
	}
	
    private String getMathRepresentationOf(String s) {
        if (s.equals("minus")) {
            return "-";
        } else if (s.equals("plus")) {
            return "+";
        } else if (s.equals("times")) {
            return "*";
        } else if (s.equals("divide")) {
            return "/";
        } else if (s.equals("exp")) {
            return "EXP";
        } else if (s.equals("lt")) {
            return "LT";
        } else if (s.equals("leq")) {
            return "LE";
        } else if (s.equals("gt")) {
            return "GT";
        } else if (s.equals("geq")) {
            return "GE";
        } else if (s.equals("eq")) {
            return "EQ";
        } else if (s.equals("neq")) {
            return "NE";
        } else if (s.equals("and")) {
            return "AND";
        } else if (s.equals("or")) {
            return "OR";
        } else if (s.equals("log")) {
            return "log";
        } else if (s.equals("ln")) {
            return "ln";
        } else if (s.equals("sqrt")) {
            return "SQRT";
        } 
		else if (s.equals("power")) {
            return "**";
        } else if (s.equals("cos")) {
            return "COS";
        } else {
            throw new IllegalArgumentException("Not supported operand: "+ s);
        }
    }

    private boolean isSimulation() {
        boolean simulationStepFound=false
        pmlDOM.modellingSteps.commonModellingStep.each {
            if (it.value instanceof SimulationStepType) {
                simulationStepFound=true;
            }
        }
        simulationStepFound
    }

    private boolean isEstimation() {
        boolean estimationStepFound=false
        pmlDOM.modellingSteps.commonModellingStep.each {
            if (it.value instanceof EstimationStepType) {
                estimationStepFound=true;
            }
        }
        estimationStepFound
    }
}
