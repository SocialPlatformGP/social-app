package com.gp.users.model

data class SelectableUser(
    val data: User,
    var isSelected: Boolean = false
) {
    companion object {
        fun User.toSelectableUser() = SelectableUser(this)
    }
}
