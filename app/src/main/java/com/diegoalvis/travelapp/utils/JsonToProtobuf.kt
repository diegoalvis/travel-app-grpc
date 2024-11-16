
import com.diegoalvis.example.grpc.Destination
import com.diegoalvis.example.grpc.FoodAndDrink
import com.diegoalvis.example.grpc.ThingToDo
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


fun parseJsonToDataClass(jsonString: String): List<DestinationData> {
    val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    val type = Types.newParameterizedType(List::class.java, DestinationData::class.java)
    val jsonAdapter = moshi.adapter<List<DestinationData>>(type)

    return jsonAdapter.fromJson(jsonString) ?: emptyList()
}

fun convertToProtobuf(destinations: List<DestinationData>): List<Destination> {
    return destinations.map { dest ->
        Destination.newBuilder().apply {
            id = dest.id
            title = dest.title
            description = dest.description
            imageUrl = dest.imageUrl
            country = dest.country
            region = dest.region
            addAllThingsToDo(dest.thingsToDo.map { thing ->
                ThingToDo.newBuilder().apply {
                    id = thing.id
                    title = thing.title
                    description = thing.description
                    imageUrl = thing.imageUrl
                    reviewsCount = thing.reviewsCount
                    score = thing.score
                }.build()
            })
            addAllFoodAndDrinks(dest.foodAndDrinks.map { food ->
                FoodAndDrink.newBuilder().apply {
                    id = food.id
                    name = food.name
                    imageUrl = food.imageUrl
                }.build()
            })
        }.build()
    }
}

fun jsonToProtobuf(jsonString: String): List<Destination> {
    val dataClasses = parseJsonToDataClass(jsonString)
    return convertToProtobuf(dataClasses)
}


data class DestinationData(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val country: String,
    val region: String,
    val thingsToDo: List<ThingToDoData>,
    val foodAndDrinks: List<FoodAndDrinkData>
)

data class ThingToDoData(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val reviewsCount: Int,
    val score: Float
)

data class FoodAndDrinkData(
    val id: String,
    val name: String,
    val imageUrl: String
)