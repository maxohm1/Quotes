package max.ohm.quoteapp.presentation.quotedetail

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import max.ohm.quoteapp.domain.model.Quote

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteDetailScreen(
    quoteId: String,
    onNavigateBack: () -> Unit,
    onNavigateToShare: (String) -> Unit,
    viewModel: QuoteDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(quoteId) {
        viewModel.loadQuote(quoteId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quote") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    uiState.quote?.let { quote ->
                        IconButton(onClick = { onNavigateToShare(quote.id) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share"
                            )
                        }
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
                        Button(onClick = { viewModel.loadQuote(quoteId) }) {
                            Text("Retry")
                        }
                    }
                }
                uiState.quote != null -> {
                    QuoteDetailContent(
                        quote = uiState.quote!!,
                        onToggleFavorite = { viewModel.toggleFavorite() }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuoteDetailContent(
    quote: Quote,
    onToggleFavorite: () -> Unit
) {
    val scrollState = rememberScrollState()
    val favoriteColor by animateColorAsState(
        targetValue = if (quote.isFavorite) 
            MaterialTheme.colorScheme.error 
        else 
            MaterialTheme.colorScheme.onSurfaceVariant,
        label = "favoriteColor"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Quote text
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "❝",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = quote.text,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    lineHeight = MaterialTheme.typography.headlineSmall.lineHeight * 1.3f
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "— ${quote.author}",
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Category chip
        AssistChip(
            onClick = { },
            label = {
                Text(
                    text = quote.category.displayName,
                    style = MaterialTheme.typography.labelLarge
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Favorite button
        FilledTonalIconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(64.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = if (quote.isFavorite) 
                    MaterialTheme.colorScheme.errorContainer 
                else 
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Icon(
                imageVector = if (quote.isFavorite) 
                    Icons.Filled.Favorite 
                else 
                    Icons.Outlined.FavoriteBorder,
                contentDescription = if (quote.isFavorite) "Remove from favorites" else "Add to favorites",
                modifier = Modifier.size(32.dp),
                tint = favoriteColor
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (quote.isFavorite) "In your favorites" else "Add to favorites",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
