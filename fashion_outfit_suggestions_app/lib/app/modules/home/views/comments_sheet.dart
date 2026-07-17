import 'package:fashion_outfit_suggestions_app/core/models/outfit_response.dart';
import 'package:flutter/material.dart';
import 'package:get/get_state_manager/src/rx_flutter/rx_obx_widget.dart';
import 'package:get/get_state_manager/src/simple/get_view.dart';

import '../../../../core/models/outfit_comment_response.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_fonts.dart';
import '../controllers/home_controller.dart';

class CommentsSheet extends GetView<HomeController> {
  final OutfitResponse outfit;

  const CommentsSheet({super.key, required this.outfit});

  @override
  Widget build(BuildContext context) {
    final bottomInset = MediaQuery.of(context).viewInsets.bottom;

    return Container(
      height: MediaQuery.of(context).size.height * 0.85,
      decoration: const BoxDecoration(
        color: Color(0xFF1C1C1E),
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      child: Column(
        children: [
          Container(
            margin: const EdgeInsets.symmetric(vertical: 10),
            width: 40,
            height: 4,
            decoration: BoxDecoration(
              color: Colors.white30,
              borderRadius: BorderRadius.circular(2),
            ),
          ),

          Text(
            'Comments',
            style: AppFonts.base(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          const Divider(color: Color(0xFF3A3A3C)),

          Expanded(
            child: Obx(() {
              if (controller.isCommentsLoading.value) {
                return const Center(
                  child: CircularProgressIndicator(color: Colors.white),
                );
              }

              return ListView(
                padding: const EdgeInsets.symmetric(vertical: 8),
                children: [
                  _buildPostOwnerCaption(),
                  const Divider(color: Color(0xFF2C2C2E)),
                  if (controller.comments.isEmpty)
                    Padding(
                      padding: const EdgeInsets.symmetric(vertical: 40),
                      child: Center(
                        child: Text(
                          'No comments yet.\nBe the first to comment!',
                          textAlign: TextAlign.center,
                          style: AppFonts.base(
                            color: Colors.white38,
                            fontSize: 14,
                          ),
                        ),
                      ),
                    )
                  else
                    ...controller.comments.map(
                      (comment) => _buildCommentItem(comment),
                    ),
                ],
              );
            }),
          ),

          Obx(() {
            if (controller.replyToUsername.value == null) {
              return const SizedBox.shrink();
            }
            return Container(
              color: const Color(0xFF2C2C2E),
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              child: Row(
                children: [
                  Expanded(
                    child: Text(
                      'Replying to @${controller.replyToUsername.value}',
                      style: AppFonts.base(color: Colors.white70, fontSize: 12),
                    ),
                  ),
                  GestureDetector(
                    onTap: controller.cancelReply,
                    child: const Icon(
                      Icons.close,
                      color: Colors.white54,
                      size: 16,
                    ),
                  ),
                ],
              ),
            );
          }),

          const Divider(color: Color(0xFF3A3A3C), height: 1),
          Padding(
            padding: EdgeInsets.only(
              left: 12,
              right: 12,
              top: 8,
              bottom: bottomInset + 12,
            ),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: controller.commentController,
                    focusNode: controller.commentFocusNode,
                    style: AppFonts.base(color: Colors.white, fontSize: 14),
                    decoration: InputDecoration(
                      hintText: 'Add a comment...',
                      hintStyle: AppFonts.base(
                        color: Colors.white38,
                        fontSize: 14,
                      ),
                      filled: true,
                      fillColor: const Color(0xFF2C2C2E),
                      contentPadding: const EdgeInsets.symmetric(
                        horizontal: 16,
                        vertical: 10,
                      ),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(20),
                        borderSide: BorderSide.none,
                      ),
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                Obx(
                  () => controller.isPostingComment.value
                      ? const SizedBox(
                          width: 36,
                          height: 36,
                          child: CircularProgressIndicator(
                            color: AppColors.primary,
                            strokeWidth: 2,
                          ),
                        )
                      : GestureDetector(
                          onTap: () => controller.postComment(outfit.id),
                          child: Text(
                            'Post',
                            style: AppFonts.base(
                              color: AppColors.primary,
                              fontSize: 14,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildPostOwnerCaption() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CircleAvatar(
            radius: 18,
            backgroundColor: Colors.grey.shade800,
            backgroundImage:
                outfit.ownerAvatar != null && outfit.ownerAvatar!.isNotEmpty
                ? NetworkImage(outfit.ownerAvatar!)
                : const AssetImage('assets/images/avatar.png') as ImageProvider,
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                RichText(
                  text: TextSpan(
                    style: AppFonts.base(
                      color: Colors.white,
                      fontSize: 13,
                      height: 1.4,
                    ),
                    children: [
                      TextSpan(
                        text: '${outfit.ownerName ?? "User"} ',
                        style: const TextStyle(fontWeight: FontWeight.bold),
                      ),
                      TextSpan(text: outfit.description ?? outfit.name),
                    ],
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  'Author',
                  style: AppFonts.base(
                    color: AppColors.primary,
                    fontSize: 11,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildCommentItem(
    OutfitCommentResponse comment, {
    bool isReply = false,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: EdgeInsets.only(
            left: isReply ? 46.0 : 16.0,
            right: 16.0,
            top: 6.0,
            bottom: 6.0,
          ),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              CircleAvatar(
                radius: isReply ? 12 : 18,
                backgroundColor: Colors.grey.shade800,
                backgroundImage:
                    comment.userAvatar != null && comment.userAvatar!.isNotEmpty
                    ? NetworkImage(comment.userAvatar!)
                    : null,
                child: comment.userAvatar == null || comment.userAvatar!.isEmpty
                    ? Text(
                        comment.username.isNotEmpty
                            ? comment.username[0].toUpperCase()
                            : '?',
                        style: AppFonts.base(
                          color: Colors.white,
                          fontSize: isReply ? 10 : 12,
                        ),
                      )
                    : null,
              ),
              const SizedBox(width: 10),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    RichText(
                      text: TextSpan(
                        style: AppFonts.base(
                          color: Colors.white,
                          fontSize: 13,
                          height: 1.4,
                        ),
                        children: [
                          TextSpan(
                            text: '${comment.username} ',
                            style: const TextStyle(fontWeight: FontWeight.bold),
                          ),
                          TextSpan(text: comment.content),
                        ],
                      ),
                    ),
                    const SizedBox(height: 4),
                    Row(
                      children: [
                        Text(
                          comment.timeAgo,
                          style: AppFonts.base(
                            color: Colors.white38,
                            fontSize: 11,
                          ),
                        ),
                        if (comment.likeCount > 0) ...[
                          const SizedBox(width: 12),
                          Text(
                            '${comment.likeCount} like${comment.likeCount > 1 ? "s" : ""}',
                            style: AppFonts.base(
                              color: Colors.white38,
                              fontSize: 11,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                        const SizedBox(width: 12),
                        GestureDetector(
                          onTap: () => controller.enterReplyMode(comment),
                          child: Text(
                            'Reply',
                            style: AppFonts.base(
                              color: Colors.white38,
                              fontSize: 11,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
              GestureDetector(
                onTap: () => controller.toggleLikeComment(comment.id),
                child: Container(
                  padding: const EdgeInsets.all(4),
                  child: Icon(
                    comment.isLiked ? Icons.favorite : Icons.favorite_border,
                    color: comment.isLiked ? Colors.red : Colors.white30,
                    size: 14,
                  ),
                ),
              ),
            ],
          ),
        ),

        if (comment.replies.isNotEmpty)
          ...comment.replies.map(
            (reply) => _buildCommentItem(reply, isReply: true),
          ),
      ],
    );
  }
}
