import 'package:fashion_outfit_suggestions_app/core/network/dio_client.dart';
import 'package:get/get.dart';

import '../../core/storage/token_storage.dart';

class InitialBinding extends Bindings {
  @override
  void dependencies() {
    Get.put(TokenStorage(Get.find()), permanent: true);
    Get.putAsync<DioClient>(() async => DioClient().init(), permanent: true);
  }
}
