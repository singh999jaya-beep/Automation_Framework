package com.example.pages.support;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Opens and selects Flutter status filter dropdowns using semantics labels and visible-text fallbacks.
 */
public class StatusFilterSupport {

    private static final Duration STATUS_MENU_WAIT = Duration.ofSeconds(5);

    private final WebDriver driver;
    private final FlutterSemanticsSupport semantics;
    private final StatusFilterConfig config;

    public StatusFilterSupport(WebDriver driver, FlutterSemanticsSupport semantics, StatusFilterConfig config) {
        this.driver = driver;
        this.semantics = semantics;
        this.config = config;
    }

    public static StatusFilterSupport forIndividual(WebDriver driver, FlutterSemanticsSupport semantics) {
        return new StatusFilterSupport(driver, semantics, StatusFilterConfig.individual());
    }

    public static StatusFilterSupport forCompany(WebDriver driver, FlutterSemanticsSupport semantics) {
        return new StatusFilterSupport(driver, semantics, StatusFilterConfig.company());
    }

    public static StatusFilterSupport forTransactions(WebDriver driver, FlutterSemanticsSupport semantics) {
        return new StatusFilterSupport(driver, semantics, StatusFilterConfig.transactions());
    }

    public static StatusFilterSupport forModeration(WebDriver driver, FlutterSemanticsSupport semantics) {
        return new StatusFilterSupport(driver, semantics, StatusFilterConfig.moderation());
    }

    public static StatusFilterSupport forManageAdminUsers(WebDriver driver, FlutterSemanticsSupport semantics) {
        return new StatusFilterSupport(driver, semantics, StatusFilterConfig.manageAdminUsers());
    }

    public static StatusFilterSupport forManageAdminUsersAccess(WebDriver driver, FlutterSemanticsSupport semantics) {
        return new StatusFilterSupport(driver, semantics, StatusFilterConfig.manageAdminUsersAccess());
    }

    public static StatusFilterSupport forAuditLogsModules(WebDriver driver, FlutterSemanticsSupport semantics) {
        return new StatusFilterSupport(driver, semantics, StatusFilterConfig.auditLogsModules());
    }

    public static StatusFilterSupport forAuditLogsActions(WebDriver driver, FlutterSemanticsSupport semantics) {
        return new StatusFilterSupport(driver, semantics, StatusFilterConfig.auditLogsActions());
    }

    public static StatusFilterSupport forDeletedAppUsers(WebDriver driver, FlutterSemanticsSupport semantics) {
        return new StatusFilterSupport(driver, semantics, StatusFilterConfig.deletedAppUsers());
    }

    public boolean isDropdownOpen() {
        return isMenuOpen();
    }

    public void prepareFilterArea() {
        semantics.enableFlutterSemantics();
        scrollUntilStatusTriggerVisible();
    }

    public void openDropdown() {
        // Do not treat lingering *_option_* semantics as an open menu — Flutter often keeps
        // option nodes in the tree while the popup is closed (Manage Admin / Moderation CI).
        if (isMenuOpen()) {
            return;
        }
        for (int attempt = 0; attempt < 10; attempt++) {
            if (isMenuOpen()) {
                return;
            }
            scrollUntilStatusTriggerVisible();
            boolean optionsPresentBeforeClick = hasConfiguredOptionSemanticsPresent();
            if (tryOpenDropdown()) {
                semantics.pauseAfterScroll();
                if (waitForMenuOpen()) {
                    return;
                }
                // Only trust newly appeared option nodes after a trigger click.
                if (!optionsPresentBeforeClick && waitForConfiguredOptionSemantics()) {
                    return;
                }
            }
            scrollFilterAreaRightOnce();
            semantics.pauseAfterScroll();
        }
        if (isMenuOpen()) {
            return;
        }
        logStatusRelatedSemantics();
        throw new NoSuchElementException(buildOpenFailureMessage());
    }

    private boolean waitForConfiguredOptionSemantics() {
        return semantics.waitUntil(d -> hasConfiguredOptionSemanticsPresent(), STATUS_MENU_WAIT);
    }

    /** True when any configured option is present and visible (menu is effectively open). */
    private boolean hasConfiguredOptionSemanticsPresent() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const labels = [...(arguments[0] || []), ...(arguments[1] || [])].filter(Boolean);" +
                        "return labels.some(label => {" +
                        "  const node = document.querySelector('flt-semantics[aria-label=\"' + label + '\"]');" +
                        "  if (!node) return false;" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  return rect.width > 0 && rect.height > 0" +
                        "    && rect.bottom > 0 && rect.top < window.innerHeight;" +
                        "});",
                config.activeOption().semanticsLabels(),
                config.inactiveOption().semanticsLabels()
        ));
    }

    private boolean waitForMenuOpen() {
        return semantics.waitUntil(d -> isMenuOpen(), STATUS_MENU_WAIT);
    }

    public void selectActiveOption() {
        selectOption(config.activeOption());
    }

    public void selectInactiveOption() {
        selectOption(config.inactiveOption());
    }

    public void selectOption(String visibleText, String[] semanticsLabels, String... alternateVisibleTexts) {
        selectOption(new StatusFilterConfig.StatusOptionConfig(visibleText, semanticsLabels), alternateVisibleTexts);
    }

    public void scrollFilterAreaRightOnce() {
        if (isMenuOpen()) {
            return;
        }
        ((JavascriptExecutor) driver).executeScript(
                buildScrollScript(),
                config.searchFieldLabel(),
                config.alternateSearchFieldLabels()
        );
        for (String searchLabel : allSearchFieldLabels()) {
            List<WebElement> searchAreas = driver.findElements(By.xpath(
                    "//flt-semantics[@aria-label='" + searchLabel + "']"));
            if (!searchAreas.isEmpty()) {
                try {
                    new Actions(driver).moveToElement(searchAreas.get(0)).sendKeys(Keys.ARROW_RIGHT).perform();
                    return;
                } catch (Exception ignored) {
                    // Try next search anchor.
                }
            }
        }
    }

    private void selectOption(StatusFilterConfig.StatusOptionConfig option, String... alternateVisibleTexts) {
        if (!isMenuOpen()) {
            prepareFilterArea();
            openDropdown();
        }
        // Long Flutter popup menus (Audit Logs All Modules / All Actions) virtualize/clip options.
        // Keep short menus (Company/Individual status) on a fast path to avoid CI timeouts.
        int maxAttempts = isLongPopupMenu() ? 12 : 3;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // Failed CDP clicks can dismiss the menu without applying a selection.
            if (!isMenuOpen()) {
                openDropdown();
                if (!isMenuOpen()) {
                    prepareFilterArea();
                    openDropdown();
                }
            }
            for (String semanticsLabel : option.semanticsLabels()) {
                scrollOptionIntoView(semanticsLabel, option.visibleText());
                if (trySelectVisibleOption(semanticsLabel, option.visibleText())) {
                    return;
                }
            }
            if (trySelectVisibleOption(null, option.visibleText())) {
                return;
            }
            for (String alternateText : alternateVisibleTexts) {
                if (alternateText != null && !alternateText.isBlank()
                        && trySelectVisibleOption(null, alternateText)) {
                    return;
                }
            }
            for (String alternateText : alternateOptionTexts(option.visibleText())) {
                if (trySelectVisibleOption(null, alternateText)) {
                    return;
                }
            }
            // Keyboard is often more reliable than CDP for virtualized Flutter menus.
            if (isLongPopupMenu() && attempt >= 2 && trySelectOptionByKeyboard(option.visibleText())) {
                return;
            }
            if (isLongPopupMenu()) {
                if (attempt % 2 == 0) {
                    scrollPopupMenuDown();
                } else {
                    scrollPopupMenuUp();
                }
                semantics.pauseAfterScroll();
            }
        }
        if (trySelectOptionByKeyboard(option.visibleText())) {
            return;
        }
        throw new NoSuchElementException(
                "Status option is not clickable: " + option.visibleText());
    }

    private boolean isLongPopupMenu() {
        for (String trigger : config.dropdownTriggerLabels()) {
            if (trigger != null && (trigger.contains("all_modules")
                    || trigger.contains("all_actions")
                    // Manage Admin / Moderation: table row status text matches filter option
                    // labels (Active/Blocked/Pending), so only trust the Flutter Popup menu root.
                    || trigger.contains("all_status")
                    || trigger.contains("all_access"))) {
                return true;
            }
        }
        for (String text : config.dropdownTriggerVisibleTexts()) {
            if (text != null) {
                String normalized = text.toLowerCase();
                if (normalized.contains("all modules")
                        || normalized.contains("all actions")
                        || normalized.contains("all status")
                        || normalized.contains("all access")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean trySelectVisibleOption(String semanticsLabel, String visibleText) {
        if (!clickOption(semanticsLabel, visibleText)) {
            return false;
        }
        if (semantics.waitUntil(d -> !isMenuOpen() || isSelectionApplied(visibleText), STATUS_MENU_WAIT)) {
            semantics.pauseAfterScroll();
            // Long menus: menu can close without applying a filter (missed click). Require the
            // trigger/selection text — otherwise Pending/Blocked looked "selected" and validation failed.
            if (isLongPopupMenu()) {
                return isSelectionApplied(visibleText);
            }
            return !isMenuOpen() || isSelectionApplied(visibleText);
        }
        // Short status menus: trust a successful clickOption even when menu-open detection
        // lags (hasPopupMenu / option semantics can linger briefly). Required for Company Users.
        if (!isLongPopupMenu()) {
            semantics.pauseAfterScroll();
            return true;
        }
        return isSelectionApplied(visibleText);
    }

    private boolean isSelectionApplied(String visibleText) {
        if (visibleText == null || visibleText.isBlank()) {
            return false;
        }
        // While the popup is still open, option labels in the menu can look like a selection.
        if (isLongPopupMenu() && hasPopupMenuVisible()) {
            return false;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() +
                        searchAnchorHelperJs() +
                        "const wanted = normalizeText(arguments[0]);" +
                        "const triggers = arguments[1] || [];" +
                        "const triggerTexts = arguments[2] || [];" +
                        "const primarySearch = arguments[3];" +
                        "const alternateSearch = arguments[4] || [];" +
                        "if (!wanted) return false;" +
                        "if (hasPopupMenu()) return false;" +
                        "const searchRect = searchRectFor(primarySearch, alternateSearch);" +
                        "const nearFilterBar = (node) => {" +
                        "  if (!node || isInNav(node) || isSidebarSubmenu(node)) return false;" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "  if (!searchRect) return true;" +
                        "  return rect.top >= searchRect.top - 140 && rect.top <= searchRect.bottom + 140;" +
                        "};" +
                        "for (const label of triggers) {" +
                        "  const node = document.querySelector('flt-semantics[aria-label=\"' + label + '\"]');" +
                        "  if (node && nearFilterBar(node) && normalizeText(node.textContent).includes(wanted)) return true;" +
                        "}" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  if (!nearFilterBar(node)) return false;" +
                        "  const text = normalizeText(node.textContent);" +
                        "  const label = normalizeText(node.getAttribute('aria-label'));" +
                        "  if (triggerTexts.includes(text)) return false;" +
                        "  if ((label || '').toLowerCase().includes('_submenu')) return false;" +
                        "  return text === wanted || (text.includes(wanted) && text.length < wanted.length + 24);" +
                        "});",
                visibleText,
                config.dropdownTriggerLabels(),
                config.dropdownTriggerVisibleTexts(),
                config.searchFieldLabel(),
                config.alternateSearchFieldLabels()
        ));
    }

    private boolean trySelectOptionByKeyboard(String visibleText) {
        try {
            // Reset menu so ARROW_DOWN indices are predictable after failed scroll/click attempts.
            if (isLongPopupMenu()) {
                try {
                    new Actions(driver).sendKeys(Keys.ESCAPE).perform();
                    semantics.pauseAfterScroll();
                } catch (Exception ignored) {
                    // Continue with reopen.
                }
                if (!isMenuOpen()) {
                    prepareFilterArea();
                    openDropdown();
                }
            } else if (!isMenuOpen()) {
                openDropdown();
            }
            if (!isMenuOpen()) {
                return false;
            }
            // Walk the menu: Arrow Down until the target option is present, then Enter.
            // Fixed indices are brittle when Flutter focuses the first item on open.
            for (int attempt = 0; attempt < 30; attempt++) {
                if (isOptionPresent(null, visibleText)) {
                    if (trySelectVisibleOption(null, visibleText)) {
                        return true;
                    }
                    new Actions(driver).sendKeys(Keys.ENTER).perform();
                    semantics.pauseAfterScroll();
                    if (semantics.waitUntil(d -> !isMenuOpen() || isSelectionApplied(visibleText), STATUS_MENU_WAIT)) {
                        if (isLongPopupMenu()) {
                            if (isSelectionApplied(visibleText)) {
                                return true;
                            }
                        } else if (!isMenuOpen() || isSelectionApplied(visibleText)) {
                            return true;
                        }
                    }
                }
                new Actions(driver).sendKeys(Keys.ARROW_DOWN).perform();
                semantics.pauseAfterScroll();
                if (attempt > 0 && attempt % 8 == 0) {
                    scrollPopupMenuDown();
                    semantics.pauseAfterScroll();
                }
            }
            int targetIndex = keyboardOptionIndex(visibleText);
            if (targetIndex > 0) {
                try {
                    new Actions(driver).sendKeys(Keys.ESCAPE).perform();
                    semantics.pauseAfterScroll();
                } catch (Exception ignored) {
                    // Continue.
                }
                if (!isMenuOpen()) {
                    openDropdown();
                }
                // Try both 0-based and 1-based focus assumptions.
                for (int offset = 0; offset <= 1; offset++) {
                    if (!isMenuOpen()) {
                        openDropdown();
                    }
                    int steps = Math.max(0, targetIndex - offset);
                    for (int step = 0; step < steps; step++) {
                        new Actions(driver).sendKeys(Keys.ARROW_DOWN).perform();
                        semantics.pauseAfterScroll();
                    }
                    new Actions(driver).sendKeys(Keys.ENTER).perform();
                    semantics.pauseAfterScroll();
                    if (semantics.waitUntil(d -> !isMenuOpen() || isSelectionApplied(visibleText), STATUS_MENU_WAIT)) {
                        if (isLongPopupMenu()) {
                            if (isSelectionApplied(visibleText)) {
                                return true;
                            }
                        } else if (!isMenuOpen() || isSelectionApplied(visibleText)) {
                            return true;
                        }
                    }
                    try {
                        new Actions(driver).sendKeys(Keys.ESCAPE).perform();
                        semantics.pauseAfterScroll();
                    } catch (Exception ignored) {
                        // Continue next offset.
                    }
                }
            }
        } catch (Exception ignored) {
            // Fall through to caller error.
        }
        return false;
    }

    private int keyboardOptionIndex(String visibleText) {
        if (visibleText == null) {
            return -1;
        }
        for (String trigger : config.dropdownTriggerLabels()) {
            if (trigger != null && trigger.contains("all_submenu")) {
                String[] types = {"Monthly Subscription", "Event Invitation", "Advertisement"};
                for (int i = 0; i < types.length; i++) {
                    if (types[i].equalsIgnoreCase(visibleText)) {
                        return i + 1;
                    }
                }
            }
            if (trigger != null && trigger.contains("all_access")) {
                String[] access = {"Permanent", "Temporary"};
                for (int i = 0; i < access.length; i++) {
                    if (access[i].equalsIgnoreCase(visibleText)) {
                        return i + 1;
                    }
                }
            }
            if (trigger != null && trigger.contains("all_status")) {
                // Both Manage Admin and Moderation use all_status_*; pick order from page config.
                // Admin list first used to map Moderation Pending→index 3 and Blocked→index 2.
                String rowPrefix = config.rowStatusLabelPrefix() == null
                        ? ""
                        : config.rowStatusLabelPrefix().toLowerCase();
                String[] statuses = rowPrefix.contains("moderation")
                        ? new String[]{"Pending", "Report Dismissed", "Blocked"}
                        : new String[]{"Active", "Blocked", "Pending", "Deleted", "Expired"};
                for (int i = 0; i < statuses.length; i++) {
                    if (statuses[i].equalsIgnoreCase(visibleText)) {
                        return i + 1;
                    }
                }
            }
        }
        // All Modules popup order (1-based from first ARROW_DOWN after open).
        String[] modules = {
                "Individual Users", "Company Users", "Input Categories", "Events", "Advertisement",
                "Transactions", "Moderation Center", "Admin Settings", "Manage Roles", "Manage Admin Users",
                "Deeplink History", "Consent Audit Log", "Content Versions", "Audit Logs", "Deleted Users"
        };
        for (int i = 0; i < modules.length; i++) {
            if (modules[i].equalsIgnoreCase(visibleText)) {
                return i + 1;
            }
        }
        String[] actions = {
                "Created", "Updated", "Deleted", "Blocked", "Unblocked", "Approved", "Rejected",
                "Closed", "Invited", "Enabled", "Expired"
        };
        for (int i = 0; i < actions.length; i++) {
            if (actions[i].equalsIgnoreCase(visibleText) || ("Close".equalsIgnoreCase(visibleText) && "Closed".equals(actions[i]))) {
                return i + 1;
            }
        }
        return -1;
    }

    private void scrollOptionIntoView(String semanticsLabel, String visibleText) {
        ((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() +
                        "const semanticsLabel = (arguments[0] || '').toLowerCase();" +
                        "const visibleText = arguments[1];" +
                        "const node = findOptionNode(semanticsLabel, visibleText);" +
                        "if (node) node.scrollIntoView({block: 'center', inline: 'nearest'});",
                semanticsLabel,
                visibleText
        );
        semantics.pauseAfterScroll();
    }

    private void scrollPopupMenuDown() {
        scrollPopupMenu(280);
    }

    private void scrollPopupMenuUp() {
        scrollPopupMenu(-280);
    }

    private void scrollPopupMenu(int deltaY) {
        ((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() +
                        "const deltaY = arguments[0];" +
                        "const root = popupMenuRoot() || document.body;" +
                        "const anchor = root;" +
                        "const rect = anchor.getBoundingClientRect ? anchor.getBoundingClientRect() : null;" +
                        "const x = rect ? rect.left + Math.min(rect.width, 80) / 2 : window.innerWidth / 2;" +
                        "const y = rect ? rect.top + Math.min(rect.height, 120) / 2 : window.innerHeight / 2;" +
                        "const target = document.elementFromPoint(x, y) || anchor;" +
                        "for (let i = 0; i < 5; i++) {" +
                        "  target.dispatchEvent(new WheelEvent('wheel', {" +
                        "    deltaX: 0, deltaY: deltaY, bubbles: true, cancelable: true, clientX: x, clientY: y" +
                        "  }));" +
                        "  if (anchor.scrollBy) anchor.scrollBy(0, deltaY);" +
                        "  if (anchor.scrollTop !== undefined) {" +
                        "    anchor.scrollTop = Math.max(0, Math.min(anchor.scrollHeight || 0, (anchor.scrollTop || 0) + deltaY));" +
                        "  }" +
                        "}",
                deltaY
        );
    }

    private List<String> alternateOptionTexts(String visibleText) {
        // Blocked↔Inactive is only for user status filters. Audit Logs Actions has a real
        // "Blocked" option; mapping it to Inactive causes long retries / false matches.
        if (!isLongPopupMenu()) {
            if ("Inactive".equals(visibleText)) {
                return List.of("Blocked");
            }
            if ("Blocked".equals(visibleText)) {
                return List.of("Inactive");
            }
        }
        if ("Closed".equals(visibleText)) {
            return List.of("Close");
        }
        if ("Close".equals(visibleText)) {
            return List.of("Closed");
        }
        return List.of();
    }

    private boolean isMenuOpen() {
        // Audit Logs All Modules / All Actions: sidebar labels (Individual Users, Company Users,
        // etc.) match the same visible text as dropdown options. Only trust the Flutter
        // "Popup menu" root — otherwise openDropdown() no-ops and option clicks miss.
        if (isLongPopupMenu()) {
            return hasPopupMenuVisible();
        }
        if (hasPopupMenuVisible()) {
            return true;
        }
        // Do not use hasBothDropdownOptionSemantics() here: Flutter often keeps option
        // nodes in the semantics tree while the menu is closed (Manage Admin / Moderation),
        // which made openDropdown() return early and left options unclickable in CI.
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                buildMenuOpenScript(),
                config.rowStatusLabelPrefix(),
                config.activeOption().visibleText(),
                config.inactiveOption().visibleText(),
                alternateOptionTexts(config.inactiveOption().visibleText()).toArray(new String[0])
        ));
    }

    private boolean hasPopupMenuVisible() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() +
                        "return hasPopupMenu();"
        ));
    }

    private static String popupMenuHelperJs() {
        return "const normalizeText = (value) => (value || '').replace(/\\u00A0/g, ' ').trim();" +
                "const nodeVisibleText = (node) => {" +
                "  if (!node) return '';" +
                "  const label = normalizeText(node.getAttribute('aria-label'));" +
                "  const text = normalizeText(node.textContent);" +
                // Flutter option nodes often use semantics ids as aria-label (e.g. closed_option_closed).
                // Prefer visible textContent for display matching in that case.
                "  if (label && (label.includes('_option_') || label.includes('_submenu'))) {" +
                "    return text || label;" +
                "  }" +
                "  return label || text;" +
                "};" +
                "const matchesVisibleText = (node, target) => {" +
                "  if (!target) return false;" +
                "  const wanted = normalizeText(target);" +
                "  if (nodeVisibleText(node) === wanted) return true;" +
                "  const label = normalizeText(node.getAttribute('aria-label'));" +
                "  const text = normalizeText(node.textContent);" +
                "  return label === wanted || text === wanted;" +
                "};" +
                "const isPopupMenuText = (text) => normalizeText(text) === 'Popup menu';" +
                "const isPopupMenuNode = (node) => {" +
                "  if (!node) return false;" +
                "  const label = normalizeText(node.getAttribute('aria-label'));" +
                "  const text = normalizeText(node.textContent);" +
                "  return isPopupMenuText(label) || isPopupMenuText(text);" +
                "};" +
                "const hasPopupMenu = () => Array.from(document.querySelectorAll('flt-semantics')).some(isPopupMenuNode);" +
                "const popupMenuRoot = () => {" +
                "  const popup = Array.from(document.querySelectorAll('flt-semantics')).find(isPopupMenuNode);" +
                "  if (!popup) return null;" +
                "  return popup.parentElement;" +
                "};" +
                "const popupMenuNodes = () => {" +
                "  const root = popupMenuRoot();" +
                "  if (!root) return [];" +
                "  return Array.from(root.querySelectorAll('flt-semantics'));" +
                "};" +
                "const isInNav = (node) => {" +
                "  if (!node) return false;" +
                "  const nav = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                "  return !!(nav && nav.contains(node));" +
                "};" +
                "const isVisibleNode = (node) => {" +
                "  if (!node) return false;" +
                "  const rect = node.getBoundingClientRect();" +
                "  return rect.width > 0 && rect.height > 0" +
                "    && rect.bottom > 0 && rect.top < window.innerHeight" +
                "    && rect.right > 0 && rect.left < window.innerWidth;" +
                "};" +
                // Sidebar nav entries use *_submenu inside Navigation menu.
                // Transactions type filter also uses *_submenu outside nav — those are real options.
                "const isSidebarSubmenu = (node) => {" +
                "  if (!node || !isInNav(node)) return false;" +
                "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                "  return label.includes('_submenu');" +
                "};" +
                "const isFilterOptionLabel = (node) => {" +
                "  if (!node || isInNav(node) || isSidebarSubmenu(node)) return false;" +
                "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                "  if (label.includes('_option_')) return true;" +
                // Transactions type filter may use *_submenu outside Navigation.
                "  return label.includes('_submenu');" +
                "};" +
                "const isOptionLike = (node) => {" +
                "  if (!node || isInNav(node) || isSidebarSubmenu(node)) return false;" +
                "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                "  const role = (node.getAttribute('role') || '').toLowerCase();" +
                "  if (role === 'menuitem' || role === 'option') return true;" +
                "  if (label.includes('_option_') || label.includes('_submenu')) return true;" +
                "  if (node.getAttribute('flt-tappable') !== null) return true;" +
                "  return false;" +
                "};" +
                "const optionPool = () => {" +
                "  const popup = popupMenuNodes().filter(n => isFilterOptionLabel(n));" +
                "  if (popup && popup.length) return popup;" +
                "  return Array.from(document.querySelectorAll('flt-semantics'))" +
                "    .filter(n => isFilterOptionLabel(n));" +
                "};" +
                "const findOptionNode = (semanticsLabel, visibleText) => {" +
                "  const pools = [];" +
                "  const root = popupMenuRoot();" +
                "  const popup = popupMenuNodes().filter(n => !isInNav(n) && !isSidebarSubmenu(n));" +
                "  if (popup && popup.length) pools.push(popup);" +
                // Page-wide fallback only when no popup root: filter option nodes only.
                "  if (!root) {" +
                "    const pageOptions = Array.from(document.querySelectorAll('flt-semantics')).filter(n => {" +
                "      return isFilterOptionLabel(n);" +
                "    });" +
                "    if (pageOptions.length) pools.push(pageOptions);" +
                "  }" +
                "  const matchesSemantics = (candidate) => {" +
                "    if (isSidebarSubmenu(candidate) || isInNav(candidate)) return false;" +
                "    const label = (candidate.getAttribute('aria-label') || '').toLowerCase();" +
                "    if (!semanticsLabel) return false;" +
                "    if (!(label === semanticsLabel || label.includes(semanticsLabel))) return false;" +
                "    return label.includes('_option_') || label.includes('_submenu');" +
                "  };" +
                "  for (const pool of pools) {" +
                "    let node = null;" +
                "    if (semanticsLabel) {" +
                "      node = pool.find(c => matchesSemantics(c) && isVisibleNode(c));" +
                "      if (!node) node = pool.find(matchesSemantics);" +
                "    }" +
                "    if (!node && visibleText) {" +
                "      node = pool.find(candidate =>" +
                "        isFilterOptionLabel(candidate)" +
                "        && isOptionLike(candidate)" +
                "        && matchesVisibleText(candidate, visibleText)" +
                "        && isVisibleNode(candidate));" +
                "      if (!node) {" +
                "        node = pool.find(candidate =>" +
                "          isFilterOptionLabel(candidate)" +
                "          && matchesVisibleText(candidate, visibleText)" +
                "          && (!root || root.contains(candidate)));" +
                "      }" +
                "    }" +
                "    if (node) return node;" +
                "  }" +
                "  return null;" +
                "};";
    }

    private boolean hasBothDropdownOptionSemantics() {
        boolean hasActive = false;
        boolean hasInactive = false;
        for (String label : config.activeOption().semanticsLabels()) {
            if (!driver.findElements(By.xpath("//flt-semantics[@aria-label='" + label + "']")).isEmpty()) {
                hasActive = true;
                break;
            }
        }
        for (String label : config.inactiveOption().semanticsLabels()) {
            if (!driver.findElements(By.xpath("//flt-semantics[@aria-label='" + label + "']")).isEmpty()) {
                hasInactive = true;
                break;
            }
        }
        return hasActive && hasInactive;
    }

    private boolean isOptionPresent(String semanticsLabel, String visibleText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() +
                        "const semanticsLabel = (arguments[0] || '').toLowerCase();" +
                        "const visibleText = arguments[1];" +
                        "const rowStatusPrefix = arguments[2];" +
                        "const node = findOptionNode(semanticsLabel, visibleText);" +
                        "if (!node) return false;" +
                        "const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "if (label.includes(rowStatusPrefix) || label.includes('user_row_status')) return false;" +
                        "const rect = node.getBoundingClientRect();" +
                        "return rect.width > 0 && rect.height > 0;",
                semanticsLabel == null ? "" : semanticsLabel,
                visibleText,
                config.rowStatusLabelPrefix()
        ));
    }

    private boolean hasFilterOptionsVisible() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                buildMenuOpenScript(),
                config.rowStatusLabelPrefix(),
                config.activeOption().visibleText(),
                config.inactiveOption().visibleText(),
                alternateOptionTexts(config.inactiveOption().visibleText()).toArray(new String[0])
        ));
    }

    private boolean tryOpenDropdown() {
        for (String triggerLabel : config.dropdownTriggerLabels()) {
            if (semantics.clickSemanticsLabelViaScript(triggerLabel)) {
                return true;
            }
        }
        if (clickStatusTriggerByScript()) {
            return true;
        }
        WebElement trigger = findVisibleStatusTriggerElement();
        if (trigger != null) {
            semantics.scrollIntoView(trigger);
            if (semantics.clickElementReliably(trigger)) {
                return true;
            }
        }
        return clickStatusTriggerByCoordinates();
    }

    private boolean clickStatusTriggerByScript() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                buildClickStatusTriggerScript(),
                config.searchFieldLabel(),
                config.alternateSearchFieldLabels(),
                config.rowStatusLabelPrefix(),
                config.dropdownTriggerLabels(),
                config.dropdownTriggerVisibleTexts()
        ));
    }

    private boolean clickStatusTriggerByCoordinates() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                buildClickStatusByCoordinatesScript(),
                config.searchFieldLabel(),
                config.alternateSearchFieldLabels()
        ));
    }

    private WebElement findVisibleStatusTriggerElement() {
        return (WebElement) ((JavascriptExecutor) driver).executeScript(
                buildFindStatusTriggerScript(),
                config.searchFieldLabel(),
                config.alternateSearchFieldLabels(),
                config.rowStatusLabelPrefix(),
                config.dropdownTriggerLabels(),
                config.dropdownTriggerVisibleTexts()
        );
    }

    private String[] allSearchFieldLabels() {
        String[] alternates = config.alternateSearchFieldLabels();
        String[] labels = new String[1 + alternates.length];
        labels[0] = config.searchFieldLabel();
        System.arraycopy(alternates, 0, labels, 1, alternates.length);
        return labels;
    }

    private static String searchAnchorHelperJs() {
        return "const findSearchAnchor = (primaryLabel, alternateLabels) => {" +
                "  const labels = [primaryLabel, ...(alternateLabels || [])].filter(Boolean);" +
                "  for (const label of labels) {" +
                "    const semantics = document.querySelector('flt-semantics[aria-label=\"' + label + '\"]');" +
                "    if (semantics) return semantics;" +
                "    const input = document.querySelector('input[aria-label=\"' + label + '\"]');" +
                "    if (input) return input.closest('flt-semantics') || input;" +
                "  }" +
                "  const genericInput = document.querySelector('input[aria-label*=\"Search\"]');" +
                "  if (genericInput) return genericInput.closest('flt-semantics') || genericInput;" +
                "  return null;" +
                "};" +
                "const searchRectFor = (primaryLabel, alternateLabels) => {" +
                "  const anchor = findSearchAnchor(primaryLabel, alternateLabels);" +
                "  return anchor ? anchor.getBoundingClientRect() : null;" +
                "};";
    }

    private boolean clickOption(String semanticsLabel, String visibleText) {
        if (semanticsLabel != null) {
            scrollOptionIntoView(semanticsLabel, visibleText);
        } else if (visibleText != null) {
            scrollOptionIntoView(null, visibleText);
        }
        // Headless Flutter menus often ignore synthetic DOM events; prefer CDP canvas clicks.
        if (clickOptionViaCdp(semanticsLabel, visibleText)) {
            return true;
        }
        // Long Audit Logs menus: never use document-wide semantics clicks — sidebar entries
        // share visible labels (Individual Users / Company Users) and steal the click.
        if (!isLongPopupMenu()) {
            if (semanticsLabel != null && semantics.clickSemanticsLabelWithoutEnabling(semanticsLabel)) {
                return true;
            }
            if (semanticsLabel != null && semantics.clickSemanticsLabelViaScript(semanticsLabel)) {
                return true;
            }
            if (visibleText != null && semantics.clickVisibleText(visibleText)) {
                return true;
            }
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                buildClickOptionScript(),
                semanticsLabel,
                visibleText,
                config.rowStatusLabelPrefix()
        ));
    }

    private boolean clickOptionViaCdp(String semanticsLabel, String visibleText) {
        if (!(driver instanceof ChromeDriver chromeDriver)) {
            return false;
        }
        Object coords = ((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() +
                        "const semanticsLabel = (arguments[0] || '').toLowerCase();" +
                        "const visibleText = arguments[1];" +
                        "const node = findOptionNode(semanticsLabel, visibleText);" +
                        "if (!node) return null;" +
                        "try { node.scrollIntoView({block: 'center', inline: 'nearest'}); } catch (e) {}" +
                        "const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "const rect = target.getBoundingClientRect();" +
                        "if (rect.width <= 0 || rect.height <= 0) return null;" +
                        "if (rect.bottom < 0 || rect.top > window.innerHeight) return null;" +
                        "return { x: Math.round(rect.left + rect.width / 2), y: Math.round(rect.top + rect.height / 2) };",
                semanticsLabel,
                visibleText
        );
        if (!(coords instanceof Map<?, ?> point)) {
            return false;
        }
        Object xObj = point.get("x");
        Object yObj = point.get("y");
        if (!(xObj instanceof Number) || !(yObj instanceof Number)) {
            return false;
        }
        double x = ((Number) xObj).doubleValue();
        double y = ((Number) yObj).doubleValue();
        try {
            setDismissPointerEvents(false);
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
        } finally {
            setDismissPointerEvents(true);
        }
    }

    private void setDismissPointerEvents(boolean enabled) {
        ((JavascriptExecutor) driver).executeScript(
                "const nodes = Array.from(document.querySelectorAll('flt-semantics')).filter(n => {" +
                        "  const label = (n.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return label === 'dismiss' || label.includes('dismiss');" +
                        "});" +
                        "nodes.forEach(n => {" +
                        "  if (arguments[0]) {" +
                        "    n.style.removeProperty('pointer-events');" +
                        "  } else {" +
                        "    n.style.pointerEvents = 'none';" +
                        "  }" +
                        "});",
                enabled
        );
    }

    private void scrollUntilStatusTriggerVisible() {
        for (int attempt = 0; attempt < 8; attempt++) {
            if (findVisibleStatusTriggerElement() != null) {
                return;
            }
            scrollFilterAreaRightOnce();
            semantics.pauseAfterScroll();
        }
    }

    private void logStatusRelatedSemantics() {
        String labels = (String) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics'))" +
                        ".map(node => {" +
                        "  const label = node.getAttribute('aria-label') || '';" +
                        "  const text = (node.textContent || '').trim();" +
                        "  if (!label && !text) return '';" +
                        "  if (label.includes('status') || label.includes('filter') ||" +
                        "      label.includes('active_option') || label.includes('blocked_option') ||" +
                        "      text === 'Status' || text === 'Active' || text === 'Inactive' || text === 'Blocked') {" +
                        "    return (label || text) + (label && text && text !== label ? ':' + text : '');" +
                        "  }" +
                        "  return '';" +
                        "}).filter(Boolean).join(' | ');"
        );
        System.out.println("Status-related semantics on failure: " + labels);
        if (labels == null || labels.isBlank()) {
            String pageText = semantics.getSemanticsText(driver);
            int end = pageText == null ? 0 : Math.min(pageText.length(), 600);
            System.out.println("Page text excerpt: " + (pageText == null ? "" : pageText.substring(0, end)));
            String allLabels = (String) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('flt-semantics'))" +
                            ".map(node => {" +
                            "  const label = node.getAttribute('aria-label') || '';" +
                            "  const text = (node.textContent || '').trim();" +
                            "  if (label) return label;" +
                            "  if (text && text.length < 40) return text;" +
                            "  return '';" +
                            "}).filter(Boolean).slice(0, 100).join(' | ');"
            );
            System.out.println("Visible semantics excerpt: " + allLabels);
        }
    }

    private String buildOpenFailureMessage() {
        StatusFilterConfig.StatusOptionConfig active = config.activeOption();
        StatusFilterConfig.StatusOptionConfig inactive = config.inactiveOption();
        return "Status dropdown did not open; expected "
                + String.join("/", active.semanticsLabels()) + " or "
                + String.join("/", inactive.semanticsLabels())
                + " (visible text: " + active.visibleText() + "/" + inactive.visibleText() + ")";
    }

    private static String buildMenuOpenScript() {
        return popupMenuHelperJs() +
                "const rowStatusPrefix = arguments[0];" +
                "const activeText = arguments[1];" +
                "const inactiveText = arguments[2];" +
                "const inactiveAlternates = arguments[3] || [];" +
                "const inactiveTexts = [inactiveText, ...inactiveAlternates].filter(Boolean);" +
                "const isVisible = (node) => {" +
                "  const rect = node.getBoundingClientRect();" +
                "  return rect.width > 0 && rect.height > 0;" +
                "};" +
                "const isFilterOption = (node, text) => {" +
                "  if (!matchesVisibleText(node, text)) return false;" +
                "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                "  if (label.includes(rowStatusPrefix) || label.includes('user_row_status')) return false;" +
                "  return isVisible(node);" +
                "};" +
                "const hasActive = Array.from(document.querySelectorAll('flt-semantics')).some(n => isFilterOption(n, activeText));" +
                "const hasInactive = inactiveTexts.some(text =>" +
                "  Array.from(document.querySelectorAll('flt-semantics')).some(n => isFilterOption(n, text))" +
                ");" +
                "if (hasActive && hasInactive) return true;" +
                "if (hasPopupMenu()) {" +
                "  const root = popupMenuRoot();" +
                "  if (root) {" +
                "    const popupNodes = Array.from(root.querySelectorAll('flt-semantics'));" +
                "    const popupHasActive = popupNodes.some(n => isFilterOption(n, activeText));" +
                "    const popupHasInactive = inactiveTexts.some(text => popupNodes.some(n => isFilterOption(n, text)));" +
                "    if (popupHasActive && popupHasInactive) return true;" +
                "  }" +
                "}" +
                "return false;";
    }

    private static String buildFindStatusTriggerScript() {
        return searchAnchorHelperJs() +
                "const primarySearchLabel = arguments[0];" +
                "const alternateSearchLabels = arguments[1] || [];" +
                "const rowStatusPrefix = arguments[2];" +
                "const triggerLabels = arguments[3];" +
                "const triggerVisibleTexts = arguments[4] || ['Status'];" +
                "const isVisible = (node) => {" +
                "  const rect = node.getBoundingClientRect();" +
                "  return rect.width > 0 && rect.height > 0 &&" +
                "    rect.right > 0 && rect.left < window.innerWidth &&" +
                "    rect.bottom > 0 && rect.top < window.innerHeight;" +
                "};" +
                "const searchRect = searchRectFor(primarySearchLabel, alternateSearchLabels);" +
                "for (const label of triggerLabels) {" +
                "  const node = document.querySelector('flt-semantics[aria-label=\"' + label + '\"]');" +
                "  if (node && isVisible(node)) return node;" +
                "}" +
                "const candidates = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                "  const text = (node.textContent || '').trim();" +
                "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                "  if (!triggerVisibleTexts.includes(text)) return false;" +
                "  if (label.includes(rowStatusPrefix) || label.includes('user_row_status')) return false;" +
                "  if (label.includes('status_option_active') || label.includes('status_option_inactive') ||" +
                "      label.includes('active_option') || label.includes('blocked_option')) return false;" +
                "  if (!isVisible(node)) return false;" +
                "  if (searchRect) {" +
                "    const rect = node.getBoundingClientRect();" +
                "    return rect.top >= searchRect.top - 100 && rect.top <= searchRect.bottom + 100;" +
                "  }" +
                "  return true;" +
                "});" +
                "return candidates.length ? candidates[0] : null;";
    }

    private static String buildClickStatusTriggerScript() {
        return searchAnchorHelperJs() +
                "const primarySearchLabel = arguments[0];" +
                "const alternateSearchLabels = arguments[1] || [];" +
                "const rowStatusPrefix = arguments[2];" +
                "const triggerLabels = arguments[3];" +
                "const triggerVisibleTexts = arguments[4] || ['Status'];" +
                "const clickNode = (node) => {" +
                "  if (!node) return false;" +
                "  const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                "  const rect = target.getBoundingClientRect();" +
                "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                "  const x = rect.left + rect.width / 2;" +
                "  const y = rect.top + rect.height / 2;" +
                "  const hit = document.elementFromPoint(x, y) || target;" +
                "  ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                "    hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                "  });" +
                "  return true;" +
                "};" +
                "for (const label of triggerLabels) {" +
                "  const node = document.querySelector('flt-semantics[aria-label=\"' + label + '\"]');" +
                "  if (node && clickNode(node)) return true;" +
                "}" +
                "const searchRect = searchRectFor(primarySearchLabel, alternateSearchLabels);" +
                "const candidates = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                "  const text = (node.textContent || '').trim();" +
                "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                "  if (!triggerVisibleTexts.includes(text)) return false;" +
                "  if (label.includes(rowStatusPrefix) || label.includes('user_row_status')) return false;" +
                "  if (label.includes('status_option_active') || label.includes('status_option_inactive') ||" +
                "      label.includes('active_option') || label.includes('blocked_option')) return false;" +
                "  if (searchRect) {" +
                "    const rect = node.getBoundingClientRect();" +
                "    if (rect.top < searchRect.top - 100 || rect.top > searchRect.bottom + 100) return false;" +
                "  }" +
                "  return true;" +
                "});" +
                "for (const candidate of candidates) {" +
                "  if (clickNode(candidate)) return true;" +
                "}" +
                "return false;";
    }

    private static String buildClickStatusByCoordinatesScript() {
        return searchAnchorHelperJs() +
                "const primarySearchLabel = arguments[0];" +
                "const alternateSearchLabels = arguments[1] || [];" +
                "const searchRect = searchRectFor(primarySearchLabel, alternateSearchLabels);" +
                "if (!searchRect) return false;" +
                "const y = searchRect.top + searchRect.height / 2;" +
                "const clickPoint = (x, yCoord) => {" +
                "  const hit = document.elementFromPoint(x, yCoord);" +
                "  if (!hit) return false;" +
                "  ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                "    hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: yCoord }));" +
                "  });" +
                "  return true;" +
                "};" +
                "for (const offset of [80, 140, 200, 260, 320, 380, 440, 500, 560]) {" +
                "  if (clickPoint(Math.min(window.innerWidth - 8, searchRect.right + offset), y)) return true;" +
                "}" +
                "return false;";
    }

    private static String buildClickOptionScript() {
        return popupMenuHelperJs() +
                "const semanticsLabel = arguments[0];" +
                "const visibleText = arguments[1];" +
                "const rowStatusPrefix = arguments[2];" +
                "const clickNode = (node) => {" +
                "  if (!node) return false;" +
                "  try { node.scrollIntoView({block: 'center', inline: 'nearest'}); } catch (e) {}" +
                "  const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                "  const rect = target.getBoundingClientRect();" +
                "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                "  if (rect.bottom < 0 || rect.top > window.innerHeight) return false;" +
                "  const x = rect.left + rect.width / 2;" +
                "  const y = rect.top + rect.height / 2;" +
                "  const hit = document.elementFromPoint(x, y) || target;" +
                "  ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                "    hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                "  });" +
                "  return true;" +
                "};" +
                "const node = findOptionNode(" +
                "  semanticsLabel ? String(semanticsLabel).toLowerCase() : '', visibleText);" +
                "if (node && clickNode(node)) return true;" +
                "const pool = optionPool();" +
                "const matches = pool.filter(node => {" +
                "  if (!matchesVisibleText(node, visibleText)) return false;" +
                "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                "  if (label.includes(rowStatusPrefix) || label.includes('user_row_status')) return false;" +
                "  if (semanticsLabel && (label === String(semanticsLabel).toLowerCase()" +
                "      || label.includes(String(semanticsLabel).toLowerCase()))) {" +
                "    return true;" +
                "  }" +
                "  return !label.includes('user_row_status') && !label.includes('company_row_status')" +
                "    && !label.includes('transaction_row') && !label.includes('audit_log_row');" +
                "});" +
                "for (const match of matches) {" +
                "  if (clickNode(match)) return true;" +
                "}" +
                "return false;";
    }

    private static String buildScrollScript() {
        return searchAnchorHelperJs() +
                "const primarySearchLabel = arguments[0];" +
                "const alternateSearchLabels = arguments[1] || [];" +
                "const search = findSearchAnchor(primarySearchLabel, alternateSearchLabels);" +
                "const dispatchWheel = (x, y) => {" +
                "  const target = document.elementFromPoint(x, y) || document.body;" +
                "  for (let i = 0; i < 4; i++) {" +
                "    target.dispatchEvent(new WheelEvent('wheel', {" +
                "      deltaX: 320, deltaY: 0, bubbles: true, cancelable: true" +
                "    }));" +
                "  }" +
                "};" +
                "if (search) {" +
                "  const rect = search.getBoundingClientRect();" +
                "  dispatchWheel(rect.left + rect.width / 2, rect.top + rect.height / 2);" +
                "}" +
                "const isScrollable = (el) => {" +
                "  if (!el) return false;" +
                "  const style = window.getComputedStyle(el);" +
                "  const overflowX = style.overflowX;" +
                "  return (overflowX === 'auto' || overflowX === 'scroll') && el.scrollWidth > el.clientWidth + 8;" +
                "};" +
                "const scrollNode = (node) => {" +
                "  if (!node) return false;" +
                "  node.scrollLeft = Math.min(node.scrollWidth, node.scrollLeft + Math.max(320, node.clientWidth * 0.75));" +
                "  return true;" +
                "};" +
                "const scopes = [];" +
                "if (search) {" +
                "  let ancestor = search.parentElement;" +
                "  for (let depth = 0; depth < 14 && ancestor; depth++) {" +
                "    scopes.push(ancestor);" +
                "    ancestor = ancestor.parentElement;" +
                "  }" +
                "}" +
                "scopes.push(document.body, document.documentElement);" +
                "for (const scope of scopes) {" +
                "  if (isScrollable(scope)) scrollNode(scope);" +
                "  Array.from(scope.querySelectorAll('*')).filter(isScrollable).forEach(scrollNode);" +
                "}" +
                "window.scrollBy(Math.max(320, window.innerWidth * 0.5), 0);";
    }
}
