package com.example.pages;

import com.example.pages.support.FlutterSemanticsSupport;
import com.example.pages.support.StatusFilterSupport;
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

public class AuditLogsPage {

    private static final String MONITORING_TEXT = "Monitoring";
    private static final String AUDIT_LOGS_TEXT = "Audit Logs";
    private static final String EXPORT_LOGS_TEXT = "Export Logs";
    private static final String ALL_MODULES_TEXT = "All Modules";
    private static final String INDIVIDUAL_USERS_TEXT = "Individual Users";
    private static final String COMPANY_USERS_TEXT = "Company Users";
    private static final String INPUT_CATEGORIES_TEXT = "Input Categories";
    private static final String EVENTS_TEXT = "Events";
    private static final String ADVERTISEMENT_TEXT = "Advertisement";
    private static final String TRANSACTIONS_TEXT = "Transactions";
    private static final String MODERATION_CENTER_TEXT = "Moderation Center";
    private static final String ADMIN_SETTINGS_TEXT = "Admin Settings";
    private static final String MANAGE_ROLES_TEXT = "Manage Roles";
    private static final String MANAGE_ADMIN_USERS_TEXT = "Manage Admin Users";
    private static final String DEEPLINK_HISTORY_TEXT = "Deeplink History";
    private static final String CONSENT_AUDIT_LOG_TEXT = "Consent Audit Log";
    private static final String CONTENT_VERSIONS_TEXT = "Content Versions";
    private static final String DELETED_USERS_TEXT = "Deleted Users";
    private static final String ALL_ACTIONS_TEXT = "All Actions";
    private static final String CREATED_TEXT = "Created";
    private static final String UPDATED_TEXT = "Updated";
    private static final String DELETED_TEXT = "Deleted";
    private static final String BLOCKED_TEXT = "Blocked";
    private static final String UNBLOCKED_TEXT = "Unblocked";
    private static final String APPROVED_TEXT = "Approved";
    private static final String REJECTED_TEXT = "Rejected";
    private static final String CLOSED_TEXT = "Closed";
    private static final String INVITED_TEXT = "Invited";
    private static final String ENABLED_TEXT = "Enabled";
    private static final String EXPIRED_TEXT = "Expired";
    private static final String NAVIGATION_MENU = "Navigation menu";

    private static final String SEMANTICS_MONITORING = "monitoring_submenu";
    private static final String SEMANTICS_AUDIT_LOGS_MENU = "audit_logs_submenu";
    private static final String SEMANTICS_EXPORT_LOGS = "export_logs_button";
    private static final String SEMANTICS_ALL_MODULES = "all_modules_option_all_modules";
    private static final String SEMANTICS_INDIVIDUAL_USERS = "individual_users_option_individual_users";
    private static final String SEMANTICS_COMPANY_USERS = "company_users_option_company_users";
    private static final String SEMANTICS_INPUT_CATEGORIES = "input_categories_option_input_categories";
    private static final String SEMANTICS_EVENTS = "events_option_events";
    private static final String SEMANTICS_ADVERTISEMENT = "advertisement_option_advertisement";
    private static final String SEMANTICS_TRANSACTIONS = "transactions_option_transactions";
    private static final String SEMANTICS_MODERATION_CENTER = "moderation_center_option_moderation_center";
    private static final String SEMANTICS_ADMIN_SETTINGS = "admin_settings_option_admin_settings";
    private static final String SEMANTICS_MANAGE_ROLES = "manage_roles_option_manage_roles";
    private static final String SEMANTICS_MANAGE_ADMIN_USERS = "manage_admin_users_option_manage_admin_users";
    private static final String SEMANTICS_DEEPLINK_HISTORY = "deeplink_history_option_deeplink_history";
    private static final String SEMANTICS_CONSENT_AUDIT_LOG = "consent_audit_log_option_consent_audit_log";
    private static final String SEMANTICS_CONTENT_VERSIONS = "content_versions_option_content_versions";
    private static final String SEMANTICS_AUDIT_LOGS_OPTION = "audit_logs_option_audit_logs";
    private static final String SEMANTICS_DELETED_USERS = "deleted_users_option_deleted_users";
    private static final String SEMANTICS_ALL_ACTIONS = "all_actions_option_all_actions";
    private static final String SEMANTICS_CREATED = "created_option_created";
    private static final String SEMANTICS_UPDATED = "updated_option_updated";
    private static final String SEMANTICS_DELETED = "deleted_option_deleted";
    private static final String SEMANTICS_BLOCKED = "blocked_option_blocked";
    private static final String SEMANTICS_UNBLOCKED = "unblocked_option_unblocked";
    private static final String SEMANTICS_APPROVED = "approved_option_approved";
    private static final String SEMANTICS_REJECTED = "rejected_option_rejected";
    private static final String SEMANTICS_CLOSED = "closed_option_closed";
    private static final String SEMANTICS_INVITED = "invited_option_invited";
    private static final String SEMANTICS_ENABLED = "enabled_option_enabled";
    private static final String SEMANTICS_EXPIRED = "expired_option_expired";
    private static final String SEMANTICS_SEARCH = "search_field";

    private static final Duration DEFAULT_WAIT = FlutterSemanticsSupport.DEFAULT_WAIT;
    private static final Duration PAGE_POLL = Duration.ofSeconds(12);
    private static final Duration FILTER_VALIDATION_DELAY = Duration.ofSeconds(5);
    private static final int MAX_ACTION_ATTEMPTS = 5;

    private static final String FIND_OUTSIDE_NAV_SCRIPT =
            "function navMenu() {" +
                    "  return document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                    "}" +
                    "function isInNav(node) {" +
                    "  const menu = navMenu();" +
                    "  return !!(menu && node && menu.contains(node));" +
                    "}" +
                    "function findOutsideNav(selector) {" +
                    "  const menu = navMenu();" +
                    "  return Array.from(document.querySelectorAll(selector))" +
                    "    .find(node => !menu || !menu.contains(node)) || null;" +
                    "}";

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

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final FlutterSemanticsSupport semantics;
    private final StatusFilterSupport modulesFilter;
    private final StatusFilterSupport actionsFilter;
    private boolean modulesDropdownOpened;
    private String lastSelectedModule;
    private boolean actionsDropdownOpened;
    private String lastSelectedAction;

    private final By exportLogsButton = semanticsLocator(SEMANTICS_EXPORT_LOGS);
    private final By allModulesOption = semanticsLocator(SEMANTICS_ALL_MODULES);
    private final By allActionsOption = semanticsLocator(SEMANTICS_ALL_ACTIONS);
    private final By searchBarSemantics = semanticsLocator(SEMANTICS_SEARCH);
    private final By searchBarInput = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_SEARCH + "']//input" +
                    " | //input[@aria-label='" + SEMANTICS_SEARCH + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_SEARCH + "')]//input" +
                    " | //input[contains(@aria-label,'Search')]");

    public AuditLogsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_WAIT);
        this.semantics = new FlutterSemanticsSupport(driver);
        this.modulesFilter = StatusFilterSupport.forAuditLogsModules(driver, semantics);
        this.actionsFilter = StatusFilterSupport.forAuditLogsActions(driver, semantics);
    }

    public boolean isModulesDropdownOpened() {
        return modulesDropdownOpened;
    }

    public boolean isAuditLogsModuleFilterSelected() {
        return AUDIT_LOGS_TEXT.equals(lastSelectedModule);
    }

    public boolean isActionsDropdownOpened() {
        return actionsDropdownOpened;
    }

    public void clickMonitoringMenu() {
        semantics.enableFlutterSemantics();
        waitUntil(this::isHomePageLoaded, DEFAULT_WAIT);
        scrollNavigationToMonitoring();
        openNavigationItem(
                SEMANTICS_MONITORING,
                MONITORING_TEXT,
                "Monitoring submenu not found (expected semantics " + SEMANTICS_MONITORING + ")");
    }

    public void clickAuditLogsMenu() {
        semantics.enableFlutterSemantics();
        scrollNavigationToAuditLogs();
        openNavigationItem(
                SEMANTICS_AUDIT_LOGS_MENU,
                AUDIT_LOGS_TEXT,
                "Audit Logs submenu not found (expected semantics " + SEMANTICS_AUDIT_LOGS_MENU + ")");
        modulesDropdownOpened = false;
        lastSelectedModule = null;
        actionsDropdownOpened = false;
        lastSelectedAction = null;
        if (!waitUntil(this::isAuditLogsPageLoaded, PAGE_POLL)) {
            throw new NoSuchElementException(
                    "Audit Logs page did not load (expected semantics " + SEMANTICS_EXPORT_LOGS
                            + ", " + SEMANTICS_ALL_MODULES + ", or " + SEMANTICS_SEARCH + ")");
        }
        waitForSearchFieldReady();
    }

    public boolean isAuditLogsDisplayed() {
        return isAuditLogsPageLoaded(driver);
    }

    public void clickExportLogs() {
        ensureAuditLogsPageReady();
        scrollToSemanticsLabel(SEMANTICS_EXPORT_LOGS);
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            if (attempt > 0) {
                scrollToSemanticsLabel(SEMANTICS_EXPORT_LOGS);
            }
            if (clickSemanticsAction(SEMANTICS_EXPORT_LOGS, EXPORT_LOGS_TEXT)) {
                return;
            }
            semantics.pauseAfterScroll();
        }
        throw new NoSuchElementException(
                "Export Logs button not found (expected semantics " + SEMANTICS_EXPORT_LOGS + ")");
    }

    public void clickSearch() {
        ensureAuditLogsPageReady();
        WebElement searchInput = waitForSearchInput();
        semantics.scrollIntoView(searchInput);
        searchInput.click();
    }

    public void enterSearch(String text) {
        ensureAuditLogsPageReady();
        WebElement searchInput = waitForSearchInput();
        semantics.scrollIntoView(searchInput);
        searchInput.click();
        searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        searchInput.sendKeys(Keys.BACK_SPACE);
        searchInput.sendKeys(text);
        searchInput.sendKeys(Keys.ENTER);
        semantics.pauseAfterScroll();
        waitUntil(d -> pageContainsText(d, text) || isNoDataAvailableOnPage(d), DEFAULT_WAIT);
    }

    public boolean isSearchResultDisplayed(String text) {
        return waitUntil(d -> pageContainsText(d, text) || isNoDataAvailableOnPage(d), DEFAULT_WAIT);
    }

    public void clickAllModules() {
        ensureAuditLogsPageReady();
        modulesFilter.prepareFilterArea();
        modulesFilter.openDropdown();
        modulesDropdownOpened = true;
        lastSelectedModule = null;
    }

    public void clickIndividualUsers() {
        selectModuleOption(INDIVIDUAL_USERS_TEXT, SEMANTICS_INDIVIDUAL_USERS);
    }

    public void clickCompanyUsers() {
        selectModuleOption(COMPANY_USERS_TEXT, SEMANTICS_COMPANY_USERS);
    }

    public void clickInputCategories() {
        selectModuleOption(INPUT_CATEGORIES_TEXT, SEMANTICS_INPUT_CATEGORIES);
    }

    public void clickEvents() {
        selectModuleOption(EVENTS_TEXT, SEMANTICS_EVENTS);
    }

    public void clickAdvertisement() {
        selectModuleOption(ADVERTISEMENT_TEXT, SEMANTICS_ADVERTISEMENT);
    }

    public void clickTransactions() {
        selectModuleOption(TRANSACTIONS_TEXT, SEMANTICS_TRANSACTIONS);
    }

    public void clickModerationCenter() {
        selectModuleOption(MODERATION_CENTER_TEXT, SEMANTICS_MODERATION_CENTER);
    }

    public void clickAdminSettings() {
        selectModuleOption(ADMIN_SETTINGS_TEXT, SEMANTICS_ADMIN_SETTINGS);
    }

    public void clickManageRoles() {
        selectModuleOption(MANAGE_ROLES_TEXT, SEMANTICS_MANAGE_ROLES);
    }

    public void clickManageAdminUsers() {
        selectModuleOption(MANAGE_ADMIN_USERS_TEXT, SEMANTICS_MANAGE_ADMIN_USERS);
    }

    public void clickDeeplinkHistory() {
        selectModuleOption(DEEPLINK_HISTORY_TEXT, SEMANTICS_DEEPLINK_HISTORY);
    }

    public void clickConsentAuditLog() {
        selectModuleOption(CONSENT_AUDIT_LOG_TEXT, SEMANTICS_CONSENT_AUDIT_LOG);
    }

    public void clickContentVersions() {
        selectModuleOption(CONTENT_VERSIONS_TEXT, SEMANTICS_CONTENT_VERSIONS);
    }

    public void clickAuditLogsModuleOption() {
        selectModuleOption(AUDIT_LOGS_TEXT, SEMANTICS_AUDIT_LOGS_OPTION);
    }

    public void clickDeletedUsers() {
        selectModuleOption(DELETED_USERS_TEXT, SEMANTICS_DELETED_USERS);
    }

    public void clickAllActions() {
        ensureAuditLogsPageReady();
        actionsFilter.prepareFilterArea();
        actionsFilter.openDropdown();
        actionsDropdownOpened = true;
        lastSelectedAction = null;
    }

    public void clickCreated() {
        selectActionOption(CREATED_TEXT, SEMANTICS_CREATED);
    }

    public void clickUpdated() {
        selectActionOption(UPDATED_TEXT, SEMANTICS_UPDATED);
    }

    public void clickDeleted() {
        selectActionOption(DELETED_TEXT, SEMANTICS_DELETED);
    }

    public void clickBlocked() {
        selectActionOption(BLOCKED_TEXT, SEMANTICS_BLOCKED);
    }

    public void clickUnblocked() {
        selectActionOption(UNBLOCKED_TEXT, SEMANTICS_UNBLOCKED);
    }

    public void clickApproved() {
        selectActionOption(APPROVED_TEXT, SEMANTICS_APPROVED);
    }

    public void clickRejected() {
        selectActionOption(REJECTED_TEXT, SEMANTICS_REJECTED);
    }

    public void clickClosed() {
        ensureActionsDropdownOpen();
        for (int attempt = 0; attempt < 20; attempt++) {
            scrollActionsPopup(attempt % 2 == 0 ? 300 : -200);
            semantics.pauseAfterScroll();
            if (tryClickActionOption(SEMANTICS_CLOSED, CLOSED_TEXT, "Close")) {
                lastSelectedAction = CLOSED_TEXT;
                actionsDropdownOpened = false;
                return;
            }
        }
        selectActionOption(CLOSED_TEXT, SEMANTICS_CLOSED, "Close");
    }

    public void clickInvited() {
        selectActionOption(INVITED_TEXT, SEMANTICS_INVITED);
    }

    public void clickEnabled() {
        selectActionOption(ENABLED_TEXT, SEMANTICS_ENABLED);
    }

    public void clickExpired() {
        selectActionOption(EXPIRED_TEXT, SEMANTICS_EXPIRED);
    }

    public boolean isIndividualUsersDisplayed() {
        return validateModuleSelection(INDIVIDUAL_USERS_TEXT);
    }

    public boolean isCompanyUsersDisplayed() {
        return validateModuleSelection(COMPANY_USERS_TEXT);
    }

    public boolean isInputCategoriesDisplayed() {
        return validateModuleSelection(INPUT_CATEGORIES_TEXT);
    }

    public boolean isEventsDisplayed() {
        return validateModuleSelection(EVENTS_TEXT);
    }

    public boolean isAdvertisementDisplayed() {
        return validateModuleSelection(ADVERTISEMENT_TEXT);
    }

    public boolean isTransactionsDisplayed() {
        return validateModuleSelection(TRANSACTIONS_TEXT);
    }

    public boolean isModerationCenterDisplayed() {
        return validateModuleSelection(MODERATION_CENTER_TEXT);
    }

    public boolean isAdminSettingsDisplayed() {
        return validateModuleSelection(ADMIN_SETTINGS_TEXT);
    }

    public boolean isManageRolesDisplayed() {
        return validateModuleSelection(MANAGE_ROLES_TEXT);
    }

    public boolean isManageAdminUsersDisplayed() {
        return validateModuleSelection(MANAGE_ADMIN_USERS_TEXT);
    }

    public boolean isDeeplinkHistoryDisplayed() {
        return validateModuleSelection(DEEPLINK_HISTORY_TEXT);
    }

    public boolean isConsentAuditLogDisplayed() {
        return validateModuleSelection(CONSENT_AUDIT_LOG_TEXT);
    }

    public boolean isContentVersionsDisplayed() {
        return validateModuleSelection(CONTENT_VERSIONS_TEXT);
    }

    public boolean isAuditLogsModuleDisplayed() {
        return validateModuleSelection(AUDIT_LOGS_TEXT);
    }

    public boolean isDeletedUsersDisplayed() {
        return validateModuleSelection(DELETED_USERS_TEXT);
    }

    public boolean isCreatedDisplayed() {
        return validateActionSelection(CREATED_TEXT);
    }

    public boolean isUpdatedDisplayed() {
        return validateActionSelection(UPDATED_TEXT);
    }

    public boolean isDeletedDisplayed() {
        return validateActionSelection(DELETED_TEXT);
    }

    public boolean isBlockedDisplayed() {
        return validateActionSelection(BLOCKED_TEXT);
    }

    public boolean isUnblockedDisplayed() {
        return validateActionSelection(UNBLOCKED_TEXT);
    }

    public boolean isApprovedDisplayed() {
        return validateActionSelection(APPROVED_TEXT);
    }

    public boolean isRejectedDisplayed() {
        return validateActionSelection(REJECTED_TEXT);
    }

    public boolean isClosedDisplayed() {
        return validateActionSelection(CLOSED_TEXT);
    }

    public boolean isInvitedDisplayed() {
        return validateActionSelection(INVITED_TEXT);
    }

    public boolean isEnabledDisplayed() {
        return validateActionSelection(ENABLED_TEXT);
    }

    public boolean isExpiredDisplayed() {
        return validateActionSelection(EXPIRED_TEXT);
    }

    private void selectModuleOption(String visibleText, String semanticsLabel) {
        modulesFilter.selectOption(visibleText, new String[]{semanticsLabel});
        modulesDropdownOpened = false;
        lastSelectedModule = visibleText;
    }

    private void selectActionOption(String visibleText, String semanticsLabel, String... alternateVisibleTexts) {
        ensureActionsDropdownOpen();
        actionsFilter.selectOption(visibleText, new String[]{semanticsLabel}, alternateVisibleTexts);
        actionsDropdownOpened = false;
        lastSelectedAction = visibleText;
    }

    private void ensureActionsDropdownOpen() {
        if (actionsDropdownOpened || actionsFilter.isDropdownOpen()) {
            actionsDropdownOpened = true;
            return;
        }
        actionsFilter.prepareFilterArea();
        actionsFilter.openDropdown();
        actionsDropdownOpened = true;
    }

    private boolean tryClickActionOption(String semanticsLabel, String... visibleTexts) {
        semantics.enableFlutterSemantics();
        if (semantics.clickSemanticsLabelViaScript(semanticsLabel)
                || semantics.clickSemanticsLabelWithoutEnabling(semanticsLabel)) {
            return waitUntil(d -> !actionsFilter.isDropdownOpen(), Duration.ofSeconds(5));
        }
        for (String visibleText : visibleTexts) {
            if (visibleText != null && !visibleText.isBlank() && semantics.clickVisibleText(visibleText)) {
                return waitUntil(d -> !actionsFilter.isDropdownOpen(), Duration.ofSeconds(5));
            }
        }
        return false;
    }

    private void scrollActionsPopup(int deltaY) {
        ((JavascriptExecutor) driver).executeScript(
                "const deltaY = arguments[0];" +
                        "const popup = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').trim();" +
                        "  const text = (node.textContent || '').trim();" +
                        "  return label === 'Popup menu' || text === 'Popup menu';" +
                        "});" +
                        "const root = popup ? (popup.parentElement || popup) : document.body;" +
                        "const rect = root.getBoundingClientRect ? root.getBoundingClientRect() : null;" +
                        "const x = rect ? rect.left + Math.min(rect.width, 80) / 2 : window.innerWidth / 2;" +
                        "const y = rect ? rect.top + Math.min(rect.height, 160) / 2 : window.innerHeight / 2;" +
                        "const target = document.elementFromPoint(x, y) || root;" +
                        "for (let i = 0; i < 6; i++) {" +
                        "  target.dispatchEvent(new WheelEvent('wheel', {" +
                        "    deltaX: 0, deltaY: deltaY, bubbles: true, cancelable: true, clientX: x, clientY: y" +
                        "  }));" +
                        "  if (root.scrollBy) root.scrollBy(0, deltaY);" +
                        "}",
                deltaY
        );
    }

    private boolean validateModuleSelection(String expectedText) {
        waitBeforeFilterValidation();
        return waitUntil(d -> isFilterSelectionConfirmed(d, expectedText, lastSelectedModule), DEFAULT_WAIT);
    }

    private boolean validateActionSelection(String expectedText) {
        waitBeforeFilterValidation();
        return waitUntil(d -> isFilterSelectionConfirmed(d, expectedText, lastSelectedAction), DEFAULT_WAIT);
    }

    private boolean isFilterSelectionConfirmed(WebDriver webDriver, String expectedText, String lastSelected) {
        semantics.forceEnableFlutterSemantics();
        // selectOption already confirmed the menu closed after a successful click.
        if (expectedText.equals(lastSelected)) {
            return true;
        }
        if (pageContainsText(webDriver, expectedText)) {
            return true;
        }
        return isOutsideNavTextPresent(webDriver, expectedText);
    }

    private boolean isOutsideNavTextPresent(WebDriver webDriver, String expectedText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const wanted = (arguments[0] || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return text === wanted || text.includes(wanted) || label.includes(wanted.replace(/\\s+/g, '_'));" +
                        "});",
                expectedText
        ));
    }

    private void ensureAuditLogsPageReady() {
        semantics.enableFlutterSemantics();
        if (isAuditLogsPageLoaded(driver)) {
            return;
        }
        clickMonitoringMenu();
        clickAuditLogsMenu();
    }

    private void openNavigationItem(String semanticsLabel, String visibleText, String errorMessage) {
        if (semantics.clickSemanticsLabelViaScript(semanticsLabel)) {
            semantics.pauseAfterScroll();
            return;
        }
        By locator = semanticsLocator(semanticsLabel);
        if (!driver.findElements(locator).isEmpty()) {
            semantics.clickSemanticsElement(locator, wait);
            semantics.pauseAfterScroll();
            return;
        }
        if (semantics.clickVisibleText(visibleText)) {
            semantics.pauseAfterScroll();
            return;
        }
        if (clickActionViaScript(semanticsLabel, visibleText)) {
            semantics.pauseAfterScroll();
            return;
        }
        throw new NoSuchElementException(errorMessage);
    }

    private boolean clickSemanticsAction(String semanticsLabel, String visibleText) {
        semantics.enableFlutterSemantics();
        if (semantics.clickSemanticsLabelViaScript(semanticsLabel)) {
            return true;
        }
        if (clickActionViaScript(semanticsLabel, visibleText)) {
            return true;
        }
        return semantics.clickVisibleText(visibleText);
    }

    private boolean clickActionViaScript(String semanticsLabel, String visibleText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "const semanticsLabel = arguments[0];" +
                        "const wanted = arguments[1];" +
                        "let node = null;" +
                        "if (semanticsLabel) {" +
                        "  node = findOutsideNav('flt-semantics[aria-label=\"' + semanticsLabel + '\"]')" +
                        "    || findOutsideNav('flt-semantics[aria-label*=\"' + semanticsLabel + '\"]');" +
                        "}" +
                        "if (!node && wanted) {" +
                        "  node = Array.from(document.querySelectorAll('flt-semantics')).find(candidate => {" +
                        "    if (isInNav(candidate)) return false;" +
                        "    return (candidate.textContent || '').replace(/\\s+/g, ' ').trim() === wanted;" +
                        "  }) || null;" +
                        "}" +
                        "if (!node) return false;" +
                        "return dispatchPointerClick(node.closest('flt-semantics[flt-tappable]') || node);",
                semanticsLabel,
                visibleText
        ));
    }

    private void scrollNavigationToMonitoring() {
        scrollNavigationToLabel(SEMANTICS_MONITORING, MONITORING_TEXT);
    }

    private void scrollNavigationToAuditLogs() {
        scrollNavigationToLabel(SEMANTICS_AUDIT_LOGS_MENU, AUDIT_LOGS_TEXT);
    }

    private void scrollNavigationToLabel(String semanticsLabel, String visibleText) {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                "const semanticsLabel = (arguments[0] || '').toLowerCase();" +
                        "const wanted = arguments[1];" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "if (!menu) return;" +
                        "const target = Array.from(menu.querySelectorAll('flt-semantics')).find(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return label.includes(semanticsLabel) || text === wanted || text.includes(wanted);" +
                        "});" +
                        "if (target) target.scrollIntoView({block: 'center', inline: 'nearest'});",
                semanticsLabel,
                visibleText
        );
        semantics.pauseAfterScroll();
    }

    private void scrollToSemanticsLabel(String semanticsLabel) {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const anchor = findOutsideNav('flt-semantics[aria-label=\"' + arguments[0] + '\"]')" +
                        "  || findOutsideNav('flt-semantics[aria-label*=\"' + arguments[0] + '\"]');" +
                        "if (anchor) anchor.scrollIntoView({block: 'center', inline: 'nearest'});",
                semanticsLabel
        );
        semantics.pauseAfterScroll();
    }

    private boolean isAuditLogsPageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (!webDriver.findElements(exportLogsButton).isEmpty()
                || !webDriver.findElements(allModulesOption).isEmpty()
                || !webDriver.findElements(allActionsOption).isEmpty()
                || !webDriver.findElements(searchBarSemantics).isEmpty()
                || !webDriver.findElements(searchBarInput).isEmpty()) {
            return true;
        }
        // After a module/action filter is applied, the trigger label may change to the
        // selected option semantics (e.g. individual_users_option_individual_users / created_option_created).
        if (!webDriver.findElements(semanticsLocator(SEMANTICS_INDIVIDUAL_USERS)).isEmpty()
                || !webDriver.findElements(semanticsLocator(SEMANTICS_COMPANY_USERS)).isEmpty()
                || !webDriver.findElements(semanticsLocator(SEMANTICS_CREATED)).isEmpty()
                || !webDriver.findElements(semanticsLocator(SEMANTICS_UPDATED)).isEmpty()
                || !webDriver.findElements(semanticsLocator(SEMANTICS_EXPORT_LOGS)).isEmpty()) {
            return true;
        }
        String text = semantics.getSemanticsText(webDriver);
        if (text == null || text.isBlank()) {
            return false;
        }
        return text.contains(AUDIT_LOGS_TEXT)
                || text.contains(EXPORT_LOGS_TEXT)
                || text.contains(ALL_MODULES_TEXT)
                || text.contains(ALL_ACTIONS_TEXT)
                || text.contains(INDIVIDUAL_USERS_TEXT);
    }

    private WebElement waitForSearchInput() {
        ensureAuditLogsPageReady();
        waitForSearchFieldReady();
        if (!driver.findElements(searchBarInput).isEmpty()) {
            return wait.until(ExpectedConditions.elementToBeClickable(searchBarInput));
        }
        throw new NoSuchElementException("Search field not found (expected semantics " + SEMANTICS_SEARCH + ")");
    }

    private void waitForSearchFieldReady() {
        waitUntil(d -> !d.findElements(searchBarSemantics).isEmpty()
                || !d.findElements(searchBarInput).isEmpty(), DEFAULT_WAIT);
    }

    private boolean isNoDataAvailableOnPage(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String text = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        return text != null && text.contains(semantics.normalizeText("No data available"));
    }

    private boolean isHomePageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String text = semantics.getSemanticsText(webDriver);
        return text != null
                && (text.contains(INDIVIDUAL_USERS_TEXT)
                || text.contains(COMPANY_USERS_TEXT)
                || text.contains(MONITORING_TEXT)
                || text.contains(NAVIGATION_MENU));
    }

    private boolean pageContainsText(WebDriver webDriver, String expectedText) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String normalizedExpected = semantics.normalizeText(expectedText);
        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        return pageText != null && pageText.contains(normalizedExpected);
    }

    private void waitBeforeFilterValidation() {
        try {
            Thread.sleep(FILTER_VALIDATION_DELAY.toMillis());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean waitUntil(Function<WebDriver, Boolean> condition, Duration timeout) {
        return semantics.waitUntil(condition, timeout);
    }

    private static By semanticsLocator(String label) {
        return By.xpath(
                "//flt-semantics[@aria-label='" + label + "']" +
                        " | //flt-semantics[contains(@aria-label,'" + label + "')]");
    }
}
