package max.ohm.quoteapp.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import max.ohm.quoteapp.domain.model.QuoteCategory
import max.ohm.quoteapp.presentation.components.CategoryCard
import max.ohm.quoteapp.presentation.components.DailyQuoteCard
import max.ohm.quoteapp.presentation.components.EmptyState
import max.ohm.quoteapp.presentation.components.LoadingScreen
import max.ohm.quoteapp.presentation.components.QuoteCard
import max.ohm.quoteapp.presentation.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCategory: (QuoteCategory) -> Unit,
    onNavigateToQuoteDetail: (String) -> Unit,
    onNavigateToShare: (String) -> Unit,
    onAddToCollection: (String) -> Unit,
    initialCategory: QuoteCategory? = null,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(initialCategory) {
        if (initialCategory != null) {
            viewModel.selectCategory(initialCategory)
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is HomeEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                is HomeEvent.NavigateToShare -> onNavigateToShare(event.quoteId)
                is HomeEvent.NavigateToQuoteDetail -> onNavigateToQuoteDetail(event.quoteId)
                is HomeEvent.NavigateToCategory -> onNavigateToCategory(event.category)
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingScreen(message = "Loading quotes...")
        } else {
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    // Header
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (initialCategory != null) {
                                    Text(
                                        text = "${initialCategory.displayName} Quotes",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Text(
                                        text = "✨",
                                        fontSize = 28.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "QuoteVault",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            
                            if (initialCategory == null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Text(
                                    text = "Discover inspiring quotes",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    // Search Bar
                    if (initialCategory == null) {
                        item {
                            SearchBar(
                                query = uiState.searchQuery,
                                onQueryChange = viewModel::updateSearchQuery,
                                onSearch = { },
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                    
                    // Daily Quote (only show if not searching)
                    if (initialCategory == null) {
                        item {
                            AnimatedVisibility(
                                visible = uiState.searchQuery.isBlank() && uiState.dailyQuote != null,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                uiState.dailyQuote?.let { quote ->
                                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                        DailyQuoteCard(
                                            quote = quote,
                                            onFavoriteClick = { viewModel.toggleFavorite(quote) },
                                            onShareClick = { onNavigateToShare(quote.id) },
                                            onClick = { onNavigateToQuoteDetail(quote.id) },
                                            fontSizeScale = settings.fontSize.scale
                                        )
                                        Spacer(modifier = Modifier.height(24.dp))
                                    }
                                }
                            }
                        }
                    }
                    
                    // Categories (only show if not searching)
                    if (initialCategory == null) {
                        item {
                            AnimatedVisibility(
                                visible = uiState.searchQuery.isBlank(),
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Column {
                                    Text(
                                        text = "Browse by Category",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 20.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 20.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(QuoteCategory.entries) { category ->
                                            CategoryCard(
                                                category = category,
                                                quoteCount = uiState.categoryCounts[category] ?: 0,
                                                onClick = { onNavigateToCategory(category) }
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }
                        }
                    }
                    
                    // Section title
                    item {
                        Text(
                            text = if (uiState.searchQuery.isNotBlank()) 
                                "Search Results" 
                            else if (initialCategory != null)
                                "Filtered Quotes"
                            else 
                                "All Quotes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    // Quotes list
                    if (uiState.quotes.isEmpty()) {
                        item {
                            EmptyState(
                                title = if (uiState.searchQuery.isNotBlank()) 
                                    "No quotes found" 
                                else 
                                    "No quotes yet",
                                message = if (uiState.searchQuery.isNotBlank())
                                    "Try a different search term"
                                else
                                    "Pull down to refresh",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.FormatQuote,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            )
                        }
                    } else {
                        items(
                            items = uiState.quotes,
                            key = { it.id }
                        ) { quote ->
                            QuoteCard(
                                quote = quote,
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 16.dp),
                                onFavoriteClick = { viewModel.toggleFavorite(quote) },
                                onShareClick = { onNavigateToShare(quote.id) },
                                onAddToCollectionClick = { onAddToCollection(quote.id) },
                                onClick = { onNavigateToQuoteDetail(quote.id) },
                                fontSizeScale = settings.fontSize.scale
                            )
                        }
                    }
                }
            }
        }
    }
}
