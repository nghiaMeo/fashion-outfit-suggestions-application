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
        conversationId: json['conversation_id'],
        friendId: json['friend_id'],
        friendName: json['friend_name'],
        friendAvatar: json['friend_avatar'],
        lastMessage: json['last_message'],
        lastMessageAt: json['last_message_at'],
        unreadCount: json['unread_count'],
      );
}
