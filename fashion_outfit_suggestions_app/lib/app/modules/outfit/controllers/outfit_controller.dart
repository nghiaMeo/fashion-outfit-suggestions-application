import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../../core/models/item_response.dart';
import '../../../../core/models/outfit_response.dart';
import '../../../../core/network/dio_client.dart';
import '../../../../core/theme/app_colors.dart';

class OutfitController extends GetxController {
  final DioClient _dioClient = Get.find<DioClient>();

  final items = <ItemResponse>[].obs;
  final selectedItems = <ItemResponse>[].obs;
  final isLoading = false.obs;
  final isSaving = false.obs;

  final nameController = TextEditingController();
  final descriptionController = TextEditingController();
  final selectedOccasion = 'Casual'.obs;
  final isPublic = true.obs;

  final occasions = ['Casual', 'Formal', 'Sport', 'Party', 'Work', 'Beach'];

  final pageIndex = 0.obs;

  @override
  void onInit() {
    super.onInit();
    fetchMyItems();
  }

  Future<void> fetchMyItems() async {
    isLoading.value = true;
    try {
      final response = await _dioClient.getResult<List<ItemResponse>>(
        _dioClient.dio.get('/api/items/all-items'),
        (json) {
          final list = json as List;
          return list
              .map((e) => ItemResponse.fromJson(e as Map<String, dynamic>))
              .toList();
        },
      );
      items.assignAll(response);
    } catch (e) {
      Get.snackbar('Error', 'Unable to retrieve data cabinet');
    } finally {
      isLoading.value = false;
    }
  }

  void toggleSelectItem(ItemResponse item) {
    if (selectedItems.any((e) => e.id == item.id)) {
      selectedItems.removeWhere((e) => e.id == item.id);
    } else {
      selectedItems.add(item);
    }
  }

  bool isSelected(ItemResponse item) {
    return selectedItems.any((e) => e.id == item.id);
  }

  void goToNextStep() {
    if (selectedItems.isEmpty) {
      Get.snackbar(
        'Request',
        'Please select at least 1 item to match',
        backgroundColor: Colors.amber,
        colorText: Colors.black,
      );
      return;
    }
    pageIndex.value = 1;
  }

  void goBackStep() {
    pageIndex.value = 0;
  }

  Future<void> saveOutfit() async {
    if (nameController.text.trim().isEmpty) {
      Get.snackbar(
        'Error',
        'Please enter the name of the outfit',
        backgroundColor: Colors.amber,
        colorText: Colors.black,
      );
      return;
    }

    isSaving.value = true;
    try {
      final itemIds = selectedItems.map((e) => e.id).toList();
      final body = {
        'name': nameController.text.trim(),
        'occasion': selectedOccasion.value,
        'description': descriptionController.text.trim(),
        'itemIds': itemIds,
        'isPublic': isPublic.value,
      };

      await _dioClient.getResult<OutfitResponse>(
        _dioClient.dio.post('/api/outfits/add', data: body),
        (json) => OutfitResponse.fromJson(json as Map<String, dynamic>),
      );

      Get.back();
      Get.snackbar(
        'Success',
        'New outfit saved',
        backgroundColor: AppColors.primary,
        colorText: Colors.white,
      );
    } catch (e) {
      Get.snackbar(
        'Error',
        e.toString().replaceAll('Exception: ', ''),
        backgroundColor: AppColors.error,
        colorText: Colors.white,
      );
    } finally {
      isSaving.value = false;
    }
  }

  @override
  void onClose() {
    nameController.dispose();
    descriptionController.dispose();
    super.onClose();
  }
}
