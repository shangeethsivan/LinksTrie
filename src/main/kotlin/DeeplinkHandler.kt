interface DeeplinkHandler {
    fun getPageData(url: String, pathValues: Map<String, String>): String
}

class PostsLinkHandler : DeeplinkHandler {
    override fun getPageData(url: String, pathValues: Map<String, String>): String {
        return "Obj -1 -> $url, pathValues - $pathValues"
    }

}

class PostSharesLinkHandler : DeeplinkHandler {
    override fun getPageData(url: String, pathValues: Map<String, String>): String {
        return "Obj 0-> $url, pathValues - $pathValues"
    }
}

class RandomDeeplinkHandler : DeeplinkHandler {
    override fun getPageData(url: String, pathValues: Map<String, String>): String {
        return "Obj 1 uri-> $url, pathValues - $pathValues"
    }
}

class RandomDeeplinkHandler2 : DeeplinkHandler {
    override fun getPageData(url: String, pathValues: Map<String, String>): String {
        return "Obj 2 uri-> $url, pathValues - $pathValues"
    }
}

class RandomDeeplinkHandler3 : DeeplinkHandler {
    override fun getPageData(url: String, pathValues: Map<String, String>): String {
        return "Obj 3 uri-> $url, pathValues - $pathValues"
    }
}

class RandomDeeplinkHandler4 : DeeplinkHandler {
    override fun getPageData(url: String, pathValues: Map<String, String>): String {
        return "Obj 4 uri-> $url, pathValues - $pathValues"
    }
}