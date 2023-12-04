package com.assesment.imageuploader.utils

import com.assesment.imageuploader.dataModel.model.ImageData
import java.io.Serializable

sealed class ImageStatus : Serializable{
        data object Failure : ImageStatus()
        data object Uploaded : ImageStatus()
        data object Empty : ImageStatus()
}