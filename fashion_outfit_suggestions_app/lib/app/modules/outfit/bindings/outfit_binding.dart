import 'package:get/get.dart';

import '../controllers/outfit_controller.dart';

class OutfitBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<OutfitController>(
      () => OutfitController(),
    );
  }
}
