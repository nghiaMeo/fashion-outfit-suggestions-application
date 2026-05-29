import 'package:fashion_outfit_suggestions_app/core/models/user_response.dart';
import 'package:fashion_outfit_suggestions_app/core/network/dio_client.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../../core/theme/app_colors.dart';
import '../../../../core/widgets/dialog_alert.dart';
import '../../../routes/app_routes.dart';

class RegisterController extends GetxController {
  final formKey = GlobalKey<FormState>();

  final emailController = TextEditingController();
  final usernameController = TextEditingController();
  final displayNameController = TextEditingController();
  final passwordController = TextEditingController();

  final isLoading = false.obs;
  final obscurePassword = true.obs;

  final DioClient _dioClient = Get.find<DioClient>();

  Future<void> register() async {
    if (!(formKey.currentState!.validate() ?? false)) {
      return;
    }
    isLoading.value = true;

    try {
      final user = await _dioClient.getResult<UserResponse>(
        _dioClient.dio.post<Map<String, dynamic>>(
          '/api/auth/register',
          data: {
            'email': emailController.text,
            'username': usernameController.text,
            'displayName': displayNameController.text,
            'password': passwordController.text,
          },
        ),
        (json) => UserResponse.fromJson(json! as Map<String, dynamic>),
      );
      Get.dialog(
        DialogAlert(
          onConfirm: () {
            Get.offAllNamed(Routes.login);
          },
          color: AppColors.primary,
          icon: Icons.check_circle,
          title: 'Sign up success',
          description: 'Now you can login with your new account',
        ),
        barrierDismissible: false,
      );
    } on Exception catch (e) {
      Get.dialog(
        DialogAlert(
          onConfirm: () {
             Get.back();
          },
          color: AppColors.error,
          icon: Icons.error,
          title: 'Error sign up',
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
          title: 'Error sign up',
          description: e.toString(),
        ),
        barrierDismissible: false,
      );
    } finally {
      isLoading.value = false;
    }
  }

  String? validateEmail(String? email) {
    final value = (email ?? '').trim();
    if (email!.isEmpty) return ('Email is required');

    if (!GetUtils.isEmail(value)) return ('Invalid email');

    return null;
  }

  String? validateUsername(String? username) {
    final value = (username ?? '').trim();
    if (username!.isEmpty) return ('Username is required');
    if (username.length < 6) return ('username must at latest 6 characters');
    return null;
  }

  String? validateDisplayName(String? displayName) {
    final value = (displayName ?? '').trim();
    if (displayName!.isEmpty) return ('Display name is required');
    if (displayName.length < 8) {
      return ('display name must at latest 8 characters');
    }
    return null;
  }

  String? validatePassword(String? password) {
    final value = (password ?? '').trim();
    if (password!.isEmpty) return ('Password is required');
    if (password.length < 8) return ('Password must at latest 8 characters');
    return null;
  }

  @override
  void onClose() {
    emailController.dispose();
    usernameController.dispose();
    displayNameController.dispose();
    passwordController.dispose();
    super.onClose();
  }
}
