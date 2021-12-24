package modules.CustomMobs;

import org.bukkit.Location;

import net.md_5.bungee.api.ChatColor;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.EntityZombie;
import net.minecraft.server.v1_16_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_16_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_16_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_16_R3.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_16_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_16_R3.World;

public abstract class CustomZombie extends EntityZombie {

	public CustomZombie(EntityTypes<? extends EntityZombie> entitytypes, Location loc, World world) {
		super(entitytypes, world);
		this.setPosition(loc);
		this.setCustomName(new ChatComponentText(ChatColor.DARK_PURPLE+""+ChatColor.BOLD+"Death Knight's Slave"));
		this.setCustomNameVisible(true);
		this.setSprinting(true);
	
	}

	@Override
    protected void initPathfinder() {
		this.goalSelector.a(0, new PathfinderGoalFloat(this));
		this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, true));
		this.goalSelector.a(3, new PathfinderGoalMoveTowardsRestriction(this, 2.0F));
		this.goalSelector.a(4, new PathfinderGoalLookAtPlayer(this, EntityPlayer.class, 8.0F));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
    }
	
	 public void setPosition(Location location) {
		 this.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	 }
}