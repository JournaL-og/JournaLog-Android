import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.jinin4.journalog.R
import com.jinin4.journalog.calendar.bottom_sheet.ImageUpdatedCallback
import com.jinin4.journalog.calendar.bottom_sheet.MemoCreateBottomSheet
import com.jinin4.journalog.databinding.BottomSheetMemoCreateBinding
import com.jinin4.journalog.databinding.ItemMemoInsertImageBinding


// 이상원 - 24.01.23
class MultiImageAdapter(
    private val list: ArrayList<Uri>,
    private val context: Context,
    private val callback: ImageUpdatedCallback
    ) :

    RecyclerView.Adapter<MultiImageAdapter.ViewHolder>() {


    inner class ViewHolder(binding: ItemMemoInsertImageBinding) : RecyclerView.ViewHolder(binding.root) {
        var image: ImageView = binding.ivImage
        val btn_removeImage = binding.btnRemoveImage
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemMemoInsertImageBinding =
            ItemMemoInsertImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUri: Uri = list[position]
        Glide.with(context)
            .load(imageUri)
            .transform(MultiTransformation(CenterCrop(), RoundedCorners(20)))
            .into(holder.image)

        holder.btn_removeImage.setOnClickListener{
            callback.onMemoImageUpdated(position)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}