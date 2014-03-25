import org.parabot.environment.api.interfaces.Paintable;
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


import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;


@ScriptManifest(author = "Chas3down", category = Category.COMBAT, description = "5", name = "good", servers = { "PkHonor" }, version = 1)
public class cCombat extends Script implements Paintable {

    private final ArrayList<Strategy> strategies = new ArrayList<Strategy>();
    public final int MonsterID = 101;
    //public final int MonsterIDD[] = {1106,1107,1108,1109,1110,1111};
    public final int FoodID = 379;
    public final int[] StrengthID = {113,115,117,119};
    public final int[] AttackID = {2428,121,123,125};
    public final int MaxHP = Players.getLocal().getMaxHealth();
    public final double EatLowPerct = .50;
    public final double EatTopPerct = .75;
    public final long startTime = System.currentTimeMillis();
    public final int startExp = Skill.DEFENSE.getExperience();


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

    @Override
    public void paint(Graphics g1){
        final Color colorBackGround = new Color(0, 0, 0);
        final Color colorFont = new Color(255, 255, 255);
        final BasicStroke stroke1 = new BasicStroke(1);
        final Font font = new Font("", 0, 12);
        final Image background = getImage("http://i.imgur.com/H4US5Pa.png");

        int expGained = Skill.DEFENSE.getExperience() - startExp;
        long millis = System.currentTimeMillis() - startTime;
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;

        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        Graphics2D g = (Graphics2D)g1;

        g.drawImage(background, 559, 215, null);
        g.setColor(colorBackGround);
        g.setStroke(stroke1);
        g.setFont(font);
        g.setColor(colorFont);

        g.drawString(time, 654,279);
        g.drawString(formatNumber(expGained), 663, 359);
        g.drawString("1.60", 705,452);
        g.drawString(perHour(expGained), 663,319);

    }

    class Attack implements Strategy {

        @Override
        public boolean activate() {
            if(Npcs.getNearest(MonsterID) != null){
                final Npc[] NPCArray = Npcs.getNearest(MonsterID);
                if (NPCArray.length > 0){
                    for(Npc i : NPCArray){
                        if(i != null && !i.isInCombat()){
                            return Inventory.getCount(FoodID) > 1
                                    && !Players.getLocal().isInCombat()
                                    && !Players.getLocal().isWalking()
                                    && currentHp() >= MaxHP*EatLowPerct
                                    && Players.getLocal().getAnimation() == -1;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void execute() {
            if(Npcs.getNearest(MonsterID) != null) {
            Npc[] npc = Npcs.getNearest(new Filter<Npc>() {
                @Override
                public boolean accept(final Npc npc) {
                    return (npc != null && npc.getDef().getId() != 0 &&!npc.isInCombat() &&
                            npc.getDef().getId() == MonsterID);
                }

            });
            if (npc != null && npc.length > 0){
                Npc MonsterA = npc[0];
                if (MonsterA != null && !MonsterA.isOnScreen() && !Players.getLocal().isWalking() ){
                    MonsterA.getLocation().clickMM();
                    Camera.turnTo(MonsterA);
                    sleep(1500);
                }
                if (MonsterA != null && MonsterA.isOnScreen() && !Players.getLocal().isInCombat()
                        && !Players.getLocal().isWalking()) {
                    MonsterA.interact("Attack");
                }
                sleep(4000);
            }
            }
        }
    }

    class Eat implements Strategy {


        @Override
        public boolean activate() {
            if(Inventory.getCount(FoodID) >= 1){
                return currentHp() < MaxHP*EatLowPerct;
            }

            return false;
        }

        @Override
        public void execute() {
            if(Inventory.getCount(FoodID) >= 1 && currentHp() < MaxHP*EatLowPerct) {
                if(!Tab.INVENTORY.isOpen()){
                    Tab.INVENTORY.open();
                }else{
                    for(final Item i : Inventory.getItems(FoodID)) {
                        if(currentHp() < MaxHP*EatTopPerct){
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

            if(currentHp() >= MaxHP*EatLowPerct && Inventory.getCount(FoodID) >= 1){
                if(Inventory.getCount(StrengthID) >= 1 || Inventory.getCount(AttackID) >= 1){
                    return Skill.STRENGTH.getLevel() <= Skill.STRENGTH.getRealLevel()
                            || Skill.ATTACK.getLevel() <= Skill.ATTACK.getRealLevel();
                }

            }

            return false;
        }

        @Override
        public void execute() {
            if(currentHp() >= MaxHP*EatLowPerct){
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
                //Tab.MAGIC hook must be broken, isn't working as intended..
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

    public Image getImage(String url) {
        try { return ImageIO.read(new URL(url)); }
        catch(IOException e) { return null; }
    }

    public String formatNumber(int start) {
        DecimalFormat nf = new DecimalFormat("0.00");
        double i = start;
        if(i >= 1000000) {
            return nf.format((i / 1000000)) + "m";
        }
        if(i >=  0) {
            return nf.format((i / 1000)) + "k";
        }
        return ""+start;
    }

    public String perHour(int gained) {
        return formatNumber((int) ((gained) * 3600000D / (System.currentTimeMillis() - startTime)));
    }

}
