-- =================================================================
-- Creación de Esquemas
-- =================================================================
CREATE SCHEMA IF NOT EXISTS seguridad;
CREATE SCHEMA IF NOT EXISTS maestros;
CREATE SCHEMA IF NOT EXISTS facturacion;

-- =================================================================
-- Esquema: seguridad
-- =================================================================

-- Tabla de Roles
CREATE TABLE seguridad.roles (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

-- Tabla de Usuarios
CREATE TABLE seguridad.usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    nombre_completo VARCHAR(150),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Union: Usuarios y Roles (Muchos a Muchos)
CREATE TABLE seguridad.usuario_roles (
    usuario_id BIGINT NOT NULL,
    rol_id INT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES seguridad.usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES seguridad.roles(id) ON DELETE CASCADE
);

-- =================================================================
-- Esquema: maestros
-- =================================================================

-- Tabla de Categorías de Producto
CREATE TABLE maestros.categorias_producto (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT
);

-- =================================================================
-- Esquema: facturacion
-- =================================================================

-- Tabla de Clientes
CREATE TABLE facturacion.clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    ruc_cedula VARCHAR(20) UNIQUE NOT NULL,
    direccion TEXT,
    email VARCHAR(100),
    telefono VARCHAR(20)
);

-- Tabla de Productos
CREATE TABLE facturacion.productos (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    stock INT DEFAULT 0,
    categoria_id INT,
    FOREIGN KEY (categoria_id) REFERENCES maestros.categorias_producto(id) ON DELETE SET NULL
);

-- Tabla de Facturas (Cabecera)
CREATE TABLE facturacion.facturas (
    id BIGSERIAL PRIMARY KEY,
    numero_factura VARCHAR(50) UNIQUE NOT NULL,
    fecha_emision TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('PENDIENTE', 'PAGADA', 'ANULADA')),
    subtotal DECIMAL(12, 2) NOT NULL,
    total_impuestos DECIMAL(12, 2) NOT NULL,
    total DECIMAL(12, 2) NOT NULL,
    cliente_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES facturacion.clientes(id),
    FOREIGN KEY (usuario_id) REFERENCES seguridad.usuarios(id)
);

-- Tabla de Detalles de Factura (Líneas)
CREATE TABLE facturacion.detalles_factura (
    id BIGSERIAL PRIMARY KEY,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal_linea DECIMAL(12, 2) NOT NULL,
    factura_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    FOREIGN KEY (factura_id) REFERENCES facturacion.facturas(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES facturacion.productos(id)
);

-- Tabla de Pagos
CREATE TABLE facturacion.pagos (
    id BIGSERIAL PRIMARY KEY,
    fecha_pago TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    monto DECIMAL(12, 2) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL CHECK (metodo_pago IN ('TARJETA', 'EFECTIVO', 'TRANSFERENCIA')),
    referencia VARCHAR(255), -- Para guardar nro. de transacción, etc.
    factura_id BIGINT NOT NULL,
    FOREIGN KEY (factura_id) REFERENCES facturacion.facturas(id)
);

-- =================================================================
-- Inserción de datos iniciales (Opcional)
-- =================================================================
INSERT INTO seguridad.roles (nombre) VALUES ('ROLE_ADMIN'), ('ROLE_VENDEDOR');
INSERT INTO maestros.categorias_producto (nombre, descripcion) VALUES ('General', 'Categoría por defecto');
