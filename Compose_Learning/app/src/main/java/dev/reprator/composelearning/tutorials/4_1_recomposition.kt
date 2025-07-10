package dev.reprator.composelearning.tutorials

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private data class UserUnStable(
    val age: Int,
    var isSelected: Boolean
) // ✅ UNSTABLE because of 'var'

private data class UserStable(val age: Int, val isSelected: Boolean) // ✅ STABLE

@Composable
private fun RecompositionUnstable(modifier: Modifier = Modifier) {
    var userUnStable by remember { mutableStateOf(UserUnStable(25, false)) }
    var userStable by remember { mutableStateOf(UserStable(25, false)) }

    println("1 RecompositionUnstable lambda root")

    Column {
        println("1.1 RecompositionUnstable lambda container")
        Button(onClick = {
            val nuserUnStable = userUnStable.copy(userUnStable.age + 1, userUnStable.isSelected)
            println("1.1 Instance Check: structural ${userUnStable === nuserUnStable}, equality: ${userUnStable == nuserUnStable}, userUnStable: $userUnStable, nuserUnStable: $nuserUnStable")
            userUnStable = nuserUnStable
        }) {
            println("1.1 RecompositionUnstable unstable lambda age update")
            Text("Button 1: Update UnStable age update with values: ${userUnStable.age}, ${userUnStable.isSelected}")
        }

        Spacer(Modifier.height(20.dp))

        Button(onClick = {
            val nuserUnStable = userUnStable.copy(isSelected = !userUnStable.isSelected)
            println("1.2 Instance Check: structural ${userUnStable === nuserUnStable}, equality: ${userUnStable == nuserUnStable}, userUnStable: $userUnStable, nuserUnStable: $nuserUnStable")
            userUnStable = nuserUnStable
        }) {
            println("1.2 RecompositionUnstable unstable lambda isSelected toggle update")
            Text("Button 2: Update UnStable isSelected toggle update with values: ${userUnStable.age}, ${userUnStable.isSelected}")
        }

        Spacer(Modifier.height(20.dp))

        Button(onClick = {
            val nuserUnStable = userUnStable.copy(isSelected = userUnStable.isSelected)
            println("1.3 Instance Check: structural ${userUnStable === nuserUnStable}, equality: ${userUnStable == nuserUnStable}, userUnStable: $userUnStable, nuserUnStable: $nuserUnStable")
            userUnStable = nuserUnStable
        }) {
            println("1.3 RecompositionUnstable unstable lambda isSelected update")
            Text("Button 3: Update UnStable isSelected update with values: ${userUnStable.age}, ${userUnStable.isSelected}")
        }

        Spacer(Modifier.height(20.dp))

        Button(onClick = {
            userUnStable.isSelected = !userUnStable.isSelected
            //userUnStable.age = userUnStable.age + 1
        }) {
            println("1.4 RecompositionUnstable unstable lambda isSelected update manually")
            Text("Button 4: Update UnStable isSelected update manually with values: ${userUnStable.age}, ${userUnStable.isSelected}")
        }

        Spacer(Modifier.height(20.dp))

        Button(onClick = {
            val nuserStable = userStable.copy(userStable.age + 1)
            println("1.5 Instance Check: structural ${nuserStable === userStable}, equality: ${nuserStable == userStable}, userStable: $nuserStable, userStable: $userStable")
            userStable = nuserStable
        }) {
            println("1.5 RecompositionUnstable stable lambda")
            Text("Button 5: Update Stable with values: ${userStable.age}, ${userStable.isSelected}")
        }

        RandomColorColumn {
            var innerUserStable by remember { mutableStateOf(UserStable(125, true)) }
            Button(onClick = {
                innerUserStable = userStable.copy(innerUserStable.age + 1)
            }) {
                println("1.8 RecompositionUnstable innerUserStable lambda")
                Text("Button 6: Update innerUserStable with values: ${innerUserStable.age}, ${innerUserStable.isSelected}")
            }
        }

        Spacer(Modifier.height(20.dp))

        RandomColorColumn {
            println("1.6 RecompositionUnstable Individual both")
            UserItemUnStable1(userUnStable)
            UserItemStable1(userStable)
        }

        Spacer(Modifier.height(20.dp))

        RandomColorColumn {
            println("1.7 RecompositionUnstable Individual only UserItemUnStable1")
            UserItemUnStable1(userUnStable)
        }
    }
}

@Composable
private fun UserItemUnStable1(user: UserUnStable) {
    println("🔥 Composing UnStable: ${user.age}, ${user.isSelected}")
    Text("UnStable User: ${user.age}, ${user.isSelected}")
}

@Composable
private fun UserItemStable1(user: UserStable) {
    println("✅ Composing Stable: ${user.age}, ${user.isSelected}")
    Text("Stable User: ${user.age}, ${user.isSelected}")
}


/*
Points to be cleared:
      UserUnStable: It is unstable data class, due to use of var.

Output:
        1) Onclick of "Button 1: Update UnStable age update with values", line 45,
                1.1 Instance Check: structural false, equality: false, userUnStable: UserUnStable(age=25, isSelected=false), nuserUnStable: UserUnStable(age=26, isSelected=false)
                1.1 RecompositionUnstable unstable lambda age update
                1.2 RecompositionUnstable unstable lambda isSelected toggle update
                1.3 RecompositionUnstable unstable lambda isSelected update
                1.4 RecompositionUnstable unstable lambda isSelected update manually
                1.6 RecompositionUnstable Individual both
                🔥 Composing UnStable: 26, false
                1.7 RecompositionUnstable Individual only UserItemUnStable1
                🔥 Composing UnStable: 26, false

        2) Onclick of "Button 2: Update UnStable isSelected toggle update with values", line 56,
                1.2 Instance Check: structural false, equality: false, userUnStable: UserUnStable(age=26, isSelected=false), nuserUnStable: UserUnStable(age=26, isSelected=true)
                1.1 RecompositionUnstable unstable lambda age update
                1.2 RecompositionUnstable unstable lambda isSelected toggle update
                1.3 RecompositionUnstable unstable lambda isSelected update
                1.4 RecompositionUnstable unstable lambda isSelected update manually
                1.6 RecompositionUnstable Individual both
                🔥 Composing UnStable: 26, true
                1.7 RecompositionUnstable Individual only UserItemUnStable1
                🔥 Composing UnStable: 26, true

        3) Onclick of "Button 3: Update UnStable isSelected update with values", line 67,
                 1.3 Instance Check: structural false, equality: true, userUnStable: UserUnStable(age=27, isSelected=false), nuserUnStable: UserUnStable(age=27, isSelected=false)

    Explanation for 1,2,3:
                a) As whenever the update is done on "userUnStable" state object, compose will recompose all the composable
                    subscribed to it.
                b) So, here Button 1.2, 1.3, 1.4 and one Text composable(UserItemUnStable1) is subscribed to "userUnStable"
                    so, all gets recomposed
                c) Note: even though "isSelected" is of type var in UserUnStable, but it is still tracked by composable,
                    due to usage of userUnStable(line 20) as mutableState.
                        So, whenever the state update(object assignment) for userUnStable happen(line 47, 58, 69), the
                   compose checks for equality (==) and since, the object value were different for Button 1,2, the recomposition is
                   Dispatched to corresponding composable.

        4) Onclick of "Button 4: Update UnStable isSelected update manually with values", line 78,
                  Nothing printed
    Explanation:
            Since compose track the state changes via mutableStateOf, but here variable updates are happening(line 78, 79) normally
            not via object update like earlier(line 47, 58, 69) and hence these updates are untrackable, so compose is unaware of these changes

        5) Onclick of "Button 5: Update Stable with values", line 88,
                1.5 Instance Check: structural false, equality: false, userStable: UserStable(age=26, isSelected=false), userStable: UserStable(age=25, isSelected=false)
                1.5 RecompositionUnstable stable lambda
                1.6 RecompositionUnstable Individual both
                🔥 Composing UnStable: 27, true
                ✅ Composing Stable: 26, false

    Explanation:
        a) Here, onclick of Button 5, only stable types are update, and hence all the composable subscribed to userStable, will be recomposed.
        b) But since the "userUnStable" is of unstable type(Due to var nature of isSelected) of mutable state and is in same scope,
           as that of "userStable" - ignore it, point c is valid
        c) If there is any composable function((UserItemUnStable1(line 110)), which are dependent/subscribed
            to Unstable type, and that is in same scope where stable type is being read(line 111), then compose
            make sure that called/defined function also gets recomposed(UserItemUnStable1(line 110)), even when there is
            no change in unstable type.
                Remember the Button 1...4, will not recompose, as they in different scope

       e) Note:
                If you tweak line from(108-112), like below,
                            RandomColorColumn {
                                println("1.6 RecompositionUnstable Individual both")
                                UserItemUnStable1(userUnStable)
                                RandomColorColumn {
                                    UserItemStable1(userStable)
                                }
                            }
                   Then Output will be like:
                            1.5 Instance Check: structural false, equality: false, userStable: UserStable(age=26, isSelected=false), userStable: UserStable(age=25, isSelected=false)
                            1.5 RecompositionUnstable stable lambda
                            ✅ Composing Stable: 26, false
                    This will happen, because the stable state is being read in different composable scope(line 205-207), and
                    unstableState is being read in other composable scope(202-208).

        6) Onclick of "Button 6: Update innerUserStable with values", line 98,
                1.8 RecompositionUnstable innerUserStable lambda

    Explanation:
        a) Here, innerUserStable is inner and is being read only inside button(line 97) not outside of it

* */


private data class UnstableListWrapper(val list: List<Int>) //Unstable due to list

@Immutable
private data class StableListWrapper(val list: List<Int>) // Stable due annotation

@Composable
private fun RecompositionImmutableAnnotationSample(modifier: Modifier = Modifier) {

    var counter by remember {
        mutableIntStateOf(0)
    }

    val listWrapper by remember {
        mutableStateOf(UnstableListWrapper(listOf(1, 2, 3)))
    }

    val immutableListWrapper by remember {
        mutableStateOf(StableListWrapper(listOf(1, 2, 3)))
    }

    Column(
        modifier = Modifier
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { counter++ }) {
            Text(text = "Counter: $counter")
        }

        // This scope is recomposed because counter is read here
        // Stable types do not get recomposed when there input don't change
        RandomColorColumn {
            ImmutableSampleCounter(text = "Counter $counter")
            Spacer(Modifier.height(20.dp))
            ImmutableSampleUnstableComposable(listWrapper)
            Spacer(Modifier.height(20.dp))
            ImmutableSampleStableComposable(immutableListWrapper = immutableListWrapper)
        }
    }
}

/*
Output:
        1) Onclick of "Counter", line 253,
                ImmutableSampleCounter()
                🍏 ImmutableSampleUnstableComposable()

    Explanation:
        Here, the explanation is same as previous "RecompositionUnstable",
        a) On counter click, only counter lambda gets invalidated, due to counter update
        b) But since, listWrapper is unstable mutableState due to list and directly in same scope, where counter is
            being read(line 260), so whenever the counter/stable type is updated, it also update/recompose, all the child composable
            which are subscribed to listWrapper/unstable state type in same scope.
                    But since, immutableListWrapper is of stable type due to usage of "@Immutable" annotation class,
           It will not get recomposed.
 */

@Composable
private fun ImmutableSampleCounter(
    text: String
) {
    SideEffect {
        println("ImmutableSampleCounter()")
    }
    Text(text = "Counter() text: $text")
}

@Composable
private fun ImmutableSampleUnstableComposable(
    listWrapper: UnstableListWrapper
) {
    SideEffect {
        println("🍏 ImmutableSampleUnstableComposable()")
    }
    Text(text = "ImmutableSampleUnstableComposable() list: $listWrapper")
}

@Composable
private fun ImmutableSampleStableComposable(
    immutableListWrapper: StableListWrapper
) {
    SideEffect {
        println("🍎 ImmutableSampleStableComposable()")
    }
    Text(text = "ImmutableSampleStableComposable() list: $immutableListWrapper")
}

@Stable
private data class StableUiState(var list: List<Int> = listOf(1, 2, 3)) {
    var count by mutableIntStateOf(0)
}

@Composable
private fun RecompositionStableAnnotationSample(modifier: Modifier = Modifier) {

    var stableUiState by remember {
        mutableStateOf(StableUiState())
    }

    println("RecompositionStableAnnotationSample Root...")
    Column(
        modifier = Modifier
            .background(getRandomColor())
            .padding(4.dp)
    ) {
        println("RecompositionStableAnnotationSample 1: Column container...")

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                stableUiState = stableUiState.copy(
                    List(3) {
                        Random.nextInt(100)
                    }) }
        ) {
            Text(text = "Create new state")
        }
        Spacer(Modifier
            .background(getRandomColor())
            .height(30.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                stableUiState.count++
            }
        ) {
            Text(text = "Update Stable count param")
        }

        Spacer(Modifier
            .background(getRandomColor())
            .height(15.dp))

        RecompositionStableComposable(stableUiState)
    }
}

@Composable
private fun RecompositionStableComposable(unstableUiState: StableUiState) {
    RandomColorColumn {
        println("RecompositionStableAnnotationSample 2: RecompositionStableComposable scope...")
        RecompositionStableAnnotationListComposable(unstableUiState.list)
        RecompositionStableAnnotationValueComposable(count = unstableUiState.count)
    }
}

@Composable
private fun RecompositionStableAnnotationListComposable(
    list: List<Int>,
) {
    println("RecompositionStableAnnotationSample 3: ListComposable()")
    Column(
        modifier = Modifier
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        list.forEach {
            Text(text = "ListComposable() value: $it")
        }
    }
}

@Composable
private fun RecompositionStableAnnotationValueComposable(count: Int) {
    println("RecompositionStableAnnotationSample 4: ValueComposable()")
    Column(
        modifier = Modifier
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(text = "ValueComposable() count: $count")
    }
}

/*
Output:
        1) On click of "Create new State":
            RecompositionStableAnnotationSample Root...
            RecompositionStableAnnotationSample 1: Column container...
            RecompositionStableAnnotationSample 2: RecompositionStableComposable scope...
            RecompositionStableAnnotationSample 3: ListComposable()

        2) On click of "Update Stable count param":
            RecompositionStableAnnotationSample 2: RecompositionStableComposable scope...
            RecompositionStableAnnotationSample 3: ListComposable()
            RecompositionStableAnnotationSample 4: ValueComposable()

      Explanation 2:
            1) Here you are only updating only the count,  Ideally you should only see this:
                    ValueComposable()

            2) But the compose is recomposing the whole body/lambda of Column(line 368-372) because of following reason,
                  a)  ⚠️ The Gotcha: List<Int> is Not @Stable
                      Even though your StableUiState is marked @Stable, its field:
                            var list: List<Int> = listOf(1, 2, 3)
                       is:
                       • A mutable var
                       • Refers to a List type that Compose considers unstable by default (List<T> is not @Stable unless
                         it's an ImmutableList)
                    So Compose says:
                        “I don’t know whether list changed or not, so I must assume it did.”
                    Hence, RecompositionStableAnnotationListComposable(list) is recomposed.

                b) ✅ How to Fix / Prove the Stability
                       b.i)
                            import androidx.compose.runtime.Immutable
                            val list: ImmutableList<Int> = persistentListOf(1, 2, 3)
                       b.ii)
                            Enable strong skipping mode via build.gradle
    * */

@Composable
fun RecompositionSample(modifier: Modifier = Modifier) {
    println("🔥 RecompositionSample Root")

    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()).then(modifier)) {
        println("🔥 RecompositionSample Root Container")
        RecompositionUnstable()
        Spacer(Modifier
            .background(getRandomColor())
            .height(30.dp))
        RecompositionImmutableAnnotationSample()
        Spacer(Modifier
            .background(getRandomColor())
            .height(30.dp))
        RecompositionStableAnnotationSample()
    }
}

/*
Disable Strong skipping mode, to see the effect:
    composeCompiler {
        featureFlags = setOf(ComposeFeatureFlag.StrongSkipping.disabled())
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
    }

Stable parameters:
    If a composable has stable parameters that have not changed, Compose skips it on Recomposition
        ✅ A Type is Stable If(Requirements for stable type):
                • It is a primitive (e.g. Boolean, Int, Long, Float, Char).
                • It is a data class with only stable fields.
                • It uses Compose’s @Stable or @Immutable annotations.
                • It doesn’t override equals() or hashCode() in a way that breaks Compose's expectations.
                • All function types (lambdas)

        ✅ Valid stable types:
                 • data class User(val name: String, val age: Int) // ✅ Stable by default

                 • @Stable
                   class Counter {
                     var value: Int by mutableStateOf(0)
                   }

                 • @Immutable
                   data class UiState(val loading: Boolean, val data: String)

                 • class MyClassStable(val counter: Pair<Int, Int>)

Unstable parameters:
    If a composable has unstable parameters, Compose always recomposes it when parent recomposes.

        ✅ A Type is UnStable If(Requirements for unstable type):
                It It does not follow any of the stability steps mentioned from line 63 - 68 etc

        🛑 Unstable Type Example

                • class MyClassUnstable(val counter: Pair<LocalDate, LocalDate>)
                        as LocalDate is not of primitive type

                • class MyClassUnstable(val counter: Pair<*, *>)
                        as pair data is generic and not of primitive type

                • data class User(val age: List<Int>)
                        as List is not of primitive type

                • class User { // ❌ Not a data class, no equals/hashCode
                    var name: String = ""
                    var age: Int = 0
                  }

                • data class MutableUser(var name: String, var age: Int) // ❌ Not stable because var fields can change silently

🔍 @Stable vs @Immutable in Jetpack Compose
    🧱 Definition
        Marker	        Meaning
        @Stable	        Object might change, but it signals Compose which properties to track.
        @Immutable	    Object won’t change after creation (like Kotlin data class + vals).

    🧠 Key Differences
        Feature	                            @Stable	                                    @Immutable
        Can have mutable properties	        ✅ Yes (usually with mutableStateOf)	    ❌ No (all vals, deeply immutable)
        Compose optimization support	    ✅ Yes	                                    ✅ Full
        Used for recomposition skip	        ✅ Partial	                                ✅ Full
        Default for data class	            ❌ No	                                    ✅ If all vals & immutables
        When to use	                        Custom mutable models	                    Value objects / DTOs

    🧠 Analogy: Building Blocks vs Sculptures
        • 🔨 @Stable is like a Lego block — you can change pieces (like count++), but Compose knows
                which pieces to track.
        • 🗿 @Immutable is like a sculpture made of stone — once made, it never changes. Compose skips
                recomposition altogether if nothing else changes.

    💡 Usage Scenarios
        ✅ Use @Immutable when:
            • You use a data class with all val properties.
            • The object is passed around but never mutated.
                    @Immutable
                    data class User(val id: Int, val name: String)

        ✅ Use @Stable when:
            • Your object has internal state using mutableStateOf.
            • You want to track fine-grained changes inside an object.
                    @Stable
                    class UiState {
                        var count by mutableStateOf(0)
                    }

    🔍 How Compose Treats Them
        Object Type	            Does Compose Recompose Child on Update?
        Plain class	            ✅ Always recomposes
        @Stable class	        🔄 Recompose only if tracked properties change
        @Immutable class	    ❌ Never recomposes unless reference changes

    📌 Tips
        • @Immutable is safer and more performant if immutability fits your data model.
        • Use @Stable when you need mutable internal state.
        • Compose auto-detects immutability for data class with only val members — no need to annotate.
* */