package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.AuditLogsPage;
import com.example.pages.DeletedAppUsersPage;
import com.example.utils.UsersPageContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class AuditLogsSteps {

    private final WebDriver driver = Hooks.driver;
    private final AuditLogsPage auditLogsPage = new AuditLogsPage(driver);
    private final DeletedAppUsersPage deletedAppUsersPage = new DeletedAppUsersPage(driver);

    @When("user click on Monitoring menu")
    public void user_click_on_monitoring_menu() {
        UsersPageContext.setSection(UsersPageContext.Section.AUDIT_LOGS);
        UsersPageContext.clearSearchState();
        auditLogsPage.clickMonitoringMenu();
    }

    @And("user click on Audit Logs")
    public void user_click_on_audit_logs() {
        UsersPageContext.setSection(UsersPageContext.Section.AUDIT_LOGS);
        if (auditLogsPage.isModulesDropdownOpened()) {
            auditLogsPage.clickAuditLogsModuleOption();
            return;
        }
        UsersPageContext.clearSearchState();
        auditLogsPage.clickAuditLogsMenu();
    }

    @Then("user validate Audit Logs")
    public void user_validate_audit_logs() {
        if (auditLogsPage.isAuditLogsModuleFilterSelected()) {
            Assert.assertTrue(
                    "Audit Logs module filter is not displayed (expected semantics audit_logs_option_audit_logs)",
                    auditLogsPage.isAuditLogsModuleDisplayed()
            );
            return;
        }
        Assert.assertTrue(
                "Audit Logs page is not displayed (expected semantics export_logs_button or all_modules_option_all_modules)",
                auditLogsPage.isAuditLogsDisplayed()
        );
    }

    @Then("user click on Export Logs")
    public void user_click_on_export_logs() {
        auditLogsPage.clickExportLogs();
    }

    @And("user click on All Modules")
    public void user_click_on_all_modules() {
        auditLogsPage.clickAllModules();
    }

    @And("user click on Individual Users")
    public void user_click_on_individual_users() {
        auditLogsPage.clickIndividualUsers();
    }

    @Then("user validate Individual Users")
    public void user_validate_individual_users() {
        Assert.assertTrue(
                "Individual Users module filter is not displayed (expected semantics individual_users_option_individual_users)",
                auditLogsPage.isIndividualUsersDisplayed()
        );
    }

    @And("user click on Company Users")
    public void user_click_on_company_users() {
        auditLogsPage.clickCompanyUsers();
    }

    @Then("user validate Company Users")
    public void user_validate_company_users() {
        Assert.assertTrue(
                "Company Users module filter is not displayed (expected semantics company_users_option_company_users)",
                auditLogsPage.isCompanyUsersDisplayed()
        );
    }

    @And("user click on Input Categories")
    public void user_click_on_input_categories() {
        auditLogsPage.clickInputCategories();
    }

    @Then("user validate Input Categories")
    public void user_validate_input_categories() {
        Assert.assertTrue(
                "Input Categories module filter is not displayed (expected semantics input_categories_option_input_categories)",
                auditLogsPage.isInputCategoriesDisplayed()
        );
    }

    @And("user click on Events")
    public void user_click_on_events() {
        auditLogsPage.clickEvents();
    }

    @Then("user validate Events")
    public void user_validate_events() {
        Assert.assertTrue(
                "Events module filter is not displayed (expected semantics events_option_events)",
                auditLogsPage.isEventsDisplayed()
        );
    }

    @And("user click on Advertisement")
    public void user_click_on_advertisement() {
        auditLogsPage.clickAdvertisement();
    }

    @And("user click on Transactions")
    public void user_click_on_transactions() {
        auditLogsPage.clickTransactions();
    }

    @Then("user validate Transactions")
    public void user_validate_transactions() {
        Assert.assertTrue(
                "Transactions module filter is not displayed (expected semantics transactions_option_transactions)",
                auditLogsPage.isTransactionsDisplayed()
        );
    }

    @And("user click on Moderation Center")
    public void user_click_on_moderation_center() {
        auditLogsPage.clickModerationCenter();
    }

    @Then("user validate Moderation Center")
    public void user_validate_moderation_center() {
        Assert.assertTrue(
                "Moderation Center module filter is not displayed (expected semantics moderation_center_option_moderation_center)",
                auditLogsPage.isModerationCenterDisplayed()
        );
    }

    @Then("user validate Admin Settings")
    public void user_validate_admin_settings() {
        Assert.assertTrue(
                "Admin Settings module filter is not displayed (expected semantics admin_settings_option_admin_settings)",
                auditLogsPage.isAdminSettingsDisplayed()
        );
    }

    @And("user click on Manage Roles")
    public void user_click_on_manage_roles() {
        auditLogsPage.clickManageRoles();
    }

    @Then("user validate Manage Roles")
    public void user_validate_manage_roles() {
        Assert.assertTrue(
                "Manage Roles module filter is not displayed (expected semantics manage_roles_option_manage_roles)",
                auditLogsPage.isManageRolesDisplayed()
        );
    }

    @Then("user validate Manage Admin Users")
    public void user_validate_manage_admin_users() {
        Assert.assertTrue(
                "Manage Admin Users module filter is not displayed (expected semantics manage_admin_users_option_manage_admin_users)",
                auditLogsPage.isManageAdminUsersDisplayed()
        );
    }

    @And("user click on Deeplink History")
    public void user_click_on_deeplink_history() {
        auditLogsPage.clickDeeplinkHistory();
    }

    @Then("user validate Deeplink History")
    public void user_validate_deeplink_history() {
        Assert.assertTrue(
                "Deeplink History module filter is not displayed (expected semantics deeplink_history_option_deeplink_history)",
                auditLogsPage.isDeeplinkHistoryDisplayed()
        );
    }

    @And("user click on Consent Audit Log")
    public void user_click_on_consent_audit_log() {
        auditLogsPage.clickConsentAuditLog();
    }

    @Then("user validate Consent Audit Log")
    public void user_validate_consent_audit_log() {
        Assert.assertTrue(
                "Consent Audit Log module filter is not displayed (expected semantics consent_audit_log_option_consent_audit_log)",
                auditLogsPage.isConsentAuditLogDisplayed()
        );
    }

    @And("user click on Content Versions")
    public void user_click_on_content_versions() {
        auditLogsPage.clickContentVersions();
    }

    @Then("user validate Content Versions")
    public void user_validate_content_versions() {
        Assert.assertTrue(
                "Content Versions module filter is not displayed (expected semantics content_versions_option_content_versions)",
                auditLogsPage.isContentVersionsDisplayed()
        );
    }

    @And("user click on Deleted Users")
    public void user_click_on_deleted_users() {
        auditLogsPage.clickDeletedUsers();
    }

    @Then("user validate Deleted Users")
    public void user_validate_deleted_users() {
        Assert.assertTrue(
                "Deleted Users module filter is not displayed (expected semantics deleted_users_option_deleted_users)",
                auditLogsPage.isDeletedUsersDisplayed()
        );
    }

    @And("user click on Created")
    public void user_click_on_created() {
        auditLogsPage.clickCreated();
    }

    @Then("user validate Created")
    public void user_validate_created() {
        Assert.assertTrue(
                "Created action filter is not displayed (expected semantics created_option_created)",
                auditLogsPage.isCreatedDisplayed()
        );
    }

    @And("user click on Updated")
    public void user_click_on_updated() {
        auditLogsPage.clickUpdated();
    }

    @Then("user validate Updated")
    public void user_validate_updated() {
        Assert.assertTrue(
                "Updated action filter is not displayed (expected semantics updated_option_updated)",
                auditLogsPage.isUpdatedDisplayed()
        );
    }

    @And("user click on Deleted")
    public void user_click_on_deleted() {
        if (UsersPageContext.isDeletedAppUsersSection()) {
            deletedAppUsersPage.clickDeletedStatus();
            return;
        }
        auditLogsPage.clickDeleted();
    }

    @Then("user validate Deleted")
    public void user_validate_deleted() {
        Assert.assertTrue(
                "Deleted action filter is not displayed (expected semantics deleted_option_deleted)",
                auditLogsPage.isDeletedDisplayed()
        );
    }

    @And("user click on Unblocked")
    public void user_click_on_unblocked() {
        auditLogsPage.clickUnblocked();
    }

    @Then("user validate Unblocked")
    public void user_validate_unblocked() {
        Assert.assertTrue(
                "Unblocked action filter is not displayed (expected semantics unblocked_option_unblocked)",
                auditLogsPage.isUnblockedDisplayed()
        );
    }

    @And("user click on Approved")
    public void user_click_on_approved() {
        auditLogsPage.clickApproved();
    }

    @Then("user validate Approved")
    public void user_validate_approved() {
        Assert.assertTrue(
                "Approved action filter is not displayed (expected semantics approved_option_approved)",
                auditLogsPage.isApprovedDisplayed()
        );
    }

    @And("user click on Rejected")
    public void user_click_on_rejected() {
        auditLogsPage.clickRejected();
    }

    @Then("user validate Rejected")
    public void user_validate_rejected() {
        Assert.assertTrue(
                "Rejected action filter is not displayed (expected semantics rejected_option_rejected)",
                auditLogsPage.isRejectedDisplayed()
        );
    }

    @And("user click on Closed")
    public void user_click_on_closed() {
        auditLogsPage.clickClosed();
    }

    @Then("user validate Closed")
    public void user_validate_closed() {
        Assert.assertTrue(
                "Closed action filter is not displayed (expected semantics closed_option_closed)",
                auditLogsPage.isClosedDisplayed()
        );
    }

    @And("user click on Invited")
    public void user_click_on_invited() {
        auditLogsPage.clickInvited();
    }

    @Then("user validate Invited")
    public void user_validate_invited() {
        Assert.assertTrue(
                "Invited action filter is not displayed (expected semantics invited_option_invited)",
                auditLogsPage.isInvitedDisplayed()
        );
    }

    @And("user click on Enabled")
    public void user_click_on_enabled() {
        auditLogsPage.clickEnabled();
    }

    @Then("user validate Enabled")
    public void user_validate_enabled() {
        Assert.assertTrue(
                "Enabled action filter is not displayed (expected semantics enabled_option_enabled)",
                auditLogsPage.isEnabledDisplayed()
        );
    }

    @And("user click on Expired")
    public void user_click_on_expired() {
        auditLogsPage.clickExpired();
    }

    @Then("user validate Expired")
    public void user_validate_expired() {
        Assert.assertTrue(
                "Expired action filter is not displayed (expected semantics expired_option_expired)",
                auditLogsPage.isExpiredDisplayed()
        );
    }

    @Then("user enter {string}")
    public void user_enter(String text) {
        UsersPageContext.setSearchedName(text);
        auditLogsPage.enterSearch(text);
        Assert.assertTrue(
                "Search result for Admin Name is not displayed: " + text,
                auditLogsPage.isSearchResultDisplayed(text)
        );
    }
}
