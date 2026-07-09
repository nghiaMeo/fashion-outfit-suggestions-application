import 'package:fashion_outfit_suggestions_app/app/modules/outfit/controllers/outfit_controller.dart';
import 'package:fashion_outfit_suggestions_app/app/modules/searches/views/searches_view.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../../../../core/models/outfit_response.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_fonts.dart';
import '../../../../core/widgets/app_bottom_nav.dart';
import '../../../routes/app_routes.dart';
import '../../message/views/message_view.dart';
import '../../profile/views/profile_view.dart';
import '../../wardrobe/views/wardrobe_view.dart';
import '../controllers/home_controller.dart';

class HomeView extends GetView<HomeController> {
  const HomeView({super.key});

  @override
  Widget build(BuildContext context) {
    final List<Widget> pages = [
      const HomeFeedView(),
      const SearchesView(),
      const WardrobeView(),
      const MessageView(),
      const ProfileView(),
    ];

    return Scaffold(
      body: Obx(
        () =>
            IndexedStack(index: controller.currentIndex.value, children: pages),
      ),
      bottomNavigationBar: Obx(
        () => AppBottomNav(
          currentIndex: controller.currentIndex.value,
          onTap: controller.changPage,
        ),
      ),
    );
  }
}

class HomeFeedView extends GetView<HomeController> {
  const HomeFeedView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black,
        elevation: 0,
        centerTitle: false,
        title: Text(
          'Stylist',
          style: AppFonts.base(
            color: Colors.white,
            fontSize: 20,
            fontWeight: FontWeight.w500,
          ),
        ),
        actions: [
          IconButton(
            icon: const Icon(
              Icons.add_box_outlined,
              size: 26,
              color: Colors.white,
            ),
            onPressed: () {
              Get.toNamed(Routes.outfit);
            },
          ),
          Obx(() {
            final hasUnread = controller.unreadNotificationsCount.value > 0;
            return Stack(
              clipBehavior: Clip.none,
              children: [
                IconButton(
                  icon: const Icon(
                    Icons.favorite_border,
                    size: 26,
                    color: Colors.white,
                  ),
                  onPressed: () {
                    controller.unreadNotificationsCount.value = 0;
                    Get.toNamed(Routes.notification);
                  },
                ),
                if (hasUnread)
                  Positioned(
                    top: 10,
                    right: 10,
                    child: Container(
                      width: 10,
                      height: 10,
                      decoration: const BoxDecoration(
                        color: Colors.red,
                        shape: BoxShape.circle,
                      ),
                    ),
                  ),
              ],
            );
          }),
        ],
      ),
      body: Obx(() {
        if (controller.isFeedLoading.value) {
          return const Center(
            child: CircularProgressIndicator(color: Colors.white),
          );
        }

        if (controller.feedOutfits.isEmpty) {
          return Center(
            child: Text(
              'No posts yet. Connect with friends or create a public outfit!',
              textAlign: TextAlign.center,
              style: AppFonts.base(color: Colors.white70, fontSize: 16),
            ),
          );
        }
        return RefreshIndicator(
          onRefresh: controller.fetchHomeFeed,
          color: Colors.white,
          backgroundColor: const Color(0xFF262626),
          child: ListView.builder(
            itemCount: controller.feedOutfits.length,
            itemBuilder: (context, index) {
              final outfit = controller.feedOutfits[index];
              return _buildFeedItem(outfit);
            },
          ),
        );
      }),
    );
  }

  Widget _buildFeedItem(OutfitResponse outfit) {
    final currentPage = 0.obs;

    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            child: Row(
              children: [
                CircleAvatar(
                  radius: 18,
                  backgroundColor: const Color(0xFF262626),
                  backgroundImage:
                      outfit.ownerAvatar != null &&
                          outfit.ownerAvatar!.isNotEmpty
                      ? NetworkImage(outfit.ownerAvatar!)
                      : const AssetImage('assets/images/avatar.png')
                            as ImageProvider,
                ),
                const SizedBox(width: 10),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        outfit.ownerName ?? 'User',
                        style: AppFonts.base(
                          color: Colors.white,
                          fontSize: 14,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      Text(
                        _formatTimeAgo(outfit.createdAt),
                        style: AppFonts.base(
                          color: const Color(0xFF8E8E93),
                          fontSize: 11,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),

          if (outfit.items.isNotEmpty)
            AspectRatio(
              aspectRatio: 1,
              child: Stack(
                children: [
                  PageView.builder(
                    itemCount: outfit.items.length,
                    onPageChanged: (val) => currentPage.value = val,
                    itemBuilder: (context, itemIndex) {
                      final item = outfit.items[itemIndex];
                      return Container(
                        color: const Color(0xFF1C1C1E),
                        child: item.imageUrl != null
                            ? Image.network(item.imageUrl!, fit: BoxFit.cover)
                            : const Icon(
                                Icons.checkroom,
                                color: Colors.grey,
                                size: 80,
                              ),
                      );
                    },
                  ),
                  if (outfit.items.length > 1)
                    Positioned(
                      top: 12,
                      right: 12,
                      child: Obx(
                        () => Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 8,
                            vertical: 4,
                          ),
                          decoration: BoxDecoration(
                            color: Colors.black.withOpacity(0.7),
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Text(
                            '${currentPage.value + 1}/${outfit.items.length}',
                            style: AppFonts.base(
                              color: Colors.white,
                              fontSize: 12,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                      ),
                    ),
                ],
              ),
            ),

          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 4, vertical: 4),
            child: Row(
              children: [
                IconButton(
                  icon: Icon(
                    outfit.isLiked ? Icons.favorite : Icons.favorite_border,
                    color: outfit.isLiked ? Colors.red : Colors.white,
                    size: 26,
                  ),
                  onPressed: () => controller.toggleLikeOutfit(outfit.id),
                ),
                Text(
                  '${outfit.likeCount}',
                  style: AppFonts.base(
                    color: Colors.white,
                    fontSize: 14,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const Spacer(),
                IconButton(
                  icon: Icon(
                    outfit.isFavorite ? Icons.bookmark : Icons.bookmark_border,
                    color: outfit.isFavorite ? AppColors.primary : Colors.white,
                    size: 26,
                  ),
                  onPressed: () => controller.toggleFavoriteOutfit(outfit.id),
                ),
              ],
            ),
          ),

          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 12),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                RichText(
                  text: TextSpan(
                    style: AppFonts.base(color: Colors.white, fontSize: 14),
                    children: [
                      TextSpan(
                        text: '${outfit.ownerName ?? "User"} ',
                        style: const TextStyle(fontWeight: FontWeight.bold),
                      ),
                      TextSpan(text: outfit.name),
                    ],
                  ),
                ),
                const SizedBox(height: 4),
                if (outfit.occasion != null && outfit.occasion!.isNotEmpty)
                  Text(
                    '#${outfit.occasion}',
                    style: AppFonts.base(
                      color: const Color(0xFF0095F6),
                      fontSize: 13,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                if (outfit.description != null &&
                    outfit.description!.isNotEmpty) ...[
                  const SizedBox(height: 4),
                  Text(
                    outfit.description!,
                    style: AppFonts.base(color: Colors.white70, fontSize: 13),
                  ),
                ],
              ],
            ),
          ),
          const SizedBox(height: 12),
          const Divider(color: Color(0xFF262626), height: 1),
        ],
      ),
    );
  }

  String _formatTimeAgo(DateTime dateTime) {
    final difference = DateTime.now().difference(dateTime);
    if (difference.inDays > 7) {
      return '${dateTime.day}/${dateTime.month}/${dateTime.year}';
    } else if (difference.inDays >= 1) {
      return '${difference.inDays} days ago';
    } else if (difference.inHours >= 1) {
      return '${difference.inHours} hours ago';
    } else if (difference.inMinutes >= 1) {
      return '${difference.inMinutes} minutes ago';
    } else {
      return 'Just finished';
    }
  }
}
