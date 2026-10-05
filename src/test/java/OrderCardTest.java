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

        driver = new ChromeDriver(options);
        driver.get("http://localhost:9999");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ЗАДАЧА 1: Happy path
    @Test
    public void shouldSendFormSuccessfully() {
        WebElement nameField = driver.findElement(By.xpath("//input[@type='text']"));
        nameField.sendKeys("Иванов Иван");

        WebElement phoneField = driver.findElement(By.xpath("//input[@type='tel']"));
        phoneField.sendKeys("+79999999999");

        WebElement checkbox = driver.findElement(By.xpath("//span[@class='checkbox__box']"));
        checkbox.click();

        WebElement submitButton = driver.findElement(By.xpath("//button[contains(., 'Продолжить')]"));
        submitButton.click();

        WebElement successMessage = driver.findElement(By.xpath("//*[contains(text(), 'Ваша заявка успешно отправлена')]"));
        assertTrue(successMessage.isDisplayed(), "Сообщение об успешной отправке не появилось!");
    }

    // ЗАДАЧА 2: Проверка валидации
    @Test
    public void shouldShowErrorForInvalidName() {
        WebElement nameField = driver.findElement(By.xpath("//input[@type='text']"));
        nameField.sendKeys("Ivan Petrov");

        WebElement phoneField = driver.findElement(By.xpath("//input[@type='tel']"));
        phoneField.sendKeys("+79999999999");

        WebElement checkbox = driver.findElement(By.xpath("//span[@class='checkbox__box']"));
        checkbox.click();

        WebElement submitButton = driver.findElement(By.xpath("//button[contains(., 'Продолжить')]"));
        submitButton.click();

        // Проверяем сообщение об ошибке
        WebElement errorMessage = driver.findElement(By.xpath("//*[contains(text(), 'Допустимы только русские буквы')]"));
        assertTrue(errorMessage.isDisplayed(), "Сообщение об ошибке не появилось!");

        // Проверяем, что появился элемент с классом input_invalid (подсветка)
        WebElement invalidField = driver.findElement(By.xpath("//*[contains(@class, 'input_invalid')]"));
        assertTrue(invalidField.isDisplayed(), "Поле не подсвечено красным!");
    }
}