import 'package:get/get.dart';

import '../controllers/searches_controller.dart';

class SearchesBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<SearchesController>(
      () => SearchesController(),
    );
  }
}
