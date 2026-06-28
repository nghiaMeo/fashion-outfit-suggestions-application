import 'package:fashion_outfit_suggestions_app/core/network/dio_client.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

import '../../core/network/socket_service.dart';
import '../../core/storage/token_storage.dart';

class InitialBinding extends Bindings {
  @override
  void dependencies() {
    Get.put(TokenStorage(GetStorage()), permanent: true);
    Get.putAsync<DioClient>(() async => DioClient().init(), permanent: true);
    final socketService = SocketService();
    socketService.init();
    Get.put(socketService, permanent: true);
  }
}
