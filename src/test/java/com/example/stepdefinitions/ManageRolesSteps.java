package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.ManageRolesPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

public class ManageRolesSteps {

    private final WebDriver driver = Hooks.driver;
    private final ManageRolesPage manageRolesPage = new ManageRolesPage(driver);

    @When("user click on User & Access Management")
    public void user_click_on_user_and_access_management() {
        manageRolesPage.clickUserAccessManagementMenu();
    }

    @And("user click on Manage Roles & Permissions")
    public void user_click_on_manage_roles_and_permissions() {
        com.example.utils.UsersPageContext.setSection(com.example.utils.UsersPageContext.Section.MANAGE_ROLES);
        com.example.utils.UsersPageContext.clearSearchState();
        manageRolesPage.clickManageRolesPermissionsMenu();
    }

    @And("user click on search role")
    public void user_click_on_search_role() {
        manageRolesPage.clickSearchRole();
    }

    @And("user click on view")
    public void user_click_on_view() {
        manageRolesPage.clickViewRole();
    }

    @And("user click on users")
    public void user_click_on_users() {
        manageRolesPage.clickUsersRole();
    }

    @Then("user click on Close")
    public void user_click_on_close() {
        manageRolesPage.clickCloseRoleDetails();
    }

    @Then("user click on Delete Role")
    public void user_click_on_delete_role() {
        manageRolesPage.confirmDeleteRole();
    }

    @And("user enter role name {string}")
    public void user_enter_role_name(String roleName) {
        manageRolesPage.enterRoleName(roleName);
    }

    @And("user enter description {string}")
    public void user_enter_description(String description) {
        manageRolesPage.enterDescription(description);
    }

    @And("user click on View Listing of Individual Users")
    public void user_click_on_view_listing_of_individual_users() {
        manageRolesPage.checkViewListingIndividualUsers();
    }

    @And("user scroll down")
    public void user_scroll_down() {
        manageRolesPage.scrollDown();
    }

    @And("user click on View Listing of Company Users")
    public void user_click_on_view_listing_of_company_users() {
        manageRolesPage.checkViewListingCompanyUsers();
    }

    @And("user click on Add of Input Categories")
    public void user_click_on_add_of_input_categories() {
        manageRolesPage.checkAddInputCategories();
    }

    @And("user click on Delete of Events")
    public void user_click_on_delete_of_events() {
        manageRolesPage.checkDeleteEvents();
    }

    @And("user click on View Listing of Advertisement")
    public void user_click_on_view_listing_of_advertisement() {
        manageRolesPage.checkViewListingAdvertisement();
    }

    @And("user click on View Listing of Transactions")
    public void user_click_on_view_listing_of_transactions() {
        manageRolesPage.checkViewListingTransactions();
    }

    @Then("user click on Create Role")
    public void user_click_on_create_role() {
        manageRolesPage.clickCreateRole();
    }
}
