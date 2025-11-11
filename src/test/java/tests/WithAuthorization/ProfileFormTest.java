package tests.WithAuthorization;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.*;
import protei_qa_test_webpage.constants.ProfileFormConstants;
import protei_qa_test_webpage.pages.LoginPage;
import protei_qa_test_webpage.pages.ProfilePage;
import protei_qa_test_webpage.utils.DriverFactory;
import protei_qa_test_webpage.utils.UtilOptionCombinate;

import java.util.List;

/**
 * Набор тестов для проверки формы профиля.
 * Покрывает позитивные, негативные и граничные сценарии.
 */
public class ProfileFormTest {

    private LoginPage loginPage;
    private ProfilePage profilePage;

    @BeforeClass
    public void setUpClass() {
        loginPage = new LoginPage(DriverFactory.getDriver());
    }

    @BeforeMethod
    public void setUpMethod() {
        profilePage = loginPage.open()
                .enterEmail(ProfileFormConstants.EMAIL)
                .enterPassword(ProfileFormConstants.PASSWORD)
                .submitLogin();
    }

    @AfterClass
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    @Test(description = "Позитивный сценарий — корректное заполнение всех полей")
    public void testSubmitProfileFormAndVerifyTableEntry() {

        profilePage
                .setEmail(ProfileFormConstants.EMAIL)
                .setName(ProfileFormConstants.NAME)
                .selectGender(ProfileFormConstants.GENDER)
                .checkOption11(ProfileFormConstants.OPTION1_1)
                .checkOption12(ProfileFormConstants.OPTION1_2)
                .selectOption2(ProfileFormConstants.OPTION2)
                .submitForm();

        List<WebElement> rows = profilePage.getAllRows();
        Assert.assertFalse(rows.isEmpty(), "Строка должна быть добавлена в таблицу");

        List<WebElement> cells = profilePage.getCells(rows.getFirst());
        Assert.assertEquals(cells.get(0).getText(), ProfileFormConstants.EMAIL);
        Assert.assertEquals(cells.get(1).getText(), ProfileFormConstants.NAME);
        Assert.assertEquals(cells.get(2).getText(), ProfileFormConstants.GENDER);
        Assert.assertEquals(cells.get(3).getText(), ProfileFormConstants.OPTION1_1);
        Assert.assertEquals(cells.get(4).getText(), ProfileFormConstants.OPTION2);
    }

    @Test(description = "Минимально валидный ввод — только обязательные поля")
    public void testSubmitOnlyRequiredFields() {
        profilePage
                .setEmail(ProfileFormConstants.EMAIL)
                .setName(ProfileFormConstants.NAME)
                .selectGender(ProfileFormConstants.GENDER)
                .checkOption11(!ProfileFormConstants.OPTION1_1)
                .checkOption12(!ProfileFormConstants.OPTION1_2)
                .selectOption2(ProfileFormConstants.OPTION2)
                .submitForm();

        List<WebElement> rows = profilePage.getAllRows();
        Assert.assertFalse(rows.isEmpty(), "Строка должна быть добавлена");
        List<WebElement> cells = profilePage.getCells(rows.getFirst());

        Assert.assertEquals(cells.get(0).getText(), ProfileFormConstants.EMAIL);
        Assert.assertEquals(cells.get(1).getText(), ProfileFormConstants.NAME);
        Assert.assertEquals(cells.get(2).getText(), ProfileFormConstants.GENDER);
        Assert.assertEquals(cells.get(3).getText(), "Нет");
        Assert.assertEquals(cells.get(4).getText(), ProfileFormConstants.OPTION2);
    }

    @DataProvider(name = "option1Values")
    public Object[][] option1Values() {
        return new Object[][]{
                {false, false, "Нет"},
                {true, false, "1.1"},
                {false, true, "1.2"},
                {true, true, "1.1, 1.2"}
        };
    }

    @DataProvider(name = "radioOptions")
    public Object[][] radioOptions() {
        return new Object[][]{
                {"2.1"},
                {"2.2"},
                {"2.3"}
        };
    }

    @Test(dataProvider = "option1Values", description = "Комбинации чекбоксов")
    public void testCheckbox(
            boolean check11,
            boolean check12,
            String expectedChoice
    ) {
        profilePage
                .setEmail(ProfileFormConstants.EMAIL)
                .setName(ProfileFormConstants.NAME)
                .selectGender(ProfileFormConstants.GENDER)
                .checkOption11(check11)
                .checkOption12(check12)
                .selectOption2(ProfileFormConstants.OPTION2)
                .submitForm();

        List<WebElement> lastRow = profilePage.getLastRow();
        List<WebElement> cells = profilePage.getCells(lastRow.getFirst());

        Assert.assertEquals(cells.get(3).getText(), expectedChoice, "Выбор не соответствует ожиданию");
    }

    @Test(dataProvider = "radioOptions", description = "Проверка выбора радиокнопок")
    public void testRadioOptions(String option2) {
        profilePage
                .setEmail(ProfileFormConstants.EMAIL)
                .setName(ProfileFormConstants.NAME)
                .selectGender(ProfileFormConstants.GENDER)
                .selectOption2(option2)
                .submitForm();

        List<WebElement> lastRow = profilePage.getLastRow();
        List<WebElement> cells = profilePage.getCells(lastRow.getFirst());
        Assert.assertEquals(cells.get(4).getText(), option2, "Выбор 2 не соответствует");
    }

    @DataProvider(name = "allCombinations")
    public Object[][] combinations() {
        return UtilOptionCombinate.combinate(option1Values(), radioOptions());
    }

    @Test(dataProvider = "allCombinations", description = "Все комбинации")
    public void testCheckboxCombinations(
            boolean check11,
            boolean check12,
            String option2Value,
            String expectedChoice1,
            String expectedChoice2
    ) {
        profilePage
                .setEmail(ProfileFormConstants.EMAIL)
                .setName(ProfileFormConstants.NAME)
                .selectGender(ProfileFormConstants.GENDER)
                .checkOption11(check11)
                .checkOption12(check12)
                .selectOption2(option2Value)
                .submitForm();

        List<WebElement> lastRow = profilePage.getLastRow();
        List<WebElement> cells = profilePage.getCells(lastRow.getFirst());

        Assert.assertEquals(cells.get(3).getText(), expectedChoice1, "Выбор 1 не соответствует ожиданию");
        Assert.assertEquals(cells.get(4).getText(), expectedChoice2, "Выбор 2 не соответствует ожиданию");
    }

    @Test(description = "Негативный сценарий — попытка добавить пользователя с уже существующим email")
    public void testDuplicateEmailNotAllowed() {;

        profilePage
                .setEmail(ProfileFormConstants.EMAIL)
                .setName(ProfileFormConstants.NAME)
                .selectGender(ProfileFormConstants.GENDER)
                .selectOption2(ProfileFormConstants.OPTION2)
                .submitForm()
                .clickAlertButton();

        profilePage
                .setEmail(ProfileFormConstants.EMAIL)
                .setName(ProfileFormConstants.NAME)
                .selectGender(ProfileFormConstants.GENDER)
                .selectOption2(ProfileFormConstants.OPTION2)
                .submitForm();

        Assert.assertTrue(
                profilePage.isAlertDisplayed("Пользователь с таким E-Mail уже существует"),
                "Должно появиться сообщение о дублировании email"
        );

        List<WebElement> rows = profilePage.getAllRows();
        Assert.assertEquals(rows.size(), 1, "В таблице должна остаться только одна запись");
        Assert.assertEquals(profilePage.getCells(rows.getFirst()).get(1).getText(), ProfileFormConstants.NAME, "Имя должно быть первого пользователя");
    }

    @Test(description = "Граничный сценарий — email с пробелами до и после")
    public void testEmailWithWhitespace() {
        String emailWithSpaces = "        "+ProfileFormConstants.EMAIL+"  ";

        profilePage
                .setEmail(emailWithSpaces)
                .setName(ProfileFormConstants.NAME)
                .selectGender(ProfileFormConstants.GENDER)
                .selectOption2(ProfileFormConstants.OPTION2)
                .submitForm();
        List<WebElement> lastRow = profilePage.getLastRow();
        List<WebElement> cells = profilePage.getCells(lastRow.getFirst());

        Assert.assertEquals(cells.getFirst().getText(), ProfileFormConstants.EMAIL, "Email должен быть без пробелов по краям");
    }

    @Test(description = "Граничный сценарий — имя с неразрешёнными специальными символами")
    public void testNameWithSpecialCharacters() {
        String name = "Анна№?*:%123";

        profilePage
                .setEmail(ProfileFormConstants.EMAIL)
                .setName(name)
                .selectGender(ProfileFormConstants.GENDER)
                .selectOption2(ProfileFormConstants.OPTION2)
                .submitForm();

        Assert.assertTrue(profilePage.isAlertDisplayed("Имя содержит спецсимволы"));
    }

    @Test(description = "Граничный сценарий — имя с разрешёнными специальными символами")
    public void testNameWithAllowedSpecialCharacters() {
        String name = "Анна_-№123";

        profilePage
                .setEmail(ProfileFormConstants.EMAIL)
                .setName(name)
                .selectGender(ProfileFormConstants.GENDER)
                .selectOption2(ProfileFormConstants.OPTION2)
                .submitForm();

        List<WebElement> rows = profilePage.getAllRows();

        Assert.assertEquals(rows.size(), 1, "В таблице должна появится запись");
    }

    @Test(description = "Граничный сценарий — имя с пробелами")
    public void testNameWitheSpaceBetweenWords() {
        String name = "Анна Кульга";

        profilePage
                .setEmail(ProfileFormConstants.EMAIL)
                .setName(name)
                .selectGender(ProfileFormConstants.GENDER)
                .selectOption2(ProfileFormConstants.OPTION2)
                .submitForm();

        Assert.assertTrue(profilePage.isAlertDisplayed("Имя содержит пробел"));
    }


    @Test(description = "Проверка уникальности email без учёта регистра")
    public void testEmailCaseInsensitive() {

        String emailUpper = ProfileFormConstants.EMAIL.toUpperCase();

        profilePage
                .setEmail(ProfileFormConstants.EMAIL)
                .setName("Нижний")
                .selectGender("Мужской")
                .selectOption2("2.1")
                .submitForm()
                .clickAlertButton();

        profilePage
                .setEmail(emailUpper)
                .setName("Верхний")
                .selectGender("Женский")
                .selectOption2("2.2")
                .submitForm();

        Assert.assertTrue(
                profilePage.isAlertDisplayed("Пользователь с таким E-Mail уже существует"),
                "Email должен считаться одинаковым независимо от регистра"
        );

        Assert.assertEquals(profilePage.getAllRows().size(), 1, "Должна быть только одна запись");
    }

    @Test(description = "Негативный сценарий — пустое поле Email")
    public void testEmptyEmail() {
        profilePage
                .setEmail("")
                .setName(ProfileFormConstants.NAME)
                .selectGender(ProfileFormConstants.GENDER)
                .selectOption2(ProfileFormConstants.OPTION2)
                .submitForm();

        Assert.assertTrue(profilePage.isAlertDisplayed("E-Mail"), "Должно появиться сообщение об ошибке Email");
        Assert.assertTrue(profilePage.isTableEmpty(), "Строка не должна добавляться в таблицу");
    }

    @Test(description = "Негативный сценарий — неверный формат Email")
    public void testInvalidEmailFormat() {
        profilePage
                .setEmail("wrongEmail")
                .setName(ProfileFormConstants.NAME)
                .selectGender(ProfileFormConstants.GENDER)
                .selectOption2(ProfileFormConstants.OPTION2)
                .submitForm();

        Assert.assertTrue(profilePage.isAlertDisplayed("E-Mail"), "Должно появиться сообщение об ошибке формата Email");
    }

    @Test(description = "Негативный сценарий — пустое поле Имя")
    public void testEmptyName() {
        profilePage
                .setEmail(ProfileFormConstants.EMAIL)
                .setName("")
                .selectGender(ProfileFormConstants.GENDER)
                .selectOption2(ProfileFormConstants.OPTION2)
                .submitForm();

        Assert.assertTrue(profilePage.isAlertDisplayed("Поле имя не может быть пустым"), "Должно появиться сообщение об ошибке поля Имя");
    }

    @Test(description = "Негативный сценарий — не выбран пол")
    public void testGenderNotSelected() {
        profilePage
                .setEmail(ProfileFormConstants.EMAIL)
                .setName(ProfileFormConstants.NAME)
                .clearGenderSelection()
                .selectOption2(ProfileFormConstants.OPTION2)
                .submitForm();

        Assert.assertTrue(profilePage.isAlertDisplayed("Выберите пол"), "Должно появиться сообщение о незаполненном поле Пол");
    }

    @Test(description = "Граничный случай — длинное имя и email")
    public void testLongInputValues() {
        String longEmail = "user_very_long_email_address_over_100_chars_" + System.currentTimeMillis() + "@protei.ru";
        String longName = "user_very_long ".repeat(50);

        profilePage
                .setEmail(longEmail)
                .setName(longName)
                .selectGender(ProfileFormConstants.GENDER)
                .checkOption11(ProfileFormConstants.OPTION1_1)
                .selectOption2(ProfileFormConstants.OPTION2)
                .submitForm();

        List<WebElement> lastRow = profilePage.getLastRow();
        List<WebElement> cells = profilePage.getCells(lastRow.getFirst());

        Assert.assertTrue(cells.get(0).getText().contains(longEmail), "Email сохранён корректно");
        Assert.assertTrue(cells.get(1).getText().contains("user_very_long"), "Имя сохранено корректно");
    }

    @Test(description = "Добавление нескольких записей подряд")
    public void testMultipleSubmissions() {
        for (int i = 0; i < 3; i++) {
            profilePage
                    .setEmail("user" + i + "@protei.ru")
                    .setName(ProfileFormConstants.NAME + i)
                    .selectGender(ProfileFormConstants.GENDER)
                    .checkOption11(ProfileFormConstants.OPTION1_1)
                    .selectOption2(ProfileFormConstants.OPTION2)
                    .submitForm()
                    .clickAlertButton();
        }

        Assert.assertEquals(profilePage.getAllRows().size(), 3, "Должно быть добавлено 3 записи");
    }
}