package other.dialog

import com.android.tools.idea.wizard.template.*
import com.android.tools.idea.wizard.template.impl.activities.common.MIN_API
import other.*

val SimpleViewBindingDialogTemplate
    get() = template {
        name = "Simple ViewBinding Dialog"
        description = "基于ViewBinding基类的Dialog模板"
        minApi = MIN_API

        category = Category.Other
        formFactor = FormFactor.Mobile
        screens = listOf(WizardUiContext.FragmentGallery,
            WizardUiContext.MenuEntry,
            WizardUiContext.NewProject,
            WizardUiContext.NewModule
        )

        val codeLanguage = defaultLanguageSelectParameter
        val parentClass = stringParameter {
            name = "Parent Class PATH"
            default = defBaseDialogPath
            help = "输入基类完整路径"
            constraints = listOf(Constraint.NONEMPTY)
        }
        val dialogClass = stringParameter {
            name = "Dialog Name(不包含\"Dialog\")"
            default = "Main"
            help = "只输入名字，不要包含Dialog"
            constraints = listOf(Constraint.NONEMPTY)
        }

        val layoutName = stringParameter {
            name = "Layout Name"
            default = "Dialog_main"
            help = "请输入布局的名字"
            constraints = listOf(
                Constraint.LAYOUT,
                Constraint.UNIQUE,
                Constraint.NONEMPTY
            )
            suggest = { "dialog_${humpToLine(dialogClass.value)}" }
        }

        val packageName = defaultPackageNameParameter

        widgets(
            EnumWidget(codeLanguage),
            TextFieldWidget(parentClass),
            TextFieldWidget(dialogClass),
            TextFieldWidget(layoutName),
            PackageNameWidget(packageName)
        )
//        thumb { File("logo.png") }
        recipe = { data: TemplateData ->
            simpleViewBindingDialogRecipe(
                data as ModuleTemplateData,
                codeLanguage.value,
                parentClass.value,
                dialogClass.value,
                layoutName.value,
                packageName.value)
        }
    }

fun RecipeExecutor.simpleViewBindingDialogRecipe(
    moduleData: ModuleTemplateData,
    codeLanguage: CodeLanguage,
    parentClass: String,
    dialogClass: String,
    layoutName: String,
    packageName: String
) {
    val (projectData, srcOut, resOut) = moduleData
    val ktOrJavaExt = projectData.language.extension

    //生成Dialog文件
    if (codeLanguage == CodeLanguage.Kotlin) {
        val dialogFile = simpleViewBindingDialogKt(
            projectData.applicationPackage,
            parentClass,
            dialogClass,
            packageName)
        save(dialogFile, srcOut.resolve("${dialogClass}Dialog.kt"))
    }
    else {
        val dialogFile = simpleViewBindingDialogJava(
            projectData.applicationPackage,
            parentClass,
            dialogClass,
            packageName)
        save(dialogFile, srcOut.resolve("${dialogClass}Dialog.java"))
    }

    // 保存xml
    val xmlFile = simpleViewBindingDialogXml(packageName, dialogClass)
    save(xmlFile, resOut.resolve("layout/${layoutName}.xml"))
}

/*-------------------- Dialog code generate function ----------------------*/
fun simpleViewBindingDialogKt(
    applicationPackage:String?,
    parentClass:String,
    dialogClass:String,
    packageName:String
)="""
package $packageName

import android.os.Bundle

import $parentClass
import ${applicationPackage}.R
import ${applicationPackage}.databinding.Dialog${dialogClass}Binding

${titleComments("author")}
class ${dialogClass}Dialog(context: Context) : ${parentClass.split(".").last()}(context) {

    private val viewBinding by lazy {
        return@lazy Dialog${dialogClass}Binding.inflate(layoutInflater)
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

fun simpleViewBindingDialogJava(
    applicationPackage:String?,
    parentClass:String,
    dialogClass:String,
    packageName:String
)="""
package $packageName;

import android.os.Bundle;
import ${parentClass};
import ${applicationPackage}.R;
import ${applicationPackage}.databinding.Dialog${dialogClass}Binding;

${titleComments("author")}
public class ${dialogClass}Dialog extends ${parentClass.split(".").last()} {

    private Dialog${dialogClass}Binding viewBinding = null;

    public ${dialogClass}Dialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = Dialog${dialogClass}Binding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        initViews();
        
    }

    private void initViews() {

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
