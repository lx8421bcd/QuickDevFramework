package other

import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

val defBaseActivityPath = "androidx.appcompat.app.AppCompatActivity"
val defBaseFragmentPath = "androidx.fragment.app.Fragment"
val defBaseDialogPath = "android.app.Dialog"

private val humpPattern: Pattern = Pattern.compile("[A-Z]")
fun humpToLine(str: String): String {
    val matcher: Matcher = humpPattern.matcher(str)
    val sb = StringBuffer()
    while (matcher.find()) {
        matcher.appendReplacement(
            sb,
            if (sb.isEmpty()) matcher.group(0).toLowerCase()
            else "_${matcher.group(0).toLowerCase()}"
        )
    }
    matcher.appendTail(sb)
    return sb.toString()
}

fun titleComments(author: String)="""
/**
 * class summary
 * <p>
 *  usage and notices
 * </p>
 *
 * @author $author
 * @since ${timeMillsToDateString("yyyy-MM-dd", System.currentTimeMillis())}
 */
""".trimIndent()

fun timeMillsToDateString(format: String, timeMills: Long): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return try {
        sdf.format(Date(timeMills))
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
}