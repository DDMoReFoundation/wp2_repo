package eu.ddmore.convertertoolbox.systemtest;

/**
 * File type information enum for file types used during conversion testing.
 * It contains filetype default extension and version associated with a file type.
 */
public enum FileType {
    MDL("mdl","8.0.0"),
    PharmML("xml","0.8.1"),
    PharmML_060("xml","0.6.0"),
    NMTRAN("ctl","7.3.0"),
    JSON("json","7.0.0");
    
    private String version;
    private String extension;
    
    FileType(String extension, String version){
        this.extension = extension;
        this.version = version;
    }

    public String getExtension(){
        return this.extension;
    }
    
    public String getVersion(){
        return this.version;
    }
    
}
