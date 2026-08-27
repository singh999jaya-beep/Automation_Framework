package com.example.pages;

import com.example.pages.support.FlutterSemanticsSupport;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DeletedAppUsersPage {

    private static final String DELETED_APP_USERS_TEXT = "Deleted App Users";
    private static final String NAVIGATION_MENU = "Navigation menu";
    private static final String INDIVIDUAL_TEXT = "Individual";
    private static final String COMPANY_TEXT = "Company";
    private static final String ALL_TEXT = "All";
    private static final String DEACTIVATED_TEXT = "Deactivated";
    private static final String DELETED_TEXT = "Deleted";
    private static final String RECOVERED_TEXT = "Recovered";

    private static final String SEMANTICS_MENU = "drawer_item_deleted_app_users";
    private static final String SEMANTICS_USER_TYPE_ALL = "user_type_all_option";
    private static final String SEMANTICS_USER_TYPE_INDIVIDUAL = "user_type_individual_option";
    private static final String SEMANTICS_USER_TYPE_COMPANY = "user_type_company_option";
    private static final String SEMANTICS_DELETED_USER_TYPE = "deleted_user_type_0";
    private static final String SEMANTICS_STATUS_ALL = "status_all_option";
    private static final String SEMANTICS_STATUS_DEACTIVATED = "status_deactivated_option";
    private static final String SEMANTICS_STATUS_DELETED = "status_deleted_option";
    private static final String SEMANTICS_STATUS_RECOVERED = "status_recovered_option";
    private static final String SEMANTICS_DELETED_USER_STATUS = "deleted_user_status_0";
    private static final String SEMANTICS_DELETED_USER_STATUS_LEGACY = "deleted_user_deletion_mode_0";
    private static final String SEMANTICS_RECOVER_BUTTON = "recover_account_button_0";
    private static final String SEMANTICS_RECOVER_BUTTON_PREFIX = "recover_account_button";
    private static final String SEMANTICS_SEARCH = "search_deleted_users_field";
    private static final String RECOVER_TEXT = "Recover";
    private static final String USER_ID_LABEL_PREFIX = "User ID: ";
    private static final String NO_DATA_AVAILABLE_TEXT = "No data available";

    private static final Duration DEFAULT_WAIT = FlutterSemanticsSupport.DEFAULT_WAIT;
    private static final Duration FILTER_VALIDATION_DELAY = Duration.ofSeconds(5);
    private static final Duration RECOVER_WAIT = Duration.ofSeconds(12);
    private static final int MAX_NAV_ATTEMPTS = 12;

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

    private static final ThreadLocal<String> LAST_SELECTED_USER_TYPE = new ThreadLocal<>();
    private static final ThreadLocal<String> LAST_SELECTED_STATUS = new ThreadLocal<>();
    private static final ThreadLocal<Double> USER_TYPE_DROPDOWN_X = new ThreadLocal<>();
    private static final ThreadLocal<Double> USER_TYPE_DROPDOWN_Y = new ThreadLocal<>();
    private static final ThreadLocal<Double> STATUS_DROPDOWN_X = new ThreadLocal<>();
    private static final ThreadLocal<Double> STATUS_DROPDOWN_Y = new ThreadLocal<>();

    private final By deletedAppUsersMenu = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_MENU + "']" +
                    " | //flt-semantics[@role='button' and normalize-space(.)='" + DELETED_APP_USERS_TEXT + "']");
    private final By searchBarSemantics = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_SEARCH + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_SEARCH + "')]");
    private final By searchBarInput = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_SEARCH + "']//input" +
                    " | //input[@aria-label='" + SEMANTICS_SEARCH + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_SEARCH + "')]//input" +
                    " | //input[contains(@aria-label,'Search')]");

    public DeletedAppUsersPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_WAIT);
        this.semantics = new FlutterSemanticsSupport(driver);
    }

    public void clickDeletedAppUsersMenu() {
        semantics.enableFlutterSemantics();
        waitUntil(this::isHomePageLoaded, DEFAULT_WAIT);
        LAST_SELECTED_USER_TYPE.remove();
        LAST_SELECTED_STATUS.remove();
        USER_TYPE_DROPDOWN_X.remove();
        USER_TYPE_DROPDOWN_Y.remove();
        STATUS_DROPDOWN_X.remove();
        STATUS_DROPDOWN_Y.remove();
        if (isDeletedAppUsersPageLoaded(driver)) {
            return;
        }
        for (int attempt = 0; attempt < MAX_NAV_ATTEMPTS; attempt++) {
            scrollNavigationToDeletedAppUsers();
            if (clickDeletedAppUsersNavItem()) {
                semantics.pauseAfterScroll();
                if (waitUntil(this::isDeletedAppUsersPageLoaded, DEFAULT_WAIT)) {
                    return;
                }
            }
            scrollNavigationSidebarDown();
            semantics.pauseAfterScroll();
        }
        String current = driver.getCurrentUrl();
        String origin = current.contains("://")
                ? current.substring(0, current.indexOf('/', current.indexOf("://") + 3))
                : current;
        driver.get(origin + "/deleted-users");
        semantics.enableFlutterSemantics();
        if (waitUntil(this::isDeletedAppUsersPageLoaded, DEFAULT_WAIT)) {
            return;
        }
        throw new NoSuchElementException(
                "Deleted App Users page not loaded (expected /deleted-users or User Type filter)");
    }

    public void clickAll() {
        ensureDeletedAppUsersPageReady();
        waitForFilterTriggersReady();
        semantics.forceEnableFlutterSemantics();
        // Capture both triggers before opening either popup (opening collapses semantics).
        captureUserTypeDropdownPoint();
        captureStatusDropdownPoint();
        LAST_SELECTED_USER_TYPE.remove();
        LAST_SELECTED_STATUS.remove();
        if (USER_TYPE_DROPDOWN_X.get() == null && STATUS_DROPDOWN_X.get() == null) {
            throw new NoSuchElementException(
                    "All filter triggers not found. Available=" + debugAllButtons());
        }
        // Open User Type All for Individual/Company flows. Status flows dismiss and
        // reopen Status All from the captured rightmost coordinates.
        if (USER_TYPE_DROPDOWN_X.get() != null
                && cdpClick(USER_TYPE_DROPDOWN_X.get(), USER_TYPE_DROPDOWN_Y.get())) {
            semantics.pauseAfterScroll();
            return;
        }
        openUserTypeDropdown();
    }

    private void waitForFilterTriggersReady() {
        if (!waitUntil(d -> {
            semantics.forceEnableFlutterSemantics();
            return allButtonCenter(true) != null
                    || hasSemantics(SEMANTICS_USER_TYPE_ALL)
                    || hasSemantics(SEMANTICS_STATUS_ALL)
                    || hasAnyExactAllButton();
        }, DEFAULT_WAIT)) {
            throw new NoSuchElementException(
                    "Deleted App Users filters not ready (expected All / "
                            + SEMANTICS_USER_TYPE_ALL + " / " + SEMANTICS_STATUS_ALL
                            + "). url=" + safeUrl()
                            + " count=" + semanticsCount()
                            + " Available=" + debugAllButtons()
                            + " sample=" + debugShortTexts());
        }
    }

    private boolean hasAnyExactAllButton() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics')).some(n => {" +
                        "  const text = (n.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const rect = n.getBoundingClientRect();" +
                        "  return text === 'All' && rect.width > 10 && rect.height > 10;" +
                        "});"
        ));
    }

    private long semanticsCount() {
        Object raw = ((JavascriptExecutor) driver).executeScript(
                "const host = document.querySelector('flt-semantics-host');" +
                        "return host ? host.querySelectorAll('flt-semantics').length : 0;"
        );
        return raw instanceof Number ? ((Number) raw).longValue() : 0L;
    }

    private String safeUrl() {
        try {
            return driver.getCurrentUrl();
        } catch (Exception e) {
            return "";
        }
    }

    private String debugShortTexts() {
        Object raw = ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics')).map(n => {" +
                        "  const t = (n.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const r = n.getBoundingClientRect();" +
                        "  if (r.width <= 0 || r.height <= 0 || !t || t.length > 24) return null;" +
                        "  return t;" +
                        "}).filter(Boolean).filter((v,i,a)=>a.indexOf(v)===i).slice(0,30);"
        );
        return String.valueOf(raw);
    }

    public void clickIndividual() {
        selectUserTypeOption(INDIVIDUAL_TEXT, SEMANTICS_USER_TYPE_INDIVIDUAL);
    }

    public void clickCompany() {
        selectUserTypeOption(COMPANY_TEXT, SEMANTICS_USER_TYPE_COMPANY);
    }

    public void clickDeactivated() {
        selectStatusOption(DEACTIVATED_TEXT, SEMANTICS_STATUS_DEACTIVATED);
    }

    public void clickDeletedStatus() {
        selectStatusOption(DELETED_TEXT, SEMANTICS_STATUS_DELETED);
    }

    public void clickRecovered() {
        selectStatusOption(RECOVERED_TEXT, SEMANTICS_STATUS_RECOVERED);
    }

    public void scrollTowardsRight() {
        ensureDeletedAppUsersPageReady();
        semantics.forceEnableFlutterSemantics();
        waitForDeletedUsersTableReady();
        if (!isRecoverButtonPresentInTree()) {
            ensureRecoverableRowsVisible();
        }
        for (int attempt = 0; attempt < 8; attempt++) {
            semantics.forceEnableFlutterSemantics();
            scrollActionsColumnIntoView();
            semantics.pauseAfterScroll();
            if (isRecoverButtonVisible() || isRecoverButtonPresentInTree()) {
                return;
            }
        }
        // Scroll is best-effort here; clickRecover owns the hard assertion.
    }

    public void clickRecover() {
        ensureDeletedAppUsersPageReady();
        semantics.forceEnableFlutterSemantics();
        waitForDeletedUsersTableReady();
        if (!isRecoverButtonPresentInTree()) {
            ensureRecoverableRowsVisible();
        }
        for (int attempt = 0; attempt < 8; attempt++) {
            semantics.forceEnableFlutterSemantics();
            scrollActionsColumnIntoView();
            semantics.pauseAfterScroll();

            String recoverLabel = findRecoverSemanticsLabel();
            if (recoverLabel != null && clickSemanticsOrCdp(recoverLabel, RECOVER_TEXT)) {
                semantics.pauseAfterScroll();
                return;
            }
            if (clickOptionByVisibleText(RECOVER_TEXT)
                    || clickOptionByVisibleText("Recover Account")
                    || clickRecoverByLooseText()) {
                semantics.pauseAfterScroll();
                return;
            }
            if (clickRecoverInActionsColumn()) {
                semantics.pauseAfterScroll();
                return;
            }
            Map<String, Double> point = recoverLabel != null
                    ? centerOfSemantics(recoverLabel)
                    : centerOfRecoverButton();
            if (point != null && cdpClick(point.get("x"), point.get("y"))) {
                semantics.pauseAfterScroll();
                return;
            }
        }
        logRecoverRelatedSemantics();
        throw new NoSuchElementException(
                "Recover button not clickable (expected semantics "
                        + SEMANTICS_RECOVER_BUTTON_PREFIX + "_* / recover* or visible text '"
                        + RECOVER_TEXT + "'). Available=" + debugShortTexts());
    }

    public void clickSearch() {
        ensureDeletedAppUsersPageReady();
        WebElement searchInput = waitForSearchInput();
        semantics.scrollIntoView(searchInput);
        searchInput.click();
    }

    public void enterSearchId(String userId) {
        ensureDeletedAppUsersPageReady();
        WebElement searchInput = waitForSearchInput();
        semantics.scrollIntoView(searchInput);
        searchInput.click();
        searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        searchInput.sendKeys(Keys.BACK_SPACE);
        searchInput.sendKeys(userId);
        searchInput.sendKeys(Keys.ENTER);
        semantics.pauseAfterScroll();
        waitUntil(d -> isUserIdDisplayedOnPage(d, userId) || isNoDataAvailableOnPage(d), DEFAULT_WAIT);
    }

    public boolean isUserIdDisplayed(String userId) {
        waitBeforeFilterValidation();
        reenableSemantics();
        return waitUntil(d -> isUserIdDisplayedOnPage(d, userId), DEFAULT_WAIT);
    }

    private void scrollActionsColumnIntoView() {
        ((JavascriptExecutor) driver).executeScript(
                "const recoverPrefix = arguments[0];" +
                        "const recoverText = arguments[1];" +
                        "const typeLabel = arguments[2];" +
                        "const statusLabel = arguments[3];" +
                        "const statusLegacy = arguments[4];" +
                        "const recover = Array.from(document.querySelectorAll('flt-semantics')).find(n => {" +
                        "  const label = (n.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (n.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return label.includes(recoverPrefix) || text === recoverText;" +
                        "});" +
                        "const rowAnchor = document.querySelector('flt-semantics[aria-label=\"' + statusLabel + '\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label^=\"deleted_user_status_\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label=\"' + statusLegacy + '\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label=\"' + typeLabel + '\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label^=\"deleted_user_\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label^=\"VI-\"]');" +
                        "const anchor = recover || rowAnchor;" +
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
                        "const target = document.elementFromPoint(" +
                        "  rect.left + rect.width / 2, rect.top + rect.height / 2) || anchor;" +
                        "for (let i = 0; i < 10; i++) {" +
                        "  target.dispatchEvent(new WheelEvent('wheel', {" +
                        "    deltaX: 450, deltaY: 0, bubbles: true, cancelable: true" +
                        "  }));" +
                        "}",
                SEMANTICS_RECOVER_BUTTON_PREFIX,
                RECOVER_TEXT,
                SEMANTICS_DELETED_USER_TYPE,
                SEMANTICS_DELETED_USER_STATUS,
                SEMANTICS_DELETED_USER_STATUS_LEGACY
        );
    }

    private void waitForDeletedUsersTableReady() {
        waitUntil(d -> hasSemantics(SEMANTICS_DELETED_USER_TYPE)
                        || hasSemantics(SEMANTICS_DELETED_USER_STATUS)
                        || hasSemantics(SEMANTICS_DELETED_USER_STATUS_LEGACY)
                        || isRecoverButtonPresentInTree()
                        || isNoDataAvailableOnPage(d),
                DEFAULT_WAIT);
    }

    /**
     * Recover exists for Deactivated (and sometimes Deleted) accounts. Prefer Deactivated
     * so the Recover action is present after scrolling to {@code deleted_user_status_0}.
     */
    private void ensureRecoverableRowsVisible() {
        try {
            if (STATUS_DROPDOWN_X.get() == null || STATUS_DROPDOWN_Y.get() == null) {
                captureStatusDropdownPoint();
            }
            selectStatusOption(DEACTIVATED_TEXT, SEMANTICS_STATUS_DEACTIVATED);
            semantics.pauseAfterScroll();
            waitUntil(d -> isRecoverButtonPresentInTree()
                            || isStatusValueVisibleInRows(DEACTIVATED_TEXT)
                            || hasSemantics(SEMANTICS_DELETED_USER_STATUS)
                            || isNoDataAvailableOnPage(d),
                    RECOVER_WAIT);
            if (isRecoverButtonPresentInTree()
                    || isStatusValueVisibleInRows(DEACTIVATED_TEXT)
                    || hasSemantics(SEMANTICS_DELETED_USER_STATUS)) {
                return;
            }
            selectStatusOption(DELETED_TEXT, SEMANTICS_STATUS_DELETED);
            semantics.pauseAfterScroll();
            waitUntil(d -> isRecoverButtonPresentInTree()
                            || isStatusValueVisibleInRows(DELETED_TEXT)
                            || hasSemantics(SEMANTICS_DELETED_USER_STATUS)
                            || isNoDataAvailableOnPage(d),
                    RECOVER_WAIT);
        } catch (RuntimeException ignored) {
            // Keep scrolling/click path; caller reports a clear Recover-not-found error.
        }
    }

    private boolean isRecoverButtonPresentInTree() {
        return findRecoverSemanticsLabel() != null || hasVisibleRecoverText(false);
    }

    private boolean isRecoverButtonVisible() {
        String label = findRecoverSemanticsLabel();
        if (label != null) {
            return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                    "const node = document.querySelector('flt-semantics[aria-label=\"' + arguments[0] + '\"]');" +
                            "if (!node) return false;" +
                            "const rect = node.getBoundingClientRect();" +
                            "return rect.width > 0 && rect.height > 0" +
                            "  && rect.right > 0 && rect.left < window.innerWidth;",
                    label
            ));
        }
        return hasVisibleRecoverText(true);
    }

    private boolean hasVisibleRecoverText(boolean requireInViewport) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const requireInViewport = !!arguments[0];" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(n => {" +
                        "  if (menu && menu.contains(n)) return false;" +
                        "  const text = (n.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  const label = (n.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const rect = n.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "  const isRecover = (text === 'recover' || text.startsWith('recover ')" +
                        "    || (label.includes('recover') && !label.includes('recovered')));" +
                        "  if (!isRecover) return false;" +
                        "  if (!requireInViewport) return true;" +
                        "  return rect.right > 0 && rect.left < window.innerWidth;" +
                        "});",
                requireInViewport
        ));
    }

    private String findRecoverSemanticsLabel() {
        Object label = ((JavascriptExecutor) driver).executeScript(
                "const exact = arguments[0];" +
                        "const prefix = (arguments[1] || '').toLowerCase();" +
                        "const exactNode = document.querySelector('flt-semantics[aria-label=\"' + exact + '\"]');" +
                        "if (exactNode) return exact;" +
                        "const match = Array.from(document.querySelectorAll('flt-semantics')).find(n => {" +
                        "  const aria = (n.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (!aria) return false;" +
                        "  if (aria.includes('recovered') || aria.includes('status_recovered')) return false;" +
                        "  return aria === prefix" +
                        "    || aria.startsWith(prefix + '_')" +
                        "    || aria.includes('recover_account')" +
                        "    || aria.includes('recover_button')" +
                        "    || (aria.includes('recover') && aria.includes('button'));" +
                        "});" +
                        "return match ? (match.getAttribute('aria-label') || null) : null;",
                SEMANTICS_RECOVER_BUTTON,
                SEMANTICS_RECOVER_BUTTON_PREFIX
        );
        return label == null ? null : String.valueOf(label);
    }

    private Map<String, Double> centerOfRecoverButton() {
        String label = findRecoverSemanticsLabel();
        if (label != null) {
            return centerOfSemantics(label);
        }
        return centerOfVisibleOption(RECOVER_TEXT);
    }

    private boolean clickRecoverByLooseText() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                DISPATCH_POINTER_CLICK_SCRIPT +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const nodes = Array.from(document.querySelectorAll('flt-semantics')).filter(n => {" +
                        "  if (menu && menu.contains(n)) return false;" +
                        "  const text = (n.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  const label = (n.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const rect = n.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "  return text === 'recover' || text.startsWith('recover ')" +
                        "    || (label.includes('recover') && !label.includes('recovered') && label.includes('button'));" +
                        "});" +
                        "for (const node of nodes) {" +
                        "  const tap = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "  if (dispatchPointerClick(tap)) return true;" +
                        "}" +
                        "return false;"
        ));
    }

    /**
     * Flutter may omit Recover from the semantics tree while clipped. Click the
     * actions area on the first data row using CDP coordinates as a fallback.
     */
    private boolean clickRecoverInActionsColumn() {
        Object coords = ((JavascriptExecutor) driver).executeScript(
                "const typeLabel = arguments[0];" +
                        "const statusLabel = arguments[1];" +
                        "const statusLegacy = arguments[2];" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const row = Array.from(document.querySelectorAll('flt-semantics')).find(n => {" +
                        "  if (menu && menu.contains(n)) return false;" +
                        "  const aria = (n.getAttribute('aria-label') || '');" +
                        "  const rect = n.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "  return aria === statusLabel || aria.startsWith('deleted_user_status_')" +
                        "    || aria === statusLegacy || aria === typeLabel" +
                        "    || aria.startsWith('deleted_user_') || /^VI-\\d+/.test(aria);" +
                        "});" +
                        "if (!row) return null;" +
                        "const rect = row.getBoundingClientRect();" +
                        "const y = Math.round(rect.top + rect.height / 2);" +
                        // Prefer clicks to the right of the status cell (actions column).
                        "return {" +
                        "  y: y," +
                        "  xs: [" +
                        "    Math.round(rect.right + 40)," +
                        "    Math.round(rect.right + 80)," +
                        "    Math.round(rect.right + 120)," +
                        "    Math.round(window.innerWidth - 48)," +
                        "    Math.round(window.innerWidth - 88)," +
                        "    Math.round(window.innerWidth - 128)" +
                        "  ]" +
                        "};",
                SEMANTICS_DELETED_USER_TYPE,
                SEMANTICS_DELETED_USER_STATUS,
                SEMANTICS_DELETED_USER_STATUS_LEGACY
        );
        if (!(coords instanceof Map<?, ?> point)) {
            return false;
        }
        Object yObj = point.get("y");
        Object xsObj = point.get("xs");
        if (!(yObj instanceof Number) || !(xsObj instanceof List<?> xs)) {
            return false;
        }
        double y = ((Number) yObj).doubleValue();
        boolean hadRecover = isRecoverButtonPresentInTree();
        for (Object xObj : xs) {
            if (!(xObj instanceof Number)) {
                continue;
            }
            if (cdpClick(((Number) xObj).doubleValue(), y)) {
                semantics.pauseAfterScroll();
                if (isRecoverConfirmVisible()) {
                    return true;
                }
                if (hadRecover && !isRecoverButtonPresentInTree()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isRecoverConfirmVisible() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const texts = ['Confirm', 'Recover', 'Yes', 'OK', 'Restore'];" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(n => {" +
                        "  const text = (n.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const role = (n.getAttribute('role') || '').toLowerCase();" +
                        "  const rect = n.getBoundingClientRect();" +
                        "  return texts.includes(text) && (role === 'button' || n.hasAttribute('flt-tappable'))" +
                        "    && rect.width > 0 && rect.height > 0;" +
                        "});"
        ));
    }

    private void logRecoverRelatedSemantics() {
        Object labels = ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics'))" +
                        ".map(n => (n.getAttribute('aria-label') || '').trim())" +
                        ".filter(l => l && (l.toLowerCase().includes('recover')" +
                        "  || l.toLowerCase().includes('button')" +
                        "  || l.toLowerCase().includes('deleted_user')" +
                        "  || l.toLowerCase().startsWith('vi-')))" +
                        ".filter((v,i,a)=>a.indexOf(v)===i).slice(0,40);"
        );
        System.out.println("Deleted App Users recover-related semantics: " + labels);
    }

    public boolean isUserTypeDisplayed() {
        waitBeforeFilterValidation();
        reenableSemantics();
        return waitUntil(this::isUserTypeSelectionConfirmed, DEFAULT_WAIT);
    }

    public boolean isStatusDisplayed() {
        waitBeforeFilterValidation();
        reenableSemantics();
        return waitUntil(this::isStatusSelectionConfirmed, DEFAULT_WAIT);
    }

    private void openUserTypeDropdown() {
        openDropdownAt(
                SEMANTICS_USER_TYPE_ALL,
                USER_TYPE_DROPDOWN_X,
                USER_TYPE_DROPDOWN_Y,
                true,
                "User Type All dropdown did not open (expected semantics "
                        + SEMANTICS_USER_TYPE_ALL + " or visible All filter)"
        );
    }

    private void openStatusDropdown() {
        dismissOpenMenus();
        reenableSemantics();
        Double x = STATUS_DROPDOWN_X.get();
        Double y = STATUS_DROPDOWN_Y.get();
        if (x != null && y != null && cdpClick(x, y)) {
            semantics.pauseAfterScroll();
            return;
        }
        openDropdownAt(
                SEMANTICS_STATUS_ALL,
                STATUS_DROPDOWN_X,
                STATUS_DROPDOWN_Y,
                false,
                "Status All dropdown did not open (expected semantics "
                        + SEMANTICS_STATUS_ALL + " or visible All filter)"
        );
    }

    private void dismissOpenMenus() {
        try {
            org.openqa.selenium.interactions.Actions actions =
                    new org.openqa.selenium.interactions.Actions(driver);
            actions.sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
            semantics.pauseAfterScroll();
            actions.sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
            semantics.pauseAfterScroll();
        } catch (Exception ignored) {
            // Menu may already be closed.
        }
    }

    private void openDropdownAt(
            String semanticsLabel,
            ThreadLocal<Double> dropdownX,
            ThreadLocal<Double> dropdownY,
            boolean leftmostAll,
            String errorMessage) {
        semantics.forceEnableFlutterSemantics();
        Double x = dropdownX.get();
        Double y = dropdownY.get();
        if (x != null && y != null && cdpClick(x, y)) {
            semantics.pauseAfterScroll();
            return;
        }
        if (clickSemanticsOrCdp(semanticsLabel, null)) {
            Map<String, Double> point = centerOfSemantics(semanticsLabel);
            if (point == null) {
                point = allButtonCenter(leftmostAll);
            }
            if (point != null) {
                dropdownX.set(point.get("x"));
                dropdownY.set(point.get("y"));
            }
            semantics.pauseAfterScroll();
            return;
        }
        Map<String, Double> point = allButtonCenter(leftmostAll);
        if (point == null || !cdpClick(point.get("x"), point.get("y"))) {
            throw new NoSuchElementException(errorMessage);
        }
        dropdownX.set(point.get("x"));
        dropdownY.set(point.get("y"));
        semantics.pauseAfterScroll();
        reenableSemantics();
    }

    private void selectUserTypeOption(String visibleText, String semanticsLabel) {
        ensureDeletedAppUsersPageReady();
        if (USER_TYPE_DROPDOWN_X.get() == null) {
            openUserTypeDropdown();
        }
        // Dropdown should already be open from clickAll; if closed, reopen.
        if (!isPopupLikelyOpen() && !isFilterTriggerShowing(ALL_TEXT)
                && !isFilterTriggerShowing(INDIVIDUAL_TEXT)
                && !isFilterTriggerShowing(COMPANY_TEXT)) {
            openUserTypeDropdown();
        }
        int[] offsets = COMPANY_TEXT.equals(visibleText)
                ? new int[]{96, 120, 144, 168, 72, 48}
                : new int[]{48, 72, 96, 120, 36};
        selectFilterOption(
                visibleText,
                semanticsLabel,
                USER_TYPE_DROPDOWN_X,
                USER_TYPE_DROPDOWN_Y,
                offsets,
                this::openUserTypeDropdown,
                LAST_SELECTED_USER_TYPE
        );
    }

    private void selectStatusOption(String visibleText, String semanticsLabel) {
        ensureDeletedAppUsersPageReady();
        int[] offsets;
        if (RECOVERED_TEXT.equals(visibleText)) {
            offsets = new int[]{144, 168, 192, 120, 96, 72, 216};
        } else if (DELETED_TEXT.equals(visibleText)) {
            offsets = new int[]{96, 120, 144, 72, 168, 48, 192};
        } else {
            offsets = new int[]{48, 72, 96, 120, 36};
        }
        Double x = STATUS_DROPDOWN_X.get();
        Double baseY = STATUS_DROPDOWN_Y.get();
        for (int offset : offsets) {
            openStatusDropdown();
            x = STATUS_DROPDOWN_X.get();
            baseY = STATUS_DROPDOWN_Y.get();
            if (x == null || baseY == null) {
                continue;
            }
            cdpClick(x, baseY + offset);
            semantics.pauseAfterScroll();
            reenableSemantics();
            if (isFilterTriggerShowing(visibleText)
                    || isStatusValueVisibleInRows(visibleText)) {
                LAST_SELECTED_STATUS.set(visibleText);
                return;
            }
        }
        reenableSemantics();
        openStatusDropdown();
        if (clickSemanticsOrCdp(semanticsLabel, visibleText)
                || clickOptionByVisibleText(visibleText)) {
            LAST_SELECTED_STATUS.set(visibleText);
            reenableSemantics();
            return;
        }
        throw new NoSuchElementException(
                "Filter option not selectable: " + visibleText
                        + " (expected semantics " + semanticsLabel + ")");
    }

    private boolean isStatusValueVisibleInRows(String visibleText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const wanted = (arguments[0] || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const matches = Array.from(document.querySelectorAll('flt-semantics')).filter(n => {" +
                        "  if (menu && menu.contains(n)) return false;" +
                        "  const text = (n.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  const rect = n.getBoundingClientRect();" +
                        "  return text === wanted && rect.width > 0 && rect.height > 0 && rect.top > 200;" +
                        "});" +
                        // Require several row matches so a single stray label is not enough.
                        "return matches.length >= 2;",
                visibleText
        ));
    }

    private void selectFilterOption(
            String visibleText,
            String semanticsLabel,
            ThreadLocal<Double> dropdownX,
            ThreadLocal<Double> dropdownY,
            int[] offsets,
            Runnable reopenDropdown,
            ThreadLocal<String> lastSelected) {
        Double x = dropdownX.get();
        Double baseY = dropdownY.get();
        if (x != null && baseY != null) {
            for (int offset : offsets) {
                if (!isFilterTriggerShowing(ALL_TEXT) && !isPopupLikelyOpen()) {
                    reopenDropdown.run();
                    x = dropdownX.get();
                    baseY = dropdownY.get();
                    if (x == null || baseY == null) {
                        break;
                    }
                }
                cdpClick(x, baseY + offset);
                semantics.pauseAfterScroll();
                reenableSemantics();
                if (isFilterTriggerShowing(visibleText)) {
                    lastSelected.set(visibleText);
                    return;
                }
            }
        }
        reenableSemantics();
        if (clickSemanticsOrCdp(semanticsLabel, visibleText)) {
            lastSelected.set(visibleText);
            reenableSemantics();
            return;
        }
        if (!clickOptionByVisibleText(visibleText)) {
            throw new NoSuchElementException(
                    "Filter option not selectable: " + visibleText
                            + " (expected semantics " + semanticsLabel + ")");
        }
        lastSelected.set(visibleText);
        reenableSemantics();
    }

    private boolean isPopupLikelyOpen() {
        // After opening All, Flutter often collapses the semantics tree until a selection.
        long count = (Long) ((JavascriptExecutor) driver).executeScript(
                "const host = document.querySelector('flt-semantics-host');" +
                        "return host ? host.querySelectorAll('flt-semantics').length : 0;"
        );
        return count > 0 && count < 40;
    }

    private boolean clickSemanticsOrCdp(String semanticsLabel, String visibleText) {
        if (semanticsLabel != null && hasSemantics(semanticsLabel)) {
            Map<String, Double> point = centerOfSemantics(semanticsLabel);
            if (point != null && cdpClick(point.get("x"), point.get("y"))) {
                return true;
            }
            if (semantics.clickSemanticsLabelViaScript(semanticsLabel)) {
                return true;
            }
        }
        if (visibleText != null && !visibleText.isBlank()) {
            Map<String, Double> point = centerOfVisibleOption(visibleText);
            if (point != null && cdpClick(point.get("x"), point.get("y"))) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Double> leftmostAllButtonCenter() {
        return allButtonCenter(true);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Double> rightmostAllButtonCenter() {
        return allButtonCenter(false);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Double> allButtonCenter(boolean leftmost) {
        Object raw = ((JavascriptExecutor) driver).executeScript(
                "const leftmost = !!arguments[1];" +
                        "const nodes = Array.from(document.querySelectorAll('flt-semantics')).filter(n => {" +
                        "  const text = (n.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const rect = n.getBoundingClientRect();" +
                        "  return text === arguments[0] && n.getAttribute('role') === 'button'" +
                        "    && rect.width > 20 && rect.height > 20;" +
                        "}).sort((a, b) => a.getBoundingClientRect().left - b.getBoundingClientRect().left);" +
                        "if (!nodes.length) return null;" +
                        "const node = leftmost ? nodes[0] : nodes[nodes.length - 1];" +
                        "const rect = node.getBoundingClientRect();" +
                        "return { x: rect.left + rect.width / 2, y: rect.top + rect.height / 2 };",
                ALL_TEXT,
                leftmost
        );
        if (!(raw instanceof Map<?, ?> map) || map.get("x") == null || map.get("y") == null) {
            return null;
        }
        Map<String, Double> point = new HashMap<>();
        point.put("x", ((Number) map.get("x")).doubleValue());
        point.put("y", ((Number) map.get("y")).doubleValue());
        return point;
    }

    private void captureUserTypeDropdownPoint() {
        Map<String, Double> point = centerOfSemantics(SEMANTICS_USER_TYPE_ALL);
        if (point == null) {
            point = leftmostAllButtonCenter();
        }
        if (point != null) {
            USER_TYPE_DROPDOWN_X.set(point.get("x"));
            USER_TYPE_DROPDOWN_Y.set(point.get("y"));
        }
    }

    private void captureStatusDropdownPoint() {
        Map<String, Double> point = centerOfSemantics(SEMANTICS_STATUS_ALL);
        if (point == null) {
            point = rightmostAllButtonCenter();
        }
        if (point != null) {
            STATUS_DROPDOWN_X.set(point.get("x"));
            STATUS_DROPDOWN_Y.set(point.get("y"));
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Double> centerOfSemantics(String semanticsLabel) {
        Object raw = ((JavascriptExecutor) driver).executeScript(
                "const node = document.querySelector('flt-semantics[aria-label=\"' + arguments[0] + '\"]');" +
                        "if (!node) return null;" +
                        "const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "const rect = target.getBoundingClientRect();" +
                        "if (rect.width <= 0 || rect.height <= 0) return null;" +
                        "return { x: rect.left + rect.width / 2, y: rect.top + rect.height / 2 };",
                semanticsLabel
        );
        if (!(raw instanceof Map<?, ?> map) || map.get("x") == null || map.get("y") == null) {
            return null;
        }
        Map<String, Double> point = new HashMap<>();
        point.put("x", ((Number) map.get("x")).doubleValue());
        point.put("y", ((Number) map.get("y")).doubleValue());
        return point;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Double> centerOfVisibleOption(String visibleText) {
        Object raw = ((JavascriptExecutor) driver).executeScript(
                "const wanted = (arguments[0] || '').replace(/\\s+/g, ' ').trim();" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const nodes = Array.from(document.querySelectorAll('flt-semantics')).filter(n => {" +
                        "  if (menu && menu.contains(n)) return false;" +
                        "  const text = (n.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const rect = n.getBoundingClientRect();" +
                        "  return text === wanted && rect.width > 0 && rect.height > 0;" +
                        "}).sort((a, b) => a.getBoundingClientRect().top - b.getBoundingClientRect().top);" +
                        "if (!nodes.length) return null;" +
                        // Prefer the option nearest the filter bar (not deep table cells).
                        "const target = nodes[0].closest('flt-semantics[flt-tappable]') || nodes[0];" +
                        "const rect = target.getBoundingClientRect();" +
                        "return { x: rect.left + rect.width / 2, y: rect.top + rect.height / 2 };",
                visibleText
        );
        if (!(raw instanceof Map<?, ?> map) || map.get("x") == null || map.get("y") == null) {
            return null;
        }
        Map<String, Double> point = new HashMap<>();
        point.put("x", ((Number) map.get("x")).doubleValue());
        point.put("y", ((Number) map.get("y")).doubleValue());
        return point;
    }

    private boolean cdpClick(double x, double y) {
        if (!(driver instanceof ChromeDriver chromeDriver)) {
            return false;
        }
        try {
            Map<String, Object> move = new HashMap<>();
            move.put("type", "mouseMoved");
            move.put("x", x);
            move.put("y", y);
            chromeDriver.executeCdpCommand("Input.dispatchMouseEvent", move);

            Map<String, Object> press = new HashMap<>();
            press.put("type", "mousePressed");
            press.put("x", x);
            press.put("y", y);
            press.put("button", "left");
            press.put("buttons", 1);
            press.put("clickCount", 1);
            chromeDriver.executeCdpCommand("Input.dispatchMouseEvent", press);

            Map<String, Object> release = new HashMap<>();
            release.put("type", "mouseReleased");
            release.put("x", x);
            release.put("y", y);
            release.put("button", "left");
            release.put("buttons", 0);
            release.put("clickCount", 1);
            chromeDriver.executeCdpCommand("Input.dispatchMouseEvent", release);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean clickOptionByVisibleText(String visibleText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                DISPATCH_POINTER_CLICK_SCRIPT +
                        "const wanted = (arguments[0] || '').replace(/\\s+/g, ' ').trim();" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const nodes = Array.from(document.querySelectorAll('flt-semantics')).filter(n => {" +
                        "  if (menu && menu.contains(n)) return false;" +
                        "  const text = (n.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const rect = n.getBoundingClientRect();" +
                        "  return text === wanted && rect.width > 0 && rect.height > 0;" +
                        "});" +
                        "nodes.sort((a, b) => a.getBoundingClientRect().top - b.getBoundingClientRect().top);" +
                        "for (const node of nodes) {" +
                        "  const tap = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "  if (dispatchPointerClick(tap)) return true;" +
                        "}" +
                        "return false;",
                visibleText
        ));
    }

    private boolean isFilterTriggerShowing(String visibleText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const wanted = (arguments[0] || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(n => {" +
                        "  const text = (n.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  const role = n.getAttribute('role') || '';" +
                        "  const rect = n.getBoundingClientRect();" +
                        "  return text === wanted && role === 'button' && rect.top < 220 && rect.width > 20;" +
                        "});",
                visibleText
        ));
    }

    private boolean isUserTypeSelectionConfirmed(WebDriver webDriver) {
        return isSelectionConfirmed(
                webDriver,
                LAST_SELECTED_USER_TYPE.get(),
                SEMANTICS_DELETED_USER_TYPE
        );
    }

    private boolean isStatusSelectionConfirmed(WebDriver webDriver) {
        return isSelectionConfirmed(
                webDriver,
                LAST_SELECTED_STATUS.get(),
                SEMANTICS_DELETED_USER_STATUS
        ) || isSelectionConfirmed(
                webDriver,
                LAST_SELECTED_STATUS.get(),
                SEMANTICS_DELETED_USER_STATUS_LEGACY
        );
    }

    private boolean isSelectionConfirmed(WebDriver webDriver, String lastSelected, String rowSemantics) {
        semantics.forceEnableFlutterSemantics();
        if (lastSelected == null || lastSelected.isBlank()) {
            return false;
        }
        if (isFilterTriggerShowing(lastSelected)) {
            return true;
        }
        String expected = semantics.normalizeText(lastSelected);
        if (hasSemantics(rowSemantics)) {
            String cellText = semantics.normalizeText((String) ((JavascriptExecutor) webDriver).executeScript(
                    "const node = document.querySelector('flt-semantics[aria-label=\"' + arguments[0] + '\"]');" +
                            "if (!node) return '';" +
                            "return (node.getAttribute('aria-valuetext') || node.textContent || '').trim();",
                    rowSemantics
            ));
            if (cellText.contains(expected)) {
                return true;
            }
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                "const wanted = (arguments[0] || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const matches = Array.from(document.querySelectorAll('flt-semantics')).filter(n => {" +
                        "  if (menu && menu.contains(n)) return false;" +
                        "  const text = (n.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  const rect = n.getBoundingClientRect();" +
                        "  return text === wanted && rect.width > 0 && rect.height > 0 && rect.top > 180;" +
                        "});" +
                        "return matches.length > 0;",
                lastSelected
        ));
    }

    private void ensureDeletedAppUsersPageReady() {
        semantics.enableFlutterSemantics();
        if (isDeletedAppUsersPageLoaded(driver)) {
            waitForSearchBarReady();
            return;
        }
        clickDeletedAppUsersMenu();
        waitForSearchBarReady();
    }

    private void waitForSearchBarReady() {
        semantics.forceEnableFlutterSemantics();
        waitUntil(d -> {
            semantics.enableFlutterSemanticsOn(d);
            return !d.findElements(searchBarSemantics).isEmpty()
                    || !d.findElements(searchBarInput).isEmpty()
                    || findSearchInputViaAnchor(d) != null
                    || isDeletedAppUsersPageLoaded(d);
        }, DEFAULT_WAIT);
    }

    private WebElement waitForSearchInput() {
        waitForSearchBarReady();
        List<WebElement> inputs = driver.findElements(searchBarInput);
        if (!inputs.isEmpty()) {
            return wait.until(ExpectedConditions.elementToBeClickable(inputs.get(0)));
        }
        if (!driver.findElements(searchBarSemantics).isEmpty()) {
            WebElement searchSemantics = wait.until(ExpectedConditions.presenceOfElementLocated(searchBarSemantics));
            List<WebElement> nestedInputs = searchSemantics.findElements(By.xpath(".//input"));
            if (!nestedInputs.isEmpty()) {
                return nestedInputs.get(0);
            }
            return searchSemantics;
        }
        WebElement fromAnchor = findSearchInputViaAnchor(driver);
        if (fromAnchor != null) {
            return fromAnchor;
        }
        throw new NoSuchElementException(
                "Deleted App Users search field not found (expected semantics " + SEMANTICS_SEARCH + ")");
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
                        "  const genericInput = document.querySelector('input[aria-label*=\"Search\"]')" +
                        "    || document.querySelector('input[placeholder*=\"Search\"]');" +
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

    private boolean isUserIdDisplayedOnPage(WebDriver webDriver, String userId) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String expectedLabel = USER_ID_LABEL_PREFIX + userId;
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                "const expected = arguments[0];" +
                        "const id = arguments[1];" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  if (menu && menu.contains(node)) return false;" +
                        "  const label = (node.getAttribute('aria-label') || '').trim();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return label === expected || text === expected" +
                        "    || label.includes(expected) || text.includes(expected)" +
                        "    || label.includes(id) || text.includes(id);" +
                        "});",
                expectedLabel,
                userId
        ));
    }

    private boolean isNoDataAvailableOnPage(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                "const wanted = arguments[0];" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  if (menu && menu.contains(node)) return false;" +
                        "  const label = (node.getAttribute('aria-label') || '').trim();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return label === wanted || text === wanted || text.includes(wanted);" +
                        "});",
                NO_DATA_AVAILABLE_TEXT
        ));
    }

    private boolean clickDeletedAppUsersNavItem() {
        if (semantics.clickSemanticsLabelViaScript(SEMANTICS_MENU)) {
            return true;
        }
        if (semantics.clickVisibleText(DELETED_APP_USERS_TEXT)) {
            return true;
        }
        if (!driver.findElements(deletedAppUsersMenu).isEmpty()) {
            try {
                semantics.clickSemanticsElement(deletedAppUsersMenu, wait);
                return true;
            } catch (Exception ignored) {
                // Fall through.
            }
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                DISPATCH_POINTER_CLICK_SCRIPT +
                        "const wanted = arguments[0];" +
                        "const nodes = Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const target = nodes.find(node =>" +
                        "  (node.textContent || '').replace(/\\s+/g, ' ').trim() === wanted);" +
                        "if (!target) return false;" +
                        "return dispatchPointerClick(target.closest('flt-semantics[flt-tappable]') || target);",
                DELETED_APP_USERS_TEXT
        ));
    }

    private void scrollNavigationToDeletedAppUsers() {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                "const semanticsLabel = (arguments[0] || '').toLowerCase();" +
                        "const wanted = arguments[1];" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const nodes = menu ? Array.from(menu.querySelectorAll('flt-semantics'))" +
                        "  : Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const target = nodes.find(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return label.includes(semanticsLabel) || text === wanted || text.includes(wanted);" +
                        "});" +
                        "if (target) target.scrollIntoView({block: 'center', inline: 'nearest'});",
                SEMANTICS_MENU,
                DELETED_APP_USERS_TEXT
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

    private boolean isDeletedAppUsersPageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        try {
            String url = webDriver.getCurrentUrl();
            if (url != null && url.toLowerCase().contains("deleted-user")) {
                return true;
            }
        } catch (Exception ignored) {
            // Continue with semantics checks.
        }
        if (hasSemantics(SEMANTICS_SEARCH)
                || hasSemantics(SEMANTICS_USER_TYPE_ALL)
                || hasSemantics(SEMANTICS_DELETED_USER_TYPE)
                || hasSemantics(SEMANTICS_STATUS_ALL)
                || hasSemantics(SEMANTICS_DELETED_USER_STATUS)
                || hasSemantics(SEMANTICS_DELETED_USER_STATUS_LEGACY)) {
            return true;
        }
        // Use markers unique to Deleted App Users — do NOT use "Status" alone
        // (Individual Users also has a Status column/filter).
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  if (menu && menu.contains(node)) return false;" +
                        "  const label = (node.getAttribute('aria-label') || '').trim();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return label === 'User Type' || text === 'User Type'" +
                        "    || label === 'Request ID' || text === 'Request ID'" +
                        "    || label === 'Scheduled Hard Delete' || text === 'Scheduled Hard Delete';" +
                        "});"
        ));
    }

    private String debugAllButtons() {
        Object raw = ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics')).filter(n => {" +
                        "  const text = (n.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return text === 'All' || (n.getAttribute('aria-label') || '').includes('all_option');" +
                        "}).map(n => {" +
                        "  const r = n.getBoundingClientRect();" +
                        "  return (n.getAttribute('aria-label') || '') + '|' + (n.textContent||'').trim()" +
                        "    + '|role=' + (n.getAttribute('role')||'') + '|tap=' + n.hasAttribute('flt-tappable')" +
                        "    + '|xy=' + Math.round(r.left) + ',' + Math.round(r.top)" +
                        "    + '|wh=' + Math.round(r.width) + 'x' + Math.round(r.height);" +
                        "}).slice(0, 20);"
        );
        return String.valueOf(raw);
    }

    private boolean isHomePageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String text = semantics.getSemanticsText(webDriver);
        return text != null
                && (text.contains("Individual Users")
                || text.contains("Company Users")
                || text.contains(DELETED_APP_USERS_TEXT)
                || text.contains(NAVIGATION_MENU));
    }

    private boolean hasSemantics(String label) {
        return !driver.findElements(By.cssSelector("flt-semantics[aria-label='" + label + "']")).isEmpty();
    }

    private void reenableSemantics() {
        semantics.forceEnableFlutterSemantics();
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
}
