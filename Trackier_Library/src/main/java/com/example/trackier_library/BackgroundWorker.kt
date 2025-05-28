package com.trackier.sdk

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.HttpException
import kotlin.Exception



class BackgroundWorker(appContext: Context, val workerParameters: WorkerParameters):
    CoroutineWorker(appContext, workerParameters) {
    private fun getWorkData(): TrackierWorkRequest? {
        val json = inputData.getString(Constants.LOG_WORK_INPUT_KEY)
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter: JsonAdapter<TrackierWorkRequest> = moshi.adapter(TrackierWorkRequest::class.java)
        if (json?.length == 0) {
            throw Exception("Invalid work data set")
        }
        return adapter.fromJson(json!!)
    }

    override suspend fun doWork(): Result {
        try {
            val workRequest = getWorkData()
            try {
                val resp = APIRepository.doWork(workRequest!!)
                if (resp?.success == true && resp.clickId != null) {
                    Util.campaignData(applicationContext, resp)
                    Log.d("background_worker_Response", "${resp}")

                }
            } catch (ex: HttpException) {
                Log.d("background_workder_Htt", "${ex}")
                return Result.retry()
            } catch (ex: Exception) {
                Log.d("background_workder_Ex", "${ex}")
                return Result.retry()
            }
            return Result.success()
        } catch (ex: Exception) {
            return Result.failure()
            Log.d("background_worker_Failed", "${ex}")

        }
    }
}