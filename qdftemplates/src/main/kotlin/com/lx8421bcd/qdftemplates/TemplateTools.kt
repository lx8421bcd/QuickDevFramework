package com.lx8421bcd.qdftemplates

import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

const val HOLDER_PACKAGE_NAME = "\${packageName}"
const val HOLDER_APPLICATION_PACKAGE = "\${applicationPackage}"
const val HOLDER_PARENT_CLASS_PATH = "\${parentClassPath}"
const val HOLDER_PARENT_CLASS_NAME = "\${parentClassName}"
const val HOLDER_FULL_CLASS_NAME = "\${fullClassName}"
const val HOLDER_NO_TYPE_CLASS_NAME = "\${noTypeClassName}"
const val HOLDER_CLASS_HEADER = "\${classHeader}"


fun humpToLine(str: String): String {
    val matcher: Matcher = Pattern.compile("[A-Z]").matcher(str)
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

fun getDefaultTemplateFile(codeLanguage: CodeLanguage, componentType: ComponentType): String {
    val filePath = if (codeLanguage == CodeLanguage.Kotlin) {
        when(componentType) {
            ComponentType.Activity -> "/templates/DefaultActivityTemplateKt"
            ComponentType.Fragment -> "/templates/DefaultFragmentTemplateKt"
            ComponentType.Dialog -> "/templates/DefaultDialogTemplateKt"
        }
    }
    else {
        when(componentType) {
            ComponentType.Activity -> "/templates/DefaultActivityTemplateJava"
            ComponentType.Fragment -> "/templates/DefaultFragmentTemplateJava"
            ComponentType.Dialog -> "/templates/DefaultDialogTemplateJava"
        }
    }
    return PluginFileTools.loadStringFromResource(filePath)
}

fun getDefaultLayoutXml(): String {
    return PluginFileTools.loadStringFromResource("/templates/DefaultLayoutTemplateXml")
}