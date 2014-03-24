import org.parabot.environment.api.utils.Filter;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.framework.Strategy;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.ScriptManifest;
import org.rev317.api.methods.*;
import org.rev317.api.wrappers.hud.Item;
import org.rev317.api.wrappers.hud.Tab;
import org.rev317.api.wrappers.interactive.Npc;


import java.util.ArrayList;


@ScriptManifest(author = "Chas3down", category = Category.COMBAT, description = "Kill Shit", name = "Kill Shit", servers = { "PkHonor" }, version = 1)
public class cCombat extends Script{

    private final ArrayList<Strategy> strategies = new ArrayList<Strategy>();
    public final int MonsterID = 1459;
    public final int FoodID = 379;
    public final int[] LootID = {1,2,3};
    public final int[] StrengthID = {113,115,117,119};
    public final int[] AttackID = {2428,121,123,125};
    public final int MaxHL = Players.getLocal().getMaxHealth();
    public final int LowHP = 10;
    public final double EatLowPerct = .50;
    public final double EatTopPerct = .75;


    @Override
    public boolean onExecute() {

            Item[] i = Inventory.getItems();

                i[0].interact("Drink");

        strategies.add(new Attack());
        strategies.add(new Eat());
        strategies.add(new Drink());
        strategies.add(new Loot());
        provide(strategies);



        return true;
    }

    @Override
    public void onFinish() {

    }

    class Attack implements Strategy {

        @Override
        public boolean activate() {

            for(Npc i : Npcs.getNearest(MonsterID)) {
                if(i != null) {
                    if(!i.isInCombat()){
                        return Inventory.getCount(FoodID) > 1
                                && !Players.getLocal().isInCombat()
                                && !Players.getLocal().isWalking()
                                && getHp() >= MaxHL*EatLowPerct
                                && Players.getLocal().getAnimation() == -1;

                    }
                }
            }



            return false;

        }

        @Override
        public void execute() {

            Npc[] npc = Npcs.getNearest(new Filter<Npc>() {
                @Override
                public boolean accept(final Npc npc) {
                    return (npc != null && !npc.isInCombat() && npc.getDef().getId() == MonsterID);
                }

            });
            if (npc != null && npc.length > 0){
                Npc MonsterA = npc[0];
                if (!MonsterA.isOnScreen()){
                    Camera.turnTo(MonsterA);
                    sleep(1000);
                }
                if (!Players.getLocal().isWalking() && !Players.getLocal().isInCombat() && Players.getLocal().getAnimation() == -1) {
                    MonsterA.interact("Attack");
                }
                sleep(2000);
            }







        }
    }

    class Eat implements Strategy {


        @Override
        public boolean activate() {
            if(Inventory.getCount(379) >= 1){
                return getHp() < MaxHL*EatLowPerct;
            }

            return false;
        }

        @Override
        public void execute() {
            if(Inventory.getCount(379) >= 1 && getHp() < MaxHL*EatLowPerct) {
                if(!Tab.INVENTORY.isOpen()){
                    Tab.INVENTORY.open();
                }else{
                    for(final Item i : Inventory.getItems(379)) {
                        if(getHp() < MaxHL*EatTopPerct){
                            i.interact("Eat");
                            sleep(1000);
                        }
                    }
                }
            }

        }
    }

    class Drink implements Strategy {


        @Override
        public boolean activate() {

            if(getHp() >= MaxHL*EatLowPerct){
                if(Inventory.getCount(StrengthID) >= 1 || Inventory.getCount(AttackID) >= 1){
                    return Skill.STRENGTH.getLevel() <= Skill.STRENGTH.getRealLevel()
                            || Skill.ATTACK.getLevel() <= Skill.ATTACK.getRealLevel();
                }

            }

            return false;
        }

        @Override
        public void execute() {
            if(getHp() >= MaxHL*EatLowPerct){
                if(Skill.ATTACK.getLevel() <= Skill.ATTACK.getRealLevel() && Inventory.getCount(AttackID) >= 1){
                    for(final Item i : Inventory.getItems(AttackID)) {
                        if(!Tab.INVENTORY.isOpen()){
                            Tab.INVENTORY.open();
                        }
                        if(Skill.ATTACK.getLevel() <= Skill.ATTACK.getRealLevel() && Inventory.getCount(AttackID) >= 1){
                            i.interact("Drink");
                            sleep(1000);
                        }
                    }
                }
                if(Skill.STRENGTH.getLevel() <= Skill.STRENGTH.getRealLevel() && Inventory.getCount(StrengthID) >= 1){
                    for(final Item i : Inventory.getItems(AttackID)) {
                        if(!Tab.INVENTORY.isOpen()){
                            Tab.INVENTORY.open();
                        }
                        if(Skill.STRENGTH.getLevel() <= Skill.STRENGTH.getRealLevel() && Inventory.getCount(StrengthID) >= 1){
                            i.interact("Drink");
                            sleep(1000);
                        }
                    }
                }
            }
        }
    }

    class Loot implements Strategy {


        @Override
        public boolean activate() {


            return false;
        }

        @Override
        public void execute() {

        }
    }

    public static final int getHp() {
        String CurrentHp = Interfaces.get(3918).getChildren()[11].getText();
        CurrentHp = CurrentHp.replace("@whi@", "");
        return Integer.parseInt(CurrentHp);
    }

}
