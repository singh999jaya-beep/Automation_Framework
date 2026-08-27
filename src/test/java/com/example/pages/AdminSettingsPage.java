package com.example.pages;

import com.example.pages.support.FlutterSemanticsSupport;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

public class AdminSettingsPage {

    private static final String ADMIN_SETTINGS_TEXT = "Admin Settings";

    private static final String SEMANTICS_MENU = "drawer_item_admin_settings";
    private static final String SEMANTICS_ENABLE_FREE_SUBSCRIPTION = "Enable Free Subscription";
    private static final String SEMANTICS_ENABLE_FREE_SUBSCRIPTION_TOGGLE =
            "Enable Free Subscription toggle switch";
    private static final String SEMANTICS_AUTOMATED_TEST_USER_CREATION =
            "Enable Automated Test User Creation";
    private static final String SEMANTICS_AUTOMATED_TEST_USER_CREATION_TOGGLE =
            "Enable Automated Test User Creation toggle switch";
    private static final String SEMANTICS_ENABLE_SENIORITY_STARS = "Enable Seniority Stars";
    private static final String SEMANTICS_ENABLE_SENIORITY_STARS_FALLBACK =
            "Role Band Matching Affinity";
    private static final String SEMANTICS_ENABLE_SENIORITY_STARS_TOGGLE =
            "Enable Seniority Stars  toggle switch";
    private static final String SEMANTICS_ENABLE_SENIORITY_STARS_TOGGLE_NORMALIZED =
            "Enable Seniority Stars toggle switch";
    private static final String SEMANTICS_ENABLE_WHITE_LABEL_BRANDING = "Enable White Label Branding";
    private static final String SEMANTICS_ENABLE_WHITE_LABEL_BRANDING_FALLBACK =
            "White-Label Branding";
    private static final String SEMANTICS_ENABLE_WHITE_LABEL_BRANDING_TOGGLE =
            "Enable White Label Branding toggle switch";
    private static final String SEMANTICS_CROSS_PATH_COOL_DOWN = "Cross Path Cool Down Period";
    private static final String SEMANTICS_CROSS_PATH_COOL_DOWN_FALLBACK =
            "Crossed Paths Notification Cooldown";
    private static final String SEMANTICS_ENTER_VALUE = "enter_value_field";
    private static final String SEMANTICS_COOLDOWN_SAVE = "cooldown_period_field_save_button";
    private static final String SEMANTICS_COOLDOWN_SAVE_FALLBACK = "Save";
    private static final String SEMANTICS_SPLASH_SCREEN_EXPERIENCE = "Splash Screen Experience";
    private static final String SEMANTICS_SPLASH_SCREEN_DROPDOWN = "Splash Screen Experience dropdown";
    private static final String SEMANTICS_SPLASH_SCREEN_DROPDOWN_FALLBACK = "Woodhouse";
    private static final String SEMANTICS_SPLASH_SCREEN_DISPLAY_DURATION =
            "Splash Screen Display Duration";
    private static final String SEMANTICS_SPLASH_SCREEN_DURATION_FIELD =
            "splash_screen_display_duration_field";
    private static final String SEMANTICS_SPLASH_SCREEN_DURATION_SAVE =
            "splash_screen_display_duration_field_save_button";

    /** Default cooldown value used by the feature step "user enter value". */
    private static final String DEFAULT_COOLDOWN_VALUE = "30";
    /** Default splash duration (seconds). UI guardrails are 1s to 4s. */
    private static final String DEFAULT_SPLASH_DURATION_VALUE = "2";

    private static final Duration DEFAULT_WAIT = FlutterSemanticsSupport.DEFAULT_WAIT;
    private static final Duration PAGE_POLL = Duration.ofSeconds(12);
    private static final int MAX_NAV_ATTEMPTS = 10;

    private static final Keys SELECT_ALL_MODIFIER =
            System.getProperty("os.name", "").toLowerCase().contains("mac")
                    ? Keys.COMMAND
                    : Keys.CONTROL;

    private static final String DISPATCH_POINTER_CLICK_SCRIPT =
            "function dispatchPointerClick(target) {" +
                    "  if (!target) return false;" +
                    "  target.scrollIntoView({block: 'center', inline: 'nearest'});" +
                    "  const rect = target.getBoundingClientRect();" +
                    "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                    "  const x = rect.left + rect.width / 2;" +
                    "  const y = rect.top + rect.height / 2;" +
                    "  const hit = document.elementFromPoint(x, y) || target;" +
                    "  ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                    "    hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                    "  });" +
                    "  return true;" +
                    "}";

    private static final String FIND_OUTSIDE_NAV_SCRIPT =
            "function navMenu() {" +
                    "  return document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                    "}" +
                    "function isInNav(node) {" +
                    "  const menu = navMenu();" +
                    "  return !!(menu && node && menu.contains(node));" +
                    "}";

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final FlutterSemanticsSupport semantics;

    private final By adminSettingsMenu = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_MENU + "']" +
                    " | //flt-semantics[@aria-label='Navigation menu']" +
                    "//flt-semantics[@role='button' and contains(., '" + ADMIN_SETTINGS_TEXT + "')]");

    private final By enterValueInput = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_ENTER_VALUE + "']//input" +
                    " | //input[@aria-label='" + SEMANTICS_ENTER_VALUE + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_ENTER_VALUE + "')]//input" +
                    " | //input[contains(@aria-label,'" + SEMANTICS_ENTER_VALUE + "')]");

    private final By splashDurationInput = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_SPLASH_SCREEN_DURATION_FIELD + "']//input" +
                    " | //input[@aria-label='" + SEMANTICS_SPLASH_SCREEN_DURATION_FIELD + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_SPLASH_SCREEN_DURATION_FIELD + "')]//input" +
                    " | //input[contains(@aria-label,'" + SEMANTICS_SPLASH_SCREEN_DURATION_FIELD + "')]");

    public AdminSettingsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_WAIT);
        this.semantics = new FlutterSemanticsSupport(driver);
    }

    public void clickAdminSettingsMenu() {
        openAdminSettingsNavigation();
        if (!waitUntil(this::isAdminSettingsPageLoaded, DEFAULT_WAIT)) {
            throw new NoSuchElementException(
                    "Admin Settings page is not loaded (expected semantics "
                            + SEMANTICS_ENABLE_FREE_SUBSCRIPTION + " or text '"
                            + ADMIN_SETTINGS_TEXT + "'). Available outside-nav labels: "
                            + dumpOutsideNavSemanticsLabels());
        }
    }

    public boolean isEnableFreeSubscriptionDisplayed() {
        return isSettingLabelDisplayed(SEMANTICS_ENABLE_FREE_SUBSCRIPTION);
    }

    public boolean isEnableFreeSubscriptionToggleDisplayed() {
        return isToggleButtonAvailableNearSetting(
                SEMANTICS_ENABLE_FREE_SUBSCRIPTION_TOGGLE,
                SEMANTICS_ENABLE_FREE_SUBSCRIPTION);
    }

    public boolean isAutomatedTestUserCreationDisplayed() {
        return isSettingLabelDisplayed(SEMANTICS_AUTOMATED_TEST_USER_CREATION);
    }

    public boolean isAutomatedTestUserCreationToggleDisplayed() {
        return isToggleButtonAvailableNearSetting(
                SEMANTICS_AUTOMATED_TEST_USER_CREATION_TOGGLE,
                SEMANTICS_AUTOMATED_TEST_USER_CREATION);
    }

    public boolean isEnableSeniorityStarsDisplayed() {
        return isSettingLabelDisplayed(SEMANTICS_ENABLE_SENIORITY_STARS)
                || isSettingLabelDisplayed(SEMANTICS_ENABLE_SENIORITY_STARS_FALLBACK);
    }

    public boolean isEnableSeniorityStarsToggleDisplayed() {
        return isToggleButtonAvailableNearSetting(
                SEMANTICS_ENABLE_SENIORITY_STARS_TOGGLE,
                SEMANTICS_ENABLE_SENIORITY_STARS_TOGGLE_NORMALIZED,
                SEMANTICS_ENABLE_SENIORITY_STARS,
                SEMANTICS_ENABLE_SENIORITY_STARS_FALLBACK);
    }

    public boolean isEnableWhiteLabelBrandingDisplayed() {
        return isSettingLabelDisplayed(SEMANTICS_ENABLE_WHITE_LABEL_BRANDING)
                || isSettingLabelDisplayed(SEMANTICS_ENABLE_WHITE_LABEL_BRANDING_FALLBACK);
    }

    public boolean isEnableWhiteLabelBrandingToggleDisplayed() {
        return isToggleButtonAvailableNearSetting(
                SEMANTICS_ENABLE_WHITE_LABEL_BRANDING_TOGGLE,
                SEMANTICS_ENABLE_WHITE_LABEL_BRANDING,
                SEMANTICS_ENABLE_WHITE_LABEL_BRANDING_FALLBACK);
    }

    /**
     * Availability-only check for a toggle switch.
     * Checks the toggle semantics first, then a switch near the setting label.
     * Does not click or assert on/off state.
     */
    private boolean isToggleButtonAvailableNearSetting(String toggleSemanticsLabel, String... settingLabels) {
        ensureAdminSettingsPageReady();
        scrollToSemanticsLabel(toggleSemanticsLabel);
        if (isToggleButtonAvailable(toggleSemanticsLabel)) {
            return true;
        }
        for (String settingLabel : settingLabels) {
            scrollToSemanticsLabel(settingLabel);
            if (isToggleButtonAvailable(settingLabel) || isSwitchNearLabel(settingLabel)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Availability-only check for a toggle switch semantics node.
     * Does not click or assert on/off state.
     */
    private boolean isToggleButtonAvailable(String toggleSemanticsLabel) {
        semantics.enableFlutterSemantics();
        return waitUntil(d -> isSemanticsPresent(d, toggleSemanticsLabel), DEFAULT_WAIT);
    }

    private boolean isSwitchNearLabel(String settingLabel) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const wanted = (arguments[0] || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "const nodes = Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const labelNode = nodes.find(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const aria = (node.getAttribute('aria-label') || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  return aria === wanted || text === wanted || aria.includes(wanted) || text.includes(wanted);" +
                        "});" +
                        "if (!labelNode) return false;" +
                        "function hasSwitch(node) {" +
                        "  if (!node) return false;" +
                        "  const role = (node.getAttribute('role') || '').toLowerCase();" +
                        "  const aria = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (role === 'switch' || node.hasAttribute('aria-checked')" +
                        "      || aria.includes('toggle') || aria.includes('switch')) return true;" +
                        "  return Array.from(node.querySelectorAll('flt-semantics')).some(child => {" +
                        "    const childRole = (child.getAttribute('role') || '').toLowerCase();" +
                        "    const childAria = (child.getAttribute('aria-label') || '').toLowerCase();" +
                        "    return childRole === 'switch' || child.hasAttribute('aria-checked')" +
                        "      || childAria.includes('toggle') || childAria.includes('switch');" +
                        "  });" +
                        "}" +
                        "let ancestor = labelNode;" +
                        "for (let depth = 0; depth < 8 && ancestor; depth++) {" +
                        "  if (hasSwitch(ancestor)) return true;" +
                        "  ancestor = ancestor.parentElement;" +
                        "}" +
                        "return false;",
                settingLabel
        ));
    }

    public boolean isCrossPathCoolDownPeriodDisplayed() {
        return isSettingLabelDisplayed(SEMANTICS_CROSS_PATH_COOL_DOWN)
                || isSettingLabelDisplayed(SEMANTICS_CROSS_PATH_COOL_DOWN_FALLBACK);
    }

    public void enterCoolDownPeriodValue() {
        enterCoolDownPeriodValue(DEFAULT_COOLDOWN_VALUE);
    }

    public void enterCoolDownPeriodValue(String value) {
        ensureAdminSettingsPageReady();
        scrollToSemanticsLabel(SEMANTICS_CROSS_PATH_COOL_DOWN_FALLBACK);
        scrollToSemanticsLabel(SEMANTICS_ENTER_VALUE);
        WebElement input = waitForEnterValueInput();
        typeExactValue(input, value);
    }

    public boolean isCoolDownPeriodSaveButtonDisplayed() {
        ensureAdminSettingsPageReady();
        scrollToSemanticsLabel(SEMANTICS_COOLDOWN_SAVE);
        scrollToSemanticsLabel(SEMANTICS_CROSS_PATH_COOL_DOWN_FALLBACK);
        return isSemanticsDisplayed(SEMANTICS_COOLDOWN_SAVE)
                || isSaveNearCooldownDisplayed();
    }

    public boolean isSplashScreenExperienceDisplayed() {
        return isSettingLabelDisplayed(SEMANTICS_SPLASH_SCREEN_EXPERIENCE);
    }

    public boolean isSplashScreenDropdownDisplayed() {
        ensureAdminSettingsPageReady();
        scrollContentToTop();
        for (int attempt = 0; attempt < 8; attempt++) {
            scrollToSemanticsLabel(SEMANTICS_SPLASH_SCREEN_DROPDOWN);
            scrollToSemanticsLabel(SEMANTICS_SPLASH_SCREEN_EXPERIENCE);
            if (isSemanticsPresent(driver, SEMANTICS_SPLASH_SCREEN_DROPDOWN)
                    || isSemanticsPresent(driver, SEMANTICS_SPLASH_SCREEN_DROPDOWN_FALLBACK)
                    || pageContainsTextOutsideNav(driver, SEMANTICS_SPLASH_SCREEN_DROPDOWN_FALLBACK)
                    || isDropdownNearLabel(SEMANTICS_SPLASH_SCREEN_EXPERIENCE)) {
                return true;
            }
            scrollContentAreaDown();
        }
        return isSemanticsPresent(driver, SEMANTICS_SPLASH_SCREEN_DROPDOWN)
                || isSemanticsPresent(driver, SEMANTICS_SPLASH_SCREEN_DROPDOWN_FALLBACK)
                || pageContainsTextOutsideNav(driver, SEMANTICS_SPLASH_SCREEN_DROPDOWN_FALLBACK)
                || isDropdownNearLabel(SEMANTICS_SPLASH_SCREEN_EXPERIENCE);
    }

    public boolean isSplashScreenDisplayDurationDisplayed() {
        return isSettingLabelDisplayed(SEMANTICS_SPLASH_SCREEN_DISPLAY_DURATION);
    }

    public void enterSplashScreenDisplayDuration() {
        enterSplashScreenDisplayDuration(DEFAULT_SPLASH_DURATION_VALUE);
    }

    public void enterSplashScreenDisplayDuration(String duration) {
        ensureAdminSettingsPageReady();
        scrollToSemanticsLabel(SEMANTICS_SPLASH_SCREEN_DISPLAY_DURATION);
        scrollToSemanticsLabel(SEMANTICS_SPLASH_SCREEN_DURATION_FIELD);
        WebElement input = waitForSplashDurationInput();
        typeExactValue(input, duration);
    }

    public boolean isSplashScreenDisplayDurationSaveButtonDisplayed() {
        ensureAdminSettingsPageReady();
        scrollToSemanticsLabel(SEMANTICS_SPLASH_SCREEN_DISPLAY_DURATION);
        scrollToSemanticsLabel(SEMANTICS_SPLASH_SCREEN_DURATION_SAVE);
        return isSemanticsDisplayed(SEMANTICS_SPLASH_SCREEN_DURATION_SAVE)
                || isSaveNearSplashDurationDisplayed();
    }

    /** Diagnostic helper for assertion messages. */
    public String describeAvailableSettingsLabels() {
        return dumpOutsideNavSemanticsLabels();
    }

    private boolean isDropdownNearLabel(String settingLabel) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const wanted = (arguments[0] || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "const nodes = Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const labelNode = nodes.find(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const aria = (node.getAttribute('aria-label') || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  return aria === wanted || text === wanted || aria.includes(wanted) || text.includes(wanted);" +
                        "});" +
                        "if (!labelNode) return false;" +
                        "function isDropdown(node) {" +
                        "  if (!node) return false;" +
                        "  const role = (node.getAttribute('role') || '').toLowerCase();" +
                        "  const aria = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (role === 'combobox' || role === 'listbox' || role === 'button'" +
                        "      || aria.includes('dropdown') || aria.includes('splash')) return true;" +
                        "  return Array.from(node.querySelectorAll('flt-semantics')).some(child => {" +
                        "    const childRole = (child.getAttribute('role') || '').toLowerCase();" +
                        "    const childAria = (child.getAttribute('aria-label') || '').toLowerCase();" +
                        "    return childRole === 'combobox' || childRole === 'listbox'" +
                        "      || childAria.includes('dropdown') || childAria.includes('splash screen');" +
                        "  });" +
                        "}" +
                        "let ancestor = labelNode;" +
                        "for (let depth = 0; depth < 8 && ancestor; depth++) {" +
                        "  if (isDropdown(ancestor)) return true;" +
                        "  ancestor = ancestor.parentElement;" +
                        "}" +
                        "return false;",
                settingLabel
        ));
    }

    private void openAdminSettingsNavigation() {
        semantics.enableFlutterSemantics();
        if (isAdminSettingsPageLoaded(driver)) {
            return;
        }
        waitUntil(this::isHomePageLoaded, DEFAULT_WAIT);
        for (int attempt = 0; attempt < MAX_NAV_ATTEMPTS; attempt++) {
            scrollNavigationToAdminSettings();
            if (clickAdminSettingsNavItem()) {
                semantics.pauseAfterScroll();
                if (isAdminSettingsPageLoaded(driver)
                        || waitUntil(this::isAdminSettingsPageLoaded, PAGE_POLL)) {
                    return;
                }
            }
            scrollNavigationSidebarDown();
            semantics.pauseAfterScroll();
        }
        throw new NoSuchElementException(
                "Admin Settings menu item not found in navigation sidebar (expected semantics "
                        + SEMANTICS_MENU + ")");
    }

    private boolean clickAdminSettingsNavItem() {
        // Prefer exact drawer semantics; avoid matching audit-log option labels that contain admin_settings.
        if (semantics.clickSemanticsLabelViaScript(SEMANTICS_MENU)) {
            return true;
        }
        if (!driver.findElements(adminSettingsMenu).isEmpty()) {
            try {
                semantics.clickSemanticsElement(adminSettingsMenu, wait);
                return true;
            } catch (Exception ignored) {
                // Continue to text/script fallbacks.
            }
        }
        if (semantics.clickVisibleText(ADMIN_SETTINGS_TEXT)) {
            return true;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                DISPATCH_POINTER_CLICK_SCRIPT +
                        "const wanted = arguments[0];" +
                        "const semanticsLabel = arguments[1];" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const nodes = menu ? Array.from(menu.querySelectorAll('flt-semantics'))" +
                        "  : Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const target = nodes.find(node => {" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const label = (node.getAttribute('aria-label') || '');" +
                        "  return label === semanticsLabel || text === wanted;" +
                        "});" +
                        "if (!target) return false;" +
                        "const tap = target.closest('flt-semantics[flt-tappable]') || target;" +
                        "return dispatchPointerClick(tap);",
                ADMIN_SETTINGS_TEXT,
                SEMANTICS_MENU
        ));
    }

    private void scrollNavigationToAdminSettings() {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                "const wanted = arguments[0];" +
                        "const semanticsLabel = arguments[1];" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const nodes = menu ? Array.from(menu.querySelectorAll('flt-semantics'))" +
                        "  : Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const target = nodes.find(node => {" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const label = (node.getAttribute('aria-label') || '');" +
                        "  return label === semanticsLabel || text === wanted;" +
                        "});" +
                        "if (target) target.scrollIntoView({block: 'center', inline: 'nearest'});",
                ADMIN_SETTINGS_TEXT,
                SEMANTICS_MENU
        );
        semantics.pauseAfterScroll();
    }

    private void scrollNavigationSidebarDown() {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const anchor = menu || document.body;" +
                        "for (let i = 0; i < 3; i++) {" +
                        "  anchor.dispatchEvent(new WheelEvent('wheel', {" +
                        "    deltaX: 0, deltaY: 240, bubbles: true, cancelable: true" +
                        "  }));" +
                        "}"
        );
        semantics.pauseAfterScroll();
    }

    private boolean isSettingLabelDisplayed(String semanticsLabel) {
        ensureAdminSettingsPageReady();
        scrollContentToTop();
        for (int attempt = 0; attempt < 8; attempt++) {
            scrollToSemanticsLabel(semanticsLabel);
            if (isSemanticsPresent(driver, semanticsLabel)
                    || pageContainsTextOutsideNav(driver, semanticsLabel)) {
                return true;
            }
            scrollContentAreaDown();
        }
        return waitUntil(
                d -> isSemanticsPresent(d, semanticsLabel) || pageContainsTextOutsideNav(d, semanticsLabel),
                Duration.ofSeconds(5));
    }

    private boolean isSemanticsDisplayed(String semanticsLabel) {
        semantics.enableFlutterSemantics();
        scrollContentToTop();
        for (int attempt = 0; attempt < 8; attempt++) {
            scrollToSemanticsLabel(semanticsLabel);
            if (isSemanticsPresent(driver, semanticsLabel)) {
                return true;
            }
            scrollContentAreaDown();
        }
        return waitUntil(d -> isSemanticsPresent(d, semanticsLabel), Duration.ofSeconds(5));
    }

    private void scrollContentAreaDown() {
        ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const glass = document.querySelector('flt-glass-pane')" +
                        "  || document.querySelector('flt-semantics-host')" +
                        "  || document.body;" +
                        "const rect = glass.getBoundingClientRect();" +
                        "const x = rect.left + Math.min(rect.width * 0.65, rect.width - 40);" +
                        "const y = rect.top + rect.height / 2;" +
                        "const target = document.elementFromPoint(x, y) || glass;" +
                        "for (let i = 0; i < 4; i++) {" +
                        "  target.dispatchEvent(new WheelEvent('wheel', {" +
                        "    deltaX: 0, deltaY: 360, bubbles: true, cancelable: true, clientX: x, clientY: y" +
                        "  }));" +
                        "}"
        );
        semantics.pauseAfterScroll();
        semantics.forceEnableFlutterSemantics();
    }

    private void scrollContentToTop() {
        ((JavascriptExecutor) driver).executeScript(
                "const glass = document.querySelector('flt-glass-pane')" +
                        "  || document.querySelector('flt-semantics-host')" +
                        "  || document.body;" +
                        "const rect = glass.getBoundingClientRect();" +
                        "const x = rect.left + Math.min(rect.width * 0.65, rect.width - 40);" +
                        "const y = rect.top + rect.height / 2;" +
                        "const target = document.elementFromPoint(x, y) || glass;" +
                        "for (let i = 0; i < 8; i++) {" +
                        "  target.dispatchEvent(new WheelEvent('wheel', {" +
                        "    deltaX: 0, deltaY: -400, bubbles: true, cancelable: true, clientX: x, clientY: y" +
                        "  }));" +
                        "}"
        );
        semantics.pauseAfterScroll();
        semantics.forceEnableFlutterSemantics();
    }

    private boolean isSemanticsPresent(WebDriver webDriver, String semanticsLabel) {
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const label = arguments[0];" +
                        "const lower = (label || '').toLowerCase();" +
                        "const exact = document.querySelector('flt-semantics[aria-label=\"' + label + '\"]');" +
                        "if (exact && !isInNav(exact)) return true;" +
                        "const nodes = Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const match = nodes.find(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const aria = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return aria === lower || aria.includes(lower);" +
                        "});" +
                        "return !!match;",
                semanticsLabel
        ));
    }

    private void scrollToSemanticsLabel(String semanticsLabel) {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const label = arguments[0];" +
                        "const lower = (label || '').toLowerCase();" +
                        "const nodes = Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const target = nodes.find(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const aria = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  return aria === lower || aria.includes(lower) || text === lower || text.includes(lower);" +
                        "});" +
                        "if (target) target.scrollIntoView({block: 'center', inline: 'nearest'});",
                semanticsLabel
        );
        semantics.pauseAfterScroll();
    }

    private boolean isSaveNearCooldownDisplayed() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const markers = arguments[0];" +
                        "const nodes = Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const cooldown = nodes.find(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return markers.some(marker => {" +
                        "    const wanted = marker.toLowerCase();" +
                        "    return text.includes(wanted) || label.includes(wanted);" +
                        "  });" +
                        "});" +
                        "if (!cooldown) {" +
                        "  return nodes.some(node => {" +
                        "    if (isInNav(node)) return false;" +
                        "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "    const label = (node.getAttribute('aria-label') || '');" +
                        "    const role = (node.getAttribute('role') || '').toLowerCase();" +
                        "    return (text === 'Save' || label.toLowerCase().includes('save'))" +
                        "      && (role === 'button' || node.hasAttribute('flt-tappable'));" +
                        "  });" +
                        "}" +
                        "let ancestor = cooldown;" +
                        "for (let depth = 0; depth < 10 && ancestor; depth++) {" +
                        "  const save = Array.from(ancestor.querySelectorAll('flt-semantics')).find(node => {" +
                        "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "    const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "    return text === 'Save' || label.includes('save') || label.includes('cooldown_period_field_save_button');" +
                        "  });" +
                        "  if (save) return true;" +
                        "  ancestor = ancestor.parentElement;" +
                        "}" +
                        "return false;",
                java.util.List.of(
                        SEMANTICS_CROSS_PATH_COOL_DOWN,
                        SEMANTICS_CROSS_PATH_COOL_DOWN_FALLBACK,
                        "cooldown")
        ));
    }

    private WebElement waitForEnterValueInput() {
        if (!driver.findElements(enterValueInput).isEmpty()) {
            return wait.until(ExpectedConditions.elementToBeClickable(enterValueInput));
        }
        WebElement inputFromScript = findEnterValueInputViaScript();
        if (inputFromScript != null) {
            return inputFromScript;
        }
        throw new NoSuchElementException(
                "Cool Down Period value field not found (expected semantics " + SEMANTICS_ENTER_VALUE + ")");
    }

    private WebElement waitForSplashDurationInput() {
        if (!driver.findElements(splashDurationInput).isEmpty()) {
            return wait.until(ExpectedConditions.elementToBeClickable(splashDurationInput));
        }
        WebElement inputFromScript = findSplashDurationInputViaScript();
        if (inputFromScript != null) {
            return inputFromScript;
        }
        throw new NoSuchElementException(
                "Splash Screen Display Duration field not found (expected semantics "
                        + SEMANTICS_SPLASH_SCREEN_DURATION_FIELD + ")");
    }

    private WebElement findSplashDurationInputViaScript() {
        return (WebElement) ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const labels = arguments[0];" +
                        "const markers = arguments[1];" +
                        "for (const label of labels) {" +
                        "  const field = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "    if (isInNav(node)) return false;" +
                        "    const aria = (node.getAttribute('aria-label') || '');" +
                        "    return aria === label || aria.includes(label);" +
                        "  });" +
                        "  if (field) {" +
                        "    const nested = field.querySelector('input, textarea');" +
                        "    if (nested) return nested;" +
                        "  }" +
                        "  const direct = document.querySelector('input[aria-label=\"' + label + '\"]')" +
                        "    || document.querySelector('input[aria-label*=\"' + label + '\"]');" +
                        "  if (direct && !isInNav(direct)) return direct;" +
                        "}" +
                        "const marker = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  const aria = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return markers.some(marker => {" +
                        "    const wanted = marker.toLowerCase();" +
                        "    return text.includes(wanted) || aria.includes(wanted);" +
                        "  });" +
                        "});" +
                        "if (marker) {" +
                        "  let ancestor = marker;" +
                        "  for (let depth = 0; depth < 10 && ancestor; depth++) {" +
                        "    const input = ancestor.querySelector('input, textarea');" +
                        "    if (input) return input;" +
                        "    ancestor = ancestor.parentElement;" +
                        "  }" +
                        "}" +
                        "return null;",
                java.util.List.of(SEMANTICS_SPLASH_SCREEN_DURATION_FIELD),
                java.util.List.of(
                        SEMANTICS_SPLASH_SCREEN_DISPLAY_DURATION,
                        "Set duration in seconds")
        );
    }

    private boolean isSaveNearSplashDurationDisplayed() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const markers = arguments[0];" +
                        "const saveLabel = arguments[1];" +
                        "const nodes = Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const exactSave = nodes.find(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const label = (node.getAttribute('aria-label') || '');" +
                        "  return label === saveLabel || label.includes(saveLabel);" +
                        "});" +
                        "if (exactSave) return true;" +
                        "const duration = nodes.find(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return markers.some(marker => {" +
                        "    const wanted = marker.toLowerCase();" +
                        "    return text.includes(wanted) || label.includes(wanted);" +
                        "  });" +
                        "});" +
                        "if (!duration) return false;" +
                        "let ancestor = duration;" +
                        "for (let depth = 0; depth < 10 && ancestor; depth++) {" +
                        "  const save = Array.from(ancestor.querySelectorAll('flt-semantics')).find(node => {" +
                        "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "    const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "    return text === 'Save' || label.includes('save')" +
                        "      || label.includes('splash_screen_display_duration_field_save_button');" +
                        "  });" +
                        "  if (save) return true;" +
                        "  ancestor = ancestor.parentElement;" +
                        "}" +
                        "return false;",
                java.util.List.of(
                        SEMANTICS_SPLASH_SCREEN_DISPLAY_DURATION,
                        SEMANTICS_SPLASH_SCREEN_DURATION_FIELD,
                        "duration"),
                SEMANTICS_SPLASH_SCREEN_DURATION_SAVE
        ));
    }

    private WebElement findEnterValueInputViaScript() {
        return (WebElement) ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const labels = arguments[0];" +
                        "const markers = arguments[1];" +
                        "for (const label of labels) {" +
                        "  const field = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "    if (isInNav(node)) return false;" +
                        "    const aria = (node.getAttribute('aria-label') || '');" +
                        "    return aria === label || aria.includes(label);" +
                        "  });" +
                        "  if (field) {" +
                        "    const nested = field.querySelector('input, textarea');" +
                        "    if (nested) return nested;" +
                        "  }" +
                        "  const direct = document.querySelector('input[aria-label=\"' + label + '\"]')" +
                        "    || document.querySelector('input[aria-label*=\"' + label + '\"]');" +
                        "  if (direct && !isInNav(direct)) return direct;" +
                        "}" +
                        "const marker = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  return markers.some(marker => text.includes(marker.toLowerCase()));" +
                        "});" +
                        "if (marker) {" +
                        "  let ancestor = marker;" +
                        "  for (let depth = 0; depth < 10 && ancestor; depth++) {" +
                        "    const input = ancestor.querySelector('input, textarea');" +
                        "    if (input) return input;" +
                        "    ancestor = ancestor.parentElement;" +
                        "  }" +
                        "}" +
                        "const inputs = Array.from(document.querySelectorAll('input, textarea'))" +
                        "  .filter(el => !isInNav(el) && el.offsetParent !== null);" +
                        "return inputs.length ? inputs[0] : null;",
                java.util.List.of(SEMANTICS_ENTER_VALUE),
                java.util.List.of(
                        SEMANTICS_CROSS_PATH_COOL_DOWN_FALLBACK,
                        "Value is in minutes",
                        "cooldown")
        );
    }

    private void typeExactValue(WebElement input, String value) {
        semantics.scrollIntoView(input);
        if (!semantics.clickElementReliably(input)) {
            input.click();
        }
        try {
            input.sendKeys(Keys.chord(SELECT_ALL_MODIFIER, "a"));
            input.sendKeys(Keys.BACK_SPACE);
            input.sendKeys(value);
            String currentValue = input.getAttribute("value");
            if (currentValue == null || !currentValue.equals(value)) {
                setInputValueViaScript(input, value);
            }
        } catch (Exception ex) {
            setInputValueViaScript(input, value);
        }
        input.sendKeys(Keys.TAB);
    }

    private void setInputValueViaScript(WebElement input, String value) {
        ((JavascriptExecutor) driver).executeScript(
                "const el = arguments[0];" +
                        "const val = arguments[1];" +
                        "el.focus();" +
                        "el.value = val;" +
                        "el.dispatchEvent(new Event('input', { bubbles: true }));" +
                        "el.dispatchEvent(new Event('change', { bubbles: true }));",
                input,
                value
        );
    }

    private void ensureAdminSettingsPageReady() {
        semantics.enableFlutterSemantics();
        if (isAdminSettingsPageLoaded(driver)) {
            return;
        }
        clickAdminSettingsMenu();
    }

    private boolean isAdminSettingsPageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return isSemanticsPresent(webDriver, SEMANTICS_ENABLE_FREE_SUBSCRIPTION)
                || isSemanticsPresent(webDriver, SEMANTICS_ENABLE_FREE_SUBSCRIPTION_TOGGLE)
                || isSemanticsPresent(webDriver, SEMANTICS_AUTOMATED_TEST_USER_CREATION)
                || isSemanticsPresent(webDriver, SEMANTICS_CROSS_PATH_COOL_DOWN)
                || isSemanticsPresent(webDriver, SEMANTICS_CROSS_PATH_COOL_DOWN_FALLBACK)
                || isSemanticsPresent(webDriver, SEMANTICS_SPLASH_SCREEN_EXPERIENCE)
                || isSemanticsPresent(webDriver, SEMANTICS_SPLASH_SCREEN_DISPLAY_DURATION)
                || isSemanticsPresent(webDriver, SEMANTICS_SPLASH_SCREEN_DURATION_FIELD)
                || isSemanticsPresent(webDriver, SEMANTICS_ENTER_VALUE)
                || isSemanticsPresent(webDriver, SEMANTICS_COOLDOWN_SAVE)
                || pageContainsTextOutsideNav(webDriver, SEMANTICS_ENABLE_FREE_SUBSCRIPTION)
                || pageContainsTextOutsideNav(webDriver, SEMANTICS_CROSS_PATH_COOL_DOWN_FALLBACK)
                || pageContainsTextOutsideNav(webDriver, SEMANTICS_SPLASH_SCREEN_EXPERIENCE)
                || pageContainsTextOutsideNav(webDriver, SEMANTICS_ENABLE_WHITE_LABEL_BRANDING_FALLBACK);
    }

    private boolean isHomePageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String text = semantics.getSemanticsText(webDriver);
        return text != null
                && (text.contains("Individual Users")
                || text.contains("Company Users")
                || text.contains(ADMIN_SETTINGS_TEXT)
                || text.contains("Navigation menu"));
    }

    private boolean pageContainsText(WebDriver webDriver, String expectedText) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String normalizedExpected = semantics.normalizeText(expectedText);
        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        return pageText.contains(normalizedExpected);
    }

    private boolean pageContainsTextOutsideNav(WebDriver webDriver, String expectedText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const wanted = (arguments[0] || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "const nodes = Array.from(document.querySelectorAll('flt-semantics'));" +
                        "return nodes.some(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  const label = (node.getAttribute('aria-label') || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  return text === wanted || text.includes(wanted) || label === wanted || label.includes(wanted);" +
                        "});",
                expectedText
        ));
    }

    private String dumpOutsideNavSemanticsLabels() {
        Object result = ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const entries = Array.from(document.querySelectorAll('flt-semantics'))" +
                        "  .filter(node => !isInNav(node))" +
                        "  .map(node => {" +
                        "    const label = (node.getAttribute('aria-label') || '').trim();" +
                        "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "    const role = (node.getAttribute('role') || '').trim();" +
                        "    const checked = node.getAttribute('aria-checked');" +
                        "    let value = label || text;" +
                        "    if (!value) return '';" +
                        "    if (role) value += ' [role=' + role + ']';" +
                        "    if (checked != null) value += ' [checked=' + checked + ']';" +
                        "    return value;" +
                        "  })" +
                        "  .filter(value => value.length > 0 && value.length < 120);" +
                        "return Array.from(new Set(entries)).slice(0, 100).join(' | ');"
        );
        return result == null ? "" : String.valueOf(result);
    }

    private boolean waitUntil(Function<WebDriver, Boolean> condition, Duration timeout) {
        return semantics.waitUntil(condition, timeout);
    }
}
