package poa.poalib.displays;

import jdk.jfr.Description;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;
import poa.poalib.PoaLib;

import java.util.ArrayList;
import java.util.List;

public class Displays {


    @Description("Axis: 1 = y, 2 = x, 3 = z")
    public static void spawnRotatingItemDisplays(double circumference, int rotationSpeed, Location center, List<ItemStack> items, Vector scale, int axis) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Item list cannot be empty.");
        }

        int amount = items.size();
        double radius = circumference / (2 * Math.PI);
        double angleStep = 2 * Math.PI / amount;
        List<ItemDisplay> displays = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            double angle = i * angleStep;
            Location spawnLoc = center.clone().add(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
            ItemDisplay display = (ItemDisplay) center.getWorld().spawnEntity(spawnLoc, EntityType.ITEM_DISPLAY);
            display.setItemStack(items.get(i));

            final Transformation transformation = display.getTransformation();
            display.setTransformation(new Transformation(
                    transformation.getTranslation(),
                    transformation.getLeftRotation(),
                    new Vector3f((float) scale.getX(), (float) scale.getY(), (float) scale.getZ()),
                    transformation.getRightRotation()
            ));

            displays.add(display);
        }

        new BukkitRunnable() {
            double currentAngle = 0;
            final double rotationStep = (2 * Math.PI) / (rotationSpeed * 20);
            final Chunk chunk = displays.getFirst().getChunk();

            @Override
            public void run() {
                if (!chunk.isLoaded()) return;

                currentAngle += rotationStep;
                for (int i = 0; i < displays.size(); i++) {
                    double angle = i * angleStep + currentAngle;
                    final ItemDisplay itemDisplay = displays.get(i);

                    if (itemDisplay == null || !itemDisplay.isValid()) {
                        this.cancel();
                        return;
                    }

                    Location newLoc = center.clone();
                    switch (axis) {
                        case 1: // Rotate around Y-axis (horizontal)
                            newLoc.add(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
                            break;
                        case 2: // Rotate around X-axis (up/down)
                            newLoc.add(0, Math.sin(angle) * radius, Math.cos(angle) * radius);
                            break;
                        case 3: // Rotate around Z-axis (sideways)
                            newLoc.add(Math.cos(angle) * radius, Math.sin(angle) * radius, 0);
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid axis. Use 1 (Y-axis), 2 (X-axis), or 3 (Z-axis).");
                    }

                    itemDisplay.teleportAsync(newLoc);
                }
            }
        }.runTaskTimerAsynchronously(PoaLib.libINSTANCE, 0L, 1L);
    }




}
