package mcts.tree

import java.util.*
import kotlin.system.measureTimeMillis
import kotlin.test.Asserter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.math.pow


fun <T> timeIt(func: () -> T): Pair<T, Float> {
    val startTime = System.nanoTime()
    val value = func()
    val timeTakenMs = (System.nanoTime() - startTime) * 0.000001
    return Pair(value, timeTakenMs.toFloat())
}


class McTreeTest {

    fun constructTree(childrenCount: Int, depth: Int): Tree {
        val maxNodeCount = (1..depth).map{childrenCount.toFloat().pow(it).toInt() }.sum() + 1
        println("max node count ${maxNodeCount}")
        val tree = Tree(
                maxNodeCount = maxNodeCount,
                maxChildCount = childrenCount + 1
        )

        val rootNode = tree.rootNodeId
        var prevNodes = listOf(rootNode)
        (0 until depth).forEach { _ ->
            prevNodes = prevNodes.flatMap { nodeId ->
                (0 until childrenCount).map { tree.createNode(nodeId) }
            }
        }
        return tree
    }

    fun traverseTree(tree: Tree): Int {
        val root = tree.rootNodeId
        val nodeStack = Stack<NodeId>()
        nodeStack.push(root)
        var traversedNodes = 0
        while (nodeStack.isNotEmpty()) {
            val currRoot = nodeStack.pop()
            traversedNodes += 1
            tree.setNodeValue(currRoot, 10f)

            tree.getChildrenAsSequence(currRoot).forEach { child ->
                nodeStack.push(child)
            }
        }
        return traversedNodes
    }

    fun constructTree2(childrenCount: Int, depth: Int): McNode {
        val root = McNode(null, McNodeData())

        var prevNodes = listOf(root)
        (0 until depth).forEach { _ ->
            prevNodes = prevNodes.flatMap { node ->
                (0 until childrenCount).map { McNode(node, McNodeData()) }
            }
        }
        return root
    }

    fun traverseTree2(root: McNode): Int {
        val nodeStack = Stack<McNode>()
        nodeStack.push(root)
        var traversedNodes = 0
        while (nodeStack.isNotEmpty()) {
            val currRoot = nodeStack.pop()
            traversedNodes += 1
//            tree.setNodeValue(currRoot, 10f)

            for (child in currRoot.children) {
                nodeStack.push(child)
            }
        }
        return traversedNodes
    }

    @Test
    fun testTree() {
        val startConstructionTime = System.currentTimeMillis()
        val tree = constructTree(
                childrenCount = 100,
                depth = 3
        )
        val treeConstructionTime = System.currentTimeMillis() - startConstructionTime

        val startConstructionTime2 = System.currentTimeMillis()
        val tree2 = constructTree2(
                childrenCount = 100,
                depth = 3
        )
        val treeConstructionTime2 = System.currentTimeMillis() - startConstructionTime2

        println("Tree construction with ${tree.size} nodes time: $treeConstructionTime ms")
        println("Tree object based construction with ${tree2.allChildrenCount()} nodes time: $treeConstructionTime2 ms")

        val startTraversalTime = System.nanoTime()
        var nodesTraversed = traverseTree(tree)
        val traversalTime = (System.nanoTime() - startTraversalTime) * 0.000001

        val traversal2 = timeIt {
            traverseTree2(tree2)
        }

        println("Traversed $nodesTraversed nodes in $traversalTime ms")
        println("Traversed2 ${traversal2.first} nodes in ${traversal2.second} ms")

    }

    @Test
    fun testRootChildren() {
        val tree = Tree(
                maxNodeCount = 100,
                maxChildCount = 10
        )

        val child1 = tree.createNode(tree.rootNodeId)
        val child2 = tree.createNode(tree.rootNodeId)
        val child21 = tree.createNode(child2)
        val child3 = tree.createNode(tree.rootNodeId)

        println(tree)

        assertEquals(tree.rootNodeId, tree.getParent(child1))
        assertEquals(tree.rootNodeId, tree.getParent(child2))
        assertEquals(tree.rootNodeId, tree.getParent(child3))
        assertEquals(listOf(child1, child2, child3), tree.getChildren(tree.rootNodeId).toList())

        assertEquals(child2, tree.getParent(child21))
        assertEquals(listOf(child21), tree.getChildren(child2).toList())
    }
}