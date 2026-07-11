import 'package:flutter/material.dart';
import 'package:get/get_state_manager/src/rx_flutter/rx_obx_widget.dart';
import 'package:get/get_state_manager/src/simple/get_view.dart';

import '../../../../core/models/outfit_response.dart';
import '../../../../core/theme/app_fonts.dart';
import '../controllers/home_controller.dart';

class ShareToMessageSheet extends GetView<HomeController> {
  final OutfitResponse outfit;

  const ShareToMessageSheet({required this.outfit});

  @override
  Widget build(BuildContext context) {
    return Container(
      height: MediaQuery.of(context).size.height * 0.65,
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
            'Share to...',
            style: AppFonts.base(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          const Divider(color: Color(0xFF3A3A3C)),

          // Danh sách hội thoại
          Expanded(
            child: Obx(() {
              if (controller.isConversationsLoading.value) {
                return const Center(
                  child: CircularProgressIndicator(color: Colors.white),
                );
              }
              if (controller.conversations.isEmpty) {
                return Center(
                  child: Text(
                    'No conversations yet.',
                    style: AppFonts.base(color: Colors.white38, fontSize: 14),
                  ),
                );
              }
              return ListView.builder(
                itemCount: controller.conversations.length,
                itemBuilder: (context, index) {
                  final conv = controller.conversations[index];
                  return Padding(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 16,
                      vertical: 8,
                    ),
                    child: Row(
                      children: [
                        // Avatar bạn bè
                        CircleAvatar(
                          radius: 24,
                          backgroundColor: Colors.grey.shade800,
                          backgroundImage:
                              conv.friendAvatar != null &&
                                  conv.friendAvatar!.isNotEmpty
                              ? NetworkImage(conv.friendAvatar!)
                              : null,
                          child:
                              conv.friendAvatar == null ||
                                  conv.friendAvatar!.isEmpty
                              ? Text(
                                  conv.friendName?.isNotEmpty == true
                                      ? conv.friendName![0].toUpperCase()
                                      : '?',
                                  style: AppFonts.base(color: Colors.white),
                                )
                              : null,
                        ),
                        const SizedBox(width: 12),

                        // Tên bạn bè
                        Expanded(
                          child: Text(
                            conv.friendName ?? 'Unknown',
                            style: AppFonts.base(
                              color: Colors.white,
                              fontSize: 14,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ),

                        // Nút Send / Sent
                        Obx(() {
                          final alreadySent = controller.sentConversationIds
                              .contains(conv.conversationId);
                          return ElevatedButton(
                            style: ElevatedButton.styleFrom(
                              backgroundColor: alreadySent
                                  ? const Color(0xFF3A3A3C)
                                  : const Color(0xFF0095F6),
                              foregroundColor: Colors.white,
                              padding: const EdgeInsets.symmetric(
                                horizontal: 16,
                                vertical: 8,
                              ),
                              minimumSize: Size.zero,
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(8),
                              ),
                            ),
                            onPressed: alreadySent
                                ? null // Vô hiệu hóa nếu đã gửi
                                : () => controller.shareOutfitToConversation(
                                    conv.conversationId!,
                                    outfit.id,
                                  ),
                            child: Text(
                              alreadySent ? 'Sent' : 'Send',
                              style: AppFonts.base(
                                fontSize: 13,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          );
                        }),
                      ],
                    ),
                  );
                },
              );
            }),
          ),

          const SizedBox(height: 16),
        ],
      ),
    );
  }
}
