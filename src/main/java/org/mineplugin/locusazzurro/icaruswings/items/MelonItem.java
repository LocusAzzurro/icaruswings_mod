package org.mineplugin.locusazzurro.icaruswings.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.mineplugin.locusazzurro.icaruswings.data.ModGroup;
import org.mineplugin.locusazzurro.icaruswings.registry.BlockRegistry;

import javax.annotation.Nullable;

public class MelonItem extends BlockItem {
	
	public MelonItem() {
		super(BlockRegistry.melon.get(), new Item.Properties().tab(ModGroup.itemGroup).rarity(Rarity.RARE));
	}
	
	@Nullable
	@Override
	public EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return EquipmentSlot.HEAD;
	}
}
