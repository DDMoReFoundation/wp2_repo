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
package eu.ddmore.convertertoolbox.rest.hal;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import eu.ddmore.convertertoolbox.domain.ConversionStatus;
import eu.ddmore.convertertoolbox.domain.hal.ConversionResource;
import eu.ddmore.convertertoolbox.domain.hal.LinkRelation;
import eu.ddmore.convertertoolbox.domain.internal.ObjectMapper;
import eu.ddmore.convertertoolbox.rest.ConversionController;

/**
 * Component responsible for facilitating wrapping of the { @link Conversion } into { @link ConversionResource }
 * 
 * This is in fact a helper class that Controllers should use to create HAL Representations of Resources.
 * 
 * Refer to Spring HATEOAS for details.
 */
@Component
public class ConversionResourceAssembler extends ResourceAssemblerSupport<eu.ddmore.convertertoolbox.domain.internal.Conversion, ConversionResource> {
    @Autowired
    private EntityLinks entityLinks;
    
    public ConversionResourceAssembler() {
        super(ConversionController.class, ConversionResource.class);
    }

    @Override
    public ConversionResource toResource(eu.ddmore.convertertoolbox.domain.internal.Conversion conversion) {
        ConversionResource resource = new ConversionResource(ObjectMapper.map(conversion));
        resource.add(entityLinks.linkToSingleResource(conversion));
        if(ConversionStatus.Completed.equals(conversion.getStatus())) {
            addLinksForCompletedConversion(conversion, resource);
        }
        return resource;
    }

    private void addLinksForCompletedConversion(eu.ddmore.convertertoolbox.domain.internal.Conversion conversion, ConversionResource conversionResource) {
        if(conversion.getOutputArchive()!=null) {
            if(conversion.getOutputArchive().exists()) {
                conversionResource.add(linkTo(methodOn(ConversionController.class).getOutputs(conversionResource.getContent().getId())).withRel(LinkRelation.RESULT.getRelation()));
            }
        }
        conversionResource.add(linkTo(methodOn(ConversionController.class).delete(conversionResource.getContent().getId())).withRel(LinkRelation.DELETE.getRelation()));
    }
    
    public void setEntityLinks(EntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }
}
