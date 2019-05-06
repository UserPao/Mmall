package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
//get set 的注解
@Data
//无参构造器的注解
@NoArgsConstructor
//全参构造器的注解
@AllArgsConstructor
@ToString
public class Cart implements Serializable {
    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private Integer checked;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;


}