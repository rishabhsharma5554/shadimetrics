# Docker Build Fix Guide

## Problem
Remote Docker builder cannot find `/src` directory during build.

```
Build Failed: /src not found
scheduling build on Metal builder "builder-ujogcf"
```

## Root Cause
Your Docker Desktop is configured to use a **remote/cloud builder** instead of the local Docker daemon. The build context is not being properly transferred.

## Solution

### Option 1: Build Locally (Recommended)
Use your local Docker daemon instead of remote builder:

```bash
# Navigate to project root
cd C:\Users\RishabhSharma\Desktop\shadimetrics

# Build with local Docker daemon
docker buildx build --load -t shaadi-metrics:latest .

# Or use docker build
docker build -t shaadi-metrics:latest .
```

### Option 2: Use Docker Compose (Easiest)
Create a `docker-compose.yml` in your project root:

```yaml
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/shaadimetrics
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
    depends_on:
      - mysql
    volumes:
      - ./data:/app/data
      - ./uploads:/app/uploads

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: shaadimetrics
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql

volumes:
  mysql-data:
```

Then run:
```bash
docker-compose up --build
```

### Option 3: Reset Docker Builders
If you need to use remote builders, reset the buildx configuration:

```powershell
# List available builders
docker buildx ls

# Remove current builder
docker buildx rm builder-ujogcf

# Create new local builder
docker buildx create --name local-builder --use

# Verify
docker buildx ls
```

### Option 4: Check Docker Desktop Settings
In Docker Desktop:
1. Go to **Settings** → **Builder**
2. Change from **Cloud** to **Local** (if available)
3. Or switch the active builder context

## Verification Steps

### Step 1: Verify Build Files
```powershell
# Check all required files exist
$required = @(
    "Dockerfile",
    "gradlew",
    "gradlew.bat",
    "settings.gradle",
    "build.gradle",
    "src"
)

$required | ForEach-Object {
    $path = "C:\Users\RishabhSharma\Desktop\shadimetrics\$_"
    if (Test-Path $path) {
        Write-Host "✅ $_"
    } else {
        Write-Host "❌ $_ - NOT FOUND"
    }
}
```

### Step 2: Test Local Build
```bash
# Clean build locally first
mvn clean package

# Then build Docker image
docker build -t shaadi-metrics:latest .

# Check if image was created
docker images | grep shaadi-metrics
```

### Step 3: Run Container
```bash
# Run the image
docker run -p 8080:8080 shaadi-metrics:latest

# Verify it's running
curl http://localhost:8080
```

## Common Issues

### Issue: "Cannot find gradlew"
**Solution:** Ensure gradlew file is executable:
```bash
chmod +x gradlew
```

### Issue: "Cannot find gradle files"
**Solution:** Verify build.gradle exists:
```powershell
Test-Path "C:\Users\RishabhSharma\Desktop\shadimetrics\build.gradle"
```

### Issue: "Out of disk space"
**Solution:** Docker build is using disk space. Clean up:
```bash
docker system prune -a
docker builder prune
```

### Issue: Remote builder still being used
**Solution:** Switch to local builder:
```bash
docker buildx use default
# or
docker buildx create --name local --use
```

## Recommended Build Command

For your project specifically:

```bash
cd C:\Users\RishabhSharma\Desktop\shadimetrics

# Option A: Using docker buildx locally
docker buildx build --load -t shaadi-metrics:latest .

# Option B: Using standard docker build
docker build -t shaadi-metrics:latest .

# Option C: Using docker-compose (with MySQL)
docker-compose up --build
```

## What These Do

| Command | What It Does | When to Use |
|---------|------------|------------|
| `docker build` | Builds image locally using local Docker daemon | Simple builds, local testing |
| `docker buildx build --load` | Uses buildx but loads to local Docker | More control, local testing |
| `docker-compose up` | Builds image AND starts with supporting services | Full app with database |

## Environment Variables for Container

When running the container, set these variables:

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/shaadimetrics" \
  -e SPRING_DATASOURCE_USERNAME="root" \
  -e SPRING_DATASOURCE_PASSWORD="root" \
  shaadi-metrics:latest
```

Or use docker-compose (handles this automatically).

## Next Steps

1. **Try Option 1 first** (local build) - simplest fix
2. If that works, your code is correct
3. Then debug remote builder configuration if needed
4. Consider using docker-compose for full stack

---

**Status**: Guide provided - Use local Docker build to verify, then configure remote builder if needed
