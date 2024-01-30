import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TagMemoImagesRecyclerViewAdapter(private val photoUris: List<String>) :
    RecyclerView.Adapter<TagMemoImagesRecyclerViewAdapter.ImageViewHolder>() {
    class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val imageSize = when (getItemCount()) {
            1 -> 200
            2 -> 150
            else -> 100
        }

        val pixelSize = dpToPx(imageSize, parent.context)
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(pixelSize, pixelSize).also {
                it.setMargins(dpToPx(3, context), 0, dpToPx(3, context), 0)
            }
            scaleType = ImageView.ScaleType.CENTER_CROP

        }
        return ImageViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        val photoUri = photoUris[position]
        Glide.with(holder.imageView.context)
            .load(photoUri)
            .centerCrop()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return photoUris.size
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

}