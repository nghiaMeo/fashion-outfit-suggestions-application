import 'package:fashion_outfit_suggestions_app/app/modules/searches/views/searches_view.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_fonts.dart';
import '../../../../core/widgets/app_bottom_nav.dart';
import '../../../routes/app_routes.dart';
import '../../message/views/message_view.dart';
import '../../profile/views/profile_view.dart';
import '../../wardrobe/views/wardrobe_view.dart';
import '../controllers/home_controller.dart';

class HomeView extends GetView<HomeController> {
  const HomeView({super.key});

  @override
  Widget build(BuildContext context) {
    final List<Widget> pages = [
      const HomeFeedView(),
      const SearchesView(),
      const WardrobeView(),
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
        backgroundColor: AppColors.background,
        elevation: 0,
        centerTitle: false,
        title: Text(
          'Stylist',
          style: AppFonts.base(
            color: Colors.white,
            fontSize: 20,
            fontWeight: FontWeight.w500,
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
            onPressed: () {
              Get.toNamed(Routes.notification);
            },
          ),
        ],
      ),
      body: Center(child: Text('Home Feed View')),
    );
  }
}
