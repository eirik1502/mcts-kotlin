package mcts.tree

import org.w3c.dom.Node
import java.util.*


typealias NodeId = Int

class Tree(
        val maxNodeCount: Int,
        val maxChildCount: Int
) {
    private val nodesIndex: IntArray = IntArray(maxNodeCount) { i -> i }
    private val nodesParentId: Array<NodeId> = Array(maxNodeCount) { -1 }
    private val nodesChildrensId: Array<Array<NodeId>> = Array(maxNodeCount) { Array(maxChildCount){ -1 } }
    private val nodesNextChildIndex: IntArray = IntArray(maxNodeCount)
    private val nodesValue: FloatArray = FloatArray(maxNodeCount) { -1f }

    val rootNodeId: NodeId = createRootNode()

    private var nextNodeId: Int = 0
    private fun popNodeId() = nextNodeId++

    val size: Int
        get() = nextNodeId

    private fun createRootNode(): NodeId {
        val rootNodeId = popNodeId()
        return rootNodeId
    }

    fun createNode(parentId: NodeId): NodeId {
        val nodeId = popNodeId()
        setNodeParent(nodeId, parentId)
        addNodeChild(parentId, nodeId)
        return nodeId
    }

    fun getChildrenAsSequence(nodeId: NodeId): Sequence<NodeId> {
        val nodeChildrenId = nodesChildrensId[nodeId]
        return nodeChildrenId.asSequence().take(getNextChildIndex(nodeId))
    }

    fun getChildren(nodeId: NodeId): Array<NodeId> {
        val nodeChildrenId = nodesChildrensId[nodeId]
        return nodeChildrenId.sliceArray(IntRange(0, getNextChildIndex(nodeId)+1))
    }

    fun getParent(nodeId: NodeId) = nodesParentId[nodeId]

    fun setNodeValue(nodeId: NodeId, value: Float) {
        nodesValue[nodeId] = value
    }

    fun getNodeValue(nodeId: NodeId): Float = nodesValue[nodeId]

    private fun getNextChildIndex(nodeId: NodeId) = nodesNextChildIndex[nodeId]

    private fun setNodeParent(nodeId: NodeId, parentId: NodeId) {
        nodesParentId[nodeId] = parentId
    }

    private fun addNodeChild(nodeId: NodeId, childId: NodeId) {
        nodesChildrensId[nodeId][getNextChildIndex(nodeId)] = childId
        ++nodesNextChildIndex[nodeId]
    }

    fun nodeToString(nodeId: NodeId): String {
        return "(Node childrenCount: ${getChildren(nodeId).size})"
    }

    override fun toString(): String {
        fun nodeString(nodeId: NodeId, indent: Int): String {
            var nodeStr = ""
            nodeStr += "\t".repeat(indent) + nodeToString(nodeId)
            for (child in getChildren(nodeId)) {
                nodeStr += "\n" + nodeString(child, indent + 1)
            }
            return nodeStr
        }

        return nodeString(rootNodeId, 0)
    }
}