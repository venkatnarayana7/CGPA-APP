package com.citizenconnect.app.ui.complaints
import android.content.Intent; import android.os.Bundle; import android.text.Editable; import android.text.TextWatcher; import android.view.LayoutInflater
import androidx.activity.viewModels; import androidx.appcompat.app.AppCompatActivity
import com.citizenconnect.app.R; import com.citizenconnect.app.databinding.ActivityMyComplaintsBinding
import com.citizenconnect.app.model.ComplaintStatus
import com.citizenconnect.app.ui.adapters.ComplaintAdapter; import com.citizenconnect.app.ui.detail.ComplaintDetailActivity
import com.citizenconnect.app.utils.*; import com.citizenconnect.app.viewmodel.ComplaintViewModel
import com.google.android.material.chip.Chip
class MyComplaintsActivity : AppCompatActivity() {
    private lateinit var b: ActivityMyComplaintsBinding; private val vm: ComplaintViewModel by viewModels()
    private lateinit var adapter: ComplaintAdapter
    override fun onCreate(s: Bundle?) {
        super.onCreate(s); b = ActivityMyComplaintsBinding.inflate(layoutInflater); setContentView(b.root)
        b.toolbar.setNavigationOnClickListener { finish() }
        adapter = ComplaintAdapter(emptyList()) { c -> startActivity(Intent(this,ComplaintDetailActivity::class.java).putExtra("complaint_id",c.id)) }
        b.rvComplaints.adapter = adapter
        b.rvComplaints.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        
        setupCategoryChips()
        
        b.chipGroup.setOnCheckedStateChangeListener { _,ids ->
            val s = when { ids.contains(R.id.chipSubmitted)->ComplaintStatus.SUBMITTED; ids.contains(R.id.chipInProgress)->ComplaintStatus.IN_PROGRESS; ids.contains(R.id.chipResolved)->ComplaintStatus.RESOLVED; else->null }
            vm.filter(s)
        }
        
        b.chipGroupCategory.setOnCheckedStateChangeListener { group, ids ->
            if (ids.isEmpty()) { vm.filterByCategory(null); return@setOnCheckedStateChangeListener }
            val chip = group.findViewById<Chip>(ids.first())
            vm.filterByCategory(chip.text.toString())
        }

        b.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { vm.search(s?.toString() ?: "") }
            override fun afterTextChanged(s: Editable?) {}
        })
        val uid = SessionManager.uid(this) ?: run { finish(); return }
        // For My Complaints screen, we only want to see OUR complaints
        vm.load(uid, myComplaintsOnly = true)
        vm.loading.observe(this) { if (it) b.progressBar.visible() else b.progressBar.gone() }
        vm.filteredComplaints.observe(this) { list ->
            adapter.updateData(list)
            if (list.isEmpty()) { b.emptyState.visible(); b.rvComplaints.gone() } else { b.emptyState.gone(); b.rvComplaints.visible() }
        }
    }

    private fun setupCategoryChips() {
        val categories = resources.getStringArray(R.array.issue_categories)
        categories.forEach { cat ->
            val chip = Chip(this).apply {
                text = cat
                isCheckable = true
                setChipBackgroundColorResource(R.color.chip_filter_bg_color)
                setTextColor(getColorStateList(R.color.chip_filter_text_color))
                chipStrokeWidth = 0f
            }
            b.chipGroupCategory.addView(chip)
        }
    }
}
