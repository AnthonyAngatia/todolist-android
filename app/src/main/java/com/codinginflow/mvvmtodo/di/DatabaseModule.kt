package com.codinginflow.mvvmtodo.di

import android.content.Context
import androidx.room.Room
import com.codinginflow.mvvmtodo.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    fun provideTaskDao(taskDatabase: TaskDatabase){
        taskDatabase.taskDao()
    }

    @Singleton
    @Provides
    fun provideTaskDatabase(@ApplicationContext context: Context, callback: TaskDatabase.Callback){
        Room.databaseBuilder(context,TaskDatabase::class.java, "task_database")
            .addCallback(callback)
            .build()
    }
    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())



}
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope