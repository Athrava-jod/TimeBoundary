package com.timeboundary.app.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.timeboundary.app.data.model.AppUsageStat;
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
public final class AppUsageStatDao_Impl implements AppUsageStatDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AppUsageStat> __insertionAdapterOfAppUsageStat;

  private final SharedSQLiteStatement __preparedStmtOfClearAllStats;

  public AppUsageStatDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAppUsageStat = new EntityInsertionAdapter<AppUsageStat>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `app_usage_stats` (`id`,`packageName`,`dateString`,`openCount`,`limitExceededCount`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AppUsageStat entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPackageName());
        statement.bindString(3, entity.getDateString());
        statement.bindLong(4, entity.getOpenCount());
        statement.bindLong(5, entity.getLimitExceededCount());
      }
    };
    this.__preparedStmtOfClearAllStats = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM app_usage_stats";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdate(final AppUsageStat stat,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAppUsageStat.insert(stat);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllStats(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllStats.acquire();
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
          __preparedStmtOfClearAllStats.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AppUsageStat>> getStatsForDate(final String dateString) {
    final String _sql = "SELECT * FROM app_usage_stats WHERE dateString = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, dateString);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"app_usage_stats"}, new Callable<List<AppUsageStat>>() {
      @Override
      @NonNull
      public List<AppUsageStat> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfDateString = CursorUtil.getColumnIndexOrThrow(_cursor, "dateString");
          final int _cursorIndexOfOpenCount = CursorUtil.getColumnIndexOrThrow(_cursor, "openCount");
          final int _cursorIndexOfLimitExceededCount = CursorUtil.getColumnIndexOrThrow(_cursor, "limitExceededCount");
          final List<AppUsageStat> _result = new ArrayList<AppUsageStat>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppUsageStat _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpDateString;
            _tmpDateString = _cursor.getString(_cursorIndexOfDateString);
            final int _tmpOpenCount;
            _tmpOpenCount = _cursor.getInt(_cursorIndexOfOpenCount);
            final int _tmpLimitExceededCount;
            _tmpLimitExceededCount = _cursor.getInt(_cursorIndexOfLimitExceededCount);
            _item = new AppUsageStat(_tmpId,_tmpPackageName,_tmpDateString,_tmpOpenCount,_tmpLimitExceededCount);
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
  public Object getStat(final String packageName, final String dateString,
      final Continuation<? super AppUsageStat> $completion) {
    final String _sql = "SELECT * FROM app_usage_stats WHERE packageName = ? AND dateString = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, packageName);
    _argIndex = 2;
    _statement.bindString(_argIndex, dateString);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AppUsageStat>() {
      @Override
      @Nullable
      public AppUsageStat call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfDateString = CursorUtil.getColumnIndexOrThrow(_cursor, "dateString");
          final int _cursorIndexOfOpenCount = CursorUtil.getColumnIndexOrThrow(_cursor, "openCount");
          final int _cursorIndexOfLimitExceededCount = CursorUtil.getColumnIndexOrThrow(_cursor, "limitExceededCount");
          final AppUsageStat _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpDateString;
            _tmpDateString = _cursor.getString(_cursorIndexOfDateString);
            final int _tmpOpenCount;
            _tmpOpenCount = _cursor.getInt(_cursorIndexOfOpenCount);
            final int _tmpLimitExceededCount;
            _tmpLimitExceededCount = _cursor.getInt(_cursorIndexOfLimitExceededCount);
            _result = new AppUsageStat(_tmpId,_tmpPackageName,_tmpDateString,_tmpOpenCount,_tmpLimitExceededCount);
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
