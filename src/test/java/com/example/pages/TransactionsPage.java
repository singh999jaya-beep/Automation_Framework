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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;

public class TransactionsPage {

    private static final String NAVIGATION_MENU = "Navigation menu";
    private static final String TRANSACTIONS_SUBMENU = "transactions_submenu";
    private static final String TRANSACTIONS_TEXT = "Transactions";
    private static final String SEMANTICS_ALL = "all_submenu";
    private static final String SEMANTICS_MONTHLY_SUBSCRIPTION = "monthlysub_option_monthlysub";
    private static final String SEMANTICS_EVENT_INVITATION = "eventinv_option_eventinv";
    private static final String SEMANTICS_ADVERTISEMENT = "advertisement_submenu";
    private static final String SEMANTICS_SEARCH = "transaction_search_field_input";
    private static final String SEMANTICS_TRANSACTION_ROW_EMAIL = "transaction_row_email_0";
    private static final String SEMANTICS_TRANSACTION_ROW_NAME = "transaction_row_name_0";
    private static final String SEMANTICS_MASKING_TOGGLE = "masking_toggle";
    private static final String NO_DATA_AVAILABLE_TEXT = "No data available";

    private static final String MONTHLY_SUBSCRIPTION_TEXT = "Monthly Subscription";
    private static final String EVENT_INVITATION_TEXT = "Event Invitation";
    private static final String ADVERTISEMENT_FILTER_TEXT = "Advertisement";

    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(20);
    private static final Duration POST_LOGIN_WAIT = Duration.ofSeconds(2);
    private static final Duration STATUS_VALIDATION_DELAY = Duration.ofSeconds(10);
    private static final int MAX_CLICK_ATTEMPTS = 8;

    private static final String DISPATCH_POINTER_CLICK_SCRIPT =
            "function dispatchPointerClick(target) {" +
                    "  if (!target) return false;" +
                    "  const rect = target.getBoundingClientRect();" +
                    "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                    "  target.scrollIntoView({block: 'center', inline: 'nearest'});" +
                    "  const x = rect.left + rect.width / 2;" +
                    "  const y = rect.top + rect.height / 2;" +
                    "  const hit = document.elementFromPoint(x, y) || target;" +
                    "  ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                    "    hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                    "  });" +
                    "  return true;" +
                    "}";

    private static final String FIND_SEMANTICS_SCRIPT =
            "function findSemantics(label) {" +
                    "  const lower = (label || '').toLowerCase();" +
                    "  const exact = document.querySelector('flt-semantics[aria-label=\"' + label + '\"]');" +
                    "  if (exact) return exact;" +
                    "  const nodes = Array.from(document.querySelectorAll('flt-semantics'));" +
                    "  const byContains = nodes.find(node => {" +
                    "    const aria = (node.getAttribute('aria-label') || '').toLowerCase();" +
                    "    return aria === lower || aria.includes(lower);" +
                    "  });" +
                    "  if (byContains) return byContains;" +
                    "  return document.querySelector('input[aria-label=\"' + label + '\"]')" +
                    "    || document.querySelector('textarea[aria-label=\"' + label + '\"]');" +
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
    private final StatusFilterSupport typeFilter;

    private final By transactionsNavItem = By.xpath(
            "//flt-semantics[@aria-label='" + NAVIGATION_MENU + "']" +
                    "//flt-semantics[normalize-space(.)='" + TRANSACTIONS_TEXT + "']" +
                    " | //flt-semantics[@aria-label='" + NAVIGATION_MENU + "']" +
                    "//flt-semantics[contains(normalize-space(.), '" + TRANSACTIONS_TEXT + "')]" +
                    " | //flt-semantics[@aria-label='" + TRANSACTIONS_SUBMENU + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + TRANSACTIONS_SUBMENU + "')]");

    private final By searchBarSemantics = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_SEARCH + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_SEARCH + "')]");

    private final By searchBarInput = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_SEARCH + "']//input" +
                    " | //input[@aria-label='" + SEMANTICS_SEARCH + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_SEARCH + "')]//input" +
                    " | //input[contains(@aria-label,'Search')]");

    private final By transactionEmailCell = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_TRANSACTION_ROW_EMAIL + "']" +
                    " | //flt-semantics[contains(@aria-label,'transaction_row_email')]");

    private final By transactionNameCell = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_TRANSACTION_ROW_NAME + "']" +
                    " | //flt-semantics[contains(@aria-label,'transaction_row_name')]");

    private final By firstTransactionRow = By.xpath(
            "(//flt-semantics[@aria-label='" + SEMANTICS_TRANSACTION_ROW_EMAIL + "'])[1]" +
                    " | (//flt-semantics[contains(@aria-label,'transaction_row_email')])[1]");

    private final By eyeIconTapTarget = By.xpath(
            "//flt-semantics[@flt-tappable and (" +
                    "contains(@aria-label,'" + SEMANTICS_MASKING_TOGGLE + "') or " +
                    "contains(@aria-valuetext,'hidden') or contains(@aria-valuetext,'visible') or " +
                    ".//flt-semantics[contains(@aria-label,'" + SEMANTICS_MASKING_TOGGLE + "')])]");

    private final By eyeIcon = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_MASKING_TOGGLE + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_MASKING_TOGGLE + "')]" +
                    " | //flt-semantics[@aria-valuetext='hidden' or @aria-valuetext='visible' or " +
                    "@aria-valuetext='Hidden' or @aria-valuetext='Visible']");

    public TransactionsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_WAIT);
        this.semantics = new FlutterSemanticsSupport(driver);
        this.typeFilter = StatusFilterSupport.forTransactions(driver, semantics);
    }

    public void clickTransactionsMenu() {
        semantics.enableFlutterSemantics();
        waitUntil(this::isHomePageLoaded, DEFAULT_WAIT);
        pauseAfterLogin();
        ensureNavigationSemanticsReady();
        openTransactionsSubmenu();
        if (!waitUntil(this::isTransactionsPageLoaded, DEFAULT_WAIT)) {
            throw new NoSuchElementException(
                    "Transactions page not loaded (expected semantics " + SEMANTICS_SEARCH
                            + " or navigation item " + TRANSACTIONS_TEXT + ")");
        }
        waitForSearchBarReady();
    }

    /** Opens the All type filter dropdown (same pattern as Company Users status filter). */
    public void clickAll() {
        ensureTransactionsPageReady();
        typeFilter.openDropdown();
    }

    public void clickMonthlySubscriptionStatus() {
        typeFilter.selectOption(
                MONTHLY_SUBSCRIPTION_TEXT,
                new String[]{SEMANTICS_MONTHLY_SUBSCRIPTION},
                "Monthly subscription",
                "Subscription");
    }

    public void clickEventInvitationStatus() {
        typeFilter.selectOption(
                EVENT_INVITATION_TEXT,
                new String[]{SEMANTICS_EVENT_INVITATION},
                "Event invitation");
    }

    public void clickAdvertisementStatus() {
        typeFilter.selectOption(
                ADVERTISEMENT_FILTER_TEXT,
                new String[]{SEMANTICS_ADVERTISEMENT, "advertisement_option_advertisement"},
                "Advertisement status");
    }

    public void scrollTowardsRight() {
        ensureTransactionsPageReady();
        typeFilter.scrollFilterAreaRightOnce();
        semantics.pauseAfterScroll();
    }

    public boolean isMonthlySubscriptionDisplayed() {
        return validateTransactionRowType(MONTHLY_SUBSCRIPTION_TEXT, "Monthly subscription", "Subscription");
    }

    public boolean isEventInvitationDisplayed() {
        return validateTransactionRowType(EVENT_INVITATION_TEXT, "Event invitation");
    }

    public boolean isAdvertisementDisplayed() {
        return validateTransactionRowType(ADVERTISEMENT_FILTER_TEXT);
    }

    public void clickSearchBar() {
        WebElement searchInput = waitForSearchInput();
        semantics.scrollIntoView(searchInput);
        searchInput.click();
    }

    public void enterSearchEmail(String email) {
        enterSearch(email, transactionEmailCell);
    }

    public void enterSearchName(String name) {
        WebElement searchInput = waitForSearchInput();
        semantics.scrollIntoView(searchInput);
        searchInput.click();
        searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), name);
        searchInput.sendKeys(Keys.ENTER);
        waitUntil(d -> isNameSearchResultVisible(d, name) || isNoDataAvailableOnPage(d), DEFAULT_WAIT);
    }

    public void clickEyeIcon() {
        waitUntil(this::isTransactionsPageLoaded, DEFAULT_WAIT);
        waitUntil(d -> !d.findElements(firstTransactionRow).isEmpty()
                || !d.findElements(transactionEmailCell).isEmpty(), DEFAULT_WAIT);
        semantics.enableFlutterSemantics();
        clickEyeIconForRow(transactionEmailCell);
    }

    public void clickEyeIconAndWaitForEmailUnmasked(String email) {
        toggleMaskingUntilUnmasked(transactionEmailCell, email);
    }

    public void clickEyeIconAndWaitForNameUnmasked(String name) {
        toggleMaskingUntilUnmasked(transactionNameCell, name);
    }

    public boolean isMailIdDisplayed(String email) {
        return waitUntil(d -> isRowUnmasked(d, transactionEmailCell, email), DEFAULT_WAIT);
    }

    public boolean isNameDisplayed(String name) {
        return waitUntil(d -> isRowUnmasked(d, transactionNameCell, name), DEFAULT_WAIT);
    }

    public boolean isMailIdDisplayedOrNoDataAvailable(String email) {
        return waitUntil(d -> isSearchResultVisibleInRow(d, transactionEmailCell, email)
                || isSearchTextVisibleOnPage(d, email)
                || isNoDataAvailableOnPage(d), DEFAULT_WAIT);
    }

    public boolean isNameDisplayedOrNoDataAvailable(String name) {
        return waitUntil(d -> isRowValueDisplayed(d, transactionNameCell, name)
                || isNoDataAvailableOnPage(d), DEFAULT_WAIT);
    }

    public boolean isNoDataAvailable() {
        return waitUntil(this::isNoDataAvailableOnPage, DEFAULT_WAIT);
    }

    private boolean validateTransactionRowType(String... typeTexts) {
        scrollTowardsRight();
        waitBeforeStatusValidation();
        return waitUntil(d -> {
            semantics.enableFlutterSemanticsOn(d);
            for (String typeText : typeTexts) {
                if (typeText != null && !typeText.isBlank() && hasTransactionRowWithType(d, typeText)) {
                    return true;
                }
            }
            return false;
        }, DEFAULT_WAIT);
    }

    private boolean hasTransactionRowWithType(WebDriver webDriver, String typeText) {
        if (typeText == null || typeText.isBlank()) {
            return false;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        FIND_SEMANTICS_SCRIPT +
                        "const wanted = arguments[0];" +
                        "const search = findSemantics(arguments[1]);" +
                        "const searchBottom = search ? search.getBoundingClientRect().bottom : 0;" +
                        "const normalize = value => (value || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "const wantedNorm = normalize(wanted);" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "  if (searchBottom > 0 && rect.top < searchBottom - 20) return false;" +
                        "  const text = normalize(node.textContent);" +
                        "  const label = normalize(node.getAttribute('aria-label'));" +
                        "  if (text.includes('vi-') || text.includes('gpa.') || text.includes('showing ')) return false;" +
                        "  return text.includes(wantedNorm) || label.includes(wantedNorm);" +
                        "});",
                typeText,
                SEMANTICS_SEARCH
        ));
    }

    private void ensureNavigationSemanticsReady() {
        semantics.enableFlutterSemantics();
        waitUntil(d -> {
            semantics.enableFlutterSemanticsOn(d);
            return isSemanticsPresent(d, NAVIGATION_MENU) || isHomePageLoaded(d);
        }, DEFAULT_WAIT);
        semantics.enableFlutterSemantics();
    }

    private void openTransactionsSubmenu() {
        semantics.enableFlutterSemantics();

        for (int attempt = 0; attempt < MAX_CLICK_ATTEMPTS; attempt++) {
            scrollNavigationToTransactions();

            if (clickTransactionsSubmenu()) {
                pauseAfterNavigation();
                if (isTransactionsPageLoaded(driver)) {
                    logFoundSubmenu(TRANSACTIONS_SUBMENU);
                    return;
                }
            }

            if (clickTransactionsInNavigationMenu()) {
                pauseAfterNavigation();
                semantics.enableFlutterSemantics();
                if (isTransactionsPageLoaded(driver)) {
                    logFoundSubmenu(TRANSACTIONS_TEXT);
                    return;
                }
                if (clickTransactionsSubmenu() && isTransactionsPageLoaded(driver)) {
                    logFoundSubmenu(TRANSACTIONS_SUBMENU);
                    return;
                }
            }

            if (!driver.findElements(transactionsNavItem).isEmpty()) {
                semantics.clickSemanticsElement(transactionsNavItem, wait);
                pauseAfterNavigation();
                semantics.enableFlutterSemantics();
                if (isTransactionsPageLoaded(driver)) {
                    return;
                }
            }

            scrollNavigationSidebarDown();
            semantics.pauseAfterScroll();
        }

        logSubmenuSemantics();
        throw new NoSuchElementException(
                "Transactions navigation not found (expected nav text " + TRANSACTIONS_TEXT
                        + " or semantics " + TRANSACTIONS_SUBMENU + ")");
    }

    private boolean clickTransactionsInNavigationMenu() {
        semantics.enableFlutterSemantics();
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_SEMANTICS_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "const wanted = arguments[0];" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "if (!menu) return false;" +
                        "const nodes = Array.from(menu.querySelectorAll('flt-semantics'));" +
                        "const target = nodes.find(node => (node.textContent || '').replace(/\\s+/g, ' ').trim() === wanted);" +
                        "if (!target) return false;" +
                        "target.scrollIntoView({block: 'center', inline: 'nearest'});" +
                        "const tap = target.closest('flt-semantics[flt-tappable]') || target;" +
                        "return dispatchPointerClick(tap);",
                TRANSACTIONS_TEXT
        ));
    }

    private void logFoundSubmenu(String submenuLabel) {
        System.out.println("Available submenu semantics: " + submenuLabel);
    }

    private boolean clickTransactionsSubmenu() {
        scrollNavigationToTransactions();
        if (semantics.clickSemanticsLabelViaScript(TRANSACTIONS_SUBMENU)) {
            return true;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_SEMANTICS_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "const label = arguments[0];" +
                        "const node = findSemantics(label);" +
                        "if (!node) return false;" +
                        "const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "return dispatchPointerClick(target);",
                TRANSACTIONS_SUBMENU
        ));
    }

    private void scrollNavigationToTransactions() {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                FIND_SEMANTICS_SCRIPT +
                        "const wanted = arguments[0];" +
                        "const semanticsLabel = arguments[1];" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const target = findSemantics(semanticsLabel) || (menu ? Array.from(menu.querySelectorAll('flt-semantics')).find(node => {" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const nodeLabel = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return text === wanted || nodeLabel === semanticsLabel.toLowerCase() || nodeLabel.includes('transaction');" +
                        "}) : null);" +
                        "if (target) target.scrollIntoView({block: 'center', inline: 'nearest'});",
                TRANSACTIONS_TEXT,
                TRANSACTIONS_SUBMENU
        );
        semantics.pauseAfterScroll();
    }

    private void scrollNavigationSidebarDown() {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "if (!menu) return;" +
                        "const dispatchWheel = (x, y) => {" +
                        "  const target = document.elementFromPoint(x, y) || menu;" +
                        "  for (let i = 0; i < 3; i++) {" +
                        "    target.dispatchEvent(new WheelEvent('wheel', {" +
                        "      deltaX: 0, deltaY: 240, bubbles: true, cancelable: true" +
                        "    }));" +
                        "  }" +
                        "};" +
                        "const rect = menu.getBoundingClientRect();" +
                        "dispatchWheel(rect.left + rect.width / 2, rect.top + rect.height / 2);"
        );
        semantics.pauseAfterScroll();
    }

    private void logSubmenuSemantics() {
        String labels = (String) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics'))" +
                        ".map(node => node.getAttribute('aria-label') || '')" +
                        ".filter(label => label && (label.includes('submenu') || label.includes('transaction')))" +
                        ".join(' | ');"
        );
        System.out.println("Available submenu semantics: " + (labels == null || labels.isBlank()
                ? "(none)"
                : labels));
    }

    private void enterSearch(String value, By rowCellLocator) {
        WebElement searchInput = waitForSearchInput();
        semantics.scrollIntoView(searchInput);
        searchInput.click();
        searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), value);
        searchInput.sendKeys(Keys.ENTER);
        waitUntil(d -> isSearchResultVisibleInRow(d, rowCellLocator, value)
                || isSearchTextVisibleOnPage(d, value)
                || isNoDataAvailableOnPage(d), DEFAULT_WAIT);
    }

    private void ensureTransactionsPageReady() {
        waitUntil(this::isTransactionsPageLoaded, DEFAULT_WAIT);
        waitForSearchBarReady();
        semantics.enableFlutterSemantics();
        typeFilter.prepareFilterArea();
    }

    private void waitForSearchBarReady() {
        semantics.enableFlutterSemantics();
        typeFilter.prepareFilterArea();
        if (!waitUntil(d -> {
            semantics.enableFlutterSemanticsOn(d);
            return !d.findElements(searchBarSemantics).isEmpty()
                    || !d.findElements(searchBarInput).isEmpty();
        }, DEFAULT_WAIT)) {
            throw new NoSuchElementException(
                    "Transaction search bar not ready (expected semantics " + SEMANTICS_SEARCH + ")");
        }
    }

    private WebElement waitForSearchInput() {
        waitForSearchBarReady();
        List<WebElement> inputs = driver.findElements(searchBarInput);
        if (!inputs.isEmpty()) {
            return inputs.get(0);
        }
        if (!driver.findElements(searchBarSemantics).isEmpty()) {
            WebElement searchSemantics = wait.until(ExpectedConditions.presenceOfElementLocated(searchBarSemantics));
            List<WebElement> nestedInputs = searchSemantics.findElements(By.xpath(".//input"));
            if (!nestedInputs.isEmpty()) {
                return nestedInputs.get(0);
            }
        }
        WebElement fromAnchor = findSearchInputViaAnchor(driver);
        if (fromAnchor != null) {
            return fromAnchor;
        }
        throw new NoSuchElementException(
                "Transaction search field not found (expected semantics " + SEMANTICS_SEARCH + ")");
    }

    private WebElement findSearchInputViaAnchor(WebDriver webDriver) {
        Object node = ((JavascriptExecutor) webDriver).executeScript(
                "const primaryLabel = arguments[0];" +
                        "const findSearchAnchor = (label) => {" +
                        "  const semantics = document.querySelector('flt-semantics[aria-label=\"' + label + '\"]');" +
                        "  if (semantics) return semantics;" +
                        "  const input = document.querySelector('input[aria-label=\"' + label + '\"]');" +
                        "  if (input) return input.closest('flt-semantics') || input;" +
                        "  const byContains = Array.from(document.querySelectorAll('flt-semantics'))" +
                        "    .find(node => (node.getAttribute('aria-label') || '').includes(label));" +
                        "  if (byContains) return byContains;" +
                        "  const genericInput = document.querySelector('input[aria-label*=\"Search\"]');" +
                        "  if (genericInput) return genericInput.closest('flt-semantics') || genericInput;" +
                        "  return null;" +
                        "};" +
                        "const anchor = findSearchAnchor(primaryLabel);" +
                        "if (!anchor) return null;" +
                        "if (anchor.tagName === 'INPUT' || anchor.tagName === 'TEXTAREA') return anchor;" +
                        "const nested = anchor.querySelector('input, textarea');" +
                        "return nested || anchor;",
                SEMANTICS_SEARCH
        );
        return node instanceof WebElement webElement ? webElement : null;
    }

    private WebElement findSemanticsElement(WebDriver webDriver, String semanticsLabel) {
        Object node = ((JavascriptExecutor) webDriver).executeScript(
                FIND_SEMANTICS_SCRIPT + "return findSemantics(arguments[0]);",
                semanticsLabel
        );
        return node instanceof WebElement webElement ? webElement : null;
    }

    private void clickEyeIconForRow(By rowCellLocator) {
        if (!tryClickEyeIconForRow(rowCellLocator)) {
            throw new NoSuchElementException(
                    "Eye icon not found for row (expected semantics " + SEMANTICS_MASKING_TOGGLE + ")");
        }
    }

    private boolean tryClickEyeIconForRow(By rowCellLocator) {
        String rowLabel = rowCellAriaLabel(rowCellLocator);
        int attempts = 0;
        while (attempts < 5) {
            attempts++;
            if (semantics.clickSemanticsLabelViaScript(SEMANTICS_MASKING_TOGGLE)) {
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
                    semantics.scrollIntoView(eye);
                    if (semantics.clickElementReliably(eye)) {
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
                        " | (//flt-semantics[contains(@aria-label,'transaction_row_')])[1]" +
                        "/preceding-sibling::flt-semantics[@flt-tappable][1]" +
                        " | (//flt-semantics[contains(@aria-label,'transaction_row_')])[1]" +
                        "/following-sibling::flt-semantics[@flt-tappable][1]");
    }

    private WebElement findEyeIconViaScript(String rowAriaLabel) {
        semantics.enableFlutterSemanticsOn(driver);
        return (WebElement) ((JavascriptExecutor) driver).executeScript(
                "const rowLabel = arguments[0];" +
                        "const toggleLabel = arguments[1];" +
                        "let rowCell = document.querySelector('flt-semantics[aria-label=\"' + rowLabel + '\"]');" +
                        "if (!rowCell) {" +
                        "  rowCell = Array.from(document.querySelectorAll('flt-semantics'))" +
                        "    .find(node => (node.getAttribute('aria-label') || '').includes(rowLabel.replace('_0', '')));" +
                        "}" +
                        "if (!rowCell) return document.querySelector('flt-semantics[aria-label=\"' + toggleLabel + '\"]');" +
                        "let ancestor = rowCell.parentElement;" +
                        "for (let depth = 0; depth < 8 && ancestor; depth++) {" +
                        "  const buttons = ancestor.querySelectorAll('flt-semantics[flt-tappable]');" +
                        "  for (const button of buttons) {" +
                        "    const label = (button.getAttribute('aria-label') || '').toLowerCase();" +
                        "    const value = (button.getAttribute('aria-valuetext') || '').toLowerCase();" +
                        "    if (label === toggleLabel || label.includes(toggleLabel) ||" +
                        "        value === 'hidden' || value === 'visible') {" +
                        "      return button;" +
                        "    }" +
                        "  }" +
                        "  ancestor = ancestor.parentElement;" +
                        "}" +
                        "return document.querySelector('flt-semantics[aria-label=\"' + toggleLabel + '\"]');",
                rowAriaLabel,
                SEMANTICS_MASKING_TOGGLE
        );
    }

    private void toggleMaskingUntilUnmasked(By rowCellLocator, String expectedValue) {
        waitUntil(this::isTransactionsPageLoaded, DEFAULT_WAIT);
        semantics.enableFlutterSemantics();
        for (int attempt = 0; attempt < 6; attempt++) {
            if (isRowUnmasked(driver, rowCellLocator, expectedValue)) {
                return;
            }
            WebElement rowToggle = findMaskingToggleForRow(rowCellAriaLabel(rowCellLocator));
            if (rowToggle == null && rowCellLocator.equals(transactionNameCell)) {
                rowToggle = findMaskingToggleForRow(SEMANTICS_TRANSACTION_ROW_EMAIL);
            }
            if (rowToggle != null) {
                semantics.scrollIntoView(rowToggle);
                semantics.clickElementReliably(rowToggle);
            } else {
                clickAllHiddenMaskingToggles();
                if (!tryClickEyeIconForRow(rowCellLocator)) {
                    tryClickEyeIconForRow(transactionEmailCell);
                }
            }
            if (waitUntil(d -> isRowUnmasked(d, rowCellLocator, expectedValue), Duration.ofSeconds(5))) {
                return;
            }
        }
        throw new NoSuchElementException("Data remained masked after eye icon click: " + expectedValue);
    }

    private void clickAllHiddenMaskingToggles() {
        semantics.enableFlutterSemanticsOn(driver);
        List<WebElement> hiddenToggles = driver.findElements(By.xpath(
                "//flt-semantics[contains(@aria-label,'" + SEMANTICS_MASKING_TOGGLE + "') and @aria-valuetext='hidden']" +
                        " | //flt-semantics[@aria-valuetext='hidden' and contains(@aria-label,'" + SEMANTICS_MASKING_TOGGLE + "')]"));
        for (WebElement toggle : hiddenToggles) {
            try {
                semantics.scrollIntoView(toggle);
                semantics.clickElementReliably(toggle);
            } catch (Exception ignored) {
                // Continue trying other toggles in the row.
            }
        }
    }

    private WebElement findMaskingToggleForRow(String rowAriaLabel) {
        semantics.enableFlutterSemanticsOn(driver);
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
                        "    return label.includes('masking_toggle') || value === 'hidden' || value === 'visible';" +
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

    private String rowCellAriaLabel(By rowCellLocator) {
        if (rowCellLocator.equals(transactionNameCell)) {
            return SEMANTICS_TRANSACTION_ROW_NAME;
        }
        return SEMANTICS_TRANSACTION_ROW_EMAIL;
    }

    private boolean isSearchResultVisibleInRow(WebDriver webDriver, By rowCellLocator, String searchText) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String actual = semantics.normalizeText(getRowCellText(webDriver, rowCellLocator));
        if (actual.isEmpty()) {
            return false;
        }
        return matchesSearchText(actual, searchText, false);
    }

    private boolean isNameSearchResultVisible(WebDriver webDriver, String name) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (isSearchResultVisibleInRow(webDriver, transactionNameCell, name)) {
            return true;
        }
        String text = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        return text.contains(semantics.normalizeText(name));
    }

    private boolean isRowValueDisplayed(WebDriver webDriver, By rowCellLocator, String searchText) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String actual = semantics.normalizeText(getRowCellText(webDriver, rowCellLocator));
        String expected = semantics.normalizeText(searchText);
        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        if (actual.equals(expected) || actual.contains(expected)) {
            return true;
        }
        if (pageText.contains(expected)) {
            return true;
        }
        String[] nameParts = expected.split(" ");
        if (nameParts.length > 1) {
            return pageText.contains(nameParts[0]) && pageText.contains(nameParts[nameParts.length - 1]);
        }
        return false;
    }

    private boolean isSearchTextVisibleOnPage(WebDriver webDriver, String searchText) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        if (pageText == null || pageText.isBlank()) {
            return false;
        }
        return matchesSearchText(pageText, searchText, false);
    }

    private boolean isNoDataAvailableOnPage(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String text = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        return text != null && text.contains(semantics.normalizeText(NO_DATA_AVAILABLE_TEXT));
    }

    private boolean isRowUnmasked(WebDriver webDriver, By rowCellLocator, String searchText) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String actual = semantics.normalizeText(getRowCellText(webDriver, rowCellLocator));
        String expected = semantics.normalizeText(searchText);
        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
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
        String expected = semantics.normalizeText(searchText);
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

    private String getRowCellText(WebDriver webDriver, By rowCellLocator) {
        if (!webDriver.findElements(rowCellLocator).isEmpty()) {
            return semantics.getSemanticsNodeText(webDriver.findElement(rowCellLocator));
        }
        String semanticsLabel = rowCellLocator.equals(transactionNameCell)
                ? SEMANTICS_TRANSACTION_ROW_NAME
                : SEMANTICS_TRANSACTION_ROW_EMAIL;
        WebElement cell = findSemanticsElement(webDriver, semanticsLabel);
        if (cell != null) {
            return semantics.getSemanticsNodeText(cell);
        }
        return "";
    }

    private boolean isHomePageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String text = semantics.getSemanticsText(webDriver);
        return text != null
                && (text.contains("Individual Users")
                || text.contains("Company Users")
                || text.contains(TRANSACTIONS_TEXT)
                || text.contains("Navigation menu"));
    }

    private boolean isTransactionsPageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (!webDriver.findElements(searchBarSemantics).isEmpty()
                || !webDriver.findElements(searchBarInput).isEmpty()) {
            return true;
        }
        if (isSemanticsPresent(webDriver, SEMANTICS_ALL)
                || isSemanticsPresent(webDriver, TRANSACTIONS_SUBMENU)) {
            return true;
        }
        String text = semantics.getSemanticsText(webDriver);
        return text != null && text.contains(TRANSACTIONS_TEXT);
    }

    private boolean isSemanticsPresent(WebDriver webDriver, String semanticsLabel) {
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_SEMANTICS_SCRIPT +
                        "return !!findSemantics(arguments[0]);",
                semanticsLabel
        ));
    }

    private void pauseAfterNavigation() {
        semantics.pauseAfterScroll();
    }

    private void pauseAfterLogin() {
        try {
            Thread.sleep(POST_LOGIN_WAIT.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        semantics.enableFlutterSemantics();
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
