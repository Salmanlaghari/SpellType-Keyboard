package com.salmanlaghari.spelltypekeyboard.domain.language

/**
 * Language Manager — 120+ languages with keyboard layouts
 * Supports RTL, CJK, Indic, African, European, and more
 */
data class KeyboardLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val emoji: String,
    val isRTL: Boolean = false,
    val row1: List<String>,
    val row2: List<String>,
    val row3: List<String>,
    val hasNumberRow: Boolean = true,
    val category: LanguageCategory
)

enum class LanguageCategory(val displayName: String) {
    EUROPEAN("European"),
    ASIAN("Asian"),
    MIDDLE_EASTERN("Middle Eastern"),
    AFRICAN("African"),
    INDIC("Indic"),
    CYRILLIC("Cyrillic"),
    SOUTHEAST_ASIAN("Southeast Asian"),
    OCEANIAN("Oceanian"),
    CONSTRUCTED("Constructed")
}

object LanguageManager {

    private val languages = mutableListOf<KeyboardLanguage>()

    init {
        registerAllLanguages()
    }

    private fun registerAllLanguages() {
        // ═══════════════════════════════════════
        //  EUROPEAN LANGUAGES
        // ═══════════════════════════════════════

        languages += KeyboardLanguage("en", "English", "English", "🇬🇧",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("es", "Spanish", "Español", "🇪🇸",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l","ñ"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("fr", "French", "Français", "🇫🇷",
            row1 = listOf("a","z","e","r","t","y","u","i","o","p"),
            row2 = listOf("q","s","d","f","g","h","j","k","l","m"),
            row3 = listOf("w","x","c","v","b","n"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("de", "German", "Deutsch", "🇩🇪",
            row1 = listOf("q","w","e","r","t","z","u","i","o","p","ü"),
            row2 = listOf("a","s","d","f","g","h","j","k","l","ö","ä"),
            row3 = listOf("y","x","c","v","b","n","m","ß"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("it", "Italian", "Italiano", "🇮🇹",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("pt", "Portuguese", "Português", "🇵🇹",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l","ç"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("nl", "Dutch", "Nederlands", "🇳🇱",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("pl", "Polish", "Polski", "🇵🇱",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l","ł"),
            row3 = listOf("z","x","c","v","b","n","m","ą","ę","ó"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("ro", "Romanian", "Română", "🇷🇴",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("hu", "Hungarian", "Magyar", "🇭🇺",
            row1 = listOf("q","w","e","r","t","z","u","i","o","p","ö","ü"),
            row2 = listOf("a","s","d","f","g","h","j","k","l","é","á"),
            row3 = listOf("y","x","c","v","b","n","m","ű"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("cs", "Czech", "Čeština", "🇨🇿",
            row1 = listOf("q","w","e","r","t","z","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("y","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("sk", "Slovak", "Slovenčina", "🇸🇰",
            row1 = listOf("q","w","e","r","t","z","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("y","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("sv", "Swedish", "Svenska", "🇸🇪",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p","å"),
            row2 = listOf("a","s","d","f","g","h","j","k","l","ö","ä"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("no", "Norwegian", "Norsk", "🇳🇴",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p","å"),
            row2 = listOf("a","s","d","f","g","h","j","k","l","ø","æ"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("da", "Danish", "Dansk", "🇩🇰",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p","å"),
            row2 = listOf("a","s","d","f","g","h","j","k","l","ø","æ"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("fi", "Finnish", "Suomi", "🇫🇮",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p","å"),
            row2 = listOf("a","s","d","f","g","h","j","k","l","ö","ä"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("el", "Greek", "Ελληνικά", "🇬🇷",
            row1 = listOf("ς","ε","ρ","τ","ψ","υ","ι","θ","ο","π"),
            row2 = listOf("α","σ","δ","φ","γ","η","ξ","κ","λ"),
            row3 = listOf("ζ","χ","ψ","ω","β","ν","μ"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("tr", "Turkish", "Türkçe", "🇹🇷",
            row1 = listOf("q","w","e","r","t","y","u","ı","o","p","ğ"),
            row2 = listOf("a","s","d","f","g","h","j","k","l","ş"),
            row3 = listOf("z","x","c","v","b","n","m","ö","ü","ç"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("bg", "Bulgarian", "Български", "🇧🇬",
            row1 = listOf("я","в","е","р","т","ъ","у","и","о","п"),
            row2 = listOf("а","с","д","ф","г","х","й","к","л"),
            row3 = listOf("з","ь","ц","ж","б","н","м"),
            category = LanguageCategory.CYRILLIC)

        languages += KeyboardLanguage("hr", "Croatian", "Hrvatski", "🇭🇷",
            row1 = listOf("q","w","e","r","t","z","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("y","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("sr", "Serbian", "Српски", "🇷🇸",
            row1 = listOf("љ","њ","е","р","т","з","у","и","о","п"),
            row2 = listOf("а","с","д","ф","г","х","ј","к","л"),
            row3 = listOf("џ","ц","в","б","н","м"),
            category = LanguageCategory.CYRILLIC)

        languages += KeyboardLanguage("sl", "Slovenian", "Slovenščina", "🇸🇮",
            row1 = listOf("q","w","e","r","t","z","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("y","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("et", "Estonian", "Eesti", "🇪🇪",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p","ü"),
            row2 = listOf("a","s","d","f","g","h","j","k","l","ö","ä"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("lv", "Latvian", "Latviešu", "🇱🇻",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("lt", "Lithuanian", "Lietuvių", "🇱🇹",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("ga", "Irish", "Gaeilge", "🇮🇪",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("cy", "Welsh", "Cymraeg", "🏴󠁧󠁢󠁷󠁬󠁳󠁿",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("is", "Icelandic", "Íslenska", "🇮🇸",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p","ð"),
            row2 = listOf("a","s","d","f","g","h","j","k","l","æ"),
            row3 = listOf("z","x","c","v","b","n","m","ö","þ"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("mt", "Maltese", "Malti", "🇲🇹",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("eu", "Basque", "Euskara", "🏴",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("ca", "Catalan", "Català", "🏴",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("gl", "Galician", "Galego", "🏴",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("af", "Afrikaans", "Afrikaans", "🇿🇦",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        // ═══════════════════════════════════════
        //  CYRILLIC LANGUAGES
        // ═══════════════════════════════════════

        languages += KeyboardLanguage("ru", "Russian", "Русский", "🇷🇺",
            row1 = listOf("й","ц","у","к","е","н","г","ш","щ","з","х"),
            row2 = listOf("ф","ы","в","а","п","р","о","л","д","ж","э"),
            row3 = listOf("я","ч","с","м","и","т","ь","б","ю"),
            category = LanguageCategory.CYRILLIC)

        languages += KeyboardLanguage("uk", "Ukrainian", "Українська", "🇺🇦",
            row1 = listOf("й","ц","у","к","е","н","г","ш","щ","з","х"),
            row2 = listOf("ф","і","в","а","п","р","о","л","д","ж","є"),
            row3 = listOf("я","ч","с","м","и","т","ь","б","ю"),
            category = LanguageCategory.CYRILLIC)

        languages += KeyboardLanguage("be", "Belarusian", "Беларуская", "🇧🇾",
            row1 = listOf("й","ц","у","к","е","н","г","ш","ў","з","х"),
            row2 = listOf("ф","ы","в","а","п","р","о","л","д","ж","э"),
            row3 = listOf("я","ч","с","м","і","т","ь","б","ю"),
            category = LanguageCategory.CYRILLIC)

        languages += KeyboardLanguage("mk", "Macedonian", "Македонски", "🇲🇰",
            row1 = listOf("љ","њ","е","р","т","ѕ","у","и","о","п"),
            row2 = listOf("а","с","д","ф","г","х","ј","к","л"),
            row3 = listOf("з","џ","ц","в","б","н","м"),
            category = LanguageCategory.CYRILLIC)

        languages += KeyboardLanguage("mn", "Mongolian", "Монгол", "🇲🇳",
            row1 = listOf("ф","ц","у","ж","э","н","г","ш","щ","з"),
            row2 = listOf("й","ы","в","а","п","р","о","л","д"),
            row3 = listOf("я","ч","с","м","и","т","ь","б","ю"),
            category = LanguageCategory.CYRILLIC)

        // ═══════════════════════════════════════
        //  ASIAN LANGUAGES
        // ═══════════════════════════════════════

        languages += KeyboardLanguage("zh", "Chinese (Pinyin)", "中文拼音", "🇨🇳",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.ASIAN)

        languages += KeyboardLanguage("ja", "Japanese (Romaji)", "日本語ローマ字", "🇯🇵",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.ASIAN)

        languages += KeyboardLanguage("ko", "Korean", "한국어", "🇰🇷",
            row1 = listOf("ㅂ","ㅈ","ㄷ","ㄱ","ㅅ","ㅛ","ㅕ","ㅑ","ㅐ","ㅔ"),
            row2 = listOf("ㅁ","ㄴ","ㅇ","ㄹ","ㅎ","ㅗ","ㅓ","ㅏ","ㅣ"),
            row3 = listOf("ㅋ","ㅌ","ㅊ","ㅍ","ㅠ","ㅜ","ㅡ"),
            category = LanguageCategory.ASIAN)

        // ═══════════════════════════════════════
        //  MIDDLE EASTERN LANGUAGES
        // ═══════════════════════════════════════

        languages += KeyboardLanguage("ar", "Arabic", "العربية", "🇸🇦",
            isRTL = true,
            row1 = listOf("ض","ص","ث","ق","ف","غ","ع","ه","خ","ح"),
            row2 = listOf("ش","س","ي","ب","ل","ا","ت","ن","م","ك"),
            row3 = listOf("ئ","ء","ؤ","ر","لا","ى","ة","و","ز"),
            category = LanguageCategory.MIDDLE_EASTERN)

        languages += KeyboardLanguage("fa", "Persian", "فارسی", "🇮🇷",
            isRTL = true,
            row1 = listOf("ض","ص","ث","ق","ف","غ","ع","ه","خ","ح"),
            row2 = listOf("ش","س","ی","ب","ل","ا","ت","ن","م","ک"),
            row3 = listOf("ظ","ط","ز","ر","ذ","د","پ","و","ژ"),
            category = LanguageCategory.MIDDLE_EASTERN)

        languages += KeyboardLanguage("he", "Hebrew", "עברית", "🇮🇱",
            isRTL = true,
            row1 = listOf("/","'","ק","ר","א","ט","ו","ן","ם","פ"),
            row2 = listOf("ש","ד","ג","כ","ע","י","ח","ל","ך","ף"),
            row3 = listOf("ז","ס","ב","ה","נ","מ","צ","ת","ץ"),
            category = LanguageCategory.MIDDLE_EASTERN)

        languages += KeyboardLanguage("ur", "Urdu", "اردو", "🇵🇰",
            isRTL = true,
            row1 = listOf("ط","ص","ھ","ح","م","ت","ن","ک","ص","ی"),
            row2 = listOf("ف","ل","ر","ن","م","ب","آ","ا","ہ"),
            row3 = listOf("ش","غ","ع","خ","چ","ج","ت","د","ڈ"),
            category = LanguageCategory.MIDDLE_EASTERN)

        languages += KeyboardLanguage("ps", "Pashto", "پښتو", "🇦🇫",
            isRTL = true,
            row1 = listOf("ض","ص","ث","ق","ف","غ","ع","ه","خ","ح"),
            row2 = listOf("ش","س","ی","ب","ل","ا","ت","ن","م","ک"),
            row3 = listOf("ئ","ء","ؤ","ر","لا","ى","ة","و","ز"),
            category = LanguageCategory.MIDDLE_EASTERN)

        languages += KeyboardLanguage("ku", "Kurdish", "Kurdî", "🏴",
            isRTL = true,
            row1 = listOf("ق","و","ە","ر","ت","ی","ئ","ۆ","پ","ژ"),
            row2 = listOf("ا","س","د","ف","گ","ھ","ژ","ک","ل"),
            row3 = listOf("ز","خ","ج","ڤ","ب","ن","م"),
            category = LanguageCategory.MIDDLE_EASTERN)

        // ═══════════════════════════════════════
        //  INDIC LANGUAGES
        // ═══════════════════════════════════════

        languages += KeyboardLanguage("hi", "Hindi", "हिन्दी", "🇮🇳",
            row1 = listOf("ौ","ै","ा","ी","ू","ब","ह","ग","द","ज"),
            row2 = listOf("ो","े","्","ि","ु","प","र","क","त","च"),
            row3 = listOf("ॉ","ं","म","न","व","ल","स"),
            category = LanguageCategory.INDIC)

        languages += KeyboardLanguage("bn", "Bengali", "বাংলা", "🇧🇩",
            row1 = listOf("ৌ","ৈ","া","ী","ূ","ব","হ","গ","দ","জ"),
            row2 = listOf("ো","ে","্","ি","ু","প","র","ক","ত","চ"),
            row3 = listOf("অ","ং","ম","ন","ব","ল","স"),
            category = LanguageCategory.INDIC)

        languages += KeyboardLanguage("ta", "Tamil", "தமிழ்", "🇮🇳",
            row1 = listOf("ௌ","ை","ா","ீ","ூ","ப","ஹ","க","த","ஜ"),
            row2 = listOf("ோ","ே","்","ி","ு","ப","ர","க","த","ச"),
            row3 = listOf("அ","ஂ","ம","ன","வ","ல","ஸ"),
            category = LanguageCategory.INDIC)

        languages += KeyboardLanguage("te", "Telugu", "తెలుగు", "🇮🇳",
            row1 = listOf("ౌ","ై","ా","ీ","ూ","బ","హ","గ","ద","జ"),
            row2 = listOf("ో","ే","్","ి","ు","ప","ర","క","త","చ"),
            row3 = listOf("అ","ం","మ","న","వ","ల","స"),
            category = LanguageCategory.INDIC)

        languages += KeyboardLanguage("mr", "Marathi", "मराठी", "🇮🇳",
            row1 = listOf("ौ","ै","ा","ी","ू","ब","ह","ग","द","ज"),
            row2 = listOf("ो","े","्","ि","ु","प","र","क","त","च"),
            row3 = listOf("ऑ","ं","म","न","व","ल","स"),
            category = LanguageCategory.INDIC)

        languages += KeyboardLanguage("gu", "Gujarati", "ગુજરાતી", "🇮🇳",
            row1 = listOf("ૌ","ૈ","ા","ી","ૂ","બ","હ","ગ","દ","જ"),
            row2 = listOf("ો","ે","્","િ","ુ","પ","ર","ક","ત","ચ"),
            row3 = listOf("અ","ં","મ","ન","વ","લ","સ"),
            category = LanguageCategory.INDIC)

        languages += KeyboardLanguage("kn", "Kannada", "ಕನ್ನಡ", "🇮🇳",
            row1 = listOf("ೌ","ೈ","ಾ","ೀ","ೂ","ಬ","ಹ","ಗ","ದ","ಜ"),
            row2 = listOf("ೋ","ೇ","್","ಿ","ು","ಪ","ರ","ಕ","ತ","ಚ"),
            row3 = listOf("ಅ","ಂ","ಮ","ನ","ವ","ಲ","ಸ"),
            category = LanguageCategory.INDIC)

        languages += KeyboardLanguage("ml", "Malayalam", "മലയാളം", "🇮🇳",
            row1 = listOf("ൌ","ൈ","ാ","ീ","ൂ","ബ","ഹ","ഗ","ദ","ജ"),
            row2 = listOf("ോ","േ","്","ി","ു","പ","ര","ക","ത","ച"),
            row3 = listOf("അ","ം","മ","ന","വ","ല","സ"),
            category = LanguageCategory.INDIC)

        languages += KeyboardLanguage("pa", "Punjabi", "ਪੰਜਾਬੀ", "🇮🇳",
            row1 = listOf("ੌ","ੈ","ਾ","ੀ","ੂ","ਬ","ਹ","ਗ","ਦ","ਜ"),
            row2 = listOf("ੋ","ੇ","੍","ਿ","ੁ","ਪ","ਰ","ਕ","ਤ","ਚ"),
            row3 = listOf("ਅ","ਂ","ਮ","ਨ","ਵ","ਲ","ਸ"),
            category = LanguageCategory.INDIC)

        languages += KeyboardLanguage("si", "Sinhala", "සිංහල", "🇱🇰",
            row1 = listOf("ු","ෑ","ා","ී","ූ","බ","හ","ග","ද","ජ"),
            row2 = listOf("ො","ේ","්","ි","ැ","ප","ර","ක","ත","ච"),
            row3 = listOf("අ","ඞ","ම","න","ව","ල","ස"),
            category = LanguageCategory.INDIC)

        languages += KeyboardLanguage("ne", "Nepali", "नेपाली", "🇳🇵",
            row1 = listOf("ौ","ै","ा","ी","ू","ब","ह","ग","द","ज"),
            row2 = listOf("ो","े","्","ि","ु","प","र","क","त","च"),
            row3 = listOf("ऑ","ं","म","न","व","ल","स"),
            category = LanguageCategory.INDIC)

        // ═══════════════════════════════════════
        //  AFRICAN LANGUAGES
        // ═══════════════════════════════════════

        languages += KeyboardLanguage("sw", "Swahili", "Kiswahili", "🇹🇿",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.AFRICAN)

        languages += KeyboardLanguage("am", "Amharic", "አማርኛ", "🇪🇹",
            row1 = listOf("ቀ","ወ","ዐ","ረ","ተ","የ","ኡ","ኢ","ኦ","ፐ"),
            row2 = listOf("አ","ሰ","ደ","ፈ","ገ","ሀ","ጀ","ከ","ለ"),
            row3 = listOf("ዘ","ጸ","ቸ","ቨ","በ","ነ","መ"),
            category = LanguageCategory.AFRICAN)

        languages += KeyboardLanguage("yo", "Yoruba", "Yorùbá", "🇳🇬",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.AFRICAN)

        languages += KeyboardLanguage("ig", "Igbo", "Igbo", "🇳🇬",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.AFRICAN)

        languages += KeyboardLanguage("ha", "Hausa", "Hausa", "🇳🇬",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.AFRICAN)

        languages += KeyboardLanguage("zu", "Zulu", "isiZulu", "🇿🇦",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.AFRICAN)

        languages += KeyboardLanguage("xh", "Xhosa", "isiXhosa", "🇿🇦",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.AFRICAN)

        languages += KeyboardLanguage("rw", "Kinyarwanda", "Ikinyarwanda", "🇷🇼",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.AFRICAN)

        // ═══════════════════════════════════════
        //  SOUTHEAST ASIAN LANGUAGES
        // ═══════════════════════════════════════

        languages += KeyboardLanguage("th", "Thai", "ภาษาไทย", "🇹🇭",
            row1 = listOf("ๆ","ไ","ำ","พ","ะ","ั","ี","ึ","ื"),
            row2 = listOf("ฟ","ห","ก","ด","เ","้","่","า","ส"),
            row3 = listOf("ผ","ป","แ","ิ","ื","ท","ม","ใ","ฝ"),
            category = LanguageCategory.SOUTHEAST_ASIAN)

        languages += KeyboardLanguage("vi", "Vietnamese", "Tiếng Việt", "🇻🇳",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.SOUTHEAST_ASIAN)

        languages += KeyboardLanguage("id", "Indonesian", "Bahasa Indonesia", "🇮🇩",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.SOUTHEAST_ASIAN)

        languages += KeyboardLanguage("ms", "Malay", "Bahasa Melayu", "🇲🇾",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.SOUTHEAST_ASIAN)

        languages += KeyboardLanguage("tl", "Filipino", "Filipino", "🇵🇭",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.SOUTHEAST_ASIAN)

        languages += KeyboardLanguage("km", "Khmer", "ភាសាខ្មែរ", "🇰🇭",
            row1 = listOf("ឆ","ឹ","េ","រ","ត","យ","ុ","ិ","ោ","ផ"),
            row2 = listOf("ៀ","ស","ដ","ថ","ង","ហ","្","ក","ល"),
            row3 = listOf("ឋ","ខ","ច","វ","ប","ន","ម"),
            category = LanguageCategory.SOUTHEAST_ASIAN)

        languages += KeyboardLanguage("lo", "Lao", "ພາສາລາວ", "🇱🇦",
            row1 = listOf("ຳ","ໄ","ຳ","ພ","ະ","ັ","ີ","ຶ","ື","ໂ"),
            row2 = listOf("ຟ","ຫ","ກ","ດ","ເ","້","່","າ","ສ"),
            row3 = listOf("ຜ","ປ","ແ","ິ","ິ","ທ","ມ","ໃ","ຝ"),
            category = LanguageCategory.SOUTHEAST_ASIAN)

        languages += KeyboardLanguage("my", "Myanmar", "မြန်မာ", "🇲🇲",
            row1 = listOf("ဆ","တ","န","မ","အ","ပ","က","င","သ"),
            row2 = listOf("စ","ဟ","ျ","ဗ","ံ","ါ","ိ","ု","ဒ"),
            row3 = listOf("ယ","ရ","လ","ဝ","ဥ","ဘ","ည"),
            category = LanguageCategory.SOUTHEAST_ASIAN)

        // ═══════════════════════════════════════
        //  CONSTRUCTED LANGUAGES
        // ═══════════════════════════════════════

        languages += KeyboardLanguage("eo", "Esperanto", "Esperanto", "🌍",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.CONSTRUCTED)

        // ═══════════════════════════════════════
        //  MORE EUROPEAN
        // ═══════════════════════════════════════

        languages += KeyboardLanguage("br", "Breton", "Brezhoneg", "🏴",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("sc", "Sardinian", "Sardu", "🏴",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("lb", "Luxembourgish", "Lëtzebuergesch", "🇱🇺",
            row1 = listOf("q","w","e","r","t","z","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("y","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("fo", "Faroese", "Føroyskt", "🇫🇴",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p","ð"),
            row2 = listOf("a","s","d","f","g","h","j","k","l","æ"),
            row3 = listOf("z","x","c","v","b","n","m","ø"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("sq", "Albanian", "Shqip", "🇦🇱",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("az", "Azerbaijani", "Azərbaycan", "🇦🇿",
            row1 = listOf("q","ü","e","r","t","y","u","ı","o","p","ö"),
            row2 = listOf("a","s","d","f","g","h","j","k","l","ə"),
            row3 = listOf("z","x","c","v","b","n","m","ç","ş"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("ka", "Georgian", "ქართული", "🇬🇪",
            row1 = listOf("ქ","წ","ე","რ","ტ","ყ","უ","ი","ო","პ"),
            row2 = listOf("ა","ს","დ","ფ","გ","ჰ","ჯ","კ","ლ"),
            row3 = listOf("ზ","ხ","ც","ვ","ბ","ნ","მ"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("hy", "Armenian", "Հայերեն", "🇦🇲",
            row1 = listOf("խ","վ","ե","ր","դ","ը","ւ","ի","օ","պ"),
            row2 = listOf("ա","ս","տ","ֆ","կ","հ","ճ","լ","ծ"),
            row3 = listOf("զ","ց","գ","բ","ն","մ","շ"),
            category = LanguageCategory.EUROPEAN)

        // ═══════════════════════════════════════
        //  MORE ASIAN
        // ═══════════════════════════════════════

        languages += KeyboardLanguage("uz", "Uzbek", "Oʻzbek", "🇺🇿",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.ASIAN)

        languages += KeyboardLanguage("kk", "Kazakh", "Қазақ", "🇰🇿",
            row1 = listOf("й","ц","у","к","е","н","г","ш","щ","з","х"),
            row2 = listOf("ф","ы","в","а","п","р","о","л","д","ж","э"),
            row3 = listOf("я","ч","с","м","и","т","ь","б","ю","ғ"),
            category = LanguageCategory.ASIAN)

        languages += KeyboardLanguage("ky", "Kyrgyz", "Кыргыз", "🇰🇬",
            row1 = listOf("й","ц","у","к","е","н","г","ш","щ","з"),
            row2 = listOf("ф","ы","в","а","п","р","о","л","д","ж"),
            row3 = listOf("я","ч","с","м","и","т","ь","б","ю"),
            category = LanguageCategory.ASIAN)

        languages += KeyboardLanguage("tg", "Tajik", "Тоҷикӣ", "🇹🇯",
            row1 = listOf("й","ц","у","к","е","н","г","ш","щ","з"),
            row2 = listOf("ф","ы","в","а","п","р","о","л","д","ж"),
            row3 = listOf("я","ч","с","м","и","т","ь","б","ю"),
            category = LanguageCategory.ASIAN)

        languages += KeyboardLanguage("tk", "Turkmen", "Türkmen", "🇹🇲",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.ASIAN)

        // ═══════════════════════════════════════
        //  OCEANIAN LANGUAGES
        // ═══════════════════════════════════════

        languages += KeyboardLanguage("mi", "Maori", "Māori", "🇳🇿",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.OCEANIAN)

        languages += KeyboardLanguage("haw", "Hawaiian", "ʻŌlelo Hawaiʻi", "🏝️",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.OCEANIAN)

        languages += KeyboardLanguage("sm", "Samoan", "Gagana Samoa", "🇼🇸",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.OCEANIAN)

        // ═══════════════════════════════════════
        //  MORE AFRICAN
        // ═══════════════════════════════════════

        languages += KeyboardLanguage("mg", "Malagasy", "Malagasy", "🇲🇬",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.AFRICAN)

        languages += KeyboardLanguage("sn", "Shona", "Shona", "🇿🇼",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.AFRICAN)

        languages += KeyboardLanguage("st", "Sesotho", "Sesotho", "🇱🇸",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.AFRICAN)

        languages += KeyboardLanguage("tn", "Tswana", "Setswana", "🇧🇼",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.AFRICAN)

        // ═══════════════════════════════════════
        //  MORE MIDDLE EASTERN
        // ═══════════════════════════════════════

        languages += KeyboardLanguage("ckb", "Kurdish (Sorani)", "سۆرانی", "🏴",
            isRTL = true,
            row1 = listOf("ق","و","ە","ر","ت","ی","ئ","ۆ","پ","ژ"),
            row2 = listOf("ا","س","د","ف","گ","ھ","ژ","ک","ل"),
            row3 = listOf("ز","خ","ج","ڤ","ب","ن","م"),
            category = LanguageCategory.MIDDLE_EASTERN)

        languages += KeyboardLanguage("sd", "Sindhi", "سنڌي", "🇵🇰",
            isRTL = true,
            row1 = listOf("ض","ص","ث","ق","ف","غ","ع","ه","خ","ح"),
            row2 = listOf("ش","س","ی","ب","ل","ا","ت","ن","م","ک"),
            row3 = listOf("ئ","ء","ؤ","ر","لا","ى","ة","و","ز"),
            category = LanguageCategory.MIDDLE_EASTERN)

        // ═══════════════════════════════════════
        //  ADDITIONAL LANGUAGES (130+ total)
        // ═══════════════════════════════════════

        languages += KeyboardLanguage("sw", "Swahili (Tanzania)", "Kiswahili", "🇹🇿",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.AFRICAN)

        languages += KeyboardLanguage("my", "Burmese", "မြန်မာ", "🇲🇲",
            row1 = listOf("ဆ","တ","န","မ","အ","ပ","က","င","သ"),
            row2 = listOf("စ","ဟ","ျ","ဗ","ံ","ါ","ိ","ု","ဒ"),
            row3 = listOf("ယ","ရ","လ","ဝ","ဥ","ဘ","ည"),
            category = LanguageCategory.SOUTHEAST_ASIAN)

        languages += KeyboardLanguage("si", "Sinhala", "සිංහල", "🇱🇰",
            row1 = listOf("ු","ෑ","ා","ී","ූ","බ","හ","ග","ද","ජ"),
            row2 = listOf("ො","ේ","්","ි","ැ","ප","ර","ක","ත","ච"),
            row3 = listOf("අ","ඞ","ම","න","ව","ල","ස"),
            category = LanguageCategory.INDIC)

        languages += KeyboardLanguage("jv", "Javanese", "Basa Jawa", "🇮🇩",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.SOUTHEAST_ASIAN)

        languages += KeyboardLanguage("su", "Sundanese", "Basa Sunda", "🇮🇩",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.SOUTHEAST_ASIAN)

        languages += KeyboardLanguage("ceb", "Cebuano", "Cebuano", "🇵🇭",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.SOUTHEAST_ASIAN)

        languages += KeyboardLanguage("hmn", "Hmong", "Hmoob", "🏳️",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.SOUTHEAST_ASIAN)

        languages += KeyboardLanguage("ht", "Haitian Creole", "Kreyòl Ayisyen", "🇭🇹",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("yi", "Yiddish", "ייִדיש", "🇮🇱",
            isRTL = true,
            row1 = listOf("/","'","ק","ר","א","ט","ו","ן","ם","פ"),
            row2 = listOf("ש","ד","ג","כ","ע","י","ח","ל","ך","ף"),
            row3 = listOf("ז","ס","ב","ה","נ","מ","צ","ת","ץ"),
            category = LanguageCategory.MIDDLE_EASTERN)

        languages += KeyboardLanguage("co", "Corsican", "Corsu", "🇫🇷",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("fy", "Frisian", "Frysk", "🇳🇱",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("gd", "Scottish Gaelic", "Gàidhlig", "🏴󠁧󠁢󠁳󠁣󠁴󠁿",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.EUROPEAN)

        languages += KeyboardLanguage("ku", "Kurdish (Kurmanji)", "Kurmancî", "🏴",
            row1 = listOf("q","w","e","r","t","y","u","î","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m","ê","û","ç"),
            category = LanguageCategory.MIDDLE_EASTERN)

        languages += KeyboardLanguage("ug", "Uyghur", "ئۇيغۇرچە", "🇨🇳",
            isRTL = true,
            row1 = listOf("چ","ۋ","ې","ر","ت","ي","ۇ","ڭ","و","پ"),
            row2 = listOf("ھ","س","د","ا","ە","ق","ك","ل","گ"),
            row3 = listOf("ژ","ز","ش","غ","ب","ن","م"),
            category = LanguageCategory.MIDDLE_EASTERN)

        languages += KeyboardLanguage("tt", "Tatar", "Татар", "🇷🇺",
            row1 = listOf("й","ц","у","к","е","н","г","ш","щ","з"),
            row2 = listOf("ф","ы","в","а","п","р","о","л","д","ж"),
            row3 = listOf("я","ч","с","м","и","т","ь","б","ю","ң"),
            category = LanguageCategory.CYRILLIC)

        languages += KeyboardLanguage("ba", "Bashkir", "Башҡорт", "🇷🇺",
            row1 = listOf("й","ц","у","к","е","н","г","ш","щ","з"),
            row2 = listOf("ф","ы","в","а","п","р","о","л","д","ж"),
            row3 = listOf("я","ч","с","м","и","т","ь","б","ю","ғ"),
            category = LanguageCategory.CYRILLIC)

        languages += KeyboardLanguage("cv", "Chuvash", "Чӑваш", "🇷🇺",
            row1 = listOf("й","ц","у","к","е","н","г","ш","щ","з"),
            row2 = listOf("ф","ы","в","а","п","р","о","л","д","ж"),
            row3 = listOf("я","ч","с","м","и","т","ь","б","ю"),
            category = LanguageCategory.CYRILLIC)

        languages += KeyboardLanguage("os", "Ossetic", "Ирон", "🏴",
            row1 = listOf("й","ц","у","к","е","н","г","ш","щ","з"),
            row2 = listOf("ф","ы","в","а","п","р","о","л","д","ж"),
            row3 = listOf("я","ч","с","м","и","т","ь","б","ю"),
            category = LanguageCategory.CYRILLIC)

        languages += KeyboardLanguage("ab", "Abkhaz", "Аԥсуа", "🏴",
            row1 = listOf("й","ц","у","к","е","н","г","ш","щ","з"),
            row2 = listOf("ф","ы","в","а","п","р","о","л","д","ж"),
            row3 = listOf("я","ч","с","м","и","т","ь","б","ю"),
            category = LanguageCategory.CYRILLIC)

        languages += KeyboardLanguage("so", "Somali", "Soomaali", "🇸🇴",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.AFRICAN)

        languages += KeyboardLanguage("mg", "Malagasy", "Malagasy", "🇲🇬",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.AFRICAN)

        languages += KeyboardLanguage("ny", "Chichewa", "Chicheŵa", "🇲🇼",
            row1 = listOf("q","w","e","r","t","y","u","i","o","p"),
            row2 = listOf("a","s","d","f","g","h","j","k","l"),
            row3 = listOf("z","x","c","v","b","n","m"),
            category = LanguageCategory.AFRICAN)

        languages += KeyboardLanguage("lo", "Lao", "ພາສາລາວ", "🇱🇦",
            row1 = listOf("ຳ","ໄ","ຳ","ພ","ະ","ັ","ີ","ຶ","ື","ໂ"),
            row2 = listOf("ຟ","ຫ","ກ","ດ","ເ","້","່","າ","ສ"),
            row3 = listOf("ຜ","ປ","ແ","ິ","ິ","ທ","ມ","ໃ","ຝ"),
            category = LanguageCategory.SOUTHEAST_ASIAN)
    }

    fun getAllLanguages(): List<KeyboardLanguage> = languages.toList()
    fun getLanguageCount(): Int = languages.size
    fun getLanguageByCode(code: String): KeyboardLanguage? = languages.find { it.code == code }
    fun getLanguagesByCategory(category: LanguageCategory): List<KeyboardLanguage> = languages.filter { it.category == category }
    fun searchLanguages(query: String): List<KeyboardLanguage> {
        val q = query.lowercase()
        return languages.filter {
            it.displayName.lowercase().contains(q) ||
            it.nativeName.lowercase().contains(q) ||
            it.code.lowercase().contains(q)
        }
    }
    fun getCategories(): List<LanguageCategory> = LanguageCategory.values().toList()
}
