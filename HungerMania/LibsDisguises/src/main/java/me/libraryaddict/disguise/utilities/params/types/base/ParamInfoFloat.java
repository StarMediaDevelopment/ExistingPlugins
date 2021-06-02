package me.libraryaddict.disguise.utilities.params.types.base;

import me.libraryaddict.disguise.utilities.params.ParamInfo;

/**
 * Created by libraryaddict on 7/09/2018.
 */
public class ParamInfoFloat extends ParamInfo {
    public ParamInfoFloat(String name, String description) {
        super(float.class, name, description);
    }

    @Override
    protected Object fromString(String string) {
        return Float.parseFloat(string);
    }

    @Override
    public String toString(Object object) {
        return object.toString();
    }
}
