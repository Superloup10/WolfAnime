package fr.wolfdev.wolfanime

object CrunchyrollNative {
    init {
        System.loadLibrary("rust_lib")
    }

    external fun getCrunchyrollDataBySeriesId(seriesId: String): String
}
