/*******************************************************************************

/**
 * Represents {@link eu.ddmore.mdl.mdl.MclObject} for MDL <-> JSON conversion.
 */
public class MclObject extends Expando implements MDLPrintable {
    
    public static String PROPERTY_NAME = "name"
    public static String PROPERTY_TYPE = "type"
    public static String PROPERTY_BLOCKS = "blocks"

    public MclObject(final eu.ddmore.mdl.mdl.MclObject mclObj) {
        
        setProperty(PROPERTY_NAME, mclObj.getName())
        setProperty(PROPERTY_TYPE, mclObj.getObjId().getName())
        setProperty(PROPERTY_BLOCKS, TopLevelBlockStatements.fromMDL(mclObj.getBlocks()))
        
    }
    
    public MclObject(final Map json) {
        
        setProperty(PROPERTY_NAME, json[PROPERTY_NAME])
        setProperty(PROPERTY_TYPE, json[PROPERTY_TYPE])
        setProperty(PROPERTY_BLOCKS, TopLevelBlockStatements.fromJSON(json[PROPERTY_BLOCKS]))
        
    }
}