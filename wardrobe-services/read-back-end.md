# Tài liệu Backend — Fashion Outfit Suggestions Application

## Mục lục
1. [Kiến trúc tổng quan](#1-kiến-trúc-tổng-quan)
2. [Tại sao Service có folder impl?](#2-tại-sao-service-có-folder-impl)
3. [Chiến lược lưu trữ Token (Redis)](#3-chiến-lược-lưu-trữ-token-redis)
4. [Luồng Đăng ký](#4-luồng-đăng-ký)
5. [Luồng Đăng nhập](#5-luồng-đăng-nhập)
6. [Luồng truy cập API bảo vệ (JWT Filter)](#6-luồng-truy-cập-api-bảo-vệ)
7. [Luồng làm mới Token](#7-luồng-làm-mới-token)
8. [Luồng Đăng xuất (Logout + Blacklist)](#8-luồng-đăng-xuất)
9. [Luồng OAuth2 Google](#9-luồng-oauth2-google)
10. [Hệ thống xử lý lỗi](#10-hệ-thống-xử-lý-lỗi)
11. [Bản đồ Debug](#11-bản-đồ-debug)

---

## 1. Kiến trúc tổng quan

```
Client (Flutter/Postman)
    ↓ HTTP Request
JwtAuthenticationFilter  ← Kiểm tra Bearer token + Blacklist
    ↓
SecurityConfig           ← Cho phép hoặc chặn request
    ↓
AuthController           ← Nhận request, gọi service
    ↓
Service (impl/)          ← Xử lý logic nghiệp vụ
    ↓
Repository               ← Truy vấn Database / Redis
    ↓
PostgreSQL + Redis       ← Lưu trữ dữ liệu
```

### Cấu trúc thư mục

```
src/main/java/com/example/wardrobeservices/
├── configuration/                       ← Cấu hình bảo mật (đổi tên từ config/)
│   ├── SecurityConfig.java              ← Cấu hình endpoint public/private, CSRF, session
│   ├── JwtAuthenticationFilter.java     ← Filter chặn mọi request, xác thực JWT + kiểm tra blacklist
│   └── JwtAuthenticationEntryPoint.java ← Trả JSON 401 khi chưa đăng nhập
├── controller/
│   └── AuthController.java             ← API: register, login, refresh-token, oauth2/google, logout
├── dto/
│   ├── request/
│   │   ├── UserCreationRequest.java     ← Đăng ký: email, username, displayName, password
│   │   ├── LoginRequest.java            ← Đăng nhập: email, password
│   │   ├── RefreshTokenRequest.java     ← Làm mới / đăng xuất: refreshToken
│   │   └── OAuth2Request.java           ← OAuth2: token
│   └── response/
│       ├── ApiResponse.java             ← Wrapper chuẩn {code, message, result}
│       ├── AuthResponse.java            ← Token + user info
│       ├── UserResponse.java            ← User info (không có password)
│       └── ErrorResponse.java           ← Lỗi {code, message, timestamp}
├── entity/                              ← JPA Entity (lưu trong PostgreSQL)
│   ├── User.java                        ← Bảng users
│   ├── UserPreference.java              ← Profile/sở thích user
│   └── enums/
│       ├── Role.java                    ← USER, ADMIN
│       └── AuthProvider.java            ← LOCAL, GOOGLE
├── model/                               ← Non-JPA model (lưu trong Redis)
│   └── RefreshToken.java                ← @RedisHash — Refresh token lưu Redis với TTL tự hết hạn
├── exception/
│   ├── ErrorCode.java                   ← Tập trung mọi mã lỗi
│   ├── AppException.java                ← Custom exception
│   └── GlobalExceptionHandler.java      ← Bắt tất cả lỗi, trả JSON
├── repository/
│   ├── UserRepository.java              ← JpaRepository (PostgreSQL)
│   ├── RefreshTokenRepository.java      ← CrudRepository (Redis)
│   └── ... (các repository khác)
└── service/
    ├── UserService.java                 ← Interface
    ├── AuthService.java                 ← Interface (login, refreshToken, logout)
    ├── JwtService.java                  ← Interface (generateToken, blacklist, validate)
    ├── RefreshTokenService.java         ← Interface
    ├── OAuth2Service.java               ← Interface
    └── impl/
        ├── UserServiceImpl.java
        ├── AuthServiceImpl.java
        ├── JwtServiceImpl.java          ← Sử dụng Redis cho blacklist
        ├── RefreshTokenServiceImpl.java ← Sử dụng Redis cho refresh token
        └── OAuth2ServiceImpl.java
```

### Tech Stack
- **Framework**: Spring Boot 4.0.5, Java 21
- **Database**: PostgreSQL (user data, entities)
- **Cache/Session**: Redis (refresh token, JWT blacklist)
- **Auth**: JWT (jjwt 0.12.5) + Spring Security
- **API Docs**: SpringDoc OpenAPI (Swagger UI)

---

## 2. Tại sao Service có folder impl?

Pattern **Interface + Implementation**, best practice chuẩn trong Spring Boot.

**Lý do 1: Dễ thay thế triển khai**
```java
// Interface — chỉ nói "TÔI CẦN GÌ"
public interface JwtService {
    String generateAccessToken(User user);
    void blacklistToken(String token);
}

// Đang dùng jjwt + Redis:
public class JwtServiceImpl implements JwtService { ... }
// Sau này muốn đổi sang auth0-jwt? Tạo class mới, không sửa Controller
```

**Lý do 2: Unit Testing** — Mock dễ dàng vì là Interface
```java
@Mock private JwtService jwtService;
```

**Lý do 3: SOLID** — Controller chỉ phụ thuộc Interface
```java
private final AuthService authService;   // ✅ Interface
// KHÔNG: private final AuthServiceImpl authService;  // ❌
```

Spring tự "nối dây": thấy `@Service` trên `AuthServiceImpl` → inject vào nơi cần `AuthService`.

---

## 3. Chiến lược lưu trữ Token (Redis)

Hệ thống sử dụng **Redis** cho cả 2 loại token:

### Access Token — JWT Stateless + Blacklist trong Redis
- Là JWT chứa thông tin (email, role, userId, exp)
- Backend chỉ giải mã bằng secret key → **không tốn query DB**
- Sống ngắn: **1 giờ**
- Khi logout → token bị đưa vào **Blacklist Redis** với key `jwt:blacklist:<token>` và TTL = thời gian còn lại

### Refresh Token — Lưu hoàn toàn trong Redis (`@RedisHash`)
- Sống dài: **7 ngày**
- Lưu dưới dạng `@RedisHash("RefreshToken")` với `@TimeToLive` — Redis tự xóa khi hết hạn
- Cấu trúc:
```
RefreshToken {
    id: "random-uuid"         ← Redis key
    token: "abc-123-def"      ← Client gửi lên để refresh
    userId: UUID              ← Liên kết với User trong PostgreSQL
    email: "user@email.com"
    expirySeconds: 604800     ← TTL 7 ngày (Redis tự xóa)
}
```

### Tại sao dùng Redis thay vì PostgreSQL?
| Tiêu chí | PostgreSQL (cũ) | Redis (hiện tại) |
|---|---|---|
| Tốc độ | ~5ms/query | ~0.1ms/query |
| Hết hạn tự động | Phải tự kiểm tra + xóa | `@TimeToLive` — tự xóa |
| Blacklist JWT | Không có | Key với TTL → tự hủy |
| Phù hợp | Dữ liệu lâu dài | Session, cache, token |

---

## 4. Luồng Đăng ký

**Endpoint:** `POST /api/auth/register`

**Request body:**
```json
{
  "email": "test@example.com",
  "username": "tester123",
  "displayName": "Test User",
  "password": "mysecretpassword"
}
```

**Luồng xử lý:**
```
1. JwtAuthenticationFilter
   → Không có Bearer token → bỏ qua, cho đi tiếp

2. SecurityConfig
   → /api/auth/register nằm trong PUBLIC_ENDPOINTS → permitAll()

3. AuthController.register()
   → @Valid kiểm tra UserCreationRequest:
     - @NotBlank: email, username, password không được rỗng
     - @Email: email phải đúng format
     - @Size(min=6): username >= 6 ký tự
     - @Size(min=8): password >= 8 ký tự, displayName >= 8 ký tự
   → Nếu lỗi → MethodArgumentNotValidException → GlobalExceptionHandler

4. UserServiceImpl.register()
   → existsByEmail() — kiểm tra email trùng → nếu trùng: throw EMAIL_EXISTED
   → existsByUsername() — kiểm tra username trùng → nếu trùng: throw USERNAME_EXISTED
   → passwordEncoder.encode() — băm mật khẩu: "mysecret" → "$2a$10$xK3j..."
   → userRepository.save(user) — lưu user vào PostgreSQL
   → userPreferenceRepository.save() — tạo profile mặc định

5. Trả về: ApiResponse { code: 200, result: UserResponse }
```

---

## 5. Luồng Đăng nhập

**Endpoint:** `POST /api/auth/login`

```json
{ "email": "test@example.com", "password": "mysecretpassword" }
```

**Luồng xử lý:**
```
1. AuthServiceImpl.login()
   → findByEmail("test@example.com") — Không tìm thấy? → throw INVALID_CREDENTIALS
   → passwordEncoder.matches() — Sai mật khẩu? → throw INVALID_CREDENTIALS

2. JwtServiceImpl.generateAccessToken(user)
   → Tạo JWT: { sub: email, role: USER, userId: UUID, exp: now+1h }
   → Ký bằng HMAC-SHA với secret key từ application.yml

3. RefreshTokenServiceImpl.createRefreshToken(user)
   → Xóa refresh token cũ trong Redis (nếu có)
   → Tạo mới RefreshToken với TTL = 7 ngày
   → Lưu vào Redis (RedisHash)

4. Trả về: AuthResponse { accessToken, refreshToken, tokenType, user }
```

**Response:**
```json
{
  "code": 200,
  "result": {
    "accessToken": "eyJhbGciOi...",
    "refreshToken": "abc-123-def-456",
    "tokenType": "Bearer",
    "user": { "id": "...", "email": "...", "displayName": "...", "role": "USER" }
  }
}
```

---

## 6. Luồng truy cập API bảo vệ

**Ví dụ:** `GET /api/items` với header `Authorization: Bearer eyJ...`

```
1. JwtAuthenticationFilter.doFilterInternal()
   → Đọc header "Authorization", tách token: "Bearer eyJ..." → "eyJ..."

2. JwtServiceImpl.extractEmail(token)
   → Parse JWT bằng secret key → verify chữ ký → kiểm tra hết hạn
   → Trả về email từ claim "sub"

3. UserRepository.findByEmail(email)
   → Query PostgreSQL lấy User entity

4. JwtServiceImpl.isTokenValid(token, user)
   → email khớp?
   → chưa hết hạn?
   → KHÔNG nằm trong blacklist Redis? (isTokenBlacklisted)
   → Cả 3 true → hợp lệ ✅

5. Tạo Authentication → đặt vào SecurityContextHolder
   → Controller xử lý request bình thường
```

**Nếu KHÔNG có token hoặc token lỗi:**
```
→ JwtAuthenticationEntryPoint trả:
  { "code": 401, "message": "Unauthenticated — Bạn cần phải đăng nhập..." }
```

**Lấy user hiện tại trong Controller/Service:**
```java
User currentUser = (User) SecurityContextHolder.getContext()
    .getAuthentication().getPrincipal();
```

---

## 7. Luồng làm mới Token

**Endpoint:** `POST /api/auth/refresh-token`

**Khi nào dùng?** Access Token hết hạn (sau 1 giờ), client gửi Refresh Token lấy Access Token mới.

```
1. AuthServiceImpl.refreshToken()
   → RefreshTokenServiceImpl.findByToken("abc-123")
     - Không tìm thấy trong Redis? → throw REFRESH_TOKEN_NOT_FOUND
   → verifyExpiration() — token null? → throw REFRESH_TOKEN_EXPIRED
     (Redis đã tự xóa token hết hạn nhờ @TimeToLive)
   → userRepository.findById(refreshToken.getUserId())
   → Tạo ACCESS TOKEN MỚI (refresh token giữ nguyên)
   → Trả về AuthResponse
```

**Lưu ý:** Endpoint `/api/auth/refresh-token` hiện **KHÔNG** nằm trong `PUBLIC_ENDPOINTS` (đã bị comment out). Client cần gửi Access Token (dù đã hết hạn filter sẽ bỏ qua) hoặc endpoint này cần được mở lại.

---

## 8. Luồng Đăng xuất

**Endpoint:** `POST /api/auth/logout`

**Request:** Header `Authorization: Bearer <accessToken>` + Body `{ "refreshToken": "abc-123" }`

```
1. AuthController.logout()
   → Lấy accessToken từ header, refreshToken từ body

2. AuthServiceImpl.logout()
   → refreshTokenService.deleteByToken("abc-123")
     - Tìm trong Redis → xóa RefreshToken → user không thể refresh nữa
   → jwtService.blacklistToken(accessToken)
     - Tính thời gian còn lại của token
     - Lưu vào Redis: key = "jwt:blacklist:<token>", TTL = thời gian còn lại
     - Khi TTL hết → Redis tự xóa (vì token cũng đã hết hạn)

3. Từ giờ:
   - Access Token cũ → JwtAuthenticationFilter kiểm tra blacklist → BỊ CHẶN
   - Refresh Token cũ → đã xóa khỏi Redis → KHÔNG thể refresh
   → User hoàn toàn bị đăng xuất ✅
```

**Tại sao cần blacklist Access Token?**
Access Token là JWT stateless (không lưu DB). Nếu chỉ xóa Refresh Token, Access Token cũ vẫn dùng được cho đến khi hết hạn (tối đa 1 giờ). Blacklist đảm bảo logout **có hiệu lực ngay lập tức**.

---

## 9. Luồng OAuth2 Google

**Endpoint:** `POST /api/auth/oauth2/google`

```
1. Flutter mở Google Sign-In SDK → User đăng nhập
2. Google trả về ID Token → Flutter gửi đến backend: { "token": "eyG..." }

3. OAuth2ServiceImpl.loginWithGoogle()
   → Gọi Google API: GET https://oauth2.googleapis.com/tokeninfo?id_token=eyG...
   → Google trả về: { email, sub (Google ID), name, picture }

4. Tìm user:
   → findByProviderAndProviderId(GOOGLE, googleId)
   → Không thấy → findByEmail(email)
   → Vẫn không thấy → createOAuth2User()
     - Tạo user KHÔNG CÓ PASSWORD
     - provider = GOOGLE, providerId = googleId
     - username = tên + random suffix

5. Tạo JWT + Refresh Token → trả AuthResponse (giống login thường)
```

---

## 10. Hệ thống xử lý lỗi

**Cơ chế 1: GlobalExceptionHandler** (lỗi trong Controller/Service)
- `AppException` → Lấy ErrorCode → trả ErrorResponse
- `MethodArgumentNotValidException` → Lấy message từ annotation → trả ErrorResponse
- `Exception` → Trả 500 "Uncategorized error"

**Cơ chế 2: JwtAuthenticationEntryPoint** (lỗi 401 từ Security)
- Gọi API bảo vệ mà không có token → trả JSON 401

### Bảng ErrorCode

| ErrorCode | HTTP | Khi nào xảy ra |
|---|---|---|
| `EMAIL_EXISTED` | 400 | Đăng ký email đã có |
| `USERNAME_EXISTED` | 400 | Đăng ký username đã có |
| `INVALID_CREDENTIALS` | 401 | Đăng nhập sai email/password |
| `UNAUTHENTICATED` | 401 | Gọi API bảo vệ không có token |
| `UNAUTHORIZED` | 403 | Không đủ quyền |
| `REFRESH_TOKEN_EXPIRED` | 401 | Refresh token hết hạn (Redis đã xóa) |
| `REFRESH_TOKEN_NOT_FOUND` | 404 | Refresh token không tồn tại |
| `OAUTH2_INVALID_TOKEN` | 401 | Token Google không hợp lệ |
| `USER_NOT_FOUND` | 404 | User không tồn tại |
| `INVALID_KEY` | 400 | Validation dữ liệu đầu vào lỗi |

---

## 11. Bản đồ Debug

| Tôi gặp lỗi... | Tìm trong file | Gợi ý |
|---|---|---|
| 400 "Email is mandatory" | `UserCreationRequest.java` | Body JSON thiếu/rỗng trường email |
| 400 "Email already existed" | `UserServiceImpl.java` | Email đã có trong PostgreSQL |
| 401 "Invalid email or password" | `AuthServiceImpl.java` | Email không tồn tại HOẶC sai mật khẩu |
| 401 "Unauthenticated" | `JwtAuthenticationEntryPoint.java` | Thiếu header Authorization hoặc token hết hạn/blacklisted |
| 401 "Invalid OAuth2 token" | `OAuth2ServiceImpl.java` | Token từ Google không hợp lệ |
| 401 "Refresh token has expired" | `RefreshTokenServiceImpl.java` | Token đã bị Redis tự xóa (quá 7 ngày) |
| 404 "Refresh token is not found" | `RefreshTokenServiceImpl.java` | Token không tồn tại trong Redis |
| 500 "Uncategorized error" | `GlobalExceptionHandler.java` | Lỗi không mong đợi — xem log server |
| Request không đến Controller | `SecurityConfig.java` | Endpoint không nằm trong PUBLIC_ENDPOINTS |
| Token bị từ chối sau logout | `JwtServiceImpl.java` | Token nằm trong blacklist Redis (`jwt:blacklist:<token>`) |
| Decode JWT | https://jwt.io | Paste token vào xem payload |

### Cấu hình quan trọng (application.yml)
```yaml
server:
  port: 8090

spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}    # Redis server
      port: ${REDIS_PORT:6379}

jwt:
  secret-key: ${JWT_SECRET_KEY}         # Key ký JWT (từ .env)
  access-token-expiration: 3600000      # 1 giờ (ms)
  refresh-token-expiration: 604800000   # 7 ngày (ms)

oauth2:
  google:
    client-id: ${GOOGLE_CLIENT_ID}      # Từ Google Cloud Console
```

### Kiểm tra Redis khi debug
```bash
# Xem tất cả refresh token
redis-cli KEYS "RefreshToken:*"

# Xem chi tiết 1 refresh token
redis-cli HGETALL "RefreshToken:<id>"

# Kiểm tra token có bị blacklist không
redis-cli GET "jwt:blacklist:<token>"

# Xem TTL còn lại
redis-cli TTL "RefreshToken:<id>"
```
