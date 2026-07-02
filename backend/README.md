# Fashion Outfit Suggestions - Backend Service

## 🎯 Project Overview

A Spring Boot microservice backend for a fashion outfit suggestion application. Users can:
- Build and manage their digital wardrobe
- Create outfit combinations
- Share with friends
- Get suggestions based on style preferences
- Interact via real-time chat
- Receive notifications

---

## 🚀 Quick Start

### Prerequisites
- **Java 21+** ([Download](https://www.oracle.com/java/technologies/downloads/#java21))
- **PostgreSQL 12+** ([Download](https://www.postgresql.org/download/))
- **Redis 6+** ([Download](https://redis.io/download))
- **Docker & Docker Compose** (optional, recommended)
- **Gradle 8.10+** (included via gradlew)

### Installation

```bash
# 1. Clone and navigate
git clone https://github.com/nghiaMeo/fashion-outfit-suggestions-application.git
cd fashion-outfit-suggestions-application/backend

# 2. Setup environment
cp .env.example .env
# Edit .env with your configuration

# 3. Start services (using Docker)
docker-compose up -d

# 4. Install dependencies & run application
./gradlew bootRun
```

✅ **Application running at:** `http://localhost:8080/api`  
📖 **For detailed setup:** See [SETUP.md](../SETUP.md)  
📚 **API Documentation:** `http://localhost:8080/api/swagger-ui.html`

---

## 📁 Project Structure

```
src/main/java/com/example/
├── auth/
│   ├── controller/          # AuthController - Authentication endpoints
│   ├── service/             # AuthService, JwtService, OAuth2Service
│   ├── dto/                 # LoginRequest, AuthResponse, etc.
│   ├── entity/              # User, RefreshToken, PasswordResetOtp
│   └── repository/          # Database queries
│
├── wardrobe/
│   ├── controller/          # ItemController, OutfitController
│   ├── service/             # ItemService, OutfitService
│   ├── dto/                 # ItemRequest, OutfitResponse, etc.
│   ├── entity/              # WardrobeItem, Outfit, OutfitItem
│   └── repository/          # ItemRepository, OutfitRepository
│
├── social/
│   ├── controller/          # FriendshipController, ChatController
│   ├── service/             # FriendshipService, ChatService
│   ├── dto/                 # FriendRequest, MessageRequest, etc.
│   ├── entity/              # Friendship, ChatConversation, Message
│   └── repository/          # Persistence layer
│
├── notification/
│   ├── controller/          # NotificationController
│   ├── service/             # NotificationService, FirebaseService
│   ├── entity/              # Notification entity
│   └── repository/          # NotificationRepository
│
├── user/
│   ├── controller/          # UserController - Profile management
│   ├── service/             # UserService, CloudinaryService
│   ├── dto/                 # UserResponse, UserUpdateRequest
│   ├── entity/              # User, UserPreference
│   └── repository/          # UserRepository, UserPreferenceRepository
│
└── common/
    ├── config/              # SecurityConfig, OpenApiConfig, etc.
    ├── exception/           # GlobalExceptionHandler, ErrorCode enum
    ├── security/            # JwtAuthenticationFilter, RateLimitFilter
    ├── dto/                 # ApiResponse, ErrorResponse
    └── util/                # Utility classes

src/main/resources/
├── application.yml          # Base configuration
├── application-dev.yml      # Development profile
├── application-prod.yml     # Production profile
├── application-swagger.yml  # Swagger configuration
└── db/migration/
    └── V1__initial_schema.sql  # Flyway database migration
```

---

## 🔑 Key Features

### 🔐 Authentication & Security
- ✅ **JWT-based authentication** (HS512, 24hr expiry)
- ✅ **OAuth2 Google login** (auto account creation)
- ✅ **Password reset with OTP** (email-based)
- ✅ **Rate limiting** (100 req/min per IP)
- ✅ **Token blacklisting** on logout (Redis-backed)
- ✅ **Secure password hashing** (BCrypt 10 rounds)
- ✅ **CORS enabled** for web/mobile clients

### 👕 Wardrobe Management
- ✅ **Add/Edit/Delete** wardrobe items
- ✅ **Soft delete** with trash recovery
- ✅ **Item categorization** (Top, Bottom, Dress, Shoes, etc.)
- ✅ **Image uploads** via Cloudinary
- ✅ **Search & filter** items (by name, type, color, tags)
- ✅ **Wardrobe statistics** (total items, by type, etc.)
- ✅ **Condition tracking** (New, Like New, Good, Fair, Poor)

### 🎭 Outfit Creation & Management
- ✅ **Combine items** into outfits
- ✅ **Rate and favorite** outfits
- ✅ **Privacy settings** (public/private)
- ✅ **Occasion tagging** (Office, Casual, Formal, Party, etc.)
- ✅ **Season tagging** (Spring, Summer, Fall, Winter)
- ✅ **Home feed** with personalized recommendations
- ✅ **Outfit search** and filtering

### 👥 Social Features
- ✅ **Friend management** (requests, accept/decline, remove)
- ✅ **Real-time chat** (WebSocket)
- ✅ **Outfit sharing** in messages
- ✅ **Friend profiles** & statistics
- ✅ **Pending friend requests** tracking

### 🔔 Notifications
- ✅ **Real-time notifications** (WebSocket)
- ✅ **Firebase push notifications** (mobile)
- ✅ **Mark as read/unread**
- ✅ **Notification types** (friend request, new message, outfit liked, etc.)
- ✅ **Notification history** & management

---

## 🛠 Technology Stack

| Layer | Technology | Version | Purpose |
|-------|-----------|---------|----------|
| **Framework** | Spring Boot | 3.3.5 | Web framework & dependency injection |
| **Language** | Java | 21 | Programming language |
| **Build Tool** | Gradle | 8.10 | Project build automation |
| **Database** | PostgreSQL | 17 | Primary relational database |
| **Cache** | Redis + Caffeine | 8.4.3 | Distributed & local caching |
| **Security** | JWT + OAuth2 | JJWT 0.12.5 | Authentication & authorization |
| **API Docs** | SpringDoc OpenAPI | 2.3.0 | Interactive API documentation |
| **File Storage** | Cloudinary | v1.39.0 | Cloud image hosting & optimization |
| **Notifications** | Firebase Admin SDK | 9.9.0 | Push notifications & real-time |
| **Mapping** | MapStruct | 1.5.5 | DTO ↔ Entity mapping |
| **ORM** | Hibernate JPA | 6.x | Object-relational mapping |
| **Validation** | Jakarta Validation | 3.x | Bean validation |
| **Logging** | SLF4J + Logback | Latest | Application logging |
| **DB Migrations** | Flyway | Latest | Database schema versioning |
| **Container** | Docker | Latest | Containerization & deployment |

---

## 📚 API Documentation

### 🌐 Access Swagger UI
```
Local:      http://localhost:8080/api/swagger-ui.html
JSON:       http://localhost:8080/api/v3/api-docs
YAML:       http://localhost:8080/api/v3/api-docs.yaml
```

### 🔐 Authentication in Swagger
1. Login to get JWT token
2. Click **"Authorize"** button (top-right)
3. Enter: `Bearer <your-jwt-token>`
4. Click **"Authorize"** to apply to all requests

### 📋 API Endpoints Summary

```
🔐 Authentication (7 endpoints)
  POST   /api/auth/register
  POST   /api/auth/login
  POST   /api/auth/oauth2/google
  POST   /api/auth/refresh-token
  POST   /api/auth/logout
  POST   /api/auth/forgot-password
  POST   /api/auth/reset-password

👕 Wardrobe Items (9 endpoints)
  GET    /api/items/all-items
  GET    /api/items/search-items
  GET    /api/items/statistics
  POST   /api/items/add
  PUT    /api/items/{id}
  DELETE /api/items/delete-item/{id}
  GET    /api/items/trash
  POST   /api/items/restore/{id}
  DELETE /api/items/hard-delete/{id}

🎭 Outfits (10 endpoints)
  GET    /api/outfits/all-outfit
  GET    /api/outfits/{id}
  GET    /api/outfits/public/{id}
  GET    /api/outfits/search
  GET    /api/outfits/home-feed
  POST   /api/outfits/add
  PUT    /api/outfits/{id}
  DELETE /api/outfits/{id}
  PATCH  /api/outfits/{id}/favorite
  PATCH  /api/outfits/{id}/visibility
  POST   /api/outfits/{id}/like

👥 Social - Friends (6 endpoints)
  GET    /api/friends/list
  GET    /api/friends/pending
  POST   /api/friends/request/{id}
  POST   /api/friends/accept/{id}
  DELETE /api/friends/decline/{id}
  DELETE /api/friends/{id}

💬 Social - Chat (4 endpoints)
  GET    /api/chat/conversations
  GET    /api/chat/{conversationId}/messages
  POST   /api/chat/send
  POST   /api/chat/create/{friendId}

🔔 Notifications (5 endpoints)
  GET    /api/notifications
  GET    /api/notifications/unread
  POST   /api/notifications/{id}/read
  POST   /api/notifications/read-all
  DELETE /api/notifications/{id}

👤 User Profile (4 endpoints)
  GET    /api/users/{id}
  GET    /api/users/me
  PUT    /api/users/me
  POST   /api/users/avatar
```

📄 **Full details:** See [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

---

## 🔐 Security Configuration

### JWT Configuration
```yaml
Algorithm:              HS512
Access Token Expiry:    24 hours
Refresh Token Expiry:   7 days
Token Storage:          Redis (blacklisting)
Secret Key Length:      Minimum 32 characters
```

### CORS Policy
```yaml
Allowed Origins:   All (*) - configurable per environment
Allowed Methods:   GET, POST, PUT, DELETE, PATCH, OPTIONS
Allowed Headers:   All (*)
Credentials:       Enabled
```

### Rate Limiting
```yaml
Max Requests:  100 per minute
Scope:         Per IP address
Response:      429 Too Many Requests
```

### Public Endpoints (No Auth Required)
```
GET    /api/outfits/public/**
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/oauth2/**
POST   /api/auth/refresh-token
POST   /api/auth/forgot-password
POST   /api/auth/reset-password
```

---

## 🗄️ Database Schema

### Core Tables (15 total)

**User Management:**
- `users` - User accounts with profile info
- `user_preferences` - User style preferences
- `refresh_tokens` - Token management
- `password_reset_otp` - OTP for password reset

**Wardrobe:**
- `wardrobe_items` - Clothing items (with soft delete)
- `outfits` - Outfit combinations
- `outfit_items` - Junction table (many-to-many)
- `outfit_likes` - Users who liked outfits

**Social:**
- `friendships` - Friend relationships (pending/accepted)
- `chat_conversations` - Chat rooms
- `conversation_members` - Members in conversations
- `messages` - Chat messages

**Notifications:**
- `notifications` - User notifications

**Indexes:** 30+ indexes for optimal query performance

📊 **View full schema:** `src/main/resources/db/migration/V1__initial_schema.sql`

---

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests AuthServiceTest

# Run with coverage report
./gradlew test jacocoTestReport
# Report: build/reports/jacoco/test/html/index.html

# Run in watch mode
./gradlew test --continuous
```

---

## 📦 Building & Deployment

### Build JAR
```bash
./gradlew clean bootJar
# Output: build/libs/fashion-outfit-service-1.0.0.jar
```

### Build Docker Image
```bash
# Build
docker build -t fashion-outfit-service:1.0.0 .

# Run
docker run -d \
  -p 8080:8080 \
  --env-file .env \
  fashion-outfit-service:1.0.0
```

### Run in Production
```bash
java -jar fashion-outfit-service-1.0.0.jar \
  --spring.profiles.active=prod \
  --server.port=8080 \
  --logging.level.root=INFO
```

### Docker Compose (Development)
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f api

# Stop services
docker-compose down
```

---

## 🔧 Configuration Files

| File | Environment | Purpose |
|------|-------------|----------|
| `application.yml` | All | Base configuration (database, JWT, mail, etc.) |
| `application-dev.yml` | Development | Flyway disabled, DEBUG logging, show SQL |
| `application-prod.yml` | Production | Flyway enabled, INFO logging, Swagger disabled |
| `application-swagger.yml` | All | Swagger UI customization |
| `.env.example` | Template | Environment variables template |
| `build.gradle` | Build | Gradle dependencies & build tasks |
| `docker-compose.yml` | Docker | PostgreSQL, Redis services |

---

## 📝 Environment Variables

See `.env.example` for complete list:

```env
# Database
DB_URL=jdbc:postgresql://localhost:5432/fashion_db
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Application
PORT=8080
SPRING_PROFILE=dev

# JWT (min 32 characters)
JWT_SECRET_KEY=your-secret-key-min-32-chars

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# External APIs
GOOGLE_CLIENT_ID=xxx
CLOUDINARY_NAME=xxx
CLOUDINARY_API_KEY=xxx
FIREBASE_PROJECT_ID=xxx

# Email
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=app-password
```

---

## 🐛 Troubleshooting

### Database Connection Failed
```bash
# Check PostgreSQL is running
docker-compose ps postgres

# Or verify connection
psql -U postgres -h localhost -d fashion_db
```

### Redis Connection Failed
```bash
# Check Redis is running
redis-cli ping

# Or check container
docker-compose ps redis
```

### Port 8080 Already in Use
```bash
# Change PORT in .env
PORT=8081

# Or kill process
lsof -ti:8080 | xargs kill -9
```

### Swagger UI Not Loading
```bash
# Verify Swagger is enabled
echo $SPRING_PROFILE  # Should be 'dev' not 'prod'

# Check in application.yml
springdoc:
  swagger-ui:
    enabled: true
```

### JWT Token Validation Failed
```bash
# 1. Check secret key length (min 32)
echo $JWT_SECRET_KEY | wc -c

# 2. Verify token format
# Header: Authorization: Bearer <token>

# 3. Check token expiration
# Access: 24 hours, Refresh: 7 days
```

### Gradle Build Fails
```bash
# Clear cache and rebuild
./gradlew clean build --refresh-dependencies

# Check Java version
java -version  # Should be 21+
```

---

## 📚 Documentation

- 📖 **[Setup Guide](../SETUP.md)** - Detailed installation & setup
- 📄 **[API Documentation](API_DOCUMENTATION.md)** - Complete API guide
- 📋 **[API Collection](api_collection.md)** - Postman collection
- 🏗️ **[System Architecture](he_thong_va_luong_du_lieu.md)** - System design
- 🔄 **[Database Schema](src/main/resources/db/migration/V1__initial_schema.sql)** - SQL schema
- 🌐 **[Swagger UI](http://localhost:8080/api/swagger-ui.html)** - Interactive docs (when running)

---

## 👥 Contributing

1. **Create feature branch:**
   ```bash
   git checkout -b feature/amazing-feature
   ```

2. **Make changes and commit:**
   ```bash
   git commit -m 'Add amazing feature'
   ```

3. **Push to branch:**
   ```bash
   git push origin feature/amazing-feature
   ```

4. **Open Pull Request** on GitHub

---

## 📄 License

This project is licensed under the **MIT License** - see LICENSE file for details.

---

## 📞 Support & Contact

For issues, questions, or suggestions:

- 🐛 **Report Issue:** [GitHub Issues](https://github.com/nghiaMeo/fashion-outfit-suggestions-application/issues)
- 💬 **Discussions:** [GitHub Discussions](https://github.com/nghiaMeo/fashion-outfit-suggestions-application/discussions)
- 📧 **Email:** support@example.com
- 🌐 **Repository:** [GitHub](https://github.com/nghiaMeo/fashion-outfit-suggestions-application)

---

## 🎯 Quick Links

| Link | URL |
|------|-----|
| Main Repository | https://github.com/nghiaMeo/fashion-outfit-suggestions-application |
| API Swagger UI | http://localhost:8080/api/swagger-ui.html |
| PostgreSQL Setup | https://www.postgresql.org/ |
| Redis Setup | https://redis.io/ |
| Docker Setup | https://www.docker.com/ |
| Java Download | https://www.oracle.com/java/ |

---

## 📊 Project Statistics

- **Total Endpoints:** 45+
- **Database Tables:** 15
- **API Versions:** 1.0
- **Java Version:** 21+
- **Framework:** Spring Boot 3.3.5
- **Build Tool:** Gradle 8.10
- **Documentation:** 100% covered

---

**Last Updated:** 2026-07-02  
**Version:** 1.0.0  
**Status:** ✅ Production Ready  
**Maintainer:** [nghiaMeo](https://github.com/nghiaMeo)
