package com.linxiao.quickdevframework.sample.json

class TestKtObject {
    var id = "000000"
    var name = "KtObject"
    var age = 0
    var gender = 1
    lateinit var study: String
    override fun toString(): String {
        if (!this::study.isInitialized) {
            study = "high school"
        }
        return "TestKtObject(id='$id', name='$name', age=$age, gender=$gender, study='$study')"
    }
}