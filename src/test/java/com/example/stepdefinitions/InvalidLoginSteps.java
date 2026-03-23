package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.InvalidLoginPage;
import io.cucumber.java.en.Then;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class InvalidLoginSteps {

    private final WebDriver driver = Hooks.driver;
    private final InvalidLoginPage invalidLoginPage = new InvalidLoginPage(driver);

    @Then("user in  login page")
    public void userInLoginPage() {
        Assert.assertTrue("User is not on login page", invalidLoginPage.isLoginPageDisplayed());
    }
}
