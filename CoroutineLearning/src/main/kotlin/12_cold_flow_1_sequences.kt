package org.example

/*
In Sequences, yield() causes the iterator coroutine to pause(act like a state machine).
    But it's a non-suspending coroutine.
    It's managed manually through a state machine that Kotlin generates under the hood.
    ⚡ It just stops execution at yield, remembers the place, and resumes later when next() is called.
    ✅ No suspend fun. ✅ No real "async" waiting like delay().
* */
private fun iterators() {
    val iterator = iterator {
            println("zero heck")
        yield("First")
            println("First heck")
        yield("Second")
            println("Second heck")
    }
    println(iterator.next())
    println(iterator.next())
   // println(iterator.next())
}
/*
line 11, creates an iterator BUT DOES NOT RUN the block yet.

This function will build an iterator that contains 2 elements.
    When we call, iterator.next() for first time line 18, the following thing will happen internally
        Now the coroutine starts executing:
                prints zero heck
                Hits yield("First")
            yield:
                Saves "First" as next value to be returned.
                Suspends the coroutine (pauses it here, after line 13).
                next() then returns "First".

         Output After Step1(line 18)
            zero heck
            First
        Important: At this point, the coroutine is suspended right after yield("First") but before println("First heck")
        It remembers exactly where it stopped!

    When we call, iterator.next() for Second time line 19, the following thing will happen internally
        Now the coroutine starts executing:
                prints First heck
                Hits yield("Second")
            yield:
                Saves "Second" as next value to be returned.
                Suspends the coroutine (pauses it here, after line 15).
                next() then returns "Second".

         Output After Step1(line 19)
            First heck
            Second
        Important: At this point, the coroutine is suspended right after yield("Second") but before println("Second heck")

    When we call, iterator.next() for third time or uncomment line 20, the following thing will happen internally
        Kotlin resumes the coroutine from where it was suspended (after yield(line 15)).
        So it continues:
            Prints Second heck
        The block ends after that. Because there is no more yield, the iterator now knows it's done.
    Since you are doing next(), Kotlin expects a value.
    But there's no value yielded, so it throws NoSuchElementException (error).

    So to avoid that, we could have done like below,
        if (iterator.hasNext()) {
            println(iterator.next())
        }
* */

/*
* Internal Working of Yield for sequence or iterator for above example:
* So "suspend" here only means "pause and resume locally in the iterator", whose pseudo-code is like below:

    when(state) {
        0 -> {
            println("zero heck")
            state = 1
            return First
        }
        2 -> {
            println("First heck")
            state = 2
            return Second
        }
        else -> {
            println("Second heck")
            throw NoSuchElementException()
        }
}
*
* */

private fun sequences() {
    val sequence = sequence {
        for (i in 0..9) {
            println("Yielding $i")
            yield(i)
        }
    }

    println("Requesting index 1")
    sequence.elementAt(1)
    println("Requesting index 2")
    sequence.elementAt(2)
    println("Taking 3")
    sequence.take(3).joinToString()
}
/*
*
 What are Sequences in Kotlin?
    Sequence is like a lazy version of collections (like List, Set)
    Instead of doing all operations immediately, sequences delay processing until absolutely necessary
    This can lead to much better performance when dealing with large collections.

 Characteristics of suspending sequences(due to yield):
    They can retrieve a value by index
    They are stateless, and they reset automatically after being interacted with
    You can take a group of values with a single call

 Output:
    Line 101.103, proves point(line 115)
    Line 105, proves point(line 117)
    Whenever we call element at or any other terminal operations(elementAt, take etc), it starts from beginning,
    hence it proves the point(line 116),
    Output to consolidate point(line 116)
        You may notice that a sequence will simply execute from the beginning for each call, which
    is different from using an iterator:
        Requesting index 1
        Yielding 0
        Yielding 1
        Requesting index 2
        Yielding 0
        Yielding 1
        Yielding 2
        Taking 3
        Yielding 0
        Yielding 1
        Yielding 2
* */

fun main() {
    println("Iterators")
    iterators()
    println()
    println()
    println("sequences")
    sequences()
}