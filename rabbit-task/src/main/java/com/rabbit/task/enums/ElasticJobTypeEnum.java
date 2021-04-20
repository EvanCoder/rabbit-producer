package com.rabbit.task.enums;

/**
 * @author Evan
 * @create 2021/2/23 13:44
 */
public enum ElasticJobTypeEnum {

    SIMPLE("SimpleJob", "简单类型Job"),
    DATAFLOW("DataflowJob", "流式类型Job"),
    SCRIPT("ScriptJob", "流式类型Job");

    private String type;

    private String desc;

    private ElasticJobTypeEnum(String type, String desc){
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
