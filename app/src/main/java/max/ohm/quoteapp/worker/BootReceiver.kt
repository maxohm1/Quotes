package max.ohm.quoteapp.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import max.ohm.quoteapp.util.Constants
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule daily quote notification
            scheduleDailyQuoteWork(context)
        }
    }
    
    companion object {
        fun scheduleDailyQuoteWork(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<DailyQuoteWorker>(
                1, TimeUnit.DAYS
            ).build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                Constants.DAILY_QUOTE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
