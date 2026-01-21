package uni.zf.xinpian.list

import uni.zf.xinpian.json.model.FilterOption

interface FilterOptionListener {
    fun onFilterOption(key:String, option: FilterOption)
}