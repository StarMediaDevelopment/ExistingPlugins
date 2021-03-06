package me.firestar311.lib.sql.data.handlers;

import lombok.Getter;
import me.firestar311.lib.sql.data.model.DataType;

public abstract class DataTypeHandler<T> {
    
    @Getter protected Class<?> javaClass;
    @Getter protected DataType mysqlType;
    @Getter protected int defaultLength;

    public DataTypeHandler(Class<?> javaClass, DataType mysqlType) {
        this.javaClass = javaClass;
        this.mysqlType = mysqlType;
    }

    public DataTypeHandler(Class<?> javaClass, DataType mysqlType, int defaultLength) {
        this(javaClass, mysqlType);
        this.defaultLength = defaultLength;
    }
    
    protected DataTypeHandler() {}
    
    public boolean matchesType(Class<?> clazz) {
       return clazz.isAssignableFrom(javaClass);
    }

    public abstract Object serializeSql(Object object);
    public abstract T deserialize(Object object);
    public abstract String serializeRedis(Object object);
}
