package com.lx8421bcd.qdftemplates

enum class TemplateType(
    val classSuffix: String,
    val layoutPrefix: String,
    val defaultBaseClass: String,
) {
    Activity(
        "Activity",
        "activity",
        "androidx.appcompat.app.AppCompatActivity"
    ),
    Fragment(
        "Fragment",
        "fragment",
        "androidx.fragment.app.Fragment"
    ),
    Dialog(
        "Dialog",
        "dialog",
        "android.app.Dialog"
    ),
}