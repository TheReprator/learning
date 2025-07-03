package dev.reprator.composelearning.tutorials

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class UserUnStable(val age: Int, var isSelected: Boolean) // ✅ UNSTABLE because of 'var'
data class UserStable(val age: Int, val isSelected: Boolean) // ✅ STABLE

@Composable
private fun RecompositionUnstable(modifier: Modifier = Modifier) {
    var userUnStable by remember { mutableStateOf(UserUnStable(25, false)) }
    var userStable by remember { mutableStateOf(UserStable(25, false)) }

    println("1 RecompositionUnstable lambda root")

    Column {
        println("1.1 RecompositionUnstable lambda container")
        Button(onClick = {
            val nuserUnStable = userUnStable.copy(userUnStable.age+1, userUnStable.isSelected)
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
            val nuserStable = userStable.copy(userStable.age+1)
            println("1.5 Instance Check: structural ${nuserStable === userStable}, equality: ${nuserStable == userStable}, userStable: $nuserStable, userStable: $userStable")
            userStable = nuserStable
        }) {
            println("1.5 RecompositionUnstable stable lambda")
            Text("Button 5: Update Stable with values: ${userStable.age}, ${userStable.isSelected}")
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
fun RecompositionSample(modifier: Modifier = Modifier) {
    println("🔥 RecompositionSample Root")

    Column(modifier = modifier.padding(16.dp)) {
        println("🔥 RecompositionSample Root Container")
        RecompositionUnstable()
    }
}

/*
Points to be cleared:
      UserUnStable: It is unstable data class, due to use of var.

Output:
        1) Onclick of "Button 1: Update UnStable age update with values", line 30,
                1.1 Instance Check: structural false, equality: false, userUnStable: UserUnStable(age=25, isSelected=false), nuserUnStable: UserUnStable(age=26, isSelected=false)
                1.1 RecompositionUnstable unstable lambda age update
                1.2 RecompositionUnstable unstable lambda isSelected toggle update
                1.3 RecompositionUnstable unstable lambda isSelected update
                1.4 RecompositionUnstable unstable lambda isSelected update manually
                1.6 RecompositionUnstable Individual both
                🔥 Composing UnStable: 26, false
                1.7 RecompositionUnstable Individual only UserItemUnStable1
                🔥 Composing UnStable: 26, false

        2) Onclick of "Button 2: Update UnStable isSelected toggle update with values", line 41,
                1.2 Instance Check: structural false, equality: false, userUnStable: UserUnStable(age=26, isSelected=false), nuserUnStable: UserUnStable(age=26, isSelected=true)
                1.1 RecompositionUnstable unstable lambda age update
                1.2 RecompositionUnstable unstable lambda isSelected toggle update
                1.3 RecompositionUnstable unstable lambda isSelected update
                1.4 RecompositionUnstable unstable lambda isSelected update manually
                1.6 RecompositionUnstable Individual both
                🔥 Composing UnStable: 26, true
                1.7 RecompositionUnstable Individual only UserItemUnStable1
                🔥 Composing UnStable: 26, true

        3) Onclick of "Button 3: Update UnStable isSelected update with values", line 52,
                 1.3 Instance Check: structural false, equality: true, userUnStable: UserUnStable(age=27, isSelected=false), nuserUnStable: UserUnStable(age=27, isSelected=false)

    Explanation for 1,2,3:
                a) As whenever the update is done on "userUnStable" state object, compose will recompose all the composable
                    subscribed to it.
                b) So, here Button 1.2, 1.3, 1.4 and one Text composable(UserItemUnStable1) is subscribed to "userUnStable"
                    so, all gets recomposed
                c) Note: even though "isSelected" is of type var in UserUnStable, but it is still tracked by composable,
                    due to usage of userUnStable(line 20) as mutableState.
                        So, whenever the state update(object assignment) for userUnStable happen(line 32, 43, 54), the
                   compose checks for equality (==) and since, the object value were different for Button 1,2, the recomposition is
                   Dispatched to corresponding composable.

        4) Onclick of "Button 4: Update UnStable isSelected update manually with values", line 63,
                  Nothing printed
    Explanation:
            Since compose track the state changes via mutableStateOf, but here variable updates are happening(line 63, 64) normally
            not via object update like earlier(line 32, 43, 54) and hence these updates are untrackable, so compose is unaware of these changes

        5) Onclick of "Button 5: Update Stable with values", line 73,
                1.5 Instance Check: structural false, equality: false, userStable: UserStable(age=26, isSelected=false), userStable: UserStable(age=25, isSelected=false)
                1.5 RecompositionUnstable stable lambda
                1.6 RecompositionUnstable Individual both
                🔥 Composing UnStable: 27, true
                ✅ Composing Stable: 26, false

    Explanation:
        a) Here, onclick of Button 5, only stable types are update, and hence all the composable subscribed to userStable, will be recomposed.
        b) But since the "userUnStable" is of unstable type due to "var nature of isSelected", hence, whoever composable is subscribed
            to it via parameter like(UserItemUnStable1(line 172) from line 85,93),  will also be recomposed
                Remember the Button 1...4, will not recompose, as they are withing the same composable
* */


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

                • class User { // ❌ Not a data class, no equals/hashCode
                    var name: String = ""
                    var age: Int = 0
                  }

                • data class MutableUser(var name: String, var age: Int) // ❌ Not stable because var fields can change silently

* */