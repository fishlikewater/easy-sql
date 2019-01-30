package com;

import scorpio.annotation.Table;
import scorpio.core.BaseMapper;

@Table(pojo = ResourcesDTO.class ,table = "resources")
public class ResourcesMapper extends BaseMapper {
}
