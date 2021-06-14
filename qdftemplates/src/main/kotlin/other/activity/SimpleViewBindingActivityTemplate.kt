package other.activity

import com.android.tools.idea.wizard.template.*
import com.android.tools.idea.wizard.template.impl.activities.common.MIN_API
import com.android.tools.idea.wizard.template.impl.activities.common.generateManifest
import com.intellij.util.xml.DomManager
import other.humpToLine

val defaultPackageNameParameter
    get() = stringParameter {
        name = "Package name"
        visible = { !isNewModule }
        default = "com.lx8421bcd.example"
        constraints = listOf(Constraint.PACKAGE)
        suggest = { packageName }
    }

val SimpleViewBindingActivityTemplate
    get() = template {
        revision = 1
        name = "Simple ViewBinding Activity"
        description = "基于ViewBinding基类的Activity模板"
        minApi = MIN_API
        minBuildApi = MIN_API

        category = Category.Other
        formFactor = FormFactor.Mobile
        screens = listOf(WizardUiContext.ActivityGallery,
            WizardUiContext.MenuEntry,
            WizardUiContext.NewProject,
            WizardUiContext.NewModule
        )

        lateinit var layoutName: StringParameter

        val activityClass = stringParameter {
            name = "Activity Name(不包含\"Activity\")"
            default = "Main"
            help = "只输入名字，不要包含Activity"
            constraints = listOf(Constraint.NONEMPTY)
        }

        layoutName = stringParameter {
            name = "Layout Name"
            default = "activity_main"
            help = "请输入布局的名字"
            constraints = listOf(Constraint.LAYOUT, Constraint.UNIQUE, Constraint.NONEMPTY)
            suggest = { activityToLayout(humpToLine(activityClass.value)) }
        }

        val packageName = defaultPackageNameParameter

        widgets(
            TextFieldWidget(activityClass),
            TextFieldWidget(layoutName),
            PackageNameWidget(packageName)
        )
//        thumb { File("logo.png") }
        recipe = { data: TemplateData ->
            simpleViewBindingActivityRecipe(
                data as ModuleTemplateData,
                activityClass.value,
                layoutName.value,
                packageName.value)
        }
    }

fun RecipeExecutor.simpleViewBindingActivityRecipe(
    moduleData: ModuleTemplateData,
    activityClass: String,
    layoutName: String,
    packageName: String
) {
    val (projectData, srcOut, resOut) = moduleData
    val ktOrJavaExt = projectData.language.extension
    // 插入manifest声明
    generateManifest(
        moduleData = moduleData,
        activityClass = "${activityClass}Activity",
        activityTitle = activityClass,
        packageName = packageName,
        isLauncher = false,
        hasNoActionBar = false,
        generateActivityTitle = false,
    )
    //生成activity文件
    val activityFile = simpleViewBindingActivityKt(projectData.applicationPackage, activityClass, packageName)
    save(activityFile, srcOut.resolve("${activityClass}Activity.${ktOrJavaExt}"))
    // 保存xml
    val xmlFile = simpleViewBindingActivityXml(packageName, activityClass)
    save(xmlFile, resOut.resolve("layout/${layoutName}.xml"))
}

/*-------------------- activity code generate function ----------------------*/
fun simpleViewBindingActivityKt(
    applicationPackage:String?,
    activityClass:String,
    packageName:String
)="""
package $packageName
import android.os.Bundle
import com.linxiao.framework.architecture.SimpleViewBindingActivity
import ${applicationPackage}.R
import ${applicationPackage}.databinding.Activity${activityClass}Binding
class ${activityClass}Activity : SimpleViewBindingActivity<Activity${activityClass}Binding>() {

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        
    }

    private fun initView() {

    }
} 
"""

/*-------------------- layout xml code generate function ----------------------*/

fun simpleViewBindingActivityXml(
    packageName: String,
    activityClass: String
) = """
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout 
xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:app="http://schemas.android.com/apk/res-auto"
xmlns:tools="http://schemas.android.com/tools"
android:layout_width="match_parent"
android:layout_height="match_parent"
tools:context="${packageName}.${activityClass}Activity">
    
    
    
</RelativeLayout>
"""
