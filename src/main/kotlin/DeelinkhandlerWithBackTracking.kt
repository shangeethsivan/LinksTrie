fun main() {

    val deeplinkTrie = DeeplinkTrie()
    println("Test case 1 : result = " + deeplinkTrie.getDeeplinkResult("books/2342/test"))
    println("Test case 2 : result = " + deeplinkTrie.getDeeplinkResult("books/test/book"))

    println("Test case 3 : result = " + deeplinkTrie.getDeeplinkResult("books/harrypotter/partName/gobletofFire/page/2"))

    println("Test case 4 : result = " + deeplinkTrie.getDeeplinkResult("books/ponniyinselvan/partName/paagamondru/2"))
}

class DeeplinkTrie {


    private val urlToHandlerMap = mapOf(
        "test/{pathValue}/posts" to PostsLinkHandler(),
        "test/{test}" to PostsLinkHandler(),
        "test/{pathValue}/shares" to PostSharesLinkHandler(),
        "test1/{pathValue}/test2/test3/test4/demo/{pathValue}/shangeeth" to RandomDeeplinkHandler(),
        "books/{bookId}/test" to RandomDeeplinkHandler2(),
        "books/test/book" to RandomDeeplinkHandler3(),
        "books/{bookId}/partName/{partName}/page/{pageId}" to RandomDeeplinkHandler4(),
        "books/ponniyinselvan/partName/paagamondru/{pageId}" to RandomDeeplinkHandler4(),
    )

    private val rootNode: DeeplinkTrieNode = DeeplinkTrieNode(
        key = "root",
        children = mutableMapOf(),
        handler = null,
    )

    class DeeplinkTrieInitiationException(override val message: String) : Exception(message)

    init {
        val urls = urlToHandlerMap.keys
        urls.forEach { url ->
            val pathSegments = url.split("/")
            createAndInsertNodes(
                paths = pathSegments,
                currentIndex = 0,
                completePath = url,
                currentNode = rootNode,
            )
        }
    }

    fun getDeeplinkResult(input: String): String? {

        val inputPaths = input.split("/")

        val pathValuesMap = mutableMapOf<String, String>()

        return findHandler(inputPaths, rootNode, pathValuesMap)?.getPageData(pathValuesMap)
    }

    private fun createAndInsertNodes(
        paths: List<String>,
        currentIndex: Int,
        completePath: String,
        currentNode: DeeplinkTrieNode,
    ) {
        val currentPath = paths[currentIndex]
        val isTerminalPath = paths.lastIndex == currentIndex
        val dynamicResult = currentPath.getDynamicResult()
        val key = if (dynamicResult != null) {
            DYNAMIC_VALUE
        } else {
            currentPath
        }

        val currentPathNode = if (isTerminalPath) {
            val handler = requireNotNull(urlToHandlerMap[completePath])
            val existingCurrentPathNode = currentNode.children[key]

            val updatedNode = if (existingCurrentPathNode != null) {
                if (existingCurrentPathNode.handler == null) {
                    existingCurrentPathNode.copy(handler = handler)
                } else {
                    throw DeeplinkTrieInitiationException(
                        "Same path cannot have two different Handlers for Path : $completePath, " +
                                "currentHandler: ${existingCurrentPathNode.handler.javaClass.simpleName} " +
                                "new handler: ${handler.javaClass.simpleName}",
                    )
                }
            } else {
                DeeplinkTrieNode(
                    key = dynamicResult?.dynamicKey ?: currentPath,
                    children = mutableMapOf(),
                    handler = handler,
                )
            }
            currentNode.children[key] = updatedNode
            updatedNode
        } else {
            currentNode.children.computeIfAbsent(key) {
                DeeplinkTrieNode(
                    key = dynamicResult?.dynamicKey ?: currentPath,
                    children = mutableMapOf(),
                    handler = null,
                )
            }
        }

        if (!isTerminalPath) {
            createAndInsertNodes(paths, currentIndex + 1, completePath, currentPathNode)
        }
    }

    private fun String.getDynamicResult(): DynamicResult? {
        return Regex("\\{(\\w+)\\}").matchEntire(this)?.let {
            DynamicResult(it.groupValues[1])
        }
    }

    private fun findHandler(
        inputPaths: List<String>,
        rootNode: DeeplinkTrieNode,
        pathValuesMap: MutableMap<String, String>,
    ): DeeplinkHandler? {
        val positionToDynamicNodesMap = sortedMapOf<Int, DeeplinkTrieNode>()

        fun findHandlerRecursively(
            paths: List<String>,
            currentIndex: Int,
            currentNode: DeeplinkTrieNode,
            pathValues: MutableMap<String, String>,
        ): DeeplinkHandler? {

            return if (currentIndex <= paths.lastIndex) {
//                println("find handler - $paths index -  $currentIndex - currentNode - ${currentNode.key}, pathValues $pathValues")
                val currentPath = paths[currentIndex]
                var nodeFound = currentNode.children[currentPath]

                if (nodeFound == null) {
                    nodeFound = currentNode.children[DYNAMIC_VALUE]
                    if (nodeFound != null) {
                        // if dynamic node found
                        pathValues[nodeFound.key] = currentPath
                    }
                } else {
                    val dynamicNodeFound = currentNode.children[DYNAMIC_VALUE]
                    // adding position and dynamic nodemap
                    if (dynamicNodeFound != null) {
                        positionToDynamicNodesMap[currentIndex + 1] = dynamicNodeFound
                    }
                }

                nodeFound?.let {
                    if (currentIndex == paths.lastIndex) {
                        it.handler
                    } else {
                        findHandlerRecursively(paths, currentIndex + 1, it, pathValues)
                    }
                }
            } else {
                null
            }
        }

        var handler = findHandlerRecursively(inputPaths, 0, rootNode, pathValuesMap)

        if (handler == null) {
            positionToDynamicNodesMap.keys.reversed().forEach { position ->
                val dynamicNode = requireNotNull(positionToDynamicNodesMap[position])
                findHandlerRecursively(inputPaths, position, dynamicNode, pathValuesMap)?.let {
                    handler = it
                    return@forEach
                }
            }
        }

        return handler
    }


    private data class DeeplinkTrieNode(
        val key: String,
        val children: MutableMap<String, DeeplinkTrieNode>,
        val handler: DeeplinkHandler?,
    )


    private class DynamicResult(
        val dynamicKey: String,
    )


    companion object {
        const val DYNAMIC_VALUE = "{pathValue}"
    }
}
