package net.firecraftmc.manialib.data.handlers;

import net.firecraftmc.manialib.data.model.DataType;

public class BooleanHandler extends DataTypeHandler<Boolean> {
    public BooleanHandler() {
        super(Boolean.class, DataType.VARCHAR, 5);
    }

    public boolean matchesType(Class<?> clazz) {
        return super.matchesType(clazz) || clazz.isAssignableFrom(boolean.class);
    }

    public Object serializeSql(Object object) {
        if (object.getClass().isAssignableFrom(javaClass)) {
            return object + "";
        }
        return null;
    }

    public Boolean deserialize(Object object) {
        Boolean value = null;
        if (object instanceof Boolean) {
            value = (Boolean) object;
        } else if (object instanceof String) {
            value = Boolean.parseBoolean((String) object);
        }
        return value;
    }

    public String serializeRedis(Object object) {
        return Boolean.toString((boolean) object);
    }
}
