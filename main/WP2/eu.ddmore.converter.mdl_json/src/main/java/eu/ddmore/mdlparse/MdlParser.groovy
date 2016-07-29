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
package eu.ddmore.mdlparse

import java.util.Map;

import org.apache.log4j.Logger
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.URIConverter
import org.eclipse.emf.ecore.resource.Resource.Diagnostic
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.resource.XtextResourceSet

import com.google.common.base.Preconditions
import com.google.inject.Injector

import eu.ddmore.mdl.MdlStandaloneSetup
import eu.ddmore.mdl.mdl.Mcl
import eu.ddmore.mdl.scoping.MdlImportURIGlobalScopeProvider;

import eu.ddmore.convertertoolbox.api.response.ConversionDetail
import eu.ddmore.convertertoolbox.api.response.ConversionReport
import eu.ddmore.convertertoolbox.api.response.ConversionDetail.Severity
import eu.ddmore.convertertoolbox.api.response.ConversionReport.ConversionCode
import eu.ddmore.convertertoolbox.domain.ConversionDetailImpl


class MdlParser {
    private static final Logger LOG = Logger.getLogger(MdlParser.class)

    static Injector injector;
    static XtextResourceSet resourceSet;

    static {
		new eu.ddmore.mdllib.MdlLibStandaloneSetup().createInjectorAndDoEMFRegistration();
        injector = new MdlStandaloneSetup().createInjectorAndDoEMFRegistration();
        resourceSet = injector.getInstance(XtextResourceSet.class);
        resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		
		registerURIMappingsForImplicitImports(resourceSet);
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
	
    /**
     * Parse the MDL file into an object graph of Xtext domain objects rooted at an {@link Mcl} object.
     * Any errors encountered in parsing the MDL will be populated in the provided {@link ConversionReport}
     * and the report's status will be set as FAILED.
     * <p>
     * @param mdlFile - the {@link File} object referencing the MDL file to parse
     * @param report - a newly created {@link ConversionReport}; this will be populated in the event of errors in parsing the MDL
     * @return the {@link Mcl} object graph representing the parsed MDL file
     */
    public Mcl parse(final File mdlFile, final ConversionReport report) {
        Preconditions.checkNotNull(mdlFile, "No MDL File provided to MdlParser.parse()")
        Preconditions.checkNotNull(report, "A ConversionReport must be provided to MdlParser.parse()")
		
        final Resource resource = resourceSet.getResource(URI.createURI("file:///" + mdlFile.getAbsolutePath()), true)

        EList<Diagnostic> errors = resource.getErrors()
        EList<Diagnostic> warnings = resource.getWarnings()
        if (warnings) {
            LOG.warn(String.format("%1\$d warning(s) encountered in parsing MDL file %2\$s", warnings.size(), mdlFile.getAbsolutePath()));
            for (Diagnostic w : warnings) {
                LOG.warn(w);
                final ConversionDetail detail = new ConversionDetailImpl();
                detail.setMessage(w.toString());
                detail.setSeverity(Severity.WARNING);
                report.addDetail(detail);
            }
        }
        if (errors) {
            LOG.error(String.format("%1\$d error(s) encountered in parsing MDL file %2\$s", errors.size(), mdlFile.getAbsolutePath()));
            for (Diagnostic e : errors) {
                LOG.error(e);
                final ConversionDetail detail = new ConversionDetailImpl();
                detail.setMessage(e.toString());
                detail.setSeverity(Severity.ERROR);
                report.addDetail(detail);
            }
            report.setReturnCode(ConversionCode.FAILURE);
            return null; // Bail out
        }

        return (Mcl) resource.getContents().get(0);
    }

}
