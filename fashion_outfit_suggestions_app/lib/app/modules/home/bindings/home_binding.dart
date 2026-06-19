import 'package:fashion_outfit_suggestions_app/app/modules/profile/controllers/profile_controller.dart';
import 'package:fashion_outfit_suggestions_app/app/modules/wardrobe/controllers/wardrobe_controller.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../message/controllers/message_controller.dart';
import '../controllers/home_controller.dart';

class HomeBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<HomeController>(() => HomeController());
    Get.lazyPut<ProfileController>(() => ProfileController());
    Get.lazyPut<MessageController>(() => MessageController());
    Get.lazyPut<SearchController>(() => SearchController());
    Get.lazyPut<WardrobeController>(() => WardrobeController());
  }
}
