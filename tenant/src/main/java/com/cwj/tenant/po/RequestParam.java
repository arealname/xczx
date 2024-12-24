package com.cwj.tenant.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestParam implements Serializable {
    String dimension;
    String endTime;
    String propertyId;
    String propertyType;
    String startTime;

}
