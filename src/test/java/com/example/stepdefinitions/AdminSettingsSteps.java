package com.example.stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.AdminSettingsPage;
import com.example.pages.AuditLogsPage;
import com.example.utils.UsersPageContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class AdminSettingsSteps {

    private final WebDriver driver = Hooks.driver;
    private final AdminSettingsPage adminSettingsPage = new AdminSettingsPage(driver);
    private final AuditLogsPage auditLogsPage = new AuditLogsPage(driver);

    @And("user click on Admin Settings")
    public void user_click_on_admin_settings() {
        if (UsersPageContext.isAuditLogsSection()) {
            auditLogsPage.clickAdminSettings();
            return;
        }
        UsersPageContext.setSection(UsersPageContext.Section.ADMIN_SETTINGS);
        UsersPageContext.clearSearchState();
        adminSettingsPage.clickAdminSettingsMenu();
    }

    @When("user validate Enable Free Subscription")
    public void user_validate_enable_free_subscription() {
        Assert.assertTrue(
                "Enable Free Subscription is not displayed (expected semantics Enable Free Subscription)",
                adminSettingsPage.isEnableFreeSubscriptionDisplayed()
        );
    }

    @Then("user validate toggle button of Enable Free Subscription")
    public void user_validate_toggle_button_of_enable_free_subscription() {
        Assert.assertTrue(
                "Enable Free Subscription toggle button is not available"
                        + " (expected semantics Enable Free Subscription toggle switch)",
                adminSettingsPage.isEnableFreeSubscriptionToggleDisplayed()
        );
    }

    @When("user validate Automated Test User Creation")
    public void user_validate_automated_test_user_creation() {
        Assert.assertTrue(
                "Automated Test User Creation is not displayed"
                        + " (expected semantics Enable Automated Test User Creation)",
                adminSettingsPage.isAutomatedTestUserCreationDisplayed()
        );
    }

    @Then("user validate toggle button of Automated Test User Creation")
    public void user_validate_toggle_button_of_automated_test_user_creation() {
        Assert.assertTrue(
                "Automated Test User Creation toggle button is not available"
                        + " (expected semantics Enable Automated Test User Creation toggle switch)",
                adminSettingsPage.isAutomatedTestUserCreationToggleDisplayed()
        );
    }

    @When("user validate Enable Seniority Stars")
    public void user_validate_enable_seniority_stars() {
        Assert.assertTrue(
                "Enable Seniority Stars is not displayed (expected semantics Enable Seniority Stars"
                        + " or Role Band Matching Affinity)",
                adminSettingsPage.isEnableSeniorityStarsDisplayed()
        );
    }

    @Then("user validate toggle button of Enable Seniority Stars")
    public void user_validate_toggle_button_of_enable_seniority_stars() {
        Assert.assertTrue(
                "Enable Seniority Stars toggle button is not available"
                        + " (expected semantics Enable Seniority Stars toggle switch)",
                adminSettingsPage.isEnableSeniorityStarsToggleDisplayed()
        );
    }

    @When("user validate Enable White Label Branding")
    public void user_validate_enable_white_label_branding() {
        Assert.assertTrue(
                "Enable White Label Branding is not displayed"
                        + " (expected semantics Enable White Label Branding)",
                adminSettingsPage.isEnableWhiteLabelBrandingDisplayed()
        );
    }

    @Then("user validate toggle button of Enable White Label Branding")
    public void user_validate_toggle_button_of_enable_white_label_branding() {
        Assert.assertTrue(
                "Enable White Label Branding toggle button is not available"
                        + " (expected semantics Enable White Label Branding toggle switch)",
                adminSettingsPage.isEnableWhiteLabelBrandingToggleDisplayed()
        );
    }

    @When("user validate Cross Path Cool Down Period")
    public void user_validate_cross_path_cool_down_period() {
        Assert.assertTrue(
                "Cross Path Cool Down Period is not displayed"
                        + " (expected semantics Cross Path Cool Down Period"
                        + " or Crossed Paths Notification Cooldown)",
                adminSettingsPage.isCrossPathCoolDownPeriodDisplayed()
        );
    }

    @And("user enter value")
    public void user_enter_value() {
        adminSettingsPage.enterCoolDownPeriodValue();
    }

    @Then("user validate save button of Cross Path Cool Down Period")
    public void user_validate_save_button_of_cross_path_cool_down_period() {
        Assert.assertTrue(
                "Cross Path Cool Down Period save button is not displayed"
                        + " (expected semantics cooldown_period_field_save_button)",
                adminSettingsPage.isCoolDownPeriodSaveButtonDisplayed()
        );
    }

    @When("user validate Splash Screen Experience")
    public void user_validate_splash_screen_experience() {
        Assert.assertTrue(
                "Splash Screen Experience is not displayed"
                        + " (expected semantics Splash Screen Experience)",
                adminSettingsPage.isSplashScreenExperienceDisplayed()
        );
    }

    @Then("user validate Splash Screen dropdown")
    public void user_validate_splash_screen_dropdown() {
        Assert.assertTrue(
                "Splash Screen Experience dropdown is not displayed"
                        + " (expected semantics Splash Screen Experience dropdown or Woodhouse)",
                adminSettingsPage.isSplashScreenDropdownDisplayed()
        );
    }

    @When("user validate Splash Screen Display Duration")
    public void user_validate_splash_screen_display_duration() {
        Assert.assertTrue(
                "Splash Screen Display Duration is not displayed"
                        + " (expected semantics Splash Screen Display Duration)",
                adminSettingsPage.isSplashScreenDisplayDurationDisplayed()
        );
    }

    @And("user enter duration")
    public void user_enter_duration() {
        adminSettingsPage.enterSplashScreenDisplayDuration();
    }

    @Then("user validate save button of Splash Screen Display Duration")
    public void user_validate_save_button_of_splash_screen_display_duration() {
        Assert.assertTrue(
                "Splash Screen Display Duration save button is not displayed"
                        + " (expected semantics splash_screen_display_duration_field_save_button)",
                adminSettingsPage.isSplashScreenDisplayDurationSaveButtonDisplayed()
        );
    }
}
