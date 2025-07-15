# ARQUITECTURA DE MICROSERVICIOS RabbitMQ

## REQUERIMIENTOS SOLICITADOS
### Requerimiento 2: "Dos microservicios para leer mensajes, uno por cada cola"  
- **CUMPLIDO:** VentaConsumer (cola ventas) + PromocionConsumer (cola promociones)

### Requerimiento 3: "El microservicio consumidor de mensajes de promociones debía generar un archivo JSON"
- **CUMPLIDO:** PromocionConsumer genera archivos JSON únicos en `promociones_json/`
- **Formato:** `promocion_yyyyMMdd_HHmmss.json`
- **Contenido:** JSON pretty-printed de la promoción recibida

---

## CAMBIOS REALIZADOS PARA CUMPLIR REQUERIMIENTOS

### Eliminación de Productores Adicionales
- Se eliminaron los métodos `enviarMensajeVentas()` y `enviarMensajePromocion()` del `MensajeServiceImpl`
- Se eliminaron los endpoints `/mensajes/ventas` y `/mensajes/promociones` del `MensajeController`
- **RESULTADO:** Ahora solo existen exactamente 2 productores para las colas principales

### Productores Únicos Confirmados
- **RabbitProducerService** - ÚNICO productor para cola "ventas"
- **PromocionRabbitProducer** - ÚNICO productor para cola "cola.promociones"

### Consumidores Únicos Confirmados  
- **VentaConsumer** - ÚNICO consumidor para cola "ventas"
- **PromocionConsumer** - ÚNICO consumidor para cola "cola.promociones" + genera archivos JSON

---

## CONFIGURACIÓN DE COLAS

**RabbitMQConfig:** `src/main/java/cl/duoc/azuread/ejemplo/config/RabbitMQConfig.java`

- **Cola "ventas"** - Para mensajes de ventas
- **Cola "cola.promociones"** - Para mensajes de promociones
- **Cola "mensajes.colas2"** - Para mensajes generales/admin
- **Cola "dlx-queue"** - Para mensajes fallidos (Dead Letter Queue)1:** "Dos microservicios que generaran mensajes, uno por cada cola"  
**REQUERIMIENTO 2:** "Dos microservicios para leer mensajes, uno por cada cola"  
**REQUERIMIENTO 3:** "El microservicio consumidor de mensajes de promociones debía generar un archivo JSON"  

---

## ARQUITECTURA IMPLEMENTADA

### MICROSERVICIOS PRODUCTORES (2 en total)

#### 1. **RabbitProducerService** - PRODUCTOR 1
- **Ubicación:** `src/main/java/cl/duoc/azuread/ejemplo/service/RabbitProducerService.java`
- **Función:** Envía mensajes a la cola **"ventas"**
- **Tipo de mensaje:** VentaDTO (objetos de venta)
- **Utilizado por:** VentaController al registrar nuevas ventas

#### 2. **PromocionRabbitProducer** - PRODUCTOR 2  
- **Ubicación:** `src/main/java/cl/duoc/azuread/ejemplo/service/PromocionRabbitProducer.java`
- **Función:** Envía mensajes a la cola **"cola.promociones"**
- **Tipo de mensaje:** Promocion (objetos de promoción en JSON)
- **Utilizado por:** PromocionController al crear nuevas promociones

---

### MICROSERVICIOS CONSUMIDORES (2 en total)

#### 1. **VentaConsumer** - CONSUMIDOR 1
- **Ubicación:** `src/main/java/cl/duoc/azuread/ejemplo/listener/VentaConsumer.java`  
- **Función:** Consume mensajes de la cola **"ventas"**
- **Procesamiento:** 
  - Recibe VentaDTO desde RabbitProducerService
  - Convierte DTO a entidad Venta
  - Guarda la venta en la base de datos Oracle

#### 2. **PromocionConsumer** - CONSUMIDOR 2 
- **Ubicación:** `src/main/java/cl/duoc/azuread/ejemplo/listener/PromocionConsumer.java`
- **Función:** Consume mensajes de la cola **"cola.promociones"**  
- **Procesamiento:**
  - Recibe Promocion JSON desde PromocionRabbitProducer
  - Guarda la promoción en la base de datos Oracle
  - **GENERA ARCHIVO JSON** (requerimiento específico cumplido)
  - Los archivos se guardan en directorio `promociones_json/`

---

## FLUJO DE DATOS

### Flujo de Ventas:
```
VentaController → RabbitProducerService → Cola "ventas" → VentaConsumer → Base de Datos
```

### Flujo de Promociones:
```
PromocionController → PromocionRabbitProducer → Cola "cola.promociones" → PromocionConsumer → Base de Datos + Archivo JSON
```

---

## CONFIGURACIÓN DE COLAS

**RabbitMQConfig:** `src/main/java/cl/duoc/azuread/ejemplo/config/RabbitMQConfig.java`

- **Cola "ventas"** - Para mensajes de ventas
- **Cola "cola.promociones"** - Para mensajes de promociones
- **Cola "mensajes.colas2"** - Para mensajes generales/admin
- **Cola "dlx-queue"** - Para mensajes fallidos (Dead Letter Queue)

---

## VERIFICACIÓN DE REQUERIMIENTOS

### Requerimiento 1: "Dos microservicios que generaran mensajes, uno por cada cola"
- **CUMPLIDO:** RabbitProducerService (cola ventas) + PromocionRabbitProducer (cola promociones)

### Requerimiento 3: "El microservicio consumidor de mensajes de promociones debía generar un archivo JSON"
- **CUMPLIDO:** PromocionConsumer genera archivos JSON únicos en `promociones_json/`
- **Formato:** `promocion_yyyyMMdd_HHmmss.json`
- **Contenido:** JSON pretty-printed de la promoción recibida

---

## CONFIGURACIÓN DE COLAS

**RabbitMQConfig:** `src/main/java/cl/duoc/azuread/ejemplo/config/RabbitMQConfig.java`

- **Cola "ventas"** - Para mensajes de ventas
- **Cola "cola.promociones"** - Para mensajes de promociones
- **Cola "mensajes.colas2"** - Para mensajes generales/admin
- **Cola "dlx-queue"** - Para mensajes fallidos (Dead Letter Queue)

---

## PRUEBAS DE FUNCIONALIDAD

### Prueba de Productor de Ventas:
1. **Endpoint:** `POST /ventas/crear`
2. **Función:** Envía VentaDTO a la cola "ventas"
3. **Verificación:** Revisar logs de VentaConsumer

### Prueba de Productor de Promociones:
1. **Endpoint:** `POST /promociones/crear`
2. **Función:** Envía Promocion a la cola "cola.promociones"
3. **Verificación:** Revisar logs de PromocionConsumer y archivos JSON generados

### Verificación de Archivos JSON:
- **Ubicación:** Directorio `promociones_json/`
- **Formato:** `promocion_yyyyMMdd_HHmmss.json`
- **Contenido:** JSON de la promoción consumida

---

## ESTRUCTURA DE ARCHIVOS

```
src/main/java/cl/duoc/azuread/ejemplo/
├── service/
│   ├── RabbitProducerService.java        # PRODUCTOR 1 
│   └── PromocionRabbitProducer.java      # PRODUCTOR 2
├── listener/
│   ├── VentaConsumer.java                # CONSUMIDOR 1
│   └── PromocionConsumer.java            # CONSUMIDOR 2 (con generación JSON)
├── controllers/
│   ├── VentaController.java              # API para ventas
│   └── PromocionController.java          # API para promociones
└── config/
    └── RabbitMQConfig.java               # Configuración de colas
```

---

**RESULTADO FINAL:** Arquitectura de microservicios RabbitMQ COMPLETAMENTE AJUSTADA que cumple EXACTAMENTE con los 3 requerimientos solicitados:

1. **Exactamente 2 productores** (uno por cada cola principal)
2. **Exactamente 2 consumidores** (uno por cada cola principal)  
3. **Consumidor de promociones genera archivos JSON**

**COMPILACIÓN EXITOSA** - Sin errores de código después de las modificaciones.
