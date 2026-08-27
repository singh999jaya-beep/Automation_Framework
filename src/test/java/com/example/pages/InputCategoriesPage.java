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

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class InputCategoriesPage {

    private static final String INPUT_CATEGORIES_TEXT = "Input Categories";

    private static final String SEMANTICS_MENU = "drawer_item_input_categories";
    private static final String SEMANTICS_TITLE = "manage_lookup_title";
    private static final String SEMANTICS_ADD_NEW = "add_new_lookup_detail_button";
    private static final String SEMANTICS_ADD_BUTTON = "professional_background_add_button";
    private static final String SEMANTICS_EDIT_SAVE_BUTTON = "professional_background_edit_button";
    private static final String SEMANTICS_MORE_ABOUT_YOU_EDIT_SAVE_BUTTON = "more_about_you_edit_button";
    private static final String SEMANTICS_ENGLISH_NAME = "name_field";
    private static final String SEMANTICS_SPANISH_NAME = "spname_field";
    private static final String SEMANTICS_PORTUGUESE_NAME = "ptname_field";
    private static final String SEMANTICS_CANADIAN_NAME = "frname_field";
    private static final String SEMANTICS_EDIT = "lookup_detail_edit_button_0";
    private static final String SEMANTICS_DELETE = "lookup_detail_delete_button_0";
    private static final String SEMANTICS_BLOCK = "lookup_detail_block_button_0";
    private static final String SEMANTICS_CONFIRM_DELETE = "confirm_delete_button";
    private static final String SEMANTICS_CONFIRM_BLOCK = "confirm_block_button";

    private static final String SEMANTICS_WHATS_IMPORTANT = "lookup_row_count_0";
    private static final String SEMANTICS_MORE_ABOUT_YOU = "lookup_row_name_1";
    private static final String SEMANTICS_INTERESTS_AND_HOBBIES = "lookup_row_name_2";
    private static final String SEMANTICS_INTERESTS_ADD_BUTTON = "interest_and_hobbies_add_button";
    private static final String SEMANTICS_INTERESTS_EDIT_SAVE_BUTTON = "interest_and_hobbies_edit_button";
    private static final String SEMANTICS_PROFESSIONAL_BACKGROUND = "lookup_row_count_3";
    private static final String SEMANTICS_SKILL_ADD_BUTTON = "what_is_important_to_you_add_button";
    private static final String SEMANTICS_SKILL_EDIT_SAVE_BUTTON = "what_is_important_to_you_edit_button";
    private static final String SEMANTICS_SOCIAL_MEDIA_LINKS = "lookup_row_count_4";
    private static final String SEMANTICS_SOCIAL_MEDIA_ADD_BUTTON = "social_media_link_name_add_button";
    private static final String SEMANTICS_SOCIAL_MEDIA_ICON_UPLOAD = "social_media_icon_upload_button";
    private static final String WHATS_IMPORTANT_TEXT = "What's important to you";
    private static final String MORE_ABOUT_YOU_TEXT = "More about you";
    private static final String INTERESTS_AND_HOBBIES_TEXT = "Interests and hobbies";
    private static final String PROFESSIONAL_BACKGROUND_TEXT = "Professional background";
    private static final String SOCIAL_MEDIA_LINKS_TEXT = "Social media links";
    private static final String SOCIAL_MEDIA_ICON_FILE_NAME = "linkedin.png";
    private static final String SOCIAL_MEDIA_ICON_PATH = "C:\\Users\\Jaya\\Downloads\\linkedin.png";
    private static final String MORE_ABOUT_YOU_DETAIL_MARKER = "Roles";
    private static final String INTERESTS_DETAIL_MARKER = "Interests";
    private static final String ADD_NEW_VISIBLE_TEXT = "Add New";
    private static final Duration DEFAULT_WAIT = FlutterSemanticsSupport.DEFAULT_WAIT;
    private static final Duration LOOKUP_DETAIL_POLL = Duration.ofSeconds(12);
    private static final Duration NAVIGATION_POLL = Duration.ofSeconds(10);
    private static final int MAX_LOOKUP_ACTION_ATTEMPTS = 5;
    private static final int MAX_NAV_ATTEMPTS = 10;

    private static final Keys SELECT_ALL_MODIFIER =
            System.getProperty("os.name", "").toLowerCase().contains("mac")
                    ? Keys.COMMAND
                    : Keys.CONTROL;

    private record LookupCategoryRow(
            String semanticsLabel,
            String visibleText,
            int combinedRowMaxLen,
            int parentRowMaxLen
    ) {
    }

    private final LookupCategoryRow whatsImportantCategory = new LookupCategoryRow(
            SEMANTICS_WHATS_IMPORTANT, WHATS_IMPORTANT_TEXT, 80, 100);
    private final LookupCategoryRow moreAboutYouCategory = new LookupCategoryRow(
            SEMANTICS_MORE_ABOUT_YOU, MORE_ABOUT_YOU_TEXT, 60, 80);
    private final LookupCategoryRow interestsCategory = new LookupCategoryRow(
            SEMANTICS_INTERESTS_AND_HOBBIES, INTERESTS_AND_HOBBIES_TEXT, 60, 80);
    private final LookupCategoryRow professionalBackgroundCategory = new LookupCategoryRow(
            SEMANTICS_PROFESSIONAL_BACKGROUND, PROFESSIONAL_BACKGROUND_TEXT, 80, 100);
    private final LookupCategoryRow socialMediaLinksCategory = new LookupCategoryRow(
            SEMANTICS_SOCIAL_MEDIA_LINKS, SOCIAL_MEDIA_LINKS_TEXT, 80, 100);

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final FlutterSemanticsSupport semantics;

    private final By inputCategoriesMenu = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_MENU + "']" +
                    " | //flt-semantics[@aria-label='Navigation menu']" +
                    "//flt-semantics[@role='button' and contains(., '" + INPUT_CATEGORIES_TEXT + "')]");

    private final By inputCategoriesTitle = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_TITLE + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_TITLE + "')]");

    private final By whatsImportantToYou = By.xpath(
            "//flt-semantics[@flt-tappable and contains(normalize-space(.), \"" + WHATS_IMPORTANT_TEXT + "\")]" +
                    " | //flt-semantics[contains(normalize-space(.), \"" + WHATS_IMPORTANT_TEXT + "\")]");

    private final By moreAboutYouBySemantics = semanticsButton(SEMANTICS_MORE_ABOUT_YOU);
    private final By moreAboutYouByText = By.xpath(
            "//flt-semantics[@flt-tappable and contains(normalize-space(.), \"" + MORE_ABOUT_YOU_TEXT + "\")]" +
                    " | //flt-semantics[contains(normalize-space(.), \"" + MORE_ABOUT_YOU_TEXT + "\")]" +
                    " | //flt-semantics[@aria-label='" + SEMANTICS_MORE_ABOUT_YOU + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_MORE_ABOUT_YOU + "')]");

    private final By interestsAndHobbiesBySemantics = semanticsButton(SEMANTICS_INTERESTS_AND_HOBBIES);
    private final By interestsAndHobbiesByText = By.xpath(
            "//flt-semantics[@flt-tappable and contains(normalize-space(.), \"" + INTERESTS_AND_HOBBIES_TEXT + "\")]" +
                    " | //flt-semantics[contains(normalize-space(.), \"" + INTERESTS_AND_HOBBIES_TEXT + "\")]" +
                    " | //flt-semantics[@aria-label='" + SEMANTICS_INTERESTS_AND_HOBBIES + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_INTERESTS_AND_HOBBIES + "')]");

    private final By professionalBackgroundBySemantics = semanticsButton(SEMANTICS_PROFESSIONAL_BACKGROUND);
    private final By professionalBackgroundByText = By.xpath(
            "//flt-semantics[@flt-tappable and contains(normalize-space(.), \"" + PROFESSIONAL_BACKGROUND_TEXT + "\")]" +
                    " | //flt-semantics[contains(normalize-space(.), \"" + PROFESSIONAL_BACKGROUND_TEXT + "\")]" +
                    " | //flt-semantics[@aria-label='" + SEMANTICS_PROFESSIONAL_BACKGROUND + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_PROFESSIONAL_BACKGROUND + "')]");

    private final By socialMediaLinksBySemantics = semanticsButton(SEMANTICS_SOCIAL_MEDIA_LINKS);
    private final By socialMediaLinksByText = By.xpath(
            "//flt-semantics[@flt-tappable and contains(normalize-space(.), \"" + SOCIAL_MEDIA_LINKS_TEXT + "\")]" +
                    " | //flt-semantics[contains(normalize-space(.), \"" + SOCIAL_MEDIA_LINKS_TEXT + "\")]" +
                    " | //flt-semantics[@aria-label='" + SEMANTICS_SOCIAL_MEDIA_LINKS + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_SOCIAL_MEDIA_LINKS + "')]");

    private final By addNewButton = semanticsButton(SEMANTICS_ADD_NEW);
    private final By addNewHeaderAddText = By.xpath(
            "//flt-semantics[normalize-space(.)='Add' and not(contains(normalize-space(.), 'Edit'))]");
    private final By addNewByVisibleText = By.xpath(
            "//flt-semantics[@flt-tappable and normalize-space(.)='" + ADD_NEW_VISIBLE_TEXT + "']" +
                    " | //flt-semantics[@role='button' and normalize-space(.)='" + ADD_NEW_VISIBLE_TEXT + "']" +
                    " | //flt-semantics[normalize-space(.)='" + ADD_NEW_VISIBLE_TEXT + "']" +
                    " | //flt-semantics[contains(normalize-space(.), '" + ADD_NEW_VISIBLE_TEXT + "')]" +
                    " | //flt-semantics[.//span[normalize-space(.)='" + ADD_NEW_VISIBLE_TEXT + "']]");
    private final By addButton = semanticsButton(SEMANTICS_ADD_BUTTON);
    private final By editButton = semanticsButton(SEMANTICS_EDIT);
    private final By deleteButton = semanticsButton(SEMANTICS_DELETE);
    private final By blockButton = semanticsButton(SEMANTICS_BLOCK);
    private final By confirmDeleteButton = semanticsButton(SEMANTICS_CONFIRM_DELETE);
    private final By confirmBlockButton = semanticsButton(SEMANTICS_CONFIRM_BLOCK);

    public InputCategoriesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_WAIT);
        this.semantics = new FlutterSemanticsSupport(driver);
    }

    public void clickInputCategoriesMenu() {
        openInputCategoriesNavigation();
        if (!waitUntil(this::isInputCategoriesPageLoaded, DEFAULT_WAIT)) {
            throw new NoSuchElementException(
                    "Input Categories page is not loaded (expected title semantics "
                            + SEMANTICS_TITLE + " or text '" + INPUT_CATEGORIES_TEXT + "')");
        }
    }

    private void openInputCategoriesNavigation() {
        semantics.enableFlutterSemantics();
        if (isInputCategoriesPageLoaded(driver)) {
            return;
        }
        waitUntil(this::isNavigationDashboardReady, DEFAULT_WAIT);
        for (int attempt = 0; attempt < MAX_NAV_ATTEMPTS; attempt++) {
            scrollNavigationToInputCategories();
            if (clickInputCategoriesNavItem()) {
                pauseAfterNavigation();
                if (isInputCategoriesPageLoaded(driver)
                        || waitUntil(this::isInputCategoriesPageLoaded, NAVIGATION_POLL)) {
                    return;
                }
            }
            scrollNavigationSidebarDown();
            pauseAfterNavigation();
        }
        throw new NoSuchElementException(
                "Input Categories menu item not found in navigation sidebar (expected semantics "
                        + SEMANTICS_MENU + ")");
    }

    private boolean clickInputCategoriesNavItem() {
        if (semantics.clickSemanticsLabelViaScript(SEMANTICS_MENU)) {
            return true;
        }
        if (!driver.findElements(inputCategoriesMenu).isEmpty()) {
            try {
                clickSemanticsElement(inputCategoriesMenu);
                return true;
            } catch (Exception ignored) {
                // Continue to text/script fallbacks.
            }
        }
        if (semantics.clickVisibleText(INPUT_CATEGORIES_TEXT)) {
            return true;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                DISPATCH_POINTER_CLICK_SCRIPT +
                        "const wanted = arguments[0];" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const nodes = menu ? Array.from(menu.querySelectorAll('flt-semantics'))" +
                        "  : Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const target = nodes.find(node => {" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return text === wanted || label.includes('input_categories');" +
                        "});" +
                        "if (!target) return false;" +
                        "const tap = target.closest('flt-semantics[flt-tappable]') || target;" +
                        "return dispatchPointerClick(tap);",
                INPUT_CATEGORIES_TEXT
        ));
    }

    private void scrollNavigationToInputCategories() {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "if (!menu) return;" +
                        "const target = Array.from(menu.querySelectorAll('flt-semantics')).find(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return label.includes('input_categories') || text === 'Input Categories';" +
                        "});" +
                        "if (target) target.scrollIntoView({block: 'center', inline: 'nearest'});"
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

    private void pauseAfterNavigation() {
        semantics.pauseAfterScroll();
    }

    private boolean isNavigationDashboardReady(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String text = semantics.getSemanticsText(webDriver);
        return text != null
                && (text.contains("Individual Users")
                || text.contains("Company Users")
                || text.contains(INPUT_CATEGORIES_TEXT)
                || text.contains("Navigation menu"));
    }

    public boolean isInputCategoriesTitleDisplayed() {
        return isInputCategoriesPageLoaded(driver);
    }

    public void clickWhatsImportantToYou() {
        ensureOnInputCategoriesOverview();
        navigateToLookupCategoryDetail(
                whatsImportantCategory,
                this::hasNavigatedToWhatsImportantDetail,
                "What's important to you detail page did not load",
                true,
                true);
    }

    public void clickInterestsAndHobbies() {
        ensureOnInputCategoriesOverview();
        navigateToLookupCategoryDetail(
                interestsCategory,
                this::hasNavigatedToInterestsDetail,
                "Interests and hobbies detail page did not load (expected lookup_row_name_2 navigation)",
                true,
                true);
    }

    public void clickProfessionalBackground() {
        ensureOnInputCategoriesOverview();
        navigateToLookupCategoryDetail(
                professionalBackgroundCategory,
                this::hasNavigatedToProfessionalBackgroundDetail,
                "Professional background detail page did not load (expected lookup_row_count_3 navigation)",
                true,
                true);
    }

    public void clickSocialMediaLinks() {
        ensureOnInputCategoriesOverview();
        navigateToLookupCategoryDetail(
                socialMediaLinksCategory,
                this::hasNavigatedToSocialMediaLinksDetail,
                "Social media links detail page did not load (expected lookup_row_count_4 navigation)",
                true,
                true);
    }

    public void enterSocialMediaName(String name) {
        enterSemanticsField(SEMANTICS_ENGLISH_NAME, name);
    }

    public void clickImageUploadButton() {
        semantics.enableFlutterSemantics();
        scrollToSemanticsLabel(SEMANTICS_SOCIAL_MEDIA_ICON_UPLOAD);
        boolean clicked = semantics.clickSemanticsLabelViaScript(SEMANTICS_SOCIAL_MEDIA_ICON_UPLOAD);
        if (!clicked && !driver.findElements(semanticsButton(SEMANTICS_SOCIAL_MEDIA_ICON_UPLOAD)).isEmpty()) {
            clickSemanticsElement(semanticsButton(SEMANTICS_SOCIAL_MEDIA_ICON_UPLOAD));
            clicked = true;
        }
        if (!clicked) {
            clicked = clickSocialMediaIconUploadViaScript();
        }
        if (!clicked) {
            clicked = clickVisibleSemanticsText("Icon") || clickVisibleSemanticsText("Upload")
                    || clickVisibleSemanticsText("Browse");
        }
        if (!clicked) {
            logLookupDetailSemantics();
            throw new NoSuchElementException(
                    "Image upload button is not clickable (expected " + SEMANTICS_SOCIAL_MEDIA_ICON_UPLOAD
                            + " or visible text 'Icon')");
        }
        uploadSocialMediaIconIfPresent();
        semantics.pauseAfterScroll();
        waitUntil(webDriver -> hasFileInput(webDriver) || isLookupFormVisible(webDriver), DEFAULT_WAIT);
    }

    private boolean clickSocialMediaIconUploadViaScript() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                DISPATCH_POINTER_CLICK_SCRIPT +
                        "const normalizeText = (value) => (value || '').replace(/\\s+/g, ' ').trim();" +
                        "const extra = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  const text = normalizeText(node.textContent);" +
                        "  const label = node.getAttribute('aria-label') || '';" +
                        "  return text === 'Icon' || label.includes('social_media_icon_upload');" +
                        "});" +
                        "const candidates = [" +
                        "  document.querySelector('flt-semantics[aria-label=\"social_media_icon_upload_button\"]')," +
                        "  document.querySelector('flt-semantics[aria-label*=\"social_media_icon_upload\"]')" +
                        "].concat(extra);" +
                        "for (const node of candidates) {" +
                        "  if (!node) continue;" +
                        "  const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "  if (dispatchPointerClick(target)) return true;" +
                        "}" +
                        "return false;"
        ));
    }

    public boolean isMoreAboutYouTextDisplayed() {
        if (isMoreAboutYouDetailPageLoaded(driver)) {
            return true;
        }
        return isMoreAboutYouSemanticsTextVisible(driver);
    }

    public boolean isMoreAboutYouDetailPageLoaded() {
        return isMoreAboutYouDetailPageLoaded(driver);
    }

    public void clickMoreAboutYou() {
        ensureOnInputCategoriesOverview();
        navigateToLookupCategoryDetail(
                moreAboutYouCategory,
                this::hasNavigatedToMoreAboutYouDetail,
                "More about you detail page did not load (expected lookup_row_name_1 navigation)",
                false,
                false);
    }

    private void ensureOnInputCategoriesOverview() {
        semantics.enableFlutterSemantics();
        if (isInputCategoriesPageLoaded(driver) && isCategoryOverviewPage(driver)) {
            scrollInputCategoriesOverviewToTop();
            return;
        }
        if (!isInputCategoriesPageLoaded(driver)) {
            openInputCategoriesNavigation();
        }
        if (!waitUntil(this::isInputCategoriesPageLoaded, DEFAULT_WAIT)) {
            throw new NoSuchElementException("Input Categories overview is not loaded");
        }
        scrollInputCategoriesOverviewToTop();
    }

    private void scrollInputCategoriesOverviewToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
        semantics.pauseAfterScroll();
    }

    private void scrollInputCategoriesOverviewDown() {
        ((JavascriptExecutor) driver).executeScript(
                "const delta = Math.max(window.innerHeight * 0.75, 320);" +
                        "window.scrollBy(0, delta);" +
                        "const host = document.querySelector('flt-semantics-host');" +
                        "if (host) host.scrollTop += delta;"
        );
        semantics.pauseAfterScroll();
    }

    private void navigateToCategoryDetailByVisibleText(
            String categoryText,
            Function<WebDriver, Boolean> hasNavigatedToDetail,
            String navigationErrorMessage) {
        semantics.enableFlutterSemantics();
        if (hasNavigatedToDetail.apply(driver)) {
            scrollLookupDetailListIntoView();
            return;
        }
        for (int attempt = 0; attempt < MAX_LOOKUP_ACTION_ATTEMPTS; attempt++) {
            scrollToCategoryListRow(categoryText);
            if (clickCategoryListRow(categoryText)
                    || clickCategoryListRowByPartialText(categoryText)
                    || clickVisibleSemanticsText(categoryText)
                    || semantics.clickVisibleText(categoryText)) {
                semantics.pauseAfterScroll();
                if (waitUntil(hasNavigatedToDetail, LOOKUP_DETAIL_POLL)) {
                    scrollLookupDetailListIntoView();
                    return;
                }
            }
            scrollInputCategoriesOverviewDown();
        }
        logLookupDetailSemantics();
        throw new NoSuchElementException(navigationErrorMessage);
    }

    private boolean clickCategoryListRowByPartialText(String categoryText) {
        String partial = categoryText.contains("'")
                ? categoryText.substring(categoryText.indexOf(' ') + 1)
                : categoryText;
        semantics.enableFlutterSemanticsOn(driver);
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                CATEGORY_LIST_ROW_FIND_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "const partial = arguments[0];" +
                        "const node = Array.from(document.querySelectorAll('flt-semantics[flt-tappable], flt-semantics[role=\"button\"]'))" +
                        "  .find(candidate => normalizeApostrophe(candidate.textContent).includes(normalizeApostrophe(partial)));" +
                        "if (!node) return false;" +
                        "return dispatchPointerClick(node.closest('flt-semantics[flt-tappable]') || node);",
                partial
        ));
    }

    private void navigateToLookupCategoryDetail(
            LookupCategoryRow category,
            Function<WebDriver, Boolean> hasNavigatedToDetail,
            String navigationErrorMessage,
            boolean detailWaitUsesFallback,
            boolean pauseBeforeDetailWait) {
        semantics.enableFlutterSemantics();
        if (hasNavigatedToDetail.apply(driver)) {
            scrollLookupDetailListIntoView();
            return;
        }
        scrollToSemanticsLabel(category.semanticsLabel());
        for (int scrollAttempt = 0; scrollAttempt < MAX_LOOKUP_ACTION_ATTEMPTS; scrollAttempt++) {
            if (isLookupCategorySemanticsTextVisible(driver, category)) {
                break;
            }
            scrollToLookupCategoryListRow(category);
            scrollInputCategoriesOverviewDown();
        }
        if (!waitUntil(webDriver -> isLookupCategorySemanticsTextVisible(webDriver, category), DEFAULT_WAIT)) {
            logLookupDetailSemantics();
            throw new NoSuchElementException(
                    category.visibleText() + " row is not visible (expected semantics "
                            + category.semanticsLabel() + " with text '" + category.visibleText() + "')");
        }
        if (!clickLookupCategoryRow(category)) {
            if (!clickCategoryListRow(category.visibleText())
                    && !clickCategoryListRowByPartialText(category.visibleText())) {
                logLookupDetailSemantics();
                throw new NoSuchElementException(
                        category.visibleText() + " row is not clickable (expected semantics "
                                + category.semanticsLabel() + ")");
            }
        }
        if (pauseBeforeDetailWait) {
            semantics.pauseAfterScroll();
        }
        boolean navigated = waitUntil(hasNavigatedToDetail, LOOKUP_DETAIL_POLL);
        if (!navigated && detailWaitUsesFallback) {
            navigated = waitUntil(hasNavigatedToDetail, DEFAULT_WAIT);
        }
        if (!navigated) {
            logLookupDetailSemantics();
            throw new NoSuchElementException(navigationErrorMessage);
        }
        semantics.pauseAfterScroll();
        scrollLookupDetailListIntoView();
    }

    private boolean isLookupCategorySemanticsTextVisible(WebDriver webDriver, LookupCategoryRow category) {
        semantics.enableFlutterSemanticsOn(webDriver);
        By semanticsLocator = categorySemanticsLocator(category.semanticsLabel());
        if (!webDriver.findElements(semanticsLocator).isEmpty()) {
            WebElement row = webDriver.findElement(semanticsLocator);
            String rowText = semantics.normalizeText(row.getText());
            if (rowText.contains(semantics.normalizeText(category.visibleText()))) {
                return true;
            }
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                LOOKUP_CATEGORY_ROW_SCRIPT +
                        "return isLookupCategorySemanticsTextVisible(arguments[0], arguments[1], arguments[2], arguments[3]);",
                category.semanticsLabel(),
                category.visibleText(),
                category.combinedRowMaxLen(),
                category.parentRowMaxLen()
        ));
    }

    private boolean clickLookupCategoryRow(LookupCategoryRow category) {
        scrollToSemanticsLabel(category.semanticsLabel());
        scrollToLookupCategoryListRow(category);
        By semanticsLocator = categorySemanticsLocator(category.semanticsLabel());
        By visibleTextLocator = categoryVisibleTextLocator(category.semanticsLabel());
        for (int attempt = 0; attempt < MAX_LOOKUP_ACTION_ATTEMPTS; attempt++) {
            if (attempt > 0) {
                scrollToSemanticsLabel(category.semanticsLabel());
                scrollToLookupCategoryListRow(category);
                semantics.pauseAfterScroll();
            }
            if (semantics.clickSemanticsLabelViaScript(category.semanticsLabel())) {
                return true;
            }
            if (!driver.findElements(semanticsLocator).isEmpty()) {
                clickSemanticsElement(semanticsLocator);
                return true;
            }
            WebElement row = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    LOOKUP_CATEGORY_ROW_SCRIPT +
                            "return findLookupCategorySemanticsRow(arguments[0], arguments[1], arguments[2], arguments[3]);",
                    category.semanticsLabel(),
                    category.visibleText(),
                    category.combinedRowMaxLen(),
                    category.parentRowMaxLen()
            );
            if (row != null) {
                semantics.scrollIntoView(row);
                if (semantics.clickElementReliably(row)) {
                    return true;
                }
            }
            if (Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                    LOOKUP_CATEGORY_ROW_SCRIPT +
                            DISPATCH_POINTER_CLICK_SCRIPT +
                            "const node = findLookupCategorySemanticsRow(arguments[0], arguments[1], arguments[2], arguments[3]);" +
                            "if (!node) return false;" +
                            "const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                            "return dispatchPointerClick(target);",
                    category.semanticsLabel(),
                    category.visibleText(),
                    category.combinedRowMaxLen(),
                    category.parentRowMaxLen()
            ))) {
                return true;
            }
            if (clickLookupCategoryRowByVisibleText(category.visibleText(), visibleTextLocator)
                    || clickCategoryListRow(category.visibleText())
                    || clickVisibleSemanticsText(category.visibleText())) {
                return true;
            }
        }
        return false;
    }

    private void scrollToLookupCategoryListRow(LookupCategoryRow category) {
        semantics.enableFlutterSemanticsOn(driver);
        ((JavascriptExecutor) driver).executeScript(
                LOOKUP_CATEGORY_ROW_SCRIPT +
                        "const node = findLookupCategorySemanticsRow(arguments[0], arguments[1], arguments[2], arguments[3]);" +
                        "if (!node) return;" +
                        "node.scrollIntoView({block: 'center', inline: 'nearest'});",
                category.semanticsLabel(),
                category.visibleText(),
                category.combinedRowMaxLen(),
                category.parentRowMaxLen()
        );
        semantics.pauseAfterScroll();
    }

    private By categorySemanticsLocator(String semanticsLabel) {
        return switch (semanticsLabel) {
            case SEMANTICS_WHATS_IMPORTANT -> semanticsButton(SEMANTICS_WHATS_IMPORTANT);
            case SEMANTICS_MORE_ABOUT_YOU -> moreAboutYouBySemantics;
            case SEMANTICS_INTERESTS_AND_HOBBIES -> interestsAndHobbiesBySemantics;
            case SEMANTICS_PROFESSIONAL_BACKGROUND -> professionalBackgroundBySemantics;
            case SEMANTICS_SOCIAL_MEDIA_LINKS -> socialMediaLinksBySemantics;
            default -> semanticsButton(semanticsLabel);
        };
    }

    private By categoryVisibleTextLocator(String semanticsLabel) {
        return switch (semanticsLabel) {
            case SEMANTICS_WHATS_IMPORTANT -> whatsImportantToYou;
            case SEMANTICS_MORE_ABOUT_YOU -> moreAboutYouByText;
            case SEMANTICS_INTERESTS_AND_HOBBIES -> interestsAndHobbiesByText;
            case SEMANTICS_PROFESSIONAL_BACKGROUND -> professionalBackgroundByText;
            case SEMANTICS_SOCIAL_MEDIA_LINKS -> socialMediaLinksByText;
            default -> By.xpath("//flt-semantics[contains(normalize-space(.), '" + semanticsLabel + "')]");
        };
    }

    private boolean isMoreAboutYouSemanticsTextVisible(WebDriver webDriver) {
        return isLookupCategorySemanticsTextVisible(webDriver, moreAboutYouCategory);
    }

    private boolean isMoreAboutYouDetailPageLoaded(WebDriver webDriver) {
        return hasNavigatedToMoreAboutYouDetail(webDriver);
    }

    private boolean hasNavigatedToProfessionalBackgroundDetail(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (isCategoryOverviewPage(webDriver)) {
            return false;
        }
        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        String professionalBackgroundText = semantics.normalizeText(PROFESSIONAL_BACKGROUND_TEXT);
        boolean onDetailBreadcrumb = pageText.contains("/ " + professionalBackgroundText);
        boolean onDetailTable = pageText.contains("Status")
                && pageText.contains("Action")
                && (pageText.contains("EditDeleteBlock") || pageText.contains("Skills")
                || pageText.contains(professionalBackgroundText));
        return onDetailBreadcrumb
                && (onDetailTable || isAddNewButtonVisible(webDriver) || isLookupDetailListVisible(webDriver));
    }

    private boolean hasNavigatedToSocialMediaLinksDetail(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (isCategoryOverviewPage(webDriver)) {
            return false;
        }
        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        String socialMediaText = semantics.normalizeText(SOCIAL_MEDIA_LINKS_TEXT);
        boolean onDetailBreadcrumb = pageText.contains("/ " + socialMediaText);
        boolean onDetailTable = pageText.contains("Status")
                && pageText.contains("Action")
                && (pageText.contains("EditDeleteBlock") || pageText.contains("Social")
                || pageText.contains(socialMediaText));
        return onDetailBreadcrumb
                && (onDetailTable || isAddNewButtonVisible(webDriver) || isLookupDetailListVisible(webDriver));
    }

    private void uploadSocialMediaIconIfPresent() {
        String iconPath = resolveSocialMediaIconPath();
        if (!java.nio.file.Files.exists(java.nio.file.Paths.get(iconPath))) {
            throw new NoSuchElementException("Social media icon file not found: " + iconPath);
        }
        semantics.pauseAfterScroll();
        if (sendKeysToFileInputIfPresent(iconPath)) {
            return;
        }
        revealFileInputsViaScript();
        if (sendKeysToFileInputIfPresent(iconPath)) {
            return;
        }
        if (waitUntil(webDriver -> hasFileInput(webDriver), Duration.ofSeconds(3))
                && sendKeysToFileInputIfPresent(iconPath)) {
            return;
        }
        uploadSocialMediaIconViaNativeDialog(iconPath);
    }

    private boolean hasFileInput(WebDriver webDriver) {
        return !webDriver.findElements(By.cssSelector("input[type='file']")).isEmpty()
                || Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                "const walk = (node) => {" +
                        "  if (!node) return false;" +
                        "  if (node.nodeName === 'INPUT' && node.type === 'file') return true;" +
                        "  if (node.shadowRoot && walk(node.shadowRoot)) return true;" +
                        "  const children = node.children || [];" +
                        "  for (let i = 0; i < children.length; i++) {" +
                        "    if (walk(children[i])) return true;" +
                        "  }" +
                        "  return false;" +
                        "};" +
                        "return walk(document.body);"
        ));
    }

    private boolean sendKeysToFileInputIfPresent(String iconPath) {
        List<WebElement> fileInputs = driver.findElements(By.cssSelector("input[type='file']"));
        if (fileInputs.isEmpty()) {
            return false;
        }
        WebElement fileInput = fileInputs.get(0);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.display = 'block';" +
                        "arguments[0].style.visibility = 'visible';" +
                        "arguments[0].style.opacity = '1';" +
                        "arguments[0].removeAttribute('hidden');",
                fileInput
        );
        fileInput.sendKeys(iconPath);
        return true;
    }

    private void revealFileInputsViaScript() {
        ((JavascriptExecutor) driver).executeScript(
                "document.querySelectorAll('input[type=\"file\"]').forEach(input => {" +
                        "  input.style.display = 'block';" +
                        "  input.style.visibility = 'visible';" +
                        "  input.style.opacity = '1';" +
                        "  input.removeAttribute('hidden');" +
                        "});"
        );
    }

    private void uploadSocialMediaIconViaNativeDialog(String iconPath) {
        try {
            boolean isMac = System.getProperty("os.name", "").toLowerCase().contains("mac");
            StringSelection selection = new StringSelection(iconPath);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            Robot robot = new Robot();
            robot.setAutoDelay(150);
            Thread.sleep(1200);
            if (isMac) {
                robot.keyPress(KeyEvent.VK_META);
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_G);
                robot.keyRelease(KeyEvent.VK_G);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                robot.keyRelease(KeyEvent.VK_META);
            } else {
                robot.keyPress(KeyEvent.VK_ALT);
                robot.keyPress(KeyEvent.VK_N);
                robot.keyRelease(KeyEvent.VK_N);
                robot.keyRelease(KeyEvent.VK_ALT);
            }
            Thread.sleep(300);
            robot.keyPress(isMac ? KeyEvent.VK_META : KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(isMac ? KeyEvent.VK_META : KeyEvent.VK_CONTROL);
            Thread.sleep(400);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            Thread.sleep(800);
        } catch (Exception exception) {
            throw new NoSuchElementException(
                    "Failed to upload social media icon via native file dialog: " + exception.getMessage());
        }
    }

    private boolean hasNavigatedToWhatsImportantDetail(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (isCategoryOverviewPage(webDriver)) {
            return false;
        }
        String pageText = normalizeApostrophes(semantics.normalizeText(semantics.getSemanticsText(webDriver)));
        if (pageText.contains("Industry Name")) {
            return true;
        }
        return pageText.contains("important to you")
                && (isAddNewButtonVisible(webDriver) || isLookupDetailListVisible(webDriver));
    }

    private String normalizeApostrophes(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('\u2019', '\'').replace('\u2018', '\'').replace('`', '\'');
    }

    private String resolveSocialMediaIconPath() {
        java.nio.file.Path downloadsPath = java.nio.file.Paths.get(
                        System.getProperty("user.home"), "Downloads", SOCIAL_MEDIA_ICON_FILE_NAME)
                .toAbsolutePath()
                .normalize();
        if (java.nio.file.Files.exists(downloadsPath)) {
            return downloadsPath.toString();
        }
        java.nio.file.Path legacyPath = java.nio.file.Paths.get(SOCIAL_MEDIA_ICON_PATH)
                .toAbsolutePath()
                .normalize();
        if (java.nio.file.Files.exists(legacyPath)) {
            return legacyPath.toString();
        }
        java.nio.file.Path testDataPath = java.nio.file.Paths.get(
                        "src/test/resources/testdata", SOCIAL_MEDIA_ICON_FILE_NAME)
                .toAbsolutePath()
                .normalize();
        if (java.nio.file.Files.exists(testDataPath)) {
            return testDataPath.toString();
        }
        throw new NoSuchElementException(
                "Social media icon file not found. Expected at " + downloadsPath + ", "
                        + legacyPath + " or " + testDataPath);
    }

    private boolean hasNavigatedToInterestsDetail(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (isCategoryOverviewPage(webDriver)) {
            return false;
        }
        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        String interestsText = semantics.normalizeText(INTERESTS_AND_HOBBIES_TEXT);
        boolean onDetailBreadcrumb = pageText.contains("/ " + interestsText);
        boolean onDetailTable = pageText.contains("Status")
                && pageText.contains("Action")
                && pageText.contains("EditDeleteBlock")
                && pageText.contains(interestsText);
        return onDetailBreadcrumb
                && (onDetailTable || isAddNewButtonVisible(webDriver) || isLookupDetailListVisible(webDriver));
    }

    private boolean hasNavigatedToMoreAboutYouDetail(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (isCategoryOverviewPage(webDriver)) {
            return false;
        }
        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        String moreAboutYou = semantics.normalizeText(MORE_ABOUT_YOU_TEXT);
        boolean onDetailBreadcrumb = pageText.contains("/ " + moreAboutYou);
        boolean onDetailTable = pageText.contains(semantics.normalizeText(MORE_ABOUT_YOU_DETAIL_MARKER))
                && pageText.contains("Status")
                && pageText.contains("Action");
        return pageText.contains(moreAboutYou)
                && onDetailBreadcrumb
                && (isAddNewButtonVisible(webDriver) || isLookupDetailListVisible(webDriver) || onDetailTable);
    }

    private boolean isCategoryOverviewPage(WebDriver webDriver) {
        if (isUsersListPage(webDriver)) {
            return false;
        }
        String pageText = semantics.normalizeText(semantics.getSemanticsText(webDriver));
        return pageText.contains("User Name")
                && pageText.contains("Count")
                && !pageText.contains("Input Categories /");
    }

    private void scrollToCategoryListRow(String categoryText) {
        semantics.enableFlutterSemanticsOn(driver);
        ((JavascriptExecutor) driver).executeScript(
                CATEGORY_LIST_ROW_FIND_SCRIPT +
                        "const node = findCategoryListRowNode(arguments[0]);" +
                        "if (!node) return;" +
                        "node.scrollIntoView({block: 'center', inline: 'nearest'});",
                categoryText
        );
        semantics.pauseAfterScroll();
    }

    private boolean clickLookupCategoryRowByVisibleText(String categoryText, By visibleTextLocator) {
        semantics.enableFlutterSemantics();
        if (semantics.clickSemanticsLabelViaScript(categoryText)) {
            return true;
        }
        if (!driver.findElements(visibleTextLocator).isEmpty()) {
            clickSemanticsElement(visibleTextLocator);
            return true;
        }
        return false;
    }

    private boolean clickCategoryListRow(String categoryText) {
        semantics.enableFlutterSemanticsOn(driver);
        for (int attempt = 0; attempt < MAX_LOOKUP_ACTION_ATTEMPTS; attempt++) {
            if (attempt > 0) {
                scrollToCategoryListRow(categoryText);
                semantics.pauseAfterScroll();
            }
            if (Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                    CATEGORY_LIST_ROW_FIND_SCRIPT +
                            DISPATCH_POINTER_CLICK_SCRIPT +
                            "const node = findCategoryListRowNode(arguments[0]);" +
                            "if (!node) return false;" +
                            "const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                            "return dispatchPointerClick(target);",
                    categoryText
            ))) {
                return true;
            }
            if (clickVisibleSemanticsText(categoryText)) {
                return true;
            }
        }
        return false;
    }

    private void clickLookupCategory(String semanticsLabel, String visibleText, By visibleTextLocator) {
        semantics.enableFlutterSemantics();
        if (semanticsLabel != null && !semanticsLabel.isBlank()) {
            scrollToSemanticsLabel(semanticsLabel);
            if (semantics.clickSemanticsLabelViaScript(semanticsLabel)) {
                pauseAndWaitForLookupDetailList();
                return;
            }
            if (!driver.findElements(semanticsButton(semanticsLabel)).isEmpty()) {
                clickSemanticsElement(semanticsButton(semanticsLabel));
                pauseAndWaitForLookupDetailList();
                return;
            }
        }
        if (semantics.clickSemanticsLabelViaScript(visibleText)) {
            pauseAndWaitForLookupDetailList();
            return;
        }
        if (clickCategoryListRow(visibleText) || clickCategoryListRowByPartialText(visibleText)) {
            pauseAndWaitForLookupDetailList();
            return;
        }
        if (clickVisibleSemanticsText(visibleText)) {
            pauseAndWaitForLookupDetailList();
            return;
        }
        if (!driver.findElements(visibleTextLocator).isEmpty()) {
            clickSemanticsElement(visibleTextLocator);
            pauseAndWaitForLookupDetailList();
        }
    }

    private void pauseAndWaitForLookupDetailList() {
        semantics.pauseAfterScroll();
        scrollLookupDetailListIntoView();
        waitUntil(this::isLookupDetailListVisible, DEFAULT_WAIT);
    }

    public void clickAddNew() {
        semantics.enableFlutterSemantics();
        if (waitUntil(d -> isLookupFormVisible(d), DEFAULT_WAIT)) {
            return;
        }
        scrollToSemanticsLabel(SEMANTICS_ADD_NEW);
        if (semantics.clickSemanticsLabelViaScript(SEMANTICS_ADD_NEW)) {
            requireLookupFormOpen();
            return;
        }
        if (!driver.findElements(addNewButton).isEmpty()) {
            clickSemanticsElement(addNewButton);
            requireLookupFormOpen();
            return;
        }
        scrollToVisibleSemanticsText(ADD_NEW_VISIBLE_TEXT);
        if (!waitUntil(this::isAddNewButtonVisible, DEFAULT_WAIT)) {
            scrollLookupDetailListIntoView();
            waitUntil(this::isAddNewButtonVisible, LOOKUP_DETAIL_POLL);
        }
        scrollToAddNewButton();
        for (int attempt = 0; attempt < MAX_LOOKUP_ACTION_ATTEMPTS; attempt++) {
            if (attempt > 0) {
                scrollToAddNewButton();
            }
            if (clickAddNewViaVisibleFallbacks() && waitUntil(d -> isLookupFormVisible(d), DEFAULT_WAIT)) {
                return;
            }
            if (clickVisibleSemanticsText(ADD_NEW_VISIBLE_TEXT) && waitUntil(d -> isLookupFormVisible(d), DEFAULT_WAIT)) {
                return;
            }
            if (!driver.findElements(addNewByVisibleText).isEmpty()) {
                clickSemanticsElement(addNewByVisibleText);
                if (waitUntil(d -> isLookupFormVisible(d), DEFAULT_WAIT)) {
                    return;
                }
            }
            if (!driver.findElements(addNewHeaderAddText).isEmpty()) {
                List<WebElement> addNodes = driver.findElements(addNewHeaderAddText);
                for (WebElement addNode : addNodes) {
                    semantics.scrollIntoView(addNode);
                    if (semantics.clickElementReliably(addNode) && waitUntil(d -> isLookupFormVisible(d), DEFAULT_WAIT)) {
                        return;
                    }
                }
            }
            semantics.pauseAfterScroll();
        }
        if (hasNavigatedToInterestsDetail(driver) || hasNavigatedToProfessionalBackgroundDetail(driver)
                || hasNavigatedToSocialMediaLinksDetail(driver)) {
            scrollLookupDetailListIntoView();
            if (clickVisibleSemanticsText("Add") && waitUntil(d -> isLookupFormVisible(d), DEFAULT_WAIT)) {
                return;
            }
        }
        logLookupDetailSemantics();
        throw new NoSuchElementException(
                "Add New button is not clickable (expected " + SEMANTICS_ADD_NEW
                        + " or visible text '" + ADD_NEW_VISIBLE_TEXT + "')");
    }

    private void requireLookupFormOpen() {
        if (!waitUntil(d -> isLookupFormVisible(d), DEFAULT_WAIT)) {
            logLookupDetailSemantics();
            throw new NoSuchElementException(
                    "Add lookup form did not open (expected field semantics: " + SEMANTICS_ENGLISH_NAME + ")");
        }
    }

    public void enterEnglishName(String name) {
        enterSemanticsField(SEMANTICS_ENGLISH_NAME, name);
    }

    public void enterSpanishName(String name) {
        enterSemanticsField(SEMANTICS_SPANISH_NAME, name);
    }

    public void enterPortugueseName(String name) {
        enterSemanticsField(SEMANTICS_PORTUGUESE_NAME, name);
    }

    public void enterCanadianName(String name) {
        enterSemanticsField(SEMANTICS_CANADIAN_NAME, name);
    }

    public void clickAddButton() {
        semantics.enableFlutterSemantics();
        if (hasNavigatedToSocialMediaLinksDetail(driver)) {
            if (clickSemanticsLabelButton(SEMANTICS_SOCIAL_MEDIA_ADD_BUTTON)) {
                return;
            }
            if (clickSaveOrAddViaScript() || clickVisibleSemanticsText("Add")) {
                return;
            }
        }
        if (hasNavigatedToInterestsDetail(driver)) {
            if (clickSemanticsLabelButton(SEMANTICS_INTERESTS_ADD_BUTTON)) {
                return;
            }
        }
        if (hasNavigatedToProfessionalBackgroundDetail(driver)) {
            if (clickSemanticsLabelButton(SEMANTICS_SKILL_ADD_BUTTON)) {
                return;
            }
        }
        if (hasNavigatedToWhatsImportantDetail(driver)) {
            if (clickSemanticsLabelButton(SEMANTICS_SKILL_ADD_BUTTON)) {
                return;
            }
        }
        if (clickSemanticsLabelButton(SEMANTICS_SOCIAL_MEDIA_ADD_BUTTON)) {
            return;
        }
        if (clickSemanticsLabelButton(SEMANTICS_ADD_BUTTON)) {
            return;
        }
        if (clickSemanticsLabelButton(SEMANTICS_SKILL_ADD_BUTTON)) {
            return;
        }
        if (clickSemanticsLabelButton(SEMANTICS_INTERESTS_ADD_BUTTON)) {
            return;
        }
        if (clickSaveOrAddViaScript()) {
            return;
        }
        if (semantics.clickSemanticsLabelViaScript("Add")) {
            return;
        }
        throw new NoSuchElementException(
                "Add button is not clickable (expected "
                        + SEMANTICS_SOCIAL_MEDIA_ADD_BUTTON + ", "
                        + SEMANTICS_SKILL_ADD_BUTTON + ", "
                        + SEMANTICS_INTERESTS_ADD_BUTTON + " or " + SEMANTICS_ADD_BUTTON + ")");
    }

    private boolean clickSemanticsLabelButton(String semanticsLabel) {
        scrollToSemanticsLabel(semanticsLabel);
        semantics.enableFlutterSemantics();
        if (semantics.clickSemanticsLabelViaScript(semanticsLabel)) {
            return true;
        }
        if (!driver.findElements(semanticsButton(semanticsLabel)).isEmpty()) {
            clickSemanticsElement(semanticsButton(semanticsLabel));
            return true;
        }
        return false;
    }

    public void clickEditSaveButton() {
        semantics.enableFlutterSemantics();
        if (hasNavigatedToInterestsDetail(driver)) {
            if (clickSemanticsLabelButton(SEMANTICS_INTERESTS_EDIT_SAVE_BUTTON)) {
                return;
            }
        }
        if (hasNavigatedToProfessionalBackgroundDetail(driver)) {
            if (clickSemanticsLabelButton(SEMANTICS_EDIT_SAVE_BUTTON)) {
                return;
            }
        }
        if (hasNavigatedToWhatsImportantDetail(driver)) {
            if (clickSemanticsLabelButton(SEMANTICS_SKILL_EDIT_SAVE_BUTTON)) {
                return;
            }
        }
        if (hasNavigatedToMoreAboutYouDetail(driver)) {
            if (clickSemanticsLabelButton(SEMANTICS_MORE_ABOUT_YOU_EDIT_SAVE_BUTTON)) {
                return;
            }
        }
        if (clickSemanticsLabelButton(SEMANTICS_EDIT_SAVE_BUTTON)) {
            return;
        }
        if (clickSemanticsLabelButton(SEMANTICS_SKILL_EDIT_SAVE_BUTTON)) {
            return;
        }
        if (clickSemanticsLabelButton(SEMANTICS_INTERESTS_EDIT_SAVE_BUTTON)) {
            return;
        }
        if (clickSemanticsLabelButton(SEMANTICS_MORE_ABOUT_YOU_EDIT_SAVE_BUTTON)) {
            return;
        }
        if (clickVisibleSemanticsText("Add") || clickVisibleSemanticsText("Save")) {
            return;
        }
        throw new NoSuchElementException(
                "Edit save button is not clickable (expected "
                        + SEMANTICS_SKILL_EDIT_SAVE_BUTTON + ", "
                        + SEMANTICS_INTERESTS_EDIT_SAVE_BUTTON + ", "
                        + SEMANTICS_MORE_ABOUT_YOU_EDIT_SAVE_BUTTON + " or "
                        + SEMANTICS_EDIT_SAVE_BUTTON + ")");
    }

    public void clickEditLookupDetail() {
        scrollLookupDetailActionsIntoView(SEMANTICS_EDIT);
        clickLookupDetailRowAction(SEMANTICS_EDIT, "Edit");
        requireLookupFormOpen();
    }

    public void editEnglishName(String name) {
        enterSemanticsField(SEMANTICS_ENGLISH_NAME, name);
    }

    public void clickDeleteLookupDetail() {
        prepareLookupDetailListForRowActions();
        scrollLookupDetailActionsIntoView(SEMANTICS_DELETE);
        clickLookupDetailRowAction(SEMANTICS_DELETE, "Delete");
        semantics.pauseAfterScroll();
    }

    public void clickBlockLookupDetail() {
        prepareLookupDetailListForRowActions();
        scrollLookupDetailActionsIntoView(SEMANTICS_BLOCK);
        clickLookupDetailRowAction(SEMANTICS_BLOCK, "Block");
        semantics.pauseAfterScroll();
    }

    private void prepareLookupDetailListForRowActions() {
        semantics.enableFlutterSemantics();
        if (hasNavigatedToSocialMediaLinksDetail(driver)
                || hasNavigatedToInterestsDetail(driver)
                || hasNavigatedToProfessionalBackgroundDetail(driver)
                || hasNavigatedToMoreAboutYouDetail(driver)
                || isLookupDetailListVisible(driver)) {
            scrollLookupDetailListIntoView();
        }
    }

    public void clickConfirmDeleteButton() {
        if (!waitUntil(this::isConfirmDeleteDialogOpen, LOOKUP_DETAIL_POLL)) {
            logLookupDetailSemantics();
            throw new NoSuchElementException(
                    "Delete confirmation dialog is not open (expected " + SEMANTICS_CONFIRM_DELETE + ")");
        }
        clickConfirmDialogAction(SEMANTICS_CONFIRM_DELETE, confirmDeleteButton, "Delete");
    }

    public void clickConfirmBlockButton() {
        if (!waitUntil(this::isConfirmBlockDialogOpen, LOOKUP_DETAIL_POLL)) {
            logLookupDetailSemantics();
            throw new NoSuchElementException(
                    "Block confirmation dialog is not open (expected " + SEMANTICS_CONFIRM_BLOCK + ")");
        }
        clickConfirmDialogAction(SEMANTICS_CONFIRM_BLOCK, confirmBlockButton, "Block");
    }

    private void clickConfirmDialogAction(String semanticsLabel, By locator, String dialogButtonText) {
        scrollToSemanticsLabel(semanticsLabel);
        semantics.enableFlutterSemantics();
        if (semantics.clickSemanticsLabelViaScript(semanticsLabel)) {
            return;
        }
        if (clickConfirmDialogButtonViaScript(semanticsLabel, dialogButtonText)) {
            return;
        }
        if (!driver.findElements(locator).isEmpty()) {
            clickSemanticsElement(locator);
            return;
        }
        if (clickVisibleSemanticsText("Confirm") || clickVisibleSemanticsText(dialogButtonText)) {
            return;
        }
        throw new NoSuchElementException("Confirm action is not clickable: " + semanticsLabel);
    }

    private boolean clickConfirmDialogButtonViaScript(String semanticsLabel, String dialogButtonText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                DISPATCH_POINTER_CLICK_SCRIPT +
                        "const semanticsLabel = arguments[0];" +
                        "const buttonText = arguments[1];" +
                        "const exact = document.querySelector('flt-semantics[aria-label=\"' + semanticsLabel + '\"]');" +
                        "if (exact) return dispatchPointerClick(exact.closest('flt-semantics[flt-tappable]') || exact);" +
                        "const dialog = Array.from(document.querySelectorAll('flt-semantics'))" +
                        "  .find(node => (node.textContent || '').includes('Are you sure'));" +
                        "if (!dialog) return false;" +
                        "let ancestor = dialog;" +
                        "for (let depth = 0; depth < 12 && ancestor; depth++) {" +
                        "  const action = Array.from(ancestor.querySelectorAll('flt-semantics'))" +
                        "    .find(node => (node.textContent || '').trim() === buttonText);" +
                        "  if (action) return dispatchPointerClick(action.closest('flt-semantics[flt-tappable]') || action);" +
                        "  ancestor = ancestor.parentElement;" +
                        "}" +
                        "return false;",
                semanticsLabel,
                dialogButtonText
        ));
    }

    private void clickSemanticsAction(String semanticsLabel, By locator, String visibleTextFallback) {
        scrollToSemanticsLabel(semanticsLabel);
        semantics.enableFlutterSemantics();
        if (semantics.clickSemanticsLabelViaScript(semanticsLabel)) {
            return;
        }
        if (clickLookupDetailActionViaScript(semanticsLabel, visibleTextFallback)) {
            return;
        }
        if (!driver.findElements(locator).isEmpty()) {
            clickSemanticsElement(locator);
            return;
        }
        if (visibleTextFallback != null && clickVisibleSemanticsText(visibleTextFallback)) {
            return;
        }
        throw new NoSuchElementException("Action is not clickable: " + semanticsLabel);
    }

    private void clickLookupDetailRowAction(String semanticsLabel, String visibleText) {
        scrollLookupDetailActionsIntoView(semanticsLabel);
        semantics.enableFlutterSemantics();
        if (semantics.clickSemanticsLabelViaScript(semanticsLabel)) {
            return;
        }
        for (int attempt = 0; attempt < MAX_LOOKUP_ACTION_ATTEMPTS; attempt++) {
            if (attempt > 0) {
                scrollLookupDetailActionsIntoView(semanticsLabel);
                semantics.pauseAfterScroll();
            }
            if (clickLookupDetailActionViaScript(semanticsLabel, visibleText)) {
                return;
            }
            if (!driver.findElements(semanticsButton(semanticsLabel)).isEmpty()) {
                clickSemanticsElement(semanticsButton(semanticsLabel));
                return;
            }
            if (clickFirstRowActionByVisibleText(visibleText)) {
                return;
            }
        }
        logLookupDetailSemantics();
        throw new NoSuchElementException(
                "Lookup detail action is not clickable: " + semanticsLabel + " / " + visibleText);
    }

    private boolean clickFirstRowActionByVisibleText(String visibleText) {
        semantics.enableFlutterSemanticsOn(driver);
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                VISIBLE_SEMANTICS_TEXT_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "const text = arguments[0];" +
                        "const rowAnchor = document.querySelector('flt-semantics[aria-label*=\"lookup_detail_\"]')" +
                        "  || Array.from(document.querySelectorAll('flt-semantics'))" +
                        "    .find(node => normalizeText(node.textContent).startsWith('Administrative'))" +
                        "  || Array.from(document.querySelectorAll('flt-semantics'))" +
                        "    .find(node => normalizeText(node.textContent).includes('Linked In'))" +
                        "  || Array.from(document.querySelectorAll('flt-semantics'))" +
                        "    .find(node => {" +
                        "      const rowText = normalizeText(node.textContent);" +
                        "      return rowText.includes('Active') && rowText.includes('Edit') && rowText.includes('Delete');" +
                        "    });" +
                        "if (rowAnchor) {" +
                        "  let ancestor = rowAnchor.parentElement;" +
                        "  for (let depth = 0; depth < 14 && ancestor; depth++) {" +
                        "    const action = Array.from(ancestor.querySelectorAll('flt-semantics'))" +
                        "      .find(node => normalizeText(node.textContent) === text);" +
                        "    if (action) return dispatchPointerClick(action.closest('flt-semantics[flt-tappable]') || action);" +
                        "    ancestor = ancestor.parentElement;" +
                        "  }" +
                        "}" +
                        "const exact = findVisibleSemanticsTextNode(text);" +
                        "if (exact) return dispatchPointerClick(exact.closest('flt-semantics[flt-tappable]') || exact);" +
                        "return false;",
                visibleText
        ));
    }

    private boolean isLookupDetailListVisible(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return !webDriver.findElements(By.xpath(
                "//flt-semantics[contains(@aria-label,'lookup_detail_')]" +
                        " | //flt-semantics[contains(@aria-label,'add_new_lookup')]")).isEmpty();
    }

    private void scrollLookupDetailListIntoView() {
        scrollToSemanticsLabel(SEMANTICS_ADD_NEW);
        scrollToVisibleSemanticsText(ADD_NEW_VISIBLE_TEXT);
        semantics.enableFlutterSemanticsOn(driver);
        ((JavascriptExecutor) driver).executeScript(
                "const normalizeText = (value) => (value || '').replace(/\\s+/g, ' ').trim();" +
                        "window.scrollTo(0, 0);" +
                        "const anchor = document.querySelector('flt-semantics[aria-label=\"" + SEMANTICS_ADD_NEW + "\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label*=\"lookup_detail_\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label*=\"add_new_lookup\"]')" +
                        "  || Array.from(document.querySelectorAll('flt-semantics')).find(node =>" +
                        "      normalizeText(node.textContent) === 'Add New'" +
                        "      || normalizeText(node.textContent).includes('Add New Input Categories'))" +
                        "  || Array.from(document.querySelectorAll('flt-semantics')).find(node =>" +
                        "      (node.textContent || '').includes(\"" + WHATS_IMPORTANT_TEXT + "\"));" +
                        "if (!anchor) return;" +
                        "anchor.scrollIntoView({block: 'start', inline: 'nearest'});"
        );
    }

    private void scrollToVisibleSemanticsText(String visibleText) {
        semantics.enableFlutterSemanticsOn(driver);
        ((JavascriptExecutor) driver).executeScript(
                VISIBLE_SEMANTICS_TEXT_SCRIPT +
                        "const node = findVisibleSemanticsTextNode(arguments[0]);" +
                        "if (!node) return;" +
                        "node.scrollIntoView({block: 'center', inline: 'nearest'});",
                visibleText
        );
        semantics.pauseAfterScroll();
    }

    private boolean isAddNewButtonVisible(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return !webDriver.findElements(addNewButton).isEmpty()
                || !webDriver.findElements(addNewByVisibleText).isEmpty()
                || Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                VISIBLE_SEMANTICS_TEXT_SCRIPT +
                        ADD_NEW_BUTTON_FIND_SCRIPT +
                        "return !!findAddNewButtonNode();"
        ));
    }

    private void scrollToAddNewButton() {
        scrollToVisibleSemanticsText(ADD_NEW_VISIBLE_TEXT);
        semantics.enableFlutterSemanticsOn(driver);
        ((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0, 0);" +
                        VISIBLE_SEMANTICS_TEXT_SCRIPT +
                        ADD_NEW_BUTTON_FIND_SCRIPT +
                        "let node = findAddNewButtonNode();" +
                        "if (!node) {" +
                        "  const fallback = Array.from(document.querySelectorAll('flt-semantics'))" +
                        "    .filter(candidate => normalizeText(candidate.textContent).includes('Add New'))" +
                        "    .filter(candidate => !isTableHeaderNode(candidate))" +
                        "    .sort((a, b) => normalizeText(a.textContent).length - normalizeText(b.textContent).length);" +
                        "  node = fallback[0] || null;" +
                        "}" +
                        "if (!node) return;" +
                        "node.scrollIntoView({block: 'start', inline: 'nearest'});"
        );
        semantics.pauseAfterScroll();
    }

    private boolean clickAddNewViaVisibleFallbacks() {
        semantics.enableFlutterSemanticsOn(driver);
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0, 0);" +
                        VISIBLE_SEMANTICS_TEXT_SCRIPT +
                        ADD_NEW_BUTTON_FIND_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "let node = findAddNewButtonNode();" +
                        "if (!node) {" +
                        "  const fallback = Array.from(document.querySelectorAll('flt-semantics'))" +
                        "    .filter(candidate => normalizeText(candidate.textContent).includes('Add New'))" +
                        "    .filter(candidate => !isTableHeaderNode(candidate))" +
                        "    .sort((a, b) => normalizeText(a.textContent).length - normalizeText(b.textContent).length);" +
                        "  node = fallback[0] || null;" +
                        "}" +
                        "if (!node) return false;" +
                        "return dispatchPointerClick(node.closest('flt-semantics[flt-tappable]') || node);"
        ));
    }

    private boolean clickVisibleSemanticsText(String visibleText) {
        semantics.enableFlutterSemanticsOn(driver);
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                VISIBLE_SEMANTICS_TEXT_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "const node = findVisibleSemanticsTextNode(arguments[0]);" +
                        "if (!node) return false;" +
                        "return dispatchPointerClick(node.closest('flt-semantics[flt-tappable]') || node);",
                visibleText
        ));
    }

    private static final String LOOKUP_CATEGORY_ROW_SCRIPT =
            "const normalizeText = (value) => (value || '').replace(/\\s+/g, ' ').trim();" +
                    "const normalizeApostrophe = (value) => normalizeText(value).replace(/[\u2018\u2019`´]/g, \"'\");" +
                    "const findLookupCategorySemanticsRow = (semanticsLabel, expectedText, combinedMaxLen, parentMaxLen) => {" +
                    "  const normalizedExpected = normalizeApostrophe(expectedText);" +
                    "  const semanticsNode = document.querySelector('flt-semantics[aria-label=\"' + semanticsLabel + '\"]')" +
                    "    || document.querySelector('flt-semantics[aria-label*=\"' + semanticsLabel + '\"]');" +
                    "  if (semanticsNode) return semanticsNode;" +
                    "  const combinedRows = Array.from(document.querySelectorAll('flt-semantics'))" +
                    "    .filter(node => {" +
                    "      const rowText = normalizeApostrophe(node.textContent);" +
                    "      return rowText.includes(normalizedExpected) && /\\d/.test(rowText) && rowText.length <= combinedMaxLen;" +
                    "    })" +
                    "    .sort((left, right) => normalizeText(left.textContent).length - normalizeText(right.textContent).length);" +
                    "  if (combinedRows.length) return combinedRows[0];" +
                    "  const nameCell = Array.from(document.querySelectorAll('flt-semantics'))" +
                    "    .find(node => {" +
                    "      const text = normalizeApostrophe(node.textContent);" +
                    "      return text === normalizedExpected || text.endsWith(normalizedExpected);" +
                    "    });" +
                    "  if (!nameCell) return null;" +
                    "  let row = nameCell.parentElement;" +
                    "  for (let depth = 0; depth < 10 && row; depth++) {" +
                    "    const rowText = normalizeText(row.textContent);" +
                    "    if (rowText.includes(normalizedExpected) && /\\d/.test(rowText) && rowText.length <= parentMaxLen) {" +
                    "      return row;" +
                    "    }" +
                    "    row = row.parentElement;" +
                    "  }" +
                    "  return nameCell;" +
                    "};" +
                    "const isLookupCategorySemanticsTextVisible = (semanticsLabel, expectedText, combinedMaxLen, parentMaxLen) => {" +
                    "  const normalizedExpected = normalizeApostrophe(expectedText);" +
                    "  const node = findLookupCategorySemanticsRow(semanticsLabel, expectedText, combinedMaxLen, parentMaxLen);" +
                    "  if (!node) return false;" +
                    "  const nodeText = normalizeApostrophe(node.textContent);" +
                    "  const rect = node.getBoundingClientRect();" +
                    "  return nodeText.includes(normalizedExpected) && rect.width > 0 && rect.height > 0;" +
                    "};";

    private static final String CATEGORY_LIST_ROW_FIND_SCRIPT =
            "const normalizeText = (value) => (value || '').replace(/\\s+/g, ' ').trim();" +
                    "const normalizeApostrophe = (value) => normalizeText(value).replace(/[\u2018\u2019`´]/g, \"'\");" +
                    "const findCategoryListRowNode = (text) => {" +
                    "  const expected = normalizeApostrophe(text);" +
                    "  const excluded = new Set(['Add New', 'Add', 'Edit', 'Delete', 'Block', 'Save', 'Confirm']);" +
                    "  const isInLookupDetailRow = (node) => {" +
                    "    let current = node;" +
                    "    for (let depth = 0; depth < 12 && current; depth++) {" +
                    "      const label = current.getAttribute('aria-label') || '';" +
                    "      if (label.includes('lookup_detail_')) return true;" +
                    "      current = current.parentElement;" +
                    "    }" +
                    "    return false;" +
                    "  };" +
                    "  const isDetailPanelHeader = (node) => {" +
                    "    const addNew = Array.from(document.querySelectorAll('flt-semantics'))" +
                    "      .find(candidate => normalizeText(candidate.textContent) === 'Add New');" +
                    "    if (!addNew) return false;" +
                    "    const nodeRect = node.getBoundingClientRect();" +
                    "    const addRect = addNew.getBoundingClientRect();" +
                    "    return Math.abs(nodeRect.top - addRect.top) < 60 && nodeRect.left >= addRect.left - 20;" +
                    "  };" +
                    "  const scoreCategoryRow = (node) => {" +
                    "    let points = 0;" +
                    "    const nodeText = normalizeText(node.textContent);" +
                    "    if (node.hasAttribute('flt-tappable')) points += 8;" +
                    "    if (node.getAttribute('role') === 'button') points += 4;" +
                    "    if (nodeText.includes(expected)) points += 6;" +
                    "    if (nodeText === expected) points += 2;" +
                    "    points -= node.getBoundingClientRect().left / 80;" +
                    "    if (!isDetailPanelHeader(node)) points += 5;" +
                    "    return points;" +
                    "  };" +
                    "  const climbToCategoryRow = (node) => {" +
                    "    let ancestor = node;" +
                    "    for (let depth = 0; depth < 10 && ancestor; depth++) {" +
                    "      const ancestorText = normalizeText(ancestor.textContent);" +
                    "      if ((ancestor.hasAttribute('flt-tappable') || ancestor.getAttribute('role') === 'button')" +
                    "          && ancestorText.includes(expected)" +
                    "          && !excluded.has(ancestorText)" +
                    "          && !isInLookupDetailRow(ancestor)" +
                    "          && !isDetailPanelHeader(ancestor)) {" +
                    "        return ancestor;" +
                    "      }" +
                    "      ancestor = ancestor.parentElement;" +
                    "    }" +
                    "    return null;" +
                    "  };" +
                    "  const textNodes = Array.from(document.querySelectorAll('flt-semantics'))" +
                    "    .filter(node => normalizeText(node.textContent) === expected);" +
                    "  for (const textNode of textNodes) {" +
                    "    const row = climbToCategoryRow(textNode);" +
                    "    if (row) return row;" +
                    "  }" +
                    "  const candidates = Array.from(document.querySelectorAll('flt-semantics[flt-tappable], flt-semantics[role=\"button\"]'))" +
                    "    .filter(node => {" +
                    "      const nodeText = normalizeText(node.textContent);" +
                    "      if (!nodeText.includes(expected)) return false;" +
                    "      if (excluded.has(nodeText)) return false;" +
                    "      if (isInLookupDetailRow(node)) return false;" +
                    "      if (isDetailPanelHeader(node)) return false;" +
                    "      const rect = node.getBoundingClientRect();" +
                    "      return rect.width > 0 && rect.height > 0;" +
                    "    });" +
                    "  candidates.sort((left, right) => scoreCategoryRow(right) - scoreCategoryRow(left));" +
                    "  return candidates[0] || null;" +
                    "};";

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
                    "  return Array.from(document.querySelectorAll(selector)).find(node => !menu || !menu.contains(node)) || null;" +
                    "}";

    private static final String DISPATCH_POINTER_CLICK_SCRIPT =
            "const dispatchPointerClick = (target) => {" +
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
                    "};";

    private static final String ADD_NEW_BUTTON_FIND_SCRIPT =
            "const isTableHeaderNode = (node) => {" +
                    "  const text = normalizeText(node.textContent);" +
                    "  return text.includes('Industry Name')" +
                    "    || text.includes('Status')" +
                    "    || text.includes('Action')" +
                    "    || text.includes('ActiveEditDeleteBlock');" +
                    "};" +
                    "const scoreAddNewNode = (node) => {" +
                    "  let points = 0;" +
                    "  if (node.hasAttribute('flt-tappable')) points += 3;" +
                    "  if (node.getAttribute('role') === 'button') points += 2;" +
                    "  points -= Math.max(0, normalizeText(node.textContent).length - 'Add New'.length);" +
                    "  points -= Math.max(0, node.getBoundingClientRect().top - 250) / 100;" +
                    "  return points;" +
                    "};" +
                    "const findAddNewButtonNode = () => {" +
                    "  const exactMatches = Array.from(document.querySelectorAll('flt-semantics'))" +
                    "    .filter(node => {" +
                    "      const nodeText = normalizeText(node.textContent);" +
                    "      const label = normalizeText(node.getAttribute('aria-label'));" +
                    "      return (nodeText === 'Add New' || label === 'Add New') && !isTableHeaderNode(node);" +
                    "    });" +
                    "  if (exactMatches.length) {" +
                    "    exactMatches.sort((a, b) => scoreAddNewNode(b) - scoreAddNewNode(a));" +
                    "    return exactMatches[0];" +
                    "  }" +
                    "  const splitLabelNodes = Array.from(document.querySelectorAll('flt-semantics'))" +
                    "    .filter(node => {" +
                    "      if (isTableHeaderNode(node)) return false;" +
                    "      const children = Array.from(node.querySelectorAll('flt-semantics, span'))" +
                    "        .map(child => normalizeText(child.textContent));" +
                    "      return children.includes('Add') && children.includes('New');" +
                    "    });" +
                    "  if (splitLabelNodes.length) return splitLabelNodes[0];" +
                    "  const partialMatches = Array.from(document.querySelectorAll('flt-semantics'))" +
                    "    .filter(node => {" +
                    "      const nodeText = normalizeText(node.textContent);" +
                    "      return nodeText.includes('Add New') && !isTableHeaderNode(node) && nodeText.length <= 80;" +
                    "    })" +
                    "    .sort((a, b) => normalizeText(a.textContent).length - normalizeText(b.textContent).length);" +
                    "  if (partialMatches.length) return partialMatches[0];" +
                    "  const addNewSpan = Array.from(document.querySelectorAll('flt-semantics span'))" +
                    "    .find(span => normalizeText(span.textContent) === 'Add New');" +
                    "  if (addNewSpan) {" +
                    "    const parent = addNewSpan.closest('flt-semantics[flt-tappable]')" +
                    "      || addNewSpan.closest('flt-semantics');" +
                    "    if (parent && !isTableHeaderNode(parent)) return parent;" +
                    "  }" +
                    "  const firstDataRow = Array.from(document.querySelectorAll('flt-semantics'))" +
                    "    .find(node => normalizeText(node.textContent).startsWith('Administrative'));" +
                    "  const tableTop = firstDataRow ? firstDataRow.getBoundingClientRect().top : 99999;" +
                    "  const headerAddButton = Array.from(document.querySelectorAll('flt-semantics'))" +
                    "    .filter(node => normalizeText(node.textContent) === 'Add')" +
                    "    .filter(node => node.getBoundingClientRect().top < tableTop - 10)" +
                    "    .sort((a, b) => a.getBoundingClientRect().top - b.getBoundingClientRect().top);" +
                    "  if (headerAddButton.length) return headerAddButton[0];" +
                    "  return null;" +
                    "};";

    private static final String VISIBLE_SEMANTICS_TEXT_SCRIPT =
            "const normalizeText = (value) => (value || '').replace(/\\s+/g, ' ').trim();" +
                    "const findVisibleSemanticsTextNode = (text) => {" +
                    "  const candidates = Array.from(document.querySelectorAll('flt-semantics'))" +
                    "    .filter(node => {" +
                    "      const nodeText = normalizeText(node.textContent);" +
                    "      const label = normalizeText(node.getAttribute('aria-label'));" +
                    "      return nodeText === text || label === text;" +
                    "    });" +
                    "  const score = (node) => {" +
                    "    let points = 0;" +
                    "    const nodeText = normalizeText(node.textContent);" +
                    "    if (node.hasAttribute('flt-tappable')) points += 3;" +
                    "    if (node.getAttribute('role') === 'button') points += 2;" +
                    "    if (nodeText === text) points += 5;" +
                    "    points -= Math.max(0, nodeText.length - text.length);" +
                    "    return points;" +
                    "  };" +
                    "  candidates.sort((a, b) => score(b) - score(a));" +
                    "  return candidates[0] || null;" +
                    "};";

    private void scrollToSemanticsLabel(String semanticsLabel) {
        semantics.enableFlutterSemanticsOn(driver);
        ((JavascriptExecutor) driver).executeScript(
                "const anchor = document.querySelector('flt-semantics[aria-label=\"' + arguments[0] + '\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label*=\"' + arguments[0] + '\"]');" +
                        "if (!anchor) return;" +
                        "anchor.scrollIntoView({block: 'center', inline: 'nearest'});",
                semanticsLabel
        );
        semantics.pauseAfterScroll();
    }

    private void scrollToFormFieldByIndex(int index) {
        semantics.enableFlutterSemanticsOn(driver);
        ((JavascriptExecutor) driver).executeScript(
                "const inputs = Array.from(document.querySelectorAll('input[data-semantics-role=\"text-field\"], flt-semantics input'));" +
                        "if (inputs[arguments[0]]) {" +
                        "  inputs[arguments[0]].scrollIntoView({block: 'center', inline: 'nearest'});" +
                        "}",
                index
        );
        semantics.pauseAfterScroll();
    }

    private boolean isLookupDetailActionAvailable(WebDriver webDriver, String semanticsLabel, String visibleText) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                LOOKUP_DETAIL_FIND_NODE_SCRIPT +
                        "const node = findLookupDetailNode(arguments[0], arguments[1]);" +
                        "if (!node) return false;" +
                        "const target = lookupDetailClickTarget(node);" +
                        "target.scrollIntoView({block: 'center', inline: 'end'});" +
                        "return isLookupDetailTargetVisible(target);",
                semanticsLabel,
                visibleText
        ));
    }

    private boolean isConfirmDeleteDialogOpen(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return !webDriver.findElements(confirmDeleteButton).isEmpty()
                || Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                "return !!document.querySelector('flt-semantics[aria-label=\"" + SEMANTICS_CONFIRM_DELETE + "\"]')" +
                        " || !!Array.from(document.querySelectorAll('flt-semantics'))" +
                        "  .find(node => (node.textContent || '').includes('Are you sure you want to delete'));"
        ));
    }

    private boolean isConfirmBlockDialogOpen(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return !webDriver.findElements(confirmBlockButton).isEmpty()
                || Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                "return !!document.querySelector('flt-semantics[aria-label=\"" + SEMANTICS_CONFIRM_BLOCK + "\"]')" +
                        " || !!Array.from(document.querySelectorAll('flt-semantics'))" +
                        "  .find(node => (node.textContent || '').includes('Are you sure you want to block'));"
        ));
    }

    private void scrollLookupDetailActionsIntoView(String semanticsLabel) {
        semantics.enableFlutterSemanticsOn(driver);
        ((JavascriptExecutor) driver).executeScript(
                LOOKUP_DETAIL_FIND_NODE_SCRIPT +
                        "const anchor = findLookupDetailNode(arguments[0], null)" +
                        "  || document.querySelector('flt-semantics[aria-label*=\"lookup_detail_\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label=\"name_field\"]');" +
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
                semanticsLabel
        );
    }

    private boolean clickLookupDetailActionViaScript(String semanticsLabel, String visibleText) {
        semantics.enableFlutterSemanticsOn(driver);
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                LOOKUP_DETAIL_FIND_NODE_SCRIPT +
                        "const dispatchPointer = (target) => {" +
                        "  if (!target) return false;" +
                        "  target.scrollIntoView({block: 'center', inline: 'end'});" +
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
                        "const node = findLookupDetailNode(arguments[0], arguments[1]);" +
                        "if (!node) return false;" +
                        "return dispatchPointer(lookupDetailClickTarget(node));",
                semanticsLabel,
                visibleText
        ));
    }

    private void logLookupDetailSemantics() {
        Object labels = ((JavascriptExecutor) driver).executeScript(
                "const related = Array.from(document.querySelectorAll('flt-semantics'))" +
                        "  .map(node => (node.getAttribute('aria-label') || '').trim())" +
                        "  .filter(label => label && (" +
                        "    label.includes('lookup_detail') ||" +
                        "    label.includes('confirm_') ||" +
                        "    label.includes('add_new') ||" +
                        "    label.includes('name_field') ||" +
                        "    label.includes('professional_background')" +
                        "  ));" +
                        "if (related.length) return related.slice(0, 40);" +
                        "return Array.from(document.querySelectorAll('flt-semantics'))" +
                        "  .map(node => (node.getAttribute('aria-label') || (node.textContent || '').trim())" +
                        "    .trim())" +
                        "  .filter(label => label)" +
                        "  .slice(0, 40);"
        );
        System.out.println("Lookup detail related semantics on failure: " + labels);
    }

    private static final String LOOKUP_DETAIL_FIND_NODE_SCRIPT =
            "const isLookupDetailTargetVisible = (target) => {" +
                    "  if (!target) return false;" +
                    "  const rect = target.getBoundingClientRect();" +
                    "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                    "  return !(rect.right < 0 || rect.left > window.innerWidth ||" +
                    "    rect.bottom < 0 || rect.top > window.innerHeight);" +
                    "};" +
                    "const lookupDetailClickTarget = (node) => {" +
                    "  if (!node) return null;" +
                    "  let current = node;" +
                    "  while (current) {" +
                    "    if (current.hasAttribute && current.hasAttribute('flt-tappable')) return current;" +
                    "    current = current.parentElement;" +
                    "  }" +
                    "  return node.closest('flt-semantics[flt-tappable]') || node;" +
                    "};" +
                    "const isLookupDetailLabel = (label) => label.includes('lookup_detail') || label.includes('add_new');" +
                    "const findLookupDetailNode = (semanticsLabel, visibleText) => {" +
                    "  const exact = document.querySelector('flt-semantics[aria-label=\"' + semanticsLabel + '\"]');" +
                    "  if (exact) return exact;" +
                    "  const prefix = semanticsLabel.replace(/_0$/, '');" +
                    "  const byPrefix = document.querySelector('flt-semantics[aria-label^=\"' + prefix + '\"]');" +
                    "  if (byPrefix) return byPrefix;" +
                    "  const byContains = Array.from(document.querySelectorAll('flt-semantics'))" +
                    "    .find(node => {" +
                    "      const label = node.getAttribute('aria-label') || '';" +
                    "      return label.includes(prefix) && isLookupDetailLabel(label);" +
                    "    });" +
                    "  if (byContains) return byContains;" +
                    "  if (!visibleText) return null;" +
                    "  const rowAnchor = document.querySelector('flt-semantics[aria-label*=\"lookup_detail_\"]');" +
                    "  if (!rowAnchor) return null;" +
                    "  let ancestor = rowAnchor.parentElement;" +
                    "  for (let depth = 0; depth < 14 && ancestor; depth++) {" +
                    "    const match = Array.from(ancestor.querySelectorAll('flt-semantics')).find(node => {" +
                    "      const text = (node.textContent || '').trim();" +
                    "      const label = node.getAttribute('aria-label') || '';" +
                    "      return text === visibleText && isLookupDetailLabel(label);" +
                    "    });" +
                    "    if (match) return match;" +
                    "    ancestor = ancestor.parentElement;" +
                    "  }" +
                    "  return null;" +
                    "};";

    private boolean isInputCategoriesPageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (isUsersListPage(webDriver)) {
            return false;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const title = findOutsideNav('flt-semantics[aria-label=\"" + SEMANTICS_TITLE + "\"]')" +
                        "  || findOutsideNav('flt-semantics[aria-label*=\"" + SEMANTICS_TITLE + "\"]');" +
                        "if (title) return true;" +
                        "const contentText = Array.from(document.querySelectorAll('flt-semantics'))" +
                        "  .filter(node => !isInNav(node))" +
                        "  .map(node => (node.textContent || '').replace(/\\s+/g, ' ').trim())" +
                        "  .join(' ');" +
                        "if (!contentText) return false;" +
                        "if (contentText.includes('Free access') && contentText.includes('Admin Subscription')) {" +
                        "  return false;" +
                        "}" +
                        "return contentText.includes('Input Categories /')" +
                        "  || contentText.includes('Industry Name')" +
                        "  || contentText.includes('important to you')" +
                        "  || contentText.includes('More about you')" +
                        "  || contentText.includes('Interests and hobbies')" +
                        "  || contentText.includes('Professional background')" +
                        "  || contentText.includes('Social media links')" +
                        "  || (contentText.includes('User Name') && contentText.includes('Count'));"
        ));
    }

    private boolean isUsersListPage(WebDriver webDriver) {
        return isUsersListPageText(semantics.normalizeText(semantics.getSemanticsText(webDriver)));
    }

    private boolean isUsersListPageText(String pageText) {
        return pageText.contains("Individual Users")
                && pageText.contains("Free access")
                && (pageText.contains("Admin Subscription") || pageText.contains("BlockedDelete"));
    }

    private boolean isLookupFormVisible(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return !webDriver.findElements(semanticsFieldInput(SEMANTICS_ENGLISH_NAME)).isEmpty()
                || !webDriver.findElements(semanticsFieldInput(SEMANTICS_SPANISH_NAME)).isEmpty()
                || !webDriver.findElements(semanticsFieldInput(SEMANTICS_PORTUGUESE_NAME)).isEmpty()
                || !webDriver.findElements(semanticsFieldInput(SEMANTICS_CANADIAN_NAME)).isEmpty()
                || !webDriver.findElements(semanticsField(SEMANTICS_ENGLISH_NAME)).isEmpty()
                || Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                "const labels = ['name_field','spname_field','ptname_field','frname_field'];" +
                        "if (labels.some(label =>" +
                        "  !!document.querySelector('flt-semantics[aria-label=\"' + label + '\"]')" +
                        "  || !!document.querySelector('input[aria-label=\"' + label + '\"]'))) return true;" +
                        "const inputs = document.querySelectorAll('input[data-semantics-role=\"text-field\"], flt-semantics input');" +
                        "if (inputs.length === 0) return false;" +
                        "const isFormLabel = (text) => text === 'Industry Name' || text === 'Role Name' || text === 'Interest Name' || text === 'Skill Name'" +
                        "  || text === 'Social Media Name' || text.includes('Social Media')" +
                        "  || text.includes('English Name') || text.includes('Spanish Name')" +
                        "  || text.includes('Portuguese Name') || text.includes('Canadian Name')" +
                        "  || text === 'English' || text === 'Spanish' || text === 'Portuguese' || text === 'Canadian';" +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node =>" +
                        "  isFormLabel((node.textContent || '').trim()));"
        ));
    }

    private void enterSemanticsField(String semanticsLabel, String value) {
        semantics.enableFlutterSemantics();
        if (!waitUntil(d -> isLookupFormVisible(d), DEFAULT_WAIT)) {
            logLookupDetailSemantics();
            throw new NoSuchElementException("Lookup detail form is not visible for field: " + semanticsLabel);
        }
        scrollToSemanticsLabel(semanticsLabel);
        scrollToFormFieldByIndex(fieldIndexForSemantics(semanticsLabel));
        WebElement input = findLookupFormInput(semanticsLabel);
        typeExactValue(input, value);
    }

    private WebElement findLookupFormInput(String semanticsLabel) {
        if (!driver.findElements(semanticsFieldInput(semanticsLabel)).isEmpty()) {
            return wait.until(ExpectedConditions.presenceOfElementLocated(
                    semanticsFieldInput(semanticsLabel)));
        }
        WebElement inputFromScript = findLookupFormInputViaScript(semanticsLabel);
        if (inputFromScript != null) {
            return inputFromScript;
        }
        By fallbackInput = formInputByIndex(fieldIndexForSemantics(semanticsLabel));
        if (!driver.findElements(fallbackInput).isEmpty()) {
            return wait.until(ExpectedConditions.presenceOfElementLocated(fallbackInput));
        }
        throw new NoSuchElementException("Lookup form input not found for field: " + semanticsLabel);
    }

    private WebElement findLookupFormInputViaScript(String semanticsLabel) {
        return (WebElement) ((JavascriptExecutor) driver).executeScript(
                "const label = arguments[0];" +
                        "const field = document.querySelector('flt-semantics[aria-label=\"' + label + '\"]')" +
                        "  || document.querySelector('flt-semantics[aria-label*=\"' + label + '\"]');" +
                        "let input = field" +
                        "  ? (field.querySelector('input, textarea') || field)" +
                        "  : (document.querySelector('input[aria-label=\"' + label + '\"]')" +
                        "    || document.querySelector('input[aria-label*=\"' + label + '\"]'));" +
                        "if (input) return input;" +
                        "const fieldOrder = { name_field: 0, spname_field: 1, ptname_field: 2, frname_field: 3 };" +
                        "const labelHints = {" +
                        "  name_field: ['Industry Name', 'Role Name', 'Roles', 'Interests', 'Interest Name', 'Skill Name', 'Skills', 'Social Media Name', 'Social Media', 'English']," +
                        "  spname_field: ['Spanish Name', 'Spanish']," +
                        "  ptname_field: ['Portuguese Name', 'Portuguese']," +
                        "  frname_field: ['Canadian Name', 'Canadian', 'French']" +
                        "};" +
                        "const allInputs = Array.from(document.querySelectorAll('input[data-semantics-role=\"text-field\"], flt-semantics input'));" +
                        "const index = fieldOrder[label];" +
                        "if (index !== undefined && allInputs[index]) {" +
                        "  allInputs[index].scrollIntoView({block: 'center', inline: 'nearest'});" +
                        "  return allInputs[index];" +
                        "}" +
                        "for (const hint of (labelHints[label] || [])) {" +
                        "  const marker = Array.from(document.querySelectorAll('flt-semantics'))" +
                        "    .find(node => (node.textContent || '').trim().includes(hint));" +
                        "  if (!marker) continue;" +
                        "  const nearbyInput = marker.querySelector('input, textarea')" +
                        "    || marker.parentElement?.querySelector('input, textarea')" +
                        "    || marker.closest('flt-semantics')?.querySelector('input, textarea');" +
                        "  if (nearbyInput) return nearbyInput;" +
                        "}" +
                        "return null;",
                semanticsLabel
        );
    }

    private void typeExactValue(WebElement input, String value) {
        semantics.scrollIntoView(input);
        if (!semantics.clickElementReliably(input)) {
            input.click();
        }
        try {
            input.sendKeys(Keys.chord(SELECT_ALL_MODIFIER, "a"));
            input.sendKeys(Keys.BACK_SPACE);
            input.sendKeys(value);
            String currentValue = input.getAttribute("value");
            if (currentValue == null || !currentValue.equals(value)) {
                setInputValueViaScript(input, value);
            }
        } catch (Exception ex) {
            setInputValueViaScript(input, value);
        }
        input.sendKeys(Keys.TAB);
    }

    private void setInputValueViaScript(WebElement input, String value) {
        ((JavascriptExecutor) driver).executeScript(
                "const el = arguments[0];" +
                        "const val = arguments[1];" +
                        "el.focus();" +
                        "el.value = val;" +
                        "el.dispatchEvent(new Event('input', { bubbles: true }));" +
                        "el.dispatchEvent(new Event('change', { bubbles: true }));",
                input,
                value
        );
    }

    private static By formInputByIndex(int index) {
        return By.xpath("(//input[@data-semantics-role='text-field'] | //flt-semantics//input)[" + (index + 1) + "]");
    }

    private boolean clickSaveOrAddViaScript() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const candidates = [" +
                        "  'social_media_link_name_add_button'," +
                        "  'what_is_important_to_you_add_button'," +
                        "  'interest_and_hobbies_add_button'," +
                        "  'professional_background_add_button'," +
                        "  'save_lookup_detail_button'," +
                        "  'add_lookup_detail_button'," +
                        "  'lookup_detail_save_button'" +
                        "];" +
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
                        "for (const label of candidates) {" +
                        "  const node = document.querySelector('flt-semantics[aria-label=\"' + label + '\"]');" +
                        "  if (clickNode(node)) return true;" +
                        "}" +
                        "const buttons = Array.from(document.querySelectorAll('flt-semantics[role=\"button\"]'));" +
                        "const addButton = buttons.find(node => (node.textContent || '').trim() === 'Add');" +
                        "return clickNode(addButton);"
        ));
    }

    private void clickSemanticsElement(By locator) {
        semantics.clickSemanticsElement(locator, wait);
    }

    private boolean waitUntil(Function<WebDriver, Boolean> condition, Duration timeout) {
        return semantics.waitUntil(condition, timeout);
    }

    private static int fieldIndexForSemantics(String semanticsLabel) {
        return switch (semanticsLabel) {
            case SEMANTICS_ENGLISH_NAME -> 0;
            case SEMANTICS_SPANISH_NAME -> 1;
            case SEMANTICS_PORTUGUESE_NAME -> 2;
            case SEMANTICS_CANADIAN_NAME -> 3;
            default -> 0;
        };
    }

    private static By semanticsButton(String label) {
        return By.xpath(
                "//flt-semantics[@aria-label='" + label + "']" +
                        " | //flt-semantics[normalize-space(@aria-label)='" + label + "']" +
                        " | //flt-semantics[contains(@aria-label,'" + label + "')]");
    }

    private static By semanticsField(String semanticsLabel) {
        return By.xpath(
                "//flt-semantics[@aria-label='" + semanticsLabel + "']" +
                        " | //flt-semantics[contains(@aria-label,'" + semanticsLabel + "')]");
    }

    private static By semanticsFieldInput(String semanticsLabel) {
        return By.xpath(
                "//flt-semantics[@aria-label='" + semanticsLabel + "']//input" +
                        " | //flt-semantics[contains(@aria-label,'" + semanticsLabel + "')]//input" +
                        " | //flt-semantics[@aria-label='" + semanticsLabel + "']//textarea" +
                        " | //flt-semantics[contains(@aria-label,'" + semanticsLabel + "')]//textarea" +
                        " | //input[@aria-label='" + semanticsLabel + "']" +
                        " | //input[contains(@aria-label,'" + semanticsLabel + "')]");
    }
}
