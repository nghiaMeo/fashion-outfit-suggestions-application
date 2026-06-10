import 'package:fashion_outfit_suggestions_app/core/storage/token_storage.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../../core/models/auth_response.dart';
import '../../../../core/network/dio_client.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/widgets/dialog_alert.dart';
import '../../../routes/app_routes.dart';

class LoginController extends GetxController {
  final formKey = GlobalKey<FormState>();

  final emailController = TextEditingController();
  final passwordController = TextEditingController();

  final isLoading = false.obs;
  final obscurePassword = true.obs;

  final DioClient _dioClient = Get.find<DioClient>();

  Future<void> login() async {
    if (!(formKey.currentState!.validate() ?? false)) {
      return;
    }
    isLoading.value = true;

    try {
      final authResponse = await _dioClient.getResult<AuthResponse>(
        _dioClient.dio.post<Map<String, dynamic>>(
          '/api/auth/login',
          data: {
            'email': emailController.text,
            'password': passwordController.text,
          },
        ),
        (json) => AuthResponse.fromJson(json! as Map<String, dynamic>),
      );

      final tokeStorage = Get.find<TokenStorage>();
      await tokeStorage.saveSession(
        accessToken: authResponse.accessToken,
        refreshToken: authResponse.refreshToken,
        userId: authResponse.userResponse?.id,
      );

      Get.offAllNamed(Routes.home);
    } on Exception catch (e) {
      Get.dialog(
        DialogAlert(
          onConfirm: () {
            Get.back();
          },
          color: AppColors.error,
          icon: Icons.error,
          title: 'Login failed',
          description: e.toString().replaceAll('Exception: ', ''),
        ),
        barrierDismissible: false,
      );
    } catch (e) {
      Get.dialog(
        DialogAlert(
          onConfirm: () {
            Get.back();
          },
          color: AppColors.error,
          icon: Icons.error,
          title: 'Error Login',
          description: e.toString(),
        ),
        barrierDismissible: false,
      );
    } finally {
      isLoading.value = false;
    }
  }

  String? validateEmailOrUsername(String? input) {
    final value = (input ?? '').trim();
    if (value.isEmpty) return ('Please enter your email or username');
    if (value.contains('@')) {
      if (!GetUtils.isEmail(value)) return ('Invalid email');
    } else {
      if (value.length < 6) return ('username must at latest 6 characters');
    }
    return null;
  }

  String? validatePassword(String? password) {
    final value = (password ?? '').trim();
    if (value.isEmpty) return ('Password is required');
    if (value.length < 8) return ('Password must at latest 8 characters');
    return null;
  }

  @override
  void onClose() {
    // emailController.dispose();
    // passwordController.dispose();
    super.onClose();
  }
}
