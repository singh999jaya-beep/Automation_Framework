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

public class ManageRolesPage {

    private static final String USER_ACCESS_MANAGEMENT_TEXT = "User & Access Management";
    private static final String MANAGE_ROLES_TEXT = "Manage Roles & Permissions";
    private static final String CREATE_ROLE_TEXT = "Create Role";

    private static final String SEMANTICS_USER_ACCESS_MENU = "user_and_access_management_submenu";
    private static final String SEMANTICS_MANAGE_ROLES_MENU = "manage_roles_permissions_submenu";
    private static final String SEMANTICS_CREATE_ROLE = "create_role_button";
    private static final String SEMANTICS_ROLE_NAME = "name_field";
    private static final String SEMANTICS_DESCRIPTION = "role_name_0";
    private static final String SEMANTICS_CREATE_CONFIRM = "create_role_confirm_button";

    private static final String SEMANTICS_ROLE_SEARCH = "role_search_field";
    private static final String SEMANTICS_ROLE_VIEW = "role_view_button_0";
    private static final String SEMANTICS_ROLE_USERS = "role_view_button_0";
    private static final String SEMANTICS_ROLE_DELETE = "role_delete_button_0";
    private static final String SEMANTICS_ROLE_EDIT = "role_edit_button_0";
    private static final String SEMANTICS_CLOSE_DETAILS = "close_role_details_button";

    private static final String VIEW_TEXT = "View";
    private static final String USERS_TEXT = "Users";
    private static final String CLOSE_TEXT = "Close";
    private static final String DELETE_ROLE_TEXT = "Delete Role";

    private static final String PERM_VIEW_LISTING = "permission_checkbox_PERM_VIEW_LISTING";
    private static final String PERM_ADD = "permission_checkbox_PERM_ADD";
    private static final String PERM_DELETE = "permission_checkbox_PERM_DELETE";
    private static final String PERM_VIEW_DETAILS = "permission_checkbox_PERM_VIEW_DETAILS";

    private static final String MODULE_INDIVIDUAL_USERS = "Individual Users";
    private static final String MODULE_COMPANY_USERS = "Company Users";
    private static final String MODULE_INPUT_CATEGORIES = "Input Categories";
    private static final String MODULE_EVENTS = "Events";
    private static final String MODULE_ADVERTISEMENT = "Advertisement";
    private static final String MODULE_TRANSACTIONS = "Transactions";

    private static final String ACTION_VIEW_LISTING = "View Listing";
    private static final String ACTION_ADD = "Add";
    private static final String ACTION_DELETE = "Delete";

    private static final Duration DEFAULT_WAIT = FlutterSemanticsSupport.DEFAULT_WAIT;
    private static final Duration PAGE_POLL = Duration.ofSeconds(12);
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

    private static final String PERMISSION_CHECKBOX_SCRIPT =
            "const normalizeText = (value) => (value || '').replace(/\\s+/g, ' ').trim();" +
                    "function permissionSuffix(permissionLabel) {" +
                    "  const label = permissionLabel || '';" +
                    "  const marker = 'permission_checkbox_';" +
                    "  const idx = label.indexOf(marker);" +
                    "  return idx >= 0 ? label.slice(idx + marker.length) : label;" +
                    "}" +
                    "function labelMatchesPermission(label, permissionLabel) {" +
                    "  if (!label || !label.includes('permission_checkbox')) return false;" +
                    "  const suffix = permissionSuffix(permissionLabel);" +
                    "  return label === permissionLabel || label.includes(suffix);" +
                    "}" +
                    "function findAssignPermissionsRoot() {" +
                    "  const nodes = Array.from(document.querySelectorAll('flt-semantics'));" +
                    "  return nodes.find(node => {" +
                    "    if (isInNav(node)) return false;" +
                    "    const text = normalizeText(node.textContent);" +
                    "    return text.includes('Assign Permissions') || text.includes('Permissions');" +
                    "  }) || null;" +
                    "}" +
                    "function findModuleAnchor(moduleName) {" +
                    "  const module = normalizeText(moduleName);" +
                    "  const root = findAssignPermissionsRoot();" +
                    "  const scopes = [];" +
                    "  if (root) scopes.push(root.parentElement || root);" +
                    "  scopes.push(document.body);" +
                    "  for (const scope of scopes) {" +
                    "    const nodes = Array.from(scope.querySelectorAll('flt-semantics'));" +
                    "    const candidates = nodes.filter(node => {" +
                    "      if (isInNav(node)) return false;" +
                    "      const text = normalizeText(node.textContent);" +
                    "      const label = node.getAttribute('aria-label') || '';" +
                    "      if (label.includes('drawer_item') || label.includes('submenu')) return false;" +
                    "      return text === module || text.startsWith(module + ' ')" +
                    "        || (text.includes(module) && text.length <= module.length + 40);" +
                    "    }).sort((left, right) => normalizeText(left.textContent).length - normalizeText(right.textContent).length);" +
                    "    if (candidates.length) return candidates[0];" +
                    "  }" +
                    "  return null;" +
                    "}" +
                    "function expandModule(moduleName) {" +
                    "  const moduleNode = findModuleAnchor(moduleName);" +
                    "  if (!moduleNode) return false;" +
                    "  const target = moduleNode.closest('flt-semantics[flt-tappable]') || moduleNode;" +
                    "  target.scrollIntoView({block: 'center', inline: 'nearest'});" +
                    "  return dispatchPointerClick(target);" +
                    "}" +
                    "function findPermissionCheckbox(moduleName, permissionLabel, actionText) {" +
                    "  const suffix = permissionSuffix(permissionLabel);" +
                    "  const moduleNode = findModuleAnchor(moduleName);" +
                    "  if (!moduleNode) return null;" +
                    "  let ancestor = moduleNode.parentElement;" +
                    "  for (let depth = 0; depth < 18 && ancestor; depth++) {" +
                    "    const ancestorText = normalizeText(ancestor.textContent);" +
                    "    if (!ancestorText.includes(normalizeText(moduleName))) {" +
                    "      ancestor = ancestor.parentElement;" +
                    "      continue;" +
                    "    }" +
                    "    const checkboxes = Array.from(ancestor.querySelectorAll('flt-semantics[aria-label*=\"permission_checkbox\"]'))" +
                    "      .filter(node => !isInNav(node) && labelMatchesPermission(node.getAttribute('aria-label') || '', permissionLabel));" +
                    "  if (checkboxes.length === 1) return checkboxes[0];" +
                    "    if (checkboxes.length > 1 && actionText) {" +
                    "      const narrowed = checkboxes.filter(node => {" +
                    "        let row = node.parentElement;" +
                    "        for (let i = 0; i < 10 && row; i++) {" +
                    "          const rowText = normalizeText(row.textContent);" +
                    "          if (rowText.includes(normalizeText(actionText))) return true;" +
                    "          row = row.parentElement;" +
                    "        }" +
                    "        return false;" +
                    "      });" +
                    "      if (narrowed.length) return narrowed[0];" +
                    "    }" +
                    "    if (checkboxes.length) return checkboxes[0];" +
                    "    if (actionText) {" +
                    "      const actionRows = Array.from(ancestor.querySelectorAll('flt-semantics')).filter(node => {" +
                    "        const text = normalizeText(node.textContent);" +
                    "        return text === normalizeText(actionText) || text.includes(normalizeText(actionText));" +
                    "      });" +
                    "      for (const row of actionRows) {" +
                    "        let scan = row;" +
                    "        for (let d = 0; d < 8 && scan; d++) {" +
                    "          const cb = Array.from(scan.querySelectorAll('flt-semantics')).find(node => {" +
                    "            const label = node.getAttribute('aria-label') || '';" +
                    "            return labelMatchesPermission(label, permissionLabel);" +
                    "          });" +
                    "          if (cb) return cb;" +
                    "          scan = scan.parentElement;" +
                    "        }" +
                    "      }" +
                    "    }" +
                    "    ancestor = ancestor.parentElement;" +
                    "  }" +
                    "  const global = Array.from(document.querySelectorAll('flt-semantics[aria-label*=\"permission_checkbox\"]'))" +
                    "    .filter(node => !isInNav(node) && labelMatchesPermission(node.getAttribute('aria-label') || '', permissionLabel));" +
                    "  if (global.length === 1) return global[0];" +
                    "  return null;" +
                    "}" +
                    "function isPermissionChecked(node) {" +
                    "  if (!node) return false;" +
                    "  const checked = node.getAttribute('aria-checked');" +
                    "  if (checked === 'true') return true;" +
                    "  const valueText = (node.getAttribute('aria-valuetext') || '').toLowerCase();" +
                    "  return valueText.includes('checked') || valueText.includes('selected');" +
                    "}" +
                    "function clickPermissionAction(moduleName, permissionLabel, actionText) {" +
                    "  const moduleNode = findModuleAnchor(moduleName);" +
                    "  if (!moduleNode) return false;" +
                    "  let ancestor = moduleNode.parentElement;" +
                    "  for (let depth = 0; depth < 18 && ancestor; depth++) {" +
                    "    const ancestorText = normalizeText(ancestor.textContent);" +
                    "    if (!ancestorText.includes(normalizeText(moduleName))) {" +
                    "      ancestor = ancestor.parentElement;" +
                    "      continue;" +
                    "    }" +
                    "    const checkbox = findPermissionCheckbox(moduleName, permissionLabel, actionText);" +
                    "    if (checkbox) {" +
                    "      if (isPermissionChecked(checkbox)) return true;" +
                    "      const cbTarget = checkbox.closest('flt-semantics[flt-tappable]') || checkbox;" +
                    "      if (dispatchPointerClick(cbTarget)) return true;" +
                    "    }" +
                    "    const actionNodes = Array.from(ancestor.querySelectorAll('flt-semantics')).filter(node => {" +
                    "      if (isInNav(node)) return false;" +
                    "      const text = normalizeText(node.textContent);" +
                    "      const wanted = normalizeText(actionText);" +
                    "      return text === wanted || text.endsWith(wanted) || text.includes(wanted);" +
                    "    });" +
                    "    const scored = actionNodes.map(node => {" +
                    "      let points = 0;" +
                    "      const text = normalizeText(node.textContent);" +
                    "      if (text === normalizeText(actionText)) points += 8;" +
                    "      if (node.hasAttribute('flt-tappable')) points += 4;" +
                    "      if (node.getAttribute('role') === 'checkbox') points += 6;" +
                    "      const label = node.getAttribute('aria-label') || '';" +
                    "      if (labelMatchesPermission(label, permissionLabel)) points += 10;" +
                    "      points -= text.length;" +
                    "      return { node, points };" +
                    "    }).sort((a, b) => b.points - a.points);" +
                    "    for (const entry of scored) {" +
                    "      const target = entry.node.closest('flt-semantics[flt-tappable]') || entry.node;" +
                    "      target.scrollIntoView({block: 'center', inline: 'nearest'});" +
                    "      if (dispatchPointerClick(target)) return true;" +
                    "    }" +
                    "    ancestor = ancestor.parentElement;" +
                    "  }" +
                    "  return false;" +
                    "}";

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final FlutterSemanticsSupport semantics;

    public ManageRolesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_WAIT);
        this.semantics = new FlutterSemanticsSupport(driver);
    }

    public void clickUserAccessManagementMenu() {
        semantics.enableFlutterSemantics();
        waitUntil(this::isHomePageLoaded, DEFAULT_WAIT);
        scrollNavigationToUserAccess();
        openNavigationItem(
                SEMANTICS_USER_ACCESS_MENU,
                USER_ACCESS_MANAGEMENT_TEXT,
                "User & Access Management submenu not found (expected semantics "
                        + SEMANTICS_USER_ACCESS_MENU + ")");
    }

    public void clickManageRolesPermissionsMenu() {
        semantics.enableFlutterSemantics();
        openNavigationItem(
                SEMANTICS_MANAGE_ROLES_MENU,
                MANAGE_ROLES_TEXT,
                "Manage Roles & Permissions submenu not found (expected semantics "
                        + SEMANTICS_MANAGE_ROLES_MENU + ")");
        if (!waitUntil(this::isManageRolesPageLoaded, PAGE_POLL)) {
            throw new NoSuchElementException(
                    "Manage Roles & Permissions page did not load (expected semantics "
                            + SEMANTICS_CREATE_ROLE + " or visible text '" + CREATE_ROLE_TEXT + "')");
        }
    }

    public void enterRoleName(String roleName) {
        openCreateRoleForm();
        enterField(SEMANTICS_ROLE_NAME, "Role Name", roleName);
    }

    public void enterDescription(String description) {
        ensureCreateRoleFormReady();
        enterField(SEMANTICS_DESCRIPTION, "Description", description);
    }

    public void checkViewListingIndividualUsers() {
        checkPermission(MODULE_INDIVIDUAL_USERS, PERM_VIEW_LISTING, ACTION_VIEW_LISTING);
    }

    public void scrollDown() {
        ensureCreateRoleFormReady();
        scrollPermissionsSectionDown();
    }

    public void checkViewListingCompanyUsers() {
        checkPermission(MODULE_COMPANY_USERS, PERM_VIEW_LISTING, ACTION_VIEW_LISTING);
    }

    public void checkAddInputCategories() {
        checkPermission(MODULE_INPUT_CATEGORIES, PERM_ADD, ACTION_ADD);
    }

    public void checkDeleteEvents() {
        checkPermission(MODULE_EVENTS, PERM_DELETE, ACTION_DELETE);
    }

    public void checkViewListingAdvertisement() {
        checkPermission(MODULE_ADVERTISEMENT, PERM_VIEW_DETAILS, ACTION_VIEW_LISTING);
    }

    public void checkViewListingTransactions() {
        checkPermission(MODULE_TRANSACTIONS, PERM_VIEW_LISTING, ACTION_VIEW_LISTING);
    }

    public void clickCreateRole() {
        ensureCreateRoleFormReady();
        scrollToSemanticsLabel(SEMANTICS_CREATE_CONFIRM);
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            if (attempt > 0) {
                scrollToSemanticsLabel(SEMANTICS_CREATE_CONFIRM);
            }
            if (clickSemanticsAction(SEMANTICS_CREATE_CONFIRM, CREATE_ROLE_TEXT)) {
                return;
            }
            semantics.pauseAfterScroll();
        }
        throw new NoSuchElementException(
                "Create Role confirm button not found (expected semantics " + SEMANTICS_CREATE_CONFIRM + ")");
    }

    public void clickSearchRole() {
        ensureManageRolesListReady();
        scrollToSemanticsLabel(SEMANTICS_ROLE_SEARCH);
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            if (attempt > 0) {
                scrollToSemanticsLabel(SEMANTICS_ROLE_SEARCH);
            }
            if (focusRoleSearchField()) {
                return;
            }
            semantics.pauseAfterScroll();
        }
        throw new NoSuchElementException(
                "Role search field not found (expected semantics " + SEMANTICS_ROLE_SEARCH + ")");
    }

    public void enterRoleSearchName(String roleName) {
        ensureManageRolesListReady();
        scrollToSemanticsLabel(SEMANTICS_ROLE_SEARCH);
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            WebElement input = findRoleSearchInput();
            if (input != null) {
                typeExactValue(input, roleName);
                return;
            }
            scrollToSemanticsLabel(SEMANTICS_ROLE_SEARCH);
            semantics.pauseAfterScroll();
        }
        throw new NoSuchElementException(
                "Role search field not found for entering name (expected semantics " + SEMANTICS_ROLE_SEARCH + ")");
    }

    public void clickViewRole() {
        clickRoleRowAction(SEMANTICS_ROLE_VIEW, VIEW_TEXT);
    }

    public void clickUsersRole() {
        clickRoleRowActionByVisibleText(USERS_TEXT);
    }

    public void clickDeleteRole() {
        clickRoleRowAction(SEMANTICS_ROLE_DELETE, "Delete");
    }

    public void clickCloseRoleDetails() {
        semantics.enableFlutterSemantics();
        scrollToSemanticsLabel(SEMANTICS_CLOSE_DETAILS);
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            if (attempt > 0) {
                scrollToSemanticsLabel(SEMANTICS_CLOSE_DETAILS);
            }
            if (clickSemanticsAction(SEMANTICS_CLOSE_DETAILS, CLOSE_TEXT)) {
                return;
            }
            semantics.pauseAfterScroll();
        }
        throw new NoSuchElementException(
                "Close role details button not found (expected semantics " + SEMANTICS_CLOSE_DETAILS + ")");
    }

    public void confirmDeleteRole() {
        semantics.enableFlutterSemantics();
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            if (clickSemanticsAction("confirm_delete_button", DELETE_ROLE_TEXT)) {
                return;
            }
            if (clickRoleRowActionByVisibleText(DELETE_ROLE_TEXT)) {
                return;
            }
            if (semantics.clickVisibleText(DELETE_ROLE_TEXT)) {
                return;
            }
            semantics.pauseAfterScroll();
        }
        throw new NoSuchElementException(
                "Delete Role confirmation button not found (expected visible text '" + DELETE_ROLE_TEXT + "')");
    }

    private void ensureManageRolesListReady() {
        semantics.enableFlutterSemantics();
        if (!waitUntil(this::isManageRolesPageLoaded, PAGE_POLL)) {
            throw new NoSuchElementException(
                    "Manage Roles & Permissions page did not load (expected semantics "
                            + SEMANTICS_CREATE_ROLE + ", " + SEMANTICS_ROLE_SEARCH + ", or role row actions)");
        }
    }

    private void clickRoleRowAction(String semanticsLabel, String visibleText) {
        ensureManageRolesListReady();
        scrollToSemanticsLabel(semanticsLabel);
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            if (attempt > 0) {
                scrollToSemanticsLabel(semanticsLabel);
            }
            if (clickSemanticsAction(semanticsLabel, visibleText)) {
                return;
            }
            if (clickRoleRowActionByVisibleText(visibleText)) {
                return;
            }
            semantics.pauseAfterScroll();
        }
        throw new NoSuchElementException(
                "Role row action not found (expected semantics " + semanticsLabel
                        + " or visible text '" + visibleText + "')");
    }

    private boolean clickRoleRowActionByVisibleText(String visibleText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "const wanted = (arguments[0] || '').replace(/\\s+/g, ' ').trim();" +
                        "const nodes = Array.from(document.querySelectorAll('flt-semantics[flt-tappable]'))" +
                        "  .filter(node => !isInNav(node));" +
                        "const target = nodes.find(node => {" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const label = node.getAttribute('aria-label') || '';" +
                        "  if (label.includes('role_delete_button') || label.includes('confirm_delete')) return false;" +
                        "  return text === wanted;" +
                        "});" +
                        "return dispatchPointerClick(target);",
                visibleText
        ));
    }

    private boolean focusRoleSearchField() {
        WebElement input = findRoleSearchInput();
        if (input == null) {
            if (clickSemanticsAction(SEMANTICS_ROLE_SEARCH, "Search")) {
                input = findRoleSearchInput();
            }
        }
        if (input == null) {
            return false;
        }
        semantics.scrollIntoView(input);
        input.click();
        return true;
    }

    private WebElement findRoleSearchInput() {
        for (String label : new String[]{SEMANTICS_ROLE_SEARCH, "Search", "Search Role", "Role Name"}) {
            if (!driver.findElements(semanticsFieldInput(label)).isEmpty()) {
                return driver.findElement(semanticsFieldInput(label));
            }
            if (!driver.findElements(inputField(label)).isEmpty()) {
                return driver.findElement(inputField(label));
            }
        }
        return (WebElement) ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "return findOutsideNav('flt-semantics[aria-label=\"' + arguments[0] + '\"] input')" +
                        "  || findOutsideNav('flt-semantics[aria-label*=\"' + arguments[0] + '\"] input')" +
                        "  || findOutsideNav('input[aria-label=\"' + arguments[0] + '\"]');",
                SEMANTICS_ROLE_SEARCH
        );
    }

    private void openCreateRoleForm() {
        semantics.enableFlutterSemantics();
        if (isCreateRoleFormLoaded(driver)) {
            return;
        }
        if (!isManageRolesPageLoaded(driver)) {
            throw new NoSuchElementException(
                    "Manage Roles & Permissions page is not loaded before entering role details (expected "
                            + SEMANTICS_CREATE_ROLE + ")");
        }
        clickCreateRoleButton();
        if (!waitUntil(this::isCreateRoleFormLoaded, PAGE_POLL)) {
            logCreateRoleFormSemantics();
            throw new NoSuchElementException(
                    "Create role form did not load (expected fields " + SEMANTICS_ROLE_NAME
                            + " and " + SEMANTICS_DESCRIPTION + ")");
        }
    }

    private void ensureCreateRoleFormReady() {
        if (!isCreateRoleFormLoaded(driver)) {
            openCreateRoleForm();
        }
    }

    private void clickCreateRoleButton() {
        scrollToSemanticsLabel(SEMANTICS_CREATE_ROLE);
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            if (attempt > 0) {
                scrollToSemanticsLabel(SEMANTICS_CREATE_ROLE);
            }
            if (clickSemanticsAction(SEMANTICS_CREATE_ROLE, CREATE_ROLE_TEXT)) {
                semantics.pauseAfterScroll();
                if (waitUntil(this::isCreateRoleFormLoaded, DEFAULT_WAIT)) {
                    return;
                }
            }
            semantics.pauseAfterScroll();
        }
        throw new NoSuchElementException(
                "Create Role button not found or form did not open (expected semantics " + SEMANTICS_CREATE_ROLE + ")");
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

    private void checkPermission(String moduleName, String permissionLabel, String actionText) {
        ensureCreateRoleFormReady();
        scrollPermissionsSectionIntoView();
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            expandPermissionModule(moduleName);
            scrollPermissionIntoView(moduleName, permissionLabel, actionText);
            if (clickPermissionCheckbox(moduleName, permissionLabel, actionText)) {
                return;
            }
            scrollPermissionsSectionDown();
            semantics.pauseAfterScroll();
        }
        logPermissionSemantics(moduleName, permissionLabel);
        throw new NoSuchElementException(
                "Permission checkbox not found for module '" + moduleName + "', action '" + actionText
                        + "' (expected semantics " + permissionLabel + ")");
    }

    private void expandPermissionModule(String moduleName) {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        PERMISSION_CHECKBOX_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "return expandModule(arguments[0]);",
                moduleName
        );
        semantics.pauseAfterScroll();
    }

    private void scrollPermissionsSectionIntoView() {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        PERMISSION_CHECKBOX_SCRIPT +
                        "const root = findAssignPermissionsRoot();" +
                        "if (root) root.scrollIntoView({block: 'start', inline: 'nearest'});"
        );
        semantics.pauseAfterScroll();
    }

    private void scrollPermissionsSectionDown() {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        PERMISSION_CHECKBOX_SCRIPT +
                        "const root = findAssignPermissionsRoot();" +
                        "const delta = Math.max(window.innerHeight * 0.45, 220);" +
                        "if (root) {" +
                        "  let ancestor = root.parentElement;" +
                        "  for (let depth = 0; depth < 10 && ancestor; depth++) {" +
                        "    const style = window.getComputedStyle(ancestor);" +
                        "    const oy = style.overflowY;" +
                        "    if ((oy === 'auto' || oy === 'scroll') && ancestor.scrollHeight > ancestor.clientHeight + 8) {" +
                        "      ancestor.scrollTop += delta;" +
                        "    }" +
                        "    ancestor = ancestor.parentElement;" +
                        "  }" +
                        "}" +
                        "window.scrollBy(0, delta);"
        );
        semantics.pauseAfterScroll();
    }

    private void logPermissionSemantics(String moduleName, String permissionLabel) {
        Object labels = ((JavascriptExecutor) driver).executeScript(
                "const wantedModule = (arguments[0] || '').toLowerCase();" +
                        "const wantedPermission = (arguments[1] || '').toLowerCase();" +
                        "return Array.from(document.querySelectorAll('flt-semantics'))" +
                        "  .map(node => {" +
                        "    const label = (node.getAttribute('aria-label') || '').trim();" +
                        "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().slice(0, 60);" +
                        "    return label ? label : text;" +
                        "  })" +
                        "  .filter(value => value && (" +
                        "    value.toLowerCase().includes('permission')" +
                        "    || value.toLowerCase().includes('perm_')" +
                        "    || value.toLowerCase().includes(wantedModule)" +
                        "    || value.toLowerCase().includes('view listing')" +
                        "    || value.toLowerCase().includes('assign')))" +
                        "  .slice(0, 80);",
                moduleName,
                permissionLabel
        );
        System.out.println("Permission semantics for " + moduleName + ": " + labels);
    }

    private boolean clickPermissionCheckbox(String moduleName, String permissionLabel, String actionText) {
        semantics.enableFlutterSemantics();
        Boolean clicked = (Boolean) ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        PERMISSION_CHECKBOX_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "return clickPermissionAction(arguments[0], arguments[1], arguments[2]);",
                moduleName,
                permissionLabel,
                actionText
        );
        if (Boolean.TRUE.equals(clicked)) {
            return true;
        }
        if (semantics.clickSemanticsLabelViaScript(permissionLabel)) {
            return true;
        }
        return semantics.clickVisibleText(actionText);
    }

    private void scrollPermissionIntoView(String moduleName, String permissionLabel, String actionText) {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        PERMISSION_CHECKBOX_SCRIPT +
                        "const moduleName = arguments[0];" +
                        "const permissionLabel = arguments[1];" +
                        "const actionText = arguments[2];" +
                        "const node = findPermissionCheckbox(moduleName, permissionLabel, actionText)" +
                        "  || findModuleAnchor(moduleName);" +
                        "if (node) node.scrollIntoView({block: 'center', inline: 'nearest'});",
                moduleName,
                permissionLabel,
                actionText
        );
        semantics.pauseAfterScroll();
    }

    private void enterField(String semanticsLabel, String fallbackLabel, String value) {
        semantics.enableFlutterSemantics();
        scrollToFieldLabel(semanticsLabel, fallbackLabel);
        WebElement input = findFormInput(semanticsLabel, fallbackLabel);
        typeExactValue(input, value);
    }

    private WebElement findFormInput(String semanticsLabel, String fallbackLabel) {
        for (String label : new String[]{semanticsLabel, fallbackLabel}) {
            By wrappedInput = semanticsFieldInput(label);
            if (!driver.findElements(wrappedInput).isEmpty()) {
                return wait.until(ExpectedConditions.elementToBeClickable(wrappedInput));
            }
            By directInput = inputField(label);
            if (!driver.findElements(directInput).isEmpty()) {
                return wait.until(ExpectedConditions.elementToBeClickable(directInput));
            }
        }
        WebElement fromScript = findFormInputViaScript(semanticsLabel, fallbackLabel);
        if (fromScript != null) {
            return wait.until(ExpectedConditions.elementToBeClickable(fromScript));
        }
        logCreateRoleFormSemantics();
        throw new NoSuchElementException(
                "Form input not found for field: " + semanticsLabel + " / " + fallbackLabel);
    }

    private WebElement findFormInputViaScript(String semanticsLabel, String fallbackLabel) {
        return (WebElement) ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const normalizeText = (value) => (value || '').replace(/\\s+/g, ' ').trim();" +
                        "const labels = [arguments[0], arguments[1]];" +
                        "for (const label of labels) {" +
                        "  const field = findOutsideNav('flt-semantics[aria-label=\"' + label + '\"]')" +
                        "    || findOutsideNav('flt-semantics[aria-label*=\"' + label + '\"]');" +
                        "  if (field) {" +
                        "    const input = field.querySelector('input, textarea') || field;" +
                        "    if (input) return input;" +
                        "  }" +
                        "  const direct = document.querySelector('input[aria-label=\"' + label + '\"]')" +
                        "    || document.querySelector('textarea[aria-label=\"' + label + '\"]');" +
                        "  if (direct && !isInNav(direct)) return direct;" +
                        "}" +
                        "const fieldOrder = { name_field: 0, role_name_0: 1 };" +
                        "const labelHints = {" +
                        "  name_field: ['Role Name', 'Name', 'Enter Role Name']," +
                        "  role_name_0: ['Description', 'Role Description', 'Enter Description']" +
                        "};" +
                        "const allInputs = Array.from(document.querySelectorAll(" +
                        "  'input[data-semantics-role=\"text-field\"], flt-semantics input, textarea'))" +
                        "  .filter(el => !isInNav(el));" +
                        "const index = fieldOrder[arguments[0]];" +
                        "if (index !== undefined && allInputs[index]) return allInputs[index];" +
                        "for (const hint of (labelHints[arguments[0]] || [])) {" +
                        "  const marker = Array.from(document.querySelectorAll('flt-semantics'))" +
                        "    .find(node => !isInNav(node) && normalizeText(node.textContent).includes(hint));" +
                        "  if (!marker) continue;" +
                        "  const nearbyInput = marker.querySelector('input, textarea')" +
                        "    || marker.parentElement?.querySelector('input, textarea')" +
                        "    || marker.closest('flt-semantics')?.querySelector('input, textarea');" +
                        "  if (nearbyInput && !isInNav(nearbyInput)) return nearbyInput;" +
                        "}" +
                        "return null;",
                semanticsLabel,
                fallbackLabel
        );
    }

    private void typeExactValue(WebElement input, String value) {
        semantics.scrollIntoView(input);
        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(value);
        input.sendKeys(Keys.TAB);
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
                        "let node = findOutsideNav('flt-semantics[aria-label=\"' + semanticsLabel + '\"]')" +
                        "  || findOutsideNav('flt-semantics[aria-label*=\"' + semanticsLabel + '\"]');" +
                        "if (!node) {" +
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
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const labels = [arguments[0], arguments[1]];" +
                        "for (const label of labels) {" +
                        "  const anchor = findOutsideNav('flt-semantics[aria-label=\"' + label + '\"]')" +
                        "    || document.querySelector('input[aria-label=\"' + label + '\"]')" +
                        "    || document.querySelector('textarea[aria-label=\"' + label + '\"]');" +
                        "  if (anchor && !isInNav(anchor)) {" +
                        "    anchor.scrollIntoView({block: 'center', inline: 'nearest'});" +
                        "    break;" +
                        "  }" +
                        "}",
                semanticsLabel,
                fallbackLabel
        );
        semantics.pauseAfterScroll();
    }

    private boolean isHomePageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String text = semantics.getSemanticsText(webDriver);
        return text != null
                && (text.contains("Individual Users")
                || text.contains("Company Users")
                || text.contains("Navigation menu"));
    }

    private boolean isManageRolesPageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (!webDriver.findElements(semanticsLocator(SEMANTICS_CREATE_ROLE)).isEmpty()) {
            return true;
        }
        if (!webDriver.findElements(semanticsLocator(SEMANTICS_ROLE_SEARCH)).isEmpty()) {
            return true;
        }
        if (!webDriver.findElements(semanticsLocator(SEMANTICS_ROLE_VIEW)).isEmpty()) {
            return true;
        }
        if (!webDriver.findElements(semanticsLocator(SEMANTICS_ROLE_DELETE)).isEmpty()) {
            return true;
        }
        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        return pageText.contains(semantics.normalizeText(MANAGE_ROLES_TEXT))
                || pageText.contains("manage roles")
                || pageText.contains(semantics.normalizeText(CREATE_ROLE_TEXT));
    }

    private boolean isCreateRoleFormLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return isInputFieldPresent(webDriver, SEMANTICS_ROLE_NAME, "Role Name")
                || isInputFieldPresent(webDriver, SEMANTICS_DESCRIPTION, "Description")
                || Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const inputs = Array.from(document.querySelectorAll(" +
                        "  'input[data-semantics-role=\"text-field\"], flt-semantics input, textarea'))" +
                        "  .filter(el => !isInNav(el));" +
                        "if (inputs.length < 1) return false;" +
                        "const hasRoleField = !!document.querySelector('flt-semantics[aria-label=\"name_field\"]')" +
                        "  || !!document.querySelector('flt-semantics[aria-label=\"role_name_0\"]');" +
                        "if (hasRoleField) return true;" +
                        "const pageText = (document.querySelector('flt-semantics-host')?.textContent || '')" +
                        "  .replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "return pageText.includes('assign permissions')" +
                        "  && (pageText.includes('role name') || pageText.includes('description'))" +
                        "  && inputs.length >= 2;"
        ));
    }

    private void logCreateRoleFormSemantics() {
        Object labels = ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics, input, textarea'))" +
                        "  .map(node => (node.getAttribute('aria-label') || node.tagName || '')" +
                        "    + ':' + (node.textContent || '').trim().slice(0, 40))" +
                        "  .filter(label => label && (" +
                        "    label.toLowerCase().includes('role')" +
                        "    || label.toLowerCase().includes('name_field')" +
                        "    || label.toLowerCase().includes('permission')" +
                        "    || label.toLowerCase().includes('create')))" +
                        "  .slice(0, 50);"
        );
        System.out.println("Create role form related semantics on failure: " + labels);
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
