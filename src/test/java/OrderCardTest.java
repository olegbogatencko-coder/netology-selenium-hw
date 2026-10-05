import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

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
                By.xpath("//*[contains(text(), 'успешно отправлена')]"));
        assertTrue(successMessage.isDisplayed(), "Сообщение об успешной отправке не появилось!");
    }

    // ===== ЗАДАЧА 2: Валидация =====

    // 1. Невалидное имя (латиница)
    @Test
    public void shouldShowErrorForInvalidName() {
        fillName("Ivan Petrov");
        fillPhone("+79999999999");
        clickAgreementCheckbox();
        clickSubmit();

        // Сообщение об ошибке — ищем .input__sub внутри блока имени
        WebElement errorMessage = driver.findElement(
                By.cssSelector("[data-test-id='name'] .input__sub"));
        assertTrue(errorMessage.isDisplayed(), "Сообщение об ошибке не появилось!");

        // Подсветка — ищем любой элемент с классом input_invalid (глобально)
        WebElement invalidField = driver.findElement(By.cssSelector(".input_invalid"));
        assertTrue(invalidField.isDisplayed(), "Поле не подсвечено красным!");
    }

    // 2. Невалидный телефон (короткий)
    @Test
    public void shouldShowErrorForInvalidPhone() {
        fillName("Иванов Иван");
        fillPhone("+7999");
        clickAgreementCheckbox();
        clickSubmit();

        WebElement errorMessage = driver.findElement(
                By.cssSelector("[data-test-id='phone'] .input__sub"));
        assertTrue(errorMessage.isDisplayed(), "Сообщение об ошибке не появилось!");

        WebElement invalidField = driver.findElement(By.cssSelector(".input_invalid"));
        assertTrue(invalidField.isDisplayed(), "Поле не подсвечено красным!");
    }

    // 3. Пустое имя
    @Test
    public void shouldShowErrorForEmptyName() {
        fillPhone("+79999999999");
        clickAgreementCheckbox();
        clickSubmit();

        WebElement errorMessage = driver.findElement(
                By.cssSelector("[data-test-id='name'] .input__sub"));
        assertTrue(errorMessage.isDisplayed(), "Сообщение об ошибке не появилось!");

        WebElement invalidField = driver.findElement(By.cssSelector(".input_invalid"));
        assertTrue(invalidField.isDisplayed(), "Поле не подсвечено красным!");
    }

    // 4. Пустой телефон
    @Test
    public void shouldShowErrorForEmptyPhone() {
        fillName("Иванов Иван");
        clickAgreementCheckbox();
        clickSubmit();

        WebElement errorMessage = driver.findElement(
                By.cssSelector("[data-test-id='phone'] .input__sub"));
        assertTrue(errorMessage.isDisplayed(), "Сообщение об ошибке не появилось!");

        WebElement invalidField = driver.findElement(By.cssSelector(".input_invalid"));
        assertTrue(invalidField.isDisplayed(), "Поле не подсвечено красным!");
    }

    // 5. Не отмечен чекбокс согласия
    @Test
    public void shouldShowErrorForUncheckedAgreement() {
        fillName("Иванов Иван");
        fillPhone("+79999999999");
        // Чекбокс НЕ ставим
        clickSubmit();

        // Ищем любой элемент с классом input_invalid
        WebElement invalidAgreement = driver.findElement(By.cssSelector(".input_invalid"));
        assertTrue(invalidAgreement.isDisplayed(), "Чекбокс не подсвечен красным!");
    }
}