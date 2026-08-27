package com.example.pages.support;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

public class FlutterSemanticsSupport {

    public static final Duration DEFAULT_WAIT = Duration.ofSeconds(20);
    public static final Duration SCROLL_SETTLE_WAIT = Duration.ofMillis(400);
    public static final Duration DELETE_DIALOG_POLL = Duration.ofSeconds(12);

    private final WebDriver driver;

    public FlutterSemanticsSupport(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void enableFlutterSemantics() {
        enableFlutterSemanticsOn(driver);
    }

    public void enableFlutterSemanticsOn(WebDriver webDriver) {
        ((JavascriptExecutor) webDriver).executeScript(
                "const host = document.querySelector('flt-semantics-host');" +
                        "const existing = host ? host.querySelectorAll('flt-semantics').length : 0;" +
                        // Any existing semantics node means a11y is already on. Re-clicking the
                        // placeholder toggles it off (clears the tree → Available: []).
                        "if (existing > 0) return;" +
                        "const p = document.querySelector('flt-semantics-placeholder');" +
                        "if (p) p.click();" +
                        "const enableBtn = Array.from(document.querySelectorAll('flt-semantics, button, [role=\"button\"]'))" +
                        "  .find(node => {" +
                        "    const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "    return label.includes('enable accessibility') || text === 'enable accessibility';" +
                        "  });" +
                        "if (enableBtn) enableBtn.click();"
        );
    }

    /** Ensure Flutter a11y is on without toggling it off when already enabled. */
    public void forceEnableFlutterSemantics() {
        enableFlutterSemanticsOn(driver);
        Boolean enabled = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "const host = document.querySelector('flt-semantics-host');" +
                        "const existing = host ? host.querySelectorAll('flt-semantics').length : 0;" +
                        "if (existing > 0) return true;" +
                        "const clickEnable = (node) => {" +
                        "  if (!node) return false;" +
                        "  try { node.click(); return true; } catch (e) { return false; }" +
                        "};" +
                        "let clicked = clickEnable(document.querySelector('flt-semantics-placeholder'));" +
                        "const enableBtn = Array.from(document.querySelectorAll('flt-semantics, button, [role=\"button\"], a'))" +
                        "  .find(node => {" +
                        "    const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "    return label.includes('enable accessibility') || text === 'enable accessibility';" +
                        "  });" +
                        "clicked = clickEnable(enableBtn) || clicked;" +
                        "const hostAfter = document.querySelector('flt-semantics-host');" +
                        "const countAfter = hostAfter ? hostAfter.querySelectorAll('flt-semantics').length : 0;" +
                        "return countAfter > 0;"
        );
        pauseAfterScroll();
        if (!Boolean.TRUE.equals(enabled)) {
            enableFlutterSemanticsOn(driver);
            pauseAfterScroll();
        }
    }

    public boolean waitUntil(Function<WebDriver, Boolean> condition, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout).until(condition::apply);
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public void pauseAfterScroll() {
        try {
            Thread.sleep(SCROLL_SETTLE_WAIT.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
    }

    public boolean clickElementReliably(WebElement element) {
        try {
            new Actions(driver).moveToElement(element).click().perform();
            return true;
        } catch (Exception ignored) {
            // Try direct click next.
        }
        try {
            element.click();
            return true;
        } catch (Exception ignored) {
            // Try JavaScript click as last option for semantics overlays.
        }
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            return true;
        } catch (Exception ignored) {
            // Flutter semantics can intercept pointer events; click visual center as fallback.
        }
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "const el = arguments[0];" +
                            "const rect = el.getBoundingClientRect();" +
                            "const x = rect.left + rect.width / 2;" +
                            "const y = rect.top + rect.height / 2;" +
                            "const target = document.elementFromPoint(x, y) || el;" +
                            "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                            "  target.dispatchEvent(new MouseEvent(type, {" +
                            "    bubbles: true, cancelable: true, clientX: x, clientY: y" +
                            "  }));" +
                            "});",
                    element
            );
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public void clickAtElementViewportCenter(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "const el = arguments[0];" +
                        "const rect = el.getBoundingClientRect();" +
                        "const x = rect.left + rect.width / 2;" +
                        "const y = rect.top + rect.height / 2;" +
                        "const target = document.elementFromPoint(x, y) || el;" +
                        "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "  target.dispatchEvent(new MouseEvent(type, {" +
                        "    bubbles: true, cancelable: true, clientX: x, clientY: y" +
                        "  }));" +
                        "});",
                element
        );
    }

    public boolean clickSemanticsLabelWithoutEnabling(String semanticsLabel) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const label = arguments[0];" +
                        "const node = document.querySelector('flt-semantics[aria-label=\"' + label + '\"]');" +
                        "if (!node) return false;" +
                        "const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "const rect = target.getBoundingClientRect();" +
                        "if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "const x = rect.left + rect.width / 2;" +
                        "const y = rect.top + rect.height / 2;" +
                        "const hit = document.elementFromPoint(x, y) || target;" +
                        "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "  hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                        "});" +
                        "return true;",
                semanticsLabel
        ));
    }

    public boolean clickSemanticsLabelViaScript(String semanticsLabel) {
        enableFlutterSemanticsOn(driver);
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const label = arguments[0];" +
                        "const node = document.querySelector('flt-semantics[aria-label=\"' + label + '\"]');" +
                        "if (!node) return false;" +
                        "const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "const rect = target.getBoundingClientRect();" +
                        "if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "const x = rect.left + rect.width / 2;" +
                        "const y = rect.top + rect.height / 2;" +
                        "const hit = document.elementFromPoint(x, y) || target;" +
                        "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "  hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                        "});" +
                        "return true;",
                semanticsLabel
        ));
    }

    public boolean clickVisibleText(String visibleText) {
        enableFlutterSemanticsOn(driver);
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const wanted = (arguments[0] || '').trim();" +
                        "const nodes = Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const match = nodes.find(n => (n.textContent || '').trim() === wanted);" +
                        "if (!match) return false;" +
                        "const target = match.closest('flt-semantics[flt-tappable]') || match;" +
                        "const rect = target.getBoundingClientRect();" +
                        "if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "const x = rect.left + rect.width / 2;" +
                        "const y = rect.top + rect.height / 2;" +
                        "const hit = document.elementFromPoint(x, y) || target;" +
                        "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "  hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                        "});" +
                        "return true;",
                visibleText
        ));
    }

    public void clickSemanticsElement(By locator, WebDriverWait wait) {
        enableFlutterSemantics();
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        scrollIntoView(element);
        if (!clickElementReliably(element)) {
            throw new org.openqa.selenium.NoSuchElementException("Semantics element is not clickable: " + locator);
        }
    }

    public String getSemanticsText(WebDriver webDriver) {
        return (String) ((JavascriptExecutor) webDriver).executeScript(
                "const host = document.querySelector('flt-semantics-host');" +
                        "return host ? host.textContent : '';"
        );
    }

    public String getSemanticsNodeText(WebElement semanticsNode) {
        return (String) ((JavascriptExecutor) driver).executeScript(
                "const node = arguments[0];" +
                        "if (!node) return '';" +
                        "const valueText = (node.getAttribute('aria-valuetext') || '').trim();" +
                        "if (valueText) return valueText;" +
                        "const text = (node.textContent || '').trim();" +
                        "const label = (node.getAttribute('aria-label') || '').trim();" +
                        "if (text && text.toLowerCase() !== label.toLowerCase() && !text.toLowerCase().startsWith('user_row_')) {" +
                        "  return text;" +
                        "}" +
                        "const child = node.querySelector('span, flt-semantics');" +
                        "if (child) {" +
                        "  const childText = (child.textContent || '').trim();" +
                        "  if (childText) return childText;" +
                        "}" +
                        "return label;",
                semanticsNode
        );
    }

    public String normalizeText(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace('\u00A0', ' ')
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase();
    }
}
