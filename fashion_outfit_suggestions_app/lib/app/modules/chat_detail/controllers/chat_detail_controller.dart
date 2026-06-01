import 'package:fashion_outfit_suggestions_app/core/models/conversation_response.dart';
import 'package:fashion_outfit_suggestions_app/core/network/dio_client.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../../core/models/message_response.dart';
import '../../../../core/storage/token_storage.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/widgets/dialog_alert.dart';

class ChatDetailController extends GetxController {
  final String friendId;
  final String friendName;
  final String friendAvatar;

  final rxConversationId = RxnString();
  final messages = <MessageResponse>[].obs;
  final isLoading = false.obs;
  final isSending = false.obs;

  final DioClient _dioClient = Get.find<DioClient>();
  final TokenStorage _tokenStorage = Get.find<TokenStorage>();

  final textController = TextEditingController();
  final scrollController = ScrollController();
  final isTyping = false.obs;

  ChatDetailController({
    required this.friendId,
    required this.friendName,
    required this.friendAvatar,
    String? conversationId,
  }) {
    rxConversationId.value = conversationId;
  }

  String? get currentUserId => _tokenStorage.userId;

  @override
  void onInit() {
    super.onInit();
    if (rxConversationId.value != null) {
      fetchMessages();
    } else {
      checkOrCreateConversation();
    }
  }

  Future<void> checkOrCreateConversation() async {
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
      final existing = response.firstWhereOrNull((c) => c.friendId == friendId);

      if (existing != null) {
        rxConversationId.value = existing.conversationId;
        await fetchMessages();
      }
    } catch (e) {
      Get.dialog(
        DialogAlert(
          onConfirm: () {
            Get.back();
          },
          color: AppColors.error,
          icon: Icons.error,
          title: 'Error checking conversations: ',
          description: e.toString(),
        ),
        barrierDismissible: false,
      );
    } finally {
      isLoading.value = false;
    }
  }

  Future<void> fetchMessages() async {
    if (rxConversationId.value == null) return;
    isLoading.value = true;
    try {
      final response = await _dioClient.dio.get(
        '/api/chat/conversations/${rxConversationId.value}/messages',
      );
      final data = response.data;
      if (data != null &&
          data['result'] != null &&
          data['result']['content'] != null) {
        final content = data['result']['content'] as List;
        final list = content
            .map((e) => MessageResponse.fromJson(e as Map<String, dynamic>))
            .toList();
        messages.assignAll(list.reversed);
        scrollBottom();
      }
    } catch (e) {
      Get.dialog(
        DialogAlert(
          onConfirm: () {
            Get.back();
          },
          color: AppColors.error,
          icon: Icons.error,
          title: 'Error fetching messages',
          description: e.toString(),
        ),
        barrierDismissible: false,
      );
    } finally {
      isLoading.value = false;
    }
  }

  Future<void> sendMessage() async {
    final text = textController.text.trim();
    if (text.isEmpty) return;
    // if user are not chat before
    if (rxConversationId.value == null) {
      isSending.value = true;
      try {
        final createResponse = await _dioClient.getResult<ConversationResponse>(
          _dioClient.dio.post('/api/chat/conversations/$friendId'),
          (json) => ConversationResponse.fromJson(json as Map<String, dynamic>),
        );
        rxConversationId.value = createResponse.conversationId;
      } catch (e) {
        Get.dialog(
          DialogAlert(
            onConfirm: () {
              Get.back();
            },
            color: AppColors.error,
            icon: Icons.error,
            title: 'Can\'t connection chatting',
            description: e.toString(),
          ),
          barrierDismissible: false,
        );
        isSending.value = false;
        return;
      }
    }

    try {
      final response = await _dioClient.getResult<MessageResponse>(
        _dioClient.dio.post(
          '/api/chat/send',
          data: {
            'conversation_id': rxConversationId.value,
            'content': text,
            'type': 'TEXT',
          },
        ),
        (json) => MessageResponse.fromJson(json as Map<String, dynamic>),
      );
      messages.add(response);
      textController.clear();
      isTyping.value = false;
      scrollBottom();
    } catch (e) {
      Get.dialog(
        DialogAlert(
          onConfirm: () {
            Get.back();
          },
          color: AppColors.error,
          icon: Icons.error,
          title: 'Can\'t send message',
          description: e.toString(),
        ),
        barrierDismissible: false,
      );
    } finally {
      isSending.value = false;
    }
  }

  void scrollBottom() {
    Future.delayed(const Duration(milliseconds: 100), () {
      if (scrollController.hasClients) {
        scrollController.animateTo(
          scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    });
  }

  @override
  void onClose() {
    textController.dispose();
    scrollController.dispose();
    super.onClose();
  }
}
