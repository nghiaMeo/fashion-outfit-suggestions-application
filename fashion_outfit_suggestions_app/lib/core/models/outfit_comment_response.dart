class OutfitCommentResponse {
  final String id;
  final String outfitId;
  final String userId;
  final String username;
  final String? userAvatar;
  final String content;
  final String? parentId;
  final DateTime createdAt;
  final int likeCount;
  final bool isLiked;
  final List<OutfitCommentResponse> replies;

  OutfitCommentResponse({
    required this.id,
    required this.outfitId,
    required this.userId,
    required this.username,
    this.userAvatar,
    required this.content,
    this.parentId,
    required this.createdAt,
    required this.likeCount,
    required this.isLiked,
    required this.replies,
  });

  factory OutfitCommentResponse.fromJson(Map<String, dynamic> json) {
    return OutfitCommentResponse(
      id: json['id']?.toString() ?? '',
      outfitId: json['outfitId']?.toString() ?? '',
      userId: json['userId']?.toString() ?? '',
      username: json['username'] as String? ?? 'User',
      userAvatar: json['userAvatar'] as String?,
      content: json['content'] as String? ?? '',
      parentId: json['parentId']?.toString(),
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'] as String)
          : DateTime.now(),
      likeCount: (json['likeCount'] as num?)?.toInt() ?? 0,
      isLiked: json['isLiked'] == true,
      replies:
          (json['replies'] as List?)
              ?.map(
                (e) =>
                    OutfitCommentResponse.fromJson(e as Map<String, dynamic>),
              )
              .toList() ??
          [],
    );
  }

  String get timeAgo {
    final now = DateTime.now();
    final difference = now.difference(createdAt);
    if (difference.inSeconds < 60) {
      return 'just now';
    } else if (difference.inMinutes < 60) {
      return '${difference.inMinutes}m';
    } else if (difference.inHours < 24) {
      return '${difference.inHours}h';
    } else {
      return '${difference.inDays}d';
    }
  }

  OutfitCommentResponse copyWith({
    int? likeCount,
    bool? isLiked,
    List<OutfitCommentResponse>? replies,
  }) {
    return OutfitCommentResponse(
      id: id,
      outfitId: outfitId,
      userId: userId,
      username: username,
      userAvatar: userAvatar,
      content: content,
      parentId: parentId,
      createdAt: createdAt,
      likeCount: likeCount ?? this.likeCount,
      isLiked: isLiked ?? this.isLiked,
      replies: replies ?? this.replies,
    );
  }
}
