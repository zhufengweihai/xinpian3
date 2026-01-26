package uni.zf.xinpian.list

data class FilterOptions(
    var fcatePid: String = "",
    var categoryId: String = "",
    var area: String = "",
    var year: String = "",
    var type: String = "",
    var sort: String = ""
) {
    companion object {
        fun fromQueryString(queryString: String): FilterOptions {
            val options = FilterOptions()

            if (queryString.isEmpty()) {
                return options
            }

            val pairs = queryString.split("&")

            for (pair in pairs) {
                val keyValue = pair.split("=")
                if (keyValue.size >= 2) {
                    val key = keyValue[0]
                    val value = keyValue[1]

                    when (key) {
                        "fcate_pid" -> options.fcatePid = value
                        "category_id" -> options.categoryId = value
                        "area" -> options.area = value
                        "year" -> options.year = value
                        "type" -> options.type = value
                        "sort" -> options.sort = value
                    }
                }
            }

            return options
        }
    }
}