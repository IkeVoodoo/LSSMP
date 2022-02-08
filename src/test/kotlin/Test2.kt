import java.util.*

class Test2 {

    private val random = Random()

    fun Double.convertHearts(): Double {
        return this
    }

    fun String.convertHearts(): Double {
        return this.toDouble().convertHearts()
    }

    fun Double.random(max: Double): Double {
        return (Math.random() * (max - this + 1) + this)
    }

    fun Double.randomHearts(max: Double): Double {
        val possible = (max - this)
        val v = random.nextInt(possible.toInt() * 2 + 1)
        val possibleArray = ArrayList<Double>()
        for (i in 0..possible.toInt()) {
            possibleArray.add(i.toDouble())
        }
        for (i in 0 until possible.toInt()) {
            possibleArray.add(i.toDouble() + .5)
        }
        possibleArray.sort()
        println(possibleArray)
        return possibleArray[v]
    }

    fun test() {
        val defaultHearts = "4-10"
        var amount = 20.0
        /*if(defaultHearts is Double)
            amount = defaultHearts.convertHearts()
        else */if(defaultHearts is String) {
            // If it contains a -, it's a range, if it contains multiple -, evaluate each range
            // and calculate the final range from first value to the last value
            val split = defaultHearts.split("-")
            if(split.size == 1) {
                amount = split[0].convertHearts()
            } else if(split.size == 2) {
                val min = split[0].convertHearts()
                val max = split[1].convertHearts()
                amount = min + min.randomHearts(max)
            } else {
                val first = split[0].convertHearts()

                val stack = Stack<Double>()
                for(i in 1 until split.size) {
                    val value = split[i].convertHearts()
                    stack.push(value)
                }

                while(!stack.isEmpty()) {
                    val v1 = stack.pop()
                    val v2 = stack.pop()
                    stack.add(0, v1.randomHearts(v2))
                }

                val second = stack.pop()
                val least = first.coerceAtMost(second)

                amount = least + least.randomHearts(first.coerceAtLeast(second))
            }
        }

        println(amount)
    }

    fun test2() {
        for (i in 1..10) {
            println(4 + 4.0.randomHearts(10.0))
        }
    }

}

fun main() {
    Test2().test()
}