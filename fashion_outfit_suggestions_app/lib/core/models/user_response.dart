import 'package:fashion_outfit_suggestions_app/core/models/user_role.dart';

class UserResponse {
  final String id;
  final String? email;
  final String? username;
  final String? displayName;
  final String? avatarUrl;
  final String? bio;
  final String? role;
  final String? createdAt;

  const UserResponse({
    required this.id,
    this.email,
    this.username,
    this.displayName,
    this.avatarUrl,
    this.bio,
    this.role,
    this.createdAt,
  });

  factory UserResponse.fromJson(Map<String, dynamic> json) {
    return UserResponse(
      id: json['id'] as String,
      email: json['email'] as String?,
      username: json['username'] as String?,
      displayName: json['displayName'] as String?,
      avatarUrl: json['avatarUrl'] as String?,
      bio: json['bio'] as String?,
      role: UserRole.fromJson(json['role'])?.name,
      createdAt: json['createdAt'] as String?,
    );
  }

  String get displayLabel => displayName?.trim().isNotEmpty == true
      ? displayName!
      : (username?.trim().isNotEmpty == true ? username! : (email ?? 'User'));

  bool get isAdmin => role == 'ADMIN';

}
