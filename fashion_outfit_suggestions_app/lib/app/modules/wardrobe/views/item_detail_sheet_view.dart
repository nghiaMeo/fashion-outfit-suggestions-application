import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../../core/models/item_response.dart';
import '../../../../core/theme/app_fonts.dart';
import '../controllers/wardrobe_controller.dart';
import 'edit_item_sheet_view.dart';

class ItemDetailSheet extends GetView<WardrobeController> {
  final ItemResponse item;

  const ItemDetailSheet({super.key, required this.item});

  @override
  Widget build(BuildContext context) {
    return Container(
      height: MediaQuery.of(context).size.height * 0.85,
      decoration: const BoxDecoration(
        color: Color(0xFF1C1C1E),
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      child: Column(
        children: [
          _buildHeader(context),
          Expanded(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  AspectRatio(
                    aspectRatio: 1,
                    child: ClipRRect(
                      borderRadius: BorderRadius.circular(12),
                      child: Container(
                        color: const Color(0xFF262626),
                        child: item.imageUrl != null
                            ? Image.network(item.imageUrl!, fit: BoxFit.cover)
                            : const Icon(
                                Icons.checkroom,
                                size: 80,
                                color: Colors.grey,
                              ),
                      ),
                    ),
                  ),
                  const SizedBox(height: 20),
                  Text(
                    item.name,
                    style: AppFonts.base(
                      color: Colors.white,
                      fontSize: 22,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 16),
                  _buildDetailRow('Item type', item.type),
                  _buildDetailRow('Color', item.color),
                  if (item.brand != null && item.brand!.isNotEmpty)
                    _buildDetailRow('Brand', item.brand!),
                  if (item.season != null && item.season!.isNotEmpty)
                    _buildDetailRow('Season', item.season!),
                  if (item.occasion != null && item.occasion!.isNotEmpty)
                    _buildDetailRow('Occasion', item.occasion!),
                  if (item.tags != null && item.tags!.isNotEmpty)
                    _buildDetailRow('Tags', item.tags!),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildHeader(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
      decoration: const BoxDecoration(
        border: Border(bottom: BorderSide(color: Color(0xFF262626))),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          IconButton(
            icon: const Icon(Icons.close, color: Colors.white),
            onPressed: () => Get.back(),
          ),
          Text(
            'Item details',
            style: AppFonts.base(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          IconButton(
            icon: const Icon(Icons.more_horiz, color: Colors.white),
            onPressed: () => _showOptions(context),
          ),
        ],
      ),
    );
  }

  Widget _buildDetailRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 100,
            child: Text(
              label,
              style: AppFonts.base(
                color: const Color(0xFF8E8E93),
                fontSize: 14,
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: AppFonts.base(color: Colors.white, fontSize: 14),
            ),
          ),
        ],
      ),
    );
  }

  void _showOptions(BuildContext context) {
    showModalBottomSheet(
      context: context,
      backgroundColor: const Color(0xFF262626),
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (_) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: const Icon(Icons.edit, color: Colors.white),
              title: Text('Edit', style: AppFonts.base(color: Colors.white)),
              onTap: () {
                Get.back();
                controller.prepareEditForm(item);
                showModalBottomSheet(
                  context: context,
                  isScrollControlled: true,
                  builder: (_) => EditItemSheet(item: item),
                );
              },
            ),
            ListTile(
              leading: const Icon(Icons.delete, color: Colors.red),
              title: Text('Delete', style: AppFonts.base(color: Colors.red)),
              onTap: () {
                Get.back();
                _confirmDelete(context);
              },
            ),
          ],
        ),
      ),
    );
  }

  void _confirmDelete(BuildContext context) {
    showDialog(
      context: context,
      builder: (_) => AlertDialog(
        backgroundColor: const Color(0xFF262626),
        title: Text(
          'Delete item',
          style: AppFonts.base(
            color: Colors.white,
            fontWeight: FontWeight.bold,
          ),
        ),
        content: Text(
          'Are you sure you want to remove this item from your wardrobe?',
          style: AppFonts.base(color: Colors.white70),
        ),
        actions: [
          TextButton(
            onPressed: () => Get.back(),
            child: Text('Cancel', style: AppFonts.base(color: Colors.white)),
          ),
          TextButton(
            onPressed: () {
              Get.back();
              controller.deleteItem(item.id);
            },
            child: Text('Delete', style: AppFonts.base(color: Colors.red)),
          ),
        ],
      ),
    );
  }
}
