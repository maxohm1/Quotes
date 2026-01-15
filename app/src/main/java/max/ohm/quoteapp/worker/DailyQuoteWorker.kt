package max.ohm.quoteapp.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import max.ohm.quoteapp.MainActivity
import max.ohm.quoteapp.R
import max.ohm.quoteapp.domain.repository.QuoteRepository
import max.ohm.quoteapp.util.Constants
import max.ohm.quoteapp.util.Resource

@HiltWorker
class DailyQuoteWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val quoteRepository: QuoteRepository
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            // Get daily quote
            when (val result = quoteRepository.getDailyQuote()) {
                is Resource.Success -> {
                    result.data?.let { quote ->
                        showNotification(quote.text, quote.author)
                    }
                    Result.success()
                }
                is Resource.Error -> {
                    Result.retry()
                }
                else -> Result.success()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private fun showNotification(quoteText: String, author: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("✨ Your Daily Inspiration")
            .setContentText("\"$quoteText\" — $author")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("\"$quoteText\"\n\n— $author"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Constants.NOTIFICATION_ID, notification)
    }
}
