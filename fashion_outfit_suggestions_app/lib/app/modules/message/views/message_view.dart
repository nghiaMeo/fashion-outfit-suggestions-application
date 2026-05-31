import 'package:fashion_outfit_suggestions_app/core/theme/app_fonts.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../../home/controllers/home_controller.dart';
import '../controllers/message_controller.dart';

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
          icon: Icon(Icons.arrow_back_ios, color: Colors.white),
        ),

        title: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text('nghiamewo_ss', style: AppFonts.base()),
            IconButton(
              color: Colors.white,
              onPressed: () {},
              icon: Icon(Icons.keyboard_arrow_down_outlined),
            ),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.edit_square, color: Colors.white, size: 24),
            onPressed: () {},
          ),
        ],
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        physics: const BouncingScrollPhysics(),
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
                        color: Color(0xFF8E8E93),
                        fontSize: 14,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              _buildChatItem(
                avatarUrl:
                    'https://images.unsplash.com/photo-1607990283143-e81e7a2c93ab?w=150',
                name: 'Quang Huy',
                lastMessage: 'Đã ta',
                time: '1w',
              ),
              _buildChatItem(
                avatarUrl:
                    'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150',
                name: 'Kaylee Thuy Phuong Truong',
                lastMessage: 'Kaylee sent a sticker.',
                time: '1w',
              ),
              _buildChatItem(
                avatarUrl:
                    'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150',
                name: 'Nhật Nam',
                lastMessage: 'Có gì đâu',
                time: '2w',
              ),
              _buildChatItem(
                avatarUrl:
                    'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150',
                name: 'Instagram User',
                lastMessage: 'You: cho mình xin giá những mẫu giống ...',
                time: '12w',
              ),
              _buildChatItem(
                avatarUrl:
                    'https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150',
                name: 'Minh Tâm',
                lastMessage: 'You: đúng rồi',
                time: '24w',
              ),
              _buildChatItem(
                avatarUrl:
                    'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150',
                name: 'Dương Quốc Khánh',
                lastMessage: 'Mà hỏi để coi đi mua á',
                time: '1y',
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildSearchBar(BuildContext context) {
    return Container(
      height: 42,
      decoration: BoxDecoration(
        color: Color(0xFF1C1C1E),
        borderRadius: BorderRadius.circular(12),
      ),
      child: TextField(
        style: AppFonts.base(color: Colors.white, fontSize: 16),
        decoration: InputDecoration(
          hintText: 'Search',
          hintStyle: AppFonts.base(color: Color(0xFF8E8E93), fontSize: 16),
          prefixIcon: Icon(Icons.search, color: Color(0xFF8E8E93), size: 20),
          border: InputBorder.none,
          contentPadding: EdgeInsets.symmetric(vertical: 9),
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
            avatarUrl:
                'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150',
            name: 'Your note',
            noteText: "What's on your mind?",
          ),
          _buildNoteItem(
            avatarUrl:
                'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150',
            name: 'Nhật Nam',
            songName: 'Cho Con (L...',
          ),
          _buildNoteItem(
            avatarUrl:
                'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150',
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
                  padding: const EdgeInsets.symmetric(
                    horizontal: 10,
                    vertical: 6,
                  ),
                  constraints: const BoxConstraints(maxWidth: 85),
                  decoration: BoxDecoration(
                    color: Color(0xFF262626),
                    borderRadius: BorderRadius.circular(15),
                  ),
                  child: hasMusic
                      ? Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            const Icon(
                              Icons.music_note,
                              color: Colors.white,
                              size: 10,
                            ),
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
                          style: AppFonts.base(
                            color: Color(0xFFEFEFEF),
                            fontSize: 9,
                          ),
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
            style: AppFonts.base(color: Color(0xFF8E8E93), fontSize: 12),
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }

  Widget _buildChatItem({
    required String avatarUrl,
    required String name,
    required String lastMessage,
    required String time,
  }) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 16),
      child: Row(
        children: [
          CircleAvatar(
            radius: 28,
            backgroundImage: NetworkImage(avatarUrl),
            backgroundColor: Colors.grey.shade800,
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
                    fontWeight: FontWeight.bold,
                  ),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
                const SizedBox(height: 4),
                Row(
                  children: [
                    Text(
                      lastMessage,
                      style: AppFonts.base(
                        color: Color(0xFF8E8E93),
                        fontSize: 13,
                      ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(width: 4),
                    Text('•', style: AppFonts.base(color: Color(0xFF8E8E93))),
                    const SizedBox(width: 4),
                    Text(
                      time,
                      style: AppFonts.base(
                        color: Color(0xFF8E8E93),
                        fontSize: 14,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
