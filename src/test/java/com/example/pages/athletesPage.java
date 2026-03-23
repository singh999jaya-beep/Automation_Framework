package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.example.hooks.Hooks.driver;

public class athletesPage {

    private final WebDriverWait wait;

    public static final String ATHLETES_LIST_URL = "https://staging-app.safeteam.io/panel/athletes";
    public static final String ADD_ATHLETE_URL = "https://staging-app.safeteam.io/panel/athletes/add-athlete";

    public athletesPage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    /* ---------- Locators ---------- */

    private final By addAthletesButton = By.xpath(
            "//button[@routerlink='/panel/athletes/add-athlete']");
    private final By firstNameInput = By.xpath(
            "//input[@formcontrolname='firstName'] | " +
            "//input[@formcontrolname='first_name'] | " +
            "//form//mat-form-field[.//*[contains(text(),'First Name') or contains(text(),'First name')]]//input | " +
            "(//div[contains(@class,'mat-form-field-infix')]//input)[1]");
    private final By lastNameInput = By.xpath(
            "//input[@formcontrolname='lastName'] | " +
            "//input[@formcontrolname='last_name'] | " +
            "//form//mat-form-field[.//*[contains(text(),'Last Name') or contains(text(),'Last name')]]//input | " +
            "(//div[contains(@class,'mat-form-field-infix')]//input)[2]");
    private final By calendarInput = By.xpath(
            "(//*[name()='path'][@fill='currentColor'])[5]");
    private final By dateInput = By.xpath(
            "//div[contains(@class,'mat-calendar-body-cell-content') and contains(@class,'mat-calendar-body-today')]");
    private final By genderDropdown = By.xpath(
            "//mat-select[@formcontrolname='gender'] | //mat-select | //mat-label[contains(text(),'Gender')]/ancestor::mat-form-field//mat-select | (//div[contains(@class,'mat-form-field-infix') and .//mat-select])[1]");
    private final By selectGender = By.xpath(
            "//mat-option[contains(.,'Female')] | //span[normalize-space()='Female']");
    private final By emailInput = By.xpath(
            "//input[@formcontrolname='email'] | " +
            "//input[@type='email'] | " +
            "//form//mat-form-field[.//*[contains(text(),'Email') or contains(text(),'email')]]//input");
    private final By mobileInput = By.xpath(
            "//input[contains(@formcontrolname,'phone') or contains(@formcontrolname,'Phone') or " +
            "contains(@formcontrolname,'mobile') or contains(@formcontrolname,'Mobile')] | " +
            "//input[contains(@placeholder,'Phone') or contains(@placeholder,'Mobile')] | " +
            "//mat-label[contains(text(),'Phone') or contains(text(),'Mobile')]/ancestor::mat-form-field//input | " +
            "(//div[contains(@class,'mat-form-field-infix')]//input)[5]");
    private final By saveOrAddAthleteButton = By.xpath(
            "//button[contains(.,'Save') or contains(.,'Add Athlete')]");
    private final By athleteListOrSuccess = By.xpath(
            "//div[contains(@class,'success') or contains(.,'success') or contains(@class,'toast')] | //table");
    private  final By scrollHousehold = By.xpath("(//div[@data-testid='athleteContainer'])[1]");
    private final By clickAddHousehold = By.xpath("(//*[name()='svg'][@class='svg-inline--fa fa-circle-plus'])[1]");

    /* ---------- Extra Locators for Household and Edit ---------- */
    private final By householdFirstNameInput = By.xpath("(//input[contains(translate(@formcontrolname,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'first')] | //mat-form-field[.//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'first name')]]//input)[last()]");
    private final By householdLastNameInput = By.xpath("(//input[contains(translate(@formcontrolname,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'last')] | //mat-form-field[.//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'last name')]]//input)[last()]");
    private final By householdPhoneInput = By.xpath("(//input[contains(translate(@formcontrolname,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'phone') or contains(translate(@formcontrolname,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'mobile')] | //input[@type='tel'] | //mat-form-field[.//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'phone') or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'mobile')]]//input)[last()]");
    private final By householdEmailInput = By.xpath("(//input[contains(translate(@formcontrolname,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'email')] | //input[@type='email'] | //mat-form-field[.//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'email')]]//input)[last()]");
    private final By householdRelationshipDropdown = By.xpath("(//mat-select[contains(translate(@formcontrolname,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'relation')] | //mat-form-field[.//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'relation')]]//mat-select)[last()]");
    private final By relationshipOption = By.xpath("(//span[normalize-space()='parent/guardian'])[1]");
    private final By continueBtn = By.xpath("(//button[@type='submit'])[1]");

    private final By editAthleteBtn = By.xpath("//tbody/tr[1]/td[11]/div[1]/app-table-action[2]/button[1]/span[1]/fa-icon[1]//*[name()='svg']");
    private final By submitEditBtn = By.xpath("//button[@class='mat-focus-indicator custom-btn solid-primary mat-flat-button mat-button-base cdk-focused cdk-mouse-focused']");
    /* ---------- Navigation ---------- */

    public void goToAthletes() {
        driver.get(ATHLETES_LIST_URL);
        wait.until(ExpectedConditions.urlContains("athletes"));
    }

    /* ---------- Actions ---------- */

    public void clickAddAthletes() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(addAthletesButton));
            scrollAndClick(btn);
        } catch (Exception e) {
            driver.get(ADD_ATHLETE_URL);
            wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput));
        }
    }

    public void enterFirstName(String firstName) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput));
        scrollAndSendKeys(input, firstName);
    }

    public void enterLastName(String lastName) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(lastNameInput));
        scrollAndSendKeys(input, lastName);
    }

    public void clickCalendarAndSelectDate() {
        WebElement cal = wait.until(ExpectedConditions.elementToBeClickable(calendarInput));
        scrollAndClick(cal);
        WebElement dateCell = wait.until(ExpectedConditions.elementToBeClickable(dateInput));
        scrollAndClick(dateCell);
    }

    public void clickGenderDropdownAndSelect() {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(genderDropdown));
        scrollAndClick(dropdown);
        WebElement gender = wait.until(ExpectedConditions.elementToBeClickable(selectGender));
        scrollAndClick(gender);

    }

    public void enterEmail(String email) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        scrollAndSendKeys(input, email);
    }

    public void enterMobileNumber(String mobile) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(mobileInput));
        scrollAndSendKeys(input, mobile);
    }

    public void clickSaveOrAddAthleteButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveOrAddAthleteButton));
        scrollAndClick(btn);
    }

    public void verifyAthleteAdded() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("athletes"),
                ExpectedConditions.visibilityOfElementLocated(athleteListOrSuccess)
        ));
    }

    private void scrollAndClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        clickViaScript(element);
    }

    private void scrollAndSendKeys(WebElement element, String text) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        try {
            element.click();
        } catch (Exception e) {
            clickViaScript(element);
        }
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), text);
    }

    private void clickViaScript(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }));",
                element);
    }
    public void scrollDownHousehold(){
        WebElement household = wait.until(ExpectedConditions.elementToBeClickable(scrollHousehold));
        scrollAndClick(household);
    }

    public void addHousehold() {
        WebElement household = wait.until(ExpectedConditions.elementToBeClickable(clickAddHousehold));
        scrollAndClick(household);
    }

    public void enterHouseholdNameAndPhone(String firstName, String lastName, String phone) {
        WebElement fName = wait.until(ExpectedConditions.visibilityOfElementLocated(householdFirstNameInput));
        scrollAndSendKeys(fName, firstName);
        
        WebElement lName = wait.until(ExpectedConditions.visibilityOfElementLocated(householdLastNameInput));
        scrollAndSendKeys(lName, lastName);
        
        WebElement ph = wait.until(ExpectedConditions.visibilityOfElementLocated(householdPhoneInput));
        scrollAndSendKeys(ph, phone);
    }

    public void enterHouseholdEmailAndRelationship(String email, String relationship) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(householdEmailInput));
        scrollAndSendKeys(el, email);

        try {
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(householdRelationshipDropdown));
            scrollAndClick(dropdown);
            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(relationshipOption));
            scrollAndClick(option);
        } catch(Exception e) {
            System.out.println("Relationship dropdown not handled perfectly");
        }
    }

    public void clickContinue() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(continueBtn));
        scrollAndClick(btn);
    }

    public void clickEditAthlete() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(editAthleteBtn));
        scrollAndClick(btn);
    }

    public void editAthleteName(String newName) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput));
        scrollAndSendKeys(input, newName);
    }

    public void scrollDownAthleteTab() {
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500);");
    }

    public void editAthleteEmail(String newEmail) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        scrollAndSendKeys(input, newEmail);
    }

    public void clickSubmitAthleteEdit() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(submitEditBtn));
        scrollAndClick(btn);
    }

    public void verifyAthleteEditSuccess() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(athleteListOrSuccess),
                ExpectedConditions.urlContains("athletes")
        ));
    }
}
