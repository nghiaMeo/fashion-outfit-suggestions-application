import 'dart:async';

import 'package:dio/dio.dart';
import 'package:fashion_outfit_suggestions_app/core/models/auth_response.dart';
import 'package:fashion_outfit_suggestions_app/core/storage/token_storage.dart';
import 'package:get/get.dart' hide Response;

import '../../app/routes/app_routes.dart';
import '../../config/env_config.dart';
import '../models/api_response.dart';

class AuthInterceptor extends Interceptor {
  final TokenStorage tokenStorage;
  final Dio dio;

  bool _isRefreshing = false;
  final List<Completer<String>> _waitQueue = [];

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

    // Bỏ qua auth endpoints để tránh vòng lặp vô tận
    if (path.contains('/auth/login') ||
        path.contains('/auth/register') ||
        path.contains('/auth/refresh-token')) {
      return handler.next(err);
    }

    if (err.response?.statusCode == 401) {
      final refresh = tokenStorage.refreshToken;

      if (refresh == null || refresh.isEmpty) {
        await _forceLogout();
        return handler.reject(err);
      }

      if (_isRefreshing) {
        final completer = Completer<String>();
        _waitQueue.add(completer);
        try {
          final newToken = await completer.future;
          err.requestOptions.headers['Authorization'] = 'Bearer $newToken';
          final retryResponse = await dio.fetch(err.requestOptions);
          return handler.resolve(retryResponse);
        } catch (_) {
          return handler.reject(err);
        }
      }

      _isRefreshing = true;
      try {
        final newAccessToken = await _doRefreshToken(refresh);

        for (final completer in _waitQueue) {
          completer.complete(newAccessToken);
        }
        _waitQueue.clear();

        err.requestOptions.headers['Authorization'] = 'Bearer $newAccessToken';
        final retryResponse = await dio.fetch(err.requestOptions);
        return handler.resolve(retryResponse);
      } catch (e) {
        for (final completer in _waitQueue) {
          completer.completeError(e);
        }
        _waitQueue.clear();
        await _forceLogout();
        return handler.reject(err);
      } finally {
        _isRefreshing = false;
      }
    }

    return handler.next(err);
  }

  Future<String> _doRefreshToken(String refreshToken) async {
    final refreshDio = Dio(
      BaseOptions(
        baseUrl: EnvConfig.apiBaseUrl,
        headers: {'Content-Type': 'application/json'},
        connectTimeout: const Duration(seconds: 15),
        receiveTimeout: const Duration(seconds: 15),
      ),
    );

    final response = await refreshDio.post<Map<String, dynamic>>(
      '/api/auth/refresh-token',
      data: {'refreshToken': refreshToken},
    );

    final body = response.data;
    if (body == null) {
      throw DioException(
        requestOptions: RequestOptions(path: '/api/auth/refresh-token'),
      );
    }

    final api = ApiResponse<AuthResponse>.fromJson(
      body,
      (json) => AuthResponse.fromJson(json! as Map<String, dynamic>),
    );

    final auth = api.result;
    if (auth == null) {
      throw DioException(
        requestOptions: RequestOptions(path: '/api/auth/refresh-token'),
      );
    }

    await tokenStorage.saveSession(
      accessToken: auth.accessToken,
      refreshToken: auth.refreshToken,
    );

    return auth.accessToken;
  }

  Future<void> _forceLogout() async {
    await tokenStorage.clearSession();
    Get.offAllNamed(Routes.login);
  }
}
