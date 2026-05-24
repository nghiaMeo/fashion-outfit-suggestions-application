import 'package:get_storage/get_storage.dart';

import '../constants/storage_keys.dart';

class TokenStorage {
  TokenStorage(this._box);

  final GetStorage _box;

  String? get accessToken => _box.read<String>(StorageKeys.accessToken);

  String? get refreshToken => _box.read<String>(StorageKeys.refreshToken);

  bool get hasSession =>
      accessToken != null &&
      accessToken!.isNotEmpty &&
      refreshToken != null &&
      refreshToken!.isNotEmpty;

  Future<void> saveSession({
    required String accessToken,
    required String refreshToken,
    String? userId,
  }) async {
    await _box.write(StorageKeys.accessToken, accessToken);
    await _box.write(StorageKeys.refreshToken, refreshToken);
    if (userId != null) {
      await _box.write(StorageKeys.userId, userId);
    }
  }

  Future<void> clearSession() async {
    await _box.remove(StorageKeys.accessToken);
    await _box.remove(StorageKeys.refreshToken);
    await _box.remove(StorageKeys.userId);
  }
}
