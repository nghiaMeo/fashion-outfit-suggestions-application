import 'package:flutter/material.dart';
import '../../../../core/widgets/social_button.dart';

Widget buildGoogleSignInButton({
  required String text,
  required String svgAssetPath,
  required VoidCallback onPressed,
  bool isLoading = false,
}) {
  return SocialButton(
    text: text,
    svgAssetPath: svgAssetPath,
    onPressed: onPressed,
  );
}