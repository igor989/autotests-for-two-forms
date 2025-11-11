package tests.NoAuthorization;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import protei_qa_test_webpage.constants.ProfileFormConstants;
import protei_qa_test_webpage.pages.LoginPage;
import protei_qa_test_webpage.pages.ProfilePage;
import protei_qa_test_webpage.utils.DriverFactory;

import java.util.List;

public class ProfileFormNoAuthTest {

    private LoginPage loginPage;
    private ProfilePage profilePage;

    @BeforeClass
    public void setUpClass(){
        loginPage = new LoginPage(DriverFactory.getDriver());
        profilePage = new ProfilePage(DriverFactory.getDriver());
    }

    @AfterClass
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    @Test(description = "Проверка безопасности: Попытайтесь отправить скрытую форму, удалив 'display: none' с помощью JavaScript")
    public void testHiddenFormSubmissionShouldBeRejected() {

        loginPage.open();
        WebDriver driver = DriverFactory.getDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement inputsPage = profilePage.getInputsPage();

        String initialDisplay = inputsPage.getCssValue("display");
        Assert.assertEquals(initialDisplay, "none", "Форма должна быть скрыта изначально (display: none)");

        js.executeScript("arguments[0].style.display = 'block';", inputsPage);

        String newDisplay = inputsPage.getCssValue("display");
        Assert.assertEquals(newDisplay, "block", "Форма должна стать видимой после изменения стиля");

        profilePage
                .setEmail(ProfileFormConstants.EMAIL)
                .setName(ProfileFormConstants.NAME)
                .selectGender(ProfileFormConstants.GENDER)
                .checkOption11(ProfileFormConstants.OPTION1_1)
                .checkOption12(!ProfileFormConstants.OPTION1_2)
                .selectOption2(ProfileFormConstants.OPTION2)
                .submitForm();

        // Вариант 1: сервер отклонил
        // Вариант 2: сервер принял - данные появились в таблице - это УЯЗВИМОСТЬ!
        try {
            WebElement elementTb = profilePage.getRowsByIndices(List.of(0)).getFirst();
            if(!profilePage.getCells(elementTb).isEmpty())
                Assert.fail("УЯЗВИМОСТЬ: Сервер принял данные из скрытой формы! Это небезопасно.");

        } catch (TimeoutException e) {
            Assert.assertTrue(true, "Сервер корректно отклонил попытку отправки данных из скрытой формы");
        }
    }
}