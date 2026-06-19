import 'package:get/get.dart';

import '../controllers/wardrobe_controller.dart';

class WardrobeBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<WardrobeController>(
      () => WardrobeController(),
    );
  }
}
