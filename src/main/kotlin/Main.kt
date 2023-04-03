class LinksTrie {

    private class LinksTrieNode(
        val key: String,
        val map: MutableMap<String, LinksTrieNode>,
        var handler: DeeplinkHandler?,
    )

    private val rootNode: LinksTrieNode = LinksTrieNode("root", mutableMapOf(), null)

    init {

        val urlToHandlerMap = mapOf(
            "test/{pathValue}/posts" to PostsLinkHandler(),
            "test/{pathValue}" to PostsLinkHandler(),
            "test/{pathValue}/shares" to PostSharesLinkHandler(),
            "test/{pathValue}" to PostsLinkHandler(),
            "test1/{pathValue}/test2/test3/test4/demo/{pathValue}/shangeeth" to RandomDeeplinkHandler(),
            "test1" to RandomDeeplinkHandler2(),
        )

        var currentNode = rootNode

        val urls = urlToHandlerMap.keys
        urls.forEach { url ->
            val pathSegments = url.split("/")

            pathSegments.forEach { pathSegment ->
                currentNode = currentNode.map.computeIfAbsent(pathSegment) {
                    LinksTrieNode(
                        key = pathSegment,
                        handler = null,
                        map = mutableMapOf()
                    )
                }
            }

            urlToHandlerMap[url]?.let { newHandler ->
                currentNode.handler = currentNode.handler.let {
                    if (it == null) {
                        newHandler
                    } else {
                        error("Error: Two handlers for the same path encountered: newHandler : $newHandler currentHandler:${currentNode.handler} $url")
                    }
                }
            }

            currentNode = rootNode
        }
    }

    fun getDeeplinkHandler(url: String): DeeplinkHandler? {

        val inputPaths = url.split("/")

        var currentTrie: LinksTrieNode? = rootNode

        inputPaths.forEach { path ->
            val trie = currentTrie ?: return@forEach
            currentTrie = trie.map[path] ?: trie.map[DYNAMIC_VALUE]
        }

        return currentTrie?.handler
    }
/*
    fun printTries() {
        println(printDeeplinkTries(rootNode))
    }

    private fun printDeeplinkTries(linksTrieNode: LinksTrieNode): String {
        val children = StringBuilder()
        linksTrieNode.map.values.forEachIndexed { index, value ->
            children.append("key: ${linksTrieNode.key} index: $index ${printDeeplinkTries(value)}")
        }
        return "Trie Key: ${linksTrieNode.key} " +
                "Handler: ${linksTrieNode.handler?.javaClass?.simpleName} \n" +
                "$children"
    }*/

    companion object {
        const val DYNAMIC_VALUE = "{pathValue}"
    }
}


fun main(args: Array<String>) {
    val linksTrie = LinksTrie()

    println("Test 1 : Handler Found = ${linksTrie.getDeeplinkHandler("test/test/posts")?.javaClass?.simpleName}")

    println(
        "Test 2: Handler Found = ${
            linksTrie.getDeeplinkHandler(
                "test1/12312123/test2/test3/test4/demo/1231sdfs/shangeeth"
            )?.javaClass?.simpleName
        }"
    )

    println(
        "Test 2: Handler Not Found = ${
            linksTrie.getDeeplinkHandler(
                "test1/12312123/test2/test3/test4/demo/1231sdfs/shaneeth"
            )?.javaClass?.simpleName
        }"
    )

    println(
        "Test 2: Handler Not Found = ${
            linksTrie.getDeeplinkHandler(
                "test1"
            )?.javaClass?.simpleName
        }"
    )

    println(
        "Test 2: Handler Found = ${
            linksTrie.getDeeplinkHandler(
                "test/test"
            )?.javaClass?.simpleName
        }"
    )
}