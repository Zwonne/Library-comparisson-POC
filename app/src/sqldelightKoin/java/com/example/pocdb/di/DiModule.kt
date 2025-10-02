package com.example.pocdb.di

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.pocdb.AppDatabase
import com.example.pocdb.MainViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appKoinModule = module {
    single<SqlDriver> {
        val context: Context = get()
        AndroidSqliteDriver(
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

    single<AppDatabase> {
        val sqlDriver: SqlDriver = get()
        AppDatabase(sqlDriver)
    }

    factoryOf(::MainViewModel)
}