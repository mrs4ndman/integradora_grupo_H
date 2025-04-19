const btn_guardar = document.getElementById("btn_guardar")

btn_guardar.addEventListener('click', () => {
    // let mensaje = "Datos Guardados en Base de Datos"
    // if (confirm(mensaje)) { // si se pulsa Aceptar se resetea el formulario
    //     console.log("Operación correcta.")
    // }else{
    //     console.log("Operación cancelada por el usuario.")
    // }
    event.preventDefault();
    alert("Datos guardados en Base de Datos");
});

