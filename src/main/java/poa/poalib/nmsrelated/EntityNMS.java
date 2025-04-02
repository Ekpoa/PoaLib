package poa.poalib.nmsrelated;

import net.minecraft.world.entity.Pose;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class EntityNMS {

    public static void setPose(LivingEntity entity, String pose){
        ((CraftLivingEntity) entity).getHandle().setPose(Pose.valueOf(pose));

    }
    public static String getStringPose(LivingEntity entity){
        return ((CraftLivingEntity) entity).getHandle().getPose().toString();
    }

    public static void setNoClip(Entity entity, boolean noClip){
        ((CraftEntity) entity).getHandle().noPhysics = noClip;
    }


    public static boolean getNoClip(Entity entity){
        return ((CraftEntity) entity).getHandle().noPhysics;
    }



}
