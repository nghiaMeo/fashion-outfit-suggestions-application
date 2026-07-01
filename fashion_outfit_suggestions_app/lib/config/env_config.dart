import 'dart:io';

import 'package:flutter/foundation.dart';

class EnvConfig {
  EnvConfig._();

  static const _apiDefine = String.fromEnvironment('API_BASE_URL');

  static String get apiBaseUrl {
    if (_apiDefine.isNotEmpty) return _apiDefine;
    if (kIsWeb) return 'http://localhost:8080';
    if (Platform.isAndroid) return 'http://10.0.2.2:8080';
    return 'http://localhost:8080';
  }

  static String get webSocketUrl {
    final uri = Uri.parse(apiBaseUrl);
    final scheme = uri.scheme == 'https' ? 'wss' : 'ws';
    final port = uri.hasPort ? uri.port : (uri.scheme == 'https' ? 443 : 80);
    return '$scheme://${uri.host}:$port/api/ws-native';
  }
}
