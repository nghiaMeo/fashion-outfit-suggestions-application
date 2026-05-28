import 'package:fashion_outfit_suggestions_app/core/storage/token_storage.dart';
import 'package:get/get.dart';

import '../../routes/app_routes.dart';

class SplashController extends GetxController {
  final _storage = Get.find<TokenStorage>();

  @override
  void onReady() {
    super.onReady();
    _goNext();
  }

  Future<void> _goNext() async {
    await Future.delayed(const Duration(milliseconds: 3000));
    if (_storage.accessToken != null) {
      Get.offAllNamed(Routes.home);
    } else {
      Get.offAllNamed(Routes.login);
    }
  }
}
