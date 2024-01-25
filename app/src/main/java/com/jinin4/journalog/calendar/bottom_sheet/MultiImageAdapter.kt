import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var image: ImageView = itemView.findViewById(R.id.image)
//    }
    inner class ViewHolder(binding: ItemMemoInsertImageBinding) : RecyclerView.ViewHolder(binding.root) {
        var image: ImageView = binding.ivImage
        val btn_removeImage = binding.btnRemoveImage
        val root = binding.root
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemMemoInsertImageBinding =
            ItemMemoInsertImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUri: Uri = list[position]
        Glide.with(context)
            .load(imageUri)
            .into(holder.image)

        holder.btn_removeImage.setOnClickListener{
//            list.removeAt(position)
            callback.onMemoImageUpdated(position)
//            Toast.makeText(holder.root.context, "fffffff", Toast.LENGTH_SHORT).show()
        }

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    override fun getItemCount(): Int {
        return list.size
    }
}