package atsaratbriut.school.config;
import java.util.Properties;


import atsaratbriut.school.security.AES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


@Configuration
public class EmailConfig {
    private static final String EMAIL_ADMIN_PASSWORD = "korbYXxWYBZT3uuONcvnfKWKHoKHE0ZNKLbIlnoTSEQ=";
    @Autowired
    Environment env;
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(env.getProperty("email.address"));
        mailSender.setPassword(AES.decrypt(EMAIL_ADMIN_PASSWORD, env.getProperty("secretPassword")));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
