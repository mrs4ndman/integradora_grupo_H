package org.grupo_h.comun.auxiliar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class RestPage<T> extends PageImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestPage(@JsonProperty("content") List<T> content,
                    @JsonProperty("number") int number, // número de página actual
                    @JsonProperty("size") int size,     // tamaño de la página
                    @JsonProperty("totalElements") long totalElements,
                    @JsonProperty("pageable") JsonNode pageableNode, // Recibir 'pageable' como un nodo genérico
                    @JsonProperty("last") boolean last,
                    @JsonProperty("totalPages") int totalPages,
                    @JsonProperty("sort") JsonNode sortNode, // Recibir 'sort' como un nodo genérico
                    @JsonProperty("first") boolean first,
                    @JsonProperty("numberOfElements") int numberOfElements,
                    @JsonProperty("empty") boolean empty) { // Añadir 'empty'

        super(content, PageRequest.of(number, size, Sort.by(extractOrders(sortNode))), totalElements);
    }

    public RestPage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public RestPage(List<T> content) {
        super(content);
    }

    // Método auxiliar para extraer las órdenes de Sort del JsonNode
    private static List<Sort.Order> extractOrders(JsonNode sortNode) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sortNode != null && sortNode.isArray()) {
            for (JsonNode orderNode : sortNode) {
                String property = orderNode.has("property") ? orderNode.get("property").asText() : null;
                String direction = orderNode.has("direction") ? orderNode.get("direction").asText("ASC") : "ASC";
                if (property != null && !property.isEmpty()) {
                    orders.add(new Sort.Order(Sort.Direction.fromString(direction), property));
                }
            }
        }
        return orders;
    }
}
