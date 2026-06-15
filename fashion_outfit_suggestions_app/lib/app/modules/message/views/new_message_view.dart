import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../../core/theme/app_fonts.dart';
import '../../chat_detail/views/chat_detail_view.dart';
import '../controllers/message_controller.dart';

class NewMessageView extends StatefulWidget {
  const NewMessageView({super.key});

  @override
  State<NewMessageView> createState() => _NewMessageViewState();
}

class _NewMessageViewState extends State<NewMessageView> {
  final MessageController controller = Get.find<MessageController>();
  final TextEditingController searchController = TextEditingController();
  final rxSearchQuery = ''.obs;

  @override
  void initState() {
    super.initState();
    controller.selectedFriend.value = null;
    controller.fetchFriends();
  }

  @override
  void dispose() {
    searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF0F0F0F),
      appBar: AppBar(
        backgroundColor: const Color(0xFF0F0F0F),
        elevation: 0.5,
        bottom: PreferredSize(
          preferredSize: const Size.fromHeight(0.5),
          child: Container(color: Colors.grey.shade900, height: 0.5),
        ),
        leading: IconButton(
          onPressed: () => Get.back(),
          icon: const Icon(Icons.arrow_back, color: Colors.white, size: 26),
        ),
        title: Text(
          'New message',
          style: AppFonts.base(
            color: Colors.white,
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
        actions: [
          Obx(() {
            final hasSelected = controller.selectedFriend.value != null;
            return TextButton(
              onPressed: hasSelected
                  ? () {
                      final friend = controller.selectedFriend.value!;
                      Get.back();
                      Get.to(
                        () => ChatDetailView(
                          friendId: friend.friendId,
                          name: friend.fullName,
                          username: friend.username,
                          avatarUrl: friend.avatarUrl ?? '' ,
                          conversationId: null,
                        ),
                        transition: Transition.rightToLeft,
                      );
                    }
                  : null,
              child: Text(
                'Next',
                style: AppFonts.base(
                  color: hasSelected ? const Color(0xFF3797F3) : const Color(0xFF8E8E93),
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
            );
          }),
          const SizedBox(width: 8),
        ],
      ),
      body: Column(
        children: [
          _buildSearchRow(),
          Divider(color: Colors.grey.shade900, height: 1),
          Expanded(
            child: Obx(() {
              if (controller.isFriendsLoading.value) {
                return const Center(
                  child: CircularProgressIndicator(color: Colors.white),
                );
              }

              final query = rxSearchQuery.value.trim().toLowerCase();
              final filteredFriends = controller.friends.where((f) {
                final nameMatches = f.fullName.toLowerCase().contains(query);
                final usernameMatches = f.username.toLowerCase().contains(query);
                return nameMatches || usernameMatches;
              }).toList();

              if (filteredFriends.isEmpty) {
                return Center(
                  child: Text(
                    'No friends found',
                    style: AppFonts.base(color: const Color(0xFF8E8E93), fontSize: 16),
                  ),
                );
              }

              return ListView.builder(
                physics: const BouncingScrollPhysics(),
                itemCount: filteredFriends.length,
                itemBuilder: (context, index) {
                  final friend = filteredFriends[index];
                  return Obx(() {
                    final isSelected = controller.selectedFriend.value?.friendId == friend.friendId;
                    return ListTile(
                      onTap: () {
                        if (isSelected) {
                          controller.selectedFriend.value = null;
                        } else {
                          controller.selectedFriend.value = friend;
                        }
                      },
                      leading: CircleAvatar(
                        radius: 22,
                        backgroundColor: Colors.grey.shade800,
                        backgroundImage: friend.avatarUrl != null && friend.avatarUrl!.isNotEmpty
                            ? NetworkImage(friend.avatarUrl!)
                            : null,
                        child: friend.avatarUrl == null || friend.avatarUrl!.isEmpty
                            ? Text(
                                friend.fullName.isNotEmpty
                                    ? friend.fullName[0].toUpperCase()
                                    : '?',
                                style: AppFonts.base(color: Colors.white),
                              )
                            : null,
                      ),
                      title: Text(
                        friend.fullName,
                        style: AppFonts.base(
                          color: Colors.white,
                          fontSize: 15,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      subtitle: Text(
                        friend.username,
                        style: AppFonts.base(
                          color: const Color(0xFF8E8E93),
                          fontSize: 13,
                        ),
                      ),
                      trailing: Container(
                        width: 24,
                        height: 24,
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          border: Border.all(
                            color: isSelected ? const Color(0xFF3797F3) : const Color(0xFF3A3A3C),
                            width: 1.5,
                          ),
                          color: isSelected ? const Color(0xFF3797F3) : Colors.transparent,
                        ),
                        child: isSelected
                            ? const Icon(Icons.check, color: Colors.white, size: 16)
                            : null,
                      ),
                    );
                  });
                },
              );
            }),
          ),
        ],
      ),
    );
  }

  Widget _buildSearchRow() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      child: Row(
        children: [
          Text(
            'To: ',
            style: AppFonts.base(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(width: 8),
          Expanded(
            child: TextField(
              controller: searchController,
              onChanged: (val) => rxSearchQuery.value = val,
              style: AppFonts.base(color: Colors.white, fontSize: 16),
              decoration: InputDecoration(
                hintText: 'Search...',
                hintStyle: AppFonts.base(color: const Color(0xFF8E8E93), fontSize: 16),
                border: InputBorder.none,
                isDense: true,
                contentPadding: EdgeInsets.zero,
              ),
            ),
          ),
        ],
      ),
    );
  }
}
