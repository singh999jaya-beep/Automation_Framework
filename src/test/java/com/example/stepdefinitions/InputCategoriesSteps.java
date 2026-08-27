package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.InputCategoriesPage;
import com.example.utils.LookupDetailApiNetworkMonitor;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class InputCategoriesSteps {

    private final WebDriver driver = Hooks.driver;
    private final InputCategoriesPage inputCategoriesPage = new InputCategoriesPage(driver);

    @When("user clicks on Input Categories menu")
    public void user_clicks_on_input_categories_menu() {
        inputCategoriesPage.clickInputCategoriesMenu();
    }

    @Then("user validate Input Categories title")
    public void user_validate_input_categories_title() {
        Assert.assertTrue(
                "Input Categories title is not displayed",
                inputCategoriesPage.isInputCategoriesTitleDisplayed()
        );
    }

    @Then("user validate More about you text")
    public void user_validate_more_about_you_text() {
        Assert.assertTrue(
                "More about you text is not displayed (expected semantics lookup_row_name_1)",
                inputCategoriesPage.isMoreAboutYouTextDisplayed()
        );
    }

    @When("user clicks on More about you")
    public void user_clicks_on_more_about_you() {
        inputCategoriesPage.clickMoreAboutYou();
    }

    @When("user clicks on What's important to you")
    public void user_clicks_on_what_s_important_to_you() {
        inputCategoriesPage.clickWhatsImportantToYou();
    }

    @When("user clicks on Interests and hobbies")
    public void user_clicks_on_interests_and_hobbies() {
        inputCategoriesPage.clickInterestsAndHobbies();
    }

    @When("user clicks on Professional background")
    public void user_clicks_on_professional_background() {
        inputCategoriesPage.clickProfessionalBackground();
    }

    @When("user clicks on Social media links")
    public void user_clicks_on_social_media_links() {
        inputCategoriesPage.clickSocialMediaLinks();
    }

    @When("user click on Add New")
    public void user_click_on_add_new() {
        inputCategoriesPage.clickAddNew();
    }

    @When("user enter English Name {string}")
    public void user_enter_english_name(String name) {
        inputCategoriesPage.enterEnglishName(name);
    }

    @When("user enter Spanish Name {string}")
    public void user_enter_spanish_name(String name) {
        inputCategoriesPage.enterSpanishName(name);
    }

    @When("user enter Portuguese Name {string}")
    public void user_enter_portuguese_name(String name) {
        inputCategoriesPage.enterPortugueseName(name);
    }

    @When("user enter Canadian Name {string}")
    public void user_enter_canadian_name(String name) {
        inputCategoriesPage.enterCanadianName(name);
    }

    @When("user enter Social Media Name {string}")
    public void user_enter_social_media_name(String name) {
        inputCategoriesPage.enterSocialMediaName(name);
    }

    @When("user click on image upload button")
    public void user_click_on_image_upload_button() {
        inputCategoriesPage.clickImageUploadButton();
    }

    @Then("user click on Add Button")
    public void user_click_on_add_button() {
        inputCategoriesPage.clickAddButton();
    }

    @Then("user click on Edit Save Button")
    public void user_click_on_edit_save_button() {
        inputCategoriesPage.clickEditSaveButton();
    }

    @When("user click on Edit")
    public void user_click_on_edit() {
        inputCategoriesPage.clickEditLookupDetail();
    }

    @When("user edit name {string}")
    public void user_edit_name(String name) {
        inputCategoriesPage.editEnglishName(name);
    }

    @And("user click on edit lookup detail")
    public void user_click_on_edit_lookup_detail() {
        inputCategoriesPage.clickEditLookupDetail();
    }

    @And("user click on delete lookup detail")
    public void user_click_on_delete_lookup_detail() {
        inputCategoriesPage.clickDeleteLookupDetail();
    }

    @And("user click on block lookup detail")
    public void user_click_on_block_lookup_detail() {
        inputCategoriesPage.clickBlockLookupDetail();
    }

    @Then("user click on confirm delete lookup")
    public void user_click_on_confirm_delete_lookup() {
        LookupDetailApiNetworkMonitor.prepareForDeleteCapture(driver);
        inputCategoriesPage.clickConfirmDeleteButton();
    }

    @Then("user click on confirm block lookup")
    public void user_click_on_confirm_block_lookup() {
        LookupDetailApiNetworkMonitor.prepareForBlockCapture(driver);
        inputCategoriesPage.clickConfirmBlockButton();
    }
}
