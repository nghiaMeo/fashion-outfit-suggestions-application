class FriendResponse {
  final String id;
  final String friendId;
  final String fullName;
  final String username;
  final String? avatarUrl;
  final String? status;

  FriendResponse({
    required this.id,
    required this.friendId,
    required this.fullName,
    required this.username,
    this.avatarUrl,
    this.status,
  });

  factory FriendResponse.fromJson(Map<String, dynamic> json) {
    return FriendResponse(
      id: json['id']?.toString() ?? '',
      friendId: json['friendId']?.toString() ?? '',
      fullName: json['fullName'] as String? ?? '',
      username: json['username'] as String? ?? '',
      avatarUrl: json['avatarUrl'] as String?,
      status: json['status'] as String?,
    );
  }

  FriendResponse copyWith({
    String? id,
    String? friendId,
    String? fullName,
    String? username,
    String? avatarUrl,
    String? status,
  }) {
    return FriendResponse(
      id: id ?? this.id,
      friendId: friendId ?? this.friendId,
      fullName: fullName ?? this.fullName,
      username: username ?? this.username,
      avatarUrl: avatarUrl ?? this.avatarUrl,
      status: status ?? this.status,
    );
  }
}
