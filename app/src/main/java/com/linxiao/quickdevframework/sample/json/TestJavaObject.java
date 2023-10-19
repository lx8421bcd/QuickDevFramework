package com.linxiao.quickdevframework.sample.json;

public class TestJavaObject {
    public int id = 0;
    public String name = "JavaObject";
    public int age = 0;
    public int gender = 1;
    public String study = "high school";

    @Override
    public String toString() {
        return "TestJavaObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", study='" + study + '\'' +
                '}';
    }
}
