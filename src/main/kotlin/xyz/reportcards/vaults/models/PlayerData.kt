package xyz.reportcards.vaults.models

import java.util.UUID


data class PlayerData(
    val uuid: UUID,
    val vaults: MutableList<VaultData>,
    val extraVaults: Int = 0,
) {

}
