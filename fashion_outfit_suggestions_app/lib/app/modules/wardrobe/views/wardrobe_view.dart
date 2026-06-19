import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../../core/theme/app_fonts.dart';
import '../controllers/wardrobe_controller.dart';
import 'add_item_sheet_view.dart';

class WardrobeView extends GetView<WardrobeController> {
  const WardrobeView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black,
        elevation: 0,
        centerTitle: true,
        title: Text(
          'Wardrobe',
          style: AppFonts.base(
            color: Colors.white,
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.add_box_outlined, color: Colors.white, size: 26),
            onPressed: () => _showAddItemSheet(context),
          ),
        ],
      ),
      body: Obx(() {
        if (controller.isLoading.value) {
          return const Center(child: CircularProgressIndicator(color: Colors.white));
        }

        if (controller.items.isEmpty) {
          return _buildEmptyState();
        }

        return RefreshIndicator(
          onRefresh: controller.fetchItems,
          color: Colors.white,
          backgroundColor: const Color(0xFF262626),
          child: GridView.builder(
            padding: const EdgeInsets.all(2),
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 3,
              crossAxisSpacing: 2,
              mainAxisSpacing: 2,
            ),
            itemCount: controller.items.length,
            itemBuilder: (context, index) {
              final item = controller.items[index];
              return GestureDetector(
                onTap: () {
                  // TODO: Mở chi tiết item
                },
                child: Container(
                  color: const Color(0xFF1C1C1E),
                  child: item.imageUrl != null
                      ? Image.network(
                    item.imageUrl!,
                    fit: BoxFit.cover,
                    errorBuilder: (_, __, ___) => _buildItemPlaceholder(item.type),
                  )
                      : _buildItemPlaceholder(item.type),
                ),
              );
            },
          ),
        );
      }),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.dry_cleaning_outlined, size: 80, color: Color(0xFF3A3A3C)),
          const SizedBox(height: 16),
          Text(
            'Tủ đồ của bạn đang trống',
            style: AppFonts.base(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 8),
          Text(
            'Thêm quần áo đầu tiên vào tủ đồ',
            style: AppFonts.base(color: const Color(0xFF8E8E93), fontSize: 14),
          ),
          const SizedBox(height: 24),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF0095F6),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
              padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 12),
            ),
            onPressed: () => _showAddItemSheet(Get.context!),
            child: Text(
              'Thêm quần áo',
              style: AppFonts.base(color: Colors.white, fontSize: 14, fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildItemPlaceholder(String type) {
    return Container(
      color: const Color(0xFF1C1C1E),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.checkroom, color: Color(0xFF3A3A3C), size: 32),
          const SizedBox(height: 4),
          Text(type, style: AppFonts.base(color: const Color(0xFF8E8E93), fontSize: 10)),
        ],
      ),
    );
  }

  void _showAddItemSheet(BuildContext context) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => const AddItemSheet(),
    );
  }
}