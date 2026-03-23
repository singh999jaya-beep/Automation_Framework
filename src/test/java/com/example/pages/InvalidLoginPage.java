package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class InvalidLoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Locator for login page heading
    private final By loginHeading = By.xpath("//h1[normalize-space()='Signin To Your SafeTeam Account']");

    // Constructor
    public InvalidLoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // Method to verify login page is displayed
    public boolean isLoginPageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(loginHeading));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}