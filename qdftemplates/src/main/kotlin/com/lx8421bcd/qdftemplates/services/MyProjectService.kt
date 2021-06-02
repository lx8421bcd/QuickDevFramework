package com.lx8421bcd.qdftemplates.services

import com.intellij.openapi.project.Project
import com.lx8421bcd.qdftemplates.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
