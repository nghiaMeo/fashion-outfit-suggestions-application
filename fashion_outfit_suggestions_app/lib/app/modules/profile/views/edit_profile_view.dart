import 'package:fashion_outfit_suggestions_app/core/theme/app_colors.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../../core/theme/app_fonts.dart';
import '../../../../core/widgets/dialog_alert.dart';
import '../controllers/profile_controller.dart';

class EditProfileView extends GetView<ProfileController> {
  const EditProfileView({super.key});

  @override
  Widget build(BuildContext context) {
    controller.startEditing();
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: AppColors.background,
        elevation: 0.5,
        bottom: PreferredSize(
          preferredSize: const Size.fromHeight(1),
          child: Container(color: Colors.white12, height: 0.5),
        ),
        leadingWidth: 80,
        leading: TextButton(
          onPressed: () => Get.back(),
          child: Text(
            'Cancel',
            style: AppFonts.base(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.normal,
            ),
          ),
        ),
        title: Text(
          'Edit',
          style: AppFonts.base(
            color: Colors.white,
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
        actions: [
          TextButton(
            onPressed: () async {
              final success = await controller.updateProfile();
              if (success) {
                Get.back();
                DialogAlert(
                  onConfirm: () {
                    Get.back();
                  },
                  color: AppColors.neutral,
                  icon: Icons.check_outlined,
                  title: 'Edit profile success',
                  description: '',
                );
              }
            },
            child: Text(
              'Done',
              style: AppFonts.base(
                color: AppColors.primary,
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ],
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            const SizedBox(height: 20),

            Obx(() {
              final profile = controller.profile.value;
              return Column(
                children: [
                  CircleAvatar(
                    radius: 48,
                    backgroundColor: AppColors.surface,
                    backgroundImage:
                        profile?.avatarUrl != null &&
                            profile!.avatarUrl!.isNotEmpty
                        ? NetworkImage(profile.avatarUrl!)
                        : const AssetImage('assets/images/avatar.png')
                              as ImageProvider,
                  ),
                  const SizedBox(height: 12),
                  GestureDetector(
                    onTap: () => _showChangeAvatarDialog(context),
                    child: Text(
                      'Change Avatar',
                      style: AppFonts.base(
                        color: AppColors.primary,
                        fontWeight: FontWeight.bold,
                        fontSize: 14,
                      ),
                    ),
                  ),
                ],
              );
            }),
            const SizedBox(height: 30),
            _buildInstagramInputRow(
              label: 'Display name',
              controller: controller.displayNameController,
              hintText: 'Enter your display name',
            ),
            _buildInstagramInputRow(
              label: 'Bio',
              controller: controller.bioController,
              hintText: 'Enter your bio',
              isMultiline: true,
            ),
            _buildInstagramInputRow(
              label: 'Link avatar',
              controller: controller.avatarUrlController,
              hintText: 'Enter your avatar URL',
            ),
            const SizedBox(height: 20),
            Padding(
              padding: EdgeInsets.symmetric(horizontal: 16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Divider(color: Colors.white12, height: 1),
                  const SizedBox(height: 10),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(
                        'Account is private',
                        style: AppFonts.base(
                          color: Colors.white,
                          fontSize: 16,
                          fontWeight: FontWeight.normal,
                        ),
                      ),
                      Obx(
                        () => Switch(
                          value: controller.editIsPrivateProfile.value,
                          activeThumbColor: AppColors.primary,
                          onChanged: (value) {
                            controller.editIsPrivateProfile.value = value;
                          },
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Text(
                    'When your account is private, only followers can see your outfit.',
                    style: AppFonts.base(color: Colors.white30, fontSize: 12),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInstagramInputRow({
    required String label,
    required TextEditingController controller,
    required String hintText,
    bool isMultiline = false,
  }) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      decoration: BoxDecoration(
        border: Border(bottom: BorderSide(color: Colors.white12, width: 0.5)),
      ),
      child: Row(
        crossAxisAlignment: isMultiline
            ? CrossAxisAlignment.start
            : CrossAxisAlignment.center,
        children: [
          SizedBox(
            width: 100,
            child: Text(
              label,
              style: AppFonts.base(
                color: Colors.white,
                fontSize: 15,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
          Expanded(
            child: TextField(
              controller: controller,
              maxLines: isMultiline ? null : 1,
              style: AppFonts.base(color: Colors.white, fontSize: 15),
              decoration: InputDecoration(
                hintText: hintText,
                hintStyle: AppFonts.base(color: Colors.white30, fontSize: 15),
                border: InputBorder.none,
                isDense: true,
                contentPadding: EdgeInsets.symmetric(vertical: 6),
              ),
            ),
          ),
        ],
      ),
    );
  }

  void _showChangeAvatarDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          backgroundColor: AppColors.surface,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          title: Text(
            'Change Avatar',
            style: AppFonts.base(color: Colors.white, fontSize: 16),
          ),
          content: TextField(
            controller: controller.avatarUrlController,
            style: AppFonts.base(color: Colors.white),
            decoration: const InputDecoration(
              hintText: 'Enter avatar URL',
              hintStyle: TextStyle(color: Colors.white30),
              enabledBorder: UnderlineInputBorder(
                borderSide: BorderSide(color: Colors.white24),
              ),
              focusedBorder: UnderlineInputBorder(
                borderSide: BorderSide(color: AppColors.primary),
              ),
            ),
          ),
          actions: [
            TextButton(
              onPressed: () {
                Get.back();
              },
              child: Text(
                'Cancel',
                style: AppFonts.base(color: Colors.white54),
              ),
            ),
            TextButton(
              onPressed: () => Get.back(),
              child: Text(
                'Done',
                style: AppFonts.base(
                  color: AppColors.primary,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ],
        );
      },
    );
  }
}
