import 'package:fashion_outfit_suggestions_app/config/env_config.dart';
import 'package:fashion_outfit_suggestions_app/core/storage/token_storage.dart';
import 'package:get/get.dart';
import 'package:socket_io_client/socket_io_client.dart' as io;

class SocketService extends GetxService {
  final TokenStorage _tokenStorage = Get.find<TokenStorage>();
  io.Socket? _socket;

  final isConnected = false.obs;

  // Danh sách các callback lắng nghe sự kiện tin nhắn mới
  final List<void Function(Map<String, dynamic> data)> _messageListeners = [];

  Future<SocketService> init() async {
    // Tự động kết nối nếu người dùng đã đăng nhập sẵn
    if (_tokenStorage.hasSession) {
      connect();
    }
    return this;
  }

  void connect() {
    // Nếu đã kết nối rồi thì không kết nối lại
    if (_socket != null && _socket!.connected) return;

    final token = _tokenStorage.accessToken;
    if (token == null || token.isEmpty) return;

    final url = EnvConfig.socialSocketUrl;

    _socket = io.io(
      url,
      io.OptionBuilder()
          .setTransports(['websocket']) // Chỉ dùng websocket protocol
          .disableAutoConnect()
          .setQuery({'token': token}) // Truyền token xác thực ở handshake query
          .build(),
    );

    _socket!.onConnect((_) {
      isConnected.value = true;
    });

    _socket!.onDisconnect((_) {
      isConnected.value = false;
    });

    // Lắng nghe sự kiện new_message từ backend phát đi
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

  // Phương thức tham gia phòng chat mới (phòng chat tương ứng với conversationId)
  void joinRoom(String conversationId) {
    if (_socket != null && _socket!.connected) {
      _socket!.emit('join_room', conversationId);
    }
  }

  // Phương thức rời khỏi phòng chat
  void leaveRoom(String conversationId) {
    if (_socket != null && _socket!.connected) {
      _socket!.emit('leave_room', conversationId);
    }
  }

  // Đăng ký nhận tin nhắn mới
  void addMessageListener(void Function(Map<String, dynamic> data) listener) {
    _messageListeners.add(listener);
  }

  // Huỷ đăng ký nhận tin nhắn mới
  void removeMessageListener(
    void Function(Map<String, dynamic> data) listener,
  ) {
    _messageListeners.remove(listener);
  }
}
