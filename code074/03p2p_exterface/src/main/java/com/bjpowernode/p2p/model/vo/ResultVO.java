package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class ResultVO implements Serializable {
    private static final long serialVersionUID = -2503975135054334694L;
    /**
     * 展示是否成功
     * success | fail
     *
     */
    private String showCode;
}
