package testing

import io.github.mappie.api.ObjectMappie
import io.github.mappie.api.EnumMappie

enum class BooleanEnum {
    TRUE,
    FALSE,
}

data class Thing(val inner: Thang, val boolean: BooleanEnum)

data class Thang(val description: String)

enum class BooleanDto {
    TRUE,
    FALSE,
}

data class ThingDto(val inner: ThangDto, val boolean: BooleanDto)

data class ThangDto(val description: String)

object ThingMapper : ObjectMappie<Thing, ThingDto>() {
    override fun map(from: Thing): ThingDto = mapping {
        ThingDto::inner mappedFromProperty Thing::inner via ThangMapper // TODO: uitcommenten levert een bug op: type check moet toegevoegd worden aan automatische resolve
        ThingDto::boolean mappedFromProperty Thing::boolean via BooleanMapper()
    }
}

object ThangMapper : ObjectMappie<Thang, ThangDto>() {
    override fun map(from: Thang): ThangDto = mapping()
}

class BooleanMapper : EnumMappie<BooleanEnum, BooleanDto>() {
    override fun map(from: BooleanEnum): BooleanDto = mapping()
}