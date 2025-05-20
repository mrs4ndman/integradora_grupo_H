package org.grupo_h.empleados.component;

import org.grupo_h.empleados.dto.PaginaVisitada;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Component
@SessionScope
public class HistorialSesionUsuario {

    private final LinkedList<PaginaVisitada> visitedPages = new LinkedList<>();

    public void addPage(PaginaVisitada page) {
        visitedPages.addFirst(page);
    }

    public List<PaginaVisitada> getVisitedPages() {
        return new LinkedList<>(visitedPages); // Devuelve una copia
    }

    public void clearHistory() {
        visitedPages.clear();
    }
}