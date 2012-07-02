package com.mango.mif.rsgeconnector;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Preconditions;
import com.mango.mif.connector.TestsHelper;
import com.mango.mif.core.api.SharedLocationManager;
import com.mango.mif.core.impl.DefaultWorkspaceManager;
import com.mango.mif.core.resource.UserSharedLocationManager;
import com.mango.mif.domain.ExecutionRequest;
import com.mango.mif.domain.ExecutionRequestBuilder;
import com.mango.mif.domain.ExecutionResponse;
import com.mango.mif.utils.JAXBUtils;
import com.mango.mif.utils.NewExecutionRequestHelper;
import com.mango.mif.utils.RequestSender;
import com.mango.mif.utils.TestProperties;
import com.mango.mif.utils.jaxb.ExecutionJaxbUtils;

/**
 * Integration Test That schedules a long running R job, waits for it to start and then sends a cancellation request
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/com/mango/mif/rsgeconnector/SGERExecutionCancellationIT-context.xml",
        "/spring/rsgeconnectorTest/Connector-context.xml",
        "/com/mango/mif/rsgeconnector/StateHandlers-context.xml", 
        "/spring/JmsTest-Config.xml" })
public class SGERExecutionCancellationIT implements MessageListener {
    /**
     * Working directory of connector.
     */
    final static File connectorWorkingDirectory = TestsHelper.createTmpDirectory();
    /**
     * LOG
     */
    final static Logger LOG = Logger.getLogger(SGERExecutionCancellationIT.class);
    
    @Resource(name = "rSGEJmsTemplate")
    JmsTemplate jmsTemplate;

    @Resource(name = "sharedLocationManager")
    private SharedLocationManager sharedLocationManager;

    /**
     * Used to synchronize test thread and requests handling
     */
    static CountDownLatch signal = new CountDownLatch(1);
    /**
     * R connector used
     */
	private final static String	CONNECTOR_ID	= "R_SGE";
    private final static String REQUEST_QUEUE = "SGE.R.JOB.REQUEST";
    private final static String CANCEL_QUEUE = "SGE.R.JOB.CANCEL";
	/**
	 * User name
	 */
    private String userName;
    /**
     * encrypted user password
     */
    private String userPassword;
    /**
     * Temporary shared directory
     */
    private File tmpSharedDirectory;

    /**
     * Execution request
     */
    static ExecutionRequest	executionRequest;
    
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
    public void tearDown() {
    	try {
			FileUtils.deleteDirectory(tmpSharedDirectory);
		} catch (IOException e) {
			LOG.error("Could not remove temporary shared directory", e);
		}
    }
    
    @DirtiesContext
    @Test(timeout = 100000)
    public void shouldStartALongRunningRJobRequestItsCancellationAndWaitForCancellationAck() throws InterruptedException, JAXBException, IOException {
        signal = new CountDownLatch(1);
        File dataSetDir = FileUtils.toFile(SGERExecutionCancellationIT.class.getResource("/testData/longRunningRScript/sleepFor60Seconds.R")).getParentFile();
		ExecutionRequestBuilder requestBuilder = NewExecutionRequestHelper.createRExecutionRequestBuilderForInputDirectory(
			sharedLocationManager, dataSetDir);
		requestBuilder.setConnectorID(CONNECTOR_ID);
		requestBuilder.setUserName(userName);
		requestBuilder.setUserPassword(userPassword);
		executionRequest = requestBuilder.getExecutionRequest();
		String requestMsg = requestBuilder.getExecutionRequestMsg();
		new Thread(new RequestSender(REQUEST_QUEUE, requestMsg, jmsTemplate)).start();

		Thread.sleep(10000);
		
		jmsTemplate.send(CANCEL_QUEUE, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage message = session.createTextMessage();
				message.setText(executionRequest.getRequestId().toString());
				return message;
			}
		});
		
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
        ExecutionResponse response = null;
		try {
			response = JAXBUtils.unmarshall(receivedText, ExecutionJaxbUtils.EXECUTION_RESPONSE_CLASS_ARRAY);
		} catch (JAXBException e) {
			LOG.error("Could not parse execution response message: " + receivedText, e);
			return false;
		}
        
        String uuid = response.getRequestId().toString();
        LOG.info("Received response for request: " + uuid + ".");
        return uuid.equals(executionRequest.getRequestId().toString());
    }

}
