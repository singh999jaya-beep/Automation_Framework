package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.DeletedAppUsersPage;
import com.example.utils.UsersPageContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class DeletedAppUsersSteps {

    private final WebDriver driver = Hooks.driver;
    private final DeletedAppUsersPage deletedAppUsersPage = new DeletedAppUsersPage(driver);

    @When("user click on Deleted App Users")
    public void user_click_on_deleted_app_users() {
        UsersPageContext.setSection(UsersPageContext.Section.DELETED_APP_USERS);
        UsersPageContext.clearSearchState();
        deletedAppUsersPage.clickDeletedAppUsersMenu();
    }

    @And("user click on Individual")
    public void user_click_on_individual() {
        deletedAppUsersPage.clickIndividual();
    }

    @And("user click on Company")
    public void user_click_on_company() {
        deletedAppUsersPage.clickCompany();
    }

    @And("user click on Deactivated")
    public void user_click_on_deactivated() {
        deletedAppUsersPage.clickDeactivated();
    }

    @And("user click on Recovered")
    public void user_click_on_recovered() {
        deletedAppUsersPage.clickRecovered();
    }

    @Then("user click on Recover")
    public void user_click_on_recover() {
        deletedAppUsersPage.clickRecover();
    }

    @And("user click on search")
    public void user_click_on_search() {
        deletedAppUsersPage.clickSearch();
    }

    @Then("user validate userID")
    public void user_validate_user_id() {
        String userId = UsersPageContext.getSearchedEventId();
        Assert.assertTrue(
                "User ID is not displayed (expected semantics User ID: " + userId + ")",
                deletedAppUsersPage.isUserIdDisplayed(userId)
        );
    }

    @Then("user validate User Type")
    public void user_validate_user_type() {
        Assert.assertTrue(
                "User Type is not displayed (expected semantics deleted_user_type_0)",
                deletedAppUsersPage.isUserTypeDisplayed()
        );
    }

    @Then("user validate Status")
    @Then("user validate  Status")
    public void user_validate_status() {
        Assert.assertTrue(
                "Status is not displayed (expected semantics deleted_user_status_0)",
                deletedAppUsersPage.isStatusDisplayed()
        );
    }
}
