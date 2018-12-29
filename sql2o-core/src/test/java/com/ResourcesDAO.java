package com;

import scorpio.annotation.Table;
import scorpio.core.BaseDAO;

@Table(pojo = ResourcesDTO.class ,table = "resources")
public class ResourcesDAO extends BaseDAO {
}
