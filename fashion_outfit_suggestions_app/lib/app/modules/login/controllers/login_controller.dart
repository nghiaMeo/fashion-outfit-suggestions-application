import 'dart:convert';

import 'package:fashion_outfit_suggestions_app/core/storage/token_storage.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:google_sign_in/google_sign_in.dart';

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
  final GoogleSignIn _googleSignIn = GoogleSignIn.instance;

  @override
  void onInit() {
    super.onInit();
    _googleSignIn.initialize(
      clientId:
          '921930100611-njvs57c1fdoadsfcdsepib00phi87pnm.apps.googleusercontent.com',
    );
    _googleSignIn.authenticationEvents.listen(
      (event) {
        if (event is GoogleSignInAuthenticationEventSignIn) {
          _handleGoogleSignInUser(event.user);
        }
      },
      onError: (error) {
        print('Google Auth Event Error: $error');
      },
    );
  }

  Future<void> _handleGoogleSignInUser(GoogleSignInAccount googleUser) async {
    isLoading.value = true;
    try {
      final GoogleSignInAuthentication googleAuth =
          await googleUser.authentication;
      final String? idToken = googleAuth.idToken;

      if (idToken == null) {
        throw Exception('Cannot retrieve Google ID Token.');
      }
      final authResponse = await _dioClient.getResult<AuthResponse>(
        _dioClient.dio.post<Map<String, dynamic>>(
          '/api/auth/oauth2/google',
          data: {'token': idToken},
        ),
        (json) => AuthResponse.fromJson(json! as Map<String, dynamic>),
      );
      final tokenStorage = Get.find<TokenStorage>();
      await tokenStorage.saveSession(
        accessToken: authResponse.accessToken,
        refreshToken: authResponse.refreshToken,
        userId: authResponse.userResponse?.id,
      );

      Get.offNamed(Routes.home);
    } catch (e) {
      Get.dialog(
        DialogAlert(
          onConfirm: () => Get.back(),
          color: AppColors.error,
          icon: Icons.error,
          title: 'Google Login Failed',
          description: e.toString().replaceAll('Exception: ', ''),
        ),
      );
    } finally {
      isLoading.value = false;
    }
  }

  Future<void> signInWithGoogle() async {
    isLoading.value = true;
    try {
      // 1. Khởi tạo GoogleSignIn với Client ID của bạn
      await _googleSignIn.initialize(
        clientId: '921930100611-njvs57c1fdoadsfcdsepib00phi87pnm.apps.googleusercontent.com', // Nhập Client ID của bạn ở đây
      );
      // 2. Kích hoạt Popup đăng nhập (Hàm authenticate() thay thế cho signIn() ở bản v7)
      final GoogleSignInAccount? googleUser = await _googleSignIn.authenticate();
      if (googleUser == null) {
        isLoading.value = false;
        return;
      }
      // 3. Lấy thông tin xác thực từ Google
      final GoogleSignInAuthentication googleAuth = await googleUser.authentication;
      final String? idToken = googleAuth.idToken;
      if (idToken == null) {
        throw Exception('Cannot retrieve Google ID Token.');
      }
      // 4. Gửi ID Token lên Backend Spring Boot
      final authResponse = await _dioClient.getResult<AuthResponse>(
        _dioClient.dio.post<Map<String, dynamic>>(
          '/api/auth/oauth2/google',
          data: {
            'token': idToken,
          },
        ),
            (json) => AuthResponse.fromJson(json! as Map<String, dynamic>),
      );
      // 5. Lưu Session đăng nhập
      final tokeStorage = Get.find<TokenStorage>();
      await tokeStorage.saveSession(
        accessToken: authResponse.accessToken,
        refreshToken: authResponse.refreshToken,
        userId: authResponse.userResponse?.id,
      );
      // 6. Chuyển sang màn hình Home
      Get.offAllNamed(Routes.home);
    } catch (e) {
      Get.dialog(
        DialogAlert(
          onConfirm: () => Get.back(),
          color: AppColors.error,
          icon: Icons.error,
          title: 'Google Login Failed',
          description: e.toString().replaceAll('Exception: ', ''),
        ),
      );
    } finally {
      isLoading.value = false;
    }
  }

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
