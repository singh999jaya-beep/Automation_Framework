# VI-Admin

Selenium + Cucumber + TestNG end-to-end tests for the VI Admin Flutter Web application.

[![VI Admin Tests](https://github.com/vilaigic/VI-Admin/actions/workflows/ci.yml/badge.svg)](https://github.com/vilaigic/VI-Admin/actions/workflows/ci.yml)

## Prerequisites

- JDK 17+
- Maven 3.x
- Google Chrome

## VI Admin Test CI/CD

All **feature modules** are covered by the CI suite. There is no separate regression suite yet.

| Feature module | TestNG runner |
|----------------|---------------|
| Login | `runner.testng.LoginTestNGRunner` |
| Password Reset | `runner.testng.PasswordResetTestNGRunner` |
| Input Categories | `runner.testng.InputCategoriesTestNGRunner` |
| Company Users | `runner.testng.CompanyUsersTestNGRunner` |
| Individual Users | `runner.testng.IndividualUsersTestNGRunner` |
| Manage Admin Users | `runner.testng.ManageAdminUsersTestNGRunner` |
| Manage Roles | `runner.testng.ManageRolesTestNGRunner` |
| Manage Events | `runner.testng.ManageEventsTestNGRunner` |
| Advertisements | `runner.testng.AdvertisementTestNGRunner` |
| Moderation Center | `runner.testng.ModerationTestNGRunner` |
| Transactions | `runner.testng.TransactionsTestNGRunner` |
| Consent Audit Log | `runner.testng.ConsentAuditLogTestNGRunner` |
| Audit Logs | `runner.testng.AuditLogsTestNGRunner` |
| Audit Logs Actions | `runner.testng.AuditLogsActionsTestNGRunner` |
| Admin Settings | `runner.testng.AdminSettingsTestNGRunner` |
| Deleted App Users | `runner.testng.DeletedAppUsersTestNGRunner` |

### Trigger the pipeline

Workflow: [`.github/workflows/ci.yml`](.github/workflows/ci.yml)

- **Push** to `main`, `develop`, or `feature/**`
- **Pull request** targeting `main`
- **Manual:** Actions → **VI Admin Tests** → **Run workflow**

CI runs modules in **parallel** (one job per TestNG runner). A `report` job merges results into one artifact. A final `summary` job fails if any module fails.

### Download reports

After a workflow run, download the single combined artifact:

- **`test-report`** — all modules in one package:
  - `allure-report/index.html` — open in a browser for the full Allure report (pass and fail)
  - `surefire-reports/` — TestNG XML per module
  - `cucumber-reports/` — Cucumber HTML per module

Per-module raw artifacts (`allure-results-*`, etc.) are temporary (1 day) and used only to build the combined report.

### GitHub Secrets and variables

In **Settings → Secrets and variables → Actions**:

| Name | Type | Purpose |
|------|------|---------|
| `TEST_EMAIL` | Secret | Admin login email for CI |
| `TEST_PASSWORD` | Secret | Admin login password for CI |
| `BASE_URL` | Variable | Target environment URL (optional; defaults to CloudFront) |

Locally, credentials fall back to the values in feature files when `TEST_EMAIL` / `TEST_PASSWORD` are unset.

## Run tests locally

```bash
# Full CI suite (all modules)
mvn clean test -Ptestng -Dtestng.suite=src/test/resources/testng-ci.xml

# Full regression single runner (all features)
mvn clean test -Dtest=runner.testng.TestNGRunner

# Single module
mvn clean test -Dtest=runner.testng.LoginTestNGRunner

# Headless (same as CI)
CI=true mvn clean test -Dtest=runner.testng.LoginTestNGRunner

# Allure HTML report
mvn allure:report
mvn allure:serve
```

## Environment variables

| Variable | Description | Default |
|----------|-------------|---------|
| `BASE_URL` | Admin app URL | `https://dq2embcxfli7y.cloudfront.net/` |
| `TEST_EMAIL` | Admin login email | `viadmin@gmail.com` |
| `TEST_PASSWORD` | Admin login password | (see feature files) |
| `CI` | Enables headless Chrome when `true` | unset |
| `HEADLESS` | Force headless Chrome locally when `true` | unset |
| `CHROME_BINARY` | Custom Chrome binary path | unset |

## Reports

Allure results are written to `target/allure-results/`.

Cucumber HTML reports are written to `target/cucumber-reports/`.

Surefire XML reports are written to `target/surefire-reports/`.

## CI notes

- **Input Categories** file upload uses `src/test/resources/testdata/linkedin.png`.
- Flutter Web needs extra wait in CI; step delays are applied in `Hooks.java`.
- Use `@ignore` only for truly broken scenarios, not to exclude tests from CI.
