package fr.wolfdev.wolfanime.utils

val CRUNCHYROLL_URL_REGEX = """^https?://(?:beta\.|www\.)?crunchyroll\.com/(?:[a-zA-Z]{2}(-[a-zA-Z])?/)?(?:series|movie_listing)/(?<id>[a-zA-Z0-9]+)/?.*$""".toRegex()
