import 'package:fashion_outfit_suggestions_app/app/routes/app_routes.dart';
import 'package:fashion_outfit_suggestions_app/core/network/dio_client.dart';
import 'package:fashion_outfit_suggestions_app/core/storage/token_storage.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../../core/theme/app_colors.dart';

class SettingController extends GetxController {
  final isLoading = false.obs;
  final DioClient _dioClient = Get.find<DioClient>();
  final TokenStorage _tokenStorage = Get.find<TokenStorage>();

  Future<void> logout() async {
    await _tokenStorage.clearSession();
    Get.offAllNamed(Routes.login);
  }

  Future<bool> changePassword({
    required String oldPassword,
    required String newPassword,
  }) async {
    isLoading.value = true;
    try {
      final body = {'oldPassword': oldPassword, 'newPassword': newPassword};
      await _dioClient.getResult<String>(
        _dioClient.dio.put('/api/auth/chang-password', data: body),
        (json) => json as String? ?? 'Success',
      );
      Get.snackbar(
        'Success',
        'Change password successfully!',
        snackPosition: SnackPosition.TOP,
        backgroundColor: AppColors.primary,
        colorText: Colors.black,
        margin: const EdgeInsets.all(10),
      );
      return true;
    } catch (e) {
      Get.defaultDialog(
        title: 'Error',
        middleText: e.toString(),
        backgroundColor: const Color(0xFF1E1E1E),
        titleStyle: const TextStyle(color: Colors.redAccent),
        middleTextStyle: const TextStyle(color: Colors.white70),
      );
      return false;
    } finally {
      isLoading.value = false;
    }
  }
}
