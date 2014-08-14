package eu.ddmore.converter.mdl2json.domain;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger
import org.ddmore.mdl.mdl.BlockStatement
import org.ddmore.mdl.mdl.EstimationBlock;
import org.ddmore.mdl.mdl.FunctionCallStatement
import org.ddmore.mdl.mdl.GroupVariablesBlock
import org.ddmore.mdl.mdl.GroupVariablesBlockStatement
import org.ddmore.mdl.mdl.IndividualVariablesBlock
import org.ddmore.mdl.mdl.InputVariablesBlock
import org.ddmore.mdl.mdl.ModelObject
import org.ddmore.mdl.mdl.ModelObjectBlock
import org.ddmore.mdl.mdl.ModelPredictionBlock
import org.ddmore.mdl.mdl.ModelPredictionBlockStatement
import org.ddmore.mdl.mdl.ObservationBlock
import org.ddmore.mdl.mdl.OutputVariablesBlock
import org.ddmore.mdl.mdl.RandomVariableDefinitionBlock
import org.ddmore.mdl.mdl.StructuralParametersBlock;
import org.ddmore.mdl.mdl.SymbolDeclaration
import org.ddmore.mdl.mdl.SymbolName
import org.ddmore.mdl.mdl.VariabilityParametersBlock

import eu.ddmore.converter.mdl2json.utils.MDLUtils
import eu.ddmore.converter.mdlprinting.MdlPrinter

public class Model extends Expando implements MDLPrintable, MDLAsJSON {

	public static final Logger logger = Logger.getLogger(Model.class)
	private static MdlPrinter mdlPrinter = MdlPrinter.getInstance()
	
	static final String IDENTIFIER = "mdlobj"
	public static String STRUCTURAL_PARAMETERS = "STRUCTURAL_PARAMETERS"
	public static String MODEL_INPUT_VARIABLES = "MODEL_INPUT_VARIABLES"
	public static String VARIABILITY_PARAMETERS = "VARIABILITY_PARAMETERS"
	public static String GROUP_VARIABLES = "GROUP_VARIABLES"
	public static String RANDOM_VARIABLE_DEFINITION = "RANDOM_VARIABLE_DEFINITION"
	public static String INDIVIDUAL_VARIABLES = "INDIVIDUAL_VARIABLES"
	public static String MODEL_PREDICTION = "MODEL_PREDICTION"
	public static String OBSERVATION = "OBSERVATION"
	public static String ESTIMATION = "ESTIMATION"
	public static String MODEL_OUTPUT_VARIABLES = "MODEL_OUTPUT_VARIABLES"
	
	public Model(ModelObject modelObject) {
	
		setProperty(IDENTIFIER_PROPNAME, IDENTIFIER)
		for(ModelObjectBlock modelObjectBlock : modelObject.getBlocks()) {
			if(modelObjectBlock.getInputVariablesBlock()) {
				// Model Input Variables - note, getter is called InputVariables
				setProperty(MODEL_INPUT_VARIABLES, makeModelInputVariables(modelObjectBlock.getInputVariablesBlock()) )
			}
			else if(modelObjectBlock.getStructuralParametersBlock()) {
				// Structural Parameters
				setProperty(STRUCTURAL_PARAMETERS, makeStructuralParameters(modelObjectBlock.getStructuralParametersBlock()))
			} else if(modelObjectBlock.getVariabilityParametersBlock()) {
				// Variability Parameters Block
				setProperty(VARIABILITY_PARAMETERS, makeVariabilityParameters(modelObjectBlock.getVariabilityParametersBlock()))
			} else if(modelObjectBlock.getGroupVariablesBlock()) {
				// Group Variables
				setProperty(GROUP_VARIABLES, makeGroupVariables(modelObjectBlock.getGroupVariablesBlock()))
			} else if(modelObjectBlock.getRandomVariableDefinitionBlock()) {
				// Random Variable Definition
				setProperty(RANDOM_VARIABLE_DEFINITION, makeRandomVariableDefinitionBlock(modelObjectBlock.getRandomVariableDefinitionBlock()))
			} else if(modelObjectBlock.getIndividualVariablesBlock()) {
				// Individual Variables
				setProperty(INDIVIDUAL_VARIABLES, makeIndividualVariables(modelObjectBlock.getIndividualVariablesBlock()))
			} else if(modelObjectBlock.getModelPredictionBlock()) {
				// Model Predictions
				setProperty(MODEL_PREDICTION, makeModelPredictionBlock(modelObjectBlock.getModelPredictionBlock()))
			} else if(modelObjectBlock.getObservationBlock()) {
				// Observations
				setProperty(OBSERVATION, makeObservationBlock(modelObjectBlock.getObservationBlock()))
			} else if(modelObjectBlock.getEstimationBlock()) {
				// Estimation block
				setProperty(ESTIMATION, makeEstimationBlock(modelObjectBlock.getEstimationBlock()))
			} else if(modelObjectBlock.getOutputVariablesBlock()) {
				// Output variables
				setProperty(MODEL_OUTPUT_VARIABLES, makeModelOutputVariables(modelObjectBlock.getOutputVariablesBlock()))
			} else if(modelObjectBlock.getSimulationBlock()) {
				// Simulation block
				throw new UnsupportedOperationException("Do not support simulation block yet")
			} else if(modelObjectBlock.getTargetBlock()) {
				// Target block 
				throw new UnsupportedOperationException("Do not support target block yet")
			}
		}			
	}

	/**
	 * Constructor from a JSON object
	 * 
	 * @param json The json object to construct the Model with
	 */
	public Model(Object json) {
		getProperties().putAll(json)
	}
	
	private List makeModelInputVariables(InputVariablesBlock inputVariables) {
		MDLUtils.makeSymbolNamedList(inputVariables.getVariables())
	}

	private List makeModelOutputVariables(OutputVariablesBlock outputVariables) {
		outputVariables.getVariables().collect { SymbolName sn ->
			sn.getName()
		}
	}

	private List makeStructuralParameters(StructuralParametersBlock structuralParametersBlock) {
		MDLUtils.makeSymbolNamedList(structuralParametersBlock.getParameters())
	}

	private List makeVariabilityParameters(VariabilityParametersBlock variabilityParameters) {
		MDLUtils.makeSymbolNamedList(variabilityParameters.getParameters())
	}

	private makeGroupVariables(GroupVariablesBlock groupVariables) {
		List statements = []
		groupVariables.getStatements().each { GroupVariablesBlockStatement statement ->
            def mixtureBlock = statement.getMixtureBlock()
            def stmt = statement.getStatement()
			if (mixtureBlock) {
				mixtureBlock.getStatements().each {
					// TODO: "it" is a BlockStatement; needs to be pretty-printed correctly
					statements.add(mdlPrinter.print(it))
				}
			} else if (stmt) {
				// TODO: "stmt" is a BlockStatement; needs to be pretty-printed correctly
				statements.add(mdlPrinter.print(stmt))
			}
		}
		statements.join()
	}
	
	private makeIndividualVariables(IndividualVariablesBlock indVariables) {
		List statements = []
		indVariables.getStatements().each { BlockStatement statement ->
			statements.add(mdlPrinter.print(statement))
		}
		statements.join("${IDT*2}")
	}
	
	private List makeRandomVariableDefinitionBlock(RandomVariableDefinitionBlock randomVariables) {
		MDLUtils.makeSymbolNamedList(randomVariables.getVariables())
	}

    private makeModelPredictionBlock(ModelPredictionBlock mpb) {
        LinkedHashMap modPredBlock = [:]
        List odeStatements = []
        List libraryStatements = []
		List statements = []
        
		mpb.getStatements().each { ModelPredictionBlockStatement statement ->
            
			if (statement.getStatement() != null) {
                statements.add("${IDT*2}" + mdlPrinter.print(statement.getStatement()))
			}
			else if (statement.getOdeBlock() != null) {
				statement.getOdeBlock().getStatements().each {
                    odeStatements.add("${IDT*3}" + mdlPrinter.print(it))
                }
			} else if (statement.getLibraryBlock() != null) {
				statement.getLibraryBlock().getStatements().each { FunctionCallStatement fcs ->
					libraryStatements.add("${IDT*3}${fcs.getSymbolName().getName()}=${mdlPrinter.print(fcs.getExpression())}\n")
				}
			}
		}
        
        if (!odeStatements.isEmpty()) {
            modPredBlock.put("ODE", odeStatements.join())
        }

		if (!libraryStatements.isEmpty()) {
		    modPredBlock.put("LIBRARY", libraryStatements.join())
		}
        
		modPredBlock.put("content", statements.join())
        
        modPredBlock
	}
	
	/**
	 * Make the observation block
	 */
	private makeObservationBlock(ObservationBlock observationBlock) {
		StringBuffer statements = new StringBuffer()
		observationBlock.getStatements().each { BlockStatement statement ->
			statements.append(mdlPrinter.print(statement))
		}
		statements.toString()
	}
	
	private makeEstimationBlock(EstimationBlock estimationBlock) {
		StringBuffer statements = new StringBuffer()
		estimationBlock.getStatements().each { BlockStatement statement ->
			statements.append(mdlPrinter.print(statement))
		}
		statements.toString()
	}

	/**
	 * Given JSON-format List of Maps representing Random Variable Definitions,
	 * process each one: Extract the 'name' attribute and put this on the LHS of
	 * a string expression; turn the remaining attributes in the Map into a
	 * comma-separated list of "key=value", in one enclosing set of brackets,
	 * and put this on the RHS of the string expression; put the operator in between
	 * as "~". After processing all variable definitions in the list, join all the
	 * generated string expressions together to produce the MDL fragment.
	 * 
	 * @param randomVariables - In JSON format, i.e. a List of Maps
	 * @return the equivalent MDL fragment
	 */
	public String makeRandomVariableMDL(List<Map> randomVariables) {
		List varStrings = []
		randomVariables.each {randomVariable ->
			def randomVarName = randomVariable['name']
			def randomVarAttrs = randomVariable.minus(['name':randomVarName])
			varStrings.add("${randomVarName} ~ (${randomVarAttrs.collect{ key,value->"${key}=${value}"}.join(",")})")
		}
		varStrings.join("\n${IDT*2}")
	}
	
	public String makeModelPredictionMDLBlock(Map content) {
		StringBuffer buff = new StringBuffer()
		
		if (content.containsKey("ODE")) {
			buff.append("ODE {\n").append(content.get("ODE")).append("${IDT*2}}\n${IDT*2}")
		}
		if (content.containsKey("LIBRARY")) {
			buff.append("LIBRARY {\n").append(content.get("LIBRARY")).append("${IDT*2}}\n${IDT*2}")
		}
		if (content.containsKey("content")) {
			buff.append("\n").append(content.get("content"));
		}
		buff.toString()
	}
	
	public String toMDL() {
		StringBuffer mdl = new StringBuffer()
		
		// Possibly may need to do something about ordering here.
		
		getProperties().minus(["identifier":"mdlobj"]).each { block, content ->
			mdl.append("\n${IDT}${block} {\n${IDT*2}")
			switch (block) {
				case "MODEL_PREDICTION":
					mdl.append(makeModelPredictionMDLBlock(content));
					break;
				case "OBSERVATION":
					if (content instanceof List) {
						mdl.append(content.join("\n${IDT*2}"))
					} else {
						mdl.append(content)
					}
					break;
				case "MODEL_INPUT_VARIABLES":
				case "STRUCTURAL_PARAMETERS":
				case "VARIABILITY_PARAMETERS":
					mdl.append(MDLUtils.makeMDLFromSymbolNamedList(content)).append("\n")
					break;
				case "MODEL_OUTPUT_VARIABLES":
					mdl.append(content.join("\n${IDT*2}")).append("\n")
					break;
				case "GROUP_VARIABLES":
				case "INDIVIDUAL_VARIABLES":
				case "ESTIMATION":
					mdl.append(content)
					break;
				case "RANDOM_VARIABLE_DEFINITION":
					mdl.append(makeRandomVariableMDL(content)).append("\n")
					break;
				default:
					break;
			}
			mdl.append("${IDT}}")
		}

		return """${IDENTIFIER} {${mdl.toString()}
}
"""
	}
}