class ApiResponse<T> {
  final int code;
  final String message;
  final T? result;

  const ApiResponse({required this.code, required this.message, this.result});

  factory ApiResponse.fromJson(
    Map<String, dynamic> json,
    T Function(Object? json) fromJsonT,
  ) {
    return ApiResponse<T>(
      code: json['code'] as int? ?? 200,
      message: json['message'] as String? ?? "Success",
      result: json['result'] == null ? null : fromJsonT(json['result']),
    );
  }

  ApiResponse copyWith({int? code, String? message, T? result}) {
    return ApiResponse<T>(
      code: code ?? this.code,
      message: message ?? this.message,
      result: result ?? this.result,
    );
  }
}
