import 'dart:ui';

import 'package:fashion_outfit_suggestions_app/app/modules/profile/controllers/profile_controller.dart';
import 'package:fashion_outfit_suggestions_app/core/theme/app_fonts.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../../../../core/theme/app_colors.dart';
import '../../profile/views/edit_profile_view.dart';
import '../controllers/setting_controller.dart';
import 'accounts_center_view.dart';

class SettingView extends GetView<SettingController> {
  const SettingView({super.key});

  @override
  Widget build(BuildContext context) {
    final profileController = Get.find<ProfileController>();
    return Scaffold(
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          onPressed: () => Get.back(),
          icon: Icon(
            Icons.chevron_left,
            color: Theme.of(context).iconTheme.color ?? Colors.white,
            size: 30,
          ),
        ),
        title: Text(
          'Settings and privacy',
          style: AppFonts.base(
            color: Theme.of(context).textTheme.bodyLarge?.color ?? Colors.white,
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              padding: const EdgeInsets.symmetric(horizontal: 12),
              decoration: BoxDecoration(
                color: Get.isDarkMode
                    ? const Color(0xFF262626)
                    : Colors.grey[200],
                borderRadius: BorderRadius.circular(10),
              ),
              child: TextField(
                style: AppFonts.base(
                  color: Get.isDarkMode ? Colors.white : Colors.black,
                  fontSize: 14,
                ),
                decoration: InputDecoration(
                  icon: const Icon(
                    Icons.search,
                    color: Colors.white30,
                    size: 20,
                  ),
                  hintText: 'Search',
                  hintStyle: AppFonts.base(color: Colors.white30, fontSize: 14),
                  border: InputBorder.none,
                  contentPadding: const EdgeInsets.symmetric(vertical: 10),
                ),
              ),
            ),
            const SizedBox(height: 10),
            _buildSectionHeader(context, 'Your account'),
            _buildAccountsCenterCard(context),
            const SizedBox(height: 16),
            _buildSectionHeader(context, 'How you use the App'),
            _buildSettingsTile(
              context: context,
              icon: Icons.person_outline,
              title: 'Edit profile',
              onTap: () => Get.to(() => const EditProfileView()),
            ),
            _buildSettingsTile(
              context: context,
              icon: Icons.notifications_none,
              title: 'Notifications',
              onTap: () => _showComingSoonSnackBar('Notifications'),
            ),
            const SizedBox(height: 16),
            _buildSectionHeader(context, 'Who can see your content'),
            Obx(() {
              final isPrivate =
                  profileController.profile.value?.isPrivateProfile ?? false;
              return _buildSettingsTile(
                context: context,
                icon: Icons.lock_outline,
                title: 'Account privacy',
                trailing: Text(
                  isPrivate ? 'Private' : 'Public',
                  style: AppFonts.base(color: Colors.white30, fontSize: 14),
                ),
                onTap: () {
                  Get.defaultDialog(
                    title: 'Account Privacy',
                    titleStyle: AppFonts.base(
                      color: Colors.white,
                      fontWeight: FontWeight.bold,
                    ),
                    middleText:
                        'You can change this in the Edit Profile screen.',
                    middleTextStyle: AppFonts.base(color: Colors.white70),
                    backgroundColor: const Color(0xFF1E1E1E),
                    confirmTextColor: Colors.black,
                    buttonColor: AppColors.primary,
                    onConfirm: () {
                      Get.back();
                      Get.to(() => const EditProfileView());
                    },
                    textConfirm: 'Go to Edit',
                    textCancel: 'Cancel',
                    cancelTextColor: Colors.white,
                  );
                },
              );
            }),
            const SizedBox(height: 16),
            _buildSectionHeader(context, 'Your app and media'),
            _buildSettingsTile(
              context: context,
              icon: Icons.brightness_medium_outlined,
              title: 'Switch appearance',
              trailing: Text(
                Get.isDarkMode ? 'Dark mode' : 'Light mode',
                style: AppFonts.base(color: Colors.white30, fontSize: 14),
              ),
              onTap: () {
                Get.changeTheme(
                  Get.isDarkMode ? ThemeData.light() : ThemeData.dark(),
                );
              },
            ),
            _buildSettingsTile(
              context: context,
              icon: Icons.language,
              title: 'Language',
              trailing: Text(
                'English',
                style: AppFonts.base(color: Colors.white30, fontSize: 14),
              ),
              onTap: () => _showLanguageDialog(context),
            ),
            const SizedBox(height: 24),
            _buildSettingsTile(
              context: context,
              icon: Icons.logout,
              iconColor: Colors.redAccent,
              textColor: Colors.redAccent,
              title: 'Log out',
              trailing: const SizedBox(),
              onTap: () => _showLogoutDialog(context),
            ),
            const SizedBox(height: 40),
          ],
        ),
      ),
    );
  }

  Widget _buildSectionHeader(BuildContext context, String title) {
    return Padding(
      padding: const EdgeInsets.only(left: 16, top: 8, bottom: 8),
      child: Text(
        title,
        style: AppFonts.base(
          color: Colors.white30,
          fontSize: 12,
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }

  Widget _buildAccountsCenterCard(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return GestureDetector(
      onTap: () => Get.to(() => const AccountsCenterView()),
      child: Container(
        margin: const EdgeInsets.symmetric(vertical: 4, horizontal: 16),
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: isDark ? const Color(0xFF1C1C1E) : Colors.grey[100],
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                const Icon(
                  Icons.account_circle_outlined,
                  color: Colors.blue,
                  size: 24,
                ),
                const SizedBox(width: 8),
                Text(
                  'Accounts Center',
                  style: AppFonts.base(
                    color: isDark ? Colors.white : Colors.black,
                    fontSize: 15,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const Spacer(),
                const Icon(
                  Icons.chevron_right,
                  color: Colors.white30,
                  size: 20,
                ),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              'Password, security, personal details, connected experiences, ad preferences',
              style: AppFonts.base(
                color: isDark ? Colors.white60 : Colors.black54,
                fontSize: 12,
                height: 1.3,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSettingsTile({
    required BuildContext context,
    required IconData icon,
    required String title,
    Widget? trailing,
    VoidCallback? onTap,
    Color? iconColor,
    Color? textColor,
  }) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final defaultTextColor = isDark ? Colors.white : Colors.black;
    return ListTile(
      leading: Icon(icon, color: iconColor ?? defaultTextColor, size: 22),
      title: Text(
        title,
        style: AppFonts.base(
          color: textColor ?? defaultTextColor,
          fontSize: 14,
          fontWeight: FontWeight.w500,
        ),
      ),
      trailing:
          trailing ??
          const Icon(Icons.chevron_right, color: Colors.white30, size: 20),
      onTap: onTap,
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 0),
      visualDensity: const VisualDensity(vertical: -2),
    );
  }

  void _showComingSoonSnackBar(String feature) {
    Get.snackbar(
      'Feature Info',
      '$feature settings screen is coming soon!',
      snackPosition: SnackPosition.BOTTOM,
      backgroundColor: AppColors.surface,
      colorText: Colors.white,
      margin: const EdgeInsets.all(16),
      duration: const Duration(seconds: 2),
    );
  }

  void _showLanguageDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: const Color(0xFF1C1C1E),
        title: Text(
          'Select Language',
          style: AppFonts.base(
            color: Colors.white,
            fontWeight: FontWeight.bold,
          ),
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              title: Text('English', style: AppFonts.base(color: Colors.white)),
              trailing: const Icon(Icons.check, color: AppColors.primary),
              onTap: () => Navigator.pop(context),
            ),
            ListTile(
              title: Text(
                'Tiếng Việt (Coming soon)',
                style: AppFonts.base(color: Colors.white60),
              ),
              onTap: () => Navigator.pop(context),
            ),
          ],
        ),
      ),
    );
  }

  void _showLogoutDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return BackdropFilter(
          filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
          child: AlertDialog(
            backgroundColor: const Color(0xFF1C1C1E),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(14),
            ),
            title: Text(
              'Log out of your account?',
              textAlign: TextAlign.center,
              style: AppFonts.base(
                color: Colors.white,
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            content: Text(
              'You will need to enter your username and password to log back in.',
              textAlign: TextAlign.center,
              style: AppFonts.base(color: Colors.white60, fontSize: 14),
            ),
            actionsAlignment: MainAxisAlignment.spaceEvenly,
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context),
                child: Text(
                  'Cancel',
                  style: AppFonts.base(
                    color: Colors.white,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              TextButton(
                onPressed: () {
                  Navigator.pop(context);
                  controller.logout();
                },
                child: Text(
                  'Log out',
                  style: AppFonts.base(
                    color: Colors.redAccent,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ],
          ),
        );
      },
    );
  }
}
