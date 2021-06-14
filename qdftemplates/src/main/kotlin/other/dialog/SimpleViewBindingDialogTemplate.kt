package other.dialog

import com.android.tools.idea.wizard.template.*
import com.android.tools.idea.wizard.template.impl.activities.common.MIN_API
import other.humpToLine

val defaultPackageNameParameter
    get() = stringParameter {
        name = "Package name"
        visible = { !isNewModule }
        default = "com.lx8421bcd.example"
        constraints = listOf(Constraint.PACKAGE)
        suggest = { packageName }
    }

val SimpleViewBindingDialogTemplate
    get() = template {
        revision = 1
        name = "Simple ViewBinding Dialog"
        description = "基于ViewBinding基类的Dialog模板"
        minApi = MIN_API
        minBuildApi = MIN_API

        category = Category.Other
        formFactor = FormFactor.Mobile
        screens = listOf(WizardUiContext.FragmentGallery,
            WizardUiContext.MenuEntry,
            WizardUiContext.NewProject,
            WizardUiContext.NewModule
        )

        lateinit var layoutName: StringParameter

        val dialogClass = stringParameter {
            name = "Dialog Name(不包含\"Dialog\")"
            default = "Main"
            help = "只输入名字，不要包含Dialog"
            constraints = listOf(Constraint.NONEMPTY)
        }

        layoutName = stringParameter {
            name = "Layout Name"
            default = "Dialog_main"
            help = "请输入布局的名字"
            constraints = listOf(Constraint.LAYOUT, Constraint.UNIQUE, Constraint.NONEMPTY)
            suggest = { "dialog_${humpToLine(dialogClass.value)}" }
        }

        val packageName = defaultPackageNameParameter

        widgets(
            TextFieldWidget(dialogClass),
            TextFieldWidget(layoutName),
            PackageNameWidget(packageName)
        )
//        thumb { File("logo.png") }
        recipe = { data: TemplateData ->
            simpleViewBindingDialogRecipe(
                data as ModuleTemplateData,
                dialogClass.value,
                layoutName.value,
                packageName.value)
        }
    }

fun RecipeExecutor.simpleViewBindingDialogRecipe(
    moduleData: ModuleTemplateData,
    DialogClass: String,
    layoutName: String,
    packageName: String
) {
    val (projectData, srcOut, resOut) = moduleData
    val ktOrJavaExt = projectData.language.extension

    //生成Dialog文件
    val dialogFile = simpleViewBindingDialogKt(projectData.applicationPackage, DialogClass, packageName)
    save(dialogFile, srcOut.resolve("${DialogClass}Dialog.${ktOrJavaExt}"))
    // 保存xml
    val xmlFile = simpleViewBindingDialogXml(packageName, DialogClass)
    save(xmlFile, resOut.resolve("layout/${layoutName}.xml"))
}

/*-------------------- Dialog code generate function ----------------------*/
fun simpleViewBindingDialogKt(
    applicationPackage:String?,
    DialogClass:String,
    packageName:String
)="""
package $packageName
import android.os.Bundle
import com.linxiao.framework.architecture.SimpleViewBindingDialog
import ${applicationPackage}.R
import ${applicationPackage}.databinding.Dialog${DialogClass}Binding
class ${DialogClass}Dialog : SimpleViewBindingDialog<Dialog${DialogClass}Binding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        
    }

    private fun initView() {

    }
} 
"""

/*-------------------- layout xml code generate function ----------------------*/

fun simpleViewBindingDialogXml(
    packageName: String,
    DialogClass: String
) = """
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout 
xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:app="http://schemas.android.com/apk/res-auto"
xmlns:tools="http://schemas.android.com/tools"
android:layout_width="match_parent"
android:layout_height="match_parent"
tools:context="${packageName}.${DialogClass}Dialog">
    
    
    
</RelativeLayout>
"""
