package com.starmediadev.com.common.objects.faction;

import com.starmediadev.com.common.enums.EnumFaction;
import com.starmediadev.com.common.enums.EnumRole;
import com.starmediadev.com.common.objects.abstraction.Faction;

import java.util.List;

public class FactionNeutral extends Faction {

    public FactionNeutral() {
        super("Neutral", EnumFaction.NEUTRAL);
    }
    
    public List<EnumRole> getRoles() {
        return EnumRole.NEUTRAL_ROLES();
    }
}