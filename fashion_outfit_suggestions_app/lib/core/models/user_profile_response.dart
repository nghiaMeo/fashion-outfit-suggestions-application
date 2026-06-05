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
  final String? friendshipStatus;

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
    this.friendshipStatus,
  });

  factory UserProfileResponse.fromJson(
    Map<String, dynamic> json,
  ) => UserProfileResponse(
    id: json["id"]?.toString() ?? '',
    username: json["username"] as String?,
    displayName: (json["display_name"] ?? json["displayName"]) as String?,
    avatarUrl: (json["avatar_url"] ?? json["avatarUrl"]) as String?,
    bio: json["bio"] as String?,
    outfitCount:
        ((json["outfit_count"] ?? json["outfitCount"]) as num?)?.toInt() ?? 0,
    friendCount:
        ((json["friend_count"] ?? json["friendCount"]) as num?)?.toInt() ?? 0,
    isPrivateProfile:
        (json["is_private_profile"] ??
            json["isPrivateProfile"] ??
            json["privateProfile"]) ==
        true,
    favoriteStyle: (json['favoriteStyles'] ?? json['favoriteStyle']) as String?,
    friendshipStatus:
        (json["friend_ship_status"] ?? json["friendshipStatus"]) as String?,
  );

  UserProfileResponse copyWith({
    String? id,
    String? username,
    String? displayName,
    String? avatarUrl,
    String? bio,
    int? outfitCount,
    int? friendCount,
    bool? isPrivateProfile,
    String? favoriteStyle,
    String? friendshipStatus,
  }) {
    return UserProfileResponse(
      id: id ?? this.id,
      username: username ?? this.username,
      displayName: displayName ?? this.displayName,
      avatarUrl: avatarUrl ?? this.avatarUrl,
      bio: bio ?? this.bio,
      outfitCount: outfitCount ?? this.outfitCount,
      friendCount: friendCount ?? this.friendCount,
      isPrivateProfile: isPrivateProfile ?? this.isPrivateProfile,
      favoriteStyle: favoriteStyle ?? this.favoriteStyle,
    );
  }
}
