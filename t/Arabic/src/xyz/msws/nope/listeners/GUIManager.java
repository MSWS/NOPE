حزمة xyz.msws.nope.listners؛

استيراد java.util.HashMap؛
import java.util.HashSet;
استيراد java.util.Map؛
استيراد java.util.UUID؛

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
استيراد org.bukit.entity.Player؛
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
استيراد org.bukit.event.inventory.ClickType؛
استيراد org.bukit.event.inventory.InventoryClickEvent;
استيراد org.bukit.event.inventory.InventoryximeEvent;
استيراد org.bukit.inventory.ItemStack؛
import org.bukkit.scheduler.BukkitRunnable;

استيراد xyz.msws.nope.NOPE؛
استيراد xyz.msws.nope.modules.AbstractModule؛
استيراد xyz.msws.nope.modules.checks.CheckType؛
استيراد xyz.msws.nope.modules.data.CPlayer؛
استيراد xyz.msws.nope.modules.data.stat;
استيراد xyz.msws.nope.utils.MSG؛

/**
 * يستمع إلى واجهة اللاعب ويدير إحصائيات واجهة المستخدم
 * 
 * المؤلف Modm
 *
 */
مدراء الصف العام يوسِّع نطاق أدوات المستمع pstractModule
	الإحصائيات الخاصة؛

	GIManager(NOPE plugin) عامة {
		فائق (إضافة)؛
	}

	خريطة خاصة<UUID, String> openCheckType = HashMap<>؛
	خريطة خاصة<UUID, String> openHackcategory = HashMap<>؛

	مجموعة هاش الخاصة<UUID> تجاهل = HashSet<>();

	@EventHandler
	الفراغ العام onClick(حدث InventoryClickEvent) {
		إذا (!(event.getWhoClicked() مثالان من اللاعب))
			العودة؛
		لاعب Player = (Player) event.getWhoClicked();
		البند = event.getcurrentItemStack();
		إذا (البند == null <unk> item.getType() == Material.AIR)
			العودة؛

		CPlayer cp = plugin.getCPlayer(player)؛

		إذا ((cp.getInventory() == null)
			العودة؛

		event.setCملغاة (صحيح)؛

		إذا (!item.hasItemMeta() <unk> <unk> !item.getItemMeta().hasDisplayName())
			العودة؛

		player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1)؛

		تبديل (cp.getInventory()) {
			حالة "إحصائيات":
				نوع CheckType ؛
				جرب {
					نوع = CheckType.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName().toUpperCase())؛
				} المصيد (الاستثناء (هـ) {
					استراحة؛
				}
				إذا (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set("Checks" + MSG.camelCase(type + "") + ".Enabled",
							!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled"))؛
					لاعب.openInventory(stats.getInventory())؛
					cp.setInventory("stats");
					استراحة؛
				}
				لاعب.openInventory(stats.getInventory(type))؛
				cp.setInventory("hackType");
				openCheckType.put(player.getUniqueId(),
						ChatColor.stripColor(item.getItemMeta().getDisplayName().toUpperCase())؛
				استراحة؛
			قضية "HackType":
				سلسلة الاختراق = ChatColor.stripColor(item.getItemMeta().getDisplayName())؛
				إذا (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set(
							"Checks" + MSG.camelCase(openCheckType.get(player.getUniqueId()+"." + اختراق
									+ "مفعل"،
							!plugin.getConfig()
									.getBoolean("Checks." + MSG.camelCase(openCheckType.get(player.getUniqueId()+ "."
											<unk> <unk> <unk> <unk> <unk> <unk> <unk> <unk> <unk> <unk> <unk> <unk> <unk> <unk> <unk> + اختراق + ".Enabled"))؛
					ignore.add(player.getUniqueId())؛
					لاعب.openInventory(
							stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId())))؛
					cp.setInventory("hackType");
					استراحة؛
				}
				ignore.add(player.getUniqueId())؛
				لاعب.openInventory(stats.getInventory(hack))؛
				cp.setInventory("hackcategorory")؛
				openHackCategory.put(player.getUniqueId(), hack);
				استراحة؛
			قضية "hackcategorory":
				String hackcategory = openHackCategory.get(player.getUniqueId())؛
				String hackType = MSG.camelCase(openCheckType.get(player.getUniqueId())؛
				سلسلة التصحيح الإسم = ChatColor.stripColor(item.getItemMeta().getDisplayName())؛
				plugin.getConfig().set("Checks." + hackType + "." + hackcategory + "." + debugName + ".Enabled",
						!plugin.getConfig()
								.getBoolean("Checks." + hackType + "." + hackcategory + "." + debugName + ".Enabled"))؛
				ignore.add(player.getUniqueId())؛
				لاعب.openInventory(stats.getInventory(hackCategory))؛
				cp.setInventory("hackcategorory")؛
				استراحة؛
		}
	}

	@EventHandler
	الفراغ العام oncle(InventoryCloseEvent event) {
		إذا (!(event.getPlayer() مثيل من اللاعب))
			العودة؛
		لاعب Player = (Player) event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player)؛

		إذا ((cp.getInventory() == null)
			العودة؛

		String inv = cp.getInventory();

		إذا (ignore.contains(player.getUniqueId())) {
			تجاهل.remove(player.getUniqueId())؛
			العودة؛
		}

		plugin.saveConfig();

		تبديل (عداء) {
			قضية "HackType":
				ignore.add(player.getUniqueId())؛
				BkkitRunnable() جديد
					@تجاوز
					تشغيل الفراغ العام() {
						لاعب.openInventory(stats.getInventory())؛
						cp.setInventory("stats");
					}
				}.runTaskLater(plugin, 1)؛
				العودة؛
			قضية "hackcategorory":
				BkkitRunnable() جديد
					@تجاوز
					تشغيل الفراغ العام() {
						لاعب.openInventory(
								stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId())))؛
						cp.setInventory("hackType");
					}
				}.runTaskLater(plugin, 1)؛
				العودة؛
			الافتراضي:
				استراحة؛
		}

		cp.setInventory(null);
	}

	@تجاوز
	الفراغ العام تمكين () {
		this.stats = plugin.getModule(Stats.class);
		Bukit.getPluginManager().registerEvents(هذه الإضافة)؛
	}

	@تجاوز
	تعطيل الفراغ العام() {
	}
}
