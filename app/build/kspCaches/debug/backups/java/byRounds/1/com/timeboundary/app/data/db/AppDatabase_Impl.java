package com.timeboundary.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile MonitoredAppDao _monitoredAppDao;

  private volatile AppUsageStatDao _appUsageStatDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `monitored_apps` (`packageName` TEXT NOT NULL, `appLabel` TEXT NOT NULL, `durationMinutes` INTEGER NOT NULL, `isEnabled` INTEGER NOT NULL, `lastSessionStartTime` INTEGER NOT NULL, `activeSessionId` INTEGER NOT NULL, PRIMARY KEY(`packageName`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `app_usage_stats` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `packageName` TEXT NOT NULL, `dateString` TEXT NOT NULL, `openCount` INTEGER NOT NULL, `limitExceededCount` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '0417e5161050d2f5a83c544dea65c321')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `monitored_apps`");
        db.execSQL("DROP TABLE IF EXISTS `app_usage_stats`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsMonitoredApps = new HashMap<String, TableInfo.Column>(6);
        _columnsMonitoredApps.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonitoredApps.put("appLabel", new TableInfo.Column("appLabel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonitoredApps.put("durationMinutes", new TableInfo.Column("durationMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonitoredApps.put("isEnabled", new TableInfo.Column("isEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonitoredApps.put("lastSessionStartTime", new TableInfo.Column("lastSessionStartTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonitoredApps.put("activeSessionId", new TableInfo.Column("activeSessionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMonitoredApps = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMonitoredApps = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMonitoredApps = new TableInfo("monitored_apps", _columnsMonitoredApps, _foreignKeysMonitoredApps, _indicesMonitoredApps);
        final TableInfo _existingMonitoredApps = TableInfo.read(db, "monitored_apps");
        if (!_infoMonitoredApps.equals(_existingMonitoredApps)) {
          return new RoomOpenHelper.ValidationResult(false, "monitored_apps(com.timeboundary.app.data.model.MonitoredApp).\n"
                  + " Expected:\n" + _infoMonitoredApps + "\n"
                  + " Found:\n" + _existingMonitoredApps);
        }
        final HashMap<String, TableInfo.Column> _columnsAppUsageStats = new HashMap<String, TableInfo.Column>(5);
        _columnsAppUsageStats.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppUsageStats.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppUsageStats.put("dateString", new TableInfo.Column("dateString", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppUsageStats.put("openCount", new TableInfo.Column("openCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppUsageStats.put("limitExceededCount", new TableInfo.Column("limitExceededCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAppUsageStats = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAppUsageStats = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAppUsageStats = new TableInfo("app_usage_stats", _columnsAppUsageStats, _foreignKeysAppUsageStats, _indicesAppUsageStats);
        final TableInfo _existingAppUsageStats = TableInfo.read(db, "app_usage_stats");
        if (!_infoAppUsageStats.equals(_existingAppUsageStats)) {
          return new RoomOpenHelper.ValidationResult(false, "app_usage_stats(com.timeboundary.app.data.model.AppUsageStat).\n"
                  + " Expected:\n" + _infoAppUsageStats + "\n"
                  + " Found:\n" + _existingAppUsageStats);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "0417e5161050d2f5a83c544dea65c321", "77f0ca5636d7b56fbfe7f03ab8a65fc3");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "monitored_apps","app_usage_stats");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `monitored_apps`");
      _db.execSQL("DELETE FROM `app_usage_stats`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(MonitoredAppDao.class, MonitoredAppDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AppUsageStatDao.class, AppUsageStatDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public MonitoredAppDao monitoredAppDao() {
    if (_monitoredAppDao != null) {
      return _monitoredAppDao;
    } else {
      synchronized(this) {
        if(_monitoredAppDao == null) {
          _monitoredAppDao = new MonitoredAppDao_Impl(this);
        }
        return _monitoredAppDao;
      }
    }
  }

  @Override
  public AppUsageStatDao appUsageStatDao() {
    if (_appUsageStatDao != null) {
      return _appUsageStatDao;
    } else {
      synchronized(this) {
        if(_appUsageStatDao == null) {
          _appUsageStatDao = new AppUsageStatDao_Impl(this);
        }
        return _appUsageStatDao;
      }
    }
  }
}
