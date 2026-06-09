import 'dart:ffi';

import 'package:fashion_outfit_suggestions_app/app/routes/app_routes.dart';
import 'package:fashion_outfit_suggestions_app/core/network/dio_client.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../../core/models/forgot_password_step.dart';
import '../../../../core/theme/app_colors.dart';

class ForgotPasswordController extends GetxController {
  final formKeyEmail = GlobalKey<FormState>();
  final formKeyRest = GlobalKey<FormState>();

  final emailController = TextEditingController();
  final otpController = TextEditingController();
  final newPasswordController = TextEditingController();
  final confirmPasswordController = TextEditingController();

  final isLoading = false.obs;
  final step = ForgotPasswordStep.enterEmail.obs;
  final obscureNew = true.obs;
  final obscureConfirm = true.obs;

  final DioClient _dioClient = Get.find<DioClient>();

  Future<void> senOtp() async {
    if (!formKeyEmail.currentState!.validate()) return;
    isLoading.value = true;

    try {
      await _dioClient.getResult<String>(
        _dioClient.dio.post(
          '/api/auth/forgot-password',
          data: {'email': emailController.text.trim()},
        ),
        (json) => json as String ?? 'OTP Sent',
      );
      step.value = ForgotPasswordStep.enterOtp;
      Get.snackbar(
        'OTP Sent!',
        'Check your email ${emailController.text.trim()} for the OTP code.',
        snackPosition: SnackPosition.TOP,
        backgroundColor: const Color(0xFF1C1C1E),
        colorText: Colors.white,
        icon: const Icon(Icons.email_outlined, color: AppColors.primary),
        margin: const EdgeInsets.all(12),
        duration: const Duration(seconds: 4),
      );
    } catch (e) {
      Get.snackbar(
        'Error',
        e.toString().replaceAll('Exception: ', ''),
        snackPosition: SnackPosition.TOP,
        backgroundColor: Colors.redAccent,
        colorText: Colors.white,
        margin: const EdgeInsets.all(12),
      );
    } finally {
      isLoading.value = false;
    }
  }

  Future<void> resetPassword() async {
    if (!formKeyRest.currentState!.validate()) return;
    isLoading.value = true;

    try {
      await _dioClient.getResult<String>(
        _dioClient.dio.post(
          '/api/auth/reset-password',
          data: {
            'email': emailController.text.trim(),
            'otp': otpController.text.trim(),
            'newPassword': newPasswordController.text.trim(),
          },
        ),
        (json) => json as String ?? 'Password reset',
      );
      Get.offAllNamed(Routes.login);
      Get.snackbar(
        'Success!',
        'Password has reset. please login again.',
        snackPosition: SnackPosition.TOP,
        backgroundColor: AppColors.primary,
        colorText: Colors.black,
        margin: const EdgeInsets.all(12),
      );
    } catch (e) {
      Get.snackbar(
        'Error',
        e.toString().replaceAll('Exception: ', ''),
        snackPosition: SnackPosition.TOP,
        backgroundColor: Colors.redAccent,
        colorText: Colors.white,
        margin: const EdgeInsets.all(12),
      );
    } finally {
      isLoading.value = false;
    }
  }

  void goBackToEmail() {
    step.value = ForgotPasswordStep.enterEmail;
    otpController.clear();
    newPasswordController.clear();
    confirmPasswordController.clear();
  }

  @override
  void onClose() {
    emailController.dispose();
    otpController.dispose();
    newPasswordController.dispose();
    confirmPasswordController.dispose();
    super.onClose();
  }
}
