package com.cwj.tenant.po;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;


@Data
public class ProPoint implements Serializable {
    @JSONField(serialize = true)
    String propertyId;
    @JSONField(serialize = true)
    String propertyName;
    @JSONField(serialize = true)
    String propertyAddress;
    @JSONField(serialize = true)
    String propertyType;
    @JSONField(serialize = true)
    String propertyGrade;
    @JSONField(serialize = true)
    Integer tenantNum;
    @JSONField(serialize = true)
    Integer otherTenantNum;
    @JSONField(serialize = true)
    Integer selfTenantNum;
    @JSONField(serialize = true)
    Integer otherSourceTenantNum;
    @JSONField(serialize = true)
    Double tenantArea;
    @JSONField(serialize = true)
    String topicIndustrial;
    @JSONField(serialize = true)
    Double[] center;
}
