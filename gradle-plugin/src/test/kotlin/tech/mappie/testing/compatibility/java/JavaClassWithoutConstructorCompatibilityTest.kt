package tech.mappie.testing.compatibility.java

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.mappie.testing.TestBase

class JavaClassWithoutConstructorCompatibilityTest : TestBase() {

    @BeforeEach
    fun setUp() {
        java("src/main/java/JavaClass.java",
            """
            public class JavaClass {
                private String value;
            
                public String getValue() {
                    return this.value;
                }
                
                public void setValue(String value) {
                    this.value = value;
                }
                
                @Override 
                public boolean equals(Object obj) {
                    if (obj == null) return false;
                    if (obj.getClass() != this.getClass()) return false;
                    return ((JavaClass) obj).value.equals(this.value);
                }
            }
            """.trimIndent()
        )

        kotlin("src/main/kotlin/KotlinClass.kt",
            """
            data class KotlinClass(val value: String)
            """.trimIndent()
        )
    }

    @Test
    fun `map Java class to Kotlin class via getter`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            object Mapper : ObjectMappie<JavaClass, KotlinClass>() {
                override fun map(from: JavaClass) = mapping {
                    to::value fromValue from.value!!
                }
            }
            """.trimIndent()
        )

        kotlin("src/test/kotlin/MapperTest.kt",
            """
            import kotlin.test.*
            
            class MapperTest {
            
                @Test
                fun test() {
                    assertEquals(
                        "value",
                        Mapper.map(JavaClass().apply { setValue("value") }).value
                    )
                }
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }

    @Test
    fun `map Kotlin class to Java class via getter`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            object Mapper : ObjectMappie<KotlinClass, JavaClass>()
            """.trimIndent()
        )

        kotlin("src/test/kotlin/MapperTest.kt",
            """
            import kotlin.test.*
            
            class MapperTest {
            
                @Test
                fun test() {
                    assertEquals(
                        "value",
                        Mapper.map(KotlinClass("value")).value
                    )
                }
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}