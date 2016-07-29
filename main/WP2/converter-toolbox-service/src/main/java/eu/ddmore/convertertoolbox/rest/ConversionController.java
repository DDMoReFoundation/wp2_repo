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
package eu.ddmore.convertertoolbox.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.FileSystemResource;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import eu.ddmore.convertertoolbox.domain.ConversionCapability;
import eu.ddmore.convertertoolbox.domain.ConversionStatus;
import eu.ddmore.convertertoolbox.domain.LanguageVersion;
import eu.ddmore.convertertoolbox.domain.hal.ConversionResource;
import eu.ddmore.convertertoolbox.domain.hal.ConversionResources;
import eu.ddmore.convertertoolbox.domain.hal.LinkRelation;
import eu.ddmore.convertertoolbox.domain.internal.Conversion;
import eu.ddmore.convertertoolbox.domain.internal.ObjectMapper;
import eu.ddmore.convertertoolbox.rest.exceptions.ConversionInputsNotSpecified;
import eu.ddmore.convertertoolbox.rest.exceptions.UnsupportedConversion;
import eu.ddmore.convertertoolbox.rest.hal.ConversionResourceAssembler;
import eu.ddmore.convertertoolbox.service.ConversionCapabilitiesProvider;
import eu.ddmore.convertertoolbox.service.ConversionService;
import eu.ddmore.convertertoolbox.service.ExceededCapacity;
import eu.ddmore.convertertoolbox.service.impl.ConversionResourcesConvention;
import eu.ddmore.convertertoolbox.service.impl.ServiceWorkingDirectory;

@RestController
@RequestMapping(value="/conversion", produces={ "application/hal+json" })
@ExposesResourceFor(Conversion.class)
@EnableHypermediaSupport(type = { HypermediaType.HAL })
public class ConversionController {
    private static final Logger LOG = Logger.getLogger(ConversionController.class);
    private final ConversionCapabilitiesProvider capabilitiesProvider;
    private final ConversionService conversionService;
    private final ConversionResourceAssembler conversionResourceAssembler;
    
    private final ServiceWorkingDirectory serviceWorkingDirectory;
    
    @Autowired(required=true)
    public ConversionController(ConversionCapabilitiesProvider capabilitiesProvider, 
            ConversionService conversionService, ConversionResourceAssembler conversionResourceAssembler,
            ServiceWorkingDirectory serviceWorkingDirectory) {
        this.capabilitiesProvider = capabilitiesProvider;
        this.conversionService = conversionService;
        this.conversionResourceAssembler = conversionResourceAssembler;
        this.serviceWorkingDirectory = serviceWorkingDirectory;
    }
    
    @RequestMapping(method=RequestMethod.GET)
    @Description("Returns a list of conversions being handled by the service")
    public @ResponseBody HttpEntity<ConversionResources> list() {
        Collection<Conversion> conversions = conversionService.getConversions();
        Collection<ConversionResource> conversionResouces = new ArrayList<ConversionResource>();
        for ( Conversion conversion : conversions ) {
            conversionResouces.add(conversionResourceAssembler.toResource(conversion));
        }
        ConversionResources conversionResources = new ConversionResources(conversionResouces);
        conversionResources.add(linkTo(HomeController.class).withRel(LinkRelation.HOME.getRelation()));
        conversionResources.add(linkTo(methodOn(ConversionController.class).list()).withSelfRel());
        return new ResponseEntity<ConversionResources>(conversionResources,HttpStatus.OK);
    }

    @RequestMapping(method=RequestMethod.POST)
    @Description("Receives a new Conversion for processing")
    public @ResponseBody HttpEntity<ConversionResource> post(@RequestParam("conversion") @NotNull eu.ddmore.convertertoolbox.domain.Conversion inputConversion, 
                                                            @RequestParam("file") @NotNull MultipartFile file) throws UnsupportedConversion, ConversionInputsNotSpecified {
        LOG.debug(String.format("Received conversion %s",inputConversion.toString()));
        LOG.debug(String.format("Conversion input file size is %sB",file.getSize()));
        
        if(!isConversionSupported(inputConversion.getFrom(), inputConversion.getTo())) {
            throw new UnsupportedConversion(String.format("Requested conversion from %s to %s is not supported",inputConversion.getFrom(), inputConversion.getTo()));
        }
        if(file.isEmpty()) {
            throw new ConversionInputsNotSpecified(String.format("File was not uploaded"));
        }
        
        Conversion internalConversion = ObjectMapper.map(inputConversion);
        Conversion persistedConversion = null;
        try {
            persistedConversion = conversionService.add(internalConversion);
            Preconditions.checkNotNull(persistedConversion, "Conversion could not be accepted");
        } catch (ExceededCapacity e) {
            return new ResponseEntity<ConversionResource>(HttpStatus.TOO_MANY_REQUESTS);
        }
        persistedConversion.setWorkingDirectory(prepareConversionWorkingDirectory(persistedConversion));
        persistedConversion.setInputArchive(persistInputFile(persistedConversion, ConversionResourcesConvention.INPUTS_ARCHIVE_NAME, file));
        
        conversionService.schedule(persistedConversion);
        
        return new ResponseEntity<ConversionResource>(conversionResourceAssembler.toResource(persistedConversion),HttpStatus.CREATED);
    }

    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    @Description("Returns a Conversion identified by the given id")
    public @ResponseBody HttpEntity<ConversionResource> find(@PathVariable("id") String id) {
        Optional<Conversion> conversion = conversionService.getConversionForId(id);
        if(!conversion.isPresent()) {
            return new ResponseEntity<ConversionResource>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<ConversionResource>(conversionResourceAssembler.toResource(conversion.get()),HttpStatus.OK);
    }
    
    @RequestMapping(value="/{id}/result", method=RequestMethod.GET, produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
    @Description("Returns a Conversion result identified by the given id")
    public @ResponseBody HttpEntity<FileSystemResource> getOutputs(@PathVariable("id") String id) {
        Optional<Conversion> conversion = conversionService.getConversionForId(id);
        if(!conversion.isPresent()) {
            return new ResponseEntity<FileSystemResource>(HttpStatus.NOT_FOUND);
        }
        if(!ConversionStatus.Completed.equals(conversion.get().getStatus())) {
            return new ResponseEntity<FileSystemResource>(HttpStatus.CONFLICT);
        }
        if(conversion.get().getOutputArchive()==null) {
            return new ResponseEntity<FileSystemResource>(HttpStatus.NOT_FOUND);
        }
        if(!conversion.get().getOutputArchive().exists()) {
            return new ResponseEntity<FileSystemResource>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<FileSystemResource>(new FileSystemResource(conversion.get().getOutputArchive()),HttpStatus.OK);
    }

    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    @Description("Removes conversion and all its resources")
    public @ResponseBody HttpEntity<String> delete(@PathVariable("id") String id) {
        Optional<Conversion> conversion = conversionService.getConversionForId(id);
        if(!conversion.isPresent()) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        if(!ConversionStatus.Completed.equals(conversion.get().getStatus())) {
            return new ResponseEntity<String>(HttpStatus.CONFLICT);
        }
        
        conversionService.delete(conversion.get());
        
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }
    private File prepareConversionWorkingDirectory(Conversion conversion) {
        File workingDir = serviceWorkingDirectory.newDirectory(conversion.getId());
        if(!workingDir.exists()) {
            throw new IllegalStateException(String.format("Could not create working directory for request %s in %s",conversion.getId(),workingDir) );
        }
        return workingDir;
    }

	private File persistInputFile(Conversion conversion, String fileName,
			MultipartFile file) {
		
		File outputFile = new File(conversion.getWorkingDirectory(), fileName);
		InputStream inputStream = null;
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		try {
			inputStream = file.getInputStream();
			fos = new FileOutputStream(outputFile);
			bos = new BufferedOutputStream(fos);
			IOUtils.copy(inputStream, bos);
		} catch (Exception e) {
			throw new RuntimeException(String.format(
					"Could not upload input file for request %s to %s",
					conversion.getId(), conversion.getWorkingDirectory()), e);
		} finally {
			if (inputStream != null) {
				IOUtils.closeQuietly(inputStream);
			}
			if (bos != null) {
				IOUtils.closeQuietly(bos);
			} else if (fos != null) {
				IOUtils.closeQuietly(fos);
			}
		}
		return outputFile;
	}

    private boolean isConversionSupported(final LanguageVersion from, final LanguageVersion to) {
        Collection<ConversionCapability> matchingCapabilities =  Collections2.filter(this.capabilitiesProvider.getCapabilities(), new Predicate<ConversionCapability>() {
            public boolean apply(ConversionCapability candidate) {
                if(candidate.getSource().equals(from)) {
                    if(candidate.getTarget().contains(to)) {
                        return true;
                    }
                }
                return false;
            }
        });
        
        if(matchingCapabilities.size()==1) {
            return true;
        }
        if(matchingCapabilities.size()>1) {
            throw new IllegalStateException(String.format("Too many converters matching requested conversion from %s to %s", from, to));
        }
        return false;
    }
}
