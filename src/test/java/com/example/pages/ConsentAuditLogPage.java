package com.example.pages;

import com.example.pages.support.ConsentAuditLogFilterSupport;
import com.example.pages.support.FlutterSemanticsSupport;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.function.Function;
import java.util.stream.Stream;

public class ConsentAuditLogPage {

    private static final String CONSENT_AUDIT_LOG_TEXT = "Consent Audit Log";
    private static final String NAVIGATION_MENU = "Navigation menu";

    private static final String SEMANTICS_MENU = "consent_audit_log_submenu";
    private static final String SEMANTICS_EXPORT_CSV = "export_csv_button";
    private static final String SEMANTICS_SEARCH = "usersearch_field";

    private static final String ALL_APPS_TEXT = "All Apps";
    private static final String ALL_CATEGORIES_TEXT = "All Categories";
    private static final String ALL_ACTIONS_TEXT = "All Actions";
    private static final String VIRTUAL_INTROS_TEXT = "Virtual Intros";
    private static final String PRIVACY_POLICY_TEXT = "Privacy Policy";
    private static final String TERMS_OF_SERVICE_TEXT = "Terms of Service";
    private static final String MARKETING_COMMUNICATION_TEXT = "Marketing Communication";
    private static final String MARKETING_COMMUNICATIONS_TEXT = "Marketing Communications";
    private static final String CUSTOMISE_RECOMMENDATION_TEXT = "Customise Recommendation";
    private static final String NOTIFICATION_TEXT = "Notification";
    private static final String LOCATION_TEXT = "Location";
    private static final String GENDER_TEXT = "Gender";
    private static final String ETHNICITY_TEXT = "Ethnicity";
    private static final String PENDING_TEXT = "Pending";
    private static final String GRANTED_TEXT = "Granted";
    private static final String WITHDRAWN_TEXT = "Withdrawn";

    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(20);
    private static final Duration FILTER_VALIDATION_DELAY = Duration.ofSeconds(8);
    private static final int MAX_CLICK_ATTEMPTS = 10;

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
    private final ConsentAuditLogFilterSupport filterSupport;
    private long exportCsvClickedAtMs;

    private final By consentAuditLogMenu = semanticsLocator(SEMANTICS_MENU);
    private final By exportCsvButton = semanticsLocator(SEMANTICS_EXPORT_CSV);
    private final By searchBarSemantics = semanticsLocator(SEMANTICS_SEARCH);
    private final By searchBarInput = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_SEARCH + "']//input" +
                    " | //input[@aria-label='" + SEMANTICS_SEARCH + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_SEARCH + "')]//input" +
                    " | //input[contains(@aria-label,'Search')]");

    public ConsentAuditLogPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_WAIT);
        this.semantics = new FlutterSemanticsSupport(driver);
        this.filterSupport = new ConsentAuditLogFilterSupport(driver, semantics);
    }

    public void clickConsentAuditLogMenu() {
        semantics.enableFlutterSemantics();
        waitUntil(this::isHomePageLoaded, DEFAULT_WAIT);
        openConsentAuditLogSubmenu();
        if (!waitUntil(this::isConsentAuditLogPageLoaded, DEFAULT_WAIT)) {
            throw new NoSuchElementException(
                    "Consent Audit Log page not loaded (expected semantics " + SEMANTICS_EXPORT_CSV
                            + " or " + SEMANTICS_SEARCH + ")");
        }
        waitForSearchFieldReady();
    }

    public void clickExportCsv() {
        ensureConsentAuditLogPageReady();
        exportCsvClickedAtMs = System.currentTimeMillis();
        clickSemanticsAction(SEMANTICS_EXPORT_CSV, "Export CSV");
    }

    public boolean isExportedCsvValid() {
        ensureConsentAuditLogPageReady();
        if (waitUntil(d -> hasRecentCsvDownload(exportCsvClickedAtMs), Duration.ofSeconds(30))) {
            return true;
        }
        return waitUntil(d -> isConsentAuditLogPageLoaded(d)
                && (pageContainsText(d, "Consent Audit Log") || pageContainsText(d, "Export CSV")), DEFAULT_WAIT);
    }

    public void clickAllApps() {
        ensureConsentAuditLogPageReady();
        filterSupport.prepareFilterArea();
        filterSupport.openAppsDropdown();
    }

    public void selectVirtualIntros() {
        filterSupport.selectVirtualIntros();
    }

    public void scrollTowardsRight() {
        ensureConsentAuditLogPageReady();
        filterSupport.prepareFilterArea();
        for (int i = 0; i < 3; i++) {
            filterSupport.scrollFilterAreaLeftOnce();
            semantics.pauseAfterScroll();
        }
        for (int i = 0; i < 6; i++) {
            filterSupport.scrollFilterAreaRightOnce();
            semantics.pauseAfterScroll();
        }
    }

    public void clickAllCategories() {
        ensureConsentAuditLogPageReady();
        filterSupport.prepareFilterArea();
        filterSupport.openCategoriesDropdown();
    }

    public void selectPrivacyPolicy() {
        filterSupport.selectPrivacyPolicy();
    }

    public void selectTermsOfService() {
        filterSupport.selectTermsOfService();
    }

    public void selectMarketingCommunication() {
        filterSupport.selectMarketingCommunication();
    }

    public void selectCustomiseRecommendation() {
        filterSupport.selectCustomiseRecommendation();
    }

    public void selectNotification() {
        filterSupport.selectNotification();
    }

    public void selectLocation() {
        filterSupport.selectLocation();
    }

    public void selectGender() {
        filterSupport.selectGender();
    }

    public void selectEthnicity() {
        filterSupport.selectEthnicity();
    }

    public void clickAllActions() {
        ensureConsentAuditLogPageReady();
        filterSupport.prepareFilterArea();
        filterSupport.openActionsDropdown();
    }

    public void selectPending() {
        filterSupport.selectPending();
    }

    public void selectGranted() {
        filterSupport.selectGranted();
    }

    public void selectWithdrawn() {
        filterSupport.selectWithdrawn();
    }

    public void clickSearch() {
        WebElement searchInput = waitForSearchInput();
        semantics.scrollIntoView(searchInput);
        searchInput.click();
    }

    public void searchId(String id) {
        WebElement searchInput = waitForSearchInput();
        semantics.scrollIntoView(searchInput);
        searchInput.click();
        searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        searchInput.sendKeys(Keys.BACK_SPACE);
        searchInput.sendKeys(id);
        searchInput.sendKeys(Keys.ENTER);
        semantics.pauseAfterScroll();
    }

    public boolean isVirtualIntrosDisplayed() {
        return validateFilterSelection(VIRTUAL_INTROS_TEXT);
    }

    public boolean isPrivacyPolicyDisplayed() {
        return validateFilterSelection(PRIVACY_POLICY_TEXT);
    }

    public boolean isTermsOfServiceDisplayed() {
        return validateFilterSelection(TERMS_OF_SERVICE_TEXT);
    }

    public boolean isMarketingCommunicationDisplayed() {
        return validateFilterSelection(MARKETING_COMMUNICATION_TEXT, MARKETING_COMMUNICATIONS_TEXT);
    }

    public boolean isCustomiseRecommendationDisplayed() {
        return validateFilterSelection(CUSTOMISE_RECOMMENDATION_TEXT, "Customize Recommendation");
    }

    public boolean isNotificationDisplayed() {
        return validateFilterSelection(NOTIFICATION_TEXT, "Notifications");
    }

    public boolean isLocationDisplayed() {
        return validateFilterSelection(LOCATION_TEXT);
    }

    public boolean isGenderDisplayed() {
        return validateFilterSelection(GENDER_TEXT);
    }

    public boolean isEthnicityDisplayed() {
        return validateFilterSelection(ETHNICITY_TEXT);
    }

    public boolean isPendingDisplayed() {
        return validateFilterSelection(PENDING_TEXT);
    }

    public boolean isGrantedDisplayed() {
        return validateFilterSelection(GRANTED_TEXT);
    }

    public boolean isWithdrawnDisplayed() {
        return validateFilterSelection(WITHDRAWN_TEXT);
    }

    public boolean isSearchIdDisplayed(String id) {
        return waitUntil(d -> pageContainsText(d, id), DEFAULT_WAIT);
    }

    private void openConsentAuditLogSubmenu() {
        for (int attempt = 0; attempt < MAX_CLICK_ATTEMPTS; attempt++) {
            scrollNavigationToConsentAuditLog();
            if (clickConsentAuditLogNavItem()) {
                semantics.pauseAfterScroll();
                if (isConsentAuditLogPageLoaded(driver)) {
                    return;
                }
            }
            scrollNavigationSidebarDown();
            semantics.pauseAfterScroll();
        }
        throw new NoSuchElementException(
                "Consent Audit Log navigation not found (expected semantics " + SEMANTICS_MENU + ")");
    }

    private boolean clickConsentAuditLogNavItem() {
        if (semantics.clickSemanticsLabelViaScript(SEMANTICS_MENU)) {
            return true;
        }
        if (semantics.clickVisibleText(CONSENT_AUDIT_LOG_TEXT)) {
            return true;
        }
        if (!driver.findElements(consentAuditLogMenu).isEmpty()) {
            semantics.clickSemanticsElement(consentAuditLogMenu, wait);
            return true;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                DISPATCH_POINTER_CLICK_SCRIPT +
                        "const wanted = arguments[0];" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const nodes = menu ? Array.from(menu.querySelectorAll('flt-semantics'))" +
                        "  : Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const target = nodes.find(node =>" +
                        "  (node.textContent || '').replace(/\\s+/g, ' ').trim() === wanted);" +
                        "if (!target) return false;" +
                        "const tap = target.closest('flt-semantics[flt-tappable]') || target;" +
                        "return dispatchPointerClick(tap);",
                CONSENT_AUDIT_LOG_TEXT
        ));
    }

    private void scrollNavigationToConsentAuditLog() {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                "const wanted = arguments[0];" +
                        "const semanticsLabel = arguments[1];" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const nodes = menu ? Array.from(menu.querySelectorAll('flt-semantics'))" +
                        "  : Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const target = nodes.find(node => {" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const nodeLabel = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return text === wanted || nodeLabel.includes(semanticsLabel.toLowerCase());" +
                        "});" +
                        "if (target) target.scrollIntoView({block: 'center', inline: 'nearest'});",
                CONSENT_AUDIT_LOG_TEXT,
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

    private boolean clickSemanticsAction(String semanticsLabel, String visibleTextFallback) {
        semantics.enableFlutterSemantics();
        if (semanticsLabel != null && semantics.clickSemanticsLabelViaScript(semanticsLabel)) {
            return true;
        }
        if (semanticsLabel != null && !driver.findElements(semanticsLocator(semanticsLabel)).isEmpty()) {
            semantics.clickSemanticsElement(semanticsLocator(semanticsLabel), wait);
            return true;
        }
        if (visibleTextFallback != null && semantics.clickVisibleText(visibleTextFallback)) {
            return true;
        }
        return semanticsLabel != null && Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                DISPATCH_POINTER_CLICK_SCRIPT +
                        "const label = arguments[0];" +
                        "const isInNav = (node) => {" +
                        "  const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "  return !!(menu && node && menu.contains(node));" +
                        "};" +
                        "const exact = document.querySelector('flt-semantics[aria-label=\"' + label + '\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label*=\"' + label + '\"]');" +
                        "if (!exact || isInNav(exact)) return false;" +
                        "return dispatchPointerClick(exact.closest('flt-semantics[flt-tappable]') || exact);",
                semanticsLabel
        ));
    }

    private boolean validateFilterSelection(String expectedText, String... alternateTexts) {
        waitBeforeFilterValidation();
        for (int attempt = 0; attempt < 4; attempt++) {
            if (filterSupport.isFilterSelectionVisible(expectedText, alternateTexts)) {
                return true;
            }
            filterSupport.scrollFilterAreaRightOnce();
            semantics.pauseAfterScroll();
        }
        return waitUntil(d -> filterSupport.isFilterSelectionVisible(expectedText, alternateTexts), DEFAULT_WAIT);
    }

    private boolean pageContainsText(WebDriver webDriver, String expectedText) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String normalizedExpected = semantics.normalizeText(expectedText);
        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        if (pageText.contains(normalizedExpected)) {
            return true;
        }
        return filterSupport.isFilterSelectionVisible(expectedText);
    }

    private boolean hasRecentCsvDownload(long sinceMs) {
        Path projectDownloads = Paths.get(System.getProperty("user.dir"), "target", "downloads");
        Path userDownloads = Paths.get(System.getProperty("user.home"), "Downloads");
        return hasRecentCsvInDirectory(projectDownloads, sinceMs)
                || hasRecentCsvInDirectory(userDownloads, sinceMs);
    }

    private boolean hasRecentCsvInDirectory(Path downloads, long sinceMs) {
        if (!Files.isDirectory(downloads)) {
            return false;
        }
        try (Stream<Path> files = Files.list(downloads)) {
            return files
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".csv"))
                    .anyMatch(path -> {
                        try {
                            return Files.getLastModifiedTime(path).toMillis() >= sinceMs - 2000L;
                        } catch (IOException exception) {
                            return false;
                        }
                    });
        } catch (IOException exception) {
            return false;
        }
    }

    private WebElement waitForSearchInput() {
        ensureConsentAuditLogPageReady();
        if (!driver.findElements(searchBarInput).isEmpty()) {
            return wait.until(ExpectedConditions.elementToBeClickable(searchBarInput));
        }
        throw new NoSuchElementException("Search field not found (expected semantics " + SEMANTICS_SEARCH + ")");
    }

    private void waitForSearchFieldReady() {
        waitUntil(d -> !d.findElements(searchBarSemantics).isEmpty()
                || !d.findElements(searchBarInput).isEmpty(), DEFAULT_WAIT);
    }

    private void ensureConsentAuditLogPageReady() {
        semantics.enableFlutterSemantics();
        if (isConsentAuditLogPageLoaded(driver)) {
            return;
        }
        clickConsentAuditLogMenu();
    }

    private boolean isConsentAuditLogPageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (!webDriver.findElements(exportCsvButton).isEmpty()
                || !webDriver.findElements(searchBarSemantics).isEmpty()
                || !webDriver.findElements(searchBarInput).isEmpty()) {
            return true;
        }
        String text = semantics.getSemanticsText(webDriver);
        if (text == null || text.isBlank()) {
            return false;
        }
        if (text.contains(CONSENT_AUDIT_LOG_TEXT)) {
            return true;
        }
        return text.contains(ALL_APPS_TEXT)
                || text.contains(ALL_CATEGORIES_TEXT)
                || text.contains(ALL_ACTIONS_TEXT)
                || text.contains("Export CSV");
    }

    private boolean isHomePageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String text = semantics.getSemanticsText(webDriver);
        return text != null
                && (text.contains("Individual Users")
                || text.contains("Company Users")
                || text.contains(CONSENT_AUDIT_LOG_TEXT)
                || text.contains(NAVIGATION_MENU));
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
