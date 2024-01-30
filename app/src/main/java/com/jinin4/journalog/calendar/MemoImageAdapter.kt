import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.jinin4.journalog.calendar.ImageDetailActivity
import com.jinin4.journalog.databinding.ItemMemoGetImageBinding


// 이상원 - 24.01.23
class MemoImageAdapter(
    private val list: ArrayList<Uri>,
    private val context: Context,
    ) :

    RecyclerView.Adapter<MemoImageAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ItemMemoGetImageBinding) : RecyclerView.ViewHolder(binding.root) {
        var image: ImageView = binding.ivImage
        val root = binding.root

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemMemoGetImageBinding =
            ItemMemoGetImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUri: Uri = list[position]

        when (list.size) {
            1 -> glideImage(imageUri, holder,20,800,800)
            2 -> glideImage(imageUri, holder,20,400,700)
            3 -> glideImage(imageUri, holder,20,300,300)
            4-> glideImage(imageUri, holder,20,300,300)
            else -> glideImage(imageUri, holder,1,180,180)
        }

        holder.image.setOnClickListener{
            val intent = Intent(context, ImageDetailActivity::class.java)
            intent.putExtra("imageResourceId", list[position].toString())
            context.startActivity(intent)
        }
    }

    private fun glideImage(imageUri: Uri, holder: ViewHolder, radius:Int,width:Int, height:Int) {
        Glide.with(context)
            .load(imageUri)
            .transform(MultiTransformation(CenterCrop(), RoundedCorners(radius)))
            .apply(RequestOptions().override(width, height))
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}