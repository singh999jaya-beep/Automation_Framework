package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.AuditLogsPage;
import com.example.pages.ConsentAuditLogPage;
import com.example.pages.ModerationCenterPage;
import com.example.utils.UsersPageContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class ModerationCenterSteps {

    private final WebDriver driver = Hooks.driver;
    private final ModerationCenterPage moderationCenterPage = new ModerationCenterPage(driver);
    private final ConsentAuditLogPage consentAuditLogPage = new ConsentAuditLogPage(driver);
    private final AuditLogsPage auditLogsPage = new AuditLogsPage(driver);

    @When("user clicks on Moderation Center")
    @When("user clicks on Moderation  Center")
    public void user_clicks_on_moderation_center() {
        UsersPageContext.setSection(UsersPageContext.Section.MODERATION);
        UsersPageContext.clearSearchState();
        moderationCenterPage.clickModerationCenterMenu();
    }

    @And("user click on Reported Users")
    public void user_click_on_reported_users() {
        moderationCenterPage.clickReportedUsersTab();
    }

    @And("user click on Pending status")
    public void user_click_on_pending_status() {
        moderationCenterPage.clickPendingStatus();
    }

    @And("user click on Report Dismissed")
    public void user_click_on_report_dismissed() {
        moderationCenterPage.clickReportDismissedStatus();
    }

    @And("user click on Blocked")
    public void user_click_on_blocked() {
        if (UsersPageContext.isAuditLogsSection()) {
            auditLogsPage.clickBlocked();
            return;
        }
        moderationCenterPage.clickBlockedStatus();
    }

    @Then("user validate Pending")
    public void user_validate_pending() {
        if (UsersPageContext.getSection() == UsersPageContext.Section.CONSENT_AUDIT_LOG) {
            Assert.assertTrue(
                    "Pending is not displayed",
                    consentAuditLogPage.isPendingDisplayed()
            );
            return;
        }
        Assert.assertTrue(
                "Pending status is not displayed (expected semantics " + "status_0_option_status_0)",
                moderationCenterPage.isPendingStatusDisplayed()
        );
    }

    @Then("user validate Report Dismissed")
    public void user_validate_report_dismissed() {
        Assert.assertTrue(
                "Report Dismissed status is not displayed (expected semantics " + "status_1_option_status_1)",
                moderationCenterPage.isReportDismissedStatusDisplayed()
        );
    }

    @Then("user validate Blocked")
    public void user_validate_blocked() {
        if (UsersPageContext.isAuditLogsSection()) {
            Assert.assertTrue(
                    "Blocked action filter is not displayed (expected semantics blocked_option_blocked)",
                    auditLogsPage.isBlockedDisplayed()
            );
            return;
        }
        Assert.assertTrue(
                "Blocked status is not displayed (expected semantics " + "status_2_option_status_2)",
                moderationCenterPage.isBlockedStatusDisplayed()
        );
    }
}
