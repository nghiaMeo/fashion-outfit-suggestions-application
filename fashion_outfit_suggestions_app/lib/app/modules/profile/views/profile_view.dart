import 'package:fashion_outfit_suggestions_app/core/theme/app_fonts.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../../../../core/constants/profile_type.dart';
import '../../../../core/theme/app_colors.dart';
import '../controllers/profile_controller.dart';
import 'edit_profile_view.dart';

class ProfileView extends GetView<ProfileController> {
  const ProfileView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: Obx(() {
          final username = controller.profile.value?.username ?? "Loading....";
          return Text(
            username,
            style: AppFonts.base(
              color: Colors.white,
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          );
        }),
        leading: controller.targetUserId != null
            ? IconButton(
                onPressed: () => Get.back(),
                icon: Icon(Icons.arrow_back_ios, color: Colors.white),
              )
            : null,
        centerTitle: true,
      ),
      body: Obx(() {
        if (controller.isLoading.value) {
          return const Center(
            child: CircularProgressIndicator(color: AppColors.primary),
          );
        }
        final profile = controller.profile.value;
        if (profile == null) {
          return Center(
            child: Text(
              'Can\'t load profile',
              style: AppFonts.base(color: Colors.white70),
            ),
          );
        }
        return RefreshIndicator(
          onRefresh: () => controller.fetchProfile(),
          color: AppColors.primary,
          backgroundColor: AppColors.surface,
          child: SingleChildScrollView(
            physics: const AlwaysScrollableScrollPhysics(),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SizedBox(height: 10),
                _buildHeader(profile),
                const SizedBox(height: 20),
                _buildActionButtons(profile),
                const SizedBox(height: 10),
                _buildSuggestedSection(profile),
                const SizedBox(height: 20),
                _buildOutfitGrid(profile),
              ],
            ),
          ),
        );
      }),
    );
  }

  Widget _buildHeader(dynamic profile) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              CircleAvatar(
                radius: 40,
                backgroundColor: AppColors.surface,
                backgroundImage:
                    profile.avatarUrl != null && profile.avatarUrl!.isNotEmpty
                    ? NetworkImage(profile.avatarUrl!)
                    : const AssetImage('assets/images/avatar.png')
                          as ImageProvider,
              ),
              const Expanded(child: SizedBox()),
              Row(
                children: [
                  _buildStatItem('Outfit', profile.outfitCount),
                  const SizedBox(width: 20),
                  _buildStatItem('Friend', profile.friendCount),
                ],
              ),
              const SizedBox(height: 20),
            ],
          ),
          const SizedBox(height: 15),
          Text(
            profile.displayName ?? '',
            style: AppFonts.base(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          if (profile.bio != null && profile.bio!.isNotEmpty) ...[
            const SizedBox(height: 5),
            Text(
              profile.bio!,
              style: AppFonts.base(color: Colors.white70, fontSize: 14),
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildStatItem(String label, int value) {
    return Column(
      children: [
        Text(
          '$value',
          style: AppFonts.base(
            color: Colors.white,
            fontWeight: FontWeight.bold,
            fontSize: 18,
          ),
        ),
        const SizedBox(height: 4),
        Text(label, style: AppFonts.base(color: Colors.white54, fontSize: 13)),
      ],
    );
  }

  Widget _buildActionButtons(dynamic profile) {
    final type = controller.profileType;
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Row(
        children: [
          if (type == ProfileType.self) ...[
            Expanded(
              child: ElevatedButton(
                onPressed: () {
                  Get.to(() => const EditProfileView());
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.surface,
                  foregroundColor: Colors.white,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                  padding: const EdgeInsets.symmetric(vertical: 12),
                ),
                child: Text(
                  'Edit Profile',
                  style: AppFonts.base(fontWeight: FontWeight.w600),
                ),
              ),
            ),
            const SizedBox(width: 8),
            GestureDetector(
              onTap: () => controller.toggleSuggestions(),
              child: Container(
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(
                  color: AppColors.surface,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Obx(
                  () => Icon(
                    controller.showSuggestion.value
                        ? Icons.person_remove_outlined
                        : Icons.person_add_outlined,
                    color: AppColors.primary,
                    size: 22,
                  ),
                ),
              ),
            ),
          ] else if (type == ProfileType.following) ...[
            Expanded(
              child: ElevatedButton(
                onPressed: () {
                  final profile = controller.profile.value;
                  if (profile != null) {
                    Get.toNamed(
                      '/chat-detail',
                      arguments: {'friendId': profile.id},
                    );
                  }
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.primary,
                  foregroundColor: Colors.black,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                  padding: const EdgeInsets.symmetric(vertical: 12),
                ),
                child: Text(
                  'Message',
                  style: AppFonts.base(color: Colors.white),
                ),
              ),
            ),
            const SizedBox(width: 8),
            Expanded(
              child: OutlinedButton(
                onPressed: () => controller.unfriend(),
                style: OutlinedButton.styleFrom(
                  side: const BorderSide(color: AppColors.error),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                  padding: const EdgeInsets.symmetric(vertical: 12),
                ),
                child: Text(
                  'Unfollow',
                  style: AppFonts.base(
                    color: AppColors.error,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ),
          ] else ...[
            Expanded(
              child: controller.isPending
                  ? ElevatedButton(
                      onPressed: () => controller.unfriend(),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppColors.surface,
                        foregroundColor: Colors.white70,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(8),
                        ),
                        padding: const EdgeInsets.symmetric(vertical: 12),
                      ),
                      child: const Text(
                        'Add Friend Request',
                        style: TextStyle(fontWeight: FontWeight.w600),
                      ),
                    )
                  : ElevatedButton(
                      onPressed: () => controller.sendFriendRequest(
                        controller.targetUserId!,
                      ),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppColors.primary,
                        foregroundColor: Colors.black,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(8),
                        ),
                        padding: const EdgeInsets.symmetric(vertical: 12),
                      ),
                      child: const Text(
                        'Add Friend',
                        style: TextStyle(fontWeight: FontWeight.bold),
                      ),
                    ),
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildSuggestedSection(dynamic profile) {
    return Obx(() {
      if (!controller.showSuggestion.value) return const SizedBox();
      if (controller.isSuggestedLoading.value) {
        return Padding(
          padding: EdgeInsets.all(20),
          child: Center(
            child: CircularProgressIndicator(color: AppColors.primary),
          ),
        );
      }
      if (controller.suggestedUsers.isEmpty) {
        return const SizedBox();
      }
      return Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: EdgeInsets.symmetric(horizontal: 20, vertical: 10),
            child: Text(
              'Suggested Users',
              style: AppFonts.base(
                color: Colors.white,
                fontSize: 15,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
          SizedBox(
            height: 175,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              padding: EdgeInsets.symmetric(horizontal: 16),
              itemCount: controller.suggestedUsers.length,
              itemBuilder: (context, index) {
                final user = controller.suggestedUsers[index];
                return _buildSuggestedCard(user);
              },
            ),
          ),
        ],
      );
    });
  }

  Widget _buildSuggestedCard(dynamic user) {
    return Container(
      width: 130,
      margin: const EdgeInsets.symmetric(horizontal: 6),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: AppColors.surface,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: Colors.white10),
      ),
      child: Column(
        children: [
          Stack(
            children: [
              CircleAvatar(
                radius: 26,
                backgroundColor: AppColors.background,
                backgroundImage:
                    user.avatarUrl != null && user.avatarUrl!.isNotEmpty
                    ? NetworkImage(user.avatarUrl!)
                    : const AssetImage('assets/images/default_avatar.png')
                          as ImageProvider,
              ),
              Positioned(
                top: 0,
                right: 0,
                child: GestureDetector(
                  onTap: () => controller.removeSuggestion(user.id),
                  child: Container(
                    decoration: const BoxDecoration(
                      color: Colors.black54,
                      shape: BoxShape.circle,
                    ),
                    padding: EdgeInsets.all(2),
                    child: const Icon(
                      Icons.close,
                      color: Colors.white70,
                      size: 14,
                    ),
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          Text(
            user.displayName ?? user.username ?? '',
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
            style: const TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
              fontSize: 12,
            ),
          ),
          const Expanded(child: SizedBox()),
          SizedBox(
            width: double.infinity,
            height: 28,
            child: ElevatedButton(
              onPressed: () => controller.sendFriendRequest(user.id),
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.primary,
                foregroundColor: Colors.black,
                elevation: 0,
                padding: EdgeInsets.zero,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(6),
                ),
              ),
              child: const Text(
                'Add',
                style: TextStyle(fontWeight: FontWeight.bold, fontSize: 11),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildOutfitGrid(dynamic profile) {
    if (profile.isPrivateProfile &&
        controller.profileType == ProfileType.notFollowing) {
      return Container(
        padding: const EdgeInsets.symmetric(vertical: 60.0),
        alignment: Alignment.center,
        child: Column(
          children: const [
            Icon(Icons.lock_outline, color: Colors.white54, size: 50),
            SizedBox(height: 12),
            Text(
              'Account is private',
              style: TextStyle(
                color: Colors.white70,
                fontWeight: FontWeight.bold,
                fontSize: 16,
              ),
            ),
            SizedBox(height: 4),
            Text(
              'Only followers can see your outfit',
              style: TextStyle(color: Colors.white30, fontSize: 13),
            ),
          ],
        ),
      );
    }
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Padding(
          padding: EdgeInsets.symmetric(horizontal: 20.0, vertical: 10.0),
          child: Text(
            'Outfits',
            style: TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
              fontSize: 16,
            ),
          ),
        ),
        // TODO: Đổ danh sách outfit thực tế của user từ API vào GridView này
        GridView.builder(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          padding: const EdgeInsets.symmetric(horizontal: 20),
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 3,
            crossAxisSpacing: 8,
            mainAxisSpacing: 8,
            childAspectRatio: 1,
          ),
          itemCount: profile.outfitCount > 0 ? profile.outfitCount : 0,
          itemBuilder: (context, index) {
            return Container(
              decoration: BoxDecoration(
                color: AppColors.surface,
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: Colors.white10),
              ),
              child: const Center(
                child: Icon(Icons.checkroom, color: Colors.white30),
              ),
            );
          },
        ),
      ],
    );
  }
}
