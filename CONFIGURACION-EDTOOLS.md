# 🔧 Configuración de EdTools para AfkZoneEdtools

## 🚨 **Problema Identificado:**

**EdTools requiere que las zonas estén creadas en su sistema antes de poder usarlas.** Nuestro plugin no puede crear zonas independientes - debe conectarse a zonas existentes de EdTools.

## ✅ **Solución:**

### **Paso 1: Crear Zona en EdTools**

Primero, debes crear la zona en EdTools usando su sistema de configuración:

1. **Accede a la configuración de EdTools** (`plugins/EdTools/zones.yml` o similar)
2. **Crea una zona** con el ID que quieres usar (ej: `diamond_mine`)
3. **Configura los límites** de la zona en EdTools
4. **Asegúrate de que la zona esté habilitada**

### **Paso 2: Configurar Nuestro Plugin**

Una vez que la zona existe en EdTools, configura nuestro plugin para usar la misma zona:

```yaml
# zones.yml de nuestro plugin
zones:
  diamond_mine:  # Mismo ID que en EdTools
    display_name: "&bDiamond Mine AFK Zone"
    type: "MINING"
    
    # Límites de zona (deben coincidir con EdTools)
    min_corner: "world:95:60:95"
    max_corner: "world:105:70:105"
    
    # Centro donde aparece el bloque
    center_location: "world:100:64:100"
    
    block_material: "DIAMOND_ORE"
    # ... resto de configuración
```

### **Paso 3: Verificar la Configuración**

Los logs te dirán si la zona existe en EdTools:

```
[INFO] EdToolsIntegration: Attempting to join player NottaBaker to existing EdTools zone: diamond_mine
[INFO] EdToolsIntegration: Player loaded blocks: X
[INFO] EdToolsIntegration: Joined global session for NottaBaker
[INFO] EdToolsIntegration: Player NottaBaker in session: true, zone: diamond_mine
```

Si ves esto:
```
[WARN] EdToolsIntegration: Session creation failed! Zone 'diamond_mine' may not exist in EdTools.
[WARN] EdToolsIntegration: Please create the zone in EdTools first before using this plugin.
```

Significa que la zona no existe en EdTools.

## 🔍 **Cómo Crear Zona en EdTools:**

### **Método 1: Comando en Game**
```
/edtools zone create diamond_mine
/edtools zone set diamond_mine min 95 60 95
/edtools zone set diamond_mine max 105 70 105
/edtools zone enable diamond_mine
```

### **Método 2: Archivo de Configuración**
Editar `plugins/EdTools/zones.yml`:
```yaml
zones:
  diamond_mine:
    enabled: true
    min-corner: "world:95:60:95"
    max-corner: "world:105:70:105"
    # ... otras configuraciones de EdTools
```

## 🎯 **Flujo de Trabajo Correcto:**

1. **Crear zona en EdTools** con ID `diamond_mine`
2. **Configurar límites** en EdTools
3. **Habilitar la zona** en EdTools
4. **Configurar nuestro plugin** para usar la misma zona
5. **Recargar nuestro plugin**: `/afkzone reload`
6. **Probar**: `/afkzone join diamond_mine`

## 🔧 **Comandos de Debug:**

### **Verificar si la zona existe en EdTools:**
```
/afkzone check diamond_mine
```

### **Ver logs detallados:**
Los logs mostrarán si la zona existe y si la sesión se creó correctamente.

## ⚠️ **Importante:**

- **El ID de la zona debe ser exactamente el mismo** en EdTools y en nuestro plugin
- **Los límites deben coincidir** entre EdTools y nuestro plugin
- **La zona debe estar habilitada** en EdTools
- **EdTools debe estar funcionando** correctamente

## 🚀 **Resultado Esperado:**

Una vez configurado correctamente:
- ✅ **Zona existe en EdTools**
- ✅ **Sesión se crea correctamente**
- ✅ **`mineBlockAsPlayer` funciona**
- ✅ **Currency se da correctamente**
- ✅ **Bloque se muestra en el centro**

---

**¡La clave es que EdTools debe conocer la zona antes de que nuestro plugin pueda usarla!**
