class UserProfileResponse {
  final String id;
  final String? username;
  final String? displayName;
  final String? avatarUrl;
  final String? bio;
  final int outfitCount;
  final int friendCount;
  final bool isPrivateProfile;
  final String? favoriteStyle;
  final String? friendShipStatus;

  UserProfileResponse({
    required this.id,
    this.username,
    this.displayName,
    this.avatarUrl,
    this.bio,
    required this.outfitCount,
    required this.friendCount,
    required this.isPrivateProfile,
    this.favoriteStyle,
    this.friendShipStatus,
  });

  factory UserProfileResponse.fromJson(Map<String, dynamic> json) =>
      UserProfileResponse(
        id: json["id"] as String,
        username: json["username"] as String?,
        displayName: json["display_name"] as String?,
        avatarUrl: json["avatar_url"] as String?,
        bio: json["bio"] as String?,
        outfitCount: (json["outfit_count"] as num?)?.toInt() ?? 0,
        friendCount: (json["friend_count"] as num?)?.toInt() ?? 0,
        isPrivateProfile: json["is_private_profile"] as bool ?? false,
        favoriteStyle: json['favoriteStyles'] as String?,
        friendShipStatus: json["friend_ship_status"] as String?,
      );
}
