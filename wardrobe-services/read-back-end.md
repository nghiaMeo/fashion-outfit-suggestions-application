# Tài liệu Backend — Fashion Outfit Suggestions Application

## Mục lục
1. [Kiến trúc tổng quan](#1-kiến-trúc-tổng-quan)
2. [Tại sao Service có folder impl?](#2-tại-sao-service-có-folder-impl)
3. [Tại sao lưu Refresh Token trong Database?](#3-tại-sao-lưu-refresh-token-trong-database)
4. [Luồng Đăng ký](#4-luồng-đăng-ký)
5. [Luồng Đăng nhập](#5-luồng-đăng-nhập)
6. [Luồng truy cập API bảo vệ (JWT Filter)](#6-luồng-truy-cập-api-bảo-vệ)
7. [Luồng làm mới Token](#7-luồng-làm-mới-token)
8. [Luồng OAuth2 (Google/Facebook)](#8-luồng-oauth2)
9. [Hệ thống xử lý lỗi](#9-hệ-thống-xử-lý-lỗi)
10. [Bản đồ Debug](#10-bản-đồ-debug)

---

## 1. Kiến trúc tổng quan

```
Client (Flutter/Postman)
    ↓ HTTP Request
JwtAuthenticationFilter  ← Kiểm tra Bearer token (nếu có)
    ↓
SecurityConfig           ← Cho phép hoặc chặn request
    ↓
AuthController           ← Nhận request, gọi service
    ↓
Service (impl/)          ← Xử lý logic nghiệp vụ
    ↓
Repository               ← Truy vấn Database
    ↓
PostgreSQL               ← Lưu trữ dữ liệu
```

Nếu có lỗi → `GlobalExceptionHandler` bắt và trả JSON chuẩn.

### Cấu trúc thư mục

```
src/main/java/com/example/wardrobeservices/
├── config/
│   ├── SecurityConfig.java              ← Cấu hình bảo mật, endpoint public/private
│   ├── JwtAuthenticationFilter.java     ← Filter chặn mọi request, xác thực JWT
│   └── JwtAuthenticationEntryPoint.java ← Trả JSON 401 khi chưa đăng nhập
├── controller/
│   └── AuthController.java             ← API endpoints: register, login, oauth2
├── dto/
│   ├── request/                         ← Dữ liệu đầu vào từ client
│   │   ├── UserCreationRequest.java
│   │   ├── LoginRequest.java
│   │   ├── RefreshTokenRequest.java
│   │   └── OAuth2Request.java
│   └── response/                        ← Dữ liệu trả về cho client
│       ├── ApiResponse.java             ← Wrapper chuẩn {code, message, result}
│       ├── AuthResponse.java            ← Token + user info
│       ├── UserResponse.java            ← User info (không có password)
│       └── ErrorResponse.java           ← Lỗi {code, message, timestamp}
├── entity/
│   ├── User.java                        ← Bảng users
│   ├── RefreshToken.java                ← Bảng refresh_token (session)
│   ├── UserPreference.java              ← Profile/sở thích user
│   └── enums/
│       ├── Role.java                    ← USER, ADMIN
│       └── AuthProvider.java            ← LOCAL, GOOGLE, FACEBOOK
├── exception/
│   ├── ErrorCode.java                   ← Tập trung mọi mã lỗi
│   ├── AppException.java                ← Custom exception
│   └── GlobalExceptionHandler.java      ← Bắt tất cả lỗi, trả JSON
├── repository/                          ← Truy vấn database (JpaRepository)
│   ├── UserRepository.java
│   └── RefreshTokenRepository.java
└── service/
    ├── UserService.java                 ← Interface
    ├── AuthService.java                 ← Interface
    ├── JwtService.java                  ← Interface
    ├── RefreshTokenService.java         ← Interface
    ├── OAuth2Service.java               ← Interface
    └── impl/                            ← Triển khai cụ thể
        ├── UserServiceImpl.java
        ├── AuthServiceImpl.java
        ├── JwtServiceImpl.java
        ├── RefreshTokenServiceImpl.java
        └── OAuth2ServiceImpl.java
```

---

## 2. Tại sao Service có folder impl?

Đây là pattern **Interface + Implementation**, một best practice chuẩn trong Spring Boot.

### Lý do 1: Dễ thay thế triển khai
```java
// Interface — chỉ nói "TÔI CẦN GÌ"
public interface JwtService {
    String generateAccessToken(User user);
}

// Implementation — nói "LÀM THẾ NÀO"
public class JwtServiceImpl implements JwtService { ... }  // dùng jjwt
public class JwtServiceAuth0Impl implements JwtService { ... } // dùng auth0
```
Nếu đổi thư viện JWT, chỉ cần tạo class mới, **không sửa Controller hay Service khác**.

### Lý do 2: Unit Testing
```java
@Mock
private JwtService jwtService;  // Mock dễ dàng vì là Interface
```

### Lý do 3: Nguyên tắc SOLID
Controller chỉ phụ thuộc Interface (trừu tượng), không phụ thuộc class cụ thể:
```java
private final AuthService authService;   // ✅ Interface
// KHÔNG PHẢI: private final AuthServiceImpl authService;  // ❌
```

Spring tự động "nối dây": thấy `@Service` trên `AuthServiceImpl` → inject vào nơi cần `AuthService`.

---

## 3. Tại sao lưu Refresh Token trong Database?

Hệ thống có **2 loại token**, mỗi loại lưu trữ khác nhau:

### Access Token — KHÔNG lưu ở đâu cả (Stateless)
- Là JWT, mọi thông tin (email, role, hạn) nằm trong chuỗi token
- Backend chỉ cần giải mã bằng secret key → không tốn query DB
- Sống ngắn: **1 giờ** → nếu bị lộ, thiệt hại có giới hạn

### Refresh Token — Lưu trong Database
- Sống dài: **7 ngày** → cần kiểm soát chặt hơn
- Lý do phải lưu DB:

| Lý do | Giải thích |
|---|---|
| **Thu hồi token** | User đổi mật khẩu → xóa refresh token trong DB → đăng xuất ngay |
| **Phát hiện đánh cắp** | Token đã bị dùng rồi → xóa toàn bộ → buộc đăng nhập lại |
| **Giới hạn phiên** | Xóa token cũ khi tạo mới → chỉ cho đăng nhập 1 thiết bị |

### So sánh các phương án lưu trữ

| Phương án | Ưu điểm | Nhược điểm |
|---|---|---|
| **PostgreSQL** ✅ đang dùng | Đơn giản, phù hợp MVP | Tốn 1 query DB mỗi lần refresh |
| **Redis** | Cực nhanh, tự hết hạn (TTL) | Cần thêm hạ tầng Redis server |
| **JWT thuần** (không lưu) | Không tốn lưu trữ | **Không thể thu hồi** — rủi ro bảo mật lớn |

---

## 4. Luồng Đăng ký

**Endpoint:** `POST /api/auth/register`

**Request body:**
```json
{
  "email": "test@example.com",
  "username": "tester123",
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
     - @Size(min=6): password >= 6 ký tự
   → Nếu lỗi → MethodArgumentNotValidException → GlobalExceptionHandler

4. UserServiceImpl.register()
   → existsByEmail() — kiểm tra email trùng → nếu trùng: throw EMAIL_EXISTED
   → existsByUsername() — kiểm tra username trùng → nếu trùng: throw USERNAME_EXISTED
   → passwordEncoder.encode() — băm mật khẩu: "mysecret" → "$2a$10$xK3j..."
   → userRepository.save(user) — lưu user vào DB
   → userPreferenceRepository.save() — tạo profile mặc định

5. Trả về:
   ApiResponse { code: 200, message: "Success", result: UserResponse }
```

**Response thành công:**
```json
{
  "code": 200,
  "message": "Success",
  "result": {
    "id": "uuid-here",
    "email": "test@example.com",
    "username": "tester123",
    "role": "USER",
    "createdAt": "2026-04-28T..."
  }
}
```

---

## 5. Luồng Đăng nhập

**Endpoint:** `POST /api/auth/login`

**Request body:**
```json
{
  "email": "test@example.com",
  "password": "mysecretpassword"
}
```

**Luồng xử lý:**
```
1. AuthController.login() → authService.login(request)

2. AuthServiceImpl.login()
   → findByEmail("test@example.com")
     - Không tìm thấy? → throw INVALID_CREDENTIALS (401)
   → passwordEncoder.matches("mysecretpassword", "$2a$10$xK3j...")
     - Sai mật khẩu? → throw INVALID_CREDENTIALS (401)
     - Cả 2 trường hợp dùng cùng message để tránh lộ thông tin

3. JwtServiceImpl.generateAccessToken(user)
   → Tạo JWT chứa: sub=email, role=USER, userId=UUID, exp=now()+1h
   → Ký bằng HMAC-SHA với secret key từ application.yml
   → Kết quả: "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoi..."

4. RefreshTokenServiceImpl.createRefreshToken(user)
   → Xóa refresh token cũ (nếu có) — giới hạn 1 phiên đăng nhập
   → Tạo UUID ngẫu nhiên làm token
   → Lưu vào DB với expiryDate = now() + 7 ngày

5. Trả về: AuthResponse { accessToken, refreshToken, tokenType, user }
```

**Response thành công:**
```json
{
  "code": 200,
  "message": "Success",
  "result": {
    "accessToken": "eyJhbGciOi...",
    "refreshToken": "abc-123-def-456",
    "tokenType": "Bearer",
    "user": { "id": "...", "email": "...", "role": "USER" }
  }
}
```

### Cấu trúc JWT Token
```
eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInVzZXJJZCI6Ii4uLiJ9.signature
        ↑ Header              ↑ Payload (Claims)                    ↑ Chữ ký
```
- `sub` = email
- `role` = "USER"
- `userId` = UUID
- `iat` = thời điểm tạo
- `exp` = thời điểm hết hạn

Decode tại: https://jwt.io

---

## 6. Luồng truy cập API bảo vệ

**Ví dụ:** `GET /api/items` với header `Authorization: Bearer eyJ...`

**Luồng xử lý:**
```
1. JwtAuthenticationFilter.doFilterInternal()
   → Đọc header "Authorization"
   → Tách token: "Bearer eyJ..." → "eyJ..."
   
2. JwtServiceImpl.extractEmail(token)
   → Parse JWT bằng secret key
   → Verify chữ ký — sai key? → exception → log warning, bỏ qua
   → Kiểm tra hết hạn — quá 1 giờ? → exception
   → Trả về email từ claim "sub"

3. UserRepository.findByEmail(email)
   → Query DB lấy User entity

4. JwtServiceImpl.isTokenValid(token, user)
   → email trong token == email trong DB?
   → token chưa hết hạn?
   → Cả 2 true → token hợp lệ ✅

5. Tạo UsernamePasswordAuthenticationToken
   → Principal = User entity
   → Authorities = "ROLE_USER"
   → Đặt vào SecurityContextHolder

6. SecurityConfig kiểm tra
   → /api/items KHÔNG phải public endpoint
   → Nhưng đã có Authentication trong SecurityContext → cho qua ✅

7. Controller xử lý request bình thường
```

**Nếu KHÔNG có token hoặc token lỗi:**
```
1. JwtAuthenticationFilter → authHeader == null → bỏ qua
2. SecurityConfig → /api/items cần authenticated nhưng chưa có → ❌
3. JwtAuthenticationEntryPoint.commence()
   → Trả JSON: { "code": 401, "message": "Unauthenticated — You need to login first" }
```

### Lấy user hiện tại trong Controller/Service:
```java
User currentUser = (User) SecurityContextHolder.getContext()
    .getAuthentication().getPrincipal();
```

---

## 7. Luồng làm mới Token

**Endpoint:** `POST /api/auth/refresh-token`

**Khi nào dùng?** Khi Access Token hết hạn (sau 1 giờ), client gửi Refresh Token để lấy Access Token mới mà không cần nhập lại mật khẩu.

**Luồng:**
```
1. AuthServiceImpl.refreshToken()
   → RefreshTokenServiceImpl.findByToken("abc-123")
     - Không tìm thấy? → throw REFRESH_TOKEN_NOT_FOUND
   → RefreshTokenServiceImpl.verifyExpiration(token)
     - Hết hạn? → XÓA token khỏi DB → throw REFRESH_TOKEN_EXPIRED
   → user = refreshToken.getUser()
   → Tạo ACCESS TOKEN MỚI (Refresh token vẫn giữ nguyên)
   → Trả về AuthResponse
```

---

## 8. Luồng OAuth2

**Endpoint:** `POST /api/auth/oauth2/google` hoặc `/api/auth/oauth2/facebook`

**Luồng tổng quan:**
```
1. Flutter mở Google/Facebook Sign-In SDK
2. User đăng nhập → SDK trả về ID Token (Google) hoặc Access Token (Facebook)
3. Flutter gửi token đến backend: POST /api/auth/oauth2/google { "token": "eyG..." }

4. OAuth2ServiceImpl:
   → Gọi Google API: GET https://oauth2.googleapis.com/tokeninfo?id_token=eyG...
   → Google trả về: { email, sub (Google ID), name, picture }
   
5. Tìm user:
   → findByProviderAndProviderId(GOOGLE, googleId)
   → Nếu không thấy → findByEmail(email)
   → Nếu vẫn không thấy → createOAuth2User()
     - Tạo user KHÔNG CÓ PASSWORD
     - provider = GOOGLE, providerId = googleId
     - username = tên + random suffix

6. Tạo JWT + Refresh Token → trả về AuthResponse (giống login thường)
```

**Lưu ý:** User OAuth2 không có mật khẩu → field `password` trong User entity cho phép null.

---

## 9. Hệ thống xử lý lỗi

Có **2 cơ chế** bắt lỗi:

### Cơ chế 1: GlobalExceptionHandler (cho lỗi trong Controller/Service)
```java
@RestControllerAdvice  // Bắt exception từ tất cả Controller
public class GlobalExceptionHandler {
    
    // Lỗi logic nghiệp vụ (do ta throw)
    @ExceptionHandler(AppException.class)
    → Lấy ErrorCode → trả ErrorResponse
    
    // Lỗi validation DTO (Spring tự throw)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    → Lấy message từ annotation (@NotBlank, @Email) → trả ErrorResponse
    
    // Lỗi không xác định
    @ExceptionHandler(Exception.class)
    → Trả 500 "Uncategorized error"
}
```

### Cơ chế 2: JwtAuthenticationEntryPoint (cho lỗi 401 từ Security)
```java
// Khi user gọi API bảo vệ mà không có token
→ Spring Security gọi JwtAuthenticationEntryPoint.commence()
→ Trả JSON: { "code": 401, "message": "Unauthenticated" }
```

### Bảng tổng hợp ErrorCode

| ErrorCode | HTTP | Message | Khi nào xảy ra |
|---|---|---|---|
| `EMAIL_EXISTED` | 400 | Email already existed | Đăng ký email đã có |
| `USERNAME_EXISTED` | 400 | Username already existed | Đăng ký username đã có |
| `INVALID_CREDENTIALS` | 401 | Invalid email or password | Đăng nhập sai |
| `UNAUTHENTICATED` | 401 | Unauthenticated | Gọi API bảo vệ không có token |
| `UNAUTHORIZED` | 403 | You do not have permission | Không đủ quyền |
| `REFRESH_TOKEN_EXPIRED` | 401 | Refresh token has expired | Refresh token quá 7 ngày |
| `OAUTH2_INVALID_TOKEN` | 401 | Invalid OAuth2 token | Token Google/Facebook không hợp lệ |
| `INVALID_KEY` | 400 | Validation failed | Dữ liệu đầu vào không hợp lệ |

---

## 10. Bản đồ Debug

| Tôi gặp lỗi... | Tìm trong file | Gợi ý |
|---|---|---|
| 400 "Email is mandatory" | `UserCreationRequest.java` | Body JSON thiếu/rỗng trường email |
| 400 "Email already existed" | `UserServiceImpl.java` | Email đã có trong DB |
| 401 "Invalid email or password" | `AuthServiceImpl.java` | Email không tồn tại HOẶC sai mật khẩu |
| 401 "Unauthenticated" | `JwtAuthenticationEntryPoint.java` | Thiếu header Authorization hoặc token hết hạn |
| 401 "Invalid OAuth2 token" | `OAuth2ServiceImpl.java` | Token từ Google/Facebook không hợp lệ |
| 401 "Refresh token has expired" | `RefreshTokenServiceImpl.java` | Refresh token quá 7 ngày |
| 500 "Uncategorized error" | `GlobalExceptionHandler.java` | Lỗi không mong đợi — xem log server |
| Request không đến Controller | `SecurityConfig.java` | Endpoint không nằm trong PUBLIC_ENDPOINTS |
| Token bị từ chối | `JwtAuthenticationFilter.java` | Xem log "JWT authentication failed" |
| Decode JWT | https://jwt.io | Paste token vào xem payload |

### Cấu hình quan trọng (application.yml)
```yaml
jwt:
  secret-key: ...          # Key ký JWT — phải giống nhau giữa tạo và verify
  access-token-expiration: 3600000     # 1 giờ (ms)
  refresh-token-expiration: 604800000  # 7 ngày (ms)

oauth2:
  google:
    client-id: ...         # Lấy từ Google Cloud Console
  facebook:
    app-id: ...            # Lấy từ Facebook Developers
    app-secret: ...
```
