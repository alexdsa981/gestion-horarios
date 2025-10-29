-- Cambiar el nombre del rol "General" a "Gestor"
UPDATE [horarios].[dbo].[rol_usuario]
SET [nombre] = 'Gestor'
WHERE [nombre] = 'General';

-- Agregar el nuevo rol "Colaborador" si no existe aún
IF NOT EXISTS (
    SELECT 1 FROM [horarios].[dbo].[rol_usuario] WHERE [nombre] = 'Colaborador'
)
BEGIN
    INSERT INTO [horarios].[dbo].[rol_usuario] ([nombre])
    VALUES ('Colaborador');
END