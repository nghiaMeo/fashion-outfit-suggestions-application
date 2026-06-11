import 'notification_type.dart';

class NotificationResponse {
  final String id;
  final String actorId;
  final String actorName;
  final String? actorAvatar;
  final NotificationType type;
  final String targetId;
  final String content;
  final bool isRead;
  final DateTime createdAt;

  NotificationResponse({
    required this.id,
    required this.actorId,
    required this.actorName,
    this.actorAvatar,
    required this.type,
    required this.targetId,
    required this.content,
    required this.isRead,
    required this.createdAt,
  });

  factory NotificationResponse.fromJson(Map<String, dynamic> json) {
    return NotificationResponse(
      id: json['id']?.toString() ?? '',
      actorId: json['actorId']?.toString() ?? '',
      actorName: json['actorName'] as String? ?? '',
      actorAvatar: json['actorAvatar'] as String?,
      type: NotificationType.values.firstWhere(
        (e) => e.toString().split('.').last == json['type'],
        orElse: () => NotificationType.system,
      ),
      targetId: json['targetId']?.toString() ?? '',
      content: json['content'] as String? ?? '',
      isRead: json['isRead'] == true,
      createdAt: _parseDateTime(json['createdAt']),
    );
  }

  static DateTime _parseDateTime(dynamic value) {
    if (value == null) return DateTime.now();
    if (value is int) {
      return DateTime.fromMillisecondsSinceEpoch(value);
    }
    if (value is double) {
      return DateTime.fromMillisecondsSinceEpoch((value * 1000).toInt());
    }
    return DateTime.tryParse(value.toString()) ?? DateTime.now();
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
    } else if (difference.inDays < 30) {
      return '${difference.inDays}d';
    } else {
      final months = [
        'Jan',
        'Feb',
        'Mar',
        'Apr',
        'May',
        'Jun',
        'Jul',
        'Aug',
        'Sep',
        'Oct',
        'Nov',
        'Dec',
      ];
      final month = months[createdAt.month - 1];
      return '$month ${createdAt.day.toString().padLeft(2, '0')}';
    }
  }
}
