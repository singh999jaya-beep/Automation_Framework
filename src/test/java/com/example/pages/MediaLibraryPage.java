package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MediaLibraryPage extends BasePage {

    /** Media Library URL (from home page). */
    public static final String MEDIA_LIBRARY_URL = "https://staging-app.safeteam.io/panel/media-library";

    /* ---------- Navigation ---------- */

    private final By dashboardLink = By.xpath("//a[contains(.,'Dashboard') or contains(.,'dashboard')] | //*[contains(@routerlink,'dashboard')]");

    /* ---------- Add New menu ---------- */
    private final By addNewButton = By.xpath("//button[@class='mat-focus-indicator mat-menu-trigger solid-primary custom-btn add-new-btn mat-flat-button mat-button-base ng-star-inserted']");
    private final By newFolderOption = By.xpath("//button[normalize-space()='NEW FOLDER']");
    private final By newDocumentOption = By.xpath("//button[normalize-space()='NEW DOCUMENT']");
    private final By addNewLinkOption = By.xpath("//button[normalize-space()='NEW LINK']");

    /* ---------- New Folder ---------- */
    /* Target the input inside folder dialog; use folder-form/mat-dialog-container so we don't depend on body/div indices. */
    private final By folderNameInput = By.xpath(
            "//mat-dialog-container//folder-form//input | " +
            "//folder-form//mat-form-field//input | " +
            "//folder-form//form//input");
    private final By createFolderButton = By.xpath("//span[normalize-space()='CREATE FOLDER']");

    /* ---------- New Document ---------- */
    /* Must target the actual input[type=file]; sendKeys does not work on the BROWSE label. */
    private final By documentFileInput = By.xpath(
            "//label[normalize-space()='BROWSE']/input[@type='file'] | " +
            "//file-form//input[@type='file'] | " +
            "//mat-dialog-container//input[@type='file'] | " +
            "//input[@type='file']");
    /* Relative locators for file-form dialog; avoid absolute /html/body/div paths. */
    private final By tagsInput = By.xpath(
            "//file-form//input[@formcontrolname='tags'] | " +
            "//file-form//form//mat-form-field//input[@type='text'] | " +
            "//mat-dialog-container//file-form//input[@type='text']");
    private final By descriptionInput = By.xpath(
            "//file-form//textarea | " +
            "//file-form//input[@formcontrolname='description'] | " +
            "//file-form//form//mat-form-field//textarea | " +
            "//mat-dialog-container//file-form//textarea");
    private final By saveFileButton = By.xpath("//span[normalize-space()='SAVE FILE']");

    /* ---------- Add New Link ---------- */
    /* Relative locators for link-form dialog; avoid absolute /html/body/div paths. */
    private final By linkTextInput = By.xpath(
            "//link-form//input[@formcontrolname='text'] | " +
            "//link-form//form//mat-form-field[1]//input | " +
            "//mat-dialog-container//link-form//input");
    private final By urlInput = By.xpath(
            "//link-form//input[@formcontrolname='url'] | " +
            "//link-form//form//mat-form-field[2]//input | " +
            "(//mat-dialog-container//link-form//input)[2]");
    private final By saveLinkButton = By.xpath("//span[normalize-space()='CREATE LINK']");

    public MediaLibraryPage(WebDriver driver) {
        super(driver);
    }

    /** Navigate from home page to Media Library via URL. */
    public void goToMediaLibrary() {
        driver.get(MEDIA_LIBRARY_URL);
    }

    public void navigateToDashboard() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(dashboardLink));
        click(link);
    }

    public void clickAddNewAndSelectNewFolder() {
        WebElement addNew = wait.until(ExpectedConditions.elementToBeClickable(addNewButton));
        click(addNew);
        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(newFolderOption));
        click(option);
    }

    public void enterNewFolderName(String folderName) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(folderNameInput));
        input.click();
        input.clear();
        input.sendKeys(folderName);
    }

    public void clickCreateFolder() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(createFolderButton));
        click(btn);
    }

    public void clickAddNewAndSelectNewDocument() {
        WebElement addNew = wait.until(ExpectedConditions.elementToBeClickable(addNewButton));
        click(addNew);
        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(newDocumentOption));
        click(option);
    }

    public void browseAndUploadDocument(String path) {
        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(documentFileInput));
        fileInput.sendKeys(path);
    }

    public void enterTagsAndDescription(String tags, String description) {
        WebElement tagsEl = wait.until(ExpectedConditions.visibilityOfElementLocated(tagsInput));
        tagsEl.click();
        tagsEl.clear();
        tagsEl.sendKeys(tags);
        WebElement descEl = wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionInput));
        descEl.click();
        descEl.clear();
        descEl.sendKeys(description);
    }

    public void clickSaveFile() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveFileButton));
        click(btn);
    }

    public void clickAddNewAndSelectAddNewLink() {
        WebElement addNew = wait.until(ExpectedConditions.elementToBeClickable(addNewButton));
        click(addNew);
        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(addNewLinkOption));
        click(option);
    }

    public void enterLinkTextAndUrl(String linkText, String url) {
        WebElement textEl = wait.until(ExpectedConditions.visibilityOfElementLocated(linkTextInput));
        textEl.click();
        textEl.clear();
        textEl.sendKeys(linkText);
        WebElement urlEl = wait.until(ExpectedConditions.visibilityOfElementLocated(urlInput));
        urlEl.click();
        urlEl.clear();
        urlEl.sendKeys(url);
    }

    public void clickSaveLink() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveLinkButton));
        click(btn);
    }
}
