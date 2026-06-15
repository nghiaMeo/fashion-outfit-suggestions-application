import 'package:fashion_outfit_suggestions_app/core/theme/app_fonts.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../chat_detail/views/chat_detail_view.dart';
import '../../home/controllers/home_controller.dart';
import '../controllers/message_controller.dart';
import 'new_message_view.dart';

class MessageView extends GetView<MessageController> {
  const MessageView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          onPressed: () {
            final homeController = Get.find<HomeController>();
            homeController.changPage(0);
          },
          icon: const Icon(Icons.arrow_back_ios, color: Colors.white),
        ),
        title: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text('Messages', style: AppFonts.base(fontWeight: FontWeight.bold, fontSize: 18)),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.edit_note_outlined, color: Colors.white, size: 24),
            onPressed: () {
              Get.to(
                    () => const NewMessageView(),
                transition: Transition.downToUp,
              );
            },
          ),
        ],
        centerTitle: true,
      ),
      body: RefreshIndicator(
        onRefresh: () => controller.fetchConversations(),
        color: Colors.white,
        backgroundColor: const Color(0xFF262626),
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(parent: BouncingScrollPhysics()),
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SizedBox(height: 16),
                _buildSearchBar(context),
                const SizedBox(height: 20),
                _buildNotesFriends(context),
                const SizedBox(height: 12),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      'Messages',
                      style: AppFonts.base(
                        color: Colors.white,
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    TextButton(
                      onPressed: () {},
                      child: Text(
                        'Requests',
                        style: AppFonts.base(
                          color: const Color(0xFF8E8E93),
                          fontSize: 14,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                _buildChatList(), // <--- Gọi hàm hiển thị list chat thực tế
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildSearchBar(BuildContext context) {
    return Container(
      height: 42,
      decoration: BoxDecoration(
        color: const Color(0xFF1C1C1E),
        borderRadius: BorderRadius.circular(12),
      ),
      child: TextField(
        style: AppFonts.base(color: Colors.white, fontSize: 16),
        decoration: InputDecoration(
          hintText: 'Search',
          hintStyle: AppFonts.base(color: const Color(0xFF8E8E93), fontSize: 16),
          prefixIcon: const Icon(Icons.search, color: Color(0xFF8E8E93), size: 20),
          border: InputBorder.none,
          contentPadding: const EdgeInsets.symmetric(vertical: 9),
        ),
      ),
    );
  }

  Widget _buildNotesFriends(BuildContext context) {
    return SizedBox(
      height: 125,
      child: ListView(
        scrollDirection: Axis.horizontal,
        physics: const BouncingScrollPhysics(),
        children: [
          _buildNoteItem(
            avatarUrl: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150',
            name: 'Your note',
            noteText: "What's on your mind?",
          ),
          _buildNoteItem(
            avatarUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150',
            name: 'Nhật Nam',
            songName: 'Cho Con (L...',
          ),
          _buildNoteItem(
            avatarUrl: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150',
            name: 'Kaylee Thuy Phu...',
            songName: 'Too Good ...',
          ),
        ],
      ),
    );
  }

  Widget _buildNoteItem({
    required String avatarUrl,
    required String name,
    String? noteText,
    String? songName,
  }) {
    final bool hasMusic = songName != null;
    return Container(
      width: 90,
      margin: const EdgeInsets.only(right: 12),
      child: Column(
        children: [
          Stack(
            alignment: Alignment.topCenter,
            clipBehavior: Clip.none,
            children: [
              Container(
                margin: const EdgeInsets.only(top: 25),
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  border: Border.all(color: Colors.grey.shade900, width: 1),
                ),
                child: CircleAvatar(
                  radius: 34,
                  backgroundImage: NetworkImage(avatarUrl),
                  backgroundColor: Colors.grey.shade800,
                ),
              ),
              Positioned(
                top: 0,
                child: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                  constraints: const BoxConstraints(maxWidth: 85),
                  decoration: BoxDecoration(
                    color: const Color(0xFF262626),
                    borderRadius: BorderRadius.circular(15),
                  ),
                  child: hasMusic
                      ? Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      const Icon(Icons.music_note, color: Colors.white, size: 10),
                      const SizedBox(height: 2),
                      Expanded(
                        child: Text(
                          songName,
                          style: AppFonts.base(
                            color: Colors.white,
                            fontSize: 9,
                            fontWeight: FontWeight.bold,
                          ),
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                    ],
                  )
                      : Text(
                    noteText ?? '',
                    style: AppFonts.base(color: const Color(0xFFEFEFEF), fontSize: 9),
                    textAlign: TextAlign.center,
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          Text(
            name,
            style: AppFonts.base(color: const Color(0xFF8E8E93), fontSize: 12),
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }

  // Hàm hiển thị danh sách hội thoại thực tế
  Widget _buildChatList() {
    return Obx(() {
      if (controller.isLoading.value) {
        return const Center(
          child: Padding(
            padding: EdgeInsets.symmetric(vertical: 40),
            child: CircularProgressIndicator(color: Colors.white),
          ),
        );
      }

      if (controller.conversations.isEmpty) {
        return Center(
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 40),
            child: Text(
              'No conversations',
              style: AppFonts.base(color: const Color(0xFF8E8E93), fontSize: 16),
            ),
          ),
        );
      }

      return ListView.builder(
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        itemCount: controller.conversations.length,
        itemBuilder: (BuildContext context, int index) {
          final conv = controller.conversations[index];
          final timeStr = _formatMessageTime(conv.lastMessageAt);
          final avatar = conv.friendAvatar ?? '';
          final name = conv.friendName ?? 'Unknown';

          return GestureDetector(
            onTap: () {
              // Đi tới màn hình chat chi tiết và truyền thông tin cần thiết
              Get.to(
                    () => ChatDetailView(
                  friendId: conv.friendId ?? '',
                  name: name,
                  username: name,
                  avatarUrl: avatar,
                  conversationId: conv.conversationId,
                ),
                transition: Transition.rightToLeft,
              );
            },
            child: _buildChatItemRow(
              avatarUrl: avatar,
              name: name,
              lastMessage: conv.lastMessage ?? 'Start a conversation',
              time: timeStr ?? '',
              unreadCount: conv.unreadCount ?? 0,
            ),
          );
        },
      );
    });
  }

  // Widget hiển thị một dòng hội thoại đơn lẻ (Sửa lỗi đệ quy ban đầu)
  Widget _buildChatItemRow({
    required String avatarUrl,
    required String name,
    required String lastMessage,
    required String time,
    int unreadCount = 0,
  }) {
    final bool hasUnread = unreadCount > 0;
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 10.0),
      color: Colors.transparent,
      child: Row(
        children: [
          CircleAvatar(
            radius: 28,
            backgroundColor: Colors.grey.shade900,
            backgroundImage: avatarUrl.isNotEmpty ? NetworkImage(avatarUrl) : null,
            child: avatarUrl.isEmpty
                ? Text(
              name.isNotEmpty ? name[0].toUpperCase() : '?',
              style: AppFonts.base(color: Colors.white, fontSize: 18),
            )
                : null,
          ),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  name,
                  style: AppFonts.base(
                    color: Colors.white,
                    fontSize: 15,
                    fontWeight: hasUnread ? FontWeight.bold : FontWeight.w600,
                  ),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
                const SizedBox(height: 5),
                Text(
                  lastMessage,
                  style: AppFonts.base(
                    color: hasUnread ? Colors.white : const Color(0xFF8E8E93),
                    fontSize: 13,
                    fontWeight: hasUnread ? FontWeight.bold : FontWeight.normal,
                  ),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
          ),
          const SizedBox(width: 8),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                time,
                style: AppFonts.base(
                  color: hasUnread ? const Color(0xFF3797F3) : const Color(0xFF8E8E93),
                  fontSize: 12,
                ),
              ),
              if (hasUnread) ...[
                const SizedBox(height: 5),
                Container(
                  padding: const EdgeInsets.all(5),
                  decoration: const BoxDecoration(
                    color: Color(0xFF3797F3),
                    shape: BoxShape.circle,
                  ),
                  child: Text(
                    '$unreadCount',
                    style: AppFonts.base(
                      color: Colors.white,
                      fontSize: 10,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ],
            ],
          ),
        ],
      ),
    );
  }

  String? _formatMessageTime(String? lastMessageAt) {
    if (lastMessageAt == null) return '';
    try {
      final dateTime = DateTime.parse(lastMessageAt).toLocal();
      final difference = DateTime.now().difference(dateTime);
      if (difference.inMinutes < 1) return 'just now';
      if (difference.inMinutes < 60) {
        return '${difference.inMinutes}m ago';
      }
      if (difference.inHours < 24) return '${difference.inHours}h ago';
      if (difference.inDays < 7) return '${difference.inDays}d ago';
      return '${difference.inDays ~/ 7}w ago';
    } catch (e) {
      return '';
    }
  }
}