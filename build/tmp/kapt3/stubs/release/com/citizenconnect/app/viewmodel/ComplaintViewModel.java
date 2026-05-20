package com.citizenconnect.app.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\n\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\'\u001a\u00020(2\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u0002J\u000e\u0010*\u001a\u00020\u001a2\u0006\u0010+\u001a\u00020\u0010J\u0010\u0010,\u001a\u00020(2\b\u0010-\u001a\u0004\u0018\u00010\u0017J\u0010\u0010.\u001a\u00020(2\b\u0010/\u001a\u0004\u0018\u00010\u0010J\u000e\u00100\u001a\u00020(2\u0006\u00101\u001a\u00020\u0010J\u000e\u00102\u001a\u00020\u001a2\u0006\u0010+\u001a\u00020\u0010J\u0006\u00103\u001a\u000204J\u0006\u00105\u001a\u000204J\u000e\u00106\u001a\u00020(2\u0006\u00107\u001a\u00020\u0010J\u000e\u00108\u001a\u00020\u001a2\u0006\u00109\u001a\u00020\bJ\u0006\u0010:\u001a\u000204J\u0016\u0010;\u001a\u00020\u001a2\u0006\u0010+\u001a\u00020\u00102\u0006\u0010-\u001a\u00020\u0017J\u000e\u0010<\u001a\u00020(2\u0006\u0010=\u001a\u00020\u0010R\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\n\u001a\u0010\u0012\f\u0012\n \f*\u0004\u0018\u00010\u000b0\u000b0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\r\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000f0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0019\u001a\u0004\u0018\u00010\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u001b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0014R\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0014R\u000e\u0010 \u001a\u00020!X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0019\u0010#\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0014R\u001d\u0010%\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000f0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u0014\u00a8\u0006>"}, d2 = {"Lcom/citizenconnect/app/viewmodel/ComplaintViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "app", "Landroid/app/Application;", "(Landroid/app/Application;)V", "_all", "Landroidx/lifecycle/MutableLiveData;", "", "Lcom/citizenconnect/app/model/Complaint;", "_filtered", "_loading", "", "kotlin.jvm.PlatformType", "_selected", "_submit", "Lkotlin/Result;", "", "complaints", "Landroidx/lifecycle/LiveData;", "getComplaints", "()Landroidx/lifecycle/LiveData;", "currentCategory", "currentStatus", "Lcom/citizenconnect/app/model/ComplaintStatus;", "currentUserId", "filterJob", "Lkotlinx/coroutines/Job;", "filteredComplaints", "getFilteredComplaints", "loadJob", "loading", "getLoading", "repo", "Lcom/citizenconnect/app/data/repository/ComplaintRepository;", "searchQuery", "selectedComplaint", "getSelectedComplaint", "submitResult", "getSubmitResult", "applyFilters", "", "allList", "deleteById", "id", "filter", "status", "filterByCategory", "category", "load", "userId", "loadById", "pendingCount", "", "resolvedCount", "search", "query", "submit", "c", "totalCount", "updateStatus", "upvote", "complaintId", "app_release"})
public final class ComplaintViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.citizenconnect.app.data.repository.ComplaintRepository repo = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.util.List<com.citizenconnect.app.model.Complaint>> _all = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.citizenconnect.app.model.Complaint>> complaints = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.util.List<com.citizenconnect.app.model.Complaint>> _filtered = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.citizenconnect.app.model.Complaint>> filteredComplaints = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<kotlin.Result<java.lang.String>> _submit = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<kotlin.Result<java.lang.String>> submitResult = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<com.citizenconnect.app.model.Complaint> _selected = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<com.citizenconnect.app.model.Complaint> selectedComplaint = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.Boolean> _loading = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.lang.Boolean> loading = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job loadJob;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job filterJob;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String currentUserId;
    @org.jetbrains.annotations.Nullable()
    private com.citizenconnect.app.model.ComplaintStatus currentStatus;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String currentCategory;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String searchQuery = "";
    
    public ComplaintViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application app) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.citizenconnect.app.model.Complaint>> getComplaints() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.citizenconnect.app.model.Complaint>> getFilteredComplaints() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<kotlin.Result<java.lang.String>> getSubmitResult() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.citizenconnect.app.model.Complaint> getSelectedComplaint() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Boolean> getLoading() {
        return null;
    }
    
    public final void load(@org.jetbrains.annotations.NotNull()
    java.lang.String userId) {
    }
    
    public final void upvote(@org.jetbrains.annotations.NotNull()
    java.lang.String complaintId) {
    }
    
    public final void filter(@org.jetbrains.annotations.Nullable()
    com.citizenconnect.app.model.ComplaintStatus status) {
    }
    
    public final void filterByCategory(@org.jetbrains.annotations.Nullable()
    java.lang.String category) {
    }
    
    public final void search(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
    }
    
    private final void applyFilters(java.util.List<com.citizenconnect.app.model.Complaint> allList) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job submit(@org.jetbrains.annotations.NotNull()
    com.citizenconnect.app.model.Complaint c) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job loadById(@org.jetbrains.annotations.NotNull()
    java.lang.String id) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job updateStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    com.citizenconnect.app.model.ComplaintStatus status) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job deleteById(@org.jetbrains.annotations.NotNull()
    java.lang.String id) {
        return null;
    }
    
    public final int totalCount() {
        return 0;
    }
    
    public final int resolvedCount() {
        return 0;
    }
    
    public final int pendingCount() {
        return 0;
    }
}