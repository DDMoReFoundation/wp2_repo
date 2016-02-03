package eu.ddmore.mdl.utils

import eu.ddmore.mdl.mdllib.mdllib.TypeClass
import eu.ddmore.mdl.mdllib.mdllib.TypeDefinition
import eu.ddmore.mdl.mdllib.mdllib.TypeSpec
import eu.ddmore.mdl.provider.ListDefinitionTable
import eu.ddmore.mdl.provider.SublistDefinitionTable
import eu.ddmore.mdl.type.BuiltinEnumTypeInfo
import eu.ddmore.mdl.type.TypeInfo
import eu.ddmore.mdl.type.TypeSystemProvider
import java.util.HashSet
import java.util.Map

class MdlLibUtils {

	val Map<String, TypeInfo> listDefns = #{
		ListDefinitionTable::PRIOR_SOURCE_TYPE.name -> ListDefinitionTable::PRIOR_SOURCE_TYPE
	}
	
	def private getScalarType(TypeSpec it){
		val typeName = typeName.name
		switch(typeName){
			case 'Int':
				TypeSystemProvider::INT_TYPE
			case 'Real':
				TypeSystemProvider::REAL_TYPE
			case 'Boolean':
				TypeSystemProvider::BOOLEAN_TYPE
			case 'String':
				TypeSystemProvider::STRING_TYPE
			case 'Pdf':
				TypeSystemProvider::PDF_TYPE
			case 'Pmf':
				TypeSystemProvider::PMF_TYPE
			default:
				TypeSystemProvider::UNDEFINED_TYPE
		}
	}

	def TypeInfo getTypeInfo(TypeSpec it){
		val typeClass = typeName.typeClass
		switch(typeClass){
			case TypeClass.SCALAR:
				scalarType
			case TypeClass.VECTOR:
				if(elementType != null && cellType == null && functionSpec == null){
					// element type specified and well formed
					val elType = elementType.typeInfo
					if(elType == TypeSystemProvider::UNDEFINED_TYPE) TypeSystemProvider::UNDEFINED_TYPE
					else elType.makeVector
				}
				else if(elementType == null && cellType == null && functionSpec == null){
					// no element spec and well formed so default to Real 
					TypeSystemProvider::REAL_VECTOR_TYPE
				}
				else TypeSystemProvider::UNDEFINED_TYPE
			case TypeClass.MATRIX:
				if(cellType != null && elementType == null && functionSpec == null){
					// element type specified and well formed
					val elType = cellType.typeInfo
					if(elType == TypeSystemProvider::UNDEFINED_TYPE) TypeSystemProvider::UNDEFINED_TYPE
					else elType.makeMatrix
				}
				else if(cellType == null && elementType == null && functionSpec == null){
					// no element spec and well formed so default to Real 
					TypeSystemProvider::REAL_MATRIX_TYPE
				}
				else TypeSystemProvider::UNDEFINED_TYPE
			case TypeClass.REFERENCE:
				if(elementType != null && cellType == null && functionSpec == null){
					// element type specified and well formed
					val elType = elementType.typeInfo
					if(elType == TypeSystemProvider::UNDEFINED_TYPE) TypeSystemProvider::UNDEFINED_TYPE
					else elType.makeReference
				}
				else if(elementType == null && cellType == null && functionSpec == null){
					// no element spec and well formed so default to Real 
					TypeSystemProvider::REAL_TYPE.makeReference
				}
				else TypeSystemProvider::UNDEFINED_TYPE
			case TypeClass.ENUM:
				createBuiltinEnum(typeName)
			case TypeClass.SUBLIST:
				SublistDefinitionTable::instance.getSublist(typeName.name) ?: TypeSystemProvider::UNDEFINED_TYPE
			case TypeClass.CATEGORY:
				TypeSystemProvider::GENERIC_ENUM_VALUE_TYPE 
			case TypeClass.LIST:
				listDefns.get(typeName.name) ?: TypeSystemProvider::UNDEFINED_TYPE
			default:
				TypeSystemProvider::UNDEFINED_TYPE
		} 
	}
	
	def private createBuiltinEnum(TypeDefinition it){
		val enumVals = new HashSet<String>
		enumArgs.forEach[enumVals.add(it)]
		
		new BuiltinEnumTypeInfo(name, enumVals)
	}
	
	
//	def TypeInfo getTypeInfo(TypeSpec it){
//		val typeName = typeName.name
//		switch(typeName){
//			case 'Int':
//				TypeSystemProvider::INT_TYPE
//			case 'Real':
//				TypeSystemProvider::REAL_TYPE
//			case 'Boolean':
//				TypeSystemProvider::BOOLEAN_TYPE
//			case 'String':
//				TypeSystemProvider::STRING_TYPE
//			case 'Vector':
//				if(elementType != null && functionSpec == null){
//					// element type specified and well formed
//					val elType = elementType.typeInfo
//					if(elType == TypeSystemProvider::UNDEFINED_TYPE) TypeSystemProvider::UNDEFINED_TYPE
//					else elType.makeVector
//				}
//				else if(elementType == null && functionSpec == null){
//					// no element spec and well formed so default to Real 
//					TypeSystemProvider::REAL_VECTOR_TYPE
//				}
//				else TypeSystemProvider::UNDEFINED_TYPE
//			case 'Matrix':
//				if(elementType != null && functionSpec == null){
//					// element type specified and well formed
//					val elType = elementType.typeInfo
//					if(elType == TypeSystemProvider::UNDEFINED_TYPE) TypeSystemProvider::UNDEFINED_TYPE
//					else elType.makeMatrix
//				}
//				else if(elementType == null && functionSpec == null){
//					// no element spec and well formed so default to Real 
//					TypeSystemProvider::REAL_MATRIX_TYPE
//				}
//				else TypeSystemProvider::UNDEFINED_TYPE
//			case 'Reference':
//				if(elementType != null && functionSpec == null){
//					// element type specified and well formed
//					val elType = elementType.typeInfo
//					if(elType == TypeSystemProvider::UNDEFINED_TYPE) TypeSystemProvider::UNDEFINED_TYPE
//					else elType.makeReference
//				}
//				else if(elementType == null && functionSpec == null){
//					// no element spec and well formed so default to Real 
//					TypeSystemProvider::REAL_TYPE.makeReference
//				}
//				else TypeSystemProvider::UNDEFINED_TYPE
//			default:
//				TypeSystemProvider::UNDEFINED_TYPE
//		} 
//	}
	
	
}