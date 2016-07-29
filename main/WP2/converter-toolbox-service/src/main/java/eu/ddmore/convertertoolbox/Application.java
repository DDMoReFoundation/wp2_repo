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
package eu.ddmore.convertertoolbox;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.hal.CurieProvider;
import org.springframework.hateoas.hal.DefaultCurieProvider;
import org.springframework.scheduling.annotation.EnableScheduling;

import eu.ddmore.archive.ArchiveFactory;
import eu.ddmore.convertertoolbox.api.conversion.ConverterManager;
import eu.ddmore.convertertoolbox.conversion.ConverterManagerImpl;
import eu.ddmore.convertertoolbox.service.impl.ConversionServiceConfiguration;
/* 
 * we make sure that the component scan does not discover Spring Configuration classes, if we didn't do this, 
 * it would pick up Unwanted Configuration classes during tests.
 * 
 * This class should explicitly import @Configuration classes!
 */
@ComponentScan(excludeFilters = @Filter(type = FilterType.ANNOTATION, value={org.springframework.context.annotation.Configuration.class}))
@EnableAutoConfiguration
@Configuration
@Import(ConversionServiceConfiguration.class)
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Instantiates converter manager
     */
    @Bean
    public ConverterManager converterManager() {
        ConverterManagerImpl converterManager =  new ConverterManagerImpl();
        converterManager.discoverConverters();
        return converterManager;
    }
    
    /**
     * Instantiates a Curie provider that is used by HATEOAS to generate link relations names
     * @param prefix A prefix used by the curie.
     * @param template A template of the URL where link relations documentation.
     */
    @Bean
    public CurieProvider curieProvider(@Value("${cts.linkRelation.prefix}") String prefix, @Value("${cts.linkRelation.template}") String template) {
        return new DefaultCurieProvider(prefix,
            new UriTemplate(template));
    }

    /**
     * Instantiates Spring MVC configuration element for HTTP file upload support. 
     * @param maxFileSize Maximum file size.
     * @param maxRequestSize Maximum request size.
     */
    @Bean
    public MultipartConfigElement multipartConfigElement(@Value("${cts.http.maxFileSize:8MB}") String maxFileSize, @Value("${cts.http.maxRequestSize:8MB}") String maxRequestSize) {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(maxFileSize);
        factory.setMaxRequestSize(maxRequestSize);
        return factory.createMultipartConfig();
    }
    
    /**
     * Creates PHEX Archive Factory Bean
     */
    @Bean
    public ArchiveFactory archiveFactory() {
        return new ArchiveFactory();
    }
}
