![NOPE Intro][logo]


[logo]: https://i.imgur.com/NZRVx3I.png

### NOPE is a Minecraft anticheat that blocks the large majority of hacks on any client.​


# Features

* BungeeCord compatible - NOPE will sync VLs, warning messages, and commands across all bungee servers.
* Automated - NOPE's default config will automatically manage and log all hacker encounters.
* GUI - Manage, view, and modify checks and statistics with an easy to use GUI.
* Configurable - Manage, toggle, and modify how NOPE prevents hacks.
* Scoreboard - Easily and efficiently display player VLs on a scoreboard.

* 60+ Custom Checks
* Extensive Logging - See exactly why someone got banned and what flags they fired

* Banwaves - Customizable banwave rates

## Installation
1. Drag and drop NOPE into your plugins folder.
2. Drag and drop ProtocolLib into your plugins folder (optional).
3. Start up your server.
4. Give your staff members the nope.message.normal permission to be notified of potential hackers.
5. Configure the config.yml to your liking.

**Commands**<br>
![commands](https://i.imgur.com/yVwLYXe.png)


## Permissions

* nope.command.[command] - Access to /nope [command]
* nope.command.banwave.addplayer - Access to /nope banwave [Player]
* nope.command.bypass.[category] - Access to bypass all of a certain category (Movement, Player, Render, etc.)
* nope.bypass.[hack] - Access to bypass a certain hack type (Flight, Speed, etc.)
* nope.bypass.[category].[debugname] - Access to bypass a specific check (nope.bypass.flight.flight#1)
* nope.scoreboard - Ability to view the scoreboard
* nope.command.toggle.[Setting] - Access to toggle certain settings (or their own scoreboard) (Dev, Pastebin, Scoreboard, etc.)
* nope.message.normal - Be notified of potential hackers
* nope.message.dev - Be notified of development message (if dev mode is enabled)
* nope.message.banwave - Be notified when a player is added to the banwave
* nope.message.update - Be notified when a new version is out.


## Checks
**Combat (12) Hacks related to combat**<br>
Reach 1<br>
KillAura 4<br>
AntiKB 1<br>
FastBow 1<br>
HighCPS 3<br>
AutoClicker 1<br>
AutoArmor 1<br>
<br>
**Movement (28) Hacks that affect server movement**<br>
Speed 3<br>
NoSlowDown 5<br>
InventoryMove 1<br>
AutoWalk 1<br>
Flight 6<br>
Step 1<br>
NoWeb 1<br>
ClonedMovements 1<br>
FastClimb 1<br>
SafeWalk 1<br>
FastSneak 1<br>
Jesus 1<br>
BHop 1<br>
Spider 1<br>
AntiAFK 1<br>
AntiRotate 1<br>
Glide 1<br>

**Render (4) Hacks that affect either client or server renders**<br>
SkinBlinker 1<br>
InvalidMovement 1<br>
Spinbot 1<br>
AutoSneak 1<br>

**Player (9) Hacks that affect only the player**<br>
ChestStealer 1<br>
Zoot 1<br>
SelfHarm 1<br>
GhostHand 1<br>
FastEat 1<br>
NoFall 1<br>
AutoTool 2<br>
AntiFire 1<br>

**World (6) Hacks that affect the world or blocks**<br>
IllegalBlockPlace 1<br>
Scaffold 3<br>
FastBreak 1<br>
IllegalBlockBreak 1<br>

**Tick (2) Hacks related with packets/ticks**<br>
Timer 2<br>

Total Checks: 61

### Test Server
A public test server is available at MSWS.xyz.