package com.maroondevelopment.networth.domain.entity

enum class CachePolicy {
    PREFER_CACHE,      // Try local first, fallback to remote
    REFRESH,           // Fetch from remote and cache
}