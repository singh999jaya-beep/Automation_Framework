package com.example.stepdefinitions;



import com.example.hooks.Hooks;

import com.example.pages.CompanyUsersPage;

import com.example.pages.IndividualUsersPage;
import com.example.pages.ManageRolesPage;

import com.example.utils.UsersPageContext;

import io.cucumber.java.en.And;

import io.cucumber.java.en.Then;

import io.cucumber.java.en.When;

import org.junit.Assert;

import org.openqa.selenium.WebDriver;



public class CompanyUsersSteps {



    private final WebDriver driver = Hooks.driver;

    private final CompanyUsersPage companyUsersPage = new CompanyUsersPage(driver);

    private final IndividualUsersPage individualUsersPage = new IndividualUsersPage(driver);
    private final ManageRolesPage manageRolesPage = new ManageRolesPage(driver);



    @When("user clicks on Company Users menu")
    public void user_clicks_on_company_users_menu() {
        UsersPageContext.setSection(UsersPageContext.Section.COMPANY);
        UsersPageContext.clearSearchState();
        companyUsersPage.clickCompanyUsersMenu();
    }

    @When("user click on Company users menu")
    public void user_click_on_company_users_menu() {
        user_clicks_on_company_users_menu();
    }



    @Then("user validate Company Users title")

    public void user_validate_company_users_title() {

        Assert.assertTrue(

                "Company Users title is not displayed",

                companyUsersPage.isCompanyUsersTitleDisplayed()

        );

    }



    @And("user click on delete")

    public void user_click_on_delete() {

        if (UsersPageContext.isManageRolesSection()) {
            manageRolesPage.clickDeleteRole();
            return;
        }

        if (UsersPageContext.isCompanySection()) {

            companyUsersPage.clickDeleteCompanyUser();

            return;

        }

        individualUsersPage.clickDeleteIndividualUser();

    }



    @And("user enter text {string} in reason")

    public void user_enter_text_in_reason(String reason) {

        if (UsersPageContext.isCompanySection()) {

            companyUsersPage.enterReasonText(reason);

            return;

        }

        individualUsersPage.enterReasonText(reason);

    }



    @Then("user click on confirm Delete button")
    public void user_click_on_confirm_delete_button() {
        if (UsersPageContext.isCompanySection()) {
            companyUsersPage.clickConfirmDeleteButton();
            return;
        }
        individualUsersPage.clickConfirmDeleteButton();
    }

    @And("user click on block")
    public void user_click_on_block() {
        if (UsersPageContext.isCompanySection()) {
            companyUsersPage.clickBlockCompanyUser();
        }
    }

    @Then("user click on confirm Block button")
    public void user_click_on_confirm_block_button() {
        if (UsersPageContext.isCompanySection()) {
            companyUsersPage.clickConfirmBlockButton();
        }
    }

    @And("user click on unblock")
    public void user_click_on_unblock() {
        if (UsersPageContext.isCompanySection()) {
            companyUsersPage.clickUnblockCompanyUser();
        }
    }

    @Then("user click on confirm Unblock button")
    public void user_click_on_confirm_unblock_button() {
        if (UsersPageContext.isCompanySection()) {
            companyUsersPage.clickConfirmUnblockButton();
        }
    }

}

