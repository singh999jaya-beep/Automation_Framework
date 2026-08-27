package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.LoginPage;
import com.example.utils.TestConfig;
import io.cucumber.java.en.*;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class LoginSteps {

    private static final String DEFAULT_TEST_EMAIL = "viadmin@gmail.com";
    private static final String DEFAULT_TEST_PASSWORD = "Virtualintros@30026";

    public static final java.lang.String USER_IS_NAVIGATED_TO_THE_HOME_PAGE = "user is navigated to the home page";
    private final WebDriver driver = Hooks.driver;
    private final LoginPage loginPage = new LoginPage(driver);

    @Given("user is on the login page")
    public void user_is_on_the_login_page() {
        for (int attempt = 0; attempt < 3; attempt++) {
            driver.get(TestConfig.baseUrl());
            if (enableFlutterSemanticsOnLoginPage()) {
                return;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private boolean enableFlutterSemanticsOnLoginPage() {
        try {
            org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(
                    driver, java.time.Duration.ofSeconds(30));
            wait.until(org.openqa.selenium.support.ui.ExpectedConditions.or(
                    org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                            org.openqa.selenium.By.tagName("flt-semantics-placeholder")),
                    org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                            org.openqa.selenium.By.tagName("flt-semantics-host")),
                    org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                            org.openqa.selenium.By.cssSelector("input[aria-label*='Email']"))
            ));
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            js.executeScript(
                    "const p = document.querySelector('flt-semantics-placeholder');" +
                            "if (p) p.click();");
            Thread.sleep(1500);
            return loginPage.isLoginPageDisplayed();
        } catch (Exception e) {
            System.err.println("Could not enable Flutter Web semantics: " + e.getMessage());
            return false;
        }
    }

    @When("user enters email {string} and password {string}")
    public void user_enters_email_and_password(String email, String password) {
        loginPage.enterCredentials(resolveEmail(email), resolvePassword(password));
    }

    @And("user clicks on the login button")
    @And("user click on the login button")
    public void user_clicks_on_the_login_button() {

        loginPage.clickLogin();
    }

    @Then(USER_IS_NAVIGATED_TO_THE_HOME_PAGE)
    public void user_is_navigated_to_the_home_page() {

        Assert.assertTrue(
                "Home page is not displayed",
                loginPage.isHomeLogoDisplayed()
        );
    }

    @When("user click on password reset")
    public void user_click_on_password_reset() {

        loginPage.clickForgotPassword();
    }

    @And("user enter registered email address {string}")
    public void user_enter_registered_email_address(String email) {
        loginPage.enterEmailOnReset(resolveEmail(email));
    }

    private static String resolveEmail(String email) {
        if (DEFAULT_TEST_EMAIL.equals(email)) {
            return TestConfig.testEmail();
        }
        return email;
    }

    private static String resolvePassword(String password) {
        if (DEFAULT_TEST_PASSWORD.equals(password)) {
            return TestConfig.testPassword();
        }
        return password;
    }

    @And("user click on confirm button")
    public void user_click_on_confirm_button() {
        loginPage.clickConfirmButton();
    }

    @Then("user remains in login page")
    public void user_remains_in_login_page() {

        Assert.assertTrue(
                "Login page is not displayed",
                loginPage.isLoginPageDisplayed()
        );
    }

    @Then("user sees password reset confirmation message")
    public void user_sees_password_reset_confirmation_message() {

        Assert.assertTrue(
                "Password reset confirmation is not displayed",
                loginPage.isPasswordResetConfirmationDisplayed()
        );
    }
}
