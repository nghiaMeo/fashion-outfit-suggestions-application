-- ==================== ADD PARENT_ID TO OUTFIT_COMMENT ====================
ALTER TABLE outfit_comment ADD COLUMN parent_id UUID;
ALTER TABLE outfit_comment ADD CONSTRAINT fk_outfit_comment_parent FOREIGN KEY (parent_id) REFERENCES outfit_comment(id) ON DELETE CASCADE;
CREATE INDEX idx_outfit_comment_parent_id ON outfit_comment(parent_id);

-- ==================== OUTFIT COMMENT LIKES TABLE ====================
CREATE TABLE IF NOT EXISTS outfit_comment_likes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    comment_id UUID NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_outfit_comment_likes_comment FOREIGN KEY (comment_id) REFERENCES outfit_comment(id) ON DELETE CASCADE,
    CONSTRAINT fk_outfit_comment_likes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT outfit_comment_likes_user_comment_unique UNIQUE (user_id, comment_id)
);

CREATE INDEX idx_outfit_comment_likes_user_id ON outfit_comment_likes(user_id);
CREATE INDEX idx_outfit_comment_likes_comment_id ON outfit_comment_likes(comment_id);
