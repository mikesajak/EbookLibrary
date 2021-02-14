package com.mikesajak.ebooklibrary.controller

import org.slf4j.LoggerFactory
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LoggingInterceptor : HandlerInterceptorAdapter() {
    private val logger = LoggerFactory.getLogger(LoggingInterceptor::class.java)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        logger.trace("[preHandle][$request][${request.method}] ${request.requestURI}${getParameters(request)}")
        return super.preHandle(request, response, handler)
    }

    private fun getParameters(request: HttpServletRequest): String? {
        val paramsStr = StringBuffer()
        val paramsEnumeration: Enumeration<*>? = request.parameterNames

        if (paramsEnumeration != null) {
            paramsStr.append(paramsEnumeration.toList()
                .map { it as String }
                .map { elem -> "$elem=" + getParamStr(elem, request) }
                .joinToString("&", "?", ""))
        }
        val ip = request.getHeader("X-FORWARDED-FOR")
        val ipAddr: String? = ip ?: getRemoteAddr(request)
        if (ipAddr != null && ipAddr.isNotBlank()) {
            paramsStr.append("&_psip=$ipAddr")
        }
        return paramsStr.toString()
    }

    private fun getParamStr(paramName: String, request: HttpServletRequest) =
        if (paramName.contains("password") || paramName.contains("pass") || paramName.contains("pwd")) "*****"
        else request.getParameter(paramName)

    private fun getRemoteAddr(request: HttpServletRequest): String? {
        val ipFromHeader = request.getHeader("X-FORWARDED-FOR")
        if (ipFromHeader != null && ipFromHeader.isNotEmpty()) {
            logger.debug("ip from proxy - X-FORWARDED-FOR : $ipFromHeader")
            return ipFromHeader
        }
        return request.remoteAddr
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        logger.trace("[postHandle][$request]")
        super.postHandle(request, response, handler, modelAndView)
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        val exMsg = if (ex != null) {
            ex.printStackTrace();
            "[exception: $ex]"
        } else ""
        logger.debug("[afterCompletion][$request]$exMsg");
        super.afterCompletion(request, response, handler, ex)
    }
}