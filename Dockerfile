# ═══════════════════════════════════════════════════════════════════════════════
# Multi-stage Dockerfile for hermetic test execution
# Stage 1: dependency cache layer (rebuilt only when pom.xml changes)
# Stage 2: test execution layer
# ═══════════════════════════════════════════════════════════════════════════════

# ── Stage 1: dependency cache ─────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-jammy AS deps

WORKDIR /app
COPY pom.xml .

# Download all dependencies without running any code
# Cached as a separate layer — pom.xml changes bust this, src changes do not
RUN mvn dependency:go-offline -q --no-transfer-progress 2>/dev/null || \
    mvn dependency:resolve --no-transfer-progress

# ── Stage 2: test runner ──────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-jammy AS runner

WORKDIR /app

# Reuse the cached Maven repo from stage 1
COPY --from=deps /root/.m2 /root/.m2
COPY --from=deps /app/pom.xml .

# Copy source after deps — maximises layer cache hits
COPY src/ src/

# Environment defaults (overridden at runtime via -e flags or docker run env)
ENV ENV=qa
ENV SUITE=regression
ENV THREADS=5

# Test execution entrypoint
CMD mvn test \
    -Denv=${ENV} \
    -Dtestng.suite=src/test/resources/testng-suites/${SUITE}.xml \
    -Dthreads=${THREADS} \
    --no-transfer-progress

# ── Usage ─────────────────────────────────────────────────────────────────────
# docker build -t api-automation-suite .
# docker run -e ENV=qa -e QA_AUTH_TOKEN=$QA_AUTH_TOKEN api-automation-suite
# docker run -e ENV=staging -e SUITE=contract api-automation-suite