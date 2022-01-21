package pl.org.mensa.rp.mc.LowerFertility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class CropListener implements Listener {
	private static Random random = new Random();
	
	private static final Map<Material, Double> modified_crops = new HashMap<Material, Double>();
	private static final Map<Material, Double> modified_blocks = new HashMap<Material, Double>();
	
	private static final Map<Material, Material> block_environment = new HashMap<Material, Material>();
	private static final Map<Material, List<Material>> block_drops = new HashMap<Material, List<Material>>();
	
	static { // it's not like this will become a community project and they'll laugh at my laziness. who needs configs anyways
		modified_crops.put(Material.WHEAT, 1.0D);
		modified_crops.put(Material.WHEAT_SEEDS, 1.06D);
		modified_crops.put(Material.CARROT, 1.2D);
		modified_crops.put(Material.POTATO, 1.1D);
		modified_crops.put(Material.MELON_SLICE, 1.5D);
		modified_crops.put(Material.COCOA_BEANS, 1.5D);
		modified_crops.put(Material.SWEET_BERRIES, 1.5D);
		modified_crops.put(Material.NETHER_WART, 1.1D);
		modified_crops.put(Material.APPLE, 1.0D);
		
		modified_blocks.put(Material.KELP, 0.25D);
		modified_blocks.put(Material.CACTUS, 0.7D);
		modified_blocks.put(Material.SUGAR_CANE, 0.5D);
		modified_blocks.put(Material.BAMBOO, 0.5D);
		
		block_environment.put(Material.KELP, Material.WATER);
		block_environment.put(Material.KELP_PLANT, Material.WATER);
		block_environment.put(Material.CACTUS, Material.AIR);
		block_environment.put(Material.SUGAR_CANE, Material.AIR);
		block_environment.put(Material.BAMBOO, Material.AIR);
		
		block_drops.put(Material.KELP_PLANT, Arrays.asList(Material.KELP));
	}
	
	private int rollAmount(double average) {
		int amount = 0;
		++average;
		
		while (--average > 1) ++amount;
		
		return amount + (random.nextDouble() < average ? 1 : 0);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockDropItem(BlockDropItemEvent e) {
		boolean not_my_problem = true;
		for (Item item : e.getItems()) {
			if (modified_crops.containsKey(item.getItemStack().getType())) {
				not_my_problem = false;
				break;
			}
		}
		if (not_my_problem) return;
		
		Map<Material, Integer> drops = new HashMap<Material, Integer>();
		Iterator<Item> it = e.getItems().iterator();
		while (it.hasNext()) {
			ItemStack item = it.next().getItemStack();
			
			Integer amount = drops.putIfAbsent(item.getType(), item.getAmount());
			if (amount != null) {
				drops.put(item.getType(), amount+item.getAmount());
				it.remove();
			}
		}
		
		for (Item item : e.getItems()) {
			ItemStack is = item.getItemStack();
			is.setAmount(drops.get(item.getItemStack().getType()));
			item.setItemStack(is);
		}
		
		e.getItems().stream().forEach(item -> {
			if (modified_crops.containsKey(item.getItemStack().getType())) {
				item.getItemStack().setAmount(rollAmount(modified_crops.get(item.getItemStack().getType())));
			}
		});
		
//		Material type = e.getBlockState().getType();
//		for (Block block = e.getBlock().getRelative(0, 1, 0); block.getType() == type; block = block.getRelative(0, 1, 0)) {
//			block.breakNaturally();
//			Utils.log("&eBroke " + block.getType() + " (" + block.getX() + " " + block.getY() + " " + block.getZ() + ")");
//		}
		
//		for (Block block = e.getBlock(); modified_crops.keySet().stream().anyMatch(block.getDrops().stream().map(item -> item.getType()).collect(Collectors.toList())::contains); block = block.getRelative(0, 1, 0)) {
//			for (ItemStack item : block.getDrops()) {
//				if (modified_crops.containsKey(item.getType())) {
//					int amount = 0;
//					double chance = modified_crops.get(item.getType()) + 1;
//					
//					while (--chance >= 1) ++amount;
//					item.setAmount(amount + (random.nextDouble() < chance ? 1 : 0));
//				}
//			}
//		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		if (modified_blocks.containsKey(e.getBlock().getType())) {
			Material type = e.getBlock().getType();
			
			e.setCancelled(true);
			
			for (Block block = e.getBlock(); block.getType() == type; block = block.getRelative(0, 1, 0)) {
				List<Material> drop_materials = block_drops.getOrDefault(block.getType(), Arrays.asList(block.getType()));
				for (Material drop_material : drop_materials) {
					block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(drop_material, rollAmount(modified_blocks.get(drop_material))));
				}
				
				block.setType(block_environment.get(block.getType()), false);
			}
		}
	}
}
