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
 * Status filter interactions for Individual Users (semantics-based dropdown).
 */
public class IndividualUsersStatusFilter {

    private static final Duration MENU_WAIT = Duration.ofSeconds(5);
    private static final String SEARCH_FIELD = "user_search_field";

    private final WebDriver driver;
    private final FlutterSemanticsSupport semantics;

    private final By statusFilterTrigger = By.xpath(
            "//flt-semantics[@aria-label='user_status_filter']" +
                    " | //flt-semantics[@aria-label='status_filter']" +
                    " | //flt-semantics[contains(@aria-label,'status_filter') and not(contains(@aria-label,'user_row_status')) and not(contains(@aria-label,'status_option'))]" +
                    " | //flt-semantics[@flt-tappable and normalize-space(.)='Status' and not(contains(@aria-label,'status_option')) and not(contains(@aria-label,'user_row_status'))]");
    private final By statusDropdownMenuOpen = By.xpath(
            "//flt-semantics[@aria-label='status_option_status']" +
                    " | //flt-semantics[@aria-label='status_option_active']" +
                    " | //flt-semantics[@aria-label='status_option_inactive']");
    private final By statusOptionActive = By.xpath(
            "//flt-semantics[@aria-label='status_option_active']" +
                    " | //flt-semantics[contains(@aria-label,'status_option_active')]");
    private final By statusOptionInactive = By.xpath(
            "//flt-semantics[@aria-label='status_option_inactive']" +
                    " | //flt-semantics[contains(@aria-label,'status_option_inactive')]");
    private final By searchBarSemantics = By.xpath("//flt-semantics[@aria-label='" + SEARCH_FIELD + "']");

    public IndividualUsersStatusFilter(WebDriver driver, FlutterSemanticsSupport semantics) {
        this.driver = driver;
        this.semantics = semantics;
    }

    public void prepareFilterArea() {
        semantics.enableFlutterSemantics();
        scrollUntilStatusTriggerVisible();
    }

    public void openDropdown() {
        if (isDropdownMenuOpen()) {
            return;
        }
        semantics.enableFlutterSemantics();
        for (int attempt = 0; attempt < 10; attempt++) {
            scrollUntilStatusTriggerVisible();
            if (tryOpenDropdown()) {
                semantics.pauseAfterScroll();
                if (semantics.waitUntil(d -> isDropdownMenuOpen(), MENU_WAIT)) {
                    return;
                }
            }
            scrollFilterAreaRightOnce();
            semantics.pauseAfterScroll();
        }
        if (isDropdownMenuOpen()) {
            return;
        }
        throw new NoSuchElementException(
                "Status dropdown did not open; status_option_active/status_option_inactive semantics not found");
    }

    public void selectActive() {
        selectOption("status_option_active", statusOptionActive, "Active");
    }

    public void selectInactive() {
        selectOption("status_option_inactive", statusOptionInactive, "Inactive");
    }

    private void selectOption(String semanticsLabel, By optionLocator, String visibleText) {
        if (!isDropdownMenuOpen()) {
            prepareFilterArea();
            openDropdown();
        }
        if (!semantics.waitUntil(d -> isOptionVisible(semanticsLabel), MENU_WAIT)) {
            throw new NoSuchElementException("Status option semantics not visible: " + semanticsLabel);
        }
        if (clickOption(semanticsLabel, optionLocator, visibleText)) {
            semantics.waitUntil(d -> !isDropdownMenuOpen(), MENU_WAIT);
            semantics.pauseAfterScroll();
            return;
        }
        throw new NoSuchElementException("Status option is not clickable: " + semanticsLabel);
    }

    private boolean isDropdownMenuOpen() {
        if (!driver.findElements(By.xpath("//flt-semantics[@aria-label='status_option_active']")).isEmpty()
                && !driver.findElements(By.xpath("//flt-semantics[@aria-label='status_option_inactive']")).isEmpty()) {
            return true;
        }
        if (!driver.findElements(statusOptionActive).isEmpty()
                && !driver.findElements(statusOptionInactive).isEmpty()) {
            return true;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const normalizeText = (value) => (value || '').replace(/\\u00A0/g, ' ').trim();" +
                        "const nodeVisibleText = (node) => normalizeText(node.getAttribute('aria-label'))" +
                        "  || normalizeText(node.textContent);" +
                        "const matchesVisibleText = (node, target) => nodeVisibleText(node) === normalizeText(target);" +
                        "const isVisible = (node) => {" +
                        "  const rect = node.getBoundingClientRect();" +
                        "  return rect.width > 0 && rect.height > 0;" +
                        "};" +
                        "const isFilterOption = (node, text) => {" +
                        "  if (!matchesVisibleText(node, text)) return false;" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (label.includes('user_row_status')) return false;" +
                        "  if (label.includes('status_option') || label.includes('active_option') ||" +
                        "      label.includes('blocked_option')) return isVisible(node);" +
                        "  return isVisible(node) && !label.includes('user_row_status');" +
                        "};" +
                        "const hasActive = Array.from(document.querySelectorAll('flt-semantics'))" +
                        "  .some(n => isFilterOption(n, 'Active'));" +
                        "const hasInactive = Array.from(document.querySelectorAll('flt-semantics'))" +
                        "  .some(n => isFilterOption(n, 'Inactive'));" +
                        "return hasActive && hasInactive;"
        ));
    }

    private boolean isOptionVisible(String semanticsLabel) {
        if (!driver.findElements(By.xpath("//flt-semantics[@aria-label='" + semanticsLabel + "']")).isEmpty()) {
            return true;
        }
        String visibleText = "status_option_active".equals(semanticsLabel) ? "Active" : "Inactive";
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const visibleText = arguments[0];" +
                        "const semanticsLabel = arguments[1];" +
                        "const normalizeText = (value) => (value || '').replace(/\\u00A0/g, ' ').trim();" +
                        "const nodeVisibleText = (node) => normalizeText(node.getAttribute('aria-label'))" +
                        "  || normalizeText(node.textContent);" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (label === semanticsLabel.toLowerCase() || label.includes(semanticsLabel.toLowerCase())) {" +
                        "    const rect = node.getBoundingClientRect();" +
                        "    return rect.width > 0 && rect.height > 0;" +
                        "  }" +
                        "  return nodeVisibleText(node) === visibleText && !label.includes('user_row_status');" +
                        "});",
                visibleText, semanticsLabel
        ));
    }

    private boolean tryOpenDropdown() {
        if (semantics.clickSemanticsLabelWithoutEnabling("user_status_filter")
                || semantics.clickSemanticsLabelWithoutEnabling("status_filter")
                || semantics.clickSemanticsLabelWithoutEnabling("status_option_status")) {
            return true;
        }
        List<WebElement> triggers = driver.findElements(statusFilterTrigger);
        if (!triggers.isEmpty()) {
            WebElement trigger = triggers.get(0);
            semantics.scrollIntoView(trigger);
            if (semantics.clickElementReliably(trigger)) {
                return true;
            }
        }
        return clickStatusFilterViaScript();
    }

    private boolean clickStatusFilterViaScript() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
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
                        "const isClosedFilter = (node) => {" +
                        "  if (!node || node.tagName !== 'FLT-SEMANTICS') return false;" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').trim();" +
                        "  if (label.includes('status_option') || label.includes('user_row_status')) return false;" +
                        "  if (text === 'Active' || text === 'Inactive' || text === 'Blocked') return false;" +
                        "  if (label === 'user_status_filter' || label.includes('status_filter')) return true;" +
                        "  return text === 'Status' && (node.hasAttribute('flt-tappable') || !!node.closest('flt-semantics[flt-tappable]'));" +
                        "};" +
                        "const search = document.querySelector('flt-semantics[aria-label=\"user_search_field\"]');" +
                        "const searchRect = search ? search.getBoundingClientRect() : null;" +
                        "let candidates = Array.from(document.querySelectorAll('flt-semantics')).filter(isClosedFilter);" +
                        "if (searchRect) {" +
                        "  candidates = candidates.filter(node => {" +
                        "    const rect = node.getBoundingClientRect();" +
                        "    return rect.width > 0 && rect.height > 0 &&" +
                        "      rect.top >= searchRect.top - 60 && rect.top <= searchRect.bottom + 60 &&" +
                        "      rect.left >= searchRect.right - 120;" +
                        "  }).sort((a, b) => a.getBoundingClientRect().left - b.getBoundingClientRect().left);" +
                        "}" +
                        "for (const candidate of candidates) {" +
                        "  const tappable = candidate.closest('flt-semantics[flt-tappable]') || candidate;" +
                        "  if (clickNode(tappable)) return true;" +
                        "}" +
                        "if (searchRect) {" +
                        "  const y = searchRect.top + searchRect.height / 2;" +
                        "  for (const offset of [80, 140, 200, 260, 320, 380]) {" +
                        "    if (clickPoint(Math.min(window.innerWidth - 8, searchRect.right + offset), y)) return true;" +
                        "  }" +
                        "}" +
                        "return false;"
        ));
    }

    private boolean clickOption(String semanticsLabel, By optionLocator, String visibleText) {
        if (semantics.clickSemanticsLabelWithoutEnabling(semanticsLabel)) {
            return true;
        }
        if (semantics.clickSemanticsLabelViaScript(semanticsLabel)) {
            return true;
        }
        List<WebElement> options = driver.findElements(optionLocator);
        if (!options.isEmpty()) {
            WebElement option = options.get(0);
            semantics.scrollIntoView(option);
            if (semantics.clickElementReliably(option)) {
                return true;
            }
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const semanticsLabel = arguments[0];" +
                        "const visibleText = arguments[1];" +
                        "const normalizeText = (value) => (value || '').replace(/\\u00A0/g, ' ').trim();" +
                        "const nodeVisibleText = (node) => normalizeText(node.getAttribute('aria-label'))" +
                        "  || normalizeText(node.textContent);" +
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
                        "const byLabel = document.querySelector('flt-semantics[aria-label=\"' + semanticsLabel + '\"]');" +
                        "if (byLabel && clickNode(byLabel)) return true;" +
                        "const score = (node) => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (label === semanticsLabel.toLowerCase()) return 0;" +
                        "  if (label.includes('status_option') || label.includes('active_option')) return 1;" +
                        "  return 2;" +
                        "};" +
                        "const matches = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (label.includes('user_row_status')) return false;" +
                        "  if (label === semanticsLabel.toLowerCase() || label.includes(semanticsLabel.toLowerCase())) {" +
                        "    return true;" +
                        "  }" +
                        "  return nodeVisibleText(node) === visibleText && !label.includes('user_row_status');" +
                        "}).sort((a, b) => score(a) - score(b));" +
                        "for (const match of matches) {" +
                        "  if (clickNode(match)) return true;" +
                        "}" +
                        "return false;",
                semanticsLabel, visibleText
        ));
    }

    private void scrollUntilStatusTriggerVisible() {
        for (int attempt = 0; attempt < 6; attempt++) {
            if (!driver.findElements(statusFilterTrigger).isEmpty()) {
                return;
            }
            scrollFilterAreaRightOnce();
            semantics.pauseAfterScroll();
        }
    }

    private void scrollFilterAreaRightOnce() {
        if (isDropdownMenuOpen()) {
            return;
        }
        ((JavascriptExecutor) driver).executeScript(
                "const search = document.querySelector('flt-semantics[aria-label=\"user_search_field\"]');" +
                        "const dispatchWheel = (x, y) => {" +
                        "  const target = document.elementFromPoint(x, y) || document.body;" +
                        "  for (let i = 0; i < 4; i++) {" +
                        "    target.dispatchEvent(new WheelEvent('wheel', { deltaX: 320, deltaY: 0, bubbles: true, cancelable: true }));" +
                        "  }" +
                        "};" +
                        "if (search) {" +
                        "  const rect = search.getBoundingClientRect();" +
                        "  dispatchWheel(rect.left + rect.width / 2, rect.top + rect.height / 2);" +
                        "}" +
                        "window.scrollBy(Math.max(320, window.innerWidth * 0.5), 0);"
        );
        List<WebElement> searchAreas = driver.findElements(searchBarSemantics);
        if (!searchAreas.isEmpty()) {
            try {
                new Actions(driver).moveToElement(searchAreas.get(0)).sendKeys(Keys.ARROW_RIGHT).perform();
            } catch (Exception ignored) {
                // Script scroll is enough.
            }
        }
    }
}
