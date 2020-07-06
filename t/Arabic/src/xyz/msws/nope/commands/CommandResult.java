حزمة xyz.msws.nope.command;

استيراد xyz.msws.nope.utils.MSG؛

/**
 * يمثل نتيجة أمر ما.
 * 
 * المؤلف Modm
 *
 */
نتيجة القائد العام {
	/**
	 * تم إكمال الأمر بنجاح. رسالة نجاح مخصصة يجب أن تكون
	 * تم إرسالها.
	 */
	مكشوف
	/**
	 * لا يملك المرسل الصلاحيات المناسبة للأوامر.
	 */
	لا_تمهيد,
	/**
	 * هناك حجة مفقودة.
	 */
	الأرضية_المكانية،
	/**
	 * قدمت حجة غير صحيحة.
	 */
	INVALID_ARGUMENT،
	/**
	 * يمكن فقط للاعب استخدام الأمر و المرسل ليس واحد.
	 */
	لاعب_احد،
	/**
	 * لم يعط المنفذ اللاعب، كما هو الحال
	 * {@link commandResult#MISSING_ARGUMENT} ولكن أكثر تحديداً
	 */
	اللعب_REQUIRED،
	/**
	 * حدث خطأ غير معروف
	 */
	خطأ؛

	سلسلة عامة getMessage() {
		تبديل (هذا) {
			القضية INVALID_ARGUMENT:
				إرجاع MSG.getString("command.InvalidArgument", "&cAn invغير صالح تم تقديم حجة ")؛
			قضية MISSING_ARGUMENT:
				إرجاع MSG.getString("command.MissingArgument", "&cYou تفقد حجة.");
			القضية NO_PERMISSION:
				إرجاع MSG.getString("command.Nopermission",
						"&4&l[&c&lNOPE&4&l] &cأنت تفتقر إلى &a%perm% &cpermission.");
			PLAYER_ONLY:
				إرجاع MSG.getString("command.PlayerOnly",
						"&cيجب عليك تحديد مشغل لتشغيل هذا الأمر كوحدة تحكم.");
			قضية PLAYER_REQUIRED:
				إرجاع MSG.getString("command.SpecifyPlayer", "&cيجب عليك تحديد لاعب كحجة.")؛
			القضية الفرعية:
				إرجاع ""؛
			الافتراضي:
				استراحة؛
		}
		إرجاع "&4A حدث خطأ أثناء تنفيذ الأمر.";
	}
}
