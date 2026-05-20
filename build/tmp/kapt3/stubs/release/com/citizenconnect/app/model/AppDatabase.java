package com.citizenconnect.app.model;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&\u00a8\u0006\b"}, d2 = {"Lcom/citizenconnect/app/model/AppDatabase;", "Landroidx/room/RoomDatabase;", "()V", "complaintDao", "Lcom/citizenconnect/app/data/db/ComplaintDao;", "userDao", "Lcom/citizenconnect/app/data/db/UserDao;", "Companion", "app_release"})
@androidx.room.Database(entities = {com.citizenconnect.app.model.User.class, com.citizenconnect.app.model.Complaint.class}, version = 4, exportSchema = false)
@androidx.room.TypeConverters(value = {com.citizenconnect.app.data.db.Converters.class})
public abstract class AppDatabase extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.citizenconnect.app.model.AppDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final com.citizenconnect.app.model.AppDatabase.Companion Companion = null;
    
    public AppDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.citizenconnect.app.data.db.UserDao userDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.citizenconnect.app.data.db.ComplaintDao complaintDao();
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/citizenconnect/app/model/AppDatabase$Companion;", "", "()V", "INSTANCE", "Lcom/citizenconnect/app/model/AppDatabase;", "getInstance", "ctx", "Landroid/content/Context;", "app_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.citizenconnect.app.model.AppDatabase getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context ctx) {
            return null;
        }
    }
}