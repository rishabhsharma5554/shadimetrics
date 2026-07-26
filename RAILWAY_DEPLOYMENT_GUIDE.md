# Railway Cloud Deployment Guide

## Problem
Docker build fails on Railway with error: `/src not found`

## Root Cause
Railway's Docker builder has different build context handling than local Docker. The `src` directory path isn't being resolved correctly during the remote build.

## Solutions

### ✅ Solution 1: Use Nixpacks (Recommended - Easiest)

Railway auto-detects Spring Boot + Gradle apps. Let Railway build it automatically without Dockerfile.

**Steps:**
1. Delete or rename the `Dockerfile` (or tell Railway to ignore it)
2. Keep the `railway.json` file (already created)
3. Push to Railway

Railway will:
- Auto-detect Gradle build system
- Auto-detect Spring Boot
- Build using Nixpacks (faster, more reliable)
- Deploy automatically

**Files needed:**
- ✅ `railway.json` (created)
- ✅ `Procfile` (created)
- ❌ Remove or rename `Dockerfile`

### ✅ Solution 2: Fix Dockerfile for Railway

If you need to keep the Dockerfile, modify it for Railway's builder:

**Updated Dockerfile:**
```dockerfile
# ---- Build stage ----
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy everything first (wider context for Railway)
COPY . .

# Make gradlew executable
RUN chmod +x gradlew

# Check Gradle version
RUN ./gradlew --no-daemon --version

# Build the application
RUN ./gradlew --no-daemon clean bootJar -x test

# ---- Runtime stage ----
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

RUN useradd --create-home --shell /bin/bash appuser \
    && mkdir -p /app/data /app/uploads \
    && chown -R appuser:appuser /app

COPY --from=build /app/build/libs/*.jar app.jar

USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

**Key change:** `COPY . .` instead of selective copies. This works better with Railway's build context.

### ✅ Solution 3: Create .dockerignore Properly

Ensure `.dockerignore` doesn't exclude important files:

```
.gradle/
build/
.idea/
.vscode/
*.iml
HELP.md
.git/
.gitignore
.env
.env.local
*.log
node_modules/
```

**Don't exclude:**
- ❌ src/
- ❌ gradle/
- ❌ gradlew
- ❌ build.gradle
- ❌ settings.gradle

## Recommended for Railway

### Step 1: Update railway.json
```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "nixpacks"
  },
  "deploy": {
    "startCommand": "java -jar build/libs/*.jar",
    "restartPolicyType": "on_failure",
    "restartPolicyMaxRetries": 5
  }
}
```

### Step 2: Create Procfile
```
web: java -jar build/libs/*.jar
```

### Step 3: Handle Dockerfile
**Option A: Delete it**
```bash
rm Dockerfile
```

**Option B: Rename it**
```bash
mv Dockerfile Dockerfile.local
```

**Option C: Keep and use fixed version** (see above)

### Step 4: Environment Variables on Railway

Set these in Railway dashboard:

| Variable | Value | Notes |
|----------|-------|-------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://host:port/db` | Railway MySQL URL |
| `SPRING_DATASOURCE_USERNAME` | `root` | From Railway MySQL |
| `SPRING_DATASOURCE_PASSWORD` | `password` | From Railway MySQL |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Auto-create tables |
| `SERVER_PORT` | `8080` | Default |

## Step-by-Step Deployment

### 1. Connect Railway to GitHub
- Go to railway.app
- Create new project
- Connect GitHub repo
- Select shaadimetrics repository

### 2. Configure Build
- Railway auto-detects Gradle + Spring Boot
- Or provide `railway.json` (already done)

### 3. Add Services

#### For Database (MySQL):
- Add MySQL service in Railway dashboard
- Note: copy connection string
- Set environment variables automatically

#### For Application:
- Environment variables auto-populated from MySQL service
- Or set manually in Railway dashboard

### 4. Deploy
- Commit changes to GitHub
```bash
git add railway.json Procfile .dockerignore
git commit -m "Add Railway deployment configuration"
git push origin main
```

- Railway auto-deploys on push
- Check Railway dashboard for build logs

## Debugging Railway Builds

### View Logs
```bash
# Using Railway CLI
railway logs

# Or check dashboard: Deployments → View Logs
```

### Common Issues

**Issue: Build still fails with `/src not found`**
- Solution: Delete Dockerfile, use Nixpacks
- Verify `.dockerignore` doesn't exclude src/

**Issue: App crashes after deploy**
- Check logs: `railway logs`
- Verify database connection string
- Check environment variables set

**Issue: Port not responding**
- Railway exposes port automatically
- Verify `SERVER_PORT=8080` in env vars
- Check app logs for startup errors

## Database on Railway

### Option 1: Railway MySQL Service
1. Add MySQL plugin in Railway dashboard
2. Connection variables auto-populated
3. Database name: `shaadimetrics` (or change as needed)

### Option 2: External Database
Set `SPRING_DATASOURCE_URL` to your database:
```
jdbc:mysql://hostname:3306/shaadimetrics
```

## Files for Railway

**Create/Modify:**
- ✅ `railway.json` - Build configuration
- ✅ `Procfile` - Start command
- ✅ `.dockerignore` - Exclude files

**Delete/Rename:**
- ❌ `Dockerfile` (use Nixpacks instead)

**Keep As-Is:**
- ✅ Everything else

## Quick Checklist

- [ ] Create `railway.json`
- [ ] Create `Procfile`
- [ ] Delete or rename `Dockerfile`
- [ ] Update `.dockerignore` (don't exclude src/)
- [ ] Commit all changes
- [ ] Push to GitHub
- [ ] Check Railway dashboard
- [ ] Verify build completes
- [ ] Set environment variables
- [ ] Test at Railway URL

## Connection String Example

For Railway MySQL service:
```
jdbc:mysql://mysql.railway.internal:3306/shaadimetrics
```

Or from Railway dashboard MySQL plugin, copy the value provided.

## After Deployment

Your app will be available at:
```
https://your-app-name-prod.up.railway.app
```

All stats will work automatically with the database!

---

**Status**: Ready for Railway deployment ✅

Choose Solution 1 (Nixpacks) for easiest, fastest deployment!
