package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class PaginationVO<T> implements Serializable {

    private static final long serialVersionUID = 8982631944346908877L;

    private Long total ;

    private List<T> dataList;

}
