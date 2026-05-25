import 'package:dio/dio.dart';
import 'package:fashion_outfit_suggestions_app/core/models/error_response.dart';

class ApiException implements Exception {
  final String message;
  final int? statusCode;
  final int? errorCode;

  ApiException({required this.message, this.statusCode, this.errorCode});

  @override
  String toString() => message;

  factory ApiException.fromResponse(dynamic data, int? statusCode) {
    if (data is Map<String, dynamic>) {
      final err = ErrorResponse.fromJson(data);
      return ApiException(
        message: err.message,
        statusCode: statusCode,
        errorCode: err.code,
      );
    }
    return ApiException(message: "Can't connect to server", statusCode: statusCode);
  }
}
