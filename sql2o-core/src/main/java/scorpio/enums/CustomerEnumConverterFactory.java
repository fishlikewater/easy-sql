package scorpio.enums;

import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;
import org.sql2o.converters.EnumConverterFactory;

/**
 * Default implementation of {@link EnumConverterFactory},
 * used by sql2o to convert a value from the database into an {@link Enum}.
 */
public class CustomerEnumConverterFactory implements EnumConverterFactory {
    public <E extends Enum> Converter<E> newConverter(final Class<E> enumType) {
        return new Converter<E>() {
            @SuppressWarnings("unchecked")
            public E convert(Object val) throws ConverterException {
                if (val == null) {
                    return null;
                }
                try {
                    E[] enumConstants = enumType.getEnumConstants();
                    if(!IEnum.class.isAssignableFrom(enumType)){
                        if (val instanceof String){
                            return (E)Enum.valueOf(enumType, val.toString());
                        } else if (val instanceof Number){
                            return enumConstants[((Number)val).intValue()];
                        }
                    }else {
                        for (Enum item: enumConstants) {
                            Object saveFieid = ((IEnum) item).getSaveFieid();
                            if(val instanceof String){
                                if(String.valueOf(saveFieid).equals(val)){
                                    return (E) item;
                                }
                            }else{
                                if(((Number)val).intValue() == (int) saveFieid){
                                    return (E) item;
                                }
                            }
                        }
                        return null;
                    }
                } catch (Throwable t) {
                    throw new ConverterException("Error converting value '" + val.toString() + "' to " + enumType.getName(), t);
                }
                throw new ConverterException("Cannot convert type '" + val.getClass().getName() + "' to an Enum");
            }

            public Object toDatabaseParam(Enum val) {
                if(val instanceof IEnum){
                    return ((IEnum)val).getSaveFieid();
                }else {
                    return val.name();
                }
            }
        };
    }
}
