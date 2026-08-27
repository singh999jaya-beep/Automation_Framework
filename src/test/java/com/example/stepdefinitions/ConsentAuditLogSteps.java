package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.AuditLogsPage;
import com.example.pages.ConsentAuditLogPage;
import com.example.pages.ManageAdminUsersPage;
import com.example.utils.UsersPageContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class ConsentAuditLogSteps {

    private final WebDriver driver = Hooks.driver;
    private final ConsentAuditLogPage consentAuditLogPage = new ConsentAuditLogPage(driver);
    private final ManageAdminUsersPage manageAdminUsersPage = new ManageAdminUsersPage(driver);
    private final AuditLogsPage auditLogsPage = new AuditLogsPage(driver);

    @When("user clicks on Consent Audit Log menu")
    public void user_clicks_on_consent_audit_log_menu() {
        UsersPageContext.setSection(UsersPageContext.Section.CONSENT_AUDIT_LOG);
        UsersPageContext.clearSearchState();
        consentAuditLogPage.clickConsentAuditLogMenu();
    }

    @And("user click on Export CSV")
    public void user_click_on_export_csv() {
        consentAuditLogPage.clickExportCsv();
    }

    @Then("user validate Exported CSV")
    public void user_validate_exported_csv() {
        Assert.assertTrue(
                "Exported CSV is not valid",
                consentAuditLogPage.isExportedCsvValid()
        );
    }

    @And("user click on All Apps")
    public void user_click_on_all_apps() {
        consentAuditLogPage.clickAllApps();
    }

    @And("user select Virtual Intros")
    public void user_select_virtual_intros() {
        consentAuditLogPage.selectVirtualIntros();
    }

    @Then("user validate Virtual Intros")
    public void user_validate_virtual_intros() {
        Assert.assertTrue(
                "Virtual Intros is not displayed",
                consentAuditLogPage.isVirtualIntrosDisplayed()
        );
    }

    @And("user click on All Categories")
    public void user_click_on_all_categories() {
        consentAuditLogPage.clickAllCategories();
    }

    @And("user select Privacy Policy")
    public void user_select_privacy_policy() {
        consentAuditLogPage.selectPrivacyPolicy();
    }

    @Then("user validate Privacy Policy")
    public void user_validate_privacy_policy() {
        Assert.assertTrue(
                "Privacy Policy is not displayed",
                consentAuditLogPage.isPrivacyPolicyDisplayed()
        );
    }

    @And("user select Terms of Service")
    public void user_select_terms_of_service() {
        consentAuditLogPage.selectTermsOfService();
    }

    @Then("user validate Terms of Service")
    public void user_validate_terms_of_service() {
        Assert.assertTrue(
                "Terms of Service is not displayed",
                consentAuditLogPage.isTermsOfServiceDisplayed()
        );
    }

    @And("user select Marketing Communication")
    public void user_select_marketing_communication() {
        consentAuditLogPage.selectMarketingCommunication();
    }

    @Then("user validate Marketing Communication")
    public void user_validate_marketing_communication() {
        Assert.assertTrue(
                "Marketing Communication is not displayed",
                consentAuditLogPage.isMarketingCommunicationDisplayed()
        );
    }

    @And("user select Customise Recommendation")
    public void user_select_customise_recommendation() {
        consentAuditLogPage.selectCustomiseRecommendation();
    }

    @Then("user validate Customise Recommendation")
    public void user_validate_customise_recommendation() {
        Assert.assertTrue(
                "Customise Recommendation is not displayed",
                consentAuditLogPage.isCustomiseRecommendationDisplayed()
        );
    }

    @And("user select Notification")
    public void user_select_notification() {
        consentAuditLogPage.selectNotification();
    }

    @Then("user validate Notification")
    public void user_validate_notification() {
        Assert.assertTrue(
                "Notification is not displayed",
                consentAuditLogPage.isNotificationDisplayed()
        );
    }

    @And("user select Location")
    public void user_select_location() {
        consentAuditLogPage.selectLocation();
    }

    @Then("user validate Location")
    public void user_validate_location() {
        Assert.assertTrue(
                "Location is not displayed",
                consentAuditLogPage.isLocationDisplayed()
        );
    }

    @And("user select Gender")
    public void user_select_gender() {
        consentAuditLogPage.selectGender();
    }

    @Then("user validate Gender")
    public void user_validate_gender() {
        Assert.assertTrue(
                "Gender is not displayed",
                consentAuditLogPage.isGenderDisplayed()
        );
    }

    @And("user select Ethnicity")
    public void user_select_ethnicity() {
        consentAuditLogPage.selectEthnicity();
    }

    @Then("user validate Ethnicity")
    public void user_validate_ethnicity() {
        Assert.assertTrue(
                "Ethnicity is not displayed",
                consentAuditLogPage.isEthnicityDisplayed()
        );
    }

    @And("user click on All Actions")
    public void user_click_on_all_actions() {
        if (UsersPageContext.isAuditLogsSection()) {
            auditLogsPage.clickAllActions();
            return;
        }
        consentAuditLogPage.clickAllActions();
    }

    @And("user select Pending")
    public void user_select_pending() {
        if (UsersPageContext.isManageAdminUsersSection()) {
            manageAdminUsersPage.selectPendingStatus();
            return;
        }
        consentAuditLogPage.selectPending();
    }

    @And("user select Granted")
    public void user_select_granted() {
        consentAuditLogPage.selectGranted();
    }

    @Then("user validate Granted")
    public void user_validate_granted() {
        Assert.assertTrue(
                "Granted is not displayed",
                consentAuditLogPage.isGrantedDisplayed()
        );
    }

    @And("user select Withdrawn")
    public void user_select_withdrawn() {
        consentAuditLogPage.selectWithdrawn();
    }

    @Then("user validate Withdrawn")
    public void user_validate_withdrawn() {
        Assert.assertTrue(
                "Withdrawn is not displayed",
                consentAuditLogPage.isWithdrawnDisplayed()
        );
    }

    @And("user click on Search")
    public void user_click_on_search() {
        if (UsersPageContext.isAuditLogsSection()) {
            auditLogsPage.clickSearch();
            return;
        }
        if (UsersPageContext.isManageAdminUsersSection()) {
            manageAdminUsersPage.clickSearch();
            return;
        }
        consentAuditLogPage.clickSearch();
    }

    @Then("user search id {string}")
    public void user_search_id(String id) {
        consentAuditLogPage.searchId(id);
        Assert.assertTrue(
                "Search id is not displayed: " + id,
                consentAuditLogPage.isSearchIdDisplayed(id)
        );
    }
}
