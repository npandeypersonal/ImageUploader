package com.assesment.imageuploader.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assesment.imageuploader.dataModel.model.ImageData
import com.assesment.imageuploader.dataModel.repository.ImageRepository
import com.assesment.imageuploader.utils.ImageStatus
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(private val imageRepository: ImageRepository) : ViewModel() {
    private val _selectedImages = MutableLiveData<List<ImageData>>()
    val selectedImages: LiveData<List<ImageData>> get() = _selectedImages
    private val _uploadProgress = MutableLiveData<Int>()
    val uploadProgress: LiveData<Int> get() = _uploadProgress
    private val allList = mutableListOf<ImageData>()

    fun addImages(list: MutableList<ImageData>){
        allList.clear()
        selectedImages.value?.let {
            allList.addAll(selectedImages.value as MutableList<ImageData>)
        }
        allList.addAll(list)
        _selectedImages.value = allList
    }
    fun uploadImages(imageList: List<ImageData>){
            var filesUploaded = 0
            val updatedList = mutableListOf<ImageData>()
            viewModelScope.launch {
                coroutineScope { // This will return only when all child coroutines have finished
                    imageList.forEach { imageData ->
                        if (imageData.imageStatus ==ImageStatus.Uploaded){
                            updatedList.add(imageData.copy(imageStatus = ImageStatus.Uploaded))
                            return@forEach
                        }
                        launch {
                            try {
                               val response: Response<JsonObject> = imageRepository.uploadImages(imageData)
                                if(response.code() == 200){
                                    updatedList.add(imageData.copy(imageStatus = ImageStatus.Uploaded))
                                }else{
                                    updatedList.add(imageData.copy(imageStatus = ImageStatus.Failure))
                                }

                                filesUploaded++
                               _uploadProgress.value = filesUploaded
                            }catch (exception: Exception) {
                                filesUploaded++
                                updatedList.add(imageData.copy(imageStatus = ImageStatus.Failure))
                                _uploadProgress.value = filesUploaded
                                Log.d("ImageViewModel", "$exception handled !")
                            }
                        }

                    }
                }
                // All images have been uploaded at this point.
                _selectedImages.value = updatedList
            }

    }
}