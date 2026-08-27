package com.example.utils;

public final class UsersPageContext {

    public enum Section {
        INDIVIDUAL,
        COMPANY,
        EVENTS,
        TRANSACTIONS,
        MODERATION,
        CONSENT_AUDIT_LOG,
        AUDIT_LOGS,
        MANAGE_ROLES,
        MANAGE_ADMIN_USERS,
        ADMIN_SETTINGS,
        DELETED_APP_USERS
    }

    private static final ThreadLocal<Section> currentSection =
            ThreadLocal.withInitial(() -> Section.INDIVIDUAL);
    private static final ThreadLocal<String> searchedEmail = new ThreadLocal<>();
    private static final ThreadLocal<String> searchedName = new ThreadLocal<>();
    private static final ThreadLocal<String> searchedEventId = new ThreadLocal<>();

    private UsersPageContext() {
    }

    public static void setSection(Section section) {
        currentSection.set(section);
    }

    public static Section getSection() {
        return currentSection.get();
    }

    public static boolean isCompanySection() {
        return Section.COMPANY == currentSection.get();
    }

    public static boolean isEventsSection() {
        return Section.EVENTS == currentSection.get();
    }

    public static boolean isTransactionsSection() {
        return Section.TRANSACTIONS == currentSection.get();
    }

    public static boolean isModerationSection() {
        return Section.MODERATION == currentSection.get();
    }

    public static boolean isConsentAuditLogSection() {
        return Section.CONSENT_AUDIT_LOG == currentSection.get();
    }

    public static boolean isAuditLogsSection() {
        return Section.AUDIT_LOGS == currentSection.get();
    }

    public static boolean isManageRolesSection() {
        return Section.MANAGE_ROLES == currentSection.get();
    }

    public static boolean isManageAdminUsersSection() {
        return Section.MANAGE_ADMIN_USERS == currentSection.get();
    }

    public static boolean isAdminSettingsSection() {
        return Section.ADMIN_SETTINGS == currentSection.get();
    }

    public static boolean isDeletedAppUsersSection() {
        return Section.DELETED_APP_USERS == currentSection.get();
    }

    public static void setSearchedEmail(String email) {
        searchedEmail.set(email);
    }

    public static String getSearchedEmail() {
        return searchedEmail.get();
    }

    public static void setSearchedName(String name) {
        searchedName.set(name);
    }

    public static String getSearchedName() {
        return searchedName.get();
    }

    public static void setSearchedEventId(String eventId) {
        searchedEventId.set(eventId);
    }

    public static String getSearchedEventId() {
        return searchedEventId.get();
    }

    public static void clearSearchState() {
        searchedEmail.remove();
        searchedName.remove();
        searchedEventId.remove();
    }
}
