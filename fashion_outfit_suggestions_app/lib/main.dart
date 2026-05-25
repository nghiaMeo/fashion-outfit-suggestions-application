import 'package:flutter/material.dart';
import 'package:get_storage/get_storage.dart';

import 'app/fashion_app.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await GetStorage.init();
  runApp(const FashionApp());
}
