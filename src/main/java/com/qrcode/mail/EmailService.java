package com.qrcode.mail;

import com.barclays.mortgages.paymentholiday.ImageAttributes;
import com.barclays.mortgages.paymentholiday.InputSourceStreamAttributes;
import com.barclays.mortgages.paymentholiday.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;


@Service
public class EmailService {
    private static Logger logger = LoggerFactory.getLogger(EmailService.class);
    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(Mail mail) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        attachments(mail,helper);
        attachInputSourceStreams(mail,helper);

        logger.info(mail.getContent());
        helper.setText(mail.getContent(), true);
        helper.setSubject(mail.getSubject());
        helper.setTo(mail.getTo());

        helper.setFrom(mail.getFrom());
        emailSender.send(message);
    }

	private void attachments(Mail mail, MimeMessageHelper helper, String classpath) throws MessagingException
    {
     System.out.println("Attaching the documents ... ");
        if (mail.getImageNames()==null)
        {
            return;
        }
        Iterator<ImageAttributes> itrAttachemnts= mail.getImageNames().iterator();

        while(itrAttachemnts.hasNext()) {
            String attachment;
            ImageAttributes attachmentAttributes= itrAttachemnts.next();
            attachment = attachmentAttributes.getName() + "." + attachmentAttributes.getExtension();
            System.out.println("image to be added " + attachment);
            if(attachmentAttributes.getExtension()!=null &&
                    !attachmentAttributes.getExtension().equals("")) {
                System.out.println("Attachment location Header "+attachmentAttributes.getHeader());
                helper.addAttachment(attachmentAttributes.getHeader(), new ClassPathResource(attachment));
            }
            else {
                System.out.println("Attachment location Filename "+attachmentAttributes.getName());
                helper.addAttachment(attachmentAttributes.getName(), new ClassPathResource(attachment));
            }
            //  helper.addInline(imageName, new ClassPathResource(imageName));
            //   ImageIO i;
            // ByteArrayInputStream b;
            //  FileDataSource f = new FileDataSource(  )
        }

    }

    private void attachments(Mail mail, MimeMessageHelper helper) throws MessagingException
    {
        logger.info("Attaching the documents ... ");
        if (mail.getImageNames()==null)
        {
            return;
        }
        Iterator<ImageAttributes> itrAttachemnts= mail.getImageNames().iterator();

        while(itrAttachemnts.hasNext()) {
            ImageAttributes attachmentAttributes= itrAttachemnts.next();
            String path = attachmentAttributes.getFilePath();
            if(attachmentAttributes.getExtension()!=null &&
                    !attachmentAttributes.getExtension().equals("")) {
                logger.info("Attachment location Header "+attachmentAttributes.getHeader());
                helper.addAttachment(attachmentAttributes.getHeader(), new File(path ));
            }
            else {
                logger.info("Attachment location Filename "+attachmentAttributes.getName());
                helper.addAttachment(attachmentAttributes.getName(), new File(path));
            }
            //  helper.addInline(imageName, new ClassPathResource(imageName));
            //   ImageIO i;
            // ByteArrayInputStream b;
            //  FileDataSource f = new FileDataSource(  )
        }

    }
    private void attachInputSourceStreams(Mail mail, MimeMessageHelper helper) throws MessagingException
    {
        if (mail.getInputStreamSourceSources()==null)
        {
            return;
        }


        Iterator<InputSourceStreamAttributes> itrInputSourceStreams= mail.getInputStreamSourceSources().iterator();
        logger.info("printing QR codes now ");
        while(itrInputSourceStreams.hasNext()) {
            logger.info("inside first qrode request");
            InputSourceStreamAttributes inputStreamSourceAttributes =itrInputSourceStreams.next();
            InputStreamSource inputStreamSource =
                    inputStreamSourceAttributes.getInputStreamSource();
                    //qrCodeGenService.getQRCodeImage(qrCodeAttributes);
            logger.info("The value of attachment "+ inputStreamSourceAttributes.getHeader());
            if (inputStreamSource!=null)
                helper.addAttachment(inputStreamSourceAttributes.getHeader(), inputStreamSource);
        }

    }

}

