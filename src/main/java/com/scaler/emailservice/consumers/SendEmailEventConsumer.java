package com.scaler.emailservice.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaler.emailservice.dtos.SendEmailEventDTO;
import com.scaler.emailservice.utils.EmailUtil;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Service
public class SendEmailEventConsumer {

    private ObjectMapper objectMapper;

    public SendEmailEventConsumer(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "sendEmail",
            groupId = "emailService"
            // all these are a part of same group!
            // so send email to ANY one of them!
    )
    public void handleSendEmailEvent(String message) throws JsonProcessingException {
        SendEmailEventDTO sendEmailEventDTO =
                objectMapper.readValue(message, SendEmailEventDTO.class);

        String to = sendEmailEventDTO.getTo();
        String from = sendEmailEventDTO.getFrom();
        String subject = sendEmailEventDTO.getSubject();
        String body = sendEmailEventDTO.getBody();


        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "vpgjointtmsplidl"); // use App password here
                // is this to whom you are sending?
                // to from whom you are sending. This.
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session, to,subject, body);
    }
}
