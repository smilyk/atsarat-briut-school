package atsaratbriut.school.service.parser;

import atsaratbriut.school.constants.Constants;
import atsaratbriut.school.dto.ConfirmationEmailDto;
import atsaratbriut.school.exception.exception.SchoolServiceException;
import atsaratbriut.school.security.AES;
import atsaratbriut.school.service.email.EmailService;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
public class ParsServiceImpl implements ParsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParsServiceImpl.class);

    LocalDate nowTime = LocalDate.now();

    @Autowired
    EmailService emailService;

    @Value("${schoolUrl}")
    String schoolUrl;
    @Value("secretPassword")
    String secretWord;

    @Override
    /**
     * checking in 7 hours 40 minutes every day without saturday
     */
    @Scheduled(cron = "0 10 7 ? * SUN-FRI", zone = "Asia/Jerusalem")
    public String sendFormToSchool() {
        WebDriver driver = getWebDriver();
        boolean fillingDocument = fillingDocument(driver);
        if (fillingDocument) {
            LOGGER.info(nowTime + ": -> " + "create screen with name  " + Constants.FILE_NAME);
        } else {
            LOGGER.error("Something wrong with filling document");
            emailService.emailError(Constants.CHILD_FIRST_NAME,
                    Constants.CHILD_SECOND_NAME);
            throw new SchoolServiceException("Something wrong with filling document");
        }
        String file = fileToBase64();
        emailService.sendSchoolEmail(getConfirmationEmailDto(file));
        return "Done";
    }

    private ConfirmationEmailDto getConfirmationEmailDto(String file) {
        return ConfirmationEmailDto.builder()
                .childFirstName(Constants.CHILD_FIRST_NAME)
                .childSecondName(Constants.CHILD_SECOND_NAME)
                .email(Constants.EMAIL)
                .picture(file)
                .userLastName(Constants.CHILD_SECOND_NAME)
                .userName(Constants.USER_FIRST_NAME)
                .build();
    }

    private String fileToBase64() {
        String encodeString = "";
        try {
            byte[] file = FileUtils.readFileToByteArray(new File(Constants.FILE_NAME));
            encodeString = Base64.getEncoder().encodeToString(file);

        } catch (IOException e) {
            LOGGER.error("Something wrong during reading file " + e.getMessage());
            e.printStackTrace();
        }
        LOGGER.info(nowTime + ": -> " + "encoded screenshot");
        return encodeString;
    }

    private boolean fillingDocument(WebDriver driver) {
        LOGGER.info(nowTime + ": -> " + "start parse school page with url: " + schoolUrl);
        WebElement buttonToSchoolLoginByMisradHinuh = driver.findElement(By.xpath("//a[@id='misradHachinuch']"));
        buttonToSchoolLoginByMisradHinuh.click();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        WebElement div = driver.findElement(By.xpath("/html/body/section/div[1]/div[3]"));
        div.click();
        WebDriverWait waitForCredentials = new WebDriverWait(driver, 10);
        WebElement studentCode = waitForCredentials.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"HIN_USERID\"]")));
        studentCode.sendKeys(Constants.SCHOOL_USER_NAME);
        LOGGER.info("Schoold user name filled");
        WebElement studentPassword = waitForCredentials.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='Ecom_Password']")));

        LOGGER.info("Schoold user password filled");
        studentPassword.sendKeys(AES.decrypt(Constants.SCHOOL_PASSWORD, secretWord));

        WebElement buttonLogin = driver.findElement(By.xpath("//button[@id='loginButton2']"));
        buttonLogin.submit();

        driver.manage().timeouts().implicitlyWait(300, TimeUnit.SECONDS);
        WebElement makeDocument = driver.findElement(
                By.xpath("/html[1]/body[1]/form[1]/main[1]/div[2]/div[1]/div[1]/div[1]/a[1]"));
        WebDriverWait wait = new WebDriverWait(driver, 60); //here, wait time is 40 seconds
        wait.until(webDriver -> ExpectedConditions.visibilityOf(makeDocument));
        driver.manage().timeouts().implicitlyWait(90, TimeUnit.SECONDS);
        makeDocument.click();
        WebElement x = driver.findElement(By.xpath("/html[1]/body[1]/form[1]/div[3]"));
        WebDriverWait wait1 = new WebDriverWait(driver, 60);
        wait1.until(webDriver -> ExpectedConditions.visibilityOf(x));
        wait1 = new WebDriverWait(driver, 60); //here, wait time is 120 seconds
        wait1.until(webDriver -> ExpectedConditions.visibilityOf(x));
        //TODO заполнить тофес
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(Constants.FILE_NAME));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        driver.quit();
        LOGGER.info(nowTime + ": -> " + "finish parse school page with url: " + schoolUrl);
        return true;
    }

    private WebDriver getWebDriver() {
//        System.setProperty("CHROMEDRIVER_PATH", "CHROMEDRIVER_PATH");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.setBinary("GOOGLE_CHROME_BIN");
        options.addArguments("--whitelisted-ips=\"\"");
        options.addArguments("window-size=1200x600");
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("https://www.webtop.co.il/mobilev2/");
        return driver;
    }
}
