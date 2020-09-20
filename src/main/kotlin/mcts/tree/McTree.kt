package mcts.tree

import mcts.Action
import mcts.State

val NULL_STATE: String = "null"

data class McEdge(
    var qValue: Float,
    var traversals: Int,
    var eval: Float
)

data class McNodeData(
        val state: State? = null,
        val action: Action? = null,
        val nextPlayer: Int = -1,
        var visits: Int = 0
)

class McNode internal constructor(
        val parent: McNode?,
        val nodeData:McNodeData,
        val edge: McEdge = McEdge(
                qValue = 0f,
                traversals = 0,
                eval = 0f
        ),
        val children: MutableList<McNode> = mutableListOf()
) {

    init {
        parent?.addChild(this)
    }


    fun addChild(child: McNode) {
        children.add(child)
    }

    fun addChildren(children: Collection<McNode>) {
        this.children.addAll(children)
    }

    fun allChildrenCount(): Int {
        return children.map { it.allChildrenCount() }.sum() + 1
    }

}

object McNodeFuncs {
    fun printTree(
            root: McNode,
            depth: Int = 0,
            maxDepth: Int = -1,
            includeUnvisitedNodes: Boolean = false
    ) {
        print("\t".repeat(depth))
        print("${root.edge}  ")
        println(root.nodeData)
        if (depth != maxDepth) {
            root.children.forEach {
                if (includeUnvisitedNodes || it.edge.traversals != 0) {
                    printTree(it, depth + 1, maxDepth, includeUnvisitedNodes)
                }
            }
        }

    }
}