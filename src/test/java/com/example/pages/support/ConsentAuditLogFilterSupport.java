package com.example.pages.support;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.List;

/**
 * Consent Audit Log filter dropdown interactions (Apps, Categories, Actions),
 * following the same semantics + visible-text pattern as Active/Inactive status filters.
 */
public class ConsentAuditLogFilterSupport {

    private static final Duration MENU_WAIT = Duration.ofSeconds(8);

    private static final String SEARCH_FIELD = "usersearch_field";

    private static final String SEMANTICS_ALL_APPS = "tenant_app_dropdown";
    private static final String SEMANTICS_VIRTUAL_INTROS = "tenant_app_dropdown_option_virtual_intros";
    private static final String SEMANTICS_CATEGORY_DROPDOWN = "category_dropdown";
    private static final String SEMANTICS_ALL_CATEGORIES = "category_option_all_categories";
    private static final String SEMANTICS_PRIVACY_POLICY = "category_option_privacy_policy";
    private static final String SEMANTICS_TERMS_OF_SERVICE = "category_option_terms_of_service";
    private static final String SEMANTICS_MARKETING_COMMUNICATION = "category_option_marketing_communication";
    private static final String SEMANTICS_CUSTOMISE_RECOMMENDATION = "category_option_customise_recommendation";
    private static final String SEMANTICS_NOTIFICATION = "category_option_notification";
    private static final String SEMANTICS_LOCATION = "category_option_location";
    private static final String SEMANTICS_GENDER = "category_option_gender";
    private static final String SEMANTICS_ETHNICITY = "category_option_ethnicity";
    private static final String SEMANTICS_ALL_ACTIONS = "action_dropdown_option_all_actions";
    private static final String SEMANTICS_PENDING = "action_dropdown_action_pending";
    private static final String SEMANTICS_GRANTED = "action_dropdown_action_granted";
    private static final String SEMANTICS_WITHDRAWN = "action_dropdown_option_withdrawn";

    private static final String ALL_APPS_TEXT = "All Apps";
    private static final String ALL_CATEGORIES_TEXT = "All Categories";
    private static final String ALL_ACTIONS_TEXT = "All Actions";

    private static final DropdownConfig APPS_DROPDOWN = new DropdownConfig(
            new String[]{SEMANTICS_ALL_APPS},
            ALL_APPS_TEXT,
            new String[]{SEMANTICS_VIRTUAL_INTROS},
            new String[]{"Virtual Intros"}
    );
    private static final DropdownConfig CATEGORIES_DROPDOWN = new DropdownConfig(
            new String[]{SEMANTICS_CATEGORY_DROPDOWN, SEMANTICS_ALL_CATEGORIES},
            ALL_CATEGORIES_TEXT,
            new String[]{
                    SEMANTICS_PRIVACY_POLICY,
                    SEMANTICS_TERMS_OF_SERVICE,
                    SEMANTICS_MARKETING_COMMUNICATION,
                    SEMANTICS_CUSTOMISE_RECOMMENDATION,
                    SEMANTICS_NOTIFICATION,
                    SEMANTICS_LOCATION,
                    SEMANTICS_GENDER,
                    SEMANTICS_ETHNICITY
            },
            new String[]{"Privacy Policy", "Terms of Service"}
    );
    private static final DropdownConfig ACTIONS_DROPDOWN = new DropdownConfig(
            new String[]{SEMANTICS_ALL_ACTIONS, ALL_ACTIONS_TEXT},
            ALL_ACTIONS_TEXT,
            new String[]{
                    SEMANTICS_PENDING, "PENDING",
                    SEMANTICS_GRANTED, "GRANTED",
                    SEMANTICS_WITHDRAWN, "WITHDRAWN"
            },
            new String[]{"Pending", "Granted", "Withdrawn"}
    );

    private static String popupMenuHelperJs() {
        return "const normalizeText = (value) => (value || '').replace(/\\u00A0/g, ' ').trim();" +
                "const nodeVisibleText = (node) => {" +
                "  if (!node) return '';" +
                "  return normalizeText(node.getAttribute('aria-label')) || normalizeText(node.textContent);" +
                "};" +
                "const matchesVisibleText = (node, target) => {" +
                "  if (!target) return false;" +
                "  return nodeVisibleText(node) === normalizeText(target);" +
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
                "};";
    }

    private final WebDriver driver;
    private final FlutterSemanticsSupport semantics;

    private final By searchBarSemantics = By.xpath(
            "//flt-semantics[@aria-label='" + SEARCH_FIELD + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEARCH_FIELD + "')]");

    public ConsentAuditLogFilterSupport(WebDriver driver, FlutterSemanticsSupport semantics) {
        this.driver = driver;
        this.semantics = semantics;
    }

    public void prepareFilterArea() {
        semantics.enableFlutterSemantics();
        scrollFilterAreaIntoView();
    }

    public void openAppsDropdown() {
        openDropdown(APPS_DROPDOWN);
    }

    public void openCategoriesDropdown() {
        openDropdown(CATEGORIES_DROPDOWN);
    }

    public void openActionsDropdown() {
        semantics.enableFlutterSemantics();
        prepareFilterArea();
        if (isDropdownMenuOpen(ACTIONS_DROPDOWN)) {
            return;
        }
        for (int attempt = 0; attempt < 10; attempt++) {
            if (isDropdownMenuOpen(ACTIONS_DROPDOWN)) {
                return;
            }
            if (clickFilterBarVisibleText(ALL_ACTIONS_TEXT)
                    || semantics.clickVisibleText(ALL_ACTIONS_TEXT)
                    || semantics.clickSemanticsLabelViaScript(SEMANTICS_ALL_ACTIONS)
                    || semantics.clickSemanticsLabelViaScript(ALL_ACTIONS_TEXT)) {
                semantics.pauseAfterScroll();
                if (semantics.waitUntil(d -> isDropdownMenuOpen(ACTIONS_DROPDOWN), MENU_WAIT)) {
                    return;
                }
            }
            scrollFilterAreaRightOnce();
            semantics.pauseAfterScroll();
        }
        throw new NoSuchElementException("Dropdown did not open: " + ALL_ACTIONS_TEXT);
    }

    private boolean clickFilterBarVisibleText(String visibleText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() +
                        "const wanted = normalizeText(arguments[0]);" +
                        "const search = document.querySelector('flt-semantics[aria-label=\"usersearch_field\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label=\"user_search_field\"]');" +
                        "const searchRect = search ? search.getBoundingClientRect() : null;" +
                        "const navMenu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
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
                        "const inFilterBar = (node) => {" +
                        "  if (!node) return false;" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "  if (navMenu && navMenu.contains(node)) return false;" +
                        "  if (hasPopupMenu()) {" +
                        "    const root = popupMenuRoot();" +
                        "    if (root && root.contains(node)) return false;" +
                        "  }" +
                        "  if (!searchRect) return rect.left >= 0 && rect.right <= window.innerWidth;" +
                        "  return rect.top >= searchRect.top - 120 && rect.top <= searchRect.bottom + 120;" +
                        "};" +
                        "const matches = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  if (!inFilterBar(node)) return false;" +
                        "  const text = normalizeText(node.textContent);" +
                        "  const label = normalizeText(node.getAttribute('aria-label'));" +
                        "  return text === wanted || label === wanted;" +
                        "});" +
                        "for (const node of matches) {" +
                        "  if (clickNode(node)) return true;" +
                        "}" +
                        "return false;",
                visibleText
        ));
    }

    private boolean clickActionsTriggerBySweep() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const search = document.querySelector('flt-semantics[aria-label=\"usersearch_field\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label=\"user_search_field\"]');" +
                        "if (!search) return false;" +
                        "const searchRect = search.getBoundingClientRect();" +
                        "const y = searchRect.top + searchRect.height / 2;" +
                        "const clickPoint = (x, yCoord) => {" +
                        "  const hit = document.elementFromPoint(x, yCoord);" +
                        "  if (!hit) return false;" +
                        "  ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "    hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: yCoord }));" +
                        "  });" +
                        "  return true;" +
                        "};" +
                        "for (let x = window.innerWidth - 48; x >= Math.max(8, searchRect.left - 720); x -= 56) {" +
                        "  clickPoint(x, y);" +
                        "}" +
                        "return true;"
        ));
    }

    private boolean clickActionsTriggerByCoordinates() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const search = document.querySelector('flt-semantics[aria-label=\"usersearch_field\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label=\"user_search_field\"]');" +
                        "if (!search) return false;" +
                        "const searchRect = search.getBoundingClientRect();" +
                        "const y = searchRect.top + searchRect.height / 2;" +
                        "const clickPoint = (x, yCoord) => {" +
                        "  const hit = document.elementFromPoint(x, yCoord);" +
                        "  if (!hit) return false;" +
                        "  ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "    hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: yCoord }));" +
                        "  });" +
                        "  return true;" +
                        "};" +
                        "for (const offset of [-80, -120, -160, -200, -240, -280, -320, -360, -400]) {" +
                        "  if (clickPoint(Math.max(8, searchRect.left + offset), y)) return true;" +
                        "}" +
                        "for (const offset of [-80, -120, -160, -200, -240]) {" +
                        "  if (clickPoint(Math.max(8, searchRect.right + offset), y)) return true;" +
                        "}" +
                        "for (const offset of [80, 140, 200, 260, 320, 380, 440, 500, 560, 620]) {" +
                        "  if (clickPoint(Math.min(window.innerWidth - 8, searchRect.right + offset), y)) return true;" +
                        "}" +
                        "for (const offset of [-80, -140, -200, -260, -320, -380, -440, -500, -560]) {" +
                        "  if (clickPoint(Math.max(8, searchRect.left + offset), y)) return true;" +
                        "}" +
                        "return false;"
        ));
    }

    private void scrollUntilActionsTriggerVisible() {
        for (int attempt = 0; attempt < 18; attempt++) {
            if (isActionsFilterTriggerVisible()) {
                return;
            }
            scrollFilterAreaRightOnce();
            semantics.pauseAfterScroll();
        }
    }

    private boolean isActionsFilterTriggerVisible() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() +
                        "const isActionMenuOption = (label) => {" +
                        "  return label === 'action_dropdown_action_pending'" +
                        "    || label === 'action_dropdown_action_granted'" +
                        "    || label === 'action_dropdown_option_withdrawn';" +
                        "};" +
                        "const isVisible = (node) => {" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  return rect.width > 0 && rect.height > 0;" +
                        "};" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = normalizeText(node.textContent);" +
                        "  if (hasPopupMenu() && isActionMenuOption(label)) return false;" +
                        "  if (label.includes('action_dropdown_option_all_actions')) return isVisible(node);" +
                        "  return text === 'All Actions' && !hasPopupMenu() && isVisible(node);" +
                        "});"
        ));
    }

    private boolean clickActionsTriggerRelativeToCategories() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() +
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
                        "const isActionMenuOption = (label) => {" +
                        "  return label === 'action_dropdown_action_pending'" +
                        "    || label === 'action_dropdown_action_granted'" +
                        "    || label === 'action_dropdown_option_withdrawn';" +
                        "};" +
                        "const findCategories = () => {" +
                        "  for (const label of ['category_dropdown', 'category_option_all_categories']) {" +
                        "    const node = document.querySelector('flt-semantics[aria-label=\"' + label + '\"]');" +
                        "    if (node) return node;" +
                        "  }" +
                        "  return Array.from(document.querySelectorAll('flt-semantics')).find(" +
                        "    node => normalizeText(node.textContent) === 'All Categories');" +
                        "};" +
                        "const categories = findCategories();" +
                        "if (!categories) return false;" +
                        "const catRect = categories.getBoundingClientRect();" +
                        "const candidates = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = normalizeText(node.textContent);" +
                        "  if (hasPopupMenu() && isActionMenuOption(label)) return false;" +
                        "  const isActions = label.includes('action_dropdown_option_all_actions')" +
                        "    || text === 'All Actions';" +
                        "  if (!isActions || isActionMenuOption(label)) return false;" +
                        "  return rect.top >= catRect.top - 60 && rect.top <= catRect.bottom + 60;" +
                        "});" +
                        "candidates.sort((a, b) => a.getBoundingClientRect().left - b.getBoundingClientRect().left);" +
                        "for (const candidate of candidates) {" +
                        "  const rect = candidate.getBoundingClientRect();" +
                        "  if (rect.left >= catRect.left - 20 && clickNode(candidate)) return true;" +
                        "}" +
                        "return false;"
        ));
    }

    private boolean tryOpenActionsDropdown() {
        for (String triggerLabel : ACTIONS_DROPDOWN.triggerLabels()) {
            if (semantics.clickSemanticsLabelWithoutEnabling(triggerLabel)
                    || semantics.clickSemanticsLabelViaScript(triggerLabel)) {
                return true;
            }
        }
        List<WebElement> triggers = driver.findElements(filterTriggerLocator(ACTIONS_DROPDOWN));
        if (!triggers.isEmpty()) {
            WebElement trigger = triggers.get(triggers.size() - 1);
            semantics.scrollIntoView(trigger);
            if (semantics.clickElementReliably(trigger)) {
                return true;
            }
        }
        if (clickFilterTriggerViaScript(ACTIONS_DROPDOWN)) {
            return true;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() +
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
                        "const isActionMenuOption = (label) => {" +
                        "  return label === 'action_dropdown_action_pending'" +
                        "    || label === 'action_dropdown_action_granted'" +
                        "    || label === 'action_dropdown_option_withdrawn';" +
                        "};" +
                        "const wanted = 'action_dropdown_option_all_actions';" +
                        "const byLabel = document.querySelector('flt-semantics[aria-label=\"' + wanted + '\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label*=\"' + wanted + '\"]');" +
                        "if (byLabel && clickNode(byLabel)) return true;" +
                        "const search = document.querySelector('flt-semantics[aria-label=\"usersearch_field\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label=\"user_search_field\"]');" +
                        "const searchRect = search ? search.getBoundingClientRect() : null;" +
                        "const matches = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = normalizeText(node.textContent);" +
                        "  if (hasPopupMenu() && isActionMenuOption(label)) return false;" +
                        "  if (label.includes('action_dropdown_option_all_actions')) return true;" +
                        "  return text === 'All Actions' && !hasPopupMenu();" +
                        "});" +
                        "if (searchRect) {" +
                        "  matches.sort((a, b) => b.getBoundingClientRect().left - a.getBoundingClientRect().left);" +
                        "}" +
                        "for (const match of matches) {" +
                        "  if (clickNode(match)) return true;" +
                        "}" +
                        "if (searchRect) {" +
                        "  const y = searchRect.top + searchRect.height / 2;" +
                        "  for (const x of [window.innerWidth - 40, window.innerWidth - 100, window.innerWidth - 160]) {" +
                        "    const hit = document.elementFromPoint(x, y);" +
                        "    if (hit) {" +
                        "      ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "        hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                        "      });" +
                        "      return true;" +
                        "    }" +
                        "  }" +
                        "}" +
                        "return false;"
        ));
    }

    public void selectVirtualIntros() {
        selectOption(APPS_DROPDOWN, option(SEMANTICS_VIRTUAL_INTROS, "Virtual Intros"));
    }

    public void selectPrivacyPolicy() {
        selectOption(CATEGORIES_DROPDOWN, option(SEMANTICS_PRIVACY_POLICY, "Privacy Policy"));
    }

    public void selectTermsOfService() {
        selectOption(CATEGORIES_DROPDOWN, option(SEMANTICS_TERMS_OF_SERVICE, "Terms of Service"));
    }

    public void selectMarketingCommunication() {
        selectOption(
                CATEGORIES_DROPDOWN,
                option(SEMANTICS_MARKETING_COMMUNICATION, "Marketing Communication", "Marketing Communications")
        );
    }

    public void selectCustomiseRecommendation() {
        selectOption(
                CATEGORIES_DROPDOWN,
                option(SEMANTICS_CUSTOMISE_RECOMMENDATION, "Customise Recommendation", "Customize Recommendation")
        );
    }

    public void selectNotification() {
        selectOption(CATEGORIES_DROPDOWN, option(SEMANTICS_NOTIFICATION, "Notification", "Notifications"));
    }

    public void selectLocation() {
        selectOption(CATEGORIES_DROPDOWN, option(SEMANTICS_LOCATION, "Location"));
    }

    public void selectGender() {
        selectOption(CATEGORIES_DROPDOWN, option(SEMANTICS_GENDER, "Gender"));
    }

    public void selectEthnicity() {
        selectOption(CATEGORIES_DROPDOWN, option(SEMANTICS_ETHNICITY, "Ethnicity"));
    }

    public void selectPending() {
        selectOption(ACTIONS_DROPDOWN, option(SEMANTICS_PENDING, "Pending"));
    }

    public void selectGranted() {
        selectOption(ACTIONS_DROPDOWN, option(SEMANTICS_GRANTED, "Granted"));
    }

    public void selectWithdrawn() {
        selectOption(ACTIONS_DROPDOWN, option(SEMANTICS_WITHDRAWN, "Withdrawn"));
    }

    public boolean isFilterSelectionVisible(String expectedText, String... alternateTexts) {
        if (isSelectionVisibleInFilterBar(expectedText)) {
            return true;
        }
        String semanticsLabel = semanticsLabelForVisibleText(expectedText);
        if (semanticsLabel != null && isSelectionSemanticsVisibleInFilterBar(semanticsLabel)) {
            return true;
        }
        if (semanticsLabel != null && isSemanticsLabelVisible(semanticsLabel)) {
            return true;
        }
        if (isVisibleFilterText(expectedText)) {
            return true;
        }
        if (alternateTexts == null) {
            return false;
        }
        for (String alternateText : alternateTexts) {
            if (alternateText != null && !alternateText.isBlank()) {
                if (isSelectionVisibleInFilterBar(alternateText)) {
                    return true;
                }
                String alternateSemantics = semanticsLabelForVisibleText(alternateText);
                if (alternateSemantics != null && isSelectionSemanticsVisibleInFilterBar(alternateSemantics)) {
                    return true;
                }
                if (alternateSemantics != null && isSemanticsLabelVisible(alternateSemantics)) {
                    return true;
                }
                if (isVisibleFilterText(alternateText)) {
                    return true;
                }
            }
        }
        return isVisibleFilterText(expectedText);
    }

    private boolean isSelectionVisibleInFilterBar(String expectedText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() +
                        "const wanted = normalizeText(arguments[0]).toLowerCase();" +
                        "const search = document.querySelector('flt-semantics[aria-label=\"usersearch_field\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label=\"user_search_field\"]');" +
                        "const searchRect = search ? search.getBoundingClientRect() : null;" +
                        "const navMenu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const inFilterBar = (node) => {" +
                        "  if (!node) return false;" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "  if (navMenu && navMenu.contains(node)) return false;" +
                        "  const root = popupMenuRoot();" +
                        "  if (root && root.contains(node)) return false;" +
                        "  if (!searchRect) return true;" +
                        "  return rect.top >= searchRect.top - 100 && rect.top <= searchRect.bottom + 100;" +
                        "};" +
                        "const matches = (node) => {" +
                        "  const text = normalizeText(node.textContent).toLowerCase();" +
                        "  const label = normalizeText(node.getAttribute('aria-label')).toLowerCase();" +
                        "  if (text === wanted || label === wanted) return true;" +
                        "  if (text.includes(wanted) || label.includes(wanted.replace(/\\s+/g, '_'))) return true;" +
                        "  return false;" +
                        "};" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(" +
                        "  node => inFilterBar(node) && matches(node));",
                expectedText
        ));
    }

    private boolean isSelectionSemanticsVisibleInFilterBar(String semanticsLabel) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const semanticsLabel = (arguments[0] || '').toLowerCase();" +
                        "const search = document.querySelector('flt-semantics[aria-label=\"usersearch_field\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label=\"user_search_field\"]');" +
                        "const searchRect = search ? search.getBoundingClientRect() : null;" +
                        "const navMenu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (label !== semanticsLabel && !label.includes(semanticsLabel)) return false;" +
                        "  if (navMenu && navMenu.contains(node)) return false;" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "  if (!searchRect) return true;" +
                        "  return rect.top >= searchRect.top - 100 && rect.top <= searchRect.bottom + 100;" +
                        "});",
                semanticsLabel
        ));
    }

    private static String[] alternateSemanticsLabels(String semanticsLabel) {
        return switch (semanticsLabel) {
            case SEMANTICS_PENDING -> new String[]{SEMANTICS_PENDING, "PENDING", "Pending"};
            case SEMANTICS_GRANTED -> new String[]{SEMANTICS_GRANTED, "GRANTED", "Granted"};
            case SEMANTICS_WITHDRAWN -> new String[]{SEMANTICS_WITHDRAWN, "WITHDRAWN", "Withdrawn"};
            default -> new String[]{semanticsLabel};
        };
    }

    private static String semanticsLabelForVisibleText(String visibleText) {
        if (visibleText == null) {
            return null;
        }
        return switch (visibleText) {
            case "Virtual Intros" -> SEMANTICS_VIRTUAL_INTROS;
            case "Privacy Policy" -> SEMANTICS_PRIVACY_POLICY;
            case "Terms of Service" -> SEMANTICS_TERMS_OF_SERVICE;
            case "Marketing Communication", "Marketing Communications" -> SEMANTICS_MARKETING_COMMUNICATION;
            case "Customise Recommendation", "Customize Recommendation" -> SEMANTICS_CUSTOMISE_RECOMMENDATION;
            case "Notification", "Notifications" -> SEMANTICS_NOTIFICATION;
            case "Location" -> SEMANTICS_LOCATION;
            case "Gender" -> SEMANTICS_GENDER;
            case "Ethnicity" -> SEMANTICS_ETHNICITY;
            case "Pending" -> SEMANTICS_PENDING;
            case "Granted" -> SEMANTICS_GRANTED;
            case "Withdrawn" -> SEMANTICS_WITHDRAWN;
            default -> null;
        };
    }

    private void openDropdown(DropdownConfig config) {
        if (isDropdownMenuOpen(config)) {
            return;
        }
        semantics.enableFlutterSemantics();
        for (int attempt = 0; attempt < 12; attempt++) {
            if (isDropdownMenuOpen(config)) {
                return;
            }
            scrollUntilTriggerVisible(config);
            if (tryOpenDropdown(config)) {
                semantics.pauseAfterScroll();
                if (semantics.waitUntil(d -> isDropdownMenuOpen(config), MENU_WAIT)) {
                    return;
                }
            }
            scrollFilterAreaRightOnce();
            semantics.pauseAfterScroll();
        }
        if (isDropdownMenuOpen(config)) {
            return;
        }
        throw new NoSuchElementException("Dropdown did not open: " + config.triggerVisibleText());
    }

    private void ensureDropdownOpen(DropdownConfig dropdown) {
        if (isDropdownMenuOpen(dropdown)) {
            return;
        }
        prepareFilterArea();
        if (dropdown == ACTIONS_DROPDOWN) {
            openActionsDropdown();
        } else {
            openDropdown(dropdown);
        }
    }

    private void selectOption(DropdownConfig dropdown, OptionConfig option) {
        semantics.enableFlutterSemantics();
        if (!isDropdownMenuOpen(dropdown)) {
            ensureDropdownOpen(dropdown);
        }
        if (!semantics.waitUntil(d -> isDropdownMenuOpen(dropdown), MENU_WAIT)) {
            ensureDropdownOpen(dropdown);
        }
        for (int attempt = 0; attempt < 15; attempt++) {
            scrollOptionIntoView(option.semanticsLabel(), option.visibleText());
            if (trySelectOption(dropdown, option)) {
                return;
            }
            for (String alternateText : option.alternateVisibleTexts()) {
                if (alternateText != null && !alternateText.isBlank()
                        && trySelectOption(dropdown, option.withVisibleText(alternateText))) {
                    return;
                }
            }
            scrollPopupMenuDown();
            semantics.pauseAfterScroll();
        }
        if (trySelectOptionByKeyboard(dropdown, option)) {
            return;
        }
        throw new NoSuchElementException(
                "Filter option is not clickable: " + option.semanticsLabel() + " / " + option.visibleText());
    }

    private boolean trySelectOption(DropdownConfig dropdown, OptionConfig option) {
        if (!clickOption(option)) {
            return false;
        }
        if (semantics.waitUntil(d -> !isDropdownMenuOpen(dropdown), MENU_WAIT)) {
            semantics.pauseAfterScroll();
            return true;
        }
        return false;
    }

    private boolean trySelectOptionByKeyboard(DropdownConfig dropdown, OptionConfig option) {
        if (!selectOptionByKeyboard(option.visibleText())) {
            return false;
        }
        if (semantics.waitUntil(d -> !isDropdownMenuOpen(dropdown), MENU_WAIT)) {
            semantics.pauseAfterScroll();
            return true;
        }
        int index = optionIndex(option);
        if (index < 0) {
            return false;
        }
        try {
            for (int step = 0; step < index; step++) {
                new Actions(driver).sendKeys(Keys.ARROW_DOWN).perform();
                semantics.pauseAfterScroll();
            }
            new Actions(driver).sendKeys(Keys.ENTER).perform();
            semantics.pauseAfterScroll();
            if (semantics.waitUntil(d -> !isDropdownMenuOpen(dropdown), MENU_WAIT)) {
                semantics.pauseAfterScroll();
                return true;
            }
        } catch (Exception ignored) {
            // Fall through to caller.
        }
        return false;
    }

    private static int optionIndex(OptionConfig option) {
        return switch (option.semanticsLabel()) {
            case SEMANTICS_VIRTUAL_INTROS -> 1;
            case SEMANTICS_PRIVACY_POLICY -> 1;
            case SEMANTICS_TERMS_OF_SERVICE -> 2;
            case SEMANTICS_MARKETING_COMMUNICATION -> 3;
            case SEMANTICS_CUSTOMISE_RECOMMENDATION -> 4;
            case SEMANTICS_NOTIFICATION -> 5;
            case SEMANTICS_LOCATION -> 6;
            case SEMANTICS_GENDER -> 7;
            case SEMANTICS_ETHNICITY -> 8;
            case SEMANTICS_PENDING -> 1;
            case SEMANTICS_GRANTED -> 2;
            case SEMANTICS_WITHDRAWN -> 3;
            default -> -1;
        };
    }

    private boolean selectOptionByKeyboard(String visibleText) {
        try {
            for (int attempt = 0; attempt < 20; attempt++) {
                if (isOptionVisibleInPopup(null, visibleText)) {
                    new Actions(driver).sendKeys(Keys.ENTER).perform();
                    semantics.pauseAfterScroll();
                    return true;
                }
                new Actions(driver).sendKeys(Keys.ARROW_DOWN).perform();
                semantics.pauseAfterScroll();
            }
        } catch (Exception ignored) {
            // Fall through to caller error.
        }
        return false;
    }

    private boolean isDropdownMenuOpen(DropdownConfig config) {
        if (config == ACTIONS_DROPDOWN) {
            return isActionsDropdownMenuOpen();
        }
        if (!hasPopupMenuVisible()) {
            return false;
        }
        for (String semanticsLabel : config.menuOpenSemanticsLabels()) {
            if (isOptionVisibleInPopup(semanticsLabel, null)) {
                return true;
            }
        }
        for (String visibleText : config.menuOpenVisibleTexts()) {
            if (isOptionVisibleInPopup(null, visibleText)) {
                return true;
            }
        }
        return false;
    }

    private boolean isActionsDropdownMenuOpen() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() +
                        "const optionLabels = ['action_dropdown_action_pending', 'action_dropdown_action_granted'," +
                        "  'action_dropdown_option_withdrawn', 'PENDING', 'GRANTED', 'WITHDRAWN'];" +
                        "const isVisible = (node) => {" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  return rect.width > 0 && rect.height > 0;" +
                        "};" +
                        "const pool = hasPopupMenu() ? popupMenuNodes()" +
                        "  : Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const visibleOptions = optionLabels.filter(label => pool.some(node => {" +
                        "  const nodeLabel = (node.getAttribute('aria-label') || '');" +
                        "  return (nodeLabel === label || nodeLabel.toLowerCase() === label.toLowerCase()" +
                        "    || nodeLabel.includes(label)) && isVisible(node);" +
                        "}));" +
                        "if (visibleOptions.length >= 2) return true;" +
                        "return ['Pending', 'Granted', 'Withdrawn'].filter(text =>" +
                        "  pool.some(node => matchesVisibleText(node, text) && isVisible(node))).length >= 2;"
        ));
    }

    private boolean hasPopupMenuVisible() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() + "return hasPopupMenu();"
        ));
    }

    private boolean isOptionVisible(OptionConfig option) {
        if (isOptionVisibleInPopup(option.semanticsLabel(), option.visibleText())) {
            return true;
        }
        if (isSemanticsLabelVisible(option.semanticsLabel())) {
            return true;
        }
        if (isVisibleFilterOption(option.visibleText())) {
            return true;
        }
        for (String alternateText : option.alternateVisibleTexts()) {
            if (alternateText != null && !alternateText.isBlank()
                    && (isOptionVisibleInPopup(null, alternateText) || isVisibleFilterOption(alternateText))) {
                return true;
            }
        }
        return false;
    }

    private boolean isOptionVisibleInPopup(String semanticsLabel, String visibleText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() +
                        "const semanticsLabel = (arguments[0] || '').toLowerCase();" +
                        "const visibleText = arguments[1];" +
                        "const isVisible = (node) => {" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  return rect.width > 0 && rect.height > 0;" +
                        "};" +
                        "const matches = (node) => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (semanticsLabel && (label === semanticsLabel || label.includes(semanticsLabel))) {" +
                        "    return isVisible(node);" +
                        "  }" +
                        "  if (visibleText && matchesVisibleText(node, visibleText)) {" +
                        "    return isVisible(node);" +
                        "  }" +
                        "  return false;" +
                        "};" +
                        "const nodes = hasPopupMenu() ? popupMenuNodes() : [];" +
                        "return nodes.some(matches);",
                semanticsLabel,
                visibleText
        ));
    }

    private boolean isSemanticsLabelVisible(String semanticsLabel) {
        if (!driver.findElements(optionLocator(semanticsLabel)).isEmpty()) {
            return true;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const semanticsLabel = (arguments[0] || '').toLowerCase();" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (label !== semanticsLabel && !label.includes(semanticsLabel)) return false;" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  return rect.width > 0 && rect.height > 0;" +
                        "});",
                semanticsLabel
        ));
    }

    private boolean isVisibleFilterOption(String visibleText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const visibleText = arguments[0];" +
                        "const normalizeText = (value) => (value || '').replace(/\\u00A0/g, ' ').trim();" +
                        "const nodeVisibleText = (node) => normalizeText(node.getAttribute('aria-label'))" +
                        "  || normalizeText(node.textContent);" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (label.includes('consent_audit_log_submenu')) return false;" +
                        "  if (nodeVisibleText(node) !== normalizeText(visibleText)) return false;" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  return rect.width > 0 && rect.height > 0;" +
                        "});",
                visibleText
        ));
    }

    private boolean isVisibleFilterText(String expectedText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const wanted = (arguments[0] || '').replace(/\\u00A0/g, ' ').trim().toLowerCase();" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "  if (menu && menu.contains(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\u00A0/g, ' ').trim().toLowerCase();" +
                        "  const label = (node.getAttribute('aria-label') || '').replace(/\\u00A0/g, ' ').trim().toLowerCase();" +
                        "  if (text !== wanted && label !== wanted) return false;" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  return rect.width > 0 && rect.height > 0;" +
                        "});",
                expectedText
        ));
    }

    private boolean tryOpenDropdown(DropdownConfig config) {
        if (ALL_ACTIONS_TEXT.equals(config.triggerVisibleText())) {
            if (semantics.clickSemanticsLabelViaScript(SEMANTICS_ALL_ACTIONS)
                    || semantics.clickSemanticsLabelWithoutEnabling(SEMANTICS_ALL_ACTIONS)) {
                return true;
            }
        }
        if (clickVisibleTriggerInFilterBar(config)) {
            return true;
        }
        if (ALL_ACTIONS_TEXT.equals(config.triggerVisibleText())) {
            if (clickActionsTriggerRelativeToCategories() || clickActionsTriggerByCoordinates()) {
                return true;
            }
        }
        for (String triggerLabel : config.triggerLabels()) {
            if (semantics.clickSemanticsLabelWithoutEnabling(triggerLabel)
                    || semantics.clickSemanticsLabelViaScript(triggerLabel)) {
                return true;
            }
        }
        List<WebElement> triggers = driver.findElements(filterTriggerLocator(config));
        if (!triggers.isEmpty()) {
            WebElement trigger = triggers.get(0);
            semantics.scrollIntoView(trigger);
            if (semantics.clickElementReliably(trigger)) {
                return true;
            }
        }
        if (semantics.clickVisibleText(config.triggerVisibleText())) {
            return true;
        }
        if (ALL_ACTIONS_TEXT.equals(config.triggerVisibleText()) && tryOpenActionsDropdown()) {
            return true;
        }
        return clickFilterTriggerViaScript(config);
    }

    private boolean clickFilterTriggerViaScript(DropdownConfig config) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const triggerLabels = (arguments[0] || []).map(label => String(label).toLowerCase());" +
                        "const triggerVisibleText = arguments[1];" +
                        "const normalizeText = (value) => (value || '').replace(/\\u00A0/g, ' ').trim();" +
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
                        "const clickPoint = (x, y) => {" +
                        "  const hit = document.elementFromPoint(x, y);" +
                        "  if (!hit) return false;" +
                        "  ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "    hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                        "  });" +
                        "  return true;" +
                        "};" +
                        "const isMenuOption = (label) => {" +
                        "  if (label.includes('tenant_app_dropdown_option_')) return true;" +
                        "  if (label.includes('category_option_') && !label.includes('all_categories')) return true;" +
                        "  if (label.includes('action_dropdown_action_') ||" +
                        "      label === 'action_dropdown_option_withdrawn') return true;" +
                        "  return false;" +
                        "};" +
                        "const isClosedTrigger = (node) => {" +
                        "  if (!node || node.tagName !== 'FLT-SEMANTICS') return false;" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = normalizeText(node.textContent);" +
                        "  if (isMenuOption(label)) return false;" +
                        "  for (const triggerLabel of triggerLabels) {" +
                        "    if (label === triggerLabel || label.includes(triggerLabel)) return true;" +
                        "  }" +
                        "  return text === triggerVisibleText;" +
                        "};" +
                        "const search = document.querySelector('flt-semantics[aria-label=\"usersearch_field\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label=\"user_search_field\"]');" +
                        "const searchRect = search ? search.getBoundingClientRect() : null;" +
                        "let candidates = Array.from(document.querySelectorAll('flt-semantics')).filter(isClosedTrigger);" +
                        "if (searchRect) {" +
                        "  candidates = candidates.filter(node => {" +
                        "    const rect = node.getBoundingClientRect();" +
                        "    return rect.width > 0 && rect.height > 0 &&" +
                        "      rect.top >= searchRect.top - 120 && rect.top <= searchRect.bottom + 120;" +
                        "  });" +
                        "}" +
                        "for (const candidate of candidates) {" +
                        "  const tappable = candidate.closest('flt-semantics[flt-tappable]') || candidate;" +
                        "  if (clickNode(tappable)) return true;" +
                        "}" +
                        "if (searchRect) {" +
                        "  const y = searchRect.top + searchRect.height / 2;" +
                        "  for (const offset of [-560, -500, -440, -380, -320, -260, -200, -140, -80," +
                        "    80, 140, 200, 260, 320, 380, 440, 500, 560, 620, 680]) {" +
                        "    const x = Math.max(8, Math.min(window.innerWidth - 8, searchRect.left + offset));" +
                        "    if (clickPoint(x, y)) return true;" +
                        "  }" +
                        "}" +
                        "return false;",
                config.triggerLabels(),
                config.triggerVisibleText()
        ));
    }

    private boolean clickActionOptionViaScript(String semanticsLabel, String visibleText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() +
                        "const semanticsLabel = (arguments[0] || '').toLowerCase();" +
                        "const visibleText = arguments[1];" +
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
                        "const pool = hasPopupMenu() ? popupMenuNodes()" +
                        "  : Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const match = pool.find(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (semanticsLabel && (label === semanticsLabel || label.includes(semanticsLabel))) return true;" +
                        "  return matchesVisibleText(node, visibleText);" +
                        "});" +
                        "return match ? clickNode(match) : false;",
                semanticsLabel,
                visibleText
        ));
    }

    private boolean clickOption(OptionConfig option) {
        String semanticsLabel = option.semanticsLabel();
        for (String label : alternateSemanticsLabels(semanticsLabel)) {
            if (semantics.clickSemanticsLabelWithoutEnabling(label)) {
                return true;
            }
            if (semantics.clickSemanticsLabelViaScript(label)) {
                return true;
            }
            if (clickActionOptionViaScript(label, option.visibleText())) {
                return true;
            }
        }
        List<WebElement> options = driver.findElements(optionLocator(semanticsLabel));
        if (!options.isEmpty()) {
            WebElement element = options.get(0);
            semantics.scrollIntoView(element);
            if (semantics.clickElementReliably(element)) {
                return true;
            }
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() +
                        "const semanticsLabel = (arguments[0] || '').toLowerCase();" +
                        "const visibleText = arguments[1];" +
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
                        "if (semanticsLabel) {" +
                        "  const byLabel = document.querySelector('flt-semantics[aria-label=\"' + semanticsLabel + '\"]')" +
                        "    || document.querySelector('flt-semantics[aria-label*=\"' + semanticsLabel + '\"]');" +
                        "  if (byLabel && clickNode(byLabel)) return true;" +
                        "}" +
                        "const score = (node) => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (label === semanticsLabel) return 0;" +
                        "  if (label.includes(semanticsLabel)) return 1;" +
                        "  return 2;" +
                        "};" +
                        "const pool = hasPopupMenu() ? popupMenuNodes()" +
                        "  : Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const matches = pool.filter(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (label.includes('consent_audit_log_submenu')) return false;" +
                        "  if (semanticsLabel && (label === semanticsLabel || label.includes(semanticsLabel))) return true;" +
                        "  return matchesVisibleText(node, visibleText);" +
                        "}).sort((a, b) => score(a) - score(b));" +
                        "for (const match of matches) {" +
                        "  if (clickNode(match)) return true;" +
                        "}" +
                        "return false;",
                semanticsLabel,
                option.visibleText()
        ));
    }

    private void scrollOptionIntoView(String semanticsLabel, String visibleText) {
        ((JavascriptExecutor) driver).executeScript(
                popupMenuHelperJs() +
                        "const semanticsLabel = (arguments[0] || '').toLowerCase();" +
                        "const visibleText = arguments[1];" +
                        "const pool = hasPopupMenu() ? popupMenuNodes()" +
                        "  : Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const node = pool.find(candidate => {" +
                        "  const label = (candidate.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (semanticsLabel && (label === semanticsLabel || label.includes(semanticsLabel))) return true;" +
                        "  return matchesVisibleText(candidate, visibleText);" +
                        "});" +
                        "if (node) node.scrollIntoView({block: 'center', inline: 'nearest'});",
                semanticsLabel,
                visibleText
        );
        semantics.pauseAfterScroll();
    }

    private void scrollUntilTriggerVisible(DropdownConfig config) {
        for (int attempt = 0; attempt < 12; attempt++) {
            if (isTriggerVisibleInFilterBar(config)) {
                return;
            }
            if (config == ACTIONS_DROPDOWN && attempt >= 6) {
                scrollFilterAreaLeftOnce();
            } else {
                scrollFilterAreaRightOnce();
            }
            semantics.pauseAfterScroll();
        }
    }

    private boolean clickVisibleTriggerInFilterBar(DropdownConfig config) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const triggerLabels = (arguments[0] || []).map(label => String(label).toLowerCase());" +
                        "const triggerVisibleText = arguments[1];" +
                        "const normalizeText = (value) => (value || '').replace(/\\u00A0/g, ' ').trim();" +
                        "const search = document.querySelector('flt-semantics[aria-label=\"usersearch_field\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label=\"user_search_field\"]');" +
                        "const searchRect = search ? search.getBoundingClientRect() : null;" +
                        "const navMenu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
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
                        "const inFilterBar = (node) => {" +
                        "  if (!node) return false;" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "  if (navMenu && navMenu.contains(node)) return false;" +
                        "  if (!searchRect) return rect.left >= 0 && rect.right <= window.innerWidth;" +
                        "  return rect.top >= searchRect.top - 120 && rect.top <= searchRect.bottom + 120" +
                        "    && rect.left >= 0 && rect.right <= window.innerWidth;" +
                        "};" +
                        "const isClosedTrigger = (node) => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = normalizeText(node.textContent);" +
                        "  for (const triggerLabel of triggerLabels) {" +
                        "    if (label === triggerLabel || label.includes(triggerLabel)) return true;" +
                        "  }" +
                        "  return text === triggerVisibleText;" +
                        "};" +
                        "const candidates = Array.from(document.querySelectorAll('flt-semantics')).filter(" +
                        "  node => inFilterBar(node) && isClosedTrigger(node));" +
                        "for (const candidate of candidates) {" +
                        "  if (clickNode(candidate)) return true;" +
                        "}" +
                        "return false;",
                config.triggerLabels(),
                config.triggerVisibleText()
        ));
    }

    private boolean isTriggerVisibleInFilterBar(DropdownConfig config) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const triggerLabels = (arguments[0] || []).map(label => String(label).toLowerCase());" +
                        "const triggerVisibleText = arguments[1];" +
                        "const normalizeText = (value) => (value || '').replace(/\\u00A0/g, ' ').trim();" +
                        "const search = document.querySelector('flt-semantics[aria-label=\"usersearch_field\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label=\"user_search_field\"]');" +
                        "const searchRect = search ? search.getBoundingClientRect() : null;" +
                        "const navMenu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const inFilterBar = (node) => {" +
                        "  if (!node) return false;" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "  if (navMenu && navMenu.contains(node)) return false;" +
                        "  if (!searchRect) return rect.left >= 0 && rect.right <= window.innerWidth;" +
                        "  return rect.top >= searchRect.top - 120 && rect.top <= searchRect.bottom + 120" +
                        "    && rect.left >= 0 && rect.right <= window.innerWidth;" +
                        "};" +
                        "const isClosedTrigger = (node) => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = normalizeText(node.textContent);" +
                        "  for (const triggerLabel of triggerLabels) {" +
                        "    if (label === triggerLabel || label.includes(triggerLabel)) return true;" +
                        "  }" +
                        "  return text === triggerVisibleText;" +
                        "};" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(" +
                        "  node => inFilterBar(node) && isClosedTrigger(node));",
                config.triggerLabels(),
                config.triggerVisibleText()
        ));
    }

    private void scrollFilterAreaIntoView() {
        ((JavascriptExecutor) driver).executeScript(
                "const search = document.querySelector('flt-semantics[aria-label=\"usersearch_field\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label=\"user_search_field\"]');" +
                        "const anchor = search" +
                        "  || document.querySelector('flt-semantics[aria-label=\"export_csv_button\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label=\"select_date_range_button\"]');" +
                        "if (anchor) anchor.scrollIntoView({block: 'center', inline: 'nearest'});"
        );
        semantics.pauseAfterScroll();
    }

    private void scrollPopupMenuDown() {
        ((JavascriptExecutor) driver).executeScript(
                "const popup = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "  const text = (node.textContent || '').trim();" +
                        "  const label = (node.getAttribute('aria-label') || '').trim();" +
                        "  return text === 'Popup menu' || label === 'Popup menu';" +
                        "});" +
                        "const anchor = popup ? (popup.parentElement || popup) : document.body;" +
                        "for (let i = 0; i < 4; i++) {" +
                        "  anchor.dispatchEvent(new WheelEvent('wheel', {" +
                        "    deltaX: 0, deltaY: 280, bubbles: true, cancelable: true" +
                        "  }));" +
                        "}"
        );
    }

    public void scrollFilterAreaRightOnce() {
        if (Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  const text = (node.textContent || '').trim();" +
                        "  return text === 'Popup menu' || (node.getAttribute('aria-label') || '').trim() === 'Popup menu';" +
                        "});"
        ))) {
            return;
        }
        scrollFilterAreaHorizontally(320);
    }

    public void scrollFilterAreaLeftOnce() {
        if (Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  const text = (node.textContent || '').trim();" +
                        "  return text === 'Popup menu' || (node.getAttribute('aria-label') || '').trim() === 'Popup menu';" +
                        "});"
        ))) {
            return;
        }
        scrollFilterAreaHorizontally(-320);
    }

    private void scrollFilterAreaHorizontally(int deltaX) {
        ((JavascriptExecutor) driver).executeScript(
                "const deltaX = arguments[0];" +
                        "const search = document.querySelector('flt-semantics[aria-label=\"usersearch_field\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label=\"user_search_field\"]');" +
                        "const dispatchWheel = (x, y) => {" +
                        "  const target = document.elementFromPoint(x, y) || document.body;" +
                        "  for (let i = 0; i < 4; i++) {" +
                        "    target.dispatchEvent(new WheelEvent('wheel', { deltaX: deltaX, deltaY: 0, bubbles: true, cancelable: true }));" +
                        "  }" +
                        "};" +
                        "if (search) {" +
                        "  const rect = search.getBoundingClientRect();" +
                        "  dispatchWheel(rect.left + rect.width / 2, rect.top + rect.height / 2);" +
                        "}" +
                        "window.scrollBy(deltaX, 0);",
                deltaX
        );
        List<WebElement> searchAreas = driver.findElements(searchBarSemantics);
        if (!searchAreas.isEmpty()) {
            try {
                new Actions(driver).moveToElement(searchAreas.get(0))
                        .sendKeys(deltaX >= 0 ? Keys.ARROW_RIGHT : Keys.ARROW_LEFT)
                        .perform();
            } catch (Exception ignored) {
                // Script scroll is enough.
            }
        }
    }

    private static By filterTriggerLocator(DropdownConfig config) {
        StringBuilder xpath = new StringBuilder();
        for (int i = 0; i < config.triggerLabels().length; i++) {
            if (i > 0) {
                xpath.append(" | ");
            }
            String label = config.triggerLabels()[i];
            xpath.append("//flt-semantics[@aria-label='").append(label).append("']");
            xpath.append(" | //flt-semantics[contains(@aria-label,'").append(label).append("')]");
        }
        xpath.append(" | //flt-semantics[@flt-tappable and normalize-space(.)='")
                .append(config.triggerVisibleText())
                .append("']");
        return By.xpath(xpath.toString());
    }

    private static By optionLocator(String semanticsLabel) {
        return By.xpath(
                "//flt-semantics[@aria-label='" + semanticsLabel + "']" +
                        " | //flt-semantics[contains(@aria-label,'" + semanticsLabel + "')]");
    }

    private static OptionConfig option(String semanticsLabel, String visibleText, String... alternateVisibleTexts) {
        return new OptionConfig(semanticsLabel, visibleText, alternateVisibleTexts);
    }

    private record DropdownConfig(
            String[] triggerLabels,
            String triggerVisibleText,
            String[] menuOpenSemanticsLabels,
            String[] menuOpenVisibleTexts
    ) {
    }

    private record OptionConfig(String semanticsLabel, String visibleText, String... alternateVisibleTexts) {
        private OptionConfig withVisibleText(String alternateVisibleText) {
            return new OptionConfig(semanticsLabel, alternateVisibleText, alternateVisibleTexts);
        }
    }
}
