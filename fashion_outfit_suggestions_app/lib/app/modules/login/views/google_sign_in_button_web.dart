import 'package:flutter/material.dart';
import 'package:google_sign_in_platform_interface/google_sign_in_platform_interface.dart';
import 'package:google_sign_in_web/google_sign_in_web.dart' as google_web;

Widget buildGoogleSignInButton({
  required String text,
  required String svgAssetPath,
  required VoidCallback onPressed,
  bool isLoading = false,
}) {
  return SizedBox(
    height: 50,
    width: double.infinity,
    child: (GoogleSignInPlatform.instance as google_web.GoogleSignInPlugin)
        .renderButton(),
  );
}
