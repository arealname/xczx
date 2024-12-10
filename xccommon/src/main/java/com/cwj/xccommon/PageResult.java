package com.cwj.xccommon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private List<T> items;   //列表结果

    private Long counts;//总记录数


    private Long pages;//总页数

    //当前页码
    private long currentpage=1L;

    //每页记录数
    private long pageSize=10L;


}
