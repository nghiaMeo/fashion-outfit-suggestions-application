import 'package:fashion_outfit_suggestions_app/core/theme/app_fonts.dart';
import 'package:fashion_outfit_suggestions_app/core/theme/app_vectors.dart';
import 'package:fashion_outfit_suggestions_app/core/widgets/social_button.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../../../../core/theme/app_colors.dart';
import '../../../../core/widgets/custom_text_field.dart';
import '../../../../core/widgets/or_divider.dart';
import '../../../../core/widgets/primary_capsule_button.dart';
import '../../../routes/app_routes.dart';
import '../controllers/register_controller.dart';

class RegisterView extends GetView<RegisterController> {
  const RegisterView({super.key});

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
                  'Create a new account to start exploring and get tips on the most suitable outfits.',
                  textAlign: TextAlign.center,
                  style: AppFonts.base(
                    fontSize: 14,
                    fontWeight: FontWeight.w400,
                    color: AppColors.placeholder,
                  ),
                ),
                const SizedBox(height: 48),
                CustomTextField(
                  label: 'Full Name',
                  hintText: 'Enter your full name',
                  obscureText: false,
                  controller: controller.displayNameController,
                  keyboardType: TextInputType.text,
                  validator: controller.validateDisplayName,
                ),
                const SizedBox(height: 16),
                CustomTextField(
                  label: 'Username',
                  hintText: 'Enter your username',
                  obscureText: false,
                  controller: controller.usernameController,
                  keyboardType: TextInputType.name,
                  validator: controller.validateUsername,
                ),
                const SizedBox(height: 16),
                CustomTextField(
                  label: 'Email',
                  hintText: 'Enter your email',
                  obscureText: false,
                  controller: controller.emailController,
                  keyboardType: TextInputType.emailAddress,
                  validator: controller.validateEmail,
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
                    text: 'Register',
                    onPressed: () => controller.register(),
                    isLoading: controller.isLoading.value,
                  ),
                ),
                const SizedBox(height: 48),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      'Do you have an account? ',
                      style: AppFonts.base(
                        fontSize: 13,
                        color: AppColors.placeholder,
                      ),
                    ),
                    GestureDetector(
                      onTap: () => Get.offAllNamed(Routes.login),
                      child: Text(
                        'Sign In',
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
