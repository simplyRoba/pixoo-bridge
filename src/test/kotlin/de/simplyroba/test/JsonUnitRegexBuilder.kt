package de.simplyroba.test

//Inspired by https://github.com/lukas-krecan/JsonUnit/commit/75d68ef1852ade004e93ca42d676f4b996631974#diff-642a52fede8473f98c5a7b25f34c6bd68f33160cf22733127f7f7f13f3cb2fc6R713
class JsonUnitRegexBuilder private constructor(private val expression: String)  {

  companion object {
    @JvmStatic
    fun regex(): JsonUnitRegexBuilder {
      return JsonUnitRegexBuilder("\"#{json-unit.regex}^")
    }
  }

  fun str(staticString: String): JsonUnitRegexBuilder {
    return JsonUnitRegexBuilder("$expression\\\\Q$staticString\\\\E")
  }

  fun exp(regex: String): JsonUnitRegexBuilder {
    return JsonUnitRegexBuilder(expression + regex)
  }

  override fun toString(): String {
    return "$expression$\""
  }
}