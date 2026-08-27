package com.example.pages;

import com.example.pages.support.FlutterSemanticsSupport;
import com.example.pages.support.UserRowActionsConfig;
import com.example.pages.support.UserRowActionsSupport;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Base page for Flutter users tables (Individual / Company) with shared semantics helpers.
 */
public abstract class FlutterUsersTablePage {

    protected static final Duration DEFAULT_WAIT = FlutterSemanticsSupport.DEFAULT_WAIT;
    protected static final Duration SCROLL_SETTLE_WAIT = FlutterSemanticsSupport.SCROLL_SETTLE_WAIT;
    protected static final Duration DELETE_DIALOG_POLL = FlutterSemanticsSupport.DELETE_DIALOG_POLL;
    protected static final Duration STATUS_MENU_WAIT = Duration.ofSeconds(3);
    protected static final Duration STATUS_VALIDATION_DELAY = Duration.ofSeconds(10);
    protected static final int MAX_NAV_ATTEMPTS = 10;

    protected static final String DISPATCH_POINTER_CLICK_SCRIPT =
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

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final FlutterSemanticsSupport semantics;
    protected final UserRowActionsSupport rowActions;

    protected FlutterUsersTablePage(WebDriver driver, UserRowActionsConfig rowActionsConfig) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_WAIT);
        this.semantics = new FlutterSemanticsSupport(driver);
        this.rowActions = new UserRowActionsSupport(driver, semantics, rowActionsConfig);
    }

    protected void enableFlutterSemantics() {
        semantics.enableFlutterSemantics();
    }

    protected void enableFlutterSemanticsOn(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
    }

    protected boolean waitUntil(Function<WebDriver, Boolean> condition, Duration timeout) {
        return semantics.waitUntil(condition, timeout);
    }

    protected void pauseAfterScroll() {
        semantics.pauseAfterScroll();
    }

    protected void scrollIntoView(WebElement element) {
        semantics.scrollIntoView(element);
    }

    protected boolean clickElementReliably(WebElement element) {
        return semantics.clickElementReliably(element);
    }

    protected void clickAtElementViewportCenter(WebElement element) {
        semantics.clickAtElementViewportCenter(element);
    }

    protected boolean clickSemanticsLabelViaScript(String semanticsLabel) {
        return semantics.clickSemanticsLabelViaScript(semanticsLabel);
    }

    protected boolean clickVisibleText(String visibleText) {
        return semantics.clickVisibleText(visibleText);
    }

    protected void clickSemanticsElement(By locator) {
        semantics.clickSemanticsElement(locator, wait);
    }

    protected String getSemanticsText(WebDriver webDriver) {
        return semantics.getSemanticsText(webDriver);
    }

    protected String getSemanticsNodeText(WebElement semanticsNode) {
        return semantics.getSemanticsNodeText(semanticsNode);
    }

    protected String normalizeText(String value) {
        return semantics.normalizeText(value);
    }

    protected boolean isNavigationDashboardReady(WebDriver webDriver) {
        enableFlutterSemanticsOn(webDriver);
        String text = getSemanticsText(webDriver);
        return text != null
                && (text.contains("Individual Users")
                || text.contains("Company Users")
                || text.contains("Navigation menu"));
    }

    protected void openDrawerNavigationItem(
            String visibleText,
            String semanticsMenuLabel,
            By menuLocator,
            Predicate<WebDriver> pageLoadedCheck) {
        enableFlutterSemantics();
        waitUntil(this::isNavigationDashboardReady, DEFAULT_WAIT);
        if (pageLoadedCheck.test(driver)) {
            return;
        }
        for (int attempt = 0; attempt < MAX_NAV_ATTEMPTS; attempt++) {
            scrollNavigationToItem(visibleText, semanticsMenuLabel);
            if (clickNavigationItem(visibleText, semanticsMenuLabel, menuLocator)) {
                pauseAfterScroll();
                if (pageLoadedCheck.test(driver)) {
                    return;
                }
            }
            scrollNavigationSidebarDown();
            pauseAfterScroll();
        }
        throw new NoSuchElementException(
                "Navigation item not found: " + visibleText
                        + " (expected semantics " + semanticsMenuLabel + ")");
    }

    protected void scrollNavigationToItem(String visibleText, String semanticsMenuLabel) {
        enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                "const wanted = arguments[0];" +
                        "const semanticsLabel = arguments[1];" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const nodes = menu ? Array.from(menu.querySelectorAll('flt-semantics'))" +
                        "  : Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const target = nodes.find(node => {" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const nodeLabel = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return text === wanted || nodeLabel.includes(semanticsLabel.toLowerCase());" +
                        "});" +
                        "if (target) target.scrollIntoView({block: 'center', inline: 'nearest'});",
                visibleText,
                semanticsMenuLabel
        );
        pauseAfterScroll();
    }

    protected boolean clickNavigationItem(String visibleText, String semanticsMenuLabel, By menuLocator) {
        if (clickSemanticsLabelViaScript(semanticsMenuLabel)) {
            return true;
        }
        if (clickVisibleText(visibleText)) {
            return true;
        }
        if (!driver.findElements(menuLocator).isEmpty()) {
            try {
                clickSemanticsElement(menuLocator);
                return true;
            } catch (Exception ignored) {
                // Continue to script click.
            }
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                DISPATCH_POINTER_CLICK_SCRIPT +
                        "const wanted = arguments[0];" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const nodes = menu ? Array.from(menu.querySelectorAll('flt-semantics'))" +
                        "  : Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const target = nodes.find(node =>" +
                        "  (node.textContent || '').replace(/\\s+/g, ' ').trim() === wanted);" +
                        "if (!target) return false;" +
                        "const tap = target.closest('flt-semantics[flt-tappable]') || target;" +
                        "return dispatchPointerClick(tap);",
                visibleText
        ));
    }

    protected void scrollNavigationSidebarDown() {
        enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const anchor = menu || document.body;" +
                        "for (let i = 0; i < 3; i++) {" +
                        "  anchor.dispatchEvent(new WheelEvent('wheel', {" +
                        "    deltaX: 0, deltaY: 240, bubbles: true, cancelable: true" +
                        "  }));" +
                        "}"
        );
        pauseAfterScroll();
    }
}
