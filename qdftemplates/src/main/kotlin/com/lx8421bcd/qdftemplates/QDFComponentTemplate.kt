package com.lx8421bcd.qdftemplates

import com.android.tools.idea.wizard.template.*
import com.android.tools.idea.wizard.template.impl.activities.common.MIN_API
import com.android.tools.idea.wizard.template.impl.activities.common.generateManifest

fun getNoTypeClassName(type: TemplateType, fullClassName: String): String {
    var className = fullClassName
    if (className.endsWith(type.value)) {
        className = className.substring(0, className.lastIndexOf(type.value))
    }
    return className
}
fun generateLayoutName(type: TemplateType, fullClassName: String): String {
    val prefix = when (type) {
        TemplateType.Activity -> "activity"
        TemplateType.Fragment -> "fragment"
        TemplateType.Dialog -> "dialog"
    }
    return "${prefix}_${humpToLine(getNoTypeClassName(type, fullClassName))}"
}

val defaultPackageNameParameter
    get() = stringParameter {
        name = "Package Name"
        visible = { !isNewModule }
        default = "com.lx8421bcd.example"
        constraints = listOf(Constraint.PACKAGE)
        suggest = { packageName }
    }

val defaultLanguageSelectParameter
    get() = enumParameter<CodeLanguage> {
        name = "source file language"
        default = CodeLanguage.Kotlin
        help = "选择语言"
    }

val defaultTemplateTypeSelectParameter
    get() = enumParameter<TemplateType> {
        name = "source file language"
        default = TemplateType.Activity
        help = "选择组件类型"
    }

val QDFComponentTemplate
    get() = template {
        name = "QuickDevTemplates"
        description = "包含Activity,Fragment,Dialog等，按引导配置"
        minApi = MIN_API
        category = Category.Other
        formFactor = FormFactor.Mobile
        screens = listOf(
            WizardUiContext.ActivityGallery,
            WizardUiContext.MenuEntry,
            WizardUiContext.NewProject,
            WizardUiContext.NewModule
        )
        val codeLanguageParameter = defaultLanguageSelectParameter
        val templateTypeParameter = defaultTemplateTypeSelectParameter
        val parentClassParameter = stringParameter {
            name = "Parent Class Path"
            default = defBaseActivityPath
            help = "输入基类完整路径"
            constraints = listOf(Constraint.NONEMPTY)
            suggest = {
                when(templateTypeParameter.value) {
                    TemplateType.Activity -> defBaseActivityPath
                    TemplateType.Fragment -> defBaseFragmentPath
                    TemplateType.Dialog -> defBaseDialogPath
                }
            }
        }
        val classNameParameter = stringParameter {
            name = "Class Name"
            default = "Sample"
            help = "输入组件Class Name(可不带组件名后缀)"
            constraints = listOf(Constraint.NONEMPTY)
            suggest = { "Sample${templateTypeParameter.value.value}" }
        }
        val layoutNameParameter = stringParameter {
            name = "Layout Name"
            default = generateLayoutName(templateTypeParameter.value, classNameParameter.value)
            help = "请输入布局的名字"
            constraints = listOf(
                Constraint.LAYOUT,
                Constraint.UNIQUE,
                Constraint.NONEMPTY
            )
            suggest = { generateLayoutName(templateTypeParameter.value, classNameParameter.value) }
        }
        val packageNameParameter = defaultPackageNameParameter
        widgets(
            EnumWidget(codeLanguageParameter),
            EnumWidget(templateTypeParameter),
            TextFieldWidget(parentClassParameter),
            TextFieldWidget(classNameParameter),
            TextFieldWidget(layoutNameParameter),
            PackageNameWidget(packageNameParameter)
        )

        recipe = { data: TemplateData ->
            qdfComponentRecipe(
                data as ModuleTemplateData,
                codeLanguageParameter.value,
                templateTypeParameter.value,
                parentClassParameter.value,
                classNameParameter.value,
                layoutNameParameter.value,
                packageNameParameter.value)
        }
    }

fun RecipeExecutor.qdfComponentRecipe(
    moduleData: ModuleTemplateData,
    codeLanguage: CodeLanguage,
    templateType: TemplateType,
    parentClassPath: String,
    fullClassName: String,
    layoutName: String,
    packageName: String,
    srcString: String = getDefaultTemplateFile(codeLanguage, templateType),
    xmlString: String = getDefaultLayoutXml()
) {
    // insert manifest if needed
    if (templateType == TemplateType.Activity) {
        generateManifest(
            moduleData = moduleData,
            activityClass = fullClassName,
            packageName = packageName,
            isLauncher = false,
            hasNoActionBar = false,
            generateActivityTitle = false,
        )
    }
    // save source file
    val applicationPackageName = moduleData.projectTemplateData.applicationPackage?:packageName
    val parentClassName = parentClassPath.split(".").last()
    val saveSrc = srcString.trimIndent()
        .replace(packageNameHolder, packageName)
        .replace(parentClassPathHolder, parentClassPath)
        .replace(parentClassNameHolder, parentClassName)
        .replace(applicationPackageHolder, applicationPackageName)
        .replace(fullClassNameHolder, fullClassName)
        .replace(noTypeClassNameHolder, getNoTypeClassName(templateType, fullClassName))
        .replace(classHeaderHolder, titleComments(System.getProperty("user.name")))
    val srcFileName = getNoTypeClassName(templateType, fullClassName) + templateType.value
    save(saveSrc, moduleData.srcDir.resolve("${srcFileName}.${codeLanguage.suffix}"))
    // save layout xml
    val saveXml = xmlString.trimIndent()
        .replace(packageNameHolder, packageName)
        .replace(fullClassNameHolder, fullClassName)
    save(saveXml, moduleData.resDir.resolve("layout/${layoutName}.xml"))
}