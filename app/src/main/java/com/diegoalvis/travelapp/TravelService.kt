package com.diegoalvis.travelapp

import android.content.Context
import android.net.Uri
import com.diegoalvis.example.grpc.Destination
import com.diegoalvis.example.grpc.TravelServiceGrpcKt
import com.diegoalvis.example.grpc.getDestinationsRequest
import com.diegoalvis.travelapp.utils.getDestinationsMockData
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.io.Closeable

const val IS_MOCK_ENABLED = true
const val SERVER_URL = "http://10.0.2.2:50051/"

class TravelService(private val context: Context) : Closeable {

    private val channel = createChannel(SERVER_URL)
    private val coroutineStub = TravelServiceGrpcKt.TravelServiceCoroutineStub(channel)

    suspend fun getDestinations(): List<Destination> {
        return if (IS_MOCK_ENABLED) {
            getDestinationsMockData(context)
        } else {
            try {
                val request = getDestinationsRequest {
                    timestamp = System.currentTimeMillis()
                    // location = "Portugal"
                }
                return coroutineStub.getDestinations(request).destinationsList
            } catch (e: Exception) {
                // TODO("Not yet implemented")
                throw e
            }
        }
    }

    override fun close() {
        channel.shutdownNow()
    }

}


fun createChannel(serverUrl: String): ManagedChannel {
    val uri = Uri.parse(serverUrl)
    val builder = ManagedChannelBuilder.forAddress(uri.host, uri.port)
    if (uri.scheme == "https") {
        builder.useTransportSecurity()
    } else {
        builder.usePlaintext()
    }
    return builder
        .intercept(LoggingInterceptor())
        .executor(Dispatchers.IO.asExecutor())
        .build()
}