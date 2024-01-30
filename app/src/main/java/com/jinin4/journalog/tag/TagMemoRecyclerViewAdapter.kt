package com.jinin4.journalog.tag

import TagMemoImagesRecyclerViewAdapter
import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jinin4.journalog.MemoRightColorSetting
import com.jinin4.journalog.R
import com.jinin4.journalog.calendar.bottom_sheet.MemoCreateBottomSheet
import com.jinin4.journalog.databinding.ItemTagMemoBinding
import com.jinin4.journalog.db.memo.MemoEntity
import com.jinin4.journalog.db.photo.PhotoEntity
import com.jinin4.journalog.utils.FontUtils
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

//최성혁 작성 - 24.01.22
class TagMemoRecyclerViewAdapter(private val memoList: ArrayList<MemoEntity>,
                                 private val photos: Map<Int, List<PhotoEntity>>,
                                 private val tag:TagFragment) :
    RecyclerView.Adapter<TagMemoRecyclerViewAdapter.TagMemoViewHolder>() {

    inner class TagMemoViewHolder(binding: ItemTagMemoBinding) : RecyclerView.ViewHolder(binding.root) {
        val root = binding.root
        val memoContent: TextView = binding.tagviewMemoContent

        val memoDate_RecycleView: TextView = binding.tagviewMemoDate
        val memoDate_Container: TextView = binding.tagviewMemoDateImageContainer

        val cltag_ = binding.clTag

        val imageRecyclerView: RecyclerView = binding.imageRecyclerView
        val imageContainer: LinearLayout = binding.imageContainer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagMemoViewHolder {
        val binding: ItemTagMemoBinding = ItemTagMemoBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return TagMemoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return memoList.size
    }

    override fun onBindViewHolder(holder: TagMemoViewHolder, position: Int) {
        val memo = memoList[position]
        val photoEntities = photos[memo.memo_id ?: 0] ?: emptyList()

        val photoUris = photoEntities.map { it.photo_uri }

        val layerDrawable = ContextCompat.getDrawable(
            holder.root.context,
            R.drawable.rounded_textview_background_content
        ) as LayerDrawable
        val memoColor = when (memo.color_id) {
            1 -> R.color.red_sw
            2 -> R.color.orange_sw
            3 -> R.color.yellow_sw
            4 -> R.color.green_sw
            5 -> R.color.blue_sw
            6 -> R.color.navi_sw
            7 -> R.color.purple_sw
            else -> R.color.red_sw
        }

        val context = holder.itemView.context

        GlobalScope.launch {
            val typeface = FontUtils.getFontType(context)
            val fontSize = FontUtils.getFontSize(context)
            withContext(Dispatchers.Main) {
                FontUtils.applyFont(holder.itemView, typeface,fontSize)
            }
        }
        holder.cltag_.background =
            MemoRightColorSetting.changeRightColor(
                ContextCompat.getColor(holder.root.context, memoColor), layerDrawable
            )
        holder.memoContent.text = memo.content

        holder.memoDate_RecycleView.text = memo.timestamp
        holder.memoDate_Container.text = memo.timestamp

        // 메모의 텍스트 내용과 날짜 설정
        holder.memoContent.text = memo.content

        holder.memoDate_RecycleView.text = memo.timestamp
        holder.memoDate_Container.text = memo.timestamp


        // 사진이 2개 이하일 경우 사진을 LinearLayout에 추가
        if (photoUris.size == 1) {

            holder.imageRecyclerView.visibility = View.GONE
            holder.imageContainer.visibility = View.VISIBLE

            holder.memoDate_RecycleView.visibility = View.GONE
            holder.memoDate_Container.visibility = View.VISIBLE

            // LinearLayout에서 모든 뷰를 제거
            holder.imageContainer.removeAllViews()

            val originalPixelSize = dpToPx(100, holder.itemView.context)
            val pixelSize = (originalPixelSize * 1.8).toInt()
            val marginSize = dpToPx(4,holder.itemView.context)

            photoUris.forEach { photoUrl ->
                val imageView = ImageView(holder.itemView.context).apply {
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixelSize).also {
                        it.setMargins(marginSize,0,marginSize,0)
                        it.marginEnd = marginSize * 2 // 오른쪽 마진을 늘려서 왼쪽으로 이동효과를 줌
                    }
                    // 이미지 로드
                    Glide.with(holder.itemView.context)
                        .load(photoUrl)
                        .transform(CenterCrop(), RoundedCorners(20)) // 둥근 모서리
                        .into(this)
                }
                // 이미지 뷰를 LinearLayout에 추가
                holder.imageContainer.addView(imageView)
            }
        }else if (photoUris.size == 2) {
            holder.imageRecyclerView.visibility = View.GONE
            holder.imageContainer.visibility = View.VISIBLE

            holder.memoDate_RecycleView.visibility = View.GONE
            holder.memoDate_Container.visibility = View.VISIBLE

            holder.imageContainer.removeAllViews() // 이미지 컨테이너 초기화

            val marginSize = dpToPx(4,holder.itemView.context)
            val imageWidth = (holder.imageContainer.width / 2) - (marginSize * 2)
            val imageHeight = dpToPx(100, holder.itemView.context) // 원하는 높이 값
            val imageRealHeight = (imageHeight * 1.9).toInt()

            photoUris.forEach { photoUrl ->
                val imageView = ImageView(holder.itemView.context).apply {
                    layoutParams = LinearLayout.LayoutParams(imageWidth, imageRealHeight,1f).also {
                        it.setMargins(marginSize, 0, marginSize, 0)
                        it.marginEnd = marginSize * 2
                    }
                    scaleType = ImageView.ScaleType.CENTER_CROP

                    Glide.with(holder.itemView.context)
                        .load(photoUrl)
                        .transform(CenterCrop(), RoundedCorners(20))
                        .into(this)
                }
                // 이미지 뷰를 LinearLayout에 추가
                holder.imageContainer.addView(imageView)
            }
        }else {
            // 사진이 세 장 이상일 때
            holder.imageRecyclerView.visibility = View.VISIBLE
            holder.imageContainer.visibility = View.GONE

            holder.memoDate_RecycleView.visibility = View.VISIBLE
            holder.memoDate_Container.visibility = View.GONE

            val imageAdapter = TagMemoImagesRecyclerViewAdapter(photoUris)
            holder.imageRecyclerView.adapter = imageAdapter
            holder.imageRecyclerView.layoutManager =
                LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        }
        holder.cltag_.setOnClickListener({showMemoEditDialog(holder,memo)})
    }

    // dp(밀도 독립적 픽셀)를 픽셀로 변환하는 도우미 함수
    private fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    fun showMemoEditDialog(holder: TagMemoViewHolder, memoEntity: MemoEntity) {

        val memoTimeSplit = memoEntity.timestamp.split(" ")  //날짜 부분 시간 부분 분할
        // 날짜 부분
        val datePart = memoTimeSplit[0]

        // 시간 부분
        val timePartRaw = memoTimeSplit[1]
        val timeParts = timePartRaw.split(":")
        val timePart = "${timeParts[0]}시 ${timeParts[1]}분"

        val injectionMemo = MemoEntity(memoEntity.memo_id,memoEntity.content,timePart,memoEntity.color_id)
        val modal = MemoCreateBottomSheet(toCalendarDay(datePart)!!, tag, injectionMemo)
        modal.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBottomSheetDialogTheme)
        modal.setTargetFragment(tag, 1)
        modal.show(
            tag.requireActivity().supportFragmentManager,
            MemoCreateBottomSheet.TAG
        )
    }

    fun toCalendarDay(dateString: String?): CalendarDay? {
        return dateString?.let {
            val localDate = LocalDate.parse(it, DateTimeFormatter.ISO_DATE)
            CalendarDay.from(localDate)
        }
    }

}