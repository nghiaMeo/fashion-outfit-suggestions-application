import 'item_response.dart';

class OutfitResponse {
  final String id;
  final String name;
  final String? occasion;
  final String? description;
  final bool isFavorite;
  final bool isAiGenerated;
  final bool isPublic;
  final String? shareLink;
  final List<ItemResponse> items;
  final int likeCount;
  final bool isLiked;
  final String? ownerName;
  final String? ownerAvatar;
  final int commentCount;
  final DateTime createdAt;

  OutfitResponse({
    required this.id,
    required this.name,
    this.occasion,
    this.description,
    required this.isFavorite,
    required this.isAiGenerated,
    required this.isPublic,
    this.shareLink,
    required this.items,
    required this.likeCount,
    required this.isLiked,
    this.ownerName,
    this.ownerAvatar,
    required this.createdAt,
    required this.commentCount,
  });

  factory OutfitResponse.fromJson(Map<String, dynamic> json) {
    return OutfitResponse(
      id: json['id']?.toString() ?? '',
      name: json['name'] as String? ?? '',
      occasion: json['occasion'] as String?,
      description: json['description'] as String?,
      isFavorite:
          (json['favorite'] ?? json['isFavorite'] ?? json['is_favorite']) ==
          true,
      isAiGenerated:
          (json['aiGenerated'] ??
              json['isAiGenerated'] ??
              json['is_ai_generated']) ==
          true,
      isPublic:
          (json['public'] ?? json['isPublic'] ?? json['is_public']) == true,
      shareLink: json['shareLink'] as String?,
      items:
          (json['items'] as List?)
              ?.map((e) => ItemResponse.fromJson(e as Map<String, dynamic>))
              .toList() ??
          [],
      likeCount: (json['likeCount'] as num?)?.toInt() ?? 0,
      isLiked: (json['liked'] ?? json['isLiked'] ?? json['is_liked']) == true,
      ownerName: json['ownerName'] as String?,
      ownerAvatar: json['ownerAvatar'] as String?,
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'] as String)
          : DateTime.now(),
      commentCount: (json['commentCount'] as num?)?.toInt() ?? 0,
    );
  }
}
