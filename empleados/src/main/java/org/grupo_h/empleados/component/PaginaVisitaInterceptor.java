package org.grupo_h.empleados.component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.grupo_h.empleados.dto.PaginaVisitada;
import org.grupo_h.empleados.component.HistorialSesionUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
@Component
public class PaginaVisitaInterceptor implements HandlerInterceptor {

    @Autowired
    private HistorialSesionUsuario historialSesionUsuario;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if ("GET".equalsIgnoreCase(request.getMethod()) && modelAndView != null &&
                !isRedirectView(modelAndView) && response.getStatus() < 400 && isHtmlView(modelAndView)) {

            String requestURI = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null && !queryString.isEmpty()) {
                requestURI += "?" + queryString;
            }

            // Filtrar URLs no deseadas
            if (!requestURI.startsWith("/css") && !requestURI.startsWith("/js") &&
                    !requestURI.startsWith("/images") && !requestURI.startsWith("/api") &&
                    !requestURI.startsWith("/error") && !requestURI.equals("/usuarios/inicio-sesion/**") &&
                    !requestURI.endsWith("/usuarios/logout") && !requestURI.contains("favicon.ico") &&
                    !requestURI.equals("/empleados/historial")) {

                historialSesionUsuario.addPage(new PaginaVisitada(requestURI));
            }
        }
    }

    private boolean isRedirectView(ModelAndView modelAndView) {
        String viewName = modelAndView.getViewName();
        return viewName != null && viewName.startsWith("redirect:");
    }

    private boolean isHtmlView(ModelAndView modelAndView) {
        return modelAndView.hasView() || (modelAndView.getViewName() != null && !modelAndView.getViewName().isEmpty());
    }
}