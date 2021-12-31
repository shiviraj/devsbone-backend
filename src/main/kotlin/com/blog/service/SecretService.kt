package com.blog.service

import com.blog.domain.Secret
import com.blog.repository.SecretRepository
import com.blog.security.crypto.Crypto
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class SecretService(val secretRepository: SecretRepository, val crypto: Crypto) {
    fun getClientId(): Mono<Secret> {
        return getSecret(SecretKeys.GITHUB_CLIENT_ID)
    }

    fun getClientSecret(): Mono<Secret> {
        return getSecret(SecretKeys.GITHUB_CLIENT_SECRET)
    }

    private fun getSecret(secretKey: SecretKeys): Mono<Secret> {
        return secretRepository.findByKey(secretKey)
            .map {
                it.value = crypto.decrypt(it.value)
                it
            }
    }


}

enum class SecretKeys {
    GITHUB_CLIENT_ID,
    GITHUB_CLIENT_SECRET
}