import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DannyCoPhotographyTests {
    private WebDriver driver;

    public static class TestCase {
        String testId;
        String startingUrl;
        String buttonXpath;
        String expectedUrl;
        boolean inTab;
        String tabXpath;

        public TestCase(String testId, String startingUrl, String buttonXpath, String expectedUrl, boolean inTab, String tabXpath) {
            this.testId = testId;
            this.startingUrl = startingUrl;
            this.buttonXpath = buttonXpath;
            this.expectedUrl = expectedUrl;
            this.inTab = inTab;
            this.tabXpath = tabXpath;
        }

        @Override
        public String toString() {
            return testId;
        }
    }

    public static Stream<TestCase> testCasesProvider() throws IOException {
        List<TestCase> testCases = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("/Users/nurbolduisenbek/Desktop/ALL SCHOOL/Fall 2023/CS3250/ShowcaseFinal/src/test/java/ALL93TestCases.csv"))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] values = line.split(",");
                boolean inTab = determineIfInTab(values[3]);
                String tabXpath = inTab ? getTabXpath(values[3]) : null;
                testCases.add(new TestCase(values[0], values[2], convertButtonToXpath(values[3], inTab), values[5], inTab, tabXpath));
            }
        }
        return testCases.stream();
    }

    private static boolean determineIfInTab(String buttonUsed) {
        return buttonUsed.equals("Experience") || buttonUsed.equals("Pricing") ||
                buttonUsed.equals("Portrait") || buttonUsed.equals("Event") ||
                buttonUsed.equals("Travel");
    }

    private static String getTabXpath(String buttonUsed) {
        if (buttonUsed.equals("Experience") || buttonUsed.equals("Pricing")) {
            return "//span[contains(text(),'Info')]";
        } else if (buttonUsed.equals("Portrait") || buttonUsed.equals("Event") || buttonUsed.equals("Travel")) {
            return "//span[contains(text(),'Portfolio')]";
        }
        return null;
    }

    private static String convertButtonToXpath(String buttonUsed, boolean inTab) {
        if (inTab) {
            // Logic for in-tab buttons
            switch (buttonUsed.toLowerCase()) {
                case "experience":
                    return "//a[@href='/experience/']";
                case "pricing":
                    return "//a[@href='/pricing/']";
                case "portrait":
                    return "//a[contains(@href, '/portrait/')]";
                case "event":
                case "events":
                    return "//a[contains(@href, '/events/')]";
                case "travel":
                    return "//a[contains(@href, '/travel/')]";
                case "portfolio":
                    return "//span[contains(text(),'Portfolio')]";
                // Add other in-tab cases as needed
            }
        } else {
            // Logic for out-of-tab buttons
            switch (buttonUsed.toLowerCase()) {
                case "home":
                case "take me home":
                    return "//a[@href='/']";
                case "find out my experience":
                    return "//h4[contains(text(),'experience')]/ancestor::div[contains(@class, 'feature-links-text__content-wrapper')]";
                case "let us connect":
                case "contact me":
                    return "//h4[contains(text(),'Contact me') or contains(text(), 'Let's Connect')]/ancestor::a";
                case "view my portfolio":
                case "check out my portfolio":
                    return "//h4[contains(text(),'Portfolio')]/ancestor::a";
                case "find out more about me":
                    return "//h4[contains(text(),'About Me')]/ancestor::div[contains(@class, 'feature-links-text__content-wrapper')]";
                case "learn about pricing":
                    return "//h4[contains(text(),'Pricing')]/ancestor::div[contains(@class, 'feature-links-text__content-wrapper')]";
                // Add other out-of-tab cases as needed
            }
        }
        return "//a[contains(text(),'" + buttonUsed + "')]"; // Fallback for unexpected cases
    }

    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    @ParameterizedTest(name = "{0}")
    @MethodSource("testCasesProvider")
    public void runTest(TestCase testCase) {
        System.out.println("Running test: " + testCase.testId);
        System.out.println("Starting URL: " + testCase.startingUrl);
        System.out.println("Button XPath: " + testCase.buttonXpath);
        System.out.println("Expected URL: " + testCase.expectedUrl);
        System.out.println("Is In Tab: " + testCase.inTab);
        if (testCase.inTab) {
            System.out.println("Tab XPath: " + testCase.tabXpath);
        }
        driver.get(testCase.startingUrl);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Actions actions = new Actions(driver);

        if (testCase.inTab) {
            WebElement tab = driver.findElement(By.xpath(testCase.tabXpath));
            // Hover over the tab to expand the dropdown (if necessary)
            actions.moveToElement(tab).perform();
            // Wait for the dropdown to be visible and for the specific item to be clickable
            WebElement dropdownItem = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(testCase.buttonXpath)));
            dropdownItem.click();
        } else {
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(testCase.buttonXpath)));
            button.click();
        }

        assertEquals(testCase.expectedUrl, driver.getCurrentUrl(), "URL mismatch for test: " + testCase.testId);
    }
}
