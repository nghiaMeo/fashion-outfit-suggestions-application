import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';

import '../../../../core/constants/profile_type.dart';
import '../../../../core/models/user_profile_response.dart';
import '../../../../core/network/dio_client.dart';
import '../../../../core/storage/token_storage.dart';
import 'package:dio/dio.dart' as dio_pkg;

class ProfileController extends GetxController {
  final String? targetUserId;
  final profile = Rxn<UserProfileResponse>();
  final suggestedUsers = <UserProfileResponse>[].obs;
  final isLoading = false.obs;
  final isSuggestedLoading = false.obs;
  final showSuggestion = false.obs;

  final displayNameController = TextEditingController();
  final bioController = TextEditingController();
  final avatarUrlController = TextEditingController();
  final editIsPrivateProfile = false.obs;

  final DioClient _dioClient = Get.find<DioClient>();
  final TokenStorage _tokenStorage = Get.find<TokenStorage>();
  final ImagePicker _imagePicker = ImagePicker();

  ProfileController({this.targetUserId});

  @override
  void onInit() {
    super.onInit();
    fetchProfile();
  }

  Future<void> fetchProfile() async {
    isLoading.value = true;
    try {
      final endpoint = targetUserId == null
          ? '/api/user/my-profile'
          : '/api/user/profile/$targetUserId';

      final response = await _dioClient.getResult<UserProfileResponse>(
        _dioClient.dio.get(endpoint),
        (json) => UserProfileResponse.fromJson(json as Map<String, dynamic>),
      );
      profile.value = response;
    } catch (e) {
      _showErrorDialog(e.toString());
    } finally {
      isLoading.value = false;
    }
  }

  Future<void> fetchSuggestedUsers() async {
    isSuggestedLoading.value = true;
    try {
      final response = await _dioClient.getResult<List<UserProfileResponse>>(
        _dioClient.dio.get('/api/user/suggest-candidates'),
        (json) {
          final list = json as List;
          return list
              .map(
                (e) => UserProfileResponse.fromJson(e as Map<String, dynamic>),
              )
              .toList();
        },
      );
      suggestedUsers.assignAll(response);
    } catch (e) {
      _showErrorDialog(e.toString());
    } finally {
      isSuggestedLoading.value = false;
    }
  }

  void toggleSuggestions() {
    if (!showSuggestion.value && suggestedUsers.isEmpty) {
      fetchSuggestedUsers();
    }
    showSuggestion.value = !showSuggestion.value;
  }

  void removeSuggestion(String userId) {
    suggestedUsers.removeWhere((user) => user.id == userId);
    if (suggestedUsers.isEmpty) {
      showSuggestion.value = false;
    }
  }

  Future<void> sendFriendRequest(String userId) async {
    try {
      await _dioClient.dio.post('/api/friendship/request/$userId');

      if (userId == targetUserId) {
        profile.value = profile.value!.copyWith(friendshipStatus: 'PENDING');
      }

      removeSuggestion(userId);
    } catch (e) {
      _showErrorDialog(e.toString());
    }
  }

  Future<void> unfriend() async {
    final userIdToDelete = targetUserId;
    if (userIdToDelete == null) return;

    try {
      await _dioClient.dio.delete('/api/friendship/user/$userIdToDelete');
      profile.value = profile.value!.copyWith(friendshipStatus: null);
      fetchProfile();
    } catch (e) {
      _showErrorDialog(e.toString());
    }
  }

  ProfileType get profileType {
    if (targetUserId == null) return ProfileType.self;
    final status = profile.value?.friendshipStatus;
    if (status == 'ACCEPTED') return ProfileType.following;
    return ProfileType.notFollowing;
  }

  bool get isPending => profile.value?.friendshipStatus == 'PENDING';

  void _showErrorDialog(String message) {
    Get.dialog(
      Dialog(
        backgroundColor: const Color(0xFF1A1A1A),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Icon(Icons.error_outline, color: Colors.red, size: 48),
              const SizedBox(height: 16),
              Text(
                message,
                textAlign: TextAlign.center,
                style: const TextStyle(color: Colors.white, fontSize: 16),
              ),
              const SizedBox(height: 20),
              TextButton(
                onPressed: () => Get.back(),
                child: const Text(
                  'Close',
                  style: TextStyle(color: Color(0xFFD9C5B2)),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  void startEditing() {
    final current = profile.value;
    if (current != null) {
      displayNameController.text = current.displayName ?? '';
      bioController.text = current.bio ?? '';
      avatarUrlController.text = current.avatarUrl ?? '';
      editIsPrivateProfile.value = current.isPrivateProfile;
    }
  }

  Future<bool> updateProfile() async {
    if (displayNameController.text.isEmpty || bioController.text.isEmpty) {
      _showErrorDialog('Please fill in all fields');
      return false;
    }
    isLoading.value = true;
    try {
      final body = {
        'display_name': displayNameController.text.trim(),
        'bio': bioController.text.trim(),
        'avatar_url': avatarUrlController.text.trim(),
        'is_private_profile': editIsPrivateProfile.value,
      };

      final response = await _dioClient.getResult<UserProfileResponse>(
        _dioClient.dio.put('/api/user/profile', data: body),
        (json) => UserProfileResponse.fromJson(json as Map<String, dynamic>),
      );
      profile.value = response;
      return true;
    } catch (e) {
      _showErrorDialog(e.toString());
      return false;
    } finally {
      isLoading.value = false;
    }
  }

  Future<void> changAvatar() async {
    try {
      final XFile? image = await _imagePicker.pickImage(
        source: ImageSource.gallery,
        imageQuality: 80,
      );
      if (image == null) return;
      isLoading.value = true;
      final formData = dio_pkg.FormData.fromMap({
        'file': await dio_pkg.MultipartFile.fromFile(
          image.path,
          filename: image.name,
        ),
      });
      final String uploadUrl = await _dioClient.getResult<String>(
        _dioClient.dio.post(
          '/api/user/profile/avatar',
          data: formData,
          options: dio_pkg.Options(contentType: 'multipart/form-data'),
        ),
        (json) => json as String,
      );
      avatarUrlController.text = uploadUrl;
      if (profile.value != null) {
        profile.value = profile.value!.copyWith(avatarUrl: uploadUrl);
      }
    } catch (e) {
      _showErrorDialog(e.toString());
    } finally {
      isLoading.value = false;
    }
  }

  @override
  void onClose() {
    displayNameController.dispose();
    bioController.dispose();
    avatarUrlController.dispose();
    super.onClose();
  }
}
