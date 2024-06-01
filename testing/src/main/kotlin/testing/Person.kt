package testing

import io.github.stefankoppier.mapping.annotations.Mapper

data class Person(val name: String)

data class PersonDto(val name: String, val fullname: String, val age: Int)

object PersonMapper : Mapper<Person, PersonDto>() {

    override fun map(from: Person): PersonDto = mapping {
        PersonDto::fullname property Person::name
        PersonDto::age constant 26
    }
}