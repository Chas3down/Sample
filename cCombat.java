import org.parabot.core.ui.components.LogArea;
import org.parabot.environment.api.interfaces.Paintable;
import org.parabot.environment.api.utils.Filter;
import org.parabot.environment.api.utils.Timer;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.framework.Strategy;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.ScriptManifest;
import org.rev317.api.methods.*;
import org.rev317.api.methods.Menu;
import org.rev317.api.wrappers.hud.Item;
import org.rev317.api.wrappers.hud.Tab;
import org.rev317.api.wrappers.interactive.Npc;
import org.rev317.api.wrappers.scene.SceneObject;


import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;


@ScriptManifest(author = "Chas3down", category = Category.COMBAT, description = "Kills, drinks pots, and banks", name = "Goblins", servers = {"PkHonor"}, version = 1)
public class cCombat extends Script implements Paintable {

    private final ArrayList<Strategy> strategies = new ArrayList<Strategy>();
    public Timer runTimer = new Timer();
    public final int MONSTER_ID = 101;
    public final int FOOD_ID = 379;
    public final int[] STRENGTH_ID = {113, 115, 117, 119};
    public final int[] ATTACK_ID = {2428, 121, 123, 125};
    public final double LOW_PRCT = .50;
    public final double HIGH_PRCT = .75;
    public final int START_EXP = Skill.DEFENSE.getExperience();
    public final int AIR_RUNE_ID = 556;
    public final int LAW_RUNE_ID = 563;
    public final int FULL_STR_ID = 113;
    public final int FULL_ATT_ID = 2428;


    public boolean onExecute() {
        runTimer.restart();
        strategies.add(new Sleeping());
        strategies.add(new Back());
        strategies.add(new Attack());
        strategies.add(new Eat());
        strategies.add(new Drink());
        strategies.add(new Banks());
        strategies.add(new Close());

        provide(strategies);

        return true;
    }

    @Override
    public void onFinish() {
        LogArea.log("Script stopped.");
    }

    @Override
    public void paint(Graphics g1) {
        final Color FONT_COLOR = Color.GREEN;
        final Font DEF_FONT = new Font("", Font.BOLD, 11);

        int expGained = Skill.DEFENSE.getExperience() - START_EXP;

        Graphics2D g = (Graphics2D) g1;

        g.setFont(DEF_FONT);
        g.setColor(FONT_COLOR);

        g.drawString("Runetime: " + runTimer.getElapsedTime(), 7,275);
        g.drawString("Exp Gained: " + formatNumber(expGained), 7, 290);
        g.drawString("Exp/Hr: " + runTimer.getPerHour(expGained), 7, 305);
        g.drawString("Version: 1.00", 7, 320);

    }

    class Attack implements Strategy {

        final int MAX_HP = Players.getLocal().getMaxHealth();

        public boolean activate() {
            if (Npcs.getNearest(MONSTER_ID) != null && Npcs.getNearest(MONSTER_ID).length > 0
                    && Npcs.getNearest(MONSTER_ID)[0].getDef() != null
                    && Npcs.getNearest(MONSTER_ID)[0].getDef().getId() != 0 ) {

                final Npc[] NPC_ARRAY = Npcs.getNearest(MONSTER_ID);

                if (NPC_ARRAY != null && NPC_ARRAY.length > 0) {
                    for (Npc i : NPC_ARRAY) {
                        if (i != null && !i.isInCombat()) {
                            return Inventory.getCount(FOOD_ID) >= 1
                                    && !Players.getLocal().isInCombat()
                                    && !Players.getLocal().isWalking()
                                    && currentHp() >= MAX_HP * LOW_PRCT
                                    && Players.getLocal().getAnimation() == -1;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void execute() {
            if (Npcs.getNearest(MONSTER_ID) != null) {
                final Npc[] NPC_ARRAY = Npcs.getNearest(new Filter<Npc>() {
                    @Override
                    public boolean accept(final Npc npc) {
                        return (npc != null && npc.getDef() != null &&
                                npc.getDef().getId() != 0 && !npc.isInCombat() &&
                                npc.getDef().getId() == MONSTER_ID);
                    }

                });
                if (NPC_ARRAY != null && NPC_ARRAY.length > 0) {
                    final Npc NPC = NPC_ARRAY[0];
                    if (NPC != null && NPC.getModel() != null && !NPC.isOnScreen()
                            && !Players.getLocal().isWalking()) {
                        NPC.getLocation().clickMM();
                        Camera.turnTo(NPC);

                    }
                    if (NPC != null && NPC.getDef() != null && NPC.getDef().getId() != 0
                            && NPC.getModel() != null && NPC.isOnScreen() && Players.getLocal() != null &&
                            !Players.getLocal().isInCombat() && !Players.getLocal().isWalking() && NPC.isOnScreen()) {
                        NPC.interact("Attack");
                        sleep(100);
                    }
                }
            }
        }
    }

    class Eat implements Strategy {

        final int MAX_HP = Players.getLocal().getMaxHealth();
        @Override
        public boolean activate() {
            if (Inventory.getCount(FOOD_ID) >= 1) {
                return currentHp() < MAX_HP * LOW_PRCT;
            }

            return false;
        }

        @Override
        public void execute() {
            if (Inventory.getCount(FOOD_ID) >= 1 && currentHp() < MAX_HP * LOW_PRCT) {
                if (!Tab.INVENTORY.isOpen()) {
                    Tab.INVENTORY.open();
                } else {
                    for (final Item i : Inventory.getItems(FOOD_ID)) {
                        if (currentHp() < MAX_HP * HIGH_PRCT) {
                            i.interact("Eat");
                            //Dynamic sleep
                            while (Players.getLocal().getAnimation() != -1){
                            sleep(250);
                            }
                        }
                    }
                }
            }

        }
    }

    class Drink implements Strategy {

        final int MAX_HP = Players.getLocal().getMaxHealth();
        @Override
        public boolean activate() {

            if (currentHp() >= MAX_HP * LOW_PRCT && Inventory.getCount(FOOD_ID) >= 1) {
                if (Inventory.getCount(STRENGTH_ID) >= 1 && Skill.STRENGTH.getLevel() <= Skill.STRENGTH.getRealLevel()) {
                    return true;
                }else if (Inventory.getCount(ATTACK_ID) >= 1 && Skill.ATTACK.getLevel() <= Skill.ATTACK.getRealLevel()) {
                    return true;
                }

            }

            return false;
        }

        @Override
        public void execute() {
            if (currentHp() >= MAX_HP * LOW_PRCT) {
                if (Skill.STRENGTH.getLevel() <= Skill.STRENGTH.getRealLevel() + 1 && Inventory.getCount(STRENGTH_ID) >= 1) {
                    for (final Item i : Inventory.getItems(STRENGTH_ID)) {
                        if (!Tab.INVENTORY.isOpen()) {
                            Tab.INVENTORY.open();
                        }
                        if (Skill.STRENGTH.getLevel() <= Skill.STRENGTH.getRealLevel() + 1 && Inventory.getCount(STRENGTH_ID) >= 1) {
                            i.interact("Drink");
                            //Dynamic sleep
                            while (Players.getLocal().getAnimation() != -1){
                            sleep(100);
                            }
                        }
                    }
                }
                if (Skill.ATTACK.getLevel() <= Skill.ATTACK.getRealLevel() && Inventory.getCount(ATTACK_ID) >= 1) {
                    for (final Item i : Inventory.getItems(ATTACK_ID)) {
                        if (!Tab.INVENTORY.isOpen()) {
                            Tab.INVENTORY.open();
                        }
                        if (Skill.ATTACK.getLevel() <= Skill.ATTACK.getRealLevel() && Inventory.getCount(ATTACK_ID) >= 1) {
                            i.interact("Drink");
                            //Dynamic sleep
                            while (Players.getLocal().getAnimation() != -1){
                                sleep(100);
                            }
                        }
                    }
                }
            }
        }
    }

    class Banks implements Strategy {

        @Override
        public boolean activate() {
            return Inventory.getCount(FOOD_ID) < 1;
        }

        @Override
        public void execute() {

            final SceneObject[] banks = Bank.getNearestBanks();
            SceneObject bankBooth = null;
            if (banks.length > 0) {
                bankBooth = banks[0];
            }

            if (bankBooth == null) {
                if (!Tab.MAGIC.isOpen()) {
                    Menu.interact("Magic", new Point(742, 184));
                    sleep(500);
                } else {
                    Magic.clickSpell(Magic.AncientMagic377.HOME_TELEPORT);
                    sleep(2500);
                }
            } else if (!Bank.isOpen()) {
                if (!Bank.getBank().isOnScreen()) {
                    Camera.turnTo(Bank.getBank());
                    sleep(1000);
                } else {
                    Bank.getBank().interact("Use-quickly");
                    sleep(5000);
                }
            } else if (Bank.isOpen()) {
                if (!Inventory.isEmpty()){
                    Bank.depositAll();
                    sleep(500);
                }
                if (Bank.getItem(FOOD_ID).getStackSize() >= 1 && Bank.getItem(AIR_RUNE_ID).getStackSize() >= 1
                        && Bank.getItem(LAW_RUNE_ID).getStackSize() >= 1 && Bank.getItem(FULL_ATT_ID).getStackSize() >= 1
                        && Bank.getItem(FULL_STR_ID).getStackSize() >= 1) {
                    Bank.getItem(LAW_RUNE_ID).interact("Withdraw 5");
                    sleep(500);
                    Bank.getItem(AIR_RUNE_ID).interact("Withdraw 10");
                    sleep(500);
                    Bank.getItem(FULL_ATT_ID).interact("Withdraw 1");
                    sleep(500);
                    Bank.getItem(FULL_STR_ID).interact("Withdraw 1");
                    sleep(500);
                    Bank.getItem(FOOD_ID).interact("Withdraw All");
                    sleep(500);
                    Bank.close();
                } else {
                    setState(Script.STATE_STOPPED);
                }
            }

        }
    }

    class Close implements Strategy {

        @Override
        public boolean activate() {

            return Bank.isOpen() && Inventory.getCount(FOOD_ID) >= 1;
        }

        @Override
        public void execute() {
            Bank.close();
        }
    }

    class Back implements Strategy {

        @Override
        public boolean activate() {
            return Npcs.getNearest(MONSTER_ID) != null
                    && Npcs.getNearest(MONSTER_ID).length < 1
                    && Inventory.getCount(FOOD_ID) > 0;
        }

        @Override
        public void execute() {
            if (!Tab.MAGIC.isOpen()) {
                Menu.interact("Magic", new Point(742,184));
                //Static sleep makes sense here..
                sleep(200);
            } else {
                Magic.clickSpell(Magic.AncientMagic377.SENNTISTEN_TELEPORT);
                //Static sleep makes sense here..
                sleep(200);
            }
            if (Interfaces.getChatboxInterface() != null && Interfaces.getChatboxInterface().isVisible()
                    && !Interfaces.getChatboxInterface().getText().contains("Congra")){
                Menu.interact("Ok", new Point(265,380));
                //Static sleep makes sense here..
                sleep(250);
                //Dynamic sleep until teleport is finished
                while (Players.getLocal().getAnimation() != -1){
                    sleep(100);
                }

            }

        }
    }
    //General dynamic sleep while moving
    class Sleeping implements Strategy {

        @Override
        public boolean activate() {
            return Players.getLocal().isWalking();
        }

        @Override
        public void execute() {
            while(Players.getLocal().isWalking()){
                sleep(250);
            }
        }
    }

    public static int currentHp() {
        String CurrentHp = Interfaces.get(3918).getChildren()[11].getText().replace("@whi@", "");
        return Integer.parseInt(CurrentHp);
    }

    public String formatNumber(double start) {
        DecimalFormat decFormat = new DecimalFormat("0.00");

        if (start >= 1000000) {
            return decFormat.format((start / 1000000)) + "m";
        }
        if (start >= 0) {
            return decFormat.format((start / 1000)) + "k";
        }
        return "" + start;
    }
}
