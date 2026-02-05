package com.aaseem18.ellof.navigation


sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Add : Routes("add")
}
