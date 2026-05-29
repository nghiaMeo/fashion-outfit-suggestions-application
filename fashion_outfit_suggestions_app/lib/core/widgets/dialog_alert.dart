import 'dart:ui';
import 'package:flutter/material.dart';
import '../theme/app_colors.dart';
import '../theme/app_fonts.dart';
import 'primary_capsule_button.dart';

class DialogAlert extends StatelessWidget {
  final VoidCallback onConfirm;
  final Color color;
  final IconData icon;
  final String title;
  final String description;

  const DialogAlert({
    super.key,
    required this.onConfirm,
    required this.color,
    required this.icon,
    required this.title,
    required this.description,
  });

  @override
  Widget build(BuildContext context) {
    return BackdropFilter(
      filter: ImageFilter.blur(sigmaX: 10, sigmaY: 10),
      child: Center(
        child: Container(
          margin: const EdgeInsets.symmetric(horizontal: 32),
          padding: const EdgeInsets.all(28),
          decoration: BoxDecoration(
            color: const Color(0xFF1E1E1E).withOpacity(0.85),
            borderRadius: BorderRadius.circular(24),
            border: Border.all(color: color.withOpacity(0.15), width: 1.5),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.4),
                blurRadius: 24,
                offset: const Offset(0, 8),
              ),
            ],
          ),
          child: Material(
            color: Colors.transparent,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Container(
                  padding: const EdgeInsets.all(16),
                  decoration: BoxDecoration(
                    color: color.withOpacity(0.1),
                    shape: BoxShape.circle,
                  ),
                  child: Icon(icon, color: color, size: 56),
                ),
                const SizedBox(height: 24),

                Text(
                  title,
                  textAlign: TextAlign.center,
                  style: AppFonts.base(
                    fontSize: 22,
                    fontWeight: FontWeight.bold,
                    color: color,
                  ),
                ),
                const SizedBox(height: 12),

                Text(
                  description,
                  textAlign: TextAlign.center,
                  style: AppFonts.base(
                    fontSize: 14,
                    color: AppColors.secondary.withOpacity(0.7),
                    height: 1.5,
                  ),
                ),
                const SizedBox(height: 32),

                SizedBox(
                  width: double.infinity,
                  child: PrimaryCapsuleButton(
                    text: 'OK',
                    onPressed: onConfirm,
                    isLoading: false,
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
