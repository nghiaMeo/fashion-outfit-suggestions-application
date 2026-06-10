import 'package:fashion_outfit_suggestions_app/core/theme/app_fonts.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../../core/models/forgot_password_step.dart';
import '../../../../core/theme/app_colors.dart';
import '../controllers/forgot_password_controller.dart';

class ForgotPasswordView extends GetView<ForgotPasswordController> {
  const ForgotPasswordView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.close, color: Colors.white, size: 26),
          onPressed: () => Get.back(),
        ),
      ),
      body: Obx(() {
        if (controller.step.value == ForgotPasswordStep.enterEmail) {
          return _buildEnterEmailStep(context);
        } else {
          return _buildEnterOtpStep(context);
        }
      }),
    );
  }

  Widget _buildEnterEmailStep(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.symmetric(horizontal: 24),
      child: Form(
        key: controller.formKeyEmail,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            const SizedBox(height: 16),
            Container(
              width: 80,
              height: 80,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                border: Border.all(color: Colors.white, width: 2.5),
              ),
              child: const Icon(
                Icons.lock_outline,
                color: Colors.white,
                size: 38,
              ),
            ),
            const SizedBox(height: 12),
            Text(
              'Enter your email and we\'ll send you an OTP to get back into your account',
              textAlign: TextAlign.center,
              style: AppFonts.base(
                color: Colors.white60,
                fontSize: 14,
                height: 1.5,
              ),
            ),
            const SizedBox(height: 32),
            TextFormField(
              controller: controller.emailController,
              keyboardType: TextInputType.emailAddress,
              style: AppFonts.base(color: Colors.white, fontSize: 14),
              validator: (value) {
                if (value == null || value.trim().isEmpty) {
                  return 'Please enter your email';
                }
                if (!GetUtils.isEmail(value.trim())) {
                  return 'Please enter a valid email';
                }
                return null;
              },
              decoration: InputDecoration(
                hintText: 'Email address',
                hintStyle: AppFonts.base(color: Colors.white38, fontSize: 14),
                filled: true,
                fillColor: const Color(0xFF1C1C1E),
                contentPadding: const EdgeInsets.symmetric(
                  horizontal: 16,
                  vertical: 14,
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8),
                  borderSide: const BorderSide(color: Color(0xFF3A3A3A)),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8),
                  borderSide: const BorderSide(color: Colors.white38),
                ),
                errorBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8),
                  borderSide: const BorderSide(color: Colors.redAccent),
                ),
                focusedErrorBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8),
                  borderSide: const BorderSide(color: Colors.redAccent),
                ),
                errorStyle: AppFonts.base(
                  color: Colors.redAccent,
                  fontSize: 12,
                ),
              ),
            ),
            const SizedBox(height: 16),
            Obx(
              () => SizedBox(
                width: double.infinity,
                height: 48,
                child: ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF0095F6),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                    ),
                    elevation: 0,
                  ),
                  onPressed: controller.isLoading.value
                      ? null
                      : controller.sendOtp,
                  child: controller.isLoading.value
                      ? const SizedBox(
                          height: 20,
                          width: 20,
                          child: CircularProgressIndicator(
                            color: Colors.white,
                            strokeWidth: 2,
                          ),
                        )
                      : Text(
                          'Send OTP',
                          style: AppFonts.base(
                            color: Colors.white,
                            fontSize: 15,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                ),
              ),
            ),
            const SizedBox(height: 24),
            Row(
              children: [
                Expanded(child: Divider(color: Color(0xFF3A3A3A))),
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 12),
                  child: Text(
                    'OR',
                    style: AppFonts.base(color: Colors.white30, fontSize: 12),
                  ),
                ),
                Expanded(child: Divider(color: Color(0xFF3A3A3A))),
              ],
            ),
            SizedBox(height: 24),
            GestureDetector(
              onTap: () => Get.back(),
              child: Text(
                'Back to login',
                style: AppFonts.base(
                  color: Colors.white,
                  fontSize: 14,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildEnterOtpStep(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.symmetric(horizontal: 24),
      child: Form(
        key: controller.formKeyRest,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            const SizedBox(height: 16),
            Container(
              width: 80,
              height: 80,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                border: Border.all(color: AppColors.primary, width: 2.5),
              ),
              child: Icon(
                Icons.mark_email_read_outlined,
                color: AppColors.primary,
                size: 38,
              ),
            ),
            const SizedBox(height: 24),
            Text(
              'Check your email',
              style: AppFonts.base(
                color: Colors.white,
                fontSize: 22,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 12),
            RichText(
              textAlign: TextAlign.center,
              text: TextSpan(
                style: AppFonts.base(
                  color: Colors.white60,
                  fontSize: 14,
                  height: 1.5,
                ),
                children: [
                  const TextSpan(text: 'Enter the OTP we sent to\n'),
                  TextSpan(
                    text: controller.emailController.text,
                    style: AppFonts.base(
                      color: Colors.white,
                      fontSize: 14,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 32),
            TextFormField(
              controller: controller.otpController,
              keyboardType: TextInputType.number,
              textAlign: TextAlign.center,
              maxLength: 6,
              style: AppFonts.base(
                color: Colors.white,
                fontSize: 22,
                fontWeight: FontWeight.bold,
              ),
              validator: (value) {
                if (value == null || value.trim().isEmpty) {
                  return 'Please enter the OTP code';
                }
                return null;
              },
              decoration: InputDecoration(
                hintText: '*********',
                hintStyle: AppFonts.base(color: Colors.white30, fontSize: 18),
                counterText: '',
                filled: true,
                fillColor: const Color(0xFF1C1C1E),
                contentPadding: const EdgeInsets.symmetric(
                  horizontal: 16,
                  vertical: 14,
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8),
                  borderSide: const BorderSide(color: Color(0xFF3A3A3A)),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8),
                  borderSide: BorderSide(color: AppColors.primary),
                ),
                errorBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8),
                  borderSide: BorderSide(color: AppColors.error),
                ),
                focusedErrorBorder: OutlineInputBorder(
                  borderSide: BorderSide(color: AppColors.error),
                  borderRadius: BorderRadius.circular(8),
                ),
                errorStyle: AppFonts.base(color: AppColors.error, fontSize: 12),
              ),
            ),
            const SizedBox(height: 20),
            _buildPasswordField(
              textController: controller.newPasswordController,
              hintText: 'New password',
              obscureObs: controller.obscureNew,
              validator: (value) {
                if (value == null || value.trim().isEmpty) {
                  return 'Please enter new password';
                }
                if (value.length < 8) {
                  return 'Password must be at least 8 characters';
                }
                return null;
              },
            ),
            const SizedBox(height: 16),
            _buildPasswordField(
              textController: controller.confirmPasswordController,
              hintText: 'Re-type new password',
              obscureObs: controller.obscureConfirm,
              validator: (value) {
                if (value == null || value.trim().isEmpty) {
                  return 'Please re-type new password';
                }
                if (value != controller.newPasswordController.text) {
                  return 'Passwords do not match';
                }
                return null;
              },
            ),
            const SizedBox(height: 24),
            Obx(
              () => SizedBox(
                width: double.infinity,
                height: 48,
                child: ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF0095F6),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                    ),
                    elevation: 0,
                  ),
                  onPressed: controller.isLoading.value
                      ? null
                      : controller.resetPassword,
                  child: controller.isLoading.value
                      ? const SizedBox(
                          width: 20,
                          height: 20,
                          child: CircularProgressIndicator(
                            color: Colors.white,
                            strokeWidth: 2,
                          ),
                        )
                      : Text(
                          'Reset Password',
                          style: AppFonts.base(
                            color: Colors.white,
                            fontSize: 15,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                ),
              ),
            ),
            const SizedBox(height: 16),
            Obx(
              () => TextButton(
                onPressed: controller.isLoading.value
                    ? null
                    : controller.sendOtp,
                child: Text(
                  'Resend OTP',
                  style: AppFonts.base(
                    color: controller.isLoading.value
                        ? Colors.white30
                        : const Color(0xFF0095F6),
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ),
            TextButton(
              onPressed: controller.goBackToEmail,
              child: Text(
                'Change email address',
                style: AppFonts.base(color: Colors.white60, fontSize: 13),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildPasswordField({
    required TextEditingController textController,
    required String hintText,
    required RxBool obscureObs,
    required String? Function(String?) validator,
  }) {
    return Obx(
      () => TextFormField(
        controller: textController,
        obscureText: obscureObs.value,
        style: AppFonts.base(color: Colors.white, fontSize: 14),
        validator: validator,
        decoration: InputDecoration(
          hintText: hintText,
          hintStyle: AppFonts.base(color: Colors.white38, fontSize: 14),
          filled: true,
          fillColor: const Color(0xFF1C1C1E),
          contentPadding: const EdgeInsets.symmetric(
            horizontal: 16,
            vertical: 14,
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8),
            borderSide: const BorderSide(color: Color(0xFF3A3A3A)),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8),
            borderSide: const BorderSide(color: Colors.white38),
          ),
          errorBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8),
            borderSide: const BorderSide(color: Colors.redAccent),
          ),
          focusedErrorBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8),
            borderSide: const BorderSide(color: Colors.redAccent),
          ),
          errorStyle: AppFonts.base(color: Colors.redAccent, fontSize: 12),
          suffixIcon: IconButton(
            icon: Icon(
              obscureObs.value
                  ? Icons.visibility_off_outlined
                  : Icons.visibility_outlined,
              color: Colors.white38,
              size: 20,
            ),
            onPressed: () => obscureObs.value = !obscureObs.value,
          ),
        ),
      ),
    );
  }
}
