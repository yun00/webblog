package com.yun.hello.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int age;

    public User() {
    }
    public User(Long id,String name,int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return this.age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString(){
        return String.format("User[id=%d,name=%s,age=%d]", this.id,this.name,this.age);
    }
}
