import 'dart:io';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

abstract final class AppFonts {
  static String get family {
    if (!kIsWeb && Platform.isIOS) return 'Helvetica Neue';
    return 'Inter';
  }

  static TextStyle base({
    double? fontSize,
    FontWeight? fontWeight,
    Color? color,
    double? height,
  }) {
    if (!kIsWeb && Platform.isIOS) {
      return TextStyle(
        fontFamily: 'Helvetica Neue',
        fontSize: fontSize,
        fontWeight: fontWeight,
        color: color,
        height: height,
      );
    }
    return TextStyle(
      fontFamily: GoogleFonts.inter(
        fontSize: fontSize,
        fontWeight: fontWeight,
        color: color,
        height: height,
      ).fontFamily,
    );
  }
}
