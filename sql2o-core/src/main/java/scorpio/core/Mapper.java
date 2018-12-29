package scorpio.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangx
 * 该类主要是实体与数据库字段对应关系
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mapper {

    private String column;

    private String property;
}
