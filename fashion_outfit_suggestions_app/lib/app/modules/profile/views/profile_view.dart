import 'package:fashion_outfit_suggestions_app/core/theme/app_fonts.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../../../../core/constants/profile_type.dart';
import '../../../../core/theme/app_colors.dart';
import '../controllers/profile_controller.dart';

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
              children: [const SizedBox(height: 10)],
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
                onPressed: () {},
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
              child: ,
            )
          ],
        ],
      ),
    );
  }
}
