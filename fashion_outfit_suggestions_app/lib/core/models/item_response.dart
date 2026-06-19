class ItemResponse {
  final String id;
  final String name;
  final String type;
  final String color;
  final String? season;
  final String? brand;
  final String? occasion;
  final String? imageUrl;
  final String? tags;

  ItemResponse({
    required this.id,
    required this.name,
    required this.type,
    required this.color,
    this.season,
    this.brand,
    this.occasion,
    this.imageUrl,
    this.tags,
  });

  factory ItemResponse.fromJson(Map<String, dynamic> json) {
    return ItemResponse(
      id: json['id']?.toString() ?? '',
      name: json['name'] as String? ?? '',
      type: json['type'] as String? ?? '',
      color: json['color'] as String? ?? '',
      season: json['season'] as String?,
      brand: json['brand'] as String?,
      occasion: json['occasion'] as String?,
      imageUrl: json['imageUrl'] as String?,
      tags: json['tags'] as String?,
    );
  }
}
