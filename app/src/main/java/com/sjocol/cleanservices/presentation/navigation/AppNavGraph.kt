package com.sjocol.cleanservices.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sjocol.cleanservices.presentation.events.GlobalEventsScreen
import com.sjocol.cleanservices.presentation.events.WorkEntryFormScreen
import com.sjocol.cleanservices.presentation.home.HomeScreen
import com.sjocol.cleanservices.presentation.home.HomeViewModel
import com.sjocol.cleanservices.presentation.house.HouseDetailScreen
import com.sjocol.cleanservices.presentation.house.HouseFormScreen

object Destinations {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val HOUSE_FORM = "houseForm"
    const val HOUSE_FORM_WITH_ID = "houseForm/{houseId}"
    const val HOUSE_DETAIL = "houseDetail/{houseId}"
    const val WORK_ENTRY_FORM = "workEntryForm"
    const val WORK_ENTRY_FORM_WITH_ID = "workEntryForm/{houseId}"
    const val GLOBAL_EVENTS = "globalEvents"
    const val REPORTS = "reports"
}

@Composable
fun AppNavGraph(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Destinations.HOME, modifier = modifier) {
        composable(Destinations.HOME) {
            val vm: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = vm,
                onAddHouse = { navController.navigate(Destinations.HOUSE_FORM) },
                onOpenSettings = { navController.navigate(Destinations.SETTINGS) },
                onEditHouse = { id -> navController.navigate("houseDetail/$id") },
                onDeleteHouse = { id -> vm.deleteHouse(id) },
                onOpenHouse = { id -> navController.navigate("workEntryForm/$id") },
                onOpenGlobal = { navController.navigate(Destinations.GLOBAL_EVENTS) }
            )
        }
        composable(Destinations.SETTINGS) { Text("Configuración") }
        composable(Destinations.HOUSE_FORM) { HouseFormScreen(onBack = { navController.popBackStack() }) }
        composable(
            route = Destinations.HOUSE_FORM_WITH_ID,
            arguments = listOf(navArgument("houseId") { type = NavType.LongType })
        ) { HouseFormScreen(onBack = { navController.popBackStack() }) }

        composable(
            route = Destinations.HOUSE_DETAIL,
            arguments = listOf(navArgument("houseId") { type = NavType.LongType })
        ) {
            val houseId = it.arguments?.getLong("houseId") ?: 0L
            HouseDetailScreen(onBack = { navController.popBackStack() }, onAddEntry = { navController.navigate("workEntryForm/$houseId") })
        }

        composable(Destinations.WORK_ENTRY_FORM) { WorkEntryFormScreen(onBack = { navController.popBackStack() }) }
        composable(
            route = Destinations.WORK_ENTRY_FORM_WITH_ID,
            arguments = listOf(navArgument("houseId") { type = NavType.LongType })
        ) { WorkEntryFormScreen(onBack = { navController.popBackStack() }) }

        composable(Destinations.GLOBAL_EVENTS) { GlobalEventsScreen(onBack = { navController.popBackStack() }) }
        composable(Destinations.REPORTS) { Text("Reportes") }
    }
} 