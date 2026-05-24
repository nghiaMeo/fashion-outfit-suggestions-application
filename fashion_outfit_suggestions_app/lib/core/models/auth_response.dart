import 'package:fashion_outfit_suggestions_app/core/models/user_response.dart';

class AuthResponse {
  final String accessToken;
  final String refreshToken;
  final UserResponse? userResponse;

  const AuthResponse({required this.accessToken, required this.refreshToken, this.userResponse});

  factory AuthResponse.fromJson(Map<String, dynamic> json){
    return AuthResponse(
      accessToken: json['accessToken'] as String,
      refreshToken: json['refreshToken'] as String,
      userResponse: json['user'] == null ? null : UserResponse.fromJson(json['user'] as Map<String, dynamic>),
    );
  }
}