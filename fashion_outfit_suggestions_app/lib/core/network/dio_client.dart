import 'package:dio/dio.dart';
import 'package:fashion_outfit_suggestions_app/core/network/api_exception.dart';
import 'package:fashion_outfit_suggestions_app/core/storage/token_storage.dart';
import 'package:get/get.dart' hide Response;

import '../../config/env_config.dart';
import '../models/api_response.dart';
import 'auth_interceptor.dart';

class DioClient extends GetxService {
  late final Dio dio;


  Future<DioClient> init() async {
    dio = Dio(
      BaseOptions(
        baseUrl: EnvConfig.apiBaseUrl,
        connectTimeout: const Duration(seconds: 30),
        receiveTimeout: const Duration(seconds: 30),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      ),
    );

    final storage = Get.find<TokenStorage>();
    dio.interceptors.add(AuthInterceptor(tokenStorage: storage, dio: dio));

    return this;
  }

  Future<T> getResult<T>(
    Future<Response<Map<String, dynamic>>> call,
    T Function(Object? json) fromJson,
  ) async {
    try {
      final response = await call;
      final data = response.data;
      if (data == null) throw ApiException(message: "Empty Body");
      final api = ApiResponse<T>.fromJson(data, fromJson);
      if (api.result == null) throw ApiException(message: api.message);
      return api.result as T;
    } on DioException catch (e) {
      throw ApiException.fromResponse(e.response?.data, e.response?.statusCode);
    }
  }

}
