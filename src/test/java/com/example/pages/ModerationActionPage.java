package com.example.pages;

import com.example.pages.support.FlutterSemanticsSupport;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

public class ModerationActionPage {

    private static final String SEMANTICS_ROW_NAME = "reporting_row_name_0";
    private static final String SEMANTICS_VIEW_REPORTED_EVENT = "view_reported_event_button_0";
    private static final String SEMANTICS_VIEW_REPORTED_USER = "view_reported_user_button_0";
    private static final String SEMANTICS_DISMISS_REPORT_CHECKBOX = "dismiss_report_checkbox";
    private static final String SEMANTICS_DISMISS_REPORT_USER_CHECKBOX = "Dismiss_report_checkbox";
    private static final String SEMANTICS_BLOCK_REPORTED_EVENT_CHECKBOX = "reported_event_blocked_checkbox";
    private static final String SEMANTICS_BLOCK_REPORTED_USER_CHECKBOX = "reported_user_blocked_checkbox";
    private static final String SEMANTICS_CONFIRM_BUTTON = "confirm_button";
    private static final String SEMANTICS_TAKE_ACTION = "take_action_button";

    private static final String TAKE_ACTION_TEXT = "Take Action";
    private static final String DISMISS_REPORT_TEXT = "Dismiss Report";
    private static final String BLOCK_REPORTED_EVENT_TEXT = "Block reported event";
    private static final String BLOCK_REPORTED_USER_ACCOUNT_TEXT = "Block reported user account";
    private static final String BLOCK_REPORTED_USER_ACCOUNT_ALT_TEXT = "Blocked Reported User Account";
    private static final String CONFIRM_TEXT = "Confirm";

    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(20);

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
                    "  const exact = document.querySelector('flt-semantics[aria-label=\"' + label + '\"]');" +
                    "  if (exact) return exact;" +
                    "  return Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                    "    const aria = node.getAttribute('aria-label') || '';" +
                    "    return aria === label || aria.startsWith(label.replace(/_0$/, ''));" +
                    "  }) || null;" +
                    "}";

    private static final String MODERATION_TARGET_MATCH_SCRIPT =
            "function normalizeText(value) {" +
                    "  return (value || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                    "}" +
                    "function nodeArea(node) {" +
                    "  const rect = node.getBoundingClientRect();" +
                    "  return Math.max(1, rect.width * rect.height);" +
                    "}" +
                    "function isVisible(node) {" +
                    "  const rect = node.getBoundingClientRect();" +
                    "  return rect.width > 0 && rect.height > 0;" +
                    "}" +
                    "function findModerationTargets(semanticsLabel, visibleText, checkboxOnly) {" +
                    "  const wantedLabel = normalizeText(semanticsLabel);" +
                    "  const wantedText = normalizeText(visibleText);" +
                    "  const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                    "  const isInNav = (node) => !!(menu && node && menu.contains(node));" +
                    "  const matches = Array.from(document.querySelectorAll('flt-semantics[flt-tappable], flt-semantics'))" +
                    "    .filter(node => {" +
                    "      if (isInNav(node) || !isVisible(node)) return false;" +
                    "      const label = normalizeText(node.getAttribute('aria-label'));" +
                    "      const text = normalizeText(node.textContent);" +
                    "      const role = normalizeText(node.getAttribute('role'));" +
                    "      const labelMatch = wantedLabel && (label === wantedLabel || label.includes(wantedLabel));" +
                    "      const textMatch = wantedText && text === wantedText;" +
                    "      const checkboxMatch = checkboxOnly && role === 'checkbox' &&" +
                    "        (labelMatch || (wantedText && text.includes(wantedText)));" +
                    "      if (checkboxOnly) return labelMatch || textMatch || checkboxMatch;" +
                    "      return labelMatch || textMatch;" +
                    "    });" +
                    "  matches.sort((a, b) => nodeArea(a) - nodeArea(b));" +
                    "  return matches;" +
                    "}";

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final FlutterSemanticsSupport semantics;
    private final ModerationCenterPage moderationCenterPage;

    private final By viewReportedEventButton = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_VIEW_REPORTED_EVENT + "']" +
                    " | //flt-semantics[contains(@aria-label,'view_reported_event_button_0')]");

    private final By viewReportedUserButton = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_VIEW_REPORTED_USER + "']" +
                    " | //flt-semantics[contains(@aria-label,'view_reported_user_button_0')]");

    public ModerationActionPage(WebDriver driver, ModerationCenterPage moderationCenterPage) {
        this.driver = driver;
        this.moderationCenterPage = moderationCenterPage;
        this.wait = new WebDriverWait(driver, DEFAULT_WAIT);
        this.semantics = new FlutterSemanticsSupport(driver);
    }

    public void clickViewReportedEvent() {
        moderationCenterPage.prepareForRowAction();
        for (int attempt = 0; attempt < 12; attempt++) {
            scrollModerationRowActionsIntoView();
            if (waitUntil(d -> isViewReportedEventButtonPresent(d), Duration.ofSeconds(3))
                    && clickViewReportedEventButton()) {
                pauseAfterNavigation();
                return;
            }
            moderationCenterPage.scrollTowardsRight();
        }
        logModerationActionSemantics();
        throw new NoSuchElementException(
                "View reported event not found (expected semantics " + SEMANTICS_VIEW_REPORTED_EVENT + ")");
    }

    public void clickTakeAction() {
        semantics.enableFlutterSemantics();
        if (waitUntil(this::isTakeActionPanelOpen, Duration.ofSeconds(3))) {
            return;
        }
        for (int attempt = 0; attempt < 8; attempt++) {
            scrollModerationRowActionsIntoView();
            moderationCenterPage.scrollTowardsRight();
            if (clickTakeActionTarget()) {
                pauseAfterNavigation();
                if (waitUntil(this::isTakeActionPanelOpen, Duration.ofSeconds(8))) {
                    return;
                }
            }
        }
        if (waitUntil(this::isTakeActionPanelOpen, Duration.ofSeconds(3))) {
            return;
        }
        logModerationActionSemantics();
        throw new NoSuchElementException(
                "Take Action panel not opened (expected semantics "
                        + SEMANTICS_DISMISS_REPORT_CHECKBOX + " or " + SEMANTICS_CONFIRM_BUTTON + ")");
    }

    public void clickDismissReportCheckbox() {
        if (!clickModerationCheckboxWithAlternates(
                new String[]{SEMANTICS_DISMISS_REPORT_CHECKBOX, SEMANTICS_DISMISS_REPORT_USER_CHECKBOX},
                DISMISS_REPORT_TEXT)) {
            logModerationActionSemantics();
            throw new NoSuchElementException(
                    "Dismiss report not found (expected semantics "
                            + SEMANTICS_DISMISS_REPORT_CHECKBOX + " or " + SEMANTICS_DISMISS_REPORT_USER_CHECKBOX + ")");
        }
        pauseAfterNavigation();
    }

    public void clickBlockReportedEventCheckbox() {
        if (!clickModerationCheckbox(SEMANTICS_BLOCK_REPORTED_EVENT_CHECKBOX, BLOCK_REPORTED_EVENT_TEXT)) {
            logModerationActionSemantics();
            throw new NoSuchElementException(
                    "Block reported event not found (expected semantics " + SEMANTICS_BLOCK_REPORTED_EVENT_CHECKBOX + ")");
        }
        pauseAfterNavigation();
    }

    public void clickBlockReportedUserAccountCheckbox() {
        if (!clickModerationCheckboxWithAlternates(
                new String[]{SEMANTICS_BLOCK_REPORTED_USER_CHECKBOX},
                BLOCK_REPORTED_USER_ACCOUNT_TEXT,
                BLOCK_REPORTED_USER_ACCOUNT_ALT_TEXT)) {
            logModerationActionSemantics();
            throw new NoSuchElementException(
                    "Block reported user account not found (expected semantics "
                            + SEMANTICS_BLOCK_REPORTED_USER_CHECKBOX + ")");
        }
        pauseAfterNavigation();
    }

    public void clickConfirmButton() {
        clickModerationAction(
                SEMANTICS_CONFIRM_BUTTON,
                CONFIRM_TEXT,
                "Confirm");
    }

    private boolean isViewReportedEventButtonPresent(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (!webDriver.findElements(viewReportedEventButton).isEmpty()
                || !webDriver.findElements(viewReportedUserButton).isEmpty()) {
            return true;
        }
        if (isModerationSemanticsPresent(webDriver, SEMANTICS_VIEW_REPORTED_EVENT)
                || isModerationSemanticsPresent(webDriver, SEMANTICS_VIEW_REPORTED_USER)) {
            return true;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_SEMANTICS_SCRIPT +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const isInNav = (node) => !!(menu && node && menu.contains(node));" +
                        "return Array.from(document.querySelectorAll('flt-semantics[flt-tappable]')).some(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return label === 'view_reported_event_button_0'" +
                        "    || label.startsWith('view_reported_event_button')" +
                        "    || label.startsWith('view_reported_user_button')" +
                        "    || text === 'View';" +
                        "});"
        ));
    }

    private boolean clickViewReportedEventButton() {
        semantics.enableFlutterSemantics();
        if (semantics.clickSemanticsLabelViaScript(SEMANTICS_VIEW_REPORTED_USER)) {
            return true;
        }
        if (semantics.clickSemanticsLabelViaScript(SEMANTICS_VIEW_REPORTED_EVENT)) {
            return true;
        }
        if (!driver.findElements(viewReportedUserButton).isEmpty()) {
            semantics.clickSemanticsElement(viewReportedUserButton, wait);
            return true;
        }
        if (!driver.findElements(viewReportedEventButton).isEmpty()) {
            semantics.clickSemanticsElement(viewReportedEventButton, wait);
            return true;
        }
        if (Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                DISPATCH_POINTER_CLICK_SCRIPT +
                        FIND_SEMANTICS_SCRIPT +
                        "const label = arguments[0];" +
                        "const node = findSemantics(label);" +
                        "if (!node) return false;" +
                        "const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "return dispatchPointerClick(target);",
                SEMANTICS_VIEW_REPORTED_USER
        ))) {
            return true;
        }
        if (Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                DISPATCH_POINTER_CLICK_SCRIPT +
                        FIND_SEMANTICS_SCRIPT +
                        "const label = arguments[0];" +
                        "const node = findSemantics(label);" +
                        "if (!node) return false;" +
                        "const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "return dispatchPointerClick(target);",
                SEMANTICS_VIEW_REPORTED_EVENT
        ))) {
            return true;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                DISPATCH_POINTER_CLICK_SCRIPT +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const isInNav = (node) => !!(menu && node && menu.contains(node));" +
                        "const viewNode = Array.from(document.querySelectorAll('flt-semantics[flt-tappable]'))" +
                        "  .find(node => {" +
                        "    if (isInNav(node)) return false;" +
                        "    const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "    const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "    return label === 'view_reported_event_button_0'" +
                        "      || label.startsWith('view_reported_event_button')" +
                        "      || label.startsWith('view_reported_user_button')" +
                        "      || text === 'View';" +
                        "  });" +
                        "if (!viewNode) return false;" +
                        "return dispatchPointerClick(viewNode);"
        ));
    }

    private boolean clickModerationCheckboxWithAlternates(String[] semanticsLabels, String... visibleTextFallbacks) {
        semantics.enableFlutterSemantics();
        waitUntil(d -> {
            for (String semanticsLabel : semanticsLabels) {
                for (String visibleTextFallback : visibleTextFallbacks) {
                    if (isModerationActionOptionPresent(d, semanticsLabel, visibleTextFallback)) {
                        return true;
                    }
                }
            }
            return false;
        }, DEFAULT_WAIT);
        for (String semanticsLabel : semanticsLabels) {
            for (String visibleTextFallback : visibleTextFallbacks) {
                if (clickModerationTarget(semanticsLabel, visibleTextFallback, true)) {
                    return true;
                }
            }
        }
        for (String visibleTextFallback : visibleTextFallbacks) {
            if (clickModerationTarget(visibleTextFallback, visibleTextFallback, true)) {
                return true;
            }
        }
        return false;
    }

    private boolean clickModerationCheckbox(String semanticsLabel, String visibleTextFallback) {
        semantics.enableFlutterSemantics();
        waitUntil(d -> isModerationActionOptionPresent(d, semanticsLabel, visibleTextFallback), DEFAULT_WAIT);
        if (clickModerationTarget(semanticsLabel, visibleTextFallback, true)) {
            return true;
        }
        return clickModerationTarget(visibleTextFallback, visibleTextFallback, true);
    }

    private void clickModerationAction(String semanticsLabel, String visibleTextFallback, String actionName) {
        semantics.enableFlutterSemantics();
        waitUntil(d -> isModerationActionOptionPresent(d, semanticsLabel, visibleTextFallback)
                || (visibleTextFallback != null && pageContainsExactVisibleText(d, visibleTextFallback)),
                DEFAULT_WAIT);
        if (clickModerationTarget(semanticsLabel, visibleTextFallback, false)) {
            pauseAfterNavigation();
            return;
        }
        if (visibleTextFallback != null && clickModerationTarget(visibleTextFallback, visibleTextFallback, false)) {
            pauseAfterNavigation();
            return;
        }
        throw new NoSuchElementException(
                actionName + " not found (expected semantics " + semanticsLabel + ")");
    }

    private boolean isTakeActionPanelOpen(WebDriver webDriver) {
        return isModerationActionOptionPresent(webDriver, SEMANTICS_DISMISS_REPORT_CHECKBOX, DISMISS_REPORT_TEXT)
                || isModerationActionOptionPresent(webDriver, SEMANTICS_DISMISS_REPORT_USER_CHECKBOX, DISMISS_REPORT_TEXT)
                || isModerationActionOptionPresent(webDriver, SEMANTICS_BLOCK_REPORTED_EVENT_CHECKBOX, BLOCK_REPORTED_EVENT_TEXT)
                || isModerationActionOptionPresent(webDriver, SEMANTICS_BLOCK_REPORTED_USER_CHECKBOX, BLOCK_REPORTED_USER_ACCOUNT_TEXT)
                || isModerationActionOptionPresent(webDriver, SEMANTICS_BLOCK_REPORTED_USER_CHECKBOX, BLOCK_REPORTED_USER_ACCOUNT_ALT_TEXT)
                || isModerationSemanticsPresent(webDriver, SEMANTICS_CONFIRM_BUTTON)
                || isModerationSemanticsPresent(webDriver, CONFIRM_TEXT);
    }

    private boolean isModerationActionOptionPresent(WebDriver webDriver, String semanticsLabel, String visibleLabel) {
        return isModerationSemanticsPresent(webDriver, semanticsLabel)
                || isModerationSemanticsPresent(webDriver, visibleLabel);
    }

    private boolean clickTakeActionTarget() {
        return clickModerationTarget(SEMANTICS_TAKE_ACTION, TAKE_ACTION_TEXT, false);
    }

    private boolean pageContainsExactVisibleText(WebDriver webDriver, String visibleText) {
        if (visibleText == null || visibleText.isBlank()) {
            return false;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                MODERATION_TARGET_MATCH_SCRIPT +
                        "return findModerationTargets(arguments[0], arguments[1], false).length > 0;",
                "",
                visibleText
        ));
    }

    private boolean clickModerationTarget(String semanticsLabel, String visibleTextFallback, boolean checkboxOnly) {
        if (semanticsLabel != null && !semanticsLabel.isBlank()) {
            if (semantics.clickSemanticsLabelViaScript(semanticsLabel)) {
                return true;
            }
            By locator = By.xpath(
                    "//flt-semantics[@aria-label='" + semanticsLabel + "']" +
                            " | //flt-semantics[contains(@aria-label,'" + semanticsLabel + "')]");
            if (!driver.findElements(locator).isEmpty()) {
                semantics.clickSemanticsElement(locator, wait);
                return true;
            }
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                DISPATCH_POINTER_CLICK_SCRIPT +
                        MODERATION_TARGET_MATCH_SCRIPT +
                        "const targets = findModerationTargets(arguments[0], arguments[1], arguments[2]);" +
                        "if (!targets.length) return false;" +
                        "const target = targets[0].closest('flt-semantics[flt-tappable]') || targets[0];" +
                        "return dispatchPointerClick(target);",
                semanticsLabel == null ? "" : semanticsLabel,
                visibleTextFallback == null ? "" : visibleTextFallback,
                checkboxOnly
        ));
    }

    private void scrollModerationRowActionsIntoView() {
        semantics.enableFlutterSemanticsOn(driver);
        ((JavascriptExecutor) driver).executeScript(
                "const viewButtonLabel = arguments[0];" +
                        "const rowNameLabel = arguments[1];" +
                        "const anchor = document.querySelector('flt-semantics[aria-label=\"' + viewButtonLabel + '\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label^=\"view_reported_event_button\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label^=\"view_reported_user_button\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label=\"' + rowNameLabel + '\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label^=\"reporting_row_name\"]');" +
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
                        "const target = document.elementFromPoint(rect.left + rect.width / 2, rect.top + rect.height / 2) || anchor;" +
                        "for (let i = 0; i < 4; i++) {" +
                        "  target.dispatchEvent(new WheelEvent('wheel', { deltaX: 400, deltaY: 0, bubbles: true, cancelable: true }));" +
                        "}",
                SEMANTICS_VIEW_REPORTED_EVENT,
                SEMANTICS_ROW_NAME
        );
        semantics.pauseAfterScroll();
    }

    private boolean isModerationSemanticsPresent(WebDriver webDriver, String semanticsLabel) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_SEMANTICS_SCRIPT +
                        "return !!findSemantics(arguments[0]);",
                semanticsLabel
        ));
    }

    private void logModerationActionSemantics() {
        String labels = (String) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics'))" +
                        ".map(node => node.getAttribute('aria-label') || '')" +
                        ".filter(label => label && (" +
                        "  label.includes('view_reported') || label.includes('reporting_row') ||" +
                        "  label.includes('take_action') || label.includes('dismiss_report') ||" +
                        "  label.includes('confirm_button')))" +
                        ".join(' | ');"
        );
        String viewRelated = (String) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics'))" +
                        ".map(node => node.getAttribute('aria-label') || '')" +
                        ".filter(label => label && (" +
                        "  label.toLowerCase().includes('view') || label.toLowerCase().includes('report')))" +
                        ".join(' | ');"
        );
        String sample = (String) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics'))" +
                        ".map(node => node.getAttribute('aria-label') || '')" +
                        ".filter(Boolean).join(' | ');"
        );
        String checkboxRelated = (String) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics'))" +
                        ".map(node => node.getAttribute('aria-label') || '')" +
                        ".filter(label => label && (" +
                        "  label.toLowerCase().includes('dismiss') || label.toLowerCase().includes('block') ||" +
                        "  label.toLowerCase().includes('checkbox') || label.toLowerCase().includes('take_action') ||" +
                        "  label.toLowerCase().includes('confirm')))" +
                        ".join(' | ');"
        );
        System.out.println("Checkbox/action related semantics: "
                + (checkboxRelated == null || checkboxRelated.isBlank() ? "(none)" : checkboxRelated));
        System.out.println("Available moderation action semantics: "
                + (labels == null || labels.isBlank() ? "(none)" : labels));
        System.out.println("View/report related semantics: "
                + (viewRelated == null || viewRelated.isBlank() ? "(none)" : viewRelated));
        if (sample != null && !sample.isBlank()) {
            int end = Math.min(sample.length(), 1200);
            System.out.println("Semantics sample: " + sample.substring(0, end));
        }
    }

    private void pauseAfterNavigation() {
        semantics.pauseAfterScroll();
    }

    private boolean waitUntil(Function<WebDriver, Boolean> condition, Duration timeout) {
        return semantics.waitUntil(condition, timeout);
    }
}
