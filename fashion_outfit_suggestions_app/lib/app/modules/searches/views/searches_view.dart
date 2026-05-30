import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/searches_controller.dart';

class SearchesView extends GetView<SearchesController> {
  const SearchesView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.black,
        elevation: 0,
        title: Container(
          height: 40,
          decoration: BoxDecoration(
            color: Color(0xFF262626),
            borderRadius: BorderRadius.circular(10),
          ),
          child: const TextField(
            style: TextStyle(color: Colors.white, fontSize: 15),
            decoration: InputDecoration(
              prefixIcon: Icon(
                Icons.search,
                color: Color(0xFF8E8E93),
                size: 20,
              ),
              border: InputBorder.none,
              contentPadding: EdgeInsets.symmetric(vertical: 8),
            ),
          ),
        ),
        centerTitle: true,
      ),
      body: const Center(child: Text('Searches View')),
    );
  }
}
