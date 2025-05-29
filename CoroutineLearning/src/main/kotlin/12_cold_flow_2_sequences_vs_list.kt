package org.example

/*
*   Hot                             Cold
    Collections (List, Set)         Sequence, Stream
    Channel                         Flow, RxJava streams


   As a result, cold data streams (like Sequence, Stream or Flow):
    • can be infinite
    • do a minimal number of operations
    • use less memory (no need to allocate all the intermediate collections)

 In Short, Cold data sources are lazy. They process elements on-demand on the terminal operation. All intermediate
 functions just define what should be done (most often using the Decorator pattern). They generally do not store
 elements and create them ondemand. These elements are Sequence, Java Stream, Flow and RxJava streams
 (Observable,Single, etc).
* */

private fun hotColdFlow_type1() {
    val l = buildList {
        repeat(3) {
            add("User$it")
            println("hotColdFlow_type1: Added User List")
        }
    }

    val l2 = l.map {
        println("hotColdFlow_type1: Processing")
        "Processed List $it"
    }

    val s = sequence {
        repeat(3) {
            yield("User$it")
            println("hotColdFlow_type1: Added User sequence")
        }
    }
    val s2 = s.map {
        println("hotColdFlow_type1: Processing")
        "Processed sequence $it"
    }
}

/*
Here, all the operation for list from line 21-31, will be called for each element. But for sequence(line 33-41),
no elements will be emitted by default
* */


private fun hotColdFlow_m(i: Int): Int {
    print("$i ")
    return i * i
}

private fun hotColdFlow_f(i: Int): Boolean {
    print("f$i ")
    return i >= 10
}

private fun hotColdFlow_type2() {
    println("hotColdFlow_type2: List Hot flow")
    listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .map { hotColdFlow_m(it) }
        .find { hotColdFlow_f(it) }
        .let { print(it) }
    println()
    println()
    println("hotColdFlow_type2: Sequence Cold flow")
    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .map { hotColdFlow_m(it) }
        .find { hotColdFlow_f(it) }
        .let { print(it) }
}

/*
Output:
    // m1 m2 m3 m4 m5 m6 m7 m8 m9 m10 f1 f4 f9 f16 16 -> List
    // m1 f1 m2 f4 m3 f9 m4 f16 16  -> Sequence

    With List(line 63)
        Every step (map, then find) creates a new intermediate collection
        Memory and CPU are wasted

     With Sequence(line 70)
        No intermediate collections
        Operations are performed element-by-element

    Key Characteristics
        Property	    Collection	                Sequence
        Evaluation	    Eager (Immediate)	        Lazy (Deferred)
        Intermediate    Results	New Collections	    No new collections
        Suitable for	Small/medium data	        Large or chained operations
        Overhead	    Higher	                    Lower

* */
fun main() {
    hotColdFlow_type1()
    println()
    println()
    hotColdFlow_type2()
}
