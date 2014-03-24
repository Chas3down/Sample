import org.parabot.environment.api.utils.Filter;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.framework.Strategy;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.ScriptManifest;
import org.rev317.api.methods.*;
import org.rev317.api.wrappers.hud.Item;
import org.rev317.api.wrappers.hud.Tab;
import org.rev317.api.wrappers.interactive.Npc;
import org.rev317.api.wrappers.scene.SceneObject;


import java.util.ArrayList;


@ScriptManifest(author = "Chas3down", category = Category.OTHER, description = "Kill Shit", name = "Kill Shit", servers = { "PkHonor" }, version = 1)
public class cCombat extends Script{

    private final ArrayList<Strategy> strategies = new ArrayList<Strategy>();
    public final int MonsterID = 1459;
    public final int FoodID = 379;
    public final int[] StrengthID = {113,115,117,119};
    public final int[] AttackID = {2428,121,123,125};
    public final int MaxHL = Players.getLocal().getMaxHealth();
    public final double EatLowPerct = .50;
    public final double EatTopPerct = .75;


    @Override
    public boolean onExecute() {


        strategies.add(new Attack());
        strategies.add(new Eat());
        strategies.add(new Drink());
        //strategies.add(new Banks());

        provide(strategies);



        return true;
    }

    @Override
    public void onFinish() {

    }

    class Attack implements Strategy {

        @Override
        public boolean activate() {
            if (Npcs.getNearest(MonsterID) != null){
                for(Npc i : Npcs.getNearest(MonsterID)) {
                  if(i != null && i.getDef().getId() != 0) {
                     if(!i.isInCombat()){
                         return Inventory.getCount(FoodID) > 1
                                    && !Players.getLocal().isInCombat()
                                    && !Players.getLocal().isWalking()
                                    && currentHp() >= MaxHL*EatLowPerct
                                    && Players.getLocal().getAnimation() == -1;

                     }
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
                    return (npc != null && npc.getDef().getId() == 0 &&!npc.isInCombat() && npc.getDef().getId() == MonsterID);
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
            if(Inventory.getCount(FoodID) >= 1){
                return currentHp() < MaxHL*EatLowPerct;
            }

            return false;
        }

        @Override
        public void execute() {
            if(Inventory.getCount(FoodID) >= 1 && currentHp() < MaxHL*EatLowPerct) {
                if(!Tab.INVENTORY.isOpen()){
                    Tab.INVENTORY.open();
                }else{
                    for(final Item i : Inventory.getItems(FoodID)) {
                        if(currentHp() < MaxHL*EatTopPerct){
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

            if(currentHp() >= MaxHL*EatLowPerct && Inventory.getCount(FoodID) >= 1){
                if(Inventory.getCount(StrengthID) >= 1 || Inventory.getCount(AttackID) >= 1){
                    return Skill.STRENGTH.getLevel() <= Skill.STRENGTH.getRealLevel()
                            || Skill.ATTACK.getLevel() <= Skill.ATTACK.getRealLevel();
                }

            }

            return false;
        }

        @Override
        public void execute() {
            if(currentHp() >= MaxHL*EatLowPerct){
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
                    for(final Item i : Inventory.getItems(StrengthID)) {
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

    class Banks implements  Strategy {

        @Override
        public boolean activate() {
            return Inventory.getCount(FoodID) < 1;
        }

        @Override
        public void execute() {

            final SceneObject[] banks = Bank.getNearestBanks();
            SceneObject bankbooth = null;
            if(banks.length  > 0){
                bankbooth = banks[0];
            }

            if (bankbooth == null)  {
                //Tab.MAGIC.open isn't working as intended..
                if(!Tab.MAGIC.isOpen()){
                    Tab.MAGIC.open();
                    sleep(500);
                }else{
                    Magic.clickSpell(Magic.AncientMagic377.HOME_TELEPORT);
                    sleep(2500);
                }
            }else if(bankbooth != null && !Bank.isOpen()){
                if(!Bank.getBank().isOnScreen()){
                    Camera.turnTo(Bank.getBank());
                    sleep(1000);
                }else{
                    Bank.getBank().interact("Use-quickly");
                    sleep(5000);
                }
            }else if(Bank.isOpen()){
                if(Bank.getItem(FoodID).getStackSize() >= 1){
                    Bank.getItem(FoodID).interact("Withdraw All");
                    sleep(1000);
                    Bank.close();
                }else{
                    stopScript();


                }
            }

        }
    }


    public static final int currentHp() {
        String CurrentHp = Interfaces.get(3918).getChildren()[11].getText().replace("@whi@", "");
        return Integer.parseInt(CurrentHp);
    }

    public void stopScript() {
        provide(null);
    }

}
