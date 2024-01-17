import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.BirdImage


data class BirdsUiState(
    val images: List<BirdImage> = emptyList(),
    val selectedCategory: String? = null
){
    val categories = images.map { it.category }.toSet()
    val selectedImage = images.filter { it.category == selectedCategory }
}
class BirdsViewModel: ViewModel() {
    private val _uiState = MutableStateFlow<BirdsUiState>(BirdsUiState())
    var uiState = _uiState.asStateFlow()


    init {
        updateImages()
    }

    override fun onCleared() {
        httpClient.close()
    }
    fun updateImages(){
        viewModelScope.launch {
           val images = getImage()
            _uiState.update {
                it.copy(images = images)
            }
        }
    }

    fun selectCategory(category: String){
        _uiState.update {
            it.copy(selectedCategory = category)
        }
    }

    private val httpClient  = HttpClient(CIO) {
        install(ContentNegotiation){
            json()
        }
    }

    private suspend fun getImage(): List<BirdImage>{
        val images = httpClient.get("https://sebi.io/demo-image-api/pictures.json")
            .body<List<BirdImage>>()

        return images
    }



}