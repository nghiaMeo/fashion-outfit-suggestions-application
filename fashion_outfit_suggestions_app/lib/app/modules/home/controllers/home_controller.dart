import 'package:get/get.dart';
import '../../../../core/models/outfit_response.dart';
import '../../../../core/network/dio_client.dart';

class HomeController extends GetxController {
  final DioClient _dioClient = Get.find<DioClient>();

  final currentIndex = 0.obs;

  final feedOutfits = <OutfitResponse>[].obs;
  final isFeedLoading = false.obs;

  @override
  void onInit() {
    super.onInit();
    fetchHomeFeed();
  }

  void changPage(int index) {
    currentIndex.value = index;
    if (index == 0) {
      fetchHomeFeed();
    }
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
}
