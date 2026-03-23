package com.example.hooks;


import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class Hooks {

    public static WebDriver driver;

    @Before
    public void setUp() {

        ChromeOptions options = new ChromeOptions();

        // Kill all proxy & offline configs
        options.addArguments("--no-proxy-server");
        options.addArguments("--proxy-server='direct://'");
        options.addArguments("--proxy-bypass-list=*");
        options.addArguments("--dns-prefetch-disable");
        options.addArguments("--no-sandbox");

        // Force online mode
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.managed_default_content_settings.images", 1);
        prefs.put("network_prediction_options", 0);
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("--disable-features=NetworkService");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
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

