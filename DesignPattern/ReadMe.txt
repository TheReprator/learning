Design patterns are reusable solutions to common software design problems. They represent best practices used by experienced software developers to solve specific design issues in a flexible and maintainable way.

📌 Benefits of Design Patterns:

    • Reusability: Design patterns encapsulate reusable solutions to common design problems, promoting code reuse and minimizing redundancy.
    • Maintainability: By adhering to established design patterns, code becomes more organized, modular, and easier to maintain.
    • Scalability: Design patterns facilitate scalability by providing flexible and adaptable solutions that can evolve with changing requirements.
    • Readability: Design patterns improve code readability and comprehension, making it easier for developers to understand and collaborate on projects.


📌 Types of Design Pattern:

    🔷 Creational patterns:
                It provide object creation mechanisms that increase flexibility and reuse of existing code.
         Short Meaning: How you create objects.

         • Types:
                1) Singleton:
                        Ensures a class has only one instance and provides a global point of access.

                    😌 Real life analogy:
                            Real-life: President of a Country
                            There can be only one official president at a time. Everyone refers to that single authority.

                    Example:
                            ./src/main/kotlin/org/example/creational/Singleton.kt


                2) Factory:
                        Defines an interface for creating an object, but lets subclasses decide which class to instantiate.

                    😌 Real life analogy:
                            Real-life: Pizza Store
                            You call and say, “I want a Veg Pizza.” The store decides whether to give you thin crust, deep dish, etc., based on availability.

                    Example:
                             ./src/main/kotlin/org/example/creational/Factory.kt

            3) Abstract Factory:
                    Provides an interface for creating families of related or dependent objects without specifying their concrete classes.

                    😌 Real life analogy:
                            Real-life: Furniture Factory by Theme
                            You want Victorian-style furniture. A Victorian Factory gives you a Victorian sofa, Victorian chair, etc., all matching a family of styles.

                    Example:
                             ./src/main/kotlin/org/example/creational/AbstractFactory.kt

            3) Builder:
                    Separates the construction of a complex object from its representation.

                    😌 Real life analogy:
                            Real-life: Ordering a Custom Burger
                            You specify you want double cheese, no pickles, extra lettuce — the restaurant builds it step by step and finally gives you a fully built custom burger.

                    Example:
                             ./src/main/kotlin/org/example/creational/Builder.kt

            3) Prototype:
                    Creates new objects by copying an existing object (clone).

                    😌 Real life analogy:
                            Real-life: Cloning a Template Document
                            Instead of writing a new contract from scratch, you copy an existing legal document and edit it — cloning and customizing.
                    Example:
                             ./src/main/kotlin/org/example/creational/Prototype.kt


    🔷 Structural patterns:
                It explain how to assemble objects and classes into larger structures, while keeping these
                structures flexible and efficient.
        Short Meaning: How you compose objects.

    🔷 Behavioral patterns:
                It take care of effective communication and the assignment of responsibilities between objects.
        Short Meaning: How you coordinate object interactions.


📌 References:
    https://refactoring.guru/design-patterns/creational-patterns