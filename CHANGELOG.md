# Changelog

Todos los cambios notables a este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-01-19

### Added
- ✨ **Funcionalidad principal**: Sistema completo de zonas AFK para EdTools
- 🎯 **Integración total con EdTools**: Soporte completo para enchants, boosters, currencies, lucky blocks y leveling
- 🖱️ **Sistema de click**: Activación/desactivación de AFK haciendo click en el bloque central
- 🚶 **Detección de movimiento**: Desactivación automática al salir de la zona
- 🛡️ **Protección de bloques**: Los bloques centrales no se pueden destruir con herramientas normales
- 🎨 **Block displays**: Visualización de bloques flotantes usando EdLib
- ✨ **Efectos visuales**: Partículas y sonidos durante el mining automático
- 📊 **Soporte para PlaceholderAPI**: 15+ placeholders para integración con otros plugins
- 🔧 **Sistema de comandos**: Comandos completos para gestión de zonas AFK
- ⚙️ **Configuración flexible**: Sistema de configuración completo con zones.yml
- 🎯 **Múltiples tipos de zona**: Soporte para MINING, FARMING y CUSTOM
- 🛠️ **Herramientas configurable**: Sistema de allowed_tools personalizable
- 💰 **Sistema de recompensas**: Integración completa con el sistema de currencies de EdTools
- 🎲 **Lucky blocks**: Activación de lucky blocks durante el AFK
- 📈 **Sistema de experiencia**: Contribución al sistema de leveling de EdTools
- 🔄 **Regeneración de bloques**: Sistema de regeneración automática configurable
- 🎭 **Animaciones**: Efectos de mining para mejor experiencia visual
- 📋 **Sistema de workers**: Gestión avanzada de workers AFK
- 🔍 **Sistema de debug**: Logs detallados para troubleshooting
- 🚀 **Optimización**: Operaciones asíncronas para evitar lag del servidor

### Technical
- 🏗️ **Arquitectura modular**: Sistema de managers separados por funcionalidad
- 🔗 **Integración con APIs**: Uso completo de EdToolsAPI y EdLibAPI
- 🎯 **Reflexión**: Implementación de PlaceholderAPI usando reflexión para evitar dependencias
- 📦 **Maven**: Sistema de build con Maven y configuración completa
- 🧪 **Compatibilidad**: Soporte para Minecraft 1.20.4+ y Java 21
- 📝 **Documentación**: README completo con ejemplos y guías de uso
- 🔧 **CI/CD**: GitHub Actions para build automático y releases
- 📄 **Licencia**: Licencia MIT para uso libre
- 🤝 **Contribuciones**: Guía completa de contribución al proyecto

### Commands
- `/afkzone join <zona>` - Unirse a una zona AFK
- `/afkzone leave` - Salir de la zona AFK actual
- `/afkzone list` - Listar todas las zonas disponibles
- `/afkzone check <zona>` - Verificar ubicación en zona
- `/afkzone reload` - Recargar configuración

### Permissions
- `afkzone.use` - Usar zonas AFK (default: true)
- `afkzone.click` - Hacer click en bloques centrales (default: true)
- `afkzone.check` - Verificar ubicación en zonas (default: true)
- `afkzone.admin` - Acceso completo a comandos (default: op)

### Placeholders
- `%afkzone_active%` - Estado del worker AFK
- `%afkzone_zone%` - Nombre de la zona actual
- `%afkzone_zone_id%` - ID de la zona actual
- `%afkzone_harvests%` - Total de cosechas
- `%afkzone_material%` - Material siendo minado
- `%afkzone_tool%` - Herramienta actual
- `%afkzone_currency%` - Tipo de moneda
- `%afkzone_reward%` - Cantidad de recompensa
- `%afkzone_time_left%` - Tiempo hasta próxima cosecha
- `%afkzone_workers_total%` - Total de workers activos
- `%afkzone_workers_zone%` - Workers en zona actual
- `%afkzone_zones_total%` - Total de zonas cargadas
- `%afkzone_enabled%` - Estado global de zonas AFK

---

## [Unreleased]

### Planned Features
- 🌍 **Soporte multi-mundo**: Zonas AFK en diferentes mundos
- 🎨 **Temas visuales**: Diferentes estilos de block displays
- 📊 **Estadísticas avanzadas**: Sistema de métricas detalladas
- 🔄 **Auto-save**: Guardado automático de progreso
- 🎯 **Zonas dinámicas**: Creación de zonas desde comandos
- 🌐 **API pública**: API para desarrolladores de terceros
- 📱 **Integración web**: Panel web para gestión de zonas
- 🎮 **GUI mejorada**: Interfaz gráfica para gestión de zonas
- 🔔 **Notificaciones**: Sistema de notificaciones avanzado
- 🌍 **Multiidioma**: Soporte para múltiples idiomas

---

**Nota**: Este proyecto fue desarrollado con la ayuda de Codella.ia para la generación automática de código y estructura del proyecto.