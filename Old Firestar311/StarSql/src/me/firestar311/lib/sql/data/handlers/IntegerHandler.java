package me.firestar311.lib.sql.data.handlers;

import me.firestar311.lib.sql.data.model.DataType;

public class IntegerHandler extends DataTypeHandler<Integer> {
    public IntegerHandler() {
        super(Integer.class, DataType.INT);
    }

    public boolean matchesType(Class<?> clazz) {
        return super.matchesType(clazz) || clazz.isAssignableFrom(int.class);
    }

    public Object serializeSql(Object object) {
        return object;
    }

    public Integer deserialize(Object object) {
        Integer value = null;
        if (object instanceof Integer) {
            value = (Integer) object;
        } else if (object instanceof String) {
            value = Integer.parseInt((String) object);
        }
        return value;
    }

    public String serializeRedis(Object object) {
        return Integer.toString((int) object);
    }
}
