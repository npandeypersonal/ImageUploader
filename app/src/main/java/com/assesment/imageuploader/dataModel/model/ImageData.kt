package com.assesment.imageuploader.dataModel.model

import android.net.Uri
import com.assesment.imageuploader.utils.ImageStatus
import java.io.Serializable

data class ImageData(val uri: String, val name: String, var imageStatus: ImageStatus = ImageStatus.Empty): Serializable
