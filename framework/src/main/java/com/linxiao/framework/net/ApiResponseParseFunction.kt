package com.linxiao.framework.net

import com.linxiao.framework.json.GsonParser.parser
import io.reactivex.functions.Function
import java.lang.reflect.Type

/**
 * flatMap function to parse data in ApiResponse to data object
 *
 * @author linxiao
 * @since 2018-08-20
 */
@Suppress("UNCHECKED_CAST")
class ApiResponseParseFunction<T> : Function<ApiResponse, T> {
    private val type: Type

    constructor() {
        type = Any::class.java
    }

    constructor(clazz: Class<T>) {
        type = clazz
    }

    constructor(type: Type) {
        this.type = type
    }

    @Throws(Exception::class)
    override fun apply(apiResponse: ApiResponse): T {
        if (type === Any::class.java) {
            return (if (apiResponse.data == null) "" else apiResponse.data) as T
        }
        return parser.fromJson(apiResponse.data, type) ?: throw ApiException(apiResponse)
    }
}