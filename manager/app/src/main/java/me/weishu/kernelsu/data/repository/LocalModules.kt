package me.weishu.kernelsu.data.repository
import android.net.Uri
import me.weishu.kernelsu.ksuApp
import me.weishu.kernelsu.data.model.Module
import org.json.JSONObject
import java.util.Properties
import java.util.zip.ZipInputStream
object LocalModules {
    private val prefs get() = ksuApp.getSharedPreferences("local_modules", 0)
    fun contains(id: String) = prefs.contains(id)
    fun all(): List<Module> = prefs.all.values.mapNotNull { value -> runCatching {
        val o = JSONObject(value as String)
        Module(o.getString("id"), o.getString("name"), o.optString("author"), o.optString("version"), o.optInt("versionCode"),
            "[本地导入] " + o.optString("description"), o.optBoolean("enabled", true), false, false, "", false, false, false, null, null)
    }.getOrNull() }
    fun toggle(id: String, enabled: Boolean) { val o = JSONObject(prefs.getString(id, "{}")!!); o.put("enabled", enabled); prefs.edit().putString(id, o.toString()).apply() }
    fun remove(id: String) { prefs.edit().remove(id).apply() }
    fun importZip(uri: Uri) {
        val input = requireNotNull(ksuApp.contentResolver.openInputStream(uri))
        val props = Properties()
        var found = false
        ZipInputStream(input).use { zip ->
            var entries = 0
            while (true) {
                val entry = zip.nextEntry ?: break
                require(++entries <= 4096) { "ZIP 条目过多" }
                if (!entry.isDirectory && entry.name == "module.prop") {
                    val bytes = zip.readNBytes(65537)
                    require(bytes.size <= 65536) { "module.prop 过大" }
                    props.load(bytes.toString(Charsets.UTF_8).reader())
                    found = true
                    break
                }
            }
        }
        require(found) { "ZIP 根目录缺少 module.prop" }
        val id = props.getProperty("id", "").trim()
        require(id.matches(Regex("[A-Za-z][A-Za-z0-9._-]{0,127}"))) { "模块 ID 无效" }
        val name = props.getProperty("name", "").trim()
        require(name.isNotEmpty()) { "缺少模块名称" }
        val o = JSONObject()
        for (key in listOf("id", "name", "author", "version", "description")) o.put(key, props.getProperty(key, ""))
        o.put("versionCode", props.getProperty("versionCode", "0").toIntOrNull() ?: 0)
        o.put("enabled", true)
        prefs.edit().putString(id, o.toString()).apply()
    }
}
