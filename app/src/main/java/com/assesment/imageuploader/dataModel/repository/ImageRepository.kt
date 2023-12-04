package com.assesment.imageuploader.dataModel.repository

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
import com.assesment.imageuploader.BaseApp
import com.assesment.imageuploader.dataModel.model.ImageData
import com.assesment.imageuploader.dataModel.network.ApiServiceImp
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject


class ImageRepository @Inject constructor(private val apiServiceImp: ApiServiceImp) {
    suspend fun uploadImages(imageData: ImageData): Response<JsonObject>{
        try {
            var (imageByteArray, extension) = readImageFileAsByteArray(getRealPathFromURI(imageData.uri.toUri()))
            if (extension == "jpg") {
                extension = "jpeg"
            }
            val mimeType = "image/$extension"
            val requestBody = RequestBody.create(
                mimeType.toMediaTypeOrNull(),
                imageByteArray
            )
            return apiServiceImp.uploadImage(requestBody)
        }catch (e:Exception){
            throw e
        }
    }
   private fun readImageFileAsByteArray(filePath: String?): Pair<ByteArray,String> {
        val file = filePath?.let { File(it) }
        if (file == null || !file.exists()) {
            throw IllegalArgumentException("File does not exist: $filePath")
        }

        val fileInputStream = FileInputStream(file)
        val byteBuffer = ByteArray(file.length().toInt())

        // Read the entire file into the byte buffer
        fileInputStream.read(byteBuffer)

        // Close the input stream
        fileInputStream.close()

        return Pair(byteBuffer,file.extension)
    }
    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor? = BaseApp.appContext.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }
}