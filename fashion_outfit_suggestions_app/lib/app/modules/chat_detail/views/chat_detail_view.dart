import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../../../../core/models/message_response.dart';
import '../../../../core/theme/app_fonts.dart';
import '../controllers/chat_detail_controller.dart';

class ChatDetailView extends GetView<ChatDetailController> {
  final String friendId;
  final String name;
  final String username;
  final String avatarUrl;
  final String? conversationId;

  const ChatDetailView({
    super.key,
    required this.friendId,
    required this.name,
    required this.username,
    required this.avatarUrl,
    this.conversationId,
  });

  @override
  Widget build(BuildContext context) {
    final controller = Get.put(
      ChatDetailController(
        friendId: friendId,
        friendName: name,
        friendAvatar: avatarUrl,
        conversationId: conversationId,
      ),
      tag: friendId,
    );

    return Scaffold(
      backgroundColor: Color(0xFF0F0F0F),
      appBar: _buildAppBar(context),
      body: Column(
        children: [
          Expanded(
            child: Obx(() {
              if (controller.isLoading.value) {
                return const Center(
                  child: CircularProgressIndicator(color: Colors.white),
                );
              }
              if (controller.messages.isEmpty) {
                return SingleChildScrollView(
                  physics: const AlwaysScrollableScrollPhysics(),
                  child: Container(
                    width: double.infinity,
                    padding: const EdgeInsets.only(top: 80),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        CircleAvatar(
                          radius: 56,
                          backgroundColor: Colors.grey.shade800,
                          backgroundImage: avatarUrl.isNotEmpty
                              ? NetworkImage(avatarUrl)
                              : null,
                          child: avatarUrl.isEmpty
                              ? Text(
                                  name.isNotEmpty ? name[0].toUpperCase() : '?',
                                  style: const TextStyle(
                                    color: Colors.white,
                                    fontSize: 32,
                                  ),
                                )
                              : null,
                        ),
                        const SizedBox(height: 16),
                        Text(
                          name,
                          style: AppFonts.base(
                            color: Colors.white,
                            fontSize: 20,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(height: 6),
                        Text(
                          '$username · Instagram',
                          style: AppFonts.base(
                            color: Color(0xFF8E8E93),
                            fontSize: 14,
                          ),
                        ),
                        const SizedBox(height: 16),
                        GestureDetector(
                          onTap: () {},
                          child: Container(
                            padding: const EdgeInsets.symmetric(
                              horizontal: 18,
                              vertical: 10,
                            ),
                            decoration: BoxDecoration(
                              color: Color(0xFF262626),
                              borderRadius: BorderRadius.circular(10),
                            ),
                            child: Text(
                              'View profile',
                              style: AppFonts.base(
                                color: Colors.white,
                                fontSize: 14,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                );
              }
              return ListView.builder(
                controller: controller.scrollController,
                padding: const EdgeInsets.symmetric(
                  vertical: 20,
                  horizontal: 16,
                ),
                itemCount: controller.messages.length,
                itemBuilder: (BuildContext context, int index) {
                  final message = controller.messages[index];
                  final currentUserId = controller.currentUserId;
                  final bool isMe = message.senderId == currentUserId;

                  bool isLastInGroup = true;
                  if (index < controller.messages.length - 1) {
                    final nextMessage = controller.messages[index + 1];
                    if (message.senderId == nextMessage.senderId) {
                      isLastInGroup = false;
                    }
                  }
                  return _buildMessageItem(
                    context,
                    message,
                    isMe,
                    isLastInGroup,
                  );
                },
              );
            }),
          ),
          Obx(() {
            if (controller.rxFriendIsTyping.value) {
              return Padding(
                padding: const EdgeInsets.symmetric(
                  horizontal: 16,
                  vertical: 6,
                ),
                child: Row(
                  children: [
                    CircleAvatar(
                      radius: 8,
                      backgroundImage: avatarUrl.isNotEmpty
                          ? NetworkImage(avatarUrl)
                          : null,
                    ),
                    const SizedBox(width: 8),
                    Text(
                      '$name Drafting a message...',
                      style: const TextStyle(
                        color: Color(0xFF8E8E93),
                        fontSize: 12,
                        fontStyle: FontStyle.italic,
                      ),
                    ),
                  ],
                ),
              );
            }
            return const SizedBox.shrink();
          }),
          _buildInputBar(controller),
        ],
      ),
    );
  }

  Widget _buildMessageItem(
    BuildContext context,
    MessageResponse message,
    bool isMe,
    bool isLastInGroup,
  ) {
    final bool isImage = message.type == 'IMAGE' || message.imageUrl != null;
    return Padding(
      padding: const EdgeInsets.only(bottom: 6.0),
      child: Row(
        mainAxisAlignment: isMe
            ? MainAxisAlignment.end
            : MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          if (!isMe)
            SizedBox(
              width: 32,
              child: isLastInGroup
                  ? CircleAvatar(
                      radius: 13,
                      backgroundImage: NetworkImage(avatarUrl),
                    )
                  : const SizedBox.shrink(),
            ),
          const SizedBox(height: 8),
          Column(
            crossAxisAlignment: isMe
                ? CrossAxisAlignment.end
                : CrossAxisAlignment.start,
            children: [
              isImage
                  ? Container(
                      constraints: BoxConstraints(maxWidth: 160),
                      child: ClipRRect(
                        borderRadius: BorderRadius.circular(16),
                        child: Image.network(
                          message.imageUrl!,
                          fit: BoxFit.cover,
                        ),
                      ),
                    )
                  : Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 16,
                        vertical: 10,
                      ),
                      constraints: BoxConstraints(
                        maxWidth: MediaQuery.of(context).size.width * 0.7,
                      ),
                      decoration: BoxDecoration(
                        color: isMe ? Color(0xFF3797F3) : Color(0xFF262626),
                        borderRadius: BorderRadius.circular(18),
                      ),
                      child: Text(
                        message.content ?? '',
                        style: AppFonts.base(color: Colors.white, fontSize: 15),
                      ),
                    ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildInputBar(ChatDetailController controller) {
    return Container(
      padding: const EdgeInsets.only(left: 16, right: 16, bottom: 24, top: 8),
      color: const Color(0xFF0F0F0F),
      child: Container(
        height: 48,
        decoration: BoxDecoration(
          color: Color(0xFF1C1C1E),
          borderRadius: BorderRadius.circular(24),
        ),
        child: Row(
          children: [
            const SizedBox(width: 16),
            Expanded(
              child: TextField(
                controller: controller.textController,
                style: AppFonts.base(color: Colors.white, fontSize: 15),
                onChanged: controller.onTextChanged,
                decoration: InputDecoration(
                  hintText: 'Type a message...',
                  hintStyle: AppFonts.base(
                    color: Color(0xFF8E8E93),
                    fontSize: 15,
                  ),
                  border: InputBorder.none,
                ),
              ),
            ),
            Obx(
              () => AnimatedCrossFade(
                duration: const Duration(milliseconds: 150),
                crossFadeState: controller.isTyping.value
                    ? CrossFadeState.showSecond
                    : CrossFadeState.showFirst,
                firstChild: Row(
                  children: [
                    IconButton(
                      onPressed: () {},
                      icon: Icon(Icons.mic_none, color: Colors.white, size: 22),
                    ),
                    IconButton(
                      onPressed: () {},
                      icon: Icon(
                        Icons.image_outlined,
                        color: Colors.white,
                        size: 22,
                      ),
                    ),
                    IconButton(
                      onPressed: () {},
                      icon: Icon(
                        Icons.sentiment_satisfied_alt_outlined,
                        color: Colors.white,
                        size: 22,
                      ),
                    ),
                    const SizedBox(width: 4),
                  ],
                ),
                secondChild: Padding(
                  padding: const EdgeInsets.only(right: 8.0),
                  child: TextButton(
                    onPressed: controller.sendMessage,
                    child: Text(
                      'Send',
                      style: AppFonts.base(
                        color: Color(0xFF3797F3),
                        fontWeight: FontWeight.bold,
                        fontSize: 15,
                      ),
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  AppBar _buildAppBar(BuildContext context) {
    return AppBar(
      backgroundColor: Color(0xFF0F0F0F),
      elevation: 0.5,
      bottom: PreferredSize(
        preferredSize: Size.fromHeight(0.5),
        child: Container(color: Colors.grey.shade900, height: 0.5),
      ),
      leading: IconButton(
        onPressed: () => Get.back(),
        icon: Icon(Icons.arrow_back, color: Colors.white, size: 26),
      ),
      titleSpacing: 0,
      title: Row(
        children: [
          CircleAvatar(radius: 18, backgroundImage: NetworkImage(avatarUrl)),
          const SizedBox(width: 10),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  name,
                  style: AppFonts.base(
                    color: Colors.white,
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                  overflow: TextOverflow.ellipsis,
                ),
                const SizedBox(height: 2),
                Text(
                  username,
                  style: AppFonts.base(color: Color(0xFF8E8E93), fontSize: 12),
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
          ),
        ],
      ),
      actions: [
        IconButton(
          onPressed: () {},
          icon: Icon(Icons.info_outline, color: Colors.white, size: 26),
        ),
        const SizedBox(width: 8),
      ],
    );
  }
}
