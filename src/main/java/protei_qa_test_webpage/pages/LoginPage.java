package protei_qa_test_webpage.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import protei_qa_test_webpage.utils.ConfigReader;
import protei_qa_test_webpage.utils.DriverFactory;

public class LoginPage {

    private final WebDriver driver;

    public static final By EMAIL_INPUT = By.id("loginEmail");
    public static final By PASSWORD_INPUT = By.id("loginPassword");
    public static final By LOGIN_BUTTON = By.id("authButton");

    public static final By EMAIL_ERROR = By.id("emailFormatError");
    public static final By EMAIL_OR_PASSWORD_ERROR = By.id("invalidEmailPassword");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public LoginPage open() {
        driver.get(ConfigReader.getProperty("base.url"));
        // Ждём, пока поле email станет видимым — гарантируем, что страница загружена
        // Каждый раз метод ищет элемент заново. Это гарантирует актуальность элемента,
        // вообще в данном случае можно вынести в private final WebElement так как динамики нет, упасть с StaleElementReferenceException не должны.
        DriverFactory.getWait().until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUT));
        return this;
    }

    public LoginPage enterEmail(String email) {
        WebElement emailEl = DriverFactory.getWait().until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUT));
        emailEl.clear();
        emailEl.sendKeys(email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        WebElement passEl = DriverFactory.getWait().until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_INPUT));
        passEl.clear();
        passEl.sendKeys(password);
        return this;
    }

    public ProfilePage submitLogin() {
        DriverFactory.getWait().until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON)).click();
        return new ProfilePage(driver);
    }

    public boolean isLoginButtonEnabled() {
        return DriverFactory.getWait()
                .until(ExpectedConditions.presenceOfElementLocated(LOGIN_BUTTON))
                .isEnabled();
    }

    public String getErrorMessage() {
        try {
            return DriverFactory.getWait().until(ExpectedConditions.visibilityOfElementLocated(EMAIL_OR_PASSWORD_ERROR)).getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }

    public boolean isEmailFieldErrorDisplayed() {
        try {
            return DriverFactory.getWait().until(ExpectedConditions.visibilityOfElementLocated(EMAIL_ERROR)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getEmailFieldErrorMessage() {
        try {
            return DriverFactory.getWait().until(ExpectedConditions.visibilityOfElementLocated(EMAIL_ERROR)).getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }

    public boolean isPasswordFieldErrorDisplayed() {
        try {
            return DriverFactory.getWait().until(ExpectedConditions.visibilityOfElementLocated(EMAIL_OR_PASSWORD_ERROR)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getPasswordFieldErrorMessage() {
        try {
            return DriverFactory.getWait().until(ExpectedConditions.visibilityOfElementLocated(EMAIL_OR_PASSWORD_ERROR)).getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }

    public String getEmailFieldValue() {
        return driver.findElement(EMAIL_INPUT).getAttribute("value");
    }

    public String getPasswordFieldValue() {
        return driver.findElement(PASSWORD_INPUT).getAttribute("value");
    }

    public boolean isPasswordInputMasked() {
        String type = driver.findElement(PASSWORD_INPUT).getAttribute("type");
        return "password".equalsIgnoreCase(type);
    }
}