import bot.script.BotScript;
import bot.script.enums.Skill;
import bot.script.methods.*;
import bot.script.util.Random;
import bot.script.wrappers.*;
import bot.script.wrappers.Component;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import bot.script.methods.Mouse;
import bot.script.methods.Npcs;
import bot.script.methods.Widgets;
import bot.script.wrappers.NPC;
import bot.script.wrappers.Widget;
 
 
public class bRangeGuild extends BotScript {
    public static final int[] NPC_RANDOMS = {4375, 2540, 410, 409, 956, 2539, 2538, 570};
    NPC randomNPC = Npcs.getNearest(NPC_RANDOMS);
    public int startGold = 0;
    public int startTickets = 0;
    long startTime;
    Integer rangeExp;
    public Tile away = new Tile(2668,3427);
    public Tile home = new Tile(2670,3419);
    public Widget a = Widgets.get(325);
    public static final int[] NPC_ACTIVATE = {4375, 2540, 410, 409, 956, 2539, 2538, 407, 411, 2470};
    int[] spirit2 = { 438, 439, 440, 441, 442, 443 };
    int[] chicken2 = { 2463, 2464, 2465, 2466, 2467, 2468 };
 
    public void rangerVisible(){
        NPC Ranger = Npcs.getNearest(693);
        int n = 0;
        if(!Ranger.isVisible()){
            n++;
            sleep(1000);
        }
        if(n>=45){
            Game.logout();
        }
    }
 
    private boolean inArea(){
        int x1 = 2669;
        int x2 = 2671;
        int y2 = 3417;
        int y1 = 3419;
        if (Players.getLocal().getLocation().getX() >= x1 && Players.getLocal().getLocation().getX() <= x2) {
            if (Players.getLocal().getLocation().getY() <= y1 && Players.getLocal().getLocation().getY() >= y2)
                return true;
        }
 
        return false;
    }
    public void myLocValid(){
        Tile a = new Tile(2670,3418);
        if(inArea() == false && !Players.getLocal().inCombat()){
            Walking.walkTo(a);
            sleep(1000,1500);
        }
 
    }
    public boolean checkRandom() {
        final int[] ids = {409, 410, 570, 956, 2538, 2539, 2540, 4375,407,2476, 411};
        return Npcs.getNearest(ids) != null;
    }
 
    public boolean boxVisible(){
        if(Widgets.get(325).isValid(a)){
            Widgets.getComponent(325, 88).interact("Close");
            sleep(500,1000);
 
        }
        if(Widgets.get(325).isValid(a)){
            return true;
        }else{
            return false;
        }
    }
 
 
    public void Combat() {
 
        rangerVisible();
        if(Players.getLocal().inCombat()){
            Walking.walkTo(away);
            sleep(50,75);
            Walking.walkTo(away);
 
            sleep(9000);
            Camera.turnToTile(away);
            Objects.getNearest(2511).interact("Climb-up");
            sleep(1000);
            if(Widgets.get(325).isValid(a) == true){
                Widgets.getComponent(564,17).click();
            }
            sleep(5000);
            Camera.setPitch(98);
            Camera.setAngle(317);
            sleep(5000);
            Objects.getNearest(2512).interact("Climb-down");
            sleep(1000);
            Walking.walkTo(home);
            Camera.setPitch(32);
            Camera.setAngle(317);
            sleep(5000);
 
        }
    }
    int n = 0;
    public void logOut(){
 
        if(randomNPC != null){
            n ++;
            sleep(1000);
        }
        if(n>=35){
            Game.logout();
            log("Logging out after 20seconds of a random being on screen..");
            n=0;
        }
        if(randomNPC == null){
            n=0;
        }
 
 
    }
 
    @Override
    public boolean onStart() {
        startTime = System.currentTimeMillis();
        Camera.setPitch(32);
        Camera.setAngle(317);
        rangeExp = Skills.getXp(Skill.RANGED);
        startGold = Inventory.getItem(996).getStacksize();
        if(Inventory.contains(1465)){
            startTickets = Inventory.getItem(1465).getStacksize();
        }
        return true;
    }
    @Override
    public int loop() {
        boxVisible();
        if(validCheck()) {
            talkRandoms();
        } else {
            antiBan();
            rangerVisible();
            myLocValid();
            logOut();
            Combat();
 
 
 
            if(Inventory.contains(3063)){
                Game.logout();
            }
 
 
 
            int counter = 0;
            int n;
            boolean boolA = false;
            NPC Ranger = Npcs.getNearest(693);
            Component Continue =   Widgets.getComponent(242, 4);
            Component Sure =       Widgets.getComponent(230, 1);
            Component Continue2 =  Widgets.getComponent(64,  3);
            Component Continue3 =  Widgets.getComponent(241, 3);
            Widget a = Widgets.get(325);
            Widget b = Widgets.get(241);
            Widget c = Widgets.get(242);
 
 
 
 
 
 
            //fail safe target view open
            if(Widgets.get(325).isValid(a)){
                Widgets.getComponent(325, 88).interact("Close");
                sleep(500,1000);
            }
 
            //fire loop
            if(!Players.getLocal().inCombat() && Inventory.contains(883) || Widgets.get(241).isValid(b) && Widgets.getComponent(241,2).getText().contains("Hello again") == true){
                if(Inventory.contains(883)){
                    Inventory.getItem(883).interact("Wield");
                    sleep(200,250);
                }
                Tile tileFire = Objects.getNearest(2513).getLocation();
 
 
                for(n=0;n<10; ){
                    talkRandoms();
                    Combat();
                    logOut();
                    rangerVisible();
 
                    if(Widgets.get(325).isValid(a) ){
                        counter++;
                        sleep(100);
                        if (counter >= 20) {
                            Widgets.getComponent(325, 88).interact("Close");
                            sleep(500,1000);
                            counter = 0;
 
                        }
                    }
                    if(Widgets.get(242).isValid(c) && checkRandom() == false){
                        if(Widgets.getComponent(242,2).getText().contains("Sorry")){
                            n=10;
                        }
                    }
                    if(!Players.getLocal().inCombat() && !Widgets.get(325).isValid(a) && checkRandom() == false && inArea() == true){
                        logOut();
                        if(Inventory.contains(883)){
                            Inventory.getItem(883).interact("Wield");
                            sleep(150,200);
                        }
                        GameObject o =  Objects.getNearest(2513);
                        Camera.setPitch(32);
                        Camera.setAngle(317);
                        if(!o.isVisible()){
                            Camera.turnToTile(tileFire);
                        }
                        final Point p = o.getPoint();
                        p.y = p.y - 16;
                        p.x = p.x + 4;
                        int counter2 = Random.nextInt(0,6);
                        if(counter2 == 3){
 
                            p.x = p.x + Random.nextInt(-1, +1);
                            p.y = p.y + Random.nextInt(-1, +1);
                        }
 
                        Mouse.move(p);
                        bot.script.methods.Menu.interact("Fire-at");
                        sleep(25,35);
                        counter = 0;
                    }
 
 
                    myLocValid();
 
                    if(Widgets.get(325).isValid(a) && boolA == false){
                        n++;
                        boolA = true;
                    }
                    if(!Widgets.get(325).isValid(a)){
                        boolA = false;
                    }
 
 
                }
 
                sleep(500,600);
            }
 
            //fail safe target view open
            if(Widgets.get(325).isValid(a)){
                Widgets.getComponent(325, 88).interact("Close");
                sleep(500,1000);
            }
 
            //talk to judge
            Tile tileRanger = Ranger.getLocation();
            if(Continue == null && Sure == null && Continue2 == null && Continue3 == null && !Players.getLocal().isMoving() && !Players.getLocal().inCombat() && !Widgets.get(325).isValid(a) && checkRandom() == false) {
                if(!Ranger.isVisible()){
                    Camera.turnToTile(tileRanger);
                }
                myLocValid();
                Ranger.interact("Talk-to Competition Judge");
                sleep(700,900);
            }
            if(Sure != null && !Players.getLocal().inCombat() && checkRandom() == false){
                Mouse.click(260 + Random.nextInt(0,10), 396  + Random.nextInt(0, 2));
                sleep(100,150);
            }
            if(Continue != null && !Players.getLocal().inCombat() && checkRandom() == false){
                Mouse.click(281 + Random.nextInt(0,50) , 452 + Random.nextInt(0,2));
                sleep(100,150);
            }
            if(Continue2 != null && !Players.getLocal().inCombat( )&& checkRandom() == false){
                Mouse.click(190 + Random.nextInt(0,50) , 455 + Random.nextInt(0,2));
                sleep(100,150);
            }
            if(Continue3 != null && !Players.getLocal().inCombat()&& checkRandom() == false){
                Mouse.click(286 + Random.nextInt(0,50) , 450 + Random.nextInt(0,2));
                sleep(100,150);
            }
 
 
 
 
 
 
        }
 
        return 1;
    }
    public void talkRandoms() {
 
        NPC Chicken = Npcs.getNearest(chicken2);
        NPC pirate = Npcs.getNearest(2539);
        NPC plant = Npcs.getNearest(407);
        NPC genie = Npcs.getNearest(409);
        NPC OldMan = Npcs.getNearest(410);
        NPC Guard = Npcs.getNearest(4375);
        NPC Hyde = Npcs.getNearest(2540);
        NPC Swarm = Npcs.getNearest(411);
        NPC drunkenDwarf = Npcs.getNearest(956);
        NPC Rick = Npcs.getNearest(2538);
        NPC frog = Npcs.getNearest(2470);
        GameObject frogobj = Objects.getNearest(5955);
        Tile frogtile = new Tile(2464, 4776);
        Widget b = Widgets.get(241);
        Widget c = Widgets.get(242);
        Widget d = Widgets.get(243);
        Widget e = Widgets.get(244);
 
        if(Chicken != null) {
            Combat();
        }
        if(plant != null) {
            boxVisible();
            if (Calculations.distanceBetween(plant.getLocation(), Players.getLocal()
                    .getLocation()) <= 2) {
                Camera.turnToTile(plant.getLocation());
                plant.interact("Pick");
                sleep(2000,4000);
                Mouse.click(300,455);
                sleep(2000,4000);
                log("Strange plant :o");
 
            }
        }
        if(Swarm != null) {
            boxVisible();
            Combat();
        }
        if(frogobj != null) {
            boxVisible();
            log("starting frog");
            if(frog != null){
                Walking.walkTo(frog.getLocation());
                sleep(4000);
                Camera.turnToTile(frog.getLocation());
                sleep(300);
                frog.interact("Talk-to Frog");
                sleep(2000,4000);
                Mouse.click(300,455);
                sleep(2000,4000);
                Mouse.click(300,455);
                sleep(2000,4000);
                Mouse.click(300,455);
                sleep(2000,4000);
                Mouse.click(300,455);
                sleep(8000,10000);
                log("Frawgie random :D");
            } else {
                Walking.walkTo(frogtile);
            }
        }
        if(pirate != null) {
            boxVisible();
            if(Calculations.distanceBetween(pirate.getLocation(), Players.getLocal()
                    .getLocation()) <= 2) {
                Camera.turnToTile(pirate.getLocation());
                pirate.interact("Talk-to Cap'n Hand");
                sleep(1000,2500);
                if(b != null && Widgets.get(241).isValid(b) || Widgets.get(242).isValid(c) || Widgets.get(243).isValid(d) || Widgets.get(244).isValid(e)) {
                    Mouse.move(Random.nextInt(196, 404), Random.nextInt(445, 455));
                    sleep(1000,1200);
                    Mouse.click(true);
                }
            }
            log("Pirate Random");
        }
        if(genie != null) {
            boxVisible();
            if(Calculations.distanceBetween(genie.getLocation(), Players.getLocal()
                    .getLocation()) <= 2) {
                Camera.turnToTile(genie.getLocation());
                genie.interact("Talk-to Genie");
                sleep(1000,2500);
                if(b != null && Widgets.get(241).isValid(b) || Widgets.get(242).isValid(c) || Widgets.get(243).isValid(d) || Widgets.get(244).isValid(e)) {
                    Mouse.move(Random.nextInt(196, 404), Random.nextInt(445, 455));
                    sleep(1000,1200);
                    Mouse.click(true);
                }
            }
            log("Genie Random");
        }
        if(OldMan != null) {
            boxVisible();
            if(Calculations.distanceBetween(OldMan.getLocation(), Players.getLocal()
                    .getLocation()) <= 2) {
                Camera.turnToTile(OldMan.getLocation());
                OldMan.interact("Talk-to Mysterious Old Man");
                sleep(1000,2500);
                if(b != null && Widgets.get(241).isValid(b) || Widgets.get(242).isValid(c) || Widgets.get(243).isValid(d) || Widgets.get(244).isValid(e)) {
                    Mouse.move(Random.nextInt(196, 404), Random.nextInt(445, 455));
                    sleep(1000,1200);
                    Mouse.click(true);
                }
            }
            log("Mysterious old man random");
        }
        if(Guard != null) {
            boxVisible();
            if(Calculations.distanceBetween(Guard.getLocation(), Players.getLocal()
                    .getLocation()) <= 2) {
                Camera.turnToTile(Guard.getLocation());
                Guard.interact("Talk-to Guard");
                sleep(1000,2500);
                if(b != null && Widgets.get(241).isValid(b) || Widgets.get(242).isValid(c) || Widgets.get(243).isValid(d) || Widgets.get(244).isValid(e)) {
                    Mouse.move(Random.nextInt(196, 404), Random.nextInt(445, 455));
                    sleep(1000,1200);
                    Mouse.click(true);
                }
            }
            log("Guard Random");
        }
        if(Hyde != null) {
            boxVisible();
            if(Calculations.distanceBetween(Hyde.getLocation(), Players.getLocal()
                    .getLocation()) <= 2) {
                Camera.turnToTile(Hyde.getLocation());
                Hyde.interact("Talk-to Dr Jekyll");
                sleep(1000,2500);
                if(b != null && Widgets.get(241).isValid(b) || Widgets.get(242).isValid(c) || Widgets.get(243).isValid(d) || Widgets.get(244).isValid(e)) {
                    Mouse.move(Random.nextInt(196, 404), Random.nextInt(445, 455));
                    sleep(2000,4000);
                    Mouse.click(true);
                    sleep(2000,4000);
                    Mouse.click(true);
                    sleep(2000,4000);
                    Mouse.click(true);
                    sleep(2000,4000);
                    Mouse.click(true);
                    sleep(2000,4000);
                }
            }
            log("Dr Jekyll hyde random");
        }
        if(drunkenDwarf != null) {
            boxVisible();
            if(Calculations.distanceBetween(drunkenDwarf.getLocation(), Players.getLocal()
                    .getLocation()) <= 2) {
                Camera.turnToTile(drunkenDwarf.getLocation());
                drunkenDwarf.interact("Talk-to Drunken Dwarf");
                sleep(1000,2500);
                if(b != null && Widgets.get(241).isValid(b) || Widgets.get(242).isValid(c) || Widgets.get(243).isValid(d) || Widgets.get(244).isValid(e)) {
                    Mouse.move(Random.nextInt(196, 404), Random.nextInt(445, 455));
                    sleep(1000,1200);
                    Mouse.click(true);
                }
            }
            log("Drunken Dwarf Random");
        }
        if(Rick != null) {
            boxVisible();
            if(Calculations.distanceBetween(Rick.getLocation(), Players.getLocal()
                    .getLocation()) <= 2) {
                Camera.turnToTile(Rick.getLocation());
                Rick.interact("Talk-to Rick Turpentine");
                sleep(1000,2500);
                if(b != null && Widgets.get(241).isValid(b) || Widgets.get(242).isValid(c) || Widgets.get(243).isValid(d) || Widgets.get(244).isValid(e)) {
                    Mouse.move(Random.nextInt(196, 404), Random.nextInt(445, 455));
                    sleep(1000,1200);
                    Mouse.click(true);
                }
            }
            log("Rick Turpentine Random");
        }
    }
 
 
    public boolean validCheck() {
        NPC n = Npcs.getNearest(NPC_ACTIVATE);
        return n != null;
    }
    private final Color colorBackGround = new Color(0, 0, 0);
    private final Color colorFont = new Color(255, 255, 255);
    private final BasicStroke stroke1 = new BasicStroke(1);
    private final Font font = new Font("", 0, 12);
 
    Image background = getImage("http://i.imgur.com/H4US5Pa.png"); //Replace url by the image's url
 
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
 
    @Override
    public void paint(Graphics g1){
        int currentGold = Inventory.getItem(996).getStacksize();
        int currentTickets = 0;
        if(Inventory.contains(1465)){
            currentTickets = Inventory.getItem(1465).getStacksize();
        }
        int expGained = Skills.getXp(Skill.RANGED) - rangeExp;
        long millis = System.currentTimeMillis() - startTime;
        Graphics2D g = (Graphics2D)g1;
        g.drawImage(background, 559, 215, null);
        g.setColor(colorBackGround);
        g.setStroke(stroke1);
        g.setFont(font);
        g.setColor(colorFont);
        Integer expHour = expGained;
        int dTickets = currentTickets - startTickets;
        int dGold = startGold - currentGold;
        int gain = (dTickets * 5) - dGold;
 
        g.drawString(perHour(gain), 663,400 );
        g.drawString(timeToString((int) millis), 654,279);
        g.drawString(formatNumber(expHour).toString(), 663, 359);
        g.drawString("1.60", 705,452);
        g.drawString(perHour(expGained), 663,319);
 
 
    }
 
    public void antiBan() {
        int type = Random.nextInt(1, 200); //raise 200 to lower frequency
        switch (type) {
            case 1:
                int cur = Camera.getAngle(); //random camera turning
                Camera.setAngle(cur + Random.nextInt(30, 80));
                break;
            case 2:
                if (Inventory.getCount() > 2) { //examines random items in inventory
                    Item[] items = Inventory.getItems();
                    int ran = Random.nextInt(1, Inventory.getCount());
                    items[ran].interact("Examine");
                }
                break;
            case 3:
                Component Wid = Widgets.get(548).getChild(55); //checks stats and fishing xp (change it to your skill)
                Wid.click();
                sleep(400, 850);
                Wid = Widgets.get(320).getChild(20); //20 is the widget component for the fishing level tile
                int x = (Wid.getX() - 8) + Random.nextInt(1, 3);
                int y = (Wid.getY() + 8) + Random.nextInt(1, 3);
                Mouse.move(x, y);
                sleep(1000, 1300);
                break;
            case 4:
                NPC Obj = Npcs.getNearest(693);
                if (Obj != null && Calculations.distanceTo(Obj.getLocation()) < 2) {
                    Obj.interact("Examine");
                }
                break;
            case 5:
                Mouse.move(Random.nextInt(30, 600), Random.nextInt(30, 300));
                break;
            case 6:
                Mouse.move(Random.nextInt(30, 600), Random.nextInt(30, 300));
                break;
            case 7:
                Mouse.move(Random.nextInt(30, 600), Random.nextInt(30, 300));
                break;
            default:
                break;
        }
        sleep(25,50);
    }
 
 
}