package eu.ddmore.mdl.validation

import eu.ddmore.mdl.type.MclTypeProvider
import eu.ddmore.mdl.type.MclTypeProvider.BuiltinEnumTypeInfo
import eu.ddmore.mdl.type.MclTypeProvider.SublistTypeInfo
import eu.ddmore.mdl.validation.ListDefinitionProvider.AttributeDefn
import java.util.Map

class SublistDefinitionTable {
	public static val INTERVENTION_SEQ_SUBLIST = "intSeqAtts"
	public static val SAMPLING_SEQ_SUBLIST = "sampSeqAtts"
	public static val FIX_EFF_SUBLIST = "fixEffAtts"
	public static val PRIOR_FORMAT_SUBLIST = "priorFormat"
	public static val COMPLEX_COMBINATION_SUBLIST = "cplxCombSublist"

	public static val COV_ATT = 'cov'
	public static val CATCOV_ATT = 'catCov'
	
	static val COV = new AttributeDefn('cov', true, MclTypeProvider::REAL_TYPE.makeReference)
	static val COEFF = new AttributeDefn('coeff', true, MclTypeProvider::REAL_TYPE.makeReference)
	static val CAT_COV = new AttributeDefn('catCov', true, MclTypeProvider::GENERIC_ENUM_VALUE_TYPE.makeReference)
	static val PRIOR_ELEMENT_TYPE_TYPE = new BuiltinEnumTypeInfo('priorElementType', #{'matrix', 'vector'})
	
	public static val Map<String, SublistTypeInfo> sublistDefns = #{
		FIX_EFF_SUBLIST -> (new SublistTypeInfo(FIX_EFF_SUBLIST, #[COV, CAT_COV, COEFF], #[
																   	#{COEFF.name -> true, COV.name -> true},
																   	#{COEFF.name -> true, CAT_COV.name -> true}
																   ])),
		INTERVENTION_SEQ_SUBLIST -> (new SublistTypeInfo(INTERVENTION_SEQ_SUBLIST, #[
											new AttributeDefn("admin", true,	ListDefinitionTable::ADMINISTRATION_TYPE.makeReference),
											new AttributeDefn("start", true, MclTypeProvider::REAL_TYPE),
											new AttributeDefn("end", true, MclTypeProvider::REAL_TYPE)],
											#[#{'admin' -> true, 'start' -> false, 'end' -> false}])),
		SAMPLING_SEQ_SUBLIST -> (new SublistTypeInfo(SAMPLING_SEQ_SUBLIST, #[
												new AttributeDefn("sample", true, ListDefinitionTable::SAMPLING_TYPE.makeReference),
												new AttributeDefn("start", true, MclTypeProvider::REAL_TYPE),
												new AttributeDefn("end", true, MclTypeProvider::REAL_TYPE)],
												#[#{'sample' -> true, 'start' -> true, 'end' -> false}])),
		PRIOR_FORMAT_SUBLIST -> (new SublistTypeInfo(PRIOR_FORMAT_SUBLIST, #[new AttributeDefn("element", true, MclTypeProvider::STRING_TYPE),
												new AttributeDefn("type", true, PRIOR_ELEMENT_TYPE_TYPE)],
												#[#{'element' -> true, 'type' -> true}])),
		COMPLEX_COMBINATION_SUBLIST -> (new SublistTypeInfo(COMPLEX_COMBINATION_SUBLIST,
												#[new AttributeDefn("sample", true, ListDefinitionTable::SAMPLING_TYPE.makeReference),
												new AttributeDefn("startTime", false, MclTypeProvider::REAL_TYPE)],
												#[#{'sample' -> true, 'startTime' -> false}]))
	}
	
	def static SublistTypeInfo getSublist(String name){
		return sublistDefns.get(name)
	}

}