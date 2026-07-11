import 'package:fashion_outfit_suggestions_app/core/models/outfit_response.dart';
import 'package:flutter/material.dart';
import 'package:get/get_state_manager/src/rx_flutter/rx_obx_widget.dart';
import 'package:get/get_state_manager/src/simple/get_view.dart';

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
          // Handle bar
          Container(
            margin: const EdgeInsets.symmetric(vertical: 10),
            width: 40,
            height: 4,
            decoration: BoxDecoration(
              color: Colors.white30,
              borderRadius: BorderRadius.circular(2),
            ),
          ),

          // Tiêu đề
          Text(
            'Comments',
            style: AppFonts.base(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          const Divider(color: Color(0xFF3A3A3C)),

          // Danh sách bình luận
          Expanded(
            child: Obx(() {
              if (controller.isCommentsLoading.value) {
                return const Center(
                  child: CircularProgressIndicator(color: Colors.white),
                );
              }
              if (controller.comments.isEmpty) {
                return Center(
                  child: Text(
                    'No comments yet.\nBe the first to comment!',
                    textAlign: TextAlign.center,
                    style: AppFonts.base(color: Colors.white38, fontSize: 14),
                  ),
                );
              }
              return ListView.builder(
                padding: const EdgeInsets.symmetric(vertical: 8),
                itemCount: controller.comments.length,
                itemBuilder: (context, index) {
                  final comment = controller.comments[index];
                  return Padding(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 16,
                      vertical: 8,
                    ),
                    child: Row(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        CircleAvatar(
                          radius: 18,
                          backgroundColor: Colors.grey.shade800,
                          backgroundImage:
                              comment.userAvatar != null &&
                                  comment.userAvatar!.isNotEmpty
                              ? NetworkImage(comment.userAvatar!)
                              : null,
                          child:
                              comment.userAvatar == null ||
                                  comment.userAvatar!.isEmpty
                              ? Text(
                                  comment.username.isNotEmpty
                                      ? comment.username[0].toUpperCase()
                                      : '?',
                                  style: AppFonts.base(color: Colors.white),
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
                                      style: const TextStyle(
                                        fontWeight: FontWeight.bold,
                                      ),
                                    ),
                                    TextSpan(text: comment.content),
                                  ],
                                ),
                              ),
                              const SizedBox(height: 4),
                              Text(
                                comment.timeAge,
                                style: AppFonts.base(
                                  color: Colors.white38,
                                  fontSize: 11,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  );
                },
              );
            }),
          ),

          // Ô nhập bình luận phía dưới
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
}
