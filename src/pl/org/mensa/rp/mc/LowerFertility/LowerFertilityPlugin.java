package pl.org.mensa.rp.mc.LowerFertility;

import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class LowerFertilityPlugin extends JavaPlugin {
	public static final String prefix = "&3[&bLowerFertility&3]&e";
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdesc = getDescription();
		
		this.getServer().getPluginManager().registerEvents(new CropListener(), this);
		
		Iterator<Recipe> it = getServer().recipeIterator();
		
		ShapelessRecipe newRecipe = new ShapelessRecipe(new NamespacedKey(this, "PUMPKIN_SEEDS_ONE"), new ItemStack(Material.PUMPKIN_SEEDS));
		newRecipe.addIngredient(Material.PUMPKIN);
		
		while (it.hasNext()) {
			Recipe recipe = it.next();
			
			if (recipe.getResult().getType() == Material.PUMPKIN_SEEDS) {
				it.remove();
			}
		}
		
		getServer().addRecipe(newRecipe);
		
		Utils.log("&a" + pdesc.getFullName() + " enabled");
	}
	
	@Override
	public void onDisable() {
		Iterator<Recipe> it = getServer().recipeIterator();
		
		ShapelessRecipe newRecipe = new ShapelessRecipe(new NamespacedKey(this, "PUMPKIN_SEEDS"), new ItemStack(Material.PUMPKIN_SEEDS, 4));
		newRecipe.addIngredient(Material.PUMPKIN);
		
		while (it.hasNext()) {
			Recipe recipe = it.next();
			
			if (recipe.getResult().getType() == Material.PUMPKIN_SEEDS) {
				it.remove();
			}
		}
		
		getServer().addRecipe(newRecipe);
	}
}
