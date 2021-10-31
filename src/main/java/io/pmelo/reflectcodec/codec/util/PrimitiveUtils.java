package io.pmelo.reflectcodec.codec.util;

public class PrimitiveUtils {

    public static Class<?> mapToBoxedType(Class<?> type) {
        switch (type.getName()) {
            case "byte":
                return Byte.class;
            case "short":
                return Short.class;
            case "int":
                return Integer.class;
            case "long":
                return Long.class;
            case "float":
                return Float.class;
            case "double":
                return Double.class;
            case "boolean":
                return Boolean.class;
            default:
                return Character.class;
        }
    }

}
