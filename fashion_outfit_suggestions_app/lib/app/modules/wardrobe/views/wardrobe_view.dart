import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/wardrobe_controller.dart';

class WardrobeView extends GetView<WardrobeController> {
  const WardrobeView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('WardrobeView'),
        centerTitle: true,
      ),
      body: const Center(
        child: Text(
          'WardrobeView is working',
          style: TextStyle(fontSize: 20),
        ),
      ),
    );
  }
}
