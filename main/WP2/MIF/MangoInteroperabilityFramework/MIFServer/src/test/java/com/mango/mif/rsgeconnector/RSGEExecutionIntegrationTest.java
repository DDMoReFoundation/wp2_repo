package com.mango.mif.rsgeconnector;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.mango.mif.connector.TestsHelper;
import com.mango.mif.core.api.SharedLocationManager;
import com.mango.mif.core.impl.DefaultWorkspaceManager;
import com.mango.mif.core.resource.UserSharedLocationManager;
import com.mango.mif.domain.ExecutionRequestBuilder;
import com.mango.mif.domain.ExecutionResponse;
import com.mango.mif.utils.NewExecutionRequestHelper;
import com.mango.mif.utils.RequestSender;
import com.mango.mif.utils.TestProperties;

/**
 * Integration Test For R Execution.
 * Mocks the navigator Request.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/com/mango/mif/rsgeconnector/RSGEExecutionIntegrationTest-context.xml",
        "/spring/rsgeconnectorTest/Connector-context.xml",
        "/com/mango/mif/rsgeconnector/StateHandlers-context.xml", 
        "/spring/JmsTest-Config.xml" })
public class RSGEExecutionIntegrationTest implements MessageListener {
    /**
     * Working directory of connector.
     */
    final static File connectorWorkingDirectory = TestsHelper.createTmpDirectory();
    final static Logger LOG = Logger.getLogger(RSGEExecutionIntegrationTest.class);
    final String expectedMessage = "Sample message sent";

    @Resource(name = "rSGEJmsTemplate")
    JmsTemplate jmsTemplate;

    @Resource(name = "sharedLocationManager")
    private SharedLocationManager sharedLocationManager;
    
    private final static String REQUEST_QUEUE = "SGE.R.JOB.REQUEST";

    //To track all threads are done
    private static final int THREAD_COUNT = 5;

    static Map<String, String> requestMap = new HashMap<String, String>(THREAD_COUNT);

    /**
     * Contains consumed Execution Responses 
     */
    static Map<String, ExecutionResponse> resultsMap = new HashMap<String, ExecutionResponse>(THREAD_COUNT);
    /** test data dir names and request UUID map. */
    static Map<String, String> testDataDirAndRequest = Maps.newHashMapWithExpectedSize(THREAD_COUNT);

    /**
     * Used to synchronize test thread and requests handling
     */
    static CountDownLatch signal = new CountDownLatch(THREAD_COUNT);

	private static final String	CONNECTOR_ID	= "R_SGE";

    private String userName;

    /**
     * encrypted user password
     */
    private String userPassword;
    
    private File tmpSharedDirectory;

    @Before
    public void setUp() {
        
    	userName = System.getProperty(TestProperties.MIF_CLIENT_USER_NAME);
    	userPassword = System.getProperty(TestProperties.MIF_CLIENT_PASSWORD);
    	Preconditions.checkNotNull(userName, "The "+TestProperties.MIF_CLIENT_USER_NAME+" property is not set");
    	Preconditions.checkNotNull(userPassword, "The "+TestProperties.MIF_CLIENT_PASSWORD+" property is not set");
    	tmpSharedDirectory = TestsHelper.createTmpDirectory();
       	((UserSharedLocationManager)sharedLocationManager).setWorkspaceManager(new DefaultWorkspaceManager(tmpSharedDirectory));
          	
    }
    
    @After
    public void tearDown() throws IOException {
        requestMap.clear();
        resultsMap.clear();
        testDataDirAndRequest.clear();
        
    }

    @DirtiesContext
    @Test(timeout = 500000)
    public void shouldSendSimpleRExecutionRequestsAndWaitForResponse() throws InterruptedException, JAXBException {
        LOG.info("Running test: " + this.getClass() + ".testSendingMessage tmp dir = " + tmpSharedDirectory);
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            ExecutionRequestBuilder requestBuilder = NewExecutionRequestHelper.createBasicRExecutionRequestBuilder(sharedLocationManager);
            requestBuilder.setConnectorID(CONNECTOR_ID);
            requestBuilder.setUserName(userName);
            requestBuilder.setUserPassword(userPassword);
            String requestMsg = requestBuilder.getExecutionRequestMsg();
            requestMap.put(requestBuilder.getExecutionRequest().getRequestId().toString(), requestMsg);
            new Thread(new RequestSender(REQUEST_QUEUE, requestMsg, jmsTemplate)).start();
        }

        signal.await();

    }
    /**
     * Subscriber to consume the messages from the MIF.
     */

    @Override
    public void onMessage(Message message) {
        LOG.info("The Response is got by navigator");
        TextMessage msg = (TextMessage) message;
        String receivedText = null;
        try {
            receivedText = msg.getText();
        } catch (JMSException e) {
            LOG.error("JMSException" + e);
        }

        if (checkIfTheReceivedTextContainsPartOfRequest(receivedText)) {
            LOG.info("Closing the request " + receivedText);
            signal.countDown();
        }
    }

    /**
     * Checks if the response text contains the uinque ID which are submitted as part of requests. 
     * @param receivedText
     * @return
     */
    private boolean checkIfTheReceivedTextContainsPartOfRequest(String receivedText) {
        Preconditions.checkNotNull(receivedText, "Received text message should not be null");
        JAXBContext context;
        ExecutionResponse response = null;
        try {
            context = JAXBContext.newInstance(ExecutionResponse.class);
            Unmarshaller um = context.createUnmarshaller();
            response = (ExecutionResponse) um.unmarshal(new StringReader(receivedText));
        } catch (JAXBException e) {
            LOG.error("JAXB Exception " + e);
        }
        String uuid = response.getRequestId().toString();
        for (String key : requestMap.keySet()) {
            if (uuid.equals(key)) {
                resultsMap.put(uuid, response);
                return true;
            }
        }
        LOG.info("Reuqest with id: " + uuid + " not found.");
        return false;
    }

}
