import 'package:fashion_outfit_suggestions_app/core/network/socket_service.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../../core/models/outfit_comment_response.dart';
import '../../../../core/models/outfit_response.dart';
import '../../../../core/network/dio_client.dart';
import '../views/comments_sheet.dart';

class HomeController extends GetxController {
  final DioClient _dioClient = Get.find<DioClient>();
  final unreadNotificationsCount = 0.obs;
  final _socketService = Get.find<SocketService>();

  final currentIndex = 0.obs;

  final feedOutfits = <OutfitResponse>[].obs;
  final isFeedLoading = false.obs;
  final comments = <OutfitCommentResponse>[].obs;
  final isCommentsLoading = false.obs;
  final isPostingComment = false.obs;
  final commentController = TextEditingController();

  @override
  void onInit() {
    super.onInit();
    fetchHomeFeed();
    fetchUnreadNotificationCount();
    _socketService.addNotificationListener(_onNewNotificationReceived);
  }

  void _onNewNotificationReceived(Map<String, dynamic> data) {
    if (data['type'] != 'NEW_MESSAGE') {
      unreadNotificationsCount.value++;
    }
  }

  void changPage(int index) {
    currentIndex.value = index;
    if (index == 0) {
      fetchHomeFeed();
    }
  }

  Future<void> fetchComments(String outfitId) async {
    isCommentsLoading.value = true;
    try {
      final result = await _dioClient.getResult<List<OutfitCommentResponse>>(
        _dioClient.dio.get('/api/outfits/$outfitId/comments'),
        (json) {
          final list = json as List;
          return list
              .map(
                (e) =>
                    OutfitCommentResponse.fromJson(e as Map<String, dynamic>),
              )
              .toList();
        },
      );
      comments.assignAll(result);
    } catch (e) {
      debugPrint('fetchComments error: $e');
    } finally {
      isCommentsLoading.value = false;
    }
  }

  Future<void> postComment(String outfitId) async {
    final text = commentController.text.trim();
    if (text.isEmpty) return;

    isPostingComment.value = true;
    try {
      final newComment = await _dioClient.getResult<OutfitCommentResponse>(
        _dioClient.dio.post(
          '/api/outfits/$outfitId/comments',
          data: {'content': text},
        ),
            (json) => OutfitCommentResponse.fromJson(json as Map<String, dynamic>),
      );

      comments.insert(0, newComment);
      commentController.clear();

      final index = feedOutfits.indexWhere((o) => o.id == outfitId);
      if (index != -1) {
        final old = feedOutfits[index];
        feedOutfits[index] = OutfitResponse(
          id: old.id,
          name: old.name,
          occasion: old.occasion,
          description: old.description,
          isFavorite: old.isFavorite,
          isAiGenerated: old.isAiGenerated,
          isPublic: old.isPublic,
          shareLink: old.shareLink,
          items: old.items,
          likeCount: old.likeCount,
          isLiked: old.isLiked,
          ownerName: old.ownerName,
          ownerAvatar: old.ownerAvatar,
          createdAt: old.createdAt,
          commentCount: old.commentCount + 1,
        );
      }
    } catch (e) {
      Get.snackbar('Error', 'Cannot push comment: $e');
    } finally {
      isPostingComment.value = false;
    }
  }

  void showCommentsSheet(OutfitResponse outfit) {
    comments.clear();
    fetchComments(outfit.id);

    Get.bottomSheet(
      CommentsSheet(outfit: outfit),
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      ignoreSafeArea: false,
    );
  }

  Future<void> fetchUnreadNotificationCount() async {
    try {
      final count = await _dioClient.getResult<int>(
        _dioClient.dio.get('/api/notifications/unread-count'),
        (json) => json as int,
      );
      unreadNotificationsCount.value = count;
    } catch (_) {}
  }

  Future<void> fetchHomeFeed() async {
    isFeedLoading.value = true;
    try {
      final response = await _dioClient.getResult<List<OutfitResponse>>(
        _dioClient.dio.get('/api/outfits/home-feed'),
        (json) {
          final list = json as List;
          return list
              .map((e) => OutfitResponse.fromJson(e as Map<String, dynamic>))
              .toList();
        },
      );
      feedOutfits.assignAll(response);
    } catch (e) {
      //
    } finally {
      isFeedLoading.value = false;
    }
  }

  Future<void> toggleLikeOutfit(String outfitId) async {
    try {
      final updated = await _dioClient.getResult<OutfitResponse>(
        _dioClient.dio.post('/api/outfits/$outfitId/like'),
        (json) => OutfitResponse.fromJson(json! as Map<String, dynamic>),
      );

      final index = feedOutfits.indexWhere((e) => e.id == outfitId);
      if (index != -1) {
        feedOutfits[index] = updated;
      }
    } catch (e) {
      Get.snackbar('Error', 'Unable to drop heart: ${e.toString()}');
    }
  }

  Future<void> toggleFavoriteOutfit(String outfitId) async {
    try {
      final updated = await _dioClient.getResult<OutfitResponse>(
        _dioClient.dio.patch('/api/outfits/$outfitId/favorite'),
        (json) => OutfitResponse.fromJson(json! as Map<String, dynamic>),
      );

      final index = feedOutfits.indexWhere((e) => e.id == outfitId);
      if (index != -1) {
        feedOutfits[index] = updated;
      }
    } catch (e) {
      Get.snackbar('Error', 'Unable to save post: ${e.toString()}');
    }
  }

  @override
  void onClose() {
    _socketService.removeNotificationListener(_onNewNotificationReceived);
    super.onClose();
  }
}
