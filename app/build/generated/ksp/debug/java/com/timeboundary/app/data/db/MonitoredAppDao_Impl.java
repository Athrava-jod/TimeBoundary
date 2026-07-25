package com.timeboundary.app.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.timeboundary.app.data.model.MonitoredApp;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MonitoredAppDao_Impl implements MonitoredAppDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MonitoredApp> __insertionAdapterOfMonitoredApp;

  private final EntityDeletionOrUpdateAdapter<MonitoredApp> __deletionAdapterOfMonitoredApp;

  private final EntityDeletionOrUpdateAdapter<MonitoredApp> __updateAdapterOfMonitoredApp;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByPackage;

  private final SharedSQLiteStatement __preparedStmtOfSetEnabledStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSessionStart;

  public MonitoredAppDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMonitoredApp = new EntityInsertionAdapter<MonitoredApp>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `monitored_apps` (`packageName`,`appLabel`,`durationMinutes`,`isEnabled`,`lastSessionStartTime`,`activeSessionId`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MonitoredApp entity) {
        statement.bindString(1, entity.getPackageName());
        statement.bindString(2, entity.getAppLabel());
        statement.bindLong(3, entity.getDurationMinutes());
        final int _tmp = entity.isEnabled() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getLastSessionStartTime());
        statement.bindLong(6, entity.getActiveSessionId());
      }
    };
    this.__deletionAdapterOfMonitoredApp = new EntityDeletionOrUpdateAdapter<MonitoredApp>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `monitored_apps` WHERE `packageName` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MonitoredApp entity) {
        statement.bindString(1, entity.getPackageName());
      }
    };
    this.__updateAdapterOfMonitoredApp = new EntityDeletionOrUpdateAdapter<MonitoredApp>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `monitored_apps` SET `packageName` = ?,`appLabel` = ?,`durationMinutes` = ?,`isEnabled` = ?,`lastSessionStartTime` = ?,`activeSessionId` = ? WHERE `packageName` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MonitoredApp entity) {
        statement.bindString(1, entity.getPackageName());
        statement.bindString(2, entity.getAppLabel());
        statement.bindLong(3, entity.getDurationMinutes());
        final int _tmp = entity.isEnabled() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getLastSessionStartTime());
        statement.bindLong(6, entity.getActiveSessionId());
        statement.bindString(7, entity.getPackageName());
      }
    };
    this.__preparedStmtOfDeleteByPackage = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM monitored_apps WHERE packageName = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetEnabledStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE monitored_apps SET isEnabled = ? WHERE packageName = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateSessionStart = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE monitored_apps SET lastSessionStartTime = ?, activeSessionId = ? WHERE packageName = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdate(final MonitoredApp app,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMonitoredApp.insert(app);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final MonitoredApp app, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMonitoredApp.handle(app);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MonitoredApp app, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMonitoredApp.handle(app);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByPackage(final String packageName,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByPackage.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, packageName);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteByPackage.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setEnabledStatus(final String packageName, final boolean isEnabled,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetEnabledStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isEnabled ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, packageName);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetEnabledStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSessionStart(final String packageName, final long startTime,
      final long sessionId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSessionStart.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, startTime);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, sessionId);
        _argIndex = 3;
        _stmt.bindString(_argIndex, packageName);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateSessionStart.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MonitoredApp>> getAllMonitoredApps() {
    final String _sql = "SELECT * FROM monitored_apps ORDER BY appLabel ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"monitored_apps"}, new Callable<List<MonitoredApp>>() {
      @Override
      @NonNull
      public List<MonitoredApp> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "appLabel");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfIsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "isEnabled");
          final int _cursorIndexOfLastSessionStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSessionStartTime");
          final int _cursorIndexOfActiveSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "activeSessionId");
          final List<MonitoredApp> _result = new ArrayList<MonitoredApp>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MonitoredApp _item;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpAppLabel;
            _tmpAppLabel = _cursor.getString(_cursorIndexOfAppLabel);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final boolean _tmpIsEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEnabled);
            _tmpIsEnabled = _tmp != 0;
            final long _tmpLastSessionStartTime;
            _tmpLastSessionStartTime = _cursor.getLong(_cursorIndexOfLastSessionStartTime);
            final long _tmpActiveSessionId;
            _tmpActiveSessionId = _cursor.getLong(_cursorIndexOfActiveSessionId);
            _item = new MonitoredApp(_tmpPackageName,_tmpAppLabel,_tmpDurationMinutes,_tmpIsEnabled,_tmpLastSessionStartTime,_tmpActiveSessionId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getEnabledMonitoredAppsSync(
      final Continuation<? super List<MonitoredApp>> $completion) {
    final String _sql = "SELECT * FROM monitored_apps WHERE isEnabled = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MonitoredApp>>() {
      @Override
      @NonNull
      public List<MonitoredApp> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "appLabel");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfIsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "isEnabled");
          final int _cursorIndexOfLastSessionStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSessionStartTime");
          final int _cursorIndexOfActiveSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "activeSessionId");
          final List<MonitoredApp> _result = new ArrayList<MonitoredApp>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MonitoredApp _item;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpAppLabel;
            _tmpAppLabel = _cursor.getString(_cursorIndexOfAppLabel);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final boolean _tmpIsEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEnabled);
            _tmpIsEnabled = _tmp != 0;
            final long _tmpLastSessionStartTime;
            _tmpLastSessionStartTime = _cursor.getLong(_cursorIndexOfLastSessionStartTime);
            final long _tmpActiveSessionId;
            _tmpActiveSessionId = _cursor.getLong(_cursorIndexOfActiveSessionId);
            _item = new MonitoredApp(_tmpPackageName,_tmpAppLabel,_tmpDurationMinutes,_tmpIsEnabled,_tmpLastSessionStartTime,_tmpActiveSessionId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getMonitoredApp(final String packageName,
      final Continuation<? super MonitoredApp> $completion) {
    final String _sql = "SELECT * FROM monitored_apps WHERE packageName = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, packageName);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MonitoredApp>() {
      @Override
      @Nullable
      public MonitoredApp call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "appLabel");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfIsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "isEnabled");
          final int _cursorIndexOfLastSessionStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSessionStartTime");
          final int _cursorIndexOfActiveSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "activeSessionId");
          final MonitoredApp _result;
          if (_cursor.moveToFirst()) {
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpAppLabel;
            _tmpAppLabel = _cursor.getString(_cursorIndexOfAppLabel);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final boolean _tmpIsEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEnabled);
            _tmpIsEnabled = _tmp != 0;
            final long _tmpLastSessionStartTime;
            _tmpLastSessionStartTime = _cursor.getLong(_cursorIndexOfLastSessionStartTime);
            final long _tmpActiveSessionId;
            _tmpActiveSessionId = _cursor.getLong(_cursorIndexOfActiveSessionId);
            _result = new MonitoredApp(_tmpPackageName,_tmpAppLabel,_tmpDurationMinutes,_tmpIsEnabled,_tmpLastSessionStartTime,_tmpActiveSessionId);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
