package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.friends.FriendRequest;
import net.firecraftmc.manialib.sql.DataType;
import net.firecraftmc.manialib.sql.Database;
import net.firecraftmc.manialib.sql.IRecord;
import net.firecraftmc.manialib.sql.Row;
import net.firecraftmc.manialib.sql.Table;

import java.util.HashMap;
import java.util.Map;

public class FriendRequestRecord implements IRecord<FriendRequest> {
    
    public FriendRequestRecord(Row row) {
        this.object = FriendRequest.builder().id(row.getInt("id")).sender(row.getUUID("from")).to(row.getUUID("to")).timestamp(row.getLong("timestamp")).build();
    }
    
    private FriendRequest object;
    
    public FriendRequestRecord(FriendRequest object) {
        this.object = object;
    }
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "friendrequests");
        table.addColumn("id", DataType.INT, true, true);
        table.addColumn("sender", DataType.VARCHAR, 36);
        table.addColumn("to", DataType.VARCHAR, 36);
        table.addColumn("timestamp", DataType.BIGINT);
        return table;
    }
    
    public int getId() {
        return object.getId();
    }
    
    public void setId(int id) {
        object.setId(id);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", object.getId());
            put("sender", object.getSender().toString());
            put("to", object.getTo().toString());
            put("timestamp", object.getTimestamp());
        }};
    }
    
    public FriendRequest toObject() {
        return object;
    }
}