# Fashion Outfit Suggestions - Backend Service

## Project Overview

A Spring Boot microservice backend for a fashion outfit suggestion application. Users can:
- Build and manage their digital wardrobe
- Create outfit combinations
- Share with friends
- Get suggestions based on weather and style preferences
- Interact via real-time chat
- Receive notifications

---

##  Quick Start

### Prerequisites
- Java 21+
- PostgreSQL 12+
- Redis 6+
- Docker & Docker Compose (optional)

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

# 4. Run application
./gradlew bootRun
```

 **For detailed setup:** See [SETUP.md](../SETUP.md)

---

##  Project Structure

```
src/main/java/com/example/
├── auth/
│   ├── controller/      # Authentication endpoints
│   ├── service/         # Auth logic (JWT, OAuth2, etc.)
│   ├── dto/             # Request/Response DTOs
│   ├── entity/          # User, RefreshToken entities
│   └── repository/      # Database queries
│
├── wardrobe/
│   ├── controller/      # Item & Outfit endpoints
│   ├── service/         # Business logic
│   ├── dto/
│   ├── entity/          # WardrobeItem, Outfit entities
│   └── repository/
│
├── social/
│   ├── controller/      # Friendship & Chat endpoints
│   ├── service/         # Social features logic
│   ├── dto/
│   ├── entity/          # Friendship, ChatConversation, Message
│   └── repository/
│
├── notification/
│   ├── controller/      # Notification endpoints
│   ├── service/         # Notification delivery
│   ├── entity/          # Notification entity
│   └── repository/
│
├── user/
│   ├── controller/      # User profile endpoints
│   ├── service/         # User management
│   ├── dto/
│   ├── entity/          # User, UserPreference entities
│   └── repository/
│
└── common/
    ├── config/          # Spring configurations
    ├── exception/       # Error handling
    ├── security/        # JWT, Security filters
    ├── dto/             # Common DTOs
    └── util/            # Utilities
```

---

##  Key Features

### Authentication & Security
- ✅ JWT-based authentication
- ✅ OAuth2 Google login
- ✅ Password reset with OTP
- ✅ Rate limiting
- ✅ Token blacklisting on logout
- ✅ Secure password hashing (BCrypt 10 rounds)

### Wardrobe Management
- ✅ Add/Edit/Delete wardrobe items
- ✅ Soft delete with trash recovery
- ✅ Item categorization (Top, Bottom, Dress, etc.)
- ✅ Image uploads via Cloudinary
- ✅ Search & filter items
- ✅ Wardrobe statistics

### Outfit Creation
- ✅ Combine items into outfits
- ✅ Rate and favorite outfits
- ✅ Privacy settings (public/private)
- ✅ Occasion & season tagging
- ✅ Home feed with recommendations

### Social Features
- ✅ Friend requests (pending/accepted)
- ✅ Real-time chat (WebSocket)
- ✅ Outfit sharing in messages
- ✅ Friend profiles & statistics

### Notifications
- ✅ Real-time notifications (WebSocket)
- ✅ Firebase push notifications
- ✅ Mark as read/unread
- ✅ Notification types (friend request, new message, etc.)

---

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|----------|
| Framework | Spring Boot | 3.3.5 |
| Language | Java | 21 |
| Build | Gradle | 8.10 |
| Database | PostgreSQL | 17 |
| Cache | Redis + Caffeine | 8.4.3 |
| Security | JWT + OAuth2 | JJWT 0.12.5 |
| File Storage | Cloudinary | v1.39.0 |
| Notifications | Firebase Admin | 9.9.0 |
| Mapping | MapStruct | 1.5.5 |
| ORM | Hibernate JPA | 6.x |
| Validation | Jakarta Validation | 3.x |
| Logging | SLF4J + Logback | Latest |
| Container | Docker | Latest |

---

## API Endpoints

### Authentication
```
POST   /api/auth/register           Register new user
POST   /api/auth/login              Login with email/password
POST   /api/auth/oauth2/google      Login with Google
POST   /api/auth/refresh-token      Refresh access token
POST   /api/auth/forgot-password    Request password reset
POST   /api/auth/reset-password     Reset password with OTP
POST   /api/auth/logout             Logout (blacklist token)
```

### Wardrobe Items
```
GET    /api/items/all-items         Get all items
GET    /api/items/search-items      Search items with filters
GET    /api/items/statistics        Get wardrobe statistics
POST   /api/items/add               Add new item (with image)
PUT    /api/items/{id}              Update item
DELETE /api/items/delete-item/{id}  Soft delete item
GET    /api/items/trash             Get deleted items
POST   /api/items/restore/{id}      Restore deleted item
DELETE /api/items/hard-delete/{id}  Permanently delete item
```

### Outfits
```
GET    /api/outfits/all-outfit      Get all outfits
GET    /api/outfits/{id}            Get outfit details
GET    /api/outfits/public/{id}     Get public outfit
GET    /api/outfits/search          Search outfits
GET    /api/outfits/home-feed       Get home feed
POST   /api/outfits/add             Create outfit
PUT    /api/outfits/{id}            Update outfit
DELETE /api/outfits/{id}            Delete outfit
PATCH  /api/outfits/{id}/favorite   Toggle favorite
PATCH  /api/outfits/{id}/visibility Toggle visibility
POST   /api/outfits/{id}/like       Like outfit
```

### Friends
```
GET    /api/friends/list            Get friends list
GET    /api/friends/pending         Get pending requests
POST   /api/friends/request/{id}    Send friend request
POST   /api/friends/accept/{id}     Accept request
DELETE /api/friends/decline/{id}    Decline request
DELETE /api/friends/{id}            Remove friend
```

### Chat
```
GET    /api/chat/conversations      Get all conversations
GET    /api/chat/{conversationId}/messages  Get messages
POST   /api/chat/send               Send message
POST   /api/chat/create/{friendId}  Start conversation
```

### Notifications
```
GET    /api/notifications           Get all notifications
GET    /api/notifications/unread    Get unread count
POST   /api/notifications/{id}/read Mark as read
POST   /api/notifications/read-all  Mark all as read
```

### User Profile
```
GET    /api/users/{id}              Get user profile
GET    /api/users/me                Get current user
PUT    /api/users/me                Update profile
POST   /api/users/avatar            Upload avatar
```

📄 **Full API collection:** See `api_collection.md` or `fashion_outfit_bruno_collection.json`

---

##  Security

### JWT Configuration
- **Algorithm:** HS512
- **Access Token Expiry:** 24 hours
- **Refresh Token Expiry:** 7 days
- **Token Blacklisting:** Redis-backed

### CORS
- Allows: All origins, Methods (GET, POST, PUT, DELETE, PATCH, OPTIONS)
- Credentials: Enabled

### Rate Limiting
- Max 100 requests per minute per IP
- Returns 429 Too Many Requests

---

## ️ Database Schema

### Main Tables
- `users` - User accounts with OAuth2 support
- `wardrobe_items` - User's clothing items
- `outfits` - Outfit combinations
- `outfit_items` - Junction table (many-to-many)
- `friendships` - Friend relationships
- `chat_conversations` - Chat rooms
- `messages` - Chat messages
- `notifications` - User notifications
- `refresh_tokens` - Token management
- `password_reset_otp` - OTP for password reset

 **See Flyway migrations:** `src/main/resources/db/migration/`

---

##  Testing

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests AuthServiceTest

# Run with coverage
./gradlew test jacocoTestReport
```

---

##  Building & Deployment

### Build JAR
```bash
./gradlew bootJar
# Output: build/libs/fashion-outfit-service-1.0.0.jar
```

### Build Docker Image
```bash
docker build -t fashion-outfit-service:1.0.0 .
```

### Run in Production
```bash
java -jar fashion-outfit-service-1.0.0.jar \
  --spring.profiles.active=prod \
  --server.port=8080
```

---

##  Configuration Files

| File | Purpose |
|------|----------|
| `application.yml` | Base configuration |
| `application-dev.yml` | Development profile (Flyway disabled, debug logging) |
| `application-prod.yml` | Production profile (Flyway enabled, minimal logging) |
| `.env.example` | Environment variables template |
| `build.gradle` | Gradle dependencies & build config |
| `docker-compose.yml` | Docker services (PostgreSQL, Redis) |

---

##  Environment Variables

See `.env.example` for all available variables:

```env
# Database
DB_URL, DB_USERNAME, DB_PASSWORD

# JWT
JWT_SECRET_KEY, JWT_ACCESS_TOKEN_EXPIRATION, JWT_REFRESH_TOKEN_EXPIRATION

# Redis
REDIS_HOST, REDIS_PORT, REDIS_PASSWORD

# External APIs
GOOGLE_CLIENT_ID
CLOUDINARY_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET
FIREBASE_PROJECT_ID, FIREBASE_PRIVATE_KEY, FIREBASE_CLIENT_EMAIL

# Email
MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD
```

---

##  Troubleshooting

### Common Issues

1. **Database Connection Error**
   ```bash
   # Check PostgreSQL is running
   docker-compose ps postgres
   ```

2. **Redis Connection Error**
   ```bash
   # Check Redis is running
   redis-cli ping
   ```

3. **Port Already in Use**
   ```bash
   # Change PORT in .env
   PORT=8081
   ```

4. **JWT Token Validation Failed**
   - Check JWT_SECRET_KEY length (min 32 characters)
   - Verify token format in Authorization header

---

##  Documentation

-  [Setup Guide](../SETUP.md)
-  [API Collection](api_collection.md)
-  [System Architecture](he_thong_va_luong_du_lieu.md)
-  [Database Schema](src/main/resources/db/migration/V1__initial_schema.sql)

---

## 👥 Contributing

1. Create feature branch: `git checkout -b feature/amazing-feature`
2. Commit changes: `git commit -m 'Add amazing feature'`
3. Push to branch: `git push origin feature/amazing-feature`
4. Open Pull Request

---

##  License

This project is licensed under the MIT License.

---

##  Support

For issues and questions:
- 📧 Email: nghia181032@gmail.com
- 🐛 GitHub Issues: [Create Issue](https://github.com/nghiaMeo/fashion-outfit-suggestions-application/issues)
- 💬 Discussions: [GitHub Discussions](https://github.com/nghiaMeo/fashion-outfit-suggestions-application/discussions)

---

**Last Updated:** 2026-07-02  
**Version:** 1.0.0  
**Maintainer:** [nghiaMeo](https://github.com/nghiaMeo)
