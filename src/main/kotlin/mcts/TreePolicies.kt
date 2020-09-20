package mcts

import kotlin.math.log2
import kotlin.math.sqrt

object TreePolicies {

    fun uct(uctConst: Float, visits: Int, edgeTraversals: Int): Float {
        val logVisits: Float = if (visits != 0) log2(visits.toFloat()) else 0f
        return uctConst * sqrt(logVisits / (1 + edgeTraversals))
    }

    fun uctTreePolicy(uctConst: Float = 1f): TreePolicy = { node ->
        val children = node.children
        val exploreChild = children
                .map { child ->
                    val edge = child.edge
                    val exploreVal = edge.qValue + uct(uctConst, node.nodeData.visits, edge.traversals)
                    Pair(child, exploreVal)
                }
                .maxBy { it.second }
                    ?.first
                    ?: throw IllegalStateException("No children present in uctTreePolicy")
        exploreChild
    }

}