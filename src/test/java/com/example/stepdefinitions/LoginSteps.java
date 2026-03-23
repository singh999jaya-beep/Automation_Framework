package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.InvalidLoginPage;
import com.example.pages.LoginPage;
import io.cucumber.java.en.*;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginSteps {

    private final WebDriver driver = Hooks.driver;
    private final LoginPage loginPage = new LoginPage(driver);   // <-- create manually

    @Given("user is on the login page")
    public void user_is_on_the_login_page() {
        driver.get("https://pre-prod-app.safeteam.io/auth/login");
    }

    @When("user enters email {string} and password {string}")
    public void user_enters_email_and_password(String email, String password) {
        loginPage.enterUsername(email);
        loginPage.enterPassword(password);
    }

    @And("user clicks on the login button")
    public void user_clicks_on_the_login_button() {
        loginPage.clickLogin();
    }

    @Then("user is navigated to the home page")
    public void user_is_navigated_to_the_home_page() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("dashboard"),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-dashboard"))
        ));
        Assert.assertTrue(driver.getCurrentUrl().contains("dashboard"));
    }



}
