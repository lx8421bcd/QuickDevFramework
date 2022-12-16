package other

import com.android.tools.idea.wizard.template.Constraint
import com.android.tools.idea.wizard.template.enumParameter
import com.android.tools.idea.wizard.template.stringParameter
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
            if (sb.isEmpty()) matcher.group(0).lowercase()
            else "_${matcher.group(0).lowercase()}"
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

/**
 * 默认包名选择配置
 */
val defaultPackageNameParameter
    get() = stringParameter {
        name = "Package name"
        visible = { !isNewModule }
        default = "com.lx8421bcd.example"
        constraints = listOf(Constraint.PACKAGE)
        suggest = { packageName }
    }

val defaultLanguageSelectParameter
    get() = enumParameter<CodeLanguage> {
        name = "source file language"
        default = CodeLanguage.Java
        help = "选择语言"
    }
