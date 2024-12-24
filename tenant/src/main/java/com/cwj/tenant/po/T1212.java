package com.cwj.tenant.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author cwj
 */
@Data
@TableName("t1212")
public class T1212 implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String city;

    private String reportQuarter;

    private String tenantId;

    private String tenantName;

    private String primaryIndustry;

    private String secondaryIndustry;

    private Integer tenantOrigin;

    private String tenantOriginLabel;

    private Integer primaryLeaseType;

    private Integer secondaryLeaseType;

    private Integer tertiaryLeaseType;

    private Double leaseArea;

    private String curPropertyId;

    private String curDistrict;

    private String curSubmarketId;

    private Integer curPropertyType;

    private String curPropertyName;

    private Integer curPropertyGrade;

    private String curPropertyGradeLabel;

    private String curPropertyLonLat;

    private String curPropertyAddress;

    private String curSubmarketName;

    private Integer curPropertyOriginType;

    private String prevPropertyId;

    private String prevDistrict;

    private String prevSubmarketId;

    private String prevSubmarketName;

    private Integer prevPropertyType;

    private String prevPropertyName;

    private Integer prevPropertyGrade;

    private String prevPropertyGradeLabel;

    private String prevPropertyLonLat;

    private String prevPropertyAddress;

    private Integer prevPropertyOriginType;

    private String createTime;

    private String updateTime;


}
