class OutfitCommentResponse {
  final String id;
  final String outfitId;
  final String userId;
  final String username;
  final String? userAvatar;
  final String content;
  final DateTime createdAt;

  OutfitCommentResponse({
    required this.id,
    required this.outfitId,
    required this.userId,
    required this.username,
    this.userAvatar,
    required this.content,
    required this.createdAt,
  });

  factory OutfitCommentResponse.fromJson(Map<String, dynamic> json) {
    return OutfitCommentResponse(
      id: json['id']?.toString() ?? '',
      outfitId: json['outfitId']?.toString() ?? '',
      userId: json['userId']?.toString() ?? '',
      username: json['username'] as String? ?? 'User',
      userAvatar: json['userAvatar'] as String?,
      content: json['content'] as String? ?? '',
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'] as String)
          : DateTime.now(),
    );
  }

  String get timeAge {
    final now = DateTime.now();
    final difference = now.difference(createdAt);
    if (difference.inSeconds < 60) {
      return 'just now';
    } else if (difference.inMinutes < 60) {
      return "${difference.inMinutes}m";
    } else if (difference.inHours < 24) {
      return "${difference.inHours}h";
    } else {
      return "${difference.inDays}d";
    }
  }
}
