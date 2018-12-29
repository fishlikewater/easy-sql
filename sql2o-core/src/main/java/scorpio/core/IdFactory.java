package scorpio.core;

import lombok.extern.slf4j.Slf4j;
import scorpio.exception.BaseRuntimeException;
import scorpio.utils.UUIDUtils;

/**
 * id构造
 */
@Slf4j
public class IdFactory {
    private static IdDefined idDefined;
    /**
     * 获取id
     * @param generator
     * @return
     */
    public static Object getId(Generator generator, IdDefined idDefined) {

        if(generator == Generator.UUID || generator == null){
            return UUIDUtils.get();
        }else if(generator == Generator.DEFINED){
            if(idDefined == null){
               throw new BaseRuntimeException("没有找到id生成器");
            }
            return idDefined.getId();
        }
        return null;
    }
}
