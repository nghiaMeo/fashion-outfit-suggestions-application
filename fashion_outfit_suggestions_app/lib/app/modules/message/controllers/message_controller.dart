import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../../core/models/conversation_response.dart';
import '../../../../core/network/dio_client.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/widgets/dialog_alert.dart';

class MessageController extends GetxController {
  final currentIndex = 3.obs;
  final conversations = <ConversationResponse>[].obs;
  final isLoading = false.obs;
  final DioClient _dioClient = Get.find<DioClient>();

  @override
  void onInit() {
    super.onInit();
    fetchConversations();
  }

  Future<void> fetchConversations() async {
    isLoading.value = true;
    try {
      final response = await _dioClient.getResult<List<ConversationResponse>>(
        _dioClient.dio.get('/api/chat/conversations'),
        (json) {
          final list = json as List;
          return list
              .map(
                (e) => ConversationResponse.fromJson(e as Map<String, dynamic>),
              )
              .toList();
        },
      );
      response.sort((a, b) {
        if (a.lastMessageAt == null) return 1;
        if (b.lastMessageAt == null) return -1;
        return b.lastMessageAt!.compareTo(a.lastMessageAt!);
      });
      conversations.assignAll(response);
    } catch (e) {
      Get.dialog(
        DialogAlert(
          onConfirm: () {
            Get.back();
          },
          color: AppColors.error,
          icon: Icons.error,
          title: 'Error fetching conversations',
          description: e.toString(),
        ),
        barrierDismissible: false,
      );
    } finally {
      isLoading.value = false;
    }
  }

  void changPage(int index) {
    currentIndex.value = index;
  }
}
