# Railway Deployment with H2 Database - Complete Setup

## ✅ Ready for Railway!

Your app is now configured for **Railway Cloud with H2 database** - no external database needed!

## What's Configured

✅ **H2 Database** - Embedded, file-based, persistent  
✅ **Railway.json** - Auto-build configuration  
✅ **Procfile** - Start command with production profile  
✅ **application-production.properties** - Production settings  
✅ **Dynamic Stats** - Already working with H2  

## Before Pushing to Railway

### 1. Delete Dockerfile
```bash
# Remove the Dockerfile (Railway will use Nixpacks)
rm Dockerfile
```

### 2. Verify Files
```bash
# Check these files exist:
ls -la railway.json
ls -la Procfile
ls -la src/main/resources/application-production.properties
```

### 3. Commit Changes
```bash
git add .
git commit -m "Configure Railway deployment with H2 database

- Remove Dockerfile for Nixpacks auto-detection
- Add production profile for H2 persistence
- Configure Railway build and deploy settings
- All stats are dynamic and database-backed"

git push origin main
```

## Railway Deployment Steps

### 1. Connect to Railway
- Go to railway.app
- Login/signup
- Create new project

### 2. Connect GitHub
- Select "GitHub"
- Authorize Claude Code access
- Select repository: `shadimetrics`

### 3. Configure Environment
In Railway dashboard → Project Settings → Variables:

```
SPRING_PROFILES_ACTIVE=production
```

**Optional but recommended:**
```
SERVER_PORT=8080
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### 4. Deploy
- Railway auto-detects Gradle + Spring Boot
- Builds using Nixpacks
- Deploys automatically
- View logs in dashboard

## Database Details

### H2 Configuration
```
Database: H2 (embedded)
Location: /railway/volumes/data/shaadimetrics
User: sa
Password: (empty)
Mode: MySQL compatible
```

### Persistence
- Data stored in `/railway/volumes/data/`
- Railway provides persistent volumes
- Data survives restarts
- Stats are preserved

### Backup
- H2 creates `shaadimetrics.h2.db` file
- You can download from Railway storage
- Or connect via H2 console (optional)

## After Deployment

### 1. Access Your App
```
https://your-app-name-prod.up.railway.app
```

### 2. Verify Stats
- Visit homepage
- Check hero stats: "Weddings Planned", "On-time", "Budgeting"
- Stats pull from H2 database automatically

### 3. Admin Panel
- Go to `/admin/login`
- Username: `admin`
- Password: `ShaadiAdmin@2026` (change this in production!)
- Add consultation leads to see stats update

### 4. View Logs
```bash
# Using Railway CLI
railway logs

# Or in Railway dashboard: Deployments → View Logs
```

## Testing Locally First (Optional)

Before pushing to Railway, test locally with production profile:

```bash
# Run with production profile locally
gradle bootRun --args='--spring.profiles.active=production'

# Or
mvn spring-boot:run -Dspring-boot.run.arguments='--spring.profiles.active=production'
```

Then visit: http://localhost:8080

## Troubleshooting

### Issue: Build fails
**Solution:** Check Railway logs for errors
- Railway dashboard → Deployments → View Logs
- Look for compilation or Gradle errors

### Issue: App crashes after deploy
**Solution:** 
1. Check logs: `railway logs`
2. Verify environment variables set
3. Ensure Procfile start command is correct

### Issue: Stats show 0
**Solution:**
1. Add test consultation leads via admin panel
2. Mark some as "CONVERTED" status
3. Stats recalculate on page reload

### Issue: Data lost after restart
**Solution:** 
- Railway uses persistent volumes
- H2 file-based DB saves data
- Data should persist across restarts
- If not, check /railway/volumes/ permissions

## Architecture on Railway

```
┌─────────────────────────────────┐
│     Railway Cloud Platform       │
├─────────────────────────────────┤
│                                  │
│  Java Application (Spring Boot)  │
│  ├─ Spring Security              │
│  ├─ Thymeleaf Templates          │
│  ├─ StatsService (Dynamic)       │
│  └─ Admin Panel                  │
│                                  │
│  H2 Database                     │
│  └─ /railway/volumes/data/       │
│                                  │
│  File Storage                    │
│  ├─ /railway/volumes/uploads/    │
│  └─ /railway/volumes/data/       │
│                                  │
└─────────────────────────────────┘
```

## Environment Variables Explanation

| Variable | Value | Purpose |
|----------|-------|---------|
| `SPRING_PROFILES_ACTIVE` | `production` | Use production config |
| `SERVER_PORT` | `8080` | Listen on 8080 (Railway handles routing) |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Auto-create/update database schema |

## Security Notes

### Change Admin Password
1. Deploy to Railway
2. Login to admin panel
3. Go to Users
4. Change admin password from `ShaadiAdmin@2026`

### Disable H2 Console (Optional)
In `application-production.properties`:
```
spring.h2.console.enabled=false
```

### Enable HTTPS
Railway provides free HTTPS by default - no configuration needed!

## Scaling

H2 works great for:
- ✅ Single instance deployment
- ✅ Small to medium traffic
- ✅ Development/staging

For high-traffic production, consider:
- ❌ Switch to PostgreSQL on Railway
- ❌ Use Railway PostgreSQL service
- ❌ Update datasource URL

## Quick Commands

```bash
# View Railway logs live
railway logs --follow

# Check app status
railway status

# Connect to H2 (if needed)
# Use H2 web console: http://app-url/h2-console

# Redeploy (after changes)
git push origin main
# Railway auto-deploys
```

## Files Changed

```
✅ railway.json (updated)
✅ Procfile (updated)
✅ application-production.properties (created)
❌ Dockerfile (delete this)
✅ All source code (unchanged)
```

## Next Steps

1. ✅ Delete Dockerfile
2. ✅ Commit changes
3. ✅ Push to GitHub
4. ✅ Watch Railway auto-deploy
5. ✅ Access your app!

---

**Status**: ✅ Ready for Railway Deployment with H2!

No MySQL needed. Everything is self-contained. Your stats are dynamic, real, and persistent! 🚀
