package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.AuditLogsPage;
import com.example.pages.DeletedAppUsersPage;
import com.example.pages.TransactionsPage;
import com.example.utils.UsersPageContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class TransactionsSteps {

    private final WebDriver driver = Hooks.driver;
    private final TransactionsPage transactionsPage = new TransactionsPage(driver);
    private final AuditLogsPage auditLogsPage = new AuditLogsPage(driver);
    private final DeletedAppUsersPage deletedAppUsersPage = new DeletedAppUsersPage(driver);

    @When("user clicks on Transactions menu")
    @When("user clicks on Transactions")
    public void user_clicks_on_transactions_menu() {
        UsersPageContext.setSection(UsersPageContext.Section.TRANSACTIONS);
        UsersPageContext.clearSearchState();
        transactionsPage.clickTransactionsMenu();
    }

    @And("user click on All")
    public void user_click_on_all() {
        if (UsersPageContext.isDeletedAppUsersSection()) {
            deletedAppUsersPage.clickAll();
            return;
        }
        transactionsPage.clickAll();
    }

    @And("user click on Monthly Subscription status")
    public void user_click_on_monthly_subscription_status() {
        transactionsPage.clickMonthlySubscriptionStatus();
    }

    @And("user click on Event Invitation status")
    public void user_click_on_event_invitation_status() {
        transactionsPage.clickEventInvitationStatus();
    }

    @And("user click on Advertisement status")
    public void user_click_on_advertisement_status() {
        transactionsPage.clickAdvertisementStatus();
    }

    @Then("user validate Monthly Subscription")
    public void user_validate_monthly_subscription() {
        Assert.assertTrue(
                "Monthly Subscription is not displayed (expected semantics "
                        + "monthlysub_option_monthlysub)",
                transactionsPage.isMonthlySubscriptionDisplayed()
        );
    }

    @Then("user validate Event Invitation")
    public void user_validate_event_invitation() {
        Assert.assertTrue(
                "Event Invitation is not displayed",
                transactionsPage.isEventInvitationDisplayed()
        );
    }

    @Then("user validate Advertisement")
    public void user_validate_advertisement() {
        if (UsersPageContext.isAuditLogsSection()) {
            Assert.assertTrue(
                    "Advertisement module filter is not displayed (expected semantics advertisement_option_advertisement)",
                    auditLogsPage.isAdvertisementDisplayed()
            );
            return;
        }
        Assert.assertTrue(
                "Advertisement is not displayed",
                transactionsPage.isAdvertisementDisplayed()
        );
    }
}
