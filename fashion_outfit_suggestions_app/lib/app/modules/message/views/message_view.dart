import 'package:fashion_outfit_suggestions_app/core/theme/app_fonts.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../../home/controllers/home_controller.dart';
import '../controllers/message_controller.dart';

class MessageView extends GetView<MessageController> {
  const MessageView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.black,
        elevation: 0,
        leading: IconButton(
          onPressed: () {
            final homeController = Get.find<HomeController>();
            homeController.changPage(0);
          },
          icon: Icon(Icons.arrow_back, color: Colors.white),
        ),

        title: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text('nghiamewo_ss', style: AppFonts.base()),
            IconButton(
              color: Colors.white,
              onPressed: () {},
              icon: Icon(Icons.keyboard_arrow_down_outlined),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () {},
            child: Text('Note', style: AppFonts.base()),
          ),
        ],
        centerTitle: true,
      ),
      body: const Center(
        child: Text('MessageView is working', style: TextStyle(fontSize: 20)),
      ),
    );
  }
}
