package xyz.reportcards.vaults.models

class PlayerVault(
    val id: Int,
    val contents: List<VaultItem>,
    val size: Int = 9
)