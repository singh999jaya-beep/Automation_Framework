package com.example.pages.support;

/**
 * Page-specific status filter semantics and visible-text labels.
 */
public record StatusFilterConfig(
        String searchFieldLabel,
        String[] alternateSearchFieldLabels,
        String rowStatusLabelPrefix,
        String[] dropdownTriggerLabels,
        String[] dropdownTriggerVisibleTexts,
        StatusOptionConfig activeOption,
        StatusOptionConfig inactiveOption
) {

    public record StatusOptionConfig(String visibleText, String[] semanticsLabels) {
    }

    public static StatusFilterConfig individual() {
        return new StatusFilterConfig(
                "user_search_field",
                new String[0],
                "user_row_status",
                new String[]{"user_status_filter", "status_filter", "status_option_status"},
                new String[]{"Status"},
                new StatusOptionConfig("Active", new String[]{"status_option_active", "active_option_active"}),
                new StatusOptionConfig("Inactive", new String[]{"status_option_inactive", "blocked_option_blocked"})
        );
    }

    public static StatusFilterConfig company() {
        return new StatusFilterConfig(
                "user_search_field",
                new String[]{"company_search_field_input", "company_search_field"},
                "company_row_status",
                new String[]{"user_status_filter", "status_filter", "status_option_status"},
                new String[]{"Status"},
                new StatusOptionConfig("Active", new String[]{"active_option_active", "status_option_active"}),
                new StatusOptionConfig("Blocked", new String[]{"blocked_option_blocked", "status_option_inactive"})
        );
    }

    public static StatusFilterConfig transactions() {
        return new StatusFilterConfig(
                "transaction_search_field_input",
                new String[0],
                "transaction_row",
                new String[]{"all_submenu"},
                new String[]{"All"},
                // Menu-open detection uses these option labels when the All type filter is open.
                new StatusOptionConfig("Event Invitation", new String[]{"eventinv_option_eventinv"}),
                // Keep Advertisement as the paired option so Advertisement filter scenarios
                // still detect an open menu; Monthly is selected via dedicated page helpers.
                new StatusOptionConfig("Advertisement", new String[]{"advertisement_submenu", "advertisement_option_advertisement"})
        );
    }

    public static StatusFilterConfig moderation() {
        return new StatusFilterConfig(
                "reporting_search_field_input",
                new String[0],
                "moderation_row_status",
                new String[]{"all_status_option_all_status"},
                new String[]{"All Status", "All status", "Status"},
                new StatusOptionConfig("Pending", new String[]{"status_0_option_status_0"}),
                new StatusOptionConfig("Report Dismissed", new String[]{"status_1_option_status_1"})
        );
    }

    public static StatusFilterConfig manageAdminUsers() {
        return new StatusFilterConfig(
                "search_field",
                new String[0],
                "admin_row_status",
                new String[]{"all_status_option_all_status"},
                new String[]{"All Status", "All status", "Status"},
                new StatusOptionConfig("Active", new String[]{"active_option_active"}),
                new StatusOptionConfig("Blocked", new String[]{"blocked_option_blocked"})
        );
    }

    public static StatusFilterConfig manageAdminUsersAccess() {
        return new StatusFilterConfig(
                "search_field",
                new String[0],
                "admin_row_access",
                new String[]{"all_access_option_all_access"},
                new String[]{"All Access", "All access", "Access"},
                new StatusOptionConfig("Permanent", new String[]{"permanent_option_permanent"}),
                new StatusOptionConfig("Temporary", new String[]{"temporary_option_temporary"})
        );
    }

    public static StatusFilterConfig auditLogsModules() {
        return new StatusFilterConfig(
                "search_field",
                new String[]{"export_logs_button"},
                "audit_log_row",
                new String[]{"all_modules_option_all_modules"},
                new String[]{"All Modules", "All modules"},
                new StatusOptionConfig("Individual Users", new String[]{"individual_users_option_individual_users"}),
                new StatusOptionConfig("Company Users", new String[]{"company_users_option_company_users"})
        );
    }

    public static StatusFilterConfig auditLogsActions() {
        return new StatusFilterConfig(
                "search_field",
                new String[]{"export_logs_button"},
                "audit_log_row",
                new String[]{"all_actions_option_all_actions"},
                new String[]{"All Actions", "All actions"},
                new StatusOptionConfig("Created", new String[]{"created_option_created"}),
                new StatusOptionConfig("Updated", new String[]{"updated_option_updated"})
        );
    }

    public static StatusFilterConfig deletedAppUsers() {
        return new StatusFilterConfig(
                "user_type_all_option",
                new String[]{"deleted_user_type_0"},
                "deleted_user_type",
                new String[]{"user_type_all_option"},
                new String[]{"All", "User Type"},
                new StatusOptionConfig("Individual", new String[]{"user_type_individual_option"}),
                new StatusOptionConfig("Company", new String[]{"user_type_company_option"})
        );
    }
}
