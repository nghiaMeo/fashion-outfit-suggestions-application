import 'package:fashion_outfit_suggestions_app/core/models/notification_response.dart';
import 'package:fashion_outfit_suggestions_app/core/models/user_profile_response.dart';
import 'package:fashion_outfit_suggestions_app/core/network/dio_client.dart';
import 'package:get/get.dart';

class NotificationController extends GetxController {
  final DioClient _dioClient = Get.find<DioClient>();

  final notification = <NotificationResponse>[].obs;
  final suggestedUsers = <UserProfileResponse>[].obs;

  final isLoading = false.obs;
  final isSuggestionsLoading = false.obs;

}
