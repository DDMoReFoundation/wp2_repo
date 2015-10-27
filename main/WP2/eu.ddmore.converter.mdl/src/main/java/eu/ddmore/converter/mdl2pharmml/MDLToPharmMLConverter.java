/*******************************************************************************
 * Copyright (C) 2013 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
package eu.ddmore.converter.mdl2pharmml;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import eu.ddmore.convertertoolbox.api.domain.LanguageVersion;
import eu.ddmore.convertertoolbox.api.domain.Version;
import eu.ddmore.convertertoolbox.api.response.ConversionDetail;
import eu.ddmore.convertertoolbox.api.response.ConversionDetail.Severity;
import eu.ddmore.convertertoolbox.api.response.ConversionReport;
import eu.ddmore.convertertoolbox.api.response.ConversionReport.ConversionCode;
import eu.ddmore.convertertoolbox.api.spi.ConverterProvider;
import eu.ddmore.convertertoolbox.domain.ConversionDetailImpl;
import eu.ddmore.convertertoolbox.domain.ConversionReportImpl;
import eu.ddmore.convertertoolbox.domain.LanguageVersionImpl;
import eu.ddmore.convertertoolbox.domain.VersionImpl;
import eu.ddmore.mdl.MdlStandaloneSetup;
import eu.ddmore.mdl.mdl.Mcl;
import eu.ddmore.mdl.mdl.MclObject;
import eu.ddmore.mdl.utils.MclUtils;

public class MDLToPharmMLConverter implements ConverterProvider {

    private final static Logger LOG = Logger.getLogger(MDLToPharmMLConverter.class);

    private LanguageVersion source;
    private LanguageVersion target;
    private Version converterVersion;

    public MDLToPharmMLConverter() {
        Version sourceVersion = new VersionImpl(7, 0, 0);
        source = new LanguageVersionImpl("MDL", sourceVersion);

        Version targetVersion = new VersionImpl(0, 6, 1);
        target = new LanguageVersionImpl("PharmML", targetVersion);

        // this should be the same as the development stream version as of this Maven module
        converterVersion = new VersionImpl(0, 3, 0);
    }

    @Override
    public ConversionReport performConvert(File src, File outputDirectory) throws IOException {
        // We know we're going to return a conversion report so create it up front; it is added to at various places in this method
        final ConversionReport report = new ConversionReportImpl();
        
        Injector injector = new MdlStandaloneSetup().createInjectorAndDoEMFRegistration();
        XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
        resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);

        Resource resource = resourceSet.getResource(URI.createURI("file:///" + src.getAbsolutePath()), true);
        
        EList<Diagnostic> errors = resource.getErrors();
        EList<Diagnostic> warnings = resource.getWarnings();
        if (!warnings.isEmpty()) {
            LOG.warn(String.format("%1$d warning(s) encountered in parsing MDL file %2$s", warnings.size(), src.getAbsolutePath()));
            for (Diagnostic w : warnings) {
                LOG.warn(w);
                final ConversionDetail detail = new ConversionDetailImpl();
                detail.setMessage(w.toString());
                detail.setSeverity(Severity.WARNING);
                report.addDetail(detail);
            }
        }
        if (!errors.isEmpty()) {
            LOG.error(String.format("%1$d error(s) encountered in parsing MDL file %2$s", errors.size(), src.getAbsolutePath()));
            for (Diagnostic e : errors) {
                LOG.error(e);
                final ConversionDetail detail = new ConversionDetailImpl();
                detail.setMessage(e.toString());
                detail.setSeverity(Severity.ERROR);
                report.addDetail(detail);
            }
            report.setReturnCode(ConversionCode.FAILURE);
            return report; // Bail out
        }
        MclUtils mclUtils = new MclUtils();
        Mcl mcl = (Mcl) resource.getContents().get(0);
        Iterable<MclObject> mogs = mclUtils.getMogObjects(mcl);

        // TODO: We're currently making an assumption that there will be a single MOG
        // in the provided file.  This should be fine for Product 4.
        // This will be addressed under DDMORE-1221
        Iterator<MclObject> mogsIt = mogs.iterator();
        if (!mogsIt.hasNext()) {
        	throw new IllegalStateException("Must be (at least) one MOG defined in the provided MCL file: " + src); 
        }
        final MclObject mog = mogsIt.next();

        final CharSequence converted = new Mdl2Pharmml().convertToPharmML(mog);
        
        final File outputFile = new File(outputDirectory.getAbsoluteFile(), FilenameUtils.getBaseName(src.getName()) + ".xml");
        
        try {
            FileUtils.writeStringToFile(outputFile, converted.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        report.setReturnCode(ConversionCode.SUCCESS);
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
    
}