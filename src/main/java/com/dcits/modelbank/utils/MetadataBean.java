package com.dcits.modelbank.utils;

/**
 * Created on 2017-11-10 17:24.
 *
 * @author kevin
 */
public class MetadataBean {
    private String groupName;
    private String type;
    private String length;
    private String chineseName;
    private String remark;
    private String scale;

    public final String getGroupName() {
        return groupName;
    }

    public final void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public final String getType() {
        return type;
    }

    public final void setType(String type) {
        this.type = type;
    }

    public final String getLength() {
        return length;
    }

    public final void setLength(String length) {
        this.length = length;
    }

    public final String getChineseName() {
        return chineseName;
    }

    public final void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public final String getRemark() {
        return remark;
    }

    public final void setRemark(String remark) {
        this.remark = remark;
    }

    public final String getScale() {
        return scale;
    }

    public final void setScale(String scale) {
        this.scale = scale;
    }
}
