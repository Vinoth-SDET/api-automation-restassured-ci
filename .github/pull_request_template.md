## Summary
Brief description of what this PR does.

## Type
- [ ] New test(s)
- [ ] Framework change
- [ ] Bug fix
- [ ] Dependency update

## PR Checklist
- [ ] Tests are in the correct package and suite XML
- [ ] New tests annotated with `@Epic`, `@Feature`, `@Story`, `@Severity`
- [ ] `ResponseValidator.of()` used — no raw assertions
- [ ] No hardcoded URLs or credentials
- [ ] New POJOs use `@Builder`, `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`
- [ ] JSON schema added for any new response type
- [ ] `target/` and `.idea/` are NOT committed
- [ ] CI passes on this branch

## Test Evidence
Paste Allure report link or screenshot of passing run.