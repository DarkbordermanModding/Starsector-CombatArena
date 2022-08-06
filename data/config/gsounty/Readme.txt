Gladiator Society Mission Creation Tutorial:

First, put the missionid of your own Missions.csv (create your mod with the same path: data/config/gsounty/Missions.csv)
Create a file.json where the name of the file is the "missionid"   (Look example)

Your Missions.csv will look like:
missionid,
"namefile",
"namefile2",
"etc",



The entire JSON looks like this:

{
"dependencies":["swp"],
"faction":"independent",
"firstname":"Random",
"lastname":"People",
"gender":"M",
"officerlevel":-1,
"officerPersonality":"aggressive",
"mainShip":"sunder_Assault",
#"mainShipCustom":["eagle_xiv","GladiatorSociety_freedom_ret"], #Optional
#"ships":["GladiatorSociety_mercury_Boom","5","GladiatorSociety_mercury_Boom","GladiatorSociety_mercury_Baam",],            WARNING: Deprecated
#"advships":[["GladiatorSociety_lasher_Raccoon",11,"aggressive"],["lasher_luddic_path_Raider",14,"aggressive"]],
"advships":[["GladiatorSociety_freedom_ret",11,"aggressive","eagle_xiv"],["lasher_luddic_path_Raider",14,"aggressive"]], #Optional
"randomFleet":true,
"combatPoints":10,
"reward":50000,
"needBounty":"random1",
"description":"Theses Gladiators matchs are so annoying than your opponent do not get a name",
"dialog":"The Gladiator's dialogue.",
"avatar":"graphics/portraits/GladiatorSociety_HollowKnight.png",
"dsrandom":1,

}

-dependencies: This is where you can dependency if your bounty need others mods to be enabled. Disable the bounty if these mods are not.
Use it if you add custom variant(like the ChunkyGourd1 bounty)

-faction: This is the faction where if you randomize the fleet, their ships will  be taken. (Same for portrait of Officer)
If the faction doesn't exist, the random fleet will be pirates

-firstname/lastname: This is clear, no need to explain. (The last name is used for the name of the fleet)

-gender: M for Male, F for Female, or N for any, if N, the gender will be random.(Same if do not change anything)

-officerlevel: To 0 to X for the level of the main captain. If you put -1(or nothing), the officer will have the max level of your settings. Same if too high.

-officerPersonality: aggressive,timid,steady,reckless,cautious   (Per default Steady if you go wrong or do not put)


-mainShip: The Flagship's main designation, this is also used for no-faction mods like ShipWeaponPack which adds new ships, if the variant doesn't exist, the bounty will be removed. If nothing, a tempest is taken.
-mainShipCustom: Same thing, but for custom variant, where you put the hull id of the ship before and the custom variant(who will not be loaded per the game).


-ships: WARNING: Deprecated
We have three cases:
[]   If you do not want any others specific variants on the fleet
["GladiatorSociety_mercury_Boom","2",]  If you want to put multiples of the same variant, do not copy/paste the same variant, write the first variant,
 then 2 for 3 of the same variant. (Sensitives cases, do not put space, no limit, we count on you for not making a fleet of 900 ships)
["GladiatorSociety_mercury_Boom","GladiatorSociety_mercury_Baam",] Else, add variant.

-advships:
[["variant",number,"personality"],["variant",number,"personality"]]:
              -variant, put the name of your variant.
              -number, put the number of this ship than you want.
              -personality, put the personality you want for theses variants. (Add a hidden hullmod who modify the personality)
               (aggressive,timid,reckless,cautious)(per default, they are steady)
BIS:
[["variant",number,"personality","hull id"]]:
              -if you add a hull id on the 4th, you can add custom variant who can use others mods weapons without problem. (Or others mods ships)

-randomFleet:  true for random fleets(of the faction) which spawns with your current fleet. Otherwise false.

-combatPoints: Used if randomFleet is true, this is the fleet's max strength. (What you get is very random)
For a ladder of values(Independent faction): (The max value on Vanilla bounty are 65. The max on this mod is 2000)
* 60 Points: 1 Odyssey + 1 Cruiser + 17 destroyer + 15 frigates
* 50 Points: 1 Paragon(capital) + 2 cruiser + 7 destroyer + 11 frigates
* 40 Points: 2 cruiser + 11destroyer + 11 frigates
* 40 Points(2): 1 Legion(capital) + 11 destroyer + 10 frigates
* 30 Points: 1 Cruiser + 11 destroyer + 5 frigates
* 30 Points(2):  9 destroyer + 11frigates
* 20 Points: 2 cruiser + 3 Destroyer + 6 frigates
* 10 Points: 1 cruiser + 7 frigates
* 5 Points: 1 destroyer + 4 frigates

-reward: The reward for defeating the fleet. (See for 1125 credits per combat point)

-needBounty: For chain instances. If true, earlier bounties will need to be done before you are able to fight the bounty.

-description: The description of the bounty on the mission board.

-dialog: The dialog displayed when you encounter it.

-avatar: The portrait of your captain.   If empty, use a random portrait(Do not look the gender) of your faction.
The portrait need to be on the faction choosen, else put your portrait on the data/world/factions/customportrait. (Just add a copy of the JSON and add your portrait)


-dsrand:
A bounty can have 3 levels of randomization with Dynasector:
0: Nothing, use existing variant.
1: The default mode, only the random ships of your bounty will have randomized variants; the flagship and ships manually added by the bounty are unaffected.
2: Every ship in the fleet will be affected by Dynasector, including flagship and manually added ships. Uses faction weapons when applicable: if you put a Lasher as flagship and make the bounty belong to the templar faction, the lasher will get templar weapons.
