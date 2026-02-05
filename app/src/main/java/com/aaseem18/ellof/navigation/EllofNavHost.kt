package com.aaseem18.ellof.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aaseem18.ellof.ui.add.AddReminderScreen
import com.aaseem18.ellof.ui.home.HomeScreen
import com.aaseem18.ellof.viewmodel.ReminderViewModel

@Composable
fun EllofNavHost(
    viewModel: ReminderViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {
        composable(Routes.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onAddClick = {
                    navController.navigate(Routes.Add.route)
                }
            )
        }

        composable(Routes.Add.route) {
            AddReminderScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
