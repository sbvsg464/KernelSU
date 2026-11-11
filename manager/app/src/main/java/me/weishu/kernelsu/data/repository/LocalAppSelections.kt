package me.weishu.kernelsu.data.repository
import me.weishu.kernelsu.Natives
import me.weishu.kernelsu.ksuApp
object LocalAppSelections {
    private val prefs get() = ksuApp.getSharedPreferences("local_app_selections", 0)
    fun profile(name: String, uid: Int) = Natives.Profile(name = name, currentUid = uid, allowSu = prefs.getBoolean(uid.toString(), false))
    fun save(profile: Natives.Profile) { prefs.edit().putBoolean(profile.currentUid.toString(), profile.allowSu).apply() }
}
