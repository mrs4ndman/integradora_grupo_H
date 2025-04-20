// *************** FUNCIONES REGISTRO EMPLEADO ***********************

// Vacía el formulario de datos pùlsando el botón vaciar
function vaciarFormulario() {
    // Obtener el formulario
    const formulario = document.getElementById('formulario');

    // Recorrer todos los elementos del formulario
    Array.from(formulario.elements).forEach(function(element) {
        if (element.type === 'text'
            || element.type === 'email'
            || element.type === 'textarea'
            || element.type === 'number') {
            // Vaciar los campos de texto, email y textarea
            element.value = '';
        } else if (element.type === 'checkbox'
            || element.type === 'radio') {
            // Desmarcar los checkboxes y radios
            element.checked = false;
        } else if (element.tagName === 'SELECT') {
            // Vaciar los selects (seleccionar la primera opción por defecto)
            element.selectedIndex = -1;  // Esto los deselecciona todos
        }
    });
}

// Highlight de los botones del NavBar en Registro Empleados
document.addEventListener("DOMContentLoaded", function() {
    let links = document.querySelectorAll("nav ul li a");
    let currentPath = window.location.pathname;
    console.log(currentPath)

    links.forEach(link => {
        window.onload;
        let linkPath = new URL(link.href, window.location.origin).pathname;
        console.log(link)
        // Activa la clase si la URL coincide exactamente o si el currentPath comienza con linkPath
        if (currentPath === linkPath || currentPath.startsWith(linkPath)) {
            link.classList.add("active");
        }
    });
});



