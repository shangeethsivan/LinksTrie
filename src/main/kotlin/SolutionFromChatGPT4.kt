class TrieNode(
    val parent: TrieNode?,
    val children: MutableMap<String, TrieNode> = mutableMapOf(),
    var handler: ((Map<String, String>) -> Unit)? = null
)

class Router {
    private val root = TrieNode(null)

    fun addRoute(path: String, handler: (Map<String, String>) -> Unit) {
        var curr = root
        for (segment in path.split('/')) {
            if (segment.isBlank()) continue
            curr = curr.children.getOrPut(segment) { TrieNode(curr) }
        }
        curr.handler = handler
    }

    fun search(path: String): Pair<((Map<String, String>) -> Unit)?, Map<String, String>?>? {
        var curr = root
        val params = mutableMapOf<String, String>()
        var matchedHandler: ((Map<String, String>) -> Unit)? = null
        var matchedParams = emptyMap<String, String>()
        for (segment in path.split('/')) {
            if (segment.isBlank()) continue
            var found = false
            for ((name, child) in curr.children) {
                if (name == segment) {
                    curr = child
                    found = true
                    break
                }
                if (name.startsWith("{") && name.endsWith("}")) {
                    params[name.substring(1, name.length - 1)] = segment
                    curr = child
                    found = true
                    if (child.handler != null) {
                        matchedHandler = child.handler
                        matchedParams = params.toMap()
                    }
                    break
                }
            }
            if (!found) {
                return null
            }
            curr.handler?.let {
                matchedHandler = it
                matchedParams = params.toMap()
            }
        }
        if (matchedHandler != null && curr.handler == null) {
            // Found a handler, but not at the end of the path
            return null
        }
        if (curr.handler != null) {
            // Found a handler at the end of the path
            return curr.handler to params.toMap()
        }
        // Traverse up to find the most specific handler
        var node = curr
        var handler: ((Map<String, String>) -> Unit)? = null
        var nodeParams = emptyMap<String, String>()
        while (node.parent != null) {
            node.parent?.let {
                node = it
                if (node.handler != null) {
                    handler = node.handler
                    nodeParams = params.toMap()
                }
            }
        }
        if (handler == null) {
            return null
        }
        return handler to nodeParams
    }

    fun printTrie() {
        printTrie(root, "")
    }

    private fun printTrie(node: TrieNode, indent: String) {
        if (node.handler != null) {
            println("$indent${node.handler}")
        }
        for ((name, child) in node.children) {
            println("$indent$name")
            printTrie(child, "$indent  ")
        }
    }
}
fun main() {
    val trie = Router()
//    trie.insert("/", ::homeHandler)
    trie.addRoute("/about", ::homeHandler)
    trie.addRoute("/blog", ::homeHandler)
    trie.addRoute("/blog/:slug", ::blogWithSlug)
    trie.addRoute("/blog/test", ::blogWithSlug2)
    trie.addRoute("/blog/:slug/test", ::blogWithSlug)
    trie.addRoute("/blog/test/demo", ::blogWithSlug2)

// Route URLs
    val pathToSearch = "blog/12312"

    trie.search(pathToSearch)?.let { (handler, test) ->
        test?.let {
            handler?.invoke(test) ?: handle404Error(pathToSearch)
        }
    }

//    trie.printTrie()
}

fun homeHandler(map: Map<String, String>) {
    println("Handler invoked $map")
}

fun blogWithSlug(map: Map<String, String>) {
    println("Handler with slug invoked $map")
}

fun blogWithSlug2(map: Map<String, String>) {
    println("Handler with slug2 invoked $map")
}

fun handle404Error(pathToSearch: String) {
    println("Path not found $pathToSearch")
}