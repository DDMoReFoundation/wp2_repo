/*******************************************************************************
 * Copyright (C) 2015 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
package eu.ddmore.convertertoolbox;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Parent class for Acceptance Tests
 */
public class ConversionToolboxServiceAcceptanceTestParent {
    private static final Logger LOG = Logger.getLogger(ConversionToolboxServiceAcceptanceTestParent.class);
    private static final Properties initialProperties = new Properties();
    private static final String TRUST_STORE_PROPERTY = "javax.net.ssl.trustStore";
    /**
     * System property specifying the Converter Toolbox Service URL
     */
    public static final String CTS_URL = "cts.url";
    
    protected ConverterToolboxServiceHttpRestClient restClient;
    private String url = "http://localhost:9020";

    public ConversionToolboxServiceAcceptanceTestParent() {
        super();
    }
    
    @BeforeClass
    public static void setUpClass() throws IOException  {
        initialProperties.putAll(System.getProperties());
        Properties props = new Properties();
        props.load(ConversionToolboxServiceAcceptanceTestParent.class.getResource("/acceptance-tests.properties").openStream());
        
        for(Entry<Object, Object> en : props.entrySet()) {
            String prop = (String) en.getKey();
            String value = (String)en.getValue();
            if(System.getProperty(prop)==null) {
                if(TRUST_STORE_PROPERTY.equals(en.getKey())) {
                    // The path must be absolute 
                    value = Paths.get(value).normalize().toAbsolutePath().toString();
                }
                System.setProperty(prop, value);
            }
        }
        for(Entry<Object, Object> en: System.getProperties().entrySet()) {
            LOG.info(String.format("Property %s=%s", en.getKey(), en.getValue()));
        }
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        Properties p = new Properties();
        p.putAll(initialProperties);
        System.setProperties(p);
    }
    
    @Before
    public void setUp() throws Exception {
        if(System.getProperty(CTS_URL)!=null) {
            url = System.getProperty(CTS_URL);
        }
        
        restClient = new ConverterToolboxServiceHttpRestClient(url);
        restClient.init();
    }

    @After
    public void tearDown() {
        restClient.close();
    }

}