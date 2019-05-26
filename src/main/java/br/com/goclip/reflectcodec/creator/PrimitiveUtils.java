package br.com.goclip.reflectcodec.creator;

public class PrimitiveUtils {

    public static Class<?> mapToBoxed(Class<?> primitive) {
        switch (primitive.getSimpleName()) {
            case "int":
                return Integer.class;
            case "char":
                return Character.class;
            case "long":
                return Long.class;
            case "double":
                return Double.class;
            case "float":
                return Float.class;
            case "byte":
                return Byte.class;
            case "boolean":
                return Boolean.class;
            default:
                return primitive;
        }
    }

    public static Object defaultValue(Class<?> primitive) {
        switch (primitive.getSimpleName()) {
            case "int":
                return 0;
            case "char":
                return -1;
            case "long":
                return 0L;
            case "double":
                return 0d;
            case "float":
                return 0f;
            case "byte":
                return 0x0;
            case "boolean":
                return false;
            default:
                return null;
        }
    }
}
