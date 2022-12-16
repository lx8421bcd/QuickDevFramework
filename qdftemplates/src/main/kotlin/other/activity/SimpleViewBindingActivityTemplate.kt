package other.activity

import com.android.tools.idea.wizard.template.*
import com.android.tools.idea.wizard.template.impl.activities.common.MIN_API
import com.android.tools.idea.wizard.template.impl.activities.common.generateManifest
import other.*

val SimpleViewBindingActivityTemplate
    get() = template {
        name = "Simple ViewBinding Activity"
        description = "基于ViewBinding基类的Activity模板"
        minApi = MIN_API

        category = Category.Other
        formFactor = FormFactor.Mobile
        screens = listOf(WizardUiContext.ActivityGallery,
            WizardUiContext.MenuEntry,
            WizardUiContext.NewProject,
            WizardUiContext.NewModule
        )

        val codeLanguage = defaultLanguageSelectParameter
        val parentClass = stringParameter {
            name = "Parent Class PATH"
            default = defBaseActivityPath
            help = "输入基类完整路径"
            constraints = listOf(Constraint.NONEMPTY)
        }
        val activityClass = stringParameter {
            name = "Activity Name(不包含\"Activity\")"
            default = "Main"
            help = "只输入名字，不要包含Activity"
            constraints = listOf(Constraint.NONEMPTY)
        }
        val layoutName = stringParameter {
            name = "Layout Name"
            default = "activity_main"
            help = "请输入布局的名字"
            constraints = listOf(
                Constraint.LAYOUT,
                Constraint.UNIQUE,
                Constraint.NONEMPTY
            )
            suggest = { activityToLayout(humpToLine(activityClass.value)) }
        }

        val packageName = defaultPackageNameParameter

        widgets(
            EnumWidget(codeLanguage),
            TextFieldWidget(parentClass),
            TextFieldWidget(activityClass),
            TextFieldWidget(layoutName),
            PackageNameWidget(packageName)
        )
//        thumb { File("logo.png") }
        recipe = { data: TemplateData ->
            simpleViewBindingActivityRecipe(
                data as ModuleTemplateData,
                codeLanguage.value,
                parentClass.value,
                activityClass.value,
                layoutName.value,
                packageName.value)
        }
    }

fun RecipeExecutor.simpleViewBindingActivityRecipe(
    moduleData: ModuleTemplateData,
    codeLanguage: CodeLanguage,
    parentClass: String,
    activityClass: String,
    layoutName: String,
    packageName: String
) {
    val (projectData, srcOut, resOut) = moduleData
    // 插入manifest声明
    generateManifest(
        moduleData = moduleData,
        activityClass = "${activityClass}Activity",
        packageName = packageName,
        isLauncher = false,
        hasNoActionBar = false,
        generateActivityTitle = false,
    )
    //生成activity文件
    if (codeLanguage == CodeLanguage.Kotlin) {
        val activityFile = simpleViewBindingActivityKt(
            projectData.applicationPackage,
            parentClass,
            activityClass,
            packageName)
        save(activityFile, srcOut.resolve("${activityClass}Activity.kt"))
    }
    else {
        val activityFile = simpleViewBindingActivityJava(
            projectData.applicationPackage,
            parentClass,
            activityClass,
            packageName)
        save(activityFile, srcOut.resolve("${activityClass}Activity.java"))
    }
    // 保存xml
    val xmlFile = simpleViewBindingActivityXml(packageName, activityClass)
    save(xmlFile, resOut.resolve("layout/${layoutName}.xml"))
}

/*-------------------- activity code generate function ----------------------*/
fun simpleViewBindingActivityKt(
    applicationPackage:String?,
    parentClass:String,
    activityClass:String,
    packageName:String
)="""
package $packageName

import android.os.Bundle
import android.content.Context

import $parentClass
import ${applicationPackage}.R
import ${applicationPackage}.databinding.Activity${activityClass}Binding

${titleComments("author")}
class ${activityClass}Activity : ${parentClass.split(".").last()}() {

    private val viewBinding by lazy {
        return@lazy Activity${activityClass}Binding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        initViews()
        
    }

    private fun initViews() {

    }
} 
"""

fun simpleViewBindingActivityJava(
    applicationPackage:String?,
    parentClass:String,
    activityClass:String,
    packageName:String
)="""
package $packageName;

import android.os.Bundle;
import android.content.Context;

import ${parentClass};
import ${applicationPackage}.R;
import ${applicationPackage}.databinding.Activity${activityClass}Binding;

${titleComments("author")}
public class ${activityClass}Activity extends ${parentClass.split(".").last()} {

     private Activity${activityClass}Binding viewBinding = null;

     @Override
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = Activity${activityClass}Binding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        initViews();
        
    }

    private void initViews() {

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
