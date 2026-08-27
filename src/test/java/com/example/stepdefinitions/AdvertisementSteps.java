package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.AdvertisementPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

public class AdvertisementSteps {

    private final WebDriver driver = Hooks.driver;
    private final AdvertisementPage advertisementPage = new AdvertisementPage(driver);

    @When("user clicks on Advertisements")
    public void user_clicks_on_advertisements() {
        advertisementPage.clickAdvertisementsMenu();
    }

    @And("user click on Create Ads button")
    public void user_click_on_create_ads_button() {
        advertisementPage.clickCreateAdButton();
    }

    @And("user enter campaign name {string}")
    public void user_enter_campaign_name(String campaignName) {
        advertisementPage.enterCampaignName(campaignName);
    }

    @And("user enter URL {string}")
    public void user_enter_url(String url) {
        advertisementPage.enterWebsiteUrl(url);
    }

    @And("user enter title {string}")
    public void user_enter_title(String title) {
        advertisementPage.enterTitle(title);
    }

    @And("user scroll down the page")
    public void user_scroll_down_the_page() {
        advertisementPage.scrollDownPage();
    }

    @And("user enter Ad description {string}")
    public void user_enter_ad_description(String description) {
        advertisementPage.enterAdDescription(description);
    }

    @And("user click on upload here")
    public void user_click_on_upload_here() {
        advertisementPage.clickUploadHere();
    }

    @And("user click on upload here with {string}")
    public void user_click_on_upload_here_with(String mediaPath) {
        advertisementPage.clickUploadHere(mediaPath);
    }

    @Then("user click on publish button")
    public void user_click_on_publish_button() {
        advertisementPage.clickPublishButton();
    }
}
