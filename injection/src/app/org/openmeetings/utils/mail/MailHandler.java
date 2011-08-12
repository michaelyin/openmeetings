package org.openmeetings.utils.mail;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author swagner
 * 
 */
public class MailHandler {

	private static final Logger log = Red5LoggerFactory.getLogger(MailHandler.class, ScopeApplicationAdapter.webAppRootKey);
	@Autowired //FIXME
	static private Configurationmanagement cfgManagement;
	
	public MailHandler() {
	}

	/**
	 * send mail to address
	 * 
	 * @param toEmail
	 * @param subj
	 * @param message
	 * @return
	 */
	public static String sendMail(String toEmail, String subj, String message) {
		try {

			// String smtpServer="smtp.xmlcrm.org";
			String smtpServer = cfgManagement.getConfKey(3, "smtp_server").getConf_value();
			String smtpPort = cfgManagement.getConfKey(3, "smtp_port").getConf_value();
			String to = toEmail;
			// String from = "openmeetings@xmlcrm.org";
			String from = cfgManagement.getConfKey(3,"system_email_addr").getConf_value();
			String subject = subj;
			String body = message;
			
			String emailUsername = cfgManagement.getConfKey(3, "email_username").getConf_value();
			String emailUserpass = cfgManagement.getConfKey(3, "email_userpass").getConf_value();

			//return send(smtpServer, smtpPort, to, from, subject, body);
		
			MailThread mailThread = new MailThread(smtpServer, smtpPort, to, from, subject, body, emailUsername, emailUserpass);
			mailThread.start();
			
			return "success";
			
		} catch (Exception ex) {
			log.error("[sendMail] " ,ex);
			return "Error: " + ex;
		}
	}


	
	
	
	/**
	 * @author o.becherer
	 * @param recipients (List of valid mail adresses)
	 * @param subject mailSubject
	 * @param iCalMimeBody byte[] containing Icaldata
	 * Sending Ical Invitation 
	 */
	//---------------------------------------------------------------------------------------------
	public static void sendIcalMessage(String recipients, String subject, byte[] iCalMimeBody, String htmlBody) throws Exception{
		log.debug("sendIcalMessage");
		
		
		MailiCalThread mailiCalThread = new MailiCalThread(recipients, subject, iCalMimeBody, htmlBody);
		
		mailiCalThread.start();
		
	}
	//---------------------------------------------------------------------------------------------
	
}
