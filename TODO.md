# TODO: 
- ModelMapper → 
    - [x] EmpleadoServiceImpl
    - [ ] UsuarioServiceImpl

- [ ] Implementar la restriccion de acceso a aqui sin iniciar sesion con el usuario
## DATOS PRUEBA:
IBAN → NL41RABO7998733292 | ES2000757587483499321899



## GENERAL
- [ ] Desconexión automática por sesión inactiva

- [ ] ModelMapper en todos los DTOs

## EMPLEADOS
- [x] Validación de edad de años transcurridos desde fecha nacimiento

- [ ] Validaciones de contraseña en registro y acceso usuario + Campo de confirmación de contraseña en registro ( PABLO ? )

- [x] Validaciones tarjeta crédito e IBAN. 

- [ ] Endpoint de resetear contraseña con AJAX

- [ ] Recuperación por email (Opcional)

- [ ] Etiquetado/desetiquetado de un empleado a sus subordinados - MVC

- [ ] Etiquetado masivo de un empleado a sus subordinados con trasvase de etiquetas entre selects - MVC



## ADMINISTRACIÓN
- [x] Crear entidad similar a Usuario en Administración

- [x] Endpoint de acceso con relación a la entidad

- [ ] Endpoint y vista que gestione el bloqueo y desbloqueo de usuarios

- [ ] Consulta parametrizada de empleados, con resultados paginados y ordenados por diferentes campos

- [ ] Links a estas 2 funcionalidades


