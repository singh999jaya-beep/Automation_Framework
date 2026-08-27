package com.example.pages;

import com.example.pages.support.StatusFilterSupport;
import com.example.pages.support.UserRowActionsConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.List;

public class CompanyUsersPage extends FlutterUsersTablePage {

    private static final String COMPANY_USERS_TEXT = "Company Users";

    private static final String SEMANTICS_MENU = "companies_submenu";
    private static final String SEMANTICS_TITLE = "company_users_title";
    private static final String SEMANTICS_SEARCH = "company_search_field_input";
    private static final String SEMANTICS_ROW_EMAIL = "user_row_email_0";
    private static final String SEMANTICS_ROW_NAME = "user_row_name_0";
    private static final String SEMANTICS_COMPANY_ROW_STATUS = "company_row_status_0";
    private static final String SEMANTICS_MASKING_TOGGLE = "masking_toggle";

    private final By companyUsersMenu = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_MENU + "']" +
                    " | //flt-semantics[@aria-label='Navigation menu']" +
                    "//flt-semantics[@role='button' and contains(., '" + COMPANY_USERS_TEXT + "')]");

    private final By companyUsersTitle = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_TITLE + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_TITLE + "')]");

    private final By firstUserId = By.xpath("(//flt-semantics[starts-with(@aria-label,'VI-')])[1]");
    private final By eyeIconTapTarget = By.xpath(
            "//flt-semantics[@flt-tappable and (" +
                    "contains(@aria-label,'" + SEMANTICS_MASKING_TOGGLE + "') or " +
                    "contains(@aria-valuetext,'masked') or contains(@aria-valuetext,'unmasked') or " +
                    "contains(@aria-valuetext,'hidden') or contains(@aria-valuetext,'visible') or " +
                    ".//flt-semantics[contains(@aria-label,'" + SEMANTICS_MASKING_TOGGLE + "')])]");
    private final By eyeIcon = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_MASKING_TOGGLE + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_MASKING_TOGGLE + "')]" +
                    " | //flt-semantics[@aria-valuetext='masked' or @aria-valuetext='unmasked' or " +
                    "@aria-valuetext='hidden' or @aria-valuetext='visible' or " +
                    "@aria-valuetext='Masked' or @aria-valuetext='Unmasked' or " +
                    "@aria-valuetext='Hidden' or @aria-valuetext='Visible']");

    private final By userEmailCell = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_ROW_EMAIL + "']" +
                    " | //flt-semantics[contains(@aria-label,'user_row_email')]");

    private final By userNameCell = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_ROW_NAME + "']" +
                    " | //flt-semantics[contains(@aria-label,'user_row_name')]");

    private final By companyRowStatusCell = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_COMPANY_ROW_STATUS + "']" +
                    " | //flt-semantics[contains(@aria-label,'company_row_status')]");

    private final By searchBarSemantics = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_SEARCH + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_SEARCH + "')]");

    private final By searchBarInput = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_SEARCH + "']//input" +
                    " | //input[@aria-label='" + SEMANTICS_SEARCH + "']" +
                    " | //input[contains(@aria-label,'Search')]");

    private final StatusFilterSupport statusFilter;

    public CompanyUsersPage(WebDriver driver) {
        super(driver, UserRowActionsConfig.company());
        this.statusFilter = StatusFilterSupport.forCompany(driver, semantics);
    }

    public void clickCompanyUsersMenu() {
        openDrawerNavigationItem(
                COMPANY_USERS_TEXT,
                SEMANTICS_MENU,
                companyUsersMenu,
                this::isCompanyUsersPageLoaded);
        if (!waitUntil(this::isCompanyUsersPageLoaded, DEFAULT_WAIT)) {
            throw new NoSuchElementException(
                    "Company Users page not loaded (expected semantics " + SEMANTICS_TITLE + ")");
        }
        waitForSearchBarReady();
    }

    public boolean isCompanyUsersTitleDisplayed() {
        return isCompanyUsersPageLoaded(driver);
    }

    public void clickEyeIcon() {
        ensureTableRowReady();
        clickEyeIconForRow(userEmailCell);
    }

    public void clickEyeIconAndWaitForEmailUnmasked(String email) {
        toggleMaskingUntilUnmasked(userEmailCell, email);
    }

    public void clickEyeIconAndWaitForNameUnmasked(String name) {
        toggleMaskingUntilUnmasked(userNameCell, name);
    }

    public void clickSearchBar() {
        WebElement searchInput = waitForSearchInput();
        scrollIntoView(searchInput);
        searchInput.click();
    }

    public void enterSearchEmail(String email) {
        enterSearch(email, userEmailCell);
    }

    public void enterSearchName(String name) {
        enterSearch(name, userNameCell);
    }

    public boolean isMailIdDisplayed(String email) {
        return waitUntil(d -> isSearchResultVisibleInRow(d, userEmailCell, email), DEFAULT_WAIT);
    }

    public boolean isNameDisplayed(String name) {
        return waitUntil(d -> isSearchResultVisibleInRow(d, userNameCell, name), DEFAULT_WAIT);
    }

    public void scrollTowardsRight() {
        ensureCompanyUsersPageReady();
        statusFilter.prepareFilterArea();
        rowActions.scrollTableActionsIntoView();
        pauseAfterScroll();
        rowActions.requireDeleteOrBlockVisible();
    }

    public void clickDeleteCompanyUser() {
        ensureTableRowReady();
        rowActions.clickDelete("Company Users", this::hoverFirstUserRow);
    }

    public void enterReasonText(String reason) {
        rowActions.enterReasonText(reason);
    }

    public void clickConfirmDeleteButton() {
        rowActions.clickConfirmDelete();
    }

    public void clickBlockCompanyUser() {
        ensureTableRowReady();
        rowActions.clickBlock("Company Users", this::hoverFirstUserRow);
    }

    public void clickConfirmBlockButton() {
        rowActions.clickConfirmBlock();
    }

    public void clickUnblockCompanyUser() {
        ensureTableRowReady();
        rowActions.clickUnblock("Company Users", null);
    }

    public void clickConfirmUnblockButton() {
        rowActions.clickConfirmUnblock();
    }

    public void clickStatus() {
        ensureTableRowReady();
        statusFilter.prepareFilterArea();
        statusFilter.openDropdown();
    }

    public void clickActiveStatus() {
        statusFilter.selectActiveOption();
    }

    public void clickInactiveStatus() {
        statusFilter.selectInactiveOption();
    }

    public boolean isActiveStatusDisplayed() {
        return validateCompanyRowStatus("Active");
    }

    public boolean isInactiveOrBlockedStatusDisplayed() {
        return validateCompanyRowStatus("Blocked");
    }

    private void enterSearch(String value, By rowCellLocator) {
        WebElement searchInput = waitForSearchInput();
        scrollIntoView(searchInput);
        searchInput.click();
        searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), value);
        searchInput.sendKeys(Keys.ENTER);
        waitUntil(d -> isSearchResultVisibleInRow(d, rowCellLocator, value), DEFAULT_WAIT);
    }

    private void ensureCompanyUsersPageReady() {
        waitUntil(this::isCompanyUsersPageLoaded, DEFAULT_WAIT);
        waitForSearchBarReady();
        enableFlutterSemantics();
    }

    private void ensureTableRowReady() {
        ensureCompanyUsersPageReady();
        waitUntil(d -> !d.findElements(firstUserId).isEmpty(), DEFAULT_WAIT);
    }

    private void hoverFirstUserRow() {
        enableFlutterSemanticsOn(driver);
        ((JavascriptExecutor) driver).executeScript(
                "const row = document.querySelector('flt-semantics[aria-label=\"" + SEMANTICS_ROW_EMAIL + "\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label^=\"VI-\"]');" +
                        "if (!row) return;" +
                        "row.scrollIntoView({block: 'center', inline: 'nearest'});" +
                        "const rect = row.getBoundingClientRect();" +
                        "const x = rect.left + rect.width / 2;" +
                        "const y = rect.top + rect.height / 2;" +
                        "const hit = document.elementFromPoint(x, y) || row;" +
                        "['mouseover','mouseenter','mousemove'].forEach(type => {" +
                        "  hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                        "});"
        );
        pauseAfterScroll();
    }

    private void toggleMaskingUntilUnmasked(By rowCellLocator, String expectedValue) {
        waitUntil(this::isCompanyUsersPageLoaded, DEFAULT_WAIT);
        enableFlutterSemantics();
        for (int attempt = 0; attempt < 6; attempt++) {
            if (isRowUnmasked(driver, rowCellLocator, expectedValue)) {
                return;
            }
            WebElement rowToggle = findMaskingToggleForRow(rowCellAriaLabel(rowCellLocator));
            if (rowToggle == null && rowCellLocator.equals(userNameCell)) {
                rowToggle = findMaskingToggleForRow(SEMANTICS_ROW_EMAIL);
            }
            if (rowToggle != null) {
                scrollIntoView(rowToggle);
                clickElementReliably(rowToggle);
            } else {
                clickAllHiddenMaskingToggles();
                if (!tryClickEyeIconForRow(rowCellLocator)) {
                    tryClickEyeIconForRow(userEmailCell);
                }
            }
            if (waitUntil(d -> isRowUnmasked(d, rowCellLocator, expectedValue), Duration.ofSeconds(5))) {
                return;
            }
        }
        throw new NoSuchElementException("Data remained masked after eye icon click: " + expectedValue);
    }

    private void clickEyeIconForRow(By rowCellLocator) {
        if (!tryClickEyeIconForRow(rowCellLocator)) {
            throw new NoSuchElementException(
                    "Eye icon not found for row (expected semantics " + SEMANTICS_MASKING_TOGGLE + ")");
        }
    }

    private boolean tryClickEyeIconForRow(By rowCellLocator) {
        String rowLabel = rowCellAriaLabel(rowCellLocator);
        for (int attempt = 0; attempt < 5; attempt++) {
            if (clickSemanticsLabelViaScript(SEMANTICS_MASKING_TOGGLE)) {
                return true;
            }
            LinkedHashSet<WebElement> ordered = new LinkedHashSet<>();
            WebElement rowToggle = findMaskingToggleForRow(rowLabel);
            if (rowToggle != null) {
                ordered.add(rowToggle);
            }
            ordered.addAll(driver.findElements(eyeIconTapTarget));
            ordered.addAll(driver.findElements(eyeIcon));
            ordered.addAll(driver.findElements(eyeIconFallbackForRow(rowLabel)));
            WebElement fromScript = findEyeIconViaScript(rowLabel);
            if (fromScript != null) {
                ordered.add(fromScript);
            }
            for (WebElement eye : ordered) {
                try {
                    scrollIntoView(eye);
                    if (clickElementReliably(eye)) {
                        return true;
                    }
                } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                    // Re-locate and continue with next candidate.
                }
            }
        }
        return false;
    }

    private By eyeIconFallbackForRow(String rowSemantics) {
        return By.xpath(
                "(//flt-semantics[@aria-label='" + rowSemantics + "'])[1]" +
                        "/preceding-sibling::flt-semantics[@flt-tappable][1]" +
                        " | (//flt-semantics[@aria-label='" + rowSemantics + "'])[1]" +
                        "/following-sibling::flt-semantics[@flt-tappable][1]" +
                        " | (//flt-semantics[starts-with(@aria-label,'VI-')])[1]" +
                        "/preceding-sibling::flt-semantics[@flt-tappable][1]" +
                        " | (//flt-semantics[starts-with(@aria-label,'VI-')])[1]" +
                        "/following-sibling::flt-semantics[@flt-tappable][1]");
    }

    private WebElement findEyeIconViaScript(String rowAriaLabel) {
        enableFlutterSemanticsOn(driver);
        return (WebElement) ((JavascriptExecutor) driver).executeScript(
                "const rowLabel = arguments[0];" +
                        "let rowCell = document.querySelector('flt-semantics[aria-label=\"' + rowLabel + '\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label^=\"VI-\"]');" +
                        "if (!rowCell) {" +
                        "  rowCell = Array.from(document.querySelectorAll('flt-semantics'))" +
                        "    .find(node => (node.getAttribute('aria-label') || '').includes(rowLabel.replace('_0', '')));" +
                        "}" +
                        "if (!rowCell) return null;" +
                        "let ancestor = rowCell.parentElement;" +
                        "for (let depth = 0; depth < 8 && ancestor; depth++) {" +
                        "  const buttons = ancestor.querySelectorAll('flt-semantics[flt-tappable]');" +
                        "  for (const button of buttons) {" +
                        "    const label = (button.getAttribute('aria-label') || '').toLowerCase();" +
                        "    const value = (button.getAttribute('aria-valuetext') || '').toLowerCase();" +
                        "    if (label.includes('masking_toggle') || value === 'masked' || value === 'unmasked' ||" +
                        "        value === 'hidden' || value === 'visible') {" +
                        "      return button;" +
                        "    }" +
                        "  }" +
                        "  ancestor = ancestor.parentElement;" +
                        "}" +
                        "return null;",
                rowAriaLabel
        );
    }

    private boolean validateCompanyRowStatus(String expectedStatus) {
        scrollTowardsRight();
        waitBeforeStatusValidation();
        return waitUntil(d -> isCompanyRowStatusDisplayed(d, expectedStatus), DEFAULT_WAIT);
    }

    private boolean isCompanyRowStatusDisplayed(WebDriver webDriver, String expectedStatus) {
        enableFlutterSemanticsOn(webDriver);
        String expected = normalizeText(expectedStatus);
        if (!webDriver.findElements(companyRowStatusCell).isEmpty()) {
            WebElement statusCell = webDriver.findElement(companyRowStatusCell);
            String actual = normalizeText(getSemanticsNodeText(statusCell));
            if (actual.equals(expected) || actual.contains(expected)) {
                return true;
            }
            String label = normalizeText(statusCell.getAttribute("aria-label"));
            if (label.contains(SEMANTICS_COMPANY_ROW_STATUS) && actual.contains(expected)) {
                return true;
            }
        }
        return normalizeText(getSemanticsText(webDriver)).contains(expected);
    }

    private void waitBeforeStatusValidation() {
        try {
            Thread.sleep(STATUS_VALIDATION_DELAY.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean isSearchResultVisibleInRow(WebDriver webDriver, By rowCellLocator, String searchText) {
        enableFlutterSemanticsOn(webDriver);
        String actual = normalizeText(getRowCellText(webDriver, rowCellLocator));
        if (actual.isEmpty()) {
            return false;
        }
        return matchesSearchText(actual, searchText, false);
    }

    private boolean isRowUnmasked(WebDriver webDriver, By rowCellLocator, String searchText) {
        enableFlutterSemanticsOn(webDriver);
        String actual = normalizeText(getRowCellText(webDriver, rowCellLocator));
        String expected = normalizeText(searchText);
        String pageText = normalizeText(getSemanticsText(webDriver));
        if (actual.isEmpty() || "n/a".equals(actual)) {
            return pageText.contains(expected) && !pageText.contains("****");
        }
        if (actual.equals(expected)) {
            return true;
        }
        if (pageText.contains(expected) && !pageText.contains("****")) {
            return true;
        }
        return matchesSearchText(actual, searchText, true);
    }

    private boolean matchesSearchText(String actual, String searchText, boolean requireUnmasked) {
        String expected = normalizeText(searchText);
        if (requireUnmasked && actual.contains("****")) {
            return false;
        }
        if (actual.equals(expected) || actual.contains(expected)) {
            return true;
        }
        if (searchText.contains("@")) {
            if (requireUnmasked && actual.contains("*")) {
                return false;
            }
            String domain = searchText.substring(searchText.indexOf('@')).toLowerCase();
            return actual.toLowerCase().contains(domain);
        }
        String[] nameParts = expected.split(" ");
        if (nameParts.length > 1) {
            return actual.contains(nameParts[0]) && actual.contains(nameParts[nameParts.length - 1]);
        }
        return actual.contains(expected);
    }

    private String rowCellAriaLabel(By rowCellLocator) {
        if (rowCellLocator.equals(userNameCell)) {
            return SEMANTICS_ROW_NAME;
        }
        return SEMANTICS_ROW_EMAIL;
    }

    private WebElement findMaskingToggleForRow(String rowAriaLabel) {
        enableFlutterSemanticsOn(driver);
        return (WebElement) ((JavascriptExecutor) driver).executeScript(
                "const rowLabel = arguments[0];" +
                        "let rowCell = document.querySelector('flt-semantics[aria-label=\"' + rowLabel + '\"]');" +
                        "if (!rowCell) {" +
                        "  rowCell = Array.from(document.querySelectorAll('flt-semantics'))" +
                        "    .find(node => (node.getAttribute('aria-label') || '').includes(rowLabel.replace('_0', '')));" +
                        "}" +
                        "if (!rowCell) return null;" +
                        "let ancestor = rowCell;" +
                        "for (let depth = 0; depth < 12 && ancestor; depth++) {" +
                        "  const toggles = Array.from(ancestor.querySelectorAll('flt-semantics'));" +
                        "  const toggle = toggles.find(node => {" +
                        "    const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "    const value = (node.getAttribute('aria-valuetext') || '').toLowerCase();" +
                        "    return label.includes('masking_toggle') || value === 'masked' || value === 'unmasked' ||" +
                        "           value === 'hidden' || value === 'visible';" +
                        "  });" +
                        "  if (toggle) {" +
                        "    return toggle.closest('flt-semantics[flt-tappable]') || toggle;" +
                        "  }" +
                        "  ancestor = ancestor.parentElement;" +
                        "}" +
                        "return null;",
                rowAriaLabel
        );
    }

    private void clickAllHiddenMaskingToggles() {
        enableFlutterSemanticsOn(driver);
        List<WebElement> maskedToggles = driver.findElements(By.xpath(
                "//flt-semantics[contains(@aria-label,'" + SEMANTICS_MASKING_TOGGLE + "') and @aria-valuetext='masked']" +
                        " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_MASKING_TOGGLE + "') and @aria-valuetext='hidden']" +
                        " | //flt-semantics[@aria-valuetext='hidden' and contains(@aria-label,'" + SEMANTICS_MASKING_TOGGLE + "')]"));
        for (WebElement toggle : maskedToggles) {
            try {
                scrollIntoView(toggle);
                clickElementReliably(toggle);
            } catch (Exception ignored) {
                // Continue trying other toggles in the row.
            }
        }
    }

    private String getRowCellText(WebDriver webDriver, By rowCellLocator) {
        if (webDriver.findElements(rowCellLocator).isEmpty()) {
            return "";
        }
        return getSemanticsNodeText(webDriver.findElement(rowCellLocator));
    }

    private boolean isCompanyUsersPageLoaded(WebDriver webDriver) {
        enableFlutterSemanticsOn(webDriver);
        if (!webDriver.findElements(companyUsersTitle).isEmpty()) {
            return true;
        }
        if (!webDriver.findElements(searchBarSemantics).isEmpty()) {
            return true;
        }
        return isCompanyUsersHeadingActive(webDriver);
    }

    private boolean isCompanyUsersHeadingActive(WebDriver webDriver) {
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                "const wanted = arguments[0];" +
                        "const other = arguments[1];" +
                        "const nodes = Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const count = (target) => nodes.filter(node =>" +
                        "  (node.textContent || '').replace(/\\s+/g, ' ').trim() === target).length;" +
                        "return count(wanted) > count(other);",
                COMPANY_USERS_TEXT, "Individual Users"
        ));
    }

    private WebElement waitForSearchInput() {
        waitForSearchBarReady();
        List<WebElement> inputs = driver.findElements(searchBarInput);
        if (!inputs.isEmpty()) {
            return inputs.get(0);
        }
        WebElement searchSemantics = wait.until(ExpectedConditions.presenceOfElementLocated(searchBarSemantics));
        return searchSemantics.findElement(By.xpath(".//input"));
    }

    private boolean waitForSearchBarReady() {
        return waitUntil(d -> {
            enableFlutterSemanticsOn(d);
            return !d.findElements(searchBarSemantics).isEmpty()
                    || !d.findElements(searchBarInput).isEmpty();
        }, DEFAULT_WAIT);
    }
}
