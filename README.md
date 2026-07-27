# Shaadi Metrics - Wedding Planner Website

A modern, feature-rich wedding planning website built with **Spring Boot 4.1.0**, **Thymeleaf**, **Spring Security**, and **H2 Database**. Shaadi Metrics helps couples plan their wedding with ease through an intuitive admin dashboard and beautiful public-facing website.

## 🎯 Features

### Public Features
- **Home Page**: Beautiful landing page showcasing services
- **Gallery**: Wedding photography showcase with categories
- **Services**: Detailed list of wedding planning services
- **Testimonials**: Real couple reviews and success stories
- **Free Consultation**: Book a consultation with the planning team
- **Responsive Design**: Mobile-friendly interface for all devices

### Admin Features
- **Secure Login**: Role-based access control with Spring Security
- **Dashboard**: Overview of statistics and recent activity
- **Consultation Lead Management**: Track and manage consultation requests
- **Gallery Management**: Upload and organize wedding photos by category
- **Services Management**: Create and manage service offerings
- **Offers Management**: Create promotional offers
- **Testimonials Management**: Add and moderate couple testimonials
- **User Management**: Manage admin accounts

### Technical Features
- **Dynamic Statistics**: Real-time wedding stats (couples served, photos uploaded, etc.)
- **File Upload**: Secure image upload for gallery
- **Database Persistence**: H2 file-based database with automatic schema creation
- **Production Ready**: Optimized for Railway deployment
- **Secure Credentials**: Admin credentials with hashed passwords

## 🚀 Getting Started

### Prerequisites
- **Java 17+**
- **Gradle 8.14+** (or use the included wrapper)
- **Git**

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/RishabhSharma-tech/shadimetrics.git
   cd shadimetrics
   ```

2. **Build the project**
   ```bash
   ./gradlew clean build -x check -x test
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

4. **Access the application**
   - Public Site: [http://localhost:8080](http://localhost:8080)
   - Admin Login: [http://localhost:8080/admin](http://localhost:8080/admin)
   - **Default Credentials**:
     - Username: `admin`
     - Password: `ShaadiAdmin@2026`

## 📦 Project Structure

```
shadimetrics/
├── src/main/
│   ├── java/com/shaadimetrics/website/
│   │   ├── admin/              # Admin controllers
│   │   ├── config/             # Configuration (Security, File Storage)
│   │   ├── domain/             # Entity models
│   │   ├── repo/               # Data repositories
│   │   ├── security/           # Security configuration
│   │   ├── web/                # Web controllers & services
│   │   └── WebsiteApplication.java
│   └── resources/
│       ├── templates/          # Thymeleaf templates
│       ├── static/             # CSS, JS, images
│       └── application-*.properties
├── gradle/wrapper/
├── Procfile                    # Railway deployment config
├── railway.json                # Railway CI/CD config
├── build.gradle                # Gradle configuration
└── settings.gradle
```

## 🏗️ Architecture

### Models
- **AppUser**: Admin user accounts
- **GalleryImage**: Wedding photos with category
- **GalleryCategory**: Photo categories
- **ServiceItem**: Wedding services offered
- **Offer**: Promotional offers
- **Testimonial**: Couple reviews
- **ConsultationLead**: Consultation request

### Security
- **Spring Security 6**: Implements authentication & authorization
- **Password Encoding**: Uses BCrypt for password hashing
- **Admin Protection**: All admin endpoints require login
- **CSRF Protection**: Enabled by default

## 🗄️ Database

### H2 Database (Embedded)
- **Local**: In-memory for development
- **Production**: File-based at `/railway/volumes/data/shaadimetrics`
- **Auto DDL**: `spring.jpa.hibernate.ddl-auto=update`
- **Console**: Disabled in production for security

### Data Seeding
On first run, the application automatically seeds:
- Admin user account
- Sample wedding statistics
- Example services and offers

## 🔧 Configuration

### Application Properties
- `application.properties`: Default development config
- `application-production.properties`: Railway production config

### Key Settings
```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:h2:file:/path/to/db;MODE=MySQL

# File Upload
app.uploads.dir=/path/to/uploads
spring.servlet.multipart.max-file-size=15MB

# Admin Seeding
app.seed.admin-username=admin
app.seed.admin-password=ShaadiAdmin@2026
```

## 🚢 Deployment

### Railway Deployment

1. **Connect Repository**
   - Push code to GitHub
   - Connect GitHub repository to Railway

2. **Environment Variables** (Optional)
   - Set custom `app.uploads.dir` and `app.seed.admin-password` if needed

3. **Build & Deploy**
   - Railway automatically detects Gradle build
   - Runs: `./gradlew clean build -x check -x test`
   - Starts: `java -jar build/libs/website-*.jar --spring.profiles.active=production`

4. **Persistent Storage**
   - Railway volumes at `/railway/volumes/data/` automatically persist
   - Database and uploads survive deployments

### Local Docker Build
```bash
./gradlew clean build -x check -x test
docker build -t shadimetrics .
docker run -p 8080:8080 shadimetrics
```

## 📝 API Endpoints

### Public Routes
- `GET /` - Home page
- `GET /gallery` - Photo gallery
- `GET /services` - Services listing
- `GET /testimonials` - Testimonials
- `POST /consult` - Book consultation

### Admin Routes (Requires Login)
- `GET /admin` - Dashboard
- `GET /admin/gallery` - Manage gallery
- `POST /admin/gallery/upload` - Upload photos
- `GET /admin/services` - Manage services
- `GET /admin/offers` - Manage offers
- `GET /admin/testimonials` - Manage testimonials
- `GET /admin/leads` - Consultation leads
- `GET /admin/users` - User management

## 🛠️ Development

### Technologies Used
- **Framework**: Spring Boot 4.1.0
- **Web**: Spring MVC, Thymeleaf
- **Security**: Spring Security 6
- **Database**: Spring Data JPA, Hibernate, H2
- **Build**: Gradle 8.14
- **Java**: 17+

### Building from Source
```bash
# Clean build
./gradlew clean build

# Run tests
./gradlew test

# Check code quality
./gradlew check

# Generate JAR
./gradlew build -x test
```

### Common Tasks
```bash
# Run locally
./gradlew bootRun

# Watch tests
./gradlew test --continuous

# Build production JAR
./gradlew clean build -x check -x test
```

## 🔐 Security Considerations

1. **Change Default Credentials**: Update admin password in production
2. **HTTPS**: Enable SSL/TLS in production
3. **Database**: Use strong passwords if exposing H2 console
4. **File Upload**: Validate file types and sizes
5. **CORS**: Configure allowed origins if using as API
6. **Rate Limiting**: Consider adding rate limits to consultation endpoint

## 📱 Responsive Design

The website is fully responsive and tested on:
- Mobile (375px - 480px)
- Tablet (768px - 1024px)
- Desktop (1280px+)

Uses CSS Grid and Flexbox for modern layouts.

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests com.shaadimetrics.website.web.SiteControllerTest

# Generate coverage report
./gradlew jacocoTestReport
```

## 📚 File Upload

- **Location**: Configured via `app.uploads.dir`
- **Max Size**: 15MB per file
- **Types**: JPEG, PNG, WebP, GIF
- **Storage**: Local filesystem or Railway volumes

## 🐛 Troubleshooting

### Build Fails
```bash
# Clean Gradle cache
rm -rf .gradle build

# Rebuild
./gradlew clean build -x check -x test
```

### Database Issues
```bash
# H2 files location
ls -la /railway/volumes/data/

# Delete and recreate
rm -f /railway/volumes/data/shaadimetrics*
```

### Port Already in Use
```bash
# Change port in application.properties
server.port=8081
```

### File Upload Not Working
```bash
# Ensure uploads directory exists and is writable
mkdir -p /railway/volumes/uploads
chmod 755 /railway/volumes/uploads
```

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 Support & Contact

For questions, issues, or suggestions:
- **Email**: rishabh.sharma@tech5-sa.com
- **GitHub Issues**: [Open an Issue](https://github.com/RishabhSharma-tech/shadimetrics/issues)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Railway for seamless cloud deployment
- The wedding industry for inspiration

---

**Happy Planning! 💒✨**

Made with ❤️ by Rishabh Sharma
