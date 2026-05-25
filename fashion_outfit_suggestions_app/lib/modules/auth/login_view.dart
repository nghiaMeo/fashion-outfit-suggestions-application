import 'package:flutter/material.dart';

/// Placeholder — Phase 1 sẽ gọi POST /api/auth/login.
class LoginView extends StatelessWidget {
  const LoginView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Đăng nhập')),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text(
            'Chưa cần backend để thấy màn này.\n'
            'Phase 1: form login gọi API Gateway :8880.',
            textAlign: TextAlign.center,
          ),
        ),
      ),
    );
  }
}
