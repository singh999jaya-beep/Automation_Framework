package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.CompanyUsersPage;
import com.example.pages.ConsentAuditLogPage;
import com.example.pages.DeletedAppUsersPage;
import com.example.pages.IndividualUsersPage;
import com.example.pages.ManageAdminUsersPage;
import com.example.pages.ManageEventsPage;
import com.example.pages.ModerationCenterPage;
import com.example.pages.ManageRolesPage;
import com.example.pages.TransactionsPage;
import com.example.utils.UsersPageContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class UsersCommonSteps {

    private final WebDriver driver = Hooks.driver;
    private final IndividualUsersPage individualUsersPage = new IndividualUsersPage(driver);
    private final CompanyUsersPage companyUsersPage = new CompanyUsersPage(driver);
    private final ManageEventsPage manageEventsPage = new ManageEventsPage(driver);
    private final TransactionsPage transactionsPage = new TransactionsPage(driver);
    private final ModerationCenterPage moderationCenterPage = new ModerationCenterPage(driver);
    private final ConsentAuditLogPage consentAuditLogPage = new ConsentAuditLogPage(driver);
    private final ManageRolesPage manageRolesPage = new ManageRolesPage(driver);
    private final ManageAdminUsersPage manageAdminUsersPage = new ManageAdminUsersPage(driver);
    private final DeletedAppUsersPage deletedAppUsersPage = new DeletedAppUsersPage(driver);

    @And("user click on search bar")
    public void user_click_on_search_bar() {
        if (UsersPageContext.isModerationSection()) {
            moderationCenterPage.clickSearchBar();
            return;
        }
        if (UsersPageContext.isTransactionsSection()) {
            transactionsPage.clickSearchBar();
            return;
        }
        if (UsersPageContext.isEventsSection()) {
            manageEventsPage.clickSearchBar();
            return;
        }
        if (UsersPageContext.isCompanySection()) {
            companyUsersPage.clickSearchBar();
            return;
        }
        individualUsersPage.clickSearchBar();
    }

    @And("user enter email {string}")
    public void user_enter_email(String email) {
        UsersPageContext.setSearchedEmail(email);
        UsersPageContext.setSearchedName(null);
        if (UsersPageContext.isTransactionsSection()) {
            transactionsPage.enterSearchEmail(email);
            return;
        }
        if (UsersPageContext.isCompanySection()) {
            companyUsersPage.enterSearchEmail(email);
            return;
        }
        individualUsersPage.enterSearchEmail(email);
    }

    @And("user enter name {string}")
    public void user_enter_name(String name) {
        UsersPageContext.setSearchedName(name);
        UsersPageContext.setSearchedEmail(null);
        UsersPageContext.setSearchedEventId(null);
        if (UsersPageContext.isManageRolesSection()) {
            manageRolesPage.enterRoleSearchName(name);
            return;
        }
        if (UsersPageContext.isTransactionsSection()) {
            transactionsPage.enterSearchName(name);
            return;
        }
        if (UsersPageContext.isEventsSection()) {
            manageEventsPage.enterSearchName(name);
            return;
        }
        if (UsersPageContext.isCompanySection()) {
            companyUsersPage.enterSearchName(name);
            return;
        }
        if (UsersPageContext.isModerationSection()) {
            if (name != null && name.contains("@")) {
                moderationCenterPage.enterSearchEmail(name);
            } else {
                moderationCenterPage.enterSearchName(name);
            }
            return;
        }
        individualUsersPage.enterSearchName(name);
    }

    @And("user click on eye icon")
    public void user_click_on_eye_icon() {
        if (UsersPageContext.isTransactionsSection()) {
            transactionsPage.clickEyeIcon();
            return;
        }
        if (UsersPageContext.isCompanySection()) {
            companyUsersPage.clickEyeIcon();
            return;
        }
        individualUsersPage.clickEyeIcon();
    }

    @Then("user validate email")
    public void user_validate_email() {
        String searchedName = UsersPageContext.getSearchedName();
        String searchedEmail = UsersPageContext.getSearchedEmail();
        String email = searchedEmail != null && !searchedEmail.isBlank() ? searchedEmail : searchedName;
        if (UsersPageContext.isModerationSection()) {
            Assert.assertTrue(
                    "Email is not displayed and No data available message is not shown",
                    moderationCenterPage.isEmailDisplayedOrNoDataAvailable(email)
            );
            return;
        }
        if (UsersPageContext.isTransactionsSection()) {
            Assert.assertTrue(
                    "Mail id is not displayed",
                    transactionsPage.isMailIdDisplayed(email)
            );
            return;
        }
        if (UsersPageContext.isCompanySection()) {
            Assert.assertTrue(
                    "Mail id is not displayed",
                    companyUsersPage.isMailIdDisplayed(email)
            );
            return;
        }
        Assert.assertTrue(
                "Mail id is not displayed",
                individualUsersPage.isMailIdDisplayed(email)
        );
    }

    @Then("user validate mail id")
    public void user_validate_mail_id() {
        String searchedEmail = UsersPageContext.getSearchedEmail();
        if (UsersPageContext.isTransactionsSection()) {
            Assert.assertTrue(
                    "Mail id is not displayed",
                    transactionsPage.isMailIdDisplayed(searchedEmail)
            );
            return;
        }
        if (UsersPageContext.isCompanySection()) {
            Assert.assertTrue(
                    "Mail id is not displayed",
                    companyUsersPage.isMailIdDisplayed(searchedEmail)
            );
            return;
        }
        Assert.assertTrue(
                "Mail id is not displayed",
                individualUsersPage.isMailIdDisplayed(searchedEmail)
        );
    }

    @Then("user validate mail id or No data available")
    public void user_validate_mail_id_or_no_data_available() {
        Assert.assertTrue(
                "Mail id is not displayed and No data available message is not shown",
                transactionsPage.isMailIdDisplayedOrNoDataAvailable(UsersPageContext.getSearchedEmail())
        );
    }

    @Then("user validate name")
    public void user_validate_name() {
        String searchedName = UsersPageContext.getSearchedName();
        if (UsersPageContext.isModerationSection()) {
            Assert.assertTrue(
                    "Name is not displayed and No data available message is not shown",
                    moderationCenterPage.isNameDisplayedOrNoDataAvailable(searchedName)
            );
            return;
        }
        if (UsersPageContext.isTransactionsSection()) {
            Assert.assertTrue(
                    "Name is not displayed and No data available message is not shown",
                    transactionsPage.isNameDisplayedOrNoDataAvailable(searchedName)
            );
            return;
        }
        if (UsersPageContext.isEventsSection()) {
            Assert.assertTrue(
                    "Name is not displayed and No data available message is not shown",
                    manageEventsPage.isNameDisplayedOrNoDataAvailable(searchedName)
            );
            return;
        }
        if (UsersPageContext.isCompanySection()) {
            Assert.assertTrue(
                    "Name is not displayed",
                    companyUsersPage.isNameDisplayed(searchedName)
            );
            return;
        }
        Assert.assertTrue(
                "Name is not displayed",
                individualUsersPage.isNameDisplayed(searchedName)
        );
    }

    @Then("user validate name or No data available")
    public void user_validate_name_or_no_data_available() {
        if (UsersPageContext.isModerationSection()) {
            Assert.assertTrue(
                    "Name is not displayed and No data available message is not shown",
                    moderationCenterPage.isNameDisplayedOrNoDataAvailable(UsersPageContext.getSearchedName())
            );
            return;
        }
        if (UsersPageContext.isTransactionsSection()) {
            Assert.assertTrue(
                    "Name is not displayed and No data available message is not shown",
                    transactionsPage.isNameDisplayedOrNoDataAvailable(UsersPageContext.getSearchedName())
            );
            return;
        }
        Assert.assertTrue(
                "Name is not displayed and No data available message is not shown",
                manageEventsPage.isNameDisplayedOrNoDataAvailable(UsersPageContext.getSearchedName())
        );
    }

    @Then("user validate email or No data available")
    public void user_validate_email_or_no_data_available() {
        String searchedName = UsersPageContext.getSearchedName();
        String searchedEmail = UsersPageContext.getSearchedEmail();
        String email = searchedEmail != null && !searchedEmail.isBlank() ? searchedEmail : searchedName;
        Assert.assertTrue(
                "Email is not displayed and No data available message is not shown",
                moderationCenterPage.isEmailDisplayedOrNoDataAvailable(email)
        );
    }

    @And("user scroll towards right")
    public void user_scroll_towards_right() {
        if (UsersPageContext.isDeletedAppUsersSection()) {
            deletedAppUsersPage.scrollTowardsRight();
            return;
        }
        if (UsersPageContext.isTransactionsSection()) {
            transactionsPage.scrollTowardsRight();
            return;
        }
        if (UsersPageContext.isModerationSection()) {
            moderationCenterPage.scrollTowardsRight();
            return;
        }
        if (UsersPageContext.isCompanySection()) {
            companyUsersPage.scrollTowardsRight();
            return;
        }
        if (UsersPageContext.isConsentAuditLogSection()) {
            consentAuditLogPage.scrollTowardsRight();
            return;
        }
        individualUsersPage.scrollTowardsRight();
    }

    @And("user click on status")
    public void user_click_on_status() {
        if (UsersPageContext.isModerationSection()) {
            moderationCenterPage.clickStatus();
            return;
        }
        if (UsersPageContext.isCompanySection()) {
            companyUsersPage.clickStatus();
            return;
        }
        individualUsersPage.clickStatus();
    }

    @And("user select Active")
    public void user_select_active() {
        if (UsersPageContext.isManageAdminUsersSection()) {
            manageAdminUsersPage.selectActiveStatus();
            return;
        }
        if (UsersPageContext.isCompanySection()) {
            companyUsersPage.clickActiveStatus();
            return;
        }
        individualUsersPage.clickActiveStatus();
    }

    @And("user click on active status")
    public void user_click_on_active_status() {
        if (UsersPageContext.isCompanySection()) {
            companyUsersPage.clickActiveStatus();
            return;
        }
        individualUsersPage.clickActiveStatus();
    }

    @And("user select Inactive")
    public void user_select_inactive() {
        if (UsersPageContext.isCompanySection()) {
            companyUsersPage.clickInactiveStatus();
            return;
        }
        individualUsersPage.clickInactiveStatus();
    }

    @And("user click on inactive status")
    public void user_click_on_inactive_status() {
        if (UsersPageContext.isCompanySection()) {
            companyUsersPage.clickInactiveStatus();
            return;
        }
        individualUsersPage.clickInactiveStatus();
    }

    @Then("user validate Active")
    public void user_validate_active() {
        if (UsersPageContext.isCompanySection()) {
            Assert.assertTrue(
                    "Active status is not displayed",
                    companyUsersPage.isActiveStatusDisplayed()
            );
            return;
        }
        Assert.assertTrue(
                "Active status is not displayed",
                individualUsersPage.isActiveStatusDisplayed()
        );
    }

    @Then("user validate Inactive")
    public void user_validate_inactive() {
        if (UsersPageContext.isCompanySection()) {
            Assert.assertTrue(
                    "Blocked status is not displayed",
                    companyUsersPage.isInactiveOrBlockedStatusDisplayed()
            );
            return;
        }
        Assert.assertTrue(
                "Inactive or Blocked status is not displayed",
                individualUsersPage.isInactiveOrBlockedStatusDisplayed()
        );
    }
}
