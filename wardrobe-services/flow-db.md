# 📊 Sơ đồ Cơ sở Dữ liệu Toàn diện (Wardrobe & Outfit System)

Tài liệu này mô tả chi tiết toàn bộ cấu trúc các bảng dữ liệu, quan hệ và mục đích của từng thực thể trong hệ thống.

## 1. Sơ đồ Quan hệ Thực thể (ERD)

```mermaid
erDiagram
    %% --- Module Hệ thống & Auth ---
    USER ||--o{ REFRESH_TOKEN : "quản lý phiên"
    USER ||--o{ PASSWORD_RESET_OTP : "yêu cầu"
    USER ||--o{ NOTIFICATION : "nhận"
    USER ||--o{ USER_PREFERENCE : "cấu hình"

    %% --- Module Tủ đồ & Phối đồ ---
    USER ||--o{ ITEM : "sở hữu"
    USER ||--o{ OUTFIT : "tạo ra"
    ITEM }|--|{ OUTFIT : "nằm trong (Many-to-Many)"
    OUTFIT ||--o{ OUTFIT_LIKE : "được thích"
    OUTFIT ||--o{ OUTFIT_COMMENT : "được bình luận"
    USER ||--o{ OUTFIT_LIKE : "thực hiện thích"
    USER ||--o{ OUTFIT_COMMENT : "viết bình luận"

    %% --- Module Xã hội & Tin nhắn ---
    USER ||--o{ FRIEND : "kết bạn"
    USER ||--o{ MESSAGE : "gửi/nhận"
    CHAT_CONVERSATION ||--o{ MESSAGE : "chứa"
    USER ||--o{ CHAT_CONVERSATION : "tham gia"

    USER {
        UUID id PK
        String email
        String password
        String fullName
        Enum role
        Gender gender
        Boolean isEnabled
    }

    ITEM {
        UUID id PK
        UUID user_id FK
        String name
        String type
        String color
        String season
        String brand
        String imageUrl
        Boolean isDeleted
        Instant createdAt
    }

    OUTFIT {
        UUID id PK
        UUID user_id FK
        String name
        String occasion
        Boolean isFavorite
        Boolean isPublic
        Boolean isAiGenerated
        Double score
        Instant createdAt
    }

    OUTFIT_LIKE {
        UUID id PK
        UUID user_id FK
        UUID outfit_id FK
        Instant createdAt
    }

    OUTFIT_COMMENT {
        UUID id PK
        UUID user_id FK
        UUID outfit_id FK
        String content
        Instant createdAt
    }

    FRIEND {
        UUID id PK
        UUID user_id FK "Người gửi yêu cầu"
        UUID friend_id FK "Người nhận yêu cầu"
        Enum status "PENDING, ACCEPTED, BLOCKED"
    }

    MESSAGE {
        UUID id PK
        UUID conversation_id FK
        UUID sender_id FK
        String content
        Instant sentAt
    }
```

## 2. Chi tiết các Module

### A. Module Wardrobe (Tủ đồ)
*   **Item**: Lưu trữ thông tin từng món đồ cá nhân.
    *   `isDeleted`: Dùng cho tính năng **Soft Delete** (Xóa tạm thời).

### B. Module Outfits (Phối đồ)
*   **Outfit**: Lưu trữ bộ trang phục được kết hợp từ nhiều Items.
    *   Quan hệ Nhiều-Nhiều với Item thông qua bảng trung gian `outfit_items`.

### C. Module Social (Xã hội)
*   **Friend**: Quản lý mối quan hệ bạn bè giữa các người dùng.
*   **ChatConversation & Message**: Hệ thống tin nhắn thời gian thực.

### D. Module System (Hệ thống)
*   **RefreshToken**: Bảo mật phiên đăng nhập.
*   **Notification**: Gửi các gợi ý phối đồ hoặc thông báo tương tác.
*   **UserPreference**: Lưu sở thích cá nhân.

---
*Tài liệu này được lưu trực tiếp vào thư mục dự án bởi Antigravity.*
