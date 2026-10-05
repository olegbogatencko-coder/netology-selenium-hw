import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderCardTest {
    private WebDriver driver;

    @BeforeAll
    public static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.get("http://localhost:9999");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ===== Вспомогательные методы =====
    private void fillName(String value) {
        WebElement input = driver.findElement(By.cssSelector("[data-test-id='name'] input"));
        input.clear();
        input.sendKeys(value);
    }

    private void fillPhone(String value) {
        WebElement input = driver.findElement(By.cssSelector("[data-test-id='phone'] input"));
        input.clear();
        input.sendKeys(value);
    }

    private void clickAgreementCheckbox() {
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
    }

    private void clickSubmit() {
        driver.findElement(By.xpath("//button[contains(., 'Продолжить')]")).click();
    }

    // ===== ЗАДАЧА 1: Happy path =====
    @Test
    public void shouldSendFormSuccessfully() {
        fillName("Иванов Иван");
        fillPhone("+79999999999");
        clickAgreementCheckbox();
        clickSubmit();

        WebElement successMessage = driver.findElement(
                By.cssSelector("[data-test-id='order-success']"));

        String expectedText = "Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.";
        String actualText = successMessage.getText().trim();
        assertEquals(expectedText, actualText, "Текст сообщения об успехе не совпадает!");
    }

    // ===== ЗАДАЧА 2: Валидация =====

    // 1. Невалидное имя (латиница)
    @Test
    public void shouldShowErrorForInvalidName() {
        fillName("Ivan Petrov");
        fillPhone("+79999999999");
        clickAgreementCheckbox();
        clickSubmit();

        WebElement errorMessage = driver.findElement(
                By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));
        assertTrue(errorMessage.isDisplayed(), "Сообщение об ошибке не отображается!");

        String expectedText = "Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.";
        assertEquals(expectedText, errorMessage.getText().trim(),
                "Текст сообщения об ошибке не совпадает!");
    }

    // 2. Невалидный телефон (короткий)
    @Test
    public void shouldShowErrorForInvalidPhone() {
        fillName("Иванов Иван");
        fillPhone("+7999");
        clickAgreementCheckbox();
        clickSubmit();

        WebElement errorMessage = driver.findElement(
                By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));
        assertTrue(errorMessage.isDisplayed(), "Сообщение об ошибке не отображается!");

        String expectedText = "Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.";
        assertEquals(expectedText, errorMessage.getText().trim(),
                "Текст сообщения об ошибке не совпадает!");
    }

    // 3. Пустое имя
    @Test
    public void shouldShowErrorForEmptyName() {
        fillPhone("+79999999999");
        clickAgreementCheckbox();
        clickSubmit();

        WebElement errorMessage = driver.findElement(
                By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));
        assertTrue(errorMessage.isDisplayed(), "Сообщение об ошибке не отображается!");

        String expectedText = "Поле обязательно для заполнения";
        assertEquals(expectedText, errorMessage.getText().trim(),
                "Текст сообщения об ошибке не совпадает!");
    }

    // 4. Пустой телефон
    @Test
    public void shouldShowErrorForEmptyPhone() {
        fillName("Иванов Иван");
        clickAgreementCheckbox();
        clickSubmit();

        WebElement errorMessage = driver.findElement(
                By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));
        assertTrue(errorMessage.isDisplayed(), "Сообщение об ошибке не отображается!");

        String expectedText = "Поле обязательно для заполнения";
        assertEquals(expectedText, errorMessage.getText().trim(),
                "Текст сообщения об ошибке не совпадает!");
    }

    // 5. Не отмечен чекбокс согласия
    // БАГ SUT: при неотмеченном чекбоксе не появляется сообщение об ошибке.
    // Issue: https://github.com/olegbogatencko-coder/netology-selenium-hw/issues/1
    @Disabled("Баг SUT: не отображается ошибка валидации чекбокса. См. issue #1")
    @Test
    public void shouldShowErrorForUncheckedAgreement() {
        fillName("Иванов Иван");
        fillPhone("+79999999999");
        // Чекбокс НЕ ставим
        clickSubmit();

        WebElement errorMessage = driver.findElement(
                By.cssSelector("[data-test-id='agreement'].input_invalid .input__sub"));
        assertTrue(errorMessage.isDisplayed(), "Сообщение об ошибке не отображается!");

        String expectedText = "Необходимо подтвердить согласие";
        assertEquals(expectedText, errorMessage.getText().trim(),
                "Текст сообщения об ошибке не совпадает!");
    }

    // 6. Только одно слово в поле "Фамилия и имя"
    // БАГ SUT: система принимает одно слово как валидное значение,
    // хотя по требованиям должно быть указано "Фамилия и имя" (два слова).
    // Issue: https://github.com/olegbogatencko-coder/netology-selenium-hw/issues/2
    @Disabled("Баг SUT: принимается одно слово вместо 'Фамилия и имя'. См. issue #2")
    @Test
    public void shouldShowErrorForSingleWordName() {
        fillName("Иванов");
        fillPhone("+79999999999");
        clickAgreementCheckbox();
        clickSubmit();

        WebElement errorMessage = driver.findElement(
                By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));
        assertTrue(errorMessage.isDisplayed(), "Сообщение об ошибке не отображается!");

        String expectedText = "Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.";
        assertEquals(expectedText, errorMessage.getText().trim(),
                "Текст сообщения об ошибке не совпадает!");
    }
}