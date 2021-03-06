package me.firestar311.lib.sql.data.model;

import me.firestar311.lib.sql.data.DatabaseManager;
import me.firestar311.lib.sql.data.annotations.ColumnInfo;
import me.firestar311.lib.sql.util.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Row {
    protected Map<String, Object> dataMap = new HashMap<>();
    protected Table table;
    
    public Row(Table table, ResultSet resultSet) {
        this.table = table;
        for (Column column : table.getColumns()) {
            try {
                Object object = column.typeHandler.deserialize(resultSet.getObject(column.getName()));
                this.dataMap.put(column.getName(), object);
            } catch (Exception throwables) {
                System.out.println("Error on getting column value " + column.getName() + " of table " + table.getName());
                throwables.printStackTrace();
            }
        }
    }
    
    public Map<String, Object> getDataMap() {
        return dataMap;
    }
    
    public int getInt(String key) {
        return (int) dataMap.get(key);
    }
    
    public <T extends IRecord> T getRecord(Class<T> recordClass, DatabaseManager databaseManager) {
        try {
            Constructor<?> constructor = recordClass.getDeclaredConstructor();
            if (constructor == null) {
                return null;
            }
            constructor.setAccessible(true);
            T record = (T) constructor.newInstance();
            Set<Field> fields = Utils.getClassFields(recordClass);
            for (Field field : fields) {
                field.setAccessible(true);
                ColumnInfo columnInfo = field.getAnnotation(ColumnInfo.class);
                if (columnInfo != null) {
                    if (columnInfo.ignored()) continue;
                }
                field.set(record, this.dataMap.get(field.getName()));
            }
            return record;
        } catch (Exception e) {
            System.out.println("An error occurred loading a record for the class " + recordClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public String getString(String key) {
        return (String) this.dataMap.get(key);
    }
    
    public long getLong(String key) {
        return (long) this.dataMap.get(key);
    }
    
    public boolean getBoolean(String key) {
        return (boolean) this.dataMap.get(key);
    }
    
    public double getDouble(String key) {
       return (double) this.dataMap.get(key);
    }
    
    public float getFloat(String key) {
        return (float) this.dataMap.get(key);
    }
    
    public UUID getUUID(String key) {
        return (UUID) this.dataMap.get(key);
    }
}