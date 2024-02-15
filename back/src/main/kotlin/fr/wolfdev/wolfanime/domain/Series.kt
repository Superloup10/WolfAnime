package fr.wolfdev.wolfanime.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val source: String,
    @SerialName("image_type") val type: String,
    val height: UInt,
    val width: UInt
)

@Serializable
data class PosterImages(
    @SerialName("poster_tall") val posterTall: Array<Image>,
    @SerialName("poster_wide") val posterWide: Array<Image>
)

@Serializable
enum class Locale(val value: String) {
    @SerialName("ar-ME")
    AR_ME("ar-ME"),

    @SerialName("ar-SA")
    AR_SA("ar-SA"),

    @SerialName("de-DE")
    DE_DE("de-DE"),

    @SerialName("en-IN")
    EN_IN("en-IN"),

    @SerialName("en-US")
    EN_US("en-US"),

    @SerialName("es-419")
    ES_419("es-419"),

    @SerialName("es-ES")
    ES_ES("es-ES"),

    @SerialName("es-LA")
    ES_LA("es-LA"),

    @SerialName("fr-FR")
    FR_FR("fr-FR"),

    @SerialName("hi-IN")
    HI_IN("hi-IN"),

    @SerialName("it-IT")
    IT_IT("it-IT"),

    @SerialName("ja-JP")
    JA_JP("ja-JP"),

    @SerialName("pt-BR")
    PT_BR("pt-BR"),

    @SerialName("pt-PT")
    PT_PT("pt-PT"),

    @SerialName("ru-RU")
    RU_RU("ru-RU"),

    @SerialName("ta-IN")
    TA_IN("ta-IN"),

    @SerialName("zh-CN")
    ZH_CN("zh-CN")
}

@Serializable
enum class Category(val value: String) {
    Action("action"),
    Adventure("adventure"),
    Comedy("comedy"),
    Drama("drama"),
    Fantasy("fantasy"),
    Music("music"),
    Romance("romance"),
    SciFi("sci-fi"),
    Seinen("seinen"),
    Shojo("shojo"),
    Shonen("shonen"),
    SliceOfLife("slice-of-life"),
    Sports("sports"),
    Supernatural("supernatural"),
    Thriller("thriller"),

    // the following categories are sub-categories, they're not listed when calling
    // `Crunchyroll::categories`
    Harem("harem"),
    Historical("historical"),
    Idols("idols"),
    Isekai("isekai"),
    Mecha("mecha"),
    Mystery("mystery"),
    PostApocalyptic("post-apocalyptic")
}

@Serializable
data class Series(
    val id: String,
    @SerialName("channel_id") val channelId: String,
    @SerialName("content_provider") val contentProvider: String? = null,
    val slug: String,
    val title: String,
    @SerialName("slug_title") val slugTitle: String,
    val description: String,
    @SerialName("extended_description") val extendedDescription: String,
    @SerialName("series_launch_year") val seriesLaunchYear: UInt?,
    @SerialName("episode_count") val episodeCount: UInt,
    @SerialName("season_count") val seasonCount: UInt,
    @SerialName("media_count") val mediaCount: UInt,
    @SerialName("season_tags") val seasonTags: Array<String>,
    @SerialName("is_subbed") val isSubbed: Boolean,
    @SerialName("is_dubbed") val isDubbed: Boolean,
    @SerialName("is_simulcast") val isSimulcast: Boolean,
    @SerialName("audio_locales") val audioLocales: Array<Locale>,
    @SerialName("subtitle_locales") val subtitleLocales: Array<Locale>,
    val images: PosterImages,
    @SerialName("tenant_categories") val categories: Array<Category>,
    val keywords: Array<String>,
    @SerialName("maturity_ratings") val maturityRatings: Array<String>,
    @SerialName("is_mature") val isMature: Boolean,
    @SerialName("mature_blocked") val matureBlocked: Boolean,
    @SerialName("availability_notes") val availabilityNotes: String
)
