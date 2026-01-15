package max.ohm.quoteapp.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Splash : Screen
    
    @Serializable
    data object Login : Screen
    
    @Serializable
    data object SignUp : Screen
    
    @Serializable
    data object ForgotPassword : Screen
    
    @Serializable
    data object Home : Screen
    
    @Serializable
    data object Favorites : Screen
    
    @Serializable
    data object Collections : Screen
    
    @Serializable
    data class CollectionDetail(val collectionId: String) : Screen
    
    @Serializable
    data object Profile : Screen
    
    @Serializable
    data object Settings : Screen
    
    @Serializable
    data class QuoteDetail(val quoteId: String) : Screen
    
    @Serializable
    data class ShareQuote(val quoteId: String) : Screen
    
    @Serializable
    data class CategoryQuotes(val category: String) : Screen
    
    @Serializable
    data class SearchResults(val query: String) : Screen
}

sealed interface BottomNavItem {
    val route: Screen
    val title: String
    val icon: String
    
    data object Home : BottomNavItem {
        override val route = Screen.Home
        override val title = "Home"
        override val icon = "home"
    }
    
    data object Favorites : BottomNavItem {
        override val route = Screen.Favorites
        override val title = "Favorites"
        override val icon = "favorite"
    }
    
    data object Collections : BottomNavItem {
        override val route = Screen.Collections
        override val title = "Collections"
        override val icon = "collections"
    }
    
    data object Profile : BottomNavItem {
        override val route = Screen.Profile
        override val title = "Profile"
        override val icon = "person"
    }
    
    companion object {
        val items = listOf(Home, Favorites, Collections, Profile)
    }
}
