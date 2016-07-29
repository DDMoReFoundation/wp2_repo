/*******************************************************************************
 * Copyright (C) 2016 Mango Business Solutions Ltd, http://www.mango-solutions.com
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/agpl-3.0.html>.
 *******************************************************************************/
package eu.ddmore.converter.mdl2json.domain

import eu.ddmore.converter.mdl2json.utils.KeyValuePairConverter
import eu.ddmore.mdl.mdl.NamedFuncArguments
import eu.ddmore.mdl.mdl.ValuePair
import eu.ddmore.mdl.utils.MdlExpressionConverter;

/**
 * Represents {@link eu.ddmore.mdl.mdl.RandomVariableDefinition}, a subclass of {@link eu.ddmore.mdl.mdl.Statement},
 * for MDL <-> JSON conversion.
 * <p>
 * Example MDL:
 * <pre>
 * ETA_CL ~ Normal(mean = 0, sd = PPV_CL)
 * </pre>
 * item within <code>RANDOM_VARIABLE_DEFINITION</code> block within <code>mdlObj</code> section.
 * <p>
 * Equivalent JSON:
 * <pre>
 * {".subtype":"RandVarDefn","blkAttrs":{"level":"ID"},"args":{"mean":"0","sd":"PPV_CL"},"name":"ETA_CL","distType":"Normal"}
 * </pre>
 * Note the <code>blkAttrs</code> attributes which aren't relevant to the item/variable itself
 * but have been propagated from the parent {@link BlockStatement} - see
 * {@link AbstractStatement#setAttributesFromBlock(Map)} and {@link AbstractStatement#getAttributesFromBlock()}.
 */
public class RandomVariableDefinition extends AbstractStatement {
    
    public final static String PROPERTY_NAME = "name"
    public final static String PROPERTY_DIST_TYPE = "distType"
    public final static String PROPERTY_ARGS = "args"
	// Initially for Product 5, store as string (e.g. "Normal(mean=0, sd=PPV_V)") with this key; TODO: split into DIST_TYPE and DISTN components 
	public final static String PROPERTY_DISTN = "distn"

    /**
     * Constructor creating from MDL grammar objects.
     * <p>
     * @param randomVarDefn - {@link eu.ddmore.mdl.mdl.RandomVariableDefinition} object from the MDL grammar
     */
    public RandomVariableDefinition(final eu.ddmore.mdl.mdl.RandomVariableDefinition randomVarDefn) {
        setProperty(PROPERTY_SUBTYPE, EStatementSubtype.RandomVarDefinition.getIdentifierString())
        setProperty(PROPERTY_NAME, randomVarDefn.getName())
		// TODO: Try and split this into 'distType' and 'args' components 
		setProperty(PROPERTY_DISTN, MdlExpressionConverter.convertToString(randomVarDefn.getDistn()))
// TODO: Commented out as BuiltinFunctionCall no longer exists - rework this
//        final eu.ddmore.mdl.mdl.BuiltinFunctionCall distn = randomVarDefn.getDistn();
//        setProperty(PROPERTY_DIST_TYPE, distn.getFunc())
//        setProperty(PROPERTY_ARGS, KeyValuePairConverter.toMap(distn.getArgList()))
    }
    
    /**
     * Constructor creating from JSON.
     * <p>
     * @param json - {@link Map} of content
     */
    public RandomVariableDefinition(final Map json) {
        super(json)
    }
    
    @Override
    public String toMDL() {
        final StringBuffer sb = new StringBuffer()
        sb.append(IDT)
        sb.append(IDT)
        sb.append(getProperty(PROPERTY_NAME))
        sb.append(" ~ ")
		sb.append(getProperty(PROPERTY_DISTN))
		// TODO: Reinstate this once split into 'distType' and 'args' components
//        sb.append(getProperty(PROPERTY_DIST_TYPE))
//        sb.append("(")
//        sb.append(KeyValuePairConverter.toMDL(getProperty(PROPERTY_ARGS)))
//        sb.append(")")
		
//        if (getProperty(PROPERTY_LIST)) {
//            sb.append(" : {")
//            sb.append(getProperty(PROPERTY_LIST).collect { // List of Maps, each Map containing one Entry
//                Map m -> m.collect {
//                    Map.Entry ent -> KeyValuePairMdlPrinter.toMDL(ent)
//                }.join("")
//            }.join(", "))
//            sb.append("}")
//        }
        sb.toString()
    }
        
}
