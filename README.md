# 🏦 Banco Digital (versión JavaFX)

Proyecto académico desarrollado en Java para la **Universidad del Quindío** como parte del curso de Programación Orientada a Objetos (POO). Aplicación bancaria completa con interfaz gráfica en **JavaFX**, sistema de sesión activa, recarga de saldo, transferencias entre billeteras y reportes de porcentaje de gastos e ingresos por mes. Implementa los patrones **Singleton** en `Banco` y `Sesion`, y usa **Lombok** para reducir código repetitivo.

---

## 📁 Estructura del proyecto

```
src/main/java/co/edu/uniquindio/banco/
├── BancoApp.java                         # Punto de entrada JavaFX, carga datos iniciales
├── config/
│   └── Constantes.java                   # COMISION = 200 (fijo por transferencia)
├── modelo/
│   ├── Sesion.java                       # Singleton que mantiene el usuario activo
│   ├── entidades/
│   │   ├── Banco.java                    # Singleton central con toda la lógica de negocio
│   │   ├── Usuario.java                  # Entidad usuario (Lombok @AllArgsConstructor)
│   │   ├── BilleteraVirtual.java         # Billetera con saldo, transacciones y cálculos
│   │   └── Transaccion.java              # Entidad transacción (Lombok)
│   ├── enums/
│   │   ├── Categoria.java                # Categorías de transacción
│   │   └── TipoTransaccion.java          # DEPOSITO / RETIRO (definido, no usado en UI)
│   └── vo/
│       ├── PorcentajeGastosIngresos.java # Value Object para reporte de porcentajes
│       └── SaldoTransaccionesBilletera.java # Value Object para consulta de saldo y movimientos
└── controlador/
    ├── InicioControlador.java            # Pantalla de bienvenida con navegación
    ├── LoginControlador.java             # Inicio de sesión con validación de credenciales
    ├── RegistroControlador.java          # Registro de nuevo usuario
    ├── PanelClienteControlador.java      # Panel principal con tabla de transacciones
    ├── ActualizarControlador.java        # Actualización de datos del usuario activo
    ├── RecargaControlador.java           # Recarga de saldo con actualización de tabla
    └── TransferenciaControlador.java     # Transferencia entre billeteras por número de cuenta

src/main/resources/
├── inicio.fxml       registro.fxml
├── login.fxml        recarga.fxml
├── panelCliente.fxml transferencia.fxml
└── actualizar.fxml
```

---

## 🧩 Modelo de clases

### `Banco` (Singleton)
Única instancia accesible mediante `Banco.getInstancia()`. Gestiona listas de `Usuario` y `BilleteraVirtual` usando Streams para búsquedas. Al registrar un usuario, crea automáticamente su billetera con número único de 10 dígitos.

**Métodos principales:**

| Método | Descripción |
|---|---|
| `registrarUsuario(id, nombre, direccion, email, password)` | Valida que ningún campo sea vacío y que el ID no exista; crea usuario y billetera |
| `ActualizarUsuario(usuario, id, nombre, ...)` | Permite cambiar el ID si el nuevo no está en uso |
| `IniciarSesion(id, password)` | Recorre la lista y retorna el `Usuario` si las credenciales coinciden, o `null` |
| `recargarBilletera(numero, monto)` | Crea transacción de tipo `RECARGA` con billeteras origen y destino iguales |
| `realizarTransferencia(origen, destino, monto, categoria)` | Valida existencia de billeteras, que no sean la misma y que haya saldo; llama a `retirar` y `depositar` |
| `consultarSaldoYTransacciones(id, password)` | Valida credenciales y retorna un `SaldoTransaccionesBilletera` |
| `obtenerPorcentajeGastosIngresos(numero, mes, anio)` | Delega en `BilleteraVirtual` el cálculo de porcentajes para el mes/año indicado |
| `buscarBilleteraUsuario(idUsuario)` | Búsqueda con `Stream.filter` por ID de usuario |
| `buscarBilletera(numero)` | Búsqueda con `Stream.filter` por número de billetera |
| `buscarUsuario(id)` | Búsqueda con `Stream.filter` por ID |

### `BilleteraVirtual`
Mantiene su propio `ArrayList<Transaccion>` y gestiona saldo directamente.

- `depositar(monto, transaccion)` — Suma el monto al saldo y registra la transacción.
- `retirar(monto, transaccion)` — Descuenta `monto + COMISION` y registra la transacción.
- `tieneSaldo(monto)` — Verifica si `saldo ≥ monto + COMISION`.
- `obtenerTransaccionesPeriodo(inicio, fin)` — Filtra transacciones entre dos `LocalDateTime` con comparación `isAfter` / `isBefore`.
- `obtenerPorcentajeGastosIngresos(mes, anio)` — Itera las transacciones, suma egresos (cuando la billetera es origen) e ingresos (cuando es destino), y retorna un `PorcentajeGastosIngresos`.

### `Transaccion`
Generada con Lombok (`@AllArgsConstructor`, `@Getter`, `@Setter`). Tiene `id` (String UUID), `monto`, `fecha` (`LocalDateTime`), `tipo` (`Categoria`), `billeteraOrigen`, `billeteraDestino` y `comision`.

### `Sesion` (Singleton)
Guarda el `Usuario` activo entre pantallas. `cerrarSesion()` lo pone a `null`.

### `Categoria` (enum)
`ALIMENTOS` · `TRANSPORTE` · `SALUD` · `EDUCACION` · `ENTRETENIMIENTO` · `RECARGA` · `OTROS`

---

## 🖥️ Flujo de navegación

```
inicio.fxml
├── → login.fxml ──────→ panelCliente.fxml
│                              ├── → actualizar.fxml → panelCliente.fxml
│                              ├── → recarga.fxml (ventana modal)
│                              └── → transferencia.fxml (ventana modal)
└── → registro.fxml → inicio.fxml
```

El panel del cliente muestra en la tabla de transacciones la columna **Tipo** con valores `RECARGA`, `DEPOSITO` o `INGRESO` determinados dinámicamente comparando las billeteras de la transacción con la del usuario activo. Las ventanas de recarga y transferencia reciben una referencia al `PanelClienteControlador` para refrescar la tabla al completar la operación.

---

## 🗂️ Datos precargados

Al iniciar, `BancoApp.cargarDatos()` registra dos usuarios con sus billeteras y las recarga:
- **Juanito** (ID: 123456789) — saldo inicial: $500.000
- **Paco** (ID: 012345678) — saldo inicial: $1.000.000

---

## 🛠️ Tecnologías

- **Java** (SE 17+)
- **JavaFX** (FXML, `TableView`, `MenuButton`, `ComboBox`, `PasswordField`)
- **Lombok** (`@Getter`, `@Setter`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@ToString`, `@Setter` en controladores)
- **Maven** (gestión del proyecto)

---

## 🚀 Cómo ejecutar

```bash
git clone <url-del-repositorio>
mvn compile
mvn javafx:run
```

---

## 📄 Licencia

GNU/GPL V3.0 — [Ver licencia](https://raw.githubusercontent.com/grid-uq/poo/main/LICENSE)
