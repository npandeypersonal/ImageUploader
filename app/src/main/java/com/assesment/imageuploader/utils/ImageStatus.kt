package com.assesment.imageuploader.utils

import java.io.Serializable

sealed class ImageStatus : Serializable{
        data object Uploading : ImageStatus()
        data object Failure : ImageStatus()
        data object Uploaded : ImageStatus()
        data object Empty : ImageStatus()
}