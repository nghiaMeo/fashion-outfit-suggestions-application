import 'dart:io';
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

  final isAdding = false.obs;
  final selectedImage = Rxn<File>();

  final nameController = TextEditingController();
  final brandController = TextEditingController();
  final tagsController = TextEditingController();

  final selectedType = ''.obs;
  final selectedColor = ''.obs;
  final selectedSeason = ''.obs;
  final selectedOccasion = ''.obs;

  final types = [
    'Áo',
    'Quần',
    'Váy',
    'Giày',
    'Túi',
    'Phụ kiện',
    'Áo khoác',
    'Khác',
  ];
  final colors = [
    'Đen',
    'Trắng',
    'Xám',
    'Đỏ',
    'Xanh dương',
    'Xanh lá',
    'Vàng',
    'Hồng',
    'Nâu',
    'Be',
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
      selectedImage.value = File(picked.path);
    }
  }

  Future<void> addItem() async {
    if (nameController.text.trim().isEmpty ||
        selectedType.value.isEmpty ||
        selectedColor.value.isEmpty ||
        selectedImage.value == null) {
      Get.snackbar(
        'Thiếu thông tin',
        'Vui lòng điền tên, loại, màu sắc và chọn ảnh',
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
        'file': await MultipartFile.fromFile(
          selectedImage.value!.path,
          filename: 'item_image.jpg',
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
        'Thành công',
        'Đã thêm "${newItem.name}" vào tủ đồ',
        snackPosition: SnackPosition.TOP,
        backgroundColor: AppColors.primary,
        colorText: Colors.white,
        margin: const EdgeInsets.all(12),
        borderRadius: 10,
      );
    } catch (e) {
      Get.snackbar(
        'Lỗi',
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

  @override
  void onClose() {
    nameController.dispose();
    brandController.dispose();
    tagsController.dispose();
    super.onClose();
  }
}
