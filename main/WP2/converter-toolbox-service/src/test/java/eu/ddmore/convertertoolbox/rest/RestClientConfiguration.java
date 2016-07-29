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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.core.AnnotationRelProvider;
import org.springframework.hateoas.core.DefaultRelProvider;
import org.springframework.hateoas.core.DelegatingRelProvider;
import org.springframework.hateoas.hal.CurieProvider;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.plugin.core.OrderAwarePluginRegistry;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configuration which prepares REST client used by Controller integration tests
 */
@Configuration
public class RestClientConfiguration {
    @Autowired
    private CurieProvider curieProvider;
    
    @Bean
    public RestTemplate restTemplate() {
        List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
        converters.add(new FormHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(halConverter());
        converters.add(new StringHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(converters);
        return restTemplate;
    }
 
    /**
     * 
     * @return converter capable of handling HAL-enabled domain objects
     */
    @Bean
    public MappingJackson2HttpMessageConverter halConverter() {
        RelProvider defaultRelProvider = defaultRelProvider();
        RelProvider annotationRelProvider = annotationRelProvider();
 
        OrderAwarePluginRegistry<RelProvider, Class<?>> relProviderPluginRegistry = OrderAwarePluginRegistry
                .create(Arrays.asList(defaultRelProvider, annotationRelProvider));
 
        DelegatingRelProvider delegatingRelProvider = new DelegatingRelProvider(relProviderPluginRegistry);
 
        ObjectMapper halObjectMapper = new ObjectMapper();
        halObjectMapper.registerModule(new Jackson2HalModule());
        halObjectMapper
                .setHandlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(delegatingRelProvider, null));
 
        MappingJackson2HttpMessageConverter halConverter = new MappingJackson2HttpMessageConverter();
        halConverter.setSupportedMediaTypes(Arrays.asList(MediaTypes.HAL_JSON));
        halConverter.setObjectMapper(halObjectMapper);
        return halConverter;
    }
 
    @Bean
    public DefaultRelProvider defaultRelProvider() {
        return new DefaultRelProvider();
    }
 
    @Bean
    public AnnotationRelProvider annotationRelProvider() {
        return new AnnotationRelProvider();
    }
}
