# AfkZoneEdtools

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.4-green.svg)](https://minecraft.net)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net)
[![EdTools](https://img.shields.io/badge/EdTools-Addon-blue.svg)](https://edtools.com)
[![PlaceholderAPI](https://img.shields.io/badge/PlaceholderAPI-Supported-yellow.svg)](https://github.com/PlaceholderAPI/PlaceholderAPI)

**AfkZoneEdtools** es un addon avanzado para EdTools que permite a los jugadores crear zonas AFK (Away From Keyboard) donde pueden obtener recursos automáticamente mientras están inactivos, funcionando exactamente como las zonas normales de EdTools pero de forma automatizada.

> 🚀 **Desarrollado con Codella.ia** - Este plugin fue creado utilizando la inteligencia artificial de Codella.ia para la generación automática de código y estructura del proyecto.

## 🚀 Características

### ✨ Funcionalidades Principales
- **Zonas AFK Completas**: Crea zonas donde los jugadores pueden obtener recursos automáticamente
- **Integración Total con EdTools**: Funciona exactamente como las zonas normales de EdTools
- **Click para Activar**: Los jugadores pueden activar/desactivar el auto-mining haciendo click en el bloque central
- **Salida Automática**: El auto-mining se desactiva automáticamente cuando el jugador sale de la zona
- **Bloques Protegidos**: El bloque central no se puede destruir con herramientas normales
- **Soporte Completo de EdTools**: Incluye enchants, boosters, currencies, lucky blocks, y leveling

### 🎯 Integración con EdTools
- **Mining/Farming Automático**: Simula la minería/farmeo real usando `mineBlockAsPlayer()`
- **Enchants de Herramientas**: Los enchants se activan automáticamente durante el AFK
- **Sistema de Boosters**: Afectado por todos los boosters de EdTools
- **Múltiples Currencies**: Soporte completo para el sistema de monedas de EdTools
- **Lucky Blocks**: Los lucky blocks se pueden activar durante el AFK
- **Sistema de Niveles**: Contribuye al sistema de experiencia de EdTools

### 🎨 Características Visuales
- **Block Displays**: Usa EdLib para mostrar bloques flotantes en el centro de las zonas
- **Efectos Visuales**: Partículas y sonidos durante el mining automático
- **Animaciones**: Efectos de romper bloques para una mejor experiencia visual

### 🔧 Soporte para PlaceholderAPI
- **Placeholders Completos**: Más de 15 placeholders disponibles
- **Información en Tiempo Real**: Estadísticas actualizadas del estado del jugador y las zonas
- **Compatibilidad Total**: Funciona con scoreboards, hologramas, y otros plugins

## 📋 Requisitos

### Dependencias Obligatorias
- **Minecraft**: 1.20.4+
- **Java**: 21+
- **EdTools**: Plugin principal requerido
- **EdLib**: Incluido en EdTools

### Dependencias Opcionales
- **PlaceholderAPI**: Para soporte de placeholders
- **Paper/Spigot**: Servidor compatible

## 🛠️ Instalación

1. **Descarga** el plugin desde [Releases](https://github.com/MarcianoDeHolanda/AfkZoneEdtools/releases)
2. **Coloca** el archivo JAR en tu carpeta `plugins/`
3. **Asegúrate** de que EdTools esté instalado y configurado
4. **Reinicia** el servidor
5. **Configura** las zonas AFK en `plugins/AfkZoneEdtools/zones.yml`

## ⚙️ Configuración

### 1. Crear Zonas en EdTools

**IMPORTANTE**: Antes de usar este plugin, debes crear las zonas en EdTools primero:

```bash
# Crear zona
/edtools zone create afk-zone

# Definir límites
/edtools zone set afk-zone min 14 71 136
/edtools zone set afk-zone max 20 73 142

# Habilitar zona
/edtools zone enable afk-zone
```

### 2. Configurar zones.yml

```yaml
zones:
  afk-zone:
    display_name: "&bDiamond Mine AFK Zone"
    type: "MINING"
    
    # Límites de la zona (deben coincidir con EdTools)
    min_corner: "world:14:73:142"
    max_corner: "world:20:71:136"
    center_location: "world:17:73:139"
    
    # Configuración del bloque
    block_material: "OAK_WOOD"
    allowed_tools:
      - "axe-tool"
    
    # Configuración de timing
    harvest_interval: 5000  # 5 segundos
    regeneration_time: 3000 # 3 segundos
    
    # Recompensas
    reward_currency: "savia"
    base_reward: 100
    
    # Integración con EdTools
    edtools_integration:
      affect_enchants: true
      affect_sell: true
      affect_block_currencies: true
      affect_lucky_blocks: true
      use_global_session: true
      apply_boosters: true
      grant_experience: true
    
    enabled: true
```

## 🎮 Comandos

| Comando | Descripción | Permisos |
|---------|-------------|----------|
| `/afkzone join <zona>` | Unirse a una zona AFK | `afkzone.use` |
| `/afkzone leave` | Salir de la zona AFK actual | `afkzone.use` |
| `/afkzone list` | Listar todas las zonas disponibles | `afkzone.use` |
| `/afkzone check <zona>` | Verificar si estás dentro de una zona | `afkzone.check` |
| `/afkzone reload` | Recargar configuración | `afkzone.reload` |

## 🔐 Permisos

| Permiso | Descripción | Default |
|---------|-------------|---------|
| `afkzone.use` | Usar zonas AFK | `true` |
| `afkzone.click` | Hacer click en bloques centrales | `true` |
| `afkzone.check` | Verificar ubicación en zonas | `true` |
| `afkzone.admin` | Acceso completo a comandos | `op` |

## 📊 Placeholders

### Información del Jugador
- `%afkzone_active%` - Si el jugador tiene un worker AFK activo
- `%afkzone_zone%` - Nombre de la zona AFK actual
- `%afkzone_zone_id%` - ID de la zona AFK actual
- `%afkzone_harvests%` - Total de cosechas/minas
- `%afkzone_material%` - Material siendo minado
- `%afkzone_tool%` - Herramienta actual
- `%afkzone_currency%` - Tipo de moneda
- `%afkzone_reward%` - Cantidad base de recompensa

### Información de Tiempo
- `%afkzone_time_left%` - Tiempo hasta próxima cosecha (segundos)

### Información Global
- `%afkzone_workers_total%` - Total de workers activos
- `%afkzone_workers_zone%` - Workers en la zona actual
- `%afkzone_zones_total%` - Total de zonas cargadas
- `%afkzone_enabled%` - Si las zonas AFK están habilitadas

## 🎯 Cómo Usar

### Para Jugadores

1. **Ve a una zona AFK** configurada
2. **Asegúrate de tener** la herramienta correcta (configurada en `allowed_tools`)
3. **Haz click** en el bloque flotante del centro con tu herramienta
4. **¡Listo!** Comenzarás a minar/farmear automáticamente
5. **Para parar**: Haz click nuevamente en el bloque o sal de la zona

### Para Administradores

1. **Crea la zona en EdTools** primero
2. **Configura** la zona en `zones.yml`
3. **Coloca bloques físicos** en la zona para que EdTools los reconozca
4. **Recarga** el plugin con `/afkzone reload`
5. **Prueba** la zona

## 🔧 Solución de Problemas

### El bloque no aparece
- Verifica que EdTools esté funcionando correctamente
- Asegúrate de que la zona existe en EdTools
- Revisa las coordenadas en `zones.yml`

### No se obtienen recursos
- Verifica que hay bloques físicos en la zona de EdTools
- Asegúrate de que la herramienta esté en la lista `allowed_tools`
- Revisa los logs para errores de integración

### El click no funciona
- Verifica permisos (`afkzone.click`)
- Asegúrate de tener la herramienta correcta en la mano
- Revisa que las coordenadas del `center_location` coincidan exactamente

## 📈 Rendimiento

- **Optimizado**: Usa operaciones asíncronas para evitar lag
- **Eficiente**: Solo procesa workers activos
- **Escalable**: Soporta múltiples zonas y workers simultáneos
- **Configurable**: Intervalos de cosecha personalizables

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Por favor:

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

## 🙏 Agradecimientos

- **Codella.ia** - Por la generación automática del código y estructura del proyecto
- **EdTools Team** - Por el excelente plugin base
- **EdLib Team** - Por las APIs de entidades y efectos
- **PlaceholderAPI Team** - Por el sistema de placeholders
- **Comunidad de Minecraft** - Por el feedback y soporte

## License

- **GitHub Issues**: [Reportar problemas](https://github.com/MarcianoDeHolanda/AfkZoneEdtools/issues)
- **Discord**: [Servidor de soporte](nottabaker)
- **Wiki**: [Documentación completa](https://github.com/MarcianoDeHolanda/AfkZoneEdtools/wiki)

---

**Desarrollado por**: [MarcianoDeHolanda](https://github.com/MarcianoDeHolanda)  
**Colaborador**: [Claude Sonnet 4](https://github.com/ClaudeSonnet4)  
**Versión**: 1.0.0  
**Última actualización**: Enero 2025