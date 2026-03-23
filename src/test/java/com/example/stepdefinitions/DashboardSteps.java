package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.DashboardPage;
import com.example.pages.ProgramPage;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;

public class DashboardSteps {
    private final WebDriver driver = Hooks.driver;
    private final DashboardPage dashboardPage;
    private final ProgramPage programPage;

    public DashboardSteps() {
        this.dashboardPage = new DashboardPage(driver);
        this.programPage = new ProgramPage(driver);
    }

    @When("user navigates to the Dashboard menu")
    public void user_navigates_to_dashboard_menu() {
        dashboardPage.waitForDashboard();
    }

    @And("user clicks on Post to Team Feed button")
    public void user_clicks_post_button() {
        dashboardPage.clickPostButton();
    }

    @Then("post should be published successfully")
    public void post_should_be_published_successfully() {
        dashboardPage.verifyPostPublished();
    }

    @When("user writes a post on the Team Feed {string}")
    public void user_writes_a_post(String postText) {
        dashboardPage.writePost(postText);
    }

    @And("user scrolls to Post to Team Feed button")
    public void userScrollsToPostToTeamFeedButton() {
        String url = driver.getCurrentUrl();
        if (url != null && url.contains("program")) {
            programPage.scrollToPostToTeamFeedButton();
        } else {
            dashboardPage.scrollToPostToTeamFeed();
        }
    }

    /* -------------------- Link / Image / Video Upload -------------------- */

    @And("user click on link and enter URL {string}")
    public void user_add_link(String url) {
        dashboardPage.addLink(url);
    }

    @And("user click on img and upload img from path {string}")
    public void user_upload_image(String path) {
        dashboardPage.uploadImage(path);
    }

    @And("user click on video and upload img from path {string}")
    public void user_upload_video(String path) {
        dashboardPage.uploadVideo(path);
    }

    /* -------------------- Edit / Delete Post -------------------- */

    @And("user click on three dots of the post")
    public void user_click_on_three_dots_of_the_post() {
        dashboardPage.clickThreeDotsOnPost();
    }

    @And("user clicks on Edit option")
    public void user_clicks_on_edit_option() {
        dashboardPage.clickEditOption();
    }

    @And("user edits the post with text {string}")
    public void user_edits_the_post_with_text(String text) {
        dashboardPage.editPostWithText(text);
    }

    @And("user scroll Save Changes and clicks on Save Changes button")
    public void user_scroll_save_changes_and_clicks_on_save_changes_button() {
        dashboardPage.scrollToSaveChangesAndClick();
    }

    @And("user clicks on Save Changes button")
    public void user_clicks_on_save_changes_button() {
        dashboardPage.clickSaveChangesButton();
    }

    @And("user clicks on Delete option")
    public void user_clicks_on_delete_option() {
        dashboardPage.clickDeleteOption();
    }

    @Then("post should be deleted successfully")
    public void post_should_be_deleted_successfully() {
        dashboardPage.verifyPostDeleted();
    }

    /* -------------------- Comment on post -------------------- */

    @And("user click on Comments of the post")
    public void user_click_on_comments_of_the_post() {
        dashboardPage.clickCommentsOfPost();
    }

    @And("user clicks on Be the first one to comment")
    public void user_clicks_on_be_the_first_one_to_comment() {
        dashboardPage.clickBeFirstToComment();
    }

    @And("user enter comment {string}")
    public void user_enter_comment(String comment) {
        dashboardPage.enterComment(comment);
    }

    @And("user clicks on Post Comment button")
    public void user_clicks_on_post_comment_button() {
        dashboardPage.clickPostCommentButton();
    }

    @Then("comment should be published successfully")
    public void comment_should_be_published_successfully() {
        dashboardPage.verifyCommentPublished();
    }

    /* -------------------- Like post -------------------- */

    @And("user click on Likes icon of the post")
    public void user_click_on_likes_icon_of_the_post() {
        dashboardPage.clickLikesIconOfPost();
    }

    @Then("post should be liked successfully")
    public void post_should_be_liked_successfully() {
        dashboardPage.verifyPostLiked();
    }

    /* -------------------- Dislike post -------------------- */

    @And("user click on alreadyLiked icon of the post")
    public void user_click_on_already_liked_icon_of_the_post() {
        dashboardPage.clickAlreadyLikedIconOfPost();
    }

    @Then("post should be dislike successfully")
    public void post_should_be_dislike_successfully() {
        dashboardPage.verifyPostDisliked();
    }

    /* -------------------- Add Event -------------------- */

    @And("user click on plus icon and select Add Event option")
    public void user_click_on_plus_icon_and_select_add_event_option() {
        dashboardPage.clickPlusIconAndSelectAddEvent();
    }

    @And("user enter event name {string}")
    public void user_enter_event_name(String name) {
        dashboardPage.enterEventName(name);
    }

    @And("user scroll current event tab")
    public void user_scroll_current_event_tab() {
        dashboardPage.scrollCurrentEventTab();
    }

    @And("user enter event location {string}")
    public void user_enter_event_location(String location) {
        dashboardPage.enterEventLocation(location);
    }

    @And("user enter event address {string}")
    public void user_enter_event_address(String address) {
        dashboardPage.enterEventAddress(address);
    }

    @And("user click on Save button")
    public void user_click_on_save_button() {
        dashboardPage.clickSaveButton();
    }

    @Then("event should be created successfully")
    public void event_should_be_created_successfully() {
        dashboardPage.verifyEventCreated();
    }

    /* -------------------- Delete Event -------------------- */

    @And("user click on the event")
    public void user_click_on_the_event() {
        dashboardPage.clickOnEvent();
    }

    @And("user click on edit button of the event")
    public void user_click_on_edit_button_of_the_event() {
        dashboardPage.clickEditButtonOfEvent();
    }

    @And("user click on Delete button")
    public void user_click_on_delete_button() {
        dashboardPage.clickEventDeleteButton();
    }

    @Then("event should be deleted successfully")
    public void event_should_be_deleted_successfully() {
        dashboardPage.verifyEventDeleted();
    }

    /* -------------------- Add Announcement -------------------- */

    @And("user click on plus icon and select Add Announcement option")
    public void user_click_on_plus_icon_and_select_add_announcement_option() {
        dashboardPage.clickPlusIconAndSelectAddAnnouncement();
    }



    @And("user enter announcement title {string}")
    public void user_enter_announcement_title(String title) {
        dashboardPage.enterAnnouncementTitle(title);
    }

    @And("user click on add announcement button")
    public void user_click_on_add_announcement_button() {
        dashboardPage.clickAddAnnouncementButton();
    }



    @Then("announcement should be created successfully")
    public void announcement_should_be_created_successfully() {
        dashboardPage.verifyAnnouncementCreated();
    }

    /* -------------------- Delete Announcement -------------------- */

    @And("user click on announcement tab")
    public void user_click_on_announcement_tab() {
        dashboardPage.clickAnnouncementTab();
    }

    @And("user click on delete button of the announcement")
    public void user_click_on_delete_button_of_the_announcement() {
        dashboardPage.clickDeleteButtonOfAnnouncement();
    }

    @Then("announcement should be deleted successfully")
    public void announcement_should_be_deleted_successfully() {
        dashboardPage.verifyAnnouncementDeleted();
    }

    /* -------------------- Add Game -------------------- */

    @And("user click on plus icon and select Add Game option")
    public void user_click_on_plus_icon_and_select_add_game_option() {
        dashboardPage.clickPlusIconAndSelectAddGame();
    }

    @And("user enter opponent name {string}")
    public void user_enter_opponent_name(String name) {
        dashboardPage.enterOpponentName(name);
    }

    @And("user enter location {string}")
    public void user_enter_location(String location) {
        dashboardPage.enterGameLocation(location);
    }


    @And("user enter address {string}")
    public void user_enter_address(String address) {
        dashboardPage.enterGameAddress(address);
    }

    @Then("game should be added successfully")
    public void game_should_be_added_successfully() {
        dashboardPage.verifyGameAdded();
    }

    @And("user click on  game tab")
    public void userClickOnGameTab() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("user click on delete button of game tab")
    public void userClickOnDeleteButtonOfGameTab() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("game should be deleted successfully")
    public void gameShouldBeDeletedSuccessfully() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("user click on plus icon and select Add click on Export Calendar")
    public void userClickOnPlusIconAndSelectAddClickOnExportCalendar() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("calendar should be exported successfully")
    public void calendarShouldBeExportedSuccessfully() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
