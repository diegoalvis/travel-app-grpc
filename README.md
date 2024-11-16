# travel-app-grpc
Travel App using gRPC in Compose


<img src="https://github.com/diegoalvis/travel-app-grpc/blob/main/screens/travel_app_gif.gif?raw=true" width="270" />


---

```kotlin
class TravelService : Closeable {

    private val channel = createChannel(SERVER_URL)
    private val coroutineStub = TravelServiceGrpcKt.TravelServiceCoroutineStub(channel)

    suspend fun getDestinations(location: String): List<Destination> {
        try {
            val request = getDestinationsRequest { }
            return coroutineStub.getDestinations(request).destinationsList
        } catch (e: Exception) {
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
```
