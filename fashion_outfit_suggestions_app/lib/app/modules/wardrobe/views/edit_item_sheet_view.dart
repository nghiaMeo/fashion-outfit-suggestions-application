import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../../core/models/item_response.dart';
import '../../../../core/theme/app_fonts.dart';
import '../../../../core/theme/app_colors.dart';
import '../controllers/wardrobe_controller.dart';

class EditItemSheet extends GetView<WardrobeController> {
  final ItemResponse item;

  const EditItemSheet({super.key, required this.item});

  @override
  Widget build(BuildContext context) {
    final bottomPadding = MediaQuery.of(context).viewInsets.bottom;

    return Container(
      height: MediaQuery.of(context).size.height * 0.92,
      decoration: const BoxDecoration(
        color: Color(0xFF1C1C1E),
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      child: Column(
        children: [
          _buildHeader(context),
          Expanded(
            child: SingleChildScrollView(
              padding: EdgeInsets.fromLTRB(16, 0, 16, bottomPadding + 24),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const SizedBox(height: 16),
                  if (item.imageUrl != null)
                    Center(
                      child: ClipRRect(
                        borderRadius: BorderRadius.circular(12),
                        child: SizedBox(
                          height: 160,
                          width: 160,
                          child: Image.network(
                            item.imageUrl!,
                            fit: BoxFit.cover,
                          ),
                        ),
                      ),
                    ),
                  const SizedBox(height: 24),
                  _buildLabel('Item Name', isRequired: true),
                  TextField(
                    controller: controller.nameController,
                    style: AppFonts.base(color: Colors.white),
                    decoration: _getInputDecoration('Example: Black T-shirt'),
                  ),
                  const SizedBox(height: 16),
                  _buildLabel('Item Type', isRequired: true),
                  Obx(
                    () => _buildDropdown(
                      value: controller.selectedType.value.isEmpty
                          ? null
                          : controller.selectedType.value,
                      items: controller.types,
                      hint: 'Choose the type of item',
                      onChanged: (val) =>
                          controller.selectedType.value = val ?? '',
                    ),
                  ),
                  const SizedBox(height: 16),
                  _buildLabel('Color', isRequired: true),
                  Obx(
                    () => _buildDropdown(
                      value: controller.selectedColor.value.isEmpty
                          ? null
                          : controller.selectedColor.value,
                      items: controller.colors,
                      hint: 'Choose a color',
                      onChanged: (val) =>
                          controller.selectedColor.value = val ?? '',
                    ),
                  ),
                  const SizedBox(height: 16),
                  _buildLabel('Season'),
                  Obx(
                    () => _buildDropdown(
                      value: controller.selectedSeason.value.isEmpty
                          ? null
                          : controller.selectedSeason.value,
                      items: controller.seasons,
                      hint: 'Choose the appropriate season',
                      onChanged: (val) =>
                          controller.selectedSeason.value = val ?? '',
                    ),
                  ),
                  const SizedBox(height: 16),
                  _buildLabel('Suitable occasion'),
                  Obx(
                    () => _buildDropdown(
                      value: controller.selectedOccasion.value.isEmpty
                          ? null
                          : controller.selectedOccasion.value,
                      items: controller.occasions,
                      hint: 'Choose the occasion',
                      onChanged: (val) =>
                          controller.selectedOccasion.value = val ?? '',
                    ),
                  ),
                  const SizedBox(height: 16),
                  _buildLabel('Brand'),
                  TextField(
                    controller: controller.brandController,
                    style: AppFonts.base(color: Colors.white),
                    decoration: _getInputDecoration('Example: Zara, Nike...'),
                  ),
                  const SizedBox(height: 16),
                  _buildLabel('Tags'),
                  TextField(
                    controller: controller.tagsController,
                    style: AppFonts.base(color: Colors.white),
                    decoration: _getInputDecoration(
                      'Example: casual, active (separated by commas)',
                    ),
                  ),
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
          TextButton(
            onPressed: () => Get.back(),
            child: Text(
              'Cancel',
              style: AppFonts.base(color: Colors.white, fontSize: 16),
            ),
          ),
          Text(
            'Edit',
            style: AppFonts.base(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          Obx(
            () => TextButton(
              onPressed: controller.isAdding.value
                  ? null
                  : () => controller.updateItem(item.id, item.imageUrl),
              child: controller.isAdding.value
                  ? const SizedBox(
                      width: 20,
                      height: 20,
                      child: CircularProgressIndicator(
                        strokeWidth: 2,
                        color: AppColors.primary,
                      ),
                    )
                  : Text(
                      'Save',
                      style: AppFonts.base(
                        color: AppColors.primary,
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLabel(String label, {bool isRequired = false}) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 8.0),
      child: Row(
        children: [
          Text(
            label,
            style: AppFonts.base(
              color: const Color(0xFF8E8E93),
              fontSize: 14,
              fontWeight: FontWeight.w500,
            ),
          ),
          if (isRequired)
            const Text(' *', style: TextStyle(color: Colors.red, fontSize: 14)),
        ],
      ),
    );
  }

  InputDecoration _getInputDecoration(String hint) {
    return InputDecoration(
      hintText: hint,
      hintStyle: AppFonts.base(color: const Color(0xFF555555), fontSize: 14),
      filled: true,
      fillColor: const Color(0xFF262626),
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(8),
        borderSide: BorderSide.none,
      ),
    );
  }

  Widget _buildDropdown({
    required String? value,
    required List<String> items,
    required String hint,
    required ValueChanged<String?> onChanged,
  }) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      decoration: BoxDecoration(
        color: const Color(0xFF262626),
        borderRadius: BorderRadius.circular(8),
      ),
      child: DropdownButtonHideUnderline(
        child: DropdownButton<String>(
          value: value,
          hint: Text(
            hint,
            style: AppFonts.base(color: const Color(0xFF555555), fontSize: 14),
          ),
          dropdownColor: const Color(0xFF262626),
          icon: const Icon(Icons.keyboard_arrow_down, color: Colors.white54),
          isExpanded: true,
          style: AppFonts.base(color: Colors.white, fontSize: 14),
          items: items
              .map((e) => DropdownMenuItem(value: e, child: Text(e)))
              .toList(),
          onChanged: onChanged,
        ),
      ),
    );
  }
}
