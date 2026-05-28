import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../core/storage/token_storage.dart';
import '../../routes/app_routes.dart';

class HomeView extends StatelessWidget {
  const HomeView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Trang chủ'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () async {
              await Get.find<TokenStorage>().clearSession();
              Get.offAllNamed(Routes.login);
            },
          ),
        ],
      ),
      body: const Center(child: Text('Đã đăng nhập (có token trong máy).')),
    );
  }
}
