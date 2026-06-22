import 'package:fashion_outfit_suggestions_app/app/modules/outfit/controllers/outfit_controller.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../../core/theme/app_fonts.dart';
import '../../../../core/theme/app_colors.dart';

class OutfitView extends GetView<OutfitController> {
  const OutfitView({super.key});

  @override
  Widget build(BuildContext context) {
    return Obx(() {
      final isStepOne = controller.pageIndex.value == 0;
      return Scaffold(
        backgroundColor: Colors.black,
        appBar: AppBar(
          backgroundColor: Colors.black,
          elevation: 0,
          leading: IconButton(
            icon: Icon(
              isStepOne ? Icons.close : Icons.arrow_back,
              color: Colors.white,
            ),
            onPressed: isStepOne ? () => Get.back() : controller.goBackStep,
          ),
          title: Text(
            isStepOne ? 'New Outfit Ideas' : 'Fill in the details',
            style: AppFonts.base(
              color: Colors.white,
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          actions: [
            if (isStepOne)
              TextButton(
                onPressed: controller.goToNextStep,
                child: Text(
                  'Continue',
                  style: AppFonts.base(
                    color: AppColors.primary,
                    fontWeight: FontWeight.bold,
                    fontSize: 16,
                  ),
                ),
              )
            else
              Obx(
                () => TextButton(
                  onPressed: controller.isSaving.value
                      ? null
                      : controller.saveOutfit,
                  child: controller.isSaving.value
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
                            fontWeight: FontWeight.bold,
                            fontSize: 16,
                          ),
                        ),
                ),
              ),
          ],
        ),
        body: isStepOne ? _buildItemSelection() : _buildDetailsForm(),
      );
    });
  }

  Widget _buildItemSelection() {
    return Obx(() {
      if (controller.isLoading.value) {
        return const Center(
          child: CircularProgressIndicator(color: Colors.white),
        );
      }

      if (controller.items.isEmpty) {
        return Center(
          child: Text(
            'Your wardrobe is empty. \nAdd items before you start styling!',
            textAlign: TextAlign.center,
            style: AppFonts.base(color: Colors.white70, fontSize: 16),
          ),
        );
      }

      return GridView.builder(
        padding: const EdgeInsets.all(4),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 3,
          crossAxisSpacing: 4,
          mainAxisSpacing: 4,
        ),
        itemCount: controller.items.length,
        itemBuilder: (context, index) {
          final item = controller.items[index];
          return Obx(() {
            final selected = controller.isSelected(item);
            return GestureDetector(
              onTap: () => controller.toggleSelectItem(item),
              child: Stack(
                fit: StackFit.expand,
                children: [
                  Container(
                    color: const Color(0xFF1C1C1E),
                    child: item.imageUrl != null
                        ? Image.network(item.imageUrl!, fit: BoxFit.cover)
                        : const Icon(Icons.checkroom, color: Colors.grey),
                  ),
                  if (selected)
                    Container(color: Colors.black.withValues(alpha: 0.4)),
                  Positioned(
                    top: 8,
                    right: 8,
                    child: Container(
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        color: selected ? AppColors.primary : Colors.black26,
                        border: Border.all(color: Colors.white, width: 1.5),
                      ),
                      padding: const EdgeInsets.all(4),
                      child: Icon(
                        Icons.check,
                        color: selected ? Colors.black : Colors.transparent,
                        size: 14,
                      ),
                    ),
                  ),
                ],
              ),
            );
          });
        },
      );
    });
  }

  Widget _buildDetailsForm() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            height: 100,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              itemCount: controller.selectedItems.length,
              itemBuilder: (context, index) {
                final item = controller.selectedItems[index];
                return Padding(
                  padding: const EdgeInsets.only(right: 8.0),
                  child: ClipRRect(
                    borderRadius: BorderRadius.circular(8),
                    child: Container(
                      width: 100,
                      color: const Color(0xFF1C1C1E),
                      child: item.imageUrl != null
                          ? Image.network(item.imageUrl!, fit: BoxFit.cover)
                          : const Icon(Icons.checkroom, color: Colors.grey),
                    ),
                  ),
                );
              },
            ),
          ),
          const SizedBox(height: 24),
          _buildLabel('Outfit Name', isRequired: true),
          TextField(
            controller: controller.nameController,
            style: AppFonts.base(color: Colors.white),
            decoration: _getInputDecoration('Example: Casual street wear'),
          ),
          const SizedBox(height: 16),

          _buildLabel('Suitable Occasion'),
          Obx(
            () => _buildDropdown(
              value: controller.selectedOccasion.value,
              items: controller.occasions,
              onChanged: (val) =>
                  controller.selectedOccasion.value = val ?? 'Casual',
            ),
          ),
          const SizedBox(height: 16),

          _buildLabel('Description'),
          TextField(
            controller: controller.descriptionController,
            maxLines: 3,
            style: AppFonts.base(color: Colors.white),
            decoration: _getInputDecoration(
              'Describe in detail how you want to combine or feel...',
            ),
          ),
          const SizedBox(height: 20),
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
    required String value,
    required List<String> items,
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
