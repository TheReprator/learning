package org.example

/*
🧩 Bonus: What is hot flow really?
"Hot Flow" is a concept, not a class.
It describes any Flow that emits independently of collectors — typically using SharedFlow, StateFlow, or callbackFlow.

🔥 Cold Flow vs Hot Flow vs SharedFlow (in Kotlin)
    💧 1. Cold Flow (Flow)
        Default flow created with flow { }
        Does nothing until collected
        Each collector gets a fresh and independent execution
        Like a lazy stream (pull-based)

    🔥 2. Hot Flow
            Hot flows emit data regardless of collectors — they are always on, like an event bus.
            Examples:
                SharedFlow
                StateFlow
                Channel
                CallbackFlow (wrapping hot sources like listeners)
    Unlike cold flows, hot flows are push-based and shared.

✅ Cold vs Hot Flow(SharedFlow vs Shared Summary)
    Feature	                Flow (Cold)	                SharedFlow (Hot)	        StateFlow (Hot + State Holder)
    Type	                Cold	                    Hot	                        Hot
    Lifecycle	            Starts per collector	    Starts once, shared	        Starts once, shared
    Current Value	        ❌ No	                    ❌ No	                    ✅ Yes (.value)
    Replay	                ❌ No	                    ✅ Configurable	            ✅ Always 1
    Collectors	            New collectors get	        Get last replay values	    Get current value immediately
                            fresh data
    Ideal For	            Streams, transformation	    One-time events, 	        UI state, single source of truth
                            chains                      broadcasts
    Backpressure	        Suspends emitter	        Configurable (buffer/drop)	Drops old value
    Thread Safety	        Generally yes	            Yes	                        Yes
    Completion	            ✅ Completes	            ❌ Infinite by design	    ❌ Infinite by design
    Emits If No Collector?	❌ No	                    ✅ Yes (stays active)	    ✅ Yes


📘 Under the Hood
    Feature	            Flow	                SharedFlow	                            StateFlow
    Based On	        Coroutine builder	    Built on Flow & Channel internally	    Built on SharedFlow internally
    Memory Model	    No cache	            Configurable replay / buffer	        Always holds latest value (replay = 1)
    Emission Model	    Push on collection	    Broadcast-style push	                Push + state


🚦 Behavior Summary
    Behavior	                        Flow	            SharedFlow	                StateFlow
    Hot / Cold	                        Cold	            Hot	                        Hot
    Starts on subscription	            Yes	                No (already active)	        No (already active)
    Shares emissions	                No	                Yes	                        Yes
    Retains last value	                No	                Optional	                Yes
    Collectors get latest value	        No	                If replay > 0	            Always
    Suitable for	                    Data streams	    Events	                    State management


🧠 When to Use What?
    Use Case	                                                            Use
    Fetching fresh data from network, DB or paged API 	                    Flow (cold)
    Emitting UI events like Navigation, clicks, Toasts	                    SharedFlow
    Exposing and collecting state from ViewModel	                        StateFlow
    Wrapping listener-based APIs (e.g., Bluetooth, Sensors)	                CallbackFlow



📌 shareIn vs stateIn
    Feature	            shareIn	                    stateIn
    Base class	        SharedFlow	                StateFlow
    Last value kept?	Optional (via replay)	    Always stores last value
    Use case	        Events, broadcasts	        UI state, configs


* */