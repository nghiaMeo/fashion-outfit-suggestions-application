import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../../core/models/conversation_response.dart';
import '../../../../core/models/friend_response.dart';
import '../../../../core/models/message_response.dart'; // <--- Thêm import này
import '../../../../core/network/dio_client.dart';
import '../../../../core/network/socket_service.dart'; // <--- Thêm import này
import '../../../../core/storage/token_storage.dart'; // <--- Thêm import này
import '../../../../core/theme/app_colors.dart';
import '../../../../core/widgets/dialog_alert.dart';

class MessageController extends GetxController {
  final currentIndex = 3.obs;
  final conversations = <ConversationResponse>[].obs;
  final isLoading = false.obs;

  final friends = <FriendResponse>[].obs;
  final isFriendsLoading = false.obs;
  final selectedFriend = Rxn<FriendResponse>();

  final searchQuery = ''.obs;
  final DioClient _dioClient = Get.find<DioClient>();
  final TokenStorage _tokenStorage = Get.find<TokenStorage>();
  final SocketService _socketService = Get.find<SocketService>();

  @override
  void onInit() {
    super.onInit();
    fetchConversations();
    fetchFriends();
    _socketService.connect();
    _socketService.addMessageListener(_onNewMessageReceived);
  }

  List<FriendResponse> get filteredFriends {
    final query = searchQuery.value.trim().toLowerCase();
    if (query.isEmpty) {
      return [];
    }
    return friends.where((friend) {
      final name = friend.fullName.toLowerCase();
      final username = friend.username.toLowerCase();
      return name.contains(query) || username.contains(query);
    }).toList();
  }

  void _onNewMessageReceived(Map<String, dynamic> data) {
    try {
      final msg = MessageResponse.fromJson(data);
      final convIndex = conversations.indexWhere(
        (c) => c.conversationId == msg.conversationId,
      );

      if (convIndex != -1) {
        final existing = conversations[convIndex];
        int newUnread = existing.unreadCount ?? 0;
        if (msg.senderId != _tokenStorage.userId) {
          newUnread += 1;
        }

        final updated = existing.copyWith(
          lastMessage: msg.content,
          lastMessageAt: msg.createdAt,
          unreadCount: newUnread,
        );

        conversations.removeAt(convIndex);
        conversations.insert(0, updated);
      } else {
        fetchConversations();
      }
    } catch (_) {}
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

  Future<void> fetchFriends() async {
    isFriendsLoading.value = true;
    try {
      final response = await _dioClient.getResult<List<FriendResponse>>(
        _dioClient.dio.get('/api/friendship/my-friends'),
        (json) {
          final list = json as List;
          return list
              .map((e) => FriendResponse.fromJson(e as Map<String, dynamic>))
              .toList();
        },
      );
      friends.assignAll(response);
    } catch (e) {
      // Silently ignore - friends list is non-critical
    } finally {
      isFriendsLoading.value = false;
    }
  }

  void changPage(int index) {
    currentIndex.value = index;
  }

  @override
  void onClose() {
    _socketService.removeMessageListener(_onNewMessageReceived);
    super.onClose();
  }
}
