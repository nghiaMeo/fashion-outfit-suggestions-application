import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:get_storage/get_storage.dart';

import 'package:fashion_outfit_suggestions_app/app/fashion_app.dart';

void main() {
  testWidgets('Splash shows loading', (WidgetTester tester) async {
    await GetStorage.init();
    await tester.pumpWidget(const FashionApp());
    await tester.pump();

    expect(find.text('Fashion Outfit'), findsOneWidget);
    expect(find.byType(CircularProgressIndicator), findsOneWidget);
  });
}
