package com.steepgraph.ta.framework.utils.pages;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.steepgraph.ta.framework.Constants;

public class EMailUtil {

	/**
	 * This Method will be used to send test execution report in mail to configured
	 * recipient.
	 * 
	 * @author SteepGraph Systems
	 * @param date
	 * @return true if input date matching with given format else return false.
	 * @throws Exception
	 */
	public void sendTestReportInMail(PropertyUtil propertyUtil) throws Exception {

		String from = propertyUtil.getProperty(Constants.PROPERTY_KEY_MAIL_FROM);
		LoggerUtil.debug("from : " + from);

		String to = propertyUtil.getProperty(Constants.PROPERTY_KEY_MAIL_TO);
		LoggerUtil.debug("to : " + to);

		String cc = propertyUtil.getProperty(Constants.PROPERTY_KEY_MAIL_CC);
		LoggerUtil.debug("cc : " + cc);

		String bcc = propertyUtil.getProperty(Constants.PROPERTY_KEY_MAIL_BCC);
		LoggerUtil.debug("bcc : " + bcc);

		final String username = propertyUtil.getProperty(Constants.PROPERTY_KEY_MAIL_SMTP_USERNAME);
		final String password = propertyUtil.getProperty(Constants.PROPERTY_KEY_MAIL_SMTP_PASSWORD);

		String smtpHost = propertyUtil.getProperty(Constants.PROPERTY_KEY_MAIL_SMTP_HOST);
		LoggerUtil.debug("bcc : " + bcc);

		String port = propertyUtil.getProperty(Constants.PROPERTY_KEY_MAIL_SMTP_PORT);
		LoggerUtil.debug("port : " + port);

		LoggerUtil.debug("bcc : " + bcc);
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", port);

		// Get the Session object.
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		Message message = new MimeMessage(session);

		String[] fromIds = from.split(",");
		for (int i = 0; i < fromIds.length; i++) {
			message.setFrom(new InternetAddress(fromIds[i]));
		}

		String[] toIds = to.split(",");
		for (int i = 0; i < toIds.length; i++) {
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toIds[i]));
		}

		if (cc != null && "".equals(cc)) {
			String[] ccIds = cc.split(",");
			for (int i = 0; i < ccIds.length; i++) {
				message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccIds[i]));
			}
		}

		if (bcc != null && "".equals(bcc)) {
			String[] bccIds = bcc.split(",");
			for (int i = 0; i < bccIds.length; i++) {
				message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bccIds[i]));
			}
		}

		String subject = propertyUtil.getProperty(Constants.PROPERTY_KEY_MAIL_SUBJECT);
		LoggerUtil.debug("subject : " + subject);
		if (subject == null || "".equals(subject)) {
			subject = "Test Automation Execution Report";
		}
		message.setSubject(subject);

		BodyPart messageBodyPart = new MimeBodyPart();
		String body = propertyUtil.getProperty(Constants.PROPERTY_KEY_MAIL_BODY);
		if (body == null || "".equals(body)) {
			body = "Hello,\n PFA test execution report.\n\n Best Regards,\n Test Automation Tool.";
		}

		messageBodyPart.setText(body);
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		File testReportFile = new File("test-output\\emailable-report.html");
		if (!testReportFile.exists())
			throw new Exception(
					"Not able to send test report in mail as test-output\\emailable-report.html is not generated.");

		// Part two is attachment
		messageBodyPart = new MimeBodyPart();
		String filename = "test-output\\emailable-report.html";
		DataSource source = new FileDataSource(filename);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);
		multipart.addBodyPart(messageBodyPart);

		// Send the complete message parts
		message.setContent(multipart);

		// Send message
		Transport.send(message);

		System.out.println("Test Report Mail Sent successfully....");
		LoggerUtil.debug("End of sendTestReportInMail");
	}

}