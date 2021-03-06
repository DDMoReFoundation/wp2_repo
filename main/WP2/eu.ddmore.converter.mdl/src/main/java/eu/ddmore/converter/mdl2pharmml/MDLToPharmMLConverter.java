/*******************************************************************************
 * Copyright (C) 2013 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
package eu.ddmore.converter.mdl2pharmml;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import eu.ddmore.converter.mdl2pharmml08.Mdl2Pharmml;
import eu.ddmore.convertertoolbox.api.domain.LanguageVersion;
import eu.ddmore.convertertoolbox.api.domain.Version;
import eu.ddmore.convertertoolbox.api.response.ConversionReport;
import eu.ddmore.convertertoolbox.api.response.ConversionReport.ConversionCode;
import eu.ddmore.convertertoolbox.api.spi.ConverterProvider;
import eu.ddmore.convertertoolbox.domain.ConversionReportImpl;
import eu.ddmore.convertertoolbox.domain.LanguageVersionImpl;
import eu.ddmore.convertertoolbox.domain.VersionImpl;
import eu.ddmore.mdl.MdlStandaloneSetup;
import eu.ddmore.mdl.mdl.Mcl;
import eu.ddmore.mdl.mdl.MclObject;
import eu.ddmore.mdl.provider.MogDefinitionProvider;
import eu.ddmore.mdl.scoping.MdlImportURIGlobalScopeProvider;

public class MDLToPharmMLConverter implements ConverterProvider {

    private LanguageVersion source;
    private LanguageVersion target;
    private Version converterVersion;
    private boolean ensureAbsoluteDataPath = false;

    public MDLToPharmMLConverter(){
    	
        Version sourceVersion = new VersionImpl(8, 0, 0);
        source = new LanguageVersionImpl("MDL", sourceVersion);

        Version targetVersion = new VersionImpl(0, 8, 1);
        target = new LanguageVersionImpl("PharmML", targetVersion);

        // this should be the same as the development stream version as of this Maven module
        converterVersion = new VersionImpl(0, 4, 0);
    }
    
    /**
     * Tells the converter to generate a path for any dependent files that is relative to the current file location.
     * Any generated file will then contain a valid path to the dependent file.  Typically such files are data files.   
     * @param yes  toggles this on of off.
     */
    public void setEnsureAbsoluteDataPath(boolean yes){
    	this.ensureAbsoluteDataPath = yes;
    }
    
    public boolean isEnsureAbsoluteDataPath(){
    	return this.ensureAbsoluteDataPath;
    }
    

    @Override
    public ConversionReport performConvert(File src, File outputDirectory) throws IOException {
        // We know we're going to return a conversion report so create it up front; it is added to at various places in this method
        final ConversionReport report = new ConversionReportImpl();
        
        new eu.ddmore.mdllib.MdlLibStandaloneSetup().createInjectorAndDoEMFRegistration();
        final Injector injector = new MdlStandaloneSetup().createInjectorAndDoEMFRegistration();
        final XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
        resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
        registerURIMappingsForImplicitImports(resourceSet);
        
        final MDLValidator validator = injector.getInstance(MDLValidator.class);

        final Resource resource = resourceSet.getResource(URI.createURI("file:///" + src.getAbsolutePath()), true);
        
        if (validator.validate(resource, report)) {
        
            MogDefinitionProvider utils = new MogDefinitionProvider();
            Mcl mcl = (Mcl) resource.getContents().get(0);
            MclObject mog = utils.getFirstMogObj(mcl);
    
            // TODO: We're currently making an assumption that there will be a single MOG
            // in the provided file.  This should be fine for Product 4.
            // This will be addressed under DDMORE-1221
            if (mog==null) {
            	throw new IllegalStateException("Must be (at least) one MOG defined in the provided MCL file: " + src); 
            }
            CharSequence converted = null;
            if(isEnsureAbsoluteDataPath())
            	converted = new Mdl2Pharmml().convertToPharmML(mog, src.getParent());
            else
            	converted = new Mdl2Pharmml().convertToPharmML(mog);
            	
            final File outputFile = new File(outputDirectory.getAbsoluteFile(), FilenameUtils.getBaseName(src.getName()) + ".xml");
            
            try {
                FileUtils.writeStringToFile(outputFile, converted.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
            report.setReturnCode(ConversionCode.SUCCESS);
        }
        
        return report;
    }

    public ConversionReport performConvertToFile(File src, File outputFile) throws IOException {
        // We know we're going to return a conversion report so create it up front; it is added to at various places in this method
        final ConversionReport report = new ConversionReportImpl();
        
        new eu.ddmore.mdllib.MdlLibStandaloneSetup().createInjectorAndDoEMFRegistration();
        final Injector injector = new MdlStandaloneSetup().createInjectorAndDoEMFRegistration();
        final XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
        resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
        registerURIMappingsForImplicitImports(resourceSet);
        
        final MDLValidator validator = injector.getInstance(MDLValidator.class);

        final Resource resource = resourceSet.getResource(URI.createURI("file:///" + src.getAbsolutePath()), true);
        
        if (validator.validate(resource, report)) {
        
            MogDefinitionProvider utils = new MogDefinitionProvider();
            Mcl mcl = (Mcl) resource.getContents().get(0);
            MclObject mog = utils.getFirstMogObj(mcl);
    
            // TODO: We're currently making an assumption that there will be a single MOG
            // in the provided file.  This should be fine for Product 4.
            // This will be addressed under DDMORE-1221
            if (mog==null) {
            	throw new IllegalStateException("Must be (at least) one MOG defined in the provided MCL file: " + src); 
            }
    
            CharSequence converted;
            if(isEnsureAbsoluteDataPath())
            	converted = new Mdl2Pharmml().convertToPharmML(mog, src.getParent());
            else
            	converted = new Mdl2Pharmml().convertToPharmML(mog);

            
            try {
                FileUtils.writeStringToFile(outputFile.getAbsoluteFile(), converted.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
            report.setReturnCode(ConversionCode.SUCCESS);
        }
        
        return report;
    }

    public ConversionReport[] performConvert(File[] src, File outputDirectory) throws IOException {
        ConversionReport[] reports = new ConversionReport[src.length];
        int i = 0;
        for (File f : src) {
            reports[i++] = performConvert(f, outputDirectory);
        }
        return reports;
    }

    @Override
    public LanguageVersion getSource() {
        return source;
    }

    @Override
    public LanguageVersion getTarget() {
        return target;
    }

    @Override
    public Version getConverterVersion() {
        return converterVersion;
    }

    @Override
    public String toString() {
        return String.format("MDLToPharmMLConverter [source=%s, target=%s, converterVersion=%s]", source, target, converterVersion);
    }
    
	private static void registerURIMappingsForImplicitImports(XtextResourceSet resourceSet) {
		URIConverter uriConverter = resourceSet.getURIConverter();
		Map<URI, URI> uriMap = uriConverter.getURIMap();
		registerPlatformToFileURIMapping(MdlImportURIGlobalScopeProvider.HEADER_URI, uriMap);
	}
 
	private static void registerPlatformToFileURIMapping(URI uri, Map<URI, URI> uriMap) {
		final URI newURI = createClasspathURIForHeaderFile(uri);
		uriMap.put(uri, newURI);
	}
	
	private static URI createClasspathURIForHeaderFile(URI uri) {
		String path = uri.path().replace("/plugin/", ""); // Eclipse RCP platform URL to a plugin resource starts with "/plugin/" so strip this off 
		path = path.substring(path.indexOf("/")); // This skips past the plugin name, i.e. eu.ddmore.mdl.definitions/
		// Now we're just left with the path to the resource within the plugin; the built plugin JAR is available on the classpath, so create a classpath URI pointing at this resource
		return URI.createURI("classpath:" + path);
	}
    
}
