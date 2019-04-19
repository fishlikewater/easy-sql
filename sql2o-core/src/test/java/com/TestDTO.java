package com;

import scorpio.annotation.Column;
import scorpio.annotation.IdGenerator;
import scorpio.annotation.Transient;
import scorpio.core.BaseModel;
import scorpio.core.Generator;

import java.sql.Types;

public class TestDTO extends BaseModel {
    @IdGenerator(value = Generator.DEFINED, idclass = MyIdFactory.class)
    private String aId;

    @Column(value = "name",type = Types.VARCHAR, length = 20,nullable = false)
    private String name;

    @Column(value = "flag",type = Types.BOOLEAN,defaultValue = "0")
    private Boolean flag;

    @Transient
    private String scrq;

    public String getaId() {
        return aId;
    }

    public void setaId(String aId) {
        this.aId = aId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScrq() {
        return scrq;
    }

    public void setScrq(String scrq) {
        this.scrq = scrq;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "TestDTO{" +
                "aId='" + aId + '\'' +
                ", name='" + name + '\'' +
                ", flag=" + flag +
                ", scrq='" + scrq + '\'' +
                '}';
    }
}
