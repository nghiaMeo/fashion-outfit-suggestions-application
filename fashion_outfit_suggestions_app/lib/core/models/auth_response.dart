import 'package:fashion_outfit_suggestions_app/core/models/user_response.dart';

class AuthResponse {
  final String accessToken;
  final String refreshToken;
  final UserResponse? userResponse;

  const AuthResponse({
    required this.accessToken,
    required this.refreshToken,
    this.userResponse,
  });

  factory AuthResponse.fromJson(Map<String, dynamic> json) {
    return AuthResponse(
      accessToken: json['accessToken'] as String,
      refreshToken: json['refreshToken'] as String,
      userResponse: json['user'] == null
          ? null
          : UserResponse.fromJson(json['user'] as Map<String, dynamic>),
    );
  }

  AuthResponse copyWith({
    String? accessToken,
    String? refreshToken,
    UserResponse? userResponse,
  }) {
    return AuthResponse(
      accessToken: accessToken ?? this.accessToken,
      refreshToken: refreshToken ?? this.refreshToken,
      userResponse: userResponse ?? this.userResponse,
    );
  }
}
