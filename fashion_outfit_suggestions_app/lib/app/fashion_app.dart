import 'package:fashion_outfit_suggestions_app/app/routes/app_routes.dart';
import 'package:fashion_outfit_suggestions_app/core/theme/app_colors.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../core/theme/app_text_styles.dart';
import '../modules/auth/login_view.dart';
import '../modules/home/home_view.dart';
import '../modules/splash/splash_binding.dart';
import '../modules/splash/splash_view.dart';
import 'bindings/initial_binding.dart';

class FashionApp extends StatefulWidget {
  const FashionApp({super.key});

  @override
  State<FashionApp> createState() => _FashionAppState();
}

class _FashionAppState extends State<FashionApp> {
  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Fashion Outfit',
      initialBinding: InitialBinding(),
      initialRoute: AppRoutes.splash,
      themeMode: ThemeMode.dark,
      theme: ThemeData(
        brightness: Brightness.dark,
        scaffoldBackgroundColor: AppColors.background,
        textTheme: TextTheme(
          headlineMedium: AppTextStyles.headline,
          bodyMedium: AppTextStyles.body,
          labelSmall: AppTextStyles.label,
        ),
      ),
      getPages: [
        GetPage(
          name: AppRoutes.splash,
          page: () => const SplashView(),
          binding: SplashBinding(),
        ),
        GetPage(name: AppRoutes.login, page: () => const LoginView()),
        GetPage(name: AppRoutes.home, page: () => const HomeView()),
      ],
    );
  }
}
