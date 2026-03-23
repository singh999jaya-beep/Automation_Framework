package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.LoginPage;
import com.example.pages.MediaLibraryPage;
import com.example.utils.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

public class MediaLibrarySteps {

    private final WebDriver driver;
    private final MediaLibraryPage mediaLibraryPage;
    private final LoginPage loginPage;

    public MediaLibrarySteps(TestContext testContext) {
        this.driver = Hooks.driver;
        this.mediaLibraryPage = new MediaLibraryPage(driver);
        this.loginPage = new LoginPage(driver);
    }

    @Given("user goes to Media Library")
    public void user_goes_to_media_library() {
        mediaLibraryPage.goToMediaLibrary();
    }

    @When("user clicks on Add New and selects New Folder")
    public void user_clicks_on_add_new_and_selects_new_folder() {
        mediaLibraryPage.clickAddNewAndSelectNewFolder();
    }

    @Given("user logs in with email {string} and password {string}")
    public void userLogsInWithEmailAndPassword(String email, String password) {
        driver.get("https://pre-prod-app.safeteam.io/auth/login");
        loginPage.enterUsername(email);
        loginPage.enterPassword(password);
        loginPage.clickLogin();
    }

    @And("user navigates to dashboard")
    public void userNavigatesToDashboard() {
        mediaLibraryPage.navigateToDashboard();
    }

    @And("user enters new folder name {string}")
    public void userEntersNewFolderName(String folderName) {
        mediaLibraryPage.enterNewFolderName(folderName);
    }

    @Then("user clicks on Create Folder")
    public void userClicksOnCreateFolder() {
        mediaLibraryPage.clickCreateFolder();
    }

    @When("user clicks on Add New and selects New Document")
    public void userClicksOnAddNewAndSelectsNewDocument() {
        mediaLibraryPage.clickAddNewAndSelectNewDocument();
    }

    @And("user browses and uploads document from path {string}")
    public void userBrowsesAndUploadsDocumentFromPath(String path) {
        mediaLibraryPage.browseAndUploadDocument(path);
    }

    @And("user enters tags {string} and description {string}")
    public void userEntersTagsAndDescription(String tags, String description) {
        mediaLibraryPage.enterTagsAndDescription(tags, description);
    }

    @Then("user clicks on Save File")
    public void userClicksOnSaveFile() {
        mediaLibraryPage.clickSaveFile();
    }

    @When("user clicks on Add New and selects Add New Link")
    public void userClicksOnAddNewAndSelectsAddNewLink() {
        mediaLibraryPage.clickAddNewAndSelectAddNewLink();
    }

    @And("user enters link text {string} and URL {string}")
    public void userEntersLinkTextAndURL(String linkText, String url) {
        mediaLibraryPage.enterLinkTextAndUrl(linkText, url);
    }

    @Then("user clicks on Save Link")
    public void userClicksOnSaveLink() {
        mediaLibraryPage.clickSaveLink();
    }
}