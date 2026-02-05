-- Inserts de ejemplo para la tabla tasks (PostgreSQL).
-- Ejecutar manualmente si quieres datos de prueba sin reiniciar la app,
-- o en otro entorno (pgAdmin, psql, etc.).
-- La app ya carga estos datos al arrancar si la tabla está vacía (DataLoader).

-- Opción 1: Con IDs fijos (útil si vacías la tabla y quieres datos conocidos)
/*
INSERT INTO tasks (id, title, description, completed, created_at, updated_at)
VALUES
  (1, 'Revisar documentación del API', 'Leer OpenAPI y probar endpoints con Swagger UI', false, NOW(), NOW()),
  (2, 'Implementar tests de integración', 'Añadir pruebas con Testcontainers y MockMvc', true, NOW() - INTERVAL '2 days', NOW() - INTERVAL '1 day'),
  (3, 'Configurar CI en GitHub Actions', 'Workflow para build y tests en push/PR', true, NOW() - INTERVAL '5 days', NOW() - INTERVAL '3 days'),
  (4, 'Desplegar en entorno de staging', 'Preparar deploy con Docker o plataforma cloud', false, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
  (5, 'Refactorizar capa de servicios', 'Extraer lógica común y mejorar manejo de errores', false, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
*/
-- Opción 2: Sin especificar ID (deja que la secuencia asigne)
-- Descomenta si prefieres no tocar la secuencia de IDs:
INSERT INTO tasks (title, description, completed, created_at, updated_at)
VALUES
  ('Revisar documentación del API', 'Leer OpenAPI y probar endpoints con Swagger UI', false, NOW(), NOW()),
  ('Implementar tests de integración', 'Añadir pruebas con Testcontainers y MockMvc', true, NOW() - INTERVAL '2 days', NOW() - INTERVAL '1 day'),
  ('Configurar CI en GitHub Actions', 'Workflow para build y tests en push/PR', true, NOW() - INTERVAL '5 days', NOW() - INTERVAL '3 days'),
  ('Desplegar en entorno de staging', 'Preparar deploy con Docker o plataforma cloud', false, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
  ('Refactorizar capa de servicios', 'Extraer lógica común y mejorar manejo de errores', false, NOW(), NOW());