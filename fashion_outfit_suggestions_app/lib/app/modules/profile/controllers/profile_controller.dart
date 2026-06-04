import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../../core/constants/profile_type.dart';
import '../../../../core/models/user_profile_response.dart';
import '../../../../core/network/dio_client.dart';
import '../../../../core/storage/token_storage.dart';

class ProfileController extends GetxController {
  final String? targetUserId;
  final profile = Rxn<UserProfileResponse>();
  final suggestedUsers = <UserProfileResponse>[].obs;
  final isLoading = false.obs;
  final isSuggestedLoading = false.obs;
  final showSuggestion = false.obs;

  final DioClient _dioClient = Get.find<DioClient>();
  final TokenStorage _tokenStorage = Get.find<TokenStorage>();

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
}
