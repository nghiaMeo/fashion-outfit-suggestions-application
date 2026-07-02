# API Documentation Guide

## 📖 Accessing Swagger UI

### Development
```
URL: http://localhost:8080/api/swagger-ui.html
JSON: http://localhost:8080/api/v3/api-docs
YAML: http://localhost:8080/api/v3/api-docs.yaml
```

### Production
```
URL: https://your-domain.com/api/swagger-ui.html
```

---

## 🔐 Authentication in Swagger

1. **Register/Login** first to get JWT token
2. Click **"Authorize"** button at top-right
3. Enter token:
   ```
   Bearer <your-jwt-token>
   ```
4. Click **"Authorize"** in modal
5. Now all requests will include the token

---

## 📚 API Endpoint Categories

### 1. Authentication (Public)
```
POST   /api/auth/register           - Register new user
POST   /api/auth/login              - Login with credentials
POST   /api/auth/oauth2/google      - Google OAuth2 login
POST   /api/auth/refresh-token      - Get new access token
POST   /api/auth/logout             - Logout (requires auth)
POST   /api/auth/forgot-password    - Request password reset
POST   /api/auth/reset-password     - Reset password with OTP
```

### 2. Wardrobe Items (Protected)
```
GET    /api/items/all-items         - Get all items
GET    /api/items/search-items      - Search with filters
GET    /api/items/statistics        - Wardrobe stats
POST   /api/items/add               - Add new item (multipart)
PUT    /api/items/{id}              - Update item
DELETE /api/items/delete-item/{id}  - Soft delete
GET    /api/items/trash             - Get deleted items
POST   /api/items/restore/{id}      - Restore item
DELETE /api/items/hard-delete/{id}  - Permanent delete
```

### 3. Outfits (Protected)
```
GET    /api/outfits/all-outfit      - Get all outfits
GET    /api/outfits/{id}            - Get details
GET    /api/outfits/public/{id}     - Get public outfit
GET    /api/outfits/search          - Search outfits
GET    /api/outfits/home-feed       - Get feed
POST   /api/outfits/add             - Create outfit
PUT    /api/outfits/{id}            - Update outfit
DELETE /api/outfits/{id}            - Delete outfit
PATCH  /api/outfits/{id}/favorite   - Toggle favorite
PATCH  /api/outfits/{id}/visibility - Toggle visibility
POST   /api/outfits/{id}/like       - Like outfit
```

### 4. Social - Friends (Protected)
```
GET    /api/friends/list            - Get friends
GET    /api/friends/pending         - Get pending requests
POST   /api/friends/request/{id}    - Send request
POST   /api/friends/accept/{id}     - Accept request
DELETE /api/friends/decline/{id}    - Decline request
DELETE /api/friends/{id}            - Remove friend
```

### 5. Social - Chat (Protected)
```
GET    /api/chat/conversations      - Get all conversations
GET    /api/chat/{conversationId}/messages - Get messages
POST   /api/chat/send               - Send message
POST   /api/chat/create/{friendId}  - Start conversation
DELETE /api/chat/{messageId}        - Delete message
```

### 6. Notifications (Protected)
```
GET    /api/notifications           - Get all
GET    /api/notifications/unread    - Get unread count
POST   /api/notifications/{id}/read - Mark as read
POST   /api/notifications/read-all  - Mark all as read
DELETE /api/notifications/{id}      - Delete notification
```

### 7. User Profile (Protected)
```
GET    /api/users/{id}              - Get user profile
GET    /api/users/me                - Get current user
PUT    /api/users/me                - Update profile
POST   /api/users/avatar            - Upload avatar
```

---

## 🧪 Testing Workflows

### 1. Register & Login
```
1. POST /api/auth/register
   {
     "email": "test@example.com",
     "username": "testuser",
     "displayName": "Test User",
     "password": "password123"
   }

2. Copy accessToken from response

3. Click "Authorize" button
   Enter: Bearer <token>

4. Now test protected endpoints
```

### 2. Create Wardrobe Item
```
1. POST /api/items/add (multipart/form-data)
   data: {"name": "Blue Shirt", "itemType": "TOP", "color": "Blue"}
   file: <image>

2. Copy item ID from response
```

### 3. Create Outfit
```
1. GET /api/items/all-items
   Copy several item IDs

2. POST /api/outfits/add
   {
     "name": "Casual Friday",
     "description": "Office outfit",
     "occasion": "Office",
     "season": "Summer",
     "itemIds": ["item-id-1", "item-id-2", "item-id-3"]
   }
```

### 4. Send Friend Request
```
1. GET /api/users/{userId}  (get friend's ID)

2. POST /api/friends/request/{userId}
   {}
```

---

## 🔍 Response Format

All endpoints return consistent JSON format:

### Success (200, 201)
```json
{
  "success": true,
  "code": 200,
  "message": "Success",
  "result": { /* data */ }
}
```

### Error (4xx, 5xx)
```json
{
  "success": false,
  "code": 400,
  "message": "Error description"
}
```

---

## ⚙️ Configuration

### Disable Swagger (Production)

In `application-prod.yml`:
```yaml
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false
```

### Customize Title/Description

In `OpenApiConfig.java`:
```java
.title("Your API Title")
.version("2.0.0")
.description("Your description")
```

---

## 📱 Mobile / External Client Integration

### Get OpenAPI JSON
```bash
curl http://localhost:8080/api/v3/api-docs
```

### Generate Client SDK (TypeScript, Python, Java, etc.)
```bash
# Using OpenAPI Generator
npx @openapitools/openapi-generator-cli generate \
  -i http://localhost:8080/api/v3/api-docs \
  -g typescript-fetch \
  -o ./generated-client
```

---

## 🐛 Troubleshooting

### Swagger not showing
```bash
# Check if enabled in application.yml
echo $SPRING_PROFILE  # Should be dev (not prod)

# Restart application
./gradlew bootRun
```

### Authorization not working
```bash
# 1. Make sure JWT_SECRET_KEY is set in .env
# 2. Verify token format: Bearer <token>
# 3. Check token expiration
```

### Endpoints not appearing
```bash
# Make sure controller has @Tag annotation
# Make sure methods have @Operation annotation
# Restart application
```

---

## 📚 Resources

- [SpringDoc OpenAPI Official Docs](https://springdoc.org/)
- [OpenAPI 3.0 Specification](https://swagger.io/specification/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)
- [OpenAPI Generator](https://openapi-generator.tech/)

---

**Last Updated:** 2026-07-02  
**Version:** 1.0.0
