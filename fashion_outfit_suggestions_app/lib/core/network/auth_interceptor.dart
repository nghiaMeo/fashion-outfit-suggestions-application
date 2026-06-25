import 'package:dio/dio.dart';
import 'package:fashion_outfit_suggestions_app/core/models/auth_response.dart';
import 'package:fashion_outfit_suggestions_app/core/storage/token_storage.dart';

import '../../config/env_config.dart';
import '../models/api_response.dart';

class AuthInterceptor extends Interceptor {
  final TokenStorage tokenStorage;
  final Dio dio;

  AuthInterceptor({required this.tokenStorage, required this.dio});

  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    final token = tokenStorage.accessToken;

    if (token != null && token.isNotEmpty) {
      options.headers['Authorization'] = 'Bearer $token';
    }
    return handler.next(options);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) async {
    final path = err.requestOptions.path;

    // Không refresh token cho các endpoint auth
    if (path.contains('/api/auth/login') ||
        path.contains('/api/auth/register') ||
        path.contains('/api/auth/refresh-token')) {
      return handler.next(err);
    }

    // Khi gặp 401 → thử refresh token
    if (err.response?.statusCode == 401) {
      final refresh = tokenStorage.refreshToken;
      if (refresh == null || refresh.isEmpty) {
        await tokenStorage.clearSession();
        return handler.next(err);
      }

      try {
        final newAccessToken = await _refreshToken(refresh);
        // Gắn token mới và retry request
        err.requestOptions.headers['Authorization'] = 'Bearer $newAccessToken';
        final response = await dio.fetch(err.requestOptions);
        return handler.resolve(response);
      } catch (_) {
        await tokenStorage.clearSession();
        return handler.next(err);
      }
    }

    return handler.next(err);
  }

  Future<String> _refreshToken(String refreshToken) async {
    final refreshDio = Dio(
      BaseOptions(
        baseUrl: EnvConfig.apiBaseUrl,
        headers: {'Content-Type': 'application/json'},
      ),
    );

    final response = await refreshDio.post<Map<String, dynamic>>(
      'api/auth/refresh-token',
      data: {'refreshToken': refreshToken},
    );

    final body = response.data;
    if (body == null) {
      throw DioException(requestOptions: RequestOptions(path: ''));
    }

    final api = ApiResponse<AuthResponse>.fromJson(
      body,
      (json) => AuthResponse.fromJson(json! as Map<String, dynamic>),
    );

    final auth = api.result;
    if (auth == null) {
      throw DioException(requestOptions: RequestOptions(path: ''));
    }
    await tokenStorage.saveSession(
      accessToken: auth.accessToken,
      refreshToken: auth.refreshToken,
    );

    return auth.accessToken;
  }
}
