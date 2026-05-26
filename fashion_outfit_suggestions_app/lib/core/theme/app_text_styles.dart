import 'package:flutter/material.dart';

import 'app_colors.dart';
import 'app_fonts.dart';

abstract final class AppTextStyles {
  static TextStyle get headline => AppFonts.base(
    fontSize: 28,
    fontWeight: FontWeight.w600,
    color: AppColors.secondary,
  );

  static TextStyle get body => AppFonts.base(
    fontSize: 16,
    fontWeight: FontWeight.w400,
    color: AppColors.primary,
  );

  static TextStyle get label => AppFonts.base(
    fontSize: 12,
    fontWeight: FontWeight.w500,
    color: AppColors.primary,
  );
}
