حزمة xyz.msws.nope.command;

استيراد java.util.ArrayList؛
استيراد java.util.List؛

استيراد javax.annotation.Nullable؛

import org.bukkit.command.CommandSender;

استيراد xyz.msws.nope.NOPE؛

/**
 * يمثل قيادة فرعية لقيادة رئيسية. من المتوقع كل أمر فرعي
 * يتعامل مع منطقه داخليا. يمكن أن يشمل ذلك وجود أوامر فرعية
 * الأوامر.
 * 
 * المؤلف Modm
 *
 */
الأمر الفرعي الخاص بالفصل المجرد العام {

	البرنامج المساعد NOPE المحمي؛

	الوكيل الفرعي العام (NOPE plugin) {
		هذه.plugin = الإضافة؛
	}

	@غير قابل
	قائمة عامة مجردة<String[]> tabCompletions(قائد المرسل)؛

	ملخص عام getName();

	الملخص العام String getUsage();

	خلاصة السلسلة العامة getDescription();

	قائمة عامة<String> getAliases() {
		إرجاع قائمة مصفوفة جديدة<>();
	}

	سلسلة عامة getPermission() {
		إرجاع بطل؛
	}

	تنفيذ نتائج القيادة المجردة العامة (القائد المرسل ، String[] args)؛
}
