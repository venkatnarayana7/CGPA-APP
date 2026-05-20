package com.citizenconnect.app.ui.adapters
import android.view.LayoutInflater; import android.view.ViewGroup; import androidx.recyclerview.widget.RecyclerView
import com.citizenconnect.app.databinding.ItemComplaintBinding; import com.citizenconnect.app.model.Complaint; import com.citizenconnect.app.utils.*
class ComplaintAdapter(private var data: List<Complaint>, private val onClick: (Complaint)->Unit) : RecyclerView.Adapter<ComplaintAdapter.VH>() {
    inner class VH(val b: ItemComplaintBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(p: ViewGroup, v: Int) = VH(ItemComplaintBinding.inflate(LayoutInflater.from(p.context),p,false))
    override fun getItemCount() = data.size
    override fun onBindViewHolder(h: VH, pos: Int) {
        val c=data[pos]; val s=c.getStatusEnum(); val ctx=h.itemView.context
        h.b.apply {
            tvTitle.text=c.title; tvDescription.text=c.description; tvCategory.text=c.category
            tvAddress.text=c.address.ifEmpty{ctx.getString(com.citizenconnect.app.R.string.location_not_recorded)}; tvDate.text=c.createdAt.toFormattedDate()
            tvStatus.text=s.displayName(); tvStatus.setTextColor(ctx.getColor(s.colorRes())); tvStatus.setBackgroundResource(s.bgRes())
            
            // Set Department
            tvDepartment.text = c.department

            if (c.imageUri.isNotEmpty()) layoutPhotoIndicator.visible() else layoutPhotoIndicator.gone()
            root.setOnClickListener { onClick(c) }
        }
    }
    fun updateData(d: List<Complaint>) { data=d; notifyDataSetChanged() }
}
