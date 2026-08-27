package com.example.pages.support;

/**
 * Flutter semantics labels for row Delete/Block actions on users tables.
 */
public record UserRowActionsConfig(
        String deleteButtonLabel,
        String blockButtonLabel,
        String unblockButtonLabel,
        String searchFieldLabel,
        String deleteButtonLabelFragment,
        String logContext
) {
    public static UserRowActionsConfig company() {
        return new UserRowActionsConfig(
                "delete_company_button_0",
                "block_company_button_0",
                "unblock_company_button_0",
                "company_search_field_input",
                "delete_company_button",
                "Company"
        );
    }

    public static UserRowActionsConfig individual() {
        return new UserRowActionsConfig(
                "delete_user_button_0",
                "block_user_button_0",
                "unblock_user_button_0",
                "user_search_field",
                "delete_user_button",
                "Individual"
        );
    }

    public static UserRowActionsConfig events() {
        return new UserRowActionsConfig(
                "delete_event_button_0",
                "block_event_button_0",
                "unblock_event_button_0",
                "search_by_name__email_or_event_id_field",
                "delete_event_button",
                "Events"
        );
    }
}
