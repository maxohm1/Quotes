package max.ohm.quoteapp.presentation.share

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import max.ohm.quoteapp.domain.model.Quote
import max.ohm.quoteapp.presentation.home.HomeViewModel
import max.ohm.quoteapp.ui.theme.GradientBlue
import max.ohm.quoteapp.ui.theme.GradientForest
import max.ohm.quoteapp.ui.theme.GradientOcean
import max.ohm.quoteapp.ui.theme.GradientPurple
import max.ohm.quoteapp.ui.theme.GradientSunset

data class CardStyle(
    val name: String,
    val gradient: List<Color>,
    val textColor: Color = Color.White
)

val cardStyles = listOf(
    CardStyle("Purple Dream", GradientPurple),
    CardStyle("Ocean Wave", GradientOcean),
    CardStyle("Sunset Glow", GradientSunset),
    CardStyle("Forest Calm", GradientForest),
    CardStyle("Sky Blue", GradientBlue),
    CardStyle("Minimal", listOf(Color(0xFF1A1A1A), Color(0xFF2D2D2D)))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    quoteId: String,
    onNavigateBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var quote by remember { mutableStateOf<Quote?>(null) }
    var selectedStyleIndex by remember { mutableIntStateOf(0) }
    
    val graphicsLayer = rememberGraphicsLayer()
    
    LaunchedEffect(quoteId) {
        viewModel.uiState.collect { state ->
            quote = state.quotes.find { it.id == quoteId } ?: state.dailyQuote
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Share Quote",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            quote?.let { q ->
                // Quote Card Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawWithContent {
                            graphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(graphicsLayer)
                        }
                ) {
                    QuoteCardPreview(
                        quote = q,
                        style = cardStyles[selectedStyleIndex]
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Style Selector
                Text(
                    text = "Choose Style",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    cardStyles.forEachIndexed { index, style ->
                        StyleOption(
                            style = style,
                            isSelected = selectedStyleIndex == index,
                            onClick = { selectedStyleIndex = index }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Share as Text Button
                OutlinedButton(
                    onClick = {
                        ShareUtils.shareAsText(context, q.text, q.author)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TextFields,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share as Text")
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Share as Image Button
                Button(
                    onClick = {
                        scope.launch {
                            val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                            ShareUtils.shareAsImage(context, bitmap, q.text)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share as Image")
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Save to Gallery Button
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                            val success = ShareUtils.saveToGallery(context, bitmap)
                            snackbarHostState.showSnackbar(
                                if (success) "Saved to gallery!" else "Failed to save"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save to Gallery")
                }
            }
        }
    }
}

@Composable
private fun QuoteCardPreview(
    quote: Quote,
    style: CardStyle
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(colors = style.gradient)
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "✨",
                    fontSize = 32.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "\"${quote.text}\"",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontStyle = FontStyle.Italic,
                        lineHeight = 32.sp
                    ),
                    color = style.textColor,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "— ${quote.author}",
                    style = MaterialTheme.typography.titleMedium,
                    color = style.textColor.copy(alpha = 0.9f),
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "QuoteVault",
                    style = MaterialTheme.typography.labelMedium,
                    color = style.textColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun StyleOption(
    style: CardStyle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(colors = style.gradient)
            )
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                } else Modifier
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
