import 'package:get/get.dart';

class MessageController extends GetxController {
  final currentIndex = 3.obs;

  void changPage(int index) {
    currentIndex.value = index;
  }
}
