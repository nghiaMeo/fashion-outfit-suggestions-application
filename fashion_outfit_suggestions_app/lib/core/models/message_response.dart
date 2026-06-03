class MessageResponse {
  final String? id;
  final String? senderId;
  final String? senderName;
  final String? content;
  final String? type;
  final String? imageUrl;
  final String? sharedOutfits;
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
  });

  factory MessageResponse.fromJson(Map<String, dynamic> json) =>
      MessageResponse(
        id: json['id'],
        senderId: json['sender_id'],
        senderName: json['sender_name'],
        content: json['content'],
        type: json['type'],
        imageUrl: json['image_url'],
        sharedOutfits: json['shared_outfits'],
        createdAt: json['created_at'],
        readAt: json['read_at'],
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
    );
  }
}
