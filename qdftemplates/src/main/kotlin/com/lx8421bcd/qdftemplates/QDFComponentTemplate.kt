package com.lx8421bcd.qdftemplates

import com.android.tools.idea.wizard.template.*
import com.android.tools.idea.wizard.template.impl.activities.common.MIN_API
import com.android.tools.idea.wizard.template.impl.activities.common.generateManifest

fun getNoTypeClassName(type: ComponentType, fullClassName: String): String {
    var className = fullClassName
    if (className.endsWith(type.classSuffix)) {
        className = className.substring(0, className.lastIndexOf(type.classSuffix))
    }
    return className
}
fun generateLayoutName(type: ComponentType, fullClassName: String): String {
    return "${type.layoutPrefix}_${humpToLine(getNoTypeClassName(type, fullClassName))}"
}

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
        name = "Source file language"
        default = CodeLanguage.Kotlin
        help = "选择语言"
    }

val defaultComponentTypeSelectParameter
    get() = enumParameter<ComponentType> {
        name = "Component type"
        default = ComponentType.Activity
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
        val templateTypeParameter = defaultComponentTypeSelectParameter
        val parentClassParameter = stringParameter {
            name = "Parent class path"
            default = templateTypeParameter.value.defaultBaseClass
            help = "输入基类完整路径"
            constraints = listOf(Constraint.NONEMPTY)
            suggest = { templateTypeParameter.value.defaultBaseClass }
        }
        val classNameParameter = stringParameter {
            name = "Class name"
            default = "Sample"
            help = "输入组件Class Name(可不带组件名后缀)"
            constraints = listOf(Constraint.NONEMPTY)
            suggest = { "Sample${templateTypeParameter.value.classSuffix}" }
        }
        val layoutNameParameter = stringParameter {
            name = "Layout name"
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
    componentType: ComponentType,
    parentClassPath: String,
    fullClassName: String,
    layoutName: String,
    packageName: String,
    srcString: String = getDefaultTemplateFile(codeLanguage, componentType),
    xmlString: String = getDefaultLayoutXml()
) {
    // insert manifest if needed
    if (componentType == ComponentType.Activity) {
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
        .replace(HOLDER_PACKAGE_NAME, packageName)
        .replace(HOLDER_PARENT_CLASS_PATH, parentClassPath)
        .replace(HOLDER_PARENT_CLASS_NAME, parentClassName)
        .replace(HOLDER_APPLICATION_PACKAGE, applicationPackageName)
        .replace(HOLDER_FULL_CLASS_NAME, fullClassName)
        .replace(HOLDER_NO_TYPE_CLASS_NAME, getNoTypeClassName(componentType, fullClassName))
        .replace(HOLDER_CLASS_HEADER, titleComments(System.getProperty("user.name")))
    val srcFileName = getNoTypeClassName(componentType, fullClassName) + componentType.classSuffix
    save(saveSrc, moduleData.srcDir.resolve("${srcFileName}.${codeLanguage.suffix}"))
    // save layout xml
    val saveXml = xmlString.trimIndent()
        .replace(HOLDER_PACKAGE_NAME, packageName)
        .replace(HOLDER_FULL_CLASS_NAME, fullClassName)
    save(saveXml, moduleData.resDir.resolve("layout/${layoutName}.xml"))
}