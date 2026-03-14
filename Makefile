# ═══════════════════════════════════════════════════════════════════════════════
# Enterprise API Automation Suite — Makefile
# ═══════════════════════════════════════════════════════════════════════════════

ENV    ?= qa
SUITE  ?= regression
THREADS ?= 5

.PHONY: help compile clean smoke regression contract negative parallel \
        report allure-serve docker-build docker-test docker-clean \
        check-deps validate

help: ## Show this help message
	@echo "Enterprise API Automation Suite"
	@echo ""
	@echo "Usage: make <target> [ENV=qa] [SUITE=regression] [THREADS=5]"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'

# ── Lifecycle ──────────────────────────────────────────────────────────────────

compile: ## Compile all source code
	mvn compile --no-transfer-progress

clean: ## Remove build artifacts
	mvn clean --no-transfer-progress
	rm -rf allure-results allure-report

validate: ## Validate POM and enforce rules
	mvn validate enforcer:enforce --no-transfer-progress

# ── Test execution ─────────────────────────────────────────────────────────────

smoke: ## Run smoke suite — fastest feedback (~30s)
	mvn test \
		-Denv=$(ENV) \
		-Dtestng.suite=src/test/resources/testng-suites/smoke.xml \
		-Dthreads=3 \
		--no-transfer-progress

regression: ## Run full regression suite
	mvn test \
		-Denv=$(ENV) \
		-Dtestng.suite=src/test/resources/testng-suites/regression.xml \
		-Dthreads=$(THREADS) \
		--no-transfer-progress

contract: ## Run WireMock contract tests (offline — no network needed)
	mvn test \
		-Dtestng.suite=src/test/resources/testng-suites/contract.xml \
		--no-transfer-progress

negative: ## Run negative / boundary tests
	mvn test \
		-Denv=$(ENV) \
		-Dtestng.suite=src/test/resources/testng-suites/negative.xml \
		--no-transfer-progress

parallel: ## Run full suite with maximum parallelism
	mvn test \
		-Denv=$(ENV) \
		-Dtestng.suite=src/test/resources/testng-suites/parallel.xml \
		-Dthreads=$(THREADS) \
		--no-transfer-progress

all-suites: smoke regression contract negative ## Run all suites sequentially

# ── Reporting ──────────────────────────────────────────────────────────────────

report: ## Open Allure report locally (requires allure CLI)
	mvn allure:serve --no-transfer-progress

allure-generate: ## Generate static Allure report in target/
	mvn allure:report --no-transfer-progress

extent-open: ## Open ExtentReport in browser (macOS)
	open target/extent-reports/TestReport.html

# ── Docker ─────────────────────────────────────────────────────────────────────

docker-build: ## Build Docker test image
	docker build -t api-automation-suite:latest .

docker-test: ## Run tests in Docker container
	docker run --rm \
		-e ENV=$(ENV) \
		-e QA_AUTH_TOKEN=$(QA_AUTH_TOKEN) \
		-v $(PWD)/output:/app/output \
		api-automation-suite:latest \
		mvn test -Denv=$(ENV) \
		-Dtestng.suite=src/test/resources/testng-suites/$(SUITE).xml

docker-clean: ## Remove Docker test image
	docker rmi api-automation-suite:latest 2>/dev/null || true

# ── Quality ────────────────────────────────────────────────────────────────────

check-deps: ## Run OWASP dependency vulnerability check
	mvn dependency-check:check --no-transfer-progress

dependency-tree: ## Print full Maven dependency tree
	mvn dependency:tree --no-transfer-progress