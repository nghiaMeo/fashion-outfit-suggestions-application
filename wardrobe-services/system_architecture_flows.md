# 🌊 Kiến Trúc và Luồng Dữ Liệu (System Flows)

Dưới đây là các luồng xử lý (flow) quan trọng nhất trong hệ thống Wardrobe Services của bạn. Việc nắm rõ các luồng này sẽ giúp bạn hình dung bức tranh toàn cảnh, dễ dàng debug và phát triển thêm tính năng mà không sợ phá vỡ luồng hiện tại.

## 1. Luồng Xác thực (Authentication & Authorization Flow)
Hệ thống kết hợp **Stateless** (JWT) và **Stateful** (Redis lưu Refresh Token và OTP) để tối ưu hiệu năng và bảo mật.

```mermaid
sequenceDiagram
    participant C as Client
    participant A as AuthController
    participant S as AuthServiceImpl
    participant R as Redis
    participant DB as Database
    
    %% Đăng nhập
    rect rgb(200, 220, 240)
    Note over C,DB: 1. Đăng Nhập (Login)
    C->>A: POST /login (email, password)
    A->>S: login()
    S->>DB: Kiểm tra User & Password
    S->>S: Tạo JWT Access Token (Sống ngắn)
    S->>R: Tạo & Lưu Refresh Token (Sống dài)
    S-->>C: Trả về Access Token + Refresh Token
    end

    %% Yêu cầu truy cập tài nguyên
    rect rgb(220, 240, 200)
    Note over C,DB: 2. Gọi API Bảo Mật
    C->>A: Header: Authorization Bearer {AccessToken}
    A->>A: JwtAuthenticationFilter kiểm tra
    alt Token hợp lệ
        A->>DB: Chạy logic (vd: Lấy tủ đồ)
        DB-->>C: Trả kết quả
    else Token hết hạn/Sai
        A-->>C: 401 Unauthorized
    end
    end
    
    %% Làm mới Token
    rect rgb(240, 220, 200)
    Note over C,DB: 3. Cấp Lại Token
    C->>A: POST /refresh-token (refreshToken)
    A->>S: refreshToken()
    S->>R: So sánh Refresh Token trong Redis
    alt Hợp lệ
        S->>S: Tạo JWT Access Token MỚI
        S-->>C: Trả về Access Token MỚI
    else Không tồn tại
        S-->>C: 403 Forbidden (Bắt đăng nhập lại)
    end
    end
```

## 2. Luồng Khôi Phục Mật Khẩu (Forgot Password)
Tận dụng tính năng tự động xóa (TTL) của Redis để quản lý vòng đời của OTP.

```mermaid
sequenceDiagram
    participant C as Client
    participant S as AuthServiceImpl
    participant R as Redis
    participant E as EmailService

    C->>S: POST /forgot-password (Email)
    S->>R: Kiểm tra Rate Limit (60s)
    alt Bị giới hạn
        S-->>C: Lỗi Rate Limit (Yêu cầu chờ)
    else Hợp lệ
        S->>S: Tạo OTP (6 số ngẫu nhiên)
        S->>R: Lưu OTP (Tự động xóa sau 3 phút)
        S->>E: Gửi OTP qua Mail
        E-->>C: User nhận Email
    end
    
    C->>S: POST /reset-password (Email, OTP, NewPass)
    S->>R: Lấy OTP từ Redis
    alt Khớp & Còn hạn
        S->>S: Cập nhật Password mới
        S-->>C: Thành công
    else Sai/Hết hạn
        S-->>C: Báo lỗi OTP
    end
```

## 3. Luồng Quản Lý Tủ Đồ và Phối Đồ (Wardrobe & Outfit)
Giao tiếp với bên thứ 3 (Cloudinary) để lưu trữ hình ảnh trước khi ghi vào Database.

```mermaid
sequenceDiagram
    participant C as Client
    participant IS as ItemService
    participant OS as OutfitService
    participant CL as Cloudinary
    participant DB as Database

    %% Thêm món đồ mới
    rect rgb(250, 240, 230)
    Note over C,DB: 1. Thêm Món Đồ (Item)
    C->>IS: POST /items (Ảnh, Tên, Màu...)
    IS->>CL: Upload ảnh lên Cloud
    CL-->>IS: Trả về URL ảnh trực tuyến
    IS->>DB: Lưu Item (ID người dùng, URL ảnh)
    DB-->>C: Trả về Item
    end

    %% Phối đồ
    rect rgb(253, 245, 230)
    Note over C,DB: 2. Tạo Phối Đồ (Outfit)
    C->>OS: POST /outfits (Tên, Danh sách Item IDs)
    OS->>DB: Xác thực các Item này có phải của User không?
    DB-->>OS: Hợp lệ
    OS->>DB: Lưu Outfit (Tạo liên kết N-N với Item)
    DB-->>C: Trả về Outfit
    end
```

## 4. Luồng Nhắn Tin và Thông Báo (Real-time & Social)
Luồng phức tạp nhất kết hợp giữa HTTP Rest API, WebSockets và Push Notifications.

```mermaid
sequenceDiagram
    participant C1 as User A (Client)
    participant C2 as User B (Client)
    participant API as Backend (Service)
    participant SK as SocketIO Server
    participant DB as Database
    participant FCM as Firebase

    %% Tạo tương tác (Kết bạn, Like)
    Note over C1,FCM: 1. Kết bạn / Like Outfit (In-App Notification)
    C1->>API: Bấm "Kết bạn"
    API->>DB: Lưu Notification vào Database (chưa đọc)
    API->>SK: Bắn Event "new_notification" vào Room(User B)
    SK-->>C2: (Nếu Online) Cập nhật quả chuông đỏ ngay lập tức

    %% Chat Real-time
    Note over C1,FCM: 2. Nhắn tin (Chat Real-time)
    C1->>SK: Emit "send_message" (Nội dung)
    SK->>DB: Lưu Message vào Database
    SK->>SK: Broadcast "receive_message" cho thành viên trong Room
    SK-->>C2: (Nếu Online) Nhận tin nhắn ngay lập tức
    
    %% Thông báo Push Offline
    alt User B đang Đóng App (Offline)
        SK->>API: Kích hoạt Push Notification
        API->>FCM: Gửi Firebase Message kèm FCM Token của User B
        FCM-->>C2: Rung điện thoại, hiện thông báo trên màn hình khóa
    end
```

## 🔄 Tóm tắt Vòng Đời Dữ Liệu (Data Pipeline)
1. **Client -> Server**: Mọi Request đều phải đi qua `JwtAuthenticationFilter`. Không có Token hoặc Token hết hạn -> Bị đá văng ngay lập tức.
2. **Controller**: Lớp bề mặt. Chỉ làm nhiệm vụ nhận JSON (Request DTO), gọi Service, rồi đóng gói kết quả thành JSON (Response DTO). KHÔNG chứa logic nghiệp vụ.
3. **Service**: "Não bộ" của hệ thống. Kiểm tra quyền sở hữu, tính toán dữ liệu, gọi các dịch vụ bên ngoài (Firebase, Cloudinary) và tương tác với Database.
4. **Bảo mật sở hữu (Row-Level Security bằng Code)**: Xuyên suốt các Service (`Outfit`, `Item`, `Notification`), luôn có logic: `if (!item.getUser().getId().equals(currentUser.getId())) throw UNAUTHORIZED`. Điều này đảm bảo User A không bao giờ sửa hoặc xóa trộm đồ của User B được.
