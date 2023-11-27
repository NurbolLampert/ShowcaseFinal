import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StructureTest {
    private WebDriver driver;
    private static final String BASE_URL = "https://www.dannycophotography.com";

    @BeforeEach
    public void setup() {
        driver = WebDriverManager.chromedriver().create();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void printHtmlStructureTest() {
        driver.get(BASE_URL);
        String pageSource = driver.getPageSource();

        // Output the HTML structure to the console or perform other actions
        System.out.println(pageSource);

        // You can also add assertions here if needed
    }
}
