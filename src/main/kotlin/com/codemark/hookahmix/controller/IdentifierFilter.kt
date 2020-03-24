//package com.codemark.hookahmix.controller
//
//import org.springframework.stereotype.Component
//import java.util.*
//import javax.servlet.Filter
//import javax.servlet.FilterChain
//import javax.servlet.ServletRequest
//import javax.servlet.ServletResponse
//import javax.servlet.http.HttpServletRequest
//import javax.servlet.http.HttpServletResponse
//import javax.servlet.http.HttpSession
//
//@Component
//class IdentifierFilter : Filter {
//
//    override fun doFilter(servletRequest: ServletRequest,
//                          servletResponse: ServletResponse,
//                          filterChain: FilterChain) {
//
//        var request: HttpServletRequest = servletRequest as HttpServletRequest
//        var response: HttpServletResponse = servletResponse as HttpServletResponse
//
//        var installationCookie = ""
//
//        if (request.getHeader("X-UserId") != null) {
//            installationCookie = request.getHeader("X-UserId")
//        } else {
//            println("Result not found")
//        }
//
//        filterChain.doFilter(request, response)
//
//    }
//}