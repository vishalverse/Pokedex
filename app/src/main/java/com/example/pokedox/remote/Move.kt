package com.example.pokedox.remote

data class Move(
    val move: MoveX,
    val version_group_details: List<VersionGroupDetail>
)