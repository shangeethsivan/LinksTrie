interface DeeplinkHandler {
    fun getPageData(): String
}

class PostsLinkHandler: DeeplinkHandler{
    override fun getPageData() : String{
        return "Closet"
    }

}

class PostSharesLinkHandler: DeeplinkHandler{
    override fun getPageData() : String{
        return "Closet Shares"
    }
}
class RandomDeeplinkHandler: DeeplinkHandler{
    override fun getPageData() : String{
        return "Closet Shares"
    }
}

class RandomDeeplinkHandler2: DeeplinkHandler{
    override fun getPageData() : String{
        return "Closet Shares"
    }
}