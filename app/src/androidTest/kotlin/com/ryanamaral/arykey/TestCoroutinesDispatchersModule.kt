import android.os.AsyncTask
import com.ryanamaral.arykey.di.CoroutinesDispatchersModule
import com.ryanamaral.arykey.di.DefaultDispatcher
import com.ryanamaral.arykey.di.IoDispatcher
import com.ryanamaral.arykey.di.MainDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher


@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CoroutinesDispatchersModule::class]
)
@Module
object TestCoroutinesDispatchersModule {

    @DefaultDispatcher
    @Provides
    fun providesDefaultDispatcher(): CoroutineDispatcher =
        AsyncTask.THREAD_POOL_EXECUTOR.asCoroutineDispatcher()

    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher =
        AsyncTask.THREAD_POOL_EXECUTOR.asCoroutineDispatcher()

    @MainDispatcher
    @Provides
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
