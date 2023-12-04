package com.assesment.imageuploader.dataModel.model

import com.assesment.imageuploader.utils.ImageStatus
import java.io.Serializable

data class ImageData(val uri: String, val name: String, var imageStatus: ImageStatus = ImageStatus.Empty): Serializable
