import 'package:fashion_outfit_suggestions_app/app/modules/searches/views/searches_view.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../../../../core/widgets/app_bottom_nav.dart';
import '../../message/views/message_view.dart';
import '../../profile/views/profile_view.dart';
import '../controllers/home_controller.dart';

class HomeView extends GetView<HomeController> {
  const HomeView({super.key});

  @override
  Widget build(BuildContext context) {
    final List<Widget> pages = [
      const HomeFeedView(),
      const SearchesView(),
      const Center(child: Text('Dry Cleaning View')),
      const MessageView(),
      const ProfileView(),
    ];

    return Scaffold(
      body: Obx(
        () =>
            IndexedStack(index: controller.currentIndex.value, children: pages),
      ),
      bottomNavigationBar: Obx(
        () => AppBottomNav(
          currentIndex: controller.currentIndex.value,
          onTap: controller.changPage,
        ),
      ),
    );
  }
}

class HomeFeedView extends StatelessWidget {
  const HomeFeedView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.black,
        elevation: 0,
        centerTitle: false,
        title: const Text(
          'Instagram',
          style: TextStyle(
            color: Colors.white,
            fontSize: 28,
            fontFamily: 'Billabong',
            fontWeight: FontWeight.w500,
            letterSpacing: 0.5,
          ),
        ),
        actions: [
          IconButton(
            icon: const Icon(
              Icons.add_box_outlined,
              size: 26,
              color: Colors.white,
            ),
            onPressed: () {},
          ),
          IconButton(
            icon: const Icon(
              Icons.favorite_border,
              size: 26,
              color: Colors.white,
            ),
            onPressed: () {},
          ),
        ],
      ),
      body: const Center(child: Text('Home Feed View')),
    );
  }
}
