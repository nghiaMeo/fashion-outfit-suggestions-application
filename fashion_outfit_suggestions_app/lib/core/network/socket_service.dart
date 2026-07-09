import 'dart:convert';

import 'package:fashion_outfit_suggestions_app/config/env_config.dart';
import 'package:fashion_outfit_suggestions_app/core/storage/token_storage.dart';
import 'package:flutter/foundation.dart';
import 'package:get/get.dart';
import 'package:stomp_dart_client/stomp_dart_client.dart';

class SocketService extends GetxService {
  final TokenStorage _tokenStorage = Get.find<TokenStorage>();
  StompClient? _stompClient;

  final isConnected = false.obs;

  final List<void Function(Map<String, dynamic> data)> _messageListeners = [];
  final List<void Function(Map<String, dynamic> data)> _typingListeners = [];

  final Set<String> _joinedRooms = {};
  final Map<String, StompUnsubscribe> _messageSubscriptions = {};
  final Map<String, StompUnsubscribe> _typingSubscriptions = {};
  StompUnsubscribe? _userMessageSubscription;
  final List<void Function(Map<String, dynamic> data)> _notificationListeners = [];
  StompUnsubscribe? _userNotificationSubscription;

  Future<SocketService> init() async {
    if (_tokenStorage.hasSession) {
      connect();
    }
    return this;
  }

  void connect() {
    if (_stompClient?.connected == true) return;

    final token = _tokenStorage.accessToken;
    if (token == null || token.isEmpty) return;

    _stompClient?.deactivate();
    _stompClient = StompClient(
      config: StompConfig(
        url: EnvConfig.webSocketUrl,
        onConnect: _onStompConnect,
        onDisconnect: (_) {
          isConnected.value = false;
          debugPrint('>>> STOMP DISCONNECTED');
        },
        onWebSocketError: (error) {
          debugPrint('>>> WEBSOCKET ERROR: $error');
        },
        onStompError: (frame) {
          debugPrint('>>> STOMP ERROR: ${frame.body}');
        },
        stompConnectHeaders: {'Authorization': 'Bearer $token'},
        webSocketConnectHeaders: {'Authorization': 'Bearer $token'},
        reconnectDelay: const Duration(seconds: 3),
      ),
    );

    _stompClient!.activate();
  }

  void _onStompConnect(StompFrame frame) {
    isConnected.value = true;
    debugPrint('>>> STOMP CONNECTED SUCCESSFULLY');

    _userNotificationSubscription?.call();
    _userNotificationSubscription  = _stompClient!.subscribe(
      destination: '/user/queue/notifications',
      callback: _handleNotificationFrame,
    );

    for (final conversationId in _joinedRooms) {
      _subscribeToConversation(conversationId);
    }
  }

  void _handleNotificationFrame(StompFrame frame){
    final body = frame.body;
    if (body == null || body.isEmpty) return;

    try {
      final data = Map<String, dynamic>.from(jsonDecode(body) as Map);
      for (final listener in _notificationListeners) {
        listener(data);
      }
    } catch (e) {
      debugPrint('>>> STOMP MESSAGE PARSE ERROR: $e');
    }
  }

  void _handleMessageFrame(StompFrame frame) {
    final body = frame.body;
    if (body == null || body.isEmpty) return;

    try {
      final data = Map<String, dynamic>.from(jsonDecode(body) as Map);
      for (final listener in _messageListeners) {
        listener(data);
      }
    } catch (e) {
      debugPrint('>>> STOMP MESSAGE PARSE ERROR: $e');
    }
  }

  void _handleTypingFrame(StompFrame frame) {
    final body = frame.body;
    if (body == null || body.isEmpty) return;

    try {
      final data = Map<String, dynamic>.from(jsonDecode(body) as Map);
      final isTyping = data['isTyping'] ?? data['typing'] ?? true;
      for (final listener in _typingListeners) {
        listener({...data, 'isTyping': isTyping});
      }
    } catch (e) {
      debugPrint('>>> STOMP TYPING PARSE ERROR: $e');
    }
  }

  void disconnect() {
    _userMessageSubscription?.call();
    _userNotificationSubscription?.call();
    _userMessageSubscription = null;

    for (final unsubscribe in _messageSubscriptions.values) {
      unsubscribe();
    }
    for (final unsubscribe in _typingSubscriptions.values) {
      unsubscribe();
    }
    _messageSubscriptions.clear();
    _typingSubscriptions.clear();

    _stompClient?.deactivate();
    _stompClient = null;
    isConnected.value = false;
  }

  void joinRoom(String conversationId) {
    _joinedRooms.add(conversationId);
    if (_stompClient?.connected == true) {
      _subscribeToConversation(conversationId);
    }
  }

  void leaveRoom(String conversationId) {
    _joinedRooms.remove(conversationId);
    _messageSubscriptions.remove(conversationId)?.call();
    _typingSubscriptions.remove(conversationId)?.call();
  }

  void _subscribeToConversation(String conversationId) {
    if (_stompClient?.connected != true) return;

    _messageSubscriptions.remove(conversationId)?.call();
    _typingSubscriptions.remove(conversationId)?.call();

    _messageSubscriptions[conversationId] = _stompClient!.subscribe(
      destination: '/topic/conversations.$conversationId',
      callback: _handleMessageFrame,
    );

    _typingSubscriptions[conversationId] = _stompClient!.subscribe(
      destination: '/topic/conversations.$conversationId.typing',
      callback: _handleTypingFrame,
    );
  }

  void addNotificationListener(void Function(Map<String, dynamic> data) listener) {
    _notificationListeners.add(listener);
  }
  void removeNotificationListener(void Function(Map<String, dynamic> data) listener) {
    _notificationListeners.remove(listener);
  }

  void addMessageListener(void Function(Map<String, dynamic> data) listener) {
    _messageListeners.add(listener);
  }

  void removeMessageListener(
    void Function(Map<String, dynamic> data) listener,
  ) {
    _messageListeners.remove(listener);
  }

  void addTypingListener(void Function(Map<String, dynamic> data) listener) {
    _typingListeners.add(listener);
  }

  void removeTypingListener(void Function(Map<String, dynamic> data) listener) {
    _typingListeners.remove(listener);
  }

  void sendTypingStatus(String conversationId, bool isTyping) {
    if (_stompClient?.connected != true) return;

    _stompClient!.send(
      destination: '/app/chat.typing',
      body: jsonEncode({
        'conversationId': conversationId,
        'typing': isTyping,
      }),
    );
  }
}
