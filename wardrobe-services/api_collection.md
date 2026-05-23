# API Collection — Fashion Outfit Suggestions Application

**Tổng cộng: 38 API endpoints** trên 5 services

---

##  Kiến trúc giao tiếp

```
                        ┌──────────────────┐
    Frontend ──────────▶│  API Gateway     │ port 8880
    (1 port duy nhất)   │  (Spring Cloud)  │
                        └────────┬─────────┘
                                 │ route theo URL path
              ┌──────────┬───────┼────────┬──────────┐
              ▼          ▼       ▼        ▼          ▼
         ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
         │  Auth  │ │Wardrobe│ │ Social │ │Notifi- │ │Eureka  │
         │  8881  │ │  8882  │ │  8883  │ │cation  │ │  8761  │
         │        │ │        │ │        │ │  8884  │ │        │
         └───┬────┘ └────────┘ └──┬─────┘ └──┬─────┘ └────────┘
             │                    │           │
             ◀────── OpenFeign ───┴───────────┘
             (giao tiếp nội bộ giữa services, KHÔNG qua Gateway)
```

- **API Gateway (port 8880)**: Frontend gọi tất cả API qua 1 cổng duy nhất
- **OpenFeign + Eureka**: Các service gọi nhau nội bộ (ví dụ: notification-service gọi auth-service lấy user info)
- **Eureka (port 8761)**: Service Discovery — giúp các service tìm nhau qua tên

---

##  Gateway Routes (port 8880)

| Route Pattern | → Service | Port trực tiếp |
|--------------|-----------|----------------|
| `/api/auth/**`, `/api/user/**` | auth-service | 8881 |
| `/api/outfits/**`, `/api/items/**` | wardrobe-service | 8882 |
| `/api/friendship/**`, `/api/chat/**`, `/api/social/outfits/**` | social-service | 8883 |
| `/api/notifications/**` | notification-service | 8884 |

> [!TIP]
> **Test qua Gateway**: `http://localhost:8880/api/auth/login`
> **Test trực tiếp**: `http://localhost:8881/api/auth/login`
> Cả hai đều hoạt động. Gateway cần Eureka đang chạy.

---

## 1. AUTH-SERVICE (port 8881)

### Auth — `/api/auth` — 8 endpoints

| # | Method | Path | Mô tả | Auth?  |
|---|--------|------|--------|--------|
| 1 | `POST` | `/api/auth/register` | Đăng ký tài khoản | X      |
| 2 | `POST` | `/api/auth/login` | Đăng nhập | X      |
| 3 | `POST` | `/api/auth/refresh-token` | Refresh access token | X      |
| 4 | `POST` | `/api/auth/oauth2/google` | Đăng nhập bằng Google | X      |
| 5 | `POST` | `/api/auth/logout` | Đăng xuất | Bearer |
| 6 | `PUT`  | `/api/auth/change-password` | Đổi mật khẩu | Bearer |
| 7 | `POST` | `/api/auth/forgot-password` | Gửi OTP quên mật khẩu | X      |
| 8 | `POST` | `/api/auth/reset-password` | Reset mật khẩu bằng OTP | X      |

#### Request Bodies

**1. POST /api/auth/register**
```json
{
  "email": "test@gmail.com",
  "username": "testuser",
  "password": "Test@1234"
}
```

**2. POST /api/auth/login**
```json
{
  "email": "test@gmail.com",
  "password": "Test@1234"
}
```
→ Response:
```json
{
  "result": {
    "accessToken": "eyJhbGci...",
    "refreshToken": "uuid-string"
  }
}
```

**3. POST /api/auth/refresh-token**
```json
{
  "refreshToken": "uuid-refresh-token-from-login"
}
```

**4. POST /api/auth/oauth2/google**
```json
{
  "token": "google-id-token-here"
}
```

**5. POST /api/auth/logout** — Header: `Authorization: Bearer <accessToken>`
```json
{
  "refreshToken": "uuid-refresh-token"
}
```

**6. PUT /api/auth/change-password**
```json
{
  "email": "test@gmail.com",
  "oldPassword": "Test@1234",
  "newPassword": "NewPass@5678"
}
```

**7. POST /api/auth/forgot-password**
```json
{
  "email": "test@gmail.com"
}
```

**8. POST /api/auth/reset-password**
```json
{
  "email": "test@gmail.com",
  "otp": "123456",
  "newPassword": "NewPass@5678"
}
```

---

### User — `/api/user` — 8 endpoints

| # | Method | Path | Mô tả | Auth?   |
|---|--------|------|--------|---------|
| 9 | `GET` | `/api/user/my-profile` | Profile hiện tại | Bearer  |
| 10 | `GET` | `/api/user/profile/{userId}` | Xem profile theo ID | Bearer  |
| 11 | `PUT` | `/api/user/profile` | Cập nhật profile | Bearer  |
| 12 | `GET` | `/api/user/search?query=xxx&currentUserId=uuid` | Tìm user |  Bearer |
| 13 | `POST` | `/api/user/profile/batch` | Lấy profiles hàng loạt |  Bearer |
| 14 | `GET` | `/api/user/suggest-candidates?currentUserId=uuid` | Gợi ý kết bạn |  Bearer |
| 15 | `POST` | `/api/user/presence?userId=uuid&isOnline=true` | Cập nhật online | Internal |
| 16 | `GET` | `/api/user/fcm-token/{userId}` | Lấy FCM token | Internal |

**11. PUT /api/user/profile**
```json
{
  "username": "newname",
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

**13. POST /api/user/profile/batch**
```json
["uuid-1", "uuid-2", "uuid-3"]
```

---

##  2. WARDROBE-SERVICE (port 8882)

### Items — `/api/items` — 8 endpoints

| # | Method | Path | Mô tả | Auth? |
|---|--------|------|--------|-------|
| 17 | `POST` | `/api/items/add` | Thêm item (multipart) | ✅ Bearer |
| 18 | `PUT` | `/api/items/{id}` | Cập nhật item | ✅ Bearer |
| 19 | `GET` | `/api/items/all-items` | Tất cả items | ✅ Bearer |
| 20 | `DELETE` | `/api/items/delete-item/{id}` | Xóa item (soft) | ✅ Bearer |
| 21 | `GET` | `/api/items/search-items` | Tìm kiếm items | ✅ Bearer |
| 22 | `GET` | `/api/items/statistics` | Thống kê wardrobe | ✅ Bearer |
| 23 | `POST` | `/api/items/restore/{id}` | Khôi phục từ thùng rác | ✅ Bearer |
| 24 | `GET` | `/api/items/trash` | Items trong thùng rác | ✅ Bearer |
| 25 | `DELETE` | `/api/items/hard-delete/{id}` | Xóa vĩnh viễn | ✅ Bearer |

**17. POST /api/items/add** — `Content-Type: multipart/form-data`
```
Key: data (JSON) →
{
  "name": "White T-Shirt",
  "type": "TOP",
  "color": "WHITE",
  "tag": "casual"
}
Key: file (File) → image.jpg
```

**18. PUT /api/items/{id}**
```json
{
  "name": "Updated T-Shirt",
  "type": "TOP",
  "color": "BLACK",
  "tag": "formal"
}
```

**21. GET /api/items/search-items**
```
?name=shirt&type=TOP&color=WHITE&tag=casual&page=1&size=10
```

### Outfits — `/api/outfits` — 9 endpoints

| # | Method | Path | Mô tả | Auth?  |
|---|--------|------|--------|--------|
| 26 | `POST` | `/api/outfits/add` | Tạo outfit | Bearer |
| 27 | `GET` | `/api/outfits/all-outfit` | Tất cả outfits | Bearer |
| 28 | `PATCH` | `/api/outfits/{id}/favorite` | Toggle yêu thích | Bearer |
| 29 | `GET` | `/api/outfits/{id}` | Chi tiết outfit | Bearer |
| 30 | `GET` | `/api/outfits/public/{id}` | Xem outfit công khai | x      |
| 31 | `DELETE` | `/api/outfits/{id}` | Xóa outfit | Bearer |
| 32 | `PUT` | `/api/outfits/{id}` | Cập nhật outfit | Bearer |
| 33 | `PATCH` | `/api/outfits/{id}/visibility` | Toggle public/private | Bearer |
| 34 | `GET` | `/api/outfits/search?name=&occasion=&isFavorite=` | Tìm kiếm | Bearer |
| 35 | `POST` | `/api/outfits/{id}/like` | Like outfit | Bearer |
| 36 | `GET` | `/api/outfits/home-feed` | Home feed công khai | Bearer |

**26. POST /api/outfits/add**
```json
{
  "name": "Summer Look",
  "occasion": "CASUAL",
  "itemIds": ["uuid-item-1", "uuid-item-2"]
}
```

---

##  3. SOCIAL-SERVICE (port 8883)

### Chat — `/api/chat` — 3 endpoints

| # | Method | Path | Mô tả | Auth? |
|---|--------|------|--------|-------|
| 37 | `GET` | `/api/chat/conversations` | Danh sách hội thoại |  Bearer |
| 38 | `GET` | `/api/chat/conversations/{id}/messages?page=0&size=20` | Lịch sử tin nhắn |  Bearer |
| 39 | `POST` | `/api/chat/send` | Gửi tin nhắn |  Bearer |

**39. POST /api/chat/send**
```json
{
  "receiverId": "uuid-of-receiver",
  "content": "Hello!"
}
```

### Friendship — `/api/friendship` — 7 endpoints

| # | Method | Path | Mô tả | Auth? |
|---|--------|------|--------|-------|
| 40 | `POST` | `/api/friendship/request/{receiverId}` | Gửi lời mời kết bạn |  Bearer |
| 41 | `POST` | `/api/friendship/accept/{friendshipId}` | Chấp nhận lời mời |  Bearer |
| 42 | `DELETE` | `/api/friendship/cancel/{friendshipId}` | Từ chối/hủy lời mời |  Bearer |
| 43 | `GET` | `/api/friendship/pending` | Lời mời đang chờ |  Bearer |
| 44 | `GET` | `/api/friendship/my-friends` | Danh sách bạn bè |  Bearer |
| 45 | `GET` | `/api/friendship/search-users?query=xxx` | Tìm kiếm user |  Bearer |
| 46 | `GET` | `/api/friendship/friend-ids` | Lấy friend IDs |  Bearer |

### Outfit Like — `/api/social/outfits` — 3 endpoints

| # | Method | Path | Mô tả | Auth? |
|---|--------|------|--------|-------|
| 47 | `POST` | `/api/social/outfits/{outfitId}/like?ownerId=uuid` | Like/Unlike outfit |  Bearer |
| 48 | `GET` | `/api/social/outfits/{outfitId}/like-status` | Trạng thái like |  Bearer |
| 49 | `POST` | `/api/social/outfits/likes-batch` | Like status hàng loạt |  Bearer |

**49. POST /api/social/outfits/likes-batch**
```json
["uuid-outfit-1", "uuid-outfit-2"]
```

---

##  4. NOTIFICATION-SERVICE (port 8884)

Base path: `/api/notifications` — 5 endpoints

| # | Method | Path | Mô tả | Auth? |
|---|--------|------|--------|-------|
| 50 | `GET` | `/api/notifications` | Danh sách thông báo |  Bearer |
| 51 | `GET` | `/api/notifications/unread-count` | Số chưa đọc |  Bearer |
| 52 | `PUT` | `/api/notifications/{notificationId}/read` | Đánh dấu đã đọc |  Bearer |
| 53 | `PUT` | `/api/notifications/read-all` | Đánh dấu tất cả đã đọc |  Bearer |
| 54 | `POST` | `/api/notifications/send` | Gửi thông báo |  Bearer |

**54. POST /api/notifications/send**
```json
{
  "recipientId": "uuid-of-receiver",
  "actorId": "uuid-of-sender",
  "type": "FRIEND_REQUEST",
  "targetId": "uuid-target",
  "content": "User X sent you a friend request"
}
```

---

##  Test Flows — Copy vào Postman/Bruno

### Flow 1: Auth cơ bản
```
1. POST http://localhost:8880/api/auth/register     → Đăng ký
2. POST http://localhost:8880/api/auth/login         → Lấy accessToken
3. GET  http://localhost:8880/api/user/my-profile    → Test token (Header: Bearer <token>)
```

### Flow 2: Wardrobe CRUD
```
4. POST   http://localhost:8880/api/items/add           → Thêm item (multipart)
5. GET    http://localhost:8880/api/items/all-items      → Xem items
6. POST   http://localhost:8880/api/outfits/add          → Tạo outfit
7. GET    http://localhost:8880/api/outfits/all-outfit   → Xem outfits
```

### Flow 3: Social (cần 2 tài khoản — 2 token khác nhau)
```
8.  POST http://localhost:8880/api/friendship/request/{user2Id}     → User1 kết bạn
9.  GET  http://localhost:8880/api/friendship/pending                → User2 xem lời mời
10. POST http://localhost:8880/api/friendship/accept/{friendshipId}  → User2 chấp nhận
11. POST http://localhost:8880/api/chat/send                        → User1 nhắn tin
12. GET  http://localhost:8880/api/chat/conversations                → Xem conversations
```

### Flow 4: Notifications
```
13. POST http://localhost:8880/api/notifications/send        → Gửi thông báo
14. GET  http://localhost:8880/api/notifications              → Xem thông báo
15. GET  http://localhost:8880/api/notifications/unread-count → Đếm chưa đọc
```

### Flow 5: Cross-service (kiểm tra giao tiếp giữa services)
```
16. POST http://localhost:8880/api/friendship/request/{user2Id}
    → social-service gọi nội bộ auth-service (qua Feign) để lấy user info
    → social-service gọi notification-service để gửi thông báo kết bạn

17. POST http://localhost:8880/api/social/outfits/{outfitId}/like?ownerId=uuid
    → social-service gọi notification-service thông báo có người like outfit
```

---

##  Thứ tự khởi động services

| Bước | Service | Port | Lý do |
|------|---------|------|-------|
| 1 | Discovery (Eureka) | 8761 | Service registry — phải chạy đầu tiên |
| 2 | API Gateway | 8880 | Route requests — cần Eureka |
| 3 | Auth Service | 8881 | Các service khác cần gọi auth |
| 4 | Wardrobe Service | 8882 | Độc lập |
| 5 | Social Service | 8883 | Cần auth + notification |
| 6 | Notification Service | 8884 | Cần auth |
