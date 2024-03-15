package com.linxiao.framework.permission

/**
 * 权限禁止监听器
 *
 * 当应用权限申请被用户以"不再提醒"完全禁止时使用，设置在此情况下应作出的操作
 * Created by linxiao on 2017/2/8.
 */
interface PermissionProhibitedListener {
    /**
     * 检测到权限申请被应用完全禁止
     */
    fun onProhibited(permission: String?)
}
