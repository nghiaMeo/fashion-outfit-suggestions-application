-- ==================== USERS TABLE ====================
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    password VARCHAR(255),
    avatar_url TEXT,
    bio TEXT,
    role VARCHAR(50) DEFAULT 'USER',
    provider VARCHAR(50) DEFAULT 'LOCAL',
    provider_id VARCHAR(255),
    fcm_token VARCHAR(255),
    is_online BOOLEAN DEFAULT FALSE,
    last_seen TIMESTAMP,
    is_private_profile BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_email_unique UNIQUE (email),
    CONSTRAINT users_username_unique UNIQUE (username)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_provider ON users(provider, provider_id);

-- ==================== USER PREFERENCES TABLE ====================
CREATE TABLE IF NOT EXISTS user_preference (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    gender VARCHAR(50),
    body_type VARCHAR(100),
    favorite_styles TEXT,
    favorite_colors VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_preference_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_preference_user_id ON user_preference(user_id);

-- ==================== ITEMS TABLE ====================
CREATE TABLE IF NOT EXISTS item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    color VARCHAR(100) NOT NULL,
    season VARCHAR(100),
    brand VARCHAR(255),
    occasion VARCHAR(100),
    image_url TEXT,
    tags TEXT,
    ai_item_id UUID NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_item_user_id ON item(user_id);
CREATE INDEX idx_item_is_deleted ON item(is_deleted);

-- ==================== OUTFITS TABLE ====================
CREATE TABLE IF NOT EXISTS outfit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    occasion VARCHAR(100),
    is_favorite BOOLEAN DEFAULT FALSE NOT NULL,
    is_ai_generated BOOLEAN DEFAULT FALSE NOT NULL,
    score DOUBLE PRECISION DEFAULT 0.0 NOT NULL,
    is_daily_suggestion BOOLEAN DEFAULT FALSE NOT NULL,
    is_public BOOLEAN DEFAULT TRUE NOT NULL,
    suitable_weather VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_outfit_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_outfit_user_id ON outfit(user_id);
CREATE INDEX idx_outfit_is_public ON outfit(is_public);
CREATE INDEX idx_outfit_is_deleted ON outfit(is_deleted);

-- ==================== OUTFIT ITEMS JOIN TABLE ====================
CREATE TABLE IF NOT EXISTS outfit_items (
    outfit_id UUID NOT NULL,
    item_id UUID NOT NULL,
    PRIMARY KEY (outfit_id, item_id),
    CONSTRAINT fk_outfit_items_outfit FOREIGN KEY (outfit_id) REFERENCES outfit(id) ON DELETE CASCADE,
    CONSTRAINT fk_outfit_items_item FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE
);

CREATE INDEX idx_outfit_items_outfit_id ON outfit_items(outfit_id);
CREATE INDEX idx_outfit_items_item_id ON outfit_items(item_id);

-- ==================== FRIENDSHIPS TABLE ====================
CREATE TABLE IF NOT EXISTS friendships (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    requester_id UUID NOT NULL,
    receiver_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_friendships_requester FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_friendships_receiver FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT friendships_requester_receiver_unique UNIQUE (requester_id, receiver_id),
    CONSTRAINT check_different_users CHECK (requester_id <> receiver_id)
);

CREATE INDEX idx_friendships_requester_id ON friendships(requester_id);
CREATE INDEX idx_friendships_receiver_id ON friendships(receiver_id);
CREATE INDEX idx_friendships_status ON friendships(status);

-- ==================== CHAT CONVERSATIONS TABLE ====================
CREATE TABLE IF NOT EXISTS chat_conversation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    last_message TEXT,
    last_message_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==================== CONVERSATION MEMBERS TABLE ====================
CREATE TABLE IF NOT EXISTS conversation_member (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL,
    user_id UUID NOT NULL,
    nickname VARCHAR(255),
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_read_at TIMESTAMP,
    is_muted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_conversation_member_conversation FOREIGN KEY (conversation_id) REFERENCES chat_conversation(id) ON DELETE CASCADE,
    CONSTRAINT fk_conversation_member_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT conversation_member_unique UNIQUE (conversation_id, user_id)
);

CREATE INDEX idx_conversation_member_conversation_id ON conversation_member(conversation_id);
CREATE INDEX idx_conversation_member_user_id ON conversation_member(user_id);

-- ==================== MESSAGES TABLE ====================
CREATE TABLE IF NOT EXISTS message (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL,
    sender_id UUID NOT NULL,
    content TEXT,
    type VARCHAR(50) DEFAULT 'TEXT',
    image_url TEXT,
    shared_outfit_id UUID,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_message_conversation FOREIGN KEY (conversation_id) REFERENCES chat_conversation(id) ON DELETE CASCADE,
    CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_message_conversation_id ON message(conversation_id);
CREATE INDEX idx_message_sender_id ON message(sender_id);
CREATE INDEX idx_message_created_at ON message(created_at);

-- ==================== NOTIFICATIONS TABLE ====================
CREATE TABLE IF NOT EXISTS notification (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_id UUID NOT NULL,
    actor_id UUID,
    type VARCHAR(50),
    target_id UUID,
    content TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notification_actor FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_notification_recipient_id ON notification(recipient_id);
CREATE INDEX idx_notification_is_read ON notification(is_read);
CREATE INDEX idx_notification_created_at ON notification(created_at);

-- ==================== OUTFIT LIKES TABLE ====================
CREATE TABLE IF NOT EXISTS outfit_likes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    outfit_id UUID NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_outfit_likes_outfit FOREIGN KEY (outfit_id) REFERENCES outfit(id) ON DELETE CASCADE,
    CONSTRAINT fk_outfit_likes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT outfit_likes_user_outfit_unique UNIQUE (user_id, outfit_id)
);

CREATE INDEX idx_outfit_likes_user_id ON outfit_likes(user_id);
CREATE INDEX idx_outfit_likes_outfit_id ON outfit_likes(outfit_id);

-- ==================== OUTFIT COMMENTS TABLE ====================
CREATE TABLE IF NOT EXISTS outfit_comment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    outfit_id UUID NOT NULL,
    user_id UUID NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_outfit_comment_outfit FOREIGN KEY (outfit_id) REFERENCES outfit(id) ON DELETE CASCADE,
    CONSTRAINT fk_outfit_comment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_outfit_comment_outfit_id ON outfit_comment(outfit_id);
CREATE INDEX idx_outfit_comment_user_id ON outfit_comment(user_id);
