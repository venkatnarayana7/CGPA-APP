package com.citizenconnect.app.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.citizenconnect.app.data.repository.ComplaintRepository
import com.citizenconnect.app.model.Complaint
import com.citizenconnect.app.model.ComplaintStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch

class ComplaintViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ComplaintRepository(app)
    
    private val _all      = MutableLiveData<List<Complaint>>(emptyList()); val complaints: LiveData<List<Complaint>> = _all
    private val _filtered = MutableLiveData<List<Complaint>>(emptyList()); val filteredComplaints: LiveData<List<Complaint>> = _filtered
    
    private val _submit   = MutableLiveData<Result<String>>();             val submitResult: LiveData<Result<String>> = _submit
    private val _selected = MutableLiveData<Complaint?>();                 val selectedComplaint: LiveData<Complaint?> = _selected
    private val _loading  = MutableLiveData(false);                        val loading: LiveData<Boolean> = _loading
    
    private var loadJob: Job? = null
    private var filterJob: Job? = null
    private var currentUserId: String? = null
    
    private var currentStatus: ComplaintStatus? = null
    private var currentCategory: String? = null
    private var searchQuery: String = ""
    private var isMyComplaintsOnly: Boolean = false

    fun load(userId: String, myComplaintsOnly: Boolean = false) {
        // Force refresh the flag and data whenever load is called
        isMyComplaintsOnly = myComplaintsOnly
        currentUserId = userId
        
        loadJob?.cancel()
        repo.syncAllFromCloud()
        
        loadJob = viewModelScope.launch {
            repo.getAllComplaints().catch { }.collect { list -> 
                _all.value = list
                applyFilters(list)
            }
        }
    }

    fun upvote(complaintId: String) {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            repo.upvote(complaintId, uid)
        }
    }

    fun filter(status: ComplaintStatus?) {
        currentStatus = status
        applyFilters(_all.value ?: emptyList())
    }

    fun filterByCategory(category: String?) {
        currentCategory = if (category == "Any Category") null else category
        applyFilters(_all.value ?: emptyList())
    }

    fun search(query: String) {
        searchQuery = query.trim()
        applyFilters(_all.value ?: emptyList())
    }

    private fun applyFilters(allList: List<Complaint>) {
        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            val uid = currentUserId
            
            val result = withContext(Dispatchers.Default) {
                var list = if (isMyComplaintsOnly) {
                    allList.filter { it.userId == uid }
                } else {
                    allList
                }
                
                currentStatus?.let { status ->
                    list = if (status == ComplaintStatus.IN_PROGRESS) {
                        list.filter { it.getStatusEnum() != ComplaintStatus.RESOLVED }
                    } else {
                        list.filter { it.getStatusEnum() == status }
                    }
                }
                
                currentCategory?.let { category ->
                    list = list.filter { it.category == category }
                }
                
                if (searchQuery.isNotEmpty()) {
                    list = list.filter { 
                        it.title.contains(searchQuery, ignoreCase = true) || 
                        it.category.contains(searchQuery, ignoreCase = true) || 
                        it.description.contains(searchQuery, ignoreCase = true) 
                    }
                }
                list
            }
            _filtered.value = result
        }
    }

    fun submit(c: Complaint) = viewModelScope.launch { 
        _loading.value = true
        val res = repo.submit(c)
        _submit.value = res
        _loading.value = false 
    }
    
    fun loadById(id: String) = viewModelScope.launch { _selected.value = repo.getById(id) }
    
    fun updateStatus(id: String, status: ComplaintStatus) = viewModelScope.launch {
        repo.updateStatus(id, status.name)
        loadById(id)
    }
    
    fun deleteById(id: String) = viewModelScope.launch { repo.deleteById(id) }

    fun totalCount()    = _all.value?.count { it.userId == currentUserId } ?: 0
    fun resolvedCount() = _all.value?.count { it.userId == currentUserId && it.getStatusEnum() == ComplaintStatus.RESOLVED } ?: 0
    fun pendingCount()  = _all.value?.count { it.userId == currentUserId && it.getStatusEnum() != ComplaintStatus.RESOLVED } ?: 0
}
