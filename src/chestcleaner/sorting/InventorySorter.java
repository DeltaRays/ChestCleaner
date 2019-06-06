package chestcleaner.sorting;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import chestcleaner.utils.InventoryConverter;
import chestcleaner.utils.InventoryDetector;

public class InventorySorter {

	private static ArrayList<ItemStack> getFullStacks(ArrayList<ItemStack> list) {

		ArrayList<ItemStack> items = new ArrayList<>();
		ArrayList<Integer> amounts = new ArrayList<>();

		for (int i = 0; i < list.size(); i++) {

			ItemStack item = list.get(i);
			int amount = item.getAmount();
			item.setAmount(1);

			int index = -1;
			for (int j = 0; j < items.size(); j++) {
				if (items.get(j).isSimilar(list.get(i))) {
					index = j;
					break;
				}
			}

			if (index >= 0) {
				amounts.set(index, amounts.get(index) + amount);
			} else {
				items.add(item);
				amounts.add(amount);
			}

		}

		ArrayList<ItemStack> out = new ArrayList<>();

		for (int i = 0; i < items.size(); i++) {
			int stacks = (amounts.get(i) / items.get(i).getType().getMaxStackSize());
			for (int j = 0; j < stacks; j++) {
				ItemStack item = items.get(i).clone();
				item.setAmount(items.get(i).getMaxStackSize());
				out.add(item);
			}

			int remainingItems = amounts.get(i) % items.get(i).getMaxStackSize();
			if (remainingItems > 0) {
				ItemStack item = items.get(i).clone();
				item.setAmount(remainingItems);
				out.add(item);
			}

		}

		return out;

	}

	/**
	 * Sorts any kind of inventory.
	 * 
	 * @param inv
	 *            the inventory you want to sort.
	 */
	public static void sortInventory(Inventory inv) {

		ArrayList<ItemStack> list = InventoryConverter.getArrayListFormInventory(inv);
		ArrayList<ItemStack> temp = new ArrayList<ItemStack>();

		if (list.size() <= 1) {
			InventoryConverter.setItemsOfInventory(inv, list);
		}

		Quicksort sorter = new Quicksort(list);
		temp = sorter.sort(0, list.size() - 1);
		ArrayList<ItemStack> out = getFullStacks(temp);

		InventoryConverter.setItemsOfInventory(inv, out);

	}

	/**
	 * Sorts a part of the inventory of a player. It sorts the slots with the
	 * index 9 to 35, that means the hotbar, armor slot and the extra item slot
	 * are not effected.
	 * 
	 * @param p The player whose inventory you want to sort.
	 */
	public static void sortPlayerInv(Player p) {

		ArrayList<ItemStack> list = InventoryDetector.getPlayerInventoryList(p);
		ArrayList<ItemStack> temp = new ArrayList<ItemStack>();

		if (list.size() <= 1) {
			InventoryConverter.setPlayerInventory(list, p);
		}

		Quicksort sorter = new Quicksort(list);
		temp = sorter.sort(0, list.size() - 1);
		ArrayList<ItemStack> out = getFullStacks(temp);

		InventoryConverter.setPlayerInventory(out, p);
	}

	/**
	 * Checks if the block has an inventory or if it is an enderchest and sorts
	 * it.
	 * 
	 * @param b
	 *            Block you want to get sorted.
	 * @param p
	 *            the player or owner of an enderchest inventory.
	 * @return returns true if an inventory got sorted, otherwise false.
	 */
	public static boolean sortPlayerBlock(Block b, Player p) {

		Inventory inv = InventoryDetector.getInventoryFormBlock(b);

		if (inv != null) {
			if (p != null) {
				playSortingSound(p);
			}
			sortInventory(inv);
			return true;
		}

		if (p != null) {
			if (b.getBlockData().getMaterial() == Material.ENDER_CHEST) {
				playSortingSound(p);
				sortInventory(p.getEnderChest());
				return true;
			}
		}

		return false;
	}

	public static void playSortingSound(Player p) {
		if (!Bukkit.getVersion().contains("1.8")) {
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PIG_SADDLE, 2F, 2F);
		}
	}

}
