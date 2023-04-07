interface DeeplinkHandler {
    fun getPageData(pathValues: Map<String, String>): String
}

class PostsLinkHandler : DeeplinkHandler {
    override fun getPageData(pathValues: Map<String, String>): String {
        return "Closet $pathValues"
    }

}

class PostSharesLinkHandler : DeeplinkHandler {
    override fun getPageData(pathValues: Map<String, String>): String {
        return "Closet Shares $pathValues"
    }
}

class RandomDeeplinkHandler : DeeplinkHandler {
    override fun getPageData(pathValues: Map<String, String>): String {
        return "Closet Shares $pathValues"
    }
}

class RandomDeeplinkHandler2 : DeeplinkHandler {
    override fun getPageData(pathValues: Map<String, String>): String {
        return "Random Shares2 $pathValues"
    }
}

class RandomDeeplinkHandler3 : DeeplinkHandler {
    override fun getPageData(pathValues: Map<String, String>): String {
        return "Random Shares3 $pathValues"
    }
}

class RandomDeeplinkHandler4 : DeeplinkHandler {
    override fun getPageData(pathValues: Map<String, String>): String {
        return "Random Shares4 $pathValues"
    }
}