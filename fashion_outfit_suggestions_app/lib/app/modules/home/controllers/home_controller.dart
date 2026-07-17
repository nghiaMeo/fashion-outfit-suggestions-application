import 'package:fashion_outfit_suggestions_app/core/network/api_endpoints.dart';
import 'package:fashion_outfit_suggestions_app/core/network/socket_service.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../../core/models/conversation_response.dart';
import '../../../../core/models/outfit_comment_response.dart';
import '../../../../core/models/outfit_response.dart';
import '../../../../core/network/dio_client.dart';
import '../views/comments_sheet.dart';
import '../views/share_to_message_sheet.dart';

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

  final conversations = <ConversationResponse>[].obs;
  final isConversationsLoading = false.obs;

  final replyToCommentId = RxnString();
  final replyToUsername = RxnString();
  final commentFocusNode = FocusNode();

  final sentConversationIds = <String>{}.obs;

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

  Future<void> shareOutfitToConversation(
    String conversationId,
    String outfitId,
  ) async {
    try {
      await _dioClient.dio.post(
        ApiEndpoints.sendMessage,
        data: {
          'conversationId': conversationId,
          'content': 'Shared an outfit',
          'type': 'OUTFIT_SHARE',
          'sharedOutfitId': outfitId,
        },
      );
      sentConversationIds.add(conversationId);
    } catch (e) {
      Get.snackbar('Error', 'Cannot share: $e');
    }
  }

  Future<void> fetchConversations() async {
    isConversationsLoading.value = true;
    try {
      final result = await _dioClient.getResult<List<ConversationResponse>>(
        _dioClient.dio.get(ApiEndpoints.conversations),
        (json) {
          final list = json as List;
          return list
              .map(
                (e) => ConversationResponse.fromJson(e as Map<String, dynamic>),
              )
              .toList();
        },
      );
      conversations.assignAll(result);
    } catch (e) {
      debugPrint('fetchConversations error: $e');
    } finally {
      isConversationsLoading.value = false;
    }
  }

  Future<void> fetchComments(String outfitId) async {
    isCommentsLoading.value = true;
    try {
      final result = await _dioClient.getResult<List<OutfitCommentResponse>>(
        _dioClient.dio.get(ApiEndpoints.comments(outfitId)),
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
      final parentId = replyToCommentId.value;

      final newComment = await _dioClient.getResult<OutfitCommentResponse>(
        _dioClient.dio.post(
          ApiEndpoints.comments(outfitId),
          data: {'content': text, 'parentId': parentId},
        ),
        (json) => OutfitCommentResponse.fromJson(json as Map<String, dynamic>),
      );

      if (parentId == null) {
        comments.insert(0, newComment);
      } else {
        final parentIndex = comments.indexWhere((c) => c.id == parentId);
        if (parentIndex != -1) {
          final parent = comments[parentIndex];
          final updatedReplies = List<OutfitCommentResponse>.from(
            parent.replies,
          )..add(newComment);
          comments[parentIndex] = parent.copyWith(replies: updatedReplies);
        }
      }

      commentController.clear();
      cancelReply();

      final index = feedOutfits.indexWhere((o) => o.id == outfitId);
      if (index != -1) {
        final old = feedOutfits[index];
        feedOutfits[index] = old.copyWith(commentCount: old.commentCount + 1);
      }
    } catch (e) {
      Get.snackbar('Lỗi', 'Không thể đăng bình luận: $e');
    } finally {
      isPostingComment.value = false;
    }
  }

  void enterReplyMode(OutfitCommentResponse comment) {
    replyToCommentId.value = comment.id;
    replyToUsername.value = comment.username;
    commentFocusNode.requestFocus();
  }

  void cancelReply() {
    replyToCommentId.value = null;
    replyToUsername.value = null;
  }

  Future<void> toggleLikeComment(String commentId) async {
    try {
      await _dioClient.dio.post(ApiEndpoints.toggleLikeComment(commentId));

      for (int i = 0; i < comments.length; i++) {
        if (comments[i].id == commentId) {
          final c = comments[i];
          comments[i] = c.copyWith(
            isLiked: !c.isLiked,
            likeCount: c.isLiked ? c.likeCount - 1 : c.likeCount + 1,
          );
          break;
        }
        final replyIndex = comments[i].replies.indexWhere(
          (r) => r.id == commentId,
        );
        if (replyIndex != -1) {
          final parent = comments[i];
          final reply = parent.replies[replyIndex];
          final updatedReply = reply.copyWith(
            isLiked: !reply.isLiked,
            likeCount: reply.isLiked
                ? reply.likeCount - 1
                : reply.likeCount + 1,
          );
          final updatedReplies = List<OutfitCommentResponse>.from(
            parent.replies,
          )..[replyIndex] = updatedReply;
          comments[i] = parent.copyWith(replies: updatedReplies);
          break;
        }
      }
    } catch (e) {
      debugPrint('toggleLikeComment error: $e');
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
        _dioClient.dio.get(ApiEndpoints.unreadNotificationCount),
        (json) => json as int,
      );
      unreadNotificationsCount.value = count;
    } catch (_) {}
  }

  Future<void> fetchHomeFeed() async {
    isFeedLoading.value = true;
    try {
      final response = await _dioClient.getResult<List<OutfitResponse>>(
        _dioClient.dio.get(ApiEndpoints.homeFeed),
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
        _dioClient.dio.post(ApiEndpoints.toggleLikeOutfit(outfitId)),
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
        _dioClient.dio.patch(ApiEndpoints.toggleFavoriteOutfit(outfitId)),
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

  void showShareToMessageSheet(OutfitResponse outfit) {
    sentConversationIds.clear();
    conversations.clear();
    fetchConversations();

    Get.bottomSheet(
      ShareToMessageSheet(outfit: outfit),
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
    );
  }

  @override
  void onClose() {
    _socketService.removeNotificationListener(_onNewNotificationReceived);
    commentFocusNode.dispose();
    super.onClose();
  }
}
