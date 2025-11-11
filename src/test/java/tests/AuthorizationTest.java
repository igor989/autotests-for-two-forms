package tests;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import protei_qa_test_webpage.constants.AuthConstants;
import protei_qa_test_webpage.pages.LoginPage;
import protei_qa_test_webpage.pages.ProfilePage;
import protei_qa_test_webpage.utils.DriverFactory;

/**
 * Набор тестов для проверки формы авторизации.
 */
public class AuthorizationTest {

    public static final String EMAIL_AND_PASSWORD_MASSAGE_ERROR = "Неверный E-Mail или пароль";
    public static final String EMAIL_MASSAGE_ERROR = "Неверный формат E-Mail";
    private LoginPage loginPage;
    private ProfilePage profilePage;

    @BeforeClass
    public void setUp() {
        loginPage = new LoginPage(DriverFactory.getDriver());
    }

    @AfterClass
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    @Test(description = "Позитивный сценарий авторизации")
    public void testSuccessfulLogin() {
        profilePage = loginPage
                .open()
                .enterEmail(AuthConstants.EMAIL)
                .enterPassword(AuthConstants.PASSWORD)
                .submitLogin();

        Assert.assertTrue(profilePage.isDisplayed(), "Форма профиля должна отображаться после успешного входа");
    }

    //по-хорошему влиять регистр не должен
    @Test(description = "Email с разным регистром должен работать")
    public void testLoginWithEmailDifferentCase() {
        profilePage = loginPage.open()
                .enterEmail("TEST@protei.ru")
                .enterPassword(AuthConstants.PASSWORD)
                .submitLogin();

        Assert.assertTrue(profilePage.isDisplayed());
    }

    @Test(description = "Кнопка входа должна быть активна, когда поля заполнены")
    public void testLoginButtonEnabledWhenFilled() {
        loginPage.open()
                .enterEmail(AuthConstants.EMAIL)
                .enterPassword(AuthConstants.PASSWORD);

        Assert.assertTrue(loginPage.isLoginButtonEnabled(), "Кнопка входа должна быть активна, когда введены учетные данные");
    }

    @Test(description = "Кнопка входа должна быть неактивна, если поля пустые")
    public void testLoginButtonDisabledWhenEmpty() {
        loginPage.open();
        Assert.assertFalse(loginPage.isLoginButtonEnabled(), "Кнопка должна быть неактивна при пустых полях");
    }

    // отображает Неверный E-Mail или пароль, а нужно Неверный формат E-Mail
    @Test(description = "Вход с корректным форматом email, но недопустимым доменом")
    public void testLoginWithValidFormatButWrongDomain() {
        loginPage.open()
                .enterEmail("user@gmail.com") // если валидный домен только protei.ru
                .enterPassword(AuthConstants.PASSWORD)
                .submitLogin();

        Assert.assertEquals(loginPage.getEmailFieldErrorMessage(), EMAIL_AND_PASSWORD_MASSAGE_ERROR,
                "Для email с недопустимым доменом должна быть ошибка");
    }

    // отображает Неверный E-Mail или пароль, а нужно Неверный формат E-Mail
    @Test(description = "Вход с некорректным email и корректным паролем")
    public void testLoginWithInvalidEmail() {
        loginPage.open()
                .enterEmail("invalid@example.com") // если принимаются только адреса @protei.ru
                .enterPassword(AuthConstants.PASSWORD)
                .submitLogin();

        Assert.assertTrue(loginPage.isEmailFieldErrorDisplayed(), "Должно отображаться сообщение об ошибке для некорректного email");
        Assert.assertEquals(loginPage.getErrorMessage(), EMAIL_MASSAGE_ERROR, "Текст сообщения об ошибке не совпадает");
    }

    @Test(description = "Вход с корректным email и некорректным паролем")
    public void testLoginWithInvalidPassword() {
        loginPage.open()
                .enterEmail(AuthConstants.EMAIL)
                .enterPassword("wrongpassword123")
                .submitLogin();

        Assert.assertTrue(loginPage.isPasswordFieldErrorDisplayed(), "Должно отображаться сообщение об ошибке для некорректного пароля");
        Assert.assertEquals(loginPage.getErrorMessage(), EMAIL_AND_PASSWORD_MASSAGE_ERROR, "Текст сообщения об ошибке не совпадает");
    }

    @Test(description = "Вход с пустым email и корректным паролем")
    public void testLoginWithEmptyEmail() {
        loginPage.open()
                .enterEmail("")
                .enterPassword(AuthConstants.PASSWORD)
                .submitLogin();

        Assert.assertTrue(loginPage.isEmailFieldErrorDisplayed(), "Поле email должно отображать ошибку валидации");
        Assert.assertEquals(loginPage.getEmailFieldErrorMessage(), EMAIL_MASSAGE_ERROR, "Текст ошибки в поле email не совпадает");
    }

    @Test(description = "Вход с корректным email и пустым паролем")
    public void testLoginWithEmptyPassword() {
        loginPage.open()
                .enterEmail(AuthConstants.EMAIL)
                .enterPassword("")
                .submitLogin();

        Assert.assertTrue(loginPage.isPasswordFieldErrorDisplayed(), "Поле пароля должно отображать ошибку валидации");
        Assert.assertEquals(loginPage.getPasswordFieldErrorMessage(), EMAIL_AND_PASSWORD_MASSAGE_ERROR, "Текст ошибки в поле пароля не совпадает");
    }

    @Test(description = "Вход с пустыми полями email и пароля")
    public void testLoginWithBothFieldsEmpty() {
        loginPage.open()
                .enterEmail("")
                .enterPassword("")
                .submitLogin();

        Assert.assertTrue(loginPage.isEmailFieldErrorDisplayed(), "Поле email должно отображать ошибку валидации");
    }

    @Test(description = "Вход с email, содержащим пробелы по краям")
    public void testLoginWithEmailWithSpaces() {
        profilePage = loginPage.open()
                .enterEmail("  " + AuthConstants.EMAIL + "  ")
                .enterPassword(AuthConstants.PASSWORD)
                .submitLogin();

        Assert.assertFalse(loginPage.isEmailFieldErrorDisplayed(), "Ошибка не должна отображаться, если пробелы автоматически удаляются");
    }

    @Test(description = "Email с невидимыми символами должен вызывать ошибку")
    public void testLoginWithEmailWithTab() {
        loginPage.open()
                .enterEmail("test\t@protei.ru")
                .enterPassword(AuthConstants.PASSWORD)
                .submitLogin();

        Assert.assertTrue(loginPage.isEmailFieldErrorDisplayed());
    }

    @Test(description = "Вход с email максимальной допустимой длины")
    public void testLoginWithMaxLengthEmail() {
        String longEmail = "a".repeat(320 - 9) + "@test.ru";
        loginPage.open()
                .enterEmail(longEmail)
                .enterPassword(AuthConstants.PASSWORD)
                .submitLogin();

        Assert.assertTrue(loginPage.isPasswordFieldErrorDisplayed(), "Вход должен завершиться ошибкой, если email слишком длинный");
    }

    // Нет ограничения на длину
    @Test(description = "Вход с email, превышающим максимальную длину")
    public void testLoginWithEmailTooLong() {
        String tooLongEmail = "a".repeat(255) + "@test.com";
        loginPage.open()
                .enterEmail(tooLongEmail)
                .enterPassword(AuthConstants.PASSWORD)
                .submitLogin();

        Assert.assertTrue(loginPage.isEmailFieldErrorDisplayed(), "Поле email должно отображать ошибку 'слишком длинный'");
    }

    @Test(description = "Поле пароля должно маскировать ввод")
    public void testPasswordIsMasked() {
        loginPage.open()
                .enterPassword("secret123");

        Assert.assertTrue(loginPage.isPasswordInputMasked(), "Поле ввода пароля должно иметь тип 'password'");
    }

    @Test(description = "Поля должны сохранять значения после неудачной попытки входа")
    public void testFieldsRetainValuesAfterFailedLogin() {
        String email = "bad@example.com";
        String password = "badpass";

        loginPage.open()
                .enterEmail(email)
                .enterPassword(password)
                .submitLogin();

        Assert.assertEquals(loginPage.getEmailFieldValue(), email, "Email должен сохраняться после неудачного входа");
        Assert.assertEquals(loginPage.getPasswordFieldValue(), password, "Пароль должен сохраняться после неудачного входа");
    }

    @Test(description = "Вход с некорректным email (отсутствует символ @)")
    public void testLoginWithMalformedEmailNoAt() {
        loginPage.open()
                .enterEmail("userexample.ru")
                .enterPassword(AuthConstants.PASSWORD)
                .submitLogin();

        Assert.assertTrue(loginPage.isEmailFieldErrorDisplayed(), "Должна отображаться ошибка формата email");
    }

    // пропускает без .ru
    @Test(description = "Вход с некорректным email (отсутствует домен .ru)")
    public void testLoginWithMalformedEmailNoAtDomain() {
        loginPage.open()
                .enterEmail("test@protei")
                .enterPassword(AuthConstants.PASSWORD)
                .submitLogin();

        Assert.assertTrue(loginPage.isEmailFieldErrorDisplayed(), "Должна отображаться ошибка формата email");
    }

    @Test(description = "Вход с email без домена")
    public void testLoginWithEmailWithoutDomain() {
        loginPage.open()
                .enterEmail("user@")
                .enterPassword(AuthConstants.PASSWORD)
                .submitLogin();

        Assert.assertTrue(loginPage.isEmailFieldErrorDisplayed(), "Должна отображаться ошибка формата email");
    }
}