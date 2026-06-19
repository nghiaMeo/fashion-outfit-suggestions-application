import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../../core/theme/app_fonts.dart';
import '../controllers/wardrobe_controller.dart';

class AddItemSheet extends GetView<WardrobeController> {
  const AddItemSheet({super.key});

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
                  _buildImagePicker(),
                  const SizedBox(height: 24),
                  _buildLabel('Name', isRequired: true),
                  const SizedBox(height: 8),
                  _buildTextField(
                    controller.nameController,
                    'Example: Basic white t-shirt',
                  ),
                  const SizedBox(height: 20),
                  _buildLabel('Type', isRequired: true),
                  const SizedBox(height: 8),
                  _buildChipSelector(controller.types, controller.selectedType),
                  const SizedBox(height: 20),
                  _buildLabel('Color', isRequired: true),
                  const SizedBox(height: 8),
                  _buildChipSelector(
                    controller.colors,
                    controller.selectedColor,
                  ),
                  const SizedBox(height: 20),
                  _buildLabel('Season'),
                  const SizedBox(height: 8),
                  _buildChipSelector(
                    controller.seasons,
                    controller.selectedSeason,
                  ),
                  const SizedBox(height: 20),
                  _buildLabel('Occasion'),
                  const SizedBox(height: 8),
                  _buildChipSelector(
                    controller.occasions,
                    controller.selectedOccasion,
                  ),
                  const SizedBox(height: 20),
                  _buildLabel('Brand'),
                  const SizedBox(height: 8),
                  _buildTextField(
                    controller.brandController,
                    'For example: Zara, H&M, Uniqlo...',
                  ),
                  const SizedBox(height: 20),
                  _buildLabel('Tags'),
                  const SizedBox(height: 8),
                  _buildTextField(
                    controller.tagsController,
                    'Example: casual,basic,summer',
                  ),
                  const SizedBox(height: 32),
                  _buildAddButton(),
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
        border: Border(bottom: BorderSide(color: Color(0xFF2C2C2C))),
      ),
      child: Row(
        children: [
          GestureDetector(
            onTap: () => Get.back(),
            child: Text(
              'Cancel',
              style: AppFonts.base(
                color: const Color(0xFF8E8E93),
                fontSize: 16,
              ),
            ),
          ),
          Expanded(
            child: Center(
              child: Text(
                'More clothes',
                style: AppFonts.base(
                  color: Colors.white,
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ),
          const SizedBox(width: 40),
        ],
      ),
    );
  }

  Widget _buildImagePicker() {
    return Obx(() {
      final image = controller.selectedImage.value;
      return GestureDetector(
        onTap: controller.pickImage,
        child: Container(
          width: double.infinity,
          height: 220,
          decoration: BoxDecoration(
            color: const Color(0xFF262626),
            borderRadius: BorderRadius.circular(12),
            border: Border.all(color: const Color(0xFF3A3A3C)),
          ),
          child: image != null
              ? ClipRRect(
                  borderRadius: BorderRadius.circular(12),
                  child: Stack(
                    fit: StackFit.expand,
                    children: [
                      Image.file(image, fit: BoxFit.cover),
                      Positioned(
                        bottom: 10,
                        right: 10,
                        child: Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 10,
                            vertical: 6,
                          ),
                          decoration: BoxDecoration(
                            color: Colors.black.withValues(alpha: 0.7),
                            borderRadius: BorderRadius.circular(20),
                          ),
                          child: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              const Icon(
                                Icons.edit,
                                color: Colors.white,
                                size: 14,
                              ),
                              const SizedBox(width: 4),
                              Text(
                                'Change photo',
                                style: AppFonts.base(
                                  color: Colors.white,
                                  fontSize: 12,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                )
              : Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Icon(
                      Icons.add_photo_alternate_outlined,
                      color: Color(0xFF8E8E93),
                      size: 48,
                    ),
                    const SizedBox(height: 12),
                    Text(
                      'Select a photo from the library',
                      style: AppFonts.base(
                        color: const Color(0xFF8E8E93),
                        fontSize: 14,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'Obligatory',
                      style: AppFonts.base(
                        color: const Color(0xFFED4956),
                        fontSize: 12,
                      ),
                    ),
                  ],
                ),
        ),
      );
    });
  }

  Widget _buildLabel(String label, {bool isRequired = false}) {
    return Row(
      children: [
        Text(
          label,
          style: AppFonts.base(
            color: Colors.white,
            fontSize: 14,
            fontWeight: FontWeight.w600,
          ),
        ),
        if (isRequired) ...[
          const SizedBox(width: 4),
          Text(
            '*',
            style: AppFonts.base(color: const Color(0xFFED4956), fontSize: 14),
          ),
        ],
      ],
    );
  }

  Widget _buildTextField(TextEditingController textController, String hint) {
    return TextField(
      controller: textController,
      style: AppFonts.base(color: Colors.white, fontSize: 14),
      decoration: InputDecoration(
        hintText: hint,
        hintStyle: AppFonts.base(color: const Color(0xFF8E8E93), fontSize: 14),
        filled: true,
        fillColor: const Color(0xFF262626),
        contentPadding: const EdgeInsets.symmetric(
          horizontal: 14,
          vertical: 12,
        ),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10),
          borderSide: const BorderSide(color: Color(0xFF3A3A3C)),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10),
          borderSide: const BorderSide(color: Color(0xFF3A3A3C)),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10),
          borderSide: const BorderSide(color: Color(0xFF0095F6)),
        ),
      ),
    );
  }

  Widget _buildChipSelector(List<String> options, RxString selected) {
    return Obx(
      () => Wrap(
        spacing: 8,
        runSpacing: 8,
        children: options.map((option) {
          final isSelected = selected.value == option;
          return GestureDetector(
            onTap: () {
              selected.value = isSelected ? '' : option;
            },
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
              decoration: BoxDecoration(
                color: isSelected
                    ? const Color(0xFF0095F6)
                    : const Color(0xFF262626),
                borderRadius: BorderRadius.circular(20),
                border: Border.all(
                  color: isSelected
                      ? const Color(0xFF0095F6)
                      : const Color(0xFF3A3A3C),
                ),
              ),
              child: Text(
                option,
                style: AppFonts.base(
                  color: isSelected ? Colors.white : const Color(0xFF8E8E93),
                  fontSize: 13,
                  fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
                ),
              ),
            ),
          );
        }).toList(),
      ),
    );
  }

  Widget _buildAddButton() {
    return Obx(
      () => SizedBox(
        width: double.infinity,
        height: 50,
        child: ElevatedButton(
          style: ElevatedButton.styleFrom(
            backgroundColor: const Color(0xFF0095F6),
            disabledBackgroundColor: const Color(
              0xFF0095F6,
            ).withValues(alpha: 0.4),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(10),
            ),
          ),
          onPressed: controller.isAdding.value ? null : controller.addItem,
          child: controller.isAdding.value
              ? const SizedBox(
                  width: 20,
                  height: 20,
                  child: CircularProgressIndicator(
                    color: Colors.white,
                    strokeWidth: 2,
                  ),
                )
              : Text(
                  'Add to wardrobe',
                  style: AppFonts.base(
                    color: Colors.white,
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
        ),
      ),
    );
  }
}
