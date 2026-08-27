package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.function.Function;

public class LoginPage {

    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(20);
    private static final Duration DASHBOARD_WAIT = Duration.ofSeconds(45);
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int MAX_HOME_CHECKS = 5;
    private static final String HOME_PAGE_TEXT = "Individual Users";
    private static final Path HOME_SCREENSHOT = Paths.get(
            System.getProperty("user.dir"), "target", "screenshots", "home_page.png");

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final Keys SELECT_ALL_MODIFIER =
            System.getProperty("os.name", "").toLowerCase().contains("mac")
                    ? Keys.COMMAND
                    : Keys.CONTROL;

    private final By emailInput = By.xpath(
            "//input[contains(@aria-label,'Enter Email') or contains(@aria-label,'Email')]");
    private final By passwordInput = By.xpath(
            "//input[@type='password' and (contains(@aria-label,'Enter Password') or contains(@aria-label,'Password'))]");
    private final By loginButton = By.xpath(
            "//flt-semantics[@role='button' and @flt-tappable and contains(., 'Log In')]");
    private final By forgotPasswordButton = By.xpath(
            "//flt-semantics[@role='button' and contains(., 'Forgot Password')]");
    private final By confirmButton = By.xpath(
            "//flt-semantics[@role='button' and (contains(., 'Confirm') or contains(., 'Submit') or contains(., 'Send'))]");
    private final By homePageTitle = By.xpath(
            "//flt-semantics[contains(normalize-space(.), '" + HOME_PAGE_TEXT + "')]");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_WAIT);
    }

    public void enterUsername(String email) {
        enableFlutterSemantics();
        typeIntoInput(emailInput, email);
    }

    public void enterPassword(String password) {
        enableFlutterSemantics();
        typeIntoInput(passwordInput, password);
    }

    public void enterCredentials(String email, String password) {
        enableFlutterSemanticsSafely();
        typeIntoInput(emailInput, email);
        pause(250);
        typeIntoInput(passwordInput, password);
    }

    public void clickLogin() {
        for (int attempt = 0; attempt < MAX_LOGIN_ATTEMPTS; attempt++) {
            if (!isSessionAlive()) {
                return;
            }
            if (isDashboardLoaded(driver)) {
                return;
            }
            submitLoginForm();
            if (waitUntil(d -> isDashboardLoaded(d), DASHBOARD_WAIT)) {
                return;
            }
            pause(1500);
        }
    }

    public void clickForgotPassword() {
        clickSemanticsButton(forgotPasswordButton);
    }

    public void enterEmailOnReset(String email) {
        typeIntoInput(emailInput, email);
    }

    public void clickConfirmButton() {
        clickSemanticsButton(confirmButton);
    }

    public boolean isLoginPageDisplayed() {
        return waitForSemanticsText(
                text -> text.contains("Log In") && text.contains("Forgot Password"),
                DEFAULT_WAIT
        );
    }

    public boolean isPasswordResetConfirmationDisplayed() {
        return waitForSemanticsText(
                text -> text.contains("Check Mail") || text.contains("reset password link"),
                DEFAULT_WAIT
        );
    }

    public boolean isHomeLogoDisplayed() {
        for (int attempt = 0; attempt < MAX_HOME_CHECKS; attempt++) {
            if (!isSessionAlive()) {
                return false;
            }
            enableFlutterSemanticsSafely();
            if (waitUntil(d -> isDashboardLoaded(d), Duration.ofSeconds(20))) {
                captureHomeScreenshot();
                return true;
            }
            if (isLoginFormVisible()) {
                submitLoginForm();
            }
            pause(2000);
        }
        return false;
    }

    private void typeIntoInput(By locator, String value) {
        enableFlutterSemanticsSafely();
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
        element.click();
        element.sendKeys(Keys.chord(SELECT_ALL_MODIFIER, "a"));
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(value);
        String currentValue = element.getAttribute("value");
        if (currentValue == null || !currentValue.equals(value)) {
            setInputValueViaScript(element, value);
        }
    }

    private void setInputValueViaScript(WebElement element, String value) {
        ((JavascriptExecutor) driver).executeScript(
                "const input = arguments[0];" +
                        "const val = arguments[1];" +
                        "input.focus();" +
                        "input.value = val;" +
                        "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                        "input.dispatchEvent(new Event('change', { bubbles: true }));",
                element,
                value
        );
    }

    private void clickSemanticsButton(By locator) {
        enableFlutterSemanticsSafely();
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        new Actions(driver).moveToElement(element).click().perform();
    }

    private void submitLoginForm() {
        enableFlutterSemanticsSafely();
        if (isPresent(loginButton)) {
            clickSemanticsButton(loginButton);
            return;
        }
        if (isPresent(passwordInput)) {
            driver.findElement(passwordInput).sendKeys(Keys.ENTER);
        }
    }

    private boolean isLoginFormVisible() {
        return isPresent(emailInput) && isPresent(passwordInput);
    }

    private boolean isSessionAlive() {
        try {
            return driver != null && !driver.getWindowHandles().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isDashboardLoaded(WebDriver webDriver) {
        enableFlutterSemanticsOn(webDriver);
        if (!webDriver.findElements(homePageTitle).isEmpty()) {
            return true;
        }
        String text = getSemanticsText(webDriver);
        return text != null
                && (text.contains(HOME_PAGE_TEXT)
                || text.contains("Company Users")
                || text.contains("Navigation menu")
                || text.contains("user_search_field")
                || text.contains("company_search_field")
                || text.contains("drawer_item_individual_users")
                || text.contains("drawer_item_company_users"));
    }

    private boolean waitForSemanticsText(Function<String, Boolean> condition, Duration timeout) {
        return waitUntil(driver -> {
            enableFlutterSemanticsOn(driver);
            String text = getSemanticsText(driver);
            return text != null && Boolean.TRUE.equals(condition.apply(text));
        }, timeout);
    }

    private boolean waitUntil(Function<WebDriver, Boolean> condition, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout).until(condition::apply);
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    private boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    private void captureHomeScreenshot() {
        try {
            Files.createDirectories(HOME_SCREENSHOT.getParent());
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), HOME_SCREENSHOT, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.err.println("Failed to capture home screenshot: " + e.getMessage());
        }
    }

    private void enableFlutterSemantics() {
        enableFlutterSemanticsSafely();
    }

    private void enableFlutterSemanticsSafely() {
        enableFlutterSemanticsOn(driver);
    }

    private void enableFlutterSemanticsOn(WebDriver webDriver) {
        try {
            ((JavascriptExecutor) webDriver).executeScript(
                    "const p = document.querySelector('flt-semantics-placeholder');" +
                            "if (p) p.click();"
            );
        } catch (org.openqa.selenium.NoSuchWindowException
                 | org.openqa.selenium.NoSuchSessionException ignored) {
            // Browser session already closed.
        }
    }

    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String getSemanticsText() {
        return getSemanticsText(driver);
    }

    private String getSemanticsText(WebDriver webDriver) {
        return (String) ((JavascriptExecutor) webDriver).executeScript(
                "const host = document.querySelector('flt-semantics-host');" +
                        "return host ? host.textContent : '';"
        );
    }
}
