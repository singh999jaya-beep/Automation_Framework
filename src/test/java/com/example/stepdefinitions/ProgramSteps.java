package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.ProgramPage;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

public class ProgramSteps {

    private final ProgramPage programPage;

    public ProgramSteps() {
        WebDriver driver = Hooks.driver;
        this.programPage = new ProgramPage(driver);
    }

    @When("user navigates to the Program menu")
    public void user_navigates_to_the_program_menu() {
        programPage.navigateToProgramMenu();
    }

    @And("user click on Add New Program")
    public void user_click_on_add_new_program() {
        programPage.clickAddNewProgram();
    }

    @And("user enter program name")
    public void user_enter_program_name() {
        programPage.enterProgramName("Test Program");
    }

    @And("user click on season and checked the box")
    public void user_click_on_season_and_checked_the_box() {
        programPage.clickOnSeason();
        programPage.checkSeasonBox();
    }

    /* "user scrolls to Post to Team Feed button" is defined in DashboardSteps and shared by this scenario. */

    @And("user click on sport and select from dropdown")
    public void user_click_on_sport_and_select_from_dropdown() {
        programPage.clickSportAndSelectFromDropdown();
    }

    @And("user click on gender and select from dropdown")
    public void user_click_on_gender_and_select_from_dropdown() {
        programPage.clickGenderAndSelectFromDropdown();
    }

    @And("user enter program website")
    public void user_enter_program_website() {
        programPage.enterProgramWebsite("https://pre-prod-app.safeteam.io");
    }

    @Then("user click on Create My  Program")
    public void user_click_on_create_my_program() {
        programPage.clickCreateMyProgram();
    }

    @And("user go to program name hyperlink")
    public void userGoToProgramNameHyperlink() {
        programPage.clickProgramNameHyperlink();
    }

    @And("user click assign button of coach")
    public void userClickAssignButtonOfCoach() {
        programPage.clickAssignButtonOfCoach();
    }

    @And("user enter Coach first name {string} and last name {string}")
    public void userEnterCoachFirstNameAndLastName(String firstName, String lastName) {
        programPage.enterStaffFirstNameAndLastName(firstName, lastName);
    }

    @And("user enter Coach email id {string}")
    public void userEnterCoachEmailId(String emailId) {
        programPage.enterStaffEmailId(emailId);
    }

    @And("user click and select the timezone")
    public void userClickAndSelectTheTimezone() {
        programPage.clickAndSelectTimezone();
    }

    @And("confirm to click on save button")
    public void confirmToClickOnSaveButton() {
        programPage.confirmClickOnSaveButton();
    }

    @Then("new coach is added")
    public void newCoachIsAdded() {
        programPage.verifyStaffIsAdded();
    }

    @And("user click assign button of assistant coach")
    public void userClickAssignButtonOfAssistantCoach() {
        programPage.clickAssignButtonOfAssistantCoach();
    }

    @And("user enter Assistant Coach first name {string} and last name {string}")
    public void userEnterAssistantCoachFirstNameAndLastName(String firstName, String lastName) {
        programPage.enterStaffFirstNameAndLastName(firstName, lastName);
    }

    @And("user enter Assistant Coach email id {string}")
    public void userEnterAssistantCoachEmailId(String emailId) {
        programPage.enterStaffEmailId(emailId);
    }

    @Then("new assistant coach is added")
    public void newAssistantCoachIsAdded() {
        programPage.verifyStaffIsAdded();
    }

    @And("user click assign button of manager")
    public void userClickAssignButtonOfManager() {
        programPage.clickAssignButtonOfManager();
    }

    @And("user enter Manager first name {string} and last name {string}")
    public void userEnterManagerFirstNameAndLastName(String firstName, String lastName) {
        programPage.enterStaffFirstNameAndLastName(firstName, lastName);
    }

    @And("user enter Manager email id {string}")
    public void userEnterManagerEmailId(String emailId) {
        programPage.enterStaffEmailId(emailId);
    }

    @Then("new manager is added")
    public void newManagerIsAdded() {
        programPage.verifyStaffIsAdded();
    }

    @And("user enter student manager first name {string} and last name {string}")
    public void userEnterStudentManagerFirstNameAndLastName(String firstName, String lastName) {
        programPage.enterStaffFirstNameAndLastName(firstName, lastName);
    }

    @And("user enter student manager email id {string}")
    public void userEnterStudentManagerEmailId(String emailId) {
        programPage.enterStaffEmailId(emailId);
    }

    @Then("student manager is added")
    public void studentManagerIsAdded() {
        programPage.verifyStaffIsAdded();
    }
}


