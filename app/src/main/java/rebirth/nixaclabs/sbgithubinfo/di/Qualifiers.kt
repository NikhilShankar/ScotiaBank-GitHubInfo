package rebirth.nixaclabs.sbgithubinfo.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MockDataSource
