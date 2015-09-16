package eu.ddmore.converter.mdl2pharmml

import java.util.ArrayList
import java.util.Comparator
import java.util.Map
import java.util.TreeMap
import org.ddmore.mdl.mdl.Argument
import org.ddmore.mdl.mdl.ArgumentExpression
import org.ddmore.mdl.mdl.EventType
import org.ddmore.mdl.mdl.Expression
import org.ddmore.mdl.mdl.IndividualVarType
import org.ddmore.mdl.mdl.List
import org.ddmore.mdl.mdl.MOGObject
import org.ddmore.mdl.mdl.ModelObject
import org.ddmore.mdl.mdl.ParameterObject
import org.ddmore.mdl.mdl.RandomList
import org.ddmore.mdl.mdl.SymbolDeclaration
import org.ddmore.mdl.mdl.VariabilityType
import org.ddmore.mdl.types.MdlDataType
import org.ddmore.mdl.validation.AttributeValidator
import org.ddmore.mdl.validation.Utils

import static eu.ddmore.converter.mdl2pharmml.Constants.*

class ModelDefinitionPrinter {
	protected extension DistributionPrinter distrPrinter = DistributionPrinter::getInstance();
	protected extension CustomDistributionPrinter custDistPrinter
	protected extension PKMacrosPrinter pkPrinter = null;
	protected extension MathPrinter mathPrinter = null;
	protected extension ReferenceResolver resolver = null;
	
	private static val CONTINUOUS_OBS = "continuous"
	private static val COUNT_OBS = "count"
	private static val DISCRETE_OBS = "discrete"
	private static val CATEGORICAL_OBS = "categorical"
	private static val TTE_OBS = "tte"
	
	
	new(MathPrinter mathPrinter, ReferenceResolver resolver){
		this.mathPrinter = mathPrinter;
		this.resolver = resolver;
		this.custDistPrinter = new CustomDistributionPrinter(distrPrinter)
		pkPrinter = new PKMacrosPrinter(mathPrinter, resolver);
	}		
	
	//////////////////////////////////////
	// I. Model Definition
	//////////////////////////////////////	
	def print_mdef_ModelDefinition(MOGObject mog){
		var objects = Utils::getMOGObjects(mog);
		var mObj = Utils::getModelObject(objects);
		if (mObj == null) return '''''';
		var pObj = Utils::getParameterObject(objects);
		'''
		<ModelDefinition xmlns="«xmlns_mdef»">
			«print_mdef_VariabilityModel»
			«mObj.print_mdef_CovariateModel»
			«mObj.print_mdef_ParameterModel(pObj)»
			«mObj.print_mdef_StructuralModel»
			«mObj.print_mdef_ObservationModel»
		</ModelDefinition>
		'''
	}

	/////////////////////////
	// I.b Variability Model
	/////////////////////////	
	protected def print_mdef_VariabilityModel(){
		var model = "";
		if (vm_err_vars.size() > 0){
			model = model + vm_err_vars.print_mdef_VariabilityModel("vm_err", VAR_TYPE_ERROR);
		}		
		if (vm_mdl_vars.size() > 0){
			model = model + vm_mdl_vars.print_mdef_VariabilityModel("vm_mdl", VAR_TYPE_PARAMETER);
		}
		return model;
	}
	
	protected def print_mdef_VariabilityModel(Map<String, Integer> vars, String blkId, String varType){
		var model = "";
		if (vars.size() > 0){
			var bvc =  new ValueComparator(vars);
			var sorted_map = new TreeMap<String, Integer>(bvc);
			sorted_map.putAll(vars);
			var prev = "";
			var levels = "";
			for (s: sorted_map.entrySet){
				levels = levels +	'''
					«IF prev.length > 0»
						<Level referenceLevel="«IF s.value == 2»true«ELSE»false«ENDIF»" symbId="«s.key»">
							<ParentLevel>
								<ct:SymbRef symbIdRef="«prev»"/>
							</ParentLevel>
						</Level>
					«ELSE»
						<Level referenceLevel="«IF s.value == 2»true«ELSE»false«ENDIF»" symbId="«s.key»"/>
					«ENDIF»
				'''
				prev = s.key
			}			
			model = '''
				<VariabilityModel blkId="«blkId»" type="«varType»">
					«levels»
				</VariabilityModel>
			'''		
		}		
		return model;
	}
	
	protected def print_VariabilityReference(String level)'''
		<ct:VariabilityReference>
			«level.print_ct_SymbolRef»
		</ct:VariabilityReference>
	'''
    
	/////////////////////////
	// I.c Covariate Model
	/////////////////////////	
	//CovariateModel (transformation with reference)
	protected def print_mdef_CovariateModel(ModelObject mObj){
		var model = "";
		var skipped = new ArrayList<String>();
		//First print transformed covariates (and exclude them from the list to avoid double defintiion) 
		for (b: mObj.blocks){
			if (b.covariateBlock != null){
				for (s: b.covariateBlock.variables){
					var covName = s.name;
					var covType = "Continuous";
					//Print transformed covariates
					var transformation = "";
					if (s.expression != null){
						var dependencies = Utils::getDependencies(s.expression);
						var isTransformed = false;
						var continue = true; //no 'break' command in xText
						for (v: dependencies){
							if (cm_vars.contains(v) && continue){
								isTransformed = true;
								covName = v;
								skipped.add(covName); 
								continue = false;
							} 
						}
						if (isTransformed){
							transformation =  '''
								<«covType»>
									<Transformation>
									    <TransformedCovariate symbId="«s.name»"></TransformedCovariate>
										«s.expression.print_Math_Equation»
									</Transformation>
								</«covType»>
								'''
								skipped.add(s.name);
							}
					}
					model = model + '''
					«IF transformation.length > 0»
						<Covariate symbId="«covName»">
							«transformation»
						</Covariate>
					«ENDIF»	
					'''
				}
			}
		}
		//Then print all remaining covariates
		for (b: mObj.blocks){
			if (b.covariateBlock != null){
				for (s: b.covariateBlock.variables){
					var covName = s.name;
					if (!skipped.contains(covName)){
						var covType = "Continuous";
						//Print categorical covariates
						var categorical = "";
						if (s.list != null){
							var type = getAttributeExpression(s.list.arguments, AttributeValidator::attr_type.name);
							if (type.isCategorical){
								covType = "Categorical";
								categorical = '''«type.print_Categorical»''';
							}
						}
						model = '''
						<Covariate symbId="«covName»">
							«IF categorical.length > 0»
								«categorical»
							«ELSE»
								<«covType»/>
							«ENDIF»
						</Covariate>
						''' + model;
					}
				}
			}
		}
		if (model.length > 0){
			model = '''
				<CovariateModel blkId="cm">
					«model»
				</CovariateModel>
			'''
		}
		return model;
	}	
		
	/////////////////////////////
	// I.d Parameter Model
	////////////////////////////	
	protected def print_mdef_ParameterModel(ModelObject mObj, ParameterObject pObj){		
		var statements = "";
		if (mObj != null){
			for (b: mObj.blocks){
				//STRUCTURAL_PARAMETERS
				if (b.structuralParametersBlock != null){
					for (id: b.structuralParametersBlock.parameters) 
						if (id.name != null)
							statements = statements + 
							'''<SimpleParameter symbId = "«id.name»"/>
							'''
		  		}
		  		//VARIABILITY_PARAMETERS
		  		if (b.variabilityParametersBlock != null){
					for (id: b.variabilityParametersBlock.parameters){
						if (id.name != null)
							statements = statements + 
							'''<SimpleParameter symbId = "«id.name»"/>
							'''
					}
		  		}
		  		//GROUP_VARIABLES (covariate parameters)
				if (b.groupVariablesBlock != null){
					for (st: b.groupVariablesBlock.statements){
						if (st.variable != null){
							statements = statements + st.variable.print_SymbolDeclaration("SimpleParameter", false);
						}							
					}
				}	
		  	}
			for (b: mObj.blocks){
				//RANDOM_VARIABLES_DEFINITION
				if (b.randomVariableDefinitionBlock != null){
					if (b.randomVariableDefinitionBlock.arguments != null){
						var level = b.randomVariableDefinitionBlock.arguments.getAttribute(AttributeValidator::attr_level_ref.name);
						if (level.length > 0){
							for (s: b.randomVariableDefinitionBlock.variables){
								if (s.name != null)
									statements = statements + s.print_mdef_RandomVariable(level);
							} 
						}
					}
		  		}
		  		//INDIVIDUAL_VARIABLES
				if (b.individualVariablesBlock != null){
					for (s: b.individualVariablesBlock.variables){
						statements = statements + s.print_SymbolDeclaration("IndividualParameter", false);
					} 
		  		}
		  	}
  		}
  		statements = statements + mObj.print_mdef_CollerationModel(pObj); 
	  	if (statements.length > 0){
			'''
				<ParameterModel blkId="pm">
					«statements»
				</ParameterModel>
			''';
		}
	}
	
	/////////////////////////////
	// I.d_1 CorrelationModel
	/////////////////////////////
	protected def print_mdef_CollerationModel(ModelObject mObj, ParameterObject pObj){
		var model = "";
		if (pObj != null){
			for (b: pObj.blocks){
				if (b.variabilityBlock != null){	
					for (s: b.variabilityBlock.parameters){
						if (s.list != null){
							val type = s.list.arguments.getAttribute(AttributeValidator::attr_type_randomEff.name);
							if (type.equals(VariabilityType::CORR.toString) || type.equals(VariabilityType::COV.toString)){
								val params = s.list.arguments.getAttributeExpression(AttributeValidator::attr_params.name);
								val values = s.list.arguments.getAttributeExpression(AttributeValidator::attr_value.name);
								if (params != null && values != null 
									&& params.vector != null && values.vector != null
									&& params.vector.expression.expressions != null && values.vector.expression.expressions != null
								){
									var k = 0; 
									for (i: 1..params.vector.expression.expressions.size - 1){
										for (j: 0..i - 1){
											var rv1 = params.vector.expression.expressions.get(j);
											var rv2 = params.vector.expression.expressions.get(i);
											if (k < values.vector.expression.expressions.size){
												var value = values.vector.expression.expressions.get(k);
												k = k + 1;
												var level = mObj.getLevel(rv1.toStr);
												model = model + type.print_mdef_Correlation(level, rv1, rv2, value);
											}
										}
									}
								}
							}
						}
					}
				}				
			}
		}
		return model;
	}
	
	def getLevel(ModelObject mObj, String randomVar){
		if (mObj != null){
			for (b: mObj.blocks){
				if (b.randomVariableDefinitionBlock != null){	
					if (b.randomVariableDefinitionBlock.arguments != null){
						var level =	b.randomVariableDefinitionBlock.arguments.getAttribute(AttributeValidator::attr_level_ref.name);	
						for (s: b.randomVariableDefinitionBlock.variables){
							if (s.name.equals(randomVar)){
								return level;	
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	/*
	def print_mdef_Correlation_Matrix(String matrixType, Vector values, Vector params, String level)'''
		<Correlation deviationMatrixType="«matrixType.convertMatrixType»">
			«IF level != null»
				«level.print_VariabilityReference»
			«ENDIF»
            «values.print_ct_Matrix(params, "Any")»
		</Correlation>
	'''
	*/
	
	def print_mdef_Correlation(String type, String level, Expression rv1, Expression rv2, Expression value){
		var res = '''
			<RandomVariable1>
				«rv1.print_Math_Expr»
			</RandomVariable1>
			<RandomVariable2>
				«rv2.print_Math_Expr»
			</RandomVariable2>
		'''
		if (type.equals(VariabilityType::COV.toString))
			res  = res + '''
				<Covariance>
					«value.print_Math_Expr»
				</Covariance>
			'''
		if (type.equals(VariabilityType::CORR.toString))
			res  = res + '''
				<CorrelationCoefficient>
					«value.print_Math_Expr»
				</CorrelationCoefficient>
			'''
		'''
			<Correlation>
				«IF level != null»
					«level.print_VariabilityReference»
				«ENDIF»
				<Pairwise>
					«res»
				</Pairwise>
			</Correlation>	
		'''
	}
	
	protected def print_mdef_RandomVariable(SymbolDeclaration s, String level)'''
		«IF s.randomList != null && s.name != null»
			<RandomVariable symbId="«s.name»">
				«IF level.length > 0»
					«level.print_VariabilityReference»
				«ENDIF»
				«print_uncert_Distribution(s.randomList)»
			</RandomVariable>
		«ENDIF»
	'''
	
	/////////////////////////
	// I.e Structural Model
	/////////////////////////
	protected def print_mdef_StructuralModel(ModelObject mObj){
		var model ="";
		if (mObj != null){
			var variables = "";
			var macros = "";
			for (b: mObj.blocks){
				if (b.modelPredictionBlock != null){
					for (st: b.modelPredictionBlock.statements){
						//MODEL_PREDICTION
						if (st.variable != null) 
							variables = variables + '''«st.variable.print_SymbolDeclaration("ct:Variable", true)»''';
						//ODE
						if (st.odeBlock != null){
							for (s: st.odeBlock.variables){
								if (s.list != null){
									if (s.list.arguments.getAttributeExpression(AttributeValidator::attr_deriv.name) != null){
										variables = variables + '''«s.print_SymbolDeclaration("ct:DerivativeVariable", true)»''';	
									}
								} else {
									variables = variables + '''«s.print_SymbolDeclaration("ct:Variable", true)»''';	
								}
							}
						}
						//LIBRARY
//						if (st.libraryBlock != null){
//							for (s: st.libraryBlock.statements)
//								variables = variables + s.expression.print_Math_FunctionCall;
//						} 
						//COMPARTMENT
						if (st.pkMacroBlock != null){
							macros += st.pkMacroBlock.statements.printCompartmentDefinitions
							macros += st.pkMacroBlock.statements.printMacros
//							macros = macros + '''
//			<PKmacros>
//		'''
//							
//							for (s: st.pkMacroBlock.statements){
//								if (s.variable != null)
//									macros = macros + s.variable.print_PKMacros;
//								if (s.list != null)
//									macros = macros + s.list.print_PKMacros;
//							}
//							macros = macros + '''
//			</PKmacros>
//		'''
							 
						}
					}
				}
			}
			model = model + 
			'''
				«IF (variables.length > 0)»
					<StructuralModel blkId="sm">
						«IF (variables.length > 0)»«variables»«ENDIF»
						«IF (macros.length > 0)»«macros»«ENDIF»
					</StructuralModel>
				«ENDIF»
			'''
		}
		return model;
	}
		
	/////////////////////////////
	// I.f Observation Model
	/////////////////////////////
	protected def print_mdef_ObservationModel(ModelObject mObj){
		var res = "";
		if (mObj != null){
			for (b: mObj.blocks){
				if (b.observationBlock != null){
					for (st: b.observationBlock.variables){
						var observation = st.print_mdef_ObservationModel;
						var idx = 1 as int;
						val omBlkId = resolver.getReferenceBlock(st.name)
						if (observation.length >0 )
							res = res + '''
								<ObservationModel blkId="«omBlkId»">
									«observation»
								</ObservationModel>
							''';
							idx = idx + 1;
						}
				}
			}
		}
		return res;
	}
	
	protected def print_mdef_ObservationModel(SymbolDeclaration s){
		var retVal = ''''''
		if(s.list != null){
			val type = s.list.arguments.getAttribute(AttributeValidator::attr_type.name)
			switch type{
				case CONTINUOUS_OBS: retVal = s.print_mdef_StandardObservation.toString
				case COUNT_OBS: retVal = s.print_mdef_CountObservations.toString
				case DISCRETE_OBS: retVal = s.print_mdef_DiscreteObservations.toString
				case CATEGORICAL_OBS: retVal = s.print_mdef_CategoricalObservations.toString
				case TTE_OBS: retVal = s.print_mdef_TimeToEventObservations.toString
			} 
		}
		else{
			retVal = s.print_mdef_ExplicitObservation.toString
		}
		return retVal
	}
	
	private def print_mdef_ExplicitObservation(SymbolDeclaration s)'''
		«IF s.name != null && s.expression != null»
			<ContinuousData>
				<General symbId="«s.name»">
					«s.expression.print_Assign»
				</General>
			</ContinuousData>
		«ENDIF»
	'''	
	
	private def getInverseFunction(String linkFunction, String paramVar){
		switch(linkFunction){
			case "log": return '''
			<math:Uniop op="exp">
				«paramVar.print_ct_SymbolRef»
			</math:Uniop>
			'''
			case "identity": return '''
				«paramVar.print_ct_SymbolRef»
			'''
		}
	}
	
	def getFunctionArgument(RandomList distn, String argName){
		if(distn.arguments.namedArguments != null){
			for(Argument arg : distn.arguments.namedArguments.arguments){
				if(arg.argumentName.argName == argName)	
					return arg.expression
			}
		}
		else if(!distn.arguments.unnamedArguments.arguments.isEmpty)
			return distn.arguments.unnamedArguments.arguments.get(0)
	}
		
	def isReference(ArgumentExpression expr){
		var retVal = false
		if(expr != null && expr.expression != null && expr.expression.expression != null
			&& expr.expression.expression.expression != null)
			retVal = MdlDataType::isReference(expr.expression.expression.expression)
			
		retVal
	}	
	
	private def print_mdef_CountObservations(SymbolDeclaration s) {
		var name = s.name
		val linkFunction = s.list.arguments.getAttribute(AttributeValidator::attr_link.name);
		val distn = s.list.arguments.getAttributeRandomList(AttributeValidator::attr_distrib.name);
		val paramVar = getFunctionArgument(distn, "lambda");
		var String tmpParamVar = null;
		if(isReference(paramVar)){
			tmpParamVar = paramVar.toStr
		}
		'''
			<Discrete>
				<CountData>
				«IF tmpParamVar != null»
						<!-- Note that this parameter is local to this block, but uses the same name
							as the lambda argument. The  --> 
						<SimpleParameter symbId="«tmpParamVar»">
						<ct:Assign>
							<math:Equation>
				«IF linkFunction.length > 0»
					«getInverseFunction(linkFunction, paramVar.toStr)»
				«ELSE»
					«paramVar.toStr.print_ct_SymbolRef»
				«ENDIF»
				</math:Equation>
				</ct:Assign>
				</SimpleParameter>
				«ENDIF»
				<CountVariable symbId="«name»"/>
				<PMF linkFunction="identity">
					«print_uncert_Distribution(distn)»
				</PMF>
				</CountData>
			</Discrete>
		'''
	}
	
	private def print_mdef_DiscreteObservations(SymbolDeclaration s) {
		var name = s.name
		val linkFunction = s.list.arguments.getAttribute(AttributeValidator::attr_link.name);
		val distn = s.list.arguments.getAttributeRandomList(AttributeValidator::attr_distrib.name);
		val paramVar = getFunctionArgument(distn, "probability");
		var String tmpParamVar = null;
		if(isReference(paramVar)){
			tmpParamVar = paramVar.toStr
		}
		val category = "cat1"
		'''
			<Discrete>
				<CategoricalData ordered="no">
				«IF tmpParamVar != null»
						<!-- Note that this parameter is local to this block, but uses the same name
							as the lambda argument. The  --> 
						<SimpleParameter symbId="«tmpParamVar»">
						<ct:Assign>
							<math:Equation>
				«IF linkFunction.length > 0»
					«getInverseFunction(linkFunction, paramVar.toStr)»
				«ELSE»
					«paramVar.toStr.print_ct_SymbolRef»
				«ENDIF»
				</math:Equation>
				</ct:Assign>
				</SimpleParameter>
				«ENDIF»
					<ListOfCategories>
						<Category symbId="«category»"/>
					</ListOfCategories>
					<CategoryVariable symbId="«name»"/>
				<PMF linkFunction="identity">
					«distn.printDiscreteDistribution(category)»
				</PMF>
				</CategoricalData>
			</Discrete>
		'''
	}
	
	
	private def print_mdef_CategoricalObservations(SymbolDeclaration s) {
		var name = s.name
		val categories = s.list.arguments.getAttributeExpression(AttributeValidator::attr_categories.name);
		val probabilities = s.list.arguments.getAttributeExpression(AttributeValidator::attr_probabilities.name);
		'''
			<Discrete>
				<CategoricalData>
					<ListOfCategories>
					«FOR cat : categories.vector.expression.expressions»
						<Category symbId="c«cat.toStr»"/>
					«ENDFOR»
					</ListOfCategories>
				<CategoryVariable symbId="«name»"/>
				«FOR i : 0 ..< categories.vector.expression.expressions.size»
					<ProbabilityAssignment>
						<Probability linkFunction="identity">
							<math:LogicBinop op="eq">
								<ct:SymbRef symbIdRef="«name»"/>
								<ct:SymbRef symbIdRef="c«categories.vector.expression.expressions.get(i).toStr»"/>
							</math:LogicBinop>
						</Probability>
						<ct:Assign>
							«probabilities.vector.expression.expressions.get(i).print_Math_Expr»
						</ct:Assign>
					</ProbabilityAssignment>
				«ENDFOR»
				</CategoricalData>
			</Discrete>
		'''
	}


	private def print_mdef_TimeToEventObservations(SymbolDeclaration s) {
		var name = s.name
		val haz = s.list.arguments.getAttributeExpression(AttributeValidator::attr_hazard.name);
		val event = s.list.arguments.getAttribute(AttributeValidator::attr_event.name);
		val maxEvent = s.list.arguments.getAttributeExpression(AttributeValidator::attr_max_event.name);
		'''
			<Discrete>
				<TimeToEventData>
					<EventVariable symbId="«name»"/>
					<HazardFunction symbId="«haz.toStr»">
						<ct:Assign>
							«haz.print_Math_Expr»
						</ct:Assign>
					</HazardFunction>
					«IF event != null»
						<Censoring censoringType="«event.getEventType»"/>
					«ENDIF»
					«IF maxEvent != null»
						<MaximumNumberEvents>
							<ct:Assign>
								«maxEvent.print_Math_Equation»
							</ct:Assign>
						</MaximumNumberEvents>
					«ENDIF»
				</TimeToEventData>
			</Discrete>
		'''
	}
	
	def getEventType(String eventType){
		switch(eventType){
			case EventType::EXACT.toString: '''rightCensored'''
			case EventType::INTERVAL_CENSORED.toString: '''intervalCensored'''
			default: ''''''
		}
	}


	private def print_mdef_StandardObservation(SymbolDeclaration s){
		var name = s.name
		val error = s.list.arguments.getAttributeExpression(AttributeValidator::attr_error.name);
		val prediction = s.list.arguments.getAttribute(AttributeValidator::attr_prediction_ref.name);
		val eps = s.list.arguments.getAttribute(AttributeValidator::attr_eps.name);
		val transfn = s.list.arguments.getAttribute(AttributeValidator::attr_trans.name);
		'''
			<ContinuousData>
				<Standard symbId="«name»">
					«IF transfn.length > 0»
						<Transformation>
							«transfn»
						</Transformation>
					«ENDIF»
					«IF prediction.length > 0»
						<Output>
							«prediction.print_ct_SymbolRef»
						</Output>
					«ENDIF»
					«IF error != null»
						<ErrorModel>
							«error.print_Assign»
						</ErrorModel>
					«ENDIF»
					«IF eps.length > 0»
						<ResidualError>
							«eps.print_ct_SymbolRef»
						</ResidualError>
					«ENDIF»
				</Standard>
			</ContinuousData>
		'''
	}
	
	protected def print_SymbolDeclaration(SymbolDeclaration st, String tag, Boolean printType){
		if (st.name != null)'''
			<«tag» symbId="«st.name»"«IF printType» symbolType="«TYPE_REAL»"«ENDIF»>
				«IF st.expression != null»
					«st.expression.print_Assign»
				«ENDIF»
				«IF st.list != null»
					«st.list.print_List»
				«ENDIF» 
			</«tag»>
			'''
	}
	
	//Convert special types of lists to PharmML
	def print_List(List list){
		var assign = "";
		var res = "";
		val type = list.arguments.getAttributeExpression(AttributeValidator::attr_type.name);
		if (type.isCategorical){
			//Categorical variables
			if (type.type.type.categories != null && type.type.type.categories.size > 0){
				assign = '''«type.print_Categorical»'''
			} else {
				val define = list.arguments.getAttributeExpression(AttributeValidator::attr_define.name);
				if (define.list != null) 
					assign = '''«define.list.print_Categorical»'''
			}
		} else {
			//Derivative variables	
			val deriv = list.arguments.getAttributeExpression(AttributeValidator::attr_deriv.name);
			if (deriv != null){
				assign = '''«deriv.print_Math_Expr»'''
				var wrtVar = independentVar;
				val wrtVarExpr = list.arguments.getAttributeExpression(AttributeValidator::attr_wrt.name);
				if (wrtVarExpr != null) wrtVar = wrtVarExpr.toStr; 
				res  = 	
				'''
					<ct:IndependentVariable>
						«wrtVar.print_ct_SymbolRef»
					</ct:IndependentVariable>
				'''	
				val initValue = list.arguments.getAttributeExpression(AttributeValidator::attr_init.name);
				val initTime = list.arguments.getAttributeExpression(AttributeValidator::attr_x0.name);
				var initValueRes = '''«AttributeValidator::attr_init.defaultValue.print_Assign»'''; 
				var initTimeRes = '''«AttributeValidator::attr_x0.defaultValue.print_Assign»'''; 
				if (initTime != null) initTimeRes = '''«initTime.print_Assign»'''; 
				if (initValue != null) initValueRes = '''«initValue.print_Assign»'''; 
				res = res + 
				'''
					<ct:InitialCondition>
						<ct:InitialValue>
							«initValueRes»
						</ct:InitialValue>
						<ct:InitialTime>
							«initTimeRes»
						</ct:InitialTime>
					</ct:InitialCondition>
				'''						
			}
		} 
		if (assign.length > 0) return 
		'''
			<ct:Assign>
				<Equation xmlns="«xmlns_math»">
					«assign»
				</Equation>
			</ct:Assign>	
		'''	+ res;
		if (type != null) {
			var modelType = type.toStr;
			//Gaussian models
			if (modelType.equals(IndividualVarType::GENERAL.toString) || modelType.equals(IndividualVarType::LINEAR.toString)){
				//Transformation
				val trans = list.arguments.getAttribute(AttributeValidator::attr_trans.name);
				//Covariate model
				var covariateContent = '''''';
				val fixEffList = list.arguments.getAttributeExpression(AttributeValidator::attr_fixEff.name);
				if (fixEffList != null){
					var pairs = fixEffList.getAttributePairs(AttributeValidator::attr_coeff.name, AttributeValidator::attr_cov.name);
					for (pair: pairs)
						covariateContent = covariateContent + '''«print_Covariate(pair.key.expression, pair.value.expression)»'''		
				}	
				//Random effect
				val ranEff = list.arguments.getAttributeExpression(AttributeValidator::attr_ranEff.name);
				var ranEffExpr = '''''';
				if (ranEff != null){
					if (ranEff.expression != null) ranEffExpr = '''
						<RandomEffects>
							«ranEff.expression.print_Math_Expr»
						</RandomEffects>
						'''	
					if (ranEff.vector != null && ranEff.vector.expression.expressions != null) {
						for (expr: ranEff.vector.expression.expressions)
							ranEffExpr = ranEffExpr + '''
							<RandomEffects>
								«expr.print_Math_Expr»
							</RandomEffects>
							''';
					}
				}				
				//Population parameter
				var popContent = '''''';
				val pop = list.arguments.getAttributeExpression(AttributeValidator::attr_pop.name);
				if (pop != null) popContent = '''«pop.print_Assign»''';
				//General vs. linear model - differences
				var covariateType = "GeneralCovariate";
				if (modelType.equals(IndividualVarType::LINEAR.toString)) {
					covariateType = "LinearCovariate";	
					if (popContent != null) popContent = '''
						<PopulationParameter>
							«popContent»
						</PopulationParameter>
				''';
				}	
				return 
				'''
					<GaussianModel>
						«IF trans.length > 0»
							<Transformation>«trans»</Transformation>
						«ENDIF»
						«IF popContent.length > 0 || covariateContent.length > 0»
							<«covariateType»>
								«popContent»
								«covariateContent»
							</«covariateType»>
						«ENDIF»
						«ranEffExpr»
					</GaussianModel>
				'''
			}
		}
	}
	
	protected def print_Covariate(Expression coeff, Expression cov)'''
		«IF coeff != null && cov != null»
			<Covariate>
				«cov.print_Math_Expr»
				<FixedEffect>
					«coeff.print_Math_Expr»
				</FixedEffect>
			</Covariate>	
		«ENDIF»
	'''
}

//A helper class for sorting maps (according to their integer values) 
class ValueComparator implements Comparator<String> {
    var Map<String, Integer> base;
    new(Map<String, Integer> base) {
        this.base = base;
    }
    override compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } 
    }
}