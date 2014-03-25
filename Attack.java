class Attack implements Strategy {

        public boolean activate() {
            if (Npcs.getNearest(MonsterID) != null && Npcs.getNearest(MonsterID).length > 0) {
                Npc[] NPCArray = null;
                
                //Try catch this because wasn't sure how to fix it..(Null error once in a while)
                try {
                NPCArray = Npcs.getNearest(MonsterID);
                } catch(Exception e) {
                    LogArea.log(e.getMessage());
                }

                if (NPCArray != null && NPCArray.length > 0) {
                    for (Npc i : NPCArray) {
                        if (i != null && !i.isInCombat()) {
                            return Inventory.getCount(FoodID) >= 1
                                    && !Players.getLocal().isInCombat()
                                    && !Players.getLocal().isWalking()
                                    && currentHp() >= MaxHP * EatLowPerct
                                    && Players.getLocal().getAnimation() == -1;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void execute() {
            if (Npcs.getNearest(MonsterID) != null) {
                Npc[] npc = Npcs.getNearest(new Filter<Npc>() {
                    @Override
                    public boolean accept(final Npc npc) {
                        return (npc != null && npc.getDef().getId() != 0 && !npc.isInCombat() &&
                                npc.getDef().getId() == MonsterID);
                    }

                });
                if (npc != null && npc.length > 0) {
                    Npc MonsterA = npc[0];
                    if (MonsterA != null && !MonsterA.isOnScreen() && !Players.getLocal().isWalking()) {
                        MonsterA.getLocation().clickMM();
                        Camera.turnTo(MonsterA);
                        sleep(1500);
                    }
                    
                    //Try catch this because wasn't sure how to fix it..(Null error once in a while)
                    try {
                        if (MonsterA != null && MonsterA.getModel() != null && MonsterA.isOnScreen() && !Players.getLocal().isInCombat()
                                && !Players.getLocal().isWalking()) {

                            MonsterA.interact("Attack");
                        }
                    } catch (Exception e) {
                        LogArea.log(e.getMessage());
                    }


                    sleep(4000);
                }
            }
        }
    }