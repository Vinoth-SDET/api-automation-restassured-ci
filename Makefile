# ──────────────────────────────────────────────────────────────────────────────
# Enterprise API Automation Framework — Makefile shortcuts
#
# Usage:
#   make test                           # run regression on QA
#   make test ENV=staging SUITE=smoke   # run smoke on staging
#   make report                         # open Allure report (local)
#   make docker-test ENV=qa             # run tests in Docker
#   make clean                          # wipe build + report artefacts
# ──────────────────────────────────────────────────────────────────────────────

ENV    ?= qa
SUITE  ?= regression
THREADS ?= 5

.PHONY: test smoke regression parallel report docker-test clean help

## Run the specified suite locally
test:
	mvn test -B \
	  -Denv=$(ENV) \
	  -Dtestng.suite=src/test/resources/testng-suites/$(SUITE).xml \
	  -Dthreads=$(THREADS)

## Run smoke suite
smoke:
	$(MAKE) test SUITE=smoke

## Run full regression suite
regression:
	$(MAKE) test SUITE=regression

## Run parallel suite
parallel:
	$(MAKE) test SUITE=parallel

## Serve Allure report in browser
report:
	mvn allure:serve

## Build Docker image and run tests inside container
docker-test:
	docker build \
	  --build-arg ENV=$(ENV) \
	  --build-arg SUITE=$(SUITE) \
	  --build-arg THREADS=$(THREADS) \
	  -t api-automation:latest .
	mkdir -p output
	docker run --rm -v $(PWD)/output:/output api-automation:latest

## Clean build artefacts and reports
clean:
	mvn clean
	rm -rf allure-results allure-report output target/logs

## Show this help
help:

	@grep -E '^##' Makefile | sed 's/## //'
