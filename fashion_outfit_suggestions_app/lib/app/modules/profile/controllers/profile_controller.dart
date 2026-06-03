import 'package:fashion_outfit_suggestions_app/core/network/dio_client.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../../core/models/user_profile_response.dart';
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
      final endpoint = targetUserId != null
          ? '/api/user/my-profile'
          : '/api/user/profile/$targetUserId';
      final response = await _dioClient.getResult<UserProfileResponse>(
        _dioClient.dio.get(endpoint),
        (json) => UserProfileResponse.fromJson(json as Map<String, dynamic>),
      );
      profile.value = response;
    } catch (e) {
      Get.dialog(
        Dialog(
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Text(e.toString()),
          ),
        ),
        barrierDismissible: false,
      );
    } finally {
      isLoading.value = false;
    }
  }
}
