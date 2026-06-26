import 'package:fashion_outfit_suggestions_app/config/env_config.dart';
import 'package:fashion_outfit_suggestions_app/core/storage/token_storage.dart';
import 'package:get/get.dart';
import 'package:socket_io_client/socket_io_client.dart' as io;

class SocketService extends GetxService {
  final TokenStorage _tokenStorage = Get.find<TokenStorage>();
  io.Socket? _socket;

  final isConnected = false.obs;

  final List<void Function(Map<String, dynamic> data)> _messageListeners = [];
  final List<void Function(Map<String, dynamic> data)> _typingListeners = [];

  Future<SocketService> init() async {
    if (_tokenStorage.hasSession) {
      connect();
    }
    return this;
  }

  void connect() {
    if (_socket != null && _socket!.connected) return;

    final token = _tokenStorage.accessToken;
    if (token == null || token.isEmpty) return;

    final url = EnvConfig.socialSocketUrl;

    _socket!.on('typing', (data) {
      if (data is Map<String, dynamic>) {
        for (var listener in _typingListeners) {
          listener({...data, 'isTyping': true});
        }
      }
    });

    _socket!.on('stop_typing', (data) {
      if (data is Map<String, dynamic>) {
        for (var listeners in _typingListeners) {
          listeners({...data, 'isTyping': false});
        }
      }
    });

    _socket = io.io(
      url,
      io.OptionBuilder()
          .setTransports(['websocket'])
          .disableAutoConnect()
          .setQuery({'token': token})
          .build(),
    );

    _socket!.onConnect((_) {
      isConnected.value = true;
    });

    _socket!.onDisconnect((_) {
      isConnected.value = false;
    });

    _socket!.on('new_message', (data) {
      if (data is Map<String, dynamic>) {
        for (var listener in _messageListeners) {
          listener(data);
        }
      }
    });

    _socket!.connect();
  }

  void disconnect() {
    if (_socket != null) {
      _socket!.disconnect();
      _socket!.close();
      _socket = null;
      isConnected.value = false;
    }
  }

  void joinRoom(String conversationId) {
    if (_socket != null && _socket!.connected) {
      _socket!.emit('join_room', conversationId);
    }
  }

  void leaveRoom(String conversationId) {
    if (_socket != null && _socket!.connected) {
      _socket!.emit('leave_room', conversationId);
    }
  }

  void addMessageListener(void Function(Map<String, dynamic> data) listener) {
    _messageListeners.add(listener);
  }

  void removeMessageListener(
    void Function(Map<String, dynamic> data) listener,
  ) {
    _messageListeners.remove(listener);
  }
}
