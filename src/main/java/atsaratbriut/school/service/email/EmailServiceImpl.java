package atsaratbriut.school.service.email;

import atsaratbriut.school.constants.Constants;
import atsaratbriut.school.dto.ConfirmationEmailDto;
import atsaratbriut.school.exception.exception.SchoolServiceException;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    private static final String CURRENTLY_DATE = LocalDateTime.now().toLocalDate().toString();
    @Autowired
    JavaMailSender emailSender;
    @Value("${email.address}")
    String adminEmail;
    @Autowired
    Environment env;

    @Override
    public String sendSchoolEmail(ConfirmationEmailDto confirmationEmailDto) {

        MimeMessage msg = emailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(msg, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        final String htmlMsg =
                "<h1> Hi " + confirmationEmailDto.getUserName() + " " + confirmationEmailDto.getUserLastName() + ".</h1>" +
                        "<br> you filled out a confirmation that the child " + confirmationEmailDto.getChildFirstName() + " " +
                        confirmationEmailDto.getChildSecondName() +
                        "<br> can attend school Ben Gurion in Mazceret Batya  <hr>" +
                        "<br>Date: " + CURRENTLY_DATE + "<hr>" +
                        "<br>Resolution documents you can check in attached file";
        try {
            helper.setTo(confirmationEmailDto.getEmail());

            byte[] decodedImage = Base64.getDecoder().decode(confirmationEmailDto.getPicture());
            helper.addAttachment("school_ben_gurion_resolution.jpg", new ByteArrayResource(decodedImage));
            helper.addAttachment("logo.png", new ClassPathResource("logo.png"));
            helper.setSubject("Atsarat Briut for School Ben Gurion In Mazceret Batya email from" +
                    Constants.APPLICATION_NAME);
            helper.setText(htmlMsg, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        try {
            emailSender.send(msg);
            LOGGER.info(" School Ben Gurion atsarat briut was send to e-mail: " + confirmationEmailDto.getEmail());
        } catch (Exception ex) {
            LOGGER.error("scgool Ben Gurion atsarat briut email: something is wrong" + ex);
            emailError(confirmationEmailDto.getUserLastName(), confirmationEmailDto.getUserName());
            LOGGER.error("send error-email to administrator" + ex.getMessage());
            throw new SchoolServiceException("send error-email to administrator" + ex.getMessage());
        }
        return "Email send!";
    }

    @Override
    public void emailError( String lastName, String firstName) {
        MimeMessage msg = emailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(msg, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        final String htmlMsg =
                "<h1> Hi  administrator </h1>>" +
                        " <hr><br>there is some problems during sending email from "
                        + "to e-mail: " + adminEmail + " for " + firstName + lastName + ". \n" +
                        "<br>please, check logfile.";
        try {
        helper.setTo(adminEmail);
        helper.addAttachment("logo.png", new ClassPathResource("logo.png"));
        helper.setSubject("Error email" + Constants.APPLICATION_NAME);
        helper.setText(htmlMsg, true);

            emailSender.send(msg);
            LOGGER.info(" Error email was send to  administrator to e-mail: " + adminEmail);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new SchoolServiceException(" error during sending e-mail " +
                    ex.getMessage());
        }
    }
    }






