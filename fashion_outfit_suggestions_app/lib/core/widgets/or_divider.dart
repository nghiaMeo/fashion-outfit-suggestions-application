import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import '../theme/app_colors.dart';

class OrDivider extends StatelessWidget {
  const OrDivider({super.key});

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        const Expanded(child: Divider(color: Color(0xFF2C2C2C), thickness: 1)),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: Text(
            'Or',
            style: Theme.of(
              context,
            ).textTheme.labelSmall!.copyWith(color: AppColors.placeholder),
          ),
        ),
        const Expanded(child: Divider(color: Color(0xFF2C2C2C), thickness: 1)),
      ],
    );
  }
}
