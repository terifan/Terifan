package org.terifan.util.log;

import java.util.Date;
import java.util.Properties;
//import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.terifan.util.Calendar;


public class MailTarget implements LogTarget
{
	private String mLocalHost;
	private String mMailHost;
	private String mFromEmail;
	private String mFromName;
	private String mToEmail;
	private String mSubject;
	private String mBody;


	/**
	 * Create new instance
	 *
	 * @param aLocalHost
	 *   local IP address of computer
	 * @param aMailHost
	 *   mail host address
	 * @param aFromEmail
	 *   senders e-mail address
	 * @param aFromName
	 *   senders name
	 * @param aToEmail
	 *   recipient e-mail address
	 * @param aSubject
	 *   the mail subject, may contain one or more of keywords <code>{date}</code>, <code>{cause}</code>,
	 *   <code>{tag} and <code>{level}. Keywords <code>{cause} and <code>{stacktrace} are only applicable
	 *   when a exception is included.
	 * @param aBody
	 *   the mail body, may contain one or more of keywords <code>{date}</code>, <code>{cause}</code>,
	 *   <code>{tag}</code>, <code>{level}</code>, <code>{message}</code> and <code>{stacktrace}</code>. Keywords <code>{cause}</code> and
	 *   <code>{stacktrace}</code> are only applicable when a exception is included.
	 */
	public MailTarget(String aLocalHost, String aMailHost, String aFromEmail, String aFromName, String aToEmail, String aSubject, String aBody)
	{
		mLocalHost = aLocalHost;
		mMailHost = aMailHost;
		mFromEmail = aFromEmail;
		mFromName = aFromName;
		mToEmail = aToEmail;
		mSubject = aSubject;
		mBody = aBody;
	}


	@Override
	public void writeLogEntry(Date aTime, LogLevel aLogLevel, String aTag, String aMessage, Throwable aThrowable)
	{
		try
		{
			String subject = mSubject;
			String body = mBody;

			if (subject.contains("{"))
			{
				if (aThrowable != null)
				{
					subject = subject.replace("{cause}", ""+aThrowable.getMessage());
				}
				else
				{
					subject = subject.replace("{cause}", "");
				}
				subject = subject.replace("{date}", Calendar.now());
				subject = subject.replace("{level}", aLogLevel.name());
				subject = subject.replace("{tag}", aTag);
			}
			if (body.contains("{"))
			{
				if (aThrowable != null)
				{
					body = body.replace("{cause}", ""+aThrowable.getMessage());
					body = body.replace("{stacktrace}", Log.getStackTraceString(aThrowable));
				}
				else
				{
					body = body.replace("{cause}", "");
					body = body.replace("{stacktrace}", "");
				}
				body = body.replace("{date}", Calendar.now());
				body = body.replace("{level}", aLogLevel.name());
				body = body.replace("{tag}", aTag);
				body = body.replace("{message}", aMessage);
			}

			Properties p = new Properties();
			p.put("mail.user", mFromEmail);
			p.put("mail.host", mMailHost);
			p.put("mail.smtp.localhost", mLocalHost);

			MimeBodyPart bodyPart = new MimeBodyPart();
//			bodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(body, "text/html")));
			bodyPart.setContent(body, "text/html");

			Multipart multiPart = new MimeMultipart();
			multiPart.addBodyPart(bodyPart);

			MimeMessage msg = new MimeMessage(Session.getDefaultInstance(p, null));
			msg.setSubject(subject);
			msg.setFrom(new InternetAddress(mFromEmail, mFromName));
			msg.setSentDate(new Date());
			msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(mToEmail, false));
			msg.setContent(multiPart);

			Transport.send(msg);
		}
		catch (Throwable e)
		{
			e.printStackTrace(Log.err);
		}
	}
}
