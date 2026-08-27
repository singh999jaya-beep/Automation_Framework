package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.IndividualUsersPage;
import com.example.utils.UsersPageContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class IndividualUsersSteps {

    private final WebDriver driver = Hooks.driver;
    private final IndividualUsersPage individualUsersPage = new IndividualUsersPage(driver);

    @When("user clicks on Individual Users menu")
    public void user_clicks_on_individual_users_menu() {
        UsersPageContext.setSection(UsersPageContext.Section.INDIVIDUAL);
        UsersPageContext.clearSearchState();
        individualUsersPage.clickIndividualUsersMenu();
    }

    @When("user click on Individual users menu")
    public void user_click_on_individual_users_menu() {
        user_clicks_on_individual_users_menu();
    }

    @Then("user validate Individual Users title")
    public void user_validate_individual_users_title() {
        Assert.assertTrue(
                "Individual Users title is not displayed",
                individualUsersPage.isIndividualUsersTitleDisplayed()
        );
    }

    @Then("user validate User ID")
    public void user_validate_user_id() {
        Assert.assertTrue(
                "User ID is not displayed",
                individualUsersPage.isUserIdDisplayed()
        );
    }

    @And("user click on delete individual users")
    public void user_click_on_delete_individual_users() {
        individualUsersPage.clickDeleteIndividualUser();
    }

}
