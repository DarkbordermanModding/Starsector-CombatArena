package src.data.utils;

/*
import src.data.scripts.campaign.GladiatorSociety_Bounty;
import src.data.scripts.campaign.GladiatorSociety_BountyData;*/
import src.data.scripts.campaign.GladiatorSociety_Content;
import src.data.scripts.campaign.GladiatorSociety_EndlessContent;
public class GladiatorSociety_XStreamConfig {

    public static void configureXStream(com.thoughtworks.xstream.XStream x) {
        x.alias("GladiatorSociety_Content", GladiatorSociety_Content.class);
        x.alias("GladiatorSociety_EndlessContent", GladiatorSociety_EndlessContent.class);
    }
}
