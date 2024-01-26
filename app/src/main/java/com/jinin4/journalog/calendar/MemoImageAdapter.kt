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
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.jinin4.journalog.R
import com.jinin4.journalog.calendar.bottom_sheet.ImageUpdatedCallback
import com.jinin4.journalog.calendar.bottom_sheet.MemoCreateBottomSheet
import com.jinin4.journalog.databinding.BottomSheetMemoCreateBinding
import com.jinin4.journalog.databinding.ItemMemoGetImageBinding
import com.jinin4.journalog.databinding.ItemMemoInsertImageBinding


// 이상원 - 24.01.23
class MemoImageAdapter(
    private val list: ArrayList<Uri>,
    private val context: Context,
//    private val callback: ImageUpdatedCallback
    ) :

    RecyclerView.Adapter<MemoImageAdapter.ViewHolder>() {

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var image: ImageView = itemView.findViewById(R.id.image)
//    }
    inner class ViewHolder(binding: ItemMemoGetImageBinding) : RecyclerView.ViewHolder(binding.root) {
        var image: ImageView = binding.ivImage
        val root = binding.root

    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemMemoGetImageBinding =
            ItemMemoGetImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUri: Uri = list[position]
        Glide.with(context)
            .load(imageUri)
            .transform(MultiTransformation(CenterCrop(), RoundedCorners(20)))
            .apply(RequestOptions().override(300, 300))
            .into(holder.image)

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    override fun getItemCount(): Int {
        return list.size
    }
}