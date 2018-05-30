package com.houlu.data.clean.bean;

/**
 * IndustryEntity
 *
 * @author hl162981
 * @date 2018/5/28
 */
public class IndustryEntity {

  private String id;

  private String name;

  private String level;

  private String children;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public String getChildren() {
    return children;
  }

  public void setChildren(String children) {
    this.children = children;
  }
}
