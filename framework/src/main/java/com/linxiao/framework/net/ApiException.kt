package com.linxiao.framework.net

import java.io.IOException

/**
 * api response exception type
 * 在请求API返回code非success的时候返回
 *
 * @author lx8421bcd
 * @since 2016-07-27
 */
class ApiException(val response: ApiResponse) : IOException("(" + response.code + ")")
