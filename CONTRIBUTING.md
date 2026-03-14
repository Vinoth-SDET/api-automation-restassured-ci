# Contributing

## Branch Strategy

| Branch | Purpose |
|---|---|
| `main` | Stable — CI gate enforced |
| `feature/EAS-NNN-short-description` | Feature or test additions |
| `fix/EAS-NNN-short-description` | Bug fixes |
| `chore/...` | Dependency updates, config changes |

## Commit Convention

```
type(scope): short imperative description

feat(users): add negative tests for 404 and 422 responses
fix(retry): correct exponential backoff calculation
chore(deps): bump restassured to 5.4.0
test(contract): add WireMock stub for POST /users
docs(readme): update CI badge URLs
```

## Pull Request Checklist

Before raising a PR, verify:

- [ ] Tests are in the correct package (`smoke`, `regression`, `contract`, `negative`)
- [ ] New test methods are annotated with `@Epic`, `@Feature`, `@Story`, `@Severity`
- [ ] No hardcoded credentials or environment-specific URLs in code
- [ ] `ResponseValidator` used for all assertions (no raw `assertEquals`)
- [ ] New POJOs use `@Builder`, `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`
- [ ] JSON schema file added to `src/test/resources/schemas/` for new response types
- [ ] Test method names follow the pattern `action_condition_expectedOutcome`
- [ ] `target/` is not committed (check `.gitignore`)
- [ ] CI passes on the feature branch before requesting review

## Test Naming Convention

```java
// Pattern: action_condition_expectedOutcome
getExistingUser_withValidId_returns200AndValidSchema()
createUser_withMissingRequiredField_returns422()
deleteUser_asUnauthorisedCaller_returns401()
```

## Adding a New API Service

1. Add endpoint constants to `constants/Endpoints.java`
2. Create request/response POJOs in `models/request/` and `models/response/`
3. Add JSON schema file to `src/test/resources/schemas/`
4. Create the service class in `services/` with `@Step` annotations
5. Add test class(es) in `tests/` — covering positive, negative, and contract cases
6. Register new tests in the appropriate `testng-suites/*.xml` files
7. Add sample test data to `src/test/resources/testdata/`
