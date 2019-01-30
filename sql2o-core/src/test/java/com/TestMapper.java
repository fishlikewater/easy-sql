package com;

import scorpio.annotation.Table;
import scorpio.core.BaseMapper;

@Table(table = "t_option",pojo = TestDTO.class,pk = "a_id")
public class TestMapper extends BaseMapper<TestDTO> {
}
