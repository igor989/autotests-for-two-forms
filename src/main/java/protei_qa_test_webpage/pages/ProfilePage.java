package protei_qa_test_webpage.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import protei_qa_test_webpage.constants.ProfileFormConstants;
import protei_qa_test_webpage.utils.DriverFactory;

import java.util.ArrayList;
import java.util.List;

public class ProfilePage {

    private final WebDriver driver;

    public static final By INPUTS_PAGE = By.id("inputsPage");

    public static final By EMAIL_FIELD = By.id("dataEmail");
    public static final By NAME_FIELD = By.id("dataName");
    public static final By GENDER_SELECT = By.id("dataGender");

    public static final By CHECKBOX_11 = By.id("dataCheck11");
    public static final By CHECKBOX_12 = By.id("dataCheck12");

    public static final By RADIO_21 = By.id("dataSelect21");
    public static final By RADIO_22 = By.id("dataSelect22");
    public static final By RADIO_23 = By.id("dataSelect23");

    public static final By SUBMIT_BUTTON = By.id("dataSend");

    public static final By TABLE_ROWS = By.cssSelector("#dataTable tbody tr");
    public static final By ALERTS_HOLDER = By.id("dataAlertsHolder");

    public static final By ALERTS_BUTTON = By.cssSelector("button.uk-button.uk-button-primary.uk-modal-close");

    public ProfilePage(WebDriver driver) {
        this.driver = driver;
    }

    public ProfilePage open() {
        driver.get(ProfileFormConstants.BASE_URL);
        return this;
    }

    public WebElement getInputsPage() {
        return driver.findElement(INPUTS_PAGE);
    }

    public boolean isDisplayed() {
        try {
            DriverFactory.getWait()
                    .until(ExpectedConditions.visibilityOfElementLocated(INPUTS_PAGE))
                    .isDisplayed();
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public ProfilePage setEmail(String email) {
        WebElement el = DriverFactory.getWait().until(ExpectedConditions.visibilityOfElementLocated(EMAIL_FIELD));
        el.clear();
        el.sendKeys(email);
        return this;
    }

    public ProfilePage setName(String name) {
        WebElement el = DriverFactory.getWait().until(ExpectedConditions.visibilityOfElementLocated(NAME_FIELD));
        el.clear();
        el.sendKeys(name);
        return this;
    }

    /**
     * Выбор пола через <select>
     * @param gender "Мужской" или "Женский"
     * Плохо, что нет значения по умолчанию - не выбрано
     */
    public ProfilePage selectGender(String gender) {
        WebElement selectElement = DriverFactory.getWait().until(ExpectedConditions.elementToBeClickable(GENDER_SELECT));
        Select select = new Select(selectElement);
        select.selectByVisibleText(gender);
        return this;
    }

    public ProfilePage checkOption11(boolean checked) {
        toggleCheckbox(CHECKBOX_11, checked);
        return this;
    }

    public ProfilePage checkOption12(boolean checked) {
        toggleCheckbox(CHECKBOX_12, checked);
        return this;
    }

    private void toggleCheckbox(By locator, boolean shouldBeChecked) {
        WebElement checkbox = DriverFactory.getWait().until(ExpectedConditions.elementToBeClickable(locator));
        if (checkbox.isSelected() != shouldBeChecked) {
            checkbox.click();
        }
    }

    public ProfilePage selectOption2(String option) {
        By radioLocator = switch (option) {
            case "2.1" -> RADIO_21;
            case "2.2" -> RADIO_22;
            case "2.3" -> RADIO_23;
            default -> throw new IllegalArgumentException("Параметр должен быть 2.1, 2.2 или 2.3");
        };
        WebElement radio = DriverFactory.getWait().until(ExpectedConditions.elementToBeClickable(radioLocator));
        if (!radio.isSelected()) {
            radio.click();
        }
        return this;
    }

    public ProfilePage submitForm() {
        DriverFactory.getWait().until(ExpectedConditions.elementToBeClickable(SUBMIT_BUTTON)).click();
        return this;
    }

    public List<WebElement> getRowsByIndices(List<Integer> indices) {
        List<WebElement> allRows = driver.findElements(TABLE_ROWS);
        List<WebElement> selectedRows = new ArrayList<>();

        for (Integer index : indices) {
            if (index >= 0 && index < allRows.size()) {
                selectedRows.add(allRows.get(index));
            } else {
                throw new IndexOutOfBoundsException("Индекс вне диапазона " + index +". Общее количество строк: " + allRows.size());
            }
        }

        return selectedRows;
    }

    public List<WebElement> getCells(WebElement element) {
        return element.findElements(By.tagName("td"));
    }

    public List<WebElement> getAllRows() {
        return driver.findElements(TABLE_ROWS);
    }

    public List<WebElement> getLastRow() {
        List<WebElement> rows = driver.findElements(TABLE_ROWS);
        if (rows.isEmpty()) {
            throw new AssertionError("Таблица пуста, строк нет");
        }
        return List.of(rows.getLast());
    }

    public boolean isTableEmpty() {
        return driver.findElements(TABLE_ROWS).isEmpty();
    }

    public boolean isAlertDisplayed(String text) {
        try {
            List<WebElement> alerts = driver.findElements(ALERTS_HOLDER);
            for (WebElement alert : alerts) {
                if (alert.getText().contains(text)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public ProfilePage clickAlertButton(){
        WebElement element = driver.findElement(ALERTS_BUTTON);
        element.click();
        return this;
    }

    public ProfilePage clearGenderSelection() {
        WebElement selectElement = DriverFactory.getWait().until(ExpectedConditions.elementToBeClickable(GENDER_SELECT));
        Select select = new Select(selectElement);
        select.selectByIndex(0); // предполагается, что первый элемент дефолтный, но сейчас в форме не реализовано, по умолчанию женский гендер
        return this;
    }
}
