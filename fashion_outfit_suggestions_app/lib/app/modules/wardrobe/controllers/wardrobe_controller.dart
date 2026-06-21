import 'dart:io';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart' hide FormData, MultipartFile;
import 'package:image_picker/image_picker.dart';
import '../../../../core/models/item_response.dart';
import '../../../../core/network/dio_client.dart';
import '../../../../core/theme/app_colors.dart';

class WardrobeController extends GetxController {
  final DioClient _dioClient = Get.find<DioClient>();

  final items = <ItemResponse>[].obs;
  final isLoading = false.obs;

  final selectedImage = Rxn<XFile>();
  final isAdding = false.obs;

  final nameController = TextEditingController();
  final brandController = TextEditingController();
  final tagsController = TextEditingController();

  final selectedType = ''.obs;
  final selectedColor = ''.obs;
  final selectedSeason = ''.obs;
  final selectedOccasion = ''.obs;

  final types = [
    'Shirt',
    'Pants',
    'Skirt',
    'Shoes',
    'Bag',
    'Accessories',
    'Jacket',
    'Other',
  ];
  final colors = [
    'Black',
    'White',
    'Gray',
    'Red',
    'Blue',
    'Green',
    'Yellow',
    'Pink',
    'Brown',
    'Beige',
  ];
  final seasons = ['Spring', 'Summer', 'Autumn', 'Winter', 'All Season'];
  final occasions = ['Casual', 'Formal', 'Sport', 'Party', 'Work', 'Beach'];

  @override
  void onInit() {
    super.onInit();
    fetchItems();
  }

  Future<void> fetchItems() async {
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
      // Silently ignore - show empty state instead
    } finally {
      isLoading.value = false;
    }
  }

  Future<void> pickImage() async {
    final picker = ImagePicker();
    final picked = await picker.pickImage(
      source: ImageSource.gallery,
      imageQuality: 80,
    );
    if (picked != null) {
      selectedImage.value = picked;
    }
  }

  Future<void> addItem() async {
    if (nameController.text.trim().isEmpty ||
        selectedType.value.isEmpty ||
        selectedColor.value.isEmpty ||
        selectedImage.value == null) {
      Get.snackbar(
        'Missing information',
        'Please fill in name, type, color and select image',
        snackPosition: SnackPosition.TOP,
        backgroundColor: const Color(0xFF262626),
        colorText: Colors.white,
        margin: const EdgeInsets.all(12),
        borderRadius: 10,
      );
      return;
    }

    isAdding.value = true;
    try {
      final dataJson =
          '{'
          '"name":"${nameController.text.trim()}",'
          '"type":"${selectedType.value}",'
          '"color":"${selectedColor.value}",'
          '"season":"${selectedSeason.value}",'
          '"brand":"${brandController.text.trim()}",'
          '"occasion":"${selectedOccasion.value}",'
          '"tags":"${tagsController.text.trim()}"'
          '}';

      final formData = FormData.fromMap({
        'data': MultipartFile.fromString(
          dataJson,
          contentType: DioMediaType('application', 'json'),
        ),
        'file': kIsWeb
            ? MultipartFile.fromBytes(
                await selectedImage.value!.readAsBytes(),
                filename: selectedImage.value!.name,
              )
            : MultipartFile.fromFile(
                selectedImage.value!.path,
                filename: selectedImage.value!.name,
              ),
      });

      final newItem = await _dioClient.getResult<ItemResponse>(
        _dioClient.dio.post<Map<String, dynamic>>(
          '/api/items/add',
          data: formData,
        ),
        (json) => ItemResponse.fromJson(json! as Map<String, dynamic>),
      );

      items.insert(0, newItem);
      _resetForm();
      Get.back();

      Get.snackbar(
        'Success',
        'Added "${newItem.name}" to the wardrobe',
        snackPosition: SnackPosition.TOP,
        backgroundColor: AppColors.primary,
        colorText: Colors.white,
        margin: const EdgeInsets.all(12),
        borderRadius: 10,
      );
    } catch (e) {
      Get.snackbar(
        'Error',
        e.toString().replaceAll('Exception: ', ''),
        snackPosition: SnackPosition.TOP,
        backgroundColor: AppColors.error,
        colorText: Colors.white,
        margin: const EdgeInsets.all(12),
        borderRadius: 10,
      );
    } finally {
      isAdding.value = false;
    }
  }

  void _resetForm() {
    nameController.clear();
    brandController.clear();
    tagsController.clear();
    selectedImage.value = null;
    selectedType.value = '';
    selectedColor.value = '';
    selectedSeason.value = '';
    selectedOccasion.value = '';
  }

  Future<void> deleteItem(String id) async {
    isLoading.value = true;
    try {
      await _dioClient.getResult<String>(
        _dioClient.dio.delete('/api/items/delete-item/$id'),
        (json) => json as String,
      );
      items.removeWhere((item) => item.id == id);
      Get.back();
      Get.snackbar(
        'Success',
        'Item removed from wardrobe',
        backgroundColor: const Color(0xFF262626),
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
      isLoading.value = false;
    }
  }

  void prepareEditForm(ItemResponse item) {
    nameController.text = item.name;
    brandController.text = item.brand ?? '';
    tagsController.text = item.tags ?? '';
    selectedType.value = item.type;
    selectedColor.value = item.color;
    selectedSeason.value = item.season ?? '';
    selectedOccasion.value = item.occasion ?? '';
  }

  Future<void> updateItem(String id, String? existingImageUrl) async {
    if (nameController.text.trim().isEmpty ||
        selectedType.value.isEmpty ||
        selectedColor.value.isEmpty) {
      Get.snackbar(
        'Missing information',
        'Please fill in the name, item type, and color',
        backgroundColor: const Color(0xFF262626),
        colorText: Colors.white,
      );
      return;
    }

    isAdding.value = true;
    try {
      final updatedItem = await _dioClient.getResult<ItemResponse>(
        _dioClient.dio.put(
          '/api/items/$id',
          data: {
            "name": nameController.text.trim(),
            "type": selectedType.value,
            "color": selectedColor.value,
            "season": selectedSeason.value,
            "brand": brandController.text.trim(),
            "occasion": selectedOccasion.value,
            "imageUrl": existingImageUrl,
            "tags": tagsController.text.trim(),
          },
        ),
        (json) => ItemResponse.fromJson(json! as Map<String, dynamic>),
      );

      final index = items.indexWhere((item) => item.id == id);
      if (index != -1) {
        items[index] = updatedItem;
      }
      _resetForm();
      Get.back();
      Get.back();
      Get.snackbar(
        'Success',
        'Item information updated',
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
      isAdding.value = false;
    }
  }

  @override
  void onClose() {
    nameController.dispose();
    brandController.dispose();
    tagsController.dispose();
    super.onClose();
  }
}
