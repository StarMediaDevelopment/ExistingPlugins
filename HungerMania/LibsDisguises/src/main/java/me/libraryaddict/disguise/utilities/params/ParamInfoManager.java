package me.libraryaddict.disguise.utilities.params;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.FallingBlockWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.LivingWatcher;
import me.libraryaddict.disguise.utilities.params.types.custom.ParamInfoItemBlock;
import me.libraryaddict.disguise.utilities.params.types.custom.ParamInfoSoundGroup;
import me.libraryaddict.disguise.utilities.parser.DisguisePerm;
import me.libraryaddict.disguise.utilities.parser.WatcherMethod;
import me.libraryaddict.disguise.utilities.watchers.DisguiseMethods;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ParamInfoManager {
    private static List<ParamInfo> paramList;
    private static DisguiseMethods disguiseMethods;
    private static ParamInfoItemBlock paramInfoItemBlock;
    private static ParamInfoSoundGroup paramInfoSoundGroup;

    public static List<ParamInfo> getParamList() {
        return paramList;
    }

    public static DisguiseMethods getDisguiseMethods() {
        return disguiseMethods;
    }

    public static ParamInfoItemBlock getParamInfoItemBlock() {
        return paramInfoItemBlock;
    }

    public static ParamInfoSoundGroup getParamInfoSoundGroup() {
        return paramInfoSoundGroup;
    }

    public static List<ParamInfo> getParamInfos() {
        return paramList;
    }

    public static String toString(Object object) {
        if (object == null) {
            return "null";
        }

        ParamInfo info = getParamInfo(object.getClass());

        if (info == null) {
            throw new IllegalArgumentException(object.getClass() + " is not handled by ParamInfo!");
        }

        return info.toString(object);
    }

    public static ParamInfo getParamInfo(WatcherMethod method) {
        if (method.getName().equals("setSoundGroup")) {
            return getParamInfoSoundGroup();
        }

        if (method.getWatcherClass() == FallingBlockWatcher.class && (method.getParam() == Material.class || method.getParam() == ItemStack.class)) {
            return getParamInfoItemBlock();
        }

        return getParamInfo(method.getParam());
    }

    public static ParamInfo getParamInfo(Class c) {
        for (ParamInfo info : getParamInfos()) {
            if (!info.isParam(c)) {
                continue;
            }

            return info;
        }

        return null;
    }

    public static ParamInfo getParamInfo(DisguisePerm disguiseType, String methodName) {
        return getParamInfo(disguiseType.getType(), methodName);
    }

    public static ParamInfo getParamInfo(DisguiseType disguiseType, String methodName) {
        for (WatcherMethod method : getDisguiseWatcherMethods(disguiseType.getWatcherClass())) {
            if (!method.getName().toLowerCase(Locale.ENGLISH).equals(methodName.toLowerCase(Locale.ENGLISH))) {
                continue;
            }

            return getParamInfo(method);
        }

        return null;
    }

    static {
        ParamInfoTypes infoTypes = new ParamInfoTypes();
        paramList = infoTypes.getParamInfos();
        paramInfoItemBlock = infoTypes.getParamInfoBlock();
        paramInfoSoundGroup = (ParamInfoSoundGroup) paramList.stream().filter(p -> p instanceof ParamInfoSoundGroup).findAny().orElse(null);
        disguiseMethods = new DisguiseMethods();

        //paramList.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));
    }

    public static WatcherMethod[] getDisguiseWatcherMethods(@Nullable Class<? extends FlagWatcher> watcherClass) {
        if (watcherClass == null) {
            return new WatcherMethod[0];
        }

        ArrayList<WatcherMethod> methods = new ArrayList<>(disguiseMethods.getMethods(watcherClass));

        // Order first by their declaring class, the top class (SheepWatcher) goes before (FlagWatcher)
        // Order methods in the same watcher by their name from A to Z
        methods.sort((m1, m2) -> {
            int v1 = getValue(m1);
            int v2 = getValue(m2);

            if (v1 != v2) {
                return v1 - v2;
            }

            return String.CASE_INSENSITIVE_ORDER.compare(m1.getName(), m2.getName());
        });



        return methods.toArray(new WatcherMethod[0]);
    }

    /**
     * Value of the method, used namely for ordering the more unique methods to a disguise
     */
    public static int getValue(WatcherMethod method) {
        Class<?> declaring = method.getWatcherClass();

        if (declaring == LivingWatcher.class) {
            return 1;
        } else if (!(FlagWatcher.class.isAssignableFrom(declaring)) || declaring == FlagWatcher.class) {
            return 2;
        }

        return 0;
    }
}
