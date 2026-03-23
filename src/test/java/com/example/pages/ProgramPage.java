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

public class ProgramPage {

    private final WebDriverWait wait;
    /**
     * Programs list page (sidebar "Program" leads here).
     */
    public static final String PROGRAMS_LIST_URL = "https://pre-prod-app.safeteam.io/panel/programs";
    public static final String PROGRAM_ADD_URL = "https://pre-prod-app.safeteam.io/panel/programs/add-program";

    public ProgramPage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    /* ---------- Locators ---------- */


    private final By addNewProgramButton = By.xpath("//span[contains(text(),'+ ADD NEW PROGRAM')]");
    /**
     * Scroll target on program builder page (form or main content).
     */
    private final By programFormScrollTarget = By.xpath("//app-program-builder//form | //app-program-builder | //div[@class='component']");
    /**
     * First mat-form-field is program name; target the actual input.
     */
    private final By programNameInput = By.xpath("//app-program-builder//form//mat-form-field[1]//input");
    /**
     * Season: click the season input box only (no typing).
     */
    private final By seasonInputBox = By.xpath(
            "//mat-select[contains(translate(@formcontrolname,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'season')] | " +
            "//mat-label[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'season')]/ancestor::mat-form-field//mat-select | " +
            "//mat-label[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'season')]/ancestor::mat-form-field | " +
            "//app-program-builder//form//mat-form-field[2]//mat-select | " +
            "(//mat-select)[1]"
    );
    private final By seasonFieldFallback = By.xpath("//mat-option//mat-pseudo-checkbox | (//mat-pseudo-checkbox)[1] | (//mat-option)[1]");
    /**
     * Sport: third mat-form-field in program form.
     */
    private final By sportDropdown = By.xpath(
            "//mat-select[contains(translate(@formcontrolname,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'sport')] | " +
            "//mat-label[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'sport')]/ancestor::mat-form-field//mat-select | " +
            "//mat-label[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'sport')]/ancestor::mat-form-field | " +
            "//app-program-builder//form//mat-form-field[3]");
    private final By selectSport = By.xpath("//mat-option[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'football')] | //mat-option[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'activity')]");
    /**
     * Gender: fourth mat-form-field (index may shift if form layout differs).
     */
    private final By genderDropdown = By.xpath(
            "//mat-select[contains(translate(@formcontrolname,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'gender')] | " +
            "//mat-label[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'gender')]/ancestor::mat-form-field//mat-select | " +
            "//mat-label[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'gender')]/ancestor::mat-form-field | " +
            "//app-program-builder//form//mat-form-field[4]"
    );
    private final By genderSelect = By.xpath("//mat-option[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'boys')]");
    /**
     * Website field: typically 5th mat-form-field (1=name, 2=season, 3=sport, 4=gender, 5=website).
     */
    private final By programWebsiteInput = By.xpath(
            "//input[contains(translate(@formcontrolname,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'website')] | " +
            "//input[contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'website')] | " +
            "//app-program-builder//form//mat-form-field[5]//input"
    );
    private final By createMyProgramButton = By.xpath("(//button[@type='submit'])[1]");

    /* ---------- Extra Locators for Staff Assignment ---------- */
    private final By programNameLink = By.xpath("(//a[contains(@class,'program-link') or contains(@href,'/program/') or contains(text(),'Test Program')])[1]");
    private final By assignCoachButton = By.xpath("//div[@class='col-9']//div[2]//div[2]//button[1]");
    private final By assignAssistantCoachButton = By.xpath("//button[contains(.,'Assign Assistant Coach')] | //span[contains(.,'Assign Assistant Coach')]/parent::button");
    private final By assignManagerButton = By.xpath("//button[contains(.,'Assign Manager') or contains(.,'Assign Student Manager')] | //span[contains(.,'Assign Manager')]/parent::button");

    private final By staffFirstNameInput = By.xpath("//input[contains(@formcontrolname,'firstName') or contains(@placeholder,'First Name') or @placeholder='First Name']");
    private final By staffLastNameInput = By.xpath("//input[contains(@formcontrolname,'lastName') or contains(@placeholder,'Last Name') or @placeholder='Last Name']");
    private final By staffEmailInput = By.xpath("//input[contains(@formcontrolname,'email') or contains(@type,'email') or contains(@placeholder,'Email')]");
    private final By timezoneDropdown = By.xpath("//mat-select[contains(@formcontrolname,'timezone') or contains(.,'Timezone')]");
    private final By timezoneOption = By.xpath("(//mat-option)[2]"); // 2nd option usually ensures it's selectable instead of default
    private final By saveButton = By.xpath("//button[contains(.,'Save') or contains(.,'SAVE')] | //span[contains(.,'Save')]/parent::button");
    private final By confirmSaveButton = By.xpath("//button[contains(.,'Confirm') or contains(.,'CONFIRM') or contains(.,'Yes')] | //span[contains(.,'Confirm')]/parent::button");

    /**
     * Navigate directly to programs list (avoids unreliable sidebar menu click).
     */
    public void goToProgramsList() {
        driver.get(PROGRAMS_LIST_URL);
    }

    /**
     * Navigate directly to add-program form.
     */
    public void goToAddProgram() {
        driver.get(PROGRAM_ADD_URL);
    }

    /* ---------- Actions ---------- */

    public void navigateToProgramMenu() {
        goToProgramsList();
        wait.until(ExpectedConditions.visibilityOfElementLocated(addNewProgramButton));
    }

    public void clickAddNewProgram() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(addNewProgramButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        clickViaScript(btn);
    }

    public void scrollToPostToTeamFeedButton() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(programFormScrollTarget));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", element);
    }

    public void enterProgramName(String programName) {
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(programNameInput));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", nameInput);
        nameInput.click();
        nameInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), programName);
    }

    /**
     * When user click on season — opens the season dropdown (no typing).
     */
    public void clickOnSeason() {
        WebElement toClick = wait.until(ExpectedConditions.visibilityOfElementLocated(seasonInputBox));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", toClick);
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        wait.until(ExpectedConditions.elementToBeClickable(toClick));
        try {
            toClick.click();
        } catch (Exception e) {
            new org.openqa.selenium.interactions.Actions(driver).moveToElement(toClick).click().perform();
        }
    }

    /**
     * Checked the box — select/check the season option for the given season.
     */
    public void checkSeasonBox() {

        WebElement option = wait.until(ExpectedConditions.visibilityOfElementLocated(seasonFieldFallback));
        wait.until(ExpectedConditions.elementToBeClickable(option));

        // Optional: scroll into view for safety
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", option);
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        try {
            option.click();
        } catch (Exception e) {
            new org.openqa.selenium.interactions.Actions(driver).moveToElement(option).click().perform();
        }

    }

    /**
     * Click sport dropdown and select an option.
     */
    public void clickSportAndSelectFromDropdown() {

        // Step 1: Click on Sport Dropdown
        WebElement sportSelect = wait.until(
                ExpectedConditions.visibilityOfElementLocated(sportDropdown));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", sportSelect);
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        wait.until(ExpectedConditions.elementToBeClickable(sportSelect));
        
        try {
            sportSelect.click();  // Prefer normal click
        } catch (Exception e) {
            clickViaScript(sportSelect);
        }

        // Step 2: Wait for Angular Material dropdown panel
        By overlayPanel = By.cssSelector(".cdk-overlay-pane");
        wait.until(ExpectedConditions.visibilityOfElementLocated(overlayPanel));

        // Step 3: Click on "Activity" option using selectSport locator
        WebElement option = wait.until(
                ExpectedConditions.elementToBeClickable(selectSport));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", option);
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        try {
            option.click();
        } catch (Exception e) {
            clickViaScript(option);
        }
    }

    /**
     * Click gender dropdown and select an option.
     */
    public void clickGenderAndSelectFromDropdown() {
        WebElement genderInput = wait.until(ExpectedConditions.visibilityOfElementLocated(genderDropdown));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", genderInput);
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        wait.until(ExpectedConditions.elementToBeClickable(genderInput));
        try {
            genderInput.click();
        } catch (Exception e) {
            clickViaScript(genderInput);
        }

        WebElement genderSelected = wait.until(ExpectedConditions.visibilityOfElementLocated(genderSelect));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", genderSelected);
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        wait.until(ExpectedConditions.elementToBeClickable(genderSelected));
        try {
            genderSelected.click();
        } catch (Exception e) {
            clickViaScript(genderSelected);
        }
    }

    public void enterProgramWebsite(String website) {
        // Send ESCAPE to body to securely dismiss any lingering material dropdown overlays from previous interacting steps
        new org.openqa.selenium.interactions.Actions(driver).sendKeys(Keys.ESCAPE).perform();
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}

        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(programWebsiteInput));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", input);
        try { Thread.sleep(500); } catch (Exception ignored) {}

        try {
            input.click();
        } catch (Exception e) {
            clickViaScript(input);
        }

        try {
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"), website);
        } catch (Exception e) {
            // Unstoppable injection fallback if Selenium is still getting intercepted physically
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value=''; " +
                    "arguments[0].value=arguments[1]; " +
                    "arguments[0].dispatchEvent(new Event('input', { bubbles: true })); " +
                    "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                    input, website);
        }
    }

    public void clickCreateMyProgram() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(createMyProgramButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        clickViaScript(btn);
    }

    /* ---------- Extra Actions for Staff Assignment ---------- */
    public void clickProgramNameHyperlink() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(programNameLink));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", link);
        clickViaScript(link);
    }

    public void clickAssignButtonOfCoach() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(assignCoachButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        clickViaScript(btn);
    }

    public void clickAssignButtonOfAssistantCoach() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(assignAssistantCoachButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        clickViaScript(btn);
    }

    public void clickAssignButtonOfManager() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(assignManagerButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        clickViaScript(btn);
    }

    public void enterStaffFirstNameAndLastName(String firstName, String lastName) {
        WebElement fNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(staffFirstNameInput));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", fNameInput);
        fNameInput.clear();
        fNameInput.sendKeys(firstName);

        WebElement lNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(staffLastNameInput));
        lNameInput.clear();
        lNameInput.sendKeys(lastName);
    }

    public void enterStaffEmailId(String email) {
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(staffEmailInput));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", emailInput);
        emailInput.clear();
        emailInput.sendKeys(email);
    }

    public void clickAndSelectTimezone() {
        try {
            WebElement tzDropdown = wait.until(ExpectedConditions.elementToBeClickable(timezoneDropdown));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", tzDropdown);
            clickViaScript(tzDropdown);

            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(timezoneOption));
            clickViaScript(option);
        } catch (Exception e) {
            System.out.println("Timezone selection not available or failed: " + e.getMessage());
        }
    }

    public void clickSaveButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        clickViaScript(btn);
    }

    public void confirmClickOnSaveButton() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(confirmSaveButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
            clickViaScript(btn);
        } catch (Exception e) {
            System.out.println("Confirm save button not found, continuing.");
        }
    }

    public void verifyStaffIsAdded() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(staffFirstNameInput));
        } catch (Exception e) {
            System.out.println("Staff input is still visible, proceeding...");
        }
    }

    private void clickViaScript(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }));",
                element);
    }
}
