import 'package:fashion_outfit_suggestions_app/core/models/notification_response.dart';
import 'package:fashion_outfit_suggestions_app/core/models/user_profile_response.dart';
import 'package:fashion_outfit_suggestions_app/core/network/dio_client.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../../core/theme/app_colors.dart';

class NotificationController extends GetxController {
  final DioClient _dioClient = Get.find<DioClient>();

  final notification = <NotificationResponse>[].obs;
  final suggestedUsers = <UserProfileResponse>[].obs;

  final isLoading = false.obs;
  final isSuggestionsLoading = false.obs;

  @override
  void onInit() {
    super.onInit();
    fetchNotifications();
    fetchSuggestions();
    markAllNotificationsAsRead();
  }

  Future<void> acceptFriendRequest(String friendshipId, int index) async {
    try {
      await _dioClient.dio.post('/api/friendship/accept/$friendshipId');
      notification.removeAt(index);
      Get.snackbar(
        'Success',
        'Follow request accepted!',
        snackPosition: SnackPosition.TOP,
        backgroundColor: AppColors.primary,
        colorText: Colors.black,
      );
    } catch (e) {
      Get.snackbar('Error', 'Failed to accept: $e');
    }
  }

  Future<void> rejectFriendRequest(String friendshipId, int index) async {
    try {
      await _dioClient.dio.delete('/api/friendship/cancel/$friendshipId');
      notification.removeAt(index);
      Get.snackbar(
        'Removed',
        'Follow request removed.',
        snackPosition: SnackPosition.TOP,
        backgroundColor: Colors.white24,
        colorText: Colors.white,
      );
    } catch (e) {
      Get.snackbar('Error', 'Failed to remove: $e');
    }
  }

  Future<void> followUser(String userId, int index) async {
    try {
      await _dioClient.dio.post('/api/friendship/request/$userId');
      suggestedUsers.removeAt(index);
      Get.snackbar(
        'Request Sent',
        'Follow request sent successfully!',
        snackPosition: SnackPosition.TOP,
        backgroundColor: AppColors.primary,
        colorText: Colors.black,
      );
    } catch (e) {
      Get.snackbar('Error', 'Failed to follow: $e');
    }
  }

  Future<void> markAllNotificationsAsRead() async {
    try {
      await _dioClient.dio.put('/api/notifications/read-all');
    } catch (e) {
      print('Mark read-all error: $e');
    }
  }

  Future<void> fetchSuggestions() async {
    isSuggestionsLoading.value = true;
    try {
      final List<UserProfileResponse> list = await _dioClient
          .getResult<List<UserProfileResponse>>(
            _dioClient.dio.get('/api/user/suggest-candidates'),
            (json) {
              final data = json as List<dynamic>;
              return data
                      ?.map(
                        (e) => UserProfileResponse.fromJson(
                          e as Map<String, dynamic>,
                        ),
                      )
                      .toList() ??
                  [];
            },
          );
      suggestedUsers.value = list;
    } catch (e) {
      print('Fetch suggestions error: $e');
    } finally {
      isSuggestionsLoading.value = false;
    }
  }

  Future<void> fetchNotifications() async {
    isLoading.value = true;
    try {
      final List<NotificationResponse> list = await _dioClient
          .getResult<List<NotificationResponse>>(
            _dioClient.dio.get('/api/notifications'),
            (json) {
              final data = json as List<dynamic>;
              return data
                      ?.map(
                        (e) => NotificationResponse.fromJson(
                          e as Map<String, dynamic>,
                        ),
                      )
                      .toList() ??
                  [];
            },
          );
      notification.value = list;
    } catch (e) {
      print('Fetch notifications error: $e');
    } finally {
      isLoading.value = false;
    }
  }
}
