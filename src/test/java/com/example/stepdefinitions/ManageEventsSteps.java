package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.DeletedAppUsersPage;
import com.example.pages.InputCategoriesPage;
import com.example.pages.ManageEventsPage;
import com.example.utils.LookupDetailApiNetworkMonitor;
import com.example.utils.UsersPageContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class ManageEventsSteps {

    private final WebDriver driver = Hooks.driver;
    private final ManageEventsPage manageEventsPage = new ManageEventsPage(driver);
    private final InputCategoriesPage inputCategoriesPage = new InputCategoriesPage(driver);
    private final DeletedAppUsersPage deletedAppUsersPage = new DeletedAppUsersPage(driver);

    @When("user clicks on Events")
    public void user_clicks_on_events() {
        UsersPageContext.setSection(UsersPageContext.Section.EVENTS);
        UsersPageContext.clearSearchState();
        manageEventsPage.clickEventsMenu();
    }

    @And("user enter eventid {string}")
    public void user_enter_eventid(String eventId) {
        UsersPageContext.setSearchedEventId(eventId);
        UsersPageContext.setSearchedName(null);
        UsersPageContext.setSearchedEmail(null);
        manageEventsPage.enterSearchEventId(eventId);
    }

    @And("user enter ID {string}")
    public void user_enter_id(String id) {
        if (UsersPageContext.isDeletedAppUsersSection()) {
            UsersPageContext.setSearchedEventId(id);
            UsersPageContext.setSearchedName(null);
            UsersPageContext.setSearchedEmail(null);
            deletedAppUsersPage.enterSearchId(id);
            return;
        }
        user_enter_eventid(id);
    }

    @Then("user validate ID")
    public void user_validate_id() {
        String searchedEventId = UsersPageContext.getSearchedEventId();
        Assert.assertTrue(
                "Event ID is not displayed and No data available message is not shown",
                manageEventsPage.isEventIdDisplayedOrNoDataAvailable(searchedEventId)
        );
    }

    @Then("user validate ID or No data available")
    public void user_validate_id_or_no_data_available() {
        user_validate_id();
    }

    @When("user click on Delete")
    public void user_click_on_delete() {
        if (UsersPageContext.isEventsSection()) {
            manageEventsPage.clickDeleteEvent();
            return;
        }
        inputCategoriesPage.clickDeleteLookupDetail();
    }

    @Then("user click on  confirm Delete Button")
    public void user_click_on_confirm_delete_button() {
        LookupDetailApiNetworkMonitor.prepareForDeleteCapture(driver);
        if (UsersPageContext.isEventsSection()) {
            manageEventsPage.clickConfirmDeleteButton();
            return;
        }
        inputCategoriesPage.clickConfirmDeleteButton();
    }

    @And("delete lookup detail API should return status {int}")
    public void delete_lookup_detail_api_should_return_status(int expectedStatus) {
        Assert.assertTrue(
                "Expected DELETE API status " + expectedStatus + " but got: "
                        + LookupDetailApiNetworkMonitor.lastCapturedDeleteSummary(driver),
                LookupDetailApiNetworkMonitor.waitForDeleteStatus(driver, expectedStatus, Duration.ofSeconds(20))
        );
    }

    @When("user click on Block")
    public void user_click_on_block() {
        if (UsersPageContext.isEventsSection()) {
            manageEventsPage.clickBlockEvent();
            return;
        }
        inputCategoriesPage.clickBlockLookupDetail();
    }

    @Then("user click on  confirm Block Button")
    public void user_click_on_confirm_block_button() {
        LookupDetailApiNetworkMonitor.prepareForBlockCapture(driver);
        if (UsersPageContext.isEventsSection()) {
            manageEventsPage.clickConfirmBlockButton();
            return;
        }
        inputCategoriesPage.clickConfirmBlockButton();
    }

    @And("block lookup detail API should return status {int}")
    public void block_lookup_detail_api_should_return_status(int expectedStatus) {
        Assert.assertTrue(
                "Expected BLOCK API status " + expectedStatus + " but got: "
                        + LookupDetailApiNetworkMonitor.lastCapturedBlockSummary(driver),
                LookupDetailApiNetworkMonitor.waitForBlockStatus(driver, expectedStatus, Duration.ofSeconds(20))
        );
    }
}
