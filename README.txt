# Serverside miners-movies plugin (proof of concept)

Based on the TinyProtocol Example of https://github.com/aadnk/ProtocolLib

## Compile:

    mvn package

## Install:

    copy target/MinersMovies*.jar into your plugin directory of your bukkit server

## Usage:

    Nothing to do. The plugin starts recording once a player joins and stops when the player
    quits.

## TODO:

    Explicit starting of recordings:

        Idea: Make the player switch to another dimension and immediatelly back, starting the recording
        before that happens. So the recording would start with a Respawn Packet. Those should contain
        enough information to create a replay.

## Random developer notes:

    Some packets at the start of the recording are missing, as the player socket begins sending data
    before the onPlayerJoin hook gets a chance to inject it's intercepting handlers. The information
    (line 99 - 105) is therefore saved outside the normal recorded stream of data. It can then be
    reconstructed while preparing a replay.
    
    Decompiled spigot source code (using jd-gui):
    
    net/minecraft/server/v1_7_R1/PlayerList.java
    
    /*      */   public void a(NetworkManager networkmanager, EntityPlayer entityplayer) {
    /*   76 */     NBTTagCompound nbttagcompound = a(entityplayer);
    /*      */
    /*   78 */     entityplayer.spawnIn(this.server.getWorldServer(entityplayer.dimension));
    /*   79 */     entityplayer.playerInteractManager.a((WorldServer)entityplayer.world);
    /*   80 */     String s = "local";
    /*      */
    /*   82 */     if (networkmanager.getSocketAddress() != null) {
    /*   83 */       s = networkmanager.getSocketAddress().toString();
    /*      */     }
    /*      */
    /*   87 */     d.info(entityplayer.getName() + "[" + s + "] logged in with entity id " + entityplayer.getId() + " at ([" + entityplayer.world.worldData.getName() + "] " + entityplayer.locX + ", " + entityplayer.locY + ", " + entityplayer.locZ + ")");
    /*   88 */     WorldServer worldserver = this.server.getWorldServer(entityplayer.dimension);
    /*   89 */     ChunkCoordinates chunkcoordinates = worldserver.getSpawn();
    /*      */
    /*   91 */     a(entityplayer, (EntityPlayer)null, worldserver);
    /*   92 */     PlayerConnection playerconnection = new PlayerConnection(this.server, networkmanager, entityplayer);
    /*      */
    /*   95 */     int maxPlayers = getMaxPlayers();
    /*   96 */     if (maxPlayers > 60) {
    /*   97 */       maxPlayers = 60;
    /*      */     }
    /*   99 */     playerconnection.sendPacket(new PacketPlayOutLogin(entityplayer.getId(), entityplayer.playerInteractManager.getGameMode(), worldserver.getWorldData().isHardcore(), worldserver.worldProvider.dimension, worldserver.difficulty, maxPlayers, worldserver.getWorldData().getType()));
    /*  100 */     entityplayer.getBukkitEntity().sendSupportedChannels();
    /*      */
    /*  102 */     playerconnection.sendPacket(new PacketPlayOutCustomPayload("MC|Brand", getServer().getServerModName().getBytes(Charsets.UTF_8)));
    /*  103 */     playerconnection.sendPacket(new PacketPlayOutSpawnPosition(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z));
    /*  104 */     playerconnection.sendPacket(new PacketPlayOutAbilities(entityplayer.abilities));
    /*  105 */     playerconnection.sendPacket(new PacketPlayOutHeldItemSlot(entityplayer.inventory.itemInHandIndex));
    /*  106 */     entityplayer.x().d();
    /*  107 */     entityplayer.x().b(entityplayer);
    /*  108 */     a((ScoreboardServer)worldserver.getScoreboard(), entityplayer);
    /*  109 */     this.server.au();
    /*      */
    /*  116 */     c(entityplayer);
    
    onPlayerJoin is called here
    
    /*  117 */     playerconnection.a(entityplayer.locX, entityplayer.locY, entityplayer.locZ, entityplayer.yaw, entityplayer.pitch);
    /*  118 */     b(entityplayer, worldserver);
    /*  119 */     if (this.server.getResourcePack().length() > 0) {
    /*  120 */       entityplayer.a(this.server.getResourcePack());
    /*      */     }
    /*      */
    /*  123 */     Iterator iterator = entityplayer.getEffects().iterator();
    /*      */
    /*  125 */     while (iterator.hasNext()) {
    /*  126 */       MobEffect mobeffect = (MobEffect)iterator.next();
    /*      */
    /*  128 */       playerconnection.sendPacket(new PacketPlayOutEntityEffect(entityplayer.getId(), mobeffect));
    /*      */     }
    /*      */
    /*  131 */     entityplayer.syncInventory();
    /*  132 */     if ((nbttagcompound != null) && (nbttagcompound.hasKeyOfType("Riding", 10))) {
    /*  133 */       Entity entity = EntityTypes.a(nbttagcompound.getCompound("Riding"), worldserver);
    /*      */
    /*  135 */       if (entity != null) {
    /*  136 */         entity.o = true;
    /*  137 */         worldserver.addEntity(entity);
    /*  138 */         entityplayer.mount(entity);
    /*  139 */         entity.o = false;
    /*      */       }
    /*      */     }
    /*      */   }
