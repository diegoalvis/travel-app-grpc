package com.diegoalvis.travelapp

import android.net.Uri
import com.diegoalvis.example.grpc.Destination
import com.diegoalvis.example.grpc.TravelServiceGrpcKt
import com.diegoalvis.example.grpc.getDestinationsRequest
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.io.Closeable

const val SERVER_URL = "http://10.0.2.2:50051/"

class TravelService : Closeable {

    private val channel = createChannel(SERVER_URL)
    private val coroutineStub = TravelServiceGrpcKt.TravelServiceCoroutineStub(channel)

    suspend fun getDestinations(): List<Destination> {
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