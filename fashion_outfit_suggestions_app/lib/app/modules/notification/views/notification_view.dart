import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../../core/models/notification_response.dart';
import '../../../../core/models/notification_type.dart';
import '../../../../core/models/user_profile_response.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_fonts.dart';
import '../controllers/notification_controller.dart';

class NotificationView extends GetView<NotificationController> {
  const NotificationView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: AppColors.background,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.chevron_left, color: Colors.white, size: 30),
          onPressed: () => Get.back(),
        ),
        title: Text(
          'Notifications',
          style: AppFonts.base(
            color: Colors.white,
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
      ),
      body: Obx(() {
        if (controller.isLoading.value) {
          return const Center(
            child: CircularProgressIndicator(color: Colors.white),
          );
        }

        // Chia nhóm các thông báo theo tháng này và trước đó
        final now = DateTime.now();
        final thisMonthLimit = now.subtract(const Duration(days: 30));

        final thisMonth = controller.notification
            .where((n) => n.createdAt.isAfter(thisMonthLimit))
            .toList();

        final earlier = controller.notification
            .where((n) => n.createdAt.isBefore(thisMonthLimit))
            .toList();

        return ListView(
          physics: const BouncingScrollPhysics(),
          children: [
            // Section 1: Follow Requests Header (nếu có yêu cầu)
            if (thisMonth.any((n) => n.type == NotificationType.friendRequest))
              _buildFollowRequestsHeader(
                thisMonth
                    .where((n) => n.type == NotificationType.friendRequest)
                    .length,
              ),

            // Section 2: This Month
            if (thisMonth.isNotEmpty) ...[
              _buildSectionHeader('This month'),
              ...List.generate(thisMonth.length, (index) {
                return _buildNotificationItem(
                  thisMonth[index],
                  controller.notification.indexOf(thisMonth[index]),
                );
              }),
            ],

            // Section 3: Earlier
            if (earlier.isNotEmpty) ...[
              _buildSectionHeader('Earlier'),
              ...List.generate(earlier.length, (index) {
                return _buildNotificationItem(
                  earlier[index],
                  controller.notification.indexOf(earlier[index]),
                );
              }),
            ],

            // Section 4: Suggested for you
            _buildSectionHeader('Suggested for you'),
            if (controller.isSuggestionsLoading.value)
              const Padding(
                padding: EdgeInsets.symmetric(vertical: 20),
                child: Center(
                  child: CircularProgressIndicator(
                    color: Colors.white30,
                    strokeWidth: 2,
                  ),
                ),
              )
            else if (controller.suggestedUsers.isEmpty)
              Padding(
                padding: const EdgeInsets.all(16),
                child: Text(
                  'No suggestions available',
                  style: AppFonts.base(color: Colors.white38, fontSize: 13),
                ),
              )
            else
              ...List.generate(controller.suggestedUsers.length, (index) {
                return _buildSuggestionItem(
                  controller.suggestedUsers[index],
                  index,
                );
              }),
          ],
        );
      }),
    );
  }

  Widget _buildSectionHeader(String title) {
    return Padding(
      padding: const EdgeInsets.only(left: 16, top: 16, bottom: 8),
      child: Text(
        title,
        style: AppFonts.base(
          color: Colors.white,
          fontSize: 15,
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }

  Widget _buildFollowRequestsHeader(int count) {
    return ListTile(
      leading: Container(
        width: 44,
        height: 44,
        decoration: BoxDecoration(
          shape: BoxShape.circle,
          border: Border.all(color: Colors.white24),
        ),
        child: const Icon(
          Icons.group_add_outlined,
          color: Colors.white,
          size: 22,
        ),
      ),
      title: Text(
        'Follow requests',
        style: AppFonts.base(
          color: Colors.white,
          fontSize: 14,
          fontWeight: FontWeight.bold,
        ),
      ),
      subtitle: Text(
        '$count requests pending',
        style: AppFonts.base(color: Colors.white38, fontSize: 12),
      ),
      trailing: const Icon(Icons.chevron_right, color: Colors.white30),
      onTap: () {},
    );
  }

  Widget _buildNotificationItem(
    NotificationResponse notification,
    int mainIndex,
  ) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Row(
        children: [
          // Avatar người tương tác
          CircleAvatar(
            radius: 22,
            backgroundColor: Colors.grey.shade800,
            backgroundImage:
                notification.actorAvatar != null &&
                    notification.actorAvatar!.isNotEmpty
                ? NetworkImage(notification.actorAvatar!)
                : null,
            child:
                notification.actorAvatar == null ||
                    notification.actorAvatar!.isEmpty
                ? Text(
                    notification.actorName.isNotEmpty
                        ? notification.actorName[0].toUpperCase()
                        : '?',
                    style: AppFonts.base(color: Colors.white),
                  )
                : null,
          ),
          const SizedBox(width: 12),
          // Nội dung thông báo
          Expanded(
            child: RichText(
              text: TextSpan(
                style: AppFonts.base(
                  color: Colors.white,
                  fontSize: 13,
                  height: 1.4,
                ),
                children: [
                  TextSpan(
                    text: '${notification.actorName} ',
                    style: const TextStyle(fontWeight: FontWeight.bold),
                  ),
                  TextSpan(text: '${notification.content}  '),
                  TextSpan(
                    text: notification.timeAgo,
                    style: const TextStyle(color: Colors.white30, fontSize: 11),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(width: 12),
          // Nút hành động bên phải
          _buildActionForNotification(notification, mainIndex),
        ],
      ),
    );
  }

  Widget _buildActionForNotification(
    NotificationResponse notification,
    int mainIndex,
  ) {
    if (notification.type == NotificationType.friendRequest) {
      // Yêu cầu theo dõi -> Nút xác nhận và xóa
      return Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF0095F6),
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
              minimumSize: Size.zero,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(6),
              ),
            ),
            onPressed: () => controller.acceptFriendRequest(
              notification.targetId,
              mainIndex,
            ),
            child: Text(
              'Confirm',
              style: AppFonts.base(fontSize: 12, fontWeight: FontWeight.bold),
            ),
          ),
          const SizedBox(width: 6),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF262626),
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
              minimumSize: Size.zero,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(6),
              ),
            ),
            onPressed: () => controller.rejectFriendRequest(
              notification.targetId,
              mainIndex,
            ),
            child: Text(
              'Delete',
              style: AppFonts.base(fontSize: 12, fontWeight: FontWeight.bold),
            ),
          ),
        ],
      );
    } else if (notification.type == NotificationType.outfitLike ||
        notification.type == NotificationType.outfitComment) {
      // Like / comment -> Ảnh thu nhỏ outfit
      return Container(
        width: 40,
        height: 40,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(4),
          color: Colors.grey.shade900,
        ),
        child: const Icon(
          Icons.photo_outlined,
          color: Colors.white30,
          size: 20,
        ),
      );
    }
    return const SizedBox.shrink();
  }

  Widget _buildSuggestionItem(UserProfileResponse user, int index) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Row(
        children: [
          CircleAvatar(
            radius: 22,
            backgroundColor: Colors.grey.shade800,
            backgroundImage:
                user.avatarUrl != null && user.avatarUrl!.isNotEmpty
                ? NetworkImage(user.avatarUrl!)
                : null,
            child: user.avatarUrl == null || user.avatarUrl!.isEmpty
                ? Text(
                    user.displayName != null && user.displayName!.isNotEmpty
                        ? user.displayName![0].toUpperCase()
                        : '?',
                    style: AppFonts.base(color: Colors.white),
                  )
                : null,
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  user.username ?? 'user',
                  style: AppFonts.base(
                    color: Colors.white,
                    fontSize: 13,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                Text(
                  user.displayName ?? '',
                  style: AppFonts.base(color: Colors.white60, fontSize: 12),
                ),
                Text(
                  'Suggested for you',
                  style: AppFonts.base(color: Colors.white30, fontSize: 11),
                ),
              ],
            ),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF0095F6),
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              minimumSize: Size.zero,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(6),
              ),
            ),
            onPressed: () => controller.followUser(user.id, index),
            child: Text(
              'Follow',
              style: AppFonts.base(fontSize: 12, fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
    );
  }
}
