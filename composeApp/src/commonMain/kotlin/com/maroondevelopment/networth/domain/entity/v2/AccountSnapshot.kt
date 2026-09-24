package com.maroondevelopment.networth.domain.entity.v2

data class AccountSnapshot(
    val account: Account,
    val assets: List<Asset>
)
