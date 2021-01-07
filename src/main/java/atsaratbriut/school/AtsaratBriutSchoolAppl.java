package atsaratbriut.school;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AtsaratBriutSchoolAppl {
    public static void main(String[] args) {

        System.setProperty("webdriver.chrome.driver", "driver/chromedriver");
        SpringApplication.run(AtsaratBriutSchoolAppl.class, args);
    }

}
