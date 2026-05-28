import 'package:fashion_outfit_suggestions_app/app/modules/splash/splash_controller.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:math_curve_loaders/math_curve_loaders.dart';

class SplashView extends GetView<SplashController> {
  const SplashView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: MathCurveLoader.lissajous(
          size: 164,
          color: const Color(0xFFFF7A90),
          duration: const Duration(milliseconds: 3000),
          xFrequency: 3,
          yFrequency: 2,
          phase: 0.31,
          radius: 18.0,
          style: const MathCurveLoaderStyle(
            particleCount: 120,
            trailSpan: 0.38,
            strokeWidth: 4.0,
          ),
          animate: true,
          reverse: false,
        ),
      ),
    );
  }
}
