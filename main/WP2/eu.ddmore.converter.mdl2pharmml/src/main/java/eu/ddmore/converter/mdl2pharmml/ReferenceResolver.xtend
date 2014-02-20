package eu.ddmore.converter.mdl2pharmml

import java.util.HashMap
import java.util.HashSet
import org.ddmore.mdl.mdl.Mcl
import org.ddmore.mdl.mdl.ModelObject
import org.ddmore.mdl.mdl.ParameterObject
import org.ddmore.mdl.mdl.FullyQualifiedSymbolName
import eu.ddmore.converter.mdlprinting.MdlPrinter
import org.ddmore.mdl.mdl.BlockStatement
import org.ddmore.mdl.mdl.Expression

class ReferenceResolver extends MdlPrinter{
	
	extension Mcl mcl = null;  
  	new(Mcl mcl) {
    	this.mcl = mcl
    	prepareCollections(mcl);
 	}  
	
	//List of PharmML declared symbols and corresponding blocks 
	protected var ind_vars = new HashSet<String>();
	protected var vm_err_vars = new HashMap<String, HashSet<String>>(); 
	protected var vm_mdl_vars = new HashMap<String, HashSet<String>>();
	protected var cm_vars = new HashMap<String, HashSet<String>>();
	protected var pm_vars = new HashMap<String, HashSet<String>>();	  
	protected var om_vars = new HashMap<String, HashSet<String>>();	  
	protected var sm_vars = new HashMap<String, HashSet<String>>();	  
		
	override prepareCollections(Mcl m){
		ind_vars.clear;
		vm_err_vars.clear;
		vm_mdl_vars.clear;
		cm_vars.clear;
		super.prepareCollections(m);
		
		for (o: m.objects){
			if (o.modelObject != null){
				//Independent variables
				var indVars = o.modelObject.getIndependentVars();
				if (indVars.size > 0)
					ind_vars.addAll(indVars);	
			
				//VariabilityModel definitions
				var errorVars = o.modelObject.getLevelVars("1");
				if (errorVars.size > 0)
					vm_err_vars.put(o.identifier.name, errorVars);

				var mdlVars = o.modelObject.getLevelVars("2");
				if (mdlVars.size > 0)
					vm_mdl_vars.put(o.identifier.name, mdlVars);

				//CovariateModel				
				var covariateVars = o.modelObject.getCovariateVars();
				if (covariateVars.size > 0)
					cm_vars.put(o.identifier.name, covariateVars);
					
				//StructuralModel
				var structuralVars = o.modelObject.getStructuralVars;	
				if (structuralVars.size > 0)
					sm_vars.put(o.identifier.name, structuralVars);
					
				//ParameterModel
				var parameters = o.modelObject.getParameters;	
				if (parameters.size > 0)
					pm_vars.put(o.identifier.name, parameters);
					
				//ObservationModel
				var observationVars = o.modelObject.getObservationVars;	
				if (observationVars.size > 0)
					sm_vars.put(o.identifier.name, observationVars);
					
			}
			if (o.parameterObject != null){
				//ParameterModel
				var parameters = o.parameterObject.getParameters;	
				if (parameters.size > 0)
					pm_vars.put(o.identifier.name, parameters);
			}
		}
	}
	
	//+Return name of PharmML block for a given reference
	def getReferenceBlock(FullyQualifiedSymbolName ref){
		if (ref.object != null){
			var source = vm_err_vars.get(ref.object.name);
			if (source != null)
				if (source.contains(ref.identifier)) return "vm_err." + ref.object.name;
			source = vm_mdl_vars.get(ref.object.name);
			if (source != null)
				if (source.contains(ref.identifier)) return "vm_mdl." + ref.object.name;
			source = cm_vars.get(ref.object.name);
			if (source != null)
				if (source.contains(ref.identifier)) return "cm." + ref.object.name;
			source = om_vars.get(ref.object.name);
			if (source != null)
				if (source.contains(ref.identifier)) return "om." + ref.object.name;	
			source = sm_vars.get(ref.object.name);
			if (source != null)
				if (source.contains(ref.identifier)) return "sm." + ref.object.name;	
			source = pm_vars.get(ref.object.name);
			if (source != null)
				if (source.contains(ref.identifier)) return "sm." + ref.object.name;	
			return ref.object.name;
		} else {
			//try to find by name
			for (set: vm_err_vars.entrySet)
				if (set.value.contains(ref.identifier)) return "vm_err." + set.key;
			for (set:vm_mdl_vars.entrySet)
				if (set.value.contains(ref.identifier)) return "vm_mdl." + set.key;
			for (set: cm_vars.entrySet)
				if (set.value.contains(ref.identifier)) return "cm." + set.key;
			for (set: om_vars.entrySet)
				if (set.value.contains(ref.identifier)) return "om." + set.key;	
			for (set: sm_vars.entrySet)
				if (set.value.contains(ref.identifier)) return "sm." + set.key;	
			for (set: pm_vars.entrySet)
				if (set.value.contains(ref.identifier)) return "sm." + set.key;	
			return "";
		}
	}	
	
	//+ Return input variables with use=idv (individual)
	def getIndependentVars(ModelObject obj){
		var independentVars = new HashSet<String>();
		for (block: obj.blocks){
			if (block.inputVariablesBlock != null){
				for (s: block.inputVariablesBlock.variables){
					if (s.expression != null){
						if (s.expression.list != null){
							var use = s.expression.list.arguments.getAttribute("use");
							if (use.equalsIgnoreCase("idv") && !independentVars.contains(s.identifier)) 
								independentVars.add(s.identifier);
						}
					}
				}
			}
		}
		return independentVars;
	}
		
		//+ Return a list of covariate variables per object
	def getCovariateVars(ModelObject obj){
		var covariateVars = new HashSet<String>();
		for (b: obj.blocks){
			if (b.inputVariablesBlock != null){
				for (s: b.inputVariablesBlock.variables){
					if (s.expression != null){
						if (s.expression.list != null){
							var use = s.expression.list.arguments.getAttribute("use");
							if (use.equalsIgnoreCase("covariate")) {
								if (!covariateVars.contains(s.identifier))
									covariateVars.add(s.identifier);
							}
						}
					}
												
				}
			}					
		}
		return covariateVars;		
	}
	
	//+Returns declarations for ParameterModel
	def getParameters(ModelObject obj){		
		var parameters = new HashSet<String>();
		for (b: obj.blocks){
			//Model object, GROUP_VARIABLES (covariate parameters)
			if (b.groupVariablesBlock != null){
				for (st: b.groupVariablesBlock.statements){
					if (st.statement != null){
						parameters.addAll(st.statement.getSymbols());
					}							
				}
			}	
			//Model object, RANDOM_VARIABLES_DEFINITION
			if (b.randomVariableDefinitionBlock != null){
				for (s: b.randomVariableDefinitionBlock.variables){
					if (eps_vars.get(s.identifier) != null)
						parameters.add(s.identifier);
				} 
	  		}
	  		//Model object, INDIVIDUAL_VARIABLES
			if (b.individualVariablesBlock != null){
				for (s: b.individualVariablesBlock.statements){
					parameters.addAll(s.getSymbols());
				} 
	  		}
	  	}
	  	return parameters;
	}
	
	//+Returns declarations for ParameterModel
	def getParameters(ParameterObject obj){		
		var parameters = new HashSet<String>();
		for (b: obj.blocks){
			//Parameter object, STRUCTURAL
			if (b.structuralBlock != null){
				for (id: b.structuralBlock.parameters) 
					parameters.add(id.identifier);
			}
			//ParameterObject, VARIABILITY
			if (b.variabilityBlock != null){
				for (st: b.variabilityBlock.statements){
					if (st.parameter != null)
						parameters.add(st.parameter.identifier);
				} 
			}
		}
  		return parameters;
	}
	
	//+Returns declarations in ObservationModel
	def getObservationVars(ModelObject obj){
		var observationVars = new HashSet<String>();
		for (b: obj.blocks){
			if (b.observationBlock != null){
				for (st: b.observationBlock.statements){
					if (st.symbol != null){//!TODO: revise
						observationVars.add(st.symbol.identifier);
						if (st.symbol.expression != null){
							if (st.symbol.expression.expression != null){
								observationVars.addAll(st.symbol.expression.expression.getReferencesToRandomVars);
							}
						}
					}
				}
			}
		}
		return observationVars;
	}
	
	//+ Return a list of structural variables per object
	def getStructuralVars(ModelObject obj){
		var structuralVars = new HashSet<String>();
		for (b: obj.blocks){
			if (b.modelPredictionBlock != null){
				for (st: b.modelPredictionBlock.statements){
					if (st.statement != null) {
						structuralVars.addAll(st.statement.getSymbols);
					} else 
						if (st.odeBlock != null){
							for (s: st.odeBlock.statements){
								structuralVars.addAll(s.getSymbols);
							}
						}
				}
			}			
		}
		return structuralVars;
	}
	
	//+Collect symbol names from a block
	def HashSet<String> getSymbols(BlockStatement b){
		var symbols = new HashSet<String>();
		if (b.symbol != null){
			if (!symbols.contains(b.symbol.identifier)) symbols.add(b.symbol.identifier);
		}
		if (b.statement != null){
			val s = b.statement;
			if (s.ifStatement != null){
				symbols.addAll(s.ifStatement.getSymbols);
			}
			if (s.elseStatement != null){
				symbols.addAll(s.elseStatement.getSymbols);
			}		
			if (s.ifBlock != null){
				for (bb:s.ifBlock.statements)
					symbols.addAll(bb.getSymbols);
			}
			if (s.elseBlock != null){
				for (bb:s.elseBlock.statements)
					symbols.addAll(bb.getSymbols);
			}			
		}
		return symbols;
	}
	
	def isDependentVariable(String s){
		return (eps_vars.get(s) != null)
	}
	
	def isIndependentVariable(String s){
		return (eta_vars.get(s) != null)
	}
	
	//+ For each reference, define its purpose
	def getReferencesToRandomVars(Expression expr){
		var classifiedVars = new HashSet<String>();
		var iterator = expr.eAllContents();
	    while (iterator.hasNext()){
	    	var obj = iterator.next();
	    	if (obj instanceof FullyQualifiedSymbolName){
	    		var ref = obj as FullyQualifiedSymbolName;
	    		if (!classifiedVars.contains(ref.toStr))
			    	if (eps_vars.get(ref.toStr) != null)
			    		classifiedVars.add(ref.toStr)
	    	}
	    }
	    return classifiedVars;
	}
		
}