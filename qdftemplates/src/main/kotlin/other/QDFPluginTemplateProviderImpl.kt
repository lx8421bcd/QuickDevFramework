package other
import com.android.tools.idea.wizard.template.Template
import com.android.tools.idea.wizard.template.WizardTemplateProvider
import other.activity.SimpleViewBindingActivityTemplate
import other.dialog.SimpleViewBindingDialogTemplate
import other.fragment.SimpleViewBindingFragmentTemplate

class QDFPluginTemplateProviderImpl : WizardTemplateProvider() {

    override fun getTemplates(): List<Template> = listOf(
        // activity的模板
        SimpleViewBindingActivityTemplate,
        // fragment的模板
        SimpleViewBindingFragmentTemplate,
        // dialog的模板
        SimpleViewBindingDialogTemplate,
    )
}