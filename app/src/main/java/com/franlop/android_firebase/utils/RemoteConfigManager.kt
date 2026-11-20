package com.franlop.android_firebase.utils

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.tasks.await

class RemoteConfigManager {
    private val remoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 10
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(
            mapOf("welcome_message" to "Bienvenidx a la App")
        )
    }

    suspend fun fetchAndActivate(): String {
        return try {
            remoteConfig.fetchAndActivate().await()
            remoteConfig.getString("welcome_message")
        } catch (e: Exception) {
            remoteConfig.getString("welcome_message")
        }
    }
}