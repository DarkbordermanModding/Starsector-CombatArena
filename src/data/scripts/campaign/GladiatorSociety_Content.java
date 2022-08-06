package src.data.scripts.campaign;

import com.fs.starfarer.api.Global;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import src.data.scripts.campaign.dataclass.GladiatorSociety_BountyData;

public class GladiatorSociety_Content {
    public transient List<GladiatorSociety_BountyData> missions = new ArrayList<GladiatorSociety_BountyData>();
    public static final Logger LOG = Global.getLogger(GladiatorSociety_Content.class);

    private Set<String> set_GladiatorSociety_bounty_done = new HashSet<>();
   // private Set<String> bounty_on = new HashSet<>();
    public transient GladiatorSociety_BountyData currentMission;
    
    private int nextSet=0;
    
   // public List<GladiatorSociety_BountyData> missions = new ArrayList<>();
    
    public void addBountyDone(String bounty) {
        ifNullDone();
        set_GladiatorSociety_bounty_done.add(bounty);
    }

    public void ifNullDone() {
        if (set_GladiatorSociety_bounty_done == null) {
            set_GladiatorSociety_bounty_done = new HashSet<>();
        }
    }
/*
    public void ifNullOn() {
        if (bounty_on == null) {
            bounty_on = new HashSet<>();
        }
    }*/
/*
    public void removeActiveBounty(String bounty) {
        ifNullOn();
        bounty_on.remove(bounty);
    }*/
    
    public void incrementNext(){
        nextSet++;
    }
    public void resetNext(){
        nextSet=0;
    }
    public int getNextSet(){
        return nextSet;
    }
    protected Object readResolve() {
        return this;
    }
    
    public void initPicker() {

        missions = new ArrayList<>();

        ifNullDone();
      //  ifNullOn();

        /*int playerLevel = 40;
        if (Global.getSector().getPlayerFleet() != null) {
            playerLevel = Global.getSector().getPlayerFleet().getCommander().getStats().getLevel();
        }*/
        LOG.info("GS: Compute the display list");

        for (GladiatorSociety_BountyData bounty : GladiatorSociety_JSONBountyRead.getAllBountyCopy()) {

            if (bounty == null || bounty.missionid == null) {
                LOG.info("-- Error Bounty --");
                continue;
            }

            if (set_GladiatorSociety_bounty_done.contains(bounty.missionid)) {
                LOG.info("-- "+bounty.missionid+" already done --");
                continue;
            }
           /* if (playerLevel < 30 && playerLevel * 15000 < bounty.bountyvalue) {
                continue;
            }*/
            if (bounty.needBounty!=null && !set_GladiatorSociety_bounty_done.contains(bounty.needBounty)) {
                LOG.info("-- "+bounty.missionid+" need "+bounty.needBounty+" to be done --");
                continue;
            }
          /*  boolean cont = false;
            for (String present : bounty_on) {
                if (present != null && present.equals(bounty.missionid)) {
                    cont = true;
                    break;
                }
            }
            if (cont) {
                continue;
            }*/
   
            LOG.info("-- "+bounty.missionid+" added to the display list --");
            missions.add(bounty);
        }
        Collections.sort(missions,new Comparator<GladiatorSociety_BountyData>(){
            @Override
            public int compare(GladiatorSociety_BountyData a, GladiatorSociety_BountyData b) {
               return  a.fullname.getFullName().compareToIgnoreCase(b.fullname.getFullName());
            }
        });
        LOG.info("-- End --");
    }
    
    
    /*
    public GladiatorSociety_BountyData createMission() {

        Random random = new Random();
        int size = entityPicker.size();
        if (size == 0) {
            return null;
        }
        int rand = random.nextInt(size);
        GladiatorSociety_BountyData target = entityPicker.remove(rand);
        if (target == null) {
            return null;
        }
        //String faction = "gladiator";

        //int days = 150;

        //GladiatorSociety_BountyMission mission = new GladiatorSociety_BountyMission(faction, target, days, this);

     //   this.bounty_on.add(target.missionid);
        this.missions.add(target);

        return target;
    }
*/
    public int getMaxConcurrent() {
        return 3;
    }
    

}
