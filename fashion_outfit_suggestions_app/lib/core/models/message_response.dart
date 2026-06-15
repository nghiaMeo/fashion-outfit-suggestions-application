class MessageResponse {
  final String? id;
  final String? senderId;
  final String? senderName;
  final String? content;
  final String? type;
  final String? imageUrl;
  final String? sharedOutfits;
  final String? conversationId;
  final String? createdAt;
  final String? readAt;

  MessageResponse({
    this.id,
    this.senderId,
    this.senderName,
    this.content,
    this.type,
    this.imageUrl,
    this.sharedOutfits,
    this.createdAt,
    this.readAt,
    this.conversationId,
  });

  factory MessageResponse.fromJson(Map<String, dynamic> json) =>
      MessageResponse(
        id: json['id']?.toString(),
        senderId: (json['senderId'] ?? json['sender_id'])?.toString(),
        senderName: (json['senderName'] ?? json['sender_name'])?.toString(),
        content: json['content']?.toString(),
        type: json['type']?.toString(),
        imageUrl: (json['imageUrl'] ?? json['image_url'])?.toString(),
        sharedOutfits:
            (json['sharedOutfitId'] ??
                    json['shared_outfit_id'] ??
                    json['shared_outfits'])
                ?.toString(),
        createdAt: (json['createdAt'] ?? json['created_at'])?.toString(),
        readAt: (json['readAt'] ?? json['read_at'])?.toString(),
        conversationId: (json['conversationId'] ?? json['conversation_id'])
            ?.toString(),
      );

  MessageResponse copyWith({
    String? id,
    String? senderId,
    String? senderName,
    String? content,
    String? type,
    String? imageUrl,
    String? sharedOutfits,
    String? createdAt,
    String? readAt,
    String? conversationId,
  }) {
    return MessageResponse(
      id: id ?? this.id,
      senderId: senderId ?? this.senderId,
      senderName: senderName ?? this.senderName,
      content: content ?? this.content,
      type: type ?? this.type,
      imageUrl: imageUrl ?? this.imageUrl,
      sharedOutfits: sharedOutfits ?? this.sharedOutfits,
      createdAt: createdAt ?? this.createdAt,
      readAt: readAt ?? this.readAt,
      conversationId: conversationId ?? this.conversationId,
    );
  }
}
