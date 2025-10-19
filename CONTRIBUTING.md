# Guía de Contribución

¡Gracias por tu interés en contribuir a AfkZoneEdtools! Este documento proporciona directrices para contribuir al proyecto.

## 🚀 Cómo Contribuir

### Reportar Problemas

Si encuentras un bug o tienes una sugerencia:

1. Verifica que el problema no haya sido reportado ya
2. Usa el template de issue correspondiente
3. Proporciona información detallada sobre el problema
4. Incluye logs y pasos para reproducir el error

### Enviar Pull Requests

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Estándares de Código

### Java
- Usa Java 21
- Sigue las convenciones de naming de Java
- Comenta el código complejo
- Usa JavaDoc para métodos públicos

### Estructura del Proyecto
```
src/main/java/
├── gz/devian/afkzoneedtools/
│   ├── AfkZoneEdtools.java          # Clase principal
│   ├── commands/                    # Comandos del plugin
│   ├── listeners/                   # Event listeners
│   ├── managers/                    # Gestores del plugin
│   ├── models/                      # Modelos de datos
│   ├── placeholders/                # Integración con PlaceholderAPI
│   └── utils/                       # Utilidades
```

### Configuración
- Mantén la compatibilidad con EdTools
- Usa configuraciones sensatas por defecto
- Documenta nuevas configuraciones

## 🧪 Testing

Antes de enviar un PR:

1. Compila el proyecto sin errores
2. Prueba las funcionalidades básicas
3. Verifica que no rompas compatibilidad existente
4. Actualiza la documentación si es necesario

## 📚 Documentación

- Actualiza el README.md si agregas nuevas funcionalidades
- Documenta nuevos placeholders
- Actualiza los ejemplos de configuración
- Agrega comentarios en el código complejo

## 🎯 Tipos de Contribuciones

### Bugs
- Fixes de bugs existentes
- Mejoras de rendimiento
- Correcciones de memoria

### Features
- Nuevas funcionalidades
- Mejoras de UI/UX
- Nuevos placeholders
- Integraciones adicionales

### Documentación
- Mejoras al README
- Ejemplos de uso
- Guías de configuración
- Traducciones

## 🔍 Proceso de Review

1. **Automático**: GitHub Actions ejecuta tests y build
2. **Manual**: Maintainers revisan el código
3. **Feedback**: Se proporciona feedback constructivo
4. **Iteración**: Se hacen cambios según sea necesario
5. **Merge**: Una vez aprobado, se mergea al main

## 📋 Checklist para PRs

- [ ] Código compila sin errores
- [ ] Tests pasan (si aplica)
- [ ] Documentación actualizada
- [ ] No breaking changes (o documentados)
- [ ] Commit messages descriptivos
- [ ] PR tiene descripción clara

## 🤝 Comportamiento

- Sé respetuoso y constructivo
- Mantén las discusiones enfocadas en el código
- Ayuda a otros contribuidores
- Sigue las directrices de la comunidad de GitHub

## 📞 Contacto

Si tienes preguntas sobre contribuir:

- Abre un issue para preguntas generales
- Usa GitHub Discussions para debates
- Contacta a los maintainers directamente si es necesario

¡Gracias por contribuir a AfkZoneEdtools! 🎉
