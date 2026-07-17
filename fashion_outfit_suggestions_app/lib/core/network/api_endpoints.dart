class ApiEndpoints {
  static const String login = '/api/auth/login';
  static const String register = '/api/auth/register';
  static const String refreshToken = '/api/auth/refresh-token';
  static const String logout = '/api/auth/logout';
  static const String forgotPassword = '/api/auth/forgot-password';
  static const String resetPassword = '/api/auth/reset-password';
  static const String oauth2Google = '/api/auth/oauth2/google';

  static const String addOutfit = '/api/outfits/add';
  static const String homeFeed = '/api/outfits/home-feed';
  static const String allOutfits = '/api/outfits/all-outfit';
  
  static String outfitDetail(String id) => '/api/outfits/$id';
  static String toggleFavoriteOutfit(String id) => '/api/outfits/$id/favorite';
  static String toggleLikeOutfit(String id) => '/api/outfits/$id/like';

  static String comments(String outfitId) => '/api/outfits/$outfitId/comments';
  static String toggleLikeComment(String commentId) => '/api/outfits/comments/$commentId/like';

  static const String conversations = '/api/chat/conversations';
  static const String sendMessage = '/api/chat/send';
  static String messageHistory(String conversationId) => '/api/chat/conversations/$conversationId/messages';
  static String createConversation(String friendId) => '/api/chat/conversations/$friendId';

  static const String allItems = '/api/items/all-items';
  static const String addItem = '/api/items/add';
  static String deleteItem(String id) => '/api/items/delete-item/$id';
  static String updateItem(String id) => '/api/items/$id';

  static const String notifications = '/api/notifications';
  static const String unreadNotificationCount = '/api/notifications/unread-count';
  static const String markAllNotificationsRead = '/api/notifications/read-all';

  static const String myFriends = '/api/friendship/my-friends';
  static String acceptFriendRequest(String requesterId) => '/api/friendship/accept/by-requester/$requesterId';
  static String deleteFriend(String userId) => '/api/friendship/user/$userId';
  static String requestFriend(String userId) => '/api/friendship/request/$userId';

  static const String suggestCandidates = '/api/user/suggest-candidates';
  static const String myProfile = '/api/user/my-profile';
  static const String updateProfile = '/api/user/profile';
  static const String updateAvatar = '/api/user/profile/avatar';
  static String userProfile(String targetUserId) => '/api/user/profile/$targetUserId';
}
