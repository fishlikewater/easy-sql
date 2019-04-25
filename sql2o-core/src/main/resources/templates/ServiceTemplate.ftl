package ${package}.service;

import ${package}.entity.${className};
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ${package}.mapper.${className}Mapper;
import org.apache.commons.lang.StringUtils;

@Service
public class ${className}Service {

@Autowired
private ${className}Mapper ${dao}Mapper;


}