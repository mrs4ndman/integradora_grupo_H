// *************** FUNCIONES COMUNES ***********************

/**
 * Muestra un diálogo de confirmación antes de realizar una acción.
 * @param elemento - El elemento que disparó el evento.
 * @param mensaje - El mensaje de confirmación.
 * @returns boolean - true si el usuario confirma, false si cancela.
 */
function confirmarAccionFormularioRegistroEmpleado(elemento, mensaje) {
    if (confirm(mensaje)) {
        return true;
    } else {
        return false;
    }
}

// *************** FUNCIONES REGISTRO EMPLEADO ***********************

// Vacía el formulario de datos pùlsando el botón vaciar
function vaciarFormulario() {
    // Obtener el formulario
    const formulario = document.getElementById('formulario');

    // Recorrer todos los elementos del formulario
    Array.from(formulario.elements).forEach(function (element) {
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

// Resetea el formulario en cada paso del registro
function resetFormulario() {
    document.getElementById("formulario").reset();
}


// Selecciona el radio del Género Femenino
function seleccionarGeneroF() {
    const radio = document.querySelector('input[type="radio"][name="generoSeleccionadoDTO.codigoGenero"][value="F"]');
    if (radio) {
        radio.checked = true;
    }
}

// Selecciona todos los radios del Género
function deseleccionaTodosGeneros() {
    const radios = document.querySelectorAll('input[type="radio"][name="generoSeleccionadoDTO.codigoGenero"]');
    radios.forEach(r => r.checked = false);
}


// Selecciona Todas las especialidades
function seleccionaTodosCheckboxes() {
    const radios = document.querySelectorAll('input[type="checkbox"]');
    radios.forEach(r => r.checked = true);
}

// Deselecciona Todas las especialidades
function deseleccionaTodosCheckboxes() {
    const radios = document.querySelectorAll('input[type="checkbox"]');
    radios.forEach(r => r.checked = false);
}

function alternarEstilos() {
    const checkbox = document.getElementById('quitaEstilos');
    const estilo = document.getElementById('estilosCss');

    if (checkbox.checked) {
        estilo.disabled = true; // Quita estilos
    } else {
        estilo.disabled = false; // Vuelve a aplicar los estilos
    }
}


// Validar contraseñas en registro
function validarRegistro() {
    const contrasena = document.getElementById('contrasena');
    const confirmarContrasena = document.getElementById('confirmarContrasena');
    const errorDiv = document.getElementById('passwordError');

    if (contrasena.value !== confirmarContrasena.value) {
        // Muestra el mensaje de error
        if (errorDiv) {
            errorDiv.style.display = 'block';
        }
        // Añade un borde rojo para indicar el error (opcional)
        confirmarContrasena.style.borderColor = 'red';
        contrasena.style.borderColor = 'red';
        // Enfoca el campo de confirmación
        confirmarContrasena.focus();
        // Evita que el formulario se envíe
        return false;
    } else {
        // Oculta el mensaje de error si las contraseñas coinciden
        if (errorDiv) {
            errorDiv.style.display = 'none';
        }
        // Restablece el borde (opcional)
        confirmarContrasena.style.borderColor = '';
        contrasena.style.borderColor = '';
        // Permite que el formulario continúe (se llamará a confirmarAccionFormulario)
        return true;
    }
}

// Highlight de los botones del NavBar en Registro Empleados
document.addEventListener("DOMContentLoaded", function () {
    let links = document.querySelectorAll("nav ul li a");
    let tipoDocumento = document.getElementById('tipoDocumento');
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

    if (tipoDocumento) {
        tipoDocumento.checked = true;
    }
});

function mostrarContraseña(buttonElement) {
    const wrapper = buttonElement.closest('.password-wrapper');
    if (!wrapper) {
        console.error("Contenedor .password-wrapper no encontrado.");
        return;
    }

    const passwordInput = wrapper.querySelector('.clase-password-objetivo');
    const icon = buttonElement.querySelector('i');

    if (!passwordInput) {
        console.error("Input .clase-password-objetivo no encontrado en el wrapper.");
        return;
    }
    if (!icon) {
        console.error("Icono <i> no encontrado en el botón.");
        return;
    }

    if (passwordInput.type === "password") {
        passwordInput.type = "text";
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
        buttonElement.setAttribute('aria-label', 'Ocultar contraseña');
    } else {
        passwordInput.type = "password";
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
        buttonElement.setAttribute('aria-label', 'Mostrar contraseña');
    }
}