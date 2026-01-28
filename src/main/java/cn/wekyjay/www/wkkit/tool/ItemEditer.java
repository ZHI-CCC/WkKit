package cn.wekyjay.www.wkkit.tool;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemEditer {
	private ItemStack itemStack;

	public ItemEditer(ItemStack itemStack) {
		if (itemStack == null) {
			throw new IllegalArgumentException("ItemStack cannot be null");
		}
		this.itemStack = itemStack.clone();
	}

	public ItemEditer(ItemStack itemStack, String displayName) {
		if (itemStack == null) {
			throw new IllegalArgumentException("ItemStack cannot be null");
		}
		this.itemStack = itemStack.clone();
		ItemMeta im = this.itemStack.getItemMeta();
		if (im != null) {
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
			this.itemStack.setItemMeta(im);
		}
	}

	/**
	 * 设置物品名称
	 * @param name 显示名称
	 * @return ItemEditer实例
	 */
	public ItemEditer setDisplayName(String name) {
		ItemMeta im = itemStack.getItemMeta();
		if (im != null) {
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
			itemStack.setItemMeta(im);
		}
		return this;
	}

	/**
	 * 设置物品Lore
	 * @param lore Lore列表
	 * @return ItemEditer实例
	 */
	public ItemEditer setLore(List<String> lore) {
		ItemMeta im = itemStack.getItemMeta();
		if (im != null) {
			List<String> newlore = new ArrayList<>();
			for (String str : lore) {
				newlore.add(ChatColor.translateAlternateColorCodes('&', str));
			}
			im.setLore(newlore);
			itemStack.setItemMeta(im);
		}
		return this;
	}

	public ItemEditer setNBTString(String key, String value) {
		ReadWriteNBT nbt = NBT.itemStackToNBT(itemStack);
		nbt.setString(key, value);
		itemStack = NBT.itemStackFromNBT(nbt);
		return this;
	}

	public ItemEditer setNBTInteger(String key, Integer value) {
		ReadWriteNBT nbt = NBT.itemStackToNBT(itemStack);
		nbt.setInteger(key, value);
		itemStack = NBT.itemStackFromNBT(nbt);
		return this;
	}

	public ItemEditer removeNBT(String key) {
		ReadWriteNBT nbt = NBT.itemStackToNBT(itemStack);
		nbt.removeKey(key);
		itemStack = NBT.itemStackFromNBT(nbt);
		return this;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public String getDisplayName() {
		if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
			return itemStack.getItemMeta().getDisplayName();
		}
		return "";
	}

	public List<String> getLore() {
		if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
			return itemStack.getItemMeta().getLore();
		}
		return new ArrayList<>();
	}

	public ReadWriteNBT getNBT() {
		return NBT.itemStackToNBT(itemStack);
	}

	/**
	 * 判断物品是否有wkkit标签，自动适配不同版本
	 * @param item 目标物品
	 * @return 是否包含标签
	 */
	public static boolean hasWkKitTag(ItemStack item) {
		if (item == null || item.getType().isAir()) {
			return false;
		}

		String fullVersion = WKTool.getFullVersion();
		ReadWriteNBT nbt = WKTool.getItemNBT(item);

		if (nbt == null) return false;

		if (WKTool.compareVersion(fullVersion, "1.20.5") >= 0) {
			// 1.20.5及以上，检查 components -> custom_data
			ReadWriteNBT components = nbt.getCompound("components");
			if (components != null) {
				ReadWriteNBT customData = components.getCompound("minecraft:custom_data");
				return customData != null && customData.hasTag("wkkit");
			}
			return false;
		} else {
			// 低于1.20.5，检查根节点 tag
			ReadWriteNBT tag = nbt.getCompound("tag");
			return tag != null && tag.hasTag("wkkit");
		}
	}

	/**
	 * 获取物品wkkit标签的值，自动适配不同版本
	 * @param item 目标物品
	 * @return 标签值，若不存在返回null
	 */
	public static String getWkKitTagValue(ItemStack item) {
		if (item == null || item.getType().isAir()) {
			return null;
		}

		String fullVersion = WKTool.getFullVersion();
		ReadWriteNBT nbt = WKTool.getItemNBT(item);

		if (nbt == null) return null;

		if (WKTool.compareVersion(fullVersion, "1.20.5") >= 0) {
			// 1.20.5及以上
			ReadWriteNBT components = nbt.getCompound("components");
			if (components != null) {
				ReadWriteNBT customData = components.getCompound("minecraft:custom_data");
				if (customData != null && customData.hasTag("wkkit")) {
					return customData.getString("wkkit");
				}
			}
			return null;
		} else {
			// 低于1.20.5
			ReadWriteNBT tag = nbt.getCompound("tag");
			if (tag != null && tag.hasTag("wkkit")) {
				return tag.getString("wkkit");
			}
			return null;
		}
	}
}