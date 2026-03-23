package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.athletesPage;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

public class athletesSteps {

    private final WebDriver driver;
    private final athletesPage athletesPage;

    public athletesSteps() {
        this.driver = Hooks.driver;
        this.athletesPage = new athletesPage(driver);
    }

    @Given("user goes to Athletes")
    public void user_goes_to_athletes() {
        athletesPage.goToAthletes();
    }

    @When("user clicks on Add Athletes")
    public void user_clicks_on_add_athletes() {
        athletesPage.clickAddAthletes();
    }

    @And("user enter first name {string}")
    public void user_enter_first_name(String firstName) {
        athletesPage.enterFirstName(firstName);
    }

    @And("user enter last name {string}")
    public void user_enter_last_name(String lastName) {
        athletesPage.enterLastName(lastName);
    }

    @And("user click on calendar and select date")
    public void user_click_on_calendar_and_select_date() {
        athletesPage.clickCalendarAndSelectDate();
    }

    @And("user click on gender dropdown and select it")
    public void user_click_on_gender_dropdown_and_select_it() {
        athletesPage.clickGenderDropdownAndSelect();
    }

    @And("user enter email {string}")
    public void user_enter_email(String email) {
        athletesPage.enterEmail(email);
    }

    @And("user enter mobile number {string}")
    public void user_enter_mobile_number(String mobile) {
        athletesPage.enterMobileNumber(mobile);
    }

    @And("user clicks on Save or Add Athlete button")
    public void user_clicks_on_save_or_add_athlete_button() {
        athletesPage.clickSaveOrAddAthleteButton();
    }

    @Then("the athlete is added successfully")
    public void the_athlete_is_added_successfully() {
        athletesPage.verifyAthleteAdded();
    }

    @And("user click on Add Household")
    public void user_click_on_household() {
        athletesPage.addHousehold();

    }

    @And("user enter firstname {string} , lastname {string} and phone no. {string} of household")
    public void user_enter_firstname_lastname_and_phone_no_of_household(String firstName, String lastName, String phone) {
        athletesPage.enterHouseholdNameAndPhone(firstName, lastName, phone);
    }

    @And("user enter household email {string} and household relationaship {string}")
    public void user_enter_household_email_and_household_relationaship(String email, String rel) {
        athletesPage.enterHouseholdEmailAndRelationship(email, rel);
    }

    @And("user clicks on Continue")
    public void user_clicks_on_continue() {
        athletesPage.clickContinue();
    }

    @When("user click on edit button of athlete")
    public void user_click_on_edit_button_of_athlete() {
        athletesPage.clickEditAthlete();
    }

    @And("user edit name of the athlete {string}")
    public void user_edit_name_of_the_athlete(String name) {
        athletesPage.editAthleteName(name);
    }

    @And("user scroll down  athlete tab")
    public void user_scroll_down_athlete_tab() {
        athletesPage.scrollDownAthleteTab();
    }

    @And("user edit email of the athlete {string}")
    public void user_edit_email_of_the_athlete(String email) {
        athletesPage.editAthleteEmail(email);
    }

    @And("user click on submit button")
    public void user_click_on_submit_button() {
        athletesPage.clickSubmitAthleteEdit();
    }

    @Then("the edit athlete saved successfully")
    public void the_edit_athlete_saved_successfully() {
        athletesPage.verifyAthleteEditSuccess();
    }

    @And("user scroll down household tab")
    public void user_scroll_down_household_tab() {
        athletesPage.scrollDownHousehold();

    }
}
