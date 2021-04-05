package converter

import java.lang.NumberFormatException


fun main() {
    while (true) {
        print("Enter what you want to convert (or exit): ")
        val valueStr = readLine().toString().toLowerCase()
        if (valueStr.contains("exit")) {
            break
        } else {
            startConvert(valueStr)
        }
    }
}

fun convertValue(checkValueArray: Array<Measure>, userValue: Double) {
    when {
        userValue < 0.0 && checkValueArray[0].type == MeasureType.Weight -> println("Weight shouldn't be negative.")
        userValue < 0.0 &&  checkValueArray[0].type == MeasureType.Length -> println("Length shouldn't be negative.")
        checkValueArray[0].type == checkValueArray[1].type && checkValueArray[0].type == MeasureType.Unknow -> {
            unknmowTypesOutput(checkValueArray, userValue)
        }
        checkValueArray[0].type == checkValueArray[1].type && checkValueArray[0].type == MeasureType.Temperature -> {
            teperatureTypesOutput(checkValueArray, userValue)
        }
        checkValueArray[0].type != checkValueArray[1].type -> {
            otherTypesOutput(checkValueArray, userValue)
        }
        else -> {
            lenghtMassTypesOutput(checkValueArray, userValue)
        }
    }
}

fun getPluralVal(unitsModel : Measure, userValue : Double) : String{
    return if(userValue != 1.0) unitsModel.plural
    else unitsModel.normal
}

fun lenghtMassTypesOutput( checkValueArray : Array<Measure>, userValue: Double) {
    val resault = (userValue * checkValueArray[0]!!.multiplier) / checkValueArray[1]!!.multiplier
    var firstWord = getPluralVal(checkValueArray[0], userValue)
    var secondWord = getPluralVal(checkValueArray[1], resault)
    println("$userValue $firstWord is $resault $secondWord")
}

fun otherTypesOutput(checkValueArray : Array<Measure>, userValue: Double) {
    var firstWord = checkValueArray[0].plural
    var secondWord = checkValueArray[1].plural
    println("Conversion from $firstWord to $secondWord is impossible")
}

fun teperatureTypesOutput( checkValueArray : Array<Measure>, userValue: Double) {
    var preResault = 0.0
    var resault = 0.0

    if (checkValueArray[0].normal == "degree Fahrenheit"){
        preResault = checkValueArray[0].calculateFahrenheitToCelsius(userValue)
    }else preResault = userValue + checkValueArray[0].shift

    if(checkValueArray[1].normal == "degree Fahrenheit"){
        resault = checkValueArray[1].calculateCelsiusToFahrenheit(preResault)
    }else resault = preResault + checkValueArray[1].multiplier

    var firstWord = getPluralVal(checkValueArray[0], userValue)
    var secondWord = getPluralVal(checkValueArray[1], resault)
    println("$userValue $firstWord is $resault $secondWord")
}

fun unknmowTypesOutput(checkValueArray : Array<Measure>, userValue: Double) {
    var firstWord = getPluralVal(checkValueArray[0], userValue)
    println("Conversion from $firstWord to ${checkValueArray[1].plural} is impossible")
}

fun startConvert(valueStr: String) {
    var userNumber = 0.0
    try {
         userNumber = valueStr.substringBefore(" ").toDouble()
    } catch (e : NumberFormatException){
        println("Parse error")
        return
    }
    var fromUnits = (valueStr.substringAfter(" ")).substringBefore(" ")
    if (fromUnits == "degree" || fromUnits == "degrees") {
        fromUnits = fromUnits + " " + valueStr.substringAfter(" ")
            .substringAfter(" ").substringBefore(" ")
    }
    var toUnits = ""
    if (valueStr.contains(" to ")) {
        toUnits = (valueStr.substringAfter(" to "))
    } else {
        toUnits = (valueStr.substringAfter(" in "))
    }
    val checkValueArray = checkValue(fromUnits, toUnits)
    convertValue(checkValueArray, userNumber)
}

fun checkValue(fromUnits: String, toUnits: String): Array<Measure> {
    var fromUnitsArray = Measure(MeasureType.Unknow, "???", "???", "???", 1.0)
    var toUnitsArray = Measure(MeasureType.Unknow, "???", "???", "???", 1.0)
    for (itemArray in measures) {
        for (item in itemArray.listValus) {
            if (fromUnits == item.toString().toLowerCase()) fromUnitsArray = itemArray
            if (toUnits == item.toString().toLowerCase())toUnitsArray = itemArray
        }
    }
    return arrayOf(fromUnitsArray, toUnitsArray)
}

enum class MeasureType {
    Length, Weight, Temperature, Unknow;

    fun of(
        short: String,
        normal: String,
        plural: String,
        multiplier: Double,
        shift: Double = 0.0,
        vararg otherNames: String
    ) = Measure(this, short, normal, plural, multiplier, shift, *otherNames)

}

class Measure(
    val type: MeasureType,
    val short: String,
    val normal: String,
    val plural: String,
    val multiplier: Double,
    val shift: Double = 0.0,
    vararg val otherNames: String
) {
    val listValus get() = listOf(short, normal, plural, multiplier, shift, *otherNames)

    fun calculateFahrenheitToCelsius(userValue: Double) = (userValue - 32) * 5 / 9
    fun calculateCelsiusToFahrenheit(userValue: Double) = (userValue * 9/5) + 32
}

val measures = listOf(
    MeasureType.Length.of("m", "meter", "meters", 1.0),
    MeasureType.Length.of("km", "kilometer", "kilometers", 1000.0), // one km is 1000.0 * 1 m
    MeasureType.Length.of("cm", "centimeter", "centimeters", 0.01),
    MeasureType.Length.of("mm", "millimeter", "millimeters", 0.001),
    MeasureType.Length.of("mi", "mile", "miles", 1609.35),
    MeasureType.Length.of("yd", "yard", "yards", 0.9144),
    MeasureType.Length.of("ft", "foot", "feet", 0.3048),
    MeasureType.Length.of("in", "inch", "inches", 0.0254),

    MeasureType.Weight.of("g", "gram", "grams", 1.0),
    MeasureType.Weight.of("kg", "kilogram", "kilograms", 1000.0),
    MeasureType.Weight.of("mg", "milligram", "milligrams", 0.001),
    MeasureType.Weight.of("lb", "pound", "pounds", 453.592),
    MeasureType.Weight.of("oz", "ounce", "ounces", 28.3495),

    MeasureType.Temperature.of("c", "degree Celsius", "degrees Celsius", 0.0, 0.0, "dc", "celsius"),
    MeasureType.Temperature.of(
        "f",
        "degree Fahrenheit",
        "degrees Fahrenheit",
        5 / 9.0 + 32,
        -32.0 * 5 / 9.0,
        "df",
        "fahrenheit"
    ), // one df is (1 -32)* 5/9 dc
    MeasureType.Temperature.of("k", "kelvin", "kelvins", 273.15, -273.15)
)



