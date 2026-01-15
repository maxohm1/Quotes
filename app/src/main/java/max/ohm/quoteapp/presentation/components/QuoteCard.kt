package max.ohm.quoteapp.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import max.ohm.quoteapp.domain.model.Quote
import max.ohm.quoteapp.domain.model.QuoteCategory
import max.ohm.quoteapp.ui.theme.GradientBlue
import max.ohm.quoteapp.ui.theme.GradientForest
import max.ohm.quoteapp.ui.theme.GradientOcean
import max.ohm.quoteapp.ui.theme.GradientPurple
import max.ohm.quoteapp.ui.theme.GradientSunset
import max.ohm.quoteapp.ui.theme.HumorColor
import max.ohm.quoteapp.ui.theme.LoveColor
import max.ohm.quoteapp.ui.theme.MotivationColor
import max.ohm.quoteapp.ui.theme.SuccessColor
import max.ohm.quoteapp.ui.theme.WisdomColor
import max.ohm.quoteapp.ui.theme.LifeColor
import max.ohm.quoteapp.ui.theme.HappinessColor
import max.ohm.quoteapp.ui.theme.FriendshipColor
import max.ohm.quoteapp.ui.theme.LeadershipColor
import max.ohm.quoteapp.ui.theme.CreativityColor
import max.ohm.quoteapp.ui.theme.CourageColor

@Composable
fun QuoteCard(
    quote: Quote,
    modifier: Modifier = Modifier,
    onFavoriteClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onAddToCollectionClick: () -> Unit = {},
    onClick: () -> Unit = {},
    fontSizeScale: Float = 1f
) {
    val favoriteScale by animateFloatAsState(
        targetValue = if (quote.isFavorite) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "favoriteScale"
    )
    
    val favoriteColor by animateColorAsState(
        targetValue = if (quote.isFavorite) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "favoriteColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Category chip
            CategoryChip(category = quote.category)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Quote text
            Text(
                text = "\"${quote.text}\"",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = (18 * fontSizeScale).sp,
                    lineHeight = (28 * fontSizeScale).sp,
                    fontStyle = FontStyle.Italic
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Author
            Text(
                text = "— ${quote.author}",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onAddToCollectionClick) {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkAdd,
                        contentDescription = "Add to collection",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = onShareClick) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.scale(favoriteScale)
                ) {
                    Icon(
                        imageVector = if (quote.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (quote.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = favoriteColor
                    )
                }
            }
        }
    }
}

@Composable
fun DailyQuoteCard(
    quote: Quote,
    modifier: Modifier = Modifier,
    onFavoriteClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onClick: () -> Unit = {},
    fontSizeScale: Float = 1f
) {
    val gradient = when (quote.category) {
        QuoteCategory.MOTIVATION -> GradientSunset
        QuoteCategory.LOVE -> GradientPurple
        QuoteCategory.SUCCESS -> GradientForest
        QuoteCategory.WISDOM -> GradientOcean
        QuoteCategory.HUMOR -> GradientBlue
        QuoteCategory.LIFE -> GradientOcean
        QuoteCategory.HAPPINESS -> GradientSunset
        QuoteCategory.FRIENDSHIP -> GradientPurple
        QuoteCategory.LEADERSHIP -> GradientBlue
        QuoteCategory.CREATIVITY -> GradientPurple
        QuoteCategory.COURAGE -> GradientSunset
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(colors = gradient)
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✨ Quote of the Day",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    
                    Row {
                        IconButton(onClick = onShareClick) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = onFavoriteClick) {
                            Icon(
                                imageVector = if (quote.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (quote.isFavorite) Color(0xFFFFCDD2) else Color.White
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "\"${quote.text}\"",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = (22 * fontSizeScale).sp,
                        lineHeight = (32 * fontSizeScale).sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "— ${quote.author}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: QuoteCategory,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val color = when (category) {
        QuoteCategory.MOTIVATION -> MotivationColor
        QuoteCategory.LOVE -> LoveColor
        QuoteCategory.SUCCESS -> SuccessColor
        QuoteCategory.WISDOM -> WisdomColor
        QuoteCategory.HUMOR -> HumorColor
        QuoteCategory.LIFE -> LifeColor
        QuoteCategory.HAPPINESS -> HappinessColor
        QuoteCategory.FRIENDSHIP -> FriendshipColor
        QuoteCategory.LEADERSHIP -> LeadershipColor
        QuoteCategory.CREATIVITY -> CreativityColor
        QuoteCategory.COURAGE -> CourageColor
    }
    
    Surface(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = category.displayName,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CategoryCard(
    category: QuoteCategory,
    quoteCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val color = when (category) {
        QuoteCategory.MOTIVATION -> MotivationColor
        QuoteCategory.LOVE -> LoveColor
        QuoteCategory.SUCCESS -> SuccessColor
        QuoteCategory.WISDOM -> WisdomColor
        QuoteCategory.HUMOR -> HumorColor
        QuoteCategory.LIFE -> LifeColor
        QuoteCategory.HAPPINESS -> HappinessColor
        QuoteCategory.FRIENDSHIP -> FriendshipColor
        QuoteCategory.LEADERSHIP -> LeadershipColor
        QuoteCategory.CREATIVITY -> CreativityColor
        QuoteCategory.COURAGE -> CourageColor
    }
    
    val emoji = when (category) {
        QuoteCategory.MOTIVATION -> "🔥"
        QuoteCategory.LOVE -> "💕"
        QuoteCategory.SUCCESS -> "🏆"
        QuoteCategory.WISDOM -> "🦉"
        QuoteCategory.HUMOR -> "😄"
        QuoteCategory.LIFE -> "🌱"
        QuoteCategory.HAPPINESS -> "😊"
        QuoteCategory.FRIENDSHIP -> "🤝"
        QuoteCategory.LEADERSHIP -> "👑"
        QuoteCategory.CREATIVITY -> "🎨"
        QuoteCategory.COURAGE -> "💪"
    }
    
    Card(
        modifier = modifier
            .width(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = emoji,
                fontSize = 28.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "$quoteCount quotes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
