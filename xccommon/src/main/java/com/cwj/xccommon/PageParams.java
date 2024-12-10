package com.cwj.xccommon;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PageParams {

    private Long PageNo=1L;
    private Long PageSize=10L;

}
