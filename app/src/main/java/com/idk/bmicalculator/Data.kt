package com.idk.bmicalculator

enum class Gender {
    Male,
    Female,
    None
}

enum class HeightUnit {
    Feet,
    Centimeter
}

enum class WeightUnit {
    Kilogram,
    Pound
}

data class UserData(
    val gender: Gender = Gender.None,
    val height: Int? = null,
    val heightUnit: HeightUnit = HeightUnit.Feet,
    val weight: Int? = null,
    val weightUnit: WeightUnit = WeightUnit.Kilogram
)

enum class Screen {
    Gender,
    Height,
    Weight,
    Result
}