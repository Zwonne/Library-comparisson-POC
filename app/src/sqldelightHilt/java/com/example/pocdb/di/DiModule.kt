package com.example.pocdb.di

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.pocdb.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DiModule {

    @Singleton
    @Provides
    fun provideSqlDelightDriver(@ApplicationContext context: Context): SqlDriver {
        return AndroidSqliteDriver(
            schema = AppDatabase.Schema.synchronous(),
            context = context,
            name = "AppDatabase.db",
            callback = object : AndroidSqliteDriver.Callback(AppDatabase.Schema.synchronous()) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                    db.enableWriteAheadLogging()
                }
            }
        )
    }

    @Singleton
    @Provides
    fun provideSqlDelightDatabase(sqlDriver: SqlDriver): AppDatabase = AppDatabase(sqlDriver)
}