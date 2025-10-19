# 🔍 Instrucciones de Debug - AfkZoneEdtools

## ✅ **Correcciones Aplicadas:**

### **1. Error de Partículas Corregido**
- ❌ **Antes**: `Particle.BLOCK` (no existe en Minecraft 1.20.4)
- ✅ **Ahora**: `Particle.CRIT` (compatible con todas las versiones)

### **2. Logs de Debug Agregados**
- ✅ Logs detallados en `BlockManager`
- ✅ Logs detallados en `EdLibIntegrationManager`
- ✅ Debug habilitado por defecto en `config.yml`

## 🚀 **Próximos Pasos:**

### **1. Reemplazar el JAR en tu Servidor**
```bash
# Copia el nuevo JAR compilado
cp AfkZoneEdtools/target/AfkZoneEdtools-1.0.0.jar /ruta/a/tu/servidor/plugins/
```

### **2. Reiniciar el Servidor**
```bash
# Reinicia tu servidor para cargar el nuevo JAR
restart
```

### **3. Probar la Zona**
```bash
# Únete a la zona
/afkz join diamond_mine
```

## 📋 **Logs que Deberías Ver:**

### **Al Cargar el Plugin:**
```
[INFO] AfkZoneEdtools v1.0.0 enabled successfully!
[INFO] Initializing block displays...
[INFO] Creating block display for zone: diamond_mine
[INFO] Location: world:100.0,65.0,100.0
[INFO] Material: DIAMOND_ORE
[INFO] Scale: 1.0
[INFO] EdLibIntegration: Starting block display creation...
[INFO] EdLibIntegration: Location: world:100.0,65.0,100.0
[INFO] EdLibIntegration: Material: DIAMOND_ORE
[INFO] EdLibIntegration: Scale: 1.0
[INFO] EdLibIntegration: EdLibAPI instance: Available
[INFO] EdLibIntegration: Creating transformation matrix...
[INFO] EdLibIntegration: Creating block display entity...
[INFO] EdLibIntegration: Block display entity created successfully
[INFO] EdLibIntegration: Spawning block display...
[INFO] EdLibIntegration: Block display spawned successfully
[INFO] Successfully created block display for zone: diamond_mine
[INFO] Block display entity: Created
```

### **Al Unirse a la Zona:**
```
[INFO] Player NottaBaker joined AFK zone: diamond_mine
[INFO] Creating AFK worker for zone: diamond_mine
```

## 🔧 **Si Sigue Sin Funcionar:**

### **Verifica estos Logs:**
1. **¿Aparece "EdLibAPI instance: NULL"?**
   - Problema: EdLib no se está cargando correctamente
   - Solución: Verificar que EdTools esté instalado y funcionando

2. **¿Aparece "Block display entity is NULL"?**
   - Problema: EdLib no puede crear el bloque
   - Solución: Verificar permisos y versión de EdLib

3. **¿Aparece "Failed to initialize EdLib integration"?**
   - Problema: EdLib no está disponible
   - Solución: Instalar EdTools (que incluye EdLib)

### **Comandos de Verificación:**
```bash
# Verificar que EdTools esté cargado
/plugins

# Verificar logs del servidor
/logs/latest.log
```

## 📝 **Configuración de Zona Actualizada:**

El archivo `zones.yml` ahora incluye límites de zona:

```yaml
zones:
  diamond_mine:
    display_name: "&bDiamond Mine AFK Zone"
    type: "MINING"
    
    # Límites de zona (como EdTools)
    min_corner: "world:95:60:95"
    max_corner: "world:105:70:105"
    
    # Centro donde aparece el bloque
    center_location: "world:100:64:100"
    
    block_material: "DIAMOND_ORE"
    
    # Resto de configuración...
```

## 🎯 **Resultado Esperado:**

1. ✅ **Sin errores de partículas** en la consola
2. ✅ **Bloque de diamante visible** en las coordenadas 100, 65, 100
3. ✅ **Logs detallados** mostrando el proceso de creación
4. ✅ **Funcionamiento normal** de la zona AFK

---

**¡Prueba el plugin actualizado y comparte los logs que aparezcan en la consola!**
