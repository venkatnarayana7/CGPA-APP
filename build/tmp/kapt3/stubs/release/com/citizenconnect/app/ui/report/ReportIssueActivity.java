package com.citizenconnect.app.ui.report;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0011\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010 \u001a\u00020!H\u0002J\b\u0010\"\u001a\u00020!H\u0002J\u0012\u0010#\u001a\u00020!2\b\u0010$\u001a\u0004\u0018\u00010%H\u0014J\b\u0010&\u001a\u00020!H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0007\u001a\u0010\u0012\f\u0012\n \n*\u0004\u0018\u00010\t0\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u000b\u001a\u0010\u0012\f\u0012\n \n*\u0004\u0018\u00010\u00040\u00040\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0011\u001a\u00020\u00128BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0015\u0010\u0016\u001a\u0004\b\u0013\u0010\u0014R\u001a\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00180\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0019\u001a\u0004\u0018\u00010\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u001b\u001a\u00020\u001c8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001f\u0010\u0016\u001a\u0004\b\u001d\u0010\u001e\u00a8\u0006\'"}, d2 = {"Lcom/citizenconnect/app/ui/report/ReportIssueActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "address", "", "b", "Lcom/citizenconnect/app/databinding/ActivityReportIssueBinding;", "cameraLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroid/content/Intent;", "kotlin.jvm.PlatformType", "galleryLauncher", "imageUri", "Landroid/net/Uri;", "lat", "", "lng", "loc", "Lcom/citizenconnect/app/utils/LocationHelper;", "getLoc", "()Lcom/citizenconnect/app/utils/LocationHelper;", "loc$delegate", "Lkotlin/Lazy;", "permLauncher", "", "photoFile", "Ljava/io/File;", "vm", "Lcom/citizenconnect/app/viewmodel/ComplaintViewModel;", "getVm", "()Lcom/citizenconnect/app/viewmodel/ComplaintViewModel;", "vm$delegate", "fetchLocation", "", "launchCamera", "onCreate", "s", "Landroid/os/Bundle;", "submit", "app_release"})
public final class ReportIssueActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.citizenconnect.app.databinding.ActivityReportIssueBinding b;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy vm$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy loc$delegate = null;
    @org.jetbrains.annotations.Nullable()
    private android.net.Uri imageUri;
    @org.jetbrains.annotations.Nullable()
    private java.io.File photoFile;
    private double lat = 0.0;
    private double lng = 0.0;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String address = "";
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<android.content.Intent> cameraLauncher = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> galleryLauncher = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String[]> permLauncher = null;
    
    public ReportIssueActivity() {
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
    
    private final void launchCamera() {
    }
    
    private final void fetchLocation() {
    }
    
    private final void submit() {
    }
}