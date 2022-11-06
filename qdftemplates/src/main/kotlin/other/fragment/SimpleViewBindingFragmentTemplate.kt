package other.fragment

import com.android.tools.idea.wizard.template.*
import com.android.tools.idea.wizard.template.impl.activities.common.MIN_API
import other.CodeLanguage
import other.defBaseFragmentPath
import other.humpToLine
import other.titleComments

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
        name = "Simple ViewBinding Fragment"
        description = "基于ViewBinding基类的Fragment模板"
        minApi = MIN_API

        category = Category.Other
        formFactor = FormFactor.Mobile
        screens = listOf(WizardUiContext.FragmentGallery,
            WizardUiContext.MenuEntry,
            WizardUiContext.NewProject,
            WizardUiContext.NewModule
        )

        val codeLanguage = enumParameter<CodeLanguage> {
            name = "Source file language"
            default = CodeLanguage.Java
            help = "选择语言"
        }
        val parentClass = stringParameter {
            name = "Parent class PATH"
            default = defBaseFragmentPath
            help = "输入基类完整路径"
            constraints = listOf(Constraint.NONEMPTY)
        }
        val fragmentClass = stringParameter {
            name = "Fragment name(不包含\"Fragment\")"
            default = "Main"
            help = "只输入名字，不要包含Fragment"
            constraints = listOf(Constraint.NONEMPTY)
        }

        val layoutName = stringParameter {
            name = "Layout name"
            default = "Fragment_main"
            help = "请输入布局的名字"
            constraints = listOf(Constraint.LAYOUT, Constraint.UNIQUE, Constraint.NONEMPTY)
            suggest = { fragmentToLayout(humpToLine(fragmentClass.value)) }
        }

        val packageName = defaultPackageNameParameter

        widgets(
            EnumWidget(codeLanguage),
            TextFieldWidget(parentClass),
            TextFieldWidget(fragmentClass),
            TextFieldWidget(layoutName),
            PackageNameWidget(packageName)
        )
//        thumb { File("logo.png") }
        recipe = { data: TemplateData ->
            simpleViewBindingFragmentRecipe(
                data as ModuleTemplateData,
                codeLanguage.value,
                parentClass.value,
                fragmentClass.value,
                layoutName.value,
                packageName.value)
        }
    }

fun RecipeExecutor.simpleViewBindingFragmentRecipe(
    moduleData: ModuleTemplateData,
    codeLanguage: CodeLanguage,
    parentClass: String,
    fragmentClass: String,
    layoutName: String,
    packageName: String
) {
    val (projectData, srcOut, resOut) = moduleData

    //生成Fragment文件
    if (codeLanguage == CodeLanguage.Kotlin) {
        val fragmentFile = simpleViewBindingFragmentKt(
            projectData.applicationPackage,
            parentClass,
            fragmentClass,
            packageName)
        save(fragmentFile, srcOut.resolve("${fragmentClass}Fragment.kt"))
    }
    else {
        val fragmentFile = simpleViewBindingFragmentJava(
            projectData.applicationPackage,
            parentClass,
            fragmentClass,
            packageName)
        save(fragmentFile, srcOut.resolve("${fragmentClass}Fragment.java"))
    }

    // 保存xml
    val xmlFile = simpleViewBindingFragmentXml(packageName, fragmentClass)
    save(xmlFile, resOut.resolve("layout/${layoutName}.xml"))
}

/*-------------------- Fragment code generate function ----------------------*/
fun simpleViewBindingFragmentKt(
    applicationPackage: String?,
    parentClass: String,
    fragmentClass: String,
    packageName: String
)="""
package $packageName

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.annotation.NonNull
import androidx.annotation.Nullable

import $parentClass
import ${applicationPackage}.R
import ${applicationPackage}.databinding.Fragment${fragmentClass}Binding

${titleComments("author")}
class ${fragmentClass}Fragment : ${parentClass.split(".").last()}() {

    private var viewBinding: Fragment${fragmentClass}Binding? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = Fragment${fragmentClass}Binding.inflate(inflater)
        return viewBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        
    }

    private fun initViews() {

    }
} 
"""

fun simpleViewBindingFragmentJava(
    applicationPackage:String?,
    parentClass:String,
    fragmentClass:String,
    packageName:String
)="""
package $packageName

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ${parentClass};
import ${applicationPackage}.R;
import ${applicationPackage}.databinding.Fragment${fragmentClass}Binding;

${titleComments("author")}
public class ${fragmentClass}Fragment extends ${parentClass.split(".").last()} {

    private Fragment${fragmentClass}Binding viewBinding = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewBinding = Fragment${fragmentClass}Binding.inflate(inflater);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        
    }

    private void initViews() {

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
