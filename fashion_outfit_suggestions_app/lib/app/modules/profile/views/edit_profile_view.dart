import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_fonts.dart';
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
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.chevron_left, color: Colors.white, size: 30),
          onPressed: () => Get.back(),
        ),
        title:  Text(
          'Edit profile',
          style: TextStyle(
            color: Colors.white,
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
            const SizedBox(height: 16),
            _buildAvatarCard(context),
            const SizedBox(height: 24),
            _buildSectionTitle('Display name'),
            const SizedBox(height: 8),
            _buildCountedField(
              textController: controller.displayNameController,
              lengthObs: controller.displayNameLength,
              maxLength: 50,
              hintText: 'Display name',
              maxLines: 1,
            ),
            const SizedBox(height: 24),
            _buildSectionTitle('Bio'),
            const SizedBox(height: 8),
            _buildCountedField(
              textController: controller.bioController,
              lengthObs: controller.bioLength,
              maxLength: 150,
              hintText: 'Bio',
              maxLines: 4,
            ),
            const SizedBox(height: 24),
            _buildSectionTitle('Private Account'),
            const SizedBox(height: 8),
            _buildSwitchTile(
              title: 'Private Account',
              subtitle:
                  'When your account is private, only friends you accept can see your outfits.',
              valueObs: controller.editIsPrivateProfile,
            ),
            const SizedBox(height: 24),
            _buildSectionTitle('AI creator'),
            const SizedBox(height: 8),
            _buildSwitchTile(
              title: 'AI creator',
              subtitle:
                  'Add this label to your profile if your content often uses AI. Learn more',
            ),
            const SizedBox(height: 24),

            _buildSectionTitle('Gender'),
            const SizedBox(height: 8),
            _buildGenderDropdown(),
            const Padding(
              padding: EdgeInsets.symmetric(horizontal: 16, vertical: 6),
              child: Text(
                "This won't be part of your public profile.",
                style: TextStyle(color: Colors.white30, fontSize: 12),
              ),
            ),

            const SizedBox(height: 24),
            _buildSectionTitle('Show account suggestions on profiles'),
            const SizedBox(height: 8),
            _buildSwitchTile(
              title: 'Show account suggestions on profiles',
              subtitle:
                  'Choose whether people can see similar account suggestions on your profile, '
                  'and whether your account can be suggested on other profiles.',
            ),
            const SizedBox(height: 12),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: RichText(
                text: TextSpan(
                  style: AppFonts.base(color: Colors.white30, fontSize: 12),
                  children: [
                    TextSpan(
                      text:
                          'Certain profile info, like your name, bio and links, is visible to everyone. ',
                    ),
                    TextSpan(
                      text: 'See what profile info is visible',
                      style: AppFonts.base(color: Color(0xFF3897F0)),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 36),
            _buildSubmitButton(),
            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }

  Widget _buildAvatarCard(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: const Color(0xFF1C1C1E),
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: Colors.white10, width: 0.8),
        ),
        child: Obx(() {
          final p = controller.profile.value;
          final url = p?.avatarUrl ?? '';
          return Row(
            children: [
              CircleAvatar(
                radius: 34,
                backgroundColor: Colors.black26,
                backgroundImage: url.isNotEmpty ? NetworkImage(url) : null,
                child: url.isEmpty
                    ? const Icon(Icons.person, color: Colors.white54, size: 34)
                    : null,
              ),
              const SizedBox(width: 16),
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    p?.username ?? '',
                    style: AppFonts.base(
                      color: Colors.white,
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  SizedBox(height: 4),
                  GestureDetector(
                    onTap: () => _showChangePhotoDialog(context),
                    child: Text(
                      'Change photo',
                      style: AppFonts.base(
                        color: Colors.blueAccent,
                        fontSize: 14,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ],
              ),
            ],
          );
        }),
      ),
    );
  }

  Widget _buildSectionTitle(String title) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
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

  Widget _buildCountedField({
    required TextEditingController textController,
    required RxInt lengthObs,
    required int maxLength,
    required String hintText,
    int maxLines = 1,
  }) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Container(
        decoration: BoxDecoration(
          color: AppColors.background,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: Colors.white10, width: 0.8),
        ),
        child: Stack(
          alignment: Alignment.bottomRight,
          children: [
            TextField(
              controller: textController,
              maxLines: maxLines,
              maxLength: maxLength,
              style: AppFonts.base(color: Colors.white, fontSize: 15),
              onChanged: (v) => lengthObs.value = v.length,
              decoration: InputDecoration(
                hintText: hintText,
                hintStyle: AppFonts.base(color: Colors.white30),
                filled: true,
                fillColor: Colors.transparent,
                counterText: '',
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide.none,
                ),
                contentPadding: EdgeInsets.only(
                  left: 16,
                  right: 16,
                  top: 16,
                  bottom: maxLines == 1 ? 16 : 36,
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(right: 12, bottom: 10),
              child: Obx(
                () => Text(
                  '${lengthObs.value} / $maxLength',
                  style: AppFonts.base(color: Colors.white30, fontSize: 12),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSwitchTile({
    required String title,
    required String subtitle,
    RxBool? valueObs,
  }) {
    final internal = (valueObs ?? false.obs);
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Container(
        decoration: BoxDecoration(
          color: AppColors.background,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: Colors.white10, width: 0.8),
        ),
        child: Obx(
          () => SwitchListTile(
            contentPadding: const EdgeInsets.symmetric(
              horizontal: 16,
              vertical: 4,
            ),
            title: Text(
              title,
              style: AppFonts.base(color: Colors.white, fontSize: 15),
            ),
            subtitle: Text(
              subtitle,
              style: AppFonts.base(
                color: Colors.white38,
                fontSize: 12,
                height: 1.4,
              ),
            ),
            value: internal.value,
            activeThumbColor: Colors.white,
            activeTrackColor: const Color(0xFF0095F6),
            inactiveThumbColor: Colors.grey.shade400,
            inactiveTrackColor: Colors.white24,
            onChanged: valueObs != null ? (v) => valueObs.value = v : null,
          ),
        ),
      ),
    );
  }

  Widget _buildGenderDropdown() {
    final gender = 'Male'.obs;
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        decoration: BoxDecoration(
          color: const Color(0xFF1C1C1E),
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: Colors.white10, width: 0.8),
        ),
        child: Obx(
          () => DropdownButtonHideUnderline(
            child: DropdownButton<String>(
              value: gender.value,
              isExpanded: true,
              dropdownColor: const Color(0xFF2C2C2E),
              icon: const Icon(
                Icons.keyboard_arrow_down,
                color: Colors.white38,
              ),
              style: AppFonts.base(color: Colors.white, fontSize: 15),
              items: [
                DropdownMenuItem(
                  value: 'Male',
                  child: Text(
                    'Male',
                    style: AppFonts.base(color: Colors.white),
                  ),
                ),
                DropdownMenuItem(
                  value: 'Female',
                  child: Text(
                    'Female',
                    style: AppFonts.base(color: Colors.white),
                  ),
                ),
                DropdownMenuItem(
                  value: 'Prefer not to say',
                  child: Text(
                    'Prefer not to say',
                    style: AppFonts.base(color: Colors.white),
                  ),
                ),
              ],
              onChanged: (v) {
                if (v != null) gender.value = v;
              },
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildSubmitButton() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: SizedBox(
        width: double.infinity,
        child: Obx(
          () => ElevatedButton(
            onPressed: controller.isLoading.value
                ? null
                : () async {
                    final ok = await controller.updateProfile();
                    if (ok) {
                      Get.back();
                      Get.snackbar(
                        'Success',
                        'Profile updated!',
                        snackPosition: SnackPosition.TOP,
                        backgroundColor: AppColors.primary,
                        colorText: Colors.black,
                        margin: const EdgeInsets.all(10),
                      );
                    }
                  },
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF002F6C),
              disabledBackgroundColor: Colors.white12,
              foregroundColor: Colors.white,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(10),
              ),
              padding: const EdgeInsets.symmetric(vertical: 16),
              elevation: 0,
            ),
            child: controller.isLoading.value
                ? const SizedBox(
                    height: 20,
                    width: 20,
                    child: CircularProgressIndicator(
                      color: Colors.white,
                      strokeWidth: 2,
                    ),
                  )
                : const Text(
                    'Submit',
                    style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                  ),
          ),
        ),
      ),
    );
  }

  void _showChangePhotoDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (_) => Dialog(
        backgroundColor: const Color(0xFF2C2C2E),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Padding(
              padding: EdgeInsets.symmetric(vertical: 16),
              child: Text(
                'Change Profile Photo',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            const Divider(color: Colors.white12, height: 1),
            TextButton(
              onPressed: () {
                Get.back();
                controller.pickAvatarFromGallery();
              },
              style: TextButton.styleFrom(
                minimumSize: const Size(double.infinity, 50),
              ),
              child: const Text(
                'Upload Photo',
                style: TextStyle(
                  color: Color(0xFF3897F0),
                  fontSize: 15,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            const Divider(color: Colors.white12, height: 1),
            TextButton(
              onPressed: () {
                Get.back();
                controller.removeAvatar();
              },
              style: TextButton.styleFrom(
                minimumSize: const Size(double.infinity, 50),
              ),
              child: const Text(
                'Remove Current Photo',
                style: TextStyle(
                  color: Colors.red,
                  fontSize: 15,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            const Divider(color: Colors.white12, height: 1),
            TextButton(
              onPressed: () => Get.back(),
              style: TextButton.styleFrom(
                minimumSize: const Size(double.infinity, 50),
              ),
              child: const Text(
                'Cancel',
                style: TextStyle(color: Colors.white, fontSize: 15),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
