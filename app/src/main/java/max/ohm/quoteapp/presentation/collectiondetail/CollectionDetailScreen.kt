package max.ohm.quoteapp.presentation.collectiondetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import max.ohm.quoteapp.domain.model.Quote
import max.ohm.quoteapp.presentation.components.QuoteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailScreen(
    collectionId: String,
    onNavigateBack: () -> Unit,
    onNavigateToQuoteDetail: (String) -> Unit,
    onNavigateToShare: (String) -> Unit,
    viewModel: CollectionDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(collectionId) {
        viewModel.loadCollection(collectionId)
    }
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var quoteToDelete by remember { mutableStateOf<Quote?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(uiState.collection?.name ?: "Collection")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.error ?: "An error occurred",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadCollection(collectionId) }) {
                            Text("Retry")
                        }
                    }
                }
                uiState.quotes.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📚",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No quotes in this collection",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add quotes from the home screen",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Collection description
                        uiState.collection?.description?.takeIf { it.isNotBlank() }?.let { description ->
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(16.dp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        
                        // Quote count
                        item {
                            Text(
                                text = "${uiState.quotes.size} quote${if (uiState.quotes.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        // Quotes list
                        items(
                            items = uiState.quotes,
                            key = { it.id }
                        ) { quote ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                QuoteCard(
                                    quote = quote,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onNavigateToQuoteDetail(quote.id) },
                                    onFavoriteClick = { viewModel.toggleFavorite(quote) },
                                    onShareClick = { onNavigateToShare(quote.id) },
                                    onAddToCollectionClick = { /* Already in collection */ }
                                )
                                IconButton(
                                    onClick = {
                                        quoteToDelete = quote
                                        showDeleteDialog = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove from collection",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog && quoteToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false
                quoteToDelete = null
            },
            title = { Text("Remove Quote") },
            text = { Text("Remove this quote from the collection?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        quoteToDelete?.let { viewModel.removeFromCollection(it.id) }
                        showDeleteDialog = false
                        quoteToDelete = null
                    }
                ) {
                    Text("Remove", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        quoteToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
