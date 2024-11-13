package com.diegoalvis.travelapp.utils

import android.content.Context
import com.diegoalvis.example.grpc.Destination
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun ReadJSONFromAssets(context: Context, path: String): String {
    try {
        val bufferedReader = context.assets.open(path).bufferedReader()
        val stringBuilder = StringBuilder()
        bufferedReader.useLines { lines ->
            lines.forEach {
                stringBuilder.append(it)
            }
        }
        val jsonString = stringBuilder.toString()
        return jsonString
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
}

suspend fun getDestinationsMockData(context: Context): List<Destination> {
    return withContext(Dispatchers.IO) {
        try {
            val json = ReadJSONFromAssets(context, "mock_destinations.json")
            val collectionType = object : TypeToken<List<Destination>>() {}.type
            Gson().fromJson(json, collectionType)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}