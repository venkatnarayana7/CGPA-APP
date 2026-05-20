package com.citizenconnect.app.ui.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001/B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u0016\u001a\u00020\u0017\"\u0004\b\u0000\u0010\u00182\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u0002H\u00180\u001aH\u0002J\b\u0010\u001b\u001a\u00020\u0017H\u0002J\u0012\u0010\u001c\u001a\u00020\u00172\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0014J\b\u0010\u001f\u001a\u00020\u0017H\u0014J \u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020#2\u0006\u0010%\u001a\u00020&H\u0002J\b\u0010\'\u001a\u00020\u0017H\u0002J\b\u0010(\u001a\u00020\u0017H\u0002J\b\u0010)\u001a\u00020\u0017H\u0002J\b\u0010*\u001a\u00020\u0017H\u0002J\u0016\u0010+\u001a\u00020\u00172\f\u0010,\u001a\b\u0012\u0004\u0012\u00020.0-H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0007\u001a\u00020\b8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000b\u0010\f\u001a\u0004\b\t\u0010\nR\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0011\u001a\u00020\u00128BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0015\u0010\f\u001a\u0004\b\u0013\u0010\u0014\u00a8\u00060"}, d2 = {"Lcom/citizenconnect/app/ui/home/HomeActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "adapter", "Lcom/citizenconnect/app/ui/adapters/ComplaintAdapter;", "b", "Lcom/citizenconnect/app/databinding/ActivityHomeBinding;", "loc", "Lcom/citizenconnect/app/utils/LocationHelper;", "getLoc", "()Lcom/citizenconnect/app/utils/LocationHelper;", "loc$delegate", "Lkotlin/Lazy;", "mapLoaded", "", "updateJob", "Lkotlinx/coroutines/Job;", "vm", "Lcom/citizenconnect/app/viewmodel/ComplaintViewModel;", "getVm", "()Lcom/citizenconnect/app/viewmodel/ComplaintViewModel;", "vm$delegate", "go", "", "T", "cls", "Ljava/lang/Class;", "observe", "onCreate", "s", "Landroid/os/Bundle;", "onResume", "osmHtml", "", "lat", "", "lng", "zoom", "", "setupBottomNav", "setupClicks", "setupMap", "setupRecyclerView", "updateMapMarkers", "list", "", "Lcom/citizenconnect/app/model/Complaint;", "WebAppInterface", "app_debug"})
public final class HomeActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.citizenconnect.app.databinding.ActivityHomeBinding b;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy vm$delegate = null;
    private com.citizenconnect.app.ui.adapters.ComplaintAdapter adapter;
    private boolean mapLoaded = false;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job updateJob;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy loc$delegate = null;
    
    public HomeActivity() {
        super();
    }
    
    private final com.citizenconnect.app.viewmodel.ComplaintViewModel getVm() {
        return null;
    }
    
    private final com.citizenconnect.app.utils.LocationHelper getLoc() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle s) {
    }
    
    private final void setupMap() {
    }
    
    private final java.lang.String osmHtml(double lat, double lng, int zoom) {
        return null;
    }
    
    private final void setupRecyclerView() {
    }
    
    private final void setupBottomNav() {
    }
    
    private final void setupClicks() {
    }
    
    private final <T extends java.lang.Object>void go(java.lang.Class<T> cls) {
    }
    
    private final void observe() {
    }
    
    private final void updateMapMarkers(java.util.List<com.citizenconnect.app.model.Complaint> list) {
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007\u00a8\u0006\u0007"}, d2 = {"Lcom/citizenconnect/app/ui/home/HomeActivity$WebAppInterface;", "", "(Lcom/citizenconnect/app/ui/home/HomeActivity;)V", "onUpvoteClick", "", "id", "", "app_debug"})
    final class WebAppInterface {
        
        public WebAppInterface() {
            super();
        }
        
        @android.webkit.JavascriptInterface()
        public final void onUpvoteClick(@org.jetbrains.annotations.NotNull()
        java.lang.String id) {
        }
    }
}