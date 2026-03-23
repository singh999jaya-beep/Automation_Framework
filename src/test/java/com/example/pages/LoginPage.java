package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage extends BasePage {

    @FindBy(xpath = "//input[@formcontrolname='email']")
    private WebElement usernameInput;

    @FindBy(xpath = "//input[@formcontrolname='password']")
    private WebElement passwordInput;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement loginButton;

    @FindBy(xpath = "//snack-bar-container//span")
    private WebElement toastMessage;

    @FindBy(xpath="//img[@alt='SafeTeam logo']")
    private WebElement dashboard;

    @FindBy(xpath = "//div[@id='toast-container']")
    private WebElement invalidCredentialError;

    @FindBy(xpath="//error-message[1]//div[1]//span[1]")
    private WebElement blankCredentialError;

    @FindBy(xpath="//a[normalize-space()='Forgot Password?']")
    private WebElement forgotPassword;



    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void enterUsername(String username) {
        sendKeys(usernameInput, username);
    }

    public void enterPassword(String password) {
        sendKeys(passwordInput, password);
    }

    public void clickLogin() {
        click(loginButton);
    }

    public boolean isHomeLogo(){
        return isDisplayed(dashboard);
    }


    public boolean isToastMessageDisplayed() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        return wait.until(ExpectedConditions.visibilityOf(toastMessage)).isDisplayed();
    }




    public boolean isInvalidCredentialError(){
        return isDisplayed(invalidCredentialError);
    }

    public boolean isBlankCredentialError(){
        return isDisplayed(blankCredentialError);
    }

    public void clickForgotPassword(){
        click(forgotPassword);
    }





}