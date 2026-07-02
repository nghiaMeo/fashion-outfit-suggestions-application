# Fashion Outfit Suggestions Application - Setup Guide

## 📋 Table of Contents
- [Prerequisites](#prerequisites)
- [Environment Setup](#environment-setup)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [Docker Setup](#docker-setup)
- [API Documentation](#api-documentation)
- [Troubleshooting](#troubleshooting)

---

## Prerequisites

Before you begin, ensure you have installed:

- **Java 21 or higher**
  ```bash
  java -version
  ```

- **PostgreSQL 12 or higher**
  ```bash
  psql --version
  ```

- **Redis 6 or higher**
  ```bash
  redis-cli --version
  ```

- **Gradle 8.10 or higher** (optional, gradlew is included)
  ```bash
  ./gradlew --version
  ```

---

## Environment Setup

### 1. Copy Environment Template

```bash
cd backend
cp .env.example .env
```

### 2. Configure Environment Variables

Edit `backend/.env` and update the following:

```env
# Database
DB_URL=jdbc:postgresql://localhost:5432/fashion_db
DB_USERNAME=postgres
DB_PASSWORD=your_password

# JWT Secret (min 32 characters)
JWT_SECRET_KEY=your-super-secret-key-change-in-production

# OAuth2 Google
GOOGLE_CLIENT_ID=your-google-client-id

# Cloudinary (for image uploads)
CLOUDINARY_NAME=your-cloudinary-name
CLOUDINARY_API_KEY=your-api-key
CLOUDINARY_API_SECRET=your-api-secret

# Firebase (for push notifications)
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_PRIVATE_KEY=your-private-key
FIREBASE_CLIENT_EMAIL=your-email

# SMTP Email
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

---

## Database Setup

### Option 1: Using Docker (Recommended)

```bash
cd backend
docker-compose up -d postgres redis
```

This will:
- Create PostgreSQL container on port 5432
- Create Redis container on port 6379
- Automatically create the `fashion_db` database

### Option 2: Manual Setup

#### Create PostgreSQL Database

```bash
# Login to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE fashion_db;

# Exit
\q
```

#### Start Redis

```bash
# Using Homebrew (macOS)
brew services start redis

# Using Docker
docker run -d -p 6379:6379 redis:8.4.3-alpine3.22
```

---

## Running the Application

### Development Mode

```bash
cd backend

# Using Gradle wrapper
./gradlew bootRun

# Or
./gradlew bootRun --args='--spring.profiles.active=dev'
```

The application will start on `http://localhost:8080/api`

### Production Mode

```bash
cd backend

# Build JAR
./gradlew bootJar

# Run with production profile
java -jar build/libs/fashion-outfit-service-1.0.0.jar --spring.profiles.active=prod
```

---

## Docker Setup

### Using Docker Compose (All Services)

```bash
cd backend

# Start all services (API, PostgreSQL, Redis)
docker-compose up -d

# View logs
docker-compose logs -f api

# Stop all services
docker-compose down
```

### Using Docker for Application Only

```bash
# Build Docker image
docker build -t fashion-outfit-service:latest .

# Run with external PostgreSQL and Redis
docker run -d \
  -p 8080:8080 \
  --env-file .env \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/fashion_db \
  -e REDIS_HOST=host.docker.internal \
  fashion-outfit-service:latest
```

---

## API Documentation

### Health Check

```bash
curl http://localhost:8080/api/actuator/health
```

### Authentication Endpoints

#### Register
```bash
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "username": "username",
  "displayName": "User Name",
  "password": "password123"
}
```

#### Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

#### OAuth2 Google
```bash
POST /api/auth/oauth2/google
Content-Type: application/json

{
  "token": "google-id-token"
}
```

### Protected Endpoints (Require JWT Token)

Add Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

#### Get All Wardrobe Items
```bash
GET /api/items/all-items
```

#### Add Wardrobe Item
```bash
POST /api/items/add
Content-Type: multipart/form-data

- data: {"name": "Blue Shirt", "itemType": "TOP", "color": "Blue"}
- file: <image-file>
```

#### Get All Outfits
```bash
GET /api/outfits/all-outfit
```

#### Create Outfit
```bash
POST /api/outfits/add
Content-Type: application/json

{
  "name": "Casual Friday",
  "description": "Perfect for casual office day",
  "occasion": "Office",
  "season": "Summer",
  "itemIds": ["item-id-1", "item-id-2"]
}
```

---

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── auth/              # Authentication module
│   │   │   ├── wardrobe/          # Wardrobe & Outfit module
│   │   │   ├── social/            # Social features (friendship, chat)
│   │   │   ├── notification/      # Notifications
│   │   │   ├── user/              # User management
│   │   │   └── common/            # Common utilities, config, exceptions
│   │   ├── resources/
│   │   │   ├── application.yml
│   │   │   ├── application-dev.yml
│   │   │   ├── application-prod.yml
│   │   │   └── db/migration/      # Flyway migrations
│   ├── test/                       # Unit tests
├── gradle/
├── build.gradle
├── settings.gradle
├── Dockerfile
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## Technology Stack

| Layer | Technology |
|-------|------------|
| **Framework** | Spring Boot 3.3.5 |
| **Language** | Java 21 |
| **Build Tool** | Gradle 8.10 |
| **Database** | PostgreSQL |
| **Caching** | Redis + Caffeine |
| **Security** | Spring Security + JWT |
| **API Documentation** | SpringDoc OpenAPI |
| **File Upload** | Cloudinary |
| **Push Notifications** | Firebase Admin SDK |
| **Email** | Spring Mail (SMTP) |
| **Mapping** | MapStruct |
| **Logging** | SLF4J + Logback |
| **Container** | Docker & Docker Compose |

---

## Troubleshooting

### Issue: "Connection refused" when connecting to PostgreSQL

**Solution:**
```bash
# Check if PostgreSQL is running
psql -U postgres -c "SELECT version();"

# If not running, start it
docker-compose up -d postgres
```

### Issue: "Redis connection timeout"

**Solution:**
```bash
# Check if Redis is running
redis-cli ping

# If not running, start it
docker-compose up -d redis
```

### Issue: "JWT token expired" on every request

**Solution:**
Check `JWT_SECRET_KEY` in `.env` - should be at least 32 characters.

### Issue: Port 8080 already in use

**Solution:**
```bash
# Find and kill process using port 8080
lsof -ti:8080 | xargs kill -9

# Or change port in .env
PORT=8081
```

### Issue: Cloudinary image upload fails

**Solution:**
```bash
# Verify Cloudinary credentials in .env
echo $CLOUDINARY_NAME
echo $CLOUDINARY_API_KEY
echo $CLOUDINARY_API_SECRET
```

---

## Useful Commands

```bash
# Build project
./gradlew clean build

# Run tests
./gradlew test

# Format code
./gradlew spotlessApply

# Check dependencies
./gradlew dependencies

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=prod'

# Generate JAR
./gradlew bootJar
```

---

## Support

For issues or questions, please:
1. Check this setup guide
2. Review the API documentation in `backend/api_collection.md`
3. Check application logs in `backend/logs/app.log`
4. Create an issue on GitHub

---

**Last Updated:** 2026-07-02  
**Version:** 1.0.0
