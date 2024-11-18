# Travel App gRPC
Travel App using gRPC and Compose

<p float="left">
  <img src="https://github.com/diegoalvis/travel-app-grpc/blob/main/screens/travel_app_gif.gif" width="200" />
</p>

---

### UI testing with Maestro


| <img src="https://github.com/diegoalvis/travel-app-grpc/blob/main/screens/ui_tests.gif?raw=true" width="370" />  |  <img width="348" alt="Screenshot 2024-11-18 at 3 19 54 PM" src="https://github.com/user-attachments/assets/7bdca6ed-9308-4ec9-9414-74aaf531b1f0"> |
| -- | -- |



---

### Gradle setup

```gradle
plugins {
    ...
    alias(libs.plugins.protobuf)
}

android {
   ... 
}

dependencies {
    ... 
    implementation(libs.grpcAndroid)
    implementation(libs.grpcKotlinStub)
    implementation(libs.grpcOkhttp)
    implementation(libs.grpcStub)
    implementation(libs.grpcProtobufLite)
    implementation(libs.protobufKotlinLite)    
}

protobuf {
    protoc {
        artifact = libs.protobufProtoc.get().toString()
    }
    plugins {
        create("grpc") {
            artifact = libs.grpcJava.get().toString()
        }
        create("grpckt") {
            artifact = "${libs.grpcKotlin.get()}:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
                create("java") {
                    option("lite")
                }
                create("kotlin") {
                    option("lite")
                }
            }
            it.plugins {
                create("grpc") {
                    option("lite")
                }
                create("grpckt") {
                    option("lite")
                }
            }
        }
    }
}
```

---

### gRPC Service Implementation sample


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
