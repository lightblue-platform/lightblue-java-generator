package com.redhat.lightblue.generator.entities;

import java.util.Date;

public class SimpleUser {
  private String name;
  private Integer age;
  private Date birthday;
  private Boolean cool;

  public Boolean getCool() {
    return cool;
  }

  public void setCool(Boolean cool) {
    this.cool = cool;
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
