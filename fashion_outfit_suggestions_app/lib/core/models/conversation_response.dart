class ConversationResponse {
  final String? conversationId;
  final String? friendId;
  final String? friendName;
  final String? friendAvatar;
  final String? lastMessage;
  final String? lastMessageAt;
  final int? unreadCount;

  ConversationResponse({
    required this.conversationId,
    this.friendId,
    this.friendName,
    this.friendAvatar,
    this.lastMessage,
    this.lastMessageAt,
    this.unreadCount,
  });

  factory ConversationResponse.fromJson(Map<String, dynamic> json) =>
      ConversationResponse(
        conversationId: (json['conversation_id'] ?? json['conversationId'])
            ?.toString(),
        friendId: (json['friend_id'] ?? json['friendId'])?.toString(),
        friendName: (json['friend_name'] ?? json['friendName'])?.toString(),
        friendAvatar: (json['friend_avatar'] ?? json['friendAvatar'])
            ?.toString(),
        lastMessage: (json['last_message'] ?? json['lastMessage'])?.toString(),
        lastMessageAt: (json['last_message_at'] ?? json['lastMessageAt'])
            ?.toString(),
        unreadCount: ((json['unread_count'] ?? json['unreadCount']) as num?)
            ?.toInt(),
      );

  ConversationResponse copyWith({
    String? conversationId,
    String? friendId,
    String? friendName,
    String? friendAvatar,
    String? lastMessage,
    String? lastMessageAt,
    int? unreadCount,
  }) {
    return ConversationResponse(
      conversationId: conversationId ?? this.conversationId,
      friendId: friendId ?? this.friendId,
      friendName: friendName ?? this.friendName,
      friendAvatar: friendAvatar ?? this.friendAvatar,
      lastMessage: lastMessage ?? this.lastMessage,
      lastMessageAt: lastMessageAt ?? this.lastMessageAt,
      unreadCount: unreadCount ?? this.unreadCount,
    );
  }
}
