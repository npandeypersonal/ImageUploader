package com.assesment.imageuploader.ui
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.assesment.imageuploader.R
import com.assesment.imageuploader.dataModel.model.ImageData
import com.assesment.imageuploader.utils.ImageStatus


class ImageAdapter(private var list: List<ImageData>) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =  LayoutInflater.from(parent.context).inflate(R.layout.custom_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageURI(list[position].uri.toUri())
        when(list[position].imageStatus){
            is ImageStatus.Uploading->{
                holder.progressBar.visibility = View.VISIBLE
            }
            is ImageStatus.Uploaded->{
                holder.tickView.visibility = View.VISIBLE
                holder.progressBar.visibility = View.GONE
                holder.tickView.setImageResource(R.drawable.done24px)
            }
            is ImageStatus.Failure->{
                holder.tickView.visibility = View.VISIBLE
                holder.progressBar.visibility = View.GONE
                holder.tickView.setImageResource(R.drawable.close24px)
            }
            else->{
                holder.progressBar.visibility = View.GONE
                holder.tickView.visibility = View.INVISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var tickView: ImageView
        var progressBar: ProgressBar

        init {
            imageView = itemView.findViewById(R.id.imageView)
            tickView = itemView.findViewById(R.id.tickView)
            progressBar = itemView.findViewById(R.id.progressBar)
        }
    }
    fun setData(postList: List<ImageData>)
    {
        this.list =postList
    }
    fun getData():List<ImageData>
    {
        return this.list
    }
}