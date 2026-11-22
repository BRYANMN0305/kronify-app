package com.kronifyapp.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.kronifyapp.ui.screens.auth.LoginScreen
import com.kronifyapp.ui.screens.auth.OnboardingStep1Screen
import com.kronifyapp.ui.screens.auth.OnboardingStep2Screen
import com.kronifyapp.ui.screens.auth.OnboardingStep3Screen
import com.kronifyapp.ui.screens.auth.RegisterScreen
import com.kronifyapp.ui.screens.intro.IntroScreen
import com.kronifyapp.ui.screens.welcome.WelcomeScreen
import com.kronifyapp.ui.screens.main.MainScreen

object Routes {
    const val INTRO = "intro"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val ONBOARDING_1 = "onboarding_1/{token}/{userId}"
    const val ONBOARDING_2 = "onboarding_2/{token}/{userId}"
    const val ONBOARDING_3 = "onboarding_3/{token}/{userId}"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.INTRO,
        enterTransition = {
            fadeIn(tween(durationMillis = 420)) +
                slideInVertically(tween(durationMillis = 460)) { it / 18 }
        },
        exitTransition = { fadeOut(tween(durationMillis = 240)) },
        popEnterTransition = { fadeIn(tween(durationMillis = 320)) },
        popExitTransition = { fadeOut(tween(durationMillis = 200)) },
    ) {
        composable(route = Routes.INTRO) {
            IntroScreen(
                onFinish = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(Routes.INTRO) { inclusive = true }
                    }
                },
            )
        }

        composable(route = Routes.WELCOME) {
            WelcomeScreen(
                onLogin = { navController.navigate(Routes.LOGIN) },
                onRegister = { navController.navigate(Routes.REGISTER) },
            )
        }

        composable(route = Routes.LOGIN) {
            LoginScreen(
                onRegister = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = { token, userId, profileType, step ->
                    if (profileType == "BUSINESS" && step < 4) {
                        navController.navigate("onboarding_$step/$token/$userId")
                    } else {
                        navController.navigate(Routes.MAIN) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
            )
        }

        composable(route = Routes.REGISTER) {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onCompleteRegister = { token, userId -> navController.navigate("onboarding_1/$token/$userId") },
            )
        }

        composable(route = Routes.MAIN) { MainScreen() }

        composable(
            route = Routes.ONBOARDING_1,
            arguments = listOf(
                navArgument("token") { type = NavType.StringType },
                navArgument("userId") { type = NavType.LongType },
            ),
        ) { backStackEntry ->
            OnboardingStep1Screen(
                onBack = { navController.popBackStack() },
                onContinue = { token, userId -> navController.navigate("onboarding_2/$token/$userId") },
                token = backStackEntry.arguments?.getString("token").orEmpty(),
                userId = backStackEntry.arguments?.getLong("userId") ?: 0L,
            )
        }

        composable(
            route = Routes.ONBOARDING_2,
            arguments = listOf(navArgument("token") { type = NavType.StringType }, navArgument("userId") { type = NavType.LongType }),
        ) { backStackEntry ->
            OnboardingStep2Screen(
                onBack = { navController.popBackStack() },
                onFinish = { token, userId -> navController.navigate("onboarding_3/$token/$userId") },
                token = backStackEntry.arguments?.getString("token").orEmpty(),
                userId = backStackEntry.arguments?.getLong("userId") ?: 0L,
            )
        }

        composable(
            route = Routes.ONBOARDING_3,
            arguments = listOf(navArgument("token") { type = NavType.StringType }, navArgument("userId") { type = NavType.LongType }),
        ) { backStackEntry ->
            OnboardingStep3Screen(
                onBack = { navController.popBackStack() },
                onFinish = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                userId = backStackEntry.arguments?.getLong("userId") ?: 0L,
            )
        }
    }
}
