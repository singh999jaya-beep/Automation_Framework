package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.ModerationActionPage;
import com.example.pages.ModerationCenterPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.openqa.selenium.WebDriver;

public class ModerationActionSteps {

    private final WebDriver driver = Hooks.driver;
    private final ModerationCenterPage moderationCenterPage = new ModerationCenterPage(driver);
    private final ModerationActionPage moderationActionPage = new ModerationActionPage(driver, moderationCenterPage);

    @And("user click on View")
    public void user_click_on_view() {
        moderationActionPage.clickViewReportedEvent();
    }

    @And("user click on Take Action")
    public void user_click_on_take_action() {
        moderationActionPage.clickTakeAction();
    }

    @And("user click on Dismiss Report")
    public void user_click_on_dismiss_report() {
        moderationActionPage.clickDismissReportCheckbox();
    }

    @And("user click on Block reported event")
    public void user_click_on_block_reported_event() {
        moderationActionPage.clickBlockReportedEventCheckbox();
    }

    @And("user click on Blocked Reported User Account")
    public void user_click_on_blocked_reported_user_account() {
        moderationActionPage.clickBlockReportedUserAccountCheckbox();
    }

    @Then("user click on Confirm Button")
    public void user_click_on_confirm_button() {
        moderationActionPage.clickConfirmButton();
    }
}
