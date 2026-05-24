class ErrorResponse {
  final int code;
  final String message;

  const ErrorResponse({required this.code, required this.message});

  factory ErrorResponse.fromJson(Map<String, dynamic> json) {
    return ErrorResponse(
      code: json['code'] as int? ?? 0,
      message: json['message'] as String? ?? "Internal Server Error",
    );
  }
}