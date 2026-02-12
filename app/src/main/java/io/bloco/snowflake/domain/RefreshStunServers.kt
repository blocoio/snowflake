package io.bloco.snowflake.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class RefreshStunServers(
    private val fetchStunServers: suspend () -> List<String>?,
    private val getStunServersDate: () -> Flow<Instant?>,
    private val setStunServersDate: suspend (Instant) -> Unit,
    private val setStunServers: suspend (List<String>) -> Unit,
) {
    suspend operator fun invoke() {
        val now = Clock.System.now()
        val lastDate = getStunServersDate().first()

        if (lastDate != null && now - lastDate < UPDATE_THRESHOLD) {
            Timber.i("STUN servers up-to-date")
            return
        }

        setStunServers(fetchStunServers() ?: return)
        setStunServersDate(now)
        Timber.i("STUN servers refreshed")
    }

    companion object {
        private val UPDATE_THRESHOLD = 2.days
    }
}