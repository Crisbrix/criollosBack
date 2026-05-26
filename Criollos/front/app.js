const API = {
  auth: "http://localhost:8080/api/criollos/usuarios",
  productos: "http://localhost:8081/productos",
  pedidos: "http://localhost:8082/api/criollos/pedidos"
};

const state = {
  token: null,
  usuario: null,
  cedula: null,
  producto: null
};

const output = document.querySelector("#output");
const loginPanel = document.querySelector("#loginPanel");
const productoPanel = document.querySelector("#productoPanel");
const pedidoPanel = document.querySelector("#pedidoPanel");
const sessionText = document.querySelector("#sessionText");
const productText = document.querySelector("#productText");

function show(title, data) {
  output.textContent = `${title}\n\n${JSON.stringify(data, null, 2)}`;
}

function formData(form) {
  return Object.fromEntries(new FormData(form).entries());
}

function numberOrZero(value) {
  const number = Number(value);
  return Number.isFinite(number) ? number : 0;
}

function setStep(step) {
  document.querySelectorAll(".step").forEach((item) => item.classList.remove("active"));
  document.querySelector(`#step${step}`).classList.add("active");
}

function goProducto() {
  productoPanel.classList.remove("hidden");
  setStep("Producto");
  productoPanel.scrollIntoView({ behavior: "smooth", block: "start" });
}

function goPedido() {
  pedidoPanel.classList.remove("hidden");
  setStep("Pedido");
  pedidoPanel.scrollIntoView({ behavior: "smooth", block: "start" });
}

async function request(title, url, options = {}) {
  try {
    const response = await fetch(url, {
      headers: {
        "Content-Type": "application/json",
        ...(state.token ? { Authorization: `Bearer ${state.token}` } : {}),
        ...(options.headers || {})
      },
      ...options
    });
    const text = await response.text();
    const data = text ? JSON.parse(text) : null;
    show(`${title} (${response.status})`, data);
    return { ok: response.ok, status: response.status, data };
  } catch (error) {
    show(`${title} - error`, {
      mensaje: error.message,
      ayuda: "Revisa que Auth, Producto y Pedidos esten encendidos en 8080, 8081 y 8082."
    });
    return { ok: false, status: 0, data: null };
  }
}

document.querySelector("#usuarioForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  const data = formData(event.currentTarget);
  data.edad = numberOrZero(data.edad);

  const result = await request("Crear usuario", `${API.auth}/guardar`, {
    method: "POST",
    body: JSON.stringify(data)
  });

  if (result.ok) {
    document.querySelector("#loginForm [name='email']").value = data.email;
    document.querySelector("#loginForm [name='password']").value = data.password;
    document.querySelector("#loginForm [name='cedula']").value = data.cedula;
  }
});

document.querySelector("#loginForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  const data = formData(event.currentTarget);

  const result = await request("Login", `${API.auth}/login`, {
    method: "POST",
    body: JSON.stringify({
      email: data.email,
      password: data.password
    })
  });

  if (result.data?.token) {
    state.token = result.data.token;
    state.usuario = result.data.usuario;
    state.cedula = data.cedula;
    sessionText.textContent = `${state.usuario.nombre} (${state.cedula})`;
    goProducto();
  }
});

document.querySelector("#productoForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  const data = formData(event.currentTarget);
  data.precio = numberOrZero(data.precio);
  data.stock = numberOrZero(data.stock);
  data.stockMinimo = numberOrZero(data.stockMinimo);
  data.activo = event.currentTarget.activo.checked;

  const result = await request("Guardar producto", `${API.productos}/guardar`, {
    method: "POST",
    body: JSON.stringify(data)
  });

  if (result.data?.productoId) {
    state.producto = result.data;
    productText.textContent = `${state.producto.nombre} #${state.producto.productoId}`;
    goPedido();
  }
});

document.querySelector("#usarProductoButton").addEventListener("click", async () => {
  const id = document.querySelector("#productoManualId").value.trim();
  const result = await request("Buscar producto", `${API.productos}/buscar/${encodeURIComponent(id)}`);

  if (result.data?.productoId) {
    state.producto = result.data;
    productText.textContent = `${state.producto.nombre} #${state.producto.productoId}`;
    goPedido();
  }
});

document.querySelector("#listarProductosButton").addEventListener("click", () => {
  request("Listar productos", `${API.productos}/todos`);
});

document.querySelector("#pedidoForm").addEventListener("submit", async (event) => {
  event.preventDefault();

  if (!state.cedula || !state.producto?.productoId) {
    show("Falta completar el flujo", {
      mensaje: "Primero haz login y selecciona un producto."
    });
    return;
  }

  const data = formData(event.currentTarget);
  const pedido = {
    cedulaCliente: state.cedula,
    mesa: data.mesa,
    metodoPago: data.metodoPago,
    impuesto: numberOrZero(data.impuesto),
    detalles: [
      {
        productoId: state.producto.productoId,
        cantidad: numberOrZero(data.cantidad),
        notas: data.notas
      }
    ]
  };

  const result = await request("Crear pedido", `${API.pedidos}/guardar`, {
    method: "POST",
    body: JSON.stringify(pedido)
  });

  if (result.data?.numeroPedido) {
    document.querySelector("#buscarPedidoNumero").value = result.data.numeroPedido;
  }
});

document.querySelector("#buscarPedidoButton").addEventListener("click", () => {
  const numero = document.querySelector("#buscarPedidoNumero").value.trim();
  request("Buscar pedido", `${API.pedidos}/buscar/${encodeURIComponent(numero)}`);
});

document.querySelector("#listarPedidosButton").addEventListener("click", () => {
  request("Listar pedidos", `${API.pedidos}/listar`);
});

document.querySelector("#clearButton").addEventListener("click", () => {
  output.textContent = "Listo para probar.";
});

document.querySelector("#resetButton").addEventListener("click", () => {
  state.token = null;
  state.usuario = null;
  state.cedula = null;
  state.producto = null;
  productoPanel.classList.add("hidden");
  pedidoPanel.classList.add("hidden");
  sessionText.textContent = "Sin login";
  productText.textContent = "Sin producto";
  setStep("Login");
  loginPanel.scrollIntoView({ behavior: "smooth", block: "start" });
  output.textContent = "Flujo reiniciado.";
});
