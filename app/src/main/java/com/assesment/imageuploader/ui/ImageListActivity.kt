package com.assesment.imageuploader.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assesment.imageuploader.R
import com.assesment.imageuploader.dataModel.model.ImageData
import com.assesment.imageuploader.utils.ImageStatus
import com.assesment.imageuploader.viewModel.ImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.Serializable


@AndroidEntryPoint
class ImageListActivity : AppCompatActivity() {
    private lateinit var imageAdapter: ImageAdapter

    private val imageViewModel:ImageViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerview()
        val button: Button = findViewById(R.id.button)
        val uploadButton: Button = findViewById(R.id.uploadButton)
        button.setOnClickListener{
            launcher.launch(
                PickVisualMediaRequest(
                    mediaType = ImageOnly
                )
            )
        }
        uploadButton.setOnClickListener{
            val currentList = imageAdapter.getData()
            val editedList = currentList.map { element ->
                if (element.imageStatus == ImageStatus.Empty) {
                    element.copy(imageStatus = ImageStatus.Uploading)
                } else {
                    element
                }
            }
            refreshAdapterView(editedList)
            uploadViewModel = imageViewModel
            val serviceIntent = Intent(this, UploadService::class.java).apply {
                putExtra(
                    UploadService.EXTRA_IMAGE_PATHS,
                    imageViewModel.selectedImages.value as Serializable
                )
            }
            startForegroundService(serviceIntent)

        }
        observeViewModel()
    }

    private fun refreshAdapterView(currentList: List<ImageData>) {
        imageAdapter.setData(currentList)
        imageAdapter.notifyDataSetChanged()
    }

    private fun observeViewModel() {
            imageViewModel.selectedImages.observe(this) {
                        refreshAdapterView(it)
            }
            imageViewModel.uploadProgress.observe(this){data->
                val currentList = imageViewModel.selectedImages.value as MutableList<ImageData>
                val index = currentList.indexOfFirst { it.name == data.second.name && it.uri == data.second.uri }
                 currentList[index] = data.second
                refreshAdapterView(currentList)
            }
    }

    private fun initRecyclerview() {
        imageAdapter = ImageAdapter(emptyList())
        val recyclerView: RecyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager = GridLayoutManager(this@ImageListActivity, 2)
        recyclerView.adapter = imageAdapter
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { list ->
        if (list == null) {
            Toast.makeText(this@ImageListActivity, "No image Selected", Toast.LENGTH_SHORT)
                .show()
        } else {
            val newImages = mutableListOf<ImageData>()
            list.forEach{uri->
                val imageFile = File(uri.path!!).name
                if(imageViewModel.selectedImages.value?.firstOrNull { it.name == imageFile && it.uri == uri.toString() } == null) {
                    newImages.add(ImageData(uri = uri.toString(), name = imageFile))
                }
            }
            imageViewModel.addImages(newImages)
        }
    }
    companion object{
        lateinit  var uploadViewModel: ImageViewModel
    }
}