package com;

import scorpio.annotation.Table;
import scorpio.core.BaseDAO;

@Table(table = "t_option",pojo = TestDTO.class,pk = "a_id")
public class TestDAO extends BaseDAO<TestDTO>{
}
