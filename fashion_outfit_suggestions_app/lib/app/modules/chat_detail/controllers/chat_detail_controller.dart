import 'dart:async';
import 'package:fashion_outfit_suggestions_app/core/models/conversation_response.dart';
import 'package:fashion_outfit_suggestions_app/core/network/dio_client.dart';
import 'package:fashion_outfit_suggestions_app/core/network/socket_service.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../../core/models/message_response.dart';
import '../../../../core/storage/token_storage.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/widgets/dialog_alert.dart';
import '../../message/controllers/message_controller.dart';


class ChatDetailController extends GetxController {

  final String friendId;
  final String friendName;
  final String friendAvatar;

  final rxFriendIsTyping = false.obs;
  Timer? _typingTimer;
  DateTime? _lastTypingEmitTime;
  final rxConversationId = RxnString();
  final messages = <MessageResponse>[].obs;
  final isLoading = false.obs;
  final isSending = false.obs;

  final DioClient _dioClient = Get.find<DioClient>();
  final TokenStorage _tokenStorage = Get.find<TokenStorage>();
  final SocketService _socketService =
      Get.find<SocketService>(); // <--- Thêm socket service

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

    // Đảm bảo kết nối Socket và lắng nghe sự kiện tin nhắn mới
    _socketService.connect();
    _socketService.addMessageListener(_onNewMessageReceived);
    _socketService.addTypingListener(_onTypingStatusReceived);

    if (rxConversationId.value != null) {
      _socketService.joinRoom(
        rxConversationId.value!,
      ); // Tham gia vào phòng chat của cuộc trò chuyện này
      fetchMessages();
    } else {
      checkOrCreateConversation();
    }
    ever(rxConversationId, (_) => _markAsReadInMessageList());
  }



  void _onNewMessageReceived(Map<String, dynamic> data) {
    try {
      final message = MessageResponse.fromJson(data);
      if (message.conversationId == rxConversationId.value ||
          (rxConversationId.value == null && message.senderId == friendId)) {
        if (rxConversationId.value == null) {
          rxConversationId.value = message.conversationId;
          _socketService.joinRoom(message.conversationId!);
        }
        final alreadyExists = messages.any((m) => m.id == message.id);
        if (!alreadyExists) {
          messages.add(message);
          scrollBottom();
        }
      }
    } catch (e) {
      // Bỏ qua lỗi parse
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
        _socketService.joinRoom(
          existing.conversationId!,
        ); // Tham gia phòng chat
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

    if (rxConversationId.value == null) {
      isSending.value = true;
      debugPrint('>>> Creating conversation with friendId: $friendId');

      try {
        final createResponse = await _dioClient.getResult<ConversationResponse>(
          _dioClient.dio.post('/api/chat/conversations/$friendId'),
          (json) => ConversationResponse.fromJson(json as Map<String, dynamic>),
        );
        rxConversationId.value = createResponse.conversationId;
        _socketService.joinRoom(
          createResponse.conversationId!,
        ); // Tham gia phòng chat
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
            'conversationId': rxConversationId.value,
            'content': text,
            'type': 'TEXT',
          },
        ),
        (json) => MessageResponse.fromJson(json as Map<String, dynamic>),
      );

      final alreadyExists = messages.any((m) => m.id == response.id);
      if (!alreadyExists) {
        messages.add(response);
        scrollBottom();
      }
      textController.clear();
      isTyping.value = false;
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

  void _markAsReadInMessageList() {
    final conversationId = rxConversationId.value;
    if (conversationId != null && Get.isRegistered<MessageController>()) {
      try {
        final messageController = Get.find<MessageController>();
        final index = messageController.conversations.indexWhere(
          (c) => c.conversationId == conversationId,
        );
        if (index != -1) {
          final existing = messageController.conversations[index];
          messageController.conversations[index] = existing.copyWith(
            unreadCount: 0,
          );
        }
      } catch (e) {
        // Bỏ qua lỗi nếu controller chưa khởi tạo
      }
    }
  }



  @override
  void onClose() {
    _markAsReadInMessageList();
    if (rxConversationId.value != null) {
      _socketService.leaveRoom(rxConversationId.value!);
    }
    _socketService.removeMessageListener(_onNewMessageReceived);
    textController.dispose();
    scrollController.dispose();
    _socketService.removeTypingListener(_onTypingStatusReceived);
    _typingTimer?.cancel();
    super.onClose();
  }
}
