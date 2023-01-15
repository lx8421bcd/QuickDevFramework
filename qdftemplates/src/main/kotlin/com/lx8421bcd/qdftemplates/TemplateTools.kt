package com.lx8421bcd.qdftemplates

import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

val packageNameHolder = "\${packageName}"
val applicationPackageHolder = "\${applicationPackage}"
val parentClassPathHolder = "\${parentClassPath}"
val parentClassNameHolder = "\${parentClassName}"
val fullClassNameHolder = "\${fullClassName}"
val noTypeClassNameHolder = "\${noTypeClassName}"
val classHeaderHolder = "\${classHeader}"


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

fun getDefaultTemplateFile(codeLanguage: CodeLanguage, templateType: TemplateType): String {
    val filePath = if (codeLanguage == CodeLanguage.Kotlin) {
        when(templateType) {
            TemplateType.Activity -> "/templates/DefaultActivityTemplateKt"
            TemplateType.Fragment -> "/templates/DefaultFragmentTemplateKt"
            TemplateType.Dialog -> "/templates/DefaultDialogTemplateKt"
        }
    }
    else {
        when(templateType) {
            TemplateType.Activity -> "/templates/DefaultActivityTemplateJava"
            TemplateType.Fragment -> "/templates/DefaultFragmentTemplateJava"
            TemplateType.Dialog -> "/templates/DefaultDialogTemplateJava"
        }
    }
    return PluginFileTools.loadStringFromResource(filePath)
}

fun getDefaultLayoutXml(): String {
    return PluginFileTools.loadStringFromResource("/templates/DefaultLayoutTemplateXml")
}