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
 * Delete/Block row actions, reason dialog entry, and confirm buttons for users tables.
 */
public class UserRowActionsSupport {

    private static final String CONFIRM_DELETE_REASON_BUTTON = "confirm_delete_reason_button";
    private static final String CONFIRM_BLOCK_REASON_BUTTON = "confirm_block_reason_button";
    private static final String CONFIRM_UNBLOCK_REASON_BUTTON = "confirm_unblock_reason_button";
    private static final String REASON_FIELD = "reason_field";

    private final WebDriver driver;
    private final FlutterSemanticsSupport semantics;
    private final UserRowActionsConfig config;

    private final By deleteButton;
    private final By deleteButtonTapTarget;
    private final By blockButton;
    private final By blockButtonTapTarget;
    private final By unblockButton;
    private final By unblockButtonTapTarget;
    private final By reasonFieldSemantics;
    private final By reasonFieldInput;
    private final By confirmDeleteButton;
    private final By confirmBlockButton;
    private final By confirmUnblockButton;

    public UserRowActionsSupport(WebDriver driver, FlutterSemanticsSupport semantics, UserRowActionsConfig config) {
        this.driver = driver;
        this.semantics = semantics;
        this.config = config;
        this.deleteButton = semanticsButton(config.deleteButtonLabel());
        this.deleteButtonTapTarget = semanticsTapTarget(config.deleteButtonLabel());
        this.blockButton = semanticsButton(config.blockButtonLabel());
        this.blockButtonTapTarget = semanticsTapTarget(config.blockButtonLabel());
        this.unblockButton = semanticsButton(config.unblockButtonLabel());
        this.unblockButtonTapTarget = semanticsTapTarget(config.unblockButtonLabel());
        this.reasonFieldSemantics = By.xpath(
                "//flt-semantics[@aria-label='" + REASON_FIELD + "']" +
                        " | //flt-semantics[contains(@aria-label,'" + REASON_FIELD + "')]");
        this.reasonFieldInput = By.xpath(
                "//flt-semantics[@aria-label='" + REASON_FIELD + "']//input" +
                        " | //flt-semantics[contains(@aria-label,'" + REASON_FIELD + "')]//input" +
                        " | //input[contains(@aria-label,'" + REASON_FIELD + "')]");
        this.confirmDeleteButton = semanticsButton(CONFIRM_DELETE_REASON_BUTTON);
        this.confirmBlockButton = semanticsButton(CONFIRM_BLOCK_REASON_BUTTON);
        this.confirmUnblockButton = semanticsButton(CONFIRM_UNBLOCK_REASON_BUTTON);
    }

    public static UserRowActionsSupport forCompany(WebDriver driver, FlutterSemanticsSupport semantics) {
        return new UserRowActionsSupport(driver, semantics, UserRowActionsConfig.company());
    }

    public static UserRowActionsSupport forIndividual(WebDriver driver, FlutterSemanticsSupport semantics) {
        return new UserRowActionsSupport(driver, semantics, UserRowActionsConfig.individual());
    }

    public void scrollTableActionsIntoView() {
        semantics.enableFlutterSemanticsOn(driver);
        ((JavascriptExecutor) driver).executeScript(
                "const rowCell = document.querySelector('flt-semantics[aria-label=\"user_row_id_0\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label^=\"VI-\"]');" +
                        "const deleteBtn = document.querySelector('flt-semantics[aria-label=\"" + config.deleteButtonLabel() + "\"]');" +
                        "const blockBtn = document.querySelector('flt-semantics[aria-label=\"" + config.blockButtonLabel() + "\"]')" +
                        "  || Array.from(document.querySelectorAll('flt-semantics')).find(n => {" +
                        "       const label = (n.getAttribute('aria-label') || '').toLowerCase();" +
                        "       const text = (n.textContent || '').trim();" +
                        "       return label.includes('block_') && label.includes('button') && text === 'Block';" +
                        "     });" +
                        "const unblockBtn = document.querySelector('flt-semantics[aria-label=\"" + config.unblockButtonLabel() + "\"]');" +
                        "const anchor = blockBtn || deleteBtn || unblockBtn || rowCell;" +
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
                        "for (let i = 0; i < 6; i++) {" +
                        "  target.dispatchEvent(new WheelEvent('wheel', { deltaX: 400, deltaY: 0, bubbles: true, cancelable: true }));" +
                        "}"
        );
    }

    public void requireDeleteOrBlockVisible() {
        if (!semantics.waitUntil(d -> isDeleteActionAvailable(d) || isBlockActionAvailable(d)
                        || isUnblockActionAvailable(d),
                FlutterSemanticsSupport.DEFAULT_WAIT)) {
            logRowActionSemantics();
            throw new NoSuchElementException(
                    "Delete, Block, or Unblock action not visible after scroll (expected "
                            + config.deleteButtonLabel() + ", " + config.blockButtonLabel()
                            + ", or " + config.unblockButtonLabel() + ")");
        }
    }

    public void clickDelete(String pageName, Runnable beforeEachAttempt) {
        semantics.enableFlutterSemantics();
        if (!semantics.waitUntil(this::isDeleteActionAvailable, FlutterSemanticsSupport.DEFAULT_WAIT)) {
            logRowActionSemantics();
            throw new NoSuchElementException(
                    "Delete action is not visible on " + pageName + " table (expected "
                            + config.deleteButtonLabel() + " or row Delete text)");
        }
        int maxAttempts = beforeEachAttempt == null ? 4 : 5;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            if (attempt > 0) {
                scrollTableActionsIntoView();
                semantics.pauseAfterScroll();
            }
            semantics.enableFlutterSemantics();
            if (beforeEachAttempt != null) {
                beforeEachAttempt.run();
            }
            if (clickDeleteButtonSemantics(beforeEachAttempt != null)) {
                semantics.pauseAfterScroll();
                if (isDeleteConfirmationDialogOpen(FlutterSemanticsSupport.DELETE_DIALOG_POLL)) {
                    return;
                }
            }
            performDeleteButtonClickFallback(beforeEachAttempt != null);
            semantics.pauseAfterScroll();
            if (isDeleteConfirmationDialogOpen(FlutterSemanticsSupport.DELETE_DIALOG_POLL)) {
                return;
            }
        }
        logDeleteRelatedSemantics();
        logRowActionSemantics();
        throw new NoSuchElementException(
                "Delete confirmation dialog did not open after clicking " + config.deleteButtonLabel());
    }

    public void clickBlock(String pageName, Runnable beforeEachAttempt) {
        semantics.forceEnableFlutterSemantics();
        scrollTableActionsIntoView();
        semantics.pauseAfterScroll();
        if (beforeEachAttempt != null) {
            beforeEachAttempt.run();
            scrollTableActionsIntoView();
            semantics.pauseAfterScroll();
        }
        if (!semantics.waitUntil(this::isBlockActionAvailable, FlutterSemanticsSupport.DEFAULT_WAIT)) {
            // First-row Block may be off-screen; retry scroll + semantics once more.
            semantics.forceEnableFlutterSemantics();
            scrollTableActionsIntoView();
            semantics.pauseAfterScroll();
            if (beforeEachAttempt != null) {
                beforeEachAttempt.run();
            }
        }
        if (!semantics.waitUntil(this::isBlockActionAvailable, FlutterSemanticsSupport.DEFAULT_WAIT)) {
            logRowActionSemantics();
            throw new NoSuchElementException(
                    "Block action is not visible on " + pageName + " table (expected "
                            + config.blockButtonLabel() + " or row Block text)");
        }
        for (int attempt = 0; attempt < 5; attempt++) {
            if (attempt > 0) {
                scrollTableActionsIntoView();
                semantics.pauseAfterScroll();
            }
            semantics.enableFlutterSemantics();
            if (beforeEachAttempt != null) {
                beforeEachAttempt.run();
            }
            if (clickBlockButtonSemantics(beforeEachAttempt != null)) {
                semantics.pauseAfterScroll();
                if (isBlockConfirmationDialogOpen(FlutterSemanticsSupport.DELETE_DIALOG_POLL)) {
                    return;
                }
            }
            performBlockButtonClickFallback();
            semantics.pauseAfterScroll();
            if (isBlockConfirmationDialogOpen(FlutterSemanticsSupport.DELETE_DIALOG_POLL)) {
                return;
            }
        }
        logDeleteRelatedSemantics();
        logRowActionSemantics();
        throw new NoSuchElementException(
                "Block confirmation dialog did not open after clicking " + config.blockButtonLabel());
    }

    public void enterReasonText(String reason) {
        waitForReasonConfirmationDialog();
        semantics.enableFlutterSemantics();
        clickReasonFieldToFocus();
        semantics.pauseAfterScroll();
        List<WebElement> reasonInputs = driver.findElements(reasonFieldInput);
        if (!reasonInputs.isEmpty()) {
            WebElement reasonInput = reasonInputs.get(0);
            semantics.scrollIntoView(reasonInput);
            reasonInput.click();
            reasonInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), reason);
            if (semantics.waitUntil(d -> {
                List<WebElement> inputs = d.findElements(reasonFieldInput);
                if (inputs.isEmpty()) {
                    return false;
                }
                String value = inputs.get(0).getAttribute("value");
                return value != null && value.contains(reason);
            }, Duration.ofSeconds(3))) {
                return;
            }
        }
        for (int attempt = 0; attempt < 6; attempt++) {
            WebElement reasonInput = findReasonInputViaScript();
            if (reasonInput != null) {
                semantics.scrollIntoView(reasonInput);
                reasonInput.click();
                reasonInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), reason);
                if (semantics.waitUntil(d -> {
                    WebElement input = findReasonInputViaScriptOn(d);
                    if (input == null) {
                        return false;
                    }
                    String value = input.getAttribute("value");
                    return value != null && value.contains(reason);
                }, Duration.ofSeconds(3))) {
                    return;
                }
                enterReasonTextViaScript(reason);
                return;
            }
            clickReasonFieldToFocus();
            semantics.pauseAfterScroll();
            try {
                new Actions(driver).sendKeys(reason).perform();
                if (semantics.waitUntil(d -> {
                    String text = semantics.getSemanticsText(d);
                    return text != null && text.contains(reason);
                }, Duration.ofSeconds(3))) {
                    return;
                }
            } catch (Exception ignored) {
                // Retry focus and typing on next attempt.
            }
        }
        logDeleteRelatedSemantics();
        throw new NoSuchElementException("Reason field input not found (reason_field semantics)");
    }

    public void clickConfirmDelete() {
        semantics.enableFlutterSemantics();
        semantics.pauseAfterScroll();
        if (semantics.clickSemanticsLabelViaScript(CONFIRM_DELETE_REASON_BUTTON)) {
            waitForDeleteDialogClosed();
            return;
        }
        if (clickConfirmDeleteViaScript()) {
            waitForDeleteDialogClosed();
            return;
        }
        if (clickConfirmDeleteInDialogViaScript()) {
            waitForDeleteDialogClosed();
            return;
        }
        List<WebElement> confirmButtons = driver.findElements(confirmDeleteButton);
        if (!confirmButtons.isEmpty()) {
            WebElement confirmButton = confirmButtons.get(0);
            semantics.scrollIntoView(confirmButton);
            if (semantics.clickElementReliably(confirmButton)) {
                waitForDeleteDialogClosed();
                return;
            }
        }
        throw new NoSuchElementException(
                "Confirm Delete button is not clickable (" + CONFIRM_DELETE_REASON_BUTTON + ")");
    }

    public void clickConfirmBlock() {
        semantics.enableFlutterSemantics();
        semantics.pauseAfterScroll();
        if (semantics.clickSemanticsLabelViaScript(CONFIRM_BLOCK_REASON_BUTTON)) {
            waitForBlockDialogClosed();
            return;
        }
        if (clickConfirmBlockViaScript()) {
            waitForBlockDialogClosed();
            return;
        }
        List<WebElement> confirmButtons = driver.findElements(confirmBlockButton);
        if (!confirmButtons.isEmpty()) {
            WebElement confirmButton = confirmButtons.get(0);
            semantics.scrollIntoView(confirmButton);
            if (semantics.clickElementReliably(confirmButton)) {
                waitForBlockDialogClosed();
                return;
            }
        }
        throw new NoSuchElementException(
                "Confirm Block button is not clickable (" + CONFIRM_BLOCK_REASON_BUTTON + ")");
    }

    public void clickUnblock(String pageName, Runnable beforeEachAttempt) {
        semantics.enableFlutterSemantics();
        if (!semantics.waitUntil(this::isUnblockActionAvailable, FlutterSemanticsSupport.DEFAULT_WAIT)) {
            logRowActionSemantics();
            throw new NoSuchElementException(
                    "Unblock action is not visible on " + pageName + " table (expected "
                            + config.unblockButtonLabel() + " or row Unblock text)");
        }
        for (int attempt = 0; attempt < 5; attempt++) {
            if (attempt > 0) {
                scrollTableActionsIntoView();
                semantics.pauseAfterScroll();
            }
            semantics.enableFlutterSemantics();
            if (beforeEachAttempt != null) {
                beforeEachAttempt.run();
            }
            if (clickUnblockButtonSemantics(beforeEachAttempt != null)) {
                semantics.pauseAfterScroll();
                if (isUnblockConfirmationDialogOpen(FlutterSemanticsSupport.DELETE_DIALOG_POLL)) {
                    return;
                }
            }
            performUnblockButtonClickFallback();
            semantics.pauseAfterScroll();
            if (isUnblockConfirmationDialogOpen(FlutterSemanticsSupport.DELETE_DIALOG_POLL)) {
                return;
            }
        }
        logDeleteRelatedSemantics();
        logRowActionSemantics();
        throw new NoSuchElementException(
                "Unblock confirmation dialog did not open after clicking " + config.unblockButtonLabel());
    }

    public void clickConfirmUnblock() {
        semantics.enableFlutterSemantics();
        semantics.pauseAfterScroll();
        if (semantics.clickSemanticsLabelViaScript(CONFIRM_UNBLOCK_REASON_BUTTON)) {
            waitForUnblockDialogClosed();
            return;
        }
        if (clickConfirmUnblockViaScript()) {
            waitForUnblockDialogClosed();
            return;
        }
        List<WebElement> confirmButtons = driver.findElements(confirmUnblockButton);
        if (!confirmButtons.isEmpty()) {
            WebElement confirmButton = confirmButtons.get(0);
            semantics.scrollIntoView(confirmButton);
            if (semantics.clickElementReliably(confirmButton)) {
                waitForUnblockDialogClosed();
                return;
            }
        }
        throw new NoSuchElementException(
                "Confirm Unblock button is not clickable (" + CONFIRM_UNBLOCK_REASON_BUTTON + ")");
    }

    private boolean isDeleteActionAvailable(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FlutterRowActionScripts.DELETE_ROW_FIND_SCRIPT +
                        "const node = findFirstRowDeleteNode(arguments[0]);" +
                        "if (!node) return false;" +
                        "const target = deleteClickTarget(node);" +
                        "target.scrollIntoView({block: 'center', inline: 'end'});" +
                        "return isTargetVisible(target);",
                config.deleteButtonLabel()
        ));
    }

    private boolean isBlockActionAvailable(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FlutterRowActionScripts.BLOCK_ROW_FIND_SCRIPT +
                        "const node = findFirstRowBlockNode(arguments[0]);" +
                        "if (!node) return false;" +
                        "const target = blockClickTarget(node);" +
                        "target.scrollIntoView({block: 'center', inline: 'end'});" +
                        "return isTargetVisible(target) || isTargetVisible(node);",
                config.blockButtonLabel()
        ));
    }

    private boolean isUnblockActionAvailable(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FlutterRowActionScripts.UNBLOCK_ROW_FIND_SCRIPT +
                        "const node = findFirstRowUnblockNode(arguments[0]);" +
                        "if (!node) return false;" +
                        "const target = unblockClickTarget(node);" +
                        "target.scrollIntoView({block: 'center', inline: 'end'});" +
                        "return isTargetVisible(target) || isTargetVisible(node);",
                config.unblockButtonLabel()
        ));
    }

    private boolean clickDeleteButtonSemantics(boolean withHoverEvents) {
        scrollDeleteButtonIntoView();
        String pointerEvents = withHoverEvents
                ? "['mouseover','mouseenter','pointerdown','mousedown','pointerup','mouseup','click']"
                : "['pointerdown','mousedown','pointerup','mouseup','click']";
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FlutterRowActionScripts.DELETE_ROW_FIND_SCRIPT +
                        "const label = arguments[0];" +
                        "const dispatchPointer = (target, x, y) => {" +
                        "  const hit = document.elementFromPoint(x, y) || target;" +
                        "  " + pointerEvents + ".forEach(type => hit.dispatchEvent(new MouseEvent(type, {" +
                        "    bubbles: true, cancelable: true, clientX: x, clientY: y, view: window" +
                        "  })));" +
                        "  return true;" +
                        "};" +
                        "const node = findFirstRowDeleteNode(label);" +
                        "if (!node) return false;" +
                        "const target = deleteClickTarget(node);" +
                        "target.scrollIntoView({block: 'center', inline: 'end'});" +
                        "const rect = target.getBoundingClientRect();" +
                        "if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "return dispatchPointer(target, rect.left + rect.width / 2, rect.top + rect.height / 2);",
                config.deleteButtonLabel()
        ));
    }

    private boolean clickBlockButtonSemantics(boolean withHoverEvents) {
        scrollBlockButtonIntoView();
        String pointerEvents = withHoverEvents
                ? "['mouseover','mouseenter','pointerdown','mousedown','pointerup','mouseup','click']"
                : "['pointerdown','mousedown','pointerup','mouseup','click']";
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FlutterRowActionScripts.BLOCK_ROW_FIND_SCRIPT +
                        "const label = arguments[0];" +
                        "const dispatchPointer = (target, x, y) => {" +
                        "  const hit = document.elementFromPoint(x, y) || target;" +
                        "  " + pointerEvents + ".forEach(type => hit.dispatchEvent(new MouseEvent(type, {" +
                        "    bubbles: true, cancelable: true, clientX: x, clientY: y, view: window" +
                        "  })));" +
                        "  return true;" +
                        "};" +
                        "const node = findFirstRowBlockNode(label);" +
                        "if (!node) return false;" +
                        "const target = blockClickTarget(node);" +
                        "target.scrollIntoView({block: 'center', inline: 'end'});" +
                        "const rect = target.getBoundingClientRect();" +
                        "if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "return dispatchPointer(target, rect.left + rect.width / 2, rect.top + rect.height / 2);",
                config.blockButtonLabel()
        ));
    }

    private boolean clickUnblockButtonSemantics(boolean withHoverEvents) {
        scrollUnblockButtonIntoView();
        String pointerEvents = withHoverEvents
                ? "['mouseover','mouseenter','pointerdown','mousedown','pointerup','mouseup','click']"
                : "['pointerdown','mousedown','pointerup','mouseup','click']";
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FlutterRowActionScripts.UNBLOCK_ROW_FIND_SCRIPT +
                        "const label = arguments[0];" +
                        "const dispatchPointer = (target, x, y) => {" +
                        "  const hit = document.elementFromPoint(x, y) || target;" +
                        "  " + pointerEvents + ".forEach(type => hit.dispatchEvent(new MouseEvent(type, {" +
                        "    bubbles: true, cancelable: true, clientX: x, clientY: y, view: window" +
                        "  })));" +
                        "  return true;" +
                        "};" +
                        "const node = findFirstRowUnblockNode(label);" +
                        "if (!node) return false;" +
                        "const target = unblockClickTarget(node);" +
                        "target.scrollIntoView({block: 'center', inline: 'end'});" +
                        "const rect = target.getBoundingClientRect();" +
                        "if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "return dispatchPointer(target, rect.left + rect.width / 2, rect.top + rect.height / 2);",
                config.unblockButtonLabel()
        ));
    }

    private void performDeleteButtonClickFallback(boolean useViewportCenterClick) {
        scrollDeleteButtonIntoView();
        semantics.enableFlutterSemantics();
        semantics.pauseAfterScroll();
        clickDeleteButtonViaScript();
        if (isDeleteConfirmationDialogOpen(Duration.ofSeconds(2))) {
            return;
        }
        for (WebElement tapTarget : driver.findElements(deleteButtonTapTarget)) {
            semantics.scrollIntoView(tapTarget);
            semantics.clickElementReliably(tapTarget);
            if (useViewportCenterClick) {
                semantics.clickAtElementViewportCenter(tapTarget);
            }
            if (isDeleteConfirmationDialogOpen(Duration.ofSeconds(2))) {
                return;
            }
        }
        List<WebElement> buttons = driver.findElements(deleteButton);
        if (!buttons.isEmpty()) {
            WebElement deleteBtn = buttons.get(0);
            semantics.scrollIntoView(deleteBtn);
            semantics.clickElementReliably(deleteBtn);
            if (useViewportCenterClick) {
                semantics.clickAtElementViewportCenter(deleteBtn);
            }
        }
    }

    private void performBlockButtonClickFallback() {
        scrollBlockButtonIntoView();
        semantics.enableFlutterSemantics();
        semantics.pauseAfterScroll();
        if (semantics.clickSemanticsLabelViaScript(config.blockButtonLabel())) {
            if (isBlockConfirmationDialogOpen(Duration.ofSeconds(2))) {
                return;
            }
        }
        for (WebElement tapTarget : driver.findElements(blockButtonTapTarget)) {
            semantics.scrollIntoView(tapTarget);
            semantics.clickElementReliably(tapTarget);
            semantics.clickAtElementViewportCenter(tapTarget);
        }
        List<WebElement> buttons = driver.findElements(blockButton);
        if (!buttons.isEmpty()) {
            WebElement blockBtn = buttons.get(0);
            semantics.scrollIntoView(blockBtn);
            semantics.clickElementReliably(blockBtn);
            semantics.clickAtElementViewportCenter(blockBtn);
        }
    }

    private void performUnblockButtonClickFallback() {
        scrollUnblockButtonIntoView();
        semantics.enableFlutterSemantics();
        semantics.pauseAfterScroll();
        if (semantics.clickSemanticsLabelViaScript(config.unblockButtonLabel())) {
            if (isUnblockConfirmationDialogOpen(Duration.ofSeconds(2))) {
                return;
            }
        }
        for (WebElement tapTarget : driver.findElements(unblockButtonTapTarget)) {
            semantics.scrollIntoView(tapTarget);
            semantics.clickElementReliably(tapTarget);
            semantics.clickAtElementViewportCenter(tapTarget);
        }
        List<WebElement> buttons = driver.findElements(unblockButton);
        if (!buttons.isEmpty()) {
            WebElement unblockBtn = buttons.get(0);
            semantics.scrollIntoView(unblockBtn);
            semantics.clickElementReliably(unblockBtn);
            semantics.clickAtElementViewportCenter(unblockBtn);
        }
    }

    private void scrollDeleteButtonIntoView() {
        semantics.enableFlutterSemanticsOn(driver);
        ((JavascriptExecutor) driver).executeScript(
                FlutterRowActionScripts.DELETE_ROW_FIND_SCRIPT +
                        "const deleteBtn = findFirstRowDeleteNode(arguments[0]);" +
                        "if (deleteBtn) {" +
                        "  deleteBtn.scrollIntoView({block: 'center', inline: 'end'});" +
                        "  return;" +
                        "}" +
                        "const rowCell = document.querySelector('flt-semantics[aria-label=\"user_row_id_0\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label^=\"VI-\"]');" +
                        "if (rowCell) rowCell.scrollIntoView({block: 'center', inline: 'end'});",
                config.deleteButtonLabel()
        );
    }

    private void scrollBlockButtonIntoView() {
        semantics.enableFlutterSemanticsOn(driver);
        ((JavascriptExecutor) driver).executeScript(
                FlutterRowActionScripts.BLOCK_ROW_FIND_SCRIPT +
                        "const blockBtn = findFirstRowBlockNode(arguments[0]);" +
                        "if (blockBtn) {" +
                        "  blockBtn.scrollIntoView({block: 'center', inline: 'end'});" +
                        "  return;" +
                        "}" +
                        "const rowCell = document.querySelector('flt-semantics[aria-label=\"user_row_id_0\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label^=\"VI-\"]');" +
                        "if (rowCell) rowCell.scrollIntoView({block: 'center', inline: 'end'});",
                config.blockButtonLabel()
        );
    }

    private void scrollUnblockButtonIntoView() {
        semantics.enableFlutterSemanticsOn(driver);
        ((JavascriptExecutor) driver).executeScript(
                FlutterRowActionScripts.UNBLOCK_ROW_FIND_SCRIPT +
                        "const unblockBtn = findFirstRowUnblockNode(arguments[0]);" +
                        "if (unblockBtn) {" +
                        "  unblockBtn.scrollIntoView({block: 'center', inline: 'end'});" +
                        "  return;" +
                        "}" +
                        "const rowCell = document.querySelector('flt-semantics[aria-label=\"user_row_id_0\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label^=\"VI-\"]');" +
                        "if (rowCell) rowCell.scrollIntoView({block: 'center', inline: 'end'});",
                config.unblockButtonLabel()
        );
    }

    private void clickDeleteButtonViaScript() {
        semantics.enableFlutterSemanticsOn(driver);
        ((JavascriptExecutor) driver).executeScript(
                "const clickNode = (node) => {" +
                        "  if (!node) return false;" +
                        "  node.scrollIntoView({block: 'center', inline: 'nearest'});" +
                        "  const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "  const rect = target.getBoundingClientRect();" +
                        "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "  const x = rect.left + rect.width / 2;" +
                        "  const y = rect.top + rect.height / 2;" +
                        "  const hit = document.elementFromPoint(x, y) || target;" +
                        "  ['pointerdown','mousedown','mouseup','pointerup','click'].forEach(type => {" +
                        "    hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                        "  });" +
                        "  return true;" +
                        "};" +
                        "const deleteLabel = arguments[0];" +
                        "const fragment = arguments[1];" +
                        "const deleteSemantics = document.querySelector('flt-semantics[aria-label=\"' + deleteLabel + '\"]');" +
                        "if (deleteSemantics) {" +
                        "  const tappable = deleteSemantics.closest('flt-semantics[flt-tappable]');" +
                        "  if (tappable && clickNode(tappable)) return;" +
                        "  if (clickNode(deleteSemantics)) return;" +
                        "}" +
                        "const rowCell = document.querySelector('flt-semantics[aria-label=\"user_row_id_0\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label^=\"VI-\"]');" +
                        "if (!rowCell) return;" +
                        "let rowAncestor = rowCell;" +
                        "for (let depth = 0; depth < 14 && rowAncestor; depth++) {" +
                        "  const deleteBtn = Array.from(rowAncestor.querySelectorAll('flt-semantics')).find(node => {" +
                        "    const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "    return label.includes(fragment);" +
                        "  });" +
                        "  if (deleteBtn && clickNode(deleteBtn)) return;" +
                        "  rowAncestor = rowAncestor.parentElement;" +
                        "}",
                config.deleteButtonLabel(), config.deleteButtonLabelFragment()
        );
    }

    private boolean isDeleteConfirmationDialogOpen(Duration timeout) {
        return semantics.waitUntil(this::isDeleteConfirmationDialogVisible, timeout);
    }

    private boolean isDeleteConfirmationDialogVisible(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (findReasonInputViaScriptOn(webDriver) != null) {
            return true;
        }
        if (!webDriver.findElements(confirmDeleteButton).isEmpty()) {
            return true;
        }
        String pageText = semantics.getSemanticsText(webDriver);
        return pageText != null && pageText.toLowerCase().contains("are you sure you want to delete");
    }

    private boolean isBlockConfirmationDialogOpen(Duration timeout) {
        return semantics.waitUntil(this::isBlockConfirmationDialogVisible, timeout);
    }

    private boolean isBlockConfirmationDialogVisible(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (findReasonInputViaScriptOn(webDriver) != null) {
            return true;
        }
        if (!webDriver.findElements(confirmBlockButton).isEmpty()) {
            return true;
        }
        String pageText = semantics.getSemanticsText(webDriver);
        return pageText != null && pageText.toLowerCase().contains("are you sure you want to block");
    }

    private boolean isUnblockConfirmationDialogOpen(Duration timeout) {
        return semantics.waitUntil(this::isUnblockConfirmationDialogVisible, timeout);
    }

    private boolean isUnblockConfirmationDialogVisible(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (findReasonInputViaScriptOn(webDriver) != null) {
            return true;
        }
        if (!webDriver.findElements(confirmUnblockButton).isEmpty()) {
            return true;
        }
        String pageText = semantics.getSemanticsText(webDriver);
        return pageText != null && pageText.toLowerCase().contains("are you sure you want to unblock");
    }

    private void waitForReasonConfirmationDialog() {
        if (!semantics.waitUntil(d -> isDeleteConfirmationDialogVisible(d)
                || isBlockConfirmationDialogVisible(d)
                || isUnblockConfirmationDialogVisible(d), FlutterSemanticsSupport.DEFAULT_WAIT)) {
            throw new NoSuchElementException(
                    "Confirmation dialog did not appear (reason_field / confirm button semantics)");
        }
    }

    private WebElement findReasonInputViaScript() {
        return findReasonInputViaScriptOn(driver);
    }

    private WebElement findReasonInputViaScriptOn(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return (WebElement) ((JavascriptExecutor) webDriver).executeScript(
                "const searchInput = document.querySelector('flt-semantics[aria-label=\"' + arguments[0] + '\"] input');" +
                        "const isUsableInput = (input) => {" +
                        "  if (!input || input === searchInput) return false;" +
                        "  const label = (input.getAttribute('aria-label') || '').toLowerCase();" +
                        "  if (label.includes('search') || label.includes('email') || label.includes('password')) {" +
                        "    return false;" +
                        "  }" +
                        "  const rect = input.getBoundingClientRect();" +
                        "  return rect.width > 0 && rect.height > 0;" +
                        "};" +
                        "const selectors = [" +
                        "  'flt-semantics[aria-label=\"reason_field\"] input'," +
                        "  'flt-semantics[aria-label*=\"reason_field\"] input'," +
                        "  'input[aria-label*=\"reason_field\"]'" +
                        "];" +
                        "for (const selector of selectors) {" +
                        "  const input = document.querySelector(selector);" +
                        "  if (isUsableInput(input)) return input;" +
                        "}" +
                        "const hostText = (document.querySelector('flt-semantics-host') || {}).textContent || '';" +
                        "const hostLower = hostText.toLowerCase();" +
                        "if (!hostLower.includes('are you sure you want to delete') &&" +
                        "    !hostLower.includes('are you sure you want to block') &&" +
                        "    !hostLower.includes('are you sure you want to unblock')) return null;" +
                        "const dialogInputs = Array.from(document.querySelectorAll('input, textarea')).filter(isUsableInput);" +
                        "return dialogInputs.length ? dialogInputs[dialogInputs.length - 1] : null;",
                config.searchFieldLabel()
        );
    }

    private void clickReasonFieldToFocus() {
        semantics.clickSemanticsLabelViaScript(REASON_FIELD);
        ((JavascriptExecutor) driver).executeScript(
                "const hostText = (document.querySelector('flt-semantics-host') || {}).textContent || '';" +
                        "const lower = hostText.toLowerCase();" +
                        "if (!lower.includes('are you sure you want to delete') &&" +
                        "    !lower.includes('are you sure you want to block') &&" +
                        "    !lower.includes('are you sure you want to unblock')) return;" +
                        "const reasonNode = document.querySelector('flt-semantics[aria-label=\"reason_field\"]');" +
                        "if (!reasonNode) return;" +
                        "const target = reasonNode.closest('flt-semantics[flt-tappable]') || reasonNode;" +
                        "const rect = target.getBoundingClientRect();" +
                        "const x = rect.left + Math.max(8, rect.width / 2);" +
                        "const y = rect.top + Math.max(8, rect.height / 2);" +
                        "const hit = document.elementFromPoint(x, y) || target;" +
                        "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "  hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                        "});"
        );
    }

    private void enterReasonTextViaScript(String reason) {
        WebElement input = findReasonInputViaScript();
        if (input == null) {
            return;
        }
        ((JavascriptExecutor) driver).executeScript(
                "const text = arguments[0];" +
                        "const input = arguments[1];" +
                        "input.focus();" +
                        "input.value = text;" +
                        "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                        "input.dispatchEvent(new Event('change', { bubbles: true }));",
                reason, input
        );
    }

    private boolean clickConfirmDeleteViaScript() {
        semantics.enableFlutterSemanticsOn(driver);
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const reason = document.querySelector('flt-semantics[aria-label=\"reason_field\"]');" +
                        "const dialogRoot = reason ? reason.closest('flt-semantics-host') || document.body : document.body;" +
                        "const fragment = arguments[0];" +
                        "const matches = Array.from(dialogRoot.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').trim();" +
                        "  if (label.includes(fragment)) return false;" +
                        "  if (label.includes('reason_field')) return false;" +
                        "  if (label === 'confirm_delete_reason_button' || label.includes('confirm_delete_reason_button')) return true;" +
                        "  return text === 'Delete' && !!reason;" +
                        "});" +
                        "const target = matches.length ? matches[matches.length - 1] : null;" +
                        "if (!target) return false;" +
                        "const tappable = target.closest('flt-semantics[flt-tappable]') || target;" +
                        "const rect = tappable.getBoundingClientRect();" +
                        "const x = rect.left + rect.width / 2;" +
                        "const y = rect.top + rect.height / 2;" +
                        "const hit = document.elementFromPoint(x, y) || tappable;" +
                        "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "  hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                        "});" +
                        "return true;",
                config.deleteButtonLabelFragment()
        ));
    }

    private boolean clickConfirmDeleteInDialogViaScript() {
        semantics.enableFlutterSemanticsOn(driver);
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const dispatchPointer = (target, x, y) => {" +
                        "  const hit = document.elementFromPoint(x, y) || target;" +
                        "  ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "    hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                        "  });" +
                        "  return true;" +
                        "};" +
                        "const hostText = (document.querySelector('flt-semantics-host') || {}).textContent || '';" +
                        "if (!hostText.toLowerCase().includes('are you sure you want to delete')) return false;" +
                        "const fragment = arguments[0];" +
                        "const candidates = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').trim();" +
                        "  if (label.includes(fragment)) return false;" +
                        "  if (label.includes('reason_field')) return false;" +
                        "  if (label === 'confirm_delete_reason_button' || label.includes('confirm_delete_reason_button')) {" +
                        "    return true;" +
                        "  }" +
                        "  return text === 'Delete' && !label.includes('block');" +
                        "});" +
                        "const confirm = candidates.length ? candidates[candidates.length - 1] : null;" +
                        "if (!confirm) return false;" +
                        "const target = confirm.closest('flt-semantics[flt-tappable]') || confirm;" +
                        "const rect = target.getBoundingClientRect();" +
                        "if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "return dispatchPointer(target, rect.left + rect.width / 2, rect.top + rect.height / 2);",
                config.deleteButtonLabelFragment()
        ));
    }

    private boolean clickConfirmBlockViaScript() {
        semantics.enableFlutterSemanticsOn(driver);
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const dispatchPointer = (target, x, y) => {" +
                        "  const hit = document.elementFromPoint(x, y) || target;" +
                        "  ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "    hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                        "  });" +
                        "  return true;" +
                        "};" +
                        "const hostText = (document.querySelector('flt-semantics-host') || {}).textContent || '';" +
                        "if (!hostText.toLowerCase().includes('are you sure you want to block')) return false;" +
                        "const fragment = arguments[0];" +
                        "const candidates = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').trim();" +
                        "  if (label.includes(fragment.replace('delete', 'block'))) return false;" +
                        "  if (label.includes('reason_field')) return false;" +
                        "  if (label === 'confirm_block_reason_button' || label.includes('confirm_block_reason_button')) {" +
                        "    return true;" +
                        "  }" +
                        "  return text === 'Block' && !label.includes('delete');" +
                        "});" +
                        "const confirm = candidates.length ? candidates[candidates.length - 1] : null;" +
                        "if (!confirm) return false;" +
                        "const target = confirm.closest('flt-semantics[flt-tappable]') || confirm;" +
                        "const rect = target.getBoundingClientRect();" +
                        "if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "return dispatchPointer(target, rect.left + rect.width / 2, rect.top + rect.height / 2);",
                config.deleteButtonLabelFragment()
        ));
    }

    private boolean clickConfirmUnblockViaScript() {
        semantics.enableFlutterSemanticsOn(driver);
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const dispatchPointer = (target, x, y) => {" +
                        "  const hit = document.elementFromPoint(x, y) || target;" +
                        "  ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "    hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                        "  });" +
                        "  return true;" +
                        "};" +
                        "const hostText = (document.querySelector('flt-semantics-host') || {}).textContent || '';" +
                        "if (!hostText.toLowerCase().includes('are you sure you want to unblock')) return false;" +
                        "const fragment = arguments[0];" +
                        "const candidates = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').trim();" +
                        "  if (label.includes(fragment.replace('delete', 'unblock'))) return false;" +
                        "  if (label.includes('reason_field')) return false;" +
                        "  if (label === 'confirm_unblock_reason_button' || label.includes('confirm_unblock_reason_button')) {" +
                        "    return true;" +
                        "  }" +
                        "  return text === 'Unblock' && !label.includes('delete') && !label.includes('block');" +
                        "});" +
                        "const confirm = candidates.length ? candidates[candidates.length - 1] : null;" +
                        "if (!confirm) return false;" +
                        "const target = confirm.closest('flt-semantics[flt-tappable]') || confirm;" +
                        "const rect = target.getBoundingClientRect();" +
                        "if (rect.width <= 0 || rect.height <= 0) return false;" +
                        "return dispatchPointer(target, rect.left + rect.width / 2, rect.top + rect.height / 2);",
                config.deleteButtonLabelFragment()
        ));
    }

    private void waitForDeleteDialogClosed() {
        semantics.waitUntil(d -> {
            semantics.enableFlutterSemanticsOn(d);
            if (!d.findElements(reasonFieldSemantics).isEmpty()) {
                return false;
            }
            String text = semantics.getSemanticsText(d);
            return text == null || !text.toLowerCase().contains("are you sure you want to delete");
        }, FlutterSemanticsSupport.DEFAULT_WAIT);
    }

    private void waitForBlockDialogClosed() {
        semantics.waitUntil(d -> {
            semantics.enableFlutterSemanticsOn(d);
            if (!d.findElements(reasonFieldSemantics).isEmpty()) {
                return false;
            }
            String text = semantics.getSemanticsText(d);
            return text == null || !text.toLowerCase().contains("are you sure you want to block");
        }, FlutterSemanticsSupport.DEFAULT_WAIT);
    }

    private void waitForUnblockDialogClosed() {
        semantics.waitUntil(d -> {
            semantics.enableFlutterSemanticsOn(d);
            if (!d.findElements(reasonFieldSemantics).isEmpty()) {
                return false;
            }
            String text = semantics.getSemanticsText(d);
            return text == null || !text.toLowerCase().contains("are you sure you want to unblock");
        }, FlutterSemanticsSupport.DEFAULT_WAIT);
    }

    private void logDeleteRelatedSemantics() {
        semantics.enableFlutterSemanticsOn(driver);
        String labels = (String) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics'))" +
                        ".map(node => node.getAttribute('aria-label'))" +
                        ".filter(label => label && (" +
                        "  label.includes('delete') || label.includes('reason') || label.includes('block') ||" +
                        "  label.includes('unblock')" +
                        ")).join(' | ');"
        );
        System.out.println(config.logContext() + " delete-related semantics on failure: " + labels);
    }

    private void logRowActionSemantics() {
        semantics.enableFlutterSemanticsOn(driver);
        String labels = (String) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('flt-semantics'))" +
                        ".map(node => {" +
                        "  const label = node.getAttribute('aria-label') || '';" +
                        "  const text = (node.textContent || '').trim();" +
                        "  if (label) return label;" +
                        "  if (text === 'Delete' || text === 'Block' || text === 'Unblock' || text === 'Active') return 'text:' + text;" +
                        "  return null;" +
                        "})" +
                        ".filter(v => v && (" +
                        "  v.includes('row') || v.includes('delete') || v.includes('block') || v.includes('unblock') ||" +
                        "  v.startsWith('text:Delete') || v.startsWith('text:Block') || v.startsWith('text:Unblock')" +
                        ")).join(' | ');"
        );
        System.out.println(config.logContext() + " row-action semantics on failure: " + labels);
    }

    private static By semanticsButton(String label) {
        return By.xpath(
                "//flt-semantics[@aria-label='" + label + "']" +
                        " | //flt-semantics[normalize-space(@aria-label)='" + label + "']" +
                        " | //flt-semantics[contains(@aria-label,'" + label + "')]");
    }

    private static By semanticsTapTarget(String label) {
        return By.xpath(
                "//flt-semantics[@aria-label='" + label + "']/ancestor::flt-semantics[@flt-tappable][1]" +
                        " | //flt-semantics[normalize-space(@aria-label)='" + label + "']" +
                        "/ancestor::flt-semantics[@flt-tappable][1]");
    }
}
