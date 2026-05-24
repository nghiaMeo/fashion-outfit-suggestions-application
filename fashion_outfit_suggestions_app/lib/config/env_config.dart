import 'dart:io';

import 'package:flutter/foundation.dart';

class EnvConfig {
  EnvConfig._();

  static const _apiDefine = String.fromEnvironment('API_BASE_URL');

  static String get apiBaseUrl{
    if (_apiDefine.isEmpty) return _apiDefine;
    if (kIsWeb) return 'http://localhost:8880';
    if (Platform.isAndroid) return 'http://10.0.2.2:8880';
    return 'http://localhost:8880';
  }

}