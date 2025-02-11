package studcraft.darkness.perks;

import studcraft.darkness.perks.impl.weapon.ArsonistPerk;
import studcraft.darkness.perks.impl.weapon.ChainReactionPerk;
import studcraft.darkness.perks.impl.weapon.FireChargePerk;
import studcraft.darkness.perks.impl.weapon.PoisonPerk;
import studcraft.darkness.perks.impl.weapon.ThunderPerk;

import java.util.HashMap;
import java.util.Map;

public class PerkFactory {

    private static final Map<String, Perk> REGISTERED_PERKS = new HashMap<>();

    static {
        // Weapon
        registerPerk(new PoisonPerk());
        registerPerk(new ArsonistPerk());
        registerPerk(new ThunderPerk());

        registerPerk(new FireChargePerk());


        registerPerk(new ChainReactionPerk());

        // Armor
    }

    public static void registerPerk(Perk perk) {
        REGISTERED_PERKS.put(perk.getId(), perk);
    }

    public static Perk getPerk(String key) {
        return REGISTERED_PERKS.get(key);
    }
}