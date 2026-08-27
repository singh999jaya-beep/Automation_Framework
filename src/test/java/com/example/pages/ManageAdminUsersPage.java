package com.example.pages;

import com.example.pages.support.FlutterSemanticsSupport;
import com.example.pages.support.StatusFilterSupport;
import com.example.utils.UsersPageContext;
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

public class ManageAdminUsersPage {

    private static final String MANAGE_ADMIN_USERS_TEXT = "Manage Admin Users";
    private static final String INVITE_ADMIN_TEXT = "Invite Admin";
    private static final String INVITE_NEW_ADMIN_TEXT = "Invite New Admin";
    private static final String EMAIL_ADDRESS_TEXT = "Email Address";
    private static final String FULL_NAME_TEXT = "Full Name";
    private static final String ASSIGN_ROLE_TEXT = "Assign Role";
    private static final String SELECT_ROLE_TEXT = "Select role";
    private static final String SUBMIT_INVITE_TEXT = "Submit Invite";
    private static final String SEND_INVITATION_TEXT = "Send Invitation";
    private static final String ALL_STATUS_TEXT = "All Status";
    private static final String ALL_ACCESS_TEXT = "All Access";
    private static final String TEMPORARY_TEXT = "Temporary";
    private static final String TEMPORARY_ACCESS_TEXT = "Temporary Access";
    private static final String PERMANENT_TEXT = "Permanent";
    private static final String PERMANENT_ACCESS_TEXT = "Permanent Access";

    private static final String SEMANTICS_USER_ACCESS_MENU = "user_and_access_management_submenu";
    private static final String SEMANTICS_ADMIN_USERS_MENU = "admin_users_submenu";
    private static final String SEMANTICS_SEARCH = "search_field";
    private static final String SEMANTICS_ALL_STATUS = "all_status_option_all_status";
    private static final String SEMANTICS_ALL_ACCESS = "all_access_option_all_access";
    private static final String SEMANTICS_ACTIVE = "active_option_active";
    private static final String SEMANTICS_BLOCKED = "blocked_option_blocked";
    private static final String SEMANTICS_PENDING = "pending_option_pending";
    private static final String SEMANTICS_DELETED = "deleted_option_deleted";
    private static final String SEMANTICS_DELETED_LEGACY = "seleted_option_deleted";
    private static final String SEMANTICS_EXPIRED = "expired_option_expired";
    private static final String SEMANTICS_PERMANENT = "permanent_option_permanent";
    private static final String SEMANTICS_TEMPORARY_FILTER = "temporary_option_temporary";
    private static final String SEMANTICS_ROW_STATUS = "admin_row_status_0";
    private static final String SEMANTICS_ROW_ACCESS = "admin_row_access_0";
    private static final String SEMANTICS_ROW_ID = "admin_row_id_0";

    private static final String SEMANTICS_INVITE_ADMIN = "invite_admin_button";
    private static final String SEMANTICS_EMAIL = "email_field";
    private static final String SEMANTICS_FULL_NAME = "full_name_field";
    private static final String SEMANTICS_NAME = "name_field";
    private static final String SEMANTICS_ROLE_DROPDOWN = "role_dropdown";
    private static final String SEMANTICS_ROLE_RAHUL = "role_name_option_role_name";
    private static final String SEMANTICS_TEMPORARY_ACCESS = "temporary_access_button";
    private static final String SEMANTICS_PERMANENT_ACCESS = "permanent_access_button";
    private static final String SEMANTICS_SUBMIT_INVITE = "submit_invite_button";

    private static final Duration DEFAULT_WAIT = FlutterSemanticsSupport.DEFAULT_WAIT;
    private static final Duration PAGE_POLL = Duration.ofSeconds(12);
    private static final Duration INVITE_FORM_POLL = Duration.ofSeconds(20);
    private static final Duration STATUS_VALIDATION_DELAY = Duration.ofSeconds(10);
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
                    // Always click the target node. elementFromPoint hits Flutter Dismiss overlays.
                    "  ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                    "    target.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y, view: window }));" +
                    "  });" +
                    "  try { target.click(); } catch (e) {}" +
                    "  return true;" +
                    "}";

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final FlutterSemanticsSupport semantics;
    private final StatusFilterSupport statusFilter;
    private final StatusFilterSupport accessFilter;

    private final By searchBarSemantics = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_SEARCH + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_SEARCH + "')]");

    private final By searchBarInput = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_SEARCH + "']//input" +
                    " | //input[@aria-label='" + SEMANTICS_SEARCH + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_SEARCH + "')]//input" +
                    " | //input[contains(@aria-label,'Search')]");

    private final By adminRowStatusCell = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_ROW_STATUS + "']" +
                    " | //flt-semantics[contains(@aria-label,'admin_row_status')]");

    private final By adminRowAccessCell = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_ROW_ACCESS + "']" +
                    " | //flt-semantics[contains(@aria-label,'admin_row_access')]");

    private final By adminRowIdCell = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_ROW_ID + "']" +
                    " | //flt-semantics[contains(@aria-label,'admin_row_id')]" +
                    " | (//flt-semantics[starts-with(@aria-label,'VI-')])[1]");

    public ManageAdminUsersPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_WAIT);
        this.semantics = new FlutterSemanticsSupport(driver);
        this.statusFilter = StatusFilterSupport.forManageAdminUsers(driver, semantics);
        this.accessFilter = StatusFilterSupport.forManageAdminUsersAccess(driver, semantics);
    }

    public void clickManageAdminUsersMenu() {
        semantics.enableFlutterSemantics();
        waitUntil(this::isHomePageLoaded, DEFAULT_WAIT);
        scrollNavigationToUserAccess();
        openNavigationItem(
                SEMANTICS_ADMIN_USERS_MENU,
                MANAGE_ADMIN_USERS_TEXT,
                "Manage Admin Users submenu not found (expected semantics "
                        + SEMANTICS_ADMIN_USERS_MENU + ")");
        if (!waitUntil(this::isManageAdminUsersPageLoaded, PAGE_POLL)) {
            throw new NoSuchElementException(
                    "Manage Admin Users page did not load (expected semantics "
                            + SEMANTICS_SEARCH + " or visible text '" + MANAGE_ADMIN_USERS_TEXT + "')");
        }
        waitForSearchBarReady();
    }

    public void clickInviteAdmin() {
        ensureManageAdminUsersPageReady();
        scrollToSemanticsLabel(SEMANTICS_INVITE_ADMIN);
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            if (attempt > 0) {
                scrollToSemanticsLabel(SEMANTICS_INVITE_ADMIN);
            }
            if (tryClickSemanticsAction(SEMANTICS_INVITE_ADMIN, INVITE_ADMIN_TEXT, INVITE_NEW_ADMIN_TEXT, "Invite")) {
                semantics.pauseAfterScroll();
                if (waitUntil(this::isInviteAdminFormLoaded, INVITE_FORM_POLL)) {
                    return;
                }
            }
            semantics.pauseAfterScroll();
        }
        logInviteFormSemantics();
        throw new NoSuchElementException(
                "Invite Admin form did not load (expected semantics " + SEMANTICS_EMAIL
                        + ", " + SEMANTICS_FULL_NAME + ", or " + SEMANTICS_ROLE_DROPDOWN + ")");
    }

    public void enterEmailAddress(String email) {
        ensureInviteAdminFormReady();
        // Avoid TAB — it can move focus onto the Flutter a11y control; later SPACE toggles a11y off.
        enterField(new String[]{SEMANTICS_EMAIL}, new String[]{EMAIL_ADDRESS_TEXT, "Email", "Enter Email"}, email, false);
    }

    public void enterFullName(String fullName) {
        ensureInviteAdminFormReady();
        enterField(new String[]{SEMANTICS_FULL_NAME, SEMANTICS_NAME},
                new String[]{FULL_NAME_TEXT, "Enter full name", "Name"}, fullName, false);
        ensureSemanticsAlive(driver);
        scrollInviteFormToRoleField();
        waitUntil(d -> {
            ensureSemanticsAlive(d);
            return !d.findElements(semanticsLocator(SEMANTICS_ROLE_DROPDOWN)).isEmpty()
                    || hasCollapsedInviteSummary(d)
                    || pageHasAssignRoleText(d)
                    || isInviteAccessTypeSectionVisible(d);
        }, Duration.ofSeconds(12));
    }

    /**
     * Opens Assign Role dropdown and selects Rahul in the same action.
     * Crossing a Cucumber step boundary while the Flutter Popup menu is open lets the
     * fullscreen Dismiss overlay wipe semantics (Available: []).
     */
    public void clickAssignRole() {
        ensureInviteRoleSelectionReady();
        ensureSemanticsAlive(driver);
        waitUntil(d -> {
            if (isRolePickerOpen(d) || isSelectedRoleVisible(d, "Rahul")
                    || isInviteAccessTypeSectionVisible(d)) {
                return true;
            }
            ensureSemanticsAlive(d);
            return !d.findElements(semanticsLocator(SEMANTICS_ROLE_DROPDOWN)).isEmpty()
                    || hasCollapsedInviteSummary(d)
                    || pageHasAssignRoleText(d);
        }, Duration.ofSeconds(10));
        // Never treat Access Type alone as role selected — Permanent/Temporary are always on the form.
        if (isSelectedRoleVisible(driver, "Rahul") || isAnyKnownInviteRoleSelected()) {
            return;
        }
        if (driver.findElements(semanticsLocator(SEMANTICS_ROLE_DROPDOWN)).isEmpty()
                && !isRolePickerOpen(driver)) {
            scrollInviteFormToRoleField();
            ensureSemanticsAlive(driver);
        }
        // Open + select atomically — do not return with an open menu for the next step.
        // Prefer Rahul; if missing in this environment, fall back to another invite role.
        openAndSelectRole(resolveInviteRoleName("Rahul"));
    }

    private static final String[] KNOWN_INVITE_ROLES = {
            "Rahul", "Anupam", "QA", "CEO", "CTO", "COO", "Marketing",
            "team lead", "Manager", "Company Manager", "new admin"
    };

    private boolean isAnyKnownInviteRoleSelected() {
        for (String role : KNOWN_INVITE_ROLES) {
            if (isSelectedRoleVisible(driver, role)) {
                return true;
            }
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        ROLE_PICKER_SCOPE_SCRIPT +
                        "const roles = arguments[0];" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  if (isInNav(node) || !isInsideInviteForm(node)) return false;" +
                        "  const role = (node.getAttribute('role') || '').toLowerCase();" +
                        "  if (role !== 'button') return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return roles.some(r => text === r);" +
                        "});",
                java.util.Arrays.asList(KNOWN_INVITE_ROLES)
        ));
    }

    private boolean pageHasAssignRoleText(WebDriver webDriver) {
        String text = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        return text.contains(semantics.normalizeText(ASSIGN_ROLE_TEXT))
                || text.contains(semantics.normalizeText(SELECT_ROLE_TEXT));
    }

    private void scrollInviteFormToRoleField() {
        ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const markers = ['Assign Role', 'Select role', 'Full Name', 'Email Address'];" +
                        "for (const wanted of markers) {" +
                        "  const node = Array.from(document.querySelectorAll('flt-semantics')).find(n => {" +
                        "    if (isInNav(n)) return false;" +
                        "    return ((n.textContent || '').replace(/\\s+/g, ' ').trim()) === wanted;" +
                        "  });" +
                        "  if (node) {" +
                        "    node.scrollIntoView({block:'center', inline:'nearest'});" +
                        "    return true;" +
                        "  }" +
                        "}" +
                        "return false;"
        );
        semantics.pauseAfterScroll();
        ensureSemanticsAlive(driver);
    }

    /**
     * Selects a role option (same pattern as inactive status selectOption).
     * Prefer verifying a selection already applied during {@link #clickAssignRole()}.
     */
    public void selectRole(String roleName) {
        String resolvedRole = resolveInviteRoleName(roleName);
        // Already applied during Assign Role — never force a11y / reopen (clears the tree).
        if (isSelectedRoleVisible(driver, resolvedRole)
                || isSelectedRoleVisible(driver, roleName)
                || isAnyKnownInviteRoleSelected()) {
            return;
        }
        // Menu still open from Assign Role: click without enable/reset first.
        if (isRolePickerOpen(driver) && trySelectRoleFromOpenMenu(resolvedRole)) {
            return;
        }
        openAndSelectRole(resolvedRole);
    }

    /**
     * Prefer the requested invite role; if it is absent from the open/available menu, use another
     * known invite role present in the environment (CI staging data can omit Rahul).
     */
    private String resolveInviteRoleName(String preferredRole) {
        if (preferredRole == null || preferredRole.isBlank()) {
            return preferredRole;
        }
        if (isSelectedRoleVisible(driver, preferredRole)
                || isRoleOptionPresent(driver, preferredRole, preferredRole)
                || ("Rahul".equalsIgnoreCase(preferredRole)
                && isRoleOptionPresent(driver, SEMANTICS_ROLE_RAHUL, "Rahul"))) {
            return preferredRole;
        }
        String available = listAvailableInviteRoleOptions();
        if (available == null || available.isBlank() || "[]".equals(available.trim())) {
            return preferredRole;
        }
        String availableLower = available.toLowerCase();
        if (availableLower.contains(preferredRole.toLowerCase())) {
            return preferredRole;
        }
        String[] fallbacks = KNOWN_INVITE_ROLES;
        for (String candidate : fallbacks) {
            if (availableLower.contains(candidate.toLowerCase())) {
                return candidate;
            }
        }
        return preferredRole;
    }

    /**
     * Opens the invite role picker (if needed) and selects {@code roleName} before the Dismiss
     * overlay can blank Flutter semantics between Cucumber steps.
     */
    private void openAndSelectRole(String roleName) {
        String targetRole = roleName;
        if (isSelectedRoleVisible(driver, targetRole)) {
            return;
        }
        if (isRolePickerOpen(driver)) {
            targetRole = resolveInviteRoleName(roleName);
            if (trySelectRoleFromOpenMenu(targetRole)) {
                return;
            }
        }

        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            if (isSelectedRoleVisible(driver, targetRole) || isSelectedRoleVisible(driver, roleName)) {
                return;
            }
            if (!isInviteFlowActive(driver) && !isRolePickerOpen(driver)) {
                restoreInviteAdminFormIfNeeded();
            }
            if (!isRolePickerOpen(driver)) {
                if (!isInviteFlowActive(driver)) {
                    ensureInviteDialogForRoleSelection();
                }
                openRoleDropdown();
            }
            if (isRolePickerOpen(driver)) {
                targetRole = resolveInviteRoleName(roleName);
                if (trySelectRoleFromOpenMenu(targetRole)) {
                    return;
                }
            }
            // Bad click can dismiss invite via fullscreen overlay — recover and retry.
            if (!isInviteFlowActive(driver) && !isRolePickerOpen(driver)) {
                restoreInviteAdminFormIfNeeded();
            }
        }
        if (isSelectedRoleVisible(driver, targetRole) || isSelectedRoleVisible(driver, roleName)
                || isAnyKnownInviteRoleSelected()) {
            return;
        }
        logRolePickerSemantics();
        throw new NoSuchElementException(
                "Role option not found: " + roleName
                        + " (tried " + targetRole + "; expected semantics " + roleName
                        + " or " + SEMANTICS_ROLE_RAHUL + ")"
                        + ". Available invite role options: " + listAvailableInviteRoleOptions());
    }

    /** Clicks a role option while the Popup menu is open — no a11y enable/reset. */
    private boolean trySelectRoleFromOpenMenu(String roleName) {
        if (!isRolePickerOpen(driver)) {
            return false;
        }
        String legacySemanticsLabel = "Rahul".equalsIgnoreCase(roleName) ? SEMANTICS_ROLE_RAHUL : null;
        for (int attempt = 0; attempt < 4; attempt++) {
            if (!isRolePickerOpen(driver)) {
                return isSelectedRoleVisible(driver, roleName);
            }
            // Prefer canvas CDP — DOM events on flt-semantics rarely select Flutter menu items.
            boolean clicked = clickRoleOptionViaCdp(roleName)
                    || (legacySemanticsLabel != null && clickRoleOptionViaCdp(legacySemanticsLabel))
                    || clickRoleMenuItemDirect(roleName)
                    || (legacySemanticsLabel != null && clickRoleMenuItemDirect(legacySemanticsLabel))
                    || clickRoleOption(roleName, roleName)
                    || (legacySemanticsLabel != null && clickRoleOption(legacySemanticsLabel, roleName))
                    || clickRoleOptionInPopupByText(roleName);
            if (clicked && (waitForRoleSelection(roleName) || isSelectedRoleVisible(driver, roleName))) {
                return true;
            }
            try {
                Thread.sleep(120);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return isSelectedRoleVisible(driver, roleName);
    }

    /** Like StatusFilterSupport.openDropdown — open trigger and wait until menu is open. */
    private void openRoleDropdown() {
        if (isRolePickerOpen(driver)) {
            return;
        }
        for (int attempt = 0; attempt < 8; attempt++) {
            ensureSemanticsAlive(driver);
            if (isRolePickerOpen(driver)) {
                return;
            }
            if (hasCollapsedInviteSummary(driver)
                    && driver.findElements(semanticsLocator(SEMANTICS_ROLE_DROPDOWN)).isEmpty()) {
                clickCollapsedInviteRoleSection();
                semantics.pauseAfterScroll();
            }
            if (!isInviteFlowActive(driver)) {
                restoreInviteAdminFormIfNeeded();
                ensureSemanticsAlive(driver);
            }
            if (!isInviteFlowActive(driver)) {
                continue;
            }
            if (tryOpenRoleDropdown()) {
                semantics.pauseAfterScroll();
                if (waitForRoleMenuOpen()) {
                    return;
                }
            }
            if (driver.findElements(semanticsLocator(SEMANTICS_ROLE_DROPDOWN)).isEmpty()
                    && !pageHasAssignRoleText(driver)
                    && !isInviteFlowActive(driver)) {
                ensureSemanticsAlive(driver);
                if (!isInviteFlowActive(driver) && !pageHasAssignRoleText(driver)) {
                    break;
                }
            }
            semantics.pauseAfterScroll();
        }
        if (isRolePickerOpen(driver) || waitForRoleMenuOpen()) {
            return;
        }
        logRolePickerSemantics();
        throw new NoSuchElementException(
                "Assign Role dropdown did not open; expected semantics " + SEMANTICS_ROLE_DROPDOWN
                        + " then role options (e.g. " + SEMANTICS_ROLE_RAHUL + "). Available: "
                        + listAvailableInviteRoleOptions());
    }

    private boolean waitForRoleMenuOpen() {
        return waitUntil(d -> isRolePickerOpen(d)
                        || isRoleOptionPresent(d, SEMANTICS_ROLE_RAHUL, "Rahul")
                        || hasVisibleRoleMenuOptions(d)
                        || pageHasPopupMenu(d),
                Duration.ofSeconds(8));
    }

    private void ensureSemanticsAlive(WebDriver webDriver) {
        // Never hard-reset while the role popup is open — placeholder toggle blanks Available: [].
        if (isRolePickerOpen(webDriver)) {
            return;
        }
        if (hasUsefulInviteSemantics(webDriver)) {
            return;
        }
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (hasUsefulInviteSemantics(webDriver) || isRolePickerOpen(webDriver)) {
            return;
        }
        int nodeCount = inviteSemanticsNodeCount(webDriver);
        if (nodeCount == 0) {
            semantics.forceEnableFlutterSemantics();
            waitUntil(this::hasUsefulInviteSemantics, Duration.ofSeconds(5));
            if (hasUsefulInviteSemantics(webDriver) || isRolePickerOpen(webDriver)) {
                return;
            }
            // Only hard-reset when invite form is also gone — otherwise keep invite semantics.
            if (!isInviteFlowActive(webDriver)) {
                hardResetInviteSemantics();
                waitUntil(this::hasUsefulInviteSemantics, Duration.ofSeconds(8));
            }
            return;
        }
        // Ghost nodes with no useful labels: hard reset only when not mid invite/picker.
        if (!hasUsefulInviteSemantics(webDriver) && !isInviteFlowActive(webDriver)) {
            hardResetInviteSemantics();
            waitUntil(this::hasUsefulInviteSemantics, Duration.ofSeconds(5));
        }
    }

    /**
     * Local invite-only helpers — keep FlutterSemanticsSupport unchanged so other modules are unaffected.
     */
    private static final String INVITE_USEFUL_SEMANTICS_SCRIPT =
            "const semanticsState = () => {" +
                    "  const host = document.querySelector('flt-semantics-host');" +
                    "  const nodes = host ? Array.from(host.querySelectorAll('flt-semantics')) : [];" +
                    "  const text = ((host && host.textContent) || '').replace(/\\s+/g, ' ').trim();" +
                    "  const useful = nodes.some(node => {" +
                    "    const label = (node.getAttribute('aria-label') || '').trim();" +
                    "    const nodeText = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                    "    return label.length > 1 || nodeText.length > 0;" +
                    "  }) || text.length > 0;" +
                    "  return { count: nodes.length, useful: useful };" +
                    "};";

    private boolean hasUsefulInviteSemantics(WebDriver webDriver) {
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                INVITE_USEFUL_SEMANTICS_SCRIPT + "return semanticsState().useful;"
        ));
    }

    private int inviteSemanticsNodeCount(WebDriver webDriver) {
        Long count = (Long) ((JavascriptExecutor) webDriver).executeScript(
                INVITE_USEFUL_SEMANTICS_SCRIPT + "return semanticsState().count;"
        );
        return count == null ? 0 : count.intValue();
    }

    private void hardResetInviteSemantics() {
        ((JavascriptExecutor) driver).executeScript(
                INVITE_USEFUL_SEMANTICS_SCRIPT +
                        "const state = semanticsState();" +
                        "if (state.useful) return;" +
                        "const p = document.querySelector('flt-semantics-placeholder');" +
                        "if (!p) return;" +
                        "if (state.count > 0) { try { p.click(); } catch (e) {} }" +
                        "try { p.click(); } catch (e) {}"
        );
        semantics.pauseAfterScroll();
    }

    private boolean hasVisibleRoleMenuOptions(WebDriver webDriver) {
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        INVITE_ROLE_OPTION_SCRIPT +
                        "return Array.from(document.querySelectorAll('flt-semantics'))" +
                        "  .some(node => isInviteRoleOptionNode(node));"
        ));
    }

    /**
     * Opens the invite-form role picker.
     * Staging exposes Select role as {@code role=button} with an empty aria-label (no
     * {@code role_dropdown}). Flutter only handles real pointer hits on the canvas /
     * glass pane; DOM events on flt-semantics nodes do not open PopupMenuButton.
     */
    private boolean tryOpenRoleDropdown() {
        if (isRolePickerOpen(driver)) {
            return true;
        }
        // Primary: CDP click through semantics host onto Flutter canvas at Select role.
        if (clickSelectRoleViaCanvasCdp()) {
            if (isRolePickerOpen(driver) || waitUntil(this::isRolePickerOpen, Duration.ofSeconds(6))) {
                return true;
            }
        }
        // Secondary: JS hit with semantics host pointer-events disabled (canvas receives click).
        if (clickSelectRoleThroughCanvas()) {
            if (isRolePickerOpen(driver) || waitUntil(this::isRolePickerOpen, Duration.ofSeconds(5))) {
                return true;
            }
        }
        // Fallbacks that may work when a11y labels are present.
        if (clickSelectRoleButtonInInvite() || clickInviteRoleDropdownViaScript()) {
            if (isRolePickerOpen(driver) || waitUntil(this::isRolePickerOpen, Duration.ofSeconds(4))) {
                return true;
            }
        }
        if (activateSelectRoleWithKeyboard()) {
            if (isRolePickerOpen(driver) || waitUntil(this::isRolePickerOpen, Duration.ofSeconds(4))) {
                return true;
            }
        }
        return waitForRoleMenuOpen();
    }

    /** Resolves Select role / role_dropdown button center for canvas hits. */
    private Map<String, Double> findSelectRoleClickPoint() {
        Object coords = ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const target = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (label === 'dismiss' || label.includes('dismiss')) return false;" +
                        "  const role = (node.getAttribute('role') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "  if (label === 'role_dropdown' || label.includes('role_dropdown')) return true;" +
                        "  return (role === 'button' || node.hasAttribute('flt-tappable'))" +
                        "    && text === 'select role';" +
                        "});" +
                        "if (!target) return null;" +
                        "target.scrollIntoView({block:'center', inline:'nearest'});" +
                        "const rect = target.getBoundingClientRect();" +
                        "return {" +
                        "  x: Math.round(rect.left + Math.min(rect.width - 8, Math.max(8, rect.width * 0.88)))," +
                        "  y: Math.round(rect.top + rect.height / 2)" +
                        "};"
        );
        if (!(coords instanceof Map<?, ?> point)) {
            return null;
        }
        Object xObj = point.get("x");
        Object yObj = point.get("y");
        if (!(xObj instanceof Number) || !(yObj instanceof Number)) {
            return null;
        }
        Map<String, Double> result = new HashMap<>();
        result.put("x", ((Number) xObj).doubleValue());
        result.put("y", ((Number) yObj).doubleValue());
        return result;
    }

    /**
     * Flutter Web routes gestures through the glass pane/canvas. Temporarily disable
     * pointer events on the semantics host so CDP/JS clicks reach Flutter hit-testing
     * at the Select role coordinates (inside the dialog, not the page behind).
     */
    private boolean clickFlutterCanvasAt(double x, double y) {
        ((JavascriptExecutor) driver).executeScript(
                "const host = document.querySelector('flt-semantics-host');" +
                        "if (host) { host.setAttribute('data-vi-pe', host.style.pointerEvents || '');" +
                        "  host.style.pointerEvents = 'none'; }"
        );
        try {
            boolean viaCdp = dispatchCdpClick(x, y);
            if (viaCdp) {
                return true;
            }
            return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                    "const x = arguments[0], y = arguments[1];" +
                            "const hit = document.elementFromPoint(x, y);" +
                            "if (!hit) return false;" +
                            "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                            "  hit.dispatchEvent(new MouseEvent(type, {" +
                            "    bubbles: true, cancelable: true, clientX: x, clientY: y, view: window" +
                            "  }));" +
                            "});" +
                            "try { hit.click(); } catch (e) {}" +
                            "return true;",
                    x, y
            ));
        } finally {
            ((JavascriptExecutor) driver).executeScript(
                    "const host = document.querySelector('flt-semantics-host');" +
                            "if (!host) return;" +
                            "const prev = host.getAttribute('data-vi-pe');" +
                            "host.removeAttribute('data-vi-pe');" +
                            "if (prev === null || prev === '') host.style.removeProperty('pointer-events');" +
                            "else host.style.pointerEvents = prev;"
            );
        }
    }

    private boolean clickSelectRoleViaCanvasCdp() {
        Map<String, Double> point = findSelectRoleClickPoint();
        if (point == null) {
            return false;
        }
        return clickFlutterCanvasAt(point.get("x"), point.get("y"));
    }

    private boolean clickSelectRoleThroughCanvas() {
        return clickSelectRoleViaCanvasCdp();
    }

    private boolean clickSelectRoleViaCdp() {
        return clickSelectRoleViaCanvasCdp();
    }

    private boolean clickRoleOptionViaCdp(String roleName) {
        Object coords = ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        INVITE_ROLE_OPTION_SCRIPT +
                        "const wanted = (arguments[0] || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "const match = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  if (label.includes('admin_row') || label === 'dismiss') return false;" +
                        "  if (!(label === wanted || text === wanted)) return false;" +
                        "  return isInviteRoleOptionNode(node)" +
                        "    || ((hasInvitePopupMenu() || hasUniqueInviteRoleOptionCluster())" +
                        "        && (label === wanted || text === wanted));" +
                        "});" +
                        "if (!match) return null;" +
                        "match.scrollIntoView({block:'nearest', inline:'nearest'});" +
                        "const rect = match.getBoundingClientRect();" +
                        "if (rect.width <= 0 || rect.height <= 0) return null;" +
                        "return { x: Math.round(rect.left + rect.width / 2), y: Math.round(rect.top + rect.height / 2) };",
                roleName
        );
        if (!(coords instanceof Map<?, ?> point)) {
            return false;
        }
        Object xObj = point.get("x");
        Object yObj = point.get("y");
        if (!(xObj instanceof Number) || !(yObj instanceof Number)) {
            return false;
        }
        return clickFlutterCanvasAt(((Number) xObj).doubleValue(), ((Number) yObj).doubleValue());
    }

    private boolean dispatchCdpClick(double x, double y) {
        if (!(driver instanceof ChromeDriver chromeDriver)) {
            return false;
        }
        try {
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

    /**
     * Clicks the invite-form "Select role" control (role=button, no aria-label on staging).
     * Disable Dismiss hit-testing first, then click via elementFromPoint so Flutter's
     * underlying gesture detector receives a real hit (synthetic events on semantics
     * nodes alone often do not open the menu in headless Chrome).
     */
    private boolean clickSelectRoleButtonInInvite() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const dismissNodes = Array.from(document.querySelectorAll('flt-semantics')).filter(n => {" +
                        "  const label = (n.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return label === 'dismiss' || label.includes('dismiss');" +
                        "});" +
                        "const prev = dismissNodes.map(n => n.style.pointerEvents);" +
                        "dismissNodes.forEach(n => { n.style.pointerEvents = 'none'; });" +
                        "try {" +
                        "  const pageText = (document.querySelector('flt-semantics-host')?.textContent || '');" +
                        "  if (!pageText.includes('Invite New Admin') && !pageText.includes('Assign Role')" +
                        "      && !pageText.includes('Select role')) return false;" +
                        // Never open via 'Assign Role' label — only Select role / role_dropdown.
                        "  const semanticsTarget = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "    if (isInNav(node)) return false;" +
                        "    const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "    if (label === 'dismiss' || label.includes('dismiss')) return false;" +
                        "    const role = (node.getAttribute('role') || '').toLowerCase();" +
                        "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "    const rect = node.getBoundingClientRect();" +
                        "    if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "    if (label === 'role_dropdown' || label.includes('role_dropdown')) return true;" +
                        "    return (role === 'button' || node.hasAttribute('flt-tappable'))" +
                        "      && text === 'select role';" +
                        "  });" +
                        "  if (!semanticsTarget) return false;" +
                        "  semanticsTarget.scrollIntoView({block:'center', inline:'nearest'});" +
                        "  try { semanticsTarget.focus(); } catch (e) {}" +
                        "  const host = document.querySelector('flt-semantics-host');" +
                        "  const hostPrev = host ? host.style.pointerEvents : null;" +
                        "  if (host) host.style.pointerEvents = 'none';" +
                        "  try {" +
                        "    const rect = semanticsTarget.getBoundingClientRect();" +
                        "    const x = rect.left + Math.min(rect.width - 8, Math.max(8, rect.width * 0.88));" +
                        "    const y = rect.top + rect.height / 2;" +
                        "    const hit = document.elementFromPoint(x, y) || semanticsTarget;" +
                        "    ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "      hit.dispatchEvent(new MouseEvent(type, {" +
                        "        bubbles: true, cancelable: true, clientX: x, clientY: y, view: window" +
                        "      }));" +
                        "    });" +
                        "    try { hit.click(); } catch (e) {}" +
                        "    return true;" +
                        "  } finally {" +
                        "    if (host) {" +
                        "      if (hostPrev === null || hostPrev === '') host.style.removeProperty('pointer-events');" +
                        "      else host.style.pointerEvents = hostPrev;" +
                        "    }" +
                        "  }" +
                        "} finally {" +
                        "  dismissNodes.forEach((n, i) => {" +
                        "    if (prev[i] === null || prev[i] === '') n.style.removeProperty('pointer-events');" +
                        "    else n.style.pointerEvents = prev[i];" +
                        "  });" +
                        "}"
        ));
    }

    private boolean activateSelectRoleWithKeyboard() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const dismissNodes = Array.from(document.querySelectorAll('flt-semantics')).filter(n => {" +
                        "  const label = (n.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return label === 'dismiss' || label.includes('dismiss');" +
                        "});" +
                        "const prev = dismissNodes.map(n => n.style.pointerEvents);" +
                        "dismissNodes.forEach(n => { n.style.pointerEvents = 'none'; });" +
                        "try {" +
                        "  const target = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "    if (isInNav(node)) return false;" +
                        "    const role = (node.getAttribute('role') || '').toLowerCase();" +
                        "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "    const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "    if (label === 'dismiss' || label.includes('dismiss')) return false;" +
                        "    return (role === 'button' && text === 'Select role')" +
                        "      || label === 'role_dropdown';" +
                        "  });" +
                        "  if (!target) return false;" +
                        "  try { target.focus(); } catch (e) {}" +
                        "  target.dispatchEvent(new KeyboardEvent('keydown', {key:' ', code:'Space', bubbles:true}));" +
                        "  target.dispatchEvent(new KeyboardEvent('keyup', {key:' ', code:'Space', bubbles:true}));" +
                        "  target.dispatchEvent(new KeyboardEvent('keydown', {key:'ArrowDown', code:'ArrowDown', bubbles:true}));" +
                        "  target.dispatchEvent(new KeyboardEvent('keyup', {key:'ArrowDown', code:'ArrowDown', bubbles:true}));" +
                        "  target.dispatchEvent(new KeyboardEvent('keydown', {key:'Enter', code:'Enter', bubbles:true}));" +
                        "  target.dispatchEvent(new KeyboardEvent('keyup', {key:'Enter', code:'Enter', bubbles:true}));" +
                        "  return true;" +
                        "} finally {" +
                        "  dismissNodes.forEach((n, i) => {" +
                        "    if (prev[i] === null || prev[i] === '') n.style.removeProperty('pointer-events');" +
                        "    else n.style.pointerEvents = prev[i];" +
                        "  });" +
                        "}"
        ));
    }

    /** Opens role_dropdown by dispatching pointer events on the node itself (never elementFromPoint). */
    private boolean clickRoleDropdownViaTargetOnly() {
        List<WebElement> dropdowns = driver.findElements(semanticsLocator(SEMANTICS_ROLE_DROPDOWN));
        if (dropdowns.isEmpty()) {
            return false;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const node = arguments[0];" +
                        "const dismissNodes = Array.from(document.querySelectorAll('flt-semantics')).filter(n => {" +
                        "  const label = (n.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return label === 'dismiss' || label.includes('dismiss');" +
                        "});" +
                        "const prev = dismissNodes.map(n => n.style.pointerEvents);" +
                        "dismissNodes.forEach(n => { n.style.pointerEvents = 'none'; });" +
                        "try {" +
                        "  const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "  target.scrollIntoView({block:'center', inline:'nearest'});" +
                        "  const rect = target.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "  const x = rect.left + Math.min(rect.width - 8, Math.max(8, rect.width * 0.85));" +
                        "  const y = rect.top + rect.height / 2;" +
                        "  ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "    target.dispatchEvent(new MouseEvent(type, {" +
                        "      bubbles: true, cancelable: true, clientX: x, clientY: y, view: window" +
                        "    }));" +
                        "  });" +
                        "  try { target.click(); } catch (e) {}" +
                        "  return true;" +
                        "} finally {" +
                        "  dismissNodes.forEach((n, i) => {" +
                        "    if (prev[i] === null || prev[i] === '') n.style.removeProperty('pointer-events');" +
                        "    else n.style.pointerEvents = prev[i];" +
                        "  });" +
                        "}",
                dropdowns.get(0)
        ));
    }

    private boolean pageHasPopupMenu(WebDriver webDriver) {
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').replace(/\\s+/g, ' ').trim();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const role = (node.getAttribute('role') || '').toLowerCase();" +
                        "  return label === 'Popup menu' || text === 'Popup menu' || role === 'menu';" +
                        "});"
        ));
    }

    /** Clicks invite-form text via real pointer on a compact semantics node (avoids Dismiss). */
    private boolean clickVisibleInviteText(String visibleText) {
        WebElement target = findCompactInviteTextNode(visibleText);
        if (target == null) {
            return clickVisibleInviteTextViaScript(visibleText);
        }
        semantics.scrollIntoView(target);
        try {
            int width = Math.max(target.getSize().getWidth(), 1);
            // Click near the trailing chevron of the dropdown field.
            new org.openqa.selenium.interactions.Actions(driver)
                    .moveToElement(target, Math.max(width / 2 - 8, 1), 0)
                    .pause(Duration.ofMillis(150))
                    .click()
                    .perform();
            return true;
        } catch (Exception ignored) {
            try {
                new org.openqa.selenium.interactions.Actions(driver)
                        .moveToElement(target)
                        .click()
                        .perform();
                return true;
            } catch (Exception ignoredAgain) {
                try {
                    target.click();
                    return true;
                } catch (Exception ignoredThird) {
                    return clickVisibleInviteTextViaScript(visibleText);
                }
            }
        }
    }

    private WebElement findCompactInviteTextNode(String visibleText) {
        return (WebElement) ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const wanted = (arguments[0] || '').replace(/\\s+/g, ' ').trim();" +
                        "const isDismiss = (node) => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return label === 'dismiss' || label.includes('dismiss');" +
                        "};" +
                        "const matches = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  if (isInNav(node) || isDismiss(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  if (text !== wanted) return false;" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  return rect.width > 0 && rect.height > 0 && rect.width < 480 && rect.height < 80;" +
                        "});" +
                        "matches.sort((a, b) => {" +
                        "  const ra = a.getBoundingClientRect();" +
                        "  const rb = b.getBoundingClientRect();" +
                        "  return (ra.width * ra.height) - (rb.width * rb.height);" +
                        "});" +
                        "return matches.length ? matches[0] : null;",
                visibleText
        );
    }

    private boolean clickVisibleInviteTextViaScript(String visibleText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        ROLE_PICKER_SCOPE_SCRIPT +
                        "const wanted = (arguments[0] || '').replace(/\\s+/g, ' ').trim();" +
                        "const pageText = (document.querySelector('flt-semantics-host')?.textContent || '')" +
                        "  .replace(/\\s+/g, ' ').trim();" +
                        "const inviteOpen = pageText.includes('Invite New Admin')" +
                        "  || pageText.includes('Email Address')" +
                        "  || pageText.includes('Assign Role')" +
                        "  || pageText.includes('Select role')" +
                        "  || (pageText.includes('Name:') && pageText.includes('Email:') && pageText.includes('Role:'));" +
                        "if (!inviteOpen) return false;" +
                        "const isDismiss = (node) => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return label === 'dismiss' || label.includes('dismiss');" +
                        "};" +
                        "const matches = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  if (isInNav(node) || isDismiss(node) || !isInsideInviteForm(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return text === wanted;" +
                        "});" +
                        "let best = null;" +
                        "let bestArea = Number.POSITIVE_INFINITY;" +
                        "for (const match of matches) {" +
                        "  const rect = match.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) continue;" +
                        "  const area = rect.width * rect.height;" +
                        "  if (area < bestArea) { best = match; bestArea = area; }" +
                        "}" +
                        "if (!best) return false;" +
                        "let clickable = best;" +
                        "if (!best.hasAttribute('flt-tappable')) {" +
                        "  let parent = best.parentElement;" +
                        "  while (parent) {" +
                        "    if (parent.matches && parent.matches('flt-semantics[flt-tappable]') && !isDismiss(parent)) {" +
                        "      clickable = parent; break;" +
                        "    }" +
                        "    if (parent.matches && parent.matches('flt-semantics') && isDismiss(parent)) break;" +
                        "    parent = parent.parentElement;" +
                        "  }" +
                        "}" +
                        "clickable.scrollIntoView({block:'center', inline:'nearest'});" +
                        "const rect = clickable.getBoundingClientRect();" +
                        "if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "const x = rect.left + rect.width / 2;" +
                        "const y = rect.top + rect.height / 2;" +
                        "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "  clickable.dispatchEvent(new MouseEvent(type, {" +
                        "    bubbles: true, cancelable: true, clientX: x, clientY: y, view: window" +
                        "  }));" +
                        "});" +
                        "try { clickable.click(); return true; } catch (e) {" +
                        "  try { best.click(); return true; } catch (e2) { return false; }" +
                        "}",
                visibleText
        ));
    }

    private void ensureInviteDialogForRoleSelection() {
        semantics.enableFlutterSemantics();
        if (isInviteFlowActive(driver) || isRolePickerOpen(driver)) {
            return;
        }
        restoreInviteAdminFormIfNeeded();
        if (isInviteFlowActive(driver) || isRolePickerOpen(driver)) {
            return;
        }
        throw new NoSuchElementException(
                "Invite Admin dialog is not open before role selection");
    }

    private String listAvailableInviteRoleOptions() {
        Object roles = ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        INVITE_ROLE_OPTION_SCRIPT +
                        "const popupOpen = hasInvitePopupMenu();" +
                        "return Array.from(document.querySelectorAll('flt-semantics'))" +
                        "  .filter(node => {" +
                        "    if (isInNav(node)) return false;" +
                        "    const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "    const role = (node.getAttribute('role') || '').toLowerCase();" +
                        "    if (label === 'role_dropdown' || label.includes('role_dropdown')" +
                        "        || role === 'menu' || label === 'popup menu') return true;" +
                        "    return isInviteRoleOptionNode(node);" +
                        "  })" +
                        "  .map(node => {" +
                        "    const label = node.getAttribute('aria-label') || '';" +
                        "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "    const role = node.getAttribute('role') || '';" +
                        "    return (label || text) + (role ? '[' + role + ']' : '')" +
                        "      + (text && text !== label ? ':' + text : '');" +
                        "  })" +
                        "  .filter(Boolean)" +
                        "  .concat(popupOpen ? ['<popup-open>'] : ['<popup-closed>']);"
        );
        return roles == null ? "[]" : roles.toString();
    }

    public boolean isSelectedRoleDisplayed(String roleName) {
        return isSelectedRoleVisible(driver, roleName);
    }

    /** True when any known invite role replaced the Select role placeholder (CI staging may omit Rahul). */
    public boolean isAnyInviteRoleApplied() {
        return isAnyKnownInviteRoleSelected() || isInviteAccessTypeReady();
    }

    public boolean isInviteAccessTypeReady() {
        // Only treat access type as ready after a role has replaced the Select role placeholder.
        return isInviteAccessTypeSectionVisible(driver)
                && !pageHasSelectRolePlaceholder(driver);
    }

    private boolean pageHasSelectRolePlaceholder(WebDriver webDriver) {
        String text = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        return text.contains(semantics.normalizeText(SELECT_ROLE_TEXT));
    }

    private boolean waitForRoleSelection(String roleName) {
        return waitUntil(d -> isSelectedRoleVisible(d, roleName) && !hasInvitePopupMenuOpen(d),
                Duration.ofSeconds(8));
    }

    private boolean hasInvitePopupMenuOpen(WebDriver webDriver) {
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                INVITE_ROLE_OPTION_SCRIPT + "return hasInvitePopupMenu();"
        ));
    }

    private boolean isSelectedRoleVisible(WebDriver webDriver, String roleName) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (!isInviteAdminFormLoaded(webDriver) && !hasCollapsedInviteSummary(webDriver)) {
            return false;
        }
        String wanted = semantics.normalizeText(roleName);
        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        boolean inviteContext = pageText.contains(semantics.normalizeText(INVITE_NEW_ADMIN_TEXT))
                || pageText.contains(semantics.normalizeText(EMAIL_ADDRESS_TEXT))
                || pageText.contains(semantics.normalizeText(ASSIGN_ROLE_TEXT))
                || (pageText.contains("name:") && pageText.contains("email:") && pageText.contains("role:"));
        if (!inviteContext) {
            return false;
        }
        if (pageText.contains(wanted)
                && (pageText.contains("role:")
                || pageText.contains(semantics.normalizeText(ASSIGN_ROLE_TEXT)))
                && !pageText.contains(semantics.normalizeText(SELECT_ROLE_TEXT))) {
            return true;
        }
        if (pageText.contains(wanted) && pageText.contains(semantics.normalizeText(ASSIGN_ROLE_TEXT))) {
            // Compact invite form: "Assign Role" + selected role name, no "Select role" placeholder.
            return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                    FIND_OUTSIDE_NAV_SCRIPT +
                            ROLE_PICKER_SCOPE_SCRIPT +
                            "const wanted = (arguments[0] || '').replace(/\\s+/g, ' ').trim();" +
                            "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                            "  if (isInNav(node) || !isInsideInviteForm(node)) return false;" +
                            "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                            "  return text === wanted;" +
                            "});",
                    roleName
            ));
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        ROLE_PICKER_SCOPE_SCRIPT +
                        "const wanted = (arguments[0] || '').toLowerCase();" +
                        "const roleDropdown = findOutsideNav('flt-semantics[aria-label=\"role_dropdown\"]');" +
                        "if (roleDropdown) {" +
                        "  const dropdownText = (roleDropdown.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  if (dropdownText.includes(wanted)) return true;" +
                        "}" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  if (isInNav(node) || !isInsideInviteForm(node)) return false;" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  if (label === 'role_name_option_role_name' || label.includes('role_name_option')) {" +
                        "    return text === wanted || label.includes(wanted);" +
                        "  }" +
                        "  return false;" +
                        "});",
                roleName
        ));
    }

    private boolean clickRoleOption(String semanticsLabel, String roleName) {
        // Role popup sits under a fullscreen flt-semantics[aria-label=Dismiss]. Selenium
        // Actions/elementFromPoint hit Dismiss; click the menuitem node via JS only.
        if (clickRoleMenuItemDirect(roleName) || clickRoleMenuItemDirect(semanticsLabel)) {
            return true;
        }
        if (clickRoleOptionBySemanticsAndText(semanticsLabel, roleName)) {
            return true;
        }
        if (clickRoleOptionInPopupByText(roleName)) {
            return true;
        }
        if (semanticsLabel != null && !semanticsLabel.equals(roleName)
                && clickRoleOptionBySemanticsAndText(SEMANTICS_ROLE_RAHUL, roleName)) {
            return true;
        }
        return clickRoleOptionViaScript(semanticsLabel, roleName);
    }

    /** Clicks invite role option by aria-label or visible text, including Flutter role=cell rows. */
    private boolean clickRoleMenuItemDirect(String roleLabel) {
        if (roleLabel == null || roleLabel.isBlank()) {
            return false;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        INVITE_ROLE_OPTION_SCRIPT +
                        "const wanted = (arguments[0] || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "const dismissNodes = Array.from(document.querySelectorAll('flt-semantics')).filter(n => {" +
                        "  const label = (n.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return label === 'dismiss' || label.includes('dismiss');" +
                        "});" +
                        "const prev = dismissNodes.map(n => n.style.pointerEvents);" +
                        "dismissNodes.forEach(n => { n.style.pointerEvents = 'none'; });" +
                        "try {" +
                        "  const popupOpen = hasInvitePopupMenu();" +
                        "  const candidates = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "    if (isInNav(node)) return false;" +
                        "    const label = (node.getAttribute('aria-label') || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "    const role = (node.getAttribute('role') || '').toLowerCase();" +
                        "    if (label === 'dismiss' || label.includes('dismiss') || label.includes('admin_row')) return false;" +
                        "    const labelMatch = label === wanted"
                        + " || (label.includes('role_name_option') && text === wanted);" +
                        "    const textMatch = text === wanted;" +
                        "    if (!(labelMatch || textMatch)) return false;" +
                        "    if (isInviteRoleOptionNode(node)) return true;" +
                        // When Popup is open, also accept matching cell/option rows even if role is blank.
                        "    if (popupOpen && (role === 'cell' || role === 'option' || role === 'listitem'" +
                        "        || role === 'menuitem' || node.hasAttribute('flt-tappable'))) {" +
                        "      return isUnderInvitePopup(node) || role === 'menuitem';" +
                        "    }" +
                        "    return false;" +
                        "  });" +
                        "  candidates.sort((a, b) => {" +
                        "    const score = (n) => {" +
                        "      const role = (n.getAttribute('role') || '').toLowerCase();" +
                        "      const label = (n.getAttribute('aria-label') || '').toLowerCase();" +
                        "      let s = 0;" +
                        "      if (role === 'menuitem') s += 4;" +
                        "      if (isUnderInvitePopup(n)) s += 3;" +
                        "      if (label === wanted) s += 2;" +
                        "      if (n.hasAttribute('flt-tappable')) s += 1;" +
                        "      if (role === 'cell') s += 1;" +
                        "      return s;" +
                        "    };" +
                        "    return score(b) - score(a);" +
                        "  });" +
                        "  const match = candidates[0];" +
                        "  if (!match) return false;" +
                        "  const target = (match.hasAttribute('flt-tappable') ? match" +
                        "    : (match.querySelector('flt-semantics[flt-tappable]') || match));" +
                        "  target.scrollIntoView({block:'nearest', inline:'nearest'});" +
                        "  const rect = target.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "  const x = rect.left + rect.width / 2;" +
                        "  const y = rect.top + rect.height / 2;" +
                        "  const host = document.querySelector('flt-semantics-host');" +
                        "  const hostPrev = host ? host.style.pointerEvents : null;" +
                        "  if (host) host.style.pointerEvents = 'none';" +
                        "  try {" +
                        "    const hit = document.elementFromPoint(x, y) || target;" +
                        "    ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "      hit.dispatchEvent(new MouseEvent(type, {" +
                        "        bubbles: true, cancelable: true, clientX: x, clientY: y, view: window" +
                        "      }));" +
                        "    });" +
                        "    try { hit.click(); } catch (e) {}" +
                        "  } finally {" +
                        "    if (host) {" +
                        "      if (hostPrev === null || hostPrev === '') host.style.removeProperty('pointer-events');" +
                        "      else host.style.pointerEvents = hostPrev;" +
                        "    }" +
                        "  }" +
                        "  return true;" +
                        "} finally {" +
                        "  dismissNodes.forEach((n, i) => {" +
                        "    if (prev[i] === null || prev[i] === '') n.style.removeProperty('pointer-events');" +
                        "    else n.style.pointerEvents = prev[i];" +
                        "  });" +
                        "}",
                roleLabel
        ));
    }

    private boolean clickSelectRoleFieldInInvite() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const pageText = (document.querySelector('flt-semantics-host')?.textContent || '');" +
                        "if (!pageText.includes('Invite New Admin') && !pageText.includes('Invite Admin')) return false;" +
                        "const targets = ['Select role', 'Assign Role'];" +
                        "for (const wanted of targets) {" +
                        "  const matches = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "    if (isInNav(node)) return false;" +
                        "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "    return text === wanted;" +
                        "  });" +
                        "  let best = null;" +
                        "  let bestLen = Number.POSITIVE_INFINITY;" +
                        "  for (const match of matches) {" +
                        "    const len = (match.textContent || '').length;" +
                        "    if (len < bestLen) { best = match; bestLen = len; }" +
                        "  }" +
                        "  if (!best) continue;" +
                        "  const clickable = best.closest('flt-semantics[flt-tappable]') || best;" +
                        "  const rect = clickable.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) continue;" +
                        "  const x = rect.left + rect.width / 2;" +
                        "  const y = rect.top + rect.height / 2;" +
                        "  ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "    clickable.dispatchEvent(new MouseEvent(type, {" +
                        "      bubbles: true, cancelable: true, clientX: x, clientY: y, view: window" +
                        "    }));" +
                        "  });" +
                        "  try { clickable.click(); } catch (e) {}" +
                        "  return true;" +
                        "}" +
                        "return false;"
        ));
    }

    private boolean isRoleOptionPresent(WebDriver webDriver, String semanticsLabel, String roleName) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (semanticsLabel != null
                && !webDriver.findElements(semanticsLocator(semanticsLabel)).isEmpty()) {
            return true;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const semanticsLabel = (arguments[0] || '').trim().toLowerCase();" +
                        "const wanted = (arguments[1] || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const label = (node.getAttribute('aria-label') || '').trim();" +
                        "  const lower = label.toLowerCase();" +
                        "  const role = (node.getAttribute('role') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  if (lower.includes('admin_row') || lower === 'dismiss') return false;" +
                        "  if (semanticsLabel && lower === semanticsLabel) return true;" +
                        "  if (wanted && (lower === wanted || text === wanted) && (role === 'menuitem' || lower.includes('role'))) {" +
                        "    return true;" +
                        "  }" +
                        "  if (lower.includes('role_name_option') && (!wanted || text === wanted || text.includes(wanted) || lower.includes(wanted))) {" +
                        "    return true;" +
                        "  }" +
                        "  return role === 'menuitem' && wanted && lower === wanted;" +
                        "});",
                semanticsLabel,
                roleName
        ));
    }

    private boolean clickRoleOptionBySemanticsAndText(String semanticsLabel, String roleName) {
        if (semanticsLabel == null || roleName == null || roleName.isBlank()) {
            return false;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        ROLE_PICKER_SCOPE_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "const semanticsLabel = (arguments[0] || '').toLowerCase();" +
                        "const wanted = (arguments[1] || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "const popup = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').replace(/\\s+/g, ' ').trim();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const role = (node.getAttribute('role') || '').toLowerCase();" +
                        "  return label === 'Popup menu' || text === 'Popup menu' || role === 'menu';" +
                        "});" +
                        "const scope = rolePickerScope();" +
                        "const nodes = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const role = (node.getAttribute('role') || '').toLowerCase();" +
                        "  if (label.includes('admin_row') || label === 'dismiss') return false;" +
                        "  return label === semanticsLabel || label.includes('role_name_option')" +
                        "    || (role === 'menuitem' && (label === semanticsLabel || label === wanted));" +
                        "});" +
                        "const preferText = nodes.filter(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  return label === wanted || text === wanted || text.includes(wanted);" +
                        "});" +
                        "const candidates = preferText.length > 0 ? preferText : nodes.filter(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return label === semanticsLabel;" +
                        "});" +
                        "for (const match of candidates) {" +
                        "  if (dispatchPointerClick(match.closest('flt-semantics[flt-tappable]') || match)) {" +
                        "    return true;" +
                        "  }" +
                        "}" +
                        "return false;",
                semanticsLabel,
                roleName
        ));
    }

    private boolean clickRoleOptionInPopupByText(String roleName) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        ROLE_PICKER_SCOPE_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "const wanted = (arguments[0] || '').replace(/\\s+/g, ' ').trim();" +
                        "const wantedLower = wanted.toLowerCase();" +
                        "const popup = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').replace(/\\s+/g, ' ').trim();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const role = (node.getAttribute('role') || '').toLowerCase();" +
                        "  return label === 'Popup menu' || text === 'Popup menu' || role === 'menu';" +
                        "});" +
                        "const scope = rolePickerScope();" +
                        "const nodes = popup ? Array.from((popup.parentElement || popup).querySelectorAll('flt-semantics'))" +
                        "  : (scope ? Array.from(scope.querySelectorAll('flt-semantics'))" +
                        "  : Array.from(document.querySelectorAll('flt-semantics')));" +
                        "const matches = nodes.filter(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const label = (node.getAttribute('aria-label') || '').replace(/\\s+/g, ' ').trim();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const role = (node.getAttribute('role') || '').toLowerCase();" +
                        "  if (label.toLowerCase() === 'dismiss') return false;" +
                        "  return label === wanted || text === wanted" +
                        "    || (role === 'menuitem' && label.toLowerCase() === wantedLower);" +
                        "});" +
                        "for (const match of matches) {" +
                        "  if (dispatchPointerClick(match.closest('flt-semantics[flt-tappable]') || match)) {" +
                        "    return true;" +
                        "  }" +
                        "}" +
                        "return false;",
                roleName
        ));
    }

    private boolean clickRoleDropdown() {
        scrollToSemanticsLabel(SEMANTICS_ROLE_DROPDOWN);
        List<WebElement> dropdowns = driver.findElements(semanticsLocator(SEMANTICS_ROLE_DROPDOWN));
        for (WebElement dropdown : dropdowns) {
            semantics.scrollIntoView(dropdown);
            try {
                new org.openqa.selenium.interactions.Actions(driver)
                        .moveToElement(dropdown)
                        .pause(Duration.ofMillis(200))
                        .click()
                        .perform();
                semantics.pauseAfterScroll();
                return true;
            } catch (Exception ignored) {
                if (semantics.clickElementReliably(dropdown)) {
                    semantics.pauseAfterScroll();
                    return true;
                }
            }
        }
        if (clickRoleDropdownByCoordinates()) {
            return true;
        }
        if (semantics.clickSemanticsLabelWithoutEnabling(SEMANTICS_ROLE_DROPDOWN)) {
            return true;
        }
        if (clickInviteRoleDropdownViaScript()) {
            return true;
        }
        return semantics.clickSemanticsLabelViaScript(SEMANTICS_ROLE_DROPDOWN);
    }

    private boolean clickRoleDropdownByCoordinates() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const roleDropdown = findOutsideNav('flt-semantics[aria-label=\"role_dropdown\"]');" +
                        "const selectRole = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  return (node.textContent || '').replace(/\\s+/g, ' ').trim() === 'Select role';" +
                        "});" +
                        "const target = roleDropdown || selectRole;" +
                        "if (!target) return false;" +
                        "const rect = target.getBoundingClientRect();" +
                        "if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "const x = rect.left + rect.width / 2;" +
                        "const y = rect.top + rect.height / 2;" +
                        "const hit = document.elementFromPoint(x, y) || target;" +
                        "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "  hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                        "});" +
                        "return true;"
        ));
    }

    private boolean openRoleDropdownWithKeyboard() {
        List<WebElement> dropdowns = driver.findElements(semanticsLocator(SEMANTICS_ROLE_DROPDOWN));
        if (dropdowns.isEmpty()) {
            return false;
        }
        WebElement dropdown = dropdowns.get(0);
        semantics.scrollIntoView(dropdown);
        try {
            if (!semantics.clickElementReliably(dropdown)) {
                return false;
            }
            dropdown.sendKeys(Keys.ARROW_DOWN);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean clickInviteRoleDropdownViaScript() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const pageText = (document.querySelector('flt-semantics-host')?.textContent || '');" +
                        "if (!pageText.includes('Invite New Admin') && !pageText.includes('Invite Admin')" +
                        "    && !pageText.includes('Assign Role') && !pageText.includes('Select role')) {" +
                        "  return false;" +
                        "}" +
                        "const dismissNodes = Array.from(document.querySelectorAll('flt-semantics')).filter(n => {" +
                        "  const label = (n.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return label === 'dismiss' || label.includes('dismiss');" +
                        "});" +
                        "const prev = dismissNodes.map(n => n.style.pointerEvents);" +
                        "dismissNodes.forEach(n => { n.style.pointerEvents = 'none'; });" +
                        "try {" +
                        "  const roleDropdown = findOutsideNav('flt-semantics[aria-label=\"role_dropdown\"]');" +
                        "  const selectRole = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "    if (isInNav(node)) return false;" +
                        "    return (node.textContent || '').replace(/\\s+/g, ' ').trim() === 'Select role';" +
                        "  });" +
                        "  const target = (roleDropdown || selectRole);" +
                        "  if (!target) return false;" +
                        "  const clickable = target.closest('flt-semantics[flt-tappable]') || target;" +
                        "  clickable.scrollIntoView({block: 'center', inline: 'nearest'});" +
                        "  const rect = clickable.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "  try { clickable.focus(); } catch (e) {}" +
                        "  const x = rect.left + Math.min(rect.width - 8, Math.max(8, rect.width * 0.85));" +
                        "  const y = rect.top + rect.height / 2;" +
                        "  ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "    clickable.dispatchEvent(new MouseEvent(type, {" +
                        "      bubbles: true, cancelable: true, clientX: x, clientY: y, view: window" +
                        "    }));" +
                        "  });" +
                        "  try { clickable.click(); } catch (e) {}" +
                        "  return true;" +
                        "} finally {" +
                        "  dismissNodes.forEach((n, i) => {" +
                        "    if (prev[i] === null || prev[i] === '') n.style.removeProperty('pointer-events');" +
                        "    else n.style.pointerEvents = prev[i];" +
                        "  });" +
                        "}"
        ));
    }

    public void clickPermanentAccessType() {
        if (!isInviteFlowActive(driver)) {
            throw new NoSuchElementException(
                    "Invite Admin dialog is not open before selecting Permanent access");
        }
        semantics.enableFlutterSemantics();
        scrollToSemanticsLabel(SEMANTICS_PERMANENT_ACCESS);
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            waitUntil(this::isInviteAccessTypeSectionVisible, Duration.ofSeconds(5));
            if (tryClickInviteFormAction(SEMANTICS_PERMANENT_ACCESS, PERMANENT_TEXT, PERMANENT_ACCESS_TEXT)) {
                return;
            }
            if (clickInviteFormActionViaScript(SEMANTICS_PERMANENT_ACCESS, PERMANENT_TEXT, PERMANENT_ACCESS_TEXT)) {
                return;
            }
            // Do not use unscoped "Permanent" clicks — table rows also contain Permanent.
            if (clickVisibleInviteTextViaScript(PERMANENT_ACCESS_TEXT)
                    || clickVisibleInviteTextViaScript(PERMANENT_TEXT)) {
                return;
            }
            scrollToSemanticsLabel(SEMANTICS_PERMANENT_ACCESS);
            semantics.pauseAfterScroll();
        }
        logInviteAccessSemantics();
        throw new NoSuchElementException(
                "Action not found: " + PERMANENT_TEXT
                        + " (expected semantics " + SEMANTICS_PERMANENT_ACCESS + ")");
    }

    public void clickTemporaryAccessType() {
        if (!isInviteFlowActive(driver)) {
            throw new NoSuchElementException(
                    "Invite Admin dialog is not open before selecting Temporary access");
        }
        semantics.enableFlutterSemantics();
        scrollToSemanticsLabel(SEMANTICS_TEMPORARY_ACCESS);
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            waitUntil(this::isInviteAccessTypeSectionVisible, Duration.ofSeconds(5));
            if (tryClickInviteFormAction(SEMANTICS_TEMPORARY_ACCESS, TEMPORARY_TEXT, TEMPORARY_ACCESS_TEXT)) {
                return;
            }
            if (clickInviteFormActionViaScript(SEMANTICS_TEMPORARY_ACCESS, TEMPORARY_TEXT, TEMPORARY_ACCESS_TEXT)) {
                return;
            }
            scrollToSemanticsLabel(SEMANTICS_TEMPORARY_ACCESS);
            semantics.pauseAfterScroll();
        }
        logInviteAccessSemantics();
        throw new NoSuchElementException(
                "Action not found: " + TEMPORARY_TEXT
                        + " (expected semantics " + SEMANTICS_TEMPORARY_ACCESS + ")");
    }

    public void clickSendInvitation() {
        ensureInviteAdminFormReady();
        if (!isInviteFlowActive(driver)) {
            throw new NoSuchElementException(
                    "Invite Admin dialog is not open before Send Invitation");
        }
        semantics.enableFlutterSemantics();
        scrollToSemanticsLabel(SEMANTICS_SUBMIT_INVITE);
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            if (tryClickInviteFormAction(SEMANTICS_SUBMIT_INVITE, SUBMIT_INVITE_TEXT, SEND_INVITATION_TEXT)) {
                return;
            }
            if (clickInviteFormActionViaScript(SEMANTICS_SUBMIT_INVITE, SUBMIT_INVITE_TEXT, SEND_INVITATION_TEXT)) {
                return;
            }
            if (clickVisibleInviteTextViaScript(SEND_INVITATION_TEXT)
                    || clickVisibleInviteTextViaScript(SUBMIT_INVITE_TEXT)) {
                return;
            }
            scrollToSemanticsLabel(SEMANTICS_SUBMIT_INVITE);
            semantics.pauseAfterScroll();
        }
        logInviteAccessSemantics();
        throw new NoSuchElementException(
                "Action not found: " + SEND_INVITATION_TEXT
                        + " (expected semantics " + SEMANTICS_SUBMIT_INVITE + ")");
    }

    public void clickAllStatus() {
        ensureManageAdminUsersPageReady();
        statusFilter.prepareFilterArea();
        statusFilter.openDropdown();
    }

    public void selectActiveStatus() {
        statusFilter.selectOption("Active", new String[]{SEMANTICS_ACTIVE}, "Active status");
    }

    public void selectBlockedStatus() {
        statusFilter.selectOption("Blocked", new String[]{SEMANTICS_BLOCKED}, "Blocked status");
    }

    public void selectPendingStatus() {
        statusFilter.selectOption("Pending", new String[]{SEMANTICS_PENDING}, "Pending status");
    }

    public void selectDeletedStatus() {
        statusFilter.selectOption(
                "Deleted",
                new String[]{SEMANTICS_DELETED, SEMANTICS_DELETED_LEGACY},
                "Deleted status");
    }

    public void selectExpiredStatus() {
        statusFilter.selectOption("Expired", new String[]{SEMANTICS_EXPIRED}, "Expired status");
    }

    public void clickAllAccess() {
        ensureManageAdminUsersPageReady();
        accessFilter.prepareFilterArea();
        accessFilter.openDropdown();
    }

    public void selectPermanentAccess() {
        accessFilter.selectOption(PERMANENT_TEXT, new String[]{SEMANTICS_PERMANENT}, "Permanent access");
    }

    public void selectTemporaryAccess() {
        accessFilter.selectOption(
                TEMPORARY_TEXT, new String[]{SEMANTICS_TEMPORARY_FILTER}, "Temporary access");
    }

    public void clickSearch() {
        WebElement searchInput = waitForSearchInput();
        semantics.scrollIntoView(searchInput);
        searchInput.click();
    }

    public void searchUser(String userId) {
        WebElement searchInput = waitForSearchInput();
        semantics.scrollIntoView(searchInput);
        searchInput.click();
        searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), userId);
        searchInput.sendKeys(Keys.ENTER);
        waitUntil(d -> isSearchResultDisplayed(d, userId), DEFAULT_WAIT);
    }

    public boolean isActiveStatusDisplayed() {
        return validateAdminRowStatus("Active");
    }

    public boolean isBlockedStatusDisplayed() {
        return validateAdminRowStatus("Blocked");
    }

    public boolean isPendingStatusDisplayed() {
        return validateAdminRowStatus("Pending");
    }

    public boolean isDeletedStatusDisplayed() {
        return validateAdminRowStatus("Deleted");
    }

    public boolean isExpiredStatusDisplayed() {
        return validateAdminRowStatus("Expired");
    }

    public boolean isPermanentAccessDisplayed() {
        return validateAdminRowAccess(PERMANENT_TEXT);
    }

    public boolean isTemporaryAccessDisplayed() {
        return validateAdminRowAccess(TEMPORARY_TEXT);
    }

    public boolean isSearchResultDisplayed(String userId) {
        return waitUntil(d -> isSearchResultDisplayed(d, userId), DEFAULT_WAIT);
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

    private void ensureInviteAdminFormReady() {
        if (isInviteFlowActive(driver)) {
            return;
        }
        clickInviteAdmin();
    }

    private void ensureInviteRoleSelectionReady() {
        if (isInviteFlowActive(driver) || isRolePickerOpen(driver)) {
            return;
        }
        restoreInviteAdminFormIfNeeded();
        if (isInviteFlowActive(driver) || isRolePickerOpen(driver)) {
            return;
        }
        throw new NoSuchElementException(
                "Invite Admin dialog is not open before role selection");
    }

    private void restoreInviteAdminFormIfNeeded() {
        if (isInviteFlowActive(driver) || isRolePickerOpen(driver)) {
            return;
        }
        ensureSemanticsAlive(driver);
        waitUntil(d -> isManageAdminUsersPageLoaded(d) || isOnManageUsersUrl(d), Duration.ofSeconds(8));
        if (!isManageAdminUsersPageLoaded(driver) && !isOnManageUsersUrl(driver)) {
            String pageText = semantics.getSemanticsText(driver);
            int end = pageText == null ? 0 : Math.min(pageText.length(), 400);
            System.out.println("restoreInviteAdminFormIfNeeded: page not detected as Manage Admin Users. url="
                    + driver.getCurrentUrl()
                    + " text=" + (pageText == null ? "" : pageText.substring(0, end)));
            ensureSemanticsAlive(driver);
            // Still attempt Invite Admin by visible text — semantics may be incomplete.
        } else if (!isManageAdminUsersPageLoaded(driver) && isOnManageUsersUrl(driver)) {
            System.out.println("restoreInviteAdminFormIfNeeded: on manage-users URL with blank/partial semantics; retrying a11y");
            ensureSemanticsAlive(driver);
            waitUntil(this::isManageAdminUsersPageLoaded, Duration.ofSeconds(8));
        }
        try {
            if (!tryClickSemanticsAction(SEMANTICS_INVITE_ADMIN, INVITE_ADMIN_TEXT, INVITE_NEW_ADMIN_TEXT, "Invite")) {
                if (!clickVisibleInviteTextViaScript(INVITE_ADMIN_TEXT)
                        && !clickVisibleInviteTextViaScript(INVITE_NEW_ADMIN_TEXT)
                        && !semantics.clickVisibleText(INVITE_ADMIN_TEXT)
                        && !semantics.clickVisibleText("Invite")) {
                    System.out.println("restoreInviteAdminFormIfNeeded: could not click Invite Admin");
                    return;
                }
            }
            semantics.pauseAfterScroll();
            waitUntil(this::isInviteAdminFormLoaded, INVITE_FORM_POLL);
        } catch (RuntimeException ex) {
            System.out.println("restoreInviteAdminFormIfNeeded: reopen failed: " + ex.getMessage());
            return;
        }
        if (!isInviteFlowActive(driver)) {
            System.out.println("restoreInviteAdminFormIfNeeded: invite form did not load after Invite Admin");
            return;
        }
        String email = UsersPageContext.getSearchedEmail();
        String name = UsersPageContext.getSearchedName();
        if (email != null && !email.isBlank()) {
            enterField(new String[]{SEMANTICS_EMAIL}, new String[]{EMAIL_ADDRESS_TEXT, "Email"}, email, false);
        }
        if (name != null && !name.isBlank()) {
            enterField(new String[]{SEMANTICS_FULL_NAME, SEMANTICS_NAME},
                    new String[]{FULL_NAME_TEXT, "Enter full name", "Name"}, name, false);
        }
        waitUntil(d -> !d.findElements(semanticsLocator(SEMANTICS_ROLE_DROPDOWN)).isEmpty()
                        || hasCollapsedInviteSummary(d)
                        || pageHasAssignRoleText(d),
                Duration.ofSeconds(8));
    }

    private boolean isOnManageUsersUrl(WebDriver webDriver) {
        try {
            String url = webDriver.getCurrentUrl();
            return url != null && url.toLowerCase().contains("manage-users");
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean isRolePickerOpen(WebDriver webDriver) {
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        INVITE_ROLE_OPTION_SCRIPT +
                        "if (hasInvitePopupMenu()) return true;" +
                        "return Array.from(document.querySelectorAll('flt-semantics'))" +
                        "  .some(node => isInviteRoleOptionNode(node));"
        ));
    }

    private void ensureManageAdminUsersPageReady() {
        waitUntil(this::isManageAdminUsersPageLoaded, DEFAULT_WAIT);
        waitForSearchBarReady();
        semantics.enableFlutterSemantics();
    }

    private void enterField(String semanticsLabel, String fallbackLabel, String value) {
        enterField(new String[]{semanticsLabel}, new String[]{fallbackLabel}, value, false);
    }

    private void enterField(String[] semanticsLabels, String[] fallbackLabels, String value) {
        enterField(semanticsLabels, fallbackLabels, value, false);
    }

    private void enterField(String[] semanticsLabels, String[] fallbackLabels, String value, boolean sendTabAfter) {
        semantics.enableFlutterSemantics();
        scrollToFieldLabels(semanticsLabels, fallbackLabels);
        WebElement input = findFormInput(semanticsLabels, fallbackLabels);
        typeExactValue(input, value, sendTabAfter, !sendTabAfter);
        semantics.pauseAfterScroll();
    }

    private WebElement findFormInput(String semanticsLabel, String fallbackLabel) {
        return findFormInput(new String[]{semanticsLabel}, new String[]{fallbackLabel});
    }

    private WebElement findFormInput(String[] semanticsLabels, String[] fallbackLabels) {
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            for (String label : semanticsLabels) {
                if (label == null || label.isBlank()) {
                    continue;
                }
                if (semantics.clickSemanticsLabelViaScript(label)) {
                    semantics.pauseAfterScroll();
                }
                By wrappedInput = semanticsFieldInput(label);
                if (!driver.findElements(wrappedInput).isEmpty()) {
                    return wait.until(ExpectedConditions.elementToBeClickable(wrappedInput));
                }
                By directInput = inputField(label);
                if (!driver.findElements(directInput).isEmpty()) {
                    return wait.until(ExpectedConditions.elementToBeClickable(directInput));
                }
            }
            for (String label : fallbackLabels) {
                if (label == null || label.isBlank()) {
                    continue;
                }
                By wrappedInput = semanticsFieldInput(label);
                if (!driver.findElements(wrappedInput).isEmpty()) {
                    return wait.until(ExpectedConditions.elementToBeClickable(wrappedInput));
                }
                By directInput = inputField(label);
                if (!driver.findElements(directInput).isEmpty()) {
                    return wait.until(ExpectedConditions.elementToBeClickable(directInput));
                }
            }
            WebElement fromScript = findFormInputViaScript(semanticsLabels, fallbackLabels);
            if (fromScript != null) {
                return wait.until(ExpectedConditions.elementToBeClickable(fromScript));
            }
            semantics.pauseAfterScroll();
        }
        logInviteFormSemantics();
        throw new NoSuchElementException(
                "Form input not found for fields: " + String.join(", ", semanticsLabels));
    }

    private WebElement findFormInputViaScript(String[] semanticsLabels, String[] fallbackLabels) {
        return (WebElement) ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const semanticsLabels = arguments[0] || [];" +
                        "const fallbackLabels = arguments[1] || [];" +
                        "const labels = [...semanticsLabels, ...fallbackLabels].filter(Boolean);" +
                        "const findInputForLabel = (label) => {" +
                        "  const field = findOutsideNav('flt-semantics[aria-label=\"' + label + '\"]')" +
                        "    || findOutsideNav('flt-semantics[aria-label*=\"' + label + '\"]');" +
                        "  if (field) {" +
                        "    const input = field.querySelector('input, textarea');" +
                        "    if (input) return input;" +
                        "  }" +
                        "  return findOutsideNav('input[aria-label=\"' + label + '\"]')" +
                        "    || findOutsideNav('textarea[aria-label=\"' + label + '\"]')" +
                        "    || findOutsideNav('input[aria-label*=\"' + label + '\"]')" +
                        "    || findOutsideNav('textarea[aria-label*=\"' + label + '\"]');" +
                        "};" +
                        "const findInputNearText = (marker) => {" +
                        "  const wanted = (marker || '').replace(/\\s+/g, ' ').trim();" +
                        "  const nodes = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "    if (isInNav(node)) return false;" +
                        "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "    return text === wanted || text.startsWith(wanted + ' ') || text.startsWith(wanted + ':');" +
                        "  });" +
                        "  for (const node of nodes) {" +
                        "    let scan = node;" +
                        "    for (let depth = 0; depth < 10 && scan; depth++) {" +
                        "      const input = scan.querySelector('input, textarea');" +
                        "      if (input && !isInNav(input)) return input;" +
                        "      scan = scan.parentElement;" +
                        "    }" +
                        "  }" +
                        "  return null;" +
                        "};" +
                        "for (const label of labels) {" +
                        "  const input = findInputForLabel(label) || findInputNearText(label);" +
                        "  if (input) return input;" +
                        "}" +
                        "const searchField = findOutsideNav('flt-semantics[aria-label=\"search_field\"]');" +
                        "const inviteInputs = Array.from(document.querySelectorAll(" +
                        "  'input[data-semantics-role=\"text-field\"], flt-semantics input, textarea'))" +
                        "  .filter(el => !isInNav(el))" +
                        "  .filter(el => !searchField || !searchField.contains(el));" +
                        "const pageText = (document.querySelector('flt-semantics-host')?.textContent || '')" +
                        "  .replace(/\\s+/g, ' ');" +
                        "if (pageText.includes('Invite New Admin') || pageText.includes('Invite Admin')) {" +
                        "  if (semanticsLabels.includes('email_field') && inviteInputs.length > 0) return inviteInputs[0];" +
                        "  if ((semanticsLabels.includes('full_name_field') || semanticsLabels.includes('name_field'))" +
                        "      && inviteInputs.length > 1) return inviteInputs[1];" +
                        "}" +
                        "return null;",
                semanticsLabels,
                fallbackLabels
        );
    }

    private void typeExactValue(WebElement input, String value) {
        typeExactValue(input, value, true);
    }

    private void typeExactValue(WebElement input, String value, boolean sendTabAfter) {
        typeExactValue(input, value, sendTabAfter, false);
    }

    private void typeExactValue(WebElement input, String value, boolean sendTabAfter, boolean focusOnly) {
        semantics.scrollIntoView(input);
        if (focusOnly) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", input);
        } else {
            input.click();
        }
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(value);
        if (sendTabAfter) {
            input.sendKeys(Keys.TAB);
        }
    }

    private void clickSemanticsAction(String semanticsLabel, String... visibleTexts) {
        if (!tryClickSemanticsAction(semanticsLabel, visibleTexts)) {
            String labels = visibleTexts.length > 0 ? String.join(", ", visibleTexts) : "";
            throw new NoSuchElementException(
                    "Action not found: " + labels
                            + (semanticsLabel != null ? " (expected semantics " + semanticsLabel + ")" : ""));
        }
    }

    private boolean tryClickSemanticsAction(String semanticsLabel, String... visibleTexts) {
        semantics.enableFlutterSemantics();
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            if (semanticsLabel != null && semantics.clickSemanticsLabelViaScript(semanticsLabel)) {
                return true;
            }
            for (String visibleText : visibleTexts) {
                if (visibleText == null || visibleText.isBlank()) {
                    continue;
                }
                if (semanticsLabel != null && clickActionViaScript(semanticsLabel, visibleText)) {
                    return true;
                }
                if (clickActionViaScript(null, visibleText)) {
                    return true;
                }
                if (semantics.clickVisibleText(visibleText)) {
                    return true;
                }
            }
            semantics.pauseAfterScroll();
        }
        return false;
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

    private void scrollNavigationToUserAccess() {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "if (!menu) return;" +
                        "const target = Array.from(menu.querySelectorAll('flt-semantics')).find(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return label.includes('user_and_access') || text.includes('User & Access');" +
                        "});" +
                        "if (target) target.scrollIntoView({block: 'center', inline: 'nearest'});"
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

    private void scrollToFieldLabel(String semanticsLabel, String fallbackLabel) {
        scrollToFieldLabels(new String[]{semanticsLabel}, new String[]{fallbackLabel});
    }

    private void scrollToFieldLabels(String[] semanticsLabels, String[] fallbackLabels) {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const labels = [...(arguments[0] || []), ...(arguments[1] || [])].filter(Boolean);" +
                        "for (const label of labels) {" +
                        "  const anchor = findOutsideNav('flt-semantics[aria-label=\"' + label + '\"]')" +
                        "    || document.querySelector('input[aria-label=\"' + label + '\"]')" +
                        "    || document.querySelector('textarea[aria-label=\"' + label + '\"]')" +
                        "    || document.querySelector('input[aria-label*=\"' + label + '\"]');" +
                        "  if (anchor && !isInNav(anchor)) {" +
                        "    anchor.scrollIntoView({block: 'center', inline: 'nearest'});" +
                        "    break;" +
                        "  }" +
                        "}",
                semanticsLabels,
                fallbackLabels
        );
        semantics.pauseAfterScroll();
    }

    private boolean tryClickInviteFormAction(String semanticsLabel, String... visibleTexts) {
        semantics.enableFlutterSemantics();
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            if (clickInviteFormActionViaScript(semanticsLabel, visibleTexts)) {
                semantics.pauseAfterScroll();
                return true;
            }
            semantics.pauseAfterScroll();
        }
        return false;
    }

    private boolean clickInviteFormActionViaScript(String semanticsLabel, String... visibleTexts) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        ROLE_PICKER_SCOPE_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "const semanticsLabel = (arguments[0] || '').toLowerCase();" +
                        "const visibleTexts = (arguments[1] || []).filter(Boolean);" +
                        "const pageText = (document.querySelector('flt-semantics-host')?.textContent || '')" +
                        "  .replace(/\\s+/g, ' ').trim();" +
                        "const hasInviteTitle = pageText.includes('Invite New Admin')" +
                        "  || (pageText.includes('Invite Admin') && pageText.includes('Email Address'));" +
                        "const hasInviteFields = pageText.includes('Email Address')" +
                        "  || pageText.includes('Full Name')" +
                        "  || pageText.includes('Assign Role')" +
                        "  || pageText.includes('Select role');" +
                        "const hasCollapsedInviteSummary = pageText.includes('Name:')" +
                        "  && pageText.includes('Email:')" +
                        "  && (pageText.includes('Role:') || pageText.includes('Assign Role'));" +
                        "if (!hasInviteTitle && !hasCollapsedInviteSummary) return false;" +
                        "if (!hasInviteFields && !hasCollapsedInviteSummary) return false;" +
                        "const matches = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  if (!isInsideInviteForm(node) && !hasCollapsedInviteSummary) return false;" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (label.includes('temporary_option') || label.includes('permanent_option')) {" +
                        "    return false;" +
                        "  }" +
                        "  if (semanticsLabel && (label === semanticsLabel" +
                        "      || label.includes(semanticsLabel.replace(/_button$/, '')))) {" +
                        "    return true;" +
                        "  }" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return visibleTexts.some(wanted => text === wanted" +
                        "    || text.startsWith(wanted + ' ')" +
                        "    || (wanted.endsWith(':') && text.startsWith(wanted)));" +
                        "});" +
                        "for (const match of matches) {" +
                        "  if (!isInsideInviteForm(match) && !hasCollapsedInviteSummary) continue;" +
                        "  if (dispatchPointerClick(match.closest('flt-semantics[flt-tappable]') || match)) {" +
                        "    return true;" +
                        "  }" +
                        "}" +
                        "return false;",
                semanticsLabel,
                visibleTexts
        ));
    }

    private static final String ROLE_PICKER_SCOPE_SCRIPT =
            "function rolePickerScope() {" +
                    "  const popup = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                    "    return (node.textContent || '').replace(/\\s+/g, ' ').trim() === 'Popup menu';" +
                    "  });" +
                    "  if (popup) {" +
                    "    let root = popup.parentElement;" +
                    "    for (let depth = 0; depth < 10 && root; depth++) {" +
                    "      if (root.querySelector && root.querySelector('flt-semantics[flt-tappable]')) {" +
                    "        return root;" +
                    "      }" +
                    "      root = root.parentElement;" +
                    "    }" +
                    "    return popup.parentElement || popup;" +
                    "  }" +
                    "  const dropdown = findOutsideNav('flt-semantics[aria-label*=\"role_dropdown\"]');" +
                    "  return dropdown ? (dropdown.parentElement || dropdown) : null;" +
                    "}" +
                    "function isInsideInviteForm(node) {" +
                    "  let scan = node;" +
                    "  for (let depth = 0; depth < 30 && scan; depth++) {" +
                    "    const text = (scan.textContent || '').replace(/\\s+/g, ' ');" +
                    "    if (text.includes('Invite New Admin')" +
                    "        || (text.includes('Email Address') && text.includes('Full Name'))" +
                    "        || (text.includes('Assign Role') && text.includes('Email Address'))" +
                    "        || (text.includes('Name:') && text.includes('Email:') && text.includes('Role:'))) {" +
                    "      return true;" +
                    "    }" +
                    "    scan = scan.parentElement;" +
                    "  }" +
                    "  return false;" +
                    "}";

    /**
     * Invite role picker options only — never treat Manage Admin Users table {@code role=cell}
     * values (Rahul/CEO/CTO in rows) as an open dropdown.
     */
    private static final String INVITE_ROLE_OPTION_SCRIPT =
            "const INVITE_ROLE_NAMES = ['Rahul','Anupam','CEO','CTO','QA','COO','Marketing','team lead','Manager','Company Manager','new admin'];" +
                    "const INVITE_ROLE_BLOCKLIST = ['select role','assign role','permanent','temporary','cancel',"
                    + "'send invitation','invite admin','invite new admin','access type'];" +
                    "function hasInvitePopupMenu() {" +
                    "  return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                    "    const label = (node.getAttribute('aria-label') || '').replace(/\\s+/g, ' ').trim();" +
                    "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                    "    const role = (node.getAttribute('role') || '').toLowerCase();" +
                    "    return label === 'Popup menu' || text === 'Popup menu' || role === 'menu';" +
                    "  });" +
                    "}" +
                    "function isUnderInvitePopup(node) {" +
                    "  let scan = node;" +
                    "  for (let depth = 0; depth < 25 && scan; depth++) {" +
                    "    const label = ((scan.getAttribute && scan.getAttribute('aria-label')) || '')" +
                    "      .replace(/\\s+/g, ' ').trim();" +
                    "    const text = (scan.textContent || '').replace(/\\s+/g, ' ').trim();" +
                    "    const role = ((scan.getAttribute && scan.getAttribute('role')) || '').toLowerCase();" +
                    "    if (label === 'Popup menu' || text === 'Popup menu' || role === 'menu') return true;" +
                    "    scan = scan.parentElement;" +
                    "  }" +
                    "  return false;" +
                    "}" +
                    "function isInviteDialogContext() {" +
                    "  const pageText = (document.querySelector('flt-semantics-host')?.textContent || '')" +
                    "    .replace(/\\s+/g, ' ');" +
                    "  return pageText.includes('Invite New Admin')" +
                    "    || pageText.includes('Email Address')" +
                    "    || pageText.includes('Assign Role')" +
                    "    || pageText.includes('Select role');" +
                    "}" +
                    "function matchedInviteRoleName(label, text) {" +
                    "  const candidates = [label, text].filter(Boolean);" +
                    "  for (const value of candidates) {" +
                    "    const matched = INVITE_ROLE_NAMES.find(name =>" +
                    "      value === name || value.toLowerCase() === name.toLowerCase());" +
                    "    if (matched) return matched;" +
                    "  }" +
                    "  return '';" +
                    "}" +
                    // Staging popup uses role=button/menuitem/cell. Admin table repeats CTO/QA,
                    // so require >=2 distinct role names and ignore form control labels.
                    "function hasUniqueInviteRoleOptionCluster() {" +
                    "  if (!isInviteDialogContext()) return false;" +
                    "  const labels = Array.from(document.querySelectorAll('flt-semantics')).map(node => {" +
                    "    if (isInNav(node)) return '';" +
                    "    const label = (node.getAttribute('aria-label') || '').replace(/\\s+/g, ' ').trim();" +
                    "    const lower = label.toLowerCase();" +
                    "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                    "    const textLower = text.toLowerCase();" +
                    "    const role = (node.getAttribute('role') || '').toLowerCase();" +
                    "    const rect = node.getBoundingClientRect();" +
                    "    if (rect.width <= 0 || rect.height <= 0) return '';" +
                    "    if (lower.includes('admin_row') || lower === 'dismiss') return '';" +
                    "    if (INVITE_ROLE_BLOCKLIST.includes(textLower)) return '';" +
                    "    if (role !== 'cell' && role !== 'option' && role !== 'listitem'" +
                    "        && role !== 'menuitem' && role !== 'button') return '';" +
                    "    return matchedInviteRoleName(label, text);" +
                    "  }).filter(Boolean);" +
                    "  return new Set(labels.map(v => v.toLowerCase())).size >= 2;" +
                    "}" +
                    "function hasUniqueInviteRoleCellCluster() { return hasUniqueInviteRoleOptionCluster(); }" +
                    "function isInviteRoleOptionNode(node) {" +
                    "  if (!node || isInNav(node)) return false;" +
                    "  const label = (node.getAttribute('aria-label') || '').replace(/\\s+/g, ' ').trim();" +
                    "  const lower = label.toLowerCase();" +
                    "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                    "  const textLower = text.toLowerCase();" +
                    "  const role = (node.getAttribute('role') || '').toLowerCase();" +
                    "  const rect = node.getBoundingClientRect();" +
                    "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                    "  if (lower === 'dismiss' || lower.includes('dismiss') || lower.includes('admin_row')) return false;" +
                    "  if (INVITE_ROLE_BLOCKLIST.includes(textLower)) return false;" +
                    "  if (lower.includes('role_name_option') || lower.includes('role_option')) return true;" +
                    "  if (role === 'menuitem') return true;" +
                    "  if (role === 'cell' || role === 'option' || role === 'listitem' || role === 'button') {" +
                    "    const nameMatch = matchedInviteRoleName(label, text);" +
                    "    if (!nameMatch) return false;" +
                    "    return isUnderInvitePopup(node) || hasUniqueInviteRoleOptionCluster();" +
                    "  }" +
                    "  return false;" +
                    "}";

    private boolean isInviteDialogOpen(WebDriver webDriver) {
        return isInviteAdminFormLoaded(webDriver);
    }

    private boolean isInviteFlowActive(WebDriver webDriver) {
        return isInviteAdminFormLoaded(webDriver)
                || isRolePickerOpen(webDriver)
                || hasCollapsedInviteSummary(webDriver);
    }

    private boolean hasCollapsedInviteSummary(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const markers = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return text === 'Name:' || text === 'Email:' || text === 'Role:';" +
                        "});" +
                        "return markers.length >= 2;"
        ));
    }

    private boolean clickCollapsedInviteRoleSection() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const roleDropdown = findOutsideNav('flt-semantics[aria-label=\"role_dropdown\"]');" +
                        "if (roleDropdown) {" +
                        "  const target = roleDropdown.closest('flt-semantics[flt-tappable]') || roleDropdown;" +
                        "  try { target.click(); return true; } catch (e) {}" +
                        "}" +
                        "const targets = ['Role:', 'Select role'];" +
                        "for (const wanted of targets) {" +
                        "  const node = Array.from(document.querySelectorAll('flt-semantics')).find(candidate => {" +
                        "    if (isInNav(candidate)) return false;" +
                        "    const text = (candidate.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "    return text === wanted || text.startsWith(wanted + ' ');" +
                        "  });" +
                        "  if (!node) continue;" +
                        "  const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "  try { target.click(); return true; } catch (e) {}" +
                        "}" +
                        "return false;"
        ));
    }

    private boolean isInviteAdminFormOpenAfterRoleSelection(WebDriver webDriver) {
        if (isRolePickerOpen(webDriver)) {
            return false;
        }
        return isInviteAdminFormLoaded(webDriver) || hasCollapsedInviteSummary(webDriver);
    }

    private boolean isInviteAccessTypeSectionVisible(WebDriver webDriver) {
        if (!isInviteFlowActive(webDriver)) {
            return false;
        }
        semantics.enableFlutterSemanticsOn(webDriver);
        if (!webDriver.findElements(semanticsLocator(SEMANTICS_PERMANENT_ACCESS)).isEmpty()) {
            return true;
        }
        if (!webDriver.findElements(semanticsLocator(SEMANTICS_TEMPORARY_ACCESS)).isEmpty()) {
            return true;
        }
        if (!webDriver.findElements(semanticsLocator(SEMANTICS_SUBMIT_INVITE)).isEmpty()) {
            return true;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "return !!findOutsideNav('flt-semantics[aria-label*=\"permanent_access\"]')" +
                        "  || !!findOutsideNav('flt-semantics[aria-label*=\"temporary_access\"]')" +
                        "  || !!findOutsideNav('flt-semantics[aria-label*=\"submit_invite\"]');"
        ));
    }

    private void logInviteAccessSemantics() {
        Object labels = ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics'))" +
                        "  .map(node => {" +
                        "    const label = node.getAttribute('aria-label') || '';" +
                        "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "    if (!label && !text) return '';" +
                        "    if (label.includes('access') || label.includes('invite') || label.includes('submit')" +
                        "        || text.includes('Temporary') || text.includes('Permanent')" +
                        "        || text.includes('Submit') || text.includes('Send Invitation')) {" +
                        "      return (label || text) + (label && text && text !== label ? ':' + text : '');" +
                        "    }" +
                        "    return '';" +
                        "  }).filter(Boolean).slice(0, 80);"
        );
        System.out.println("Invite access related semantics on failure: " + labels);
    }

    private boolean clickRoleOptionViaScript(String semanticsLabel, String visibleText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        ROLE_PICKER_SCOPE_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "const semanticsLabel = arguments[0];" +
                        "const wanted = (arguments[1] || '').replace(/\\s+/g, ' ').trim();" +
                        "const scope = rolePickerScope();" +
                        "const popup = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "  return (node.textContent || '').replace(/\\s+/g, ' ').trim() === 'Popup menu';" +
                        "});" +
                        "const scopedNodes = scope" +
                        "  ? Array.from(scope.querySelectorAll('flt-semantics'))" +
                        "  : [];" +
                        "const textMatches = (nodes) => nodes.filter(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return !!wanted && text === wanted;" +
                        "});" +
                        "for (const match of textMatches(scopedNodes)) {" +
                        "  if (dispatchPointerClick(match.closest('flt-semantics[flt-tappable]') || match)) {" +
                        "    return true;" +
                        "  }" +
                        "}" +
                        "for (const match of textMatches(Array.from(document.querySelectorAll('flt-semantics')))) {" +
                        "  if (isInNav(match)) continue;" +
                        "  if (popup || isInsideInviteForm(match) || (scope && scope.contains(match))) {" +
                        "    if (dispatchPointerClick(match.closest('flt-semantics[flt-tappable]') || match)) {" +
                        "      return true;" +
                        "    }" +
                        "  }" +
                        "}" +
                        "if (!semanticsLabel) return false;" +
                        "const labelMatches = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (label !== semanticsLabel.toLowerCase()" +
                        "      && !label.includes('role_name_option')) {" +
                        "    return false;" +
                        "  }" +
                        "  if (popup || isInsideInviteForm(node) || (scope && scope.contains(node))) {" +
                        "    return true;" +
                        "  }" +
                        "  return false;" +
                        "});" +
                        "for (const match of labelMatches) {" +
                        "  if (dispatchPointerClick(match.closest('flt-semantics[flt-tappable]') || match)) {" +
                        "    return true;" +
                        "  }" +
                        "}" +
                        "const documentMatches = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  if (semanticsLabel && label === semanticsLabel.toLowerCase()) return true;" +
                        "  if (semanticsLabel && label.includes('role_name_option') && (!wanted || text === wanted)) {" +
                        "    return true;" +
                        "  }" +
                        "  return false;" +
                        "});" +
                        "for (const match of documentMatches) {" +
                        "  if (!(popup || isInsideInviteForm(match) || (scope && scope.contains(match)))) continue;" +
                        "  if (dispatchPointerClick(match.closest('flt-semantics[flt-tappable]') || match)) {" +
                        "    return true;" +
                        "  }" +
                        "}" +
                        "return false;",
                semanticsLabel,
                visibleText
        ));
    }

    private void logRolePickerSemantics() {
        Object labels = ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics'))" +
                        "  .map(node => {" +
                        "    const label = node.getAttribute('aria-label') || '';" +
                        "    const text = (node.textContent || '').trim();" +
                        "    if (!label && !text) return '';" +
                        "    if (label.includes('role_name_option') || label.includes('role_option')" +
                        "        || label.includes('role_dropdown') || label.includes('role') || label.includes('option')" +
                        "        || text === 'Rahul' || text.includes('Select role') || text.includes('Assign Role')" +
                        "        || text === 'Popup menu' || text.includes('Invite')) {" +
                        "      return (label || text) + (label && text && text !== label ? ':' + text : '');" +
                        "    }" +
                        "    return '';" +
                        "  }).filter(Boolean).slice(0, 80);"
        );
        System.out.println("Role picker related semantics on failure: " + labels);
        Object diagnostics = ((JavascriptExecutor) driver).executeScript(
                "const host = document.querySelector('flt-semantics-host');" +
                        "const nodes = host ? Array.from(host.querySelectorAll('flt-semantics')) : [];" +
                        "const hostText = ((host && host.textContent) || '').replace(/\\s+/g, ' ').trim().slice(0, 400);" +
                        "const bodyText = (document.body && document.body.innerText || '').replace(/\\s+/g, ' ').trim().slice(0, 400);" +
                        "const placeholder = !!document.querySelector('flt-semantics-placeholder');" +
                        "const sample = nodes.slice(0, 20).map(n => ({" +
                        "  label: n.getAttribute('aria-label') || ''," +
                        "  role: n.getAttribute('role') || ''," +
                        "  text: (n.textContent || '').replace(/\\s+/g, ' ').trim().slice(0, 40)," +
                        "  tappable: n.hasAttribute('flt-tappable')," +
                        "  w: Math.round(n.getBoundingClientRect().width)," +
                        "  h: Math.round(n.getBoundingClientRect().height)" +
                        "}));" +
                        "return {count: nodes.length, placeholder: placeholder, hostText: hostText, bodyText: bodyText, sample: sample};"
        );
        System.out.println("Semantics diagnostics on failure: " + diagnostics);
        String pageText = semantics.getSemanticsText(driver);
        int end = pageText == null ? 0 : Math.min(pageText.length(), 800);
        System.out.println("Page text excerpt: " + (pageText == null ? "" : pageText.substring(0, end)));
    }

    private void logInviteFormSemantics() {
        Object labels = ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics, input, textarea'))" +
                        "  .map(node => (node.getAttribute('aria-label') || '')" +
                        "    + ':' + (node.textContent || '').trim().slice(0, 40))" +
                        "  .filter(label => label && (" +
                        "    label.toLowerCase().includes('invite')" +
                        "    || label.toLowerCase().includes('email')" +
                        "    || label.toLowerCase().includes('name')" +
                        "    || label.toLowerCase().includes('role')))" +
                        "  .slice(0, 50);"
        );
        System.out.println("Invite form related semantics on failure: " + labels);
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

    private boolean validateAdminRowStatus(String expectedStatus) {
        ensureManageAdminUsersPageReady();
        waitBeforeStatusValidation();
        return waitUntil(d -> isAdminRowStatusDisplayed(d, expectedStatus), DEFAULT_WAIT);
    }

    private boolean validateAdminRowAccess(String expectedAccess) {
        ensureManageAdminUsersPageReady();
        waitBeforeStatusValidation();
        return waitUntil(d -> isAdminRowAccessDisplayed(d, expectedAccess), DEFAULT_WAIT);
    }

    private boolean isAdminRowStatusDisplayed(WebDriver webDriver, String expectedStatus) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String expected = semantics.normalizeText(expectedStatus);

        List<WebElement> statusCells = webDriver.findElements(By.xpath(
                "//flt-semantics[contains(@aria-label,'admin_row_status_')]"));
        for (WebElement cell : statusCells) {
            String actual = semantics.normalizeText(semantics.getSemanticsNodeText(cell));
            if (actual.equals(expected) || actual.contains(expected)) {
                return true;
            }
        }

        if (!webDriver.findElements(adminRowStatusCell).isEmpty()) {
            WebElement statusCell = webDriver.findElement(adminRowStatusCell);
            String actual = semantics.normalizeText(semantics.getSemanticsNodeText(statusCell));
            if (actual.equals(expected) || actual.contains(expected)) {
                return true;
            }
        }

        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        return pageText.contains(expected);
    }

    private boolean isAdminRowAccessDisplayed(WebDriver webDriver, String expectedAccess) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String expected = semantics.normalizeText(expectedAccess);

        List<WebElement> accessCells = webDriver.findElements(By.xpath(
                "//flt-semantics[contains(@aria-label,'admin_row_access_')]"));
        for (WebElement cell : accessCells) {
            String actual = semantics.normalizeText(semantics.getSemanticsNodeText(cell));
            if (actual.equals(expected) || actual.contains(expected)) {
                return true;
            }
        }

        if (!webDriver.findElements(adminRowAccessCell).isEmpty()) {
            WebElement accessCell = webDriver.findElement(adminRowAccessCell);
            String actual = semantics.normalizeText(semantics.getSemanticsNodeText(accessCell));
            if (actual.equals(expected) || actual.contains(expected)) {
                return true;
            }
        }

        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        return pageText.contains(expected);
    }

    private boolean isSearchResultDisplayed(WebDriver webDriver, String userId) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String expected = semantics.normalizeText(userId);

        if (!webDriver.findElements(adminRowIdCell).isEmpty()) {
            WebElement idCell = webDriver.findElement(adminRowIdCell);
            String ariaLabel = semantics.normalizeText(idCell.getAttribute("aria-label"));
            String actual = semantics.normalizeText(semantics.getSemanticsNodeText(idCell));
            if (ariaLabel.contains(expected) || actual.contains(expected)) {
                return true;
            }
        }

        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        return pageText.contains(expected);
    }

    private void waitBeforeStatusValidation() {
        try {
            Thread.sleep(STATUS_VALIDATION_DELAY.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean isHomePageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String text = semantics.getSemanticsText(webDriver);
        return text != null
                && (text.contains("Individual Users")
                || text.contains("Company Users")
                || text.contains("Navigation menu"));
    }

    private boolean isManageAdminUsersPageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (!webDriver.findElements(searchBarSemantics).isEmpty()
                || !webDriver.findElements(searchBarInput).isEmpty()) {
            return true;
        }
        if (!webDriver.findElements(semanticsLocator(SEMANTICS_ALL_STATUS)).isEmpty()) {
            return true;
        }
        if (!webDriver.findElements(semanticsLocator(SEMANTICS_ALL_ACCESS)).isEmpty()) {
            return true;
        }
        if (!webDriver.findElements(semanticsLocator(SEMANTICS_INVITE_ADMIN)).isEmpty()) {
            return true;
        }
        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        return pageText.contains(semantics.normalizeText(MANAGE_ADMIN_USERS_TEXT))
                || pageText.contains("manage admin users");
    }

    private boolean isInviteAdminFormLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (isInputFieldPresent(webDriver, SEMANTICS_EMAIL, EMAIL_ADDRESS_TEXT)) {
            return true;
        }
        if (isInputFieldPresent(webDriver, SEMANTICS_FULL_NAME, FULL_NAME_TEXT)
                || isInputFieldPresent(webDriver, SEMANTICS_NAME, FULL_NAME_TEXT)) {
            return true;
        }
        if (!webDriver.findElements(semanticsLocator(SEMANTICS_ROLE_DROPDOWN)).isEmpty()) {
            return true;
        }
        if (!webDriver.findElements(semanticsLocator(SEMANTICS_SUBMIT_INVITE)).isEmpty()) {
            return true;
        }
        if (!webDriver.findElements(semanticsLocator(SEMANTICS_PERMANENT_ACCESS)).isEmpty()) {
            return true;
        }
        if (!webDriver.findElements(semanticsLocator(SEMANTICS_TEMPORARY_ACCESS)).isEmpty()) {
            return true;
        }
        if (hasCollapsedInviteSummary(webDriver)) {
            return true;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "if (findOutsideNav('flt-semantics[aria-label=\"email_field\"]')) return true;" +
                        "if (findOutsideNav('input[aria-label=\"email_field\"]')) return true;" +
                        "if (findOutsideNav('flt-semantics[aria-label=\"role_dropdown\"]')) return true;" +
                        "if (findOutsideNav('flt-semantics[aria-label=\"submit_invite_button\"]')) return true;" +
                        "const pageText = (document.querySelector('flt-semantics-host')?.textContent || '')" +
                        "  .replace(/\\s+/g, ' ').trim();" +
                        "const hasInviteTitle = pageText.includes('Invite New Admin') || pageText.includes('Invite Admin');" +
                        "const hasInviteFields = pageText.includes('Email Address')" +
                        "  || pageText.includes('Full Name')" +
                        "  || pageText.includes('Assign Role')" +
                        "  || pageText.includes('Select role');" +
                        "const hasCollapsedInviteSummary = pageText.includes('Name:')" +
                        "  && pageText.includes('Email:')" +
                        "  && (pageText.includes('Role:') || pageText.includes('Assign Role'));" +
                        "if (!hasInviteTitle && !hasCollapsedInviteSummary) return false;" +
                        "if (!hasInviteFields && !hasCollapsedInviteSummary) return false;" +
                        "const searchField = findOutsideNav('flt-semantics[aria-label=\"search_field\"]');" +
                        "const inviteInputs = Array.from(document.querySelectorAll(" +
                        "  'input[data-semantics-role=\"text-field\"], flt-semantics input, textarea'))" +
                        "  .filter(el => !isInNav(el))" +
                        "  .filter(el => !searchField || !searchField.contains(el));" +
                        "if (hasCollapsedInviteSummary) return true;" +
                        "const collapsedMarkers = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return text === 'Name:' || text === 'Email:' || text === 'Role:';" +
                        "});" +
                        "if (collapsedMarkers.length >= 2) return true;" +
                        "return inviteInputs.length > 0;"
        ));
    }

    private boolean isInputFieldPresent(WebDriver webDriver, String semanticsLabel, String fallbackLabel) {
        for (String label : new String[]{semanticsLabel, fallbackLabel}) {
            if (!webDriver.findElements(semanticsFieldInput(label)).isEmpty()) {
                return true;
            }
            if (!webDriver.findElements(inputField(label)).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean waitUntil(Function<WebDriver, Boolean> condition, Duration timeout) {
        return semantics.waitUntil(condition, timeout);
    }

    private static By semanticsLocator(String label) {
        return By.xpath(
                "//flt-semantics[@aria-label='" + label + "']" +
                        " | //flt-semantics[contains(@aria-label,'" + label + "')]");
    }

    private static By semanticsFieldInput(String semanticsLabel) {
        return By.xpath(
                "//flt-semantics[@aria-label='" + semanticsLabel + "']//input" +
                        " | //flt-semantics[contains(@aria-label,'" + semanticsLabel + "')]//input" +
                        " | //flt-semantics[@aria-label='" + semanticsLabel + "']//textarea" +
                        " | //flt-semantics[contains(@aria-label,'" + semanticsLabel + "')]//textarea");
    }

    private static By inputField(String label) {
        return By.xpath(
                "//input[@aria-label='" + label + "']" +
                        " | //input[contains(@aria-label,'" + label + "')]" +
                        " | //textarea[@aria-label='" + label + "']" +
                        " | //textarea[contains(@aria-label,'" + label + "')]");
    }
}
