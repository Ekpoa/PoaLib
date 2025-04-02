package poa.poalib.items;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.Map;

public class Attributes {

    public static ItemStack copyAttributes(ItemStack toCopy, ItemStack newStack, boolean directApply){
        ItemMeta meta = toCopy.getItemMeta();
        ItemStack tr = newStack.clone();
        if(directApply)
            tr = newStack;
        ItemMeta meta1 = tr.getItemMeta();
        for (Map.Entry<Attribute, Collection<AttributeModifier>> attributeCollectionEntry : meta.getAttributeModifiers().asMap().entrySet()) {
            for (AttributeModifier attributeModifier : attributeCollectionEntry.getValue()) {
                meta1.addAttributeModifier(attributeCollectionEntry.getKey(),attributeModifier);
            }
        }
        tr.setItemMeta(meta1);
        return tr;
    }

    public static ItemStack copyAttributes(ItemStack toCopy, ItemStack newStack){
        return copyAttributes(toCopy, newStack, false);
    }

}
