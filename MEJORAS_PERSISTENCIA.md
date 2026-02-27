# 🚀 Mejoras de Persistencia Implementadas

## 📋 Resumen

Se ha implementado un sistema completo de persistencia local usando **Room Database** siguiendo la arquitectura Clean Architecture existente en el proyecto. Esto soluciona los problemas de pérdida de datos cuando se cierra la aplicación.

## ✅ Problemas Solucionados

### 1. **Pérdida de Sesión al Cerrar la App**
- ❌ **Antes**: Al cerrar la app, el usuario tenía que volver a iniciar sesión
- ✅ **Ahora**: La sesión se mantiene persistente usando Room Database + SharedPreferences

### 2. **Datos de Salas No Persistentes**
- ❌ **Antes**: Si perdías conexión o cerrabas la app, perdías toda la información de la sala
- ✅ **Ahora**: Los datos de las salas se cachean localmente con estrategia offline-first

### 3. **Ranking No Disponible Sin Conexión**
- ❌ **Antes**: El ranking solo se mostraba con conexión al servidor
- ✅ **Ahora**: El ranking se guarda localmente y se sincroniza automáticamente

### 4. **Estado de la App No Se Restaura**
- ❌ **Antes**: La navegación siempre comenzaba en Login
- ✅ **Ahora**: La app recuerda en qué sala estabas y te lleva directamente ahí

## 🏗️ Arquitectura Implementada

### Capas de la Arquitectura Clean (mantenidas):
```
📱 Presentation (ViewModels, Screens, Components)
    ↓
🎯 Domain (UseCases, Entities, Repositories - Interfaces)
    ↓
💾 Data (Repositories Impl, DataSources)
    ├── 🌐 Remote (API, DTOs, Mappers)
    └── 💿 Local (Room Database, DAOs, Entities)
```

## 📦 Componentes Creados

### 1. **Base de Datos Room**
- `QuickScoreDatabase.kt` - Base de datos principal
- **Entidades**:
  - `UserEntity` - Datos del usuario autenticado
  - `RoomEntity` - Información de las salas
  - `RankingEntity` - Ranking de participantes por sala
  - `AppStateEntity` - Estado de navegación de la app

### 2. **DAOs (Data Access Objects)**
- `UserDao` - Operaciones CRUD para usuarios
- `RoomDao` - Operaciones CRUD para salas
- `RankingDao` - Operaciones CRUD para rankings
- `AppStateDao` - Gestión del estado de la aplicación

### 3. **Módulo de Inyección de Dependencias**
- `DatabaseModule.kt` - Provee la base de datos y DAOs con Hilt

### 4. **Repositorios Mejorados**
- `AuthRepositoryImpl` - Ahora guarda usuarios en la BD local
- `RoomsRepositoryImpl` - Implementa estrategia offline-first:
  1. Obtiene datos del caché local (respuesta rápida)
  2. Intenta actualizar desde el servidor en segundo plano
  3. Si falla la red, usa los datos cacheados
  4. Actualiza el caché con los nuevos datos del servidor

### 5. **Nuevos UseCases**
- `GetCurrentUserUseCase` - Verifica si hay sesión activa
- `LogoutUseCase` - Cierra sesión y limpia todos los datos
- `GetAppStateUseCase` - Obtiene el estado guardado para restaurar navegación

### 6. **ViewModels Mejorados**
- `AuthViewModel`:
  - ✅ Verifica automáticamente sesión activa al iniciar
  - ✅ Implementa auto-login si hay token válido
  - ✅ Método logout que limpia todos los datos

- `RoomViewModel`:
  - ✅ Guarda el estado cuando entras a una sala
  - ✅ Permite restaurar la sala si cierras y vuelves a abrir la app
  - ✅ Limpia el estado cuando la sesión termina

## 🎯 Estrategia Offline-First

### Flujo de Datos:
```
1. Usuario solicita datos
   ↓
2. Se muestran datos del caché local (rápido)
   ↓
3. En paralelo, se hace fetch del servidor
   ↓
4. Se actualiza el caché con datos frescos
   ↓
5. Se actualiza la UI automáticamente
```

### Ventajas:
- ⚡ **Respuesta inmediata**: La UI carga instantáneamente con datos cacheados
- 🔌 **Funciona offline**: El usuario puede ver datos aunque no haya internet
- 🔄 **Sincronización transparente**: Cuando recupera conexión, todo se sincroniza
- 💾 **Ahorro de datos**: No descarga todo cada vez, solo actualiza lo necesario

## 📊 Gestión del Estado

### AppStateEntity
Guarda:
- `currentRoomCode`: Código de la última sala activa
- `isInRoom`: Si el usuario está en una sala
- `isHost`: Si el usuario es host o participante
- `lastUpdatedTimestamp`: Cuándo se guardó el estado

Esto permite:
- Restaurar la navegación exactamente donde la dejaste
- Reconectar al WebSocket automáticamente
- Mantener consistencia entre cierres de la app

## 🔐 Seguridad y Privacidad

### Datos Sensibles:
- El token se guarda en **SharedPreferences** (cifrado en Android 6+)
- La base de datos Room está en el almacenamiento privado de la app
- Al cerrar sesión, **todos los datos se eliminan** (BD + SharedPrefs)

### Validación:
- Se valida que el token no esté expirado antes de auto-login
- Si el servidor rechaza el token, se limpia la sesión automáticamente
- Los datos cacheados tienen timestamps para saber si están desactualizados

## 🔄 WebSocket Resiliente

El WebSocketClient ya implementaba reconexión automática:
- ⚡ Reconecta automáticamente si se pierde la conexión
- ⏱️ Espera 5 segundos antes de reintentar
- 🔄 Mantiene intentos hasta reconectar o hasta que el usuario cierre manualmente
- 📡 Emite el estado de conexión para que la UI lo muestre

## 🛠️ Cómo Usar

### Auto-Login (ya implementado)
```kotlin
// En AuthViewModel, automáticamente verifica sesión al iniciar
init {
    checkCurrentSession()
}
```

### Logout
```kotlin
// En cualquier pantalla donde néedites cerrar sesión
authViewModel.logout()
```

### Restaurar Estado de Sala
```kotlin
// El RoomViewModel guarda automáticamente el estado
// Al volver a abrir la app, puedes obtenerlo:
val appState = getAppStateUseCase()
if (appState.isInRoom && appState.currentRoomCode != null) {
    // Navegar a la sala automáticamente
    roomViewModel.initRoom(appState.currentRoomCode)
}
```

## 📦 Dependencias Agregadas

```kotlin
// build.gradle.kts (app)
implementation(libs.androidx.room.runtime)
implementation(libs.androidx.room.ktx)
ksp(libs.androidx.room.compiler)
```

```toml
# gradle/libs.versions.toml
[versions]
room = "2.7.0"

[libraries]
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
```

## 🚀 Próximos Pasos Recomendados

### 1. **Implementar Limpieza Periódica**
Los datos cacheados pueden acumularse. Considera:
- Borrar salas finalizadas después de 7 días
- Limpiar rankings antiguos
- Implementar un `CacheCleanupWorker` con WorkManager

### 2. **Migraciones de BD**
Actualmente usa `.fallbackToDestructiveMigration()` que borra todo al cambiar el schema.
En producción, implementa migraciones apropiadas:
```kotlin
.addMigrations(MIGRATION_1_2, MIGRATION_2_3)
```

### 3. **Exportar Schema**
Room puede exportar el schema de la BD para testing:
```kotlin
android {
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
    }
}
```

### 4. **Testing**
Agregar tests para:
- DAOs (Room tiene utilidades de testing)
- Repositorios con datos mockeados
- UseCases de persistencia

### 5. **Sincronización Avanzada**
Implementar WorkManager para:
- Sincronizar datos en segundo plano
- Manejar cambios de red
- Resolver conflictos de datos

## 📱 Manejo de Casos Edge

### Caso 1: Token Expirado
```kotlin
// En el AuthInterceptor o al hacer requests
if (response.code == 401) {
    // Limpiar sesión
    logoutUseCase()
    // Redirigir a login
}
```

### Caso 2: Datos Muy Antiguos
```kotlin
// En el Repository
val cachedData = dao.getData()
val isStale = System.currentTimeMillis() - cachedData.timestamp > MAX_AGE
if (isStale && networkAvailable) {
    fetchFromServer()
}
```

### Caso 3: Sincronización de Conflictos
```kotlin
// Estrategia: Server Wins
// Si hay conflicto, los datos del servidor siempre prevalecen
val serverData = api.getData()
dao.clearAndInsert(serverData)
```

## 🎉 Resultado Final

Tu aplicación ahora es **resiliente**, **rápida** y **funciona offline**. Los usuarios tendrán una experiencia mucho mejor:

- ✅ No pierden su sesión
- ✅ Los datos se cargan instantáneamente
- ✅ Funciona sin conexión
- ✅ Vuelven exactamente donde estaban
- ✅ El WebSocket se reconecta automáticamente

¡La persistencia está lista y siguiendo las mejores prácticas de arquitectura Android! 🚀
