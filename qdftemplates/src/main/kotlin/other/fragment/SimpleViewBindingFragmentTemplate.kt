package other.fragment

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

val SimpleViewBindingFragmentTemplate
    get() = template {
        revision = 1
        name = "Simple ViewBinding Fragment"
        description = "基于ViewBinding基类的Fragment模板"
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

        val FragmentClass = stringParameter {
            name = "Fragment Name(不包含\"Fragment\")"
            default = "Main"
            help = "只输入名字，不要包含Fragment"
            constraints = listOf(Constraint.NONEMPTY)
        }

        layoutName = stringParameter {
            name = "Layout Name"
            default = "Fragment_main"
            help = "请输入布局的名字"
            constraints = listOf(Constraint.LAYOUT, Constraint.UNIQUE, Constraint.NONEMPTY)
            suggest = { fragmentToLayout(humpToLine(FragmentClass.value)) }
        }

        val packageName = defaultPackageNameParameter

        widgets(
            TextFieldWidget(FragmentClass),
            TextFieldWidget(layoutName),
            PackageNameWidget(packageName)
        )
//        thumb { File("logo.png") }
        recipe = { data: TemplateData ->
            simpleViewBindingFragmentRecipe(
                data as ModuleTemplateData,
                FragmentClass.value,
                layoutName.value,
                packageName.value)
        }
    }

fun RecipeExecutor.simpleViewBindingFragmentRecipe(
    moduleData: ModuleTemplateData,
    FragmentClass: String,
    layoutName: String,
    packageName: String
) {
    val (projectData, srcOut, resOut) = moduleData
    val ktOrJavaExt = projectData.language.extension

    //生成Fragment文件
    val FragmentFile = simpleViewBindingFragmentKt(projectData.applicationPackage, FragmentClass, packageName)
    save(FragmentFile, srcOut.resolve("${FragmentClass}Fragment.${ktOrJavaExt}"))
    // 保存xml
    val xmlFile = simpleViewBindingFragmentXml(packageName, FragmentClass)
    save(xmlFile, resOut.resolve("layout/${layoutName}.xml"))
}

/*-------------------- Fragment code generate function ----------------------*/
fun simpleViewBindingFragmentKt(
    applicationPackage:String?,
    FragmentClass:String,
    packageName:String
)="""
package $packageName
import android.os.Bundle
import com.linxiao.framework.architecture.SimpleViewBindingFragment
import ${applicationPackage}.R
import ${applicationPackage}.databinding.Fragment${FragmentClass}Binding
class ${FragmentClass}Fragment : SimpleViewBindingFragment<Fragment${FragmentClass}Binding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        
    }

    private fun initView() {

    }
} 
"""

/*-------------------- layout xml code generate function ----------------------*/

fun simpleViewBindingFragmentXml(
    packageName: String,
    FragmentClass: String
) = """
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout 
xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:app="http://schemas.android.com/apk/res-auto"
xmlns:tools="http://schemas.android.com/tools"
android:layout_width="match_parent"
android:layout_height="match_parent"
tools:context="${packageName}.${FragmentClass}Fragment">
    
    
    
</RelativeLayout>
"""
