package other

import java.util.regex.Matcher
import java.util.regex.Pattern
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