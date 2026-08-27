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
import java.util.List;
import java.util.function.Function;

public class ModerationCenterPage {

    private static final String MODERATION_CENTER_TEXT = "Moderation Center";
    private static final String MODERATION_CENTRE_TEXT = "Moderation Centre";
    private static final String REPORTED_USERS_TEXT = "Reported Users";

    private static final String SEMANTICS_MENU = "moderation_center_submenu";
    private static final String SEMANTICS_REPORTED_USERS_TAB = "reported_users_tab";
    private static final String SEMANTICS_ALL_STATUS = "all_status_option_all_status";
    private static final String SEMANTICS_PENDING = "status_0_option_status_0";
    private static final String SEMANTICS_REPORT_DISMISSED = "status_1_option_status_1";
    private static final String SEMANTICS_BLOCKED = "status_2_option_status_2";
    private static final String SEMANTICS_ROW_STATUS = "moderation_row_status_0";
    private static final String SEMANTICS_SEARCH = "reporting_search_field_input";
    private static final String SEMANTICS_ROW_NAME = "reporting_row_name_0";
    private static final String SEMANTICS_ROW_EMAIL = "reporting_row_email_0";
    private static final String SEMANTICS_VIEW_REPORTED_EVENT = "view_reported_event_button_0";

    private static final String PENDING_TEXT = "Pending";
    private static final String REPORT_DISMISSED_TEXT = "Report Dismissed";
    private static final String BLOCKED_TEXT = "Blocked";
    private static final String NO_DATA_AVAILABLE_TEXT = "No data available";

    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(20);
    private static final Duration STATUS_VALIDATION_DELAY = Duration.ofSeconds(10);

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final FlutterSemanticsSupport semantics;
    private final StatusFilterSupport statusFilter;

    private final By moderationMenu = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_MENU + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_MENU + "')]");

    private final By reportedUsersTab = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_REPORTED_USERS_TAB + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_REPORTED_USERS_TAB + "')]");

    private final By moderationRowStatusCell = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_ROW_STATUS + "']" +
                    " | //flt-semantics[contains(@aria-label,'moderation_row_status')]");

    private final By searchBarSemantics = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_SEARCH + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_SEARCH + "')]");

    private final By searchBarInput = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_SEARCH + "']//input" +
                    " | //input[@aria-label='" + SEMANTICS_SEARCH + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_SEARCH + "')]//input" +
                    " | //input[contains(@aria-label,'Search')]");

    private final By reportingNameCell = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_ROW_NAME + "']" +
                    " | //flt-semantics[contains(@aria-label,'reporting_row_name')]");

    private final By reportingEmailCell = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_ROW_EMAIL + "']" +
                    " | //flt-semantics[contains(@aria-label,'reporting_row_email')]");

    public ModerationCenterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_WAIT);
        this.semantics = new FlutterSemanticsSupport(driver);
        this.statusFilter = StatusFilterSupport.forModeration(driver, semantics);
    }

    public void clickModerationCenterMenu() {
        semantics.enableFlutterSemantics();
        waitUntil(this::isHomePageLoaded, DEFAULT_WAIT);
        openModerationCenterSubmenu();
        waitUntil(this::isModerationCenterPageLoaded, DEFAULT_WAIT);
        waitForSearchBarReady();
    }

    public void clickReportedUsersTab() {
        ensureModerationCenterPageReady();
        semantics.enableFlutterSemantics();
        if (semantics.clickSemanticsLabelViaScript(SEMANTICS_REPORTED_USERS_TAB)) {
            pauseAfterNavigation();
            waitForReportedUsersTabReady();
            return;
        }
        if (!driver.findElements(reportedUsersTab).isEmpty()) {
            semantics.clickSemanticsElement(reportedUsersTab, wait);
            pauseAfterNavigation();
            waitForReportedUsersTabReady();
            return;
        }
        if (semantics.clickVisibleText(REPORTED_USERS_TEXT)) {
            pauseAfterNavigation();
            waitForReportedUsersTabReady();
            return;
        }
        throw new NoSuchElementException(
                "Reported Users tab not found (expected semantics " + SEMANTICS_REPORTED_USERS_TAB + ")");
    }

    public void clickSearchBar() {
        WebElement searchInput = waitForSearchInput();
        semantics.scrollIntoView(searchInput);
        searchInput.click();
    }

    public void enterSearchName(String name) {
        enterSearch(name, reportingNameCell);
    }

    public void enterSearchEmail(String email) {
        enterSearch(email, reportingEmailCell);
    }

    public boolean isNameDisplayed(String name) {
        return waitUntil(d -> isRowValueDisplayed(d, reportingNameCell, name), DEFAULT_WAIT);
    }

    public boolean isEmailDisplayed(String email) {
        return waitUntil(d -> isRowValueDisplayed(d, reportingEmailCell, email), DEFAULT_WAIT);
    }

    public boolean isNameDisplayedOrNoDataAvailable(String name) {
        return waitUntil(d -> isRowValueDisplayed(d, reportingNameCell, name)
                || isNoDataAvailableOnPage(d), DEFAULT_WAIT);
    }

    public boolean isEmailDisplayedOrNoDataAvailable(String email) {
        return waitUntil(d -> isRowValueDisplayed(d, reportingEmailCell, email)
                || isNoDataAvailableOnPage(d), DEFAULT_WAIT);
    }

    public boolean isNoDataAvailable() {
        return waitUntil(this::isNoDataAvailableOnPage, DEFAULT_WAIT);
    }

    public void clickStatus() {
        ensureModerationCenterPageReady();
        statusFilter.prepareFilterArea();
        statusFilter.openDropdown();
    }

    public void clickPendingStatus() {
        statusFilter.selectOption(
                PENDING_TEXT,
                new String[]{SEMANTICS_PENDING},
                "Pending status");
    }

    public void clickReportDismissedStatus() {
        statusFilter.selectOption(
                REPORT_DISMISSED_TEXT,
                new String[]{SEMANTICS_REPORT_DISMISSED},
                "Report dismissed");
    }

    public void clickBlockedStatus() {
        statusFilter.selectOption(
                BLOCKED_TEXT,
                new String[]{SEMANTICS_BLOCKED},
                "Blocked status");
    }

    public boolean isPendingStatusDisplayed() {
        return validateModerationRowStatus(PENDING_TEXT);
    }

    public boolean isReportDismissedStatusDisplayed() {
        return validateModerationRowStatus(REPORT_DISMISSED_TEXT);
    }

    public boolean isBlockedStatusDisplayed() {
        return validateModerationRowStatus(BLOCKED_TEXT);
    }

    public void prepareForRowAction() {
        ensureModerationCenterPageReady();
        waitBeforeStatusValidation();
    }

    public void scrollTowardsRight() {
        ensureModerationCenterPageReady();
        statusFilter.scrollFilterAreaRightOnce();
        scrollModerationTableRight();
        semantics.pauseAfterScroll();
    }

    private void scrollModerationTableRight() {
        semantics.enableFlutterSemanticsOn(driver);
        ((JavascriptExecutor) driver).executeScript(
                "const rowLabel = arguments[0];" +
                        "const viewLabel = arguments[1];" +
                        "const rowCell = document.querySelector('flt-semantics[aria-label=\"' + rowLabel + '\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label^=\"reporting_row_name\"]');" +
                        "const viewBtn = document.querySelector('flt-semantics[aria-label=\"' + viewLabel + '\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label^=\"view_reported_event_button\"]');" +
                        "const anchor = viewBtn || rowCell;" +
                        "if (!anchor) return;" +
                        "const isScrollable = (el) => {" +
                        "  if (!el) return false;" +
                        "  const style = window.getComputedStyle(el);" +
                        "  const overflowX = style.overflowX;" +
                        "  return (overflowX === 'auto' || overflowX === 'scroll') && el.scrollWidth > el.clientWidth + 8;" +
                        "};" +
                        "const scrollNodeFull = (node) => {" +
                        "  node.scrollLeft = Math.max(0, node.scrollWidth - node.clientWidth);" +
                        "};" +
                        "let ancestor = anchor.parentElement;" +
                        "for (let depth = 0; depth < 16 && ancestor; depth++) {" +
                        "  if (isScrollable(ancestor)) scrollNodeFull(ancestor);" +
                        "  Array.from(ancestor.querySelectorAll('*')).filter(isScrollable).forEach(scrollNodeFull);" +
                        "  ancestor = ancestor.parentElement;" +
                        "}" +
                        "anchor.scrollIntoView({block: 'center', inline: 'end'});" +
                        "const rect = anchor.getBoundingClientRect();" +
                        "const target = document.elementFromPoint(rect.left + rect.width / 2, rect.top + rect.height / 2) || anchor;" +
                        "for (let i = 0; i < 4; i++) {" +
                        "  target.dispatchEvent(new WheelEvent('wheel', { deltaX: 400, deltaY: 0, bubbles: true, cancelable: true }));" +
                        "}",
                SEMANTICS_ROW_NAME,
                SEMANTICS_VIEW_REPORTED_EVENT
        );
    }

    private void openModerationCenterSubmenu() {
        semantics.enableFlutterSemantics();

        if (semantics.clickSemanticsLabelViaScript(SEMANTICS_MENU)) {
            pauseAfterNavigation();
            return;
        }
        if (!driver.findElements(moderationMenu).isEmpty()) {
            semantics.clickSemanticsElement(moderationMenu, wait);
            pauseAfterNavigation();
            return;
        }

        if (!semantics.clickVisibleText(MODERATION_CENTER_TEXT)
                && !semantics.clickVisibleText(MODERATION_CENTRE_TEXT)) {
            throw new NoSuchElementException(
                    "Moderation Center menu item not found (expected semantics " + SEMANTICS_MENU + ")");
        }
        pauseAfterNavigation();

        if (semantics.clickSemanticsLabelViaScript(SEMANTICS_MENU)) {
            pauseAfterNavigation();
            return;
        }
        if (!driver.findElements(moderationMenu).isEmpty()) {
            semantics.clickSemanticsElement(moderationMenu, wait);
            pauseAfterNavigation();
            return;
        }
        if (!semantics.clickVisibleText(MODERATION_CENTER_TEXT)
                && !semantics.clickVisibleText(MODERATION_CENTRE_TEXT)) {
            throw new NoSuchElementException(
                    "Moderation Center submenu not found (expected semantics " + SEMANTICS_MENU + ")");
        }
        pauseAfterNavigation();
    }

    private void ensureModerationCenterPageReady() {
        waitUntil(this::isModerationCenterPageLoaded, DEFAULT_WAIT);
        semantics.enableFlutterSemantics();
    }

    private void waitForReportedUsersTabReady() {
        if (!waitUntil(this::isReportedUsersTabContentLoaded, DEFAULT_WAIT)) {
            throw new NoSuchElementException(
                    "Reported Users tab content did not load (expected semantics " + SEMANTICS_REPORTED_USERS_TAB + ")");
        }
    }

    private boolean isReportedUsersTabContentLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (!webDriver.findElements(reportedUsersTab).isEmpty()) {
            return true;
        }
        if (isModerationCenterPageLoaded(webDriver)) {
            String text = semantics.getSemanticsText(webDriver);
            return text != null && text.contains(REPORTED_USERS_TEXT);
        }
        return false;
    }

    private boolean validateModerationRowStatus(String expectedStatus) {
        ensureModerationCenterPageReady();
        waitBeforeStatusValidation();
        return waitUntil(d -> isModerationRowStatusDisplayed(d, expectedStatus), DEFAULT_WAIT);
    }

    private boolean isModerationRowStatusDisplayed(WebDriver webDriver, String expectedStatus) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String expected = semantics.normalizeText(expectedStatus);

        List<org.openqa.selenium.WebElement> statusCells = webDriver.findElements(By.xpath(
                "//flt-semantics[contains(@aria-label,'moderation_row_status_')]"));
        for (org.openqa.selenium.WebElement cell : statusCells) {
            String actual = semantics.normalizeText(semantics.getSemanticsNodeText(cell));
            if (actual.equals(expected) || actual.contains(expected)) {
                return true;
            }
            String label = semantics.normalizeText(cell.getAttribute("aria-label"));
            if (label.contains("moderation_row_status") && actual.contains(expected)) {
                return true;
            }
        }

        if (!webDriver.findElements(moderationRowStatusCell).isEmpty()) {
            org.openqa.selenium.WebElement statusCell = webDriver.findElement(moderationRowStatusCell);
            String actual = semantics.normalizeText(semantics.getSemanticsNodeText(statusCell));
            if (actual.equals(expected) || actual.contains(expected)) {
                return true;
            }
        }

        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        return pageText.contains(expected);
    }

    private void enterSearch(String value, By rowCellLocator) {
        WebElement searchInput = waitForSearchInput();
        semantics.scrollIntoView(searchInput);
        searchInput.click();
        searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), value);
        searchInput.sendKeys(Keys.ENTER);
        waitUntil(d -> isRowValueDisplayed(d, rowCellLocator, value)
                || isNoDataAvailableOnPage(d), DEFAULT_WAIT);
    }

    private WebElement waitForSearchInput() {
        waitForSearchBarReady();
        List<WebElement> inputs = driver.findElements(searchBarInput);
        if (!inputs.isEmpty()) {
            return inputs.get(0);
        }
        WebElement semanticsNode = wait.until(ExpectedConditions.presenceOfElementLocated(searchBarSemantics));
        return semanticsNode.findElement(By.xpath(".//input"));
    }

    private void waitForSearchBarReady() {
        waitUntil(d -> {
            semantics.enableFlutterSemanticsOn(d);
            return !d.findElements(searchBarSemantics).isEmpty()
                    || !d.findElements(searchBarInput).isEmpty();
        }, DEFAULT_WAIT);
    }

    private boolean isSearchResultVisibleInRow(WebDriver webDriver, By rowCellLocator, String searchText) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String actual = semantics.normalizeText(getRowCellText(webDriver, rowCellLocator));
        if (actual.isEmpty()) {
            return false;
        }
        String expected = semantics.normalizeText(searchText);
        if (actual.equals(expected) || actual.contains(expected)) {
            return true;
        }
        String[] nameParts = expected.split(" ");
        if (nameParts.length > 1) {
            return actual.contains(nameParts[0]) && actual.contains(nameParts[nameParts.length - 1]);
        }
        return actual.contains(expected);
    }

    private boolean isRowValueDisplayed(WebDriver webDriver, By rowCellLocator, String searchText) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (isSearchResultVisibleInRow(webDriver, rowCellLocator, searchText)) {
            return true;
        }
        String expected = semantics.normalizeText(searchText);
        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        if (pageText.contains(expected)) {
            return true;
        }
        if (searchText.contains("@")) {
            String domain = searchText.substring(searchText.indexOf('@')).toLowerCase();
            if (pageText.contains(domain)) {
                return true;
            }
        }
        String[] nameParts = expected.split(" ");
        if (nameParts.length > 1) {
            return pageText.contains(nameParts[0]) && pageText.contains(nameParts[nameParts.length - 1]);
        }
        return false;
    }

    private boolean isNoDataAvailableOnPage(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String text = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        return text != null && text.contains(semantics.normalizeText(NO_DATA_AVAILABLE_TEXT));
    }

    private String getRowCellText(WebDriver webDriver, By rowCellLocator) {
        if (webDriver.findElements(rowCellLocator).isEmpty()) {
            return "";
        }
        return semantics.getSemanticsNodeText(webDriver.findElement(rowCellLocator));
    }

    private boolean isModerationCenterPageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (!webDriver.findElements(searchBarSemantics).isEmpty()
                || !webDriver.findElements(searchBarInput).isEmpty()) {
            return true;
        }
        if (!webDriver.findElements(By.xpath(
                "//flt-semantics[@aria-label='" + SEMANTICS_ALL_STATUS + "']" +
                        " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_ALL_STATUS + "')]")).isEmpty()) {
            return true;
        }
        String text = semantics.getSemanticsText(webDriver);
        return text != null
                && (text.contains(MODERATION_CENTER_TEXT)
                || text.contains(MODERATION_CENTRE_TEXT)
                || text.contains("Status"));
    }

    private boolean isHomePageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String text = semantics.getSemanticsText(webDriver);
        return text != null
                && (text.contains("Individual Users")
                || text.contains("Company Users")
                || text.contains(MODERATION_CENTER_TEXT)
                || text.contains(MODERATION_CENTRE_TEXT)
                || text.contains("Navigation menu"));
    }

    private void pauseAfterNavigation() {
        semantics.pauseAfterScroll();
    }

    private void waitBeforeStatusValidation() {
        try {
            Thread.sleep(STATUS_VALIDATION_DELAY.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean waitUntil(Function<WebDriver, Boolean> condition, Duration timeout) {
        return semantics.waitUntil(condition, timeout);
    }
}
