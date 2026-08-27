package com.example.hooks;


import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Hooks {

    private static final long STEP_WAIT_MS = 1000L;
    /** Pinned with Selenium 4.33 for headless CDP file-chooser support. */
    private static final String HEADLESS_CHROME_VERSION = "136";

    public static WebDriver driver;

    @AfterStep
    public void waitAfterEachStep() {
        try {
            Thread.sleep(STEP_WAIT_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Before
    public void setUp() {
        String chromeDriverPath = System.getenv("CHROMEDRIVER_PATH");
        String chromeBinary = System.getenv("CHROME_BINARY");
        // Headless needs Chrome 136: CDP (ads) + Flutter Block/row semantics (users tables).
        // CI always supplies CHROME_BINARY=136; locally pin via setBrowserVersion unless overridden.
        boolean usePinnedHeadlessChrome = isHeadless()
                && (chromeBinary == null || chromeBinary.isBlank());

        if (chromeDriverPath != null && !chromeDriverPath.isBlank()) {
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        } else if (!usePinnedHeadlessChrome) {
            // Headless + setBrowserVersion lets Selenium Manager resolve Chrome/Driver together.
            WebDriverManager.chromedriver().setup();
        }

        ChromeOptions options = new ChromeOptions();

        // Kill all proxy & offline configs
        options.addArguments("--no-proxy-server");
        options.addArguments("--proxy-server='direct://'");
        options.addArguments("--proxy-bypass-list=*");
        options.addArguments("--dns-prefetch-disable");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        // Force online mode
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.managed_default_content_settings.images", 1);
        prefs.put("network_prediction_options", 0);
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("--disable-features=NetworkService");
        options.addArguments("--remote-allow-origins=*");

        LoggingPreferences loggingPreferences = new LoggingPreferences();
        loggingPreferences.enable(LogType.PERFORMANCE, Level.ALL);
        options.setCapability("goog:loggingPrefs", loggingPreferences);

        // Always set an explicit size. Avoid maximize() under CI/Xvfb: with Chrome 150 +
        // Selenium 4.33, maximizeCurrentWindow fails via CDP (`Runtime.evaluate` missing).
        options.addArguments("--window-size=1920,1080");
        if (isHeadless()) {
            options.addArguments("--headless=new");
            // Chrome 150 has no Selenium 4.33 CDP module; pin Chrome for Testing 136 for uploads.
            if (usePinnedHeadlessChrome) {
                options.setBrowserVersion(HEADLESS_CHROME_VERSION);
            }
        }

        if (chromeBinary != null && !chromeBinary.isBlank()) {
            options.setBinary(chromeBinary);
        }

        driver = new ChromeDriver(options);
        if (!isHeadless() && !isContinuousIntegration()) {
            try {
                driver.manage().window().maximize();
            } catch (Exception ignored) {
                // Fall back to the window-size Chrome argument above.
            }
        }
    }

    private static boolean isHeadless() {
        if ("false".equalsIgnoreCase(System.getenv("HEADLESS"))) {
            return false;
        }
        return "true".equalsIgnoreCase(System.getenv("CI"))
                || "true".equalsIgnoreCase(System.getenv("HEADLESS"));
    }

    private static boolean isContinuousIntegration() {
        return "true".equalsIgnoreCase(System.getenv("CI"));
    }

    @After(order = 0)
    public void attachScreenshotOnFailure(Scenario scenario) {
        if (scenario.isFailed() && driver != null) {
            try {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("Screenshot on failure", "image/png", new ByteArrayInputStream(screenshot), "png");
            } catch (Exception e) {
                // Session may already be closed; skip screenshot to avoid NoSuchSessionException
            }
        }
    }

    @After(order = 1)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
