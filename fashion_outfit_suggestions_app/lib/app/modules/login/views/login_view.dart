import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_fonts.dart';
import '../../../../core/theme/app_vectors.dart';
import '../../../../core/widgets/custom_text_field.dart';
import '../../../../core/widgets/or_divider.dart';
import '../../../../core/widgets/primary_capsule_button.dart';
import '../../../routes/app_routes.dart';
import '../controllers/login_controller.dart';
import 'google_sign_in_button.dart';

class LoginView extends GetView<LoginController> {
  const LoginView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      body: SafeArea(
        child: Form(
          key: controller.formKey,
          child: SingleChildScrollView(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                const SizedBox(height: 40),
                Text(
                  'Fashion Outfit Suggestion',
                  style: AppFonts.base(
                    fontSize: 28,
                    fontWeight: FontWeight.bold,
                    color: AppColors.primary,
                  ),
                ),
                const SizedBox(height: 12),
                Text(
                  'Welcome back. Log in to continue.',
                  textAlign: TextAlign.center,
                  style: AppFonts.base(
                    fontSize: 14,
                    fontWeight: FontWeight.w400,
                    color: AppColors.placeholder,
                  ),
                ),
                const SizedBox(height: 48),
                CustomTextField(
                  label: 'Email or Username',
                  hintText: 'Enter your email or username',
                  obscureText: false,
                  controller: controller.emailController,
                  keyboardType: TextInputType.emailAddress,
                  validator: controller.validateEmailOrUsername,
                ),
                const SizedBox(height: 16),
                Obx(
                  () => CustomTextField(
                    label: 'Password',
                    hintText: 'Enter your password',
                    obscureText: controller.obscurePassword.value,
                    controller: controller.passwordController,
                    keyboardType: TextInputType.visiblePassword,
                    validator: controller.validatePassword,
                    suffixIcon: IconButton(
                      onPressed: () {
                        controller.obscurePassword.value =
                            !controller.obscurePassword.value;
                      },
                      icon: Icon(
                        controller.obscurePassword.value
                            ? Icons.visibility
                            : Icons.visibility_off,
                        color: AppColors.placeholder,
                        size: 20,
                      ),
                    ),
                  ),
                ),
                const SizedBox(height: 48),
                Obx(
                  () => PrimaryCapsuleButton(
                    text: 'Log In',
                    onPressed: () => controller.login(),
                    isLoading: controller.isLoading.value,
                  ),
                ),
                const SizedBox(height: 20),
                GestureDetector(
                  onTap: () {
                    Get.toNamed(Routes.forgotPassword);
                  },
                  child: Text(
                    'Forgot password?',
                    style: AppFonts.base(
                      fontSize: 13,
                      fontWeight: FontWeight.bold,
                      color: AppColors.primary,
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                OrDivider(),
                const SizedBox(height: 32),
                Obx(
                  () => buildGoogleSignInButton(
                    text: 'Sign in with Google',
                    svgAssetPath: AppVectors.google,
                    onPressed: () {
                      if (!controller.isLoading.value) {
                        controller.signInWithGoogle();
                      }
                    },
                    isLoading: controller.isLoading.value,
                  ),
                ),
                const SizedBox(height: 48),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      'Don\'t have an account? ',
                      style: AppFonts.base(
                        fontSize: 13,
                        color: AppColors.placeholder,
                      ),
                    ),
                    GestureDetector(
                      onTap: () => Get.offAllNamed(Routes.register),
                      child: Text(
                        'Sign Up',
                        style: AppFonts.base(
                          fontSize: 13,
                          fontWeight: FontWeight.bold,
                          color: AppColors.primary,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 24),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
