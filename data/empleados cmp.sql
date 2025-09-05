select Empleado,nombrecompleto,documento,fechanacimiento,tipoCMP,GE_Varios.Descripcion as TipoColegio,empleadomast.cmp ,SS_GE_EspecialidadMedico.IdEspecialidad, 
SS_GE_Especialidad.Descripcion as EspecialidadNombre, EstadoEmpleado
from PersonaMast 
left join empleadomast on PersonaMast.persona = empleadomast.empleado
left join GE_Varios on GE_Varios.ValorTexto = empleadomast.tipoCMP and codigotabla = 'COLEGIOCMP'
left join SS_GE_EspecialidadMedico on SS_GE_EspecialidadMedico.IdMedico = PersonaMast.Persona
left join SS_GE_Especialidad on SS_GE_Especialidad.IdEspecialidad = SS_GE_EspecialidadMedico.IdEspecialidad
where EsEmpleado = 's' and PersonaMast.Estado = 'A' and FechaNacimiento is not null

